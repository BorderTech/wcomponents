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
 * @requires module:wc/ajax/setLoading
 */
define(["wc/dom/ariaAnalog",
		"wc/dom/formUpdateManager",
		"wc/dom/getFilteredGroup",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/ui/containerload",
		"wc/ajax/setLoading"],
	function(ariaAnalog, formUpdateManager, getFilteredGroup, initialise, shed, Widget, containerload, setLoading) {
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
				TABPANEL,

				/**
				 * The description of a tab set.
				 * @var
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				TABSET,

				/**
				 * The attribute on a TAB which points to its content tab panel.
				 * @var
				 * @type String
				 * @private
				 */
				CONTENT_ATTRIB = "aria-controls";

			/**
			 * The description of a tab control.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ITEM = new Widget("", "", {"role": "tab"});

			/**
			 * Do not automatically select a tab when navigating with the keyboard. NOTE: this directly contravenes the
			 * WAI-ARIA keyboard guidelines for a Tab Panel widget but automatically activating a tab on navigate causes
			 * all sorts of usability problems.
			 *
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#tabpanel}
			 * @constant
			 * @type {Boolean}
			 * @protected
			 * @override
			 * @default false
			 */
			this.selectOnNavigate = false;

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
			 * @param {Element} tabset The tabset to test.
			 * @returns {String} The value of the aria-multiselectable attribute "true" or "false" (or undefined if not
			 *    an accordion).
			 */
			function getAccordion(tabset) {
				return tabset.getAttribute("aria-multiselectable");
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
			 * Helper for shedObserver, called when there has been a EXPAND or COLLAPSE.
			 * @param {Tabset} tabset The tabset controller instance.
			 * @param {string} action either shed.actions.EXPAND or shed.actions.COLLAPSE
			 * @param {Element} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
			 */
			function onItemExpansion(tabset, action, element) {
				var contentId,
					content,
					container;

				container = tabset.getGroupContainer(element);
				if (container) {
					if ((contentId = element.getAttribute(CONTENT_ATTRIB)) && (content = document.getElementById(contentId))) {
						if (action === shed.actions.EXPAND) {
							shed.show(content);
							if (getAccordion(container) === "false") {
								collapseOthers(element);
							}
						}
						else if (action === shed.actions.COLLAPSE) {
							shed.hide(content);

						}
					}
				}

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
					if ((action === shed.actions.SELECT || action === shed.actions.DESELECT) && this.ITEM.isOneOfMe(element)) {
						onItemSelection(this, action, element);
					}
					if ((action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) && this.ITEM.isOneOfMe(element)) {
						onItemExpansion(this, action, element);
					}
					else if ((action === shed.actions.DISABLE || action === shed.actions.ENABLE)  && TABLIST.isOneOfMe(element)) {
						// if the tablist is disabled or enabled, diable/enable all the tabs.
						Array.prototype.forEach.call(this.ITEM.findDescendants(element), function (next) {
							shed[action](next);
						});
					}
				}
			};

			/**
			 * Helper for shedObserver, called when there has been a SELECT or DESELECT.
			 * @param {Tabset} tabset The tabset controller instance.
			 * @param {string} action either shed.actions.SELECT or shed.actions.DESELECT
			 * @param {Element} element Guaranteed to pass `this.ITEM.isOneOfMe(element)`
			 */
			function onItemSelection(tabset, action, element) {
				var contentId,
					content,
					contentContainer,
					container;

				if (action === shed.actions.SELECT) {
					tabset.constructor.prototype.shedObserver.call(tabset, element, action);
				}
				container = tabset.getGroupContainer(element);
				if (container) {
					if ((contentId = element.getAttribute(CONTENT_ATTRIB)) && (content = document.getElementById(contentId))) {
						if (!getAccordion(container)) {
							contentContainer = content.parentNode;
						}
						if (action === shed.actions.SELECT) {
							shed.show(content, true);
							containerload.onshow(content).then(function() {
								setLoading.clearSize(contentContainer);
							});
						}
						else if (action === shed.actions.DESELECT) {
							if (contentContainer) {
								setLoading.fixSize(contentContainer);  // TODO only do this if it's an AJAX tab
							}
							shed.hide(content);
						}
					}
				}
			}

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
				var container = this.getGroupContainer(element);

				if (container) {
					if (getAccordion(container)) {
						shed.toggle(element, shed.actions.EXPAND);
					}
					else if (!shed.isSelected(element)) {
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
			 * Extended setup for tabsets.
			 * @function module:wc/ui/tabset._extendedInitialisation
			 * @protected
			 */
			this._extendedInitialisation = function() {
				shed.subscribe(shed.actions.EXPAND, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.COLLAPSE, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.ENABLE, this.shedObserver.bind(this));
				shed.subscribe(shed.actions.DISABLE, this.shedObserver.bind(this));
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
					}
					else {
						config.filter = config.filter | getFilteredGroup.FILTERS.selected;
					}
					if ((tabs = getFilteredGroup(next, config))) {
						selected = tabs.filtered;
						tabs = tabs.unfiltered;
						if (!selected.length) {
							// no open tabs (or all individually disabled)
							formUpdateManager.writeStateField(stateContainer, tabsetName, "");
						}
						else {
							selected.forEach(function(theTab) {
								position = tabs.indexOf(theTab);
								formUpdateManager.writeStateField(stateContainer, tabsetName, position);
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

				TABPANEL = TABPANEL || new Widget("", "", {"role": "tabpanel"});

				if ((panel = TABPANEL.findAncestor(element)) && (panelId = panel.id)) {
					TABSET = TABSET || new Widget("", "wc-tabset");
					if ((tabset = TABSET.findAncestor(panel))) {
						tabWidget = instance.ITEM.extend("", {"aria-controls": panelId});
						result = tabWidget.findDescendant(tabset);
					}
				}
				return result;
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

				if (keyCode === KeyEvent.DOM_VK_PAGE_UP || keyCode === KeyEvent.DOM_VK_PAGE_DOWN) {
					tab = getTabFor(target);
					if (tab) {
						direction = (keyCode === KeyEvent.DOM_VK_PAGE_UP ? instance.KEY_DIRECTION.NEXT : instance.KEY_DIRECTION.PREVIOUS);
						targetTab = instance.navigate(tab, direction);
						if (targetTab) {
							$event.preventDefault();
							instance.activate(targetTab, false, true);
						}
					}
				}
				else {
					this.constructor.prototype.keydownEvent.call(this, $event);
				}
			};
		}

		Tabset.prototype = ariaAnalog;
		var /** @alias module:wc/ui/tabset */ instance = new Tabset();
		instance.constructor = Tabset;
		initialise.register(instance);
		return instance;
	});
