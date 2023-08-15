define([],
	function () {
		"use strict";

		var document; // purposely shadow document.

		/**
		 * "Safe" conversion of HTML to DocumentFragment.

		 * @param {String} html the HTML to convert to a document fragment
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

			tmpDF = document.createDocumentFragment();

			tmpElement = (tmpDF.createElement ? tmpDF : document).createElement("div");
			tmpContainer = tmpDF.appendChild(tmpElement);
			tmpContainer.innerHTML = html;
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
			 * @param {String} fromHTML A block of HTML to preprocess for IE.
			 */
			function preloadResources(fromHTML) {
				var preloadDf = document.createDocumentFragment(),
					container,
					SCRIPTRE = /<script.*?>.*?<\/script>/gi;  // html parsing in JS anyone?
				fromHTML = fromHTML.replace(SCRIPTRE, "");  // strip scripts
				container = document.createElement("div");
				preloadDf.appendChild(container);
				// container.innerHTML = html;
				container.insertAdjacentHTML("afterbegin", fromHTML);
			}
		}


		return toDocFragment;
	});
