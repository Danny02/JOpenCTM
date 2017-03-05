#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_d8d1ab557d6e_key -iv $encrypted_d8d1ab557d6e_iv -in deploy/codesigning.asc.enc -out deploy/codesigning.asc -d
    gpg --fast-import deploy/codesigning.asc
fi
