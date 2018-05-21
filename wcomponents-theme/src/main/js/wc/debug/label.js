define(["wc/dom/classList",
	"wc/dom/initialise",
	"wc/dom/tag",
	"wc/ui/ajax/processResponse",
	"wc/i18n/i18n",
	"wc/ui/getFirstLabelForElement",
	"wc/ui/getVisibleText",
	"wc/timers"],
	function (classList, initialise, tag, processResponse, i18n, getFirstLabelForElement, getVisibleText, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/debug/label~Label
		 * @private
		 */
		function Label() {
			var TAGS = [tag.INPUT, tag.TEXTAREA, tag.SELECT, tag.FIELDSET],
				IMG_QS,
				AFTER_END = "afterend";

			function nonEmptyAttribute(element, attr) {
				var content = element.getAttribute(attr);
				return content && content.trim();
			}

			function insertLegend(fieldset) {
				fieldset.insertAdjacentHTML("afterbegin", "<legend class='wc-err'>" + i18n.get("requiredLabel") + "</legend>");
			}

			function insertLabel(input) {
				var id = input.id,
					youHaveBeenNaughty = "<label class='wc-label wc-err'",
					endLabel = "</label>",
					isCheckRadio = input.type === "checkbox" || input.type ==="radio";
				if (id) {
					youHaveBeenNaughty += " for='" + input.id + "'";
				}

				youHaveBeenNaughty += ">" + i18n.get("requiredLabel");

				if (id || isCheckRadio) {
					youHaveBeenNaughty += endLabel;
				}

				input.insertAdjacentHTML(isCheckRadio ? AFTER_END : "beforebegin", youHaveBeenNaughty);

				if (!(id || isCheckRadio)) {
					input.insertAdjacentHTML(AFTER_END, endLabel);
				}
			}

			function isLabelEmpty(label) {
				var content, images, i;
				if (!(content = getVisibleText(label, false, true))) {
					// is there an image with an alt attribute?
					IMG_QS = IMG_QS || "img[alt]";
					images = label.querySelectorAll(IMG_QS);
					for (i = 0; i < images.length; ++i) {
						if ((content = images[i].getAttribute("alt")) && content.trim()) {
							return false;
						}
					}
					return true;
				}
				return false;
			}

			function isLabelMissing(input) {
				var label = getFirstLabelForElement(input);

				if (label) {
					if (isLabelEmpty(label)) {
						label.insertAdjacentHTML("beforeend", i18n.get("requiredLabel"));
						classList.add(label, "wc-err");
					}
					return false;
				}
				return true;
			}

			function testLabel(element) {
				// hidden inputs do not need to be labelled.
				if (element.type === "hidden" || classList.contains(element, "wc_nolabel")) {
					return;
				}
				// Any one (or more) of aria-label, title or aria-describedby is OK
				if (nonEmptyAttribute(element, "aria-label") || nonEmptyAttribute(element, "title") || nonEmptyAttribute(element, "aria-describedby")) {
					// We have something, it may not be great but it is there.
					return;
				}

				// if the label is missing, create a VERY NASTY PLACEHOLDER LABEL!!
				if (isLabelMissing(element)) {
					if (element.tagName === tag.FIELDSET) {
						insertLegend(element);
					} else {
						insertLabel(element);
					}
				}
			}

			function flagBadLabels(container) {
				var inside = container || document,
					candidates,
					tagName;

				if (!inside.querySelectorAll) {
					// nothing gets in here.
					return;
				}

				if (container) {
					tagName = container.tagName;
					if (~TAGS.indexOf(tagName)) {
						candidates = [container];
					}
				}
				if (!candidates) {
					candidates = inside.querySelectorAll(TAGS.join(","));
				}

				if (candidates && candidates.length) {
					Array.prototype.forEach.call(candidates, testLabel);
				}
			}

			/**
			 * AJAX subscriber to test for missing labels after ajax has happened.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced).
			 */
			function ajaxSubscriber(element) {
				if (element) {
					timers.setTimeout(flagBadLabels, 100, element);
				}
			}

			/**
			 * Initialiser callback to subscribe to {@link module:wc/dom/shed} and
			 * {@link module:wc/ui/ajax/processResponse}.
			 *
			 * @function module:wc/ui/label.postInit
			 * @public
			 */
			this.postInit = function () {
				processResponse.subscribe(ajaxSubscriber, true);
				timers.setTimeout(flagBadLabels, 500);
			};
		}

		/**
		 * Highlight any labellable elements which are not adequately labelled. This means:
		 *
		 * 1. no label (or legend for a fieldset); and
		 * 2. no aria-label or aria-describedby attribute; and
		 * 3. no title
		 *
		 * @module
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/tag
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires module:wc/ui/getVisibleText
		 * @requires module:wc/timers
		 */
		var instance= new Label();
		initialise.register(instance);
		return instance;
	});
