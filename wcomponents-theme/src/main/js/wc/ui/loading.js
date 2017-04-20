define(["wc/dom/initialise", "wc/ui/modalShim", "wc/timers", "wc/has"],
	function(initialise, modalShim, timers, has) {
		"use strict";

		/**
		 * Provides a loading overlay which is removed as part of the last phase of page initialisation. This is intended to
		 * reduce the probability of a user interacting with a control before it has been initialised. This module does not
		 * provide re-usable functionality, it **must** always be included in `wc/common.js` or in the page setup.
		 *
		 * @module
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/modalShim
		 */
		var loading = {

			/**
			 * A Promise that is resolved when the page is first initialized.
			 * If other scripts wish to be notified when the UI is no longer in the loading state they can use this promise.
			 * @var
			 * @type {Promise}
			 * @public
			 */
			done: new Promise(function(loaded, error) {
				try {
					loaded();
				}
				catch (ex) {
					error(ex);
				}
			})
		};

		/**
		 * Remove the loading indicator and clear the shim.
		 * @function
		 * @private
		 */
		function clearLoadingShim() {
			var container;
			try {
				Array.prototype.forEach.call(document.getElementsByTagName("form"), function(form) {
					form.removeAttribute("hidden");
					if (has("ie") === 8) {
						require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
							inlineBlock.checkRepaint(form);
						});
					}
				});
				container = document.getElementById("wc-ui-loading");
				if (container && container.parentNode) {
					container.parentNode.removeChild(container);
				}
			}
			finally {
				modalShim.clearModal();
			}
		}
		/**
		 * Call when the DOM is loaded and UI controls are initialized to dismiss the loading overlay.
		 * @function
		 * @private
		 */
		function postInit() {
			loading.done.then(timers.setTimeout(clearLoadingShim, 0));
		}

		initialise.register({"postInit": postInit});
		return loading;
	});
