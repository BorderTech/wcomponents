define(["wc/has",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/tag",
	"wc/dom/Widget",
	"wc/Observer",
	"wc/i18n/i18n",
	"wc/ui/getFirstLabelForElement",
	"wc/ui/feedback",
	"wc/config"],
	function(has, initialise, shed, tag, Widget, Observer, i18n, getFirstLabelForElement, feedback, wcconfig) {
		"use strict";

		/**
		 * This module know when to mark things and valid/invalid but not "how".
		 * Good rule of thumb, if you import "i18n" or "classList" or build DOM snippets in here you should rethink it.
		 * @constructor
		 * @alias module:wc/ui/validation/validationManager~ValidationManager
		 * @private
		 */
		function ValidationManager() {
			var
				/**
				 * At its heart validationManager is just an observer surrogate and this is the instance of
				 * {@link module:wc/Observer} used to subscribe and publish.
				 *
				 * @var
				 * @type {module:wc/Observer}
				 * @private
				 */
				observer,
				/**
				 * The description of a FORM. Instantiated on first use.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				FORM,
				/**
				 * The description of a component in an invalid state.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				INVALID_COMPONENT = new Widget("", "", { "aria-invalid": "true" }),
				REVALIDATE_OBSERVER_GROUP = "reval",
				allowValidateOnChange = null,
				allowValidateOnBlur = null;

			/**
			 * Listen for DISABLE, HIDE or OPTIONAL actions and clear any error message for the component.
			 * @function
			 * @private
			 * @param {Element} element The element being acted upon.
			 */
			function shedSubscriber(element) {
				if (element && INVALID_COMPONENT.isOneOfMe(element)) {
					feedback.remove(element);
				}
			}

			/**
			 * Set up the validation configuration.
			 * @function
			 * @private
			 */
			function setValidateRules() {
				var conf = wcconfig.get("validationManager", {
					"doOnChange": true,
					"doOnBlur": false
				});
				allowValidateOnChange = conf.doOnChange;
				allowValidateOnBlur = conf.doOnBlur;
			}

			/**
			 * Indicates whether a component is associated with a message indicating that an error has been resolved.
			 *
			 * @function
			 * @private
			 * @param {Element} element The HTML element to test.
			 * @returns {boolean} `true` if the element is associated with a success message.
			 */
			function isMarkedOK(element) {
				return !!feedback.getBox(element, feedback.LEVEL.SUCCESS);
			}

			/**
			 * An element is exempt from participating in client side validation if:
			 * <ol><li>the element in INPUT type hidden;</li>
			 * <li>the element is disabled; or</li>
			 * <li>the element is not 'visible' (do shed test first - it is quicker).</li>
			 * </ol>
			 *
			 * @function module:wc/ui/validation/validationManager.isExempt
			 * @param {Element} element The component to test.
			 * @returns {Boolean} true if the component is exempt from client side validation.
			 */
			this.isExempt = function(element) {
				var result = false;
				if ((element.tagName === tag.INPUT && element.type === "hidden") || shed.isDisabled(element) || shed.isHidden(element)) {
					result = true;
				}
				return result;
			};

			/**
			 * Is an element currently in an invalid state? This is used to indicate that revalidation may be needed
			 * (commonly for a change event listener). NOTE: this does not test the validity of the element, merely
			 * returns whether anything has put the element into an invalid state previously.
			 *
			 * @function module:wc/ui/validation/validationManager.isInvalid
			 * @param {Element} element The component to test for validity.
			 * @returns {Boolean} true if the element is invalid.
			 */
			this.isInvalid = function(element) {
				return INVALID_COMPONENT.isOneOfMe(element);
			};

			/**
			 * Most validating components have a pretty similar mechanism to revalidate whern their input changes so
			 * this helper exists to take care of it.
			 *
			 * @function module:wc/ui/validation/validationManager.revalidationHelper
			 * @param {Element} element The component being re-validated.
			 * @param {Function} _validateFunc The component's validation function.
			 */
			this.revalidationHelper = function(element, _validateFunc) {
				var initiallyInvalid = this.isInvalid(element),
					isNowInvalid = initiallyInvalid;

				if (initiallyInvalid) {
					if ((_validateFunc(element))) {
						this.setOK(element);
						isNowInvalid = false;
					} else {
						isNowInvalid = true;
					}
				} else if (isMarkedOK(element, this)) {
					isNowInvalid = !_validateFunc(element);
				}

				if (observer && isNowInvalid !== initiallyInvalid) { // if the current component's validity has changed
					observer.setFilter(REVALIDATE_OBSERVER_GROUP);
					observer.notify(element);
				}
			};

			/**
			 * Tests the validity of form bound elements within a specified container.
			 *
			 * @function module:wc/ui/validation/validationManager.isValid
			 * @param {Element} [container] A DOM node (preferably containing form controls). If the container is not specified finds the form
			 *   containing the activeElement (this is for use with controls with submitOnchange).
			 * @returns {Boolean} true if the container is in a valid state (all components in the container which support validation are valid).
			 */
			this.isValid = function (container) {
				var result = true;

				/**
				 * Observer callback function to keep track of validity from all subscribers. A container is only valid
				 * if all of its subscribers return true.
				 * @function
				 * @private
				 * @param {Boolean} decision true if valid.
				 * @returns {bitmap}
				 */
				function _callback(decision) {
					result &= decision;  // we are only valid if all observers are valid
				}

				if (!container) {
					FORM = FORM || new Widget("form");
					container = FORM.findAncestor(document.activeElement);
				}
				if (container && observer) {
					observer.setCallback(_callback);
					observer.notify(container);
				}

				result = !!result;  // convert the potentially bitwise result to a Boolean

				if (!result && repainter) {  // IE8 has repaint issues when validation errors are inserted into columns
					repainter.checkRepaint(container);
				}
				return result;
			};

			/**
			 * Late intialisation callback to subscribe to shed to listen for state changes which impact any existing
			 * validation error messages.
			 * @function module:wc/ui/validation/validationManager.postInit
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
			};

			/**
			 * Allows a component to subscribe to client side validation.
			 * @function module:wc/ui/validation/validationManager.subscribe
			 * @see {@link module:wc/Observer#subscribe}
			 *
			 * @param {Function} subscriber The function that will be notified by validationManager. This function MUST be present at "publish" time,
			 *   but need not be present at "subscribe" time.
			 * @param {boolean} [revalidate] if truthy subscribe to revalidation rather than validation.
			 * @returns {Function} A reference to the subscriber.
			 */
			this.subscribe = function(subscriber, revalidate) {
				observer = observer || new Observer();
				var group = revalidate ? { group: REVALIDATE_OBSERVER_GROUP } : null;
				return observer.subscribe(subscriber, group);
			};

			this.getLabelText = function(element, fallbackToken) {
				var token = fallbackToken || "validation_common_unlabelledfield";
				return getFirstLabelForElement(element, true) ||
					element.getAttribute("aria-label") ||
					element.title ||
					i18n.get(token);
			};

			/**
			 * Updates an error box to a succcess box and its error box once an error is corrected.
			 *
			 * @function
			 * @public
			 * @param {Element} element the HTML element which was in an error state.
			 */
			this.setOK = function(element) {
				return feedback.flagSuccess({
					element: element,
					message: i18n.get("validation_ok")
				});
			};

			/**
			 * @function
			 * @public
			 * @returns {boolean} `true` if we want controls to validate when their value changes, false to validate only when submitting.
			 */
			this.isValidateOnChange = function() {
				if (allowValidateOnChange !== null) {
					return allowValidateOnChange;
				}
				setValidateRules();
				return allowValidateOnChange;
			};

			/**
			 * @function
			 * @public
			 * @returns {boolean} `true` if we want mandatory controls to validate when the user exits them, even if there has not been a change.
			 */
			this.isValidateOnBlur = function() {
				if (allowValidateOnBlur !== null) {
					return allowValidateOnBlur;
				}
				setValidateRules();
				return allowValidateOnBlur;
			};
		}

		var instance, repainter;

		/* ie8's interesting inline-block bug means we need to force a repaint after all validating activites.*/
		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		/**
		 * Generic client side validation manager. This is the publisher for client side validation. Any component which
		 * requires custom validation subscribes to this using validationManager.subscribe.
		 *
		 * @module
		 * @requires wc/has
		 * @requires wc/dom/initialise
		 * @requires wc/dom/shed
		 * @requires wc/dom/tag
		 * @requires wc/dom/Widget
		 * @requires wc/Observer
		 * @requires wc/i18n/i18n
		 * @requires wc/ui/getFirstLabelForElement
		 * @requires wc/ui/feedback
		 */
		instance = new ValidationManager();
		initialise.register(instance);
		return instance;
	});
