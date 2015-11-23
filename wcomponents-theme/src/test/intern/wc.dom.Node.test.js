define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var testHolder;

		registerSuite({
			name: "domNode",
			setup: function() {
				return testutils.setupHelper([], function() {
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				testHolder.innerHTML = "<div id=\"elephant\">i want a crocodile</div>";
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testInterface: function() {
				/**
				* Tests the following:
				* - Node is implemented either natively by the browser OR it is added by our compatibility code
				* - Node presents the interface we are expecting
				* - The right elements match the right node types
				*
				* Note that not all node types can be tested in HTML mode, some are only reported
				* correctly in XML mode (eg CDATA).
				*/
				assert.isDefined(Node.ELEMENT_NODE);
				assert.isDefined(Node.ATTRIBUTE_NODE);
				assert.isDefined(Node.TEXT_NODE);
				assert.isDefined(Node.CDATA_SECTION_NODE);
				assert.isDefined(Node.ENTITY_REFERENCE_NODE);
				assert.isDefined(Node.ENTITY_NODE);
				assert.isDefined(Node.PROCESSING_INSTRUCTION_NODE);
				assert.isDefined(Node.COMMENT_NODE);
				assert.isDefined(Node.DOCUMENT_NODE);
				assert.isDefined(Node.DOCUMENT_TYPE_NODE);
				assert.isDefined(Node.DOCUMENT_FRAGMENT_NODE);
				assert.isDefined(Node.NOTATION_NODE);
			},
			testElementNode: function() {
				var node = document.getElementById("elephant");
				assert.strictEqual(Node.ELEMENT_NODE, node.nodeType);
			},
			testAttributeNode: function() {
				var node = document.getElementById("elephant").getAttributeNode("id");
				assert.strictEqual(Node.ATTRIBUTE_NODE, node.nodeType);
			},
			testTextNode: function() {
				var node = document.getElementById("elephant").firstChild;
				assert.strictEqual(Node.TEXT_NODE, node.nodeType);
			},
			testComment: function() {
				var comment = document.createComment("I want an elephant"),
					elephant = document.getElementById("elephant"),
					node;
				elephant.parentNode.appendChild(comment);
				node = elephant.nextSibling;
				assert.strictEqual(Node.COMMENT_NODE, node.nodeType);
			},
			testDocumentNode: function() {
				assert.strictEqual(Node.DOCUMENT_NODE, document.nodeType);
			},
			testDocumentFragment: function() {
				var node = document.createDocumentFragment();
				assert.strictEqual(Node.DOCUMENT_FRAGMENT_NODE, node.nodeType);
			}
		});
	});
