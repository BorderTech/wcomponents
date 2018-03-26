define(["wc/dom/Widget",
	"wc/dom/tag"],
	function(Widget, tag) {
		"use strict";

		function MessageBox() {
			var MB_CLASS = "wc-messagebox",
				ERROR_CLASS = "wc-messagebox-type-error",
				GENERIC_BOX,
				W_MESSSAGE_BOX,
				ERROR_BOX,
				SECTION = tag.SECTION;

			function init() {
				if (GENERIC_BOX) {
					return;
				}
				GENERIC_BOX = new Widget(SECTION, MB_CLASS);
			}

			function checkElementArg(element, lenient) {
				if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
					if (lenient) {
						return false;
					}
					throw new TypeError("Argument must be an element");
				}
				return true;
			}

			this.getWidget = function() {
				init();
				return (W_MESSSAGE_BOX = W_MESSSAGE_BOX || new Widget(SECTION, MB_CLASS));
			};

			this.isOneOfMe = function(element) {
				if (!checkElementArg(element, true)) {
					return false;
				}
				return this.getWidget().isOneOfMe(element);
			};

			function getWithWidget(element, widget, all) {
				var container = element,
					func = "findDescendant";
				if (!checkElementArg(element, true)) {
					container = document.body;
				}
				if (all) {
					func += "s";
				}
				return widget[func](container);
			}

			this.get = function(element, all) {
				return getWithWidget(element, this.getWidget(), all);
			};

			this.getErrorBoxWidget = function() {
				return (ERROR_BOX = ERROR_BOX || this.getWidget().extend(ERROR_CLASS));
			};

			this.getErrorBoxes = function(element, all) {
				return getWithWidget(element, this.getErrorBoxWidget(), all);
			};
		}

		return new MessageBox();

	});
