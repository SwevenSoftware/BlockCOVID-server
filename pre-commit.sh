#!/bin/bash
RED='\033[1;31m'
NC='\033[0m'
if !(./gradlew spotlessCheck) >/dev/null 2>&1
then
    echo "${RED}Spotless error:${NC} please run './gradlew spotlessApply'" 1>&2
    exit 1
fi
