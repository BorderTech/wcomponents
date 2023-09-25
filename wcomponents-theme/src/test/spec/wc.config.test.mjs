import wcconfig from "wc/config.mjs";

describe("wc/config", () => {
	it("testGetUnregistered", function() {
		const id = "wc/config/testGetUnregistered",
			actual = wcconfig.get(id);
		expect(actual).withContext("Should not return a value when not found in registry").toBeFalsy();
	});

	it("testGetUnregisteredWithDefaults", function() {
		const id = "wc/config/testGetUnregisteredWithDefaults",
			expected = {
				foo: "foo",
				bar: { baa: "baa" },
				fubar: ["f", "u", "b", "a", "r"]
			},
			actual = wcconfig.get(id, expected);
		expect(actual).withContext("Should return a clone of defaults when not found in registry").toEqual(expected);
	});

	it("testGetRegistered", function() {
		const id = "wc/config/testGetRegistered",
			expected = {
				foo: "foo",
				bar: { baa: "baa" },
				fubar: ["f", "u", "b", "a", "r"]
			};
		wcconfig.set(expected, id);
		const actual = wcconfig.get(id);
		expect(actual).withContext("Should return the registered configuration").toEqual(expected);
	});

	it("testGetRegisteredWithOverrides", function() {
		const id = "wc/config/testGetRegisteredWithOverrides",
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
		const actual = wcconfig.get(id, defaults);
		expect(actual).withContext("Should return the registered configuration with overrides applied to defaults").toEqual(expected);
	});

	it("testSetKeepsDefaults", function() {
		const id = "wc/config/testSetKeepsDefaults",
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
		expect(wcconfig.get(id)).toEqual(expected);
	});

	it("testSetKeepsSomeDefaults", function() {
		const id = "wc/config/testSetKeepsSomeDefaults",
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
		expect(wcconfig.get(id)).toEqual(expected);
	});
});

