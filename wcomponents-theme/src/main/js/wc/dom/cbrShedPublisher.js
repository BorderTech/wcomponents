/**
 * Provides a mechanism to publish {@link module"wc/dom/shed"} events on native selectable controls (radio buttons and
 * check boxes) which are not instrinsically 'sheddy'. This allows us to use shed subscribers to do stuff on select/
 * deselect of these native controls as if they were ARIA widgets.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/shed
 * @requires module:wc/has
 *
 * @todo re-order code, document private members.
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed", "wc/has"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param has wc/has @ignore */
	function(event, initialise, Widget, shed, has) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/cbrShedPublisher~CheckRadioPublisher
		 * @private
		 */
		function CheckboxRadioPublisher() {
			var RADIO = new Widget("input", "", {type: "radio"}),
				LABEL,
				WIDGETS = [new Widget("input", "", {"type": "checkbox"}), RADIO],
				registry = {};

			/**
			 * Focus listener to store the state of a radio button on focus.
			 *
			 * A MutationObserver may be more effective for this ... if only they worked reliably :(
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (element.id && RADIO.isOneOfMe(element)) {
					registry[(element.id)] = element.checked;
				}
			}

			/**
			 * A helper for the click event listener which does the publishing.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element which has been clicked.
			 */
			function callback(element) {
				var action,
					doIt = true,
					chkd;
				if (!shed.isDisabled(element)) {
					action = shed.isSelected(element) ? shed.actions.SELECT : shed.actions.DESELECT;

					if (element.id) {
						if ((chkd = registry[(element.id)]) !== undefined) {
							doIt = shed.isSelected(element) !== chkd;
						}
					}
					if (doIt) {
						shed.publish(element, action);
						if (chkd !== undefined) {
							registry[(element.id)] = !chkd;
						}
					}
				}
			}

			/**
			 * A click event listener publishes shed.SELECT & shed.DESELECT.
			 *
			 * @function
			 * @private
			 * @param {Event} $event Wrapped click event.
			 */
			function clickEvent($event) {
				var target = $event.target, label, labelFor, radio;
				if (!$event.defaultPrevented) {
					if (has("trident") && $event.button === -1 && RADIO.isOneOfMe(target)) {
						/* WHAT?
						 * Well it seems that when one uses the arrow keys to move between radio buttons in IE (11 at least)
						 * then the click event is fired on the target radio before the focus event. At the time the
						 * click event is fired the target radio is already returning true to checked.
						 * We know, being a radio, that this is a state change so we can fool callback by setting the
						 * registry entry to !checked which is what it would be if the focus event fired first.
						 */
						registry[(target.id)] = !target.checked;
						callback(target);
					}
					else if (Widget.isOneOfMe(target, WIDGETS)) {
						callback(target);
					}
					else {
						LABEL = LABEL || new Widget("label");
						if ((label = LABEL.findAncestor(target)) && (labelFor = label.getAttribute("for")) && (radio = document.getElementById(labelFor)) && RADIO.isOneOfMe(radio) && radio.id) {
							registry[(radio.id)] = radio.checked;
						}
					}
				}
			}

			/**
			 * Set up event listeners in intialisation.
			 *
			 * @function
			 * @alias module:wc/dom/cbrShedPublisher.initialise
			 * @param {Element} element The element being initialised: usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
			};
		}

		var /** @alias module:wc/dom/cbrShedPublisher */instance = new CheckboxRadioPublisher();
		initialise.register(instance);
		return instance;
	});
