/**
 * Provides functionality to undertake client validation of WDateField.
 *
 * @module ${validation.core.path.name}/dateField
 * @requires module:wc/date/interchange
 * @requires module:wc/date/getDifference
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/i18n/i18n
 * @requires module:wc/ui/dateField
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires external:lib/sprintf
 * @requires module:${validation.core.path.name}/isComplete
 */
define(["wc/date/interchange",
		"wc/date/getDifference",
		"wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/i18n/i18n",
		"wc/ui/dateField",
		"${validation.core.path.name}/validationManager",
		"wc/ui/getFirstLabelForElement",
		"lib/sprintf",
		"${validation.core.path.name}/isComplete"],
	/** @param interchange wc/date/interchange @param getDifference wc/date/getDifference @param attribute wc/dom/attribute @param event wc/dom/event @param initialise wc/dom/initialise @param i18n wc/i18n/i18n @param dateField wc/ui/dateField @param validationManager ${validation.core.path.name}/validationManager @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param sprintf lib/sprintf @param isComplete ${validation.core.path.name}/isComplete @ignore */
	function(interchange, getDifference, attribute, event, initialise, i18n, dateField, validationManager, getFirstLabelForElement, sprintf, isComplete) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/dateField~ValidationDateInput
		 * @private
		 */
		function ValidationDateInput() {
			/**
			 * A descriptor for a WDateField.
			 * @see {@link module:wc/ui/dateField#getWidget}.
			 * @var {Widget}
			 * @private
			 */
			var DATE_FIELD = dateField.getWidget();

			/**
			 * Array filter function which checks if a date is within the future/past constraint (if any).
			 * @function
			 * @private
			 * @param {Element} element A WDateField
			 * @returns {boolean} true if constraint not met; false if constraints met, no constraints or date field is empty
			 */
			function isDateInvalid(element) {
				var invalid = false,
					date, flag, value,
					textbox = dateField.getTextBox(element),
					comparisonDate,
					label,
					LABEL_PLACEHOLDER = "%s",
					minAttrib = "${wc.common.attrib.min}",
					maxAttrib = "${wc.common.attrib.max}";

				if (!textbox || dateField.getPartialDateWidget().isOneOfMe(textbox)) {
					return false;  // do not apply constraint validation to partial date fields, even if the date entered is a full date.
				}

				if (textbox && (value = dateField.getValue(element)) && !validationManager.isExempt(element)) {
					if (textbox.getAttribute("type") === "date") {
						minAttrib = "min";
						maxAttrib = "max";
					}

					date = interchange.toDate(value);
					/*
					 * There are a set of very unusual circumstances which can cause the change and close calls to
					 * acceptFirstMatch to fail. These involve concurrent use of the keyboard and mouse along with
					 * strategic defocusing of the document whilst not losing focus of the window. Odd but true. This
					 * double up just makes sure that if there is any input into the input element then we make every
					 * effort to convert it to a value we can understand.
					 */
					if (!date) {
						dateField.acceptFirstMatch(textbox);
						value = dateField.getValue(textbox);
						date = interchange.toDate(value);
					}

					if (date) {
						if ((comparisonDate = textbox.getAttribute(minAttrib))) {
							comparisonDate = interchange.toDate(comparisonDate);
							if (getDifference(date, comparisonDate) < 0) {
								invalid = true;
								comparisonDate = comparisonDate.toLocaleDateString();
								flag = i18n.get("${validation.dateField.i18n.min}");
								// manipulate flag to replace the numbered string placeholders (so it ends up in the same format as the other flags)
								flag = sprintf.sprintf(flag, LABEL_PLACEHOLDER, comparisonDate);
							}
						}
						if ((comparisonDate = textbox.getAttribute(maxAttrib))) {
							comparisonDate = interchange.toDate(comparisonDate);
							if (getDifference(date, comparisonDate) > 0) {
								invalid = true;
								comparisonDate = comparisonDate.toLocaleDateString();
								flag = i18n.get("${validation.dateField.i18n.max}");
								// manipulate flag to replace the numbered string placeholders (so it ends up in the same format as the other flags)
								flag = sprintf.sprintf(flag, LABEL_PLACEHOLDER, comparisonDate);
							}
						}
					}
					else {
						// a full date field can only be valid if a full date is entered and getDateFromElement will return ""
						flag = i18n.get("${validation.dateField.i18n.mustBeFull}");
						invalid = true;
					}
				}
				if (invalid) {
					label = getFirstLabelForElement(textbox, true) || element.title || i18n.get("${validation.core.i18n.unlabelledQualifier}");
					validationManager.flagError({element: element, message: sprintf.sprintf(flag, label)});
				}
				return invalid;
			}

			/**
			 * Message formatting function. Used by the function validate.
			 * @function
			 * @private
			 * @param {Element} element The element in an invalid state.
			 * @returns {String} The formatted validation message.
			 */
			function messageFunction(element) {
				var textbox = dateField.getTextBox(element),
					label = getFirstLabelForElement(textbox, true) || textbox.title || i18n.get("${validation.core.i18n.unlabelledQualifier}");
				return sprintf.sprintf(i18n.get("${validation.core.i18n.requiredField}"), label);
			}

			/**
			 * Determines if a date field is 'valid' for client side validation. A WDateField is valid
			 * if it meets the following criteria:
			 * <ol>
			 *  <li>If the WDateField is mandatory then the WDateField has content</li>
			 *  <li>If the WDateField has content then it is able to be parsed to a date</li>
			 *  <li>If the WDateField has min and/or max constraints and has content then the content is within the
			 *      expected range.</li></ol>
			 * <p><strong>NOTE:</strong> will always return TRUE for a partial date field to prevent date validation problems.</p>
			 * @function
			 * @private
			 * @param {Element} container The element being validated, a form, container or WDateField
			 * @returns {boolean} true if the WDateField is valid
			 */
			function validate(container) {
				var valid = true,
					invalid,
					candidates,
					incomplete = [],
					complete = true;

				if (dateField.isOneOfMe(container, true)) {
					candidates = [container];
				}
				else {
					candidates = DATE_FIELD.findDescendants(container);
				}
				Array.prototype.forEach.call(candidates, function(next) {
					var textBox;
					if (dateField.isNativeInput(next)) {
						textBox = dateField.getTextBox(next);
						if (!textBox.getAttribute("required")) {
							return;
						}
						if (!textBox.value) {
							incomplete.push(next);
						}
					}
					else {
						if (!next.getAttribute("aria-required")) {
							return;
						}
						if (!dateField.getValue(next)) {
							incomplete.push(next);
						}
					}
				});

				if (incomplete.length) {
					complete = false;

					incomplete.forEach(function(next) {
						var message = messageFunction(next),
							obj = {element: next, message: message};
						validationManager.flagError(obj);
					});
				}


				if (dateField.isOneOfMe(container, true)) {
					valid = !isDateInvalid(container);
				}
				else {
					invalid = Array.prototype.filter.call(candidates, isDateInvalid, this);
					if (invalid && invalid.length) {
						valid = false;
					}
				}
				return complete && valid;
			}


			/**
			 * Re-validate a WDateField which was in an invalid state.
			 * @function
			 * @private
			 * @param {Element} element The WDateField to test.
			 */
			function revalidate(element) {
				validationManager.revalidationHelper(element, validate);
			}


			/**
			 * Change event handler. This is attached to body in browsers which capture and bubble change events and
			 * directly to each WDateField's input element when the element is first focused otherwise.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event The wrapped change event as published by the WComponent event manager
			 */
			function changeEvent($event) {
				var dateField = DATE_FIELD.findAncestor($event.target);
				if (dateField) {
					revalidate(dateField);
				}
			}


			/**
			 * Focus event handler used to lazily attach a change event listener to a WDateField when first focused.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event the wrapped focus/focusin event as published by the WComponent event manager.
			 */
			function focusEvent($event) {
				var element = $event.target,
					BOOTSTRAPPED = "validation.dateField.bs";
				if (!$event.defaultPrevented && dateField.isOneOfMe(element, false) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
				}
			}


			/**
			 * Initialisation function to attach the change event listener (or focus listener in obsolete browsers).
			 * TODO: maybe move to postInit?
			 * @function module:${validation.core.path.name}/dateField.initialise
			 * @public
			 * @param {Element} element The element being initialised.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.change, changeEvent, 1, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent, 1);
				}
			};


			/**
			 * Determines if a WDateField is 'complete'. A date input is complete is it has content without attempting
			 * to determine if the content is a valid date.
			 * @function
			 * @private
			 * @param {Element} element A WDateField.
			 * @returns {boolean} true if element is complete.
			 */
			function isCompleteHelper(element) {
				var textbox = dateField.getTextBox(element),
					iAmComplete = false;
				if (textbox && textbox.value) {
					iAmComplete = true;
				}
				return iAmComplete;
			}


			/**
			 * Subscriber function for {@link ./isComplete} to test the completeness of a container.
			 * @function
			 * @private
			 * @param {Element} container The form, subform or date field we are testing for completeness.
			 * @returns {boolean} true if container is complete.
			 */
			function isCompleteSubscriber(container) {
				return isComplete.isCompleteHelper(container, DATE_FIELD, isCompleteHelper);
			}


			/**
			 * Late initialisation function to set up dateField validation.
			 * TODO: move initialisation here, do we need the change listeners so early?
			 * @function module:${validation.core.path.name}/dateField.postInit
			 * @public
			 */
			this.postInit = function() {
				isComplete.subscribe(isCompleteSubscriber);
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:${validation.core.path.name}/dateField */ instance = new ValidationDateInput();
		initialise.register(instance);
		return instance;
	});
