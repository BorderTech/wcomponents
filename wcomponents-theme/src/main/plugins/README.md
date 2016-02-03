# WComponents build-time plugins

This directory contains plugins for the WComponents theme which are **optionally** included at build time.

## Why?

The plugins (originally there were more than one) may have onerous XSLT or JavaScript weight and so are not really
appropriate to include in WComponents unless they are required. They are build-time plugins rather than run-time plugins
because they require XSLT inclusion.

## How?

Including a build-time plugin requires these steps:

1. add the ANT selector for the plugin in yout theme's `plugins.include.txt` file;
2. make a copy of the file `wc.common.registrationScripts.impl_registration.xsl` into your theme's src/main/xslt folder
  and modify the template named `impl_registration` to call the plugin's root XSLT. This, by convention, is called "plugin_[PLUGIN_NAME]".
3. Rebuild your theme.

### Example - adding the client side validation plugin.

1. In your theme's plugins.include.txt add the following:
    ```
    validation/**
    ```

2. In your theme's `wc.common.registrationScripts.impl_registration.xsl` file:
  ``` xml
  <xsl:template name="impl_registration">
    <xsl:call-template name="plugin_validation"/>
  </xsl:template>
  ```

3. Rebuild your theme.
