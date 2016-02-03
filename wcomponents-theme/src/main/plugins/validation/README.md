# Client Side Validation

This is a sample build-time plugin for WComponents which does simple in-client validation of WComponent input and selection components.

## What this plugin does

The plugin checks for all of the following and will prevent form submission if any tests fail:

* all mandatory interactive and enabled input or selection components are not empty;
* all mandatory `WFieldSet` components contain either at least one interactive, enabled input or selection controls which is not empty _or_ **exactly zero** of these controls;
* all components which have a minimum acceptable value, size or number of selections are either empty or meet this minimum requirement,
  for example a `WTextField` with `minLength` set the field is either empty or the input is at least this long;
* all components which have a maximum acceptable value, size or number of selections are either empty or meet this maximum requirement, for example a `WNumberField` with `max` set the field is either empty or the input is at least this value;
* any `WTextField` with a `pattern` set is either empty or the entered value matches the expected pattern;
* any `WEmailField` is either empty or contains a potentially valid email address.

## Why not use HTML5 native validation?

This is an option. There is a property in `wcomponents-theme/src/main/properties/wc.ui.application.properties` named `wc.ui.application.xslt.HTML5clientSideValidation`. If this property is set equal to an empty string then your application will use native validation where available. We do not recommend this yet and do not recommend using both the native validation and the client validation plugin.

### Why native validation is not a brilliant idea (yet)

We would _really_ like to use native validation but it is still not ready (February 2016). The following are significant issues:

* fields inside closed/hidden containers _may_ be validated but are not necessarily applicable in the current context;
* there is no mechanism to determine the completeness of a fieldset where at least one field must be completed;
* there is no facility for sub-form validation which could be required before undertaking an AJAX action, for example;
* some very popular user agents will only report the first invalid field which may lead to the user having to make many attempts to submit a form if they have not met validation rules in several fields;
* `select` elements are always complete as there is no standard to determine a `null` option representing the unselected state.

## Adding the plugin to your theme

1. In your theme's `plugins.include.txt` add the following line.
  ```
  validation/**
  ```
2. Copy the file `wc.common.registrationScripts.impl_registration.xsl` to your theme's src/main/xslt directory if required (it may be necessary to create this directory) and change the template `impl_registration` to call the named template `plugin_validation`. If you have no other theme overrides in this template it may look like the following.
  ``` xml
  <xsl:template name="impl_registration">
    <xsl:call-template name="plugin_validation"/>
  </xsl:template>
  ```

## Using as a run-time plugins

This plugin's XSLT _only_ sets up a script element and it does not have any Sass. It is, therefore, notionally possible to use it as a run-time plugin. This will still require a build of your theme since the JavaScript will have to be built so you still need to add the ANT selector to plugins.include.txt but you _do not_ include any changes to the XSLT. This way the code will be available if needed but not included unless you _explicitly add JavaScript_ to require it _to an individual application or view_ as shown below.

To include the plugin on a run-time as required basis you would need to add a JavaScript file which does the require config and includes the required validation code. You then include this script using `WApplication.addJsFile(String fileName)` or any other method as described in the [wiki](https://github.com/BorderTech/wcomponents/wiki/Adding-custom-JavaScript).

The JavaScript to add should be something like:

``` javascript
require(["wc/compat/compat!"], function() {
    try {
        requirejs.config({
            paths: {
                validation: "/plugins/validation/js"
            }
        });
        require(["validation/all"]); // this will add all of the client validation modules.
    }
    catch (e) { // dispose of errors but you might want to do something more sensible with them.
        if (console && console.log) {
            console.log(e.message);
        }
    }
});
```
