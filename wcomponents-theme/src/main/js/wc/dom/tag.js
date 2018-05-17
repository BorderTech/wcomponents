/**
 * A module to provide case insensitive access to tag names in a way that the tag name will be the case of the BODY
 * tagName.
 *
 * Instead of using regular expressions to test the name of a tag (using the case insensitive flag i) or calling
 * toLowerCase() each time, tests can be made directly against this module. This should provide a slight speed
 * improvement as well as making your code clearer.
 * @example // to test if 'element' is a fieldset:
 * if (element.tagName == tag.FIELDSET) // ... do stuff
 *
 * @module
 */
define(function() {
	"use strict";
	/**
	 * @alias module:wc/dom/tag
	 */
	var tags = [
			"A",
			"ABBR",
			"ACRONYM",
			"ADDRESS",
			"AREA",
			"ARTICLE",
			"ASIDE",
			"AUDIO",
			"B",
			"BASE",
			"BDI",
			"BDO",
			"BIG",
			"BLOCKQUOTE",
			"BODY",
			"BR",
			"BUTTON",
			"CANVAS",
			"CAPTION",
			"CITE",
			"CODE",
			"COL",
			"COLGROUP",
			"COMMAND",
			"DATALIST",
			"DD",
			"DEL",
			"DATALIST",
			"DETAILS",
			"DFN",
			"DIALOG",
			"DIV",
			"DL",
			"DT",
			"EM",
			"FIELDSET",
			"FIGCAPTION",
			"FIGURE",
			"FOOTER",
			"FORM",
			"FRAME",
			"FRAMESET",
			"H1",
			"H2",
			"H3",
			"H4",
			"H5",
			"H6",
			"HEAD",
			"HEADER",
			"HGROUP",
			"HR",
			"HTML",
			"I",
			"IFRAME",
			"IMG",
			"INPUT",
			"INS",
			"KBD",
			"KEYGEN",
			"LABEL",
			"LEGEND",
			"LI",
			"LINK",
			"MAP",
			"MARK",
			"META",
			"METER",
			"NAV",
			"NOFRAMES",
			"NOSCRIPT",
			"OBJECT",
			"OL",
			"OPTGROUP",
			"OPTION",
			"OUTPUT",
			"P",
			"PARAM",
			"PRE",
			"PROGRESS",
			"Q",
			"RP",
			"RT",
			"RUBY",
			"S",
			"SAMP",
			"SCRIPT",
			"SECTION",
			"SELECT",
			"SMALL",
			"SOURCE",
			"SPAN",
			"STRONG",
			"STYLE",
			"SUB",
			"SUMMARY",
			"SUP",
			"TABLE",
			"TBODY",
			"TD",
			"TEXTAREA",
			"TFOOT",
			"TH",
			"THEAD",
			"TIME",
			"TITLE",
			"TR",
			"TRACK",
			"TT",
			"UL",
			"VAR",
			"VIDEO",
			"WBR"
		],
		lower = false;

	(function() {
		var a, i, next, propDesc,
			TAGNAME = "tagName", HEAD = "head",
			textNodeTagName = "#text";
		try {
			a = document.getElementsByTagName(HEAD)[0];
			lower = a.tagName === HEAD;
			if (window.Text && typeof Object.getOwnPropertyDescriptor !== "undefined") {  // it's nice if text nodes have a tagName property
				propDesc = Object.getOwnPropertyDescriptor(window.Text.prototype, TAGNAME);
				if (!propDesc || (!propDesc.get && !propDesc.value)) {
					if (!lower) {
						textNodeTagName = textNodeTagName.toUpperCase();
					}
					// Note I tried to use a data property here but IE8 wouldn't let me set writable to false
					Object.defineProperty(window.Text.prototype, TAGNAME, {
						get: function() {
							return textNodeTagName;
						}});
				}
			}
			for (i = 0; i < tags.length; i++) {
				next = tags[i];
				tags[next] = lower ? next.toLowerCase() : next;
			}
		} finally {
			a = null;  // this cleanup is essential to avoid known memory leak (IE6/7)
		}
	})();

	/**
	 * Helper function can be employed when building up strings for use in setting innerHTML.
	 * @function module:wc/dom/tag.toTag
	 * @public
	 * @static
	 * @param {String} tagName The name of the tag, eg "input"
	 * @param {boolean} [closing] If true will return a closing tag (eg "&lt;/p&gt;") (if true, closing obsoletes the
	 *    next two arguments).
	 * @param {String|Object} [attributes] The attributes to include in the tag, eg 'class="someClass" type="text"'. If using the object
	 * variant then each property native to the object is added as `key="value"`
	 * @param {boolean} [empty] If true will return an empty tag, eg &lt;input/&gt;
	 */
	tags.toTag = function (tagName, closing, attributes, empty) {
		var tag = ["<"], p;
		if (closing) {
			tag.push("/");
			tag.push(tagName);
			tag.push(">");
			return tag.join("");
		}

		tag.push(tagName);
		if (attributes) {
			if (attributes.constructor === String) {
				tag.push(" " + attributes);
			} else if (typeof attributes === "object") {
				for (p in attributes) {
					if (attributes.hasOwnProperty(p)) {
						tag.push(" " + p + "=\"" + attributes[p] + "\"");
					}
				}
			}
		}
		if (empty) {
			tag.push("/");
		}
		tag.push(">");
		return tag.join("");
	};
	return tags;
});
