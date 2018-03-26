define(["wc/dom/classList",
	"wc/dom/initialise",
	"wc/dom/tag",
	"wc/ui/ajax/processResponse",
	"wc/i18n/i18n",
	"wc/ui/getVisibleText",
	"wc/timers"],
	function (classList, initialise, tag, processResponse, i18n, getVisibleText, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/heading~Heading
		 * @private
		 */
		function Heading() {
			var MISSING_HEADING, TAGS = [tag.H1, tag.H2, tag.H3, tag.H4, tag.H5, tag.H6],
				IMG_QS;


			function isHeadingEmpty(label) {
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

			function testHeading(element) {
				if (isHeadingEmpty(element)) {
					element.insertAdjacentHTML("beforeend", MISSING_HEADING);
					classList.add(element, "wc-err");
				}
			}

			function flagBadHeadings(container) {
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
					Array.prototype.forEach.call(candidates, testHeading);
				}
			}

			/**
			 * AJAX subscriber to test for empty headings after ajax has happened.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced).
			 */
			function ajaxSubscriber(element) {
				if (element) {
					timers.setTimeout(flagBadHeadings, 0, element);
				}
			}

			/**
			 * Initialiser callback
			 *
			 * @function module:wc/ui/debug/heading.postInit
			 * @public
			 */
			this.postInit = function () {
				return i18n.translate("missingHeading").then(function(missingHeading) {
					MISSING_HEADING = missingHeading;
					processResponse.subscribe(ajaxSubscriber, true);
					timers.setTimeout(flagBadHeadings, 500);
				});
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
		 * @requires module:wc/ui/getVisibleText
		 * @requires module:wc/timers
		 */
		var instance = new Heading();
		initialise.register(instance);
		return instance;
	});
