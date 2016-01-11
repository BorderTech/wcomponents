/**
 * Provides functionality to select/deselect all checkboxes in a group. Generally applies to
 * {@link module:wc/ui/checkBoxSelect} but can work with any check boxes in any container.
 *
 *
 * @module
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/checkboxAnalog
 * @requires module:wc/ui/radioAnalog
 *
 */
define(["wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/getFilteredGroup",
		"wc/array/toArray",
		"wc/dom/formUpdateManager",
		"wc/dom/Widget",
		"wc/dom/initialise",
		"wc/ui/checkboxAnalog",
		"wc/ui/radioAnalog"],
	/** @param shed wc/dom/shed @param tag wc/dom.tag @param getFilteredGroup wc/dom/getFilteredGroup @param toArray wc/array/toArray @param formUpdateManager wc/dom/formUpdateManager @param Widget wc/dom/Widget @param initialise wc/dom/initialise @ignore */
	function(shed, tag, getFilteredGroup, toArray, formUpdateManager, Widget, initialise) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/selectToggle~SelectToggle
		 * @private
		 */
		function SelectToggle() {
			var CLASS_TOGGLE = "wc_seltog",
				registry = {},
				CONTROLLER_WD = new Widget("", CLASS_TOGGLE),
				GROUP_CONTROLLER,
				CONTROLLER_ABSTRACT = new Widget("button", CLASS_TOGGLE),
				CONTROLLER_CHECKBOX_WD = CONTROLLER_ABSTRACT.extend("", {role: "checkbox"}),
				CONTROLLER_LIST_WD = new Widget("span", CLASS_TOGGLE),
				SUBCONTROLLER_WD = CONTROLLER_ABSTRACT.extend("", {"role": "radio"}),
				ACTIVE_CONTROLLER_WD = SUBCONTROLLER_WD.extend("", {"aria-checked": "true"}),
				CHECKBOX_WD = new Widget("input", "", {"type": "checkbox"}),
				ARIA_CB_WD = new Widget("", "", {"role": "checkbox"}),
				ROW_WD = new Widget("tr", "", {"role": "row", "aria-selected": null}),
				ALL_CB = [CHECKBOX_WD, ARIA_CB_WD, ROW_WD],
				TABLE_WRAPPER = new Widget("div", "table"),
				STATE = {ALL: "all",
						NONE: "none",
						MIXED: "some",
						UNKOWN: "unknown"};

			SUBCONTROLLER_WD.descendFrom(CONTROLLER_WD);

			/**
			 * Set up a registry of all select togglers and their group keyed on id.
			 *
			 * @function
			 * @public
			 * @param {Object[]} objArr an array of selectToggle dtos.
			 */
			this.register = function(objArr) {
				objArr.forEach(function (next) {
					registry[next.identifier] = next;
				});
			};


			/**
			 * Helper function to determine if the selectToggle is a table rowSelection controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The selectToggle
			 * @returns {Boolean} true if the element is a table row selection controller.
			 */
			function isTableRowSelectToggle(element) {
				var table,
					_element = element,
					controlId;

				if (CONTROLLER_LIST_WD.isOneOfMe(element)) {
					_element = SUBCONTROLLER_WD.findDescendant(element);
				}

				controlId = _element.getAttribute("aria-controls");
				if (!controlId) {
					return false;
				}

				// table rowSelectionControls only control their table
				if (controlId.split(/\s+/).length > 1) {
					return false;
				}

				table = document.getElementById(controlId);

				if (!table) {
					return false;
				}

				return table.tagName === tag.TBODY;
			}

			/**
			* Write the state of the select toggles when a form submission takes place.
			*
			* @function
			* @private
			* @param {Element} form The form or sub-form the state of which is being written.
			* @param {Element} stateContainer The element to which to append the state inputs.
			*/
			function writeState(form, stateContainer) {
				Array.prototype.forEach.call(CONTROLLER_CHECKBOX_WD.findDescendants(form), function (next) {
					var state = STATE.UNKNOWN;
					if (!shed.isDisabled(next) && !isTableRowSelectToggle(next)) {
						if (shed.isSelected(next) === shed.state.MIXED) {
							state = STATE.MIXED;
						}
						else if (shed.isSelected(next) === shed.state.SELECTED) {
							state = STATE.ALL;
						}
						else if (shed.isSelected(next) === shed.state.DESELECTED) {
							state = STATE.NONE;
						}
						if (state !== STATE.UNKNOWN) {
							formUpdateManager.writeStateField(stateContainer, next.getAttribute("data-wc-name"), state);
						}
					}
				});

				/*
				 * Write the state of selectToggles of type text when no options are selected
				 * NOTE: if either radio analog is selected then the state is written by radioAnalog
				 * @param next the containing element of a selectToggle of type text
				 */
				Array.prototype.forEach.call(CONTROLLER_LIST_WD.findDescendants(form), function (next) {
					var activeController, reportValue, reportName;
					if (!shed.isDisabled(next) && !isTableRowSelectToggle(next)) {
						activeController = ACTIVE_CONTROLLER_WD.findDescendant(next);
						if (!activeController) {
							reportValue = "some";
							reportName = SUBCONTROLLER_WD.findDescendant(next).getAttribute("data-wc-name");
							// note: all buttons in the selectToggle group have the same name
						}
						formUpdateManager.writeStateField(stateContainer, reportName, reportValue);
					}
				});
			}

			/**
			 * Get a group object from the registry based on name. This is a helper for getController.
			 *
			 * @function
			 * @private
			 * @param {String} name The groupName we are hoping to find in the registry.
			 * @returns {Object} registry object if any found.
			 */
			function getNamedGroup(name) {
				var key,
					next;
				for (key in registry) {
					next = registry[key];
					if (next.groupName === name) {
						return next;
					}
				}
				return null;
			}

			/**
			 * Find the registry object of the nearest ancestor container which is controlled by a selectToggle. Test to
			 * make sure we have a controller:element type match because a tableRowSelect can only control table row
			 * selection but the table rows can be controlled by a WSelectToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} element The checkbox which is controlled.
			 * @returns {Element} The selection controller if any.
			 */
			function getController(element) {
				var parent = element,
					controllerDto,
					controller,
					table,
					groupName;

				// whilst a table row selection control can only select rows a row can be selected by a WSelectToggle.
				// Therefore it is not sufficient to find the table row selection controller or return null here.
				if (ROW_WD.isOneOfMe(element)) {
					if ((table = TABLE_WRAPPER.findAncestor(element))) {
						controller = CONTROLLER_WD.findDescendant(table);
					}
				}
				else if ((groupName = element.getAttribute("data-wc-cbgroup"))) {
					GROUP_CONTROLLER = CONTROLLER_WD.extend("", {"data-wc-cbgroup": groupName});
					controller = GROUP_CONTROLLER.findDescendant(document.body);
				}

				if (!controller) {
					while (parent && parent.parentNode) {
						if (parent.id && (controllerDto = getNamedGroup(parent.id))) {
							break;
						}
						parent = parent.parentNode;
					}
					if (controllerDto) {
						controller = document.getElementById(controllerDto.identifier);
					}
					// if we have a checkbox inside a table content we may get a false positive
					if (controller && isTableRowSelectToggle(controller) && !ROW_WD.isOneOfMe(element)) {
						controller = null;
					}
				}
				return controller;
			}

			/**
			 * Get all of the checkboxes which are controlled by a selectToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} controller The selectToggle
			 * @returns {Element[]} The elements in the group as an Array not as a nodeList or null if no group found.
			 */
			function getGroup(controller) {
				var result = null,
					groupName,
					container,
					namedGroupWd,
					SPACE = /\s+/,
					ARIA_CONTROLS = "aria-controls";

				if ((groupName = controller.getAttribute("data-wc-cbgroup"))) {
					namedGroupWd = [CHECKBOX_WD.extend("", {"data-wc-cbgroup": groupName}), ARIA_CB_WD.extend("", {"data-wc-name": groupName})];
					result = toArray(Widget.findDescendants(document, namedGroupWd));
				}
				else if (CONTROLLER_CHECKBOX_WD.isOneOfMe(controller) || SUBCONTROLLER_WD.isOneOfMe(controller)) {
					groupName = controller.getAttribute(ARIA_CONTROLS);
				}
				else {
					groupName = SUBCONTROLLER_WD.findDescendant(controller).getAttribute(ARIA_CONTROLS);
				}

				if (!result && SPACE.test(groupName)) {
					groupName = groupName.split(SPACE);
					result = groupName.map(function (next) {
						return document.getElementById(next);
					});
				}
				if (!result) {
					container = document.getElementById(groupName);
					if (container && isTableRowSelectToggle(controller)) {
						result = toArray(ROW_WD.findDescendants(container, true));  // group.get(container);
					}
					else if (container) {
						result = toArray(Widget.findDescendants(container, ALL_CB));
					}
					else {
						namedGroupWd = [CHECKBOX_WD.extend("", { "name": groupName }), ARIA_CB_WD.extend("", { "data-wc-name": groupName })];
						result = toArray(Widget.findDescendants(document, namedGroupWd));
					}
				}
				return result;
			}

			/**
			 * Undertake the "click" of selectable components controlled by a select toggle. A helper for
			 * {@link module:wc/ui/selectToggle~shedSubscriber}.
			 *
			 * @function
			 * @private
			 * @param {Element} trigger The select toggle trigger element.
			 */
			function activateClick(trigger) {
				var _group, state;

				if ((_group = getGroup(trigger)) && _group.length) {
					if (CONTROLLER_CHECKBOX_WD.isOneOfMe(trigger) || !(state = trigger.getAttribute("data-wc-value"))) {
						state = shed.isSelected(trigger) === shed.state.DESELECTED ? STATE.NONE : STATE.ALL;
					}
					_group = getFilteredGroup(_group, {filter: getFilteredGroup.FILTERS[(state === STATE.ALL) ? "deselected" : "selected"] | getFilteredGroup.FILTERS.enabled | getFilteredGroup.FILTERS.visible});
					if (_group.length) {
						_group = _group.filter(function (next) {
							return !(CONTROLLER_WD.isOneOfMe(next) || next.getAttribute("aria-readonly") === "true");
						});
					}
					if (_group.length) {
						_group.forEach(function (next) {
							shed[(state === STATE.ALL) ? "select" : "deselect"](next);
						});
					}
				}
			}

			/**
			 * Set the controller based on status. A helper for {@link module:wc/ui/selectToggle~shedSubscriber}.
			 *
			 * @function
			 * @private
			 * @param {Element} controller A WSelectToggle.
			 * @param {String} status The status to set "all", "some" or "none".
			 */
			function setControllerStatus(controller, status) {
				var controllerWd, activeController;
				if (!controller) {
					return;
				}

				if (CONTROLLER_CHECKBOX_WD.isOneOfMe(controller)) {
					if (status === STATE.ALL && shed.isSelected(controller) !== shed.state.SELECTED) {
						shed.select(controller);
					}
					else if (status === STATE.MIXED && shed.isSelected(controller) !== shed.state.MIXED) {
						shed.mix(controller);
					}
					else if (status === STATE.NONE && shed.isSelected(controller) !== shed.state.DESELECTED) {
						shed.deselect(controller);
					}
				}
				else if (status === STATE.MIXED && (activeController = ACTIVE_CONTROLLER_WD.findDescendant(controller))) {
					shed.deselect(activeController);
				}
				else {
					controllerWd = SUBCONTROLLER_WD.extend("", {"data-wc-value": status});
					if ((activeController = controllerWd.findDescendant(controller)) && !shed.isSelected(activeController)) {
						shed.select(activeController);
					}
				}
			}

			/**
			 * Listen for select/deselect and act on any controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being selected/deselected.
			 * @param {String} action shed.SELECT or shed.DESELECT.
			 */
			function shedObserver(element, action) {
				var _group,
					selected,
					controller,
					groupState;

				if (!element) {
					return;
				}

				if ((action === shed.actions.SELECT && SUBCONTROLLER_WD.isOneOfMe(element)) ||
					((action === shed.actions.SELECT || action === shed.actions.DESELECT) && CONTROLLER_CHECKBOX_WD.isOneOfMe(element))) {
					activateClick(element);
				}

				if (Widget.isOneOfMe(element, ALL_CB) && (controller = getController(element))) {
					_group = getGroup(controller);

					if (!_group && _group.length) {
						// no grouped items means no controller to set state on.
						return;
					}

					selected = getFilteredGroup(_group);
					groupState = STATE.MIXED;

					if (_group.length === selected.length) {
						groupState = STATE.ALL;
					}
					else if (selected.length === 0) {
						groupState = STATE.NONE;
					}

					setControllerStatus(controller, groupState);
				}
			}


			/**
			 * Late initialisation to addd {@link module:wc/dom/shed} and {@link module:wc/dom/formUpdateManager}
			 * subscribers.
			 *
			 * @function module:wc/ui/selectToggle.postInit
			 * @public
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.DESELECT, shedObserver);
				shed.subscribe(shed.actions.MIX, shedObserver);
				formUpdateManager.subscribe(writeState);
			};
		}

		var /** @alias module:wc/ui/selectToggle */instance = new SelectToggle();
		initialise.register(instance);
		return instance;
	});
