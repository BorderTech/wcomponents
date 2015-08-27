## Including and excluding components

Each UI control consists of XSLT, JavaScript and CSS. The XSLT and CSS are included by default and if never required
may be added to the excludes.txt file. The JavaScript is only included if required.

### Requiring JavaScript

The XSLT sets up the JavaScript requires for every available component. This is done in the following XSLT files:

<dl>
<dt>wc.common.registrationScripts.xsl</dt>
<dd>This file sets up the registration and require processes. It should rarely need to be changed or overridden. The
	rest of these XSLT files are subsctions of this.</dd>

<dt>wc.common.registrationScripts.coreRegistrationScripts.xsl</dt>
<dd>This file tests for all components which require some form of registration for functionality. It does this by using
	a descendant XPath query. If a particular component is never required in a theme this file may be overridden and
	replaced with one which does not include a search for that file. In general these descendant selectors, whilst slow
	in XSLT terms, are not a significant burden in real time and so this file is usually left as-is. This file contains
	one subsection.</dd>

<dt>wc.common.registrationScripts.localRegistrationScripts.xsl</dt>
<dd>This file provides a null template by default. It is designed to make it easier to add registration for theme
	specific components as it means one does not have to override the entire
	wc.common.registrationScripts.coreRegistrationScripts.xsl merely to add new components.</dd>

<dt>wc.common.registrationScripts.requiredLibraries.xsl</dt>
<dd>This file provides all requirejs 'requires' for all components. It also uses descendant XPath queries and may be
	overridden. If the theme includes JavaScript in the file wc/common.js then the requires for the same
	JavaScript in this XSLT should be removed to improve load-time efficiency. It contains several sub-sections.</dd>

<dt>wc.common.registrationScripts.commonRequiredLibraries.xsl</dt>
<dd>This file provides requires for components which are commonly found in many views in typical applications. In many
	cases these would be included in wc/common.js and this file be replaced with a null template. If the theme does not
	use this file and overrides wc.common.registrationScripts.requiredLibraries.xsl then the call to this template may
	be omitted and the file name added to the excludes.txt. This will reduce the overall size of the XSLT and make a
	tiny improvement in processing speed.</dd>

<dt>wc.common.registrationScripts.localRequiredLibraries.xsl</dt>
<dd>This file provides a null template by default. It is designed to allow a theme to add new requires without making
	substantial changes to its inherited wc.common.registrationScripts.requiredLibraries.xsl. If the theme does not use
	this file and overrides wc.common.registrationScripts.requiredLibraries.xsl then the call to this template may be
	omitted and the file itself be added to the excludes.txt. This will reduce the overall size of the XSLT and make a
	tiny improvement in processing speed.</dd>

<dt>wc.common.registrationScripts.impl_registration.xsl</dt>
<dd>This file provides a null template by default. It allows for complete registration and require processing at the
	theme level. It is most commonly used to include the root XSLT named template for each required plugin.</dd>
</dl>

### Including CSS

All core CSS for all components is included by default. CSS for particular browsers and platforms is included as
required from the script wc/style/loader.js. To add new CSS or override existing files the CSS source files are simply
added to the CSS directory in the theme. Using the same file name as the default file will override that entire file,
adding a file with a different name will add the CSS. The CSS build will create one or more output CSS files in such a
way that any CSS source file named wc.\*.css will appear in the output before any css which does not begin with wc.
Within a named group the CSS files are concatenated in the order of an ANT fileset. The part of the name immediately
preceding the .css file extension may indicate that the CSS file is platform or browser specific.

The following CSS is built in the main build_css.xml ANT build:

<dl>
<dt>*.mob.scss</dt>
<dd>Creates a CSS file which is included if the browser identifies as identified as being a mobile browser being any one of the
	following:
<ul>
<li>any browser on an Android platform;</li>
<li>any browser on iOS;</li>
<li>IE Mobile;</li>
<li>Opera Mobile;</li>
<li>Opera Mini; or</li>
<li>Blackberry.</li></ul></dd>

<dt>*.dt.scss</dt>
<dd> Creates a CSS file which will be included if the browser identifies as not identified as being a mobile browser (being any
	browser which is not in the list above).</dd>

<dt>*.ff.scss</dt>
<dd> Creates a CSS file which is included in any Firefox browser on any platform.</dd>

<dt>*.safari.scss</dt>
<dd> Creates a CSS file which is included in any Safari browser on OS-X or iOS.</dd>

<dt>*.webkit.scss</dt>
<dd> Creates a CSS file which is included in any Webkit based browser on any platform based on dojo/sniff This includes
	Microsoft Edge.</dd>

<dt>*.ie/[0-9]+/.scss</dt>
<dd> Creates a file for each supported version of Internet Explorer where the version identified
is less than or equal to the version included in the CSS file name.</dd></dl>

The default theme of wc/style/loader.js loads CSS after the core CSS and in the following CSS (in order):

1. screenmob.css or wc.screendt.css (these are mutually exclusive);
2. screenie11.css if the browser identifies as Internet Explorer and the version is less than or equal to IE 11;
3. screenie10.css if the browser identifies as Internet Explorer and the version is less than or equal to IE 10;
4. screenie9.css if the browser identifies as Internet Explorer and the version is less than or equal to IE 9;
5. screenie8.css if the browser identifies as Internet Explorer and the version is less than or equal to IE 8;
6. screenwebkit.scss if the browser identifies as Webkit;
6. screenff.css if the browser identifies as Firefox on any platform;
7. screensafari.css if the browser identifies as Safari on any platform;
8. screenios.css if the browser is on iOS.

#### Changing supported browsers and platforms.

To Change the CSS added (by removing support for browsers which are not required or to add new browsers or platforms)
the theme must provide a configuration to wc/style/loader.js. This is done in the XSLT in a template in file
wc.ui.root.n.styleLoaderConfig.xsl.

The format of the configuration file is explained at length in the JSDoc for module wc/style/loader.js and the format
for including custom CSS is explained in the XSLT file wc.ui.root.n.styleLoaderConfig.xsl.

#### Excluding a component's CSS

If a particular component is never required its CSS may be excluded by adding it to the excludes.txt file. All CSS
files particular to a specified component (including the .mob.css etc if they are present) should be excluded. This
will make the CSS output files smaller and improve performance. This is particularly important for applications which
target mobile users.

### Including XSLT

All XSLT for all components is included by default. To add XSLT for a new component its file(s) is/are included in the
XSLT directory of the theme.

### Excluding XSLT

There are two options for excluding XSLT. Using the normal process of using excludes.txt  by adding the component's XSLT
file name(s). All helper templates and explicit decendant element templates should also be excluded but may be excluded
by the XSLT compressor if they are unused.

A preferred method for excluding a component's XSLT is by creating a masking template. Excluding a component's XSLT from
a theme does not prevent a Java application developer from using that component but will result in the XML being copied
to the output stream rather than being transformed.  In this scenario helper templates and distinct sub element
templates are added to the excludes.txt file but the top level element of the component is not excluded; instead the
template for the component is overridden with a template which provides no output.