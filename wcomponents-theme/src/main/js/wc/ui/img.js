define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/loader/resource",
	"lib/handlebars/handlebars",
	"wc/i18n/i18n",
	"wc/ui/ajax/processResponse"],
	function(initialise, Widget, loader, handlebars, i18n, processResponse) {
		"use strict";

		function Image() {
			var IMG = new Widget("img", "", {"data-wc-editor": null}),
				TEMPLATE,
				EDIT;

			function getTemplate() {
				if (!TEMPLATE) {
					return loader.load("imgedit.html", true, true).then(function (template) {
						TEMPLATE = handlebars.compile(template);
					});
				}
				return Promise.resolve();
			}

			function makeEditButton(element) {
				var id = element.id,
					sibling = element.nextSibling;
				if (sibling && sibling.getAttribute("data-wc-img") === id) {
					return;
				}
				EDIT = EDIT || i18n.get("imgedit_edit");
				getTemplate().then(function() {
					var html,
						props;
					if (TEMPLATE) {
						props = {
							id: id,
							editor: element.getAttribute("data-wc-editor"),
							text: EDIT
						};
						html = TEMPLATE(props);

						if (html) {
							element.insertAdjacentHTML("afterend", html);
						}
					}
				});
			}

			function makeEditButtons(element) {
				var el = element || document.body;
				if (element && IMG.isOneOfMe(element)) {
					makeEditButton(element);
				}
				else {
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
