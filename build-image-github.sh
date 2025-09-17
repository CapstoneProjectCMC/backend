#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix2834}"
DOCKERHUB_TOKEN="${DOCKERHUB_TOKEN:-}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"
DOCKER_PLATFORMS="${DOCKER_PLATFORMS:-linux/amd64}"
GITHUB_OWNER="${GITHUB_OWNER:-${GITHUB_REPOSITORY_OWNER:-}}"

DEFAULT_SERVICES=(
  ai-service chat-service coding-service gateway-service identity-service
  notification-service payment-service post-service profile-service
  quiz-service search-service submission-service org-service
)

log() { echo "[$(date +'%H:%M:%S')] $*"; }

login() {
  if [ -n "$DOCKERHUB_TOKEN" ]; then
    log "Logging in Docker Hub as $DOCKERHUB_USER"
    echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
  else
    log "DOCKERHUB_TOKEN empty -> skip docker login (build will fail on --push if registry requires auth)"
  fi

  # Login GHCR bằng GITHUB_TOKEN
  if [ -n "${GITHUB_TOKEN:-}" ]; then
    log "Logging in GHCR as ${GITHUB_ACTOR:-github-actions}"
      echo "$GITHUB_TOKEN" | docker login ghcr.io -u "${GITHUB_ACTOR:-github-actions}" --password-stdin
    else
      log "GITHUB_TOKEN empty -> skip GHCR login"
  fi
}

extra_tags_args() {
  local repo="$1"
  local args=()
  if [ "${GITHUB_REF_TYPE:-}" = "tag" ] && [ -n "${GITHUB_REF_NAME:-}" ]; then
    local version="${GITHUB_REF_NAME#v}"
    args+=(-t "${repo}:${version}")
  fi
  if [ "${GITHUB_REF_NAME:-}" = "main" ]; then
    args+=(-t "${repo}:latest")
  fi
  printf '%s ' "${args[@]}"
}

build_push_java() {
  local module="$1"
  local repo="${DOCKERHUB_USER}/codecampus-${module}"
  local repo_ghcr="ghcr.io/${GITHUB_OWNER}/codecampus-${module}"
  log "Building Java service: ${module}"
  docker buildx build \
    --platform "${DOCKER_PLATFORMS}" \
    -f docker/java-service.Dockerfile \
    --build-arg "MODULE=${module}" \
    -t "${repo}:${IMAGE_TAG}" \
    -t "${repo_ghcr}:${IMAGE_TAG}" \
    $(extra_tags_args "${repo}") \
    $(extra_tags_args "${repo_ghcr}") \
    --label "org.opencontainers.image.source=${GITHUB_SERVER_URL:-}/$([ -n "${GITHUB_REPOSITORY:-}" ] && echo "${GITHUB_REPOSITORY}")" \
    --label "org.opencontainers.image.revision=${GITHUB_SHA:-}" \
    --label "org.opencontainers.image.created=$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
    --push .
}


main() {
  login
  log "Using DOCKER_PLATFORMS=${DOCKER_PLATFORMS}"
  log "Using IMAGE_TAG=${IMAGE_TAG}"

  local services=()
  if [ "$#" -gt 0 ]; then
    services=("$@")
  elif [ -n "${SERVICES:-}" ]; then
    # shellcheck disable=SC2206
    services=(${SERVICES})
  else
    services=("${DEFAULT_SERVICES[@]}")
  fi

  for svc in "${services[@]}"; do
    build_push_java "${svc}"
  done

  log "All done."
}

main "$@"