## Introduction

The WComponents themes represent the client layer and are, therefore, the JavaScript, CSS, HTML etc which are used to
provide the user interface.

A theme is built within an implementation. A single WComponents framework development environment may contain many
implementations each building on the same common code. The client code is designed to be built using Maven, one cannot
simply tweak a JavaScript or CSS file and reload a screen to see the changes.

The main components of a theme are:

* XSLT to transform the WComponents XML into something a browser can use;
* JavaScript for functionality;
* CSS for appearance;
* images (usually SVG) for eye-candy;
* a few XML helpers; and
* Ant property files to tie them all together.

## About this documentation
This documentation is primarily aimed at WComponents theme developers. Some information is included which may be
pertinent to or primarily written for Java application developers using WComponents as the UI toolkit in their web
application. In general though, the Java API documentation should be more pertinent for those consumers.

## Philosophy

### Accessible by default

Accessiblity is a first class citizen. Indeed it may be the only first class citizen.

A significant ideal of WComponents is to create accessible web applications. The default theme is created in such a way
that it is easy to create WCAG2 AA level accessible applications. We have made a conscious decision to hang
functionality and appearance off of the accessibility requirements wherever possible.


### Standards based

We build all new theme code to published standards. Where a supported browser is lacking in one of these areas we will,
where possible, implement a fix which is delivered only to the browsers which are flawed.

### Usable

The original themes were built under the direction of a user centred design group and usability is still a key
requirement of all components. A component will be added to a theme only if it is required by an application and there
has been rigorous usability analysis and interaction design for the component.

### Lazy

Components will get wired up as late as possible and event listeners will be applied as high as possible in the DOM.
This is intended to make the framework more scalable than other frameworks which are not specifically for building
applications and are therefore more general purpose. Where possible controllers are singletons which avoids the memory
issues of having a controller for each instance of a particular component and makes WComponents very scaleable.

### Modular

WComponents is a framework for creating web applications, not web sites. This does not remove the need for modularity
so that only the components needed by an application are delivered by that application. Fixes are delivered based on
need and user interface components may be removed if they are not required.

### Internally documented

Every source file _should_ be internally documented. This should cover JSDoc for all functions in JavaScript; comments
of all blocks of related selectors in the CSS; header comments for every component transform file and for every named
transform and global variable in XSLT; and additional comments for any unusual, complex, controversial or convoluted
piece of code and for any fixes which have to be in the main source. Still working on this one.