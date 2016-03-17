/*
 * IF YOU CHANGE ANY OF THESE TESTS YOU MUST CONFIRM THEM AGAINST A NATIVE
 * TREEWALKER IMPLEMENTATION!!!
 * I.E. run the tests on Firefox/Opera and make sure they pass!
 *
 * Adapted by Rick Brown 23/12/2009 from the code here:
 * http://www.koders.com/javascript/fidC8A68BA991D5D546F54B4794109CD5A2AA6C644A.aspx?s=%22james%22#L10
 * Original licence follows:
 */
/* ***** BEGIN LICENSE BLOCK *****
 * Licensed under Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * Full Terms at http://mozile.mozdev.org/0.8/LICENSE
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is James A. Overton's code (james@overton.ca).
 *
 * The Initial Developer of the Original Code is James A. Overton.
 * Portions created by the Initial Developer are Copyright (C) 2005-2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *	James A. Overton <james@overton.ca>
 *
 * ***** END LICENSE BLOCK ***** */
define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";


	var CONTAINER_ID = "treewalkerTestContainerId",
		ID_1 = "treewalkerTestId1",
		ID_2 = "treewalkerTestId2",
		testHolder, urlResource = "@RESOURCES@/domTreeWalker.html";

	/*
	 * Helper for a couple of the tests
	 */
	function getFirstChild(element) {
		var result;
		result = element.firstChild;
		while (result && result.nodeType !== Node.ELEMENT_NODE) {
			result = result.nextSibling;
		}
		return result;
	}
	registerSuite({
		name: "Treewalker",
		setup: function() {
			var result = testutils.setupHelper([]).then(function() {
				return (testHolder = testutils.getTestHolder());

			}).then(function(testHolder) {
				return testutils.setUpExternalHTML(urlResource, testHolder);
			});
			return result;
		},
		teardown: function() {
			testHolder.innerHTML = "";
		},

		testInit: function() {
			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);
			assert.strictEqual(typeof treeWalker, "object", "Treewalker constructor should create a treeWalker object");
		},

		testNavigateAllFirstChild: function() {
			var target = document.getElementById("target"),
				treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);

			treeWalker.currentNode = target;

			treeWalker.firstChild();
			assert.equal(treeWalker.currentNode, target.firstChild, "firstChild is text");
		},
		testNavigateAllNextSibling: function() {
			var target = document.getElementById("target"),
				treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);

			treeWalker.currentNode = target;

			treeWalker.firstChild();  // precondition
			treeWalker.nextSibling();
			assert.equal(treeWalker.currentNode, target.childNodes[1], "nextSibling is span");
		},
		testNavigateAllLastChild: function() {
			var target = document.getElementById("target"),
				treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);

			treeWalker.currentNode = target;
			treeWalker.firstChild();
			treeWalker.nextSibling();  // preconditions
			treeWalker.lastChild();
			assert.equal(treeWalker.currentNode, target.childNodes[1].lastChild, "lastChild is span's text");
		},
		testNavigateAllParentNode: function() {
			var target = document.getElementById("target"),
				treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);

			treeWalker.currentNode = target;
			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.lastChild();  // preconditions
			treeWalker.parentNode();
			assert.equal(treeWalker.currentNode, target.childNodes[1], "parentNode is span");
		},
		testNavigateAllPreviousSibling: function() {
			var target = document.getElementById("target"),
				treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);

			treeWalker.currentNode = target;
			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.lastChild();
			treeWalker.parentNode();  // preconditions
			treeWalker.previousSibling();
			assert.equal(treeWalker.currentNode, target.firstChild, "previousSibling is text");
		},
		testNavigateElementsFirstChild: function() {
			var container = document.getElementById(CONTAINER_ID);

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = container;

			treeWalker.firstChild();
			assert.equal(treeWalker.currentNode, getFirstChild(container), "firstChild is p");
		},
		testNavigateElementsNextSibling: function() {
			var target = document.getElementById("target"),
				container = document.getElementById(CONTAINER_ID);

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = container;

			treeWalker.firstChild();
			treeWalker.nextSibling();
			assert.equal(treeWalker.currentNode, target, "nextSibling is target");
		},
		testNavigateElementsLastChild: function() {
			var target = document.getElementById("target5"),
				container = document.getElementById(CONTAINER_ID);

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = container;

			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.lastChild();
			assert.equal(treeWalker.currentNode, target, "lastChild is target5");
		},
		testNavigateElementsParentNode: function() {
			var target = document.getElementById("target"),
				container = document.getElementById(CONTAINER_ID);

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = container;

			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.lastChild();
			treeWalker.parentNode();
			assert.equal(treeWalker.currentNode, target, "parentNode is span");
		},
		testNavigateElementsPreviousSibling: function() {
			var container = document.getElementById(CONTAINER_ID);

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = container;

			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.lastChild();
			treeWalker.parentNode();
			treeWalker.previousSibling();
			assert.equal(treeWalker.currentNode, getFirstChild(container), "previousSibling is text");
		},
		testNavigateTextFirstChild: function() {
			var target = document.getElementById("target");

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_TEXT, null, false);
			treeWalker.currentNode = target;

			treeWalker.firstChild();
			assert.equal(treeWalker.currentNode, target.firstChild, "firstChild is text");
		},
		testNavigateText: function() {
			var target = document.getElementById("target");

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_TEXT, null, false);
			treeWalker.currentNode = target;
			treeWalker.firstChild();
			treeWalker.nextSibling();
			treeWalker.previousSibling();
			assert.equal(treeWalker.currentNode, target.firstChild, "previousSibling is text");
		},
		testWalkAllNext: function() {
			var target = document.getElementById("target");

			var nodes = [
				target, target.childNodes[0], target.childNodes[1], target.childNodes[1].firstChild,
				target.childNodes[2], target.childNodes[3], target.childNodes[3].firstChild, target.childNodes[4],
				target.childNodes[5], target.childNodes[6], target.childNodes[7], target.childNodes[7].firstChild,
				target.childNodes[7].firstChild.firstChild, target.childNodes[8]];

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);
			treeWalker.currentNode = nodes[0];

			// Next
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Next node " + i + " should match the currentNode");
			}
		},
		testWalkAllPrevious: function() {
			var target = document.getElementById("target");

			var nodes = [
				target, target.childNodes[0], target.childNodes[1], target.childNodes[1].firstChild,
				target.childNodes[2], target.childNodes[3], target.childNodes[3].firstChild, target.childNodes[4],
				target.childNodes[5], target.childNodes[6], target.childNodes[7], target.childNodes[7].firstChild,
				target.childNodes[7].firstChild.firstChild, target.childNodes[8]];

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ALL, null, false);
			treeWalker.currentNode = nodes[0];

			// Next - just to get to the end
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
			}

			// Previous
			for (i = nodes.length - 2; i >= 0; i--) {
				treeWalker.previousNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Previous node " + i + " should match the currentNode");
			}
		},
		testWalkElementsNext: function() {
			var target = document.getElementById("target");

			var nodes = [
				target, target.childNodes[1], target.childNodes[3], target.childNodes[5], target.childNodes[7], target.childNodes[7].firstChild];
			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = nodes[0];

			// Next
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Next node " + i + " should match the currentNode");
			}
		},
		testWalkElementsPrevious: function() {
			var target = document.getElementById("target");

			var nodes = [
				target, target.childNodes[1], target.childNodes[3], target.childNodes[5], target.childNodes[7], target.childNodes[7].firstChild];
			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, null, false);
			treeWalker.currentNode = nodes[0];
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
			}
			// Previous
			for (i = nodes.length - 2; i >= 0; i--) {
				treeWalker.previousNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Previous node " + i + " should match the currentNode");
			}
		},
		testWalkTextNext: function() {
			var target = document.getElementById("target");

			var nodes = [
				target.childNodes[0], target.childNodes[1].firstChild, target.childNodes[2], target.childNodes[3].firstChild, target.childNodes[4], target.childNodes[6], target.childNodes[7].firstChild.firstChild, target.childNodes[8]];

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_TEXT, null, false);
			treeWalker.currentNode = nodes[0];

			// Next
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Next node " + i + " should match the currentNode");
			}
		},
		testWalkTextPrevious: function() {
			var target = document.getElementById("target");

			var nodes = [
				target.childNodes[0], target.childNodes[1].firstChild, target.childNodes[2], target.childNodes[3].firstChild, target.childNodes[4], target.childNodes[6], target.childNodes[7].firstChild.firstChild, target.childNodes[8]];

			var treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_TEXT, null, false);
			treeWalker.currentNode = nodes[0];

			// Next
			for (var i = 1; i < nodes.length; i++) {
				treeWalker.nextNode();
			}
			// Previous
			for (i = nodes.length - 2; i >= 0; i--) {
				treeWalker.previousNode();
				assert.equal(treeWalker.currentNode, nodes[i], "Previous node " + i + " should match the currentNode");
			}
		},

		/**
		 * Test for a bug in the IE polyfill:
		 * 	when calling previousNode and we have a backtrack which has child elements
		 *	this backtrack should be returned if the child elements are all skipped.
		 */
		testPreviousNodeBackTrack: function() {
			var treeWalker,
				element = document.getElementById(ID_1),
				expected = document.getElementById(ID_2),
				filter = function(el) {
					var result = NodeFilter.FILTER_SKIP;
					if (el.tagName.toLowerCase() === "ul") {
						result = NodeFilter.FILTER_ACCEPT;
					}
					return result;
				};

			treeWalker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_ELEMENT, filter, false);

			treeWalker.currentNode = element;
			treeWalker.previousNode();
			assert.equal(treeWalker.currentNode, expected, "Previous node should match the currentNode");
		}
	});
});
