/**
 * Meta-module used to group all table functionality for ease of inclusion. You could explicitly include those table
 * aspects you use in your application but they are not big. You do not use this module directly - it is only used as a
 * faux-layer.
 *
 * @module
 * @requires module:wc/ui/table/action
 * @requires module:wc/ui/table/pagination
 * @requires module:wc/ui/rowAnalog
 * @requires module:wc/ui/table/rowExpansion
 * @requires module:wc/ui/table/sort
 */
define(["wc/ui/table/action", "wc/ui/table/pagination", "wc/ui/rowAnalog", "wc/ui/table/rowExpansion", "wc/ui/table/sort"],
	function() {
		"use strict";
		return 1;
	});
