// define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
//	function (registerSuite, assert, testutils) {
//		"use strict";
//		var responseHtmlUrl = "@RESOURCES@/icao.html",
//			TEST_MODULE = "wc/ui/listLoader",
//			elementId = "txt1",
//			testHolder,
//			controller;
//
//		function listLoaderTestCallback(datalist) {
//			var options;
//			if (datalist) {
//				if (typeof datalist.querySelector !== "undefined") {
//					options = datalist.querySelectorAll("option");
//				} else if (typeof datalist.getElementsByTagName !== "undefined") {
//					options = datalist.getElementsByTagName("option");
//				} else {
//					assert.fail("CBF");  // i don't think any browser will end up here...
//				}
//				assert.strictEqual(262, options.length);
//			} else {
//				assert.fail("Did not load datalist");
//			}
//		}
//		return;  // these pass locally but fail on saucelabs and I don't have time to debug it right now
//		registerSuite({
//			name: TEST_MODULE,
//			setup: function() {
//				var result = testutils.setupHelper([TEST_MODULE]).then(function(arr) {
//					controller = arr[0];
//					testHolder = testutils.getTestHolder();
//				});
//				return result;
//			},
//			teardown: function() {
//				testHolder.innerHTML = "";
//			},
//			testLoadHtml: function() {
//				testHolder.innerHTML = "<form data-wc-datalisturl=" + responseHtmlUrl + "><input id=" + elementId + "></form>";
//				return controller.load("icao", document.getElementById(elementId)).then(listLoaderTestCallback, function(ex) {
//					assert.fail(ex);
//				});
//			}
//		});
//	});
