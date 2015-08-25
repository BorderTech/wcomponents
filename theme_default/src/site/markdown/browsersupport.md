##Default Browser Support

Out of the box WComponents default theme will support relatively modern browsers. **Some** support is provided for
obsolete versions of Internet Explorer which are commonly found in institutional environments for using internal
applications.

###Default support matrix

The latest versions of following browsers are tested and known to work:

* Google Chrome (tested on Windows, OSX, Linux Mint, Android, iOS);
* Firefox (tested on Windows, OSX, Linux Mint);
* Safari (OS X) and Mobile Safari (iPhone and iPad);
* UC on iOS and Android;
* Android browser;
* Internet Explorer 11; and
* Mobile IE on Windows phone 8 (with caveats).

Internet Explorer 8 is known to work and have few CSS issues. There is no default support for IE9 or IE10 but fixes to
known issues from earlier versions of WComponents have been retained.

Other browsers and platforms may be made to work as required. WComponents does not, by default, provide **any** out of
the box support for versions of Internet Explorer before version 8. It may be extended to provide this support within an
implementation. As of February 2015 at least one implementation of WComponents provided support for IE6 (yes, February 2015!).

## JavaScript compatibility

Fixes and polyfills are included where required and should be based on feature detection. In some cases a feature may
need to be included for a specific browser based on conditions which are not amenable to feature detection. In these
cases, and for all CSS inclusions, the feature is included using a test based on hasjs and dojo/sniff.

There are three groups of scripts used to provide these fixes:

1. wc/compat/*.js provide browser compatibility polyfills. All of these polyfills are included based on feature testing.
2. wc/ecma5/*.js provide polyfills for ECMA5 features which may be missing from a particular browser. All of these
polyfills are included based on feature testing.
3. wc/fix/*.js provides fixes for buggy implementations. The file names provide an indication of the browsers or
rendering engines known to require the fix but the fixes are, where possible, based on feature testing rather than
sniffing.

## CSS support

In principal additional CSS support is similar to JavaScript support. There are some nuances around providing support
and 'fixes' for specific browsers and platforms. All of these 'fixes' are, of necessity, based on dojo/sniff tests.

CSS inclusion is currently undertaken in the following manner:

1. The core CSS for all platforms and browsers is included using a link element.
2. All other framework CSS is then included by the JavaScript module wc/loader/style.js. This module allows for
extensive configuration to include CSS based on platform, browser and media query.

### Defining browser or platform specific CSS

The CSS is built according to file name pseudo-extension. For example:

* \*.ie8.css applies to IE8 and before
* \*.ff.css applies to Firefox
* \*.webkit.css applies to all Webkit browsers
* \*.safari.css applies to Safari on OSX and iOS
* \*.ios.css applies to any browser on iOS

This list may be extended at will to include any pseudo-extension to apply to any condition allowed by wc/loader/style.js.
There are two 'fixed' extensions which are mutually exclusive:

* \*.mob.css contains CSS which is applied to any browser which is deemed to be mobile based on dojo/sniff. The following
are currently deemed to be mobile:
    * any browser on Android;
    * any browser on iOS;
    * Mobile IE;
    * Opera Mobile and Opera Mini on any platform; and
    * Blackberry.
* \*.dt.css contains CSS which is applied to any desktop user agent (being any user agent not deemed to be mobile as
described below).
