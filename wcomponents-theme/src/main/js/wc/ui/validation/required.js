define([
	"wc/ui/validation/isComplete",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/ui/validation/validationManager",
	"wc/ui/feedback"],
	function(isComplete, Widget, i18n, validationManager, feedback) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/required~ValidateRequired
		 * @private
		 */
		function ValidateRequired() {
			/**
			 * @constant {object}  module:wc/ui/validation/required.CONSTRAINTS Indicates how mandatory-ness
			 * is determined for a particular component one of the aria-required attribute, a className or the required
			 * attribute.
			 * @property {String} ARIA Use aria-required.
			 * @property {String} CLASSNAME Use a className as a required indicator.
			 * @property {String} ATTRIB Use the required attribute.
			 */
			this.CONSTRAINTS = {
				ARIA: "aria",
				CLASSNAME: "classname",
				ATTRIB: "attrib"};

			/**
			 * Get the required field message for flagging a required field in an error state.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element (component) with the error.
			 * @returns {String} A formatted error message.
			 */
			function getRequiredMessage(element) {
				return i18n.get("validation_common_incomplete", validationManager.getLabelText(element));
			}

			/**
			 * Common helper to add an error message indicator to each of an array of components.
			 * @function
			 * @private
			 * @param {Element[]} elements An array of elements in an invalid state.
			 * @param {module:wc/ui/validation/required~config} [config] Configuration object.
			 */
			function flagAllThese(elements, config) {
				var messageFunc = (config && config.messageFunc) ? config.messageFunc : getRequiredMessage;

				Array.prototype.forEach.call(elements, function (next) {
					feedback.flagError({ element: next, message: messageFunc(next)});
				});
			}

			/**
			 * Gets all required instances of a given Widget in a container.
			 *
			 * @function module:wc/ui/validation/required.getRequired
			 * @param {Element} container Where to look (we look inside, container doesn't count).
			 * @param {module:wc/dom/Widget} widget A Widget describing the type of component for which we are looking.
			 * @param {module:wc/ui/validation/required.CONSTRAINTS} [requiredConstraint] Sets the required constraint if not using the required attribute.
			 * @returns {Element[]} Will return an empty array if there are no required components in the container(including the container itself).
			 */
			this.getRequired = function(container, widget, requiredConstraint) {
				var reqWidget,
					result,
					exObj;

				/**
				 * Array map function to extend the orignal widget to add the necessary required constraints.
				 *
				 * @function
				 * @private
				 * @param {module:wc/dom/Widget} nextWidget The widget we are extending.
				 */
				function _mapFn(nextWidget) {
					var extendedWidget = nextWidget;  // if no extension just return itself
					if (typeof exObj === "string") {
						extendedWidget = nextWidget.extend(exObj);
					} else if (exObj) {
						extendedWidget = nextWidget.extend("", exObj);
					}
					return extendedWidget;
				}

				/**
				 * Array filter function to remove controls exempt from validation.
				 *
				 * @function
				 * @private
				 * @param {Element} next The element to test.
				 */
				function _filter(next) {
					return !validationManager.isExempt(next);
				}

				switch (requiredConstraint) {
					case this.CONSTRAINTS.ARIA:
						exObj = {"aria-required": "true"};
						break;
					case this.CONSTRAINTS.CLASSNAME:
						exObj = "wc_req";
						break;
					default:
						exObj = {required: null};
						break;
				}

				if (!Array.isArray(widget) && (widget = _mapFn(widget))) {
					if (widget.isOneOfMe(container)) {
						result = [container];
					} else {
						result = widget.findDescendants(container);
					}
				} else {
					reqWidget = widget.map(_mapFn);
					if (Widget.isOneOfMe(container, reqWidget)) {
						result = [container];
					} else {
						result = Widget.findDescendants(container, reqWidget);
					}
				}
				if (result) {
					result = Array.prototype.filter.call(result, _filter);
				}
				return result || [];
			};

			/**
			 * Determines if a given element is not 'complete' and therefore fails a mandatory test.
			 *
			 * @function
			 * @private
			 * @param {Element} element A form control or aria surrogate.
			 * @returns {Boolean} true if not complete.
			 */
			function isNotComplete(element) {
				return !isComplete.isComplete(element);
			}

			/**
			 * the majority of components required validation is all the same: a component is required or aria-required,
			 * it is incomplete, it gets a standard message and the flag is applied to the element "afterEnd".
			 *
			 * @function module:wc/ui/validation/required.doItAllForMe
			 * @param {Element} container the container being validated.
			 * @param {module:dom/Widget} widget the descriptor of the component being tested.
			 * @param {Boolean} [useAria] set true to use aria-required as the indicator of mandatory-ness, otherwise
			 *    use required attribute.
			 * @returns {Boolean} true if the container is valid.
			 */
			this.doItAllForMe = function(container, widget, useAria) {
				var elements = this.getRequired(container, widget, useAria),
					result = true;
				// just get the failures for flagging
				elements = elements.filter(isNotComplete);
				if (elements && elements.length) {
					result = false;
					flagAllThese(elements);
				}
				return result;
			};

			/**
			 * A helper for doing all of the required validation but allowing individual components to set a lot of
			 * optional parameters.
			 *
			 * @function module:wc/ui/validation/required.complexValidationHelper
			 * @param {module:wc/ui/validation/required~config} obj Configuration parameters.
			 * @returns {Boolean} true if obj.container is valid.
			 */
			this.complexValidationHelper = function(obj) {
				var result = true,
					widget = obj.widget,
					container = obj.container,
					constraint = obj.constraint,
					filterFunc = obj.filter || isNotComplete,
					flagFunc = obj.flag || flagAllThese,
					elements;

				if (widget && container) {
					elements = this.getRequired(container, widget, constraint);
					elements = elements.filter(filterFunc);

					if (elements && elements.length) {
						result = false;
						flagFunc(elements, obj);
					}
				}
				return result;
			};

			/**
			 * Helper for re-validating a single control which is already in an invalid state. This is used for
			 * changeEvent based revalidation. This function assumes that the calling function has called
			 * <code>validationManager.isInvalid</code> for element but is not dependent on that. It is just better
			 * practice to do so before doing any further revalidation but you could turn on in-context validation for
			 * all change events by not doing that test.
			 *
			 * @function module:wc/ui/validation/required.revalidate
			 * @param {Element} element The element to re-validate.
			 * @param {module:wc/ui/validation/required~config} config Configuration parameters.
			 */
			this.revalidate = function (element, config) {
				var result = isComplete.isComplete(element);
				if (!result) {
					flagAllThese([element], config);
				}
				return result;
			};
		}

		/**
		 * This is a set of helpers for required field validation. The actual validation should be handed off to individual
		 * components. There are a lot of similarities though so I have included a few public functions which will suffice for
		 * all required testing for most components.
		 *
		 * @module
		 * @requires module:wc/ui/validation/isComplete
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/validation/validationManager
		 * @requires module:wc/ui/feedback
		 */
		var instance = new ValidateRequired();
		return instance;

		/**
		 * Configuration object for several functions.
		 * @typedef {Object} module:wc/ui/validation/required~config
		 * @property {Element} container The container being validated.
		 * @property {module:wc/dom/Widget} widget The description of the component we are currently testing.
		 * @property {Function} [filterFunc] A function to call to test for completeness, defaults to
		 *    {@link module:wc/ui/validation/required~isNotComplete}.
		 * @property {Function} [flagFunc] A function to set the error message box. Defaults to
		 *    {@link module:wc/ui/validation/required~flagAllThese}
		 * @property {Function} [messageFunc] A function to get the error message. Defaults to
		 *    {@link module:wc/ui/validation/required~getRequiredMessage}.
		 */
	});
