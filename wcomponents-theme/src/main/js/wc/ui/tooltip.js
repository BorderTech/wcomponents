define(["wc/dom/classList", "wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/timers", "wc/dom/textContent"],
	function(classList, event, initialise, shed, Widget, timers, textContent) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/tooltip~Tooltip
		 * @private
		 * @todo document private members.
		 */
		function Tooltip() {
			var showing,
				isOpen = false,
				TOOLTIP_TTL = 5000,
				SHOW_UNDERLINE_CLASS = "wc_alt",
				TOOLTIP_ELEMENT = "span",
				TOOLTIPS = new Widget(TOOLTIP_ELEMENT, "", {"role": "tooltip"}),
				KEYED_ELEMENTS = [new Widget("", "", {accesskey: null}), new Widget("", "", {"data-wc-accesskey": null})],
				UNDERLINE = new Widget("span", "wc_accesskey");

			/*
			 * Manages scheduled cleanups of any tooltips being shown - they will be
			 * automatically hidden in n seconds unless another routine has hidden
			 * them before that time.
			 */
			function scheduledCleanup(hiding) {
				if (hiding) {  // someone else is hiding the tooltips so cancel cleanup
					if (showing) {
						timers.clearTimeout(showing);
						showing = null;
					}
				}
				else {  // someone is (re)showing tooltips, (re)schedule a cleanup
					if (showing) {
						timers.clearTimeout(showing);
					}
					showing = timers.setTimeout(toggleTooltips, TOOLTIP_TTL, true);
				}
			}

			function toggleTooltips(hide) {
				var tooltips = TOOLTIPS.findDescendants(document),
					i, next, nextId, len, control,
					showHide = hide ? "hide" : "show";

				isOpen = !hide;
				for (i = 0, len = tooltips.length; i < len; ++i) {
					next = tooltips[i];
					nextId = next.id;
					control = document.getElementById(nextId.slice(0, nextId.indexOf("_wctt")));
					if (!control || (control && !shed.isDisabled(control))) {
						shed[showHide](next, true);
					}
				}
				if (hide) {
					removeAccessLetter();
				}
				else {
					showAllUnderlines();
				}
				scheduledCleanup(hide);
			}

			/**
			 * Helper for toggleTooltips.
			 * @private
			 * @function
			 */
			function showAllUnderlines() {
				var body = document.body;

				Array.prototype.forEach.call(Widget.findDescendants(body, KEYED_ELEMENTS), function (next) {
					if (!shed.isDisabled(next)) {
						underlineAccessLetter(next);
					}
				});
				classList.add(body, SHOW_UNDERLINE_CLASS);
			}

			function keydownEvent($event) {
				if (!$event.defaultPrevented && !isOpen && $event.keyCode === KeyEvent.DOM_VK_ALT && !($event.repeat)/* won't detect repeat in FF*/) {
					isOpen = true;
					toggleTooltips(false);
				}
			}

			function keyupEvent($event) {
				if (isOpen && !$event.defaultPrevented) {// && $event.keyCode == KeyEvent.DOM_VK_ALT)  // IE8 does not fire keyup when releasing ALT if there is a previous keyup (such as releasing the accesskey key) and if the browser native accesskey functionality (for menus) has been invoked
					toggleTooltips(true);
				}
			}

			/*
			 * Find the first letter matching the access key and underline it. Only look in text node children.
			 * @param element
			 * @returns Boolean, true if an access key letter has been underlined
			 */
			function underlineAccessLetter(element) {
				var textNode,
					content,
					parent,
					underlineElement,
					letter = element.accessKey,
					position,
					tw;
				/*
				 * A treewalker filter to get child text nodes
				 * @returns integer
				 */
				function childFilter(textNode) {
					var rval = NodeFilter.FILTER_SKIP,
						content = textNode.nodeValue;

					if (TOOLTIPS.findAncestor(textNode)) {
						rval = NodeFilter.FILTER_REJECT;
					}
					else if (content.toLocaleUpperCase().indexOf(letter) > -1) {
						rval = NodeFilter.FILTER_ACCEPT;
					}
					return rval;
				}

				if (letter && !(UNDERLINE.findDescendant(element)) && (content = textContent.get(element)) && content.toLocaleUpperCase().indexOf(letter) > -1) {
					tw = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, childFilter, false);

					textNode = null;
					if (tw) {
						tw.currentNode = element;
						tw.nextNode();
						textNode = tw.currentNode;
					}

					if (textNode && textNode.nodeType === Node.TEXT_NODE) {
						content = textNode.nodeValue;
						if ((position = content.toLocaleUpperCase().indexOf(letter)) > -1) {
							parent = textNode.parentNode;
							underlineElement = document.createElement(TOOLTIP_ELEMENT);
							underlineElement.className = "wc_accesskey";
							underlineElement.innerHTML = content.substr(position, 1);

							if (position !== 0) {
								parent.insertBefore(document.createTextNode(content.substr(0, position)), textNode);
							}
							parent.insertBefore(underlineElement, textNode);
							textNode.nodeValue = content.substring(position + 1);
						}
					}
				}
			}

			function removeAccessLetter() {
				classList.remove(document.body, SHOW_UNDERLINE_CLASS);
			}

			/**
			 * Initialise the tooltips by attaching necessary event listeners.
			 * @function module:wc/ui/tooltip.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.keydown, keydownEvent);
				event.add(element, event.TYPE.keyup, keyupEvent);
			};

			/**
			 * Get a toolTip from an element.
			 * @function module:wc/ui/tooltip.getTooltip
			 * @param {Element} element An HTML element which may contain a toolTip.
			 * @returns {?Element} A toolTip element.
			 */
			this.getTooltip = function(element) {
				return TOOLTIPS.findDescendant(element);
			};
		}

		/**
		 * Provides a mechanism to expose access keys in the UI. This is done using a little "tooltip" style element which
		 * is shown when the ALT/META key is pressed.
		 *
		 * @module
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/timers
		 * @requires module:wc/dom/textContent
		 */
		var instance = new Tooltip();
		initialise.register(instance);
		return instance;
	});
