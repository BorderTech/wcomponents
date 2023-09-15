import isNumeric from "wc/isNumeric.mjs";

describe("wc/date/Format", function() {
	/* eslint-disable no-new-wrappers */

	it("testisNumericIntegerString", function() {
		const arg = "666",
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericFloatStringObject", function() {
		const arg = new String("666.666"),
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericFloatStringObjectNoMutation", function() {
		const arg = new String("666.666"),
			result = isNumeric(arg);
		expect(result).toBe(true);
		expect(arg.valueOf()).toBe("666.666");  // not mutated
	});

	it("testisNumericInteger", function() {
		const arg = 666,
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericFloat", function() {
		const arg = 666.666,
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericNegativeInteger", function() {
		const arg = -666,
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericNegativeStringFloat", function() {
		const arg = "-666.666",
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericNothing", function() {
		let arg, result = isNumeric(arg);
		expect(result).toBe(false);
	});

	it("testisNumericNonNumericString", function() {
		const arg = "xyz333",
			result = isNumeric(arg);
		expect(result).toBe(false);
	});

	it("testisNumericNumber", function() {
		const arg = new Number(-666),
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericNumberNoMutation", function() {
		const arg = new Number(-666),
			result = isNumeric(arg);
		expect(result).toBe(true);
		expect(arg.valueOf()).toBe(-666);  // not mutated
	});

	it("testisNumericNumber2", function() {
		const arg = new Number(666),
			result = isNumeric(arg);
		expect(result).toBe(true);
	});

	it("testisNumericNumber2NoMutation", function() {
		const arg = new Number(666),
			result = isNumeric(arg);
		expect(result).toBe(true);
		expect(arg.valueOf()).toBe(666);  // not mutated
	});
	/* eslint-enable no-new-wrappers */
});
