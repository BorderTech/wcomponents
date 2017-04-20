/**
 * Provides table sort controls. A column is sorted by an algorithm controlled by the server application. There is no
 * client side sorting.
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/event
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/group
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onloadFocusControl
 * @requires module:wc/dom/isEventInLabel
 * @requires module:wc/dom/isAcceptableTarget
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/table/common
 */
define(["wc/dom/initialise",
		"wc/dom/event",
		"wc/dom/formUpdateManager",
		"wc/dom/attribute",
		"wc/dom/group",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/onloadFocusControl",
		"wc/dom/isEventInLabel",
		"wc/dom/isAcceptableTarget",
		"wc/dom/shed",
		"wc/ui/table/common"],
	/** @param initialise @param event @param formUpdateManager @param attribute @param ajaxRegion @param processResponse @param onloadFocusControl @param isEventInLabel @param isAcceptableEventTarget @param shed @param common @ignore */
	function(initialise, event, formUpdateManager, attribute, group, ajaxRegion, processResponse, onloadFocusControl, isEventInLabel, isAcceptableEventTarget, shed, common) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/table/sort~Sort
		 * @private
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 */
		function Sort() {
			/**
			 * The {@link module:wc/dom/Widget} description of the container of WTable.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @private
			 */
			var TABLE_WRAPPER = common.WRAPPER,
				SORTABLE_TABLE = common.TABLE.extend("", {"sortable": null}),
				THEAD = common.THEAD.clone(),
				SORT_CONTROL = common.TH.extend("", {"aria-sort": null}),
				ID_EXTENDER = "_thh",
				BOOTSTRAPPED = "wc.ui.table.sort.BS",
				SORT_ATTRIB = "sorted",
				ARIA_SORT_ATTRIB = "aria-sort",
				SORTED_COL = SORT_CONTROL.extend("", {"sorted": null});

			THEAD.descendFrom(SORTABLE_TABLE, true);
			SORT_CONTROL.descendFrom(THEAD);

			function getWrapper(element) {
				return TABLE_WRAPPER.findAncestor(element);
			}

			/**
			 * Provides a post insertion subscriber to {@link module:wc/ui/ajax/processResponse} which will
			 * attempt to refocus the replacement sort control when one of the unsorted sort controls is the ajax
			 * trigger.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being replaced.
			 * @param {String} action The ajax action, not required for this function.
			 * @param {String} triggerId The id of the original AJAX trigger.
			 */
			function ajaxSubscriber(element, action, triggerId) {
				if (element && triggerId && triggerId.indexOf(ID_EXTENDER) > 0 && TABLE_WRAPPER.isOneOfMe(element)) {
					if (document.getElementById(triggerId)) {
						onloadFocusControl.requestFocus(triggerId);
					}
				}
			}


			/**
			 * Helper function for key and click initiated collapse toggling. Used to determine if the event is
			 * expected to change the state of the WCollapsible or some other nested interactive control. If we are able
			 * to act on element, or find a different focusable element before we get to element then return that
			 * element so we can use it to work out if we have to prevent default on SPACEBAR
			 * @function
			 * @private
			 * @param {Event} $event The event which initiated the toggle.
			 * @param {Element} element A sortable column header. Element must already have been determined to be a
			 *    SORT_CONTROL and since we have already extracted this from $event we may as well pass it in as
			 *    an arg rather than re-testing.
			 * @returns {?Element} The first interactive ancestor element of the event target if any. This may or may
			 *    not be the collapsible header.
			 */
			function toggleEventHelper($event, element) {
				var target = $event.target,
					sorted,
					controlGroup;
				if ((element === target || (!isEventInLabel(target) && isAcceptableEventTarget(element, target))) && !shed.isDisabled(element)) {
					sorted = element.getAttribute(SORT_ATTRIB);

					if (!sorted || sorted.indexOf("reversed") > -1) {
						if (!sorted) { // remove current sort col if any
							controlGroup = group.getGroup(element, SORTED_COL, THEAD); // there should be only one
							if (controlGroup && controlGroup.length) {
								controlGroup.forEach(function(next) {
									next.removeAttribute(SORT_ATTRIB);
									next.setAttribute(ARIA_SORT_ATTRIB, "none");
								});
							}
						}
						element.setAttribute(SORT_ATTRIB, "1");
						element.setAttribute(ARIA_SORT_ATTRIB, "ascending");
					}
					else {
						element.setAttribute(SORT_ATTRIB, "1 reversed");
						element.setAttribute(ARIA_SORT_ATTRIB, "descending");
					}

					ajaxRegion.requestLoad(element, common.getAjaxDTO(element, true));
				}
			}

			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = SORT_CONTROL.findAncestor($event.target))) {
					toggleEventHelper($event, element);
				}
			}

			function keydownEvent($event) {
				if ($event.defaultPrevented) {
					return;
				}
				if ($event.keyCode === KeyEvent["DOM_VK_RETURN"]) { // remember this event is only attached to an element which is a SORT_CONTROL.
					toggleEventHelper($event, $event.target);
				}
			}

			function focusEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && (SORT_CONTROL.isOneOfMe(element)) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.keydown, keydownEvent);
				}
			}

			function writeState(container, stateContainer) {
				Array.prototype.forEach.call(SORTABLE_TABLE.findDescendants(container), function(next) {
					var container = getWrapper(next),
						tableId = container.id,
						sortedColumn;

					// we need to do the reverse look-up to allow for the possibility of nested tables.
					if ((sortedColumn = SORTED_COL.findDescendant(next)) && next === SORTABLE_TABLE.findAncestor(sortedColumn)) {
						formUpdateManager.writeStateField(stateContainer, tableId + ".sort", sortedColumn.getAttribute("data-wc-columnidx"));
						if (sortedColumn.getAttribute("sorted").indexOf("reversed") > -1) {
							formUpdateManager.writeStateField(stateContainer, tableId + ".sortDesc", "true");
						}
					}
				});
			}

			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
			};

			this.postInit = function() {
				processResponse.subscribe(ajaxSubscriber, true);
				formUpdateManager.subscribe(writeState);
			};
		}

		var /** @alias module:wc/ui/table/sort */ instance = new Sort();
		initialise.register(instance);
		return instance;
	});
