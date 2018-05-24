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
	"wc/ui/rowAnalog",
	"wc/ui/table/common",
	"wc/ajax/triggerManager",
	"wc/ui/icon",
	"wc/ui/radioAnalog"],
	function(toArray, attribute, event, focus, formUpdateManager, initialise, shed, tag, Widget, ajaxRegion, timers, processResponse, rowAnalog, common, triggerManager, icon) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/table/rowExpansion~RowExpansion
		 * @private
		 */
		function RowExpansion() {
			var EXP_COLL_ALL_CONTAINER = new Widget("", "wc-rowexpansion"),
				TABLE_WRAPPER = common.WRAPPER,
				ROW_TRIGGER = common.TD.extend("", {"role": "button"}),
				TBL_EXPANDABLE_ROW = common.TR.extend("", {"aria-expanded": null}),
				TABLE = common.TABLE.extend("wc_tbl_expansion"),
				TBODY = common.TBODY.clone(),
				EXPAND_COLLAPSE_ALL = new Widget("button", "wc_rowexpansion"),
				CONTROLS = "aria-controls",
				BOOTSTRAPPED = "wc.ui.table.rowExpansion.bootStrapped",
				NO_AJAX = "data-wc-tablenoajax",
				MODE = "data-wc-expmode",
				VALUE = "data-wc-value",
				EXPAND = "expand",
				TRUE = "true",
				FALSE = "false",
				LAZY = "lazy",
				CLIENT = "client";

			TABLE.descendFrom(TABLE_WRAPPER, true);
			TBODY.descendFrom(TABLE, true);
			TBL_EXPANDABLE_ROW.descendFrom(TBODY, true);

			function getControlled(trigger) {
				var actualTrigger = EXP_COLL_ALL_CONTAINER.isOneOfMe(trigger) ? EXPAND_COLLAPSE_ALL.findDescendant(trigger) : trigger,
					idList = actualTrigger.getAttribute(CONTROLS);
				if (!idList) {
					return null;
				}
				return idList.split(/\s+/).map(function (next) {
					return document.getElementById(next);
				});
			}

			/**
			 * Get all controllers for a given row.
			 *
			 * @function
			 * @private
			 * @param {Element} element the element being controlled
			 * @returns { Element[]} An array containing all of the controllers for the row
			 */
			function getControllers(element) {
				var controllerWidget,
					candidates;

				if (!(element && element.id)) {
					return null;
				}

				controllerWidget = EXPAND_COLLAPSE_ALL.extend("", {"aria-controls": element.id});
				candidates = controllerWidget.findDescendants(document.body);

				if (!(candidates && candidates.length)) {
					return null;
				}

				return (toArray(candidates));
			}

			/**
			 * Are all rows in a particular state?
			 *
			 * @function
			 * @private
			 * @param {Element} controller The WCollapsibleToggle control.
			 * @param {Boolean} expanded true if we are checking if all expanded, otherwise false
			 */
			function areAllInExpandedState(controller, expanded) {
				var candidates = getControlled(controller);

				if (candidates && candidates.length) {
					return candidates.every(function (next) {
						return next && shed.isExpanded(next) === expanded;
					});
				}
				return false;
			}

			function setControllerState(controller) {
				var testVal = controller.getAttribute(VALUE);

				if (areAllInExpandedState(controller, testVal === "expand")) {
					shed.select(controller, true); // no need to publish
				} else {
					shed.deselect(controller, true); // no need to publish
				}
			}

			function getWrapper(element) {
				return TABLE_WRAPPER.findAncestor(element);
			}

			function getMode(row) {
				var wrapper = getWrapper(row);
				if (wrapper) {
					return wrapper.getAttribute(MODE);
				}
				return null;
			}

			function isAjaxExpansion(row) {
				var mode = getMode(row);
				return mode === LAZY || mode === "dynamic";
			}

			/**
			 * Get a DTO suitable for registering an AJAX trigger for an expandable row or expand/collapse all control.
			 *
			 * @function
			 * @private
			 * @param {Element} element The triggering element.
			 * @returns {Object} An object suitable to create a {@link module:wc/ajax/Trigger}.
			 */
			function getTriggerDTO(element) {
				return common.getAjaxDTO(element, element.getAttribute(MODE) === LAZY);
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
				Array.prototype.forEach.call(TABLE.findDescendants(form), function(next) {
					var id = next.parentElement.id,
						rows = toArray(TBL_EXPANDABLE_ROW.findDescendants(next)).filter(function(row) {
							return shed.isExpanded(row);
						});
					rows.forEach(function(row) {
						var rowIndex = row.getAttribute("data-wc-rowindex");
						formUpdateManager.writeStateField(stateContainer, id + ".expanded", rowIndex, false, true);
					});
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
						if (ignoreAjax && isAjaxExpansion(row)) {
							row.setAttribute(NO_AJAX, TRUE);
						}
						shed.expand(row);
					} else if (show === FALSE) { // We need to collapse disabled rows otherwise we have nesting vestige issues
						shed.collapse(row);
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
				var shedFunc = action === shed.actions.EXPAND ? "show" : "hide",
					controllers,
					controlled = getControlled(triggerRow);

				if (controlled) {
					controlled.forEach(function(row) {
						if (row) {
							shed[shedFunc](row);
						}
					});
					if ((controllers = getControllers(triggerRow))) {
						controllers.forEach(setControllerState);
					}
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
			function expCollapseObserver(element, action) {
				var control, add, remove;
				if (element && TBL_EXPANDABLE_ROW.isOneOfMe(element)) {
					if ((control = ROW_TRIGGER.findDescendant(element, true))) {
						add = action === shed.actions.EXPAND ? "fa-caret-down" : "fa-caret-right";
						remove = action === shed.actions.EXPAND ? "fa-caret-right" : "fa-caret-down";
						icon.change(control, add, remove);
					}
					if (action === shed.actions.EXPAND && isAjaxExpansion(element)) {
						if (element.getAttribute(NO_AJAX) === TRUE) {
							element.removeAttribute(NO_AJAX);
						} else if (element.getAttribute(MODE) !== CLIENT) {
							ajaxRegion.requestLoad(element, getTriggerDTO(element));
						}
						if (getMode(element) === LAZY) {
							element.setAttribute(MODE, CLIENT);
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
				if (element && TBL_EXPANDABLE_ROW.isOneOfMe(element) && shed.isExpanded(element)) {
					toggleRow(element, true);
				}
			}

			/**
			 * Reset focus to a row expander after dynamic/lazy expansion.
			 *
			 * @function
			 * @private
			 */
			function ajaxSubscriber(/* element, action, triggerId */) {
				setControls();
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
				if ((element = ROW_TRIGGER.findAncestor($event.target, tag.TD))) {
					switch ($event.keyCode) {
						case KeyEvent["DOM_VK_SPACE"]: // The control is a td with a role - some browsers do not have a default click from SPACE.
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
				if (!$event.defaultPrevented && (element = ROW_TRIGGER.findAncestor($event.target, tag.TD)) && !attribute.get(element, BOOTSTRAPPED)) {
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

				if ((element = ROW_TRIGGER.findAncestor($event.target, tag.TD))) {
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
				} else if ((element = EXPAND_COLLAPSE_ALL.findAncestor($event.target)) && !shed.isDisabled(element)) {
					triggerManager.removeTrigger(element.id);
				}
			}

			/**
			 * Expand/collapse all available row controllers in a table (but not in any further nested tables).
			 * @function
			 * @private
			 * @param {Element} element One of the expand all/collapse all buttons.
			 * @returns {Boolean} {@code true} if there are any rows to toggle.
			 */
			function toggleAll(element) {
				var candidates, open;

				if (element && (candidates = getControlled(element))) {
					if (!candidates.length) {
						return false;
					}
					open = element.getAttribute(VALUE) === EXPAND;

					candidates = candidates.filter(function(next) {
						if (!next) {
							return false;
						}
						if (open) {
							return !(shed.isExpanded(next)|| shed.isHidden(next));
						}
						return shed.isExpanded(next);
					});

					if (!open) {
						candidates.reverse();
					}

					candidates.forEach(function(next) {
						toggleRow(next, true);
					});

					return true;
				}

				return false;
			}

			/**
			 * Toggle rows whan the select/deselect all options are triggered.
			 * @param {Element} element The element being selected.
			 */
			function activateOnSelect(element) {
				var toggled, wrapper;

				if (element && EXPAND_COLLAPSE_ALL.isOneOfMe(element)) {
					toggled = toggleAll(element);
					wrapper = EXP_COLL_ALL_CONTAINER.findAncestor(element);
					Array.prototype.forEach.call(EXPAND_COLLAPSE_ALL.findDescendants(wrapper), setControllerState);
					if (toggled && element.getAttribute(VALUE) === EXPAND && isAjaxExpansion(element)) {
						ajaxRegion.requestLoad(element, getTriggerDTO(element));
					}
				}
			}

			/**
			 * Set the aria-controls attribute on the buttons of a collapsibleToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} element a collapsible toggle wrapper
			 * @returns {undefined}
			 */
			function setControlList(element) {
				var wrapper,
					idArray = [],
					ids;

				wrapper = TABLE_WRAPPER.findAncestor(element);
				if (!wrapper) {
					return;
				}

				Array.prototype.forEach.call(TBL_EXPANDABLE_ROW.findDescendants(wrapper), function (next) {
					idArray.push(next.id);
				});

				if (idArray.length) {
					ids = idArray.join(" ");
					Array.prototype.forEach.call(EXPAND_COLLAPSE_ALL.findDescendants(element), function (next) {
						next.setAttribute(CONTROLS, ids);
					});
				}
			}

			/**
			 * Set aria-controls for each collapsible toggle.
			 *
			 * @function
			 * @private
			 */
			function setControls() {
				Array.prototype.forEach.call(EXP_COLL_ALL_CONTAINER.findDescendants(document.body), setControlList);
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
				} else {
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
				setControls();
				processResponse.subscribe(ajaxSubscriber, true);
				shed.subscribe(shed.actions.EXPAND, expCollapseObserver);
				shed.subscribe(shed.actions.COLLAPSE, expCollapseObserver);
				shed.subscribe(shed.actions.HIDE, closeOnHide);
				shed.subscribe(shed.actions.SELECT, activateOnSelect);
				formUpdateManager.subscribe(writeState);
			};

			/**
			 * Is a given table a treegrid? We cannot currently use the treegrid role because it causes a11y failure in common screenreader/browser
			 * combos.
			 * @param {Element} element the element to test
			 * @returns {Boolean} {@code true} if the element is a table with row expansion.
			 */
			this.isTreeGrid = function(element) {
				return TABLE.isOneOfMe(element);
			};
		}

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
		 * @requires module:wc/ui/rowAnalog
		 * @requires module:wc/ui/table/common
		 * @requires module:wc/ajax/triggerManager
		 * @requires module:wc/ui/icon
		 */
		var instance = new RowExpansion();
		initialise.register(instance);
		return instance;
	});
