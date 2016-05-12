define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var testHolder,
			impliedARIA,
			urlResource = "@RESOURCES@/domAria.html",
			ANY_SEL_STATE = "any";

		function doNativeStateTest(elements, state, notThisState) {
			var i, len, next;
			for (i = 0, len = elements.length; i < len; ++i) {
				next = elements[i];

				if (notThisState) {
					assert.isFalse(impliedARIA.supportsNativeState(next, state), "native state (" + state + ") returned unexpected result for " + next.id);
				}
				else {
					assert.isTrue(impliedARIA.supportsNativeState(next, state), "native state (" + state + ") returned unexpected result for " + next.id);
				}
			}
		}

		registerSuite({
			name: "impliedARIA",
			setup: function() {
				var result = testutils.setupHelper(["wc/dom/impliedARIA"]).then(function(arr) {
					impliedARIA = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				if (testHolder) {
					testHolder.innerHTML = "";
				}
			},
			testGetImpliedRole: function() {
				var i, len, next, container = document.getElementById("impliedRole"),
					expected;
				for (i = 0, len = container.children.length; i < len; ++i) {
					next = container.children[i];
					expected = next.value;
					if (expected) {
						assert.strictEqual(expected, impliedARIA.getImpliedRole(next), "Incorrect implied role on " + next.id);
					}
					if (next.selectedIndex && (next.options[next.selectedIndex].value)) {
						expected = next.options[next.selectedIndex].value;
						assert.strictEqual(expected, impliedARIA.getImpliedRole(next));
					}

					if (next.href && (next.getAttribute("href"))) {
						expected = next.getAttribute("href");
						assert.strictEqual(expected, impliedARIA.getImpliedRole(next));
					}
				}
			},
			testGetImpliedRoleNoRole: function() {
				var i, len, next, container = document.getElementById("no_role"),
					elements = container.getElementsByTagName("*"), result;
				for (i = 0, len = elements.length; i < len; ++i) {
					next = elements[i];
					result = impliedARIA.getImpliedRole(next);
					assert.isFalse(!!result, "element " + next.id + " should not have an implied role but actually got " + result);
				}
			},
			testNativeSelect: function() {
				doNativeStateTest(document.getElementById("selectable").getElementsByTagName("*"), "selected");
			},
			testNativeCheck: function() {
				doNativeStateTest(document.getElementById("checkable").getElementsByTagName("*"), "checked");
			},
			testNativeSelectCheckAny: function() {
				doNativeStateTest(document.getElementById("checkable").getElementsByTagName("*"), ANY_SEL_STATE);
				doNativeStateTest(document.getElementById("selectable").getElementsByTagName("*"), ANY_SEL_STATE);
			},
			testNativeSelectCheckAnyFalse: function() {
				doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), ANY_SEL_STATE, true);
				doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), "selected", true);
				doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), "checked", true);
			},
			testNativeRequired: function() {
				doNativeStateTest(document.getElementById("requireable").getElementsByTagName("*"), "required");
			},
			testNativeRequiredFalse: function() {
				doNativeStateTest(document.getElementById("not_requireable").getElementsByTagName("*"), "required", true);
			},
			testNativeDisabled: function() {
				doNativeStateTest(document.getElementById("disableable").getElementsByTagName("*"), "disabled");
			},
			testNativeDisabledFalse: function() {
				doNativeStateTest(document.getElementById("not_disableable").getElementsByTagName("*"), "disabled", true);
			}
		});
	});
