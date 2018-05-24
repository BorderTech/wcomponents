define(["wc/has",
	"wc/dom/attribute",
	"wc/dom/classList",
	"wc/dom/event",
	"wc/dom/focus",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/key",
	"wc/timers",
	"wc/ui/ajaxRegion",
	"wc/ui/ajax/processResponse",
	"wc/ui/onchangeSubmit",
	"wc/ui/listboxAnalog",
	"wc/config"
],
	function(has, attribute, classList, event, focus, initialise, shed, Widget, key, timers, ajaxRegion, processResponse, onchangeSubmit, listboxAnalog, wcconfig) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/comboBox~ComboBox
		 * @private
		 */
		function ComboBox() {
			var COMBO = new Widget("", "wc-combo", {"role": "combobox"}),
				LISTBOX = listboxAnalog.CONTAINER.clone(),
				TEXTBOX = new Widget("input"),
				OPTION = listboxAnalog.ITEM,
				OPENER_BUTTON = new Widget("button"),
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
				conf = wcconfig.get("wc/ui/comboBox", {
					delay: 250,  // Wait this long before updating the list on keydown
					min: 3  // Only update the list if the user has entered at least this number of characters.
				}),
				CHAR_KEYS,  // used in the keydown event handler if we cannot use the input event
				nothingLeftReg = {};  // last search returned no match, keep the search term for future reference

			LISTBOX.descendFrom(COMBO, true);
			TEXTBOX.descendFrom(COMBO, true);
			OPENER_BUTTON.descendFrom(COMBO, true);

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
			 * @returns {Element} The list box if it is able to be found.
			 */
			function getListBox(element) {
				var combo;
				if (!element) {
					return null;
				}
				if (COMBO.isOneOfMe(element)) {
					return LISTBOX.findDescendant(element);
				}
				if (TEXTBOX.isOneOfMe(element)) {
					if ((combo = getCombo(element))) {
						return LISTBOX.findDescendant(combo);
					}
					return null;
				}
				return LISTBOX.findAncestor(element);
			}

			/**
			 * Find all selected options in the _LISTBOX and deselect them.
			 *
			 * @function
			 * @private
			 * @param {Element} combo The combo box from which to strip selected.
			 */
			function clearList(combo) {
				var listbox = getListBox(combo);
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
			 * @param {integer} [delay] A timeout delay, default to 250 if not set
			 */
			function filterOptions(combo, delay) {
				var _delay = delay, _filter;
				if (!COMBO.isOneOfMe(combo)) {
					return;
				}

				_filter = function() {
					var i,
						optval,
						textbox,
						value,
						options,
						setTabIndexOn = -1,
						list = getListBox(combo);
					if (!list) {
						return;
					}
					options = OPTION.findDescendants(list);
					if (!options) {
						return;
					}

					textbox = TEXTBOX.findDescendant(combo);
					if (textbox) {
						value = textbox.value.toLocaleLowerCase();
					}

					Array.prototype.forEach.call(options, function (next) {
						optval = listboxAnalog.getOptionValue(next, true, true);
						if (!value || optval.indexOf(value) >= 0) {
							shed.show(next, true);
							if (setTabIndexOn === -1) {
								setTabIndexOn = i;
								next.tabIndex = 0;
							}
						} else {
							shed.hide(next, true);
							next.tabIndex = -1;
						}
					});
					if (repainter) {
						repainter.checkRepaint(combo);
					}
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
			 * @param {Element} combo the combo we are updating
			 * @param {Element} element the textbox in the combo
			 */
			function load(combo, element) {
				var list = getListBox(combo),
					getData,
					id;

				if (list) {
					id = list.id;
					getData = id + "=" + window.encodeURIComponent(element.value);
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
			 * @param {Element} combo the combo being updated
			 * @param {Element} element the text field in combo
			 */
			function getNewOptions(combo, element) {
				var id = combo.id;
				if (nothingLeftReg[id]) {
					if (element.value.indexOf(nothingLeftReg[id]) === 0) {
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
			 * @param {Element} element The input element we are interested in.
			 */
			function updateList(element) {
				var combo = element.parentNode ,
					list = getListBox(combo), min;

				if (!list) {
					return;
				}

				min = list.getAttribute("data-wc-minchars") || conf.min;
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
			 * @param {Element} listbox the LISTBOX sub-component to focus.
			 */
			function focusListbox(listbox) {
				if (listbox && OPTION.findDescendant(listbox)) {
					timers.setTimeout(focus.focusFirstTabstop, IETimeout, listbox, function(target) {
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
			 * @param {Element} element The start element.
			 * @returns {Element} The combo box wrapper element.
			 */
			function getCombo(element) {
				return COMBO.findAncestor(element);
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
				var value,
					textbox = TEXTBOX.findDescendant(combo, true);

				if (textbox) {
					value = listboxAnalog.getOptionValue(option);
					textbox.value = value;
				}
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
				var textbox, opener;

				if (!(element && COMBO.isOneOfMe(element))) {
					return;
				}

				textbox = TEXTBOX.findDescendant(element);

				if (action === shed.actions.EXPAND) {
					if (shed.isExpanded(element)) {
						onchangeSubmit.ignoreNextChange();
						ajaxRegion.ignoreNextChange();
						openSelect = element.id;
						optionVal[(element.id)] = textbox ? textbox.value : null;
						if (filter && !CHATTY_COMBO.isOneOfMe(element)) {
							filterOptions(element, 0);
						}
					}
					return;
				}

				if (action === shed.actions.COLLAPSE) {
					if (!shed.isExpanded(element)) {
						onchangeSubmit.clearIgnoreChange();
						ajaxRegion.clearIgnoreChange();

						if (element.getAttribute("data-wc-listcomplete") === "true") {
							acceptFirstMatch(element);
						}
						openSelect = "";
						if (optionVal[(element.id)] !== textbox.value) {
							timers.setTimeout(event.fire, 0, textbox, event.TYPE.change);
						}
						optionVal[(element.id)] = null;
					}
					return;
				}

				if (action === shed.actions.DISABLE) {
					shed.disable(textbox, true);
					if ((opener = OPENER_BUTTON.findDescendant(element))) {
						shed.disable(opener, true);
					}
					shed.collapse(element, true);
					return;
				}

				if (action === shed.actions.ENABLE) {
					shed.enable(textbox, true);
					if ((opener = OPENER_BUTTON.findDescendant(element))) {
						shed.enable(opener, true);
					}
					return;
				}

				if (action === shed.actions.HIDE && shed.isExpanded(element)) {
					shed.collapse(element, true);
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
			 * Handles a keypress on "listbox".
			 * @function
			 * @private
			 * @param {Element} listbox The listbox.
			 * @param {number} keyCode The key that was pressed.
			 * @returns {boolean} true if the key event needs to be cancelled.
			 */
			function handleKeyListbox(listbox, keyCode) {
				var combo = getCombo(listbox),
					textbox;
				if (!combo) {
					return false;
				}

				if ((keyCode === KeyEvent.DOM_VK_ESCAPE || keyCode === KeyEvent.DOM_VK_RETURN)) {
					textbox = TEXTBOX.findDescendant(combo);
					focus.setFocusRequest(textbox, function() {
						shed.collapse(combo);
					});
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
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var keyCode = $event.keyCode,
					target = $event.target,
					listbox,
					openCombo;

				if (TEXTBOX.isOneOfMe(target)) {
					if (handleKeyTextbox(target, keyCode, $event.altKey)) {
						$event.preventDefault();
					}
					return;
				}

				if ((listbox = getListBox(target))) {
					if (handleKeyListbox(listbox, keyCode)) {
						$event.preventDefault();
					}
					return;
				}

				if (openSelect && keyCode === KeyEvent.DOM_VK_ESCAPE) {
					openCombo = document.getElementById(openSelect);
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
			 * @param {Element} combo the combo control
			 * @param {boolean} altKey `true` if the ALT key is pressed with the arrow
			 */
			function doDownButton(combo, altKey) {
				var listbox;

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
			 * @param {Element} combo the combo control
			 * @param {boolean} altKey `true` if the ALT key is pressed with the arrow
			 */
			function doUpKey(combo, altKey) {
				var listbox;
				if (shed.isExpanded(combo)) {
					if (altKey) {
						shed.collapse(combo);
					} else if ((listbox = getListBox(combo))) {
						focusListbox(listbox);
					}
				}
			}

			/**
			 * Handles a keypress on "combobox" itself (not the listbox).
			 * @function
			 * @private
			 * @param {Element} target The combobox
			 * @param {number} keyCode The key that was pressed.
			 * @param {boolean} altKey
			 * @returns {boolean} true if the key event needs to be cancelled.
			 */
			function handleKeyTextbox(target, keyCode, altKey) {
				var combo;
				/* keydown happens when a combo input is focused */
				if (keyCode === KeyEvent.DOM_VK_TAB) {
					// TAB out, do nothing, focus will take care of it.
					return false;
				}

				combo = getCombo(target);
				if (!combo) {
					return false;
				}

				switch (keyCode) {
					case KeyEvent.DOM_VK_ESCAPE:
						if (shed.isExpanded(combo)) {
							shed.collapse(combo);
							return true;
						}
						break;
					case KeyEvent.DOM_VK_DOWN:
						doDownButton(combo, altKey);
						break;
					case KeyEvent.DOM_VK_UP:
						doUpKey(combo, altKey);
						break;
					default:
						if (filter && (!key.isMeta(keyCode)) && !CHATTY_COMBO.isOneOfMe(combo)) {
							filterOptions(combo);
						}
				}
				return false;
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
					} else if ((keyName = key.getLiteral(keyCode))) {
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
				var target = $event.target, combo, textbox;

				if (!$event.defaultPrevented && (combo = getCombo(target))) {
					if (LISTBOX.findAncestor(target)) {
						if ((textbox = TEXTBOX.findDescendant(combo))) {
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
			 * Focus event handler closes any open combo when any interactive component is focused.
			 * This essentially means anything in the document receives focus which is not:
			 * - the listbox for the currently open combo
			 * - something preposterous like the "body" element
			 *
			 * Note that this behaviour is important to work around an IE11 bug where clicking the scrollbar of the listbox will set focus to the body.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focus/focusin event as published by the wc event manager.
			 */
			function focusEvent($event) {
				var element = $event.target,
					openCombo,
					listbox,
					combo;

				if (TEXTBOX.isOneOfMe(element)) {
					if ((combo = element.parentNode) && !attribute.get(combo, INITED)) {
						attribute.set(combo, INITED, true);
						event.add(combo, event.TYPE.keydown, keydownEvent);

						// chatty ajax combos need a special input listener
						if ((listbox = getListBox(combo)) && listbox.hasAttribute("data-wc-chat")) {
							classList.add(combo, CLASS_CHATTY);
							if (event.canCapture) {
								event.add(element, event.TYPE.input, inputEvent);
							} else {
								event.add(element, event.TYPE.keydown, lameInputEvent);
							}
						}
					}
				}

				if (openSelect) {
					combo = getCombo(element);
					// check openSelect before trying to collapse element in case we have gone straight from an open combo to another combo
					if (!(combo && combo.id === openSelect)) {
						openCombo = document.getElementById(openSelect);
						if (openCombo) {
							/* close any open combos when focusing elsewhere but
							 * if I have focussed in the current combo's list box (or something silly like the body)
							 * do not close the combo.*/

							if (element !== window && element !== document.body) {
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
			 * @param {Element} element The AJAX target element in the DOM after the AJAX action.
			 */
			function postAjaxSubscriber(element) {
				var combo;
				if (!element) {
					return;
				}
				setUpSuggestions(element);

				if ((LISTBOX.isOneOfMe(element))) {
					combo = getCombo(element);

					if (!combo) { // this would be a disaster.
						shed.hide(element, true);
						return;
					}

					if (!OPTION.findDescendant(element)) {
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
			 * Force the value of the given element to be parsed according to its parser and the first resulting match (if any) to be chosen. Allows
			 * us to force selection from a list making a broken combo or an overly complicated SELECT.
			 * @function
			 * @private
			 * @param {Element} element An input element, either full or partial date.
			 */
			function acceptFirstMatch(element) {
				var listbox,
					textbox,
					candidates,
					value,
					match, txtMatch;

				if (!((textbox = TEXTBOX.findDescendant(element, true)) &&
					(value = textbox.value.toLocaleLowerCase()))) {
					return;
				}

				listbox = getListBox(element);
				if (!listbox || shed.isHidden(listbox, true)) { // listbox should always be available.
					return;
				}

				candidates = listboxAnalog.getAvailableOptions(listbox);

				if (candidates && candidates.length) {
					if (candidates.some(function (next) {
						var optVal = listboxAnalog.getOptionValue(next, true);
						return optVal === value;
					})) {
						// we have entered a matching value so do nothing.
						return;
					}
					match = candidates[0];
					// there is a chance, though it would be unusual, that the textbox value was updated and the ajax suggesion mechanism did not take.
					// in this case we may have not reset the filtered suggestions for the new input. I can force this to occur if I am very sneaky.
					txtMatch = listboxAnalog.getOptionValue(match, true);
					if (txtMatch.indexOf(value) === -1) {
						textbox.value = ""; // If I am very sneaky I deserve to suffer.
						return;
					}
					setValue(element, match);
				} else {
					textbox.value = "";
				}
			}

			function moveSugestionList(el) {
				var listBox = getListBox(el),
					listId;
				if (listBox) {
					return;
				}
				listId = el.getAttribute("data-wc-suggest");
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
					el.insertAdjacentHTML("beforeend", "<span role='listbox' aria-busy='true' id='" + listId + "'></span>");
				}
			}

			function setUpSuggestions(element) {
				var el = element || document.body;
				if (element && COMBO.isOneOfMe(element)) {
					moveSugestionList(el);
				} else {
					Array.prototype.forEach.call(COMBO.findDescendants(el), moveSugestionList);
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
				setUpSuggestions(element);
				if (event.canCapture) {
					event.add(window, event.TYPE.focus, focusEvent, null, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);

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
				processResponse.subscribe(postAjaxSubscriber, true);
			};

			/**
			 * gets the Widget which describes the combo box.
			 *
			 * @function module:wc/ui/comboBox.getWidget
			 * @public
			 * @returns {wc/dom/Widget} the COMBO box Widget descriptor.
			 */
			this.getWidget = function() {
				return COMBO;
			};

			/**
			 * gets the Widget which describes the listbox part of a combo.
			 *
			 * @function module:wc/ui/comboBox.getListWidget
			 * @public
			 * @returns {wc/dom/Widget} the LISTBOX Widget descriptor.
			 */
			this.getListWidget = function() {
				return LISTBOX;
			};

			/**
			 * Publicise getListBox for use in ComboLoader.
			 * @ignore
			 */
			this._getList = getListBox;

			/**
			 * Reference to the keydown event handler. Public for testing.
			 * @ignore
			 */
			this._keydownEvent = keydownEvent;

			/**
			 * Set client side list filtering on or off. Public for testing as most of the unit tests require we do not do list filtering in the
			 * client and therefore no equivalent used internally.
			 * @function
			 * @param {boolean} [set] force on (true) or off.
			 * @ignore
			 */
			this._setFilter = function(set) {
				filter = set;
			};
		}

		/**
		 * Provides combo functionality.
		 *
		 * @module
		 * @requires module:wc/has
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/key
		 * @requires module:wc/timers
		 * @requires module:wc/ui/ajaxRegion
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/ui/onchangeSubmit
		 * @requires module:wc/ui/listboxAnalog
		 * @requires module:wc/config
		 */
		var instance = new ComboBox();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/comboBox~config Optional module configuration.
		 * @property {?int} min The global (default) minimum number of characters which must be entered before a comboBox will
		 * update its dynamic datalist. This can be over-ridden per instance of WSuggestions.
		 * @default 3
		 * @property {?int} delay The number of milliseconds for which a user must pause before a comboBox's datalist is
		 * updated.
		 * @default 250
		 */
	});
