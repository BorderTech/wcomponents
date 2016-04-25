define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var has, testHolder;

		registerSuite({
			name: "domGetElementsByName",
			setup: function() {
				return testutils.setupHelper(["wc/has"], function(obj) {
					has = obj;
					testHolder = testutils.getTestHolder();
					testHolder.innerHTML = "<form><input name='animal' value='woof' /> <input name='animal' value='woof' id='dog1' /><input name='dog1' value='woof' /></form>";
				});
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetElementsByNameBasic: function() {
				var animals = document.getElementsByName("animal");
				assert.strictEqual(2, animals.length);
			},
			testGetElementsByNameStrict: function() {
				var dogs = document.getElementsByName("dog1");
				assert.strictEqual(1, dogs.length);
			},
			testSelective: function() {
				var gebnToString,
					isNativeRe = /\[native code\]/;

				if (!has("ie")) {
					gebnToString = document.getElementsByName.toString();
					assert.isTrue(isNativeRe.test(gebnToString), "Do not override native methods that don't need it");
				}
				else {
					assert.isFalse(isNativeRe.test(gebnToString), "IE below 10 should have a custom getElementsByName");
				}
			}
		});
	});
