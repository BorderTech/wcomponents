define(["wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/focus",
	"wc/dom/shed",
	"wc/dom/uid",
	"wc/i18n/i18n",
	"wc/ui/selectLoader",
	"wc/timers",
	"wc/ui/prompt",
	"wc/ui/ajaxRegion",
	"wc/ui/fieldset",
	"wc/ui/icon"],
function(event, initialise, focus, shed, uid, i18n, selectLoader, timers, prompt, ajaxRegion, fieldset, icon) {
	"use strict";
	let instance;

	/**
	 * @constructor
	 * @alias module:wc/ui/multiFormComponent~MultiFormComponent
	 * @private
	 */
	function MultiFormComponent() {
		const containerSelector = "fieldset.wc_mfc";
		const fieldSelector = `${containerSelector} li`;
		const buttonSelector = `${fieldSelector} button`;
		const selectSelector = `${fieldSelector} select`;
		const inputSelector = `${fieldSelector} input`;
		const controlsSelectors = [selectSelector, inputSelector];
		const BUTTON_TYPE = {add: 0, remove: 1};
		const MAX = "data-wc-max";
		let queueTimer, REMOVE_BUTTON_TITLE;


		/**
		 * Load data list for cachable WMultiDropdown.
		 *
		 * @see {@link module:wc/ui/selectLoader.load}
		 * @function
		 * @private
		 * @param {String} id The id of a multiDropdown.
		 */
		function load(id) {
			const element = document.getElementById(id);
			if (element) {
				const selects = element.querySelectorAll(selectSelector);
				Array.prototype.forEach.call(selects, function(next) {
					const nextId = next.id;
					selectLoader.load(nextId);
				});
			}
		}

		function processNow(idArr) {
			let id;
			while ((id = idArr.shift())) {
				load(id);
			}
		}

		/**
		 * on click:
		 * 1. Am I a button?
		 * 2. Am I a button that belongs to multiFormComponent?
		 * 3. Am I an add button?
		 * 	- Yes: add a new field
		 *  - No: remove field
		 *  @param {MouseEvent} $event
		 */
		function clickEvent($event) {
			let element;
			if (!$event.defaultPrevented && (element = $event.target.closest(buttonSelector)) && !shed.isDisabled(element)) {
				doClick(element, $event.shiftKey || event.shiftKey);  // event.shiftKey - see wc/fixes/shiftKey_ff
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
			} else if (type === BUTTON_TYPE.remove) {
				removeField(button, SHIFT);
				if (button.type === "submit") {
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
		 * @param {HTMLElement} element An add or remove button.
		 * @returns {int} either BUTTON_TYPE.add (0) or BUTTON_TYPE.remove (1).
		 */
		this.getButtonType = function (element) {
			const container = getContainer(element);
			let result = -1;
			if (container) {
				result = (element === container.querySelector(buttonSelector)) ? BUTTON_TYPE.add : BUTTON_TYPE.remove;
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
		 * @returns {Element} The container if element is a multi form control or one of its descendent elements.
		 */
		function getContainer(element) {
			return element.closest(containerSelector);
		}

		/**
		 * Get the field(s) containing each of the interactive controls in a multiFormControl.
		 *
		 * @param {Element} container A multiFormControl.
		 * @param {Boolean} [firstOnly] If true only the first field will be returned.
		 * @returns {(NodeList|Element)} A collection of fields OR a single fielt if firstOnly is true.
		 */
		function getFields(container, firstOnly) {
			const method = firstOnly ? "querySelector" : "querySelectorAll";
			return container[method](fieldSelector);
		}

		/*
		 * @param element An add/remove button
		 * @returns true if max has not been reached yet
		 */
		function checkMaxInputs(element) {
			let result = true;
			const container = getContainer(element),
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
			const idSelector = "[id]",
				candidates = field.querySelectorAll(idSelector);
			for (let i = 0; i < candidates.length; i++) {
				let next = candidates[i];
				next.elid = "";
				let labelSelector = `label[for='${next.id}']`;
				let nextLabel = field.querySelector(labelSelector);
				let nextButtonSelector = `${buttonSelector}[aria-controls]`;
				let nextButton = field.querySelector(nextButtonSelector);
				let nextId = uid();
				if (nextLabel) {
					nextLabel.htmlFor = nextId;
				}
				if (nextButton) {
					nextButton.setAttribute("aria-controls", nextId);
					nextButton.title = REMOVE_BUTTON_TITLE;
					icon.change(nextButton,"fa-minus-square", "fa-plus-square");
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
			const newSelects = newField.querySelectorAll(selectSelector),
				selects = prototypeField.querySelectorAll(selectSelector);
			for (let i = 0; i < selects.length; i++) {
				newSelects[i].selectedIndex = selects[i].selectedIndex;
			}
		}

		/*
		 * Prototype field has passed on its data (values) to the new field,
		 * therefore we should clear the values from the prototype field.
		 * @param field
		 */
		function resetPrototypeField(field) {
			Array.prototype.forEach.call(field.querySelectorAll(inputSelector), processCandidateField);
			Array.prototype.forEach.call(field.querySelectorAll(selectSelector), processCandidateField);
			// Array.prototype.forEach.call(TEXTAREA_WD.findDescendants(field), processCandidateField);

			function processCandidateField($element) {
				if ($element.matches(selectSelector)) {
					$element.selectedIndex = 0;
				} else {
					$element.value = "";
				}
			}
		}

		/*
		 * @param element The "add button" that initiated this action
		 */
		function addNewField(element) {
			const container = getContainer(element),
				prototypeField = getFields(container, true);
			if (prototypeField) {
				if (checkMaxInputs(element)) {
					const newField = prototypeField.cloneNode(true);
					if (prototypeField.nextSibling) {
						prototypeField.parentNode.insertBefore(newField, prototypeField.nextSibling);
					} else {
						prototypeField.parentNode.appendChild(newField);
					}

					resetField(newField);
					setSelectValues(newField, prototypeField);
					resetPrototypeField(prototypeField);
				} else {
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
			let field,
				container;
			if (removeAll) {
				container = getContainer(element);
				let fields = getFields(container);
				let i = fields.length;
				while (--i > 0) {
					removeField(fields[i]);
				}
			} else if ((field = element.closest(fieldSelector))) {
				container = field.parentNode;
				container.removeChild(field);

				queue(() => focus.focusFirstTabstop(container));
			}
		}

		function queue(func) {
			if (queueTimer) {
				timers.clearTimeout(queueTimer);
			}
			queueTimer = timers.setTimeout(func, 100);
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
		 * @returns {string}
		 */
		this.getSelector = function() {
			return containerSelector;
		};

		/**
		 * Get the description of an input witin a multiFormControl.
		 * @function module:wc/ui/multiFormComponent.getInputWidgets
		 * @returns {string} The selector which describes the individual dropdowns (in a WMultiDropdown) or
		 *    text inputs (in a WMultiTextField).
		 */
		this.getInputSelector = function() {
			return controlsSelectors.join();
		};

		/**
		 * initialisation: set up internationalised strings and event handlers.
		 * @function module:wc/ui/multiFormComponent.initialise
		 * @param {Element} element a DOM element: in practice BODY
		 */
		this.initialise = function(element) {
			event.add(element, "click", clickEvent);
			return i18n.translate("mfc_remove").then(function(s) {
				REMOVE_BUTTON_TITLE = s;
			});
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
	 * @requires module:wc/dom/event
	 * @requires module:wc/dom/initialise
	 * @requires module:wc/dom/focus
	 * @requires module:wc/dom/shed
	 * @requires module:wc/dom/uid
	 * @requires module:wc/i18n/i18n
	 * @requires module:wc/ui/selectLoader
	 * @requires module:wc/timers
	 * @requires module:wc/ui/prompt
	 * @requires module:wc/ui/ajaxRegion
	 * @requires module:wc/ui/fieldset
	 * @requires module:wc/ui/icon
	 * @todo Document private members, fix source order.
	 */
	instance = new MultiFormComponent();

	initialise.register(instance);

	return instance;
});
