/**
 * This loader plugin module determines the the dependencies we need to load the configure JS environment with the necessary
 * features expected by the rest of the codebase, i.e. polyfills.
 *
 * It is intended that this loader plugin will be a loader dependency <http://requirejs.org/docs/api.html#config-deps>
 * it **must** run before other modules because it loads the compatibility modules and fixes required for
 * this browser to handle the rest of the codebase.
 *
 * You **must not** load anything here that needs to wire up events (those are fixes, not compatibility
 * code). This is for basic scripting API support.
 *
 * Many of the tests are written by us for our own specific needs however some are also lifted with little or no
 * change from the has project: <https://github.com/phiggins42/has.js/>
 *
 * Read the source Luke!
 *
 * @module
 * @private
 * @param has @ignore
 */
define(["wc/has"], function(has) {
	"use strict";
	var result = ["lib/dojo/sniff"];

	(function(addtest) {

		addtest("css-flex", function(g, d) {
			var c,
				start,
				end;
			if (!g.getComputedStyle) {
				return false;
			}
			c = d.createElement("div");
			try {
				d.body.appendChild(c);
				start = g.getComputedStyle(c, null).display;
				c.style.display = "flex";
				end = g.getComputedStyle(c, null).display;
				return (start !== end);
			} catch (e) {
				return false;
			} finally {
				d.body.removeChild(c);
			}
		});

	})(has.add);



	result.load = function (id, parentRequire, callback) {
		parentRequire(result, callback);
	};
	return result;
});
