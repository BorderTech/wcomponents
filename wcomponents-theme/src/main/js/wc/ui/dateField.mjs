/**
 * Provides functionality for implementing a date input control. Allows for native support of full date input and
 * provides both a polyfill for date input and an implementation of a control which can accept and parse parts of a
 * date.
 *
 * @see {@link module:wc/ui/calendar} which is used as the date picker polyfill.
 *
 */
import unique from "wc/array/unique";
import Parser from "wc/date/Parser";
import interchange from "wc/date/interchange";
import Format from "wc/date/Format";
import cancelUpdate from "wc/ui/cancelUpdate";
import event from "wc/dom/event";
import focus from "wc/dom/focus";
import formUpdateManager from "wc/dom/formUpdateManager";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import Widget from "wc/dom/Widget";
import i18n from "wc/i18n/i18n";
import timers from "wc/timers";
import key from "wc/key";
import ajaxRegion from "wc/ui/ajaxRegion";
import processResponse from "wc/ui/ajax/processResponse";
import onchangeSubmit from "wc/ui/onchangeSubmit";
import feedback from "wc/ui/feedback";
import listboxAnalog from "wc/ui/listboxAnalog";

const FIELD_CLASS = "wc-datefield",
	FAKE_VALUE_ATTRIB = "data-wc-value",
	instance = new DateInput();
export default initialise.register(instance);


/**
 * @constructor
 * @alias module:wc/ui/dateField~DateInput
 * @private
 */
function DateInput() {
	let parsers,  // lazy init will store the Parser instances when first used
		formatter,  // lazy init on first use
		filterTimer,  // timeout handle
		openDateCombo = "";  // {string} the id of the currently open date field (if any)

	const DATE_FIELD = new Widget("div", FIELD_CLASS),
		DATE_FIELD_PARTIAL = DATE_FIELD.extend("wc_datefield_partial"),
		DATE_WRAPPER_INCL_RO = new Widget("", FIELD_CLASS),
		DATE_RO = new Widget("", "", {"data-wc-component": "datefield"}),
		INPUT = new Widget("input"),
		DATE_WC = INPUT.extend("", {"type": "text"}),
		DATE_PARTIAL = DATE_WC.clone(),
		SUGGESTION_LIST = new Widget("", "", {"role": "listbox"}),
		optionVal = {},
		LAUNCHER = new Widget("button", "wc_wdf_cal"),
		startVal = {};

	INPUT.descendFrom(DATE_FIELD, true);
	DATE_WC.descendFrom(DATE_FIELD, true);
	DATE_PARTIAL.descendFrom(DATE_FIELD_PARTIAL, true);
	SUGGESTION_LIST.descendFrom(DATE_FIELD, true);

	/**
	 * Get the SUGGESTION_LIST part of a dateField.
	 * @function
	 * @private
	 * @param {Element} element A dateField or an option in the list.
	 * @param {Number} [force] Use a specific direction rather than doing a component lookup:
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

		if (force === -1 || (!force && DATE_FIELD.isOneOfMe(element))) {
			return SUGGESTION_LIST.findDescendant(element);
		}
		if (force === 1) {
			return SUGGESTION_LIST.findAncestor(element);
		}
		const dateField = instance.get(element);
		if (dateField) {
			return SUGGESTION_LIST.findDescendant(dateField);
		}
		return null;
	}

	function isPartial(dateField) {
		return DATE_FIELD_PARTIAL.isOneOfMe(dateField);
	}

	/**
	 * Polyfill for input type date.
	 * @param {Element} element the WDateField wrapper.
	 */
	function initWcDateField(element) {
		const childEl = instance.getTextBox(element), id = element.id;

		if (!childEl) {
			return;
		}

		let value = element.getAttribute(FAKE_VALUE_ATTRIB);
		if (value) {
			if (document.activeElement === childEl) {
				startVal[id] = value;
				onchangeSubmit.ignoreNextChange();
				ajaxRegion.ignoreNextChange();
			}
		}

		const diagnostic = feedback.getLast(element),
			BEFORE_BEGIN = "beforebegin",
			BEFORE_END = "beforeend";
		// Add the calendar launch button.
		if (!(LAUNCHER.findDescendant(element))) {
			let launcherHtml = "<button value='" + id + "_input' tabindex='-1' id='" + id +
					"_cal' type='button' aria-hidden='true' class='wc_wdf_cal wc-invite' aria-haspopup='true'";
			if (shed.isDisabled(childEl)) {
				launcherHtml += " disabled='disabled'";
			}
			launcherHtml += "><i class='fa fa-calendar' aria-hidden='true'></i></button>";
			if (diagnostic) {
				diagnostic.insertAdjacentHTML(BEFORE_BEGIN, launcherHtml);
			} else {
				element.insertAdjacentHTML(BEFORE_END, launcherHtml);
			}
		}
		// Then add the suggestion list holder to make the combobox role valid.
		if (!(getSuggestionList(element, -1))) {
			let listHTML = "<span role='listbox' aria-busy='true'></span>";
			if (diagnostic) {
				diagnostic.insertAdjacentHTML(BEFORE_BEGIN, listHTML);
			} else {
				element.insertAdjacentHTML(BEFORE_END, listHTML);
			}
		}

		element.setAttribute("role", "combobox");
		element.setAttribute("aria-expanded", "false");
		element.setAttribute("aria-autocomplete", "list");
		childEl.setAttribute("autocomplete", "off");
		childEl.setAttribute("aria-owns", id + "_cal");

		// set the value of the input control.
		value = childEl.value;
		if (value) {
			instance.acceptFirstMatch(childEl);
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
		const dateField = instance.get(element);

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
			shed.collapse(_df);
		}

		onchangeSubmit.clearIgnoreChange();
		ajaxRegion.clearIgnoreChange();
		if (openDateCombo) {
			// close any open dateFields when focusing elsewhere
			const otherDateField = document.getElementById(openDateCombo);
			if (otherDateField && DATE_FIELD.isOneOfMe(otherDateField) && (!dateField || dateField.id !== openDateCombo) && shed.isExpanded(otherDateField)) {
				_collapseHelper(otherDateField);
			}
		}
		if (dateField && isDateInput(element) && shed.isExpanded(dateField)) {
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
		const OPTION_WD = new Widget("", "", {"role": "option"});
		function activateOption(option) {
			if (!shed.isSelected(option)) {
				listboxAnalog.activate(option);
			}
		}

		if (suggestionList && OPTION_WD.findDescendant(suggestionList)) {
			onchangeSubmit.ignoreNextChange();
			ajaxRegion.ignoreNextChange();

			if (filterTimer) {
				timers.clearTimeout(filterTimer);
			}
			focus.focusFirstTabstop(suggestionList, activateOption);
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
		const myFormatter = formatter || (formatter = new Format(i18n.get("datefield_mask_format")));
		return myFormatter.format(xfer);
	}

	/*
	 * Indicates if an element a date field's input component.
	 * @function
	 * @private
	 * @param {Element} element Any dom node.
	 * @returns {Boolean} true if the element is a date field's input element.
	 */
	function isDateInput(element) {
		return INPUT.isOneOfMe(element);
	}

	/**
	 * Update the value of the dateField based on interaction with an option. NOTE: native combos in HTML5 do
	 * not update on select of the options!
	 * @function
	 * @private
	 * @param {HTMLElement} dateField The dateField to update.
	 * @param {HTMLElement} option The option which caused the update.
	 */
	function setValueFromOption(dateField, option) {
		const suggestionList = getSuggestionList(dateField, -1);

		if (suggestionList) {
			let value = option.hasAttribute(FAKE_VALUE_ATTRIB) ? option.getAttribute(FAKE_VALUE_ATTRIB) : option.textContent;

			if (value && interchange.isValid(value)) {
				value = format(value);
			}
			let textbox = instance.getTextBox(dateField);
			if (textbox) {
				textbox.value = value;  // do not fire change event here: do it on collapse
			}

			if (optionVal[(dateField.id)] !== instance.getValue(dateField, true)) {
				timers.setTimeout(event.fire, 0, instance.getTextBox(dateField), "change");
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
		let result = false;
		const s1 = valA.trim(), s2 = valB.trim();
		if (s1 === s2 || s1.toLocaleLowerCase() === s2.toLocaleLowerCase()) {
			result = true;
		}
		return result;
	}

	/**
	 * Takes an array of strings and builds them into HTML.
	 * @function
	 * @private
	 * @param suggestions The date suggestions.
	 * @returns {String} The suggestion elements as a single string.
	 */
	function getSuggestions(suggestions) {
		const baseAttrs = "role='option' class='wc-invite'";
		const html = [];
		for (let i = 0; i < suggestions.length; i++) {
			let tabIndex = i === 0 ? "0" : "-1";
			let {
				attributes = "",
				html: h
			} = suggestions[i];
			html.push(`<span ${baseAttrs} ${attributes} tabindex='${tabIndex}' ${FAKE_VALUE_ATTRIB}='${h}'>${h}</span>`);
		}
		return html.join("");
	}

	/**
	 * Initialises the "parsers" instance variable.
	 * @function
	 * @private
	 */
	function initParsers() {
		const shortcuts = ["ytm", "+-"],
			standardMasks = shortcuts.concat(i18n.get("datefield_masks_full").split(",")),
			partialMasks = standardMasks.concat(i18n.get("datefield_masks_partial").split(","));

		/*
		 * Creates a new instance of a Parser
		 */
		function createParser(masks, expandYearIntoPast, rolling) {
			const result = new Parser();
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
		if (!parsers) {
			initParsers();
		}

		if (DATE_PARTIAL.isOneOfMe(element)) {
			return parsers.partial;
		}

		return parsers.standard;
	}

	/**
	 * Get a list of potential date matches based on the user's input.
	 * @function
	 * @private
	 * @param {Element} element The input element of the date field.
	 * @param {String} [overrideVal] Use this as the value to match, instead of the element's value.
	 * @returns {module:wc/date/Parser#parsedDate[]} Potential dates as strings.
	 */
	function getMatches(element, overrideVal) {
		// trim leading & trailing spaces
		const value = overrideVal || element.value,
			parser = getParser(element);

		let matches = parser.parse(value.trim());
		matches = unique(matches, function(a, b) {
			let result = 1;
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
		let result, matches;
		const currentValue = element.value.trim();

		if (currentValue) {
			matches = getMatches(element);
			const len = matches.length;
			for (let i = 0; i < len; i++) {
				const next = matches[i];
				const value = format(next.toXfer());
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
	 * @param matches The content for the suggestions.
	 * @param {Element} dateField The date field to which the matches belong.
	 */
	function showSuggestions(matches, dateField) {
		let lastVal = "", suggestionList;

		for (let i = 0; i < matches.length; i++) {
			lastVal = format(matches[i].toXfer());
			matches[i].html = lastVal;
		}

		if (matches.length && (suggestionList = getSuggestionList(dateField))) {
			suggestionList.setAttribute("aria-busy", "true");
			suggestionList.innerHTML = "";
			if (!(matches.length === 1 && formattedDatesSame(lastVal, instance.getTextBox(dateField).value))) {
				suggestionList.innerHTML = getSuggestions(matches);
				suggestionList.removeAttribute("aria-busy");
				if (!shed.isExpanded(dateField)) {
					shed.expand(dateField);
				}
			} else if (shed.isExpanded(dateField)) {
				shed.collapse(dateField);
			}
		}
	}

	/**
	 * Get any matches for the input element content and pass them through to the SUGGESTION_LIST sub-component
	 * to provide an on-the-fly filtered list of potential matches.
	 * @function
	 * @private
	 * @param {Element} dateField a date field
	 * @param {Number} [delay] a timeout delay, default to 250 if not set. Set explicitly to 0 to have no delay.
	 */
	function filterOptions(dateField, delay) {
		let _delay = delay;

		function _filter() {
			const textbox = instance.getTextBox(dateField),
				matches = getMatches(textbox);
			let suggestionList;
			if (matches.length) {
				if (!shed.isExpanded(dateField)) {
					shed.expand(dateField);
				}
				showSuggestions(matches, dateField);
			} else if ((suggestionList = getSuggestionList(dateField))) {
				suggestionList.innerHTML = "";
				suggestionList.setAttribute("aria-busy", "true");
				if (shed.isExpanded(dateField)) {
					shed.collapse(dateField);
				}
			}
		}

		if (DATE_FIELD.isOneOfMe(dateField)) {
			if (filterTimer) {
				timers.clearTimeout(filterTimer);
			}
			if (delay === 0) {
				_filter();
			} else {
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
	 * @param {HTMLElement} field The date field wrapper element.
	 */
	function setInputValue(field) {
		const value = field.getAttribute(FAKE_VALUE_ATTRIB);
		let textVal;
		if ((value || field.getAttribute("datetime")) && (textVal = format(value))) {
			if (DATE_RO.isOneOfMe(field)) {
				field.textContent = textVal;
			} else {
				const textBox = instance.getTextBox(field);
				textBox.value = textVal;
			}
		}
	}

	/**
	 * Undertake setup on any date fields. This involves setting the initial readable value and removing
	 * unnecessary artifacts if the input is a native date input.
	 * @function
	 * @private
	 * @param {Element|DocumentFragment} [container] A HTML element which is itself or may contain date fields.
	 */
	function setUpDateFields(container) {
		const _container = container || document.body;
		let fields;

		if (container && DATE_WRAPPER_INCL_RO.isOneOfMe(container)) {
			fields = [container];
		} else {
			fields = DATE_WRAPPER_INCL_RO.findDescendants(_container);
		}

		Array.prototype.forEach.call(fields, function(next) {
			if (DATE_RO.isOneOfMe(next) || isPartial(next)) {
				setInputValue(next);
			} else if (instance.isLameDateField(next)) {
				initWcDateField(next);
			} else { // native date inputs
				next.removeAttribute(FAKE_VALUE_ATTRIB);
			}
		});

		cancelUpdate.resetAllFormState();
	}

	/**
	 * Modify fields in an AJAX response before insertion into the DOM.
	 * @function
	 * @private
	 * @param {Element} _element The Element being replaced/filled. Not used.
	 * @param {DocumentFragment} documentFragment The document fragment from the AJAX response.
	 */
	function ajaxSetup(_element, documentFragment) {
		setUpDateFields(documentFragment);
	}

	/**
	 * Update the text box when an option is selected.
	 *
	 * @function
	 * @private
	 * @param {Element} element The element being selected.
	 */
	function shedSelectSubscriber(element) {
		let dateField;
		if (element && element.hasAttribute(FAKE_VALUE_ATTRIB) && getSuggestionList(element, 1) && (dateField = instance.get(element))) {
			setValueFromOption(dateField, element);
		}
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
		if (element) {
			let textbox;
			if (DATE_FIELD.isOneOfMe(element)) {
				if (action === shed.actions.EXPAND) {
					if (shed.isExpanded(element)) {
						openDateCombo = element.id;
						optionVal[(element.id)] = instance.getValue(element);
						filterOptions(element, 0);
					}
				} else if (action === shed.actions.COLLAPSE) {
					if (!shed.isExpanded(element)) {
						optionVal[(element.id)] = null;
						openDateCombo = "";
						if (filterTimer) {
							timers.clearTimeout(filterTimer);
							filterTimer = null;
						}
					}
				} else if ((textbox = instance.getTextBox(element))) {
					const func = getFuncForAction(action);
					if (func) {
						let target;
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
		let func;
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
		const dateFields = DATE_FIELD.findDescendants(form), nameSuffix = "-date";
		for (let i = 0; i < dateFields.length; i++) {
			const next = dateFields[i];
			const name = next.id + nameSuffix;
			if (!shed.isDisabled(next)) {
				let numVal = instance.getValue(next);
				if (numVal) {
					formUpdateManager.writeStateField(stateContainer, name, numVal);
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
		const element = $event.target;
		const dateField = instance.get(element);
		if (dateField) {
			instance.acceptFirstMatch(element);
			dateField.removeAttribute(FAKE_VALUE_ATTRIB);
		}
	}

	/**
	 * Focus event handler closes any open dateField when ANYTHING is focused. This also needs to set up some
	 * options for native date inputs so we can successfully override some 'interesting' implementations of
	 * change event publication.
	 * @function
	 * @private
	 * @param {FocusEvent & { target: HTMLElement }} $event The focus/focusin event.
	 */
	function focusEvent($event) {
		const BOOTSTRAPPED = "wc.ui.dateField_bootstrapped";
		const element = $event.target;

		if ($event.defaultPrevented) {
			return;
		}
		if (isDateInput(element) && !element[BOOTSTRAPPED]) {
			element[BOOTSTRAPPED] = true;
			event.add(element, "change", changeEvent);
		}

		let dateField = instance.get(element);
		if (dateField && !dateField[BOOTSTRAPPED]) {
			dateField[BOOTSTRAPPED] = true;
			event.add(dateField, "keydown", keydownEvent);
		}
		closeDateCombo(element);
	}

	/**
	 * Click event handler for the suggestion list options. Set the date field value according to the option
	 * clicked.
	 * @function
	 * @private
	 * @param {Event} $event The click event as published by the wc event manager
	 */
	function clickEvent($event) {
		const target = $event.target;
		if ($event.defaultPrevented) {
			return;
		}
		const dateField = instance.get(target);
		if (dateField && !shed.isDisabled(dateField) && getSuggestionList(target, 1)) {
			// update on option click
			// setValueFromOption(dateField, target);  // yes, revert to $event.target here: we want the option not the SUGGESTION_LIST
			focus.setFocusRequest(instance.getTextBox(dateField), function() {
				shed.collapse(dateField);
			});
			$event.preventDefault();
		}
	}


	function focusAndSetValue(element/* , option */) {
		const textbox = instance.getTextBox(element);
		// setValueFromOption(element, option);
		if (textbox) {
			focus.setFocusRequest(textbox, function() {
				shed.collapse(element);
			});
		} else {
			shed.collapse(element);
		}
	}

	/**
	 * Keydown event handler. Used to set values and close the suggestion list. Much of this involves trying to
	 * capture closing the suggestion list or leaving the component completely and manipulating the input based
	 * on the selected (or first) option in the suggestion list.
	 * @function
	 * @private
	 * @param {KeyboardEvent} $event The keydown event.
	 */
	function keydownEvent($event) {
		const dateField = $event.currentTarget,
			keyCode = $event.key,
			target = $event.target;

		// dateField = instance.get(target);

		if (!dateField || shed.isDisabled(dateField)) {
			return;
		}

		if (keyCode === "Escape") {
			if (handleEscapeKey(dateField, target)) {
				$event.preventDefault();
			}
			return;
		}

		if (keyCode === "Enter") {
			if (handleEnterKey(dateField, target)) {
				$event.preventDefault();
			}
			return;
		}

		if ((keyCode === "ArrowDown" || keyCode === "ArrowUp") && !(getSuggestionList(target, 1))) {
			let suggestionList;
			if (shed.isExpanded(dateField) && (suggestionList = getSuggestionList(dateField, -1))) {
				focusListbox(suggestionList);
				$event.preventDefault(); // so we don't cause a line scroll
			}
			return;
		}

		//			if (keyCode === KeyEvent.DOM_VK_SPACE && target.hasAttribute(FAKE_VALUE_ATTRIB) && getSuggestionList(target, 1)) {
		//				// SPACE on an option should update the dateField
		//				focusAndSetValue(dateField, target);
		//				$event.preventDefault(); // so we don't cause a page scroll
		//				return;
		//			}

		if (keyCode === "Tab") {
			handleTabKey(dateField, target);
			return;
		}

		if (!key.isMeta(keyCode) && isDateInput(target)) {
			dateField.removeAttribute(FAKE_VALUE_ATTRIB);
			filterOptions(dateField);
		}
	}

	/*
	 * Helper for keydownEvent.
	 */
	function handleEnterKey(element, target) {
		let preventDefault;
		if (shed.isExpanded(element)) {
			if (target.hasAttribute(FAKE_VALUE_ATTRIB) && getSuggestionList(target, 1)) {
				preventDefault = true;  // so we don't submit from the suggestion list - yes this is needed I checked.
				focusAndSetValue(element /* , target */);
			} else if (isDateInput(target)) {
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
			} /* else if (target.hasAttribute(FAKE_VALUE_ATTRIB) && getSuggestionList(target, 1)) {
				// tab from an option should update the dateField
				setValueFromOption(element, target);
				shed.collapse(element);
			} */
		}
	}

	/*
	 * Helper for keydownEvent.
	 */
	function handleEscapeKey(element, target) {
		if (shed.isExpanded(element)) {
			let textbox;
			// if we ESCAPE when on a SUGGESTION_LIST item focus the textbox
			if (getSuggestionList(target, 1) && (textbox = instance.getTextBox(element))) {
				focus.setFocusRequest(textbox, function() {
					shed.collapse(element);
				});
			} else {
				shed.collapse(element);
			}
			return true; // so we don't close a dialog which contains the dropdown
		}
		return false;
	}

	/**
	 * This allows another function to force the value of the given element to be parsed according to its parser
	 * and the first resulting match (if any) to be chosen.
	 * @function module:wc/ui/dateField.acceptFirstMatch
	 * @public
	 * @param {Element} element An input element, either full or partial date.
	 */
	this.acceptFirstMatch = function(element) {
		let dateField;
		const matches = getMatches(element);
		if (matches && matches.length && (dateField = this.get(element))) {
			const _matches = matches.map(function(next) {
				return format(next.toXfer());
			});
			if (!(~_matches.indexOf(element.value))) {
				// if the element value is an exact match it means one of the potential matches is exactly the same as the current value
				dateField.removeAttribute(FAKE_VALUE_ATTRIB);
				const _value = format(matches[0].toXfer());
				if (_value !== element.value) {
					element.value = _value;
					/*
					 * Do not fire the change event if this update is occuring in a documentFragment.
					 * For example when processing an AJAX response ajaxSetup will eventually call this function to set up date fields
					 * before inserting the documentFragment into the DOM. We do not want change events fired in this scenario.
					 * It leads to potential infinite AJAX triggering if the date field is both a trigger and a target.
					 * See issue #1455 https://github.com/BorderTech/wcomponents/issues/1455
					 */
					if (document.body && document.body.contains(element)) {
						timers.setTimeout(event.fire, 0, element, "change");
					}
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
		return INPUT.findDescendant(element);
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
		let result, _element;
		if (element && (_element = this.get(element))) {
			let textbox;
			result = _element.getAttribute(FAKE_VALUE_ATTRIB);
			if (result) {
				return result;
			}

			if (!result && (textbox = instance.getTextBox(_element)) && textbox.value) {
				// we don't have a recorded xfer date for this element, check its value
				return reverseFormat(textbox, guess);
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
		return DATE_WRAPPER_INCL_RO;
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
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "click", clickEvent);
		formUpdateManager.subscribe(writeState);
		setUpDateFields();
	};

	this.hasNativeInput = function () {
		return false;
	};

	/**
	 * Is a particular field a native date input?
	 * Not so lame according to the feedback from users.
	 *
	 * @param {HTMLElement} dateField a date field container.
	 * @returns {Boolean}
	 */
	this.isLameDateField = function(dateField) {
		return !!DATE_WC.findDescendant(dateField);
	};

	/**
	 * Indicates that the requested element is a dateField OR the textbox sub-component
	 * @function module:wc/ui/dateField.isOneOfMe
	 * @public
	 * @param {Element} element The DOM element to test
	 * @param {Boolean} [onlyContainer] Set `true` to test if the element is exactly the dateField, explicitly
	 *    `false` to test if only the input element.
	 * @returns {Boolean} true if the passed in element is a dateField or date input textbox sub-component of a
	 *    dateField
	 */
	this.isOneOfMe = function (element, onlyContainer) {
		let result;
		if (onlyContainer) {
			result = DATE_WRAPPER_INCL_RO.isOneOfMe(element);
		} else if (onlyContainer === false) {
			result = INPUT.isOneOfMe(element);
		} else {
			result = Widget.isOneOfMe(element, [INPUT, DATE_WRAPPER_INCL_RO]);
		}
		return result;
	};

	this.isReadOnly = function (element) {
		return DATE_RO.isOneOfMe(element);
	};

	/**
	 * Late initialisation to add other subscribers and set up the date fields for first use.
	 * @function module:wc/ui/dateField.postInit
	 * @public
	 */
	this.postInit = function() {
		shed.subscribe(shed.actions.SELECT, shedSelectSubscriber);
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

	this.get = function(element) {
		return DATE_FIELD.findAncestor(element);
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
