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
		initialise.addCallback(
			function() {
				try {
					var container = document.getElementById("wc_ui_loading");
					if (container && container.parentNode) {
						container.parentNode.removeChild(container);
					}
				}
				finally {
					modalShim.clearModal();
				}
			});
		return true;
	});
