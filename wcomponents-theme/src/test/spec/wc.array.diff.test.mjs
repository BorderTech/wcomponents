import diff from "wc/array/diff.mjs";

describe("wc/array/diff", function() {
	
	it("testDiff", function() {
		const expected = [1, 2],
			actual = diff([1, 2, 3], [3, 4, 5]);
		expect(actual).toEqual(expected);
	});
	
	it("testSame", function() {
		const actual = diff([1, 2, 3], [1, 2, 3]);
		expect(Array.isArray(actual)).toBeTrue();
		expect(actual.length).toBe(0);
	});
	
	it("testNothingToDiff", function() {
		const actual = diff();
		expect(Array.isArray(actual)).toBeTrue();
		expect(actual.length).toBe(0);
	});
	
	it("testNotArray", function() {
		const actual = diff([1, 2, 3], {});
		expect(Array.isArray(actual)).toBeTrue();
		expect(actual.length).toBe(0);
	});
	
	it("testSubset", function() {
		const actual = diff([1, 2, 3], [1, 2, 3, 4, 5]);
		expect(Array.isArray(actual)).toBeTrue();
		expect(actual.length).toBe(0);
	});
	
	it("testDifferentOrderStillSame", function() {
		var actual = diff([1, 2, 3], [3, 1, 2]);
		expect(Array.isArray(actual)).toBeTrue();
		expect(actual.length).toBe(0);
	});
});
