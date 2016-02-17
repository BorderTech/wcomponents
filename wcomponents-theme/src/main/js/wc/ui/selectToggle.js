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
		"wc/ui/table/common",
		"wc/ui/rowAnalog",
		"wc/ui/checkboxAnalog",
		"wc/ui/radioAnalog"],
	/** @param shed wc/dom/shed @param tag wc/dom.tag @param getFilteredGroup wc/dom/getFilteredGroup @param toArray wc/array/toArray @param formUpdateManager wc/dom/formUpdateManager @param Widget wc/dom/Widget @param initialise wc/dom/initialise @param table @param rowAnalog @ignore */
	function(shed, tag, getFilteredGroup, toArray, formUpdateManager, Widget, initialise, table, rowAnalog) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/selectToggle~SelectToggle
		 * @private
		 */
		function SelectToggle() {
			var registry = {},
				CONTROLLER_WD,
				GROUP_CONTROLLER,
				CONTROLLER_ABSTRACT,
				CONTROLLER_CHECKBOX_WD,
				CONTROLLER_LIST_WD,
				CONTROLLER_MENU_WD,
				RADIO_SUBCONTROLLER,
				MENU_SUBCONTROLLER,
				SUBCONTROLLER_WD,
				ACTIVE_CONTROLLER_WD,
				CHECKBOX_WD,
				ARIA_CB_WD,
				ROW_WD,
				CELL_WD,
				ALL_CB,
				TABLE_WRAPPER,
				ARIA_CONTROLS = "aria-controls",
				STATE = {ALL: "all",
						NONE: "none",
						MIXED: "some",
						UNKOWN: "unknown"},
				inited;

			/**
			 * This module has a **lot** of Widgets. We only need these Widgets if the module actually gets used. There
			 * are three entry points: two shed subscribers and the state writer. This means we can delay the
			 * instantiation of these Widgets until some of them are needed.
			 *
			 * @function
			 * @public
			 */
			function initialiseControllers() {
				var CHECKBOX = "checkbox",
					CLASS_TOGGLE = "wc_seltog";

				CONTROLLER_WD = new Widget("", CLASS_TOGGLE),
				CONTROLLER_ABSTRACT = CONTROLLER_ABSTRACT || new Widget("button", CLASS_TOGGLE);
				CONTROLLER_CHECKBOX_WD = CONTROLLER_CHECKBOX_WD || CONTROLLER_ABSTRACT.extend("", {role: CHECKBOX});
				CONTROLLER_LIST_WD = CONTROLLER_LIST_WD || new Widget("span", CLASS_TOGGLE);
				CONTROLLER_MENU_WD = CONTROLLER_MENU_WD || CONTROLLER_WD.extend("wc_submenucontent");
				RADIO_SUBCONTROLLER = RADIO_SUBCONTROLLER || CONTROLLER_ABSTRACT.extend("", {"role": "radio"});
				MENU_SUBCONTROLLER = MENU_SUBCONTROLLER || CONTROLLER_ABSTRACT.extend("", {"role": "menuitemradio"});
				SUBCONTROLLER_WD = SUBCONTROLLER_WD || [RADIO_SUBCONTROLLER, MENU_SUBCONTROLLER];

				ACTIVE_CONTROLLER_WD = ACTIVE_CONTROLLER_WD || SUBCONTROLLER_WD.map(function (next) {
					return next.extend("", {"aria-checked": "true"});
				});

				CHECKBOX_WD = CHECKBOX_WD || new Widget("input", "", {"type": CHECKBOX});
				ARIA_CB_WD = ARIA_CB_WD || new Widget("", "", {"role": CHECKBOX});
				ROW_WD = ROW_WD || rowAnalog.ITEM;
				CELL_WD = CELL_WD || table.TD.extend("wc_table_sel_wrapper");
				ALL_CB = ALL_CB || [CHECKBOX_WD, ARIA_CB_WD, ROW_WD];
				TABLE_WRAPPER = TABLE_WRAPPER || table.WRAPPER;

				inited = true;
			}

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
					_element = RADIO_SUBCONTROLLER.findDescendant(element);
				}

				controlId = _element.getAttribute(ARIA_CONTROLS);

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
				if (!inited) {
					initialiseControllers();
				}

				// CHECKBOX type controllers
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
						activeController = Widget.findDescendant(next, ACTIVE_CONTROLLER_WD);
						if (!activeController) {
							reportValue = "some";
							reportName = RADIO_SUBCONTROLLER.findDescendant(next).getAttribute("data-wc-name"); // note: all buttons in the selectToggle group have the same name
							formUpdateManager.writeStateField(stateContainer, reportName, reportValue);
						}
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
			 * Get the sub-row controlling 'menu' controller which controls a row. A row which is itself both a
			 * selectable sub-row and a row with selectable sub-rows will be controlled by both its 'parent' row's
			 * controller and its own controller. This inMe arg tells us which one to get.
			 *
			 * @function
			 * @private
			 * @param {Element} element A selectable table row.
			 * @param {boolean} [inMe] If true then look for the row controller inside the row.
			 * @returns {?Element} The sub-row select controller which controls element.
			 */
			function getSubRowController(element, inMe) {
				var sibling, idList, cell;

				if (inMe) {
					if ((cell = CELL_WD.findDescendant(element, true))) {
						return CONTROLLER_MENU_WD.findDescendant(cell);
					}
				}
				else if (element.getAttribute("aria-level") > 1) {
					sibling = element;
					while ((sibling = sibling.previousSibling)) {
						if (sibling.nodeType === Node.ELEMENT_NODE && ROW_WD.isOneOfMe(sibling) && (idList = sibling.getAttribute(ARIA_CONTROLS))) {
							idList = idList.split(/\s/);
							if (idList.indexOf(element.id) >= 0 && (cell = CELL_WD.findDescendant(sibling, true))) {
								return CONTROLLER_MENU_WD.findDescendant(cell);
							}
						}
					}
				}
				return null;
			}

			/**
			 * Is a particular element a sub-row select toggle controller?
			 *
			 * @function
			 * @private
			 * @param {boolean} element The element to text
			 * @returns {Boolean} true if element is a sub-row toggler.
			 */
			function isSubRowController (element) {
				return !!CONTROLLER_MENU_WD.isOneOfMe(element);
			}

			/**
			 * Get the `ui:rowselection/@selectAll` control artefact for a table from any element in the table.
			 *
			 * @function
			 * @private
			 * @param {Element} element The start point.
			 * @returns {?Element} The selectToggle control for the table, if any.
			 */
			function getTableSelectToggleController (element) {
				var wrapper = TABLE_WRAPPER.findAncestor(element),
					controller;
				if (wrapper && (controller = CONTROLLER_WD.findDescendant(wrapper)) && isTableRowSelectToggle(controller)) {
					return controller;
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
				var parent,
					controllerDto,
					controller,
					groupName;

				// whilst a table row selection control can only select rows a row can be selected by a WSelectToggle.
				// Therefore it is **not sufficient** to return the table row selection controller or null here.
				if (ROW_WD.isOneOfMe(element)) {
					controller = (getSubRowController(element, true) || getSubRowController(element)) || getTableSelectToggleController(element);
				}
				else if (isSubRowController(element)) {
					parent = ROW_WD.findAncestor(element);
					// do not look inside the row if we start on a controller - we have already done that.
					controller = getSubRowController(parent) || getTableSelectToggleController(parent);
				}
				else if ((groupName = element.getAttribute("data-wc-cbgroup"))) {
					// we can return a controller or null here.
					GROUP_CONTROLLER = CONTROLLER_WD.extend("", {"data-wc-cbgroup": groupName});
					return GROUP_CONTROLLER.findDescendant(document.body);
				}

				if (!controller) {
					parent = element;
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
			 * @param {Element} controller The selectToggle.
			 * @returns {Element[]} The elements in the group as an Array not as a nodeList or null if no group found.
			 */
			function getGroup(controller) {
				var groupName,
					subController,
					container,
					namedGroupWd,
					SPACE = /\s+/;

				if (!controller) {
					return null;
				}

				if ((groupName = controller.getAttribute("data-wc-cbgroup"))) {
					namedGroupWd = [CHECKBOX_WD.extend("", {"data-wc-cbgroup": groupName}), ARIA_CB_WD.extend("", {"data-wc-name": groupName})];
					return toArray(Widget.findDescendants(document, namedGroupWd));
				}

				groupName = controller.getAttribute(ARIA_CONTROLS);

				if (!groupName && (subController = Widget.findDescendant(controller, SUBCONTROLLER_WD))) {
					groupName = subController.getAttribute(ARIA_CONTROLS);
				}

				if (!groupName) {
					return null;
				}

				if (SPACE.test(groupName)) {
					groupName = groupName.split(SPACE);
					return groupName.map(function (next) {
						return document.getElementById(next);
					});
				}
				if ((container = document.getElementById(groupName))) {
					if (Widget.isOneOfMe(container, ALL_CB)) {
						return [container];
					}

					if (isTableRowSelectToggle(controller)) {
						return toArray(ROW_WD.findDescendants(container, true));
					}
					return toArray(Widget.findDescendants(container, ALL_CB));
				}
				return null;
			}

			/**
			 * Undertake the "click" of selectable components controlled by a select toggle. A helper for
			 * {@link module:wc/ui/selectToggle~shedSubscriber}.
			 *
			 * @function
			 * @private
			 * @param {Element} trigger The select toggle trigger element.
			 * @return {?number} The number of items affected by this activation. This is needed to prevent a double
			 * activation of a SUB_CONTROLLER from erroneously setting the sub-controllers state.
			 */
			function activateTrigger(trigger) {
				var _group, state, groupFilter;

				if ((_group = getGroup(trigger)) && _group.length) {
					if (CONTROLLER_CHECKBOX_WD.isOneOfMe(trigger) || !(state = trigger.getAttribute("data-wc-value"))) {
						state = shed.isSelected(trigger) === shed.state.DESELECTED ? STATE.NONE : STATE.ALL;
					}

					/*
					 * Why the filter variation?
					 *
					 * We normally do not allow users to interact with controls they are not able to perceive. This
					 * complies with normal usability guidelines and keeps "client mode" controls in sync with ajax
					 * modes of the same controls
					 *
					 * Example with row selection
					 *
					 * * When table also has row expansion:
					 *   * in client mode all sub rows are present so could be "selectable" by a select toggle;
					 *   * in lazy or dynamic mode only the descendants of opened rows are available.
					 * * When "select all" is invoked if we allowed hidden rows to be selected
					 *   * in client rows the newly visible rows would be selected;
					 *   * in lazy/dynamic mode the newly visible rows would not be selected.
					 *   This leads to an inconsistent user experience so we do not allow interaction with controls
					 *   which are not visible.
					 *
					 * HOWEVER
					 * * If a row is expanded, then "select all" is invoked the sub row(s) will be selected.
					 * * If the row is then collapsed and "deselect all" is invoked the sub row(s) will not be
					 *   deselected as we do not allow interaction with hidden controls.
					 *
					 * SO:
					 * * If the expand mode is ajax and some other control then refreshes the view (or part thereof
					 *   containing the table)
					 *   * the closed row does not have children, if it is then expanded again
					 *   * the table state does not include "selected" for the child rows as they are not present
					 *   * therefore the newly visible rows will not be selected
					 *
					 * * If the expand mode is client the rows are always present so when the table is refreshed
					 *   * the closed row still has its children, if it is expanded again
					 *   * the table does not send its state to the server and the child rows are not changed
					 *   * therefore the newly visible rows will remain selected which is inconsistent with the above.
					 *
					 * THEREFORE we must allow a selectToggle to **deselect** hidden controls.
					 *
					 * That is why we have a filter variation.
					 */
					groupFilter = getFilteredGroup.FILTERS[(state === STATE.ALL) ? "deselected" : "selected"] | getFilteredGroup.FILTERS.enabled;
					if (state === STATE.ALL) { // we have to allow "hidden" controls to be deslected but not selected.
						groupFilter = groupFilter | getFilteredGroup.FILTERS.visible;
					}
					_group = getFilteredGroup(_group, {filter: groupFilter});

					_group = _group.filter(function (next) {
						return !(CONTROLLER_WD.isOneOfMe(next) || next.getAttribute("aria-readonly") === "true");
					});

					_group.forEach(function (next) {
						shed[(state === STATE.ALL) ? "select" : "deselect"](next);
					});

					return _group.length;
				}
				return null; // do not return 0 as this means we got a group which after filtering was zero length.
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
				else if (status === STATE.MIXED && (activeController = Widget.findDescendant(controller, ACTIVE_CONTROLLER_WD))) {
					shed.deselect(activeController);
				}
				else {
					controllerWd = SUBCONTROLLER_WD.map(function(next) {
						return next.extend("", {"data-wc-value": status});
					});
					if ((activeController = Widget.findDescendant(controller, controllerWd)) && !shed.isSelected(activeController)) {
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
				var controller, done;

				if (!element) {
					return;
				}

				if (!inited) {
					initialiseControllers();
				}

				if ((action === shed.actions.SELECT && Widget.isOneOfMe(element, SUBCONTROLLER_WD)) ||
					((action === shed.actions.SELECT || action === shed.actions.DESELECT) && (CONTROLLER_CHECKBOX_WD.isOneOfMe(element)))) {
					done = activateTrigger(element);
					/* If activateTrigger returns exactly 0 we did not change the state of any controls so we won't
					 * have set the state of the controller and if it was a sub controller then it may be in the
					 * incorrect state. This _will_ be the case if, for example, someone clicks a "select all" sub
					 * controller which controls only selected and hidden components. Nothing will be selected in
					 * activateTigger so the state of the controller will not have been updated by this shed observer
					 * and it will remain in an erroneous selected state (the controller should be mixed). */
					if (done === 0 && Widget.isOneOfMe(element, SUBCONTROLLER_WD) && shed.isSelected(element)) {
						 // could merge but it is getting a bit long ...
						if ((controller = Widget.findAncestor(element, [CONTROLLER_LIST_WD, CONTROLLER_MENU_WD]))) {
							controlStatusHelper(controller);
						}
					}
				}
				/*
				else if ((action === shed.actions.SELECT || action === shed.actions.DESELECT) && ROW_WD.isOneOfMe(element)) {
					if ((controller = getSubRowController(element, true))) {
						setControllerStatus(controller, action === shed.actions.SELECT ? STATE.ALL : STATE.NONE);
					}
				}
				*/
				if (Widget.isOneOfMe(element, ALL_CB) && (controller = getController(element))) {
					if (isSubRowController(controller)) {
						do {
							controlStatusHelper(controller);
						}
						while ((controller = getController(controller)) && (isSubRowController(controller) || isTableRowSelectToggle(controller)));
					}
					else {
						controlStatusHelper(controller);
					}
				}
			}


			function controlStatusHelper(controller) {
				var _group= getGroup(controller),
					selected,
					groupState;

				if (!_group) {
					// no grouped items means no controller to set state on.
					return;
				}
				selected = getFilteredGroup(_group);
				groupState = STATE.MIXED;

				if (_group.length === 0) {
					groupState = STATE.NONE;
				}
				else if (_group.length === selected.length) {
					groupState = STATE.ALL;
				}
				else if (selected.length === 0) {
					groupState = STATE.NONE;
				}
				setControllerStatus(controller, groupState);
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

		}

		var /** @alias module:wc/ui/selectToggle */instance = new SelectToggle();
		initialise.register(instance);
		return instance;
	});
