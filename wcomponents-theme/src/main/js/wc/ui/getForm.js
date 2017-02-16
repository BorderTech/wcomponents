define(["wc/dom/Widget"], function (Widget) {
	"use strict";

	var FORM;

	return function(el, forceAncestor) {
		var forms;
		if (!el) {
			if (!forceAncestor) {
				forms = document.getElementsByTagName("form");
				if (forms && forms.length) {
					return forms[0];
				}
			}
			return null;
		}
		if (el.form) {
			return el.form;
		}
		FORM = FORM || new Widget("form");
		return FORM.findAncestor(el);
	};
});


