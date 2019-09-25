/* eslint-env node, es6  */
const { registerSuite } = intern.getPlugin("interface.object");
const { requireRoot } = require("./util");
const buildUtil = requireRoot("scripts/build-util");
const { assert } = intern.getPlugin("chai");
let mixin;

registerSuite("wc/mixin", {
	before: function() {
		return new Promise(function (win, lose) {
			buildUtil.requireAmd(["wc/mixin"], function(obj) {
				try {
					mixin = obj;
					win();
				} catch (ex) {
					lose(ex);
				}
			});
		});
	},
	tests: {
		"simple": function() {
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
		"no target": function() {
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
		"no target doesn't return source": function() {
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
		"no source returns target unharmed": function() {
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
		"no source, no target": function() {
			assert.isObject(mixin());
		},
		"NoSourceNoTargetRReturnsEmpty": function() {
			assert.deepEqual(mixin(), {}); // want to use assert.isEmpty when we upgrade intern/chai
		},
		"Shallow": function() {
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
	}
});
