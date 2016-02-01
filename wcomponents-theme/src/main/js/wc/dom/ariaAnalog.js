/**
 * Aria Analog is a reuse class that provides functions other classes can "borrow" to implement ARIA roles.
 *
 * There are two aspects to implementing an ARIA role:
 *
 * * Managing focus (keyboard navigation - left/right/up/down etc)
 * * Activation / Selection (click, spacebar, enter etc)
 * * State writing (tell the server the state of the aria control)
 *
 * A few points to note:
 *
 * * Event listeners are called in the scope of the object. In other words the "this" in an event listener will
 *   not reference event.currentTarget like it normally does, it will reference the "this" as if it was just a regular
 *   function, not an event listener.
 * * If you override any event handlers it's up to YOU to ensure you honor the above contract.
 *
 * **ACHTUNG! WARNING! ATTENTION! POZOR!**
 *
 * This is an "abstract class". That means it is not a complete implementation, subclasses are required to
 * implement certain properties / methods. The absolute minimum is  {@link module:wc/dom/Widget} ITEM.
 *
 * @module
 * @requires module:wc/has
 * @requires module:wc/dom/clearSelection
 * @requires module:wc/dom/event
 * @requires module:wc/dom/group
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/Widget
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/keyWalker
 * @requires module:wc/dom/isEventInLabel
 * @requires module:wc/dom/isAcceptableTarget
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/focus
 */
define(["wc/has",
		"wc/dom/clearSelection",
		"wc/dom/event",
		"wc/dom/group",
		"wc/dom/shed",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/array/toArray",
		"wc/dom/formUpdateManager",
		"wc/dom/keyWalker",
		"wc/dom/isEventInLabel",
		"wc/dom/isAcceptableTarget"],
	/** @param has wc/has @param clearSelection wc/dom/clearSelection @param event wc/dom/event @param group wc/dom/group @param shed wc/dom/shed @param uid wc/dom/uid @param Widget wc/dom/Widget @param toArray wc/array/toArray @param formUpdateManager wc/dom/formUpdateManager @param keyWalker wc/dom/keyWalker @param isEventInLabel wc/dom/isEventInLabel @param isAcceptableEventTarget wc/dom/isAcceptableTarget @ignore */
	function(has, clearSelection, event, group, shed, uid, Widget, toArray, formUpdateManager, keyWalker, isEventInLabel, isAcceptableEventTarget) {
		"use strict";

		var /** @alias module:wc/dom/ariaAnalog */ariaAnalog,
			getFilteredGroup,
			focus,
			genericAnalog,
			gridWidgets,
			keyWalkerConfig;  // we only need one keywalker for all group based walking with aria-analogs

		/* circular dependencies */
		require(["wc/dom/getFilteredGroup", "wc/dom/focus"], function($getFilteredGroup, $focus) {
			getFilteredGroup = $getFilteredGroup;
			focus = $focus;
		});

		/**
		 * Deselect all elements in a group except any defined by the arg except.
		 *
		 * @function deselect
		 * @private
		 * @param {NodeList} _group The group of elements which define an instance of an ARIA-analog.
		 * @param {?Element} except The element we do not want to deselect: usually the just-selected element.
		 * @param {?Element} container The element which contains a group.
		 * @param {Object} inst The instance of a subclass of AriaAnalog.
		 */
		function deselect(_group, except, container, inst) {
			var i,
				next,
				silent = true,
				doneException = false,
				_container;
			for (i = _group.length - 1; i >= 0; i--) {
				next = _group[i];
				if (i === 0 || (i === 1 && !doneException)) {
					silent = false;
				}
				if (next !== except) {
					if (inst.selectionIsImmediate) {
						_container = container || group.getContainer(except, inst.CONTAINER);
						if (_container === group.getContainer(next, inst.CONTAINER)) {
							shed.deselect(next, silent);
						}
					}
					else {
						shed.deselect(next, silent);
					}
				}
				else {
					doneException = true;
				}
			}
		}

		/**
		 * The eventWrapper allows late binding of event listeners to events so that subclasses can override event
		 * listeners if they really want to. If we didn't use this mechanism then the superclass events would always be
		 * called even if they were overridden.
		 *
		 * @function eventWrapper
		 * @private
		 * @param {Event} $event The event to be wrapped.
		 */
		function eventWrapper($event) {
			var result,  // return undefined by default;
				type = $event.type,
				methodName = type.toLowerCase(),
				handler;
			if (methodName === "focusout") {
				methodName = "blur";
			}
			else if (methodName === "focusin") {
				methodName = "focus";
			}
			handler = this[methodName + "Event"];
			if (handler) {
				// there's a handler for this event so pass the call through
				result = handler.call(this, $event);
			}
			return result;
		}

		/**
		 * Get the group which a particular element belongs to. A wrapper for {@link module:wc/dom/group#getGroup} and
		 * {@link module:wc/dom/group#get}.
		 *
		 * @function getGroup
		 * @private
		 * @param {Element} element The element in a group
		 * @param {Object} analog An instance of a subclass of AriaAnalog.
		 * @returns {?Element[]} The group of items in the element's ARIA analog group.
		 */
		function getGroup(element, analog) {
			var result;
			if (analog.CONTAINER) {
				result = group.getGroup(element, analog.ITEM, analog.CONTAINER);
			}
			else {
				result = group.get(element);
			}
			return result;
		}

		/**
		 * Filter a group of elements to exclude all those which are disabled or hidden.
		 *
		 * @function filterGroup
		 * @private
		 * @param {Element[]} _group
		 * @returns {Element[]} The filtered group.
		 */
		function filterGroup(_group) {
			return _group.filter(function(next) {
				var result = true;
				if (shed.isHidden(next) || shed.isDisabled(next)) {
					result = false;
				}
				return result;
			});
		}

		/**
		 * @alias module:wc/dom/ariaAnalog~AriaAnalog
		 * @constructor
		 * @private
		 */
		function AriaAnalog() { }

		/**
		 * Provides all possible selection modes: multiple, single, mixed.
		 * Keys are MULTIPLE, SINGLE and MIXED.
		 *
		 * @var
		 * @protected
		 * @type Object
		 * @property {int} MULTIPLE Instance supports multiple selection.
		 * @property {int} SINGLE Instance supports single selection.
		 * @property {int} MIXED Instance supports either single multiple selection as determined by its aria-multiselectable property.
		 */
		AriaAnalog.prototype.SELECT_MODE = {
			MULTIPLE: 0,
			SINGLE: 1,
			MIXED: 2
		};

		/**
		 * Provides all possible directions for group-based key navigation. This navigation paradigm is only suitable
		 * for simple linear navigation. Keys are PREVIOUS, NEXT, FIRST and LAST.
		 *
		 * @var
		 * @type Object
		 * @property {int} PREVIOUS Move to the previous item.
		 * @property {int} NEXT Move to the next item.
		 * @property {int} FIRST Move to the first item.
		 * @property {int} LAST Move to the last item.
		 * @protected
		 */
		AriaAnalog.prototype.KEY_DIRECTION = {
			PREVIOUS: 1,
			NEXT: 2,
			FIRST: 4,
			LAST: 8
		};

		/**
		 * Indicates that keyboard navigation should cycle at the limits of a group/sibling group.
		 *
		 * @var
		 * @protected
		 * @type Boolean
		 */
		AriaAnalog.prototype._cycle = false;

		/**
		 * An array of Widgets which describe 'actionable' items which is used to prevent default action on some key
		 * presses and not others depending upon the target element. This is set once per sub-class during
		 * initialisation.
		 *
		 * @var
		 * @type {?Array}
		 * @protected
		 */
		AriaAnalog.prototype.actionable = null;

		/**
		 * Indicates whether navigating with the keyboard selects items.
		 *
		 * @var
		 * @type Boolean
		 * @default false
		 * @protected
		 */
		AriaAnalog.prototype.selectOnNavigate = false;

		/**
		 * Indicates whether  only one item can be selected at a time. Must be a
		 * value of AriaAnalog.prototype.SELECT_MODE.
		 *
		 * @var
		 * @type Integer
		 * @default 0
		 * @protected
		 */
		AriaAnalog.prototype.exclusiveSelect = AriaAnalog.prototype.SELECT_MODE.MULTIPLE;

		/**
		 * Indicates that the key navigation method uses a DOM grouping. Group navigation is efficient but insufficient
		 * for some complex groups (such as menu items).
		 *
		 * @var
		 * @type Boolean
		 * @default true
		 * @protected
		 */
		AriaAnalog.prototype.groupNavigation = true;

		/**
		 * This property is used to get the start point element for SHIFT+activate and should only be initialised for
		 * sub classes which support multiple selection.
		 *
		 * @var
		 * @type {?Object}
		 * @default null
		 * @protected
		 */
		AriaAnalog.prototype.lastActivated = null;

		/**
		 * This property indicates that a particular type of selectable thing can have its selection removed if the ctrl
		 * key is held down whilst selecting. This only applies to single selection since multi-selectable items can
		 * always be deselected. In particular it is currently implemented for treeitem and listbox.
		 * @var
		 * @type Boolean
		 * @default false
		 * @protected
		 */
		AriaAnalog.prototype.ctrlAllowsDeselect = false;

		/**
		 * This property indicates that a particular type of mixed-mode multi selectable thing works like a check box
		 * rather than an option. This is currently only implemented in row.
		 * @var
		 * @type Boolean
		 * @default false
		 * @protected
		 */
		AriaAnalog.prototype.simpleSelection = false;

		/**
		 * Allow subclasses to add extended initialisation.
		 *
		 * @var
		 * @type {?Function}
		 * @protected
		 */
		AriaAnalog.prototype._extendedInitialisation = null;

		/**
		 * Helper for getting a group container from a member of a group.
		 *
		 * @function
		 * @public
		 * @param {Element} element The group member we are using to derive the group container
		 * @returns {?Element} The element which defines a group by containment (such as a fieldset).
		 */
		AriaAnalog.prototype.getGroupContainer = function(element) {
			return group.getContainer(element, this.CONTAINER);
		};

		/**
		 * Subscriber to module:wc/dom/shed to act on shed events SELECT and DESELECT. This will selectively deselect
		 * other items in the group if the group selection mode ({@link module:wc/dom/ariaAnalog#exclusiveSelect}) is
		 * single or mixed a container element and that element does not have attribute aria-multiselectable = "true".
		 *
		 * @function
		 * @public
		 * @param {Element} element The element SHED is acting on.
		 * @param {String} action The select or deselect action.
		 */
		AriaAnalog.prototype.shedObserver = function(element, action) {
			var _group, container, deselectOthers = false, config;
			if (action === shed.actions.SELECT && this.ITEM.isOneOfMe(element)) {
				if (this.exclusiveSelect === this.SELECT_MODE.SINGLE) {
					deselectOthers = true;
				}
				else if (this.exclusiveSelect === this.SELECT_MODE.MIXED) {
					if ((container = this.getGroupContainer(element))) {
						if (container.getAttribute("aria-multiselectable") !== "true") {
							deselectOthers = true;
						}
					}
				}
				if (deselectOthers) {
					if (this.CONTAINER) {
						config = {"itemWd": this.ITEM, "containerWd": this.CONTAINER};
					}
					if ((_group = getFilteredGroup(element, config)) && _group.length) {
						deselect(_group, element, container, this);
					}
				}
			}
		};

		/**
		 * Initialise the subclass. A subscriber to {@link module:wc/dom/initialise}.
		 * You should not override this method. If JS allowed a way of declaring a method as final we would us that
		 * here. If you do override it you are responsible for calling it from the subclass, perhaps like this:
		 * this.constructor.prototype.initialise.call(this, element);
		 *
		 * @function
		 * @public
		 * @param {Element} element The element being initialised. Usually document.body.
		 */
		AriaAnalog.prototype.initialise = function(element) {
			// Do some deferred setup...
			if (this.ITEM) {
				this.TAB_ITEM = this.ITEM.extend("", {"tabindex": "0"});
			}
			this.actionable = [new Widget("button"), new Widget("a")];  // do not fire spacebar event listener or prevent default on these
			genericAnalog = new Widget("", "", {"role": null});

			if (event.canCapture) {
				event.add(element, event.TYPE.focus, eventWrapper.bind(this), null, null, true);
			}
			else {
				event.add(element, event.TYPE.focusin, eventWrapper.bind(this));
			}
			event.add(element, event.TYPE.click, eventWrapper.bind(this));
			event.add(element, event.TYPE.keydown, eventWrapper.bind(this));
			shed.subscribe(shed.actions.SELECT, this.shedObserver.bind(this));
			shed.subscribe(shed.actions.DESELECT, this.shedObserver.bind(this));

			if (this._extendedInitialisation) {
				this._extendedInitialisation(element);
			}
			formUpdateManager.subscribe(this);
		};

		/**
		 * Write the state of the ARIA analog component into any form submission or required AJAX request.
		 *
		 * @function
		 * @protected
		 * @param {Element} form The form or form segment whosr state is being written.
		 * @param {ELement} container The container for writing the state fields.
		 * @todo Anonymize the inner function.
		 */
		AriaAnalog.prototype.writeState = function(form, container) {
			var selectedItems,
				items = this.ITEM.findDescendants(form);

			if (items.length) {
				selectedItems = getFilteredGroup(toArray(items));
				selectedItems.forEach(function (next) {
					if (next.hasAttribute("data-wc-value") && !shed.isDisabled(next)) {
						formUpdateManager.writeStateField(container, next.getAttribute("data-wc-name"), next.getAttribute("data-wc-value"));
					}
				});
			}
		};

		/**
		 * Focus event listener to manage tab index on simple linear groups. Note though that components which do their
		 * own navigation are also responsible for maintaining their own tab indices.
		 * @function
		 * @protected
		 * @param {Event} $event The focus event.
		 */
		AriaAnalog.prototype.focusEvent = function($event) {
			var element = $event.target;
			if (!$event.defaultPrevented && this.groupNavigation && this.ITEM.isOneOfMe(element) && !shed.isDisabled(element)) {
				if (this.setFocusIndex && !(has("event-ontouchstart"))) {
					this.setFocusIndex(element);
				}
			}
		};

		/**
		 * Click event listener to activate a group member on click (assuming it is acceptable); for example, radio
		 * buttons get selected on click but not if the click is on something else with a tabStop.
		 * @function
		 * @protected
		 * @param {Event} $event The click event.
		 */
		AriaAnalog.prototype.clickEvent = function($event) {
			var target = $event.target, element;
			if (!$event.defaultPrevented && (element = this.getActivableFromTarget(target)) && !shed.isDisabled(element) && isAcceptableEventTarget(element, target)) {
				this.activate(element, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
			}
		};

		/**
		 * Keydown event listener to navigate between items or activate on SPACE where supported.
		 *
		 * @function
		 * @protected
		 * @param {Event} $event The keydown event.
		 */
		AriaAnalog.prototype.keydownEvent = function($event) {
			var element, keyCode = $event.keyCode, target = $event.target, moveTo, preventDefaultAction = false;
			if (!$event.defaultPrevented && !$event.altKey && (element = this.getActivableFromTarget(target))) {
				if (this.groupNavigation && isDirectionKey(keyCode)) {
					moveTo = calcMoveTo(this, keyCode);
					if (moveTo && (target = this.navigate(element, moveTo))) {
						if (this.selectOnNavigate && !($event.ctrlKey || $event.metaKey)) {
							this.activate(target, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
						}
						preventDefaultAction = true;
					}
				}
				else if ((keyCode === KeyEvent.DOM_VK_SPACE || keyCode === KeyEvent.DOM_VK_RETURN) && !Widget.isOneOfMe(element, this.actionable)) {
					if (isAcceptableEventTarget(element, target)) {
						this.activate(element, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
						if (keyCode === KeyEvent.DOM_VK_SPACE) {
							preventDefaultAction = true;  // preventDefault here otherwise you get a page scroll
						}
					}
				}
				if (preventDefaultAction) {
					$event.preventDefault();
				}
			}
		};

		/**
		 * Helper for keydownEvent.
		 * Determine if the user has pressed an arrow key or similar.
		 * @param {Number} keyCode The key pressed.
		 * @returns {boolean} true if it's a direction key
		 * @function
		 * @private
		 */
		function isDirectionKey(keyCode) {
			return (keyCode === KeyEvent.DOM_VK_HOME || keyCode === KeyEvent.DOM_VK_END ||
					keyCode >= KeyEvent.DOM_VK_LEFT && keyCode <= KeyEvent.DOM_VK_DOWN);
		}

		/**
		 * Helper for keydownEvent.
		 * Calculates where to move based on the key pressed by the user.
		 * @param {AriaAnalog} instance The AriaAnalog controller.
		 * @param {Number} keyCode The key pressed.
		 * @returns {instance.KEY_DIRECTION.NEXT|instance.KEY_DIRECTION.LAST|instance.KEY_DIRECTION.FIRST|instance.KEY_DIRECTION.PREVIOUS}
		 * @function
		 * @private
		 */
		function calcMoveTo(instance, keyCode) {
			var moveTo;
			switch (keyCode) {
				case KeyEvent.DOM_VK_HOME:
					moveTo = instance.KEY_DIRECTION.FIRST;
					break;
				case KeyEvent.DOM_VK_END:
					moveTo = instance.KEY_DIRECTION.LAST;
					break;
				case KeyEvent.DOM_VK_LEFT:
				case KeyEvent.DOM_VK_UP:
					moveTo = instance.KEY_DIRECTION.PREVIOUS;
					break;
				case KeyEvent.DOM_VK_RIGHT:
				case KeyEvent.DOM_VK_DOWN:
					moveTo = instance.KEY_DIRECTION.NEXT;
					break;
			}
			return moveTo;
		}

		/**
		 * key navigation for simple linear groups.
		 *
		 * @function
		 * @protected
		 * @param {Element} start Start element
		 * @param {number} direction -1 to previous in group, 1 to next in group NOTE: radio button groups allow native
		 *    group cycling at the extremities so we allow that here too. Only useful if one of
		 *    {@link module:wc/dom/ariaAnalog~AriaAnalog#KEY_DIRECTION}
		 * @returns {?Element} The end point of the navigation. If start is part of a navigable group but there is
		 *    nowhere to go then we may return the start element.
		 */
		AriaAnalog.prototype.navigate = function(start, direction) {
			var target,
				kwDirection,
				result,
				_group = getGroup(start, this);

			if (_group && (_group = filterGroup(_group)) && _group.length > 1) {  // no point navigating if only 1 option
				if (keyWalkerConfig) {
					keyWalkerConfig["root"] = _group;
				}
				else {
					keyWalkerConfig = {
						root: _group,
						filter: function(el) {
							/* the group filter EXCLUDES elements return true*/
							var result = NodeFilter.FILTER_ACCEPT;
							// return shed.isDisabled(el) || shed.isHidden(el);
							if (shed.isDisabled(el) || shed.isHidden(el)) {
								result = NodeFilter.FILTER_REJECT;
							}
							return result;
						}
					};
				}
				keyWalkerConfig[keyWalker.OPTIONS.CYCLE] = this._cycle;

				switch (direction) {
					case this.KEY_DIRECTION.PREVIOUS:
						kwDirection = keyWalker.MOVE_TO.PREVIOUS;
						break;
					case this.KEY_DIRECTION.NEXT:
						kwDirection = keyWalker.MOVE_TO.NEXT;
						break;
					case this.KEY_DIRECTION.FIRST:
						kwDirection = keyWalker.MOVE_TO.FIRST;
						break;
					case this.KEY_DIRECTION.LAST:
						kwDirection = keyWalker.MOVE_TO.LAST;
						break;
				}

				target = keyWalker.getTarget(keyWalkerConfig, start, kwDirection);

				if (target && target !== start) {
					focus.setFocusRequest(target);
					result = target;
				}
			}
			return result;
		};

		/**
		 * Activate the element, that is SELECT or DESELECT it.
		 *
		 * @function
		 * @public
		 * @param {Element} element the element being directly activated. This should never be called on components
		 *    which are natively selectable such as radios, checkboxes or options.
		 * @param {Boolean} [SHIFT] Indicates the SHIFT key was held during the event which lead to activation.
		 * @param {Boolean} [CTRL] The event was accompanied by ctrlKey or metaKey.
		 */
		AriaAnalog.prototype.activate = function(element, SHIFT, CTRL) {
			var container,
				lastActivated,
				selectMode,
				isMultiSelect,
				setLastActivated = true;

			try {
				container = this.getGroupContainer(element);
				isMultiSelect = container ? container.getAttribute("aria-multiselectable") : false;
				if (this.exclusiveSelect === this.SELECT_MODE.MIXED && isMultiSelect === "true") {
					selectMode = this.exclusiveSelect;
					if (this.simpleSelection || SHIFT || CTRL) {
						this.exclusiveSelect = this.SELECT_MODE.MULTIPLE;
					}
					else {
						this.exclusiveSelect = this.SELECT_MODE.SINGLE;
					}
				}

				if (this.exclusiveSelect === this.SELECT_MODE.SINGLE || ((this.exclusiveSelect === this.SELECT_MODE.MIXED && isMultiSelect !== "true"))) {
					if (this.simpleSelection || (CTRL && this.ctrlAllowsDeselect)) {
						shed.toggle(element, shed.actions.SELECT);
					}
					else {
						shed.select(element, shed.isSelected(element)); // do not publish a failed deselect.
					}
				}
				else if (SHIFT && container) {
					if (this.lastActivated && this.lastActivated[container.id]) {
						lastActivated = document.getElementById(this.lastActivated[container.id]);
					}
					if (lastActivated) {
						this.doGroupSelect(element, lastActivated, CTRL);
						setLastActivated = false;
					}
					else {
						shed.toggle(element, shed.actions.SELECT);
					}
				}
				else {
					shed.toggle(element, shed.actions.SELECT);
				}
				if (setLastActivated && this.lastActivated) {
					this.setLastActivated(element, container);
				}
			}
			finally {
				if (selectMode !== undefined) {
					this.exclusiveSelect = selectMode;
				}
			}
		};

		/**
		 * SHIFT + ACTIVATE helper: gets the group the element is in and selected/deselects all items between it and the
		 * last activated item. Then, if the CTRL key was not in play and items are being selected it will deselect
		 * items outside of the group.
		 *
		 * @function
		 * @protected
		 * @param {Element} element The source element.
		 * @param {Element} [lastActivated] The last activated element in the group.
		 * @param {Booelan} [CTRL] true if the ctrl or meta key was pressed during the event which resulted in the
		 *    function being called.
		 */
		AriaAnalog.prototype.doGroupSelect = function(element, lastActivated, CTRL) {
			var selectedFilter,
				groupAction,
				filtered,
				unfiltered,
				i,
				next,
				start,
				end,
				filter;

			if (shed.isSelected(element)) {
				selectedFilter = getFilteredGroup.FILTERS.selected;
				groupAction = shed.deselect;
			}
			else {
				selectedFilter = getFilteredGroup.FILTERS.deselected;
				groupAction = shed.select;
			}
			filter = getFilteredGroup.FILTERS.visible | getFilteredGroup.FILTERS.enabled;
			filtered = getFilteredGroup(element, {filter: (filter | selectedFilter), containerWd: this.CONTAINER, itemWd: (this.CONTAINER ? this.ITEM : null)});
			unfiltered = getFilteredGroup(element, {filter: filter, containerWd: this.CONTAINER, itemWd: (this.CONTAINER ? this.ITEM : null)});

			if (filtered && filtered.length) {
				start = Math.min(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));
				end = Math.max(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));
				for (i = 0; i < unfiltered.length; ++i) {
					next = unfiltered[i];
					if (start <= unfiltered.indexOf(next) && end >= unfiltered.indexOf(next)) {
						if (~filtered.indexOf(next)) {
							groupAction(next);
						}
					}
					else if (!CTRL && selectedFilter === getFilteredGroup.FILTERS.deselected && next !== lastActivated && shed.isSelected(next)) {
						shed.deselect(next);
					}
				}
			}
			clearSelection();
		};

		/**
		 * Set the last activated element in the group.
		 *
		 * @function
		 * @protected
		 * @param {Element} element The last activated element.
		 * @param {Element} [container] The group container (if known).
		 */
		AriaAnalog.prototype.setLastActivated = function (element, container) {
			var containerId,
				elementId;
			if ((container = container || this.getGroupContainer(element)) && this.lastActivated) {
				containerId = container.id || (container.id = uid());
				elementId = element.id || (element.id = uid());
				this.lastActivated[containerId] = elementId;
			}
		};

		/**
		 * determines if the "this" item found in an event listener is the first aria analog. This is a rudimentary
		 * check to overcome the issue of all aria analogs listening for the same events with an ancestor lookup to
		 * determine if they are the target. This results in multiple analogs responding if nested.
		 *
		 * @function
		 * @protected
		 * @param {Element} target The event target
		 * @param {Element} item The element found using this.ITEM.
		 * @returns {Boolean} true if the item is the first active analog found in the ancestor tree.
		 */
		AriaAnalog.prototype.isActiveAnalog = function(target, item) {
			var firstAnalog, gridContainer,
				IGNORE_ROLES = ["presentation", "banner", "application",
					"alert", "tablist", "tabpanel",
					"group", "heading", "rowheading", "columnheading",
					"separator"], skip = false;

			firstAnalog = genericAnalog.findAncestor(target);
			if (firstAnalog === null || firstAnalog === item) {
				return true;
			}

			while (firstAnalog && firstAnalog.parentNode) {
				skip = false;
				if (IGNORE_ROLES.indexOf(firstAnalog.getAttribute("role")) > -1 || firstAnalog.getAttribute("aria-readonly") === "true" || shed.isDisabled(firstAnalog)) {
					skip = true;
				}

				// ignore role gridcell if the nearest containing grid/treegid is aria-readonly as this state is inherited.
				gridWidgets = gridWidgets || [new Widget("","",{"role": "grid"}), new Widget("","",{"role": "treegrid"})];
				if (firstAnalog.getAttribute("role") === "gridcell" && (gridContainer = Widget.findAncestor(firstAnalog, gridWidgets)) && gridContainer.getAttribute("aria-readonly") === "true") {
					skip = true;
				}

				if (skip) {
					firstAnalog = genericAnalog.findAncestor(firstAnalog.parentNode);
					continue;
				}
				break;
			}
			return (firstAnalog === null || firstAnalog === item);
		};

		/**
		 * When we change the selected item in a group we set the tabIndex otherwise tabbing into the group may not be
		 * possible or may result in the wrong element receiving focus.
		 *
		 * @function
		 * @protected
		 * @param {Element} element The element to receive future focus.
		 */
		AriaAnalog.prototype.setFocusIndex = function(element) {
			var _group = getGroup(element, this);

			if (_group && _group.length > 1) {
				_group.forEach(function(next) {
					next.tabIndex = "-1";
				});
				element.tabIndex = "0";
			}
		};

		/**
		 * Gets the Widget which describes the active component.
		 *
		 * @function
		 * @public
		 * @returns {Widget}
		 */
		AriaAnalog.prototype.getWidget = function() {
			return this.ITEM;
		};

		/**
		 * Determine if an event target is inside an ariaAnalog and if so if that analog is able to be activated. If
		 * this is the case return the analog ITEM element.
		 *
		 * @function
		 * @public
		 * @param {Element} target The element which was the target of an event.
		 * @returns {?Element} The activable aria analog ancestor of target.
		 */
		AriaAnalog.prototype.getActivableFromTarget = function(target) {
			var element;
			if (!isEventInLabel(target)) {
				element = this.ITEM.findAncestor(target);
				if (element && (shed.isDisabled(element) || !this.isActiveAnalog(target, element))) {
					element = null;
				}
			}
			return element;
		};


		ariaAnalog = new AriaAnalog();
		if (typeof Object.freeze !== "undefined") {
			Object.freeze(ariaAnalog);  // freeze, cos this is shared as the proto for many different constructors
		}

		return ariaAnalog;
	});
