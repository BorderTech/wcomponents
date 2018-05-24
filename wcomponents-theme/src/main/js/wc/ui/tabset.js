/**
 * Provides tab and tab set functionality.
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/containerload
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/classList
 * @requires module:wc/ui/viewportUtils
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/dom/event
 *
 */
define(["wc/array/toArray",
	"wc/dom/ariaAnalog",
	"wc/dom/formUpdateManager",
	"wc/dom/getFilteredGroup",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/ui/containerload",
	"wc/dom/focus",
	"wc/dom/classList",
	"wc/ui/viewportUtils",
	"wc/ui/ajax/processResponse",
	"wc/dom/event",
	"wc/debounce",
	"wc/dom/getStyle"],
	function(toArray, ariaAnalog, formUpdateManager, getFilteredGroup, initialise, shed, Widget, containerload,
		focus, classList, viewportUtils, processResponse, event, debounce, getStyle) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/tabset~Tabset
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function Tabset() {
			var
				/**
				 * The description of a tab list.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				TABLIST = new Widget("", "", {"role": "tablist"}),

				/**
				 * The description of a tab panel.
				 * @var
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				TABPANEL = new Widget("", "", {"role": "tabpanel"}),

				/**
				 * The description of a tab set.
				 * @var
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				TABSET = new Widget("", "wc-tabset"),

				/**
				 * The ID of the last focused tab. This is needed to reset focusability to the open tab in a tabset
				 * once focus leaves the tablist.
				 * @var
				 * @type String
				 * @private
				 */
				lastTabId,
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
			 * The description of a tab control.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ITEM = new Widget("", "", {"role": "tab"});

			/**
			 * Select items immediately on navigation.
			 * @function
			 * @protected
			 * @param {Element} element the tab being navigated to
			 * @returns {Boolean} true unless the tab is in an accordion.
			 * @override
			 */
			this.selectOnNavigate = function(element) {
				var container = this.getGroupContainer(element);

				if (container && container.getAttribute(MULTISELECT)) {
					return false;
				}
				return true;
			};

			/**
			 * The selection mode for the group of tabs context. The select mode is mixed as accordions may be
			 * multi-selectable.
			 *
			 * @var
			 * @type {int}
			 * @default 2
			 * @public
			 * @override
			 */
			this.exclusiveSelect = this.SELECT_MODE.MIXED;

			/**
			 * Indicates is keyboard navigation should cycle between the end points of the tablist.
			 * @constant
			 * @type {Boolean}
			 * @default true
			 * @protected
			 * @override
			 */
			this._cycle = true;

			/**
			 * Get the descriptor of the list component. This is required by some custom implementations but is not
			 * currently used in WComponents core.
			 *
			 * TODO: remove this functionality and set up the TABLIST Widget independently where required.
			 *
			 * @function module:wc/ui/tabset.getList
			 * @public
			 * @returns {module:wc/dom/Widget} The TABLIST Widget.
			 */
			this.getList = function() {
				return TABLIST;
			};

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
				var i, next, grp,
					conf = {filter: getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.expanded, containerWd: TABLIST, itemWd: instance.ITEM};
				grp = getFilteredGroup(element, conf);

				for (i = grp.length - 1; i >= 0; i--) {
					next = grp[i];
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
			 * @param {Element} element The element being made no longer busy.
			 */
			function clearSize (element) {
				var size;
				if (element.getAttribute(UPDATE_SIZE)) {
					element.removeAttribute(UPDATE_SIZE);
					if ((size = element.getAttribute(OLD_WIDTH))) {
						element.style.width = size;
					} else if (element.style.width) {
						element.style.width = "";
					}
					if ((size = element.getAttribute(OLD_HEIGHT))) {
						element.style.height = size;
					} else if (element.style.height) {
						element.style.height = "";
					}
				}
			}

			function fixSize (element) {
				var result = false, width, height, oldWidth, oldHeight;

				if (!element.getAttribute(UPDATE_SIZE)) {  // already targeted (ie: a conflict) therefore nothing to do
					width = getStyle(element, "width", true, true);
					height = getStyle(element, "height", true, true);
					if (width && height) { // no point playing with custom sizes if the target has no size
						result = true;
						element.setAttribute(UPDATE_SIZE, "x");
						oldWidth = element.style.width;

						if (oldWidth) {
							element.setAttribute(OLD_WIDTH, oldWidth);
						} else {
							element.style.width = width;
						}
						oldHeight = element.style.height;
						if (oldHeight) {
							element.setAttribute(OLD_HEIGHT, oldHeight);
						} else {
							element.style.height = height;
						}
					}
				}
				return result;
			}

			function expandCollapseAll(tabset, expand) {
				var list = TABLIST.findDescendant(tabset),
					func,
					accordion;

				if (!list) {
					return;
				}

				accordion = getAccordion(list);
				if (!accordion || (expand && accordion !== TRUE)) {
					return;
				}

				func = expand ? "expand" : "collapse";

				Array.prototype.forEach.call(instance.ITEM.findDescendants(list), function(next) {
					if (shed.isExpanded(next) !== expand) {
						shed[func](next);
					}
				});
			}

			this.collapseAll = function(tabset) {
				expandCollapseAll(tabset, false);
			};

			this.expandAll = function(tabset) {
				expandCollapseAll(tabset, true);
			};

			/**
			 * Are all tabs in a tabset inthe same expanded state?
			 * @param {Element} tabset The tabset to test
			 * @param {boolean} expanded true is testing for
			 * @returns {Boolean}
			 */
			this.areAllInExpandedState = function(tabset, expanded) {
				var list = TABLIST.findDescendant(tabset), accordion;

				if (!(list && (accordion = getAccordion(list)))) { // only accordions can have all items in a state.
					return false;
				}

				if (accordion === FALSE && expanded) {
					return false; // single accordion all in the same state only if collapsed.
				}

				return Array.prototype.every.call(this.ITEM.findDescendants(list), function(next) {
					if (shed.isExpanded(next) === expanded) {
						return true;
					}
				});
			};

			/**
			 * Helper for shedObserver, called when there has been a EXPAND or COLLAPSE.
			 * @param {string} action either shed.actions.EXPAND or shed.actions.COLLAPSE
			 * @param {Element} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
			 */
			function onItemExpansion(action, element) {
				var content,
					container;
				if (action === shed.actions.EXPAND) {
					instance.setFocusIndex(element);
				}
				container = instance.getGroupContainer(element);
				if (container) {
					if ((content = getPanel(element))) {
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
			 * Helper for shedObserver, called when there has been a SELECT or DESELECT.
			 * @param {string} action either shed.actions.SELECT or shed.actions.DESELECT
			 * @param {Element} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
			 */
			function onItemSelection(action, element) {
				var content,
					contentContainer,
					container,
					onShown = function() {
						if (contentContainer) {
							clearSize(contentContainer);
						}
					};

				if (action === shed.actions.SELECT) {
					instance.setFocusIndex(element);
					instance.constructor.prototype.shedObserver.call(instance, element, action);
				}
				container = instance.getGroupContainer(element);
				if (container) {
					if ((content = getPanel(element))) {
						if (!getAccordion(container)) {
							contentContainer = content.parentNode;
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
			 * When disabling an open tab we may have to reset tabindexes. Helper for shedObserver.
			 * @function
			 * @private
			 * @param {Element} element The element being disabled.
			 */
			function onItemDisabled(element) {
				var container,
					isAccordion,
					group,
					open;

				if (element.tabIndex !== "0") { // We only care if we are disabling a focusable tab.
					return;
				}

				container = instance.getGroupContainer(element);
				if (!container) { // this should never happen...
					return;
				}

				isAccordion = getAccordion(container);
				open = isAccordion ? shed.isExpanded(element) : shed.isSelected(element);
				if (!open) { // we really do not care if closed
					return;
				}

				if (isAccordion) {
					// are there any other open tabs?
					group = getFilteredGroup(element, {filter: getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.expanded, containerWd: TABLIST, itemWd: instance.ITEM});
					if (group && group.length) {
						return; // we have other open tabs.
					}
				}

				group = getFilteredGroup(element, {filter: getFilteredGroup.FILTERS.enabled, containerWd: TABLIST, itemWd: instance.ITEM});
				if (group && group.length) {
					element.tabIndex = "-1";
					group[0].tabIndex = "0";
				}
			}

			/**
			 * When disabling a tab we may have to fix tabIndex. Helper for shedObserver.
			 * @function
			 * @private
			 * @param {Element} element The element being enabled.
			 */
			function onItemEnabled(element) {
				var container,
					isAccordion,
					group,
					open,
					filter;

				if (element.tabIndex !== "-1") { // if we have not made this tab unfocusable we don't care what it is, it should come out in the wash..
					return;
				}

				container = instance.getGroupContainer(element);
				if (!container) { // this should never happen...
					return;
				}

				isAccordion = getAccordion(container);
				open = isAccordion ? shed.isExpanded(element) : shed.isSelected(element);

				if (open) { // if it is open set its tabIndec to 0 and exit.
					element.tabIndex = "0";
					return;
				}

				// Are there any open tabs?
				filter = getFilteredGroup.FILTERS.enabled | (isAccordion ? getFilteredGroup.FILTERS.expanded : getFilteredGroup.FILTERS.selected);
				group = getFilteredGroup(element, {filter: filter, containerWd: TABLIST, itemWd: instance.ITEM});
				if (group && group.length) { // yes, we have an open tab so set this tabInde to -1 and exit.
					element.tabIndex = "-1";
					return;
				}

				// no other open tabs so this one may as well be focusable.
				element.tabIndex = "0";
			}

			/**
			 * A subscriber to {@link module:wc/dom/shed} to react to these pseudo-events.
			 *
			 * @function module:wc/ui/tabset.shedObserver
			 * @protected
			 * @override
			 * @param {Element} element The element on which the shed action acted.
			 * @param {String} action The type of shed event. One of EXPAND, COLLAPSE, SELECT or DESELECT.
			 */
			this.shedObserver = function (element, action) {
				if (element) {
					if (this.ITEM.isOneOfMe(element)) {
						switch (action) {
							case shed.actions.SELECT:
							case shed.actions.DESELECT:
								onItemSelection(action, element);
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
					} else if ((action === shed.actions.DISABLE || action === shed.actions.ENABLE) && TABLIST.isOneOfMe(element)) {
						// if the tablist is disabled or enabled, diable/enable all the tabs.
						Array.prototype.forEach.call(this.ITEM.findDescendants(element), function (next) {
							shed[action](next);
						});
					}
				}
			};

			/**
			 * Tab interaction functionality. Shows the tab's content if the tab is a regular tab. Toggles the
			 * visibility. ofthe tab's content if the tab is an accordion tab.
			 *
			 * @function module:wc/ui/tabset.activate
			 * @protected
			 * @override
			 * @param {Element} element the tab being opened or closed.
			 */
			this.activate = function(element) {
				var container = this.getGroupContainer(element), tabset;

				if (container) {
					if (getAccordion(container)) {
						tabset = TABSET.findAncestor(container);
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
			};

			/**
			 * A subscriber to {@link module:wc/dom/formUpdateManager} to write the state of tabsets during submission.
			 *
			 * @function
			 * @public
			 * @override
			 * @param {Element} container The form or sub form the state of which is being written.
			 * @param {Element} stateContainer The element into which the state is being written.
			 */
			this.writeState = function(container, stateContainer) {
				var tabsets = TABLIST.findDescendants(container),
					writeTabState = writeTabStateHelper.bind(this, stateContainer);

				if (tabsets.length) {
					Array.prototype.forEach.call(tabsets, writeTabState);
				}
				if (this.ITEM.isOneOfMe(container)) {
					writeTabState(TABLIST.findAncestor(container));
				}
			};

			/**
			 * In order to navigate a tablist we must set focusIndex as we go. Once we leave a tablist, however, we must
			 * be sure that we reset the focus index so that the open tab is focussed if we TAB back into the list.
			 * (http://www.w3.org/TR/wai-aria-practices/#tabpanel).
			 *
			 */
			function resetFocusIndex() {
				var tab, openTabs, isAccordion, filter;
				try {
					if (!(lastTabId && (tab = document.getElementById(lastTabId)))) {
						return;
					}
					isAccordion = getAccordion(instance.getGroupContainer(tab));
					filter = getFilteredGroup.FILTERS.enabled | (isAccordion ? getFilteredGroup.FILTERS.expanded : getFilteredGroup.FILTERS.selected);
					openTabs = getFilteredGroup(tab, {filter: filter, containerWd: TABLIST, itemWd: instance.ITEM});
					if (openTabs && openTabs.length) {
						instance.setFocusIndex(openTabs[0]);
					}
				} finally {
					lastTabId = "";
				}
			}

			this.focusEvent = function($event) {
				var target = $event.target, tab, lastTab, lastTabList;
				this.constructor.prototype.focusEvent.call(this, $event);

				if ((tab = instance.ITEM.findAncestor(target))) {
					if (lastTabId && (lastTab = document.getElementById(lastTabId))) {
						lastTabList = TABLIST.findAncestor(lastTab);
					}
					if (lastTabList && lastTabList !== TABLIST.findAncestor(tab)) {
						resetFocusIndex();
					}
					lastTabId = tab.id;
				} else if (lastTabId) {
					resetFocusIndex();
				}
			};

			/* NOTE:
			 * next is a tablist. The tabset container element is the tablist's parent element.
			 * If the tabset is disabled, the parent element has the aria-disabled="true" flag.
			 */
			function writeTabStateHelper(stateContainer, next) {
				var tabs, selected, position, config = {asObject: true, filter: getFilteredGroup.FILTERS.enabled}, tabsetName = next.parentNode.id;
				if (!(shed.isDisabled(next) || shed.isDisabled(next.parentElement))) {
					if (getAccordion(next)) {
						config.filter = config.filter | getFilteredGroup.FILTERS.expanded;
					} else {
						config.filter = config.filter | getFilteredGroup.FILTERS.selected;
					}
					if ((tabs = getFilteredGroup(next, config))) {
						selected = tabs.filtered;
						tabs = tabs.unfiltered;
						if (!selected.length) {
							// no open tabs (or all individually disabled)
							formUpdateManager.writeStateField(stateContainer, tabsetName, "", false, true);
						} else {
							selected.forEach(function(theTab) {
								position = tabs.indexOf(theTab);
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
			 * @returns {Element} A tab element or undefined if element is not inside a tab panel.
			 */
			function getTabFor(element) {
				var result,
					panel,
					panelId,
					tabWidget,
					tabset;

				if (instance.ITEM.findAncestor(element)) {
					return null;
				}

				if ((panel = TABPANEL.findAncestor(element)) && (panelId = panel.id)) {
					if ((tabset = TABSET.findAncestor(panel))) {
						tabWidget = instance.ITEM.extend("", {"aria-controls": panelId});
						result = tabWidget.findDescendant(tabset);
					}
				}
				return result;
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
				var tablist;
				if ((tablist = TABPANEL.findAncestor(element)) && (tablist = tablist.parentNode) &&  TABLIST.isOneOfMe(tablist)) {
					return !!getAccordion(tablist);
				}
				return false;
			}

			/**
			 * This is the keydown extension for CTRL + PAGE_UP/PAGE_DOWN to match the WAI-ARIA key map for tabsets. It
			 * does not work in webkit but is fine in other browsers. The webkit issue is due to it not triggering a
			 * key event for the CTRL+ PAGE_[UP|DOWN] combo - it is always used to navigate between the browser tabs.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Event} $event The wrapped keydown event.
			*/
			this.keydownEvent = function($event) {
				var keyCode = $event.keyCode,
					target = $event.target,
					tab,
					targetTab,
					direction;

				if ($event.shiftKey || !($event.ctrlKey || $event.metaKey)) {
					this.constructor.prototype.keydownEvent.call(this, $event);
					return;
				}

				// WAI-ARIA 1.1 update: these now only apply to accordions.
				if (!isInAccordion(target)) {
					return;
				}

				if (keyCode === KeyEvent.DOM_VK_PAGE_UP || keyCode === KeyEvent.DOM_VK_PAGE_DOWN) {
					tab = getTabFor(target);
					if (tab) {
						direction = (keyCode === KeyEvent.DOM_VK_PAGE_UP ? instance.KEY_DIRECTION.PREVIOUS : instance.KEY_DIRECTION.NEXT);
						targetTab = instance.navigate(tab, direction);
					}
				} else if (keyCode === KeyEvent.DOM_VK_UP) {
					targetTab = getTabFor(target);
				} else {
					this.constructor.prototype.keydownEvent.call(this, $event);
					return;
				}

				if (targetTab) {
					$event.preventDefault();
					focus.setFocusRequest(targetTab);
				}
			};

			/**
			 * Get the tabPanel for a tab.
			 * @function
			 * @private
			 * @param {Element} tab
			 * @returns {Element} the tab panel for the tab.
			 */
			function getPanel(tab) {
				var panelId;
				if (!(panelId = tab.getAttribute("aria-controls"))) {
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
				var tablist,
					successful,
					candidates;
				if (!(TABSET.isOneOfMe(accordion) && accordion.getAttribute(CONVERTED))) {
					return;
				}
				tablist = TABLIST.findDescendant(accordion, true);
				if (!(tablist && getAccordion(tablist))) {
					return;
				}

				try {
					if ((candidates = instance.ITEM.findDescendants(tablist, true)) && candidates.length) {
						Array.prototype.forEach.call(candidates, function(tab) {
							var tabPanel = getPanel(tab);
							if (tabPanel) {
								accordion.appendChild(tabPanel);
							}
							tab.setAttribute("aria-selected", shed.isExpanded(tab) ? TRUE : FALSE);
							tab.removeAttribute("aria-expanded");
						});
						successful = true;
					}
				} finally {
					if (successful) {
						classList.remove(accordion, ACCORDION_CLASS);
						if (classList.contains(accordion, "wc-tabset-type-left") || classList.contains(accordion, "wc-tabset-type-right")) {
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
			 * @param {Element} tabset the tabset to convert.
			 */
			function tabsetToAccordion(tabset) {
				var tablist,
					successful,
					candidates;
				if (!TABSET.isOneOfMe(tabset) ) {
					return;
				}
				tablist = TABLIST.findDescendant(tabset, true);
				if (!tablist || getAccordion(tablist)) {
					return;
				}
				try {
					if ((candidates = instance.ITEM.findDescendants(tablist, true)) && candidates.length) {
						Array.prototype.forEach.call(candidates, function(tab) {
							var tabPanel = getPanel(tab),
								parent = tab.parentNode;
							if (tabPanel) {
								if (tab.nextSibling) {
									parent.insertBefore(tabPanel, tab.nextSibling);
								} else {
									parent.appendChild(tabPanel);
								}
							}

							tab.setAttribute("aria-expanded", shed.isSelected(tab) ? TRUE : FALSE);
							tab.removeAttribute("aria-selected");
						});
						successful = true;
					}
				} finally {
					if (successful) {
						classList.add(tabset, ACCORDION_CLASS);
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
				var candidates,
					element = container || document.body;

				if (TABSET.isOneOfMe(element)) {
					candidates = [element];
				} else {
					candidates = toArray(TABSET.findDescendants(element));
				}

				if (!candidates.length) {
					return;
				}

				if (viewportUtils.isSmallScreen()) {
					candidates.forEach(tabsetToAccordion);
				} else {
					candidates.forEach(accordionToTabset);
				}
			}

			/**
			 * Extended setup for tabsets.
			 * @function module:wc/ui/tabset._extendedInitialisation
			 * @protected
			 * @param {Element} element the element being initialised.
			 */
			this._extendedInitialisation = function(element) {
				toggleToFromAccordions(element);
				processResponse.subscribe(toggleToFromAccordions, true);
				shed.subscribe(shed.actions.EXPAND, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.COLLAPSE, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.ENABLE, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.DISABLE, this.shedObserver.bind(this));
				event.add(window, event.TYPE.resize, resizeEvent, 1);
			};
		}

		Tabset.prototype = ariaAnalog;
		var /** @alias module:wc/ui/tabset */ instance = new Tabset();
		instance.constructor = Tabset;
		initialise.register(instance);
		return instance;
	});
