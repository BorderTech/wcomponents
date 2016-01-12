/**
 * Provides controller for expanding and collapsing table rows.
 * @module
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/timers
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onloadFocusControl
 * @requires module:wc/ui/rowAnalog
 */
define(["wc/array/toArray",
		"wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
 		"wc/ui/ajaxRegion",
		"wc/timers",
		"wc/ui/ajax/processResponse",
		"wc/ui/onloadFocusControl",
		"wc/ui/rowAnalog",
		"wc/ui/radioAnalog"],
	/** @param toArray @param attribute @param event @param focus @param formUpdateManager @param initialise @param shed @param tag @param Widget @param ajaxRegion @param timers @param processResponse @param onloadFocusControl @param rowAnalog @ignore */
	function(toArray, attribute, event, focus, formUpdateManager, initialise, shed, tag, Widget, ajaxRegion, timers, processResponse, onloadFocusControl, rowAnalog) {
		"use strict";
		/*
		 * IMPLICIT DEPENDENCIES:
		 * wc/ui/radioAnalog is used for the tables expand All/None controls if they are included in a table. Since they
		 * are almost always on when a table has expandable rows it is much cheaper to include it here than do a
		 * descendant search in XSLT and adds little extra weight.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/table/rowExpansion~RowExpansion
		 * @private
		 */
		function RowExpansion() {
			var TABLE_WRAPPER = new Widget("div", "table"),
				TBL_TRIGGER = new Widget("td", "wc_table_rowexp_container", {"role": "button"}),
				TBL_EXPANDABLE_ROW = new Widget("tr", "", {"role": "row", "aria-expanded": null}),
				TABLE = new Widget("TABLE", "", {"role": "treegrid"}),
				EXPAND_COLLAPSE_ALL = new Widget("button", "rowExpansion"),
				BOOTSTRAPPED = "wc.ui.table.rowExpansion.bootStrapped",
				TRUE = "true",
				FALSE = "false";


			/**
			 * Get the list of elements controlled by an expander.
			 *
			 * @param {Element} element A collapsible trigger.
			 * @returns {?String} The value of the aria-controls attribute, being a space separated list of elemet IDs
			 */
			function getContentList(element) {
				if (TBL_EXPANDABLE_ROW.isOneOfMe(element)) {
					return element.getAttribute("aria-controls");
				}
				return null;
			}

			/**
			 * Get the expandable rows which belong explicitly to a given table and not to any nested tables.
			 *
			 * @function
			 * @private
			 * @param {Element} table The table in which we are interested.
			 * @param {module:wc/dom/Widget} [widget] A widget which describes the rows we want. If not set the fuinction gets all expandable rows.
			 * @returns {Element[]} An array of rows.
			 */
			function getExpandableRows(table, widget) {
				var rowWidget = widget || TBL_EXPANDABLE_ROW;

				return toArray(rowWidget.findDescendants(table)).filter(function(next) {
					return TABLE.findAncestor(next) === table;
				});
			}

			/**
			 * Get a DTO suitable for registering an AJAX trigger for an expandable row or expand/collapse all control.
			 *
			 * @function
			 * @private
			 * @param {Element} element The triggering element.
			 * @param {String} alias The AJAX alias: this will be the ID of the table being targetted.
			 * @returns {Object} An object suitable to create a {@link module:wc/ajax/Trigger}.
			 */
			function getTriggerDTO(element, alias) {
				var id = element.id,
					oneShot = (element.getAttribute("data-wc-expmode") === "lazy");
				return {
					id: id,
					loads: [alias],
					alias: alias,
					formRegion: alias,
					oneShot: oneShot
				};
			}

			/**
			 * Write the state of collapsible rows. As usual this function expects state fields from previous calls to
			 * be cleaned up elsewhere (most commonly in {@link module:wc/dom/formUpdateManager}).
			 *
			 * @function
			 * @private
			 * @param {Element} form The form or form segment the state of which is being written.
			 * @param {Element} stateContainer The element into which the sate is written.
			 */
			function writeState(form, stateContainer) {
				Array.prototype.forEach.call(TBL_EXPANDABLE_ROW.findDescendants(form), function (element) {
					var collapsibleTable, rowIndex;
					if (shed.isExpanded(element) && !shed.isDisabled(element)) {
						collapsibleTable = TABLE.findAncestor(element).parentElement; // the table id is on the table container
						rowIndex = element.getAttribute("data-wc-rowindex");
						formUpdateManager.writeStateField(stateContainer, collapsibleTable.id + ".expanded", rowIndex);
					}
				});
			}

			/**
			 * Toggles the expanded/collapsed state of a single collapsible row.
			 *
			 * @function
			 * @private
			 * @param {Element} row A collapsible row.
			 * @param {String} [forceDirection] use a particular direction "true" or "false" rather than inferring from
			 *    the current button state. Required when recursively closing/hiding rows.
			 * @param {Boolean} [ignoreRecursion] true if we want to ignore the recursion of controllers to toggle sub
			 *    rows. This is used when invoked from collapsibleToggle.
			 * @param {Boolean| [ignoreAjax] used when recursing to prevent multiple ajax calls for the same table.
			 * @returns {Boolean} true if successfully expanded/collapsed.
			 */
			function toggleRow(row, ignoreAjax) {
				var show;

				if (row) {
					show = shed.isExpanded(row) ? FALSE : TRUE;
					if (show === TRUE && !shed.isDisabled(row)) {
						if (ignoreAjax && row.hasAttribute("data-wc-ajaxalias")) {
							row.setAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}", TRUE);
						}
						shed.expand(row);
					}
					else if (show === FALSE) { // We need to collapse disabled rows otherwise we have nesting vestige issues
						shed.collapse(row);
					}
				}
			}

			/**
			 * Expand/collapse all available row controllers in a table (but not in any further nested tables).
			 * @function
			 * @private
			 * @param {Element} element One of the expand all/collapse all buttons.
			 */
			function toggleAll(element) {
				var tableWrapper, table, alias, candidates, open, rowWidget;

				if (element && (tableWrapper = TABLE_WRAPPER.findAncestor(element)) && (table = TABLE.findDescendant(tableWrapper, true))) {
					open = (element.getAttribute("data-wc-value") === "expand") ? true : false;
					rowWidget = new Widget("tr", "", {"role": "row", "aria-expanded": (open ? FALSE : TRUE)});
					candidates = getExpandableRows(table, rowWidget);

					if (!candidates && candidates.length) {
						return;
					}

					if (open) {
						candidates = candidates.filter(function(next) {
							return !shed.isHidden(next);
						});
					}
					else {
						candidates.reverse();
					}

					candidates.forEach(function(next) {
						toggleRow(next, true);
					});

					if (open && (alias = element.getAttribute("data-wc-ajaxalias"))) {
						ajaxRegion.register(getTriggerDTO(element, alias));
					}
				}
			}

			/**
			 * Helper to show and hide rows controlled by an expandable row.
			 *
			 * @function
			 * @private
			 * @param {Element} triggerRow The expandable row.
			 * @param {String} action A {@link module:wc/dom/shed} action: one of shed.actions.EXPAND or shed.actions.COLLAPSE.
			 */
			function showHideContent(triggerRow, action) {
				var content = getContentList(triggerRow),
					shedFunc = action === shed.actions.EXPAND ? "show" : "hide";
				if (content) {
					content = content.split(" ");
					content.forEach(function(next) {
						var element = document.getElementById(next);
						if (element) {
							shed[shedFunc](element);
						}
					});
				}
			}

			/**
			 * Subscriber to {@link module:wc/dom/shed} to manage showing and hiding content when a row is expanded or
			 * collapsed.
			 *
			 * @function
			 * @private
			 * @param {Element} element The expandable row being expanded or collapsed.
			 * @param {String} action The shed action EXPAND or COLLAPSE.
			 */
			function shedObserver(element, action) {
				var alias;
				if (element && TBL_EXPANDABLE_ROW.isOneOfMe(element)) {
					if (action === shed.actions.EXPAND && (alias = element.getAttribute("data-wc-ajaxalias"))) {
						if (element.getAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}") === TRUE) {
							element.removeAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}");
						}
						else {
							ajaxRegion.requestLoad(element, getTriggerDTO(element, alias));
						}
					}
					showHideContent(element, action);
				}
			}

			/**
			 * Subscriber to {@link module:wc/dom/shed} to manage collapsing an expandable row if it is hidden. This
			 * allows us to manage multiply nested ex[andables and hiding rows using (for example) client pagination.
			 *
			 * @function
			 * @private
			 * @param {Element} element The expandable row being hidden.
			 */
			function closeOnHide(element) {
				if (element && TBL_EXPANDABLE_ROW.isOneOfMe(element) && shed.isExpanded(element) && shed.isHidden(element)) {
					toggleRow(element, true);
				}
			}

			/**
			 * Reset focus to a row expander after dynamic/lazy expansion.
			 *
			 * @function
			 * @private
			 * @param {Element} element The target element.
			 * @param {String} action Not used.
			 * @param {String} triggerId The id of the original ajax trigger element. We are only interested if this is
			 *    the id of a table with row expansion.
			 */
			function ajaxSubscriber(element, action, triggerId) {
				var button;
				if (element && triggerId && (TABLE_WRAPPER.isOneOfMe(element))) {
					if ((button = document.getElementById(triggerId))) {
						if (Widget.isOneOfMe(button, [TBL_TRIGGER, EXPAND_COLLAPSE_ALL])) {
							onloadFocusControl.requestFocus(triggerId);
						}
					}
				}
			}


			/**
			 * Keydown event listener to operate collapsibles via the keyboard.
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var element, row;
				if (event.defaultPrevented || $event.altKey || $event.ctrlKey || $event.metaKey) {
					return;
				}
				// NOTE to self: I split these if tests for ease of reading.
				if ((element = TBL_TRIGGER.findAncestor($event.target, tag.TD))) {
					switch ($event.keyCode) {
						case KeyEvent["DOM_VK_SPACE"]:
						case KeyEvent["DOM_VK_RETURN"]:
							timers.setTimeout(event.fire, 0, element, event.TYPE.click);
							$event.preventDefault();
							break;
						case KeyEvent["DOM_VK_LEFT"] :
							if ((row = rowAnalog.ITEM.findAncestor(element, tag.TR)) && !shed.isDisabled(row)) {
								rowAnalog.setFocusIndex(row);
								focus.setFocusRequest(row);
								$event.preventDefault();
							}
							break;
					}
				}
			}

			/**
			 * Focus bootstrapper to wire up jkeydown event listener.
			 *
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event The wrapped focus event.
			 */
			function focusEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = TBL_TRIGGER.findAncestor($event.target, tag.TD)) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.keydown, keydownEvent);
				}
			}

			/**
			 * Click on table row expander control or expand/collapse all control.
			 *
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event The wrapped click event.
			 */
			function clickEvent($event) {
				var element, row;
				if ($event.defaultPrevented) {
					return;
				}

				if ((element = TBL_TRIGGER.findAncestor($event.target, tag.TD))) {
					if (shed.isDisabled(element)) {
						return;
					}

					row = TBL_EXPANDABLE_ROW.findAncestor(element, tag.TR);
					if (row) {
						if (shed.isDisabled(row)) {
							return;
						}
						if (toggleRow(row) && !shed.isExpanded(row)) {// if we have collapsed the row do nothing else. This stops dynamic ajax on collapse
							$event.preventDefault();
						}
					}
				}
				else if ((element = EXPAND_COLLAPSE_ALL.findAncestor($event.target)) && !shed.isDisabled(element)) {
					toggleAll(element);
				}
			}

			/**
			 * Set up the collapsible row controllers.
			 * @function module:wc/ui/table/rowExpansion.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
			};

			/**
			 * Late setup to wire up the shed observer.
			 * @function module:wc/ui/table/rowExpansion.postInit
			 * @public
			 */
			this.postInit = function() {
				processResponse.subscribe(ajaxSubscriber, true);
				shed.subscribe(shed.actions.EXPAND, shedObserver);
				shed.subscribe(shed.actions.COLLAPSE, shedObserver);
				shed.subscribe(shed.actions.HIDE, closeOnHide);
				formUpdateManager.subscribe(writeState);
			};
		}

		var /**  @alias module:wc/ui/table/rowExpansion */ instance = new RowExpansion();
		initialise.register(instance);
		return instance;
	});
