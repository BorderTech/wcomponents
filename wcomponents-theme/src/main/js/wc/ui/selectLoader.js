define(["wc/ui/listLoader",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/getFilteredGroup",
	"wc/ui/selectboxSearch",
	"wc/dom/shed",
	"wc/dom/event",
	"wc/dom/textContent",
	"wc/i18n/i18n",
	"wc/dom/getLabelsForElement",
	"wc/ui/feedback",
	"wc/has"],
	function(listLoader, initialise, Widget, getFilteredGroup, selectboxSearch, shed, event, textContent, i18n, getLabelsForElement, feedback, has) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/selectLoader~SelectLoader
		 * @private
		 */
		function SelectLoader() {
			var DISABLED_BY_ME = "data-wc-selectloader-disabled",
				OPTION_CONTAINER = new Widget("SELECT");

			/**
			 * Generates new callback functions curried with the id of the element we want the callback to operate on.
			 * @function
			 * @private
			 * @param {String} id The id of a select element.
			 */
			function callbackFactory(id) {
				return function (datalist) {
					var currentOptions, optContainer, message,
						element = document.getElementById(id),
						selectList;
					if (element) {
						message = getErrorMessage(id, false);
						if (message) {
							feedback.remove(message, element);
						}
						try {
							selectList = OPTION_CONTAINER.isOneOfMe(element) ? element : OPTION_CONTAINER.findDescendant(element);
							if (!selectList) {
								return;
							}
							if (shed.isDisabled(element) && element.hasAttribute(DISABLED_BY_ME)) {
								element.removeAttribute(DISABLED_BY_ME);
								shed.enable(element, true);
							}
							currentOptions = getFilteredGroup(selectList);
							optContainer = OPTION_CONTAINER.findDescendant(datalist);
							if (optContainer) {
								if (has("ie") < 10) {
									console.info("Forced to populate list slowly due to IE8 bugs");
									selectList.innerHTML = "";
									Array.prototype.forEach.call(optContainer.options, function(next) {
										selectList.appendChild(next.cloneNode(true));
									});
								} else {
									selectList.innerHTML = optContainer.innerHTML;
								}
								// re-select all the options that were originally selected
								Array.prototype.forEach.call(currentOptions, function(next) {
									var nextIdx = selectboxSearch.indexOf(next, optContainer), selIdx;
									if (nextIdx >= 0) {
										shed.select(selectList.options[nextIdx], true); // do not publish as the selection has not changed.
										if (!selectList.hasAttribute(("multiple"))) { // the following is a Safari 8.0.8 bug workaround.
											selIdx = selectboxSearch.indexOf(next, selectList);
											if (selectList.selectedIndex !== selIdx) {
												selectList.selectedIndex = selIdx;
											}
										}
									}
								});
							} else {
								console.warn("Datalist malformed");
							}
						} finally {
							element.removeAttribute("aria-busy");
							currentOptions = element = selectList = null;
						}
					} else {
						console.warn("Could not load list", id);
					}
				};
			}

			/**
			 * Generates new callback functions curried with the id of the element we want the callback to operate on.
			 * @function
			 * @private
			 * @param {String} id The id of a select element.
			 */
			function errorCallbackFactory(id) {
				return function () {
					var element = document.getElementById(id);
					if (element) {
						getErrorMessage(id, true);
						if (!shed.isDisabled(element)) {
							element.setAttribute(DISABLED_BY_ME, true);
							shed.disable(element, true);
						}
						element.removeAttribute("aria-busy");
					} else {
						console.warn("Could not find element", id);
					}
				};
			}

			/*
			 * Present the user with a message if the list cannot be loaded.
			 * To a large extent this probably belongs in listLoader so it can be reused.
			 */
			function getErrorMessage(id, create) {
				var element = document.getElementById(id),
					labels, label,
					button,
					message = feedback.getBox(element, feedback.LEVEL.ERROR),
					errorResult;
				if (!message && element && create) {
					labels = getLabelsForElement(element, true);
					if (labels && labels.length) {
						label = textContent.get(labels[0]);
						label = " '" + label + "'";
					} else {
						label = "";
					}
					label = i18n.get("loader_loaderr", label);
					errorResult = feedback.flagError(element, label);
					if (errorResult && (message = document.getElementById(errorResult))) {
						button = document.createElement("button");
						button.type = "button";
						button.innerHTML = i18n.get("loader_retry", label);
						event.add(button, "click", function($event) {
							$event.preventDefault();  // important! stop any other listeners responding to this button
							instance.load(id);

						}, false);
						message.appendChild(button);
					}
				}
				return message;
			}

			/*
			 * Registration processor
			 * @param {String[]} idArr An array of element ids.
			 */
			function processNow(idArr) {
				var id;
				while ((id = idArr.shift())) {
					instance.load(id);
				}
			}

			/**
			 * Load data list SELECT elements.
			 * @function
			 * @public
			 * @param {String[]} idArr An array of element ids.
			 */
			this.register = function(idArr) {
				if (idArr && idArr.length) {
					initialise.addCallback(function() {
						processNow(idArr);
					});
				}
			};

			/**
			 * Populates the select with options.
			 * Select lists will call this public method directly on page load.
			 * @param {String} id The id of the select element we are loading.
			 */
			this.load = function (id) {
				var element = document.getElementById(id),
					win = callbackFactory(id),
					lose = errorCallbackFactory(id);
				if (element) {
					element.setAttribute("aria-busy", true);
					listLoader.load(element.getAttribute("data-wc-list"), element, false).then(win, lose);
				}
			};
		}
		/**
		 * This module allows the options of a select list to be loaded and cached. This is all about improving performance by
		 * keeping the payload small and using listLoader to get cache benefits.
		 *
		 * Note that IE8 ruins this in a few ways:
		 *
		 * * Ideally we would simply transform the options into a documentFragment and then append that documentFragment to the
		 *   existing select. However IE8 can simply not cope with options that are not inside a select. This creates a heavier
		 *   routine for adding options the select.  Optgroup is not good as a container as it requires a label and alters
		 *   formatting.
		 * * Even with the above concession to IE, using a select instead of a documentFragment IE8 still has further issues in
		 *   that you can't say sel1.innerHTML = sel2.innerHTML.  So we are forced to loop through each option and add then one
		 *   by one for IE8 (tested on IE9, still can't do it).
		 *
		 * @module
		 * @requires module:wc/ui/listLoader
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/getFilteredGroup
		 * @requires module:wc/ui/selectboxSearch
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/textContent
		 * @requires module:wc/dom/i18n
		 * @requires module:wc/dom/getLabelsForElement
		 * @requires module:wc/has
		 *
		 * @todo Document private members, check source order.
		 */
		var instance = new SelectLoader();
		return instance;
	});
