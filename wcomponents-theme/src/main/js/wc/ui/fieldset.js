define(["wc/dom/initialise",
		"wc/ui/ajax/processResponse",
		"wc/ui/getFirstLabelForElement",
		"wc/dom/Widget"],
	function(initialise, processResponse, getFirstLabelForElement, Widget) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/fieldset~Fieldset
		 * @private
		 */
		function Fieldset() {
			var FSET = new Widget("fieldset"),
				LEGEND = new Widget("legend");

			function makeLegendFromLabel(el) {
				var label = getFirstLabelForElement(el),
					accesskey;
				if (!label || LEGEND.isOneOfMe(label)) {
					return;
				}
				accesskey = label.getAttribute("data-wc-accesskey");
				el.insertAdjacentHTML("afterbegin", "<legend class='wc-off'" + (accesskey ? " accesskey = '" + accesskey + "'" : "") + ">" + label.innerHTML + "</legend>");
			}

			function labelToLegend(element) {
				var el = element || document.body;
				if (element && FSET.isOneOfMe(el)) {
					makeLegendFromLabel(el);
				}
				else {
					Array.prototype.forEach.call(FSET.findDescendants(el), makeLegendFromLabel);
				}
			}

			/**
			 * Gets the {@link module:wc/dom/Widget} descriptor for a fieldset element.
			 *
			 * @function module:wc/ui/fieldset.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget} The description of a fieldset.
			 */
			this.getWidget = function() {
				return FSET;
			};

			/**
			 * Initialiser callback to wire in subscribers.
			 *
			 * @function module:wc/ui/fieldset.postInit
			 * @public
			 */
			this.postInit = function() {
				labelToLegend();
				processResponse.subscribe(labelToLegend, true);
			};
		}

		/**
		 * Provides functionality peculiar to FIELDSET elements around ensuring that the contained controls and legends
		 * are correctly synchronised after AJAX transactions.
		 *
		 * @module
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires module:wc/dom/Widget
		 */
		var instance = new Fieldset();
		initialise.register(instance);
		return instance;
	});
