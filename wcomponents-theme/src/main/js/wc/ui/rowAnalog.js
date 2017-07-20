
define(["wc/dom/ariaAnalog",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/ui/table/common",
	"wc/ui/icon"],
	function(ariaAnalog, initialise, shed, Widget, table, icon) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/RowAnalog~RowAnalog
		 * @private
		 */
		function RowAnalog() {
			var EXPANDER;
			/**
			 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
			 * @var
			 * @protected
			 * @type int
			 * @override
			 */
			this.exclusiveSelect = this.SELECT_MODE.MIXED;

			/**
			 * @override
			 */
			this.simpleSelection = true;

			/**
			 * The definition of a grouped item in a row analog. This is the equivalent of an option element.
			 * @var
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.ITEM = table.TR.extend("", {"aria-selected": null});

			/**
			 * Holds a reference to the last activated member of any identified listBox analog keyed on the listbox
			 * id with the property value being the id of the last activated item. Needed for correct implementation of
			 * group selection (such as with SHIFT+ Click).
			 * @var
			 * @type {Object}
			 * @protected
			 * @override
			 */
			this.lastActivated = {};

			/**
			 * According to the WAI-ARIA spec listbox MUST contain option and option must be contained by listbox.
			 * However the rdf is broken on this point with regard to looking up what 'option' is contained by. So this
			 * is a fill for that bug.
			 *
			 * @constant
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.CONTAINER = table.TABLE;

			/**
			 * Change icon on select/deselect.
			 * @function
			 * @public
			 * @override
			 * @param {Element} element the element being acted upon
			 * @param {String} action the shed action
			 */
			this.shedObserver = function(element, action) {
				var cell, isMultiSelect, add, remove;
				if (element && (action === shed.actions.SELECT || action === shed.actions.DESELECT) && this.ITEM.isOneOfMe(element)) {
					EXPANDER = EXPANDER || new Widget("td", "wc_table_sel_wrapper");
					if ((cell = EXPANDER.findDescendant(element, true))) {
						isMultiSelect = this.isMultiSelect(element);
						if (action === shed.actions.SELECT) {
							add = isMultiSelect ? "fa-check-square-o" : "fa-dot-circle-o";
							remove = isMultiSelect ? "fa-square-o" : "fa-circle-o";
						} else {
							add = isMultiSelect ? "fa-square-o" : "fa-circle-o";
							remove = isMultiSelect ? "fa-check-square-o" : "fa-dot-circle-o";
						}
						icon.change(cell, add, remove);
					}
				}

				this.constructor.prototype.shedObserver.call(this, element, action);
			};
		}

		RowAnalog.prototype = ariaAnalog;
		/**
		 * Provides ARIA based row in treegrid functionality (lists of selectable options - cf a select element).
		 * @module module:wc/ui/RowAnalog
		 * @extends module:wc/dom/ariaAnalog
		 * @requires module:wc/dom/ariaAnalog
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/ui/table/common
		 */
		var instance = new RowAnalog();
		instance.constructor = RowAnalog;
		initialise.register(instance);
		return instance;
	});
