define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function(registerSuite, assert, testutils) {
		"use strict";
		var controller,
			urlResource = "../../target/test-classes/wcomponents-theme/intern/resources/domUsefulDom.html",
			testHolder;

		registerSuite({
			name: "toArray",
			setup: function() {
				var result = new testutils.LamePromisePolyFill();
				testutils.setupHelper(["wc/array/toArray"], function(obj) {
					controller = obj;
					testHolder = testutils.getTestHolder();
					testutils.setUpExternalHTML(urlResource, testHolder).then(result._resolve);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testToArrayWithNodeListHasNodeListToTest: function() {/* test the precondition */
				var nodeList = testHolder.getElementsByTagName("input");
				assert.isTrue(nodeList.length > 0, "Not testing anything!");
			},
			testToArrayWithNodeList: function() {
				var nodeList = testHolder.getElementsByTagName("input"),
					result = controller(nodeList);
				assert.isTrue(Array.isArray(result));
			},
			testToArrayWithNodeListCorrectLength: function() {
				var nodeList = testHolder.getElementsByTagName("input"),
					result = controller(nodeList);
				assert.strictEqual(result.length, nodeList.length, "Result should be the same length");
			},
			testToArrayWithNodeListCorrectOrder: function() {
				var nodeList = testHolder.getElementsByTagName("input"), i,
					result = controller(nodeList);
				for (i = 0; i < result.length; i++) {
					assert.isTrue(nodeList[i] === result[i], "Result should be in the same order");
				}
			},
			testToArrayWithEmptyNodeListPrecondition: function() {/* test the precondition we do not have a populated nodeList to test */
				var nodeList = testHolder.getElementsByTagName("foobar");
				assert.isTrue(nodeList.length === 0, "Not testing the right thing!");
			},
			testToArrayWithEmptyNodeList: function() {
				var nodeList = testHolder.getElementsByTagName("foobar"),
					result = controller(nodeList);
				assert.isTrue(Array.isArray(result));
			},
			testToArrayWithEmptyNodeListCorrectLength: function() {
				var nodeList = testHolder.getElementsByTagName("foobar"),
					result = controller(nodeList);
				assert.strictEqual(result.length, nodeList.length, "Result should be the same length");
			}
		});
	});
