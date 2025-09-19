#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix2834}"
DOCKERHUB_TOKEN="${DOCKERHUB_TOKEN:-}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"
DOCKER_PLATFORMS="${DOCKER_PLATFORMS:-linux/amd64}"

# Xác định DOCKER_GID an toàn
if [[ "${OSTYPE:-}" == "darwin"* ]]; then
  DOCKER_GID=999
elif [ -S /var/run/docker.sock ]; then
  DOCKER_GID=$(stat -c '%g' /var/run/docker.sock 2>/dev/null || echo 999)
else
  DOCKER_GID=999
fi

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

build_push_coding() {
  local module="${1:-coding-service}"
  local repo="${DOCKERHUB_USER}/codecampus-${module}"

  log "Building coding-service with DOCKER_GID=${DOCKER_GID}"
  docker buildx build \
    --platform "${DOCKER_PLATFORMS}" \
    -f docker/java-service-coding.Dockerfile \
    --build-arg "MODULE=${module}" \
    --build-arg "DOCKER_HOST_GID=${DOCKER_GID}" \
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
  build_push_coding "${1:-coding-service}"
  log "Done."
}

main "$@"
