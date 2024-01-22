#!/bin/sh

set -eu

# we assume here that all secrets are already read to environment variables

# if TIAMAT_DB_URL was not set, build it from parts
if [ -z "${TIAMAT_DB_URL+x}" ]; then
  export TIAMAT_DB_PORT=${TIAMAT_DB_PORT:-"5432"}
  # TODO: Remove default values once secrets work properly
  export TIAMAT_DB_HOSTNAME=${TIAMAT_DB_HOSTNAME:-"jore4-testdb"}
  export TIAMAT_DB_DATABASE=${TIAMAT_DB_DATABASE:-"stopdb"}
  export TIAMAT_DB_URL="jdbc:postgresql://${TIAMAT_DB_HOSTNAME}:${TIAMAT_DB_PORT}/${TIAMAT_DB_DATABASE}?stringtype=unspecified"
fi
