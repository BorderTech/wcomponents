/**
 * @module ${validation.core.path.name}/minMax
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires module:wc/i18n/i18n
 * @requires external:lib/sprintf
 * @requires module:${validation.core.path.name}/validationManager
 *
 */
define(["wc/ui/getFirstLabelForElement",
		"wc/i18n/i18n",
		"lib/sprintf",
		"${validation.core.path.name}/validationManager"],
	/** @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param i18n wc/i18n/i18n @param sprintf lib/sprintf @param validationManager ${validation.core.path.name}/validationManager @ignore */
	function(getFirstLabelForElement, i18n, sprintf, validationManager) {
		"use strict";

		/**
		 * Provides all of the min and max selection constraint validation for multi-selectable controls.
		 *
		 * @todo Split this up to remove nested functions.
		 *
		 * @function
		 * @alias module:${validation.core.path.name}/minMax
		 * @param {module:${validation.core.path.name}/minMax~config} conf Contains the validator configuration options.
		 * @returns {boolean} true if the tested component meets its constraints (incuding if their are no constraints).
		 */
		function minMax(conf) {
			var MIN = "${wc.common.attrib.min}",
				MAX = "${wc.common.attrib.max}",
				container = conf.container,
				widget = conf.widget,
				selectedFunc = conf.selectedFunc,
				filter = conf.filter || _filter,
				selectionElementFunc = conf.selectFunc,
				flagFunc = conf.flag || _flag,
				position = conf.position,
				attachToFunc = conf.attachTo,
				minText = conf.minText || "${validation.core.i18n.selectableUnderMin}",
				maxText = conf.maxText || "${validation.core.i18n.selectableOverMax}",
				result = true,
				selectables;
			if (!(widget && container)) {
				return true;
			}

			/**
			 * Array filter function for typical selection constraint testing.
			 * @function
			 * @private
			 * @param selectable an instance of the selectable component being tested
			 * @returns {boolean} true if the selectable does not meet its constraints
			 */
			function _filter(selectable) {
				var isInvalid = false,
					min = selectable.getAttribute(MIN),
					max = selectable.getAttribute(MAX),
					count = 0, limit, flag, testElement = selectable, listLabel, selections;

				if (selectionElementFunc) {
					testElement = selectionElementFunc(selectable);
				}

				if ((min || max)) {
					selections = selectedFunc(testElement);
					if (selections) {
						count = selections.length;
					}
					if (count) {
						if (min && count < min) {
							isInvalid = true;
							limit = min;
							flag = i18n.get(minText);
						}
						else if (max && count > max) {
							isInvalid = true;
							limit = max;
							flag = i18n.get(maxText);
						}
					}
				}
				if (isInvalid) {
					if (testElement !== selectable) {
						listLabel = getFirstLabelForElement(testElement, true) || testElement.title;
					}
					flagFunc(selectable, flag, limit, listLabel);
				}
				return isInvalid;
			}

			/**
			 * Default error flagging function for min/max constraint validation.
			 * @function
			 * @private
			 * @param {Element} selectable the component being tested.
			 * @param {String} flag The error message frame (sprintf formatted).
			 * @param {int} limit The number of the constraint.
			 * @param {String} [secondaryLabel] The text content of an inner label to add context to complex error
			 *    messages. This is used for validation of WMultiSelectPair.
			 */
			function _flag(selectable, flag, limit, secondaryLabel) {
				var label = getFirstLabelForElement(selectable, true) || selectable.title || i18n.get("${validation.core.i18n.unlabelledQualifier}"),
					message = sprintf.sprintf(flag, label, limit, secondaryLabel),
					obj = {element: selectable, message: message};
				if (position) {
					obj["position"] = position;
				}
				if (attachToFunc) {
					obj["attachTo"] = attachToFunc(selectable);
				}
				validationManager.flagError(obj);
			}

			if (widget && container) {
				if (widget.isOneOfMe(container)) {
					selectables = [container].filter(filter);
				}
				else {
					selectables = Array.prototype.filter.call(widget.findDescendants(container), filter);
				}
				if (selectables && selectables.length) {
					result = false;
				}
			}
			return result;

		/**
		 * The configuration object for the module's return function.
		 * @typedef {Object} module:${validation.core.path.name}/minMax~config
		 * @property {Element} container That which is being validated. Usually a FORM element.
		 * @property {module:wc/dom/Widget} widget Description of the component being tested.
		 * @property {Function} selectedFunc Function to get the list of selections from the test element.
		 * @property {Function} [filter] Function used as an array filter to find and flag errors.
		 * @property {Function} [selectionElementFunc] Function to determine the element which holds the selection if
		 *    not the output of widget.findDescendants(container).
		 * @property {Function} [flagFunc] Function used to flag the error message if not this functions' inbuilt
		 *    flag function.
		 * @property {String} [minText] The i18n argument for errors where fewer than min options are selected.
		 * @property {String} [maxText] The i18n argument for errors where more than max options are selected.
		 * @property {Element} [attachTo] Element to which the error is attached if not the element being tested.
		 * @property {String} [position] Used as argument for insertAdjacentHTML, defaults to "afterEnd".
		 */
		}

		return minMax;
	});
