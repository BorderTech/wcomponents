/**
 * Table actions are WButtons which are bound to a particular WTable and are usually constrained such that the
 * action can only be invoked if the table has row selection and the number of rows selected is within set bounds.
 * It is not invalid to have table actions without constraints or event without row selection, but it would be unusual.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/table/common
 */
define(["wc/dom/event",
		"wc/dom/getFilteredGroup",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/ui/table/common",
		"wc/ui/ajax/processResponse"],
	/** @param event @param getFilteredGroup @param initialise  @param shed @param common @ignore */
	function(event, getFilteredGroup, initialise, shed, common, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/action~Action
		 * @private
		 */
		function Action() {
			var ACTION_BUTTON = common.BUTTON.extend("wc_table_cond"),
				ACTION_TABLE = common.WRAPPER,
				ROW_CONTAINER = common.TBODY,
				ROW = common.TR.clone();

			ROW.descendFrom(ROW_CONTAINER, true);

			/**
			 * Determines if an action condition is met.
			 *
			 * @function
			 * @private
			 * @param {Element} button The table action invoking button.
			 * @param {Object} condition The action condition.
			 * @returns {Boolean} true if the condition is met, or the button is not a table action.
			 */
			function isConditionMet(button, condition) {
				var min,
					max,
					table,
					// todo: this filter can be deleted once we drop WDataTable as rows will no longer be able to be disabled
					filter = getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.selected,
					selected = 0;

				if ((table = ACTION_TABLE.findAncestor(button))) {
					min = condition.min;
					max = condition.max;

					if (!(min || max)) { // no condition worth testing.
						return true;
					}

					selected = (getFilteredGroup(ROW_CONTAINER.findDescendant(table), {filter: filter})).length;

					if ((min && selected < parseInt(min, 10)) || (max && selected > parseInt(max, 10))) {
						return false;
					}
				}
				return true;
			}

			/**
			 * Get any conditions from a table action button and parse them to a JSON object.
			 *
			 * @function
			 * @private
			 * @param {Element} button The table action invoking button.
			 * @returns {Object} The action conditions as a JSON object.
			 */
			function parseConditions(button) {
				var conditions = button.getAttribute("data-wc-condition");
				if (conditions) {
					return window.JSON.parse(conditions);
				}
				return null;
			}
			/**
			 * Test if an action button can be enabled.
			 * @function
			 * @private
			 * @param {Element} button
			 * @returns {Boolean} true if the conditions of the button are met.
			 */
			function canEnableButton(button) {
				var conditions;
				if ((conditions = parseConditions(button))) {
					return Array.prototype.every.call(conditions, function (next) {
						if (next.type === "error") {
							return isConditionMet(button, next);
						}
						return true;
					});
				}
				// if no conditions we can always be enabled.
				return true;
			}

			/**
			 * Helper to actually toggle the disabled state.
			 * @function
			 * @private
			 * @param {Element} button the button which we are disabling/enabling.
			 */
			function enableDisableButton(button) {
				var func = canEnableButton(button) ? "enable" : "disable";
				shed[func](button);
			}

			function canSubmit (button) {
				var conditions;

				if (!canEnableButton(button)) {
					return false;
				}

				if ((conditions = parseConditions(button))) {
					return Array.prototype.every.call(conditions, function(next) {
						if (next.type === "error") {
							return isConditionMet(button, next);
						}
						if (!isConditionMet(button, next)) {
							return window.confirm(next.message);
						}
						return true;
					});
				}
				// if no conditions we can always submit.
				return true;
			}

			/**
			 * Click listener for table actions. Invokes the test of conditions before allowing the submit button's
			 * normal action.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var target;
				if ($event.defaultPrevented) {
					return;
				}
				target = ACTION_BUTTON.findAncestor($event.target);
				if (target && !shed.isDisabled(target) && !canSubmit(target)) {
					$event.preventDefault();
				}
			}

			/**
			 * Set the initial state of action buttons when a page/ajax arrives.
			 * @function
			 * @private
			 * @param {Element} container the page or ajax response.
			 */
			function setUp(container) {
				var _container = container || document.body;
				Array.prototype.forEach.call(ACTION_BUTTON.findDescendants(_container), function(next) {
					enableDisableButton(next);
				});
			}

			/**
			 * Subscriber to row select/deselect which is the trigger for changing the button's disabled state.
			 * @function
			 * @private
			 * @param {Element} element the element being selected/deselected.
			 */
			function shedSubscriber(element) {
				var table;

				if (element && ROW.isOneOfMe(element) && (table = ACTION_TABLE.findAncestor(element))) {
					Array.prototype.forEach.call(ACTION_BUTTON.findDescendants(table), function(next) {
						if (ACTION_TABLE.findAncestor(next) !== table) {
							return; // not in the same table.
						}
						enableDisableButton(next);
					});
				}
			}

			/**
			 * Initial set up for table action.
			 *
			 * @function module:wc/ui/table/action.postInit
			 * @public
			 */
			this.postInit = function() {
				setUp();
				processResponse.subscribe(setUp, true);
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSubscriber);
			};

			/**
			 * Initial set up for table action.
			 *
			 * @function module:wc/action.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, -50);
			};
		}

		var /** @alias module:wc/ui/table/action */ instance = new Action();
		initialise.register(instance);
		return instance;
	});
