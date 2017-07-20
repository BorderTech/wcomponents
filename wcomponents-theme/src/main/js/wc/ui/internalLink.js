define(["wc/dom/focus",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/shed"],
	function(focus, event, initialise, Widget, shed) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/internalLink~InternalLink
		 * @private
		 */
		function InternalLink() {
			var LEGEND = new Widget("legend"),
				WIDGETS = [LEGEND, new Widget("a"), new Widget("", "", {"data-wc-for": null})],
				FOR_ATTRIB = "data-wc-for";

			function actionClickEvent(element) {
				var url, target;
				if (!shed.isDisabled(element)) {
					if (LEGEND.isOneOfMe(element)) {
						target = element.parentNode;
					} else if (element.hasAttribute(FOR_ATTRIB)) {
						target = document.getElementById(element.getAttribute(FOR_ATTRIB));
					} else if (element.href) {
						url = element.getAttribute("href");
						if (url.indexOf("#") === 0) {
							target = document.getElementById(url.substr(1));
						}
					}
					if (target && !shed.isDisabled(target)) {
						if (focus.canFocus(target)) {
							focus.setFocusRequest(target);
							return true;
						}
						if (focus.canFocusInside(target)) {
							focus.focusFirstTabstop(target);
							return true;
						}
					}
				}
				return false;
			}

			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = Widget.findAncestor($event.target, WIDGETS)) && !shed.isDisabled(element)) {
					if (actionClickEvent(element)) {
						$event.preventDefault();
					}
				}
			}

			/**
			 * Initialisation function for internal link focuser. This function wires up the event listeners.
			 * @function module:wc/ui/internalLink.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
			};
		}

		/**
		 * Provides functionality required to set focus when invoking an internal link and to add label-like functionality to
		 * the label surrogates used for various compund components.
		 *
		 * @module
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/shed
		 *
		 * @todo document private members.
		 */
		var instance = new InternalLink();
		initialise.register(instance);
		return instance;
	});
