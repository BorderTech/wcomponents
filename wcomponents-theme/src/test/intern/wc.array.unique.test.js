define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
		function (registerSuite, assert, testutils) {
			"use strict";
			var controller;
			registerSuite({
				name: "ArrayUnique",
				setup: function() {
					return testutils.setupHelper(["wc/array/unique"]).then(function(arr) {
						controller = arr[0];
					});
				},
				testArrayUnique: function () {
					var a1 = [0, 1, 2, 3, 4, 1, 8, 8, 8, 3, 8, 1, 0, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
						expectedResult = [0, 1, 2, 3, 4, 8, 5, 6, 7, 9],
						actual = controller(a1);
					assert.deepEqual(expectedResult, actual);
				},
				testArrayUniqueArgNotModified: function () {
					var a1 = [0, 1, 2, 3, 4, 1, 8, 8, 8, 3, 8, 1, 0, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
						len = a1.length;
					controller(a1);
					assert.strictEqual(len, a1.length);
				},
				testArrayUniqueWithStrings: function () {
					var a1 = ["0", "1", "2", "3", "4", "1", "8", "8", "8", "3", "8", "1", "0", "5", "6", "7", "8", "9", "1", "2", "3", "4", "5", "6", "4", "3", "2", "8", "4", "2"],
						expectedResult = ["0", "1", "2", "3", "4", "8", "5", "6", "7", "9"],
						actual = controller(a1);
					assert.deepEqual(expectedResult, actual);
				},
				testArrayUniqueWithMixed: function () {
					var a1 = [0, 1, 2, 3, 4, 1, 8, "8", 8, 3, 8, 1, "0", 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
						expectedResult = [0, 1, 2, 3, 4, 8, "8", "0", 5, 6, 7, 9],
						actual = controller(a1);
					assert.deepEqual(expectedResult, actual);
				},
				/**
				 * unique should work on anything array-like (anything with a length and
				 * can be addressed using square bracket notation)
				 */
				testArrayUniqueWithArrayLike: function () {
					var a1 = "whykickamoocow",
						expectedResult = ["w", "h", "y", "k", "i", "c", "a", "m", "o"],
						actual = controller(a1);
					assert.deepEqual(expectedResult, actual);
				},
				/**
				 * unique should not find any duplicates because each object is a different instance
				 */
				testArrayUniqueWithObjects: function () {
					var a1 = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
						expectedResult = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
						actual = controller(a1);
					assert.deepEqual(expectedResult, actual);
				},
				/**
				 * With a little help from our comparison function unique can now find the duplicate objects
				 */
				testArrayUniqueWithObjects2: function () {
					var a1 = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
						expectedResult = [{foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}],
						actual = controller(a1, fooFinder);
					assert.deepEqual(expectedResult, actual);

					function fooFinder(a, b) {
						if (a.foo === b.foo) {
							return 0;
						}
						return 1;
					}
				}
			});
		});
