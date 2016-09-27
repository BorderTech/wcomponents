define(["wc/dom/Widget"], function (Widget) {
	"use strict";

	var props,
		descendFrom = function () {
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

	props = Object.keys(result);
	props.forEach(function (prop) {
		var widget = result[prop];
		if (result.hasOwnProperty(prop) && widget && widget["descendFrom"]) {
			widget.descendFrom = descendFrom;
		}
	});

	return result;

	/**
	 * Common items required for table functionality.
	 * @module
	 *
	 * @requires module:wc/dom/Widget
	 */
});
