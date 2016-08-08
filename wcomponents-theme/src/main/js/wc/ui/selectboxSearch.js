/**
 * Select an option in a select list by typing into it.
 *
 * @module
 * @requires module:wc/string/escapeRe
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/event
 * @requires module:wc/dom/group
 * @requires module:wc/i18n/i18n
 * @requires module:wc/timers
 * @requires module:wc/dom/textContent
 *
 * @todo Document private members, fix source order.
 */
define(["wc/string/escapeRe",
	"wc/dom/tag",
	"wc/dom/uid",
	"wc/dom/classList",
	"wc/dom/initialise",
	"wc/dom/attribute",
	"wc/dom/shed",
	"wc/dom/event",
	"wc/dom/group",
	"wc/i18n/i18n",
	"wc/timers",
	"wc/dom/textContent"],
	/** @param escapeRe wc/string/escapeRe @param tag wc/dom/tag @param uid wc/dom/uid @param classList wc/dom/classList @param initialise wc/dom/initialise @param attribute wc/dom/attribute @param shed wc/dom/shed @param event wc/dom/event @param group wc/dom/group @param i18n wc/i18n/i18n @param timers wc/timers @param textContent wc/dom/textContent @ignore */
	function(escapeRe, tag, uid, classList, initialise, attribute, shed, event, group, i18n, timers, textContent) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/selectboxSearch~SelectboxSearch
		 * @private
		 */
		function SelectboxSearch() {
			var fireOnchange = false,
				searchTimer,
				MIN_LEN_SUBSTRING_MATCH = 3,
				MIN_LEN_VAL_MATCH = 1,
				TEXT_TRUMPS_VALUE = true,
				NO_ENDS_WITH_STRING_RE = /[^ ]$/,
				SEARCH_DELAY = 250,
				/* NOTE: moved the initialisation of ALLOWED to initialise because
				 * parts of AJAX which i18n depend upon are not yet available in IE */
				ALLOWED,  // "abcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/? ",
				CLASS_NOT_FOUND = "wc_selsch_notfound",
				CLASS_FEEDBACK = "wc_selsch",
				searchElementId,
				regexCache = {starts: {}, contains: {}};


			function needsSelectSearch(element) {
				var result = null;
				if (element.tagName === tag.SELECT && !element.multiple) {
					result = element;
				}
				return result;
			}

			function focusEvent(evt) {
				var element = evt.target;
				if (needsSelectSearch(element)) {
					initSelect(element);
					closeSearch(element);
				}
			}

			function blurEvent(evt) {
				closeSearch(evt.currentTarget);
			}

			function keydownEvent(evt) {
				var element = evt.target,
					keyCode = evt.keyCode || evt.which,
					search,
					val;
				/*
				 * We handle some keys on keydown because:
				 * BACKSPACE and DELETE do not trigger keypress event
				 * SPACE does not trigger keypress in IE and Chrome when the activeElement is not
				 * a form control (for example a span has focus).
				 */
				if (keyCode === KeyEvent.DOM_VK_DELETE || keyCode === KeyEvent.DOM_VK_BACK_SPACE) {
					search = getSearchElement();
					val = textContent.get(search);
					textContent.set(search, val.substr(0, val.length - 1));
					val = textContent.get(search);
					if (!val.length) {
						hideSearch(search);
					}
					// we still queue a search so that we can return to a null/no value option if we backspace/delete to nothing
					queueSearch(element, val);
					evt.preventDefault();
				}
				else if (keyCode === KeyEvent.DOM_VK_SPACE) {
					search = getSearchElement();
					val = textContent.get(search);
					if (NO_ENDS_WITH_STRING_RE.test(val)) {
						/*
						 * There is no point allowing multiple spaces in HTML - in real browsers
						 * additional spaces will not display, however they will affect the results of
						 * the search. It would be confusing to the user when what they see as "Option 1"
						 * does not match "Option 1" because (invisible to them) it is really "Option  1".
						 */
						textContent.set(search, val + " ");
						queueSearch(element, textContent.get(search));
					}
				}
				else if (keyCode === KeyEvent.DOM_VK_RETURN) {
					closeSearch(element);
				}
			}

			/*
			 * NOTE: Chrome does not trigger ANY key events on a dropdown list (select) when the
			 * list is OPEN (i.e. you focused it with the mouse). That means this WILL NOT WORK
			 * in Chrome. Probably other webkit browsers affected too.
			 */
			function keypressEvent(evt) {
				var element = evt.target,
					keyCode = evt.keyCode || evt.which,
					character,
					search,
					val;
				// shiftkey lets user enter, for example <shift> + <5> to get
				// percent symbol, keyCode is 37, same as left arrow, so we need to
				// sniff shift key
				if (keyCode > KeyEvent.DOM_VK_DOWN || evt.shiftKey) {
					character = String.fromCharCode(keyCode);
					if (ALLOWED.search(escapeRe(character.toLocaleLowerCase())) > -1) {
						search = getSearchElement();
						val = textContent.get(search);
						textContent.set(search, val + character);
						val = textContent.get(search);
						if (val.length) {
							if (shed.isHidden(search, true)) {
								element.parentNode.insertBefore(search, element);
								shed.show(search);
							}
							queueSearch(element, val);
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
			}

			function initSelect(element) {
				var inited = attribute.get(element, ns);
				if (!inited) {
					attribute.set(element, ns, true);
					event.add(element, event.TYPE.click, focusEvent);
					event.add(element, event.TYPE.blur, blurEvent);
					event.add(element, event.TYPE.keydown, keydownEvent);
					event.add(element, event.TYPE.keypress, keypressEvent);
				}
			}


			/*
			 * @returns The little feedback box thingy that shows what you have typed so far
			 * If no already exist a new one is created, otherwise it is reused.
			 */
			function getSearchElement() {
				var search = (searchElementId) ? document.getElementById(searchElementId) : null;
				if (!search) {
					searchElementId = searchElementId || uid();
					search = document.createElement(tag.SPAN);
					search.className = CLASS_FEEDBACK;
					shed.hide(search);
					search.id = searchElementId;
					document.body.appendChild(search);
				}
				return search;
			}

			/*
			 * Searches for an option in a select element which matches the given string.
			 *
			 * @param element The select element to search
			 * @param search The string to search for
			 */
			function queueSearch(element, search) {
				if (searchTimer) {
					timers.clearTimeout(searchTimer);
				}
				searchTimer = timers.setTimeout(highlightSearch, SEARCH_DELAY, element, search);
			}

			/*
			 * Don't call directly, use queueSearch instead.
			 * @param element The select element to search
			 * @param search The string to search for
			 */
			function highlightSearch(element, search) {
				var match;
				// fireOnchange = true;
				if (search) {
					if (TEXT_TRUMPS_VALUE) {
						match = getMatchByText(element, search) || getMatchByValue(element, search);
					}
					else {
						match = getMatchByValue(element, search) || getMatchByText(element, search);
					}

					if (match) {
						classList.remove(getSearchElement(), CLASS_NOT_FOUND);
						selectMatch(element, match);
					}
					else {
						classList.add(getSearchElement(), CLASS_NOT_FOUND);
					}
				}

				else if (search === "") {
					// we have previously searched and have backspaced to an empty string
					if ((match = getMatchByValue(element, search)) && !shed.isSelected(match)) {
						selectMatch(element, match);
					}
				}
			}

			/**
			 * select the matching element or first option if empty match string
			 * @param  element
			 * @param  match
			 */
			function selectMatch(element, match) {
				timers.setTimeout(function() {
					if (match) {
						fireOnchange = true;
						element.selectedIndex = match.index;
					}
					else {
						fireOnchange = true;
						element.selectedIndex = 0;
					}
					element = null;
					match = null;
				}, 0);
			}

			/*
			 * helper to get options from a listbox either as a select or as an aria listbox role
			 * @param element the element which is undergoing search
			 * @returns node list of some kind of option
			 */
			function getOptions(element) {
				return group.get(element);
			}
			/*
			 * Search for first option with a matching 'text' property text match can be a partial
			 * match (case insensitive) if it is at least MIN_LEN_SUBSTRING_MATCH characters long
			 *
			 * @param element The select element to search
			 * @param search The string to search for
			 * @returns The matching option element if found
			 */
			function getMatchByText(element, search) {
				var options,
					length,
					next,
					nextTxt,
					result,
					partialMatch,
					i = 0,
					flags = "i",
					startsWithRe,
					containsRe;

				if (regexCache.starts.hasOwnProperty(search)) {
					startsWithRe = regexCache.starts[search];
					console.log("Got regex from cache: ", startsWithRe.source);
				}
				else {
					startsWithRe = regexCache.starts[search] = new RegExp("^" + escapeRe(search), flags);
				}

				if (regexCache.contains.hasOwnProperty(search)) {
					containsRe = regexCache.contains[search];
					console.log("Got regex from cache: ", containsRe.source);
				}
				else {
					containsRe = regexCache.contains[search] = new RegExp(startsWithRe.source, flags);
				}

				if ((options = getOptions(element)) && options.length) {
					length = options.length;
				}
				while (i < length) {
					next = options[i];
					i++;
					nextTxt = textContent.get(next);
					if (nextTxt && startsWithRe.test(nextTxt)) {
						result = next;
						break;
					}
					else if (!partialMatch && search.length >= MIN_LEN_SUBSTRING_MATCH && containsRe.test(nextTxt)) {
						partialMatch = next;
					}
				}
				return result || partialMatch;
			}

			/*
			 * Search for first option with a matching 'value' property value match must be an exact
			 * match (case insensitive) and must be at least MIN_LEN_VAL_MATCH characters long
			 *
			 * @param element The select element to search
			 * @param search The string to search for
			 * @returns The matching option element if found
			 */
			function getMatchByValue(element, search) {
				var options,
					length,
					next,
					nextVal,
					i = 0,
					result;
				// allow for reset to null option if search is ""
				if (search === "" || search.length >= MIN_LEN_VAL_MATCH) {
					if (search !== "") {
						search = search.toLocaleLowerCase();
					}
					options = getOptions(element);
					length = options.length;
					while (i < length) {
						next = options[i];
						i++;
						if ((nextVal = next.getAttribute("value")) || nextVal === "") {
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
			 * @param {Element} element The SELECT element the search box is attached to.
			 */
			function closeSearch(element) {
				var search = getSearchElement();
				textContent.set(search, "");
				if (!shed.isHidden(search, true)) {
					hideSearch(search);
					if (fireOnchange && element) {
						// programatically changing the select will not fire change so we gots to do it ourselves
						timers.setTimeout(event.fire, 0, element, event.TYPE.change);
					}
				}
				fireOnchange = false;
			}

			function hideSearch(search) {
				classList.remove(search, CLASS_NOT_FOUND);
				shed.hide(search);
			}

			/**
			 * Function to return the index of the matching option in the select element. Matching means that the option
			 * does not need to be strictly equal to match. The option need not belong to the given select list,
			 * essentially just the value and text will be matched.
			 *
			 * @function module:wc/ui/selectboxSearch.indexOf
			 * @param {Element} option An object with properties: text, value
			 * @param {(Element|Element[])} optContainer A dom element containing options (ie a select or an optgroup)
			 *    OR an array of options.
			 * @param {Element} [startAt] Optionally an index to start searching at.
			 * @returns {Integer} The index of the option.
			 */
			this.indexOf = function (option, optContainer, startAt) {
				var result = -1,
					options = (Array.isArray(optContainer)) ? optContainer : optContainer.getElementsByTagName(tag.OPTION),
					val = option.value,
					text = option.text,
					startIdx = (startAt && startAt > 0) ? startAt : 0,
					i,
					len,
					next;
				for (i = startIdx, len = options.length; i < len; i++) {
					next = options[i];
					if (next.value === val && next.text === text) {
						result = i;
						break;
					}
				}
				return result;
			};

			/**
			 * Set up select element search functionality.
			 *
			 * @function module:wc/ui/selectboxSearch.initialise
			 * @public
			 * @param {Element} element the element being initialised, usually document.body
			 */
			this.initialise = function(element) {
				ALLOWED = i18n.get("select_typeahead");
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};
		}
		var ns = uid(),
			/** @alias module:wc/ui/selectboxSearch */ instance = new SelectboxSearch();

		initialise.register(instance);
		return instance;
	});
