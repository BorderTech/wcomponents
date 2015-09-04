/**
 * Provides controller for expanding and collapsing table rows.
 * @module
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/timers
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onloadFocusControl
 * @requires module:wc/ui/radioAnalog
 *
 * @todo Document private members.
 */
define(["wc/array/toArray",
		"wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/formUpdateManager",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
 		"wc/ui/ajaxRegion",
		"wc/timers",
		"wc/ui/ajax/processResponse",
		"wc/ui/onloadFocusControl",
		"wc/ui/radioAnalog"],
	/** @param toArray wc/array/toArray @param attribute wc/dom/attribute @param event wc/dom/event @param formUpdateManager wc/dom/formUpdateManager @param initialise wc/dom/initialise @param shed wc/dom/shed @param tag wc/dom/tag @param Widget wc/dom/Widget @param ajaxRegion wc/ui/ajaxRegion @param timers wc/timers @param processResponse wc/ui/ajax/processResponse @param onloadFocusControl wc/ui/onloadFocusControl @ignore */
	function(toArray, attribute, event, formUpdateManager, initialise, shed, tag, Widget, ajaxRegion, timers, processResponse, onloadFocusControl) {
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
				TBL_TRIGGER = new Widget("button", "wc_table_rowexp_ctrl"),
				TBL_EXPANDABLE_ROW = new Widget("tr", "", {"data-wc-expanded": null}),
				TABLE = new Widget("TABLE"),
				EXPAND_COLLAPSE_ALL = new Widget("button", "rowExpansion"),
				BOOTSTRAPPED = "wc.ui.table.rowExpansion.bootStrapped",
				TRUE = "true",
				FALSE = "false";

			// these next is not strictly needed but makes code very robust
			TBL_TRIGGER.descendFrom(TBL_EXPANDABLE_ROW);

			/*
			 * Listens for tables being dis/en-abled and dis/en-ables the row expansion controls in that table, including
			 * the expand/collapse all controls if present.
			 * @function
			 * @private
			 * @param {Element} element The table being enabled/disabled.
			 * @param {String} action shed action:  interested in shed.actions.DISABLE and shed.actions.ENABLE.

			function shedDisableObserver(element, action) {
				if (element && TABLE_WRAPPER.isOneOfMe(element)) {
					Array.prototype.forEach.call(Widget.findDescendants(element, [TBL_TRIGGER, EXPAND_COLLAPSE_ALL]), function(next){
						if (TABLE_WRAPPER.findAncestor(next) === element){  // only interested in the controls for this table, not for any nested table
							if (action === shed.actions.DISABLE) {
								shed.disable(next, true);  // nothing needs to know about this as nothing but the table is a WC level state or event target
							}
							else {
								shed.enable(next, true);
							}
						}
					});
				}
			}*/

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
				/*
				 * Write the state of each individual collapsible row
				 */
				function _writeStateCollapsibleTable(element) {
					var collapsibleRow, collapsibleTable, rowIndex;
					if (shed.isExpanded(element) && !shed.isDisabled(element)) {
						collapsibleRow = TBL_EXPANDABLE_ROW.findAncestor(element);
						collapsibleTable = TABLE.findAncestor(collapsibleRow).parentElement;  // the table id is on the table container
						rowIndex = collapsibleRow.getAttribute("data-wc-rowindex");
						formUpdateManager.writeStateField(stateContainer, collapsibleTable.id + ".expanded", rowIndex);
					}
				}

				Array.prototype.forEach.call(TBL_TRIGGER.findDescendants(form), _writeStateCollapsibleTable);
			}

			/*
			 * Bootstrapping listener: if "one of me" is focused then wire it up.
			 */
			function focusEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = TBL_TRIGGER.findAncestor($event.target, tag.TD)) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.keydown, keydownEvent);
				}
			}

			/*
			 * click on table row expander control
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented) {
					if ((element = TBL_TRIGGER.findAncestor($event.target, tag.TD)) && !shed.isDisabled(element) && toggleRow(element)) {
						if (!shed.isExpanded(element)) {// if we have collapsed the row do nothing else. This stops dynamic ajax & server mode submits on collapse
							$event.preventDefault();
						}
					}
					else if ((element = EXPAND_COLLAPSE_ALL.findAncestor($event.target, tag.TD)) && !shed.isDisabled(element)) {
						toggleAll(element);
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
				var element;
				if (!$event.defaultPrevented && !$event.altKey && !($event.ctrlKey || $event.metaKey) && ($event.keyCode !== KeyEvent["DOM_VK_TAB"]) && (element = TBL_TRIGGER.findAncestor($event.target, tag.TD))) {
					switch ($event.keyCode) {
						case KeyEvent["DOM_VK_SUBTRACT"]:
						case 189:
						case KeyEvent["DOM_VK_LEFT"]:
						case KeyEvent["DOM_VK_UP"]:
						case KeyEvent["DOM_VK_ADD"]:
						case 187:
						case KeyEvent["DOM_VK_RIGHT"]:
						case KeyEvent["DOM_VK_DOWN"]:
						/* case KeyEvent["DOM_VK_SPACE"]: */
							timers.setTimeout(event.fire, 0, element, event.TYPE.click);
							$event.preventDefault();
							break;
					}
				}
			}

			/*
			 * Note that server side collapsibles may or may not have content (an expanded one
			 * will have content, a collapsed one has no need for content)
			 * @param element A collapsible trigger
			 * @returns the content container if found, otherwise null
			 */
			function getContentList(element) {
				var result;
				if (TBL_TRIGGER.isOneOfMe(element)) {
					result = element.getAttribute("aria-controls");
				}
				return result;
			}

			/**
			 * Register an expand[All] control for ajax.
			 * @function
			 * @private
			 * @param {Element} element The ajax trigger element.
			 * @param {String} alias The table id.
			 */
			function registerTrigger(element, alias) {
				var id = element.id,
					oneShot = (element.getAttribute("data-wc-expmode") === "lazy");
				ajaxRegion.register({
					id: id,
					loads: [alias],
					alias: alias,
					formRegion: alias,
					oneShot: oneShot
				});
			}

			/**
			 * Expand/collapse all available row controllers in a table (but not in any further nested tables).
			 * @function
			 * @private
			 * @param {Element} element One of the expand all/collapse all buttons.
			 */
			function toggleAll(element) {
				var tableWrapper, triggerWidget, triggers, alias,
					open = (element.getAttribute("data-wc-value") === "expand") ? true : false;
				if (element && (tableWrapper = TABLE_WRAPPER.findAncestor(element))) {
					triggerWidget = TBL_TRIGGER.extend("", {"aria-expanded": (open ? FALSE : TRUE)});

					triggers = toArray(triggerWidget.findDescendants(tableWrapper));
					if (!open) {
						triggers.reverse();  // this just ensures nested rows are closed before their ancestor
					}

					triggers.forEach(function(next) {
						toggleRow(next, null, true, true);
					});

					if (open && (alias = element.getAttribute("data-wc-ajaxalias"))) {
						registerTrigger(element, alias);
					}
				}
			}

			/**
			 * Toggles the expanded/collapsed state of a single collapsible row.
			 * @function
			 * @private
			 * @param {Element} element A collapsible trigger.
			 * @param {String} [forceDirection] use a particular direction "true" or "false" rather than inferring from
			 *    the current button state. Required when recursively closing/hiding rows.
			 * @param {Boolean} [ignoreRecursion] true if we want to ignore the recursion of controllers to toggle sub
			 *    rows. This is used when invoked from collapsibleToggle.
			 * @param {Boolean| [ignoreAjax] used when recursing to prevent multiple ajax calls for the same table.
			 * @returns {Boolean} true if successfully expanded/collapsed.
			 */
			function toggleRow(element, forceDirection, ignoreRecursion, ignoreAjax) {
				var content, component, show, result = false;

				function _showContent(next) {
					if ((component = document.getElementById(next)) && !TABLE.isOneOfMe(component)) {// do not try to toggle the table wrapper, this is an AJAX mode expander at work!
						if (TBL_TRIGGER.isOneOfMe(component)) {
							if (!ignoreRecursion && show !== TRUE && shed.isExpanded(component)) {
								toggleRow(component, FALSE);
							}
						}
						else if (show === TRUE) {
							shed.show(component, true);
						}
						else {
							shed.hide(component, true);
						}
					}
				}

				if (element) {
					show = forceDirection || (shed.isExpanded(element) ? FALSE : TRUE);

					if (show === TRUE && !shed.isDisabled(element)) {
						if (ignoreAjax && element.hasAttribute("data-wc-ajaxalias")) {
							element.setAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}", TRUE);
						}
						shed.expand(element);
						result = true;
					}
					else if (show === FALSE) {// We need to collapse disabled rows otherwise we have nesting vestige issues
						shed.collapse(element);
						result = true;
					}
					if (result) {
						if ((content = getContentList(element))) {
							content = content.split(" ");
							content.forEach(_showContent);
						}
					}
				}
				return result;
			}

			function shedObserver(element, action) {
				var row, trigger, alias;
				if (element) {
					if (action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) {
						if (TBL_TRIGGER.isOneOfMe(element) && (row = TBL_EXPANDABLE_ROW.findAncestor(element))) {
							row.setAttribute("data-wc-expanded", (action === shed.actions.EXPAND ? TRUE : FALSE));
							if (action === shed.actions.EXPAND && (alias = element.getAttribute("data-wc-ajaxalias"))) {
								if (element.getAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}") === TRUE) {
									element.removeAttribute("${wc.ui.table.rowExpansion.attribute.ignoreAjax}");
								}
								else {
									registerTrigger(element, alias);
								}
							}
							// set state of expand/collapse all
							setExpandCollapseAllState(element, action);
						}
					}
					else if (action === shed.actions.HIDE && TBL_EXPANDABLE_ROW.isOneOfMe(element) && (trigger = document.getElementById(element.id + "${wc.ui.table.rowExpansion.id.suffix}")) && shed.isExpanded(trigger)) {
						// collapse expanded rows on hide
						toggleRow(trigger, FALSE);
					}
				}
			}

			function setExpandCollapseAllState(trigger, action) {
				var table, expandCollapseAlls, testVal = action === shed.actions.EXPAND ? "collapse" : "expand";
				if ((table = TABLE.findAncestor(trigger)) && (expandCollapseAlls = toArray(EXPAND_COLLAPSE_ALL.findDescendants(table)))) {
					expandCollapseAlls = expandCollapseAlls.filter(function(next) {
						return TABLE.findAncestor(next) === table;
					});
					expandCollapseAlls.forEach(function(next) {
						if (next.getAttribute("data-wc-value") === testVal) {
							shed.deselect(next, true);  // no need to publish
						}
						else if (areAllInState(table, action)) {
							shed.select(next, true);  // no need to publish
						}
					});
				}
			}

			function areAllInState(table, action) {
				var candidates = toArray(TBL_EXPANDABLE_ROW.findDescendants(table)).filter(function(next) {
						return TABLE.findAncestor(next) === table;
					}),
					result = (candidates && candidates.length), i, row, trigger,
					test = (action === shed.actions.EXPAND);

				for (i = 0; i < candidates.length; ++i) {
					row = candidates[i];
					if ((trigger = TBL_TRIGGER.findDescendant(row))) {
						if (shed.isExpanded(trigger) !== test) {
							result = false;
							break;
						}
					}
				}
				return result;
			}

			/**
			 * reset focus to a row expander after dynamic/lazy expansion.
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
				shed.subscribe(shed.actions.HIDE, shedObserver);
				/* shed.subscribe(shed.actions.DISABLE, shedDisableObserver);
				shed.subscribe(shed.actions.ENABLE, shedDisableObserver); */
				formUpdateManager.subscribe(writeState);
			};
		}

		var /**  @alias module:wc/ui/table/rowExpansion */ instance = new RowExpansion();
		initialise.register(instance);
		return instance;
	});
