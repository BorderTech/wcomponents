/**
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
 * The layer **must** include "wc/i18n/i18n!" and "wc/ui/loading", otherwise things will go terribly wrong :(
 *
 * During the build the dependencies and sub-dependencies of this module will be calculated and packaged into
 * this file meaning you get a whole lot of modules for one single HTTP request.
 *
 * @example
 *	require(["wc/i18n/i18n!",
		"wc/ui/loading",
		"wc/ui/field",
		"wc/ui/label",
		"wc/ui/wrappedInput"]);
 *
 * @ignore
 */
require(["wc/i18n/i18n!",
	"wc/ui/loading",
	"wc/ui/field",
	"wc/ui/label",
	"wc/ui/wrappedInput",
	"wc/ui/menu"]);
