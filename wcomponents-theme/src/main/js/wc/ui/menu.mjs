/**
 * Meta-module used to group all menu functionality for ease of inclusion.
 *
 * You could explicitly include those menu types you use in your application, but they are not big (compared to
 * {@link module:wc/ui/menu.core}.
 *
 * Do not use this module directly - it is only used as a faux-layer. The module {@link module:wc/ui/navigationButton}
 * is included because it is required if there are any menu items which have a url attribute, and it is also used in many
 * other locations: it should be in your common.js.
 */
import "wc/ui/menu/bar";
import "wc/ui/menu/column";
import "wc/ui/menu/tree";
import "wc/ui/menu/treemenu";
import "wc/ui/navigationButton";
