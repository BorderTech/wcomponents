/**
 * A more or less annoyingly pointless module to provide some support for the limited XSLT options when content arrives
 * in an ajax repsonse with no context. In this case a WField may be an AJAX target so has to transform without knowing
 * its WFieldLayout context. Who thought this stuff up?
 *
 * @module
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/initialise
 */
define(["wc/ui/ajax/processResponse", "wc/dom/Widget", "wc/dom/initialise", "wc/dom/classList"],
	/** @param processResponse wc/ui/ajax/processResponse @param Widget wc/dom/Widget @param initialise wc/dom/initialise  @param classList wc/dom/classList @ignore */
	function(processResponse, Widget, initialise, classList) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/field~FieldAjaxSubscriber
		 * @private
		 */
		function FieldAjaxSubscriber() {
			var FIELD = new Widget("", "wc-field"),
				NO_PARENT_ATTRIB = "data-wc-nop",
				PLACEHOLDER;

			/**
			 * Before inserting a container into the DOM we may need to manipulate some properties which are not
			 * available to the XSLT as they are ancestor dependent. This will only be the case if the element being
			 * acted upon is the output of ui:field without a ui:fieldlayout parent.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced).
			 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
			 */
			function ajaxSubscriber(element, documentFragment) {
				var fieldElement,
					layout,
					pl;

				if (!(element && FIELD.isOneOfMe(element))) {
					return;
				}

				// we have to have a field layout to reference, it should be element's parent element and we only care if it has a data-wc-labelwidth attribute
				if (!(layout = element.parentNode)) {
					return;
				}

				if (documentFragment.getElementById) {
					// IE, perhaps some others
					fieldElement = documentFragment.getElementById(element.id);
				}
				else if (documentFragment.querySelector) {
					fieldElement = documentFragment.querySelector("#" + element.id);
				}

				if (!fieldElement) {
					return;
				}

				if (fieldElement.getAttribute(NO_PARENT_ATTRIB) === "true") {
					fieldElement.removeAttribute(NO_PARENT_ATTRIB);
					if (classList.contains(layout, "stacked")) {
						PLACEHOLDER = PLACEHOLDER || new Widget("", "wc_fld_pl");
						if ((pl = PLACEHOLDER.findDescendant(fieldElement))) {
							pl.parentElement.removeChild(pl);
						}
					}
				}
			}

			/**
			 * Initialise module by adding the ajax subscriber.
			 * @function module:wc/ui/field.postInit
			 * @public
			 */
			this.postInit = function() {
				processResponse.subscribe(ajaxSubscriber);
			};
		}
		var /** @alias module:wc/ui/field */ instance = new FieldAjaxSubscriber();
		initialise.register(instance);
		return instance;
	});
