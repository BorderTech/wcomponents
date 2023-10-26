import event from "wc/dom/event.mjs";
import focus from "wc/dom/focus.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import ajaxRegion from "wc/ui/ajaxRegion.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import timers from "wc/timers.mjs";
import common from "wc/ui/table/common.mjs";
import i18n from "wc/i18n/i18n.mjs";

const IDX_BUTTON = {
		FIRST: 0,
		PREV: 1,
		NEXT: 2,
		LAST: 3
	},
	TABLE_WRAPPER = common.WRAPPER,
	PAGINATION_CONTAINER = ".wc_table_pag_cont",
	SELECTOR = `${PAGINATION_CONTAINER} select`,
	PAGINATION_SELECTOR = `${SELECTOR}.wc_table_pag_select`,
	RPP_SELECTOR = `${SELECTOR}.wc_table_pag_rpp`,
	PAGE = common.TBODY,
	ROW = `${common.TR}.wc_table_pag_row`,
	PAGINATION_LABEL_WRAPPER = ".wc_table_pag_rows",
	TABLE = common.TABLE,
	PAGINATION_BUTTON = `${PAGINATION_CONTAINER} ${common.BUTTON}`,
	START_ELEMENT = "span.wc_table_pag_rowstart",
	END_ELEMENT = "span.wc_table_pag_rowend",
	BUSY = "aria-busy",
	PAGE_ATTRIB = "data-wc-pages",
	TRUE = "true",
	NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS = 4,  // this is the number of selections to show around the current page option.
	NUM_PAGE_OPTIONS = 2 * NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS + 3;  // This weird number gives us FIRST (4 before selected) SELECTED (4 after selected) LAST.

let updateQueue,
	triggerButtonId;

/**
 * @param {Element} element
 * @return {HTMLElement}
 */
function getWrapper(element) {
	return element.closest(TABLE_WRAPPER);
}

/**
 * @param {Element} element
 * @return {boolean}
 */
function isAjax(element) {
	const wrapper = getWrapper(element);
	return wrapper?.getAttribute("data-wc-pagemode") === "dynamic";
}

/**
 * Helper for updateSelectOptions and setUpPageSelectOptions.
 * @param {number} currentPage The page currently being shown.
 * @param {Number} totalPages The number of pages in the table.
 * @returns {Number} the start point for the page select options' values.
 */
function getStartValue(currentPage, totalPages) {
	if (currentPage <= NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS) {
		return 1;
	}
	if (totalPages - currentPage <= NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS) {
		return Math.max(totalPages - NUM_PAGE_OPTIONS + 2, 1);
	}
	return currentPage - NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS;
}

/**
 * i18n of the pagination description.
 * @param {Element} wrapper a table wrapper
 */
function translate(wrapper) {
	const labels = wrapper.querySelectorAll(PAGINATION_LABEL_WRAPPER);

	Array.from(labels).forEach(next => {
		if (getWrapper(next) === wrapper) {
			const updateElement = /** @param {string} i18nString */i18nString => {
				next.innerHTML = i18nString;
			};
			// we have the correct spans
			const rows = next.getAttribute("data-wc-tablerows");
			const rpp = next.getAttribute("data-wc-tablerpp");
			const currentPage = next.getAttribute("data-wc-tablepage");
			if (!(rows && rpp)) {
				return;
			}
			const numRpp = parseInt(rpp, 10);
			const numCurrentPage = parseInt(currentPage, 10);  // currentPage is 0 based.
			const startIdx = (numRpp * numCurrentPage) + 1;
			if (numRpp === 1) {
				i18n.translate("table_pagination_label_one", String(startIdx), rows).then(updateElement);
			} else {
				const numRows = parseInt(rows, 10);
				const endIdx = Math.min(numRows, (numRpp * numCurrentPage) + numRpp);
				if (startIdx === endIdx) {
					i18n.translate("table_pagination_label_one", String(endIdx), rows).then(updateElement);
				} else {
					i18n.translate("table_pagination_label_many", String(startIdx), String(endIdx), rows).then(updateElement);
				}
			}
		}
	});
}

/**
 * Translate after Ajax.
 * @param {Element} element not used.
 * @param {Element|DocumentFragment} fragment The DocumentFragment about to be inserted by Ajax or a container element.
 */
function ajaxSubscriber(element, fragment) {
	Array.from(fragment?.querySelectorAll(TABLE_WRAPPER)).forEach(translate);
}

/**
 * Update the options in the page selector after we change page (client mode only).
 *
 * @function
 * @private
 * @param {HTMLSelectElement} element The page selector.
 * @param {boolean} [ignoreOther] If true then do not reset the "other" page select (when the table has two).
 */
function updateSelectOptions(element, ignoreOther) {
	const totalPages = parseInt(element.getAttribute(PAGE_ATTRIB), 10) - 1; // the data-* attribute is one based.

	if (totalPages < NUM_PAGE_OPTIONS) {
		// If the total number of options is such that we never re-arrange them then there is nothing to do.
		return;
	}

	const currentPage = parseInt(element.value, 10);
	const options = element.options;

	element.setAttribute(BUSY, TRUE);
	let startVal = getStartValue(currentPage, totalPages);

	for (let i = 1; i < options.length - 1; ++i) {
		let nextOption = options[i];
		if (startVal === currentPage) {
			shed.select(nextOption, true);
			// Safari bug
			element.selectedIndex = i;
		}
		let value = String(startVal++);
		nextOption.value = value;
		nextOption.innerHTML = value;
	}
	let otherSelect = (!ignoreOther ? getOtherSelector(element) : null);
	if (otherSelect) {
		updateSelectOptions(otherSelect, true);
	}
	element.removeAttribute(BUSY);
}

/**
 * Add the required options to the pagination control's page selector.
 *
 * @function
 * @private
 * @param {Element} [element] Any element defaults to document.body.
 */
function setUpPageSelectOptions(element) {
	const container = element || document.body,
		/** @type NodeListOf<HTMLSelectElement> */
		selectors = container.querySelectorAll(PAGINATION_SELECTOR);

	Array.from(selectors).forEach(next => {
		let totalPages = parseInt(next.getAttribute(PAGE_ATTRIB), 10);
		if (next.options.length > 1 || totalPages === 1) {
			return; // we have already processed this. Should never happen but hey!
		}

		totalPages--;  // the data-* attribute is one based.

		const currentPage = parseInt(next.value, 10);
		let startVal = getStartValue(currentPage, totalPages);

		let isSelected = currentPage === 0;
		let option = "<option value='0'" + (isSelected ? " selected='selected'" : "") + ">1</option>";
		let selectedIndex;
		if (isSelected) {
			selectedIndex = 0;
		}
		let i;
		for (i = 0; i < NUM_PAGE_OPTIONS - 2; i++) {
			if (i + 1 >= totalPages) {
				break;
			}
			isSelected = currentPage === startVal;
			option += "<option value='" + startVal + "'" + (isSelected ? " selected='selected'" : "") + ">" + (startVal + 1) + "</option>";
			if (isSelected) {
				selectedIndex = i + 1;
			}
			startVal++;
		}
		isSelected = currentPage === totalPages;
		option += "<option value='" + totalPages + "'" + (isSelected ? " selected='selected'" : "") + ">" + (totalPages + 1) + "</option>";
		if (isSelected) {
			selectedIndex = i + 1;
		}

		next.innerHTML = option;
		if (selectedIndex !== undefined) {
			next.selectedIndex = selectedIndex;
		}
		next.removeAttribute(BUSY);
	});
}

/**
 * Given one 'page selection dropdown' find the other (if the table has two).
 *
 * @function
 * @private
 * @param {HTMLSelectElement} selector a page selection dropdown.
 * @returns {HTMLSelectElement} the other pagination dropdown.
 */
function getOtherSelector(selector) {
	const wrapper = getWrapper(selector),
		/** @type NodeListOf<HTMLSelectElement> */
		selectors = (selector.matches(PAGINATION_SELECTOR) ?
			wrapper.querySelectorAll(PAGINATION_SELECTOR) : wrapper.querySelectorAll(RPP_SELECTOR));  // this could include selectors in nested tables
	if (selectors && selectors.length > 1) {
		for (let i = 0; i < selectors.length; ++i) {
			if (selectors[i] === selector) {
				continue;
			}
			if (wrapper === getWrapper(selectors[i])) {
				return selectors[i];
			}
		}
	}
	return null;
}

/**
 * Gets the `TYPE` of a given button.
 *
 * @function
 * @private
 * @param {Element} button A pagination button.
 * @returns {number} One the constants defined in IDX_BUTTON
 */
function getButtonType(button) {
	const container = button.closest(PAGINATION_CONTAINER),
		buttons = Array.from(container.querySelectorAll(PAGINATION_BUTTON));
	return buttons.indexOf(button);
}

/**
 * Undertake the action of a page change button .This will result in the pagination controller select
 * being updated if the page change is able to go ahead.
 *
 * @function
 * @private
 * @param {Element} button The button to action.
 * @param {Element} paginationContainer The container that holds the button.
 */
function actionButton(button, paginationContainer) {
	const activeQs = `${PAGINATION_SELECTOR}:not([disabled]):not([${BUSY}='${TRUE}'])`;
	/** @type {HTMLSelectElement} */
	const selector = paginationContainer.querySelector(activeQs);

	if (selector) {  // don't do anything if selector disabled or busy
		const len = selector.options.length;
		const oldIndex = selector.selectedIndex;
		const buttonType = getButtonType(button);
		let newIndex;
		if (buttonType === IDX_BUTTON.LAST) {
			newIndex = len - 1;  // select last option in list
		} else if (buttonType === IDX_BUTTON.PREV) {
			newIndex = oldIndex ? oldIndex - 1 : oldIndex;  // if oldIndex is zero don't decrement
		} else if (buttonType === IDX_BUTTON.NEXT) {
			newIndex = (oldIndex < (len - 1)) ? oldIndex + 1 : oldIndex;  // if we are at last page don't increment
		} else {  // FIRST
			newIndex = 0;
		}
		if (newIndex >= 0 && newIndex !== oldIndex) {
			selector.selectedIndex = newIndex;
			const otherSelector = getOtherSelector(selector);
			if (otherSelector) {
				otherSelector.selectedIndex = newIndex;
			}
			requestPageChange(selector, button);
		}
	}
}

/**
 * Helper to make an ajax request when a dynamic pagination select or rows per page select is changed.
 *
 * @function
 * @private
 * @param {Element} element The control which was updated leading to the ajax request becoming necessary.
 */
function requestAjaxLoad(element) {
	ajaxRegion.requestLoad(element, common.getAjaxDTO(element, true));
}

/**
 * Enables and disabled pagination buttons based on the page currently shown. Do not publish these changes,
 * nothing should care.
 *
 * @function
 * @private
 * @param {HTMLSelectElement} element The pagination select controller.
 */
function setPaginationButtonState(element) {
	const idx = element.selectedIndex,
		d = "disable",
		e = "enable",
		container = element.closest(PAGINATION_CONTAINER),
		buttons = container?.querySelectorAll(PAGINATION_BUTTON);

	if (buttons) {
		Array.from(buttons).forEach(button => {
			button.setAttribute(BUSY, TRUE);
			const type = getButtonType(button);
			if (idx === 0) {
				if (type === IDX_BUTTON.FIRST || type === IDX_BUTTON.PREV) {
					shed[d](button, true);
				} else {
					shed[e](button, true);
				}
			} else if (idx === element.options.length - 1) {
				if (type === IDX_BUTTON.FIRST || type === IDX_BUTTON.PREV) {
					shed[e](button, true);
				} else {
					shed[d](button, true);
				}
			} else {
				shed[e](button, true);
			}
			button.removeAttribute(BUSY);
		});
	}
}

/**
 * Interleaves the showing and hiding of rows to prevent page jumping around in slow browsers (like IE8).
 * This is not necessary in Chrome and FF but IE needs it.
 *
 * @function
 * @private
 * @param {Element[]} rows The collection of row elements
 * @param {number} count The number of rows to show and hide (if they are different put in the bigger of the two)
 * @param {number} showStart The starting index of the rows to show (inclusive, ie will be shown)
 * @param {number} hideStart The starting index of the rows to hide (inclusive, ie will be hidden)
 */
function interleavedShowHide(rows, count, showStart, hideStart) {
	let i = 0;
	while (i < count) {
		let nextHide = rows[hideStart + i];
		let nextShow = rows[showStart + i];
		i++;
		if (nextHide || nextShow) {
			if (nextHide) {
				shed.hide(nextHide);
			}
			if (nextShow) {
				shed.show(nextShow);
			}
		} else {
			break;
		}
	}
}

/**
 * Updates the record X of Y displays on client-mode page change.
 * @param {Element} wrapper The table wrapper.
 * @param {String} startHTML The content for the start span.
 * @param {String} endHTML The content for the end span.
 */
function updateRecordDisplays(wrapper, startHTML, endHTML) {
	let html;
	/**
	 * @param {Element} next
	 */
	const updater = next => {
		if (getWrapper(next) === wrapper) {
			next.innerHTML = html;
		}
	};
	html = startHTML;
	Array.from(wrapper.querySelectorAll(START_ELEMENT)).forEach(updater);
	html = endHTML;
	Array.from(wrapper.querySelectorAll(END_ELEMENT)).forEach(updater);
}

/**
 * Change the visible page to reflect a change in the selector list. Assumes a change has actually been
 * made, it's up to the caller to ensure that an update is actually necessary.
 *
 * @function
 * @private
 * @param {HTMLSelectElement} element The page change selector (dropdown list)
 * @param {Element} [button] Button which originated the change action.
 */
function changePage(element, button) {
	if (isAjax(element)) {
		triggerButtonId = button.id;
		requestAjaxLoad(element);
		return;
	}

	/**
	 * @param {Element} el
	 * @param {string} qs
	 * @return {Element}
	 */
	const findChild = (el, qs) => {
		const kids = el?.children || [];
		return Array.from(kids).find(kid => kid.matches(qs));
	};

	const wrapper = getWrapper(element);
	const paginatedTable = findChild(wrapper, TABLE);
	const page = findChild(paginatedTable, PAGE);
	if (wrapper && paginatedTable && page) {
		const rows = Array.from(page.children).filter(kid => kid.matches(ROW));
		const rowsPerPage = Number(paginatedTable.getAttribute("data-wc-rpp"));
		let i;
		for (i = 0; i < rows.length; i++) {  // don't "let i" here
			if (!shed.isHidden(rows[i])) {
				break;
			}
		}
		const requestedPage = Number(element.value);
		const startIdx = requestedPage * rowsPerPage;
		interleavedShowHide(rows, rowsPerPage, startIdx, i);
		updateSelectOptions(element);
		updateRecordDisplays(wrapper, String(startIdx + 1), String(Math.min(startIdx + rowsPerPage, rows.length)));
		setPaginationButtonState(element);
	}
}

/**
 * This function simply calls pageChange but queues requests and only actions the last request when there
 * have been no further requests for a given number of milliseconds.
 *
 * @see {changePage}
 * @function
 * @private
 * @param {Element} element The page change selector control.
 * @param {Element} [button] The button which originally triggered the change (if any).
 */
function requestPageChange(element, button) {
	if (updateQueue) {
		timers.clearTimeout(updateQueue);
	}
	updateQueue = timers.setTimeout(changePage, 250, element, button);
}

/**
 * change event listener to handle a change event on a pagination dropdown list.
 * This is the 'main event' that drives the page change.
 *
 * @function
 * @private
 * @param {Event & { target: HTMLSelectElement }} $event The change event.
 */
function changeEvent({ target, defaultPrevented }) {
	if (defaultPrevented || target.disabled) {
		return;
	}

	// if the table has two pagination/rows per page selectors they have to be kept in sync but do not fire
	// change events on the alternate.
	const alternateSelector = target.matches(SELECTOR) ? getOtherSelector(target): null;
	if (alternateSelector) {
		alternateSelector.selectedIndex = target.selectedIndex;
	}

	if (target.matches(SELECTOR) && isAjax(target)) {
		// dynamic pagination and change rows per page (latter always ajax).
		requestAjaxLoad(target);
	} else if (target.matches(PAGINATION_SELECTOR)) {
		requestPageChange(target);
	}
}

/**
 * Handles a click on any of the pagination buttons.
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
 */
function clickEvent({ target, defaultPrevented }) {
	if (!defaultPrevented) {
		/** @type {HTMLButtonElement} */
		const button = target.closest(PAGINATION_BUTTON);
		if (button && !button.disabled && button.getAttribute(BUSY) !== TRUE) {
			const paginationContainer = button.closest(PAGINATION_CONTAINER);
			actionButton(button, paginationContainer);
		}
	}
}

/**
 * Subscriber to {@link module:wc/ui/ajax/processReponse}. If the ajax trigger is a dropdown select
 * control, and we have previously stored a button ID we have to refocus to the buttonId. The
 * {@link module:wc/dom/onloadFocusControl} cannot do this for us.
 *
 * @function
 * @private
 * @param {Element} element The AJAX target element.
 * @param {String} action Not required for this function.
 * @param {String} triggerId The id of the ajax trigger element.
 */
function postAjaxSubscriber(element, action, triggerId) {
	if (element) {
		if (element.matches(TABLE_WRAPPER)) {
			setUpPageSelectOptions(element);
		}

		Array.from(element.querySelectorAll(TABLE_WRAPPER)).forEach(setUpPageSelectOptions);
	}
	const trigger = (triggerId && triggerButtonId) ? document.getElementById(triggerId) : null;
	if (trigger?.matches(PAGINATION_SELECTOR)) {
		try {
			const button = document.getElementById(triggerButtonId);
			if (button) {
				const { activeElement, body } = document;
				if (!shed.isDisabled(button) && (!activeElement || activeElement === trigger || activeElement === body)) {
					/* onLoadFocusControl may have already set the focus to the ajax trigger
					 * so we cannot use it to refocus to the button, but we can determine that
					 * we do not need to re-test for other focus since onloadFocusControl will
					 * have done that before focussing the select.*/
					focus.setFocusRequest(button);
				}
			}
		} finally {
			/* NOTE: only set triggerButtonId to null when we are sure we are
			 * processing the pagination ajax response as there may be many
			 * responses betwixt setting the triggerButtonId and the one we
			 * want (unlikely, but definitely possible).*/
			triggerButtonId = null;
		}
	}
}

/**
 * Write the state of pagination select and rows per page selector. We do not use native name:value semantics
 * here because to do so causes an unnecessary unsaved changes warning.
 *
 * @function
 * @private
 * @param {Element} form The form or form segment the state of which is being written.
 * @param {Element} stateContainer The element into which the state is written.
 */
function writeState(form, stateContainer) {
	/**
	 *
	 * @param {Element} element
	 * @private
	 */
	function _writeStatePaginableTable(element) {
		const container = getWrapper(element);
		if (!container) {
			return;
		}

		const id = container.id;

		/** @type {HTMLSelectElement} */
		let selector = element.querySelector(PAGINATION_SELECTOR);
		if (selector && !selector.disabled) {
			formUpdateManager.writeStateField(stateContainer, `${id}.page`, selector.value);
		}

		selector = element.querySelector(RPP_SELECTOR);
		if (selector && !selector.disabled) {
			formUpdateManager.writeStateField(stateContainer, `${id}.rows`, selector.value);
		}
	}

	Array.from(form.querySelectorAll(PAGINATION_CONTAINER)).forEach(_writeStatePaginableTable);
}

/**
 * Provides table pagination functionality.
 *
 * NOTE: the pagination controls consist of a SELECT element and a set of four BUTTON elements. The actual pagination
 * activation is *ALWAYS* done by the SELECT: the BUTTON elements are used only to change the selectIndex of the SELECT.
 * Trying to do anything else will lead to a sudden, and possibly permanent, loss of sanity.
 *
 * <p>Three complicating factors to account for with the page select element:</p>
 * <ol>
 *    <li>IE8 does not have event capture, so we have to directly attach change listener to select;</li>
 *    <li>The entire table, including the select, is replaced on AJAX table updates. This means we potentially have to
 *    bootstrap the select over and over;</li>
 *    <li>Change event not fired when value change programmatically, meaning we have to fire the change event manually
 *    in some cases.</li>
 * </ol>
 *
 * @module
 */
initialise.register({
	/**
	 * Setup and initialise functionality.
	 *
	 * @function
	 * @public
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: element => {
		// event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "change", changeEvent, 1);
		event.add(element, "click", clickEvent);
	},

	/**
	 * Late setup: the post-insertion ajax subscriber and formUpdateManager subscriber.
	 *
	 * @function module:wc/ui/table/pagination.postInit
	 * @public
	 */
	postInit: () => {
		setUpPageSelectOptions();
		processResponse.subscribe(ajaxSubscriber);
		processResponse.subscribe(postAjaxSubscriber, true);
		formUpdateManager.subscribe({ writeState });
	},

	/**
	 * Early initialisation to do i18n of pagination.
	 * @function module:wc/ui/table/pagination.preInit
	 * @public
	 */
	preInit: () => ajaxSubscriber(null, document.body)
});
