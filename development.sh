#!/usr/bin/env bash

set -euo pipefail

# allow running from any working directory
WD=$(dirname "$0")
cd "${WD}"

DOCKER_COMPOSE_CMD="docker  compose -f ./docker/docker-compose.yml -f ./docker/docker-compose.custom.yml"

# Download Docker Compose bundle from the "jore4-docker-compose-bundle"
# repository. GitHub CLI is required to be installed.
#
# A commit reference can be given as an argument. It can contain, for example,
# only a substring of an actual SHA digest.
download_docker_compose_bundle() {
  local commit_ref="${1:-main}"

  local repo_name="jore4-docker-compose-bundle"
  local repo_owner="HSLdevcom"

  # Check GitHub CLI availability.
  if ! command -v gh &> /dev/null; then
    echo "Please install the GitHub CLI (gh) on your machine."
    exit 1
  fi

  # Make sure the user is authenticated to GitHub.
  gh auth status || gh auth login

  echo "Using the commit reference '${commit_ref}' to fetch a Docker Compose bundle..."

  # First, try to find a commit on GitHub that matches the given reference.
  # This function exits with an error code if no matching commit is found.
  local commit_sha
  commit_sha=$(
    gh api \
      -H "Accept: application/vnd.github+json" \
      -H "X-GitHub-Api-Version: 2022-11-28" \
      "repos/${repo_owner}/${repo_name}/commits/${commit_ref}" \
      --jq '.sha'
  )

  echo "Commit with the following SHA digest was found: ${commit_sha}"

  local zip_file="/tmp/${repo_name}.zip"
  local unzip_target_dir_prefix="/tmp/${repo_owner}-${repo_name}"

  # Remove old temporary directories if any remain.
  rm -fr "$unzip_target_dir_prefix"-*

  echo "Downloading the JORE4 Docker Compose bundle..."

  # Download Docker Compose bundle from the jore4-docker-compose-bundle
  # repository as a ZIP file.
  gh api "repos/${repo_owner}/${repo_name}/zipball/${commit_sha}" > "$zip_file"

  # Extract ZIP file contents to a temporary directory.
  unzip -q "$zip_file" -d /tmp

  # Clean untracked files from `docker` directory even if they are git-ignored.
  git clean -fx ./docker

  echo "Copying JORE4 Docker Compose bundle files to ./docker directory..."

  # Copy files from the `docker-compose` directory of the ZIP file to your
  # local `docker` directory.
  mv "$unzip_target_dir_prefix"-*/docker-compose/* ./docker

  # Remove the temporary files and directories created above.
  rm -fr "$zip_file" "$unzip_target_dir_prefix"-*

  echo "Generating a release version file for the downloaded bundle..."

  # Create a release version file containing the SHA digest of the referenced
  # commit.
  echo "$commit_sha" > ./docker/RELEASE_VERSION.txt
}

function start {
  $DOCKER_COMPOSE_CMD up --build -d jore4-tiamat jore4-testdb
}

function start_dependencies {
  $DOCKER_COMPOSE_CMD up -d jore4-testdb
}

function stop_all {
  $DOCKER_COMPOSE_CMD stop
}

function remove_all {
  $DOCKER_COMPOSE_CMD down
}

function build {
  mvn clean package spring-boot:repackage
}

function run_tests {
  mvn test
}

function print_usage {
  echo "
  Usage $(basename "$0") <command>

  build
    Build the project locally

  start [<commit_ref>]
    Start Tiamat service in Docker container.

    Optionally, you can pass a commit reference as an argument (like commit SHA
    or its initial substring) to point to a commit (of the
    jore4-docker-compose-bundle repository), which determines the Docker Compose
    bundle version to download. By default, the tip of the main branch is used.

  start:deps [<commit_ref>]
    Start the dependencies of jore4-tiamat.

    Optionally, you can pass a commit reference as an argument (like commit SHA
    or its initial substring) to point to a commit (of the
    jore4-docker-compose-bundle repository), which determines the Docker Compose
    bundle version to download. By default, the tip of the main branch is used.

  stop
    Stop Tiamat Docker container and all dependencies

  remove
    Stop and remove Tiamat Docker container and all dependencies

  test
    Run tests locally

  help
    Show this usage information
  "
}

COMMAND=${1:-}

if [[ -z $COMMAND ]]; then
  print_usage
  exit 1
fi

# Shift other arguments after the command so that we can refer to them later
# with "$@".
shift

case $COMMAND in
  start)
    download_docker_compose_bundle "$@"
    start
    ;;

  start:deps)
    download_docker_compose_bundle "$@"
    start_dependencies
    ;;

  stop)
    stop_all
    ;;

  remove)
    remove_all
    ;;

  help)
    usage
    ;;

  build)
    build
    ;;

  test)
    run_tests
    ;;

  *)
    echo ""
    echo "Unknown command: '${COMMAND}'"
    print_usage
    exit 1
    ;;
esac
