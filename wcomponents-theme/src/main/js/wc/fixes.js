/*
 * This module determines which browser specific fixes we need to load.
 * Modules loaded here are different to those loaded in the compat module because they are not needed by the scripting
 * environment per se, but rather they are needed a little later - before the user interacts with the page (hopefully)
 * but after other modules have initialized. The return object of this module is an array of module name strings. This
 * module should not be used except in the environment set-up in XSLT.
 *
 * @module
 * @requires module:wc/has
 */
define(["wc/has"], function(has) {
	"use strict";
	var global = window,
		result = [],
		n = global.navigator,
		dua = n.userAgent,
		addTest = has.add;

	/* Some additions to lib/dojo/sniff*/
	if (has("opera")) {
		if (dua.indexOf("Opera Mini") >= 0) {
			addTest("operamini", parseFloat(dua.split("Version/")[1]) || undefined);
		}
		if (dua.indexOf("Opera Mobi") >= 0) {
			addTest("operamobi", parseFloat(dua.split("Version/")[1]) || undefined);
		}
	}

	addTest("edge", function() {
		var matches = dua.match(/Edg.*\/(\S+)/);  // for example: Edge/1.2.3 or Edg/1.2.3
		if (matches) {
			return parseFloat(matches[1]);
		}
	});

	addTest("uc", function () {
		return !has("css-flex");
	});
	/* end sniff additions */

	if (has("webkit") && !has("edge")) {
		result.push("wc/fix/focus_webkit");
	} else if (has("ff")) {
		result.push("wc/fix/shiftKey_ff");
	}


	if (has("uc") && has("android")) {
		result.push("wc/fix/width_uc");
	}
	return result;
});
