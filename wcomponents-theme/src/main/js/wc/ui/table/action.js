define(["wc/dom/event",
	"wc/dom/getFilteredGroup",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/table/common",
	"wc/dom/Widget",
	"wc/debounce"],
	function(event, getFilteredGroup, initialise, shed, common, Widget, debounce) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/action~Action
		 * @private
		 */
		function Action() {
			var registry = new Conditions(enableDisableButton),
				ACTION_CONTAINER = new Widget("", "wc-actions"),
				ACTION_BUTTON = common.BUTTON.clone(),
				ACTION_TABLE = common.WRAPPER,
				ROW_CONTAINER = common.TBODY,
				ROW = common.TR.clone();

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
					otherSelected = 0,
					currentSelected = 0,
					totalSelected = 0;

				if ((table = ACTION_TABLE.findAncestor(button))) {
					min = condition.min;
					max = condition.max;

					if (!(min || max)) { // no condition worth testing.
						return true;
					}

					currentSelected = getFilteredGroup(ROW_CONTAINER.findDescendant(table)).length;

					if (condition.otherSelected) {
						otherSelected = condition.otherSelected;
					}

					totalSelected = currentSelected + otherSelected;

					if ((min && totalSelected < min) || (max && totalSelected > max)) {
						return false;
					}
				}
				return true;
			}


			/**
			 * Test if an action button can be enabled.
			 * @function
			 * @private
			 * @param {Element} button
			 * @returns {Boolean} true if the conditions of the button are met.
			 */
			function canEnableButton(button) {
				var conditions = registry.get(button);
				if (conditions) {
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
				conditions = registry.get(button);
				if (conditions) {
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
//			function setUp(container) {
//				var _container = container || document.body;
//				Array.prototype.forEach.call(ACTION_BUTTON.findDescendants(_container), function(next) {
//					next.setAttribute("formnovalidate", "formnovalidate");
//					enableDisableButton(next);
//				});
//			}

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
				// setUp();
				// processResponse.subscribe(setUp, true);  // Re-evaluation will be triggered when the register method is called by AJAX scripts
				registry.update();  // Probably not strictly necessary but just in case...
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

			this.register = function(actionArray) {
				actionArray.forEach(registry.set, registry);
			};
		}

		/**
		 * A special registry to handle button conditions.
		 *
		 * @param {Function} buttonChangeFunc The function to call when a button's conditions have been changed.
		 * @constructor
		 */
		function Conditions(buttonChangeFunc) {
			var data = {},
				changed = {};

			/**
			 * Ask the registry to ensure that all buttons are "up to date" with their registered conditions.
			 * If there have been any condition changes since the last update then they will be applied.
			 * Note: buttons may not be updated synchronously.
			 */
			this.update = debounce(function() {
				var i, button, id, changedIds = Object.keys(changed);
				for (i = 0; i < changedIds.length; i++) {
					id = changedIds[i];
					button = document.getElementById(id);
					if (button) {
						delete changed[id];
						button.setAttribute("formnovalidate", "formnovalidate");  // this really only needs to happen once but meh
						buttonChangeFunc(button);
					} else {
						/*
						 * This could theoretically happen if the condition is registered before both:
						 * - the DOM is ready AND
						 * - the debounce timer is done
						 * I never saw it happen though.
						 * It should be OK if update is called again because we won't remove the changed flag until it's actioned.
						 * I wouldn't call update from itself though for risk of infinite looping.
						 */
						console.warn("Could not find button", id);  // this may be resolved in postInit
					}
				}
			}, 200);

			/**
			 * Register a button condition.
			 * @param {Object} action The table action which holds the conditions.
			 */
			this.set = function(action) {
				var id = action ? action.trigger : null;
				if (id) {
					data[id] = action.conditions;
					changed[id] = true;
					this.update();
				}
			};

			/**
			 * Get registered conditions for a given table action button.
			 *
			 * @function
			 * @private
			 * @param {Element} button The table action invoking button.
			 * @returns {Object} The action conditions.
			 */
			this.get = function(button) {
				if (!button) {
					return null;
				}
				return data[button.id];
			};
		}

		var instance = new Action();
		initialise.register(instance);
		return instance;
	});
