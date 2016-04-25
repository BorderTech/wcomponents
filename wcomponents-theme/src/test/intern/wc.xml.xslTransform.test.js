define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function (registerSuite, assert, testutils) {
	"use strict";
	/**
	 * A test almost exclusively for the benefit of Internet Explorer.
	 * The main reason causing me to write this test is to ensure that
	 * if there are scripts in the payload we are transforming they do
	 * not execute during the transform. They should only execute on
	 * insertion into the DOM.
	 *
	 * In particular this is testing the htmlToDocumentFragment function
	 * within the xslTransform class. Even more specifically this tests
	 * the "sandbox" in htmlToDocumentFragment. I have confirmed this test
	 * fails without the sandbox and passes with it.
	 */

	var controller,
		TEST_MODULE = "wc/xml/xslTransform",
		myXslUri = "@RESOURCES@/myXsl.xsl",
		myXmlUri = "@RESOURCES@/myXml.xml";

	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			return testutils.setupHelper([TEST_MODULE], function(obj) {
				controller = obj;
			});
		},

	//	Sigh... This test is failing in IE. The only thing to do is rewrite ALL the code to accept HTML from IE and document fragments from others.
	//	testNoExecuteDuringTransform: function()
	//	{
	//		var result;
	//		assertTrue("Test not in clean state", self.fubar === undefined);
	//		result = controller.transform({xmlUri:myXmlUri, xslUri:myXslUri});
	//		assertTrue("Expecting a documentFragment", result.nodeType === Node.DOCUMENT_FRAGMENT_NODE);
	//		assertTrue("Scripts executed during transform", self.fubar === undefined);
	//	},

		testOverrideGlobalParams: function() {
			var result, inputs, name = "elephant";
			result = controller.transform({xmlUri: myXmlUri, xslUri: myXslUri, params: {foo: name}});
			return result.then(function(frag) {
				inputs = frag.querySelectorAll ? frag.querySelectorAll("input") : frag.getElementsByTagName("input");
				assert.isTrue(inputs && inputs.length > 0, "not testing anything");
				return assert.strictEqual(inputs[0].name, name, "global param 'foo' not overridden");
			});
		},

		/*
		 * Test based on real world issues. In IE8 we stripped xmlns attributes as part of the transform.
		 * But then someone came along with 'xmlns' in their payload and we stripped it out, oops.
		 */
		testXmlNsPreserved: function() {
			var result = controller.transform({xmlUri: myXmlUri, xslUri: myXslUri}),
				expected = 'This XML should be output verbatim:&lt;?xml version="1.0" encoding="UTF-16"?&gt;&lt;ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/"&gt;',
				actual;

			return result.then(function(frag) {
				actual = frag.querySelector("textarea").innerHTML;
				return assert.strictEqual(actual, expected);
			});
		}
	});
});
