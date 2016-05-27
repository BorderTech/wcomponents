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

		var props, descendFrom = function() {
				if (this.hasOwnProperty("descendFrom")) {
					console.error("refusing to modify widget");
				}
				else {
					Widget.prototype.descendFrom.apply(this, arguments);
				}
			},
			result = {
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
		props = Object.keys(result);
		props.forEach(function(prop) {
			var widget = result[prop];
			if (result.hasOwnProperty(prop) && widget && widget["descendFrom"]) {
				widget.descendFrom = descendFrom;
			}
		});

		return /** @alias module:wc/ui/table/common */ result;
	});
