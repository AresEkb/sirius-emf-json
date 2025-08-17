#!/bin/bash

set -Eeuo pipefail

packages=(
https://api.github.com/orgs/metamodeldev/packages/maven/org.eclipse.sirius.org.eclipse.sirius.emfjson
)

for pkg in ${packages[@]}; do
  echo "Deleting $pkg"
  curl -L \
    -X DELETE \
    -H "Accept: application/vnd.github+json" \
    -H "Authorization: Bearer $GITHUB_DELETE_TOKEN" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "$pkg"
done
