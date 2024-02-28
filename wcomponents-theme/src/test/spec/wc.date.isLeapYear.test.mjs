import isLeapYear from "wc/date/isLeapYear.mjs";

describe("wc/date/isLeapYear", function() {
	it("returns true for valid leap years", function () {
		let leapYears = [1904, 1988, 2000, 2024, 2160, 2596, 3996];
		for (let i = 0; i < leapYears.length; i++)
			expect(isLeapYear(leapYears[i])).toBeTrue();
	});

	it("returns false for non-leap years", function () {
		let nonLeapYears = [1900, 1963, 2002, 2026, 2543, 2634, 3000];
		for (let i = 0; i < nonLeapYears.length; i++)
			expect(isLeapYear(nonLeapYears[i])).toBeFalse();
	});
});
