#!/bin/bash

set -o allexport
source .env
set +o allexport

# uncomment to view the exported variables from the .env file
#printenv | grep -E '^(DBUTIL_)' | sort || echo "No matching vars found"

mvn spring-boot:run
