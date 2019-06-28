define(["wc/date/Format", "wc/date/parsers", "wc/date/interchange", "wc/dom/Widget"], function(Format, parsers, interchange, Widget) {
	var FIELD_CLASS = "wc-datefield",
		widgets,
		utils = {
			/**
			 * Get a list of potential date matches based on the user's input.
			 * @function
			 * @private
			 * @param {Element} element The input element of the date field.
			 * @param {String} [overrideVal] Use this as the value to match, instead of the element's value.
			 * @returns {String[]} Potential dates as strings.
			 */
			getMatches: function (element, overrideVal) {
				var value = overrideVal || element.value,
					parser = utils.getParser(element),
					matches = parser.getMatches(value);
				return matches;
			},

			/**
			 * Converts a formatted date string (that is, a string formatted for display to the users) to a transfer
			 * date string. It is assumed that you have already tried to get the transfer date from the value attribute.
			 * @function
			 * @private
			 * @param {Element} element A dateField input element
			 * @param {Boolean} [guess] If true then in the case that we can not precisely reverse format the
			 * dateField's value we will return a "guess" which will be the first match (if there are possible matches).
			 * @returns {String} A transfer date string if possible.
			 */
			reverseFormat: function (element, guess) {
				var formatter = Format.getDefaultFormatter(),
					parser = utils.getParser(element),
					result = "",
					value = this.getDateValue(element);
				if (value) {
					if (interchange.isValid(value)) {
						result = value;
					} else {
						result = formatter.reverse(parser, value, guess);
					}
				}
				return result;
			},
			getDateValue: function getDateValue(element) {  // TODO there is a similar function in ui/dateField
				var result;
				if ("value" in element) {
					result = element.value;
				} else if (element.children.length < 1) {
					// something like <span>28 JUN 2019</span>
					result = element.textContent;
				} else {
					result = "";
				}
				return result;
			},
			/**
			 * Finds the correct date parser for this element.
			 * @function
			 * @private
			 * @param {Element} element A date input element.
			 * @returns {Parser} An instance of {@link module:wc/date/Parser}
			 */
			getParser: function getParser(element) {
				var result;
				if (widgets.DATE_PARTIAL.isOneOfMe(element)) {
					result = parsers.get(parsers.type.PARTIAL);
				} else {
					result = parsers.get();
				}
				return result;
			},
			getWidgets: function() {
				return widgets || (widgets = createWidgets());
			},
			/**
			 * Indicates that the requested element is a dateField OR the textbox sub-component
			 * @function module:wc/ui/dateField.isOneOfMe
			 * @public
			 * @param {Element} element The DOM element to test
			 * @param {Boolean} [onlyContainer] Set `true` to test if the element is exactly the dateField, explicitly
			 *    `false` to test if only the input element.
			 * @returns {Boolean} true if the passed in element is a dateField or date input textbox sub-component of a
			 *    dateField
			 */
			isOneOfMe: function (element, onlyContainer) {
				var result,
					widgetMap = utils.getWidgets();
				if (onlyContainer) {
					result = widgetMap.DATE_WRAPPER_INCL_RO.isOneOfMe(element);
				} else if (onlyContainer === false) {
					result = widgetMap.INPUT.isOneOfMe(element);
				} else {
					result = Widget.isOneOfMe(element, [widgetMap.INPUT, widgetMap.DATE_WRAPPER_INCL_RO]);
				}
				return result;
			},
			hasPartialDate: function (element) {
				var result = false,
					value = this.getDateValue(element),
					parser = parsers.get(parsers.type.PARTIAL),  // We always want the the most inclusive (the partial parser) here
					formatter = Format.getDefaultFormatter();

				if (!interchange.isValid(value)) {
					value = formatter.reverse(parser, value);
				}
				if (value) {
					result = !interchange.isComplete(value);
				}
				return result;
			}
		};

	function createWidgets() {
		var widgetMap = {
			DATE_FIELD: new Widget("div", FIELD_CLASS),
			DATE_WRAPPER_INCL_RO: new Widget("", FIELD_CLASS),
			DATE_RO: new Widget("", "", {"data-wc-component": "datefield"}),
			INPUT: new Widget("input"),
			SUGGESTION_LIST: new Widget("", "", { role: "listbox"}),
			OPTION_WD: new Widget("", "", { role: "option"}),
			LAUNCHER: new Widget("button", "wc_wdf_cal"),
			SWITCHER: new Widget("input", "", { type: "checkbox" })
		};
		widgetMap.DATE = widgetMap.INPUT.extend("", { type: "date"});
		widgetMap.DATE_PARTIAL = widgetMap.INPUT.extend("", { type : "text"});
		widgetMap.INPUT.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.DATE.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.DATE_PARTIAL.descendFrom(widgetMap.DATE_FIELD, true);
		widgetMap.SUGGESTION_LIST.descendFrom(widgetMap.DATE_FIELD, true);
		return widgetMap;
	}

	return utils;
});