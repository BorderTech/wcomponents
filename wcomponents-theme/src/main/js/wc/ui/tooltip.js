define(["wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/timers"],
	function(event, initialise, shed, Widget, timers) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/tooltip~Tooltip
		 * @private
		 */
		function Tooltip() {
			var showing,
				isOpen = false,
				TOOLTIP_TTL = 5000,
				TOOLTIPS = new Widget("span", "", {"role": "tooltip"});

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
				} else {  // someone is (re)showing tooltips, (re)schedule a cleanup
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
				scheduledCleanup(hide);
			}

			function keydownEvent($event) {
				if (!$event.defaultPrevented && !isOpen && $event.keyCode === KeyEvent.DOM_VK_ALT && !($event.repeat)) {
					isOpen = true;
					toggleTooltips(false);
				}
			}

			/* NOTE IE8 does not fire keyup when releasing ALT if there is a previous keyup (such as releasing the accesskey key) and if the browser
			 * native accesskey functionality (for menus) has been invoked.
			 */
			function keyupEvent($event) {
				if (isOpen && !$event.defaultPrevented) {
					toggleTooltips(true);
				}
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
			 * @returns {Element} A toolTip element.
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
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/timers
		 */
		var instance = new Tooltip();
		initialise.register(instance);
		return instance;
	});
