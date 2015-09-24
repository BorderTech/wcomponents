/**
 * Provides functionality for multi-selectable rows in WTable. This is an extension of
 * {@link module:wc/ui/checkboxAnalog} which is only used to add a grouping container.
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 *
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/radioAnalog
 */
define(["wc/dom/Widget",
		"wc/dom/ariaAnalog",
		"wc/dom/initialise",
		"wc/ui/radioAnalog"],
	/** @param Widget @param ariaAnalog @param initialise @ignore */
	function(Widget, ariaAnalog, initialise) {
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
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
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
			 * Enables group selection by storing the start point.
			 * @var
			 * @public
			 * @type {Object}
			 * @override
			 */
			this.lastActivated = {};

			/**
			 * We have to have an empty write state to prevent the state being written twice. as checkboxAnalog will
			 * write the state of selected multi-selectable table rows.
			 * NOTE: formUpdateManager will complain if writeState is anything other than a function.
			 * @function
			 * @ignore
			 */
			this.writeState = function() {};
		}

		RowCheckbox.prototype = ariaAnalog;

		var /** @alias module:wc/ui/table/rowCheckbox */ instance = new RowCheckbox();
		instance.constructor = RowCheckbox;
		initialise.register(instance);
		return instance;
	});
