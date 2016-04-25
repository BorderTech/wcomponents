define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var xmlString, testXmlString;
		registerSuite({
			name: "xmlString",
			setup: function() {
				return testutils.setupHelper(["wc/xml/xmlString"], function(obj) {
					xmlString = obj;
					testXmlString = "<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";
				});
			},
			testStringFromXml: function() {
				/* Convert a String to an XML DOM object with DOM methods etc */
				var result = xmlString.from(testXmlString),
					from = result.getElementsByTagName("from")[0];
				assert.strictEqual("note", result.documentElement.tagName);
				assert.strictEqual("from", from.tagName);
			},
			testStringToXml: function() {
				/* Convert an XML DOM to a String representation of the XML.
				 * Note, this test depends entirely on a working xmlTransformer.from
				 * ie the previous test must pass for this test to pass.  So if both of these
				 * tests fail then fix the other one first. */
				var whitespaceRe = /\s/g,
					xmlHeaderRe = /\<\?xmlversion.*\?\>/,
					result = xmlString.from(testXmlString),
					expected = testXmlString;
				result = xmlString.to(result);
				result = result.replace(whitespaceRe, "");
				result = result.replace(xmlHeaderRe, "");
				expected = expected.replace(whitespaceRe, "");
				expected = expected.replace(xmlHeaderRe, "");
				assert.strictEqual(expected, result);
			}
		});
	});
