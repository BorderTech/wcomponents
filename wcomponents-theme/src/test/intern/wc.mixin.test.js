define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var mixin;

		registerSuite({
			name: "wc/mixin",
			setup: function() {
				return testutils.setupHelper(["wc/mixin"], function(obj) {
					mixin = obj;
				});
			},
			testSimple: function() {
				var source = {
						foo: "bar",
						sheep: {
							species: "ovine",
							noise: "baa"
						}
					},
					target = {
						foo: "kung",
						num: 7,
						sheep: {
							noise: "maaaa",
							flavour: "delicious"
						}
					},
					expected = {
						foo: "bar",
						num: 7,
						sheep: {
							species: "ovine",
							noise: "baa",
							flavour: "delicious"
						}
					},
					actual = mixin(source, target);
				assert.deepEqual(actual, expected, "expected: " + JSON.stringify(expected) + ", but got: " + JSON.stringify(actual));
			},
			testNoTarget: function() {
				var source = {
						foo: "bar",
						num: 7,
						sheep: {
							species: "ovine",
							noise: "baa",
							flavour: "delicious"
						}
					},
					actual = mixin(source);
				assert.deepEqual(actual, source);
			},
			testNoTargetNotSource: function() {
				var source = {
						foo: "bar",
						num: 7,
						sheep: {
							species: "ovine",
							noise: "baa",
							flavour: "delicious"
						}
					},
					actual = mixin(source);
				assert.notEqual(actual, source);
			},
			testNoSourceReturnsTargetUnharmed: function() {
				var target = {
						foo: "bar",
						num: 7,
						sheep: {
							species: "ovine",
							noise: "baa",
							flavour: "delicious"
						}
					},
					actual = mixin(null, target);
				assert.strictEqual(actual, target);
			},
			testNoSourceNoTarget: function() {
				assert.isObject(mixin());
			},
			testNoSourceNoTargetRReturnsEmpty: function() {
				assert.deepEqual(mixin(), {}); // want to use assert.isEmpty when we upgrade intern/chai
			},
			testShallow: function() {
				var source = {
						foo: "bar",
						sheep: {
							species: "ovine",
							flavour: "delicious"
						}
					},
					target = {
						foo: "kung",
						num: 7,
						sheep: {
							noise: "maaaa"
						}
					},
					expected = {
						foo: "bar",
						sheep: {
							species: "ovine",
							flavour: "delicious"
						},
						num: 7
					};
				assert.deepEqual(mixin(source, target, true), expected);
			}
		});
	});
