#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix2834}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"

# Xác định DOCKER_GID an toàn
if [[ "$OSTYPE" == "darwin"* ]]; then
  DOCKER_GID=999
elif [ -S /var/run/docker.sock ]; then
  DOCKER_GID=$(stat -c '%g' /var/run/docker.sock 2>/dev/null || echo 999)
else
  DOCKER_GID=999
fi

login() {
  [ -n "$DOCKERHUB_TOKEN" ] && echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
}

build_push_java() {
  local module=$1
  local build_args=("--build-arg" "MODULE=$module")

  if [[ "$module" == "coding-service" ]]; then
    build_args+=(
      "--build-arg" "DOCKER_HOST_GID=${DOCKER_GID}"
    )
  fi

  docker buildx build \
    -f docker/java-service.Dockerfile \
    "${build_args[@]}" \
    -t "$DOCKERHUB_USER/codecampus-$module:$IMAGE_TAG" \
    --push .
}

main() {
  login
  echo "Building with DOCKER_GID=${DOCKER_GID}"

  for svc in search-service profile-service identity-service; do
    echo "Building $svc..."
    build_push_java "$svc"
  done
}

main "$@"