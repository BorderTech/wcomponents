define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/shed",
	"wc/i18n/i18n"],
	function (intialise, Widget, shed, i18n) {
		"use strict";

		function WrappedInput() {
			var WRAPPER = new Widget("", "wc-input-wrapper"),
				RO_WRAPPER = new Widget("", "wc-ro-input"),
				INPUT = new Widget("input"),
				SELECT = new Widget("select"),
				TEXTAREA = new Widget("textarea"),
				WRAPPED = [INPUT, SELECT, TEXTAREA],
				WIDGETS = [WRAPPER, RO_WRAPPER];

			function mandate(element, action) {
				var input,
					PLACEHOLDER = "placeholder";
				if (!(element && instance.isOneOfMe(element))) {
					return;
				}
				if ((input = Widget.findDescendant(element, WRAPPED))) {
					shed[action](input);
					if (action === shed.actions.MANDATORY) {
						input.setAttribute(PLACEHOLDER, i18n.get("requiredPlaceholder"));
					}
					else {
						input.removeAttribute(PLACEHOLDER);
					}
				}
			}

			this.isReadOnly = function(element) {
				return !!RO_WRAPPER.findAncestor(element);
			};

			this.postInit = function() {
				shed.subscribe(shed.actions.MANDATORY, mandate);
				shed.subscribe(shed.actions.OPTIONAL, mandate);
			};

			this.getWidget = function() {
				return WRAPPER;
			};

			this.getROWidget = function() {
				return RO_WRAPPER;
			};

			this.getWidgets = function() {
				return WIDGETS;
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
		}

		var instance = new WrappedInput();
		intialise.register(instance);
		return instance;
	});

