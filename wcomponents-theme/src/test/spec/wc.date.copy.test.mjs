import dateCopy from "wc/date/copy.mjs";

describe("wc/date/copy", function() {
	const FEBRUARY = 1,
		HOURS = 3,
		MINS = 4,
		SECS = 5,
		MILLIS = 6;

	it("copies the full date", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getDate()).toBe(testDate.getDate());
		expect(result.getFullYear()).toBe(testDate.getFullYear());
		expect(result.getHours()).toBe(testDate.getHours());
		expect(result.getMilliseconds()).toBe(testDate.getMilliseconds());
		expect(result.getMinutes()).toBe(testDate.getMinutes());
		expect(result.getMonth()).toBe(testDate.getMonth());
		expect(result.getSeconds()).toBe(testDate.getSeconds());
	});

	it("copies the full year", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getFullYear()).toBe(testDate.getFullYear());
	});

	it("copies the month", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getMonth()).toBe(testDate.getMonth());
	});

	it("copies the day of the month", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getDate()).toBe(testDate.getDate());
	});

	it("copies the hours", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getHours()).toBe(testDate.getHours());
	});

	it("copies the minutes", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getMinutes()).toBe(testDate.getMinutes());
	});

	it("copies the seconds", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getSeconds()).toBe(testDate.getSeconds());
	});

	it("copies the millis", function() {
		const testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		const result = dateCopy(testDate);
		expect(result.getMilliseconds()).toBe(testDate.getMilliseconds());
	});
});
