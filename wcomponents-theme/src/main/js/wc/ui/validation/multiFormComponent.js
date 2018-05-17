define(["wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/ui/multiFormComponent",
	"wc/array/unique",
	"lib/sprintf",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/isComplete",
	"wc/ui/feedback"],
	function(attribute, event, initialise, Widget, i18n, multiFormComponent, unique, sprintf, required, validationManager, isComplete, feedback) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/multiFormComponent~ValidationMultiFormComponent
		 * @private
		 */
		function ValidationMultiFormComponent() {
			var CONTAINER = multiFormComponent.getWidget(),
				BUTTON = new Widget("button"),
				SELECT_WD = new Widget("select"),
				INPUT_WD = new Widget("input");

			/**
			 * Get the top level element which contains all the sub-elements ie that which contains all of the fields.
			 * @function
			 * @private
			 * @param {Element} element A HTML element, hopefully one inside a multiFormComponent.
			 * @returns {Element} The container element if element is a descendant of a multiFormComponent.
			 */
			function getContainer(element) {
				return CONTAINER.findAncestor(element);
			}

			/**
			 * An array filter function which filters out "null" options from a select so that we can determine if a
			 * single selection select element has a valid selection.
			 * @function
			 * @private
			 * @param {Element} select A HTML SELECT element.
			 * @returns {boolean} true if the select has a selection and that selection is NOT a "null" option.
			 */
			function selectValidOptionFilter(select) {
				var result = select.selectedIndex > -1;
				if (result) {
					result = !select.options[select.selectedIndex].hasAttribute("data-wc-null");
				}
				return result;
			}

			/**
			 * An array filter function to determine if a particular component is "complete". Passed in to
			 * {@link module:wc/ui/validation/isComplete}
			 * @function
			 * @private
			 * @param {Element} next A multi form component.
			 * @returns {boolean} true if the selected items list in the component has one or more options.
			 */
			function amIComplete(next) {
				var candidates = INPUT_WD.findDescendants(next);
				if (!(candidates && candidates.length)) {
					candidates = SELECT_WD.findDescendants(next);
				}
				if (!(candidates && candidates.length)) {
					return false;
				}

				// candidates = toArray(candidates);
				return Array.prototype.some.call(candidates, function (n) {
					return isComplete.isComplete(n);
				});
			}

			/**
			 * Test if a container is complete if it is, or contains, WMultiSelectPairs.
			 * @function
			 * @private
			 * @param {Element} container A container element, mey be a WMultiSelectPair wrapper.
			 * @returns {boolean} true if the container is complete.
			 */
			function _isComplete(container) {
				return isComplete.isCompleteHelper(container, CONTAINER, amIComplete);
			}

			/**
			 * Array filter function for selecting invalid multi form controls. The filter will also flag these invalid
			 * components as it iterates: saves us another iteration.
			 * @function
			 * @private
			 * @param {element} next A multi form control.
			 * @returns {boolean} true if 'next' is invalid.
			 */
			function filter(next) {
				// added parseInt because for a while these values were being compared to non-numeric objects
				var min = window.parseInt(next.getAttribute("data-wc-min")),
					max = window.parseInt(next.getAttribute("data-wc-max")),
					underFlag = "validation_common_undermin",
					overFlag = "validation_common_overmax",
					isInvalid = false,
					count, flag, limit;
				if (min || max) {
					if ((count = SELECT_WD.findDescendants(next)) && count.length) {
						count = unique(Array.prototype.filter.call(count, selectValidOptionFilter), function(a, b) {
							return a.selectedIndex - b.selectedIndex;
						}).length;
					} else if ((count = INPUT_WD.findDescendants(next)) && count.length) {
						// WMultiTextField may have empty inputs to fool the validator!
						count = unique(Array.prototype.filter.call(count, function(input) {
							return input.value;
						}), function(a, b) {
							return (a.value === b.value ? 0 : 1);
						}).length;
						underFlag = "validation_multitext_undermin";
						overFlag = "validation_multitext_overmax";
					} else {
						// set it to zero because otherwise it will be a zero length nodelist and not equivalent of false
						count = 0;
					}

					if (count) {  // count may be zero now (after filter/unique)
						if (min && count < min) {
							isInvalid = true;
							flag = i18n.get(underFlag);
							limit = min;
						} else if (max && count > max) {
							isInvalid = true;
							flag = i18n.get(overFlag);
							limit = max;
						}
					}
				}
				if (isInvalid) {
					_flag(next, flag, limit);
				}
				return isInvalid;
			}

			/**
			 * Flag any errors.
			 * @function
			 * @private
			 * @param {Element} element The multiFormComponent which failed validation.
			 * @param {String} flag The framework text of the message in sprintf format with placeholders for the label
			 *                 text and selection constraint limit.
			 * @param {int} limit The max/min number of values/selections.
			 */
			function _flag(element, flag, limit) {
				var message = sprintf.sprintf(flag, validationManager.getLabelText(element), limit);
				feedback.flagError({element: element, message: message});
			}

			/**
			 * Validation for multiFormComponent.
			 * @function
			 * @private
			 *
			 * @param {Element} container DOM element, the container being validated (usually form).
			 * @returns {boolean} true if the container is valid.
			 */
			function validate(container) {
				var result = true,
					controls,
					obj = {container: container,
						widget: CONTAINER,
						constraint: required.CONSTRAINTS.CLASSNAME,
						position: "beforeEnd"},
					_required = required.complexValidationHelper(obj);

				if ((controls = (CONTAINER.isOneOfMe(container)) ? [container] : CONTAINER.findDescendants(container))) {
					controls = Array.prototype.filter.call(controls, filter);
					result = !(controls && controls.length);
				}
				return result && _required;
			}


			/**
			 * Revalidate a multiFormControl after a change of input/selection or a change in the number of fields.
			 * @function
			 * @private
			 * @param {Element} element The MultiFormComponent container element wrapping the component which invoked the event.
			 */
			function revalidate(element) {
				var container = getContainer(element);
				if (container) {
					validationManager.revalidationHelper(container, validate);
				}
			}

			/**
			 * Revalidate on change.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event a change event as wrapped by the WComponent event module.
			 */
			function changeEvent($event) {
				var container = getContainer($event.target);
				if (container) {
					if (validationManager.isValidateOnChange()) {
						if (validationManager.isInvalid(container)) {
							revalidate(container);
							return;
						}
						validate(container);
						return;
					}
					revalidate(container);
				}
			}

			function blurEvent($event) {
				var element = $event.target,
					container = getContainer(element);
				if (container && !validationManager.isInvalid(container)) {
					validate(container);
				}
			}

			/**
			 * Successful click on an add/remove field button will need to revalidate.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event a click event as wrapped by the WComponent event module.
			 */
			function clickEvent($event) {
				var element, container;
				if (!$event.defaultPrevented && (element = BUTTON.findAncestor($event.target))) {
					if (multiFormComponent.getButtonType(element) && (container = getContainer(element))) {
						revalidate(container);
					}
				}
			}

			/**
			 * Browsers which cannot capture change events have to attach the event to each component.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event a focus[in] event as wrapped by the WComponent event module.
			 */
			function focusEvent($event) {
				var element = $event.target,
					BOOTSTRAPPED = "validation.multiFormComponent.bs",
					container;
				if (element && !attribute.get(element, BOOTSTRAPPED) && Widget.isOneOfMe(element, multiFormComponent.getInputWidget())) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent);
					container = getContainer(element);
					if (container && !attribute.get(container, BOOTSTRAPPED) && validationManager.isValidateOnBlur()) {
						attribute.set(container, BOOTSTRAPPED, true);
						if (event.canCapture) {
							event.add(container, event.TYPE.blur, blurEvent, 1, null, true);
						} else {
							event.add(container, event.TYPE.focusout, blurEvent);
						}
					}
				}
			}

			/**
			 * Initialisation callback to set up event listeners.
			 * @function module:wc/ui/validation/multiFormComponent.initialise
			 * @param {Element} element A DOM element: in practice ths is usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent, 1);  // we want the click handler late so that add/remove runs first.
			};

			/**
			 * Initialisation callback to attach vaidation subscriber.
			 * @function module:wc/ui/validation/multiFormComponent.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
				isComplete.subscribe(_isComplete);
			};
		}

		/**
		 * Provides functionality to undertake client validation of WMultiDropdown and WMultiTextField.
		 *
		 * @module
		 * @requires wc/dom/attribute
		 * @requires wc/dom/event
		 * @requires wc/dom/initialise
		 * @requires wc/dom/Widget
		 * @requires wc/i18n/i18n
		 * @requires wc/ui/multiFormComponent
		 * @requires wc/array/unique
		 * @requires external:lib/sprintf
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/feedback
		 */
		var instance = new ValidationMultiFormComponent();
		initialise.register(instance);
		return instance;
	});
