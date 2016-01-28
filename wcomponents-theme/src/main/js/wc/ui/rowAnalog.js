/**
 * Provides ARIA based row in treegrid functionality (lists of selectable options - cf a select element).
 * @module
 * @extends module:wc/dom/ariaAnalog
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/ariaAnalog", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/selectToggle"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @ignore */
	function(ariaAnalog, initialise, Widget) {
		"use strict";

		/*
		 * Unused dependencies
		 * The table row selection Select All/None control(s) and sub-row toggle controls both require selectToggle
		 * but row itself does not use it. This saves us another expensive lookup in XSLT for a quite small module.
		 * @returns {rowAnalog_L11.RowAnalog}
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/RowAnalog~RowAnalog
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function RowAnalog() {
			/**
			 * Select items immediately on navigation.
			 * @var
			 * @protected
			 * @type Boolean
			 * @override
			 */
			this.selectOnNavigate = false;
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
			 * The defnition of a grouped item in a listbox analog. This is the equivalent of an option element.
			 * @var
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.ITEM = new Widget("", "", {"role": "row", "aria-selected": null});

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
			this.CONTAINER = new Widget("table");
		}

		RowAnalog.prototype = ariaAnalog;
		var /** @alias module:wc/ui/RowAnalog */ instance = new RowAnalog();
		instance.constructor = RowAnalog;
		initialise.register(instance);
		return instance;
	});
