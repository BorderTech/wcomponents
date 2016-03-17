/**
 * Implements eager loading of exsting options for combo boxes.
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/listLoader
 *
 * @todo document private members.
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/ui/listLoader"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param listLoader wc/ui/listLoader @ignore */
	function(initialise, Widget, listLoader) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/comboLoader~ComboLoader
		 * @private
		 */
		function ComboLoader() {
			var SELECT = new Widget("select"),
				SUGGESTION_LIST = new Widget("ul", "", {"role": "listbox"}),
				BUSY = "aria-busy";


			function getListBox(element) {
				var result,
					listId;
				if ((listId = element.getAttribute("aria-owns"))) {
					result = document.getElementById(listId);
				}
				return result;
			}

			/**
			 * Converts a select element to listbox options and replaces the optionList
			 * options with the new ones
			 *
			 * @function
			 * @private
			 * @param {(Element|Object)} dataList a select element or object with an 'options' array
			 * @param {Element} optionList the optionList to populate (a UL element).
			 */
			function selectToOptions(dataList, optionList) {
				var i,
					len,
					item,
					next,
					options = dataList.options;

				// just make sure we reset the list to throw out any we no longer need
				optionList.innerHTML = "";

				for (i = 0, len = options.length; i < len; i++) {
					next = options[i];
					item = document.createElement("li");
					item.setAttribute("data-wc-value", next.innerHTML);
					item.setAttribute("role", "option");
					item.className = "wc_invite";
					item.innerHTML = next.innerHTML;
					item.tabIndex = "0";
					optionList.appendChild(item);
				}
			}

			function callbackFactory(optionList, element, callback) {
				return function (data) {
					try {
						if (data && (data = SELECT.findDescendant(data))) {
							selectToOptions(data, optionList);
						}
						if (callback && typeof callback === "function") {
							callback(optionList, element);
						}
					}
					finally {
						element.removeAttribute(BUSY);
					}
				};
			}

			/*
			 * Registry processor for data list driven combos.
			 */
			function processNow(idArr) {
				var id, element, onsuccess,
					dataId, optionList,
					DATA_LIST_ATTRIB = "${wc.ui.selectLoader.attribute.dataListId}";

				while ((id = idArr.shift())) {
					if ((element = document.getElementById(id))) {
						if ((dataId = element.getAttribute(DATA_LIST_ATTRIB))) {
							if (SUGGESTION_LIST.isOneOfMe(element)) {
								optionList = element;
							}
							else {
								optionList = getListBox(element);
							}
						}
						if (optionList) {
							if (!SUGGESTION_LIST.isOneOfMe(element)) {
								element.setAttribute(BUSY, "true");
							}
							onsuccess = callbackFactory(optionList, element);
							listLoader.load(dataId, element).then(onsuccess);
						}
					}
				}
			}

			/**
			 * This sets up a registry of ids for eager loading data lists and adds an initialise callback to
			 * process the registry.
			 * @function module:wc/ui/comboLoader.register
			 * @public
			 * @param {String[]} idArr Array of element ID values.
			 */
			this.register = function(idArr) {
				if (idArr && idArr.length) {
					initialise.addCallback(function() {
						processNow(idArr);
					});
				}
			};
		}

		return /** @alias module:wc/ui/comboLoader */ new ComboLoader();
	});
