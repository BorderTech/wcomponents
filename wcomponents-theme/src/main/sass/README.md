# Sass Information

<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [File naming conventions](#file-naming-conventions)
	- [Platform/browser patterns](#platformbrowser-patterns)
	- [Media rules or specific files](#media-rules-or-specific-files)
- [Coding Standards](#coding-standards)
- [Things to do](#things-to-do)

<!-- /TOC -->
This is a work in progress. Feel free to fix stuff!

## File naming conventions

The WComponents roadmap includes moving to web components so eventually the CSS should become more modular. Until then we have the following conventions:

- most files which are named `wc.COMPONENT_NAME.OPTIONAL_PATTERN.scss`;
- files which are for a particular platform or browser/family are named as:
    * if for Internet Explorer `/^.*\.ie[0-9]+\.scss$/`;
    * otherwise `/^.*\.pattern_[^\.]+\.scss$/`

    These files are loaded using the JavaScript module wc/loader/style so you might want to look at the doco for that.

### Platform/browser patterns

The JavaScript CSS loader will, by default, load all patterns based on a `has` test where the string to test is the same as the pattern extension (the bit between `pattern_` and `.`). If you need more complex load rules you can set a load object which can contain much more specialised `has` test and media rules. See the module documentation for (much) more.

For example if your Sass includes a file named `foo.pattern_ff.scss` then the build process will generate a concatenated CSS file `screen.ff.css` and the CSS loader will be told to load this file if `has("ff")` is true.

If you need CSS for IE for IE8 or before I recommend adding a conditional comment in the XSLT rather than relying on the style loader as early versions of IE are rubbish at using CSS loaded by JavaScript (even though what little IE8 CSS left in the core is loaded using the style loader).

### Media rules or specific files

We want to load the smallest amount of CSS we can whilst still struggling with HTTP 1.1 optimisation by concatenation. For this reason we put most platform/browser specific CSS into pattern\_\* files. There are times, however, when media queries are warranted: one may, for example, want to apply a different appearance on a desktop if the browser window has been made small or if the monitor is huge or dense. We also add generic mobile CSS into the core Sass files using media queries.

The file mixins-respond.scss has some skeletal mixins for applying some `@media` based CSS. Feel free to extend these.

The take-home message of this is: there is no hard and fast rule for when to use `@media` compared with a platform specific file. Just use your common sense and try to keep the amount of CSS sent to the browser to a minimum as there is already too much of it.

## Coding Standards

- All Sass must be in SCSS format.
- Generated CSS **must** comply with the [CSS standards](http://www.w3.org/Style/CSS/) and we use CSS3 except where vendor extensions are **absolutely required** for consistent implementation.
- Comments are Sass inline format (`//`).
- Sass should be linted using sass-lint and the rules in the project's `.sass-lint.yml` file.
- The preferred line length is 80 characters but this is flexible within sensible limits.
- Local variations in sass-lint rules are allowed but must be commented.

## Things to do

-There is quite a lot of optimization which could happen.
- We need to get some documentation.
- A lot of this CSS was inherited and has grown like topsy, we are not completely sure it is all needed.
- Finish making color rules optional using a consistent value (-1) for any color to be completely ignored.
