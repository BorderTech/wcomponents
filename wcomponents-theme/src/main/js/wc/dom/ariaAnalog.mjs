/**
 * Aria Analog is a reuse class that provides functions other classes can "borrow" to implement ARIA roles.
 *
 * There are two aspects to implementing an ARIA role:
 *
 * * Managing focus (keyboard navigation - left/right/up/down etc.)
 * * Activation / Selection (click, spacebar, enter etc.)
 * * State writing (tell the server the state of the aria control)
 *
 * A few points to note:
 *
 * * Event listeners are called in the scope of the object. In other words the "this" in an event listener will
 *   not reference event.currentTarget like it normally does, it will reference the "this" as if it was just a regular
 *   function, not an event listener.
 *
 * * If you override any event handlers it's up to YOU to ensure you honor the above contract.
 *
 * This is an "abstract class". That means it is not a complete implementation, subclasses are required to
 * implement certain properties / methods. The absolute minimum is ITEM.
 *
 */

import clearSelection from "wc/dom/clearSelection.mjs";
import event from "wc/dom/event.mjs";
import group from "wc/dom/group.mjs";
import shed from "wc/dom/shed.mjs";
import uid from "wc/dom/uid.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import keyWalker from "wc/dom/keyWalker.mjs";
import isEventInLabel from "wc/dom/isEventInLabel.mjs";
import isAcceptableEventTarget from "wc/dom/isAcceptableTarget.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";  // circular dep
import focus from "wc/dom/focus.mjs";  // circular dep

const genericAnalogSelector = "[role]";
const gridSelectors = ["[role='grid']", "[role='treegrid']"];
const IGNORE_ROLES = ["presentation", "banner", "application", "alert",
	"tablist", "tabpanel", "group", "heading", "rowheader", "separator"];
let ariaAnalog;
let keyWalkerConfig;  // we only need one keywalker for all group based walking with aria-analogs

/**
 * Helper for keydownEvent. Determine if the user has pressed an arrow key or similar.
 * @function
 * @private
 * @param {KeyboardEvent} $event The key pressed.
 * @returns {boolean} true if it's a direction key
 */
function isDirectionKey($event) {
	const keyCode = $event.key || $event.code;
	return (keyCode === "Home" || keyCode === "End" || keyCode.startsWith("Arrow"));
}

/**
 * Helper for keydownEvent.
 * Calculates where to move based on the key pressed by the user.
 * @function
 * @private
 * @param {AriaAnalog} instance The AriaAnalog controller.
 * @param {KeyboardEvent} $event The key pressed.
 * @returns {instance.KEY_DIRECTION.NEXT|instance.KEY_DIRECTION.LAST|instance.KEY_DIRECTION.FIRST|instance.KEY_DIRECTION.PREVIOUS}
 */
function calcMoveTo(instance, $event) {
	let moveTo;
	const keyCode = $event.key;
	switch (keyCode) {
		case "Home":
			moveTo = instance.KEY_DIRECTION.FIRST;
			break;
		case "End":
			moveTo = instance.KEY_DIRECTION.LAST;
			break;
		case "ArrowLeft":
		case "ArrowUp":
			moveTo = instance.KEY_DIRECTION.PREVIOUS;
			break;
		case "ArrowRight":
		case "ArrowDown":
			moveTo = instance.KEY_DIRECTION.NEXT;
			break;
	}
	return moveTo;
}

/**
 * Deselect all elements in a group except any defined by the arg except.
 *
 * @function
 * @private
 * @param {NodeList} _group The group of elements which define an instance of an ARIA-analog.
 * @param {?Element} except The element we do not want to deselect: usually the just-selected element.
 * @param {?Element} container The element which contains a group.
 * @param {Object} inst The instance of a subclass of AriaAnalog.
 */
function deselect(_group, except, container, inst) {
	for (let i = _group.length - 1; i >= 0; i--) {
		let silent = true;
		let doneException = false;
		let next = _group[i];
		if (i === 0 || (i === 1 && !doneException)) {
			silent = false;
		}
		if (next !== except) {
			if (inst.selectionIsImmediate) {
				let _container = container || group.getContainer(except, inst.CONTAINER);
				if (_container === group.getContainer(next, inst.CONTAINER)) {
					shed.deselect(next, silent);
				}
			} else {
				shed.deselect(next, silent);
			}
		} else {
			doneException = true;
		}
	}
}

/**
 * Is an analog in a read-only state?
 *
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {Boolean} true if element has attribute aria-readonly = "true".
 */
function isReadOnly(element) {
	return element.getAttribute("aria-readonly") === "true";
}

/**
 * The eventWrapper allows late binding of event listeners to events so that subclasses can override event
 * listeners if they really want to. If we didn't use this mechanism then the superclass events would always be
 * called even if they were overridden.
 *
 * @function
 * @private
 * @param {Event} $event The event to be wrapped.
 */
function eventWrapper($event) {
	let result;  // return undefined by default;
	const { type } = $event;
	const methodName = type.toLowerCase();
	const handler = this[methodName + "Event"];
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
 * @function
 * @private
 * @param {Element} element The element in a group
 * @param {Object} analog An instance of a subclass of AriaAnalog.
 * @returns {HTMLElement[]} The group of items in the element's ARIA analog group.
 */
function getGroup(element, analog) {
	let result;
	if (analog.CONTAINER) {
		result = group.getGroup(element, analog.ITEM, analog.CONTAINER);
	} else {
		result = group.get(element);
	}
	return result;
}

/**
 * Filter a group of elements to exclude all those which are disabled or hidden.
 *
 * @function
 * @private
 * @param {Element[]} _group
 * @returns {HTMLElement[]} The filtered group.
 */
function filterGroup(_group) {
	return /** @type HTMLElement[] */ (_group.filter(function(next) {
		let result = true;
		if (shed.isHidden(next) || shed.isDisabled(next)) {
			result = false;
		}
		return result;
	}));
}

/**
 * @alias module:wc/dom/ariaAnalog~AriaAnalog
 * @constructor
 */
function AriaAnalog() { }

/**
 * The attribute which holds the analog value.
 * @var
 * @type String
 */
AriaAnalog.prototype.VALUE_ATTRIB = "data-wc-value";

/**
 * Provides all possible selection modes: multiple, single, mixed.
 * Keys are MULTIPLE, SINGLE and MIXED.
 *
 * @var
 * @type Object
 * @property {number} MULTIPLE Instance supports multiple selection.
 * @property {number} SINGLE Instance supports single selection.
 * @property {number} MIXED Instance supports either single multiple selection as determined by its aria-multiselectable property.
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
 * @property {number} PREVIOUS Move to the previous item.
 * @property {number} NEXT Move to the next item.
 * @property {number} FIRST Move to the first item.
 * @property {number} LAST Move to the last item.
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
 */
AriaAnalog.prototype.actionable = null;

/**
 * Indicates whether navigating with the keyboard selects items.
 *
 * @function
 * @param {Element} element The element being navigated to. Not used by default but needed in sub-classes.
 */
AriaAnalog.prototype.selectOnNavigate = function (element) {
	if (!element) {
		throw new TypeError("Argument must not be null");
	}
	return false;
};

/**
 * Indicates whether  only one item can be selected at a time. Must be a
 * value of AriaAnalog.prototype.SELECT_MODE.
 *
 * @var
 * @type number
 * @default 0
 */
AriaAnalog.prototype.exclusiveSelect = AriaAnalog.prototype.SELECT_MODE.MULTIPLE;

/**
 * Indicates that the key navigation method uses a DOM grouping. Group navigation is efficient but insufficient
 * for some complex groups (such as menu items).
 *
 * @var
 * @type Boolean
 * @default true
 */
AriaAnalog.prototype.groupNavigation = true;

/**
 * This property is used to get the start point element for SHIFT+activate and should only be initialised for
 * subclasses which support multiple selection.
 *
 * @var
 * @type {?Object}
 * @default null
 */
AriaAnalog.prototype.lastActivated = null;

/**
 * This property indicates that a particular type of selectable thing can have its selection removed if the ctrl
 * key is held down whilst selecting. This only applies to single selection since multi-selectable items can
 * always be deselected. In particular it is currently implemented for treeitem and listbox.
 * @var
 * @type Boolean
 * @default false
 */
AriaAnalog.prototype.ctrlAllowsDeselect = false;

/**
 * This property indicates that in a group of a particular type of selectable thing an item may be selected if
 * it is already selected in order to reset a group of selected items to a single selected item. Only applies
 * to multi selectable groups.
 * @var
 * @type Boolean
 * @default false
 */
AriaAnalog.prototype.allowSelectSelected = false;

/**
 * This property indicates that a particular type of mixed-mode multi selectable thing works like a check box
 * rather than an option. This is currently only implemented in row.
 * @var
 * @type Boolean
 * @default false
 */
AriaAnalog.prototype.simpleSelection = false;

/**
 * Allow subclasses to add extended initialisation.
 *
 * @var
 * @type {?Function}
 */
AriaAnalog.prototype._extendedInitialisation = null;

/**
 * Helper for getting a group container from a member of a group.
 *
 * @function
 * @public
 * @param {Element} element The group member we are using to derive the group container
 * @returns {HTMLElement} The element which defines a group by containment (such as a fieldset).
 */
AriaAnalog.prototype.getGroupContainer = function(element) {
	return group.getContainer(element, this.CONTAINER);
};

/**
 * Subscriber to module:wc/dom/shed to act on shed events SELECT and DESELECT. This will selectively deselect other items in the group if the
 * group selection mode ({@link module:wc/dom/ariaAnalog#exclusiveSelect}) is single or mixed a container element and that element does not
 * have attribute aria-multiselectable = "true".
 *
 * @function
 * @public
 * @param {Element} element The element SHED is acting on.
 * @param {String} action The select or deselect action.
 */
AriaAnalog.prototype.shedObserver = function(element, action) {
	let container, deselectOthers = false, config;
	if (action === shed.actions.SELECT && element.matches(this.ITEM.toString())) {
		if (this.exclusiveSelect === this.SELECT_MODE.SINGLE) {
			deselectOthers = true;
		} else if (this.exclusiveSelect === this.SELECT_MODE.MIXED) {
			container = this.getGroupContainer(element);
			if (container?.getAttribute("aria-multiselectable") !== "true") {
				deselectOthers = true;
			}
		}
		if (deselectOthers) {
			if (this.CONTAINER) {
				config = {"itemWd": this.ITEM, "containerWd": this.CONTAINER};
			}
			const _group = getFilteredGroup(element, config);
			if (_group?.length) {
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
	this.actionable = ["button", "a"];  // do not fire spacebar event listener or prevent default on these

	event.add(element, { type: "focus", listener: eventWrapper.bind(this), capture: true });
	event.add(element, { type: "click", listener: eventWrapper.bind(this), capture: true });
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
 * @param {Element} form the form or form segment whose state is being written.
 * @param {Element} container the container for writing the state fields.
 * @todo Anonymize the inner function.
 */
AriaAnalog.prototype.writeState = function(form, container) {
	const items = form.querySelectorAll(this.ITEM.toString());

	if (items.length) {
		const selectedItems = getFilteredGroup(Array.from(items));
		if (Array.isArray(selectedItems)) {
			selectedItems.forEach(function (next) {
				if (next.hasAttribute(this.VALUE_ATTRIB) && !shed.isDisabled(next)) {
					formUpdateManager.writeStateField(container, next.getAttribute("data-wc-name"), next.getAttribute(this.VALUE_ATTRIB));
				}
			}, this);
		}
	}
};


function bootstrap(element, instance) {
	const container = instance.getGroupContainer(element) || element,
		INIT_ATTRIB = "ariaAnalogKeydownInited";

	if (!container[INIT_ATTRIB]) {
		container[INIT_ATTRIB] = true;
		event.add(container, "keydown", eventWrapper.bind(instance));
	}
}

/**
 * Focus event listener to manage tab index on simple linear groups. Note though that components which do their
 * own navigation are also responsible for maintaining their own tab indices.
 * @function
 * @param {Event} $event The focus event.
 */
AriaAnalog.prototype.focusEvent = function ($event) {
	// `this` is bound in this listener
	let element = $event.target;

	if (element.matches(this.ITEM.toString()) && !shed.isDisabled(element)) {
		bootstrap(element, this);
		if (this.groupNavigation) {
			if (this.setFocusIndex) {
				this.setFocusIndex(element);
			}
		}
	}
};

/**
 * Click event listener to activate a group member on click (assuming it is acceptable); for example, radio
 * buttons get selected on click but not if the click is on something else with a tabStop.
 * @function
 * @param {MouseEvent} $event The click event.
 */
AriaAnalog.prototype.clickEvent = function ($event) {
	// `this` is bound in this listener
	const { target,
		defaultPrevented,
		shiftKey,
		metaKey,
		ctrlKey} = $event;
	if (defaultPrevented) {
		return;
	}
	const element = this.getActivableFromTarget(target);
	if (element && isAcceptableEventTarget(element, target)) {
		this.activate(element, (shiftKey || event.shiftKey), (ctrlKey || metaKey));  // event.shiftKey - see wc/fixes/shiftKey_ff
	}
};

/**
 * Keydown event listener to navigate between items or activate on SPACE where supported.
 *
 * @function
 * @param {KeyboardEvent} $event The keydown event.
 */
AriaAnalog.prototype.keydownEvent = function ($event) {
	// `this` is bound in this listener
	let target = $event.target;

	if ($event.defaultPrevented || $event.altKey) {
		return;
	}

	const element = this.getActivableFromTarget(target);
	if (!element) {
		return;
	}

	if (this.groupNavigation && isDirectionKey($event)) {
		const moveTo = calcMoveTo(this, $event);
		if (moveTo && (target = this.navigate(element, moveTo))) {
			if (this.selectOnNavigate(target) && !($event.ctrlKey || $event.metaKey)) {
				this.activate(target, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
			}
			$event.preventDefault();
		}
		return;
	}
	const keyCode = $event.key;
	const actionableSelector = this.actionable.map(next => next.toString()).join();
	if ((keyCode === "Space" || keyCode === "Enter") &&
		!element.matches(actionableSelector) && isAcceptableEventTarget(element, target)) {

		this.activate(element, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
		$event.preventDefault();  // preventDefault here otherwise you get a page scroll
	}
};

/**
 * key navigation for simple linear groups.
 *
 * @function
 * @param {Element} start Start element
 * @param {number} direction -1 to previous in group, 1 to next in group NOTE: radio button groups allow native
 *    group cycling at the extremities so we allow that here too. Only useful if one of
 *    {@link module:wc/dom/ariaAnalog~AriaAnalog#KEY_DIRECTION}
 * @returns {HTMLElement} The end point of the navigation. If start is part of a navigable group but there is
 *    nowhere to go then we may return the start element.
 */
AriaAnalog.prototype.navigate = function(start, direction) {
	let kwDirection,
		result,
		_group = getGroup(start, this);

	if (_group && (_group = filterGroup(_group)) && _group.length > 1) {  // no point navigating if only 1 option
		if (keyWalkerConfig) {
			keyWalkerConfig["root"] = _group;
		} else {
			keyWalkerConfig = {
				root: _group,
				filter: function(el) {
					/* the group filter EXCLUDES elements return true*/
					/** @type Number */
					let innerResult = NodeFilter.FILTER_ACCEPT;
					if (shed.isDisabled(el) || shed.isHidden(el)) {
						innerResult = NodeFilter.FILTER_REJECT;
					}
					return innerResult;
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

		const target = keyWalker.getTarget(keyWalkerConfig, start, kwDirection);
		if (target && target !== start) {
			focus.setFocusRequest(target);
			result = target;
		}
	}
	return result;
};

/**
 * A helper for activate which deals with selection of single-selects.
 *
 * @function
 * @param {Element} element The element being activated.
 * @param {boolean} CTRL Indicates the Ctrl key was depressed during activation.
 * @param {Object} instance The current analog module.
 */
function singleSelectActivateHelper(element, CTRL, instance) {
	if (instance.simpleSelection || (CTRL && instance.ctrlAllowsDeselect)) {
		shed.toggle(element, shed.actions.SELECT);
		return;
	}
	if (instance.allowSelectSelected && !CTRL && shed.isSelected(element)) {
		shed.select(element);
		return;
	}
	shed.select(element, shed.isSelected(element)); // do not publish a re-select selected / failed de-select.
}

/**
 * A helper for activate which deals with multi-selects with the SHIFT control depressed.
 *
 * @function
 * @param {Element} element The element being activated.
 * @param {Element} container The analog container.
 * @param {boolean} CTRL Indicates the Ctrl key was depressed during activation.
 * @param {Object} instance The current analog module.
 * @returns {Boolean} true unless a group selection is undertaken.
 */
function multiSelectWithShiftHelper(element, container, CTRL, instance) {
	let lastActivated;

	if (instance?.lastActivated[container.id]) {
		lastActivated = element.ownerDocument.getElementById(instance.lastActivated[container.id]);
	}
	if (lastActivated) {
		instance.doGroupSelect(element, lastActivated, CTRL);
		return false;
	}
	shed.toggle(element, shed.actions.SELECT);
	return true;
}

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
	let selectMode;

	try {
		let setLastActivated = true;
		const container = this.getGroupContainer(element);
		const isMultiSelect = container ? container.getAttribute("aria-multiselectable") : false;
		if (this.exclusiveSelect === this.SELECT_MODE.MIXED && isMultiSelect === "true") {
			selectMode = this.exclusiveSelect;
			if (this.simpleSelection || SHIFT || CTRL) {
				this.exclusiveSelect = this.SELECT_MODE.MULTIPLE;
			} else {
				this.exclusiveSelect = this.SELECT_MODE.SINGLE;
			}
		}

		if (this.exclusiveSelect === this.SELECT_MODE.SINGLE || this.exclusiveSelect === this.SELECT_MODE.MIXED) {
			singleSelectActivateHelper(element, CTRL, this);
		} else if (SHIFT && container) {
			setLastActivated = multiSelectWithShiftHelper(element, container, CTRL, this);
		} else {
			shed.toggle(element, shed.actions.SELECT);
		}

		if (setLastActivated && this.lastActivated) {
			this.setLastActivated(element, container);
		}
	} finally {
		if (selectMode !== undefined) {
			this.exclusiveSelect = selectMode;
		}
	}
};

/**
 * SHIFT + ACTIVATE helper: gets the group the element is in and selected/deselects all items between it and the
 * last activated item. Then, if the CTRL key was not in play and items are being selected it will deselect
 * items outside the group.
 *
 * @function
 * @param {Element} element The source element.
 * @param {Element} [lastActivated] The last activated element in the group.
 * @param {boolean} [CTRL] true if the ctrl or meta key was pressed during the event which resulted in the
 *    function being called.
 */
AriaAnalog.prototype.doGroupSelect = function(element, lastActivated, CTRL) {
	let selectedFilter,
		groupAction;

	if (shed.isSelected(element)) {
		selectedFilter = getFilteredGroup.FILTERS.selected;
		groupAction = shed.deselect;
	} else {
		selectedFilter = getFilteredGroup.FILTERS.deselected;
		groupAction = shed.select;
	}
	const filter = getFilteredGroup.FILTERS.visible | getFilteredGroup.FILTERS.enabled;
	const filtered = getFilteredGroup(element, { filter: (filter | selectedFilter), containerWd: this.CONTAINER, itemWd: (this.CONTAINER ? this.ITEM : null) });
	const unfiltered = getFilteredGroup(element, { filter: filter, containerWd: this.CONTAINER, itemWd: (this.CONTAINER ? this.ITEM : null) });

	if (Array.isArray(filtered) && Array.isArray(unfiltered)) {
		const start = Math.min(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));
		const end = Math.max(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));
		for (const next of unfiltered) {
			if (start <= unfiltered.indexOf(next) && end >= unfiltered.indexOf(next)) {
				if (~filtered.indexOf(next)) {
					groupAction(next);
				}
			} else if (!CTRL && selectedFilter === getFilteredGroup.FILTERS.deselected && next !== lastActivated && shed.isSelected(next)) {
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
 * @param {Element} element The last activated element.
 * @param {Element} [container] The group container (if known).
 */
AriaAnalog.prototype.setLastActivated = function (element, container) {
	container = container || this.getGroupContainer(element);
	if (container && this.lastActivated) {
		const containerId = container.id || (container.id = uid());
		this.lastActivated[containerId] = (element.id || (element.id = uid()));
	}
};

/**
 * Determines if the "this" item found in an event listener is the closest actiave aria analog to the event (as if we were handling the event
 * on capture for all events). This is to overcome the issue of all aria analogs listening for the same events with an ancestor lookup to
 * determine if they are the target. This results in multiple analogs responding if nested as we cannot rely on preventDefault() because we
 * cannot rely on the order in which the analogs handle an event.
 *
 * @function
 * @param {Element} target The event target
 * @param {Element} item The element found using this.ITEM.
 * @returns {Boolean} true if the item is the first active analog found in the ancestor tree.
 */
function isActiveAnalog(target, item) {

	// NOTE: We should not use focus.getFocusableAncestor or isAcceptableTarget here because we are only
	// interested in whether the analog is the nearest analog. To see a case where isAcceptableTarget here would
	// break something look at wc/ui/menu/MenuItem~clickEventHelper which gets an alternative activable element
	// if the expected item is a menuitem but is not an acceptable event target if the menuitem contains a
	// submenu and the click was on the submenu opener.

	let firstAnalog = target.closest(genericAnalogSelector);
	if (!firstAnalog) {
		return true;
	}

	if (firstAnalog === item && !isReadOnly(item)) {
		return true;
	}

	while (firstAnalog?.parentElement) {
		// A column header is active if the column is sortable.
		// NOTE: be aware we may eventually want to do the same with row header if we ever build row based sort.
		if (IGNORE_ROLES.indexOf(firstAnalog.getAttribute("role")) > -1 ||
				isReadOnly(firstAnalog) ||
				shed.isDisabled(firstAnalog) ||
				(firstAnalog.getAttribute("role") === "columnheader" && !firstAnalog.getAttribute("aria-sort"))) {
			firstAnalog = firstAnalog.parentElement.closest(genericAnalogSelector);
			continue;
		}

		if (firstAnalog.getAttribute("role") === "gridcell") {
			// ignore role gridcell if the nearest containing grid/treegid is aria-readonly as this state is inherited.
			const gridContainer = firstAnalog.closest(gridSelectors.join());
			if (gridContainer && isReadOnly(gridContainer)) {
				firstAnalog = firstAnalog.parentElement.closest(genericAnalogSelector);
				continue;
			}
		}
		break;
	}
	return (!firstAnalog || firstAnalog === item);
}

/**
 * When we change the selected item in a group we set the tabIndex otherwise tabbing into the group may not be
 * possible or may result in the wrong element receiving focus.
 *
 * @function
 * @param {HTMLElement} element The element to receive future focus.
 */
AriaAnalog.prototype.setFocusIndex = function(element) {
	const _group = getGroup(element, this);

	if (_group && _group.length > 1) {
		_group.forEach(function(next) {
			next.tabIndex = -1;
		});
		element.tabIndex = 0;
	}
};

/**
 * Gets the Widget which describes the active component.
 *
 * @function
 * @public
 * @returns {string}
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
 * @returns {HTMLElement} The activable aria analog ancestor of target.
 */
AriaAnalog.prototype.getActivableFromTarget = function(target) {
	if (isEventInLabel(target)) {
		return null;
	}

	const item = target.closest(this.ITEM.toString());
	if (!item) {
		return null;
	}

	if (!shed.isDisabled(item) && isActiveAnalog(target, item)) {
		return item;
	}
	return null;
};

/**
 * Determine if an ARIA analog control is multi-selectable. This is a helper which is only really useful for those analogs which may
 * have a mixed mode such as list options and table rows.
 * @param {Element} [element] an element which is itself a WAI-ARIA analog component. That is, it will be something which for some sub-class
 * of this will return `true` from `this.ITEM.isOneOfMe(element)`. This arg is mandatory for mixed mode analogs, and this function is only
 * really useful for those analogs.
 * @returns {Boolean} `true` if the current analog is multi-selectable.
 * @throws {TypeError} if the selection mode is mixed and no element is provided as a reference.
 */
AriaAnalog.prototype.isMultiSelect = function(element) {
	if (this.exclusiveSelect === this.SELECT_MODE.SINGLE) {
		return false;
	}
	if (this.exclusiveSelect === this.SELECT_MODE.MULTIPLE) {
		return true;
	}
	if (!element) {
		throw new TypeError ("Cannot determine mixed selection nature without a sample element.");
	}
	const container = this.getGroupContainer(element);
	return container ? (container.getAttribute("aria-multiselectable") === "true") : false;
};

/**
 * A CSS selector describing the element that implements this UI control.
 * For example a checkbox may be something like: "[role='checkbox']"
 * This must be overridden by subclasses.
 * @type {string}
 */
AriaAnalog.prototype.ITEM = "";

/**
 * A CSS selector describing the element that represents the group containing ITEM elements.
 * For example, it may be something like: "fieldset.foo"
 * @type {string}
 */
AriaAnalog.prototype.CONTAINER = "";

AriaAnalog.prototype.selectionIsImmediate = false;

export default AriaAnalog;
