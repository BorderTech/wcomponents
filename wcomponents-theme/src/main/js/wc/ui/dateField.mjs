/**
 * Provides functionality for implementing a date input control. Allows for native support of full date input and
 * provides both a polyfill for date input and an implementation of a control which can accept and parse parts of a
 * date.
 *
 * @see {@link module:wc/ui/calendar} which is used as the date picker polyfill.
 *
 */
import unique from "wc/array/unique.mjs";
import Parser from "wc/date/Parser.mjs";
import interchange from "wc/date/interchange.mjs";
import Format from "wc/date/Format.mjs";
import cancelUpdate from "wc/ui/cancelUpdate.mjs";
import event from "wc/dom/event.mjs";
import focus from "wc/dom/focus.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import i18n from "wc/i18n/i18n.mjs";
import timers from "wc/timers.mjs";
import key from "wc/key.mjs";
import ajaxRegion from "wc/ui/ajaxRegion.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import onchangeSubmit from "wc/ui/onchangeSubmit.mjs";
import feedback from "wc/ui/feedback.mjs";
import listboxAnalog from "wc/ui/listboxAnalog.mjs";

const FIELD_CLASS = "wc-datefield",
	FAKE_VALUE_ATTRIB = "data-wc-value",
	DATE_FIELD = `div.${FIELD_CLASS}`,
	DATE_FIELD_PARTIAL = `${DATE_FIELD}.wc_datefield_partial`,
	DATE_WRAPPER_INCL_RO = `.${FIELD_CLASS}`,
	DATE_RO = "[data-wc-component='datefield']",
	INPUT = `${DATE_FIELD} > input`,
	DATE_WC = `${DATE_FIELD} > ${INPUT}[type='text']`,
	DATE_PARTIAL = `${DATE_FIELD_PARTIAL} > ${INPUT}[type='text']`,
	SUGGESTION_LIST = `${DATE_FIELD} > [role='listbox']`,
	optionVal = {},
	LAUNCHER = "button.wc_wdf_cal",
	startVal = {};

let dateFormatMask,  // loaded from i18n
	parsers,  // lazy init will store the Parser instances when first used
	formatter,  // lazy init on first use
	filterTimer,  // timeout handle
	openDateCombo = "";  // {string} the id of the currently open date field (if any)

const instance = {
	/**
	 * This allows another function to force the value of the given element to be parsed according to its parser
	 * and the first resulting match (if any) to be chosen.
	 * @function module:wc/ui/dateField.acceptFirstMatch
	 * @public
	 * @param {HTMLInputElement} element An input element, either full or partial date.
	 */
	acceptFirstMatch: function(element) {
		const matches = getMatches(element);
		const dateField = matches?.length ? this.get(element) : null;
		if (dateField) {
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
					 * Do not fire the change event if this update is occurring in a documentFragment.
					 * For example when processing an AJAX response ajaxSetup will eventually call this function to set up date fields
					 * before inserting the documentFragment into the DOM. We do not want change events fired in this scenario.
					 * It leads to potential infinite AJAX triggering if the date field is both a trigger and a target.
					 * See issue #1455 https://github.com/BorderTech/wcomponents/issues/1455
					 */
					if (document?.body.contains(element)) {
						timers.setTimeout(event.fire, 0, element, "change");
					}
				}
			}
		}
	},

	/**
	 * Get the selector which describes a calendar launch button. This is required by
	 * {@link module:wc/ui/calendar}.
	 * @function module:wc/ui/dateField.getLaunchWidget
	 * @public
	 * @returns {string} The description of the calendar picker launch button.
	 */
	getLaunchWidget: () => LAUNCHER,

	/**
	 * Get the partial date widget.
	 * @function module:wc/ui/dateField.getPartialDateWidget
	 * @public
	 * @returns {string} the Partial Date Widget.
	 */
	getPartialDateWidget: () => DATE_PARTIAL,

	/**
	 * Get the text input element descendant of a date field.
	 * @function module:wc/ui/dateField.getTextBox
	 * @public
	 * @param {Element} element A dateField.
	 * @returns {HTMLInputElement} The input element of the dateField.
	 */
	getTextBox: element => element.querySelector(INPUT),

	/**
	 * Get the value (in transfer format yyyy-mm-dd) from a date field component.
	 * @function  module:wc/ui/dateField.getValue
	 * @public
	 * @param {Element} element The date field we want to get the value from.
	 * @param {Boolean} [guess] If true then try a best guess at the transfer format when formatting it. For
	 *    more info see {@link module:wc/dom/dateField~reverseFormat}
	 * @returns {String} The date in transfer format or an empty string if the field has no value.
	 */
	getValue: function(element, guess) {
		let result;
		const _element = element ? this.get(element) : null;
		if (_element) {
			result = _element.getAttribute(FAKE_VALUE_ATTRIB);
			if (result) {
				return result;
			}
			const textbox = instance.getTextBox(_element);
			if (textbox?.value) {
				// we don't have a recorded xfer date for this element, check its value
				return reverseFormat(textbox, guess);
			}
		}
		return result || "";
	},

	/**
	 * Get the date field widget.
	 * @function module:wc/ui/dateField.getWidget
	 * @public
	 * @returns {string} the DateField Widget.
	 */
	getWidget: () => DATE_WRAPPER_INCL_RO,

	hasNativeInput: () => false,

	/**
	 * Is a particular field a native date input?
	 * Not so lame according to the feedback from users.
	 *
	 * @param {Element} dateField a date field container.
	 * @returns {Boolean}
	 */
	isLameDateField: dateField => !!dateField.querySelector(DATE_WC),

	/**
	 * Indicates that the requested element is a dateField OR the textbox subcomponent
	 * @function module:wc/ui/dateField.isOneOfMe
	 * @public
	 * @param {Element} element The DOM element to test
	 * @param {Boolean} [onlyContainer] Set `true` to test if the element is exactly the dateField, explicitly
	 *    `false` to test if only the input element.
	 * @returns {Boolean} true if the passed in element is a dateField or date input textbox subcomponent of a
	 *    dateField
	 */
	isOneOfMe: function (element, onlyContainer) {
		if (onlyContainer) {
			return element.matches(DATE_WRAPPER_INCL_RO);
		}
		if (onlyContainer === false) {
			return element.matches(INPUT);
		}
		return element.matches([INPUT, DATE_WRAPPER_INCL_RO].join());

	},

	isReadOnly: element => element.matches(DATE_RO),

	/**
	 * @param {Element} element
	 * @returns {HTMLElement}
	 */
	get: element => element.closest(DATE_FIELD),

	/*
	 * public for testing
	 */
	/** @ignore */
	_closeDateCombo: closeDateCombo,
	/** @ignore */
	_getListBox: getSuggestionList,
	/** @ignore */
	_filterOptions: filterOptions
};

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
 * @returns {HTMLElement} The SUGGESTION_LIST sub-component element of the dateField.
 */
function getSuggestionList(element, force) {

	if (force === -1 || (!force && element.matches(DATE_FIELD))) {
		return element.querySelector(SUGGESTION_LIST);
	}
	if (force === 1) {
		return element.closest(SUGGESTION_LIST);
	}
	const dateField = instance.get(element);
	if (dateField) {
		return dateField.querySelector(SUGGESTION_LIST);
	}
	return null;
}

/**
 * @param {Element} dateField
 * @returns {boolean}
 */
function isPartial(dateField) {
	return dateField.matches(DATE_FIELD_PARTIAL);
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
	if (!(element.querySelector(LAUNCHER))) {
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
		if (_df.matches(DATE_FIELD)) {
			instance.acceptFirstMatch(instance.getTextBox(_df));
		}
		shed.collapse(_df);
	}

	onchangeSubmit.clearIgnoreChange();
	ajaxRegion.clearIgnoreChange();
	if (openDateCombo) {
		// close any open dateFields when focusing elsewhere
		const otherDateField = document.getElementById(openDateCombo);
		if (otherDateField?.matches(DATE_FIELD) && (!dateField || dateField.id !== openDateCombo) && shed.isExpanded(otherDateField)) {
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
	const OPTION_WD = "[role='option']";
	function activateOption(option) {
		if (!shed.isSelected(option)) {
			listboxAnalog.activate(option);
		}
	}

	if (suggestionList?.querySelector(OPTION_WD)) {
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
 * @returns {String} A human-readable date as a string.
 */
function format(xfer) {
	const myFormatter = formatter || (formatter = new Format(dateFormatMask));
	return myFormatter.format(xfer);
}

/**
 * Indicates if an element a date field's input component.
 * @function
 * @private
 * @param {Element} element Any dom node.
 * @returns {Boolean} true if the element is a date field's input element.
 */
function isDateInput(element) {
	return element.matches(INPUT);
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

	if (element.matches(DATE_PARTIAL)) {
		return parsers.partial;
	}

	return parsers.standard;
}

/**
 * Get a list of potential date matches based on the user's input.
 * @function
 * @private
 * @param {HTMLInputElement} element The input element of the date field.
 * @param {String} [overrideVal] Use this as the value to match, instead of the element's value.
 * @returns {module:wc/date/Parser#parsedDate[]} Potential dates as strings.
 */
function getMatches(element, overrideVal) {
	// trim leading & trailing spaces
	const value = overrideVal || element.value,
		parser = getParser(element);

	let matches = parser.parse(value.trim());
	matches = unique(matches, (a, b) => {
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
 * @param {HTMLInputElement} element A dateField input element
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

	for (const element of matches) {
		lastVal = format(element.toXfer());
		element.html = lastVal;
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
		if (matches.length) {
			if (!shed.isExpanded(dateField)) {
				shed.expand(dateField);
			}
			showSuggestions(matches, dateField);
			return;
		}
		const suggestionList = getSuggestionList(dateField);
		if (suggestionList) {
			suggestionList.innerHTML = "";
			suggestionList.setAttribute("aria-busy", "true");
			if (shed.isExpanded(dateField)) {
				shed.collapse(dateField);
			}
		}
	}

	if (dateField.matches(DATE_FIELD)) {
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
 * @param {Element} field The date field wrapper element.
 */
function setInputValue(field) {
	const value = field.getAttribute(FAKE_VALUE_ATTRIB);
	const textVal = (value || field.getAttribute("datetime")) ? format(value) : "";
	if (textVal) {
		if (field.matches(DATE_RO)) {
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
 * @param {HTMLInputElement|DocumentFragment} [container] A HTML element which is itself or may contain date fields.
 */
function setUpDateFields(container) {
	const _container = container || document.body;
	let fields;

	if (container instanceof Element && container.matches(DATE_WRAPPER_INCL_RO)) {
		fields = [container];
	} else {
		fields = Array.from(_container.querySelectorAll(DATE_WRAPPER_INCL_RO));
	}

	fields.forEach(next => {
		if (next.matches(DATE_RO) || isPartial(next)) {
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
	if (element?.hasAttribute(FAKE_VALUE_ATTRIB) && getSuggestionList(element, 1) && (dateField = instance.get(element))) {
		setValueFromOption(dateField, element);
	}
}

/**
 * Subscriber to SHED pseudo-event publisher. This is used to set instance variables on EXPAND for later
 * processing such as determining if the field's value has changed on COLLAPSE; and for manipulating the
 * various subcomponents on ENABLE, DISABLE, MANDATORY, OPTIONAL, HIDE and SHOW.
 * @function
 * @private
 * @param {Element} element The element SHED has acted upon.
 * @param {String} action The SHED action.
 */
function shedSubscriber(element, action) {
	if (element) {
		if (element.matches(DATE_FIELD)) {
			if (action === shed.actions.EXPAND) {
				if (shed.isExpanded(element)) {
					openDateCombo = element.id;
					optionVal[(element.id)] = instance.getValue(element);
					filterOptions(element, 0);
				}
				return;
			}
			if (action === shed.actions.COLLAPSE) {
				if (!shed.isExpanded(element)) {
					optionVal[(element.id)] = null;
					openDateCombo = "";
					if (filterTimer) {
						timers.clearTimeout(filterTimer);
						filterTimer = null;
					}
				}
				return;
			}
			const textbox = instance.getTextBox(element);
			if (textbox) {
				const func = getFuncForAction(action);
				if (func) {
					let target;
					shed[func](textbox);  // publish this to make changes to the label
					if ((action === shed.actions.ENABLE || action === shed.actions.DISABLE) && (target = element.querySelector(LAUNCHER))) {
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
	const dateFields = Array.from(form.querySelectorAll(DATE_FIELD)), nameSuffix = "-date";
	for (const next of dateFields) {
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
 * @param {UIEvent & { currentTarget: HTMLInputElement }} $event the change event
 */
function changeEvent({ currentTarget }) {
	const dateField = instance.get(currentTarget);
	if (dateField) {
		instance.acceptFirstMatch(currentTarget);
		dateField.removeAttribute(FAKE_VALUE_ATTRIB);
	}
}

/**
 * Focus event handler closes any open dateField when ANYTHING is focused. This also needs to set up some
 * options for native date inputs, so we can successfully override some 'interesting' implementations of
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
 * @param {MouseEvent & { target: HTMLElement }} $event The click event
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
		focus.setFocusRequest(instance.getTextBox(dateField), () => shed.collapse(dateField));
		$event.preventDefault();
	}
}


function focusAndSetValue(element/* , option */) {
	const textbox = instance.getTextBox(element);
	// setValueFromOption(element, option);
	if (textbox) {
		focus.setFocusRequest(textbox, () => shed.collapse(element));
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
 * @param {KeyboardEvent & { target: HTMLElement, currentTarget: HTMLElement }} $event The keydown event.
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
		const suggestionList = shed.isExpanded(dateField) ? getSuggestionList(dateField, -1) : null;
		if (suggestionList) {
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

/**
 * Helper for keydownEvent.
 * @param {Element} element
 * @param {Element} target
 * @returns {boolean}
 */
function handleEscapeKey(element, target) {
	if (shed.isExpanded(element)) {
		// if we ESCAPE when on a SUGGESTION_LIST item focus the textbox
		const textbox = getSuggestionList(target, 1) ? instance.getTextBox(element) : null;
		if (textbox) {
			focus.setFocusRequest(textbox, () => shed.collapse(element));
		} else {
			shed.collapse(element);
		}
		return true; // so we don't close a dialog which contains the dropdown
	}
	return false;
}

initialise.register({
	/**
	 * Initialise date fields by setting up handlers. We add the event handlers and then test for native
	 * controls because even browsers which provide native date field support need to be able to implement our
	 * custom partial date field.
	 * @function module:wc/ui/dateField.initialise
	 * @public
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: element => {
		return i18n.translate("datefield_mask_format").then(mask => {
			dateFormatMask = mask;
			event.add(element, { type: "focus", listener: focusEvent, capture: true });
			event.add(element, "click", clickEvent);
			formUpdateManager.subscribe({ writeState });
			setUpDateFields();
		});
	},

	/**
	 * Late initialisation to add other subscribers and set up the date fields for first use.
	 * @function module:wc/ui/dateField.postInit
	 * @public
	 */
	postInit: () => {
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
	}
});

export default instance;
