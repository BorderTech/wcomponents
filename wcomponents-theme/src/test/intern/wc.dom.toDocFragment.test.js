define(["intern!object", "intern/chai!assert", "wc/dom/toDocFragment"],
	function(registerSuite, assert, controller) {
		"use strict";

		var SIMPLE_HTML = "<div>this is some html<span>hello</span></div>";

		registerSuite({
			name: "wc/dom/toDocFragment",
			testReturnsDocFragment: function() {
				var df = controller(SIMPLE_HTML);
				assert.strictEqual(Node.DOCUMENT_FRAGMENT_NODE, df.nodeType);
			},
			testNoMunge: function() {
				var df = controller(SIMPLE_HTML),
					container = document.createElement("div");
				container.appendChild(df);
				assert.strictEqual(SIMPLE_HTML, container.innerHTML);
			},
			testToDocFragNoElements: function() {
				var content = "text node",
					df = controller(content);
				assert.strictEqual(content, df.firstChild.nodeValue);
			}
		});
	});