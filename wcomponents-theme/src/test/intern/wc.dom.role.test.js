define(["intern!object", "intern/chai!assert", "wc/dom/role", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			roleId = "roletest-hasrole",
			testRole = "button",
			noRoleId = "roletest-norole",
			buttonId = "roletest-button",
			testContent = "<span id='" + roleId + "' role='" + testRole + "'>content</span>\n\
<span id='" + noRoleId + "'>content</span>\n\
<button type='button' id='" + buttonId + "'>button</button>";

		registerSuite({
			name: "wc/dom/role",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testGet_noElement: function() {
				assert.isUndefined(controller.get());
			},
			testGet_notElement: function() {
				assert.isUndefined(controller.get({}));
			},
			testGet_norole: function() {
				assert.isNull(controller.get(document.getElementById(noRoleId)));
			},
			testGet_norole_implied: function() {
				assert.isUndefined(controller.get(document.getElementById(noRoleId), true));
			},
			testGet_role: function() {
				assert.strictEqual(controller.get(document.getElementById(roleId)), testRole);
			},
			testGet_implied: function() {
				assert.strictEqual(controller.get(document.getElementById(buttonId), true), "button");
			},
			testHas_noElement: function() {
				assert.isFalse(controller.has());
			},
			testHas_notElement: function() {
				assert.isFalse(controller.has({}));
			},
			testHas_noRole: function() {
				assert.isFalse(controller.has(document.getElementById(noRoleId)));
			},
			testHas_role: function() {
				assert.isTrue(controller.has(document.getElementById(roleId)));
			},
			testHas_notImplied: function() {
				assert.isFalse(controller.has(document.getElementById(buttonId)));
			},
			testHas_implied: function() {
				assert.isTrue(controller.has(document.getElementById(buttonId), true));
			},
			testHas_impliedNoImplication: function() {
				assert.isFalse(controller.has(document.getElementById(noRoleId), true));
			}
		});
	}
);
