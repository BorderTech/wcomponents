define(["wc/dom/Widget", "wc/dom/initialise", "wc/ui/ajax/processResponse"],
	function(Widget, initialise, processResponse) {
		"use strict";

		function BuggyPlaceHolderFix() {
			var TA = new Widget("textarea", "wc-buggyie", {"placeholder": null});

			function fixMe(textarea) {
				textarea.value = "";
				textarea.innerText = "";
			}

			function fixAll(element, container) {
				var candidates = TA.isOneOfMe(container) ? [container] : TA.findDescendants(container);
				Array.prototype.forEach.call(candidates, fixMe);
			}

			this.initialise = function(element) {
				fixAll(null, element);
				processResponse.subscribe(fixAll);
			};
		}

		var instance = new BuggyPlaceHolderFix();
		initialise.register(instance);
		return instance;
	});

