#!/bin/sh
# Maven Wrapper script - downloads Maven if needed and runs it
set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Maven 3.9.6..."
    mkdir -p "$MAVEN_HOME"
    DIST_URL=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
    curl -sL "$DIST_URL" -o /tmp/maven.zip
    unzip -qo /tmp/maven.zip -d "$HOME/.m2/wrapper/dists/"
    rm /tmp/maven.zip
fi

export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 21 2>/dev/null || echo '')}"
exec "$MAVEN_HOME/bin/mvn" "$@" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR"
