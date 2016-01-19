/**
 * Weird client side row filtering. This is historical to support something which was a bad implementation of a bad idea.
 * You should not use this component.
 * *
 * NOTE: whilst this is compatible with (client side) paginated tables they should be mutually exclusive as rows off
 * screen will be hidden and pagination isn't dynamically updated to show the requested number of filtered rows
 *
 * NOTE 2: applying or removing a filter from a table will show all rows in the table no matter how they were hidden
 * (filter clearing overrides hidden on rows).
 *
 * The XSLT to create WFilterControl artefacts and to apply row filters to WDataTable are in place but commented out.
 * This file will never be included unless you hunt down all of the following in the XSLT and uncomment them:
 *
 * * all uses of `ui:filterControl`;
 * * all uses of `data-wc-filter` and `data-wc-filters`;
 * * The parts of wc.ui.table.tr.xsl which are commented out with the remark "TODO: remove this when WFilterControl is
 *   no longer part of the Java API";
 * * both templates in wc.ui.table.tr.n.containsWords.xsl.
 *
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/timers
 *
 * @deprecated No longer supported: to be removed.
 *
 * @todo Delete me!
 * @ignore
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/dom/formUpdateManager", "wc/timers"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @param Widget wc/dom/Widget @param formUpdateManager wc/dom/formUpdateManager @param timers wc/timers @ignore */
	function(event, initialise, shed, Widget, formUpdateManager, timers) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/filterControl~FilterControl
		 * @private
		 */
		function FilterControl() {
			var STATE_NAME_SUFFIX	=	".filters",
				ROWS = new Widget("TR"),
				BODY = new Widget("TBODY"),
				FILTERS = new Widget("button", "filterControl"),
				TABLE_WD = new Widget("div", "table"),
				FILTERED_TABLE = TABLE_WD.extend("", {"${wc.ui.table.rowFilter.attribute.tableFilter}": null});

			ROWS.descendFrom(BODY);

			/**
			 * Write state of table row filters and any filters which apply to a table.
			 *
			 * @function
			 * @param {Element} fromContainer container holding rows
			 * @param {Element} toContainer container holding state inputs
			 */
			function writeState(fromContainer, toContainer) {
				var allFilters = FILTERS.findDescendants(fromContainer),
					tables = FILTERED_TABLE.findDescendants(fromContainer),
					filterVal, next, i, len;

				for (i = 0, len = allFilters.length; i < len; ++i) {
					next = allFilters[i];
					filterVal = shed.isSelected(next);
					formUpdateManager.writeStateField(toContainer, next.id, filterVal, true);
				}

				for (i = 0, len = tables.length; i < len; ++i) {
					next = tables[i];
					if ((filterVal = next.getAttribute("${wc.ui.table.rowFilter.attribute.tableFilter}"))) {
						formUpdateManager.writeStateField(toContainer, next.id + STATE_NAME_SUFFIX, filterVal, true);
					}
				}
			}

			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = FILTERS.findAncestor($event.target)) && !shed.isSelected(element)) {
					shed.select(element);
				}
			}

			function shedSubscriber(element) {
				var controllerWd, controllers, targetId, value;
				if (FILTERS.isOneOfMe(element)) {
					targetId = element.getAttribute("aria-controls");
					value = element.getAttribute("data-wc-filter");
					controllerWd = FILTERS.extend("", {"aria-controls": targetId});
					controllers = controllerWd.findDescendants(document.body);

					Array.prototype.forEach.call(controllers, function (_el) {
						if (_el !== element) {
							shed.deselect(element);
						}
					});
					filterTable(targetId, value);
				}
			}


			function filterTable(targetId, value) {
				var target;
				if (targetId) {
					target = document.getElementById(targetId);
				}
				if (target) {
					if (TABLE_WD.isOneOfMe(target)) {
						changeFilter(target, value);
					}
					else {
						Array.prototype.forEach.call(TABLE_WD.findDescendants(target), function(next) {
							changeFilter(next, value);
						});
					}
				}
			}

			function changeFilter(table, filterValue) {
				var rows, i, row, changed, rowFilterValues;
				// clear old filters
				function _clearFilter(next) {
					if (shed.isHidden(next)) {
						shed.show(next);
					}
				}
				table.removeAttribute("${wc.ui.table.rowFilter.attribute.tableFilter}");
				Array.prototype.forEach.call(ROWS.findDescendants(table), _clearFilter);
				if (filterValue) {
					table.setAttribute("${wc.ui.table.rowFilter.attribute.tableFilter}", filterValue);
					rows = ROWS.findDescendants(table);
					for (i = 0; i < rows.length; i++) {
						changed = false;
						row = rows[i];
						rowFilterValues = row.getAttribute("data-wc-filters");
						if (rowFilterValues) {
							rowFilterValues = rowFilterValues.split(" ");
						}

						if (!rowFilterValues || rowFilterValues.indexOf(filterValue) === -1) {
							shed.hide(row);
							changed = true;
						}
						if (shed.isSelected(row) && changed) {
							timers.setTimeout(event.fire, 0, row, event.TYPE.click);
						}
					}
				}
			}

			/**
			 * Initialise filter controls by adding the click event listener and form update manager subsciption.
			 * @function module:wc/ui/filterControl.initialise
			 * @public
			 * @param {Element} element document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
				formUpdateManager.subscribe(writeState);
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
			};

		}

		var /** @alias module:wc/ui/filterControl */ instance = new FilterControl();
		initialise.register(instance);
		return instance;
	});
