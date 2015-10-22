Travis CI: [![Build Status](https://travis-ci.org/BorderTech/wcomponents.svg?branch=master)](https://travis-ci.org/BorderTech/wcomponents)

Frontend Tests: 
[![Sauce Test Status](https://saucelabs.com/browser-matrix/javatech.svg)](https://saucelabs.com/u/javatech)

# wcomponents
Accessible Web UI Framework for Enterprise

WComponents is an opinionated Java framework for building accessible web applications for enterprise and government.

Its goal is to enable developers to build AJAX enabled, WCAG 2.0 compliant web applications without the need for expertise in accessibility or client side technologies.

# Supported Browsers
Out of the box WComponents supports all modern desktop and mobile browsers including:

**Mobile**

* iPhone / iPad (Mobile Safari)
* Android (Mobile Chrome)
* Mobile Firefox
* Windows Phone

**Desktop**

* Chrome
* Firefox
* Safari
* Microsoft Edge
* Internet Explorer 8, 9, 10, 11

Because WComponents client side code is extensible and configurable it is perfectly feasible to add support for older browsers not listed above.

# Building
If you wish to build WComponents you will need [Apache Maven](https://maven.apache.org/) installed.

Run these commands to fetch the source and build:

1. `git clone https://github.com/bordertech/wcomponents.git`
2. `cd wcomponents`
3. `mvn install`

## Running the Examples
### Running the executable jar file
After following the steps above to build WComponents you may run the `wcomponents-examples-lde`

1. `cd wcomponents-examples-lde/target/`
2. `java -jar wcomponents-examples-lde-VERSION-jar-with-dependencies.jar` (replace VERSION with the version you built, e.g. `1.0.0-SNAPSHOT`)

Note: you may also simply double-click the jar file.

### Running the examples project
The examples project is most easily run from the project view of your IDE.

For example:

1. Right click the `wcomponents-examples-lde` project.
2. Select the main class to run: `com.github.bordertech.wcomponents.exampleslde.PlainLauncherProxy`

# Contributing
Contributions welcome: [Contributing](https://github.com/BorderTech/wcomponents/wiki/Contributing)
