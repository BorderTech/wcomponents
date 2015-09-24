/**
 * Module provides some generic DOM manipulation on any element which is in an ajax response. This manipulation is done
 * before the element is inserted into the DOM. The module replaces some ARIA states and properties which may not be
 * able to be calculated during XSLT of a partial screen such as aria-controls.
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 */
define(["wc/dom/initialise", "wc/ui/ajaxRegion", "wc/ui/ajax/processResponse"],
	/** @param initialise wc/dom/initialise @param ajaxRegion wc/ui/ajaxRegion @param processResponse wc/ui/ajax/processResponse @ignore */
	function(initialise, ajaxRegion, processResponse) {
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
				var trigger, allElements, i, len, next, alias, id, url,
					CONTROLS = "aria-controls",
					ALIAS = "data-wc-ajaxalias",
					URL = "data-wc-ajaxurl";

				if (typeof documentFragment.querySelectorAll !== "undefined") {
					allElements = documentFragment.querySelectorAll("*");
				}
				else {
					allElements = documentFragment.getElementsByTagName("*");
				}
				for (i = 0, len = allElements.length; i < len; ++i) {
					next = allElements[i];
					id = next.id;
					if (id && !next.hasAttribute(CONTROLS) && (trigger = ajaxRegion.getTrigger(next, true))) {
						next.setAttribute(CONTROLS, trigger.loads.join(" "));
						if ((alias = trigger.alias) && !next.hasAttribute(ALIAS)) {
							next.setAttribute(ALIAS, alias);
						}
						/* debugging only - remove before delivery */
						/* if (element.id == id) {
							url = element.getAttribute(URL);
						}
						else
						{
							var descendant = document.getElementById(id);
							if (descendant) {
								url = descendant.getAttribute(URL);
							}
						} */
						/* almost end of debug stuff, remember to remove the ||url from the next line */
						if ((url = trigger.url/* ||url */) && !next.hasAttribute(URL)) {
							next.setAttribute(URL, url);
						}
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

		var /** @alias module:wc/ui/ajax/genericSubscriber */ instance = new GenericAjaxSubscriber();
		initialise.register(instance);
		return instance;
	});
