define(["wc/dom/initialise",
	"wc/dom/Widget"],
	function (intialise, Widget) {
		"use strict";

		function WrappedInput() {
			var WRAPPER = new Widget("", "wc-input-wrapper"),
				RO_WRAPPER = new Widget("", "wc-ro-input"),
				INPUT = new Widget("input"),
				SELECT = new Widget("select"),
				TEXTAREA = new Widget("textarea"),
				WRAPPED = [INPUT, SELECT, TEXTAREA],
				WIDGETS = [WRAPPER, RO_WRAPPER],
				ID_SUFFIX = "_input";

			this.getWidget = function() {
				return WRAPPER;
			};

			this.getWidgets = function() {
				return WIDGETS;
			};

			this.getWrappedWidgets = function() {
				return WRAPPED;
			};

			this.getROWidget = function() {
				return RO_WRAPPER;
			};

			this.isOneOfMe = function(element, inclReadOnly) {
				return inclReadOnly ? Widget.isOneOfMe(element, WIDGETS) : WRAPPER.isOneOfMe(element);
			};

			this.isReadOnly = function(element) {
				return RO_WRAPPER.isOneOfMe(element);
			};

			this.getInput = function(element) {
				if (!(element && WRAPPER.isOneOfMe(element))) {
					return null;
				}
				return Widget.findDescendant(element, WRAPPED);
			};

			this.isWrappedInput = function(element) {
				var parent = element.parentNode;
				return WRAPPER.isOneOfMe(parent);
			};

			this.getWrapper = function(element) {
				var parent = element.parentNode;
				return WRAPPER.isOneOfMe(parent) ? parent : null;
			};

			this.get = function (container, inclReadOnly) {
				var result = inclReadOnly ? Widget.isOneOfMe(container, WIDGETS) : WRAPPER.isOneOfMe(container);
				if (result) {
					return [container];
				}
				return inclReadOnly ? Widget.findDescendants(container, WIDGETS) : WRAPPER.findDescendants(container);
			};

			this.getWrappedId = function(element) {
				var id = element.id,
					wrapper;
				if (this.isReadOnly(element) || WRAPPER.isOneOfMe(element)) {
					return id.concat(ID_SUFFIX);
				}
				if ((wrapper = this.getWrapper(element))) {
					if (Widget.isOneOfMe(element, WRAPPED)) {
						return element.id;
					}
					return (wrapper.id).concat(ID_SUFFIX);
				}
				return null;
			};
		}

		var instance = new WrappedInput();
		intialise.register(instance);
		return instance;
	});

