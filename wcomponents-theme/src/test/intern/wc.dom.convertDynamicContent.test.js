define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var convertDynamicContent, shed,
			CONVERTIBLES = ["[name]", "[data-wc-name]", "[data-wc-value]"].join(),
			HIDDEN_FIELDS = "input[type='hidden']",
			form,
			CONVERSION_TARGET_ID = "conversionTarget",
			testHolder,
			// @ts-ignore
			urlResource = require.toUrl("intern/resources/domConvertDynamicContent.html");

		registerSuite({
			name: "domConvertDynamicContent",
			setup: function() {
				return testutils.setupHelper(["wc/dom/convertDynamicContent", "wc/dom/shed"], function(c, s) {
					convertDynamicContent = c;
					shed = s;
				});
			},
			beforeEach: function() {
				return new Promise(function(win, lose) {
					testutils.loadResource(urlResource, function(response) {
						testHolder = testHolder || testutils.getTestHolder();
						testHolder.innerHTML = response;
						form = document.getElementById("abc123");
						win();
					}, lose);
				});
			},
			afterEach: function() {
				testHolder.innerHTML = "";
				form = null;
			},
			testBeforeConvert: function() {
				assert.isNotNull(form.querySelector("#notAField"));
			},
			testConvertRemovesNoneFormNodes: function() {
				convertDynamicContent(form);
				assert.isNull(form.querySelector("#notAField"));
			},
			testRemovalBeforeConvert: function() {
				assert.isNotNull(form.querySelector("#" + CONVERSION_TARGET_ID));
			},
			testConvertRemovesNoneFormNodesContainingFormNodes: function() {
				convertDynamicContent(form);
				assert.isNull(form.querySelector("#" + CONVERSION_TARGET_ID));
			},
			testConvertAllFormNodesBecomeHiddenFields: function() {
				var candidates = form.querySelectorAll(CONVERTIBLES), i, next, nextName, expected, target;
				convertDynamicContent(form);

				for (i = 0; i < candidates.length; ++i) {
					next = candidates[i];
					nextName = next.name || next.getAttribute("data-wc-name");
					expected = next.value || next.getAttribute("data-wc-value");
					target = form.querySelector("input[type = 'hidden'][name = '" + nextName + "']") || assert.fail("Did not find target with name " + nextName);
					assert.strictEqual(expected, target.value);
				}
			},
			testConvertTargetNotForm: function() {
				var expected = 0,
					target = document.getElementById(CONVERSION_TARGET_ID) || assert.fail("Cannot find target element, check test scripts"),
					candidates = target.querySelectorAll(CONVERTIBLES), i;
				for (i = 0; i < candidates.length; ++i) {
					if (!shed.isDisabled(candidates[i])) {
						expected++;
					}
				}
				convertDynamicContent(target);
				assert.strictEqual(expected, target.querySelectorAll(HIDDEN_FIELDS).length, "All non disabled candidates should be converted " + target.innerHTML);
			}
		});
	});
