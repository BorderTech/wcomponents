# Contributing to WComponents

Contributions are welcome. Please use the standard
[Fork and Pull](https://help.github.com/articles/using-pull-requests/) workflow. Note that your pull
request will not be against `master` see [Our Branches](https://github.com/BorderTech/wcomponents/wiki/Branches).

## Before you start

We ask that you read our **CODE OF CONDUCT** before you do anything else.

Proposed changes should have an issue raised doing any work. Bugs should go straight to
[our issues](https://github.com/BorderTech/wcomponents/issues).

## Mandatory requirements

We will not pull any changes unless these requirements are met.

- Your change must make sense within a generic, abstract framework. Special cases and specific
  components should form part a higher layer, not the core framework itself. Useful composite or
  specific purpose components my be better in the [WComponents samples](https://github.com/BorderTech/wcomponents-samples)
  or [BorderTech/java-common](https://github.com/BorderTech/java-common) projects.
- Your change must be covered by unit tests.
- The [CI build](https://travis-ci.org/BorderTech/wcomponents) must pass.
- Your code must follow our [coding conventions](https://github.com/BorderTech/wcomponents/wiki/Coding=conventions).
- Theme/front-end code must be [accessible](https://github.com/BorderTech/wcomponents/wiki/Accessibility).

## Changelog

In general changes will require a CHANGELOG entry (there will be exceptions, for example if you are
reverting a previous change that had not yet been released) and be sure to reference the issue
number that the change addresses.

For example: `Fix WKittenLocator not finding tabbies (#999)`.
