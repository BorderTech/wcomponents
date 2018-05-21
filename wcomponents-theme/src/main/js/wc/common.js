/*
 * This is the one true meta module. It only makes sense to have one of these.
 *
 * This module exists solely for the purposes of optimization. Optimization here means reducing the
 * number of HTTP requests at runtime.
 *
 * You can override this module in your implementation an set whatever modules you wish to be included in the resulting
 * wc/common module. You would chose the modules most commonly loaded in your web application. Generally we would only
 * ever expect to see ui widgets listed here but you can list whatever floats your boat, EXCEPT anything loaded in
 * compat or fixes.js - they're polyfills for ES5, DOM methods etc.
 *
 * The layer **must** include "wc/i18n/i18n" and "wc/a8n", otherwise things will go terribly wrong :️( (well, things will go wrong
 * in testing if you don't include "wc/a8n").
 *
 * During the build the dependencies and sub-dependencies of this module will be calculated and packaged into
 * this file meaning you get a whole lot of modules for one single HTTP request.
 *
 * @ignore
 */
require(["wc/fixes", // you REALLY need this ...
	"wc/i18n/i18n", // ALWAYS REQUIRED IN THIS LAYER
	"wc/a8n", // ALWAYS REQUIRED IN THIS LAYER
	"wc/ui/backToTop",
	"wc/ui/field",
	"wc/ui/label",
	"wc/ui/tabset",
	"wc/ui/menu",
	"wc/ui/validation/all"], function (f) { // to apply the fixes
	"use strict";
	require(f);
});
