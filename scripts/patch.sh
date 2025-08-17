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
git cherry-pick den/enh/dont-deserialize-transient
git cherry-pick den/enh/error-on-unknown-ns-prefix
git cherry-pick den/enh/error-on-unresolved-reference
git cherry-pick den/enh/error-on-unknown-feature
git cherry-pick den/enh/throw-ioexception
git cherry-pick den/fix/reference-order
git cherry-pick den/fix/resolve-cross-document-reference-by-nsprefix
git cherry-pick den/enh/streaming-serialization
echo Success

# Publish

# git switch den/enh/scripts && ./scripts/patch.sh && JAVA_HOME=/usr/lib/jvm/zulu21 mvn clean install -s settings.xml && ./scripts/patch.sh && JAVA_HOME=/usr/lib/jvm/zulu21 mvn deploy -s settings.xml
