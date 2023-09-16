import daysInMonth from "wc/date/daysInMonth.mjs";

describe("wc/date/daysInMonth", function() {
	it("knows the days in the month in a regular year", function() {
		expect(daysInMonth(1969, 1)).toBe(31);
		expect(daysInMonth(1969, 2)).toBe(28);
		expect(daysInMonth(1969, 3)).toBe(31);
		expect(daysInMonth(1969, 4)).toBe(30);
		expect(daysInMonth(1969, 5)).toBe(31);
		expect(daysInMonth(1969, 6)).toBe(30);
		expect(daysInMonth(1969, 7)).toBe(31);
		expect(daysInMonth(1969, 8)).toBe(31);
		expect(daysInMonth(1969, 9)).toBe(30);
		expect(daysInMonth(1969, 10)).toBe(31);
		expect(daysInMonth(1969, 11)).toBe(30);
		expect(daysInMonth(1969, 12)).toBe(31);
	});
	it("knows the days in the month in a leap year", function() {
		expect(daysInMonth(1968, 1)).toBe(31);
		expect(daysInMonth(1968, 2)).toBe(29);
		expect(daysInMonth(1968, 3)).toBe(31);
		expect(daysInMonth(1968, 4)).toBe(30);
		expect(daysInMonth(1968, 5)).toBe(31);
		expect(daysInMonth(1968, 6)).toBe(30);
		expect(daysInMonth(1968, 7)).toBe(31);
		expect(daysInMonth(1968, 8)).toBe(31);
		expect(daysInMonth(1968, 9)).toBe(30);
		expect(daysInMonth(1968, 10)).toBe(31);
		expect(daysInMonth(1968, 11)).toBe(30);
		expect(daysInMonth(1968, 12)).toBe(31);
	});
});
