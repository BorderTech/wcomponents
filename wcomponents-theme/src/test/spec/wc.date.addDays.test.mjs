import addDays from "wc/date/addDays.mjs";

describe("wc/date/addDays", function() {
	const FEBRUARY = 1,
		HOURS = 3,
		MINS = 4,
		SECS = 5,
		MILLIS = 6;

	let testDate;

	beforeEach(function setUp() {
		// new Date(year, month, day, hours, minutes, seconds, milliseconds)
		testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
	});

	it("testAddDays", function () {
		expect(testDate.getDate()).toBe(12);
		addDays(12, testDate);
		expect(testDate.getDate()).toBe(24);
		expect(testDate.getHours()).toBe(HOURS);
		expect(testDate.getMinutes()).toBe(MINS);
		expect(testDate.getSeconds()).toBe(SECS);
		expect(testDate.getMilliseconds()).toBe(MILLIS);
	});

	it("testAddDaysLeapYear", function () {
		expect(testDate.getDate()).toBe(12);
		addDays(17, testDate);
		expect(testDate.getDate()).toBe(29);
		expect(testDate.getMonth()).toBe(FEBRUARY);
		expect(testDate.getHours()).toBe(HOURS);
		expect(testDate.getMinutes()).toBe(MINS);
		expect(testDate.getSeconds()).toBe(SECS);
		expect(testDate.getMilliseconds()).toBe(MILLIS);
	});

	it("testAddDaysNonLeapYear", function () {
		testDate.setFullYear(1969);
		expect(testDate.getDate()).toBe(12);
		addDays(17, testDate);
		expect(testDate.getDate()).toBe(1);
		expect(testDate.getMonth()).toBe(FEBRUARY + 1);
		expect(testDate.getHours()).toBe(HOURS);
		expect(testDate.getMinutes()).toBe(MINS);
		expect(testDate.getSeconds()).toBe(SECS);
		expect(testDate.getMilliseconds()).toBe(MILLIS);
	});
});
