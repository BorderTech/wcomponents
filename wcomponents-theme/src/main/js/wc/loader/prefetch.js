/**
 * @module
 */
define(function() {
	"use strict";
	/*
	 * Helper for public request method.
	 * @private
	 */
	function addLink(href, as, rel) {
		var link;
		if (href && document && document.head) {
			link = document.createElement("link");
			if (rel) {
				link.rel = rel;  // allow for proprietary mechanisms
			}
			else {
				link.rel = "preload";  // the standards way
			}
			if (as) {
				link.as = as;
			}
			link.href = href;
			document.head.appendChild(link);
		}
	}

	return {
		/**
		 * Ask the browser to prefetch a CACHEABLE resource.
		 * It's up to you to:
		 *  - make sure it is cacheable
		 *  - know the URL
		 *  - decide if the user is likely enough to need this resource that it's worth prefetching
		 *
		 * It's up to the browser to:
		 *   - determine when (and if) to perform the prefetch (it will find an "idle" time)
		 *   - cancel the prefetch if necessary (user performs an explicit fetch for example)
		 *
		 * It's up to the user to:
		 *   - Use a browser that supports prefetching
		 *   - Configure their browser to disable prefetching if they do not desire this behavior
		 *
		 * @param {string} href The URL to the cacheable resource to prefetch
		 * @param {string} [as] Optionally provide a request type: https://fetch.spec.whatwg.org/#concept-request-type
		 */
		request: function(href, as) {
			try {
				addLink(href, as);
				addLink(href, as, "subresource");
			}
			catch (ex) {
				console.warn(ex);  // don't die on prefetch exceptions, log 'em and move on
			}
		},
		/**
		 * Does the same as the "request" method but specifically for JS modules.
		 *
		 * Note that calling this for a module which is included in a layer file is a waste. Save it for modules that are unlikely to be inlcuded in a layer.
		 * With HTTP/2 this is likely to be EVERY module so I wouldn't sweat it too much.
		 *
		 * Why not just call "require(moduleId)"? Well you could BUT that is different because:
		 * - Require is not a "polite" request, the script WILL be fetched regardless of how much other activity is going on.
		 * - Require will actually execute the script.
		 * - Require will also fetch the script's dependencies.
		 * Most of the time you want require.
		 *
		 * @param moduleId The module (exactly as you would pass to the loader).
		 */
		jsModule: function(moduleId) {
			try {
				var href, scriptPath;
				if (moduleId && window.require && window.require.toUrl) {
					scriptPath = /.+\.js$/.test(moduleId) ? moduleId : moduleId + ".js";  // Add ".js" to the moduleId if it is not already present.
					href = window.require.toUrl(scriptPath);
					if (href) {
						this.request(href, "script");
					}
				}
			}
			catch (ex) {
				console.warn(ex);  // don't die on prefetch exceptions, log 'em and move on
			}
		}
	};
});
