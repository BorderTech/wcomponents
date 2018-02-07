define(["wc/dom/initialise",
	"wc/ui/ajax/processResponse",
	"wc/ui/getFirstLabelForElement",
	"wc/dom/Widget",
	"wc/dom/tag",
	"wc/ui/onchangeSubmit",
	"wc/dom/classList"],
	function(initialise, processResponse, getFirstLabelForElement, Widget, tag, onchangeSubmit, classList) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/fieldset~Fieldset
		 * @private
		 */
		function Fieldset() {
			var FSET = new Widget("fieldset");

			function makeLegend(el) {
				var label = el.firstChild,
					accesskey,
					labelContent,
					labelClass = "wc-moved-label ",
					WLABEL_CLASS = "wc-label";

				// quickly jump out if we have already got a legend in this fieldset.
				while (label && label.nodeType !== Node.ELEMENT_NODE) {
					label = label.nextSibling;
				}
				if (label && label.tagName === tag.LEGEND) {
					return;
				}
				label = getFirstLabelForElement(el);
				if (!label) {
					labelContent = el.getAttribute("aria-label");
					if (labelContent) {
						el.removeAttribute("aria-label");
					} else {
						labelContent = el.getAttribute("title");
						el.removeAttribute("title");
					}
					labelClass += "wc-off";
				} else {
					labelContent = label.innerHTML;
					labelClass += label.className;
					accesskey = label.getAttribute("data-wc-accesskey");
				}
				el.insertAdjacentHTML("afterbegin", "<legend class='" + labelClass + "'" + (accesskey ? " accesskey = '" + accesskey + "'" : "") +
					">" + labelContent + "</legend>");
				// label is now the first child of el.
				label = el.firstChild;
				classList.remove(label, WLABEL_CLASS);
				onchangeSubmit.warn(el, label);
			}

			function labelToLegend(element) {
				var el = element || document.body;
				if (element && FSET.isOneOfMe(el)) {
					makeLegend(el);
				} else {
					Array.prototype.forEach.call(FSET.findDescendants(el), makeLegend);
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
			 * Initialiser callback. For internal use only.
			 *
			 * @function module:wc/ui/fieldset.preInit
			 * @public
			 */
			this.preInit = function() {
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
		 * @requires module:wc/dom/tag
		 */
		var instance = new Fieldset();
		initialise.register(instance);
		return instance;
	});
