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
		WRAPPER = new Widget("div", "wc-table"),
		result = {
			WRAPPER: WRAPPER,
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

	function getWrapperId(element) {
		var wrapper = WRAPPER.findAncestor(element);
		if (wrapper) {
			return wrapper.id;
		}
		return null;
	}

	result.getAjaxDTO = function (element, isOneShot) {
		var id = element.id,
			alias = getWrapperId(element),
			oneShot = isOneShot ? 1 : -1,
			loads = alias ? [alias] : [];
		return {
			id: id,
			loads: loads,
			alias: alias,
			formRegion: alias,
			oneShot: oneShot
		};
	};

	return result;

	/**
	 * Common items required for table functionality.
	 * @module
	 *
	 * @requires module:wc/dom/Widget
	 */
});
