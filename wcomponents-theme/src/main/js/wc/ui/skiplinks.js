require(["wc/dom/initialise", "wc/dom/Widget", "wc/ui/ajax/processResponse"],
	function(initialise, Widget, processResponse) {
		"use strict";

		var instance;

		function SkipLink() {
			var CONTAINER = new Widget("", "wc-skiplinks"),
				PANEL_OF_INTEREST = new Widget("", "", {"data-wc-title": null, "accesskey": null}),
				ARIA_HIDDEN = "aria-hidden",
				skipLinkContainer;

			function setup() {
				var b = document.body;

				skipLinkContainer = skipLinkContainer || CONTAINER.findDescendant(b);
				if (skipLinkContainer) {
					skipLinkContainer.innerHTML = "";
					PANEL_OF_INTEREST = PANEL_OF_INTEREST || new Widget("", "", {"data-wc-title": null, "accesskey": null});

					Array.prototype.forEach.call(PANEL_OF_INTEREST.findDescendants(b), function (next) {
						var id = next.id, title = next.getAttribute("data-wc-title");
						if (id && title) {
							skipLinkContainer.insertAdjacentHTML("beforeEnd", "<a class='wc-skiplink' href='#" + id + "'>" + title + "</a>");
						}
					});

					if (skipLinkContainer.innerHTML) {
						skipLinkContainer.removeAttribute(ARIA_HIDDEN);
						// ensure the skipLinks are the first child of the document body
						if (b.firstChild !== skipLinkContainer) {
							b.insertBefore(skipLinkContainer, b.firstChild);
						}
					} else {
						skipLinkContainer.setAttribute(ARIA_HIDDEN, "true");
					}
				}
			}

			this.postInit = function() {
				setup();
				processResponse.subscribe(setup, true);
			};
		}

		instance = new SkipLink();
		initialise.register(instance);
		return instance;
	});
