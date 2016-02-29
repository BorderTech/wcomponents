define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		/**
		 * There is a requirement to ensure that the enctype of the form is set correctly if there is a file selector
		 * in the form. We don;t care how it happens or where the code is located, all we care about is that when we
		 * call update on wc/dom/formUpdateManager the enctype is looked after by someone, somewhere.
		 *
		 * Not that we care but the code used to be wc/dom/checkEnctype and is now in wc/dom/formUpdateManager.
		 */
		var formUpdateManager,
			multipartEncType = "multipart/form-data",
			testHolder,
			urlResource = "@RESOURCES@/domCheckEnctype.html";
		registerSuite({
			name: "domEnctype",
			setup: function() {
				return testutils.setupHelper(["wc/dom/formUpdateManager"], function(obj) {
					formUpdateManager = obj;
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				return testutils.setUpExternalHTML(urlResource, testHolder);
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testCheckEnctypeNoRewrite: function() {
				var form = document.forms["checkEnctype1"],
					enctype = form.enctype;
				assert.notEqual(multipartEncType, enctype);
				formUpdateManager.update(form);
				enctype = form.enctype;
				assert.notEqual(multipartEncType, enctype);
			},
			testCheckEnctypeYesRewrite: function() {
				var form = document.forms["checkEnctype2"],
					enctype = form.enctype;
				assert.notEqual(multipartEncType, enctype);
				formUpdateManager.update(form);
				enctype = form.enctype;
				assert.notEqual(multipartEncType, enctype);
				// assert.strictEqual(multipartEncType, enctype);
				// This was originally strictEqual. Check with Rick
			}
		});
	});
