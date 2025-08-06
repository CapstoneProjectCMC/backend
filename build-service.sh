#!/usr/bin/env bash
set -euo pipefail
export DOCKER_BUILDKIT=1

DOCKERHUB_USER="${DOCKERHUB_USER:-yunomix2834}"
IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d.%H%M%S)}"

login() {
  echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
}

build_push_java() {
  local module=$1
  docker buildx build \
    -f docker/java-service.Dockerfile \
    --build-arg MODULE="$module" \
    -t "$DOCKERHUB_USER/codecampus-$module:$IMAGE_TAG" \
    --push .
}

build_push_file_service() {
  docker buildx build \
    -f docker/file-service.Dockerfile \
    -t "$DOCKERHUB_USER/codecampus-file-service:$IMAGE_TAG" \
    --push .
}

main() {
  login
#  for svc in submission-service quiz-service
#  do
#    build_push_java "$svc"
#  done
#  build_push_java "profile-service"
  build_push_file_service
}

main "$@"
