#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix2834}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"

login() {
  [ -n "$DOCKERHUB_TOKEN" ] && echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
}

build_push_java() {
  local module=$1
  local build_args=("--build-arg" "MODULE=$module")

  docker buildx build \
    -f docker/java-service.Dockerfile \
    "${build_args[@]}" \
    -t "$DOCKERHUB_USER/codecampus-$module:$IMAGE_TAG" \
    --push .
}


build_push_file_service() {
  docker buildx build \
    -f docker/file-service.Dockerfile \
    -t "$DOCKERHUB_USER/codecampus-file-service:$IMAGE_TAG" \
    --push .
}

build_push_organization_service() {
  docker buildx build \
    -f docker/file-service.Dockerfile \
    -t "$DOCKERHUB_USER/codecampus-organization-service:$IMAGE_TAG" \
    --push .
}

main() {
  login
  echo "Building with DOCKER_GID=${DOCKER_GID}"


#  for svc in quiz-service submission-service; do
#    echo "Building $svc..."
#    build_push_java "$svc"
#  done
  for svc in ai-service; do
    echo "Building $svc..."
    build_push_java "$svc"
  done
#  build_push_file_service
#  build_push_organization_service
}

main "$@"