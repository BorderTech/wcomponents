define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/onloadFocusControl",
		"wc/timers",
		"wc/ui/table/common",
		"wc/i18n/i18n"],
	function(attribute, event, focus, formUpdateManager, initialise, shed, Widget, ajaxRegion, processResponse, onloadFocusControl, timers, common, i18n) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/table/pagination~Pagination
		 * @private
		 */
		function Pagination() {
			var BOOTSTRAP_PARAM = "wc.ui.table.pagination.bootstrapped",
				IDX_BUTTON = {
					FIRST: 0,
					PREV: 1,
					NEXT: 2,
					LAST: 3
				},
				TABLE_WRAPPER = common.WRAPPER,
				SELECTOR = new Widget("select"),
				PAGINATION_SELECTOR = SELECTOR.extend("wc_table_pag_select"),
				RPP_SELECTOR = SELECTOR.extend("wc_table_pag_rpp"),
				PAGE = common.TBODY,
				ROW = common.TR.extend("wc_table_pag_row"),
				PAGINATION_CONTAINER = new Widget("", "wc_table_pag_cont"),
				PAGINATION_LABEL_WRAPPER,
				TABLE = common.TABLE,
				PAGINATION_BUTTON = common.BUTTON.clone(),
				START_ELEMENT,
				END_ELEMENT,
				updateQueue,
				triggerButtonId,
				BUSY = "aria-busy",
				PAGE_ATTRIB = "data-wc-pages",
				NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS = 4, // this is the number of selections to show around the current page option.
				NUM_PAGE_OPTIONS = 2 * NUM_BEFORE_AFTER_CURRENT_PAGE_OPTIONS + 3; // This weird number gives us FIRST (4 before selected) SELECTED (4 after selected) LAST.
			SELECTOR.descendFrom(PAGINATION_CONTAINER);
			PAGINATION_BUTTON.descendFrom(PAGINATION_CONTAINER);

			function getWrapper(element) {
				return TABLE_WRAPPER.findAncestor(element);
			}

			function isAjax(element) {
				var wrapper = getWrapper(element);
				if (wrapper) {
					return wrapper.getAttribute("data-wc-pagemode") === "dynamic";
				}
				return false;
			}

			/**
			 * Helper for updateSelectOptions and setUpPageSelectOptions.
			 * @param {int} currentPage The page currently being shown.
			 * @param {type} totalPages The number of pages in the table.
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
				PAGINATION_LABEL_WRAPPER = PAGINATION_LABEL_WRAPPER || new Widget("", "wc_table_pag_rows");
				var labels = PAGINATION_LABEL_WRAPPER.findDescendants(wrapper);

				Array.prototype.forEach.call(labels, function (next) {
					var rows, rpp, currentPage, i18nString, startIdx, endIdx;
					if (getWrapper(next) === wrapper) {
						// we have the correct spans
						rows = next.getAttribute("data-wc-tablerows");
						rpp = next.getAttribute("data-wc-tablerpp");
						currentPage = next.getAttribute("data-wc-tablepage");
						if (!(rows && rpp)) {
							return;
						}
						rows = parseInt(rows, 10);
						rpp = parseInt(rpp, 10);
						currentPage = parseInt(currentPage, 10); // currentpage is 0 based.
						startIdx = (rpp * currentPage) + 1;
						if (rpp === 1) {
							i18nString = i18n.get("table_pagination_label_one", startIdx, rows);
						}
						else {
							endIdx = Math.min(rows, (rpp * currentPage) + rpp);
							if (startIdx === endIdx) {
								i18nString = i18n.get("table_pagination_label_one", endIdx, rows);
							}
							else {
								i18nString = i18n.get("table_pagination_label_many", startIdx, endIdx, rows);
							}
						}
						if (i18nString) {
							next.innerHTML = i18nString;
						}
					}
				});
			}

			/**
			 * Translate after Ajax.
			 * @param {Element} [element] not used.
			 * @param {Element|DocumentFragment} fragment The DocumentFragment about to be inserted by Ajax or a container element.
			 */
			function ajaxSubscriber(element, fragment) {
				Array.prototype.forEach.call(TABLE_WRAPPER.findDescendants(fragment), translate);
			}

			/**
			 * Update the options in the page selector after we change page (client mode only).
			 *
			 * @function
			 * @private
			 * @param {Element} element The page selector.
			 * @param {boolean} ignoreOther If true then do not reset the "other" page select (when the table has two).
			 */
			function updateSelectOptions(element, ignoreOther) {
				var currentPage,
					options,
					i,
					nextOption,
					otherSelect,
					startVal,
					totalPages = parseInt(element.getAttribute(PAGE_ATTRIB), 10) - 1; // the data-* attribute is one based.

				if (totalPages < NUM_PAGE_OPTIONS) {
					// If the total number of options is such that we never re-arrange them then there is nothing to do.
					return;
				}

				currentPage = parseInt(element.value, 10);
				options = element.options;

				element.setAttribute(BUSY, "true");
				startVal = getStartValue(currentPage, totalPages);

				for (i = 1; i < options.length - 1; ++i) {
					nextOption = options[i];
					if (startVal === currentPage) {
						shed.select(nextOption, true);
						// Safari bug
						element.selectedIndex = i;
					}
					nextOption.value = startVal++;
					nextOption.innerHTML = startVal; // already incremented.
				}
				element.removeAttribute(BUSY);
				if (!ignoreOther && (otherSelect = getOtherSelector(element))) {
					updateSelectOptions(otherSelect, true);
				}
			}

			/**
			 * Add the required options to the pagination control's page selector.
			 *
			 * @function
			 * @private
			 * @param {Element} [element] Any element defaults to document.body.
			 */
			function setUpPageSelectOptions(element) {
				var container = element || document.body,
					selectors = PAGINATION_SELECTOR.findDescendants(container);

				Array.prototype.forEach.call(selectors, function(next) {
					var totalPages,
						currentPage,
						startVal,
						i,
						isSelected,
						selectedIndex,
						option = "";

					if (next.options.length > 1 || ((totalPages = parseInt(next.getAttribute(PAGE_ATTRIB), 10)) === 1)) {
						return; // we have already processed this. Should never happen but hey!
					}

					totalPages--; // the data-* attribute is one based.

					currentPage = parseInt(next.value, 10);
					startVal = getStartValue(currentPage, totalPages);

					isSelected = currentPage === 0;
					option = "<option value='0'" + (isSelected ? " selected='selected'" : "") + ">1</option>";
					if (isSelected) {
						selectedIndex = 0;
					}

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
			 * Given one page selection dropdown find the other (if the table has two).
			 *
			 * @function
			 * @private
			 * @param {Element} selector a page selection dropdown.
			 * @returns {?Element} the other pagination dropdown.
			 */
			function getOtherSelector(selector) {
				var i,
					wrapper = getWrapper(selector),
					selectors = (PAGINATION_SELECTOR.isOneOfMe(selector) ? PAGINATION_SELECTOR.findDescendants(wrapper) : RPP_SELECTOR.findDescendants(wrapper)); // this could include selectors in nested tables
				if (selectors && selectors.length > 1) {
					for (i = 0; i < selectors.length; ++i) {
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
			 * Gets the TYPE of a given button.
			 *
			 * @function
			 * @private
			 * @param {Element} button A pagination button.
			 * @returns {int} One the constants defined in IDX_BUTTON
			 */
			function getButtonType(button) {
				var container = PAGINATION_CONTAINER.findAncestor(button),
					buttons = PAGINATION_BUTTON.findDescendants(container),
					result = Array.prototype.indexOf.call(buttons, button);
				return result;
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
				var len,
					buttonType,
					oldIndex,
					newIndex,
					selector = PAGINATION_SELECTOR.findDescendant(paginationContainer),
					otherSelector;

				if (selector && !shed.isDisabled(selector) && selector.getAttribute(BUSY) !== "true") {// don't do anything if selector disabled or busy
					len = selector.options.length;
					oldIndex = selector.selectedIndex;
					buttonType = getButtonType(button);
					if (buttonType === IDX_BUTTON.LAST) {
						newIndex = len - 1;  // select last option in list
					}
					else if (buttonType === IDX_BUTTON.PREV) {
						newIndex = oldIndex ? oldIndex - 1 : oldIndex;  // if oldIndex is zero don't decrement
					}
					else if (buttonType === IDX_BUTTON.NEXT) {
						newIndex = (oldIndex < (len - 1)) ? oldIndex + 1 : oldIndex;  // if we are at last page don't increment
					}
					else {  // FIRST
						newIndex = 0;
					}
					if (newIndex >= 0 && newIndex !== oldIndex) {
						selector.selectedIndex = newIndex;
						if ((otherSelector = getOtherSelector(selector))) {
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
			 * @param {Element} element The pagination select controller.
			 */
			function setPaginationButtonState(element) {
				var idx = element.selectedIndex,
					buttons,
					d = "disable",
					e = "enable",
					container;

				if ((container = PAGINATION_CONTAINER.findAncestor(element))) {
					buttons = PAGINATION_BUTTON.findDescendants(container);
				}

				if (buttons) {
					Array.prototype.forEach.call(buttons, function(button) {
						var type = getButtonType(button);
						if (idx === 0) {
							if (type === IDX_BUTTON.FIRST || type === IDX_BUTTON.PREV) {
								shed[d](button, true);
							}
							else {
								shed[e](button, true);
							}
						}
						else if (idx === element.options.length - 1) {
							if (type === IDX_BUTTON.FIRST || type === IDX_BUTTON.PREV) {
								shed[e](button, true);
							}
							else {
								shed[d](button, true);
							}
						}
						else {
							shed[e](button, true);
						}
					});
				}
			}

			/**
			 * Interleaves the showing and hiding of rows to prevent page jumping around in slow browsers (like IE8).
			 * This is not necessary in Chrome and FF but IE needs it.
			 *
			 * @function
			 * @private
			 * @param {NodeList} rows The collection of row elements
			 * @param {int} count The number of rows to show and hide (if they are different put in the bigger of the two)
			 * @param {int} showStart The starting index of the rows to show (inclusive, ie will be shown)
			 * @param {int} hideStart The starting index of the rows to hide (inclusive, ie will be hidden)
			 */
			function interleavedShowHide(rows, count, showStart, hideStart) {
				var nextHide, nextShow, i = 0;
				while (i < count) {
					nextHide = rows[hideStart + i];
					nextShow = rows[showStart + i];
					i++;
					if (nextHide || nextShow) {
						if (nextHide) {
							shed.hide(nextHide);
						}
						if (nextShow) {
							shed.show(nextShow);
						}
					}
					else {
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
				START_ELEMENT = START_ELEMENT || new Widget("span", "wc_table_pag_rowstart");
				END_ELEMENT = END_ELEMENT || new Widget("span", "wc_table_pag_rowend");
				Array.prototype.forEach.call(START_ELEMENT.findDescendants(wrapper), function(next) {
					if (getWrapper(next) === wrapper) {
						next.innerHTML = "";
						next.innerHTML = startHTML;
					}
				});
				Array.prototype.forEach.call(END_ELEMENT.findDescendants(wrapper), function(next) {
					if (getWrapper(next) === wrapper) {
						next.innerHTML = "";
						next.innerHTML = endHTML;
					}
				});
			}

			/**
			 * Change the visible page to reflect a change in the selector list. Assumes a change has actually been
			 * made, it's up to the caller to ensure that an update is actually necessary.
			 *
			 * @function
			 * @private
			 * @param {Element} element The page change selector (dropdown list)
			 * @param {Element} [button] Button which originated the change action.
			 */
			function changePage(element, button) {
				var page, rows, rowsPerPage, i, len, requestedPage,
					wrapper,
					paginatedTable,
					startIdx;

				if (isAjax(element)) {
					triggerButtonId = button.id;
					requestAjaxLoad(element);
				}
				else if ((wrapper = getWrapper(element)) && (paginatedTable = TABLE.findDescendant(wrapper, true)) && (page = PAGE.findDescendant(paginatedTable, true))) {
					rows = ROW.findDescendants(page, true);
					len = rows.length;
					requestedPage = element.value;
					rowsPerPage = paginatedTable.getAttribute("data-wc-rpp");

					for (i = 0; i < len; i++) {  // don't "let i" here
						if (!shed.isHidden(rows[i])) {
							break;
						}
					}

					startIdx = requestedPage * rowsPerPage;
					interleavedShowHide(rows, rowsPerPage, startIdx, i);
					updateSelectOptions(element);
					updateRecordDisplays(wrapper, (startIdx + 1), Math.min(startIdx + 1 * rowsPerPage, len));
					setPaginationButtonState(element);
				}
			}

			/**
			 * This function simply calls pageChange but queues requests and only actions the last request when there
			 * have been no further requests for a given number of milliseconds.
			 *
			 * @see {@link changePage}
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
			 * @param {Event} $event The change event.
			 */
			function changeEvent($event) {
				var element = $event.target,
					alternateSelector;

				if ($event.defaultPrevented || shed.isDisabled(element)) {
					return;
				}

				// if the table has two pagination/rows per page selectors they have to be kept in sync but do not fire
				// change events on the alternate.
				if (SELECTOR.isOneOfMe(element) && (alternateSelector = getOtherSelector(element))) {
					alternateSelector.selectedIndex = element.selectedIndex;
				}

				if (SELECTOR.isOneOfMe(element) && isAjax(element)) {
					// dynamic pagination and change rows per page (latter always ajax).
					requestAjaxLoad(element);
				}
				else if (PAGINATION_SELECTOR.isOneOfMe(element)) {
					requestPageChange(element);
				}
			}

			/**
			 * Focusin listener for browsers which do not capture (you know who you are). This is used to attach a
			 * change event to every pagination select element.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focusin event.
			 */
			function focusEvent($event) {
				var selector,
					footer = PAGINATION_CONTAINER.findAncestor($event.target);
				if (footer && !attribute.get(footer, BOOTSTRAP_PARAM)) {
					attribute.set(footer, BOOTSTRAP_PARAM, true);
					selector = SELECTOR.findDescendants(footer);
					if (selector && selector.length) {
						Array.prototype.forEach.call(selector, function(next) {
							event.add(next, event.TYPE.change, changeEvent, 1);
						});
					}
				}
			}

			/**
			 * Handles a click on any of the pagination buttons.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var paginationContainer, tree,
					element = $event.target;
				if (!$event.defaultPrevented) {
					tree = PAGINATION_BUTTON.findAncestor(element, "", true);
					if (tree && (element = tree[0])) {
						paginationContainer = tree[1];
						actionButton(element, paginationContainer);
					}
				}
			}

			/**
			 * Setup and initialise functionality.
			 *
			 * @function
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					// event.add(element, event.TYPE.focus, focusEvent, null, null, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
			};

			/**
			 * Subscriber to {@link module:wc/ui/ajax/processReponse}. If the ajax trigger is a dropdown select
			 * control and we have previously stored a button ID we have to refocus to the buttonId. The
			 * {@link module:wc/dom/onloadFocusControl} cannot do this for us.
			 *
			 * @function
			 * @private
			 * @param {Element} element The AJAX target element.
			 * @param {String} action Not required for this function.
			 * @param {String} triggerId The id of the ajax trigger element.
			 */
			function postAjaxSubscriber(element, action, triggerId) {
				var button, trigger;
				if (element) {
					if (TABLE_WRAPPER.isOneOfMe(element)) {
						setUpPageSelectOptions(element);
					}

					Array.prototype.forEach.call(TABLE_WRAPPER.findDescendants(element), setUpPageSelectOptions);
				}
				if (triggerId && triggerButtonId && (trigger = document.getElementById(triggerId)) && PAGINATION_SELECTOR.isOneOfMe(trigger)) {
					try {
						if ((button = document.getElementById(triggerButtonId))) {
							if (document.activeElement === trigger) {
								/* onLoadFocusControl has already set the focus to the ajax trigger
								 * so we cannot use it to refocus to the button but we can determine that
								 * we do not need to re-test for other focus since onloadFocusControl will
								 * have done that before focussing the select.*/
								focus.setFocusRequest(button);
							}
							else {
								/*
								 * There are two circumstances where we may not have focused the
								 * select: something else has focus OR onloadFocusControl's own
								 * AJAX subscriber has not yet fired. In both cases we can simply
								 * call the focus helper from onloadFocusControl which will take care
								 * of the alternate focus issue for us.
								 */
								onloadFocusControl.requestFocus(triggerButtonId);
							}
						}
					}
					finally {
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
				function _writeStatePaginableTable(element) {
					var container,
						id,
						selector;

					container = getWrapper(element);
					if (!container) {
						return;
					}

					id = container.id;

					selector = PAGINATION_SELECTOR.findDescendant(element);
					if (selector && !shed.isDisabled(selector)) {
						formUpdateManager.writeStateField(stateContainer, id + ".page", selector.value);
					}

					selector = RPP_SELECTOR.findDescendant(element);
					if (selector && !shed.isDisabled(selector)) {
						formUpdateManager.writeStateField(stateContainer, id + ".rows", selector.value);
					}
				}

				Array.prototype.forEach.call(PAGINATION_CONTAINER.findDescendants(form), _writeStatePaginableTable);
			}

			/**
			 * Late setup: the post-insertion ajax subscriber and formUpdateManager subscriber.
			 *
			 * @function module:wc/ui/table/pagination.postInit
			 * @public
			 */
			this.postInit = function() {
				setUpPageSelectOptions();
				processResponse.subscribe(ajaxSubscriber);
				processResponse.subscribe(postAjaxSubscriber, true);
				formUpdateManager.subscribe(writeState);
			};

			/**
			 * Early initialisation to do i18n of pagination.
			 * @function module:wc/ui/table/pagination.preInit
			 * @public
			 */
			this.preInit = function() {
				ajaxSubscriber(null, document.body);
			};
		}

		/**
		 * Provides table pagination functionality.
		 *
		 * NOTE: the pagination controls consist of a SELECT element and a set of four BUTTON elements. The actual pagination
		 * activation is *ALWAYS* done by the SELECT: the BUTTONs are used only to change the selectIndex of the SELECT. Trying
		 * to do anything else will lead to a sudden, and possibly permanent, loss of sanity.
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
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/formUpdateManager
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/ui/ajaxRegion
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/ui/onloadFocusControl
		 * @requires module:wc/timers
		 * @requires module:wc/ui/table/common
		 * @requires module:wc/i18n/i18n
		 */
		var instance = new Pagination();
		initialise.register(instance);
		return instance;
	});
