/**
 * Module to provide a grouped set of check boxes with some group-like behaviour which is not inherent in HTML check
 * boxes, though whether this should be implemented or not is another matter since according to
 * {@link http://www.w3.org/TR/wai-aria-practices/#checkbox} strictly speaking checkbox should not get arrow key
 * navigation nor SHIFT+CLICK range toggle support!
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 *
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/clearSelection
 * @requires module:wc/dom/group
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/cbrShedPublisher
 */
define(["wc/dom/ariaAnalog",
		"wc/dom/Widget",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/clearSelection",
		"wc/dom/group",
		"wc/dom/getFilteredGroup",
		"wc/dom/cbrShedPublisher"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param Widget wc/dom/Widget @param initialise wc/dom/initialise @param shed wc/dom/shed @param clearSelection wc/dom/clearSelection @param group wc/dom/group @param getFilteredGroup wc/dom/getFilteredGroup @ignore */
	function(ariaAnalog, Widget, initialise, shed, clearSelection, group, getFilteredGroup) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/checkBoxSelect~CheckBoxSelect
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function CheckBoxSelect() {
			var inGroupMode;

			/**
			 * The description of a group item. This makes this class concrete.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 */
			this.ITEM = new Widget("input", "", {"type": "checkbox"});

			/**
			 * The description of a group container since WCheckBoxSelects are grouped by descent.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 */
			this.CONTAINER = new Widget("fieldset", "wc-checkboxselect");

			/**
			 * The description of a group item.
			 * @var
			 * @type {int}
			 * @protected
			 */
			this.exclusiveSelect = this.SELECT_MODE.MULTIPLE;

			/**
			 * Hold a record of the last activated item in any group with which we interact. Used for SHIFT + CLICK
			 * processing. The Object has properties keyed on the container id and value is the id of the last activated
			 * check box.
			 * @var
			 * @type {Object}
			 * @protected
			 */
			this.lastActivated = {};

			/**
			 * Extra setup in the initialisation phase needed to add an exception to SPACEBAR key event handling on
			 * check boxes, since that is what we are.
			 * @see {@link module:wc/dom/ariaAnalog}#actionable}
			 * @see {@link module:wc/dom/ariaAnalog}#keydownEvent}
			 * @function
			 * @protected
			 */
			this._extendedInitialisation = function(/* element */) {
				this.actionable.push(this.ITEM);
			};

			/**
			 * Activation action which occurs when a checkbox is selected/deselected.
			 * This includes group selection and revalidation of elements and containers.
			 * This over-ride is to remove the call to toggle the selection in aria-analog.
			 *
			 * @function
			 * @protected
			 * @param {Element} element The element being activated.
			 * @param {Boolean} [SHIFT] If defined event.shiftKey.
			 * @override
			 */
			this.activate = function(element, SHIFT) {
				var container = this.getGroupContainer(element), lastActivated;

				if (container && !inGroupMode) {
					if (SHIFT) {
						if ((lastActivated = document.getElementById(this.lastActivated[container.id]))) {
							this.doGroupSelect(element, lastActivated, container);
						}
					}
					this.setLastActivated(element);
				}
			};

			/**
			 * Allow an external component to set selection of all checkboxes in a CheckBoxSelect using an array of
			 * values for those to be selected.
			 * @function
			 * @public
			 * @param {HTMLElement} element The checkBoxSelect.
			 * @param {String[]} selectedValArr An array of value(s) of the checkbox(es) to select.
			 */
			this.setSelectionByValue = function(element, selectedValArr) {
				var lastOption, _group, silent = true;
				if (this.CONTAINER.isOneOfMe(element) && !(shed.isHidden(element) || shed.isDisabled(element)) && (_group = group.get(element)) && _group.length) {
					_group.forEach(function(option, idx) {
						if (silent && idx === _group.length - 1) {
							silent = false;
						}
						if (selectedValArr.indexOf(option.value) > -1) {
							shed.select(option, silent);
						}
						else {
							shed.deselect(option, silent);
						}
						lastOption = option;
					});
					if (lastOption) {
						this.setLastActivated(lastOption);
					}
				}
			};

			/**
			 * We want to [de]select options between two end points. The select/deselect is determined by the checked
			 * state of element. element is the element being activated, lastActivated is the element LAST activated.
			 * @function
			 * @protected
			 * @param {Element} element The element currently being de/selected.
			 * @param {Element} [lastActivated] The last element in teh group which was activated.
			 * @param {Element} container The element which holds the check boxes.
			 * @override
			 */
			this.doGroupSelect = function(element, lastActivated, container) {
				var selectedFilter, filtered, unfiltered, start, end, _group, isSelected, i, next, func;

				try {
					inGroupMode = true;

					if (element && lastActivated && container && !shed.isDisabled(element) && !(shed.isHidden(container)) || shed.isDisabled(container)) {
						isSelected = shed.isSelected(element);
						selectedFilter = isSelected ? getFilteredGroup.FILTERS.deselected : getFilteredGroup.FILTERS.selected;

						_group = getFilteredGroup(element, {filter: (selectedFilter), asObject: true});
						filtered = _group.filtered;
						unfiltered = _group.unfiltered;

						if (filtered && filtered.length) {
							start = Math.min(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));

							while (unfiltered[start] && shed.isSelected(unfiltered[start]) === isSelected) {
								start++;
							}
							if (start < unfiltered.length) {
								end = Math.max(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));

								while (end >= 0 && shed.isSelected(unfiltered[end]) === isSelected) {
									end--;
								}

								if (end >= 0) {
									start = filtered.indexOf(unfiltered[start]);
									if (start > -1) {
										end = filtered.indexOf(unfiltered[end]);
										func = isSelected ? "select" : "deselect";

										for (i = start; i <= end; ++i) {
											next = filtered[i];
											shed[func](next, i !== end);
										}
									}
								}
							}
						}
					}
					clearSelection();
				}
				finally {
					inGroupMode = false;
				}
			};

			/**
			 * We do not need the inherited focus event.
			 * @function
			 * @protected
			 * @override
			 */
			this.focusEvent = null;  // do not reset tabIndex in grouped checkboxes
		}

		CheckBoxSelect.prototype = ariaAnalog;
		var /** @alias module:wc/ui/checkBoxSelect */ instance = new CheckBoxSelect();
		instance.constructor = CheckBoxSelect;
		initialise.register(instance);
		return instance;
	});
