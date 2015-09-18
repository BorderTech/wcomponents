/**
 * Workaround for the barely documented IE "noscope element" feature.Although discussion on noscope elements mentions
 * only script, style and comment elements I have found it also applies to elements with no content, for example this:
 * &lt;span&gt;&lt;!-- don't delete me --&gt;&lt;/span&gt;. Our fix is to insert this:
 * &lt;span&gt;Internet Explorer noscope fix&lt;/span&gt;. More wasted clock cycles to fix IE bugs...
 *
 * Note that inserting the noscope fix can have side-effects that cause other code to break, this has happened to us in
 * real scenarios. The general problem is that since the noscope fix has to come first, it will be the "firstChild" of
 * the container it is injected into. Anything that expects a particular firstChild will be surprised to find a stranger
 * there instead, for example CSS and querySelector will break when trying to use :firstChild. The solution is to waste
 * yet more clock cycles in IE and remove the noscope fix element immediately after injecting the new content.
 *
 * BTW Here's the blurb on noscope from some user comment on MSDN: "All NoScope elements are removed from the beginning
 * of the parsed HTML before it is injected with innerHTML or insertAdjacentHTML. To prevent this from happening, you
 * must include at least one scoped element at the beginning of the injected HTML."
 *
 * @module
 * @private
 *
 * @requires module:wc/dom/Widget
 * @requires module:wc/has
 */
define(["wc/dom/Widget", "wc/has"], /** @param Widget @param has @ignore */function(Widget, has) {
	"use strict";
	/**
	 * @function
	 * @alias module:wc/fix/noScope_ie8
	 * @param {(String|Element)} htmlOrElement Either an HTML string (you want the noscope fix inserted) OR
	 * a DOM element (you want the noscope fix removed)
	 * @ignore
	 */
	function ieNoScopeFix(htmlOrElement) {
		var fix,
			result = htmlOrElement,
			tagName = "span",
			className = "ieNoScopeBugFix";
		/*
		 * My testing on IE9 RC1 indicates that as of IE9 we don't need noscope fix anymore
		 */
		if (has("ie") < 9 && htmlOrElement) {
			if (htmlOrElement.constructor === String) {
				// we have some html, so we are adding a noscope fix
				if (htmlOrElement.length > 2) {  // rough check, html must be at least 3 characters long
					console.info("Inserting IE noscope fixer");
					fix = "<" + tagName + " style=\"display:none\" class=\"" + className + "\">Internet Explorer noscope fix</" + tagName + ">";
					result = fix + htmlOrElement;
				}
			}
			else {
				result = htmlOrElement;
				// we have a dom element, so we are removing noscope fixes
				fix = new Widget(tagName, className);
				try {
					Array.prototype.forEach.call(fix.findDescendants(htmlOrElement), function(fix) {
						var parent = fix.parentNode;
						if (parent) {
							parent.removeChild(fix);
							console.info("Removed IE noscope fixer (its work is done)");
						}
					});
				}
				catch (ex) {
					console.error(ex.message);
				}
			}
		}
		return result;
	}
	return ieNoScopeFix;
});
