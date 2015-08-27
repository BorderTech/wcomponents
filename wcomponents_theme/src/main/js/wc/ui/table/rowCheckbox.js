/**
 * Provides functionality for multi-selectable rows in WTable. This is an extension of
 * {@link module:wc/ui/checkboxAnalog} which is only used to add a grouping container.
 *
 * @module
 * @extends module:wc/ui/checkboxAnalog
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/checkboxAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/radioAnalog
 */
define(["wc/dom/Widget", "wc/ui/checkboxAnalog", "wc/dom/initialise", "wc/ui/radioAnalog"],
	/** @param Widget @param checkboxAnalog @param initialise @ignore */
	function(Widget, checkboxAnalog, initialise) {
		"use strict";
		/*
		 * IMPLICIT DEPENDENCIES:
		 * wc/ui/radioAnalog is used for the tables select All/None controls when the rowSelection type is TEXT. It is
		 * wayyyyyy cheaper to include it here than do a descendant search in XSLT even when it is not used because the
		 * rowSelection Type is control.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/table/rowCheckbox~RowCheckbox
		 * @private
		 * @extends module:wc/dom/checkboxAnalog~CheckboxAnalog
		 */
		function RowCheckbox() {
			/**
			 * The description of a group item. This makes this class concrete.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ITEM = new Widget("tr", "", {role: "checkbox"});

			/**
			 * The description of the group container item.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.CONTAINER = new Widget("tbody");

			/**
			 * We have to have an empty write state to prevent the state being written twice. as checkboxAnalog will
			 * write the state of selected multi-selectable table rows.
			 * NOTE: formUpdateManager will complain if writeState is anything other than a function.
			 * @function
			 * @ignore
			 */
			this.writeState = function() {};

			/**
			 * We have to have an empty clickEvent to prevent as checkboxAnalog will handle clicks.
			 * @todo This may be OK as null rather than as a function.
			 * @function
			 * @ignore
			 */
			this.clickEvent = function() {};
		}

		RowCheckbox.prototype = checkboxAnalog;

		var /** @alias module:wc/ui/table/rowCheckbox */ instance = new RowCheckbox();
		instance.constructor = RowCheckbox;
		initialise.register(instance);
		return instance;
	});
