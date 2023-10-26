import unique from "wc/array/unique.mjs";

describe("wc/array/unique", function() {
	it("testArrayUnique", function () {
		const a1 = [0, 1, 2, 3, 4, 1, 8, 8, 8, 3, 8, 1, 0, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
			expectedResult = [0, 1, 2, 3, 4, 8, 5, 6, 7, 9],
			actual = unique(a1);
		expect(actual).toEqual(expectedResult);
	});
	it("testArrayUniqueArgNotModified", function () {
		const a1 = [0, 1, 2, 3, 4, 1, 8, 8, 8, 3, 8, 1, 0, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
			len = a1.length;
		unique(a1);
		expect(a1.length).toBe(len);
	});
	it("testArrayUniqueWithStrings", function () {
		const a1 = ["0", "1", "2", "3", "4", "1", "8", "8", "8", "3", "8", "1", "0", "5", "6", "7", "8", "9", "1", "2", "3", "4", "5", "6", "4", "3", "2", "8", "4", "2"],
			expectedResult = ["0", "1", "2", "3", "4", "8", "5", "6", "7", "9"],
			actual = unique(a1);
		expect(actual).toEqual(expectedResult);
	});
	it("testArrayUniqueWithMixed", function () {
		const a1 = [0, 1, 2, 3, 4, 1, 8, "8", 8, 3, 8, 1, "0", 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 4, 3, 2, 8, 4, 2],
			expectedResult = [0, 1, 2, 3, 4, 8, "8", "0", 5, 6, 7, 9],
			actual = unique(a1);
		expect(actual).toEqual(expectedResult);
	});
	/**
	 * unique should work on anything array-like (anything with a length and
	 * can be addressed using square bracket notation)
	 */
	it("testArrayUniqueWithArrayLike", function () {
		const a1 = "whykickamoocow",
			expectedResult = ["w", "h", "y", "k", "i", "c", "a", "m", "o"],
			// @ts-ignore
			actual = unique(a1);
		expect(actual).toEqual(expectedResult);
	});
	/**
	 * unique should not find any duplicates because each object is a different instance
	 */
	it("testArrayUniqueWithObjects", function () {
		const a1 = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
			expectedResult = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
			actual = unique(a1);
		expect(actual).toEqual(expectedResult);
	});
	/**
	 * With a little help from our comparison function unique can now find the duplicate objects
	 */
	it("testArrayUniqueWithObjects2", function () {
		const a1 = [{foo: "bar"}, {foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}, {foo: "foo"}],
			expectedResult = [{foo: "bar"}, {foo: "foo"}, {foo: "ding"}, {foo: "dang"}, {foo: "dong"}],
			actual = unique(a1, fooFinder);
		expect(actual).toEqual(expectedResult);

		function fooFinder(a, b) {
			if (a.foo === b.foo) {
				return 0;
			}
			return 1;
		}
	});
});
