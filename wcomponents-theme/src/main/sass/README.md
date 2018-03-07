# Sass Information

This is a work in progress. Feel free to fix stuff!

## File naming conventions

The WComponents roadmap includes moving to web components so eventually the CSS should become more modular. Until then we have the following conventions:

- Most Sass exists as partials.
- Files which have CSS which is shared by many components (or for common HTML strutures) is in `common/_FILENAME.scss` where `FILENAME` should give a rough idea of the purpose;
- Files which produce CSS for a particular component (or in a small number of cases two closely related components) is in `components\_wFILENAME.scss` where `_wFILENAME` is the WComponent  simple class name.
- Files which are for a particular platform or browser/family are placed in the `fix` directory one directory per fix type. Any platform or browser which requires override has a base level file; e.g. for UC Browser: `wc-uc.scss`. This file then imports all of the required partials. These files are imported by the JavaScript module `wc/loader/style`. If additional browsers need fixing then the config object for that module will have to be used as it will, by default, only fix those which are in this default list: MS Edge, MS IE 11 and UC Browser.
- Files used _only_ in debug mode are in the `debug` directory and imported into `wcdebug.scss`.

## Coding Standards

- All Sass must be in SCSS format.
- Generated CSS **must** comply with the [CSS standards](http://www.w3.org/Style/CSS/) and we use CSS3 except where vendor extensions are **absolutely required** for consistent implementation.
- Comments are Sass inline format (`//`).
- Sass should be linted using sass-lint and the rules in the project's `.sass-lint.yml` file.
- The preferred line length is 80 characters but this is flexible within sensible limits.
- Local variations in sass-lint rules are allowed but must be commented.

## Files

WComponents themes are designed to be extended and overridden so there is a lot of configuration available. The following files may help:

- `_implementation-vars.scss` is used in a descendant theme to override the WComponents defaults and add any which are required in the implementation. It is **strongly** recommended an implementation uses this file rather than copying and modifying `vars/_wc.scss`.
- 


## Things to do

- There is still more optimization which could happen.
- We need to get better documentation and maybe use SassDoc.
- A lot of this CSS was inherited and has grown like topsy, we are still not completely sure it is all needed.
