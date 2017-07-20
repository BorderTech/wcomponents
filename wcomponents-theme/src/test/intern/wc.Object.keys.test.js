define(["intern!object", "intern/chai!assert"],
	function (registerSuite, assert) {
		"use strict";
		registerSuite({
			name: "ObjectKeys",
			testKeysWithArray: function() {
				var arr = ["a", "b", "c"],
					expected = ["0", "1", "2"];
				assert.deepEqual(expected, Object.keys(arr));
			},
			testKeysWithObject: function() {
				var obj = {0: "a", 1: "b", 2: "c"},
					expected = ["0", "1", "2"];
				assert.deepEqual(expected, Object.keys(obj));
			}
		});
	});
