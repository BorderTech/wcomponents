define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var tag,
			ID_1 = "tagTestId1",
			ID_2 = "tagTestId2",
			testHolder;

		registerSuite({
			name: "domTag",
			setup: function() {
				return testutils.setupHelper(["wc/dom/tag"], function(obj) {
					tag = obj;
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				testHolder.innerHTML = "<p id = \"" + ID_1 + "\">this is a test</p><P id = \"" + ID_2 + "\">this is a test</P>";
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testTag: function() {
				var element = document.getElementById(ID_1);
				assert.strictEqual(tag.P, element.tagName);
			},
			testTagUpperCase: function() {
				var element = document.getElementById(ID_2);
				assert.strictEqual(tag.P, element.tagName);
			},
			testToTag: function() {
				assert.strictEqual("<input>", tag.toTag("input"));
			},
			testToTagWithAttributes: function() {
				assert.strictEqual("<input foo=\"bar\" bar=\"foo\">", tag.toTag("input", false, "foo=\"bar\" bar=\"foo\""));
			},
			testToTagClosing: function() {
				assert.strictEqual("</input>", tag.toTag("input", true));
			},
			testToTagClosingWithAttributes: function() {
				assert.strictEqual("</input>", tag.toTag("input", true, "foo=\"bar\""));
			},
			testToTagSelfClosing: function() {
				assert.strictEqual("<input/>", tag.toTag("input", false, "", true));
			}
		});
	});
