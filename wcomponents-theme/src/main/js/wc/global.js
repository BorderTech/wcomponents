/**
 * An easy way to get the global namespace (e.g. window) whilst keeping your
 * own code in strict mode and not having to wrap your module in an IIFE.
 */
define(function() {
	// DO NOT use strict mode here!!!
	return this;
});
