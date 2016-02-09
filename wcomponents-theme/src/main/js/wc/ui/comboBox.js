/**
 * Provides combo functionality.
 *
 * @typedef {Object} module:wc/ui/comboBox.config() Optional module configuration.
 * @property {?int} min The global (default) minimum number of characters which must be entered before a comboBox will
 * update its dynamic datalist. This can be over-ridden per instance of WSuggestions.
 * @default 3
 * @property {?int} delay The number of milliseconds for which a user must pause before a comboBox's datalist is
 * updated.
 * @default 250
 *
 * @module
 * @requires module:wc/has
 * @requires module:wc/ajax/triggerManager
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/textContent
 * @requires module:wc/dom/Widget
 * @requires module:wc/key
 * @requires module:wc/timers
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onchangeSubmit
 * @requires module:wc/ui/listboxAnalog
 */
define(["wc/has",
		"wc/ajax/triggerManager",
		"wc/dom/attribute",
		"wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/getFilteredGroup",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/textContent",
		"wc/dom/Widget",
		"wc/key",
		"wc/timers",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/onchangeSubmit",
		"wc/ui/listboxAnalog",
		"module"
	],
	/** @param has wc/has @param triggerManager wc/ajax/triggerManager @param attribute wc/dom/attribute @param classList wc/dom/classList @param event wc/dom/event @param focus wc/dom/focus @param getFilteredGroup wc/dom/getFilteredGroup @param initialise wc/dom/initialise @param shed wc/dom/shed @param textContent wc/dom/textContent @param Widget wc/dom/Widget @param key wc/key @param timers wc/timers @param ajaxRegion wc/ui/ajaxRegion @param processResponse wc/ui/ajax/processResponse @param onchangeSubmit wc/ui/onchangeSubmit @param listboxAnalog @param module @ignore */
	function(has, triggerManager, attribute, classList, event, focus, getFilteredGroup, initialise, shed, textContent, Widget, key, timers, ajaxRegion, processResponse, onchangeSubmit, listboxAnalog, module) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/comboBox~ComboBox
		 * @private
		 */
		function ComboBox() {
			var COMBO = new Widget("input", "", {"role": "combobox", "aria-owns": null}),
				LISTBOX = listboxAnalog.CONTAINER,
				OPTION = listboxAnalog.ITEM,
				CONTROLS = "aria-controls",
				filterTimer,
				filter = true,
				optionVal = {},
				touching,
				INITED = "wc.ui.comboBox.init",
				openSelect = "",  // the id of the currently open combo (if any)
				repainter,
				IETimeout = 0,  // IE cannot update itself fast enough to focus a newly opened list
				// stuff for chatty combox
				CLASS_CHATTY = "wc_combo_dyn",
				CHATTY_COMBO = COMBO.extend(CLASS_CHATTY),
				updateTimeout,
				VALUE_ATTRIB = "data-wc-value",
				conf = module.config(),
				/**
				 * Wait this long before updating the list on keydown.
				 * @var
				 * @type Number
				 * @private
				 */
				DELAY = (conf ? (conf.delay || 250) : 250),
				/**
				 * Only update the list if the user has entered at least this number of characters.
				 * @var
				 * @type Number
				 * @private
				 */
				DEFAULT_CHARS = (conf ? (conf.min || 3) : 3),
				CHAR_KEYS,  // used in the keydown event handler if we cannot use the input event
				nothingLeftReg = {};  // last search returned no match, keep the search term for future reference

			if (has("ie") <= 8) {
				require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
					repainter = inlineBlock;
				});
				IETimeout = 150;  // IE cannot update itself fast enough to focus a newly opened list
			}

			/**
			 * Get the listbox part of a combo.
			 *
			 * @function
			 * @private
			 * @param {Element} element A combo or an option in the listbox.
			 * @returns {?Element} The list box if it is able to be found.
			 */
			function getListBox(element) {
				var result, listId;
				if (!element) {
					return null;
				}
				if (COMBO.isOneOfMe(element)) {
					if ((listId = element.getAttribute("aria-owns"))) {
						result = document.getElementById(listId);
					}
				}
				else {
					result = LISTBOX.findAncestor(element);
				}
				return result;
			}

			/**
			 * Find all selected options in the _LISTBOX and deselect them.
			 *
			 * @function
			 * @private
			 * @param {Element} combo The combo box from which to strip selected.
			 */
			function clearList(combo) {
				var listbox = getListBox(combo), options;
				if (listbox && (options = getFilteredGroup(listbox))) {
					options.forEach(function(next) {
						shed.deselect(next, true);  // do not publish they are already hidden and are not important for anything else.
						next.tabIndex = 0;
					});
				}
			}

			/**
			 * Hides options in this combo based on its current value. The behaviour is based primarily on the
			 * native behaviour of Firefox. In particular the partial string matching is a Firefox feature, all
			 * the other browsers I looked at only match the start of each option. Obviously there are
			 * performance implications on doing partial matches. If this turns out to be a problem it would be
			 * possible to retain the behaviour but speed it up by building a lookup cache so that each search
			 * is only performed once.
			 *
			 * @function
			 * @private
			 * @param {Element} combo A combo control to filter
			 * @param {integer} [delay] A timeout delay, default to 250 if not set
			 */
			function filterOptions(combo, delay) {
				var _delay = delay, _filter;

				if (COMBO.isOneOfMe(combo)) {
					_filter = function() {
						var i, len, next, optval, value = combo.value,
							list = getListBox(combo),
							options = OPTION.findDescendants(list),
							setTabIndexOn = -1;
						value = value.toLocaleLowerCase();
						for (i = 0, len = options.length; i < len; i++) {
							next = options[i];
							optval = textContent.get(next).toLocaleLowerCase();
							if (!value || optval.indexOf(value) >= 0) {
								shed.show(next, true);
								if (setTabIndexOn === -1) {
									setTabIndexOn = i;
									next.tabIndex = 0;
								}
							}
							else {
								shed.hide(next, true);
								next.tabIndex = -1;
							}
						}
						if (repainter) {
							repainter.checkRepaint(combo);
						}
					};

					if (!shed.isExpanded(combo)) {
						shed.expand(combo);
						clearList(combo);
					}

					if (!(_delay || delay === 0)) {
						_delay = DELAY;
					}
					if (filterTimer) {
						timers.clearTimeout(filterTimer);
					}
					filterTimer = timers.setTimeout(_filter, _delay);
				}
			}

			/**
			 * Load new options using ajax.
			 *
			 * @function
			 * @private
			 * @param {Element} element The combo box for which we want new options.
			 */
			function load(element) {
				var list = getListBox(element), getData, trigger, id;

				if (list) {
					id = list.id;
					getData = id + "=" + window.encodeURIComponent(element.value);
					list.setAttribute(CONTROLS, element.id);  // just to make sure because we will need this attribute when the ajax response comes in.
					ajaxRegion.register({
						id: id,
						loads: [id],
						getData: getData,
						serialiseForm: false,
						method: "get"});
					if ((trigger = triggerManager.getTrigger(id))) {
						trigger.fire();
					}
				}
			}

			/**
			 * Gets a datalist of suggestions for a particular input element.
			 *
			 * @function
			 * @private
			 * @param {Element} element The input element for which we need the suggestions.
			 */
			function getNewOptions(element) {
				var id = element.id;
				if (nothingLeftReg[id]) {
					if (element.value.indexOf(nothingLeftReg[id]) === 0) {
						return;  // there was nothing left last time we did this search
					}
					delete nothingLeftReg[id];
				}
				load(element);
			}

			/**
			 * Updates the datalist for a given combo element if the element's content is at least DEFAULT_CHARS.
			 * @function
			 * @private
			 * @param {Element} element The input element we are interested in.
			 */
			function updateList(element) {
				var list = getListBox(element), min;

				if (!list) {
					return;
				}

				min = list.getAttribute("${wc.ui.combo.list.attrib.minChars}") || DEFAULT_CHARS;
				if (element.value.length >= min) {
					if (!shed.isExpanded(element)) {
						shed.expand(element);
					}
					updateTimeout = timers.setTimeout(getNewOptions, DELAY, element);
				}
			}

			/**
			 * Focus the listbox part of a combo if it has any options.
			 *
			 * @function
			 * @private
			 * @param {Element} listbox the _LISTBOX sub-component to focus.
			 */
			function focusListbox(listbox) {
				if (listbox && OPTION.findDescendant(listbox)) {
					onchangeSubmit.ignoreNextChange();
					// NOTE: this timeout has been tested further and is absolutely required in IE8
					timers.setTimeout(focus.focusFirstTabstop, IETimeout, listbox);
				}
			}

			/**
			 * Find a fake "combo" for a given list box.
			 *
			 * @function
			 * @private
			 * @param {Element} listbox The list box component.
			 * @returns {?Element} The combo box wrapper element.
			 */
			function getCombo(listbox) {
				var comboId = listbox.getAttribute(CONTROLS), result;
				if (comboId) {
					result = document.getElementById(comboId);
				}
				return result;
			}

			function getSuggestionValue (element, getLowerCase) {
				var txt = element.hasAttribute(VALUE_ATTRIB) ? element.getAttribute(VALUE_ATTRIB) : textContent.get(element);
				return getLowerCase ? txt.toLocaleLowerCase() : txt;
			}

			/**
			 * Update the value of the combo based on interaction with an option. NOTE: native combos in HTML5 do
			 * not update on select of the options! This is why we do not have a shed.SELECT subscriber to do this.
			 *
			 * @function
			 * @private
			 * @param {Element} combo The combo to update.
			 * @param {Element} option The option which caused the update.
			 */
			function setValue(combo, option) {
				var listbox = getListBox(combo), value;

				if (listbox) {
					value = getSuggestionValue(option);
					combo.value = value;
				}
			}

			/**
			 * Given an option in a listbox and a printable character, find the next option (if any) which starts
			 * with that character.
			 *
			 * @function
			 * @private
			 * @param {Element} listbox The container for the list of options, already calculated in the calling
			 *     function so just passed through for convenience.
			 * @param {Element} start The element from which we start the search. This will not return even if
			 *     it starts with the character we want.
			 * @param {String} keyName The character we are searching for.
			 * @returns {Element} The next available option which starts with keyName (if any), or undefined.
			 */
			function getTextTarget(listbox, start, keyName) {
				var options = getFilteredGroup(listbox),
					result,
					startIdx = options.indexOf(start), i, next, txt;

				if (startIdx > -1) {
					for (i = startIdx + 1; i < options.length; ++i) {
						next = options[i];
						if ((txt = textContent.get(next)) && txt[0].toLocaleLowerCase() === keyName) {
							result = next;
							break;
						}
					}
				}

				return result;
			}

			/**
			 * Subscriber to SHED pseudo-event publisher.
			 *
			 * @function
			 * @private
			 * @param {Element} element the element SHED has acted upon.
			 * @param {String} action the SHED action.
			 */
			function shedSubscriber(element, action) {
				var listbox;

				if (!element) {
					return;
				}
				if (COMBO.isOneOfMe(element)) {
					listbox = getListBox(element);
					if (action === shed.actions.EXPAND && shed.isExpanded(element)) {
						onchangeSubmit.ignoreNextChange();
						openSelect = element.id;
						listbox.setAttribute(CONTROLS, element.id);
						if (listbox.previousSibling !== element) {
							if (element.parentNode.lastChild === element) {
								element.parentNode.appendChild(listbox);
							}
							else {
								element.parentNode.insertBefore(listbox, element.nextSibling);
							}
						}

						listbox.style.minWidth = element.clientWidth + "px";
						shed.show(listbox);
						optionVal[(element.id)] = element.value;
						if (filter && !CHATTY_COMBO.isOneOfMe(element)) {
							filterOptions(element, 0);
						}
					}
					else if (action === shed.actions.COLLAPSE && !shed.isExpanded(element)) {
						onchangeSubmit.clearIgnoreChange();
						acceptFirstMatch(element);
						shed.hide(listbox);
						openSelect = "";
						if (optionVal[(element.id)] !== element.value) {
							timers.setTimeout(event.fire, 0, element, event.TYPE.change);
						}
						optionVal[(element.id)] = null;
					}
					else if (listbox && (action === shed.actions.HIDE || action === shed.actions.DISABLE)) {
						shed.hide(listbox);
					}
				}
				else if (action === shed.actions.HIDE && LISTBOX.isOneOfMe(element)) {
					element.removeAttribute(CONTROLS);
				}
			}

			/**
			 * Update the combo when an option is selected.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being selected.
			 */
			function shedSelectSubscriber(element) {
				var listbox, combo;
				if (element && OPTION.isOneOfMe(element) && (listbox = getListBox(element)) && (combo = getCombo(listbox))) {
					setValue(combo, element);
				}
			}

			/**
			 * Keydown event handler. Handles key events as per {@link http://www.w3.org/TR/wai-aria-practices/#combobox}.
			 *
			 * <p><strong>NOTES:</strong> the LEFT ARROW and RIGHT ARROW are native in input elements in the text
			 * state; we have not implemented list pagination so PAGE_UP and PAGE_DOWN are not mapped (this may
			 * be needed in future).</p>
			 *
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var keyCode = $event.keyCode, target = $event.target, listbox;
				if (!$event.defaultPrevented) {
					if (COMBO.isOneOfMe(target)) {
						if (handleKeyCombobox(target, keyCode, $event.altKey)) {
							$event.preventDefault();
						}
					}
					else if ((listbox = getListBox(target, 1))) {
						if (handleKeyListbox(target, listbox, keyCode)) {
							$event.preventDefault();
						}
					}
				}
			}

			/**
			 * Handles a keypress on "listbox".
			 * @param {Element} target The element that received the key event.
			 * @param {Element} listbox The listbox.
			 * @param {number} keyCode The key that was pressed.
			 * @returns {boolean} true if the key event needs to be cancelled.
			 */
			function handleKeyListbox(target, listbox, keyCode) {
				var keyName, PRINTABLE_RE = /[ -~]/,
					KEY_NAME_RE = /^DOM_VK_/,
					combo = getCombo(listbox),
					preventDefault = false;
				if (!combo) {
					return;
				}
				/* keydown happens when a list item is focussed */
				if ((keyCode === KeyEvent.DOM_VK_ESCAPE || keyCode === KeyEvent.DOM_VK_RETURN)) {
					/* ESCAPE closes the combo, RETURN selects the option then collapses the combo.*/
					if (keyCode === KeyEvent.DOM_VK_RETURN) {
						setValue(combo, target);
					}
					focus.setFocusRequest(combo, function() {
						shed.collapse(combo);
					});
					preventDefault = true;
				}
				else if (keyCode === KeyEvent.DOM_VK_TAB) {
					/* TAB to leave the list so select the current option and collapse */
					setValue(combo, target);
					shed.collapse(combo);
				}
				else if ((keyName = key.getLiteral(keyCode)) && (keyName = keyName.replace(KEY_NAME_RE, "")) && keyName.length === 1 && PRINTABLE_RE.test(keyName)) {
					/* printable char pressed: find the next matching option */
					target = getTextTarget(listbox, target, keyName.toLocaleLowerCase());
					if (target) {
						focus.setFocusRequest(target);
					}
				}
				return preventDefault;
			}

			/**
			 * Handles a keypress on "combobox" itself (not the listbox).
			 * @param {Element} target The combobox
			 * @param {number} keyCode The key that was pressed.
			 * @param {boolean} altKey
			 * @returns {boolean} true if the key event needs to be cancelled.
			 */
			function handleKeyCombobox(target, keyCode, altKey) {
				var listbox, preventDefault = false;
				/* keydown happens when a combo input is focused */
				if (keyCode === KeyEvent.DOM_VK_TAB) {
					// TAB out, do nothing, focus will take care of it.
					return;
				}
				if (keyCode === KeyEvent.DOM_VK_ESCAPE) {
					if (shed.isExpanded(target)) {
						shed.collapse(target);
						preventDefault = true;
					}
				}
				else if (keyCode === KeyEvent.DOM_VK_DOWN) {
					if (shed.isExpanded(target)) {
						if ((listbox = getListBox(target))) {
							focusListbox(listbox);
						}
					}
					else if (altKey) {
						shed.expand(target);
					}
				}
				else if (keyCode === KeyEvent.DOM_VK_UP) {
					if (shed.isExpanded(target)) {
						if (altKey) {
							shed.collapse(target);
						}
						else if ((listbox = getListBox(target))) {
							focusListbox(listbox);
						}
					}
				}
				else if (filter && (!key.isMeta(keyCode)) && !CHATTY_COMBO.isOneOfMe(target)) {
					filterOptions(target);
				}
				return preventDefault;
			}

			/**
			 * Handles keydown events in a chatty combo: updates the datalist. This is a primitive effort compared
			 * to using the input event.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function lameInputEvent($event) {
				var element = $event.currentTarget, keyCode = $event.keyCode, keyName,
					KEY_NAME_RE = /^DOM_VK_/,
					NUMPAD = "NUMPAD";
				if (!$event.defaultPrevented) {
					if (updateTimeout) {
						timers.clearTimeout(updateTimeout);
						updateTimeout = null;
					}
					CHAR_KEYS = CHAR_KEYS || [KeyEvent.DOM_VK_BACK_SPACE, KeyEvent.DOM_VK_SPACE, KeyEvent.DOM_VK_DELETE, KeyEvent.DOM_VK_SEMICOLON, KeyEvent.DOM_VK_EQUALS, KeyEvent.DOM_VK_MULTIPLY, KeyEvent.DOM_VK_ADD, KeyEvent.DOM_VK_SUBTRACT, KeyEvent.DOM_VK_DECIMAL, KeyEvent.DOM_VK_DIVIDE, KeyEvent.DOM_VK_COMMA, KeyEvent.DOM_VK_PERIOD, KeyEvent.DOM_VK_SLASH, KeyEvent.DOM_VK_BACK_QUOTE, KeyEvent.DOM_VK_OPEN_BRACKET, KeyEvent.DOM_VK_BACK_SLASH, KeyEvent.DOM_VK_CLOSE_BRACKET, KeyEvent.DOM_VK_QUOTE];

					if (CHAR_KEYS.indexOf(keyCode) > -1) {
						updateList(element);
					}
					else if ((keyName = key.getLiteral(keyCode))) {
						if (keyName.indexOf("+") > -1) {
							keyName = keyName.split("+");
							keyName = keyName[keyName.length - 1];
						}
						if ((keyName = keyName.replace(KEY_NAME_RE, ""))) {
							if (keyName.length === 1 || keyName.indexOf(NUMPAD) === 0) {
								updateList(element);
							}
						}
					}
				}
			}

			/**
			 * Click event handler. If a click is in a combo then toggle its expanded state. If the click is in
			 * the combo listbox then set the combo's value.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var target = $event.target, combo, listbox;
				if (!$event.defaultPrevented) {
					if (COMBO.isOneOfMe(target) && !shed.isDisabled(target)) {
						shed.toggle(target, shed.actions.EXPAND);
						$event.preventDefault();
					}
					else if ((listbox = getListBox(target)) && (combo = getCombo(listbox))) {
						// update on option click
						setValue(combo, target);
						focus.setFocusRequest(combo, function() {
							shed.collapse(combo);
						});
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
			 * @param {Event} $event The touchstart event.
			 */
			function touchstartEvent($event) {
				var target, listbox, touch;
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
			 * @param {Event} $event The touchend event.
			 */
			function touchendEvent($event) {
				var target = $event.target, listbox, combo;
				if (!$event.defaultPrevented && touching && target === touching &&
					(listbox = getListBox(target)) && (combo = getCombo(listbox))) {
					// update on option click
					setValue(combo, target);
					focus.setFocusRequest(combo, function() {
						shed.collapse(combo);
					});
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
			 * @param {Event} $event The input event.
			 */
			function inputEvent($event) {
				if (updateTimeout) {
					timers.clearTimeout(updateTimeout);
					updateTimeout = null;
				}
				updateList($event.target);
			}
			/**
			 * Focus event handler closes any open combo when ANYTHING is focused other than the listbox for the
			 * currently open combo.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focus/focusin event as published by the wc event manager.
			 */
			function focusEvent($event) {
				var element = $event.target, isCombo, openCombo, listbox;

				isCombo = ((element === window || element === document) ? false : COMBO.isOneOfMe(element));
				if (!$event.defaultPrevented) {
					// chatty ajax combos need a special input listener
					if (isCombo && (listbox = getListBox(element)) && listbox.hasAttribute("data-wc-chat") && !attribute.get(element, INITED)) {
						attribute.set(element, INITED, true);
						classList.add(element, CLASS_CHATTY);
						if (event.canCapture) {
							event.add(element, event.TYPE.input, inputEvent);
						}
						else {
							event.add(element, event.TYPE.keydown, lameInputEvent);
						}
					}

					onchangeSubmit.clearIgnoreChange();

					// check openSelect before trying to collapse element in case we have gone straight from an open combo to another combo
					if (openSelect && element.id !== openSelect) {
						if ((openCombo = document.getElementById(openSelect))) {
							/* close any open combos when focusing elsewhere but
							 * if I have focussed in the current combo's list box
							 * do not close the combo.*/
							if (element === window || !((listbox = getListBox(element)) && listbox === getListBox(openCombo))) {
								shed.collapse(openCombo);
							}
						}
						else {
							/* this will happen in one very unusual circumstance:
							 * we open a combo then that combo gets blown away whilst it is open
							 * and so openSelect is set but does not point to anything*/
							openSelect = "";
						}
					}

					if (isCombo && shed.isExpanded(element)) {
						shed.collapse(element);
					}
				}
			}
			/**
			 * This AJAX subscriber runs before any content is added to the DOM and tests all forms in the page to
			 * determine if we have to recalculate the initial state of a form after the ajax action finishes. If the
			 * form ancestor of the ajax target element does not have unsaved changes prior to the AJAX action then we
			 * set a flag to recalculate the 'initial' state allowing for the changes made by the AJAX action.
			 *
			 * This is to cover the situation where an AJAX transaction occurs which adds or removes form fields. This
			 * will always cause an unsavedChanges warning because the serialization is different, even if the user does
			 * not actually change anything. This will occur, for example, if a WCancelButton is triggered in a WDialog
			 * before the user makes any changes.
			 *
			 * @function
			 * @private
			 * @param {Element} element The AJAX target element in the DOM prior to the ajax action.
			 * @param {DocumentFragment} documentFragment The transformed XML of the ajax response payload.
			 */
			function ajaxSubscriber(element, documentFragment) {
				var replacement, controls;
				if (element && (LISTBOX.isOneOfMe(element)) && (controls = element.getAttribute(CONTROLS))) {
					replacement = LISTBOX.findDescendant(documentFragment);  // only get one because we are only interested in the case of replacement options

					if (replacement && replacement.id === element.id) {
						replacement.setAttribute(CONTROLS, controls);
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
			 * @param {Element} element The AJAX target element in the DOM after the AJAX action.
			 */
			function postAjaxSubscriber(element) {
				var combo, option;
				if (element && (LISTBOX.isOneOfMe(element))) {
					combo = getCombo(element);

					if (!combo) { // this would be a disaster.
						shed.hide(element, true);
						return;
					}

					option = OPTION.findDescendant(element);

					if (!option) {
						nothingLeftReg[combo.id] = combo.value;
						if (shed.isExpanded(combo)) {
							shed.collapse(combo);
							return;
						}
						return;
					}

					/*
					 * TODO: we need to make an implementation of aria-autocomplete = "inline" combos but not this one.
					if (combo.getAttribute("aria-autocomplete") === "inline") {
						// set the textbox value to the first suggestion value.
						setValue (combo, option);
						shed.hide(element, true);
						if (shed.isExpanded(combo)) {
							shed.collapse(combo);
						}
						return;
					}
					*/

					if (!shed.isExpanded(combo)) {
						shed.expand(combo);
					}
					else {
						element.style.minWidth = combo.clientWidth + "px";
						shed.show(element, true);
					}
				}
			}

			/**
			 * This allows another function to force the value of the given element to be parsed according to its parser
			 * and the first resulting match (if any) to be chosen.
			 * @function module:wc/ui/comboBox.acceptFirstMatch
			 * @public
			 * @param {Element} element An input element, either full or partial date.
			 */
			function acceptFirstMatch(element) {
				var listbox, candidates,
					value = element.value.toLocaleLowerCase(),
					match, txtMatch;

				// we only want to force a match if we have a value and aria-autocomplete === "list".
				if (!value || element.getAttribute("aria-autocomplete") !== "list") {
					return;
				}

				listbox = getListBox(element);
				if (!listbox || shed.isHidden(listbox)) { // listbox should always be available.
					return;
				}

				candidates = getFilteredGroup(listbox, {filter: getFilteredGroup.FILTERS.visible, containerWd: LISTBOX, itemWd: OPTION});

				if (candidates && candidates.length) {
					if (candidates.some(function (next) {
						var optVal = getSuggestionValue(next, true);
						return optVal === value;
					})) {
						// we have entered a matching value so do nothing.
						return;
					}
					match = candidates[0];
					// there is a chance, though it would be unusual, that the textbox value was updated and the ajax suggesion mechanism did not take.
					// in this case we may have not reset the filtered suggestions for the new input. I can force this to occur if I am very sneaky.
					txtMatch = getSuggestionValue(match, true);
					if (txtMatch.indexOf(value) === -1) {
						element.value = ""; // If I am very sneaky I deserve to suffer.
						return;
					}
					setValue(element, match);
				}
				else {
					element.value = "";
				}
			}

			/**
			 * Sets up initial event handlers for faux-combos.
			 *
			 * @function module:wc/ui/comboBox.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(window, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
				event.add(element, event.TYPE.keydown, keydownEvent);

				if (has("event-ontouchstart")) {
					event.add(element, event.TYPE.touchstart, touchstartEvent);
					event.add(element, event.TYPE.touchend, touchendEvent);
					event.add(element, event.TYPE.touchcancel, touchcancelEvent);
				}
			};

			/**
			 * Undertakes late setup, including setting up faux datalist elements.
			 *
			 * @function module:wc/ui/comboBox.postInit
			 * @public
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.EXPAND, shedSubscriber);
				shed.subscribe(shed.actions.COLLAPSE, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.ENABLE, shedSubscriber);
				shed.subscribe(shed.actions.SELECT, shedSelectSubscriber);
				processResponse.subscribe(ajaxSubscriber);
				processResponse.subscribe(postAjaxSubscriber, true);
			};

			/**
			 * gets the Widgets which describe the component
			 *
			 * @function module:wc/ui/comboBox.getWidget
			 * @public
			 * @returns {wc/dom/Widget} the COMBO box Widget descriptor.
			 */
			this.getWidget = function() {
				return COMBO;
			};

			/**
			 * Public for testing
			 * @function  module:wc/ui/comboBox._getList
			 * @ignore
			 */
			this._getList = getListBox;

			/**
			 * Reference to the keydown event handler. Public for testing.
			 * function  module:wc/ui/comboBox._keydownEvent
			 * @ignore
			 */
			this._keydownEvent = keydownEvent;

			/**
			 * Set client side list filtering on or off. Public for testing as most of the unit tests require we
			 * do not do list filtering in the client and therefore no equivalent used internally.
			 * @function module:wc/ui/comboBox._setFilter
			 * @param {boolean} [set] force on (true) or off.
			 * @ignore
			 */
			this._setFilter = function(set) {
				filter = set;
			};
		}

		var /** @alias module:wc/ui/comboBox */ instance = new ComboBox();
		initialise.register(instance);
		return instance;
	});
