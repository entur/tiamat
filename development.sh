#!/usr/bin/env bash

set -eo pipefail

# allow running from any working directory
WD=$(dirname "$0")
cd "${WD}"

# initialize package folder
mkdir -p ./docker

DOCKER_COMPOSE_CMD="docker-compose -f ./docker/docker-compose.yml -f ./docker/docker-compose.custom.yml"

function check_docker {
  curl https://raw.githubusercontent.com/HSLdevcom/jore4-tools/main/docker/download-docker-bundle.sh | bash
}

function start {
  check_docker
  $DOCKER_COMPOSE_CMD up --build -d jore4-tiamat jore4-testdb
}

function start_dependencies {
  check_docker
  $DOCKER_COMPOSE_CMD up -d jore4-testdb
}

function stop_all {
  check_docker
  $DOCKER_COMPOSE_CMD stop
}

function remove_all {
  check_docker
  $DOCKER_COMPOSE_CMD down
}

function build {
  mvn clean package spring-boot:repackage
}

function run_tests {
  mvn test
}

function usage {
  echo "
  Usage $0 <command>

  build
    Build the project locally

  start
    Start hastus service in Docker container

  start-dependencies
    Start the dependencies of jore4-tiamat

  stop
    Stop all hastus Docker container

  remove
    Stop and remove hastus Docker container

  test
    Run tests locally

  help
    Show this usage information
  "
}

if [[ -z ${1} ]]; then
  usage
else
  case $1 in
  start)
    start
    ;;

  start-dependencies)
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
    usage
    ;;
  esac
fi
