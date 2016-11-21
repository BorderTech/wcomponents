define(["wc/has"],
	function (has) {
		"use strict";

		var sandbox,
			document, // purposely shadow document.
			html5Fix = null,
			noScope = null;

		if (has("ie") < 9) {
			require(["wc/fix/html5Fix_ie8", "wc/fix/noScope_ie8"], function (arg1, arg2) {
				html5Fix = arg1;
				noScope = arg2;
			});
		}

		/**
		 * "Safe" conversion of HTML to DocumentFragment.
		 * @module
		 * @requires module:wc/has
		 * @param {String) html the HTML to convert to a document fragment
		 */
		function toDocFragment(html) {
			var result,
				tmpDF,
				tmpElement,
				tmpContainer,
				next;

			if (!document) {
				document = window.document;
			}

			result = document.createDocumentFragment();
			preloadResources(html);

			if (has("ie") < 9) {
				if (!sandbox || !sandbox.parentNode) {  // check sandbox exists AND is in the DOM
					sandbox = document.createElement("iframe");
					sandbox.setAttribute("style", "display:none");
					sandbox.setAttribute("security", "restricted");
					document.body.appendChild(sandbox);
				}
				tmpDF = sandbox.contentWindow.document.createDocumentFragment();

				if (html5Fix) {
					html5Fix(tmpDF);
				}
			}
			else {
				tmpDF = document.createDocumentFragment();
			}

			tmpElement = (tmpDF.createElement ? tmpDF : document).createElement("div");
			tmpContainer = tmpDF.appendChild(tmpElement);
			if (noScope) {
				tmpContainer.innerHTML = noScope(html);
				noScope(tmpContainer);
			}
			else {
				tmpContainer.innerHTML = html;
			}
			while ((next = tmpContainer.firstChild)) {
				result.appendChild(next);
			}
			return result;


			/**
			 * This is an IE hack and is simply a waste of time to call in other browsers.
			 * Should load any resources (images) EXCEPT scripts, with requests that include cookies.
			 * Assumption is that these resources are cached, at least for the life of this page.
			 * Any external scripts will not be preloaded.
			 *
			 * @function
			 * @private
			 * @param {String} html A block of HTML to preprocess for IE.
			 */
			function preloadResources(html) {
				var preloadDf = document.createDocumentFragment(),
					container,
					SCRIPTRE = /<script.*?>.*?<\/script>/gi;  // html parsing in JS anyone?
				html = html.replace(SCRIPTRE, "");  // strip scripts
				container = document.createElement("div");
				preloadDf.appendChild(container);
				// container.innerHTML = html;
				container.insertAdjacentHTML("afterbegin", html);
			}
		}


		return toDocFragment;
	});
