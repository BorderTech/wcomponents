# Sass Information

This is a work in progress. The conversion from CSS with ANT properties to Sass is recent and ongoing. Feel free to fix stuff!

## File naming conventions

The WComponents roadmap includes moving to web components so eventually the CSS should become more modular. Until then we have the following conventions:

* files which are for a particular WComponent are named wc.ui.COMPONENT_NAME.OPTIONAL_PATTERN.scss;
* files which are for several components are named wc.common.FAMILY.OPTIONAL_PATTERN.scss;
* files which are for desktop only consumption are named \*.dt.scss;
* files which are for a particular platform or browser/family are named as:
    * if for Internet Explorer `/^.*\.ie[0-9]+\.scss$/`;
    * otherwise `/^.*\.pattern_[^\.]+\.scss$/`

    These files are loaded using the JavaScript module wc/loader/style so you might want to look at the doco for that!

### Platform/browser patterns

The JavaScript CSS loader will, by default, load all patterns based on a `has` test where the string to test is the same as the pattern extension (the bit between `pattern_` and `.`). If you need more complex load rules you can set a load object which can contain much more specialised `has` test and media rules. See the module documentation for (much) more.

For example if your Sass includes a file named `foo.pattern_ff.scss` then the build process will generate a concatenated CSS file `screen.ff.css` and the CSS loader will be told to load this file if `has("ff")` is true.

If you need CSS for IE for IE8 or before I recommend adding a conditional comment in the XSLT rather than relying on the style loader as early versions of IE are rubbish at using CSS loaded by JavaScript (even though what little IE8 CSS left in the core is loaded using the style loader).

## Coding Standards

* All Sass must be in SCSS format.
* Generated CSS **must** comply with the [CSS standards](http://www.w3.org/Style/CSS/) and we use CSS3 except where vendor extensions are **absolutely required** for consistent implementation.
* The preferred line length is 120 characters but this is flexible within sensible limits - go over by a bit if you have to to keep a selector on one line.
* Sass should be linted. The lint rules are here to make the SCSS more readable for humans and are mostly the defaults for scss-lint; exceptions to which are documented in the included `.scss-lint.yaml` configuration file.
* Local variations in scss-lint rules are allowed but must be commented. Most commonly this would be to allow local !important rules.

### Comments

* Each CSS and SCSS file must commence with a CSS comment which includes **only** its file name. This makes CSS debugging much easier (remember that CSS style comments are stripped in the final compressed output but Sass comments are stripped in all circumstances).
* Comments **must be in Sass single line** style unless they are pertinent to debugging and then they must have a local override of the `Comments` scss-lint rule. If a particular declaration in a declaration block requires a comment it **must be in Sass single line form** under all circumstances.
* There must be a single space between the start of a comment and the first character of the comment content.
* If a rule is commented there must not be any empty lines between the last line of the comment and the first selector.
* Do not place a CSS style comment inside a declaration block (it causes issues with Safari's developer tools). Sass single line comments are permitted inside rule blocks.
* If a particular selector in a multi-selector rule requires a comment it should be placed on the same line as the selector.

#### Example

``` scss
/* wc.my.component.scss */
@import 'mixins_common.scss';

//scss-lint:disable Comment
/* This declaration block does something odd and I need to know about it in debug mode */
//scss-lint:enable Comment
.foo, // foo class is for all foos!
.bar > .somelongclassname [aria-selected='true'] > :first-child {
    // ....
    line-height: -2px; // This is needed for ...
    // ....
    }
```

## Media rules and specific CSS: when to use which

We want to load the smallest amount of CSS we can whilst still struggling with HTTP 1.1 optimisation by concatenation. For this reason we put most platform specific CSS into \*.dt.scss or pattern\_\* files. There are times, however, when media queries are warranted: one may, for example, want to apply a different appearance on a desktop if the browser window has been made small or if the monitor is huge or dense. We also add generic mobile CSS into the core Sass files using media queries.

The file mixins_respond.scss has some skeletal mixins for applying some `@media` based CSS. Feel free to extend these!

The take-home message of this is: there is no hard and fast rule for when to use `@media` compared with a platform specific file. Just use your common sense and try to keep the amount of CSS sent to the browser to a minimum as there is already too much of it.

## Things to do

* The conversion from CSS is still early so there is a lot of optimization which could happen.
* We need to get on top of the documentation: there is currently no SassDoc for example.
* A lot of this CSS was inherited and has grown like topsy so we also need to standardize class names and some data-* attribute names.

## References

* [W3C CSS standards](http://www.w3.org/Style/CSS/)
* [scss-lint default configuration](https://github.com/brigade/scss-lint/blob/master/config/default.yml)
* [scss-linter documentation](https://github.com/brigade/scss-lint/blob/master/lib/scss_lint/linter/README.md)
