# wcomponents

Accessible Web UI Framework for Enterprise

*<sup>Born in 2005 - Open source since August 2015</sup>*

WComponents is an opinionated Java framework for building accessible web applications for enterprise and government.

Its goal is to enable Java developers to build AJAX enabled, WCAG 2.0 compliant web applications without the need for expertise in accessibility or client side technologies.

## Status

[![Build Status](https://travis-ci.org/BorderTech/wcomponents.svg?branch=georgie)](https://travis-ci.org/BorderTech/wcomponents)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bordertech-wcomponents&metric=alert_status)](https://sonarcloud.io/dashboard?id=bordertech-wcomponents)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=bordertech-wcomponents&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=bordertech-wcomponents)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bordertech-wcomponents&metric=coverage)](https://sonarcloud.io/dashboard?id=bordertech-wcomponents)
[![Codacy](https://api.codacy.com/project/badge/grade/7ba92824eb1f4d60abeddf1e72108bbc)](https://www.codacy.com/app/BorderTech/wcomponents)
[![Javadocs](https://javadoc.io/badge/com.github.bordertech.wcomponents/wcomponents-core.svg)](https://javadoc.io/doc/com.github.bordertech.wcomponents/wcomponents-core)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.bordertech.wcomponents/wcomponents-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.bordertech.wcomponents%22%20AND%20a:%22wcomponents-core%22)

## Contributing

Contributions welcome: See the CONTRIBUTING and CODE_OF_CONDUCT files in this project.

## Why Use WComponents

WComponents is **yet another Java UI framework**, so why would you bother?

1. WComponents is designed to make it possible to build applications which meet [WCAG 2.0](http://www.w3.org/TR/WCAG20/) at level AA or better. This is a **mandatory** requirement for Australian Government web-based applications which is where WComponents started life in 2005.
2. WComponents is extremely scaleable: we have had very large applications running thousands of components per view and still functioning in IE6!
3. WComponents based applications _can_ be pure Java: it is possible (though not mandatory) to build an application with no browser code in the application space. Someone who can build a Java application can build a Java web application without a _need_ to know extensive web-client libraries etc.
4. WComponents makes it easy to implement a common corporate style because every client side aspect of every component is controlled within the theme, even to the extent of being able to prevent the use of templates, inline HTML or some of the Java API _designed_ to allow overrides of themes. A common look & feel for a suite of applications _should_ lead to lower development and maintenance burden and lower training costs for users of intranet applications.
5. We are nice so you would _like_ to use this framework just because of that.

## Supported Browsers

Out of the box WComponents supports all modern desktop and mobile browsers including:

### Mobile

- Mobile Safari
- Chrome
- Mobile Firefox
- UC
- Mobile IE (Windows Phone) and Edge (Windows 10 mobile)

### Desktop

- Chrome
- Firefox
- Safari
- Microsoft Edge
- Internet Explorer 11

WComponents client side code is extensible and configurable therefore it is perfectly feasible to add support for older browsers not listed above.

## Building

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

Note: you may also be able to simply double-click the jar file.

### Running the examples project

The examples project is most easily run from the project view of your IDE.

For example:

1. Right click the `wcomponents-examples-lde` project.
2. Select the main class to run: `com.github.bordertech.wcomponents.examples.lde.PlainLauncherProxy`
