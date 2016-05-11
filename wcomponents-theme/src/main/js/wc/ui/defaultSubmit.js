/**
 * Provide a mechanism to allow any submit button to be deemed the default button of a form or even a section of a form
 * or even a specific input element. The default button is the submitting control of a form which is invoked when the
 * user hits the ENTER key in a submitting input element.
 *
 * NOTE: we insist that button elements (of type submit) have a value attribute.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @ignore */
	function(event, initialise, Widget) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/defaultSubmit~DefaultSubmit
		 * @private
		 */
		function DefaultSubmit() {
			var INPUT_WD = new Widget("input"),
				FILE_WD = INPUT_WD.extend("", {"type": "file"}),
				SUBMIT_CONTROL_WD = new Widget("button", "", {"type": "submit"}),
				SUBMITTER = new Widget("", "", {"data-wc-submit": null}),
				SUBMIT_ATTRIB = "data-wc-submit";
				// CLASSNAME = "wc_btn_default",  // only used as a marker for possible future styling.
				// CURRENT_SUBMIT_ID;


			/**
			 * Initialise default sumit finctinality by wiring up focus listeners which will lazily attach other
			 * required listeners later.
			 * @function  module:wc/dom/defaultSubmit.initialise
			 * @public
			 * @param {Element} element document.body
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.keypress, keyEvent, -10);
//				if (event.canCapture) {
//					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
//				}
//				else {
//					event.add(element, event.TYPE.focusin, focusEvent);
//				}
			};

			/**
			 * Event listener for keypress event. Used to determine the correct submit button to use as the form submit
			 * based on the form submit or the over-ridden defaultSubmit of the input.
			 * @function
			 * @private
			 * @param {Event} $event the keypress event.
			 */
			function keyEvent($event) {
				var keyCode = $event.keyCode,
					element = $event.target, correctSubmit;
				if (!$event.defaultPrevented && keyCode === KeyEvent.DOM_VK_RETURN && element.form && isPotentialSubmitter(element)) {
					if ((correctSubmit = findCorrectSubmit(element))) {
						$event.preventDefault();
						event.fire(correctSubmit, event.TYPE.click);
					}
				}
			}

			/*
			 * When focusing an element, test if it is a potential submitter and go find its button. If the element is
			 * not a potential submitting element clear any existing reference. All this does is mark the default
			 * button with a class name for styling purposes. This class name is not used to find the submitting button
			 * for actual activation. Therefore this may be a bit pointless.
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 *
			function focusEvent($event) {
				var element = $event.target, target;
				if (!$event.defaultPrevented) {
					target = SUBMIT_CONTROL_WD.findAncestor(element);
					if (!target && element.form && isPotentialSubmitter(element)) {
						target = findCorrectSubmit(element);
					}
					if (target) {
						activateButton(target);
					}
					else {
						clearDefaultSubmit();
					}
				}
			} */

			/**
			 * Determine if an element has a default submit behaviour.
			 * @function
			 * @private
			 * @param {Element} element The 'submitting' element.
			 */
			function isPotentialSubmitter(element) {
				var result = false;
				if (INPUT_WD.isOneOfMe(element) && !FILE_WD.isOneOfMe(element)) {
					result = true;
				}
				return result;
			}

			/**
			 * Set a classname on the default submit button. What you do with this is up to you but just don't use it to
			 * make the button bold!
			 * @function
			 * @private
			 * @param {Element} button The default button for the focussed element.
			 */
//			function activateButton(button) {
//				if (button && button.id && button.id !== CURRENT_SUBMIT_ID) {
//					clearDefaultSubmit();
//					classList.add(button, CLASSNAME);
//					CURRENT_SUBMIT_ID = button.id;
//				}
//			}

			/**
			 * Remove the "default" submit class.
			 * @function
			 * @private
			 */
//			function clearDefaultSubmit() {
//				var oldSubmit;
//				if (CURRENT_SUBMIT_ID && (oldSubmit = document.getElementById(CURRENT_SUBMIT_ID))) {
//					classList.remove(oldSubmit, CLASSNAME);
//				}
//				CURRENT_SUBMIT_ID = "";
//			}

			/**
			 * Find the correct submit button to "click" when the ENTER key is pressed on element.
			 * NOTE: we do not need this if the element is not in a form
			 * @param {Element} element A form control element.
			 * @returns {?Element} The default submit for the element which may be the form's regular submit button.
			 */
			function findCorrectSubmit(element) {
				var result,
					form = element.form,
					submitId,
					container;

				if (form && !(result = SUBMIT_CONTROL_WD.findAncestor(element))) {
					submitId = element.getAttribute(SUBMIT_ATTRIB);
					if (!submitId && (container = SUBMITTER.findAncestor(element))) {
						submitId = container.getAttribute(SUBMIT_ATTRIB);
					}
					if (submitId) {
						result = document.getElementById(submitId);
					}
					else if (form) {
						result = SUBMIT_CONTROL_WD.findDescendant(form);
					}
				}
				return result;
			}
		}

		var /** @alias module:wc/dom/defaultSubmit */ instance = new DefaultSubmit();
		initialise.register(instance);
		return instance;
	});
