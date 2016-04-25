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
	if (has("trident") && dua.indexOf("IEMobile") >= 0) {  // IE Mobile only works 9+ anyway because of the XSLT
		addTest("iemobile", parseFloat(dua.split("IEMobile/")[1]) || parseFloat(dua.split("IEMobile ")[1]) || undefined);
	}

	addTest("edge", parseFloat(dua.split("Edge/")[1]) || undefined);
	/* end sniff additions */

	if (has("ie") || has("trident")) {
		result.push("wc/fix/disabledControl_ieAll");
		if (has("ie-compat-mode")) {
			/**
			 * We do not support compatibility view.
			 * Why? Well for the same reason you would not buy a Ferrari only to run it on low grade gasoline.
			 */
			global.setTimeout(function() {
				document.body.innerHTML = "<h1>You are in Compatibility View. Please use Standards Mode</h1>";
			}, 0);
		}
	}
	else if (has("webkit")) {
		result.push("wc/fix/focus_webkit");
	}

	if (!has("global-performance")) {
		result.push("wc/compat/navigationTiming");
	}
	return result;
});
