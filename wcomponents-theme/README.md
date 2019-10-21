# WComponents Theme

This directory contains the client side codebase for WComponents.

## Fixtures

You will need either Java + Maven and/or Node + Yarn or NPM.
The examples below use yarn but you can substitute with npm.

By default tests require Firefox browser to be installed on your system.

## Building

```bash
# First time only run yarn install
yarn install

# thenceforth
yarn run build
```

OR

`mvn compile`

### Build Options

Custom build options are configured in the [package.json](package.json) under the section `"com_github_bordertech"`.

To override these without modifying a file tracked by source control:

1. Create a file in this directory with the format {username}.json, for example on Mac `vi $USER.json`
1. Make sure you add this to your [global gitignore](https://help.github.com/en/articles/ignoring-files#create-a-global-gitignore)
1. Add `com_github_bordertech` properties you want to override, for example your file contents may look like this:

```json
{
  "testMinOrMax": "max"
}
```

## Testing

We use [Intern](https://theintern.io/docs.html#Intern/4/) to run tests. You can therefore override much of the config, such as what tests, which browser, what port etc using the [`INTERN_ARGS` environment variable](https://theintern.io/docs.html#Intern/4/docs/docs%2Fconfiguration.md/environment-variable).

By default we test the minified code but for debugging you will want to use the unminified version: set `testMinOrMax` to `"max` (see Build Options above).

### Auto-Detect Test Environment

Auto-detect local headless firefox or remote saucelabs:

```bash
# If SAUCE_USERNAME and SAUCE_ACCESS_KEY env variables are set it will tunnel to saucelabs.

yarn run test
```

OR

`mvn test`

### Local Browser

Run the tests in a browser on your machine:

```bash
yarn run test:local
```

### Server Mode

Create a test server which you can visit in your browser to run tests:

```bash
# Run this then visit http://localhost:9000/__intern/

yarn run test:serve
```

### Selenium Grid Mode

Test against a running selenium server.

```bash
yarn run test:grid
```

## Developing

### Automatic Code Compilation

Client side development is facilitated by automatic code compilation. This will detect changes as you work and selectively rebuild automatically. To enable this:

```bash
# Watches for changes to source code and compiles automatically

yarn run watch
```

### Hot Module Reloading

When code is rebuilt using automatic code completion (see above) it is possible to have this loaded into a running browser without a page reload.

For example, when developing against the [WComponents Java Examples](https://github.com/BorderTech/wcomponents/wiki/Debugging-a-theme#debugging-using-the-wcomponents-java-examples).

1. Run Automatic Code Completion (as described above) - this automatically starts a hot reload server.
1. Make the examples [use your locally built theme](https://github.com/BorderTech/wcomponents/wiki/Debugging-a-theme#using-a-locally-built-theme-with-the-wcomponent-examples).
1. Ensure you are [running in debug mode](https://github.com/BorderTech/wcomponents/wiki/Debugging-a-theme)
1. In your browser:

EITHER:

```javascript
/*
This will set a cookie that tells the hot reload client to automatically connect when the page loads.
You only need to do this once every 800 days (or whatever you set it to).
*/

require("wc/dom/cookie").create("wchotmod", "true", 800);
```

OR:

 ```javascript
 // This will force the hot reload client to connect to the server you are running

 require("wc/debug/hotReloadClient").getConnection(true);
 ```
