# Including and excluding components

Each UI control consists of XSLT, JavaScript and CSS. The XSLT and CSS are included by default and if never required
may be added to the excludes.txt file. The JavaScript is only included if required.

## Requiring JavaScript

The XSLT sets up the JavaScript requires for every available component. This is done in the following XSLT files:

<dl>
<dt>wc.common.registrationScripts.xsl</dt>
<dd>This file sets up the registration and require processes. It should rarely
need to be changed or overridden. The rest of these XSLT files are subsctions of this.</dd>

<dt>wc.common.registrationScripts.coreRegistrationScripts.xsl</dt>
<dd>This file tests for all components which require some
form of registration for functionality. It does this by using a descendant XPath query. If a particular component
is never required in an implementation this file may be overridden and replaced with one which does not include a
search for that file. In general these descendant selectors, whilst slow in XSLT terms, are not a significant
burden in real time and so this file is usually left as-is. This file contains one subsection.</dd>

<dt>wc.common.registrationScripts.localRegistrationScripts.xsl</dt><dd>This file provides a null template by default. It
is designed to make it easier to add registration for implementation specific components as it means one does
not have to override the entire wc.common.registrationScripts.coreRegistrationScripts.xsl merely to add new
components.</dd>

<dt>wc.common.registrationScripts.requiredLibraries.xsl</dt><dd>This file provides all requirejs 'requires' for all
components. It also uses descendant XPath queries and may be overridden. If the implementation includes JavaScript
in the file wc.common.js then the requires for the same JavaScript in this XSLT should be removed to improve
load-time efficiency. It contains several sub-sections.</dd>

<dt>wc.common.registrationScripts.commonRequiredLibraries.xsl</dt><dd>This file provides requires for components which are
commonly found in many views in typical applications. In many cases these would be included in wc.common.js and
this file be replaced with a null template. If the implementation does not use this file and overrides
wc.common.registrationScripts.requiredLibraries.xsl then the call to this template may be omitted and the file
name added to the excludes.txt. This will reduce the overall size of the XSLT and make a tiny improvement in
processing speed.</dd>

<dt>wc.common.registrationScripts.localRequiredLibraries.xsl</dt><dd>This file provides a null template by default. It is
designed to allow an implementation to add new requires without making substantial changes to its inherited
wc.common.registrationScripts.requiredLibraries.xsl. If the implementation does not use this file and overrides
wc.common.registrationScripts.requiredLibraries.xsl then the call to this template may be omitted and the file
itself be added to the excludes.txt. This will reduce the overall size of the XSLT and make a tiny improvement
in processing speed.</dd>

<dt>wc.common.registrationScripts.impl_registration.xsl</dt><dd>This file provides a null template by default. It allows for
complete registration and require processing at the implementation level. It is most commonly used to include
the root XSLT named template for each required plugin.</dd></dl>

## Including CSS

All core CSS for all components is included by default. CSS for particular browsers and platforms is included as
required from the script wc.style.loader.js. To add new CSS or override existing files the CSS source files are simply
added to the CSS directory in the implementation. Using the same file name as the default file will override that
entire file, adding a file with a different name will add the CSS. The CSS build will create one or more output CSS
files in such a way that any CSS source file named wc.\*.css will appear in the output before any css which does not
begin with wc. Within a named group the CSS files are concatenated in the order of an ANT fileset. The part of the
name immediately preceding the .css file extension may indicate that the CSS file is a platform or

The following CSS is built in the main build_css.xml ANT build:

<dl>
<dt>*.mob.css</dt><dd>Creates a CSS file which is included if the browser is identified as being a mobile browser being any
one of the following:
<ul>
<li>any browser on an Android platform;</li>
<li>any browser on iOS;</li>
<li>IE Mobile;</li>
<li>Opera Mobile;</li>
<li>Opera Mini; or</li>
<li>Blackberry.</li></ul></dd>

<dt>*.dt.css</dt>
<dd> Creates a CSS file which will be included if the browser is not identified as being a mobile browser
(being any browser which is not in the list above).</dd>

<dt>*.ff.css</dt>
<dd> Creates a CSS file which is included in any Firefox browser on any platform.</dd>

<dt>*.safari.css</dt>
<dd> Creates a CSS file which is included in any Safari browser on OS-X or iOS.</dd>

<dt>*.ie/[0-9]+/.css</dt>
<dd> Creates a file for each supported version of Internet Explorer where the version identified
is less than or equal to the version included in the CSS file name.</dd></dl>

Note that we do not build any \*.webkit.css files. This is merely a pragmatic decision based on webkit (and blink) based
browsers being the most popular browser family in use worldwide. We include any required webkit overrides in the core
CSS as they are the ones most likely to be loaded. It just so happens that these browsers (with the exception of
Safari) are also the least quirky and need fewest overrides to standard CSS.

The default implementation of wc.style.loader.js loads CSS after the core CSS and in the following CSS (in order):

1. screenmob.css or wc.screendt.css (these are mutually exclusive);
2. screenie11.css if the browser is Internet Explorer and the version is less than or equal to IE 11;
3. screenie10.css if the browser is Internet Explorer and the version is less than or equal to IE 10;
4. screenie9.css if the browser is Internet Explorer and the version is less than or equal to IE 9;
5. screenie8.css if the browser is Internet Explorer and the version is less than or equal to IE 8;
6. screenff.css if the browser is Firefox on any platform;
7. screensafari.css if the browser is Safari on any platform;
8. screenios.css if the browser is on iOS.

### Changing supported browsers and platforms.

To Change the CSS added (by removing support for browsers which are not required or to add new browsers or platforms)
the implementation must provide a configuration to wc.style.loader.js. This is done in the XSLT in a template in file
wc.ui.root.n.styleLoaderConfig.xsl.

The format of the configuration file is explained at length in the JSDoc in wc.style.loader.js and the format for
including custom CSS is explained in the XSLT file wc.ui.root.n.styleLoaderConfig.xsl. This is a précis:

The config object is created in XSLT in wc.ui.root.n.makeRequireConfig.xsl. The helper template in
wc.ui.root.n.styleLoaderConfig.xsl should contain only the name:value pairs for changing the IE version support and
custom browser/platform (other than IE) screen CSS. The implementation may override one or both of the defaults for
these properties. The format for these are:

<pre><code>
"ie" : ["IE_VERSION", "IE_VERSION"] //WHERE IE_VERSION is a version number of
                                    //Internet explorer prefixed with the
                                    //letters "ie" and which complies with the
                                    //regular expression /ie[0-9]+/.
"screen": {Object}//Where the properties of the object define the custom CSS to load.
</code></pre>

The format of the "screen" object is as follows:

<dl>
<dt>key</dt><dd>The file name extension used in the CSS build. This is the bit immediately before the '.css' part of the
built artifact's file name (eg 'ff').</dd>

<dt>value</dt><dd>The has() argument and optional comparison value. This can be either of:
* a string to do a test without version (eg 'ff'); or
* an object with a mandatory property 'test' and optional properties \{int\} 'version' and \{String\} 'media'.
  for example the following includes Firefox of any version, Safari version 6, print styles for any
  mac and styles for safari version 8 including a media selector for large screens:

<pre>
    {"ff":"ff",
    "safari6":{"test":"safari",
          "version":6},
    "macprint":{"test":"mac",
           "media":"print"},
    "saf8big":{"test":"safari",
          "version":8,
          "media":"@media only screen and (min-device-width:2560px)"}}
</pre>

If either one of these ("ie" or "css") is present the defaults for that property then the defaults are no longer
applied **the values replace the defaults, they do not append to them**. This means that if one wants to use all of the
defaults and add some more support one would have to include the defaults in the implementation override.

To remove all support pass in an empty value. To remove all custom IE support use `"ie":\[\]` and to remove all
other customised CSS use `"css":\{\}`</dd></dl>

### Excluding a component's CSS

If a particular component is never required its CSS may be excluded by adding it to the excludes.txt file. All CSS
files particular to a specified component (including the .mob.css etc if they are present) should be excluded. This
will make the CSS output files smaller and improve performance. This is particularaly important for applications which
target mobile users.

## Including XSLT

All XSLT for all components is included by default. To add XSLT for a new component its file(s) is/are included in the
XSLT directory of the implementation.

### Excluding XSLT

To exclude a component's XSLT simply add its file name to the excludes.txt file. All helper templates and explicit
decendant element templates should also be excluded.

Excluding a component's XSLT from an implementation does not prevent a Java application developer from using that
component but will result in the XML being copied to the output stream rather than being transformed. To prevent this
an alternate approach to excluding XSLT is recommended. In this scenario helper templates and distinct sub element
templates are added to the excludes.txt file but the top level element of the component is not excluded; instead the
template for the component is overidden with a template which provides no output.