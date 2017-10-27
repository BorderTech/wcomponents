define(["wc/dom/event",
	"wc/dom/getFilteredGroup",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/table/common",
	"wc/ui/ajax/processResponse",
	"wc/dom/Widget"],
	function(event, getFilteredGroup, initialise, shed, common, processResponse, Widget) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/action~Action
		 * @private
		 */
		function Action() {
			var ACTION_CONTAINER = new Widget("", "wc-actions"),
				ACTION_BUTTON = common.BUTTON.clone(),
				ACTION_TABLE = common.WRAPPER,
				ROW_CONTAINER = common.TBODY,
				ROW = common.TR.clone(),
				registry = {};

			ROW.descendFrom(ROW_CONTAINER, true);
			ACTION_BUTTON.descendFrom(ACTION_CONTAINER);

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
					otherSelected = 0,
					selected = 0;

				if ((table = ACTION_TABLE.findAncestor(button))) {
					min = condition.min;
					max = condition.max;

					otherSelected = condition.otherSelected;

					if (!(min || max)) { // no condition worth testing.
						return true;
					}

					selected = parseInt((getFilteredGroup(ROW_CONTAINER.findDescendant(table), {filter: filter})).length)+
						parseInt(otherSelected);

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
			function getConditions(button) {
				return registry[button.id];
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
				if ((conditions = getConditions(button))) {
					return Array.prototype.every.call(conditions, function (condition) {
						if (condition.type === "error") {
							return isConditionMet(button, condition);
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

				if ((conditions = getConditions(button))) {
					return Array.prototype.every.call(conditions, function(condition) {
						if (condition.type === "error") {
							return isConditionMet(button, condition);
						}
						if (!isConditionMet(button, condition)) {
							return window.confirm(condition.message);
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
					next.setAttribute("formnovalidate", "formnovalidate");
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

			function _register(action) {
				if (action && action.trigger) {
					registry[action.trigger] = action.conditions;
				}
			}

			this.register = function(actionArray) {
				actionArray.forEach(_register);
			};
		}

		var instance = new Action();
		initialise.register(instance);
		return instance;
	});
