#!/bin/sh
# Maven wrapper script for Linux / macOS / CI
set -e

if [ -z "$JAVA_HOME" ]; then
    if [ -x "$(command -v java)" ]; then
        JAVA_EXEC="java"
    else
        echo "Error: JAVA_HOME is not set and java could not be found in PATH." >&2
        exit 1
    fi
fi

if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "mvn command found or wrapper download"
    exec mvn "$@"
fi
