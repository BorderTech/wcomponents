/**
 * @module
 */
define(function() {

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
		}
	};
});
