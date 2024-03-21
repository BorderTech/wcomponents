import asciify from "wc/i18n/asciify.mjs";

describe("wc/i18n/asciify", function() {

	it("testAsciifyWithUniChar", function() {
		const input = "\u00e9",
			expected = "e",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithAsciiChar", function() {
		const input = "e",
			expected = "e",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithNumericChar", function() {
		const input = "0",
			expected = "0",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithUnmappedUniChar", function() {
		const input = "\u5047",
			expected = "\u5047",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithEmptyString", function() {
		const input = "",
			expected = "",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithUniString", function() {
		const input = " \u00e0\u00e2\u00e4 \u00e8\u00e9\u00ea\u00eb \u00ee\u00ef \u00f4 \u00f9\u00fb\u00fc ",
			expected = " aaa eeee ii o uuu ",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithAsciiString", function() {
		const input = "aaa eeee ii o uuu",
			expected = "aaa eeee ii o uuu",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithNumericString", function() {
		const input = "0123456789",
			expected = "0123456789",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithUnmappedUniString", function() {
		const input = "\u5f62\u58f0\u5b57 / \u5f62\u8072\u5b57",
			expected = "\u5f62\u58f0\u5b57 / \u5f62\u8072\u5b57",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

	it("testAsciifyWithMixedString", function() {
		const input = "0b\u00e0\u00e2\u00e4b\u00e8\u00e9\u00ea\u00ebh\u00ee\u00efy\u00f4\u00f9\u00fb\u00fc\u5047",
			expected = "0baaabeeeehiiyouuu\u5047",
			actual = asciify(input);
		expect(actual).toBe(expected);
	});

});
