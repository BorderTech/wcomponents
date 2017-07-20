/**
 * Provides some extra functionality on single selectable SELECT elements (WDropdown).
 *
 * @module
 * @requires module:wc/dom/group
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/shed
 */
define(["wc/dom/group", "wc/dom/tag", "wc/dom/shed"],
	/** @param group wc/dom/group @param tag wc/dom/tag @param shed wc/dom/shed @ignore */
	function(group, tag, shed) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/dropdown~DropDown
		 * @private
		 */
		function DropDown() {
			/**
			 * Allow an external component to set selection of a dropdown using a value.
			 * @function module:wc/ui/dropdown.setSelectionByValue
			 * @public
			 * @param {HTMLElement} element The dropdown.
			 * @param {String} value the value of the option to select.
			 */
			this.setSelectionByValue = function(element, value) {
				var _group,
					option,
					i;
				if (element && element.tagName === tag.SELECT) {
					_group = group.get(element);
					for (i = 0; i < _group.length; ++i) {
						option = _group[i];
						if (option.value === value || option.text === value) {
							shed.select(option);
							break;
						}
					}
				}
			};
		}
		return /** @alias module:wc/ui/dropdown */ new DropDown();
	});
