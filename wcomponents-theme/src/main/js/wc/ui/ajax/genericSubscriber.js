define(["wc/dom/initialise", "wc/ui/ajax/processResponse"],
	function(initialise, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/ajax/genericSubscriber~GenericAjaxSubscriber
		 * @private
		 */
		function GenericAjaxSubscriber() {
			/**
			 * Before inserting an element into the DOM we may need to add a controller attribute which may not be
			 * available to the XSLT. We do not add a controller attribute if the element already has one as it has
			 * either already been wired up by an ajaxTrigger in the XML or it controls something else (eg calendar date
			 * picker) and is not an eligible ajax trigger.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced): not needed here.
			 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
			 */
			function ajaxSubscriber(element, documentFragment) {
				var el,
					LIVE = "aria-live",
					liveSetting = element.getAttribute(LIVE);

				if (liveSetting) {
					if (documentFragment.getElementById) {
						// IE, perhaps some others
						el = documentFragment.getElementById(element.id);
					} else if (documentFragment.querySelector) {
						el = documentFragment.querySelector("#" + element.id);
					}
					if (el) {
						el.setAttribute(LIVE, liveSetting);
					}
				}
			}

			/**
			 * initialiser callback to subscribe to {@link module:wc/ui/ajax/processResponse}.
			 * @function module:wc/ui/ajax/genericSubscriber.postInit
			 */
			this.postInit = function() {
				processResponse.subscribe(ajaxSubscriber);
			};
		}

		/**
		 * Module provides some generic DOM manipulation on any element which is in an ajax response. This manipulation is done
		 * before the element is inserted into the DOM. The module replaces some ARIA states and properties which may not be
		 * able to be calculated during XSLT of a partial screen such as aria-controls.
		 *
		 * @module
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/ajax/processResponse
		 */
		var instance = new GenericAjaxSubscriber();
		initialise.register(instance);
		return instance;
	});
