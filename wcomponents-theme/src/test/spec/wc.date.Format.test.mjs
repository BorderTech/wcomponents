import Format from "wc/date/Format.mjs";

describe("wc/date/Format", function() {

	it("testFormat", function() {
		const mask = "dd MMM yyyy",
			formatter = new Format(mask),
			date = "2000-01-01",
			expected = "01 Jan 2000",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatGB", function() {
		const mask = "dd/MM/yyyy",
			formatter = new Format(mask),
			date = "2000-02-03",
			expected = "03/02/2000",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatUS", function() {
		const mask = "MM/dd/yyyy",
			formatter = new Format(mask),
			date = "2000-02-03",
			expected = "02/03/2000",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatMissingPart", function() {
		const mask = "dd/MMM/yyyy",
			formatter = new Format(mask),
			date = "2000-??-01",
			expected = "01/ /2000",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatFullMonth", function() {
		const mask = "dd MMMM yyyy",
			formatter = new Format(mask),
			date = "1999-12-31",
			expected = "31 December 1999",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatYearOnly", function() {
		const mask = "yyyy",
			formatter = new Format(mask),
			date = "1999-12-31",
			expected = "1999",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatMonthOnly", function() {
		const mask = "MM",
			formatter = new Format(mask),
			date = "1999-12-31",
			expected = "12",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatDayOnly", function() {
		const mask = "d",
			formatter = new Format(mask),
			date = "1999-12-09",
			expected = "9",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime", function() {
		const mask = "dd/MM/yyyy HH:mm",
			formatter = new Format(mask),
			date = "2000-02-03T00:02:01",
			expected = "03/02/2000 00:02",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTimeWithSeconds", function() {
		const mask = "dd/MM/yyyy HH:mm:ss",
			formatter = new Format(mask),
			date = "2000-02-03T00:02:59",
			expected = "03/02/2000 00:02:59",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrAM", function() {
		const mask = "dd/MM/yyyy hh:mm",
			formatter = new Format(mask),
			date = "2000-02-03T00:02:01",
			expected = "03/02/2000 12:02",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrPM", function() {
		const mask = "dd/MM/yyyy hh:mm",
			formatter = new Format(mask),
			date = "2000-02-03T23:59:01",
			expected = "03/02/2000 11:59",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrShort", function() {
		const mask = "dd/MM/yyyy h:mm",
			formatter = new Format(mask),
			date = "2000-02-03T01:02:01",
			expected = "03/02/2000 1:02",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrLong", function() {
		const mask = "dd/MM/yyyy hh:mm",
			formatter = new Format(mask),
			date = "2000-02-03T01:02:01",
			expected = "03/02/2000 01:02",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrAAAM", function() {
		const mask = "dd/MM/yyyy hh:mm a",
			formatter = new Format(mask),
			date = "2000-02-03T00:02:01",
			expected = "03/02/2000 12:02 AM",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
	it("testFormatTime12HrAAPM", function() {
		const mask = "dd/MM/yyyy hh:mm a",
			formatter = new Format(mask),
			date = "2000-02-03T12:00:01",
			expected = "03/02/2000 12:00 PM",
			actual = formatter.format(date);
		expect(actual).toBe(expected);
	});
});
