#!/bin/bash

## Activate skipThemeOptionalTests until Intern and Saucelabs integration fixed

if [[ -n ${TRAVIS_SECURE_ENV_VARS+x} && ${TRAVIS_SECURE_ENV_VARS} == true && ! -z ${SONAR_TOKEN} ]]; then
	echo "Travis secure variables ARE available"
	mvn --batch-mode package sonar:sonar -Dsonar.projectKey="bordertech-wcomponents" -PskipThemeOptionalTests -PskipCoreOptionalTests
else
	echo "Travis secure variables not available"
	mvn --batch-mode package -PskipCoreOptionalTests
fi
