define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/dom/getBox",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/selectboxSearch",
		"wc/ui/modalShim"],
	function(attribute, event, initialise, focus, formUpdateManager, getBox, shed, tag, Widget, ajaxRegion, processResponse, selectboxSearch, modal) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/multiSelectPair~MultiSelectPair
		 * @private
		 */
		function MultiSelectPair() {
			var CONTAINER = new Widget("fieldset", "wc-multiselectpair"),
				SELECT = new Widget("select"),
				CONTAINER_INITIALISED_KEY = "multiSelectPair.inited",
				LIST_TYPE_AVAILABLE = 0,
				LIST_TYPE_CHOSEN = 1,
				LIST_TYPE_ORDER = 2,
				LISTS = [],
				BUTTON = new Widget("button"),
				OPTGROUP = new Widget("optgroup"),
				ACTION_MAP = {"aall": addAll,
							"add": addSelected,
							"rall": removeAll,
							"rem": removeSelected};

			LISTS[LIST_TYPE_AVAILABLE] = SELECT.extend("wc_msp_av");
			LISTS[LIST_TYPE_CHOSEN] = SELECT.extend("wc_msp_chos");
			LISTS[LIST_TYPE_ORDER] = SELECT.extend("wc_msp_order");
			BUTTON.descendFrom(CONTAINER);
			SELECT.descendFrom(CONTAINER);

			/**
			 * Fix the width and height of the available and selected lists so that they are the same size. This was
			 * reintroduced (with the addition of a height fix) because some very common browsers will render a
			 * select Element with a size attribute at a different height if it has no options. Since I had to fix
			 * height I reintroduced fix width.
			 *
			 * @function
			 * @private
			 * @param {Element} [container] A WMultiSelectPair or any container component.
			 */
			function fixWidthHeight(container) {
				var el = container || document.body,
					components, PX = "px";
				if (CONTAINER.isOneOfMe(el)) {
					components = [container];
				}
				else {
					components = CONTAINER.findDescendants(el);
				}
				Array.prototype.forEach.call(components, function(next) {
					var avail = instance.getListByType(next, LIST_TYPE_AVAILABLE),
						chosen,
						box,
						maxWidth,
						maxHeight;
					if (avail.style.width) {
						return;  // already set
					}
					chosen = instance.getListByType(next, LIST_TYPE_CHOSEN);
					box = getBox(avail);
					maxWidth = box.width;
					maxHeight = box.height;

					box = getBox(chosen);
					maxWidth = Math.max(box.width, maxWidth);
					if (maxWidth) {
						avail.style.width = maxWidth + PX;
						chosen.style.width = maxWidth + PX;

						if (box.height !== maxHeight) {
							maxHeight = Math.max(box.height, maxHeight);
							avail.style.height = maxHeight + PX;
							chosen.style.height = maxHeight + PX;
						}
					}
				});
			}

			/**
			 * Get the "other" list's type when we have a list already. That is, if we have the "selected" list get the
			 * "available" list type and vice-versa.
			 *
			 * @function
			 * @private
			 * @param {Element} list A select list from a MultiSelectPair component.
			 * @returns {?int} The opposite "LIST_TYPE_" of the list. Returns null if we cannot determine.
			 */
			function getOppositeListType(list) {
				var type = instance.getListType(list);
				if (type !== null) {
					return ((type + 1) % 2);
				}
				return null;
			}

			/**
			 * Get the action pertinent to a given button.
			 *
			 * @function
			 * @private
			 * @param {Element} element A button element.
			 * @returns {?Function} The action to perform for a button of this type.
			 */
			function getAction(element) {
				var result;
				if (BUTTON.isOneOfMe(element)) {
					result = ACTION_MAP[element.value];
				}
				return result;
			}

			/**
			 * Move any selected option(s) from one list to the other.
			 *
			 * <p>Algorithm to calculate target index is:</p>
			 * <ol>
			 * <li>Find index of option in "fromList" = fromIndex.</li>
			 * <li>Find index of option in "submitList" (the hidden select element) = originalIndex.</li>
			 * <li>target index = (originalIndex - fromIndex)</li>
			 * </ol>
			 *
			 * @function
			 * @private
			 * @param {Element} fromList The select from which the selected options are removed.
			 */
			function addRemoveSelected(fromList) {
				var oppositeType = getOppositeListType(fromList),
					toList = instance.getListByType(fromList, oppositeType),
					orderList = instance.getListByType(fromList, LIST_TYPE_ORDER),
					fromIndex = fromList.selectedIndex,
					next,
					originalIndex,
					toIndex,
					parent,
					optgroup,
					result,
					toOptgroup,
					optgroupWD,
					orderOptGroup,
					fromGroupIndex;

				if (fromList.options.length && fromIndex >= 0) {
					toList.selectedIndex = -1;
					while (fromIndex >= 0) {
						next = fromList.options[fromIndex];
						parent = next.parentNode;
						if (OPTGROUP.isOneOfMe(parent)) {
							optgroupWD = OPTGROUP.extend("", {"label": parent.label});
							orderOptGroup = optgroupWD.findDescendant(orderList);
							originalIndex = selectboxSearch.indexOf(next, orderOptGroup);
							fromGroupIndex = selectboxSearch.indexOf(next, parent);

							if ((optgroup = optgroupWD.findDescendant(toList))) {
								toIndex = calcToIndex(originalIndex, fromGroupIndex);
								if (toIndex >= optgroup.children.length) {
									optgroup.appendChild(next);
								}
								else {
									optgroup.insertBefore(next, optgroup.children[toIndex]);
								}
								result = true;
							}
							else {
								// we need to make an optgroup in toList, but where?
								optgroup = document.createElement(tag.OPTGROUP);
								optgroup.label = parent.label;
								originalIndex = selectboxSearch.indexOf(next, orderList);
								toIndex = calcToIndex(originalIndex, fromIndex);
								if (toIndex >= toList.options.length) {
									toList.appendChild(optgroup);
								}
								else {
									// does the option we are creating the optgroup before have an optgroup parent?
									toOptgroup = toList.options[toIndex].parentNode;
									if (OPTGROUP.isOneOfMe(toOptgroup)) {
										toList.insertBefore(optgroup, toOptgroup);
									}
									else {
										toList.insertBefore(optgroup, toList.options[toIndex]);
									}
								}
								optgroup.appendChild(next);
								result = true;
							}

							if (parent.children.length === 0) {
								fromList.removeChild(parent);
							}
						}
						else {
							originalIndex = selectboxSearch.indexOf(next, orderList);
							toIndex = calcToIndex(originalIndex, fromIndex);
							if (toIndex >= toList.options.length) {
								toList.appendChild(next);
								result = true;
							}
							else {
								toOptgroup = toList.options[toIndex].parentNode;
								if (OPTGROUP.isOneOfMe(toOptgroup)) {
									toList.insertBefore(next, toOptgroup);
								}
								else {
									toList.insertBefore(next, toList.options[toIndex]);
								}
								result = true;
							}
						}
						fromIndex = fromList.selectedIndex;
					}
					if (result) {
						publishSelection(fromList, toList);
					}
				}
			}

			/*
			 * Helper for addRemoveSelected.
			 * @function
			 * @private
			 */
			function calcToIndex(originalIndex, fromIndex) {
				var result = originalIndex - fromIndex;
				if (result < 0) {
					result = originalIndex;
				}
				return result;
			}

			/*
			 * Helper for addRemoveSelected.
			 * @function
			 * @private
			 */
			function publishSelection(fromList, toList) {
				if (instance.getListType(fromList) === LIST_TYPE_CHOSEN) {
					shed.select(toList);  // the list won't actually be selected but the selection will be published
				}
				else {
					shed.deselect(toList);  // moving from chose to available publishes a deselection
				}
			}

			/**
			 * Move selected options in the "available" list to the "selected" list.
			 *
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair container.
			 */
			function addSelected(element) {
				addRemoveSelected(instance.getListByType(element, LIST_TYPE_AVAILABLE));
			}

			/**
			 * Move selected options in the "selected" list to the "available" list.
			 *
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair container.
			 */
			function removeSelected(element) {
				addRemoveSelected(instance.getListByType(element, LIST_TYPE_CHOSEN));
			}

			/**
			 * Helper for {@link addAll} and {@link removeAll} which actual does the option move.
			 *
			 * @function
			 * @private
			 * @param {Element} selectList The list from which we are moving options.
			 * @param {Function} action The function to apply to the options ({@link module:wc/ui/multiSelectPair~addSelected}
			 * or {@link module:wc/ui/multiSelectPair~removeSelected}).
			 */
			function actionAllOptions(selectList, action) {
				var i;
				for (i = 0; i < selectList.options.length; i++) {
					if (!shed.isSelected(selectList.options[i])) {
						shed.select(selectList.options[i], true);  // Keep this quiet! We will publish our own shed event when done. Publishing each select would be really stupid.
					}
				}
				action(selectList);
			}

			/**
			 * Add all options to the "selected" list.
			 *
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair.
			 */
			function addAll(element) {
				var availableBucket = instance.getListByType(element, LIST_TYPE_AVAILABLE);
				actionAllOptions(availableBucket, addSelected);
			}

			/**
			 * Remove all options from the "selected" list.
			 *
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair.
			 */
			function removeAll(element) {
				var selectedBucket = instance.getListByType(element, LIST_TYPE_CHOSEN);
				actionAllOptions(selectedBucket, removeSelected);
			}


			/**
			 * Writes the state of the MultiSelectPair. All options in the "selected" list are deemed to be selected
			 * even when they are not selected in the DOM. Equally no options in the "available" list are selected
			 * irrespective of their actual selected state.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form or sub-form which is having its state written.
			 * @param {Element} stateContainer The container into which state is written.
			 */
			function writeState(form, stateContainer) {
				Array.prototype.forEach.call(CONTAINER.findDescendants(form), function (container) {
					var selectedOptions = instance.getValue(container),
						i,
						len;
					for (i = 0, len = selectedOptions.length; i < len; i++) {
						formUpdateManager.writeStateField(stateContainer, container.id, selectedOptions[i].value);
					}
				});
			}

			/**
			 * Keydown listener. Enter key adds/removes options and Left and Right Arrow keys switch between from and to
			 * selects.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var selectList,
					keyCode,
					selectType,
					focusOpposite,
					opposite;
				if ($event.defaultPrevented) {
					return;
				}

				if (!(selectList = SELECT.findAncestor($event.target))) {
					return;
				}

				keyCode = $event.keyCode;

				if (keyCode === KeyEvent.DOM_VK_RETURN) {
					$event.preventDefault();  // chrome submits form for "enter" in select multiple="multiple"
					addRemoveSelected(selectList);
				}
				else {
					selectType = instance.getListType(selectList);
					focusOpposite = false;
					if ((keyCode === KeyEvent.DOM_VK_RIGHT && selectType === LIST_TYPE_AVAILABLE) || (keyCode === KeyEvent.DOM_VK_LEFT && selectType === LIST_TYPE_CHOSEN)) {
						focusOpposite = true;
					}

					if (!focusOpposite) {
						return;
					}

					if ((opposite = instance.getListByType(selectList, getOppositeListType(selectList)))) {
						selectList.selectedIndex = -1;
						try {
							focus.setFocusRequest(opposite);
						}
						catch (ignore) {
							// Do nothing
						}
					}
				}
			}

			/**
			 * Focus listener to set up events on individual components.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focus/focusin event.
			 */
			function focusEvent($event) {
				var container = CONTAINER.findAncestor($event.target);
				if (container && !attribute.get(container, CONTAINER_INITIALISED_KEY)) {
					attribute.set(container, CONTAINER_INITIALISED_KEY, true);
					event.add(container, event.TYPE.keydown, keydownEvent);
				}
			}

			/**
			 * Click listener to move options from one list to the other.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var element = $event.target, action;
				if (!$event.defaultPrevented && !shed.isDisabled(element) && (action = getAction(element))) {
					action(element);
				}
			}

			/**
			 * Double-click listener to move options from one list to the other.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The dblclick event.
			 */
			function dblClickEvent($event) {
				var selectList,
					element;
				if ($event.defaultPrevented) {
					return;
				}
				element = $event.target;

				if ((element.tagName === tag.OPTION || element.tagName === tag.SELECT) && (selectList = SELECT.findAncestor(element)) && !shed.isDisabled(selectList)) {
					addRemoveSelected(selectList);
					if (ajaxRegion.getTrigger(selectList)) {
						ajaxRegion.requestLoad(selectList);
					}
				}
			}

			/**
			 * Get the list type for a given select list.
			 *
			 * @function module:wc/ui/multiSelectPair.getListType
			 * @public
			 * @param {Element} element Any select element component of a WMultiSelectPair.
			 * @returns {?int} The type of the element as defined in LISTS or null.
			 */
			this.getListType = function(element) {
				var list;
				if (SELECT.isOneOfMe(element)) {
					for (list = 0; list < LISTS.length; list++) {
						if (LISTS[list].isOneOfMe(element)) {
							return list;
						}
					}
				}
				return null;
			};

			/**
			 * Get the descriptor for a WMultiSelectPair.
			 *
			 * @function module:wc/ui/multiSelectPair.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget} the WMultiSelectPair container's {@link module:wc/dom/Widget}.
			 */
			this.getWidget = function() {
				return CONTAINER;
			};

			/**
			 * Get the descriptor for a WMultiSelectPair's input component.
			 *
			 * @function module:wc/ui/multiSelectPair.getInputWidget
			 * @public
			 * @returns {module:wc/dom/Widget} the WMultiSelectPair inpurs's {@link module:wc/dom/Widget}.
			 */
			this.getInputWidget = function() {
				return SELECT;
			};

			/** @var {module:wc/dom/Widget} module:wc/ui/multiSelectPair.LIST_TYPE_CHOSEN The descriptor for the "selected options" list. */
			this.LIST_TYPE_CHOSEN = LIST_TYPE_CHOSEN;

			/**
			 * @var {module:wc/dom/Widget} module:wc/ui/multiSelectPair.LIST_TYPE_AVAILABLE The descriptor for the "available options" list. */
			this.LIST_TYPE_AVAILABLE = LIST_TYPE_AVAILABLE;

			/**
			 * Gets the available, selected or oder list for a given WMultiSelectPair based on the type argument.
			 *
			 * @function module:wc/ui/multiSelectPair.getListByType
			 * @public
			 * @param {Element} element Any component element of a multiSelectPair (ie any of the lists or buttons).
			 * @param {String} type One of the types defined in LISTS.
			 * @returns {?Element} The list of the type represented by the type argument.
			 */
			this.getListByType = function(element, type) {
				var result = null,
					container = CONTAINER.findAncestor(element),
					list;
				if (container && (list = LISTS[type])) {
					result = list.findDescendant(container);
				}
				return result;
			};

			/**
			 * Get the selected options - these may not actually be marked as selected in the DOM, but they are in the
			 * selected bucket so they are logically selected.
			 *
			 * @function module:wc/ui/multiSelectPair.getValue
			 * @public
			 * @param {Element} container A multiSelectPair container.
			 * @returns {(NodeList|Array)} The logically selected options in this multiSelectPair. Returns an empty
			 *    Array if no options are selected.
			 */
			this.getValue = function(container) {
				var selectedBucket, result;
				if (!shed.isDisabled(container)) {
					selectedBucket = this.getListByType(container, LIST_TYPE_CHOSEN);
					if (selectedBucket) {
						result = selectedBucket.options;
					}
				}
				return result || [];
			};

			/**
			 * Indicates that the element is a multiSelectPair (which, for the purposes of this call is the top level
			 * container)
			 *
			 * @function module:wc/ui/multiSelectPair.isOneOfMe
			 * @public
			 * @param {Element} element The DOM element to test.
			 * @returns {boolean} True if the passed in element is a multiSelectPair.
			 */
			this.isOneOfMe = function(element) {
				return CONTAINER.isOneOfMe(element);
			};

			/**
			 * Wait for page load modal shim to remove before trying to calculate initial select width and height.
			 * See https://github.com/BorderTech/wcomponents/issues/1066. This function unsubscribes itself as the only time we are interested in
			 * the removal of a modal shim is the page load shim, not the shim associated with a WDialog or image editor.
			 *
			 * @function
			 * @private
			 */
			function modalSubscriber() {
				try {
					fixWidthHeight();
				}
				finally {
					modal.unsubscribe(modalSubscriber);
				}
			}

			/**
			 * Set up initial event handlers.
			 *
			 * @function module:wc/ui/multiSelectPair.initialise
			 * @public
			 * @param {Element} element The element being initialised: usually document.body
			 */
			this.initialise = function(element) {
				modal.subscribe(modalSubscriber);
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}

				event.add(element, event.TYPE.click, clickEvent);
				event.add(element, event.TYPE.dblclick, dblClickEvent);
			};

			/**
			 * Late set up to wire up subscribers after initialisation.
			 *
			 * @function module:wc/ui/multiSelectPair.postInit
			 * @public
			 */
			this.postInit = function () {
				shed.subscribe(shed.actions.SHOW, fixWidthHeight);
				processResponse.subscribe(fixWidthHeight, true);
				formUpdateManager.subscribe(writeState);
			};

			/** Public for testing  @ignore */
			this._keydownEvent = keydownEvent;
		}

		/**
		 * Provides functionality for WMultiSelectPair which is a side-by-side multi-selection list control.
		 *
		 * @module
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/formUpdateManager
		 * @requires module:wc/dom/getBox
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/tag
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/ui/ajaxRegion
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/ui/selectboxSearch
		 */
		var instance = new MultiSelectPair();
		initialise.register(instance);
		return instance;
	});
