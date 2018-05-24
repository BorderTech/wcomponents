define(["wc/dom/shed",
	"wc/dom/getFilteredGroup",
	"wc/dom/classList",
	"wc/array/toArray",
	"wc/dom/formUpdateManager",
	"wc/dom/Widget",
	"wc/dom/initialise",
	"wc/dom/uid",
	"wc/ui/table/common",
	"wc/ui/rowAnalog",
	"wc/ui/ajax/processResponse",
	"wc/ui/getFirstLabelForElement",
	"wc/i18n/i18n",
	"wc/ui/icon",
	"wc/ui/checkBox",
	"wc/ui/checkboxAnalog",
	"wc/ui/radioAnalog"],
	function(shed, getFilteredGroup, classList, toArray, formUpdateManager, Widget, initialise, uid, table, rowAnalog, processResponse,
		getFirstLabelForElement, i18n, icon, checkBox) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/selectToggle~SelectToggle
		 * @private
		 */
		function SelectToggle() {
			var registry = {},
				WSELECTTOGGLE_CLASS = "wc-selecttoggle",
				CONTROLLER_WD,
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
				ALL_CB,
				TABLE_WD,
				TBODY_WD,
				ARIA_CONTROLS = "aria-controls",
				TARGET_ATTRIB = "data-wc-target",
				STAND_IN_LABEL,
				STAND_IN_TEXT_EQUIV,
				STATE = {ALL: "all",
					NONE: "none",
					MIXED: "some",
					UNKOWN: "unknown"},
				inited;

			/**
			 * This module has a **lot** of Widgets. We only need these Widgets if the module actually gets used. There
			 * are two entry points: a shed subscriber and the state writer. This means we can delay the instantiation
			 * of these Widgets until some of them are needed.
			 *
			 * @function
			 * @private
			 */
			function initialiseControllers() {
				if (inited) {
					return;
				}
				var CHECKBOX = "checkbox",
					CLASS_TOGGLE = "wc_seltog",
					cbRoleObj = {"role": CHECKBOX};

				CONTROLLER_WD = new Widget("", CLASS_TOGGLE);
				CONTROLLER_ABSTRACT = new Widget("button", CLASS_TOGGLE);
				CONTROLLER_CHECKBOX_WD = CONTROLLER_ABSTRACT.extend("", cbRoleObj);
				CONTROLLER_LIST_WD = new Widget("span", CLASS_TOGGLE);
				CONTROLLER_MENU_WD = CONTROLLER_WD.extend("wc_submenucontent");
				RADIO_SUBCONTROLLER = CONTROLLER_ABSTRACT.extend("", {"role": "radio"});
				MENU_SUBCONTROLLER = CONTROLLER_ABSTRACT.extend("", {"role": "menuitemradio"});
				SUBCONTROLLER_WD = [RADIO_SUBCONTROLLER, MENU_SUBCONTROLLER];

				ACTIVE_CONTROLLER_WD = SUBCONTROLLER_WD.map(function (next) {
					return next.extend("", {"aria-checked": "true"});
				});

				CHECKBOX_WD = checkBox.getWidget().clone();
				ARIA_CB_WD = new Widget("", "", cbRoleObj);
				ROW_WD = rowAnalog.ITEM.clone();
				TABLE_WD = table.TABLE.extend("", {"aria-multiselectable": "true"});
				TBODY_WD = table.TBODY.clone();
				TBODY_WD.descendFrom(TABLE_WD, true);
				ROW_WD.descendFrom(TBODY_WD, true);
				ALL_CB = [CHECKBOX_WD, ARIA_CB_WD, ROW_WD];
				inited = true;
			}

			function isWSelectToggleContainer(element) {
				return element && classList.contains(element, WSELECTTOGGLE_CLASS);
			}

			function isWSelectToggle(element) {
				var el;
				if (isWSelectToggleContainer(element)) {
					return true;
				}
				if (RADIO_SUBCONTROLLER.isOneOfMe(element)) {
					el = CONTROLLER_LIST_WD.findAncestor(element);
					return isWSelectToggleContainer(el);
				}
				return false;
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
					if (!shed.isDisabled(next) && isWSelectToggle(next)) {
						if (shed.isSelected(next) === shed.state.MIXED) {
							state = STATE.MIXED;
						} else if (shed.isSelected(next) === shed.state.SELECTED) {
							state = STATE.ALL;
						} else if (shed.isSelected(next) === shed.state.DESELECTED) {
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
					var reportValue, reportName;
					if (!shed.isDisabled(next) && isWSelectToggle(next)) {
						if (!Widget.findDescendant(next, ACTIVE_CONTROLLER_WD)) {
							reportValue = "some";
							reportName = RADIO_SUBCONTROLLER.findDescendant(next).getAttribute("data-wc-name"); // note: all buttons in the selectToggle group have the same name
							formUpdateManager.writeStateField(stateContainer, reportName, reportValue);
						}
					}
				});
			}

			function getControlledElements(trigger) {
				var actualTrigger = trigger, idList,
					candidates;

				if (CONTROLLER_LIST_WD.isOneOfMe(trigger)) {
					actualTrigger = RADIO_SUBCONTROLLER.findDescendant(trigger);
				} else if (CONTROLLER_MENU_WD.isOneOfMe((trigger))) {
					actualTrigger = MENU_SUBCONTROLLER.findDescendant(trigger);
				}

				idList = actualTrigger.getAttribute(ARIA_CONTROLS);
				if (idList) {
					candidates = [];
					idList.split(" ").forEach(function (next) {
						candidates.push(document.getElementById(next));
					});
					return candidates;
				}
				return null;
			}

			function getNamedGroup(groupName) {
				var namedGroupWd = [CHECKBOX_WD.extend("", {"data-wc-group": groupName}), ARIA_CB_WD.extend("", {"data-wc-group": groupName})];
				return toArray(Widget.findDescendants(document, namedGroupWd));
			}

			function getAllControllers(element) {
				if (!(element && element.id)) {
					return null;
				}
				var controllingWidget = CONTROLLER_ABSTRACT.extend("", {"aria-controls": element.id});
				return controllingWidget.findDescendants(document.body);
			}

			/**
			 * Get all of the components which are controlled by a selectToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} controller The selectToggle.
			 * @returns {Element[]} The elements in the group as an Array not as a nodeList or null if no group found.
			 */
			function getGroup(controller) {
				var targetId,
					targetElement,
					candidates,
					groupName;

				if (!controller) {
					return null;
				}

				targetId = controller.getAttribute(TARGET_ATTRIB);
				if (!targetId) {
					// NOTE: the aria-controls list of a WTable row selection sub controller is set in the renderer as
					// all of the information to render this is available.
					return null;
				}

				if ((targetElement = document.getElementById(targetId))) {
					if (CHECKBOX_WD.isOneOfMe(targetElement)) {
						groupName = targetElement.getAttribute("data-wc-group");
						if (!groupName) {
							return [targetElement];
						}
						return getNamedGroup(groupName);
					}
					// hurray, the easy one! Get every checkbox or multi-selectable table row inside the target.
					// NOTE: the sub-row selector in WTable does not have the data-wc-target attribute and therefore
					// will never be here
					if (isWSelectToggle(controller)) {
						// get all checkboxes and surrogates inside the targetElement
						candidates = toArray(Widget.findDescendants(targetElement, ALL_CB));
						// remove any which are themselves a controller
						return candidates.map(function (next) {
							return CONTROLLER_CHECKBOX_WD.isOneOfMe(next) ? null : next;
						});
					}

					// WTable select/deselect all
					candidates = toArray(ROW_WD.findDescendants(targetElement));
					// we only want those rows in the currenttable, not in nested tables.
					return candidates.map(function (next) {
						return TBODY_WD.findAncestor(next) === targetElement ? next : null;
					});
				}
				// No target element means a WSelectToggle with a named group
				return getNamedGroup(targetId);
			}

			/**
			 * Undertake the "click" of selectable components controlled by a select toggle. A helper for
			 * {@link module:wc/ui/selectToggle~shedSubscriber}.
			 *
			 * @function
			 * @private
			 * @param {Element} trigger The select toggle trigger element.
			 * @returns {?number} The number of items affected by this activation. This is needed to prevent a double
			 * activation of a SUB_CONTROLLER from erroneously setting the sub-controllers state.
			 */
			function activateTrigger(trigger) {
				var _group, state, groupFilter;

				if ((_group = getControlledElements(trigger)) && _group.length) {
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
				var initialState, from = [], to,
					ICON_ALL = "fa-check-square-o",
					ICON_SOME = "fa-square",
					ICON_NONE = "fa-square-o";
				if (!controller) {
					return;
				}

				if (CONTROLLER_CHECKBOX_WD.isOneOfMe(controller)) {
					// By this stage it is too late to calculate the old icon from the
					// selected state as the controller may have had its state changed by
					// a click event on itself, not by a change in the state of one of its
					// controlled elements.
					initialState = shed.isSelected(controller);
					if (status === STATE.ALL && initialState !== shed.state.SELECTED) {
						shed.select(controller, true);
						to = ICON_ALL;
						from = [ICON_SOME, ICON_NONE];
					} else if (status === STATE.MIXED && initialState !== shed.state.MIXED) {
						shed.mix(controller, true);
						to = ICON_SOME;
						from = [ICON_ALL, ICON_NONE];
					} else if (status === STATE.NONE && initialState !== shed.state.DESELECTED) {
						shed.deselect(controller, true);
						to = ICON_NONE;
						from = [ICON_SOME, ICON_ALL];
					}
					if (to) {
						icon.change(controller, to, from[0]);
						icon.remove(controller, from[1]);
					}
					return;
				}

				if (status === STATE.MIXED || controller.getAttribute("data-wc-value") !== status) {
					shed.deselect(controller, true);
					return;
				}
				shed.select(controller, true);
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
				var allControllers;

				if (!element) {
					return;
				}

				// Change the selected stae of a WSelectToggle button:
				if ((action === shed.actions.SELECT && Widget.isOneOfMe(element, SUBCONTROLLER_WD)) ||
					((action === shed.actions.SELECT || action === shed.actions.DESELECT) && (CONTROLLER_CHECKBOX_WD.isOneOfMe(element)))) {
					/* If activateTrigger returns exactly 0 we did not change the state of any controls so we won't
					 * have set the state of the controller and it may be in the incorrect state. This _will_ be the
					 * case if, for example, someone clicks a "select all" which controls only selected and hidden
					 * components. Nothing will be selected in activateTigger so the state of the controller will not
					 * have been updated by this shed observer and it will remain in an erroneous selected state (the
					 * controller should be mixed). */
					if (activateTrigger(element) === 0) {
						controlStatusHelper(element);
					}
					return;
				}

				if (Widget.isOneOfMe(element, ALL_CB)) {
					allControllers = getAllControllers(element);
					if (!(allControllers && allControllers.length)) {
						return;
					}

					Array.prototype.forEach.call(allControllers, controlStatusHelper);
				}
			}

			function controlStatusHelper(controller) {
				var controlledElements,
					selected,
					groupState = STATE.MIXED;

				if (shed.isDisabled(controller) || !(controlledElements = getControlledElements(controller))) {
					// no grouped items means no controller to set state on.
					return;
				}

				if (controlledElements.length === 0) {
					groupState = STATE.NONE;
				} else if ((selected = getFilteredGroup(controlledElements))) {
					if (selected.length === 0) {
						groupState = STATE.NONE;
					} else if (controlledElements.length === selected.length) {
						groupState = STATE.ALL;
					}
				}

				setControllerStatus(controller, groupState);
			}

			/**
			 * Set the aria-controls attribute on the buttons of a selectToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} element a collapsible toggle wrapper
			 */
			function setControlList(element) {
				var candidates = getGroup(element),
					idArray = [], ids;

				if (candidates) {
					candidates.forEach(function (next) {
						if (!next.id) {
							next.id = uid();
						}
						idArray.push(next.id);
					});

					if (idArray.length) {
						ids = idArray.join(" ");
						if (CONTROLLER_CHECKBOX_WD.isOneOfMe(element)) {
							element.setAttribute(ARIA_CONTROLS, ids);
						} else {
							Array.prototype.forEach.call(RADIO_SUBCONTROLLER.findDescendants(element), function (next) {
								next.setAttribute(ARIA_CONTROLS, ids);
							});
						}
					}
				}
			}

			/**
			 * Set aria-controls for each collapsible toggle
			 *
			 * @function
			 * @private
			 */
			function setControls() {
				if (!inited) {
					initialiseControllers();
				}
				Array.prototype.forEach.call(CONTROLLER_WD.findDescendants(document.body), setControlList);
			}

			function setAriaLabelAttrib(element) {
				var label = getFirstLabelForElement(element),
					elId = element.id,
					id,
					labelStr;

				if (!label) {
					STAND_IN_LABEL = STAND_IN_LABEL || i18n.get("toggle_label");
					labelStr = "<span wc-data-for='" +elId + "' id='" + elId + "_l'>" + STAND_IN_LABEL + "</span>";
					element.insertAdjacentHTML("afterbegin", labelStr);
					label = element.firstChild;
				}

				if (label && (id = label.id)) {
					Array.prototype.forEach.call(RADIO_SUBCONTROLLER.findDescendants(element), function (next) {
						next.setAttribute("aria-labelledby", id);
					});
				}
			}

			function setTextEquivalent(element) {
				var label = getFirstLabelForElement(element);

				if (label) {
					element.setAttribute("aria-labelledby", label.id);
					return;
				}
				STAND_IN_TEXT_EQUIV = STAND_IN_TEXT_EQUIV || i18n.get("toggle_all_label");
				if (isWSelectToggle(element)) {
					element.setAttribute("title", STAND_IN_TEXT_EQUIV);
				} else {
					element.insertAdjacentHTML("beforeend", "<span>" + STAND_IN_TEXT_EQUIV + "</span>");
				}
			}

			function setLabelledBy(element) {
				var el = element || document.body;
				if (!inited) {
					initialiseControllers();
				}
				if (CONTROLLER_LIST_WD.isOneOfMe(el)) {
					setAriaLabelAttrib(el);
				} else if (CONTROLLER_CHECKBOX_WD.isOneOfMe(el)) {
					setTextEquivalent(el);
				} else {
					Array.prototype.forEach.call(CONTROLLER_LIST_WD.findDescendants(el), setAriaLabelAttrib);
					Array.prototype.forEach.call(CONTROLLER_CHECKBOX_WD.findDescendants(el), setTextEquivalent);
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
				setControls();
				setLabelledBy();
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.DESELECT, shedObserver);
				shed.subscribe(shed.actions.MIX, shedObserver);
				formUpdateManager.subscribe(writeState);
				processResponse.subscribe(setControls, true);
				processResponse.subscribe(setLabelledBy, true);
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

		/**
		 * Provides functionality to select/deselect all checkboxes in a group. Generally applies to {@link module:wc/ui/checkBoxSelect} but can work
		 * with any check boxes in any container.
		 *
		 * The following modules are imported as dependencies as their functionality us used by WSelectToggle but they are not required by the
		 * code in this module.
		 *
		 * * {@link module:wc/ui/checkboxAnalog}
		 * * {@link module:wc/ui/radioAnalog}
		 *
		 * @module
		 * @requires wc/dom/shed
		 * @requires wc/dom/getFilteredGroup
		 * @requires wc/dom/classList,
		 * @requires wc/array/toArray
		 * @requires wc/dom/formUpdateManager
		 * @requires wc/dom/Widget
		 * @requires wc/dom/initialise
		 * @requires wc/ui/table/common
		 * @requires wc/ui/rowAnalog
		 * @requires wc/ui/ajax/processResponse
		 * @requires wc/ui/getFirstLabelForElement
		 * @requires wc/i18n/i18n
		 * @requires wc/ui/icon
		 * @requires wc/ui/checkBox
		 */
		var instance = new SelectToggle();
		initialise.register(instance);
		return instance;
	});
