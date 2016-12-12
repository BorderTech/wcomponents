define(["wc/has",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/focus",
		"wc/dom/shed",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/ui/selectLoader",
		"wc/timers",
		"wc/ui/prompt",
		"wc/ui/ajaxRegion"],
	function(has, event, initialise, focus, shed, uid, Widget, i18n, selectLoader, timers, prompt, ajaxRegion) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/multiFormComponent~MultiFormComponent
		 * @private
		 */
		function MultiFormComponent() {
			var BUTTON_TYPE = {add: 0, remove: 1},
				MAX = "data-wc-max",
				queueTimer,
				CONTAINER = new Widget("fieldset", "wc_mfc"),
				FIELD = new Widget("li"),
				BUTTON = new Widget("button"),
				SELECT_WD = new Widget("select"),
				BUTTON_MESSAGE_WD = new Widget("SPAN"),
				INPUT_WD = new Widget("input"),
				CONTROLS = [SELECT_WD, INPUT_WD],
				REMOVE_BUTTON_TITLE;

			FIELD.descendFrom(CONTAINER);
			BUTTON.descendFrom(FIELD);
			SELECT_WD.descendFrom(FIELD);
			INPUT_WD.descendFrom(FIELD);

			/**
			 * Load data list for cachable WMultiDropdown.
			 *
			 * @see {@link module:wc/ui/selectLoader.load}
			 * @function
			 * @private
			 * @param {String} id The id of a multiDropdown.
			 */
			function load(id) {
				var element = document.getElementById(id),
					selects;
				if (element) {
					selects = SELECT_WD.findDescendants(element);
					Array.prototype.forEach.call(selects, function(next) {
						var id = next.id;
						selectLoader.load(id);
					});
				}
			}

			function processNow(idArr) {
				var id;
				while ((id = idArr.shift())) {
					load(id);
				}
			}

			/*
			 * on click:
			 * 1. Am I a button?
			 * 2. Am I a button that belongs to multiFormComponent?
			 * 3. Am I an add button?
			 * 	- Yes: add a new field
			 *  - No: remove field
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = BUTTON.findAncestor($event.target)) && !shed.isDisabled(element)) {
					doClick(element, $event.shiftKey);
				}
			}

			/*
			 * click event heavy lifter
			 * @param button an add/remove button
			 * @param SHIFT Boolean true if shift key held down during click event
			 */
			function doClick(button, SHIFT) {
				var type = instance.getButtonType(button),
					tryAjax,
					container;
				if (type === BUTTON_TYPE.add) {
					addNewField(button);
					tryAjax = true;
				}
				else if (type === BUTTON_TYPE.remove) {
					removeField(button, SHIFT);
					if (button.type === event.TYPE.submit) {
						shed.hide(button);
						button.form.appendChild(button);
					}
					tryAjax = true;
				}
				if (tryAjax && (container = getContainer(button)) && ajaxRegion.getTrigger(container, true)) {
					ajaxRegion.requestLoad(container, null, true);
				}
			}

			/**
			 * Get the button 'type' (add or remove) for a particular button in a multiFormControl. Only one of them is
			 * an add, the others are all remove.
			 *
			 * @function module:wc/ui/multiFormComponent.getButtonType
			 * @param {Element} element An add or remove button.
			 * @returns {int} either BUTTON_TYPE.add (0) or BUTTON_TYPE.remove (1).
			 */
			this.getButtonType = function (element) {
				var container = getContainer(element), result = -1;
				if (container) {
					result = (element === BUTTON.findDescendant(container)) ? BUTTON_TYPE.add : BUTTON_TYPE.remove;
				}
				return result;
			};

			/**
			 * The container is the top level element which contains all the sub-elements
			 * ie it contains all of the fields.
			 *
			 * @function
			 * @private
			 * @param {Element} element Any child of the container.
			 * @returns {?Element} The container if element is a multi form control or one of its descendent elements.
			 */
			function getContainer(element) {
				return CONTAINER.findAncestor(element);
			}

			/**
			 * Get the field(s) containing each of the interactive controls in a multiFormControl.
			 *
			 * @param {Element} container A multiFormControl.
			 * @param {Boolean} [firstOnly] If true only the first field will be returned.
			 * @returns {?(NodeList|Element)} A collection of fields OR a single fielt if firstOnly is true.
			 */
			function getFields(container, firstOnly) {
				var method = firstOnly ? "findDescendant" : "findDescendants",
					result = FIELD[method](container);
				return result;
			}

			/*
			 * @param element An add/remove button
			 * @returns true if max has not been reached yet
			 */
			function checkMaxInputs(element) {
				var result = true, container = getContainer(element),
					max = container.getAttribute(MAX);

				if (max) {
					result = (max > getFields(container).length);
				}
				return result;
			}

			/*
			 * Changes ALL ids inside the field
			 * @param field Any DOM element node
			 */
			function resetField(field) {
				var idWd = new Widget("", "", {id: null}),
					candidates = idWd.findDescendants(field),
					labelWd, buttonWd, buttonTextElement,
					nextLabel, nextButton, next, nextId, i;
				for (i = 0; i < candidates.length; i++) {
					next = candidates[i];
					next.elid = "";
					labelWd = new Widget("label", "", {"for": next.id});
					nextLabel = labelWd.findDescendant(field);
					buttonWd = BUTTON.extend("", {"aria-controls": null});
					nextButton = buttonWd.findDescendant(field);
					nextId = uid();
					if (nextLabel) {
						nextLabel.htmlFor = nextId;
					}
					if (nextButton) {
						nextButton.setAttribute("aria-controls", nextId);
						nextButton.title = REMOVE_BUTTON_TITLE;
						if ((buttonTextElement = BUTTON_MESSAGE_WD.findDescendant(nextButton))) {
							buttonTextElement.innerHTML = "";
							buttonTextElement.innerHTML = REMOVE_BUTTON_TITLE;
						}
					}
					next.id = nextId;
				}
			}

			/*
			 * Addressing two annoying issues here:
			 *
			 * 1. If the selected attribute is included in the original markup
			 * then it will be honored when you clone the select even if you have set
			 * selectedIndex to something else.  Obviously can resolve this with a 'removeAttribute'
			 * but that wouldn't help with issue 2...
			 *
			 * 2. Whenever you clone a select the selectedIndex is ignored - if no selected
			 * attribute is set then the selected index will be 0.
			 *
			 * Note that the selectedIndex is again reset in some browsers when you insert the
			 * select into the DOM so call this function AFTER insertion.
			 */
			function setSelectValues(newField, prototypeField) {
				var i, newSelects = SELECT_WD.findDescendants(newField),
					selects = SELECT_WD.findDescendants(prototypeField);
				for (i = 0; i < selects.length; i++) {
					newSelects[i].selectedIndex = selects[i].selectedIndex;
				}
			}

			/*
			 * Prototype field has passed on its data (values) to the new field,
			 * therefore we should clear the values from the prototype field.
			 * @param field
			 */
			function resetPrototypeField(field) {
				Array.prototype.forEach.call(INPUT_WD.findDescendants(field), processCandidateField);
				Array.prototype.forEach.call(SELECT_WD.findDescendants(field), processCandidateField);
				// Array.prototype.forEach.call(TEXTAREA_WD.findDescendants(field), processCandidateField);

				function processCandidateField($element) {
					if (SELECT_WD.isOneOfMe($element)) {
						$element.selectedIndex = 0;
					}
					else {
						$element.value = "";
					}
				}
			}

			/*
			 * @param element The "add button" that initiated this action
			 */
			function addNewField(element) {
				var container = getContainer(element),
					prototypeField = getFields(container, true), newField;
				if (prototypeField) {
					if (checkMaxInputs(element)) {
						newField = prototypeField.cloneNode(true);
						if (prototypeField.nextSibling) {
							prototypeField.parentNode.insertBefore(newField, prototypeField.nextSibling);
						}
						else {
							prototypeField.parentNode.appendChild(newField);
						}

						resetField(newField);
						setSelectValues(newField, prototypeField);
						resetPrototypeField(prototypeField);
						repaint(element);
					}
					else {
						prompt.alert(i18n.get("mfc_max"));
					}

				}
			}

			/*
			 * Removes a field (or all but the first field)
			 * @param element The "remove button" that initiated this action
			 * @param removeAll Boolean if true all additional fields are removed
			 */
			function removeField(element, removeAll) {
				var field,
					container,
					fields, i;
				if (removeAll) {
					container = getContainer(element);
					fields = getFields(container);
					i = fields.length;
					while (--i > 0) {
						removeField(fields[i]);
					}
				}
				else if ((field = FIELD.findAncestor(element))) {
					container = field.parentNode;
					container.removeChild(field);

					queue(function() {
						focus.focusFirstTabstop(container);
					});
				}
				repaint(container);
			}

			function queue(func) {
				if (queueTimer) {
					timers.clearTimeout(queueTimer);
				}
				queueTimer = timers.setTimeout(func, 100);
			}

			function repaint(element) {
				if (repainter) {
					repainter.checkRepaint(element);
				}
			}

			/**
			 * Register a multiFormControl on load.
			 *
			 * @function module:wc/ui/multiFormComponent.register
			 * @param {String[]} idArr An array of ids of WMultiDropdowns and WMultiTextFields.
			 */
			this.register = function(idArr) {
				if (idArr && idArr.length) {
					initialise.addCallback(function() {
						processNow(idArr);
					});
				}
			};

			/**
			 * Get the description of a multiFormControl container.
			 * @function module:wc/ui/multiFormComponent.getWidget
			 * @returns {wc/dom/Widget}
			 */
			this.getWidget = function() {
				return CONTAINER;
			};

			/**
			 * Get the description of an input witin a multiFormControl.
			 * @function module:wc/ui/multiFormComponent.getInputWidgets
			 * @returns {Array} The Widgets which describe the individual dropdowns (in a WMultiDropdown) or
			 *    text inputs (in a WMultiTextField).
			 */
			this.getInputWidget = function() {
				return CONTROLS;
			};

			/**
			 * initialisation: set up internationalised strings and event handlers.
			 * @function module:wc/ui/multiFormComponent.initialise
			 * @param {Element} element a DOM element: in practice BODY
			 */
			this.initialise = function(element) {
				REMOVE_BUTTON_TITLE = i18n.get("mfc_remove");
				event.add(element, event.TYPE.click, clickEvent);
			};
		}

		/**
		 * Provides client side multiple controls:
		 *
		 * * WMultiDropdown provides a control which has single SELECT elements which can be used to create a multiple selection
		 *   tool;
		 * * WMultiTextField provides a control which has single SELECT elements which can be used to create a set of single
		 *   line text input controls.
		 *
		 * @module
		 * @requires module:wc/has
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/uid
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/selectLoader
		 * @requires module:wc/timers
		 * @requires module:wc/ui/prompt
		 * @requires module:wc/ui/ajaxRegion
		 * @todo Document private members, fix source order.
		 */
		var instance = new MultiFormComponent();
		var repainter = null;

		initialise.register(instance);

		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}
		return instance;
	});
