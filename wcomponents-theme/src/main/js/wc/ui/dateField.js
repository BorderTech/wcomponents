/**
 * Provides functionality for implementing a date input control. Allows for native support of full date input and
 * provides both a polyfill for date input and an implementation of a control which can accept and parse parts of a
 * date.
 *
 * @see {@link module:wc/ui/calendar} which is used as the date picker polyfill.
 *
 * @module
 *
 * @requires module:wc/has
 * @requires module:wc/array/unique
 * @requires module:wc/date/Parser
 * @requires module:wc/date/interchange
 * @requires module:wc/date/Format
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/cancelUpdate
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/timers
 * @requires module:wc/key
 * @requires module:wc/dom/textContent
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onchangeSubmit
 * @requires module:wc/ui/listboxAnalog
 *
 */
define(["wc/has",
		"wc/array/unique",
		"wc/date/Parser",
		"wc/date/interchange",
		"wc/date/Format",
		"wc/dom/attribute",
		"wc/dom/cancelUpdate",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/timers",
		"wc/key",
		"wc/dom/textContent",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/onchangeSubmit",
		"wc/ui/listboxAnalog"],
	function(has, unique, Parser, interchange, Format, attribute, cancelUpdate, event, focus, formUpdateManager, initialise, shed, tag, Widget, i18n, timers, key, textContent, ajaxRegion, processResponse, onchangeSubmit) {
		"use strict";

		/* unused dependencies:
		 * wc/ui/listboxAnalog is required for the suggestion list but is not used directly in this class.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/dateField~DateInput
		 * @private
		 */
		function DateInput() {
			var parsers,  // this will store the Parser instances when first needed
				formatter,
				isNative = has("native-dateinput"),
				BOOTSTRAPPED = "wc/ui/dateField_bootstrapped",
				DATE_FIELD = new Widget("div", "wc-datefield"),
				DATE_FIELD_RO = new Widget("", "wc_datero"),
				DATE = new Widget("input", "", {"type": "date"}),
				DATE_PARTIAL = new Widget("input", "", {"type": "text"}),
				DATE_INPUTS = [DATE, DATE_PARTIAL],
				SUGGESTION_LIST = new Widget("ul", "", {"role": "listbox"}),
				OPTION_WD,
				VALUE_ATTRIB = "data-wc-value",
				optionVal = {},
				filterTimer,
				LAUNCHER = new Widget("button", "wc_wdf_cal"),
				startVal = {},
				openDateCombo = "",  // {string} the id of the currently open date field (if any)
				IETimeout = (has("ie") === 8) ? 50 : 0;  // IE cannot update itself fast enough to focus a newly opened list

			DATE.descendFrom(DATE_FIELD, true);
			DATE_PARTIAL.descendFrom(DATE_FIELD, true);
			SUGGESTION_LIST.descendFrom(DATE_FIELD, true);

			/**
			 * Get the SUGGESTION_LIST part of a dateField.
			 * @function
			 * @private
			 * @param {Element} element A dateField or an option in the list.
			 * @param {integer} force Use a specific direction rather than doing a component lookup:
			 *    <ul>
			 *    <li>-1 look down (findDescendant)</li>
			 *    <li>1 look up (findAncestor)</li>
			 *    <li>undefined (or 0 or false or null) test element.<br/>
			 *    NOTE: any value other than -1 or 1 will do a double lookup and
			 *    will find a dateField SUGGESTION_LIST if present since it finds the DATE_FIELD
			 *    ancestor and uses that the find its SUGGESTION_LIST descendant.</li>
			 *    </ul>
			 *    This parameter is a convenience to prevent unnecessary use of isOneOfMe
			 *    or looking up to the ancestor then down to the SUGGESTION_LIST when we
			 *    already know what we have before calling this function.
			 * @returns {Element} The SUGGESTION_LIST sub-component element of the dateField.
			 */
			function getSuggestionList(element, force) {
				var result, dateField;
				if (force === -1 || (!force && DATE_FIELD.isOneOfMe(element))) {
					result = SUGGESTION_LIST.findDescendant(element);
				}
				else if (force === 1) {
					result = SUGGESTION_LIST.findAncestor(element);
				}
				else if ((dateField = DATE_FIELD.findAncestor(element))) {
					result = SUGGESTION_LIST.findDescendant(dateField);
				}
				return result;
			}

			/**
			 * Set native value and remove elements and attributes from the date field wrapper not needed by native
			 * date input controls.
			 * @function
			 * @private
			 * @param {Element} element The WDateField to clean up.
			 */
			function cleanUpNative(element) {
				var childEl, value;
				childEl = instance.getTextBox(element);
				if (!childEl) {
					return;
				}
				if ((value = element.getAttribute("data-wc-value"))) {
					childEl.value = value;
					if (document.activeElement === childEl) {
						startVal[element.id] = value;
						onchangeSubmit.ignoreNextChange();
						ajaxRegion.ignoreNextChange();
					}
				}
				element.removeAttribute("role");
				element.removeAttribute("aria-expanded");
				element.removeAttribute("aria-autocomplete");
				element.removeAttribute("data-wc-value");
				if (element.getAttribute("aria-required")) {
					childEl.setAttribute("required", "required");
					element.removeAttribute("aria-required");
				}
				if (element.getAttribute("aria-disabled")) {
					childEl.setAttribute("disabled", "disabled");
					element.removeAttribute("aria-disabled");
				}
				childEl.removeAttribute("autocomplete");
				if (!childEl.getAttribute("required")) {
					childEl.removeAttribute("placeholder");
				}
				childEl.removeAttribute("aria-owns");
				if ((childEl = getSuggestionList(element, -1))) {
					element.removeChild(childEl);
				}
				if ((childEl = LAUNCHER.findDescendant(element))) {
					element.removeChild(childEl);
				}
			}

			/**
			 * Close an open dateField (if any) when any element which is not the currently open date field receives
			 * focus or when the input box of the currently open dateField receives focus.
			 * @function
			 * @private
			 * @param {Element} element The element being focussed.
			 */
			function closeDateCombo(element) {
				var dateField = DATE_FIELD.findAncestor(element),
					otherDateField;
				/*
				 * When a date field combo is closed clear its filters, set its value and then collapse it.
				 * @param {Element} dateField The field to collapse.
				 */
				function _collapseHelper(_df) {
					if (filterTimer) {
						timers.clearTimeout(filterTimer);
					}
					if (DATE_FIELD.isOneOfMe(_df)) {
						instance.acceptFirstMatch(instance.getTextBox(_df));
					}
					if (IETimeout) {
						timers.setTimeout(function() {
							shed.collapse(_df);
						}, 150);
					}
					else {
						shed.collapse(_df);
					}
				}

				onchangeSubmit.clearIgnoreChange();
				ajaxRegion.clearIgnoreChange();
				if (openDateCombo) {
					// close any open dateFields when focusing elsewhere
					otherDateField = document.getElementById(openDateCombo);
					if (otherDateField && DATE_FIELD.isOneOfMe(otherDateField) && (!dateField || dateField.id !== openDateCombo) && shed.isExpanded(otherDateField)) {
						_collapseHelper(otherDateField);
					}
				}
				if (dateField && isDateInput(element) && shed.isExpanded(element)) {
					_collapseHelper(dateField);
				}
			}

			/**
			 * Focus the listbox part of a dateField if it has any options.
			 * @function
			 * @private
			 * @param {Element} suggestionList the SUGGESTION_LIST sub-component to focus
			 */
			function focusListbox(suggestionList) {
				OPTION_WD = OPTION_WD || new Widget("", "", {"role": "option"});
				if (suggestionList && OPTION_WD.findDescendant(suggestionList)) {
					onchangeSubmit.ignoreNextChange();
					ajaxRegion.ignoreNextChange();

					if (filterTimer) {
						timers.clearTimeout(filterTimer);
					}
					// NOTE: this timeout has been tested further and is absolutely required in IE8
					if (IETimeout) {
						timers.setTimeout(focus.focusFirstTabstop, IETimeout, suggestionList);
					}
					else {
						focus.focusFirstTabstop(suggestionList);
					}
				}
			}

			/**
			 * Formats a transfer date to the display date to be presented to the user.
			 * @function
			 * @private
			 * @param {String} xfer The transfer date to be formatted.
			 * @returns {String} A human readable date as a string.
			 */
			function format(xfer) {
				var myFormatter = formatter || (formatter = new Format(i18n.get("${wc.ui.dateField.i18n.mask.format}"))),
					result = myFormatter.format(xfer);
				return result;
			}

			/*
			 * Indicates if an element a date field's input component.
			 * @function
			 * @private
			 * @param {Element} element Any dom node.
			 * @returns {Boolean} true if the element is a date field's input element.
			 */
			function isDateInput(element) {
				return Widget.isOneOfMe(element, DATE_INPUTS);
			}

			/**
			 * Update the value of the dateField based on interaction with an option. NOTE: native combos in HTML5 do
			 * not update on select of the options!
			 * @function
			 * @private
			 * @param {Element} dateField The dateField to update.
			 * @param {Element} option The option which caused the update.
			 */
			function setValueFromOption(dateField, option) {
				var suggestionList = getSuggestionList(dateField, -1), value, textbox;

				if (suggestionList) {
					value = option.hasAttribute(VALUE_ATTRIB) ? option.getAttribute(VALUE_ATTRIB) : textContent.get(option);

					if (value && interchange.isValid(value)) {
						value = format(value);
					}

					if ((textbox = instance.getTextBox(dateField))) {
						textbox.value = value;  // do not fire change event here: do it on collapse
					}

					if (optionVal[(dateField.id)] !== instance.getValue(dateField, true)) {
						timers.setTimeout(event.fire, 0, instance.getTextBox(dateField), event.TYPE.change);
					}
				}
			}

			/**
			 * Compares two formatted date strings (that is, the date as displayed to the user).
			 * @function
			 * @private
			 * @param {string} valA A formatted date string.
			 * @param {string} valB A formatted date string.
			 * @returns {Boolean} true if they are the same for display purposes.
			 */
			function formattedDatesSame(valA, valB) {
				var result = false,
					s1 = valA.trim(),
					s2 = valB.trim();
				if (s1 === s2 || s1.toLocaleLowerCase() === s2.toLocaleLowerCase()) {
					result = true;
				}
				return result;
			}

			/**
			 * Takes an array of strings and builds them into HTML li elements.
			 * @function
			 * @private
			 * @param {String[]} suggestions The date suggestions as strings.
			 * @returns {String} The li elements as a single string.
			 */
			function getSuggestions(suggestions) {
				var html = [],
					i,
					tabIndex,
					DATE_FIELD_TAGNAME = "LI",
					close = tag.toTag(DATE_FIELD_TAGNAME, true);

				for (i = 0; i < suggestions.length; i++) {
					tabIndex = i === 0 ? "0" : "-1";
					suggestions[i].attributes = suggestions[i].attributes || "";
					suggestions[i].attributes += " role='option' " + VALUE_ATTRIB + "='" + suggestions[i].html + "' tabindex='" + tabIndex + "'";

					html.push(tag.toTag(DATE_FIELD_TAGNAME, false, suggestions[i].attributes));
					html.push(suggestions[i].html);
					html.push(close);
				}
				return html.join("");
			}

			/**
			 * Initialises the "parsers" instance variable.
			 * @function
			 * @private
			 */
			function initParsers() {
				var shortcuts = ["ytm", "+-"],
					standardMasks = shortcuts.concat(i18n.get("${wc.ui.dateField.i18n.masks.full}").split(",")),
					partialMasks = standardMasks.concat(i18n.get("${wc.ui.dateField.i18n.masks.partial}").split(","));

				/*
				 * Creates a new instance of a Parser
				 */
				function createParser(masks, expandYearIntoPast, rolling) {
					var result = new Parser();
					result.setRolling(!!rolling);
					result.setMasks(masks || standardMasks);
					result.setExpandYearIntoPast(!!expandYearIntoPast);
					return result;
				}

				parsers = {
					standard: createParser(),
					past: createParser(null, true),
					partial: createParser(partialMasks),
					partialPast: createParser(partialMasks, true)
				};
			}

			/**
			 * Finds the correct date parser for this element.
			 * @function
			 * @private
			 * @param {Element} element A date input element.
			 * @returns {Parser} An instance of {@link module:wc/date/Parser}
			 */
			function getParser(element) {
				var result;
				if (!parsers) {
					initParsers();
				}

				if (DATE_PARTIAL.isOneOfMe(element)) {
					result = parsers.partial;
				}
				else {
					result = parsers.standard;
				}

				return result;
			}

			/**
			 * Get a list of potential date matches based on the user's input.
			 * @function
			 * @private
			 * @param {Element} element The input element of the date field.
			 * @param {String} [overrideVal] Use this as the value to match, instead of the element's value.
			 * @returns {String[]} Potential dates as strings.
			 */
			function getMatches(element, overrideVal) {
				// trim leading & trailing spaces
				var value = overrideVal || element.value,
					parser = getParser(element),
					matches = parser.parse(value.trim());

				matches = unique(matches, function(a, b) {
					var result = 1;
					if (a.day === b.day && a.month === b.month && a.year === b.year) {
						result = 0;
					}
					return result;
				});
				return matches;
			}

			/**
			 * Converts a formatted date string (that is, a string formatted for display to the users) to a transfer
			 * date string. It is assumed that you have already tried to get the transfer date from the value attribute.
			 * @function
			 * @private
			 * @param {Element} element A dateField input element
			 * @param {Boolean} [guess] If true then in the case that we can not precisely reverse format the
			 * dateField's value we will return a "guess" which will be the first match (if there are possible matches).
			 * @returns {String} A transfer date string if possible.
			 */
			function reverseFormat(element, guess) {
				var result, matches, next, i, value, len,
					currentValue = element.value;

				if ((currentValue = currentValue.trim())) {
					matches = getMatches(element);
					for (i = 0, len = matches.length; i < len; i++) {
						next = matches[i];
						value = format(next.toXfer());
						if (formattedDatesSame(value, currentValue)) {
							result = next.toXfer();
							break;
						}
					}
					if (!result && len && guess) {
						result = matches[0].toXfer();
					}
				}
				return result;
			}

			/**
			 * Put the list of potential matches for the value of a input element into the SUGGESTION_LIST sub-component
			 * and expand the parent dateField.
			 * @function
			 * @private
			 * @param {String[]} matches The content for the suggestions.
			 * @param {Element} dateField The date field to which the matches belong.
			 */
			function showSuggestions(matches, dateField) {
				var lastVal = "", i, suggestionList;
				// console.info("Number of matching dates:", matches.length);
				for (i = 0; i < matches.length; i++) {
					lastVal = format(matches[i].toXfer());
					matches[i].html = lastVal;
				}

				if (matches.length && (suggestionList = getSuggestionList(dateField))) {
					suggestionList.innerHTML = "";
					if (!(matches.length === 1 && formattedDatesSame(lastVal, instance.getTextBox(dateField).value))) {
						suggestionList.innerHTML = getSuggestions(matches);
						if (!shed.isExpanded(dateField)) {
							shed.expand(dateField);
						}
					}
				}
			}

			/**
			 * Get any matches for the input element content and pass them through to the SUGGESTION_LIST sub-component
			 * to provide an on-the-fly filtered list of potential matches.
			 * @function
			 * @private
			 * @param {Element} dateField a date field
			 * @param {integer} [delay] a timeout delay, default to 250 if not set. Set explicitly to 0 to have no delay.
			 */
			function filterOptions(dateField, delay) {
				var _delay = delay;

				function _filter() {
					var textbox = instance.getTextBox(dateField),
						matches = getMatches(textbox),
						suggestionList;
					if (matches.length) {
						if (!shed.isExpanded(dateField)) {
							shed.expand(dateField);
						}
						showSuggestions(matches, dateField);
					}
					else if ((suggestionList = getSuggestionList(dateField))) {
						suggestionList.innerHTML = "";
					}
				}

				if (DATE_FIELD.isOneOfMe(dateField)) {
					if (filterTimer) {
						timers.clearTimeout(filterTimer);
					}
					if (delay === 0) {
						_filter();
					}
					else {
						if (!_delay) {
							_delay = 125;
						}
						filterTimer = timers.setTimeout(_filter, _delay);
					}
				}
			}

			/**
			 * Set the readable value of the text input part of a date field or the content of a read-only date input
			 * based on its initial XML value.
			 * @function
			 * @private
			 * @param {Element} field The date field wrapper element.
			 */
			function setInputValue(field) {
				var value,
					textVal,
					textBox;
				if ((value = (field.getAttribute(VALUE_ATTRIB) || field.getAttribute("datetime"))) && (textVal = format(value))) {
					if (DATE_FIELD.isOneOfMe(field)) {
						textBox = instance.getTextBox(field);
						textBox.value = textVal;
					}
					else {
						textContent.set(field, textVal);
					}
				}
			}

			/**
			 * Undertake setup on any date fields. This involves setting the initial readable value and removing
			 * unnecessary artifacts if the input is a native date input.
			 * @function
			 * @private
			 * @param {Element} container A HTML element which is itself or may contain date fields.
			 */
			function setUpDateFields(container) {
				var _container = container || document.body,
					fields,
					RO = [DATE_FIELD, DATE_FIELD_RO];

				if (container && Widget.isOneOfMe(container, RO)) {
					fields = [container];
				}
				else {
					fields = Widget.findDescendants(_container, RO);
				}

				Array.prototype.forEach.call(fields, function(next) {
					if (instance.isNativeInput(next)) {
						cleanUpNative(next);
					}
					else {
						setInputValue(next);
					}
				});
				cancelUpdate.resetAllFormState();
			}

			/**
			 * Modify fields in an AJAX response before insertion into the DOM.
			 * @function
			 * @private
			 * @param {Element} element The Element being replace/filled. Not used.
			 * @param {DocumentFragment} documentFragment The document fragment from the AJAX response.
			 */
			function ajaxSetup(element, documentFragment) {
				setUpDateFields(documentFragment);
			}

			/**
			 * Subscriber to SHED pseudo-event publisher. This is used to set instance variables on EXPAND for later
			 * processing such as determining if the field's calue has changed on COLLAPSE; and for manipulating the
			 * various sub-components on ENABLE, DISABLE, MANDATORY, OPTIONAL, HIDE and SHOW.
			 * @function
			 * @private
			 * @param {Element} element The element SHED has acted upon.
			 * @param {String} action The SHED action.
			 */
			function shedSubscriber(element, action) {
				var textbox,
					target,
					func;
				if (element) {
					if (DATE_FIELD.isOneOfMe(element)) {
						if (action === shed.actions.EXPAND) {
							if (shed.isExpanded(element)) {
								openDateCombo = element.id;
								optionVal[(element.id)] = instance.getValue(element);
								filterOptions(element, 0);
							}
						}
						else if (action === shed.actions.COLLAPSE) {
							if (!shed.isExpanded(element)) {
								optionVal[(element.id)] = null;
								openDateCombo = "";
								if (filterTimer) {
									timers.clearTimeout(filterTimer);
									filterTimer = null;
								}
							}
						}
						else if ((textbox = instance.getTextBox(element))) {
							func = getFuncForAction(action);
							if (func) {
								shed[func](textbox);  // publish this to make changes to the label
								if ((action === shed.actions.ENABLE || action === shed.actions.DISABLE) && (target = LAUNCHER.findDescendant(element))) {
									shed[func](target, true);  // no need to publish changing the state of the calendar launcher
								}
							}
						}
					}
				}
			}

			/*
			 * Helper for shedSubscriber.
			 */
			function getFuncForAction(action) {
				var func;
				switch (action) {
					case shed.actions.SHOW:
						func = "show";
						break;
					case shed.actions.HIDE:
						func = "hide";
						break;
					case shed.actions.MANDATORY:
						func = "mandatory";
						break;
					case shed.actions.OPTIONAL:
						func = "optional";
						break;
					case shed.actions.ENABLE:
						func = "enable";
						break;
					case shed.actions.DISABLE:
						func = "disable";
						break;
				}
				return func;
			}

			/**
			 * Write the state of the date fields. DateField does not just return simple content from its input but also
			 * has to calculate and return the xferDate. If the input is a native date input then these are the same but
			 * both have to be sent to the server.
			 * @function
			 * @private
			 * @param {Element} form A form (or other container) the state of which is being written.
			 * @param {Element} stateContainer The element into which the state fields are written.
			 */
			function writeState(form, stateContainer) {
				var dateFields = DATE_FIELD.findDescendants(form), i, next, numVal, textBox;
				for (i = 0; i < dateFields.length; i++) {
					next = dateFields[i];
					if (!shed.isDisabled(next)) {
						if (instance.isNativeInput(next)) {
							if ((textBox = instance.getTextBox(next)) && textBox.value) {
								formUpdateManager.writeStateField(stateContainer, next.getAttribute("data-wc-name"), textBox.value);
							}
						}
						else if ((numVal = instance.getValue(next))) {
							formUpdateManager.writeStateField(stateContainer, next.getAttribute("data-wc-name"), numVal);
						}
					}
				}
			}

			/**
			 * Change event handler. This is attached directly to each dateField's input element when the element is
			 * first focused if the date field is not a native date input. It is used to convert user input into the
			 * required format using {@link acceptFirstMatch}.
			 * @function
			 * @private
			 * @param {Event} $event the change event
			 */
			function changeEvent($event) {
				var element = $event.target,
					dateField;

				if (instance.isNativeInput(element, true)) {
					return;
				}

				if ((dateField = DATE_FIELD.findAncestor(element))) {
					instance.acceptFirstMatch(element);
					dateField.removeAttribute(VALUE_ATTRIB);
				}
			}

			/**
			 * Focus event handler closes any open dateField when ANYTHING is focused. This also needs to set up some
			 * options for native date inputs so we can successfully override some 'interesting' implementations of
			 * change event publication.
			 * @function
			 * @private
			 * @param {Event} $event The focus/focusin event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if ($event.defaultPrevented) {
					return;
				}
				if (instance.isNativeInput(element, true)) {
					// if the input has an initial value then some mthods of changing the value may fire immediate change events in some browsers.
					startVal[element.id] = element.value;
					onchangeSubmit.ignoreNextChange();
					ajaxRegion.ignoreNextChange();
					return;
				}
				if (isDateInput(element) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent);
				}
				closeDateCombo(element);
			}

			/**
			 * Blur event listener which tries to determine if a native date input has changed so we can fire a change event.
			 * This is required as current implementations of date inputs fire a change event when any part of an existing
			 * date is changed not just when the input loses focus.
			 * @function
			 * @private
			 * @param {Event} $event The blur event.
			 */
			function blurEvent($event) {
				var element = $event.target;
				if ($event.defaultPrevented) {
					return;
				}
				if (instance.isNativeInput(element, true)) {
					onchangeSubmit.clearIgnoreChange();
					ajaxRegion.clearIgnoreChange();
					if (startVal[element.id] !== element.value) {
						startVal[element.id] = null;
						event.fire(element, event.TYPE.change);
					}
				}
			}

			/**
			 * Click event handler for the suggestion list options. Set the date field value according to the option
			 * clicked.
			 * @function
			 * @private
			 * @param {Event} $event The click event as published by the wc event manager
			 */
			function clickEvent($event) {
				var target = $event.target, dateField;
				if ($event.defaultPrevented || instance.isNativeInput(target, true)) {
					return;
				}
				if ((dateField = DATE_FIELD.findAncestor(target)) && !shed.isDisabled(dateField) && getSuggestionList(target, 1)) {
					// update on option click
					setValueFromOption(dateField, target);  // yes, revert to $event.target here: we want the option not the SUGGESTION_LIST
					focus.setFocusRequest(instance.getTextBox(dateField), function() {
						shed.collapse(dateField);
					});
					$event.preventDefault();
				}
			}


			function focusAndSetValue(element, option) {
				var textbox = instance.getTextBox(element);
				setValueFromOption(element, option);
				if (textbox) {
					focus.setFocusRequest(textbox, function() {
						shed.collapse(element);
					});
				}
				else {
					shed.collapse(element);
				}
			}

			/**
			 * Keydown event handler. Used to set values and close the suggestion list. Much of this involves trying to
			 * capture closing the suggestion list or leaving the component completely and manipulating the input based
			 * on the sleected (or first) option in teh suggestion list.
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var element,
					keyCode = $event.keyCode,
					target = $event.target,
					suggestionList,
					preventDefault;

				if (instance.isNativeInput(target, true)) {
					return;
				}

				element = DATE_FIELD.findAncestor(target);
				if (!element || shed.isDisabled(element)) {
					return;
				}
				if (keyCode === KeyEvent.DOM_VK_ESCAPE) {
					preventDefault = handleEscapeKey(element, target);
				}
				else if (keyCode === KeyEvent.DOM_VK_RETURN) {
					if (handleEnterKey(element, target)) {
						preventDefault = true;
					}
				}
				else if ((keyCode === KeyEvent.DOM_VK_DOWN || keyCode === KeyEvent.DOM_VK_UP) && !(getSuggestionList(target, 1))) {
					if (shed.isExpanded(element) && (suggestionList = getSuggestionList(element, -1))) {
						focusListbox(suggestionList);
						preventDefault = true;  // so we don't cause a line scroll
					}
				}
				else if (keyCode === KeyEvent.DOM_VK_SPACE && target.hasAttribute(VALUE_ATTRIB) && getSuggestionList(target, 1)) {
					// SPACE on an option should update the dateField
					focusAndSetValue(element, target);
					preventDefault = true;  // so we don't cause a page scroll
				}
				else if (keyCode === KeyEvent.DOM_VK_TAB) {
					handleTabKey(element, target);
				}
				else if (!key.isMeta(keyCode) && isDateInput(target)) {
					element.removeAttribute(VALUE_ATTRIB);
					filterOptions(element);
				}
				if (preventDefault) {
					$event.preventDefault();
				}
			}

			/*
			 * Helper for keydownEvent.
			 */
			function handleEnterKey(element, target) {
				var preventDefault;
				if (shed.isExpanded(element)) {
					if (target.hasAttribute(VALUE_ATTRIB) && getSuggestionList(target, 1)) {
						preventDefault = true;  // so we don't submit from the suggestion list - yes this is needed I checked.
						focusAndSetValue(element, target);
					}
					else if (isDateInput(target)) {
						instance.acceptFirstMatch(target);
					}
				}
				return preventDefault;
			}

			/*
			 * Helper for keydownEvent.
			 */
			function handleTabKey(element, target) {
				if (shed.isExpanded(element)) {
					if (isDateInput(target)) {
						// accept and update on tab
						if (filterTimer) {
							timers.clearTimeout(filterTimer);
							filterOptions(element, 0);
						}
						// tab from textbox in dateField should update by accepting the first match
						instance.acceptFirstMatch(target);
					}
					else if (target.hasAttribute(VALUE_ATTRIB) && getSuggestionList(target, 1)) {
						// tab from an option should update the dateField
						setValueFromOption(element, target);
						shed.collapse(element);
					}
				}
			}

			/*
			 * Helper for keydownEvent.
			 */
			function handleEscapeKey(element, target) {
				var preventDefault, textbox;
				if (shed.isExpanded(element)) {
					// if we ESCAPE when on a SUGGESTION_LIST item focus the textbox
					if (getSuggestionList(target, 1) && (textbox = instance.getTextBox(element))) {
						focus.setFocusRequest(textbox, function() {
							shed.collapse(element);
						});
						preventDefault = true;  // so we don't close a dialog which contains the dropdown
					}
					else {
						shed.collapse(element);
						preventDefault = true;
					}
				}
				return preventDefault;
			}

			/**
			 * This allows another function to force the value of the given element to be parsed according to its parser
			 * and the first resulting match (if any) to be chosen.
			 * @function module:wc/ui/dateField.acceptFirstMatch
			 * @public
			 * @param {Element} element An input element, either full or partial date.
			 */
			this.acceptFirstMatch = function(element) {
				var matches, dateField, _value, _matches;
				if (this.isNativeInput(element)) {
					return;
				}
				if ((matches = getMatches(element)) && matches.length && (dateField = DATE_FIELD.findAncestor(element))) {
					_matches = matches.map(function(next) {
						return format(next.toXfer());
					});
					if (!(~_matches.indexOf(element.value))) {
						// if the element value is an exact match it means one of the potential matches is exactly the same as the current value
						dateField.removeAttribute(VALUE_ATTRIB);
						_value = format(matches[0].toXfer());
						if (_value !== element.value) {
							element.value = _value;
							timers.setTimeout(event.fire, 0, element, event.TYPE.change);
						}
					}
				}
			};

			/**
			 * Get the {@link module:wc/dom/Widget} which describes a calendar launch button. This is required by
			 * {@link module:wc/ui/calendar}.
			 * @function module:wc/ui/dateField.getLaunchWidget
			 * @public
			 * @returns {module:wc/dom/Widget} The description of the calendar picker launch button.
			 */
			this.getLaunchWidget = function() {
				return LAUNCHER;
			};

			/**
			 * Get the partial date widget.
			 * @function module:wc/ui/dateField.getPartialDateWidget
			 * @public
			 * @returns {module:wc/dom/Widget} the Partial Date Widget.
			 */
			this.getPartialDateWidget = function() {
				return DATE_PARTIAL;
			};

			/**
			 * Get the text input element descendant of a date field.
			 * @function module:wc/ui/dateField.getTextBox
			 * @public
			 * @param {Element} element A dateField.
			 * @returns {Element} The input element of the dateField.
			 */
			this.getTextBox = function (element) {
				return Widget.findDescendant(element, DATE_INPUTS);
			};

			/**
			 * Get the value (in transfer format yyyy-mm-dd) from a date field component.
			 * @function  module:wc/ui/dateField.getValue
			 * @public
			 * @param {Element} element The date field we want to get the value from.
			 * @param {Boolean} [guess] If true then try a best guess at the transfer format when formatting it. For
			 *    more info see {@link module:wc/dom/dateField~reverseFormat}
			 * @returns {String} The date in transfer format or an empty string if the field has no value.
			 */
			this.getValue = function(element, guess) {
				var result, textbox;
				if (element && (element = DATE_FIELD.findAncestor(element))) {
					result = element.getAttribute(VALUE_ATTRIB);
					if (!result && (textbox = instance.getTextBox(element)) && textbox.value) {
						// we don't have a recorded xfer date for this element, check its value
						result = reverseFormat(textbox, guess);
					}
				}
				return result || "";
			};

			/**
			 * Get the date field widget.
			 * @function module:wc/ui/dateField.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget} the DateField Widget.
			 */
			this.getWidget = function() {
				return DATE_FIELD;
			};

			/**
			 * Initialise date fields by setting up handlers. We add the event handlers and then test for native
			 * controls because even browsers which provide native date field support need to be able to implement our
			 * custom partial date field.
			 * @function module:wc/ui/dateField.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
					event.add(element, event.TYPE.blur, blurEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
				event.add(element, event.TYPE.keydown, keydownEvent);
				formUpdateManager.subscribe(writeState);
				setUpDateFields();
			};

			/**
			 * Is a particular date field or input a native date input?
			 * @function module:wc/ui/dateField.isNativeInput
			 * @public
			 * @param {Element} el The element to test.
			 * @param {Boolean} [forceInput] Set true if we know we are calling with an input element to save a test.
			 * @returns {Boolean} True if el is a native date input (or the datefield wrapper of one).
			 */
			this.isNativeInput = function (el, forceInput) {
				var textBox;
				if (isNative) {
					if (forceInput) {
						textBox = el;
					}
					else {
						textBox = DATE_FIELD.isOneOfMe(el) ? instance.getTextBox(el) : el;
					}
					return DATE.isOneOfMe(textBox);
				}
				return false;
			};

			/**
			 * Indicates that the requested element is a dateField OR the textbox sub-component
			 * @function module:wc/ui/dateField.isOneOfMe
			 * @public
			 * @param {Element} element The DOM element to test
			 * @param {Boolean} [onlyContainer] Set true to test if the element is exactly the dateField, explicitly
			 *    false to test if only the input element.
			 * @returns {Boolean} true if the passed in element is a dateField or date input textbox sub-component of a
			 *    dateField
			 */
			this.isOneOfMe = function (element, onlyContainer) {
				var result;
				if (onlyContainer) {
					result = DATE_FIELD.isOneOfMe(element);
				}
				else if (onlyContainer === false) {
					result = Widget.isOneOfMe(element, DATE_INPUTS);
				}
				else {
					result = Widget.isOneOfMe(element, [DATE, DATE_PARTIAL, DATE_FIELD]);
				}
				return result;
			};

			/**
			 * Late initialisation to add other subscribers and set up the date fields for first use.
			 * @function module:wc/ui/dateField.postInit
			 * @public
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.ENABLE, shedSubscriber);
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.SHOW, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
				shed.subscribe(shed.actions.EXPAND, shedSubscriber);
				shed.subscribe(shed.actions.COLLAPSE, shedSubscriber);
				shed.subscribe(shed.actions.MANDATORY, shedSubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
				processResponse.subscribe(ajaxSetup);
			};

			/*
			 * public for testing
			 */
			/** @ignore */
			this._closeDateCombo = closeDateCombo;
			/** @ignore */
			this._getListBox = getSuggestionList;
			/** @ignore */
			this._filterOptions = filterOptions;
		}

		var /** @alias module:wc/ui/dateField */ instance = new DateInput();
		initialise.register(instance);
		return instance;
	});
