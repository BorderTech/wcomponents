/**
 * Meta-module used to group all menu functionality for ease of inclusion.
 *
 * You could explicitly include those menu types you use in your application but they are not big (compared to
 * {@link module:wc/ui/menu.core}.
 *
 * Do not use this module directly - it is only used as a faux-layer. The module {@link module:wc/ui/navigationButton}
 * is included because it is required if their are any menu items which have a url attribute and it is also used in many
 * other locations: it should be in your common.js.
 *
 * @module
 * @requires module:wc/ui/menu/bar
 * @requires module:wc/ui/menu/column
 * @requires module:wc/ui/menu/tree
 * @requires module:wc/ui/navigationButton
 */
define(["wc/ui/menu/bar", "wc/ui/menu/column", "wc/ui/menu/tree", "wc/ui/menu/treemenu", "wc/ui/navigationButton"],
	function() {
		"use strict";
		return 1;
	});
