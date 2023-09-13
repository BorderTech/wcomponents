import escapeRe from "wc/string/escapeRe.mjs";
import uid from "wc/dom/uid.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import event from "wc/dom/event.mjs";
import group from "wc/dom/group.mjs";
import i18n from "wc/i18n/i18n.mjs";
import timers from "wc/timers.mjs";
import wcconfig from "wc/config.mjs";
import debounce from "wc/debounce.mjs";

/**
 * Select an option in a select list by typing into it.
 */

const ns = uid();

let config = {
	textTrumpsValue: true,
	minLenSubstring: 3,
	minLenVal: 1,
	debounceDelay: 125  // making this too long can be counter-productive
};

const selectionChanged = debounce(function(element) {
		// programatically changing the select will not fire change so we gots to do it ourselves
		/*
		 * Note that we used to fire the change event only when the dropdown lost focus,
		 * in other words, as per any traditional change event.
		 * Nowadays this is not consistent with the native behaviour of many browsers (Chrome, IE11) which
		 * fire change as the selection changes. Only Firefox maintains the traditional behaviour.
		 * This inconsistency can lead to unexpected behaviour since the native typeahead will be firing changes
		 * while this typeahead will not. If there are side effects (e.g. an AJAX update is triggered) then the
		 * dropdown may be displaying a value that is not appropriate for the current state (only resolved when the user
		 * moves on in the form).
		 */
		event.fire(element, "change");
	}, 0),
	NO_ENDS_WITH_STRING_RE = /[^ ]$/,
	CLASS_NOT_FOUND = "wc_selsch_notfound",
	CLASS_FEEDBACK = "wc_selsch",
	regexCache = { starts: {}, contains: {} };
	/* NOTE: moved the initialisation of ALLOWED to initialise because
	 * parts of AJAX which i18n depend upon are not yet available in IE */
let ALLOWED,  // "abcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/? ",
	searchElementId;


const instance = {
	/**
	 * Find the index of the matching option in the select element. Does not need to be strictly equal.
	 * The option need not belong to the given select list, just the value and text will be matched.
	 *
	 * @param {HTMLOptionElement} option An object with properties: text, value
	 * @param {HTMLSelectElement|HTMLOptGroupElement|HTMLOptionElement[]} optContainer A dom element containing options (ie a select or an optgroup)
	 *    OR an array of options.
	 * @param {number} [startAt] Optionally an index to start searching at.
	 * @returns {number} The index of the option.
	 */
	indexOf: function (option, optContainer, startAt) {
		let result = -1;
		const options = (Array.isArray(optContainer)) ? optContainer : optContainer.querySelectorAll("option"),
			val = option.value,
			text = option.text,
			startIdx = (startAt && startAt > 0) ? startAt : 0;
		for (let i = startIdx; i < options.length; i++) {
			let next = options[i];
			if (next.value === val && next.text === text) {
				result = i;
				break;
			}
		}
		return result;
	}
};
/**
 * Determine if this element needs the typeahead, i.e. it is a dropdown.
 * @param {HTMLSelectElement|EventTarget} element An element that may potentially require typeahead.
 * @returns {HTMLSelectElement} The element that requires typeahead, or null.
 */
function needsSelectSearch(element) {
	let result = null;
	if (element instanceof HTMLSelectElement && element.matches("select:not([multiple])")) {
		result = element;
	}
	return result;
}

/**
 *
 * @param {FocusEvent} evt
 */
function focusEvent({ target }) {
	const element = needsSelectSearch(target);
	if (element) {
		initConfig();
		initSelect(element);
		closeSearch();
	}
}

/**
 * Handle a keydown event
 * @param {KeyboardEvent} evt
 */
function keydownEvent(evt) {
	const element = evt.target,
		keyCode = evt.code;
	let search, val;
	/*
	 * We handle some keys on keydown because:
	 * BACKSPACE and DELETE do not trigger keypress event
	 * SPACE does not trigger keypress in IE and Chrome when the activeElement is not
	 * a form control (for example a span has focus).
	 */
	if (keyCode === "Delete" || keyCode === "Backspace") {
		search = getSearchElement();
		val = search.textContent;
		search.textContent = val.substring(0, val.length - 1);
		val = search.textContent;
		if (!val.length) {
			hideSearch(search);
		}
		// we still queue a search so that we can return to a null/no value option if we backspace/delete to nothing
		highlightSearch(element, val);
		evt.preventDefault();
	} else if (keyCode === "Space") {
		search = getSearchElement();
		val = search.textContent;
		if (NO_ENDS_WITH_STRING_RE.test(val)) {
			/*
			 * There is no point allowing multiple spaces in HTML - in real browsers
			 * additional spaces will not display, however they will affect the results of
			 * the search. It would be confusing to the user when what they see as "Option 1"
			 * does not match "Option 1" because (invisible to them) it is really "Option  1".
			 */
			search.textContent = val + " ";
			highlightSearch(element, search.textContent);
		}
	} else if (keyCode === "Enter") {
		closeSearch();
	}
}

/*
 * NOTE: Chrome does not trigger ANY key events on a dropdown list (select) when the
 * list is OPEN (i.e. you focused it with the mouse). That means this WILL NOT WORK
 * in Chrome. Probably other webkit browsers affected too.
 */
/**
 *
 * @param {KeyboardEvent} evt
 */
function keypressEvent(evt) {
	// shiftkey lets user enter, for example <shift> + <5> to get
	// percent symbol, keyCode is 37, same as left arrow, so we need to
	// sniff shift key
	if (ALLOWED.search(escapeRe(evt.key.toLocaleLowerCase())) > -1) {
		const search = getSearchElement();
		const val = search.textContent += evt.key;
		if (val.length) {
			if (shed.isHidden(search, true)) {
				this.parentElement.insertBefore(search, this);
				shed.show(search);
			}
			highlightSearch(this, val);
		}
		/*
		 * If you preventDefault IE won't add its own typeahead behaviour, however
		 * it can work to leave IE's own behaviour alongside our own.
		 * This means when there is no match against our own algorithm IE will take
		 * over and match the Nth item starting with the character typed N times.
		 * Some users prefer this and will cry a lot if you take it out, they would
		 * rather type "C" 20 times than type "CZ".  That's because some legacy systems
		 * work this way.
		 */
		// evt.preventDefault();
	}
}

/**
 *
 * @param {HTMLSelectElement} element
 */
function initSelect(element) {
	if (!element[ns]) {
		element[ns] = true;
		event.add(element, "click", focusEvent);
		event.add(element, "blur", closeSearch);
		event.add(element, "keydown", keydownEvent);
		event.add(element, "keyup", keypressEvent);
	}
}


/**
 * @returns {HTMLElement} The little feedback box thingy that shows what you have typed so far
 * If none exist a new one is created, otherwise it is reused.
 */
function getSearchElement() {
	let search = (searchElementId) ? document.getElementById(searchElementId) : null;
	if (!search) {
		searchElementId = searchElementId || uid();
		search = document.createElement("span");
		search.className = CLASS_FEEDBACK;
		shed.hide(search);
		search.id = searchElementId;
		document.body.appendChild(search);
	}
	return search;
}


const highlightSearch = debounce(/**
	 * Searches for an option in a select element which matches the given string.
	 *
	 * @param {HTMLSelectElement} element The select element to search
	 * @param {string} search The string to search for
	 */
	function (element, search) {
		let match;
		if (search) {
			if (config.textTrumpsValue) {
				match = getMatchByText(element, search) || getMatchByValue(element, search);
			} else {
				match = getMatchByValue(element, search) || getMatchByText(element, search);
			}

			if (match) {
				getSearchElement().classList.remove(CLASS_NOT_FOUND);
				selectMatch(element, match);
			} else {
				getSearchElement().classList.add(CLASS_NOT_FOUND);
			}
		} else if (search === "") {
			// we have previously searched and have backspaced to an empty string
			if ((match = getMatchByValue(element, search)) && !shed.isSelected(match)) {
				selectMatch(element, match);
			}
		}
	}, config.debounceDelay);

/**
 * select the matching element or first option if empty match string
 * @param {HTMLSelectElement} element
 * @param {HTMLOptionElement} match
 */
function selectMatch(element, match) {
	timers.setTimeout(function() {
		element.selectedIndex = match?.index || 0;
		selectionChanged(element);
	}, 0);
}

/**
 * helper to get options from a listbox either as a select or as an aria listbox role
 * @param {HTMLSelectElement} element the element which is undergoing search
 * @returns {HTMLOptionElement[]} nodelist of options
 */
function getOptions(element) {
	// @ts-ignore
	return group.get(element);
}

/**
 * Search for first option with a matching 'text' property text match can be a partial
 * match (case-insensitive) if it is at least config.minLenSubstring characters long
 *
 * @param {HTMLSelectElement} element The select element to search
 * @param {string} search The string to search for
 * @returns {HTMLOptionElement} The matching option element if found
 */
function getMatchByText(element, search) {
	let containsRe, startsWithRe, result, length;

	if (regexCache.starts.hasOwnProperty(search)) {
		startsWithRe = regexCache.starts[search];
		console.log("Got regex from cache: ", startsWithRe.source);
	} else {
		startsWithRe = regexCache.starts[search] = new RegExp(`^${escapeRe(search)}`, "i");
	}

	if (regexCache.contains.hasOwnProperty(search)) {
		containsRe = regexCache.contains[search];
		console.log("Got regex from cache: ", containsRe.source);
	} else {
		containsRe = regexCache.contains[search] = new RegExp(`.+${escapeRe(search)}`, "i");
	}
	const options = getOptions(element);
	if (options.length) {
		length = options.length;
	}
	let i = 0, partialMatch;
	while (i < length) {
		let next = options[i];
		i++;
		let nextTxt = next.textContent;
		if (nextTxt && startsWithRe.test(nextTxt)) {
			result = next;
			break;
		} else if (!partialMatch && search.length >= config.minLenSubstring && containsRe.test(nextTxt)) {
			partialMatch = next;
		}
	}
	return result || partialMatch;
}

/**
 * Search for first option with a matching 'value' property value match must be an exact
 * match (case-insensitive) and must be at least config.minLenVal characters long
 *
 * @param {HTMLSelectElement} element The select element to search
 * @param {string} search The string to search for
 * @returns {HTMLOptionElement} The matching option element if found
 */
function getMatchByValue(element, search) {
	let result;
	// allow for reset to null option if search is ""
	if (search === "" || search.length >= config.minLenVal) {
		if (search !== "") {
			search = search.toLocaleLowerCase();
		}
		const options = getOptions(element);
		let i = 0;
		while (i < options.length) {
			let next = options[i];
			i++;
			let nextVal = next.getAttribute("value");
			if (nextVal || nextVal === "") {
				nextVal = nextVal.toLocaleLowerCase();
				if (nextVal === search) {
					result = next;
					break;
				}
			}
		}
	}
	return result;
}

/**
 * "Close" the little box thingy that shows what you have typed so far
 */
function closeSearch() {
	const search = getSearchElement();
	search.textContent = "";
	if (!shed.isHidden(search, true)) {
		hideSearch(search);
	}
}

/**
 * @param {Element} search
 */
function hideSearch(search) {
	search.classList.remove(CLASS_NOT_FOUND);
	shed.hide(search);
}

/**
 * Initialises the module configuration options.
 * This should be called as late as possible to give the application developer time to register any configuration overrides.
 * Call it comme ça
	require(["wc/config"], function(wcconfig){
		wcconfig.set({
			textTrumpsValue: true,  // if true text matches are given priority over value matches (i.e. the hidden value of the option instead of the visible text)
			minLenSubstring: 3,  // minimum length of input string to search on for partial matches (i.e. not "starts with" matches)
			minLenVal: 1,  // minimum length of input string to search on for value matches
			debounceDelay: 250  //  typing debounce delay in milliseconds
	}, "wc/ui/selectboxSearch")});
 */
function initConfig() {
	if (config.inited) {
		return;
	}
	try {
		config = wcconfig.get("wc/ui/selectboxSearch", config);
	} finally {
		config.inited = true;
	}
}

initialise.register({
	/**
	 * Set up select element search functionality.
	 *
	 * @param {HTMLBodyElement} element the element being initialised, usually document.body
	 */
	initialise: function(element) {
		i18n.translate("select_typeahead").then((val) => {
			ALLOWED = val;
			event.add(element, { type: "focus", listener: focusEvent, capture: true });
		});
	}
});

export default instance;
