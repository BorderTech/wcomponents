/**
 * Provides a loading overlay which is removed as part of the last phase of page initialisation. This is intended to
 * reduce the porbability of a user interacting with a control before it has been initialised. This module does not
 * provide re-usable functionality, it **must** always be included in wc/common.js or in the page setup.
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/modalShim
 */
define(["wc/dom/initialise", "wc/ui/modalShim"],
	/** @param initialise wc/dom/initialise @param modalShim wc/ui/modalShim @ignore */
	function(initialise, modalShim) {
		"use strict";
		var loading = {
			/**
			 * A Promise that is resolved when thepage is first initialized.
			 * If other scripts wish to be notified when the UI is no longer in the loading state they can use this promise.
			 * @var
			 * @type {Promise}
			 * @public
			 */
			done: new Promise(function(loaded, error) {
				try {
					postInit();
					loaded();
				}
				catch (ex) {
					error(ex);
				}
			}),
			/**
			 * Call to dismiss the loading overlay (under normal circumstances this happens automatically).
			 * @function module:wc/ui/loading.postInit
			 * @public
			 */
			postInit: postInit
		};

		/**
		 * Call when the DOM is loaded and UI controls are initialized to dismiss the loading overlay.
		 * This is exposed as a public method since there are rare cases when it may need to be called manually.
		 */
		function postInit() {
			try {
				var container = document.getElementById("wc_ui_loading");
				if (container && container.parentNode) {
					container.parentNode.removeChild(container);
				}
			}
			finally {
				modalShim.clearModal();
			}
		}

		initialise.register(loading);
		return loading;
	});
