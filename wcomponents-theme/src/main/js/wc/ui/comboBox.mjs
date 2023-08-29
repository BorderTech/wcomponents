import event from "wc/dom/event";
import focus from "wc/dom/focus";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import key from "wc/key";
import timers from "wc/timers";
import ajaxRegion from "wc/ui/ajaxRegion";
import processResponse from "wc/ui/ajax/processResponse";
import onchangeSubmit from "wc/ui/onchangeSubmit";
import listboxAnalog from "wc/ui/listboxAnalog";
import wcconfig from "wc/config";

const CLASS_CHATTY = "wc_combo_dyn",
	comboSelector = ".wc-combo[role='combobox']",
	chattyComboSelector = `${comboSelector}.${CLASS_CHATTY}`,
	listBoxSelector = `${comboSelector} > ${listboxAnalog.CONTAINER.toString()}`,
	textboxSelector = `${comboSelector} > input`,
	openerButtonSelector = `${comboSelector} > button`,
	optionSelector = listboxAnalog.ITEM.toString(),
	optionVal = {},
	conf = wcconfig.get("wc/ui/comboBox", {
		delay: 250,  // Wait this long before updating the list on keydown
		min: 3  // Only update the list if the user has entered at least this number of characters.
	}),
	nothingLeftReg = {};  // last search returned no match, keep the search term for future reference

let filterTimer,
	filter = true,
	touching,
	openSelect = "",  // the id of the currently open combo (if any)
	updateTimeout;

/**
 * Provides combo functionality.
 */
const instance = {
	/**
	 * gets the selector which describes the combo box.
	 *
	 * @public
	 * @returns {string} the COMBO box selector.
	 */
	getWidget: () => comboSelector,

	/**
	 * gets the Widget which describes the listbox part of a combo.
	 *
	 * @public
	 * @returns {string} the LISTBOX selector.
	 */
	getListWidget: () => listBoxSelector,

	/**
	 * Publicise getListBox for use in ComboLoader.
	 * @ignore
	 */
	_getList: getListBox,

	/**
	 * Reference to the keydown event handler. Public for testing.
	 * @ignore
	 */
	_keydownEvent: keydownEvent,

	/**
	 * Set client side list filtering on or off. Public for testing as most of the unit tests require we do not do list filtering in the
	 * client and therefore no equivalent used internally.
	 * @function
	 * @param {boolean} [set] force on (true) or off.
	 * @ignore
	 */
	_setFilter: function(set) {
		filter = set;
	}
};

/**
 * Get the listbox part of a combo.
 *
 * @function
 * @private
 * @param {HTMLElement} element A combo or an option in the listbox.
 * @returns {HTMLUListElement} The list box if it is able to be found.
 */
function getListBox(element) {
	if (!element) {
		return null;
	}
	if (element.matches(comboSelector)) {
		return element.querySelector(listBoxSelector);
	}
	if (element.matches(textboxSelector)) {
		const combo = getCombo(element);
		if (combo) {
			return combo.querySelector(listBoxSelector);
		}
		return null;
	}
	return element.closest(listBoxSelector);
}

/**
 * Find all selected options in the _LISTBOX and deselect them.
 *
 * @function
 * @private
 * @param {HTMLElement} combo The combo box from which to strip selected.
 */
function clearList(combo) {
	const listbox = getListBox(combo);
	if (listbox) {
		listboxAnalog.clearAllOptions(listbox);
	}
}

/**
 * Hides options in this combo based on its current value. The behaviour is based primarily on the  native behaviour of Firefox. In
 * particular the partial string matching is a Firefox feature, all the other browsers I looked at only match the start of each option.
 * Obviously there are performance implications on doing partial matches. If this turns out to be a problem it would be possible to retain
 * the behaviour but speed it up by building a lookup cache so that each search is only performed once.
 *
 * @function
 * @private
 * @param {Element} combo A combo control to filter
 * @param {int} [delay] A timeout delay, default to 250 if not set
 */
function filterOptions(combo, delay) {
	let _delay = delay;
	if (!combo.matches(comboSelector)) {
		return;
	}

	const _filter = function() {
		let value,
			setTabIndexOn = -1;

		const list = getListBox(combo);
		if (!list) {
			return;
		}
		const options = list.querySelectorAll(optionSelector);
		if (!options) {
			return;
		}

		const textbox = combo.querySelector(textboxSelector);
		if (textbox) {
			value = textbox.value.toLocaleLowerCase();
		}

		Array.prototype.forEach.call(options, function (next, idx) {
			const optval = listboxAnalog.getOptionValue(next, true, true);
			if (!value || optval.indexOf(value) >= 0) {
				shed.show(next, true);
				if (setTabIndexOn === -1) {
					setTabIndexOn = idx;
					next.tabIndex = 0;
				}
			} else {
				shed.hide(next, true);
				next.tabIndex = -1;
			}
		});
	};

	if (!shed.isExpanded(combo)) {
		shed.expand(combo);
		clearList(combo);
	}

	if (!(_delay || delay === 0)) {
		_delay = conf.delay;
	}
	if (filterTimer) {
		timers.clearTimeout(filterTimer);
	}
	filterTimer = timers.setTimeout(_filter, _delay);
}

/**
 * Load new options using ajax.
 *
 * @function
 * @private
 * @param {HTMLElement} combo the combo we are updating
 * @param {HTMLInputElement} element the textbox in the combo
 */
function load(combo, element) {
	const list = getListBox(combo);

	if (list) {
		const id = list.id;
		const getData = `${id}=${window.encodeURIComponent(element.value)}`;
		ajaxRegion.requestLoad(list, {
			id: id,
			loads: [id],
			getData: getData,
			serialiseForm: false,
			method: "get"}, true);
	}
}

/**
 * Gets a datalist of suggestions for a particular input element.
 *
 * @function
 * @private
 * @param {HTMLElement} combo the combo being updated
 * @param {HTMLInputElement} element the text field in combo
 */
function getNewOptions(combo, element) {
	const id = combo.id;
	if (nothingLeftReg[id]) {
		if (element.value.startsWith(nothingLeftReg[id])) {
			return; // there was nothing left last time we did this search
		}
		delete nothingLeftReg[id];
	}
	load(combo, element);
}

/**
 * Updates the datalist for a given combo element if the element's content is at least conf.min.
 * @function
 * @private
 * @param {HTMLInputElement} element The input element we are interested in.
 */
function updateList(element) {
	const combo = element.parentNode,
		list = getListBox(combo);

	if (!list) {
		return;
	}

	const min = list.getAttribute("data-wc-minchars") || conf.min;
	if (element.value.length >= min) {
		if (!shed.isExpanded(combo)) {
			shed.expand(combo);
		}
		updateTimeout = timers.setTimeout(getNewOptions, conf.delay, combo, element);
	}
}

/**
 * Focus the listbox part of a combo if it has any options.
 *
 * @function
 * @private
 * @param {HTMLUListElement} listbox the LISTBOX subcomponent to focus.
 */
function focusListbox(listbox) {
	if (listbox?.querySelector(optionSelector)) {
		timers.setTimeout(focus.focusFirstTabstop, 0, listbox, function(target) {
			if (!shed.isSelected(target)) {
				listboxAnalog.activate(target);
			}
		});
	}
}

/**
 * Find the combo for any element.
 *
 * @function
 * @private
 * @param {HTMLElement} element The start element.
 * @returns {HTMLElement} The combo box wrapper element.
 */
function getCombo(element) {
	return element.closest(comboSelector);
}

/**
 * Update the value of the combo based on interaction with an option. NOTE: native combos in HTML5 do
 * not update on select of the options! This is why we do not have a shed.SELECT subscriber to do this.
 *
 * @function
 * @private
 * @param {HTMLInputElement} combo The combo to update.
 * @param {HTMLOptionElement} option The option which caused the update.
 */
function setValue(combo, option) {
	const textbox = getTextbox(combo);
	if (textbox) {
		textbox.value = listboxAnalog.getOptionValue(option);
	}
}

/**
 * Event listener for shed custom events.
 *
 * @function
 * @private
 * @param {CustomEvent} $event The shed event that fired.
 */
function shedSubscriber($event) {
	const element = $event.target,
		action = $event.type;
	let opener;
	if (!element?.matches(comboSelector)) {
		return;
	}

	const textbox = element.querySelector(textboxSelector);

	if (action === shed.events.EXPAND) {
		if (shed.isExpanded(element)) {
			onchangeSubmit.ignoreNextChange();
			ajaxRegion.ignoreNextChange();
			openSelect = element.id;
			optionVal[(element.id)] = textbox ? textbox.value : null;
			if (filter && !element.matches(chattyComboSelector)) {
				filterOptions(element, 0);
			}
		}
		return;
	}

	if (action === shed.events.COLLAPSE) {
		if (!shed.isExpanded(element)) {
			onchangeSubmit.clearIgnoreChange();
			ajaxRegion.clearIgnoreChange();

			if (element.getAttribute("data-wc-listcomplete") === "true") {
				acceptFirstMatch(element);
			}
			openSelect = "";
			if (optionVal[(element.id)] !== textbox.value) {
				timers.setTimeout(event.fire, 0, textbox, "change");
			}
			optionVal[(element.id)] = null;
		}
		return;
	}

	if (action === shed.events.DISABLE) {
		shed.disable(textbox, true);
		opener = element.querySelector(openerButtonSelector);
		if (opener) {
			shed.disable(opener, true);
		}
		shed.collapse(element, true);
		return;
	}

	if (action === shed.events.ENABLE) {
		shed.enable(textbox, true);
		opener = element.querySelector(openerButtonSelector);
		if (opener) {
			shed.enable(opener, true);
		}
		return;
	}

	if (action === shed.events.HIDE && shed.isExpanded(element)) {
		shed.collapse(element, true);
	}
}

/**
 * Update the combo when an option is selected.
 *
 * @function
 * @private
 * @param {Event} $event Fired when an element is selected.
 */
function shedSelectSubscriber($event) {
	let listbox, combo;
	const element = $event.target;
	if (element?.matches(optionSelector) && (listbox = getListBox(element)) && (combo = getCombo(listbox))) {
		setValue(combo, element);
	}
}

/**
 * Handles a keypress on "listbox".
 * @function
 * @private
 * @param {HTMLElement} listbox The listbox.
 * @param {string} keyCode The key that was pressed.
 * @returns {boolean} true if the key event needs to be cancelled.
 */
function handleKeyListbox(listbox, keyCode) {
	const combo = getCombo(listbox);

	if (!combo) {
		return false;
	}

	// Check for NumpadEnter too just in case KeyboardEvent .code is sent instead of .key
	if ((keyCode === "Escape" || keyCode === "Enter" || keyCode === "NumpadEnter")) {
		const textbox = combo.querySelector(textboxSelector);
		focus.setFocusRequest(textbox, () => shed.collapse(combo));
		return true;
	}
	return false;
}

/**
 * Keydown event handler. Handles key events as per http://www.w3.org/TR/wai-aria-practices/#combobox.
 *
 * **NOTES:** the LEFT ARROW and RIGHT ARROW are native in input elements in the text state; we have not implemented list pagination so
 * PAGE_UP and PAGE_DOWN are not mapped (this may be needed in future)
 *
 * @function
 * @private
 * @param {KeyboardEvent} $event The keydown event.
 */
function keydownEvent($event) {
	const keyCode = $event.key,
		target = $event.target;

	if (target.matches(textboxSelector)) {
		if (handleKeyTextbox(target, keyCode, $event.altKey)) {
			$event.preventDefault();
		}
		return;
	}
	const listbox = getListBox(target);
	if (listbox) {
		if (handleKeyListbox(listbox, keyCode)) {
			$event.preventDefault();
		}
		return;
	}

	if (openSelect && keyCode === "Escape") {
		const openCombo = document.getElementById(openSelect);
		if (openCombo && shed.isExpanded(openCombo)) {
			shed.collapse(openCombo);
		}
	}
}

/**
 * Helper for handleKeyTextbox to handle pressing the DOWN ARROW when in a combo's textbox.
 *
 * @function
 * @private
 * @param {HTMLElement} combo the combo control
 * @param {boolean} altKey `true` if the ALT key is pressed with the arrow
 */
function doDownButton(combo, altKey) {
	let listbox;

	if (altKey && !shed.isExpanded(combo)) {
		shed.expand(combo);
	}

	if (shed.isExpanded(combo) && (listbox = getListBox(combo))) {
		focusListbox(listbox);
	}
}

/**
 * Helper for handleKeyTextbox to handle pressing the UP ARROW when in a combo's textbox.
 *
 * @function
 * @private
 * @param {HTMLElement} combo the combo control
 * @param {boolean} altKey `true` if the ALT key is pressed with the arrow
 */
function doUpKey(combo, altKey) {
	if (shed.isExpanded(combo)) {
		if (altKey) {
			shed.collapse(combo);
		} else {
			focusListbox(getListBox(combo));
		}
	}
}

/**
 * Handles a keypress on "combobox" itself (not the listbox).
 * @function
 * @private
 * @param {HTMLElement} target The combobox
 * @param {string} keyCode The key that was pressed.
 * @param {boolean} altKey
 * @returns {boolean} true if the key event needs to be cancelled.
 */
function handleKeyTextbox(target, keyCode, altKey) {
	/* keydown happens when a combo input is focused */
	if (keyCode === "Tab") {
		// TAB out, do nothing, focus will take care of it.
		return false;
	}

	const combo = getCombo(target);
	if (!combo) {
		return false;
	}

	switch (keyCode) {
		case "Escape":
			if (shed.isExpanded(combo)) {
				shed.collapse(combo);
				return true;
			}
			break;
		case "ArrowDown":
			doDownButton(combo, altKey);
			break;
		case "ArrowUp":
			doUpKey(combo, altKey);
			break;
		default:
			if (filter && (!key.isMeta(keyCode)) && !combo.matches(chattyComboSelector)) {
				filterOptions(combo);
			}
	}
	return false;
}

/**
 * Click event handler. If a click is in a combo then toggle its expanded state. If the click is in
 * the combo listbox then set the combo's value.
 *
 * @function
 * @private
 * @param {MouseEvent} $event The click event.
 */
function clickEvent($event) {
	const target = $event.target;
	let combo;

	if (!$event.defaultPrevented && (combo = getCombo(target))) {
		if (target.closest(listBoxSelector)) {
			const textbox = combo.querySelector(textboxSelector);
			if (textbox) {
				focus.setFocusRequest(textbox, function() {
					shed.collapse(combo);
				});
				$event.preventDefault();
			}
			return;
		}

		if (!shed.isDisabled(combo)) {
			shed.toggle(combo, shed.actions.EXPAND);
			$event.preventDefault();
		}
	}
}

/**
 * Touchstart event handler. Flags a combo as in a touching state if the touchstart event is in the
 * combo's listbox. Required as some touch device browsers do not propagate touch-instigated clicks
 * on elements which are not natively clickable.
 *
 * @function
 * @private
 * @param {TouchEvent} $event The touchstart event.
 */
function touchstartEvent($event) {
	let target, listbox, touch;
	if (!$event.defaultPrevented && $event.touches.length === 1 &&
		(touch = $event.touches[0]) && (target = touch.target) &&
		(listbox = getListBox(target)) && getCombo(listbox)) {
		touching = target;
	}
}

/**
 * Touchend event handler. Sets the combo value if the touchend was in a combo in the touch state and
 * the event was in the list of such a combo. Required as some touch device browsers do not propagate
 * touch-instigated clicks on elements which are not natively clickable.
 *
 * @function
 * @private
 * @param {TouchEvent} $event The touchend event.
 */
function touchendEvent($event) {
	const target = $event.target;
	let listbox, combo;
	if (!$event.defaultPrevented && touching && target === touching &&
		(listbox = getListBox(target)) && (combo = getCombo(listbox))) {
		// update on option click
		setValue(combo, target);
		focus.setFocusRequest(combo, () => shed.collapse(combo));
		$event.preventDefault();
	}
	touching = null;
}

/**
 * Clears the touch setup flag if a touch event is cancelled.
 * @function
 * @private
 */
function touchcancelEvent(/* $event */) {
	touching = null;
}

/**
 * Handles input events in a chatty combo: updates the datalist. NOTE: input cannot be cancelled.
 *
 * @function
 * @private
 * @param {InputEvent} $event The input event.
 */
function inputEvent($event) {
	if (updateTimeout) {
		timers.clearTimeout(updateTimeout);
		updateTimeout = null;
	}
	updateList($event.target);
}

/**
 * Focus event handler closes any open combo when any interactive component is focused.
 * This essentially means anything in the document receives focus which is not:
 * - the listbox for the currently open combo
 * - something preposterous like the "body" element
 *
 * Note that this behaviour is important to work around an IE11 bug where clicking the scrollbar of the listbox will set focus to the body.
 *
 * @function
 * @private
 * @param {FocusEvent} $event The focus/focusin event as published by the wc event manager.
 */
function focusEvent({ target }) {
	const INITED = "wc.ui.comboBox.init";
	if (target.matches(textboxSelector)) {
		const combo = target.parentElement;
		if (combo && !combo[INITED]) {
			combo[INITED] = true;
			event.add(combo, "keydown", keydownEvent);
			const listbox = getListBox(combo);
			// chatty ajax combos need a special input listener
			if (listbox?.hasAttribute("data-wc-chat")) {
				combo.classList.add(CLASS_CHATTY);
				event.add(target, "input", inputEvent);
			}
		}
	}

	if (openSelect) {
		const combo = getCombo(target);
		// check openSelect before trying to collapse element in case we have gone straight from an open combo to another combo
		if (!(combo && combo.id === openSelect)) {
			const openCombo = document.getElementById(openSelect);
			if (openCombo) {
				/* close any open combos when focusing elsewhere but
				 * if I have focussed in the current combo's list box (or something silly like the body)
				 * do not close the combo.*/
				let listbox;
				if (target !== window && target !== document.body) {
					listbox = getListBox(combo);
					if (listbox !== getListBox(openCombo)) {
						shed.collapse(openCombo);
					}
				} else {
					listbox = getListBox(openCombo);
					if (listbox) {
						/*
						 * This makes the listbox reminiscent of a modal dialog, but not quite.
						 * The listbox is closed when the user focuses another interactive component, or presses ESC (hopefully this is not annoying on touchscreen?).
						 * It primarily exists to work around an IE11 issue where clicking a scrollbar will set focus to the body element.
						 * Restoring focus to the listbox will ensure that keyboard listeners are wired up correctly.
						 */
						focusListbox(listbox);
					}
				}
			} else {
				openSelect = "";
			}
		}
	}
}

/**
 * This AJAX subscriber fires after the AJAX action has added components to the DOM. It is used to show
 * the listBox of a chatty combo after new suggestions are inserted. If the list comes back empty
 * then we hide the suggestion list and set the nothing left to find flag for this combo.
 *
 * @function
 * @private
 * @param {HTMLElement} element The AJAX target element in the DOM after the AJAX action.
 */
function postAjaxSubscriber(element) {
	if (!element) {
		return;
	}
	setUpSuggestions(element);

	if (element.matches(listBoxSelector)) {
		const combo = getCombo(element);

		if (!combo) {  // this would be a disaster.
			shed.hide(element, true);
			return;
		}

		if (!element.querySelector(optionSelector)) {
			nothingLeftReg[combo.id] = combo.value;
			if (shed.isExpanded(combo)) {
				shed.collapse(combo);
				return;
			}
			return;
		}

		if (!shed.isExpanded(combo)) {
			shed.expand(combo);
		}
	}
}

/**
 * Get the textbox for a combo.
 * @param {HTMLElement} combo
 * @return {HTMLInputElement} the textbox
 */
function getTextbox(combo) {
	if (!combo) {
		return null;
	}
	const {find} = Array.prototype;
	return find.call(combo.children, child => child.matches(textboxSelector));
}

/**
 * Force the value of the given element to be parsed according to its parser and the first resulting match (if any) to be chosen. Allows
 * us to force selection from a list making a broken combo or an overly complicated SELECT.
 * @function
 * @private
 * @param {HTMLElement} element A combo element.
 */
function acceptFirstMatch(element) {
	const textbox = getTextbox(element);
	let value = textbox ? textbox.value.toLocaleLowerCase() : "";
	if (!value) {
		return;
	}

	const listbox = getListBox(element);
	if (!listbox || shed.isHidden(listbox, true)) {  // listbox should always be available.
		return;
	}

	const candidates = listboxAnalog.getAvailableOptions(listbox);

	if (candidates?.length) {
		if (candidates.some(function (next) {
			const optVal = listboxAnalog.getOptionValue(next, true);
			return optVal === value;
		})) {
			// we have entered a matching value so do nothing.
			return;
		}
		const match = candidates[0];
		// there is a chance, though it would be unusual, that the textbox value was updated and the ajax suggestion mechanism did not take.
		// in this case we may have not reset the filtered suggestions for the new input. I can force this to occur if I am very sneaky.
		const txtMatch = listboxAnalog.getOptionValue(match, true);
		if (txtMatch.indexOf(value) === -1) {
			textbox.value = "";  // If I am very sneaky I deserve to suffer.
			return;
		}
		setValue(element, match);
	} else {
		textbox.value = "";
	}
}

function moveSuggestionList(el) {
	let listBox = getListBox(el);
	if (listBox) {
		return;
	}
	let listId = el.getAttribute("data-wc-suggest");
	if (!listId) {
		return;
	}
	listBox = document.getElementById(listId);
	if (listBox) {
		el.appendChild(listBox);
		if (listBox.getAttribute("data-wc-auto") === "list") {
			el.setAttribute("data-wc-listcomplete", "true");
		}
	} else {
		el.insertAdjacentHTML("beforeend", `<span role="listbox" aria-busy="true" id="${listId}"></span>`);
	}
}

function setUpSuggestions(element) {
	const el = element || document.body;
	if (element?.matches(comboSelector)) {
		moveSuggestionList(el);
	} else {
		Array.prototype.forEach.call(el.querySelectorAll(comboSelector), moveSuggestionList);
	}
}

/**
 * Provides combo functionality.
 *
 */

initialise.register({
	/**
	 * Sets up initial event handlers for faux-combos.
	 *
	 * @public
	 * @param {HTMLElement} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		setUpSuggestions(element);
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "click", clickEvent);

		event.add(element, "touchstart", touchstartEvent);
		event.add(element, "touchend", touchendEvent);
		event.add(element, "touchcancel", touchcancelEvent);
	},

	/**
	 * Undertakes late setup, including setting up faux datalist elements.
	 *
	 * @public
	 */
	postInit: function() {
		event.add(document.body, shed.events.EXPAND, shedSubscriber);
		event.add(document.body, shed.events.COLLAPSE, shedSubscriber);
		event.add(document.body, shed.events.HIDE, shedSubscriber);
		event.add(document.body, shed.events.DISABLE, shedSubscriber);
		event.add(document.body, shed.events.ENABLE, shedSubscriber);
		event.add(document.body, shed.events.SELECT, shedSelectSubscriber);
		processResponse.subscribe(postAjaxSubscriber, true);
	}
});

export default instance;

/**
 * @typedef {Object} Optional module configuration.
 * @property {?int} min The global (default) minimum number of characters which must be entered before a comboBox will
 * update its dynamic datalist. This can be over-ridden per instance of WSuggestions.
 * @default 3
 * @property {?int} delay The number of milliseconds for which a user must pause before a comboBox's datalist is
 * updated.
 * @default 250
 */

