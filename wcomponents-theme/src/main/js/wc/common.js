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
 * You *must* include
 *
 * * "wc/ui/loading" and at least one of:
 *   * a module which requires "wc/dom/formUpdateManager"; or
 *   *  a module which requires "wc/dom/cancelUpdate"; or
 *   * at least one of these directly.
 *
 * During the build the dependencies and sub-dependencies of this module will be calculated and packaged into
 * this file meaning you get a whole lot of modules for one single HTTP request.
 *
 * @example
 *	define(["wc/ui/template",
 *			"wc/dom/cancelUpdate",
 *			"wc/ui/calendar",
 *			"wc/ui/subordinate",
 *			"wc/ui/menu/bar"],
 *		function() {
*			return true;
*		});
 *
 * @ignore
 */
define(["wc/ui/template",
		"wc/dom/cancelUpdate",
		"wc/ui/loading",
		"wc/ui/backToTop",
		"wc/ui/label"],
	function() {
		"use strict";
		return 1;
	});
