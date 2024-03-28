import AriaAnalog from "wc/dom/ariaAnalog.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import containerload from "wc/ui/containerload.mjs";
import focus from "wc/dom/focus.mjs";
import viewportUtils from "wc/ui/viewportUtils.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import event from "wc/dom/event.mjs";
import debounce from "wc/debounce.mjs";
import getStyle from "wc/dom/getStyle.mjs";

const
	/**
	 * The description of a tab list.
	 */
	TABLIST = "[role='tablist']",

	/**
	 * The description of a tab panel.
	 */
	TABPANEL = "[role='tabpanel']",

	/**
	 * The description of a tab set.
	 */
	TABSET = ".wc-tabset",

	resizeEvent = debounce(function() {
		toggleToFromAccordions();
	}, 100),
	CONVERTED = "data-wc-converted",
	MULTISELECT = "aria-multiselectable",
	TRUE = "true",
	FALSE = "false",
	ACCORDION_CLASS = "wc-tabset-type-accordion",
	/**
	 * @constant {String} OLD_HEIGHT The name of the attribute used to hold the pre-ajax height of a target
	 * container if it was specified in a style attribute. Used to reset the height of the container to its
	 * initial (fixed) height after it stops being busy.
	 * @private
	 */
	OLD_HEIGHT = "data-wc-height",
	/**
	 * @constant {String} OLD_WIDTH The name of the attribute used to hold the pre-ajax width of a target
	 * container if it was specified in a style attribute. Used to reset the width of the container to its
	 * initial (fixed) width after it stops being busy.
	 * @private
	 */
	OLD_WIDTH = "data-wc-width",
	/**
	 * @constant {String} UPDATE_SIZE The attribute name used to indicate that the busy region has had its
	 * pre-update size calculated and set so that a region which has its contents removed does not collapse.
	 * @private
	 */
	UPDATE_SIZE = "data-wc-size";

/**
 * The ID of the last focused tab. This is needed to reset focusability to the open tab in a tabset
 * once focus leaves the tablist.
 * @var
 * @type String
 * @private
 */
let lastTabId;

/**
 * Provides tab and tabset functionality.
 *
 * @constructor
 * @alias module:wc/ui/tabset~Tabset
 */
class Tabset extends AriaAnalog {

	/**
	 * The description of a tab control.
	 * @public
	 * @override
	 */
	ITEM = "[role='tab']";

	/**
	 * Select items immediately on navigation.
	 * @function
	 * @param {Element} element the tab being navigated to
	 * @returns {Boolean} true unless the tab is in an accordion.
	 * @override
	 */
	selectOnNavigate(element) {
		const container = this.getGroupContainer(element);
		return !(container?.getAttribute(MULTISELECT));
	}

	/**
	 * The selection mode for the group of tabs context. The select mode is mixed as accordions may be
	 * multi-selectable.
	 *
	 * @var
	 * @type {number}
	 * @default 2
	 * @override
	 */
	exclusiveSelect = this.SELECT_MODE.MIXED;

	/**
	 * Indicates is keyboard navigation should cycle between the end points of the tablist.
	 * @constant
	 * @type {Boolean}
	 * @default true
	 * @override
	 */
	_cycle = true;

	/**
	 * Get the descriptor of the list component. This is required by some custom implementations but is not
	 * currently used in WComponents core.
	 *
	 * TODO: remove this functionality and set up the TABLIST Widget independently where required.
	 *
	 * @function module:wc/ui/tabset.getList
	 * @public
	 * @returns {string} The TABLIST selector.
	 */
	getList = () => TABLIST;

	collapseAll = tabset => expandCollapseAll(tabset, false);

	expandAll = tabset => expandCollapseAll(tabset, true);

	/**
	 * Are all tabs in a tabset inthe same expanded state?
	 * @param {Element} tabset The tabset to test
	 * @param {boolean} expanded true is testing for
	 * @returns {Boolean}
	 */
	areAllInExpandedState(tabset, expanded) {
		const list = tabset.querySelector(TABLIST);
		const accordion = list ? getAccordion(list) : null;
		if (!accordion) { // only accordions can have all items in a state.
			return false;
		}

		if (accordion === FALSE && expanded) {
			return false; // single accordion all in the same state only if collapsed.
		}
		return Array.from(list.querySelectorAll(this.ITEM)).every(next => shed.isExpanded(next) === expanded);
	}

	/**
	 * A subscriber to {@link module:wc/dom/shed} to react to these pseudo-events.
	 *
	 * @function module:wc/ui/tabset.shedObserver
	 * @override
	 * @param {HTMLElement} element The element on which the shed action acted.
	 * @param {String} action The type of shed event. One of EXPAND, COLLAPSE, SELECT or DESELECT.
	 */
	shedObserver (element, action) {
		if (element) {
			if (element.matches(this.ITEM.toString())) {
				switch (action) {
					case shed.actions.SELECT:
					case shed.actions.DESELECT:
						this.onItemSelection(action, element);
						break;
					case shed.actions.EXPAND:
					case shed.actions.COLLAPSE:
						onItemExpansion(action, element);
						break;
					case shed.actions.DISABLE:
						onItemDisabled(element);
						break;
					case shed.actions.ENABLE:
						onItemEnabled(element);
						break;
					default:
						console.warn("Unknown action", action);
						break;
				}
			} else if ((action === shed.actions.DISABLE || action === shed.actions.ENABLE) && element.matches(TABLIST.toString())) {
				// if the tablist is disabled or enabled, diable/enable all the tabs.
				Array.from(element.querySelectorAll(this.ITEM.toString())).forEach(next => shed[action](next));
			}
		}
	}

	/**
	 * Tab interaction functionality. Shows the tab's content if the tab is a regular tab. Toggles the
	 * visibility. of the tab's content if the tab is an accordion tab.
	 *
	 * @function module:wc/ui/tabset.activate
	 * @override
	 * @param {Element} element the tab being opened or closed.
	 */
	activate(element) {
		const container = this.getGroupContainer(element);
		if (container) {
			if (getAccordion(container)) {
				const tabset = container.closest(TABSET);
				if (tabset.getAttribute(CONVERTED)) {
					if (!shed.isExpanded(element)) {
						shed.expand(element);
					}
				} else {
					shed.toggle(element, shed.actions.EXPAND);
				}
			} else if (!shed.isSelected(element)) {
				shed.select(element);
			}
		}
	}

	/**
	 * A subscriber to {@link module:wc/dom/formUpdateManager} to write the state of tabsets during submission.
	 *
	 * @function
	 * @public
	 * @override
	 * @param {Element} container The form or sub form the state of which is being written.
	 * @param {Element} stateContainer The element into which the state is being written.
	 */
	writeState(container, stateContainer) {
		const tabsets = Array.from(container.querySelectorAll(TABLIST.toString())),
			writeTabState = writeTabStateHelper.bind(this, stateContainer);
		tabsets.forEach(writeTabState);
		if (container.matches(this.ITEM.toString())) {
			writeTabState(container.closest(TABLIST));
		}
	}

	/**
	 *
	 * @param {FocusEvent & { target: HTMLElement }} $event
	 */
	focusEvent($event) {
		const target = $event.target;
		super.focusEvent($event);
		const tab = target.closest(instance.ITEM.toString());
		if (tab) {
			const lastTab = lastTabId ? document.getElementById(lastTabId) : null;
			const lastTabList = lastTab?.closest(TABLIST);
			if (lastTabList && lastTabList !== tab.closest(TABLIST)) {
				resetFocusIndex();
			}
			lastTabId = tab.id;
		} else if (lastTabId) {
			resetFocusIndex();
		}
	}

	/**
	 * This is the keydown extension for CTRL + PAGE_UP/PAGE_DOWN to match the WAI-ARIA key map for tabsets. It
	 * does not work in webkit but is fine in other browsers. The webkit issue is due to it not triggering a
	 * key event for the CTRL+ PAGE_[UP|DOWN] combo - it is always used to navigate between the browser tabs.
	 *
	 * @function
	 * @override
	 * @param {KeyboardEvent & { target: HTMLElement }} $event The wrapped keydown event.
	*/
	keydownEvent($event) {
		const target = $event.target;

		if ($event.shiftKey || !($event.ctrlKey || $event.metaKey)) {
			super.keydownEvent($event);
			return;
		}

		// WAI-ARIA 1.1 update: these now only apply to accordions.
		if (!isInAccordion(target)) {
			return;
		}
		let targetTab;
		const keyCode = $event.key;
		if (keyCode === "PageUp" || keyCode === "PageDown") {
			const tab = getTabFor(target);
			if (tab) {
				const direction = (keyCode === "PageUp" ? instance.KEY_DIRECTION.PREVIOUS : instance.KEY_DIRECTION.NEXT);
				targetTab = instance.navigate(tab, direction);
			}
		} else if (keyCode === "ArrowUp") {
			targetTab = getTabFor(target);
		} else {
			super.keydownEvent($event);
			return;
		}

		if (targetTab) {
			$event.preventDefault();
			focus.setFocusRequest(targetTab);
		}
	}

	/**
	 * Helper for shedObserver, called when there has been a SELECT or DESELECT.
	 * @param {string} action either shed.actions.SELECT or shed.actions.DESELECT
	 * @param {HTMLElement} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
	 */
	onItemSelection(action, element) {
		let contentContainer;
		const onShown = () => {
			if (contentContainer) {
				clearSize(contentContainer);
			}
		};
		if (action === shed.actions.SELECT) {
			this.setFocusIndex(element);
			super.shedObserver(element, action);
		}
		const container = instance.getGroupContainer(element);
		if (container) {
			const content = getPanel(element);
			if (content) {

				if (!getAccordion(container)) {
					contentContainer = content.parentElement;
				}
				if (action === shed.actions.SELECT) {
					shed.show(content, true);
					containerload.onshow(content).then(onShown).catch(onShown);
				} else if (action === shed.actions.DESELECT) {
					if (contentContainer) {
						fixSize(contentContainer);  // TODO only do this if it's an AJAX tab
					}
					shed.hide(content);
				}
			}
		}
	}

	/**
	 * Extended setup for tabsets.
	 * @function module:wc/ui/tabset._extendedInitialisation
	 * @param {Element} element the element being initialised.
	 */
	_extendedInitialisation(element) {
		toggleToFromAccordions(element);
		processResponse.subscribe(toggleToFromAccordions, true);
		shed.subscribe(shed.actions.EXPAND, this.shedObserver.bind(this));
		shed.subscribe(shed.actions.COLLAPSE, this.shedObserver.bind(this));
		shed.subscribe(shed.actions.ENABLE, this.shedObserver.bind(this));
		shed.subscribe(shed.actions.DISABLE, this.shedObserver.bind(this));
		event.add(window, "resize", resizeEvent, 1);
	}
}

/**
 * Gets the value of the attribute which makes a tab set into an accordion.
 *
 * @function
 * @private
 * @param {Element} tablist The tablist to test.
 * @returns {String} The value of the aria-multiselectable attribute "true" or "false" (or undefined if not
 *    an accordion).
 */
function getAccordion(tablist) {
	return tablist.getAttribute(MULTISELECT);
}

/**
 * Collapse single-select accordion tabs when a particular tab is opened. NOTE: this is required because
 * {@link module:wc/dom/ariaAnalog#shedObserver} works on select but not on expand. Accordion tabs are
 * expanded whereas regular tabs are selected.
 *
 * @function
 * @private
 * @param {Element} element The tab being opened.
 */
function collapseOthers(element) {
	const conf = {
		filter: getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.expanded,
		containerWd: TABLIST,
		itemWd: instance.ITEM
	};

	const grp = /** @type Element[] */(getFilteredGroup(element, conf));

	for (let i = grp.length - 1; i >= 0; i--) {
		let next = grp[i];
		if (next !== element) {
			shed.collapse(next);
		}
	}
}

/**
 * Removes the custom size set when making an ajax region busy.
 *
 * @function
 * @private
 * @param {HTMLElement} element The element being made no longer busy.
 */
function clearSize (element) {
	if (element.getAttribute(UPDATE_SIZE)) {
		element.removeAttribute(UPDATE_SIZE);
		let size = element.getAttribute(OLD_WIDTH);
		if (size) {
			element.style.width = size;
		} else if (element.style.width) {
			element.style.width = "";
		}
		size = element.getAttribute(OLD_HEIGHT);
		if (size) {
			element.style.height = size;
		} else if (element.style.height) {
			element.style.height = "";
		}
	}
}

/**
 *
 * @param {HTMLElement} element
 * @returns {boolean}
 */
function fixSize (element) {
	let result = false;

	if (!element.getAttribute(UPDATE_SIZE)) {  // already targeted (ie: a conflict) therefore nothing to do
		const width = /** @type {string} */ (getStyle(element, "width", true, true));
		const height = /** @type {string} */ (getStyle(element, "height", true, true));
		if (width && height) {  // no point playing with custom sizes if the target has no size
			result = true;
			element.setAttribute(UPDATE_SIZE, "x");
			const oldWidth = element.style.width;

			if (oldWidth) {
				element.setAttribute(OLD_WIDTH, oldWidth);
			} else {
				element.style.width = width;
			}
			const oldHeight = element.style.height;
			if (oldHeight) {
				element.setAttribute(OLD_HEIGHT, oldHeight);
			} else {
				element.style.height = height;
			}
		}
	}
	return result;
}

/**
 *
 * @param {Element} tabset
 * @param expand
 */
function expandCollapseAll(tabset, expand) {
	const list = tabset.querySelector(TABLIST);

	if (!list) {
		return;
	}

	const accordion = getAccordion(list);
	if (!accordion || (expand && accordion !== TRUE)) {
		return;
	}

	const func = expand ? "expand" : "collapse";

	Array.from(list.querySelectorAll(instance.ITEM)).forEach(next => {
		if (shed.isExpanded(next) !== expand) {
			shed[func](next);
		}
	});
}

/**
 * In order to navigate a tablist we must set focusIndex as we go. Once we leave a tablist, however, we must
 * be sure that we reset the focus index so that the open tab is focussed if we TAB back into the list.
 * (http://www.w3.org/TR/wai-aria-practices/#tabpanel).
 *
 */
function resetFocusIndex() {
	try {
		const tab = lastTabId ? document.getElementById(lastTabId) : null;
		if (!tab) {
			return;
		}
		const isAccordion = getAccordion(instance.getGroupContainer(tab));
		const filter = getFilteredGroup.FILTERS.enabled | (isAccordion ? getFilteredGroup.FILTERS.expanded : getFilteredGroup.FILTERS.selected);
		const openTabs = /** @type HTMLElement[] */ (getFilteredGroup(tab, {
			filter,
			containerWd: TABLIST,
			itemWd: instance.ITEM
		}));
		if (openTabs?.length) {
			instance.setFocusIndex(openTabs[0]);
		}
	} finally {
		lastTabId = "";
	}
}

/**
 * Helper for shedObserver, called when there has been a EXPAND or COLLAPSE.
 * @param {string} action either shed.actions.EXPAND or shed.actions.COLLAPSE
 * @param {HTMLElement} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
 */
function onItemExpansion(action, element) {
	if (action === shed.actions.EXPAND) {
		instance.setFocusIndex(element);
	}
	const container = instance.getGroupContainer(element);
	if (container) {
		const content = getPanel(element);
		if (content) {
			if (action === shed.actions.EXPAND) {
				shed.show(content);
				if (getAccordion(container) === FALSE) {
					collapseOthers(element);
				}
			} else if (action === shed.actions.COLLAPSE) {
				shed.hide(content);
			}
		}
	}
}

/**
 * When disabling an open tab we may have to reset tab indexes. Helper for shedObserver.
 * @function
 * @private
 * @param {HTMLElement} element The element being disabled.
 */
function onItemDisabled(element) {
	if (element.tabIndex !== 0) {  // We only care if we are disabling a focusable tab.
		return;
	}

	const container = instance.getGroupContainer(element);
	if (!container) {  // this should never happen...
		return;
	}

	const isAccordion = getAccordion(container);
	const open = isAccordion ? shed.isExpanded(element) : shed.isSelected(element);
	if (!open) {  // we really do not care if closed
		return;
	}
	let group;
	if (isAccordion) {
		// are there any other open tabs?
		group = /** @type HTMLElement[] */ (getFilteredGroup(element, {
			filter: getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.expanded,
			containerWd: TABLIST,
			itemWd: instance.ITEM
		}));
		if (group?.length) {
			return;  // we have other open tabs.
		}
	}

	group = /** @type HTMLElement[] */ (getFilteredGroup(element, {
		filter: getFilteredGroup.FILTERS.enabled,
		containerWd: TABLIST,
		itemWd: instance.ITEM
	}));
	if (group?.length) {
		element.tabIndex = -1;
		group[0].tabIndex = 0;
	}
}

/**
 * When disabling a tab we may have to fix tabIndex. Helper for shedObserver.
 * @function
 * @private
 * @param {HTMLElement} element The element being enabled.
 */
function onItemEnabled(element) {
	if (element.tabIndex !== -1) {  // if we have not made this tab unfocusable we don't care what it is, it should come out in the wash..
		return;
	}

	const container = instance.getGroupContainer(element);
	if (!container) {  // this should never happen...
		return;
	}

	const isAccordion = getAccordion(container);
	const open = isAccordion ? shed.isExpanded(element) : shed.isSelected(element);

	if (open) {  // if it is open set its tabIndec to 0 and exit.
		element.tabIndex = 0;
		return;
	}

	// Are there any open tabs?
	const filter = getFilteredGroup.FILTERS.enabled | (isAccordion ? getFilteredGroup.FILTERS.expanded : getFilteredGroup.FILTERS.selected);
	const group = /** @type HTMLElement[] */ (getFilteredGroup(element, {
		filter,
		containerWd: TABLIST,
		itemWd: instance.ITEM
	}));
	if (group && group.length) {  // yes, we have an open tab so set this tabInde to -1 and exit.
		element.tabIndex = -1;
		return;
	}

	// no other open tabs so this one may as well be focusable.
	element.tabIndex = 0;
}

/** NOTE:
 * next is a tablist. The tabset container element is the tablist's parent element.
 * If the tabset is disabled, the parent element has the aria-disabled="true" flag.
 */
function writeTabStateHelper(stateContainer, next) {
	const config = {
			asObject: true,
			filter: getFilteredGroup.FILTERS.enabled
		},
		tabsetName = next.parentNode.id;
	if (!(shed.isDisabled(next) || shed.isDisabled(next.parentElement))) {
		if (getAccordion(next)) {
			config.filter = config.filter | getFilteredGroup.FILTERS.expanded;
		} else {
			config.filter = config.filter | getFilteredGroup.FILTERS.selected;
		}
		const tabs = /** @type {{ filtered: HTMLElement[], unfiltered: HTMLElement[] }} */(getFilteredGroup(next, config));
		if (tabs) {
			const selected = tabs.filtered;
			const all = tabs.unfiltered;
			if (!selected.length) {
				// no open tabs (or all individually disabled)
				formUpdateManager.writeStateField(stateContainer, tabsetName, "", false, true);
			} else {
				selected.forEach(theTab => {
					const position = String(all.indexOf(theTab));
					formUpdateManager.writeStateField(stateContainer, tabsetName, position, false, true);
				});
			}
		}
	}
}

/**
 * Gets the tab control which is the controller of the tab panel in which a given element exists.
 *
 * @function
 * @private
 * @param {Element} element An element.
 * @returns {Element} A tab element or null if element is not inside a tab panel.
 */
function getTabFor(element) {
	if (element.closest(instance.ITEM.toString())) {
		return null;
	}
	const panel = element.closest(TABPANEL);
	const panelId = panel?.id;
	if (panelId) {
		const tabset = panel.closest(TABSET);
		if (tabset) {
			const tabWidget = instance.ITEM + `[aria-controls='${panelId}']`;
			return tabset.querySelector(tabWidget.toString());
		}
	}
	return null;
}


/**
 * Is an element in an accordion tabset?
 *
 * @function
 * @private
 * @param {Element} element the element we are testing
 * @returns {Boolean} true if element is inside an accordion tabset.
 */
function isInAccordion(element) {
	let tablist = element.closest(TABPANEL.toString());
	tablist = tablist?.parentElement;
	tablist = tablist?.parentElement;
	if (tablist && tablist.matches(TABLIST.toString())) {
		return !!getAccordion(tablist);
	}
	return false;
}

/**
 * Get the tabPanel for a tab.
 * @function
 * @private
 * @param {Element} tab
 * @returns {HTMLElement} the tab panel for the tab.
 */
function getPanel(tab) {
	const panelId = tab.getAttribute("aria-controls");
	if (!panelId) {
		console.warn("tab does not control a tabPanel");
		return null;
	}
	return document.getElementById(panelId);
}

/**
 * Converts a temporary accordion back into a regular tabset.
 * @function
 * @private
 * @param {Element} accordion the accordion
 */
function accordionToTabset(accordion) {
	if (!(accordion.matches(TABSET.toString()) && accordion.getAttribute(CONVERTED))) {
		return;
	}
	const tablist = Array.from(accordion.children).find(child => child.matches(TABLIST.toString()));
	if (!(tablist && getAccordion(tablist))) {
		return;
	}
	let successful = false;
	try {
		const candidates = Array.from(tablist.children).filter(child => child.matches(instance.ITEM.toString()));
		candidates.forEach(tab => {
			const tabPanel = getPanel(tab);
			if (tabPanel) {
				accordion.appendChild(tabPanel);
			}
			tab.setAttribute("aria-selected", shed.isExpanded(tab) ? TRUE : FALSE);
			tab.removeAttribute("aria-expanded");
		});
		successful = true;
	} finally {
		if (successful) {
			accordion.classList.remove(ACCORDION_CLASS);
			if (accordion.classList.contains("wc-tabset-type-left") || accordion.classList.contains("wc-tabset-type-right")) {
				tablist.setAttribute("aria-orientation", "vertical");
			}
			tablist.removeAttribute(MULTISELECT);
			accordion.removeAttribute(CONVERTED);
		}
	}
}

/**
 * Converts a normal tabset into an accordion.
 * @function
 * @private
 * @param {HTMLElement} tabset the tabset to convert.
 */
function tabsetToAccordion(tabset) {
	if (!tabset.matches(TABSET.toString()) ) {
		return;
	}
	const tablist = Array.from(tabset.children).find(kid => kid.matches(TABLIST));
	if (!tablist || getAccordion(tablist)) {
		return;
	}
	let successful = false;
	try {
		const candidates = Array.from(tablist.children).filter(kid => kid.matches(instance.ITEM));
		candidates.forEach(tab => {
			const tabPanel = getPanel(tab),
				parent = tab.parentElement;
			if (tabPanel) {
				if (tab.nextElementSibling) {
					parent.insertBefore(tabPanel, tab.nextElementSibling);
				} else {
					parent.appendChild(tabPanel);
				}
			}

			tab.setAttribute("aria-expanded", shed.isSelected(tab) ? TRUE : FALSE);
			tab.removeAttribute("aria-selected");
		});
		successful = true;
	} finally {
		if (successful) {
			tabset.classList.add(ACCORDION_CLASS);
			tablist.setAttribute(MULTISELECT, FALSE); // must be a single select accordion.
			tablist.removeAttribute("aria-orientation");
			clearSize(tabset);
			tabset.setAttribute(CONVERTED, TRUE);
		}
	}
}

/**
 * Find tabset in a container and convert them if necessary.
 * @function
 * @private
 * @param {Element} [container]
 */
function toggleToFromAccordions(container) {
	const element = container || document.body;
	const candidates = element.matches(TABSET) ? [element] : Array.from(element.querySelectorAll(TABSET));
	if (!candidates.length) {
		return;
	}

	if (viewportUtils.isSmallScreen()) {
		candidates.forEach(tabsetToAccordion);
	} else {
		candidates.forEach(accordionToTabset);
	}
}

const instance = new Tabset();
initialise.register(instance);
export default instance;
