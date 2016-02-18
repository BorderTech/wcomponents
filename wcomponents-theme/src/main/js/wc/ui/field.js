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
			var FIELD = new Widget("li", "wc-field"),
				NO_PARENT_ATTRIB = "data-wc-nop";

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
					labelWidth,
					inputContainer,
					labelContainer,
					isStacked,
					PC = "%";

				if (element && FIELD.isOneOfMe(element)) {
					// we have to have a field layout to reference, it should be element's parent element and we only care if it has a data-wc-labelwidth attribute
					if (!(((layout = element.parentNode)) && (labelWidth = layout.getAttribute("data-wc-labelwidth")))) {
						return;
					}

					labelWidth = parseInt(labelWidth, 10);
					if (!labelWidth || isNaN(labelWidth)) {
						return;
					}

					if (documentFragment.getElementById) {
						// IE, perhaps some others
						fieldElement = documentFragment.getElementById(element.id);
					}
					else if (documentFragment.querySelector) {
						fieldElement = documentFragment.querySelector("#" + element.id);
					}

					if (fieldElement && fieldElement.getAttribute(NO_PARENT_ATTRIB) === "true") {
						fieldElement.removeAttribute(NO_PARENT_ATTRIB);
						isStacked = classList.contains(layout, "stacked");

						if (!isStacked && (labelContainer = fieldElement.firstChild)) {
							labelContainer.style.width = labelWidth + PC;
						}

						if ((inputContainer = fieldElement.lastChild)) {
							inputContainer.style.width = (100 - labelWidth) + PC;
							inputContainer.style.maxWidth = (100 - labelWidth) + PC;
							// stacked field layouts or inputs where the label is rendered off-screen need a left margin.
							if (isStacked || (labelContainer && classList.contains(labelContainer, "wc_off"))) {
								inputContainer.style.marginLeft = labelWidth + PC;
							}
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
