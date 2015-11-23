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
 * @requires module:wc/ui/label
 */
define(["wc/dom/classList", "wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/timers", "wc/dom/textContent", "wc/ui/label"],
	/** @param classList wc/dom/classList @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @param Widget wc/dom/Widget @param timers wc/timers @param textContent wc/dom/textContent @param label wc/ui/label @ignore */
	function(classList, event, initialise, shed, Widget, timers, textContent, label) {
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
				ID_SUFFIX = "${wc.ui.accesskey.id.suffix}",
				TOOLTIP_ELEMENT = "span",
				TOOLTIPS = new Widget(TOOLTIP_ELEMENT, "", {"role": "tooltip"}),
				KEYED_ELEMENTS = new Widget("", "", {accesskey: null}),
				BUTTON = new Widget("button", "", {accesskey: null}),
				LINK = new Widget("a", "", {accesskey: null}),
				UNDERLINE = new Widget("span", "wc_accesskey"),
				getFirstLabelForElement;

			require(["wc/ui/getFirstLabelForElement"], function(gfl) {
				getFirstLabelForElement = gfl;
			});

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

				for (i = 0, len = tooltips.length; i < len; ++i) {
					next = tooltips[i];
					nextId = next.id;
					control = document.getElementById(nextId.slice(0, nextId.indexOf(ID_SUFFIX)));
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
				var i, next, len, elements = KEYED_ELEMENTS.findDescendants(document);
				classList.add(document.body, SHOW_UNDERLINE_CLASS);
				for (i = 0, len = elements.length; i < len; ++i) {
					next = elements[i];
					if (next && !shed.isDisabled(next)) {
						underlineAccessLetter(next);
					}
				}
			}

			function keydownEvent($event) {
				if (!$event.defaultPrevented && $event.keyCode === KeyEvent.DOM_VK_ALT && !($event.repeat)/* won't detect repeat in FF*/) {
					toggleTooltips(false);
					isOpen = true;
				}
			}

			function keyupEvent($event) {
				if (isOpen && !$event.defaultPrevented) {// && $event.keyCode == KeyEvent.DOM_VK_ALT)  // IE8 does not fire keyup when releasing ALT if there is a previous keyup (such as releasing the accesskey key) and if the browser native accesskey functionality (for menus) has been invoked
					toggleTooltips(true);
					isOpen = false;
				}
			}

			/*
			 * Find the first letter matching the access key and underline it. Only look in text node children.
			 * @param element
			 * @returns Boolean, true if an access key letter has been underlined
			 */
			function underlineAccessLetter(element) {
				var labelElement, content, parent, underlineElement,
					letter = element.accessKey, position,
					tw = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, childFilter, false);
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

				if (letter) {
					letter = letter.toLocaleUpperCase();

					if (label.isOneOfMe(element) || Widget.isOneOfMe(element, [BUTTON, LINK])) {
						labelElement = element;
					}
					else {
						labelElement = getFirstLabelForElement(element);
					}

					if (labelElement && !(UNDERLINE.findDescendant(labelElement))) {
						content = textContent.get(labelElement);
						if (content.toLocaleUpperCase().indexOf(letter) > -1) {
							labelElement = null;
							if (tw) {
								tw.currentNode = element;
								tw.nextNode();
								labelElement = tw.currentNode;
							}

							if (labelElement && labelElement.nodeType === Node.TEXT_NODE) {
								content = labelElement.nodeValue;
								if ((position = content.toLocaleUpperCase().indexOf(letter)) > -1) {
									parent = labelElement.parentNode;
									underlineElement = document.createElement(TOOLTIP_ELEMENT);
									underlineElement.className = "wc_accesskey";
									underlineElement.innerHTML = content.substr(position, 1);

									if (position !== 0) {
										parent.insertBefore(document.createTextNode(content.substr(0, position)), labelElement);
									}
									parent.insertBefore(underlineElement, labelElement);
									labelElement.nodeValue = content.substring(position + 1);
								}
							}
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

		var /** @alias module:wc/ui/tooltip */ instance = new Tooltip();
		initialise.register(instance);
		return instance;
	});
