define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var wcconfig;

		registerSuite({
			name: "wc/config",
			setup: function() {
				return testutils.setupHelper(["wc/config"], function(obj) {
					wcconfig = obj;
				});
			},
			testGetUnregistered: function() {
				var id = "wc/config/testGetUnregistered",
					actual = wcconfig.get(id);
				assert.isFalse(!!actual, "Should not return a value when not found in registry");
			},
			testGetUnregisteredWithDefaults: function() {
				var id = "wc/config/testGetUnregisteredWithDefaults",
					expected = {
						foo: "foo",
						bar: { baa: "baa" },
						fubar: ["f", "u", "b", "a", "r"]
					},
					actual = wcconfig.get(id, expected);
				assert.deepEqual(actual, expected, "Should return a clone of defaults when not found in registry");
			},
			testGetRegistered: function() {
				var id = "wc/config/testGetRegistered", actual,
					expected = {
						foo: "foo",
						bar: { baa: "baa" },
						fubar: ["f", "u", "b", "a", "r"]
					};
				wcconfig.set(expected, id);
				actual = wcconfig.get(id);
				assert.deepEqual(actual, expected, "Should return the registered configuration");
			},
			testGetRegisteredWithOverrides: function() {
				var id = "wc/config/testGetRegisteredWithOverrides", actual,
					defaults = {
						foo: 0,
						bar: { baa: "baa" },
						fubar: ["f", "u", "b", "a", "r"],
						boo: null
					},
					overrides = {
						test: "icicles",
						bar: { kung: "fu" },
						fubar: ["bart"]
					},
					expected = {
						foo: 0,
						test: "icicles",
						bar: { baa: "baa", kung: "fu" },
						fubar: ["bart"],
						boo: null
					};
				wcconfig.set(overrides, id);
				actual = wcconfig.get(id, defaults);
				assert.deepEqual(actual, expected, "Should return the registered configuration with overrides applied to defaults");
			},
			testSetKeepsDefaults: function() {
				var id = "wc/config/testSetKeepsDefaults",
					defaults = {
						foo: 0,
						bar: { baa: "baa" },
						fubar: ["f", "u", "b", "a", "r"],
						boo: null
					},
					replacements = {
						bar: {black: "sheep"},
						fubar: ["one", "two", "three"],
						kung: "foo"
					},
					expected = {
						foo: 0,
						bar: { black: "sheep"},
						fubar: ["one", "two", "three"],
						kung: "foo",
						boo: null
					};
				wcconfig.set(defaults, id);
				wcconfig.set(replacements, id);
				assert.deepEqual(wcconfig.get(id), expected);
			},
			testSetKeepsSomeDefaults: function() {
				var id = "wc/config/testSetKeepsSomeDefaults",
					defaults = {
						foo: 0,
						bar: { baa: "baa" },
						baa: {sheep: "dip"}
					},
					replacements = {
						bar: null,
						baa: {sheep: "ovine"}
					},
					expected = {
						foo: 0,
						bar: null,
						baa: {sheep: "ovine"}
					};
				wcconfig.set(defaults, id);
				wcconfig.set(replacements, id);
				assert.deepEqual(wcconfig.get(id), expected);
			}
		});
	});
