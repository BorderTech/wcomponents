/**
 * This is the one true meta module. It only makes sense to have one of these.
 *
 * This module exists solely for the purposes of optimization. Optimization here means reducing the
 * number of HTTP requests at runtime.
 *
 * @example
 *	require(["wc/a8n"
 *			"wc/ui/template",
 *			"wc/dom/cancelUpdate",
 *			"wc/ui/calendar",
 *			"wc/ui/subordinate",
 *			"wc/ui/menu/bar");
 *
 * @ignore
 */
require(["wc/a8n",
	"wc/ui/template",
	"wc/ui/loading",
	"wc/ui/backToTop",
	"wc/ui/label",
	"wc/ui/menu",
	"wc/ui/tabset",
	"wc/ui/textField"]);