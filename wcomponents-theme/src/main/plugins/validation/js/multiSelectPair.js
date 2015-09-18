/**
 * Provides functionality to undertake client validation of WMultiSelectPair.
 *
 * @module ${validation.core.path.name}/multiSelectPair
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/multiSelectPair
 * @requires module:${validation.core.path.name}/isComplete
 * @requires module:${validation.core.path.name}/minMax
 * @requires module:${validation.core.path.name}/required
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires module:wc/i18n/i18n
 */
define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/ui/multiSelectPair",
		"${validation.core.path.name}/isComplete",
		"${validation.core.path.name}/minMax",
		"${validation.core.path.name}/required",
		"${validation.core.path.name}/validationManager",
		"wc/ui/getFirstLabelForElement",
		"wc/i18n/i18n"],
	/** @param attribute wc/dom/attribute @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @param multiSelectPair wc/ui/multiSelectPair @param isComplete ${validation.core.path.name}/isComplete @param minMax ${validation.core.path.name}/minMax @param required ${validation.core.path.name}/required @param validationManager ${validation.core.path.name}/validationManager @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param i18n wc/i18n/i18n @ignore */
	function(attribute, event, initialise, shed, multiSelectPair, isComplete, minMax, required, validationManager, getFirstLabelForElement, i18n) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/multiSelectPair~ValidationMultiSelectPair
		 * @private
		 */
		function ValidationMultiSelectPair() {
			var CONTAINER = multiSelectPair.getWidget(),
				SELECT = multiSelectPair.getInputWidget(),
				CONTAINER_INITIALISED_KEY = "validation.multiSelectPair.inited";

			/**
			 * Gets the multiSelectPair wrapper if the passed in element is a WMultiSelectPair wrapper or any element
			 * inside one.
			 * @function
			 * @private
			 * @param {Element} element Any DOM element.
			 * @returns {Element} A WMultiSelectPair wrapper element.
			 */
			function getContainer(element) {
				return CONTAINER.findAncestor(element);
			}

			/**
			 * Re-validate WMultiSelectPair when the selection changes.
			 * @function
			 * @private
			 * @param {Element} container a WMultiSelectPair component.
			 */
			function revalidate(container) {
				validationManager.revalidationHelper(container, validate);
			}

			/**
			 * Listen for click events and revalidate.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event A Wrapped click or dblclick event.
			 */
			function clickEvent($event) {
				var element = $event.target, container;
				if (!$event.defaultPrevented && !shed.isDisabled(element) && (container = getContainer(element))) {
					revalidate(container);
				}
			}


			/**
			 * Gets the "selected items" list from a WMultiSelectPair.
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair.
			 * @returns {Element} The select element which holds the selected options.
			 */
			function getSelectionList(element) {
				return multiSelectPair.getListByType(element, multiSelectPair.LIST_TYPE_CHOSEN);
			}


			/**
			 * Listen for keydown events which can cause a change in the selection of the component and revalidate.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped keydown event.
			 */
			function keydownEvent($event) {
				var selectList, keyCode = $event.keyCode, selectType, container;
				// this is cheaper than any other test in this function
				if ($event.defaultPrevented || !(keyCode === KeyEvent.DOM_VK_RETURN || keyCode === KeyEvent.DOM_VK_RIGHT || keyCode === KeyEvent.DOM_VK_LEFT)) {
					return;
				}
				if ((selectList = SELECT.findAncestor($event.target)) && (container = getContainer(selectList))) {
					selectType = multiSelectPair.getListType(selectList);
					if ((selectType || selectType === 0) && (keyCode === KeyEvent.DOM_VK_RETURN || (keyCode === KeyEvent.DOM_VK_RIGHT && selectType === multiSelectPair.LIST_TYPE_AVAILABLE) || (keyCode === KeyEvent.DOM_VK_LEFT && selectType === multiSelectPair.LIST_TYPE_CHOSEN))) {
						revalidate(container);
					}
				}
			}


			/**
			 * Wire up some event listeners on first focus.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped focus/focusin event.
			 */
			function focusEvent($event) {
				var container;
				if (!$event.defaultPrevented && (container = getContainer($event.target)) && !attribute.get(container, CONTAINER_INITIALISED_KEY)) {
					attribute.set(container, CONTAINER_INITIALISED_KEY, true);
					event.add(container, event.TYPE.click, clickEvent, 1);
					event.add(container, event.TYPE.dblclick, clickEvent, 1);
					event.add(container, event.TYPE.keydown, keydownEvent, 1);
				}
			}

			/**
			 * An array filter function to determine if a particular component is "complete". Passed in to
			 * {@link module:${validation.core.path.name}/isComplete} as part of
			 * {@link module:${validation.core.path.name}/multiSelectPair~_isComplete}.
			 * @function
			 * @private
			 * @param {Element} next A WMultiSelectPair.
			 * @returns {boolean} true if the selected items list in the component has one or more options.
			 */
			function amIComplete(next) {
				var _result = false, list;
				if ((list = getSelectionList(next))) {
					_result = !!list.options.length;
				}
				return _result;
			}

			/**
			 * Test if a container is complete if it is, or contains, WMultiSelectPairs.
			 * @function
			 * @private
			 * @param {Element} container A container element, mey be a WMultiSelectPair wrapper.
			 * @returns {boolean} true if the container is complete.
			 */
			function _isComplete(container) {
				return isComplete.isCompleteHelper(container, CONTAINER, amIComplete);
			}

			/**
			 * A custom message function to apply a validation error to an incomplete mandatory WMultiSelectPair.
			 * @function
			 * @private
			 * @param {Element} element A WMultiSelectPair.
			 * @returns {String} a formatted error message used by the validation flag function.
			 */
			function _requiredMessageFunc(element) {
				var label = getFirstLabelForElement(element, true) || element.title,
					list = getSelectionList(element),
					listLabel = getFirstLabelForElement(list, true) || list.title;
				return i18n.get("${validation.multiSelectPair.i18n.required}", label, listLabel);
			}

			/**
			 * A custom validation filter function for WMultiSelectPair. This is an array filter so we want to keep
			 * components which are not complete.
			 * @function
			 * @private
			 * @param {Element} element a WMultiSelectPair.
			 * @returns {boolean} true if the component is not complete.
			 */
			function _filter(element) {
				return !amIComplete(element);
			}

			/**
			 * Validate WMultiSelectPair: A WMultiSelectPair which is required must have at least one option in the
			 * "selected" list. Constraint validation exists for minimum number of options and maximum number of options.
			 * @function
			 * @private
			 * @param {Element} container A WMultiSelectPair or a container which may contain WMultiSelectPairs. Often a form.
			 * @returns {boolean} true if the container is valid.
			 */
			function validate(container) {
				var obj = {container: container,
							widget: CONTAINER,
							constraint: required.CONSTRAINTS.CLASSNAME,
							filter: _filter,
							position: "beforeEnd",
							messageFunc: _requiredMessageFunc
						},
					_required = required.complexValidationHelper(obj),
					result = true;

				// reset obj for minMax checking
				obj.constraint = obj.filter = obj.messageFunc = null;
				obj.selectFunc = getSelectionList;
				obj.minText = "${validation.multiSelectPair.i18n.underMin}";
				obj.maxText = "${validation.multiSelectPair.i18n.overMax}";

				obj.selectedFunc = function(el) {
					return el.options || [];
				};

				result = minMax(obj);
				return result && _required;
			}


			/**
			 * Set up initial event listeners.
			 * @function module:${validation.core.path.name}/multiSelectPair.initialise
			 * @param {Element} element the element being intialised: usually document.body
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Late set up to wire up subscribers after initialisation.
			 * @function module:${validation.core.path.name}/multiSelectPair.postInit
			 */
			this.postInit = function () {
				validationManager.subscribe(validate);
				isComplete.subscribe(_isComplete);
			};
		}

		var /** @alias module:${validation.core.path.name}/multiSelectPair */ instance = new ValidationMultiSelectPair();
		initialise.register(instance);
		return instance;
	});
