#!/bin/bash
if !(./gradlew spotlessCheck) >/dev/null 2>&1
then
    echo "Spotless error: please run './gradlew spotlessApply'" 1>&2
    exit 1
fi
