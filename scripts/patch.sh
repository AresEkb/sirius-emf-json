#!/bin/bash

set -Eeuo pipefail

# Update branches

# git restore . && git switch master && git pull
# git switch den/enh/scripts && git pull origin master --rebase && git push -f
# git rebase --continue && git push -f

# Apply patches

git switch metamodel
git reset --hard origin/master
git cherry-pick den/enh/scripts
git cherry-pick den/enh/custom-data-types
echo Success

# Publish

# JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64 USERNAME=$GITHUB_USERNAME PASSWORD=$GITHUB_TOKEN mvn clean verify -s settings.xml
# rm -R ~/.m2/repository/org/eclipse/sirius/org.eclipse.sirius.emfjson/
# JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64 USERNAME=$GITHUB_USERNAME PASSWORD=$GITHUB_TOKEN mvn install -s settings.xml -DskipTests -Dcheckstyle.skip
# ./scripts/package-delete-maven.sh && ./scripts/package-list-maven.sh
# JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64 USERNAME=$GITHUB_USERNAME PASSWORD=$GITHUB_PUBLISH_TOKEN mvn deploy -s settings.xml -DskipTests -Dcheckstyle.skip
