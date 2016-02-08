/**
 * Provides an indicator that debug mode is on.
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/ajax/processResponse
 */
define(["wc/dom/initialise","wc/ui/ajax/processResponse"],
	/** @param initialise @param processResponse wc/ui/ajax/processResponse @ignore */
	function(initialise, processResponse) {
		"use strict";

		function DebugModeIndicator() {
			/**
			 * Count the number of Element nodes in the current document and set it as the value of an attribute on
			 * document.body.
			 * @function
			 * @private
			 */
			function countElements() {
				document.body.setAttribute("data-wc-nodeCount", document.getElementsByTagName("*").length);
			}

			/**
			 * Late initialisation: set up subscribersand count the page's elements. This is a subscriber to
			 * {@link module:wc/dom/initialise}.
			 *
			 * @function
			 * @public
			 */
			this.postInit = function() {
				countElements();
				processResponse.subscribe(countElements, true);
			};
		}
		var /** @alias module:wc/debug/debugModeIndicator */ debug = new DebugModeIndicator();
		initialise.register(debug);
		return debug;
	});
