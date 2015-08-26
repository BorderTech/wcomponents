[![Build Status](https://travis-ci.org/OpenBorders/wcomponents.svg?branch=master)](https://travis-ci.org/OpenBorders/wcomponents)
# wcomponents
Accessible Web UI Framework for Enterprise

WComponents is an opinionated Java framework for building accessible web applications for enterprise and government.

Its goal is to enable developers to build AJAX enabled, WCAG 2.0 compliant web applications without the need for expertise in accessibility or client side technologies.

# Building
If you wish to build WComponents you will need [Apache Maven](https://maven.apache.org/) installed.

Run these commands to fetch the source and build:

1. `git clone https://github.com/OpenBorders/wcomponents.git`
2. `cd wcomponents`
3. `mvn install`

## Running the wcomponents_examples_lde executable jar
After following the steps above to build WComponents you may run the `wcomponents_examples_lde`

1. ` cd wcomponents_examples_lde/target/`
2. `java -jar wcomponents_examples_lde-VERSION-jar-with-dependencies.jar` (replace VERSION with the version you built, e.g. `1.0.0-SNAPSHOT`)

Note: you may also simply double-click the jar file.
