define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/template",
	"wc/i18n/i18n",
	"wc/ui/ajax/processResponse"],
	function(initialise, Widget, template, i18n, processResponse) {
		"use strict";

		function Image() {
			var IMG = new Widget("img", "", {"data-wc-editor": null});

			function makeEditButton(element) {
				var id = element.id,
					sibling = element.nextSibling;
				if (sibling && sibling.getAttribute("data-wc-img") === id) {
					return;
				}
				i18n.translate("imgedit_edit").then(function(editButtonText) {
					var props = {
						id: id,
						editor: element.getAttribute("data-wc-editor"),
						text: editButtonText
					};

					template.process({
						source: "imgedit.html",
						loadSource: true,
						target: element,
						context: props,
						position: "afterend"
					});
				});
			}

			function makeEditButtons(element) {
				var el = element || document.body;
				if (element && IMG.isOneOfMe(element)) {
					makeEditButton(element);
				} else {
					Array.prototype.forEach.call(IMG.findDescendants(el), makeEditButton);
				}
			}

			this.postInit = function() {
				processResponse.subscribe(makeEditButtons, true);
			};

			this.initialise = function (element) {
				makeEditButtons(element);
			};
		}

		/**
		 * Models an editable image.
		 * @module
		 */
		var instance = new Image();
		initialise.register(instance);
		return instance;
	});
