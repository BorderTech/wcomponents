/**
 * Common items reqiured for table functionality.
 * @module
 *
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/Widget"],
	/** @param Widget @ignore */
	function (Widget) {
		"use strict";

		var result = {
			WRAPPER: new Widget("div", "wc-table"),
			TABLE: new Widget("table"),
			THEAD: new Widget("thead"),
			TBODY: new Widget("tbody"),
			TR: new Widget("tr"),
			TH: new Widget("th"),
			TD: new Widget("td"),
			BUTTON: new Widget("button")
		};

		result["ROW"] = result.TR.extend("", {"role": "row"});

		return /** @alias module:wc/ui/table/common */ result;
	});
