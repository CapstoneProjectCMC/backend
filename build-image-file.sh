#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix280304}"
DOCKERHUB_TOKEN="${DOCKERHUB_TOKEN:-}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"
DOCKER_PLATFORMS="${DOCKER_PLATFORMS:-linux/amd64}"

log() { echo "[$(date +'%H:%M:%S')] $*"; }

login() {
  if [ -n "$DOCKERHUB_TOKEN" ]; then
    log "Logging in Docker Hub as $DOCKERHUB_USER"
    echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
  else
    log "DOCKERHUB_TOKEN empty -> skip docker login (build will fail on --push if registry requires auth)"
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

build_push_file_service() {
  local repo="${DOCKERHUB_USER}/codecampus-file-service"
  docker buildx build \
    --platform "${DOCKER_PLATFORMS}" \
    -f docker/file-service.Dockerfile \
    -t "${repo}:${IMAGE_TAG}" \
    -t "${repo}:latest" \
    $(extra_tags_args "${repo}") \
    --label "org.opencontainers.image.source=${GITHUB_SERVER_URL:-}/$([ -n "${GITHUB_REPOSITORY:-}" ] && echo "${GITHUB_REPOSITORY}")" \
    --label "org.opencontainers.image.revision=${GITHUB_SHA:-}" \
    --label "org.opencontainers.image.created=$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
    --push .
}

main() {
  login
  build_push_file_service
  log "Done."
}

main "$@"
