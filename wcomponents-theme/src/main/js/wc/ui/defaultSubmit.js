define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget"],
	function(event, initialise, Widget) {
		"use strict";

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
		var instance;

		/**
		 * @constructor
		 * @alias module:wc/dom/defaultSubmit~DefaultSubmit
		 * @private
		 */
		function DefaultSubmit() {
			var INPUT_WD =  new Widget("input"),
				FILE_WD = INPUT_WD.extend("", {"type": "file"}),
				SUBMIT_CONTROL_WD = new Widget("button", "", {"type": "submit"}),
				SUBMITTER = new Widget("", "", {"data-wc-submit": null}),
				SUBMIT_ATTRIB = "data-wc-submit",
				SELECT_WD;

			/**
			 * Initialise default sumit finctinality by wiring up focus listeners which will lazily attach other
			 * required listeners later.
			 * @function  module:wc/dom/defaultSubmit.initialise
			 * @public
			 * @param {Element} element document.body
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.keypress, keyEvent, -1);
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

			/**
			 * Determine if an element has a default submit behaviour.
			 * @function
			 * @private
			 * @param {Element} element The 'submitting' element.
			 */
			function isPotentialSubmitter(element) {
				SELECT_WD = SELECT_WD || new Widget("select");
				if (SELECT_WD.isOneOfMe(element)) {
					return true;
				}
				return (INPUT_WD.isOneOfMe(element) && !FILE_WD.isOneOfMe(element));
			}

			/**
			 * Find the correct submit button to "click" when the ENTER key is pressed on element.
			 * NOTE: we do not need this if the element is not in a form
			 * @param {Element} element A form control element.
			 * @returns {Element} The default submit for the element which may be the form's regular submit button.
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
					} else if (form) {
						result = SUBMIT_CONTROL_WD.findDescendant(form);
					}
				}
				return result;
			}
		}

		instance = new DefaultSubmit();
		initialise.register(instance);
		return instance;
	});
