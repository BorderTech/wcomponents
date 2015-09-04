/**
 * Provides table sort controls. A column is sorted by an algorithm controlled by the server application. There is no
 * client side sorting.
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 *
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/onloadFocusControl
 */
define(["wc/dom/ariaAnalog",
		"wc/dom/initialise",
		"wc/dom/Widget",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/onloadFocusControl"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param ajaxRegion wc/ui/ajaxRegion @param processResponse wc/ui/ajax/processResponse @param onloadFocusControl wc/ui/onloadFocusControl @ignore */
	function(ariaAnalog, initialise, Widget, ajaxRegion, processResponse, onloadFocusControl) {
		"use strict";
		Sort.prototype = ariaAnalog;

		/**
		 * @constructor
		 * @alias module:wc/ui/table/sort~Sort
		 * @private
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 */
		function Sort() {
			/**
			 * The {@link module:wc/dom/Widget} description of the container of WTable.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @private
			 */
			var TABLE_WRAPPER = new Widget("div", "table");

			/**
			 * The {@link module:wc/dom/Widget} description of the sort button.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ITEM = new Widget("button", "wc_table_sort_ctrl");

			/**
			 * The {@link module:wc/dom/Widget} description of sort controls grouping element.
			 * @constant
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.CONTAINER = new Widget("tr");

			/**
			 * The selection mode for the group of sort buttons: always single select. NOTE: this is a member rather
			 *    than a constant because in some extensions of {@link module:wc/dom/ariaAnalog} it changes according to
			 *    context.
			 * @var
			 * @type {int}
			 * @default 1
			 * @public
			 * @override
			 */
			this.exclusiveSelect = this.SELECT_MODE.SINGLE;

			/**
			 * Actions the selection of a sort button.
			 *
			 * @function
			 * @public
			 * @param {element} element The sort control being activated.
			 */
			this.activate = function(element) {
				var alias;
				this.constructor.prototype.activate.call(this, element);
				if ((alias = element.getAttribute("data-wc-ajaxalias"))) {
					ajaxRegion.register({
						id: element.id,
						loads: [alias],
						alias: alias,
						oneShot: true,
						formRegion: alias
					});
				}
			};

			/**
			 * Provides a post insertion subscriber to {@link module:wc/ui/ajax/prOcessResponse} which will
			 * attempt to refocus the replacement sort control when one of the unsorted sort controls is the ajax
			 * trigger. This is necessary because the unsorted descending control has, by necessity, a different ID from
			 * other sorting controls.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being replaced.
			 * @param {String} action The ajax action, not required for this function.
			 * @param {String} triggerId The id of the original AJAX trigger.
			 */
			function ajaxSubscriber(element, action, triggerId) {
				var EXTENSION = "${wc.ui.table.id.sort.suffix.extension}",
					SORT_ID_SUFFIX = "${wc.ui.table.id.sort.suffix}",
					diminishedId;
				// Does the triggerId contain both the SORT_ID_SUFFIX and end with the EXTENSION? Use > 0 not > -1 because the id should never start with the suffix!

				if (element && triggerId && triggerId.indexOf(SORT_ID_SUFFIX) > 0 && TABLE_WRAPPER.isOneOfMe(element)) {
					if (document.getElementById(triggerId)) {
						onloadFocusControl.requestFocus(triggerId);
					}
					else if (triggerId.indexOf(EXTENSION) === triggerId.length - EXTENSION.length) {
						diminishedId = triggerId.substr(0, triggerId.length - EXTENSION.length);
						if (document.getElementById(diminishedId)) {
							onloadFocusControl.requestFocus(diminishedId);
						}
					}
				}
			}

			/**
			 * Extend initialisation to add a post-insertion ajax subscriber to refocus the sort controls. This is
			 * required since the sort control ID changes.
			 *
			 * @function
			 * @protected
			 * @override
			 */
			this._extendedInitialisation = function() {
				processResponse.subscribe(ajaxSubscriber, true);
			};
		}

		var /** @alias module:wc/ui/table/sort */ instance = new Sort();
		instance.constructor = Sort;
		initialise.register(instance);
		return instance;
	});
