/**
 * Table actions are WButtons which are bound to a particular WDataTable and are usually constrained such that the
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
		"wc/ui/table/common"],
	/** @param event wc/dom/event @param getFilteredGroup wc/dom/getFilteredGroup @param initialise wc/dom/initialise @param shed wc/dom/shed @param common @ignore */
	function(event, getFilteredGroup, initialise, shed, common) {
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
				ACTION = { WARN: "warning",
					ERR: "error" };

			/**
			 * Determines if an action condition is met.
			 *
			 * @function
			 * @private
			 * @param {Element} button The table action invoking button.
			 * @param {Object} condition The action condition.
			 * @returns {Boolean} true if the condition is met, or the button is not a table action.
			 */
			function isButtonConditionMet(button, condition) {
				var min,
					max,
					table,
					message,
					actionType,
					selected = 0,
					result = true;

				table = ACTION_TABLE.findAncestor(button);

				if (table) {
					min = condition.min;
					max = condition.max;
					message = condition.message;
					actionType = condition.type;

					selected = (getFilteredGroup(ROW_CONTAINER.findDescendant(table))).length;

					if ((min === "" && max === "") || (min !== "" && selected < parseInt(min, 10)) || (max !== "" && selected > parseInt(max, 10))) {
						result = false;
					}
					if (!result && message) {
						if (actionType === ACTION.WARN) {
							result = window.confirm(message);
						}
						else if (actionType === ACTION.ERR) {
							window.alert(message);
							result = false;
						}
					}
				}
				return result;
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
				var result, conditions = button.getAttribute("${wc.ui.table.actions.attribute.conditions}");
				if (conditions) {
					result = window.JSON.parse(conditions);
				}
				return result;
			}

			/**
			 * Test whether a table action can proceed/
			 *
			 * @function
			 * @private
			 * @param {Element} button The table action invoking button.
			 * @returns {Boolean} true if the button has no conditions or if all conditions are met.
			 */
			function checkSubmit(button) {
				var result = true, conditions, i, len, next;
				if ((conditions = parseConditions(button))) {
					for (i = 0, len = conditions.length; i < len; ++i) {
						next = conditions[i];
						if (!(result = isButtonConditionMet(button, next))) {
							break;
						}
					}
				}
				return result;
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
				if (target && !shed.isDisabled(target) && !checkSubmit(target)) {
					$event.preventDefault();
				}
			}

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

		var /** @alias module:wc/action */ instance = new Action();
		initialise.register(instance);
		return instance;
	});
