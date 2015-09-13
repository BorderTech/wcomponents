# Sass Information

This is a work in progress. The conversion from CSS with ANT properties to Sass is recent and ongoing. Feel free to fix
stuff!

## Naming Conventions
The WComponents roadmap includes moving to web components so eventually the CSS should become more modular. Until then
we have the following conventions:

* files which are for a particular WComponent are named wc.ui.COMPONENT_NAME.OPTIONAL_PATTERN.scss;

* files which are for several distinct components are named wc.common.FAMILY.OPTIONAL_PATTERN.scss;

* files which are for mobile consumption only are named \*.mob.scss;

* files which are for desktop only consumption are named \*.dt.scss;

* files which are for a particular platform or browser/family are named as:
    * if for Internet Explorer `/^.*\.ie[0-9]+\.scss$/`;
    * otherwise `/^.*\.pattern_[^\.]+\.scss$/`

    These files are loaded using the JavaScript module wc/loader/style so you might want to look at the doco for that!.

### Platform/browser patterns
The JavaScript CSS loader will, by default, load all patterns based on a `has` test where the string to test is the same
as the pattern extension (the bit between `pattern_` and `.`). If you need more complex load rules you can set a load
object which can contain much more specialised `has` test and media rules. See the module documentation for (much) more.

For example if your Sass includes a file named `foo.pattern_ff.scss` then the build process will generate a concatenated
CSS file `screen.ff.css` and the CSS loader will be told to load this file if `has("ff")` is true.

## Coding Standards
See the wcomponents-theme Maven site.

## Media rules and specific CSS: when to use which
We want to load the smallest amount of CSS we can whilst still struggling with HTTP 1.1 optimisation by concatenation.
For this reason we put most platform specific CSS into \*.dt.scss or \*.mob.scss files. THere are times, however, when
media queries are warranted: one may, for example, want to apply a different appearance on a desktop if the browser
window has been made small.

The file mixins_respond.scss has a skeleton mixin for applying some @media based CSS. At the moment it's most common use
is to make columnar layouts into block layouts on small screens, but it is not intended to be limited to this.

The take-home message of this is: there is no hard and fast rule for when to use @media compared with a platform
specific file. Just use your common sense and try to keep the amount of CSS sent to the browser to a minimum as there is
already too much of it.

## Things to do
* The conversion from CSS is still early so there is a lot of optimization which could happen.
* We need to get on top of the documentation: there is currently no SassDoc for example.
* A lot of this CSS was inherited and has grown like topsy so we also need to standardize variable names, class names
etc.
