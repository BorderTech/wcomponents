import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import focus from "wc/dom/focus";
import formUpdateManager from "wc/dom/formUpdateManager";
import getBox from "wc/dom/getBox";
import shed from "wc/dom/shed";
import ajaxRegion from "wc/ui/ajaxRegion";
import processResponse from "wc/ui/ajax/processResponse";
import selectboxSearch from "wc/ui/selectboxSearch";
import fieldset from "wc/ui/fieldset";

const CONTAINER_INITIALISED_KEY = "multiSelectPair.inited",
	LIST_TYPE_AVAILABLE = 0,
	LIST_TYPE_CHOSEN = 1,
	LIST_TYPE_ORDER = 2,
	ACTION_MAP = {"aall": addAll,
		"add": addSelected,
		"rall": removeAll,
		"rem": removeSelected};

const containerSelector = `${fieldset.getWidget().toString()}.wc-multiselectpair`;
const selectSelector = `${containerSelector} select`;
const lists = [];
const buttonSelector = `${containerSelector} button`;
const optgroupSelector = "optgroup";
lists[LIST_TYPE_AVAILABLE] = `${selectSelector}.wc_msp_av`;
lists[LIST_TYPE_CHOSEN] = `${selectSelector}.wc_msp_chos`;
lists[LIST_TYPE_ORDER] = `${selectSelector}.wc_msp_order`;


/**
 * Provides functionality for WMultiSelectPair which is a side-by-side multi-selection list control.
 */
const instance = {

	/**
	 * Get the list type for a given select list.
	 *
	 * @function module:wc/ui/multiSelectPair.getListType
	 * @public
	 * @param {HTMLSelectElement} element Any select element component of a WMultiSelectPair.
	 * @returns {int} The type of the element as defined in LISTS or null.
	 */
	getListType: function(element) {
		if (element.matches(selectSelector)) {
			for (let list = 0; list < lists.length; list++) {
				if (element.matches(lists[list])) {
					return list;
				}
			}
		}
		return null;
	},

	/**
	 * Get the descriptor for a WMultiSelectPair.
	 *
	 * @function module:wc/ui/multiSelectPair.getWidget
	 * @public
	 * @returns {string} the WMultiSelectPair container's {@link module:wc/dom/Widget}.
	 */
	getWidget: () => containerSelector,

	/**
	 * Get the descriptor for a WMultiSelectPair's input component.
	 *
	 * @function module:wc/ui/multiSelectPair.getInputWidget
	 * @public
	 * @returns {string} the WMultiSelectPair inpurs's {@link module:wc/dom/Widget}.
	 */
	getInputWidget: () => selectSelector,

	/** @var {int} module:wc/ui/multiSelectPair.LIST_TYPE_CHOSEN The descriptor for the "selected options" list. */
	LIST_TYPE_CHOSEN,

	/**
	 * @var {int} module:wc/ui/multiSelectPair.LIST_TYPE_AVAILABLE The descriptor for the "available options" list. */
	LIST_TYPE_AVAILABLE,

	/**
	 * Gets the available, selected or oder list for a given WMultiSelectPair based on the type argument.
	 *
	 * @function module:wc/ui/multiSelectPair.getListByType
	 * @public
	 * @param {HTMLElement} element Any component element of a multiSelectPair (ie any of the lists or buttons).
	 * @param {int} type One of the types defined in LISTS.
	 * @returns {HTMLSelectElement} The list of the type represented by the type argument.
	 */
	getListByType: function(element, type) {
		let result = null;
		const container = element.closest(containerSelector);
		const list = container ? lists[type] : "";
		if (list) {
			result = container.querySelector(list);
		}
		return result;
	},

	/**
	 * Get the selected options - these may not actually be marked as selected in the DOM, but they are in the
	 * selected bucket, so they are logically selected.
	 *
	 * @function module:wc/ui/multiSelectPair.getValue
	 * @public
	 * @param {HTMLElement} container A multiSelectPair container.
	 * @returns {(NodeList|Array)} The logically selected options in this multiSelectPair. Returns an empty
	 *    Array if no options are selected.
	 */
	getValue: function(container) {
		let result;
		if (!shed.isDisabled(container)) {
			const selectedBucket = this.getListByType(container, LIST_TYPE_CHOSEN);
			if (selectedBucket) {
				result = selectedBucket.options;
			}
		}
		return result || [];
	},

	/**
	 * Indicates that the element is a multiSelectPair (which, for the purposes of this call is the top level
	 * container)
	 *
	 * @function module:wc/ui/multiSelectPair.isOneOfMe
	 * @public
	 * @param {HTMLElement} element The DOM element to test.
	 * @returns {boolean} True if the passed in element is a multiSelectPair.
	 */
	isOneOfMe: function(element) {
		return element.matches(containerSelector);
	},

	/** Public for testing  @ignore */
	_keydownEvent: keydownEvent
};

/**
 * Fix the width and height of the available and selected lists so that they are the same size. This was
 * reintroduced (with the addition of a height fix) because some very common browsers will render a
 * select Element with a size attribute at a different height if it has no options. Since I had to fix
 * height I reintroduced fix width.
 *
 * @function
 * @private
 * @param {HTMLElement} [container] A WMultiSelectPair or any container component.
 */
function fixWidthHeight(container) {
	const el = container || document.body, PX = "px";
	let components;
	if (el.matches(containerSelector)) {
		components = [container];
	} else {
		components = el.querySelectorAll(containerSelector);
	}
	Array.from(components).forEach( function(next) {
		const avail = instance.getListByType(next, LIST_TYPE_AVAILABLE);
		if (avail.style.width) {
			return;  // already set
		}
		const chosen = instance.getListByType(next, LIST_TYPE_CHOSEN);
		let box = getBox(avail);
		let maxWidth = box.width;
		let maxHeight = box.height;

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
 * @param {HTMLSelectElement} list A select list from a MultiSelectPair component.
 * @returns {int} The opposite "LIST_TYPE_" of the list. Returns null if we cannot determine.
 */
function getOppositeListType(list) {
	const type = instance.getListType(list);
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
 * @param {HTMLButtonElement} element A button element.
 * @returns {Function} The action to perform for a button of this type.
 */
function getAction(element) {
	let result;
	if (element.matches(buttonSelector)) {
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
 * @param {HTMLSelectElement} fromList The select from which the selected options are removed.
 */
function addRemoveSelected(fromList) {
	const oppositeType = getOppositeListType(fromList),
		toList = instance.getListByType(fromList, oppositeType),
		orderList = instance.getListByType(fromList, LIST_TYPE_ORDER);
	let fromIndex = fromList.selectedIndex;
	let result;
	if (fromList.options.length && fromIndex >= 0) {
		toList.selectedIndex = -1;
		while (fromIndex >= 0) {
			let next = fromList.options[fromIndex];
			const parentElement = next.parentElement;
			if (parentElement.matches(optgroupSelector)) {
				let optgroupWD = `${optgroupSelector}[label='${parentElement.label}']`;
				let orderOptGroup = orderList.querySelector(optgroupWD);
				let originalIndex = selectboxSearch.indexOf(next, orderOptGroup);
				let fromGroupIndex = selectboxSearch.indexOf(next, parentElement);
				let optgroup = toList.querySelector(optgroupWD);
				if (optgroup) {
					let toIndex = calcToIndex(originalIndex, fromGroupIndex);
					if (toIndex >= optgroup.children.length) {
						optgroup.appendChild(next);
					} else {
						optgroup.insertBefore(next, optgroup.children[toIndex]);
					}
					result = true;
				} else {
					// we need to make an optgroup in toList, but where?
					optgroup = document.createElement("optgroup");
					optgroup.label = parentElement.label;
					originalIndex = selectboxSearch.indexOf(next, orderList);
					let toIndex = calcToIndex(originalIndex, fromIndex);
					if (toIndex >= toList.options.length) {
						toList.appendChild(optgroup);
					} else {
						// does the option we are creating the optgroup before have an optgroup parent?
						let toOptgroup = toList.options[toIndex].parentNode;
						if (toOptgroup.matches(optgroupSelector)) {
							toList.insertBefore(optgroup, toOptgroup);
						} else {
							toList.insertBefore(optgroup, toList.options[toIndex]);
						}
					}
					optgroup.appendChild(next);
					result = true;
				}

				if (parentElement.children.length === 0) {
					fromList.removeChild(parentElement);
				}
			} else {
				let originalIndex = selectboxSearch.indexOf(next, orderList);
				let toIndex = calcToIndex(originalIndex, fromIndex);
				if (toIndex >= toList.options.length) {
					toList.appendChild(next);
					result = true;
				} else {
					let toOptgroup = toList.options[toIndex].parentElement;
					if (toOptgroup.matches(optgroupSelector)) {
						toList.insertBefore(next, toOptgroup);
					} else {
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
	let result = originalIndex - fromIndex;
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
	} else {
		shed.deselect(toList);  // moving from chose to available publishes a deselection
	}
}

/**
 * Move selected options in the "available" list to the "selected" list.
 *
 * @function
 * @private
 * @param {HTMLElement} element A WMultiSelectPair container.
 */
function addSelected(element) {
	addRemoveSelected(instance.getListByType(element, LIST_TYPE_AVAILABLE));
}

/**
 * Move selected options in the "selected" list to the "available" list.
 *
 * @function
 * @private
 * @param {HTMLSelectElement} element A WMultiSelectPair container.
 */
function removeSelected(element) {
	addRemoveSelected(instance.getListByType(element, LIST_TYPE_CHOSEN));
}

/**
 * Helper for {@link addAll} and {@link removeAll} which actual does the option move.
 *
 * @function
 * @private
 * @param {HTMLSelectElement} selectList The list from which we are moving options.
 * @param {Function} action The function to apply to the options ({@link module:wc/ui/multiSelectPair~addSelected}
 * or {@link module:wc/ui/multiSelectPair~removeSelected}).
 */
function actionAllOptions(selectList, action) {
	for (const element of selectList.options) {
		if (!shed.isSelected(element)) {
			shed.select(element, true);  // Keep this quiet! We will publish our own shed event when done. Publishing each select would be really stupid.
		}
	}
	action(selectList);
}

/**
 * Add all options to the "selected" list.
 *
 * @function
 * @private
 * @param {HTMLElement} element A WMultiSelectPair.
 */
function addAll(element) {
	const availableBucket = instance.getListByType(element, LIST_TYPE_AVAILABLE);
	actionAllOptions(availableBucket, addSelected);
}

/**
 * Remove all options from the "selected" list.
 *
 * @function
 * @private
 * @param {HTMLElement} element A WMultiSelectPair.
 */
function removeAll(element) {
	const selectedBucket = instance.getListByType(element, LIST_TYPE_CHOSEN);
	actionAllOptions(selectedBucket, removeSelected);
}


/**
 * Writes the state of the MultiSelectPair. All options in the "selected" list are deemed to be selected
 * even when they are not selected in the DOM. Equally, no options in the "available" list are selected
 * irrespective of their actual selected state.
 *
 * @function
 * @private
 * @param {HTMLElement} form The form or sub-form which is having its state written.
 * @param {HTMLElement} stateContainer The container into which state is written.
 */
function writeState(form, stateContainer) {
	Array.from(form.querySelectorAll(containerSelector)).forEach( function (container) {
		const selectedOptions = instance.getValue(container);
		for (const element of selectedOptions) {
			formUpdateManager.writeStateField(stateContainer, container.id, element.value);
		}
	});
}

/**
 * Keydown listener. Enter key adds/removes options and Left and Right Arrow keys switch between from and to
 * selects.
 *
 * @function
 * @private
 * @param {KeyboardEvent} $event The keydown event.
 */
function keydownEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	const selectList = $event.target.closest(selectSelector);
	if (!selectList) {
		return;
	}

	const keyCode = $event.key;

	if (keyCode === "Enter") {
		$event.preventDefault();  // chrome submits form for "enter" in select multiple="multiple"
		addRemoveSelected(selectList);
	} else {
		const selectType = instance.getListType(selectList);

		const focusOpposite = (keyCode === "ArrowRight" && selectType === LIST_TYPE_AVAILABLE) ||
			(keyCode === "ArrowLeft" && selectType === LIST_TYPE_CHOSEN);

		if (!focusOpposite) {
			return;
		}
		const opposite = instance.getListByType(selectList, getOppositeListType(selectList));
		if (opposite) {
			selectList.selectedIndex = -1;
			try {
				focus.setFocusRequest(opposite);
			} catch (ignore) {
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
 * @param {FocusEvent} $event The focus/focusin event.
 */
function focusEvent($event) {
	const container = $event.target.closest(containerSelector);
	if (container && !container[CONTAINER_INITIALISED_KEY]) {
		container[CONTAINER_INITIALISED_KEY] = true;
		event.add(container, "keydown", keydownEvent);
	}
}

/**
 * Click listener to move options from one list to the other.
 *
 * @function
 * @private
 * @param {MouseEvent} $event The click event.
 */
function clickEvent({ target, defaultPrevented}) {
	if (defaultPrevented) {
		return;
	}
	const element = target.closest(buttonSelector);
	const action = (element && !shed.isDisabled(element)) ? getAction(element) : null;
	if (action) {
		action(element);
	}
}

/**
 * Double-click listener to move options from one list to the other.
 *
 * @function
 * @private
 * @param {MouseEvent} $event The dblclick event.
 */
function dblClickEvent({ target, defaultPrevented }) {
	if (defaultPrevented) {
		return;
	}
	const selectList = target.matches("option,select") ? target.closest(selectSelector) : null;
	if (selectList && !shed.isDisabled(selectList)) {
		addRemoveSelected(selectList);
		const container = selectList.closest(containerSelector);
		if (container && ajaxRegion.getTrigger(container, true)) {
			ajaxRegion.requestLoad(selectList);
		}
	}
}

initialise.register({
	/**
	 * Set up initial event handlers.
	 *
	 * @function module:wc/ui/multiSelectPair.initialise
	 * @public
	 * @param {HTMLElement} element The element being initialised: usually `document.body`
	 */
	initialise: function(element) {
		fixWidthHeight();
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "click", clickEvent);
		event.add(element, "dblclick", dblClickEvent);
	},

	/**
	 * Late set up to wire up subscribers after initialisation.
	 *
	 * @function module:wc/ui/multiSelectPair.postInit
	 * @public
	 */
	postInit: function () {
		shed.subscribe(shed.actions.SHOW, fixWidthHeight);
		processResponse.subscribe(fixWidthHeight, true);
		formUpdateManager.subscribe(writeState);
	}
});

export default instance;
