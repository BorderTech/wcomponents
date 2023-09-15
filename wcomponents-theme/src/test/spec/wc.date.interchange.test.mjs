import interchange from "wc/date/interchange.mjs";
import today from "wc/date/today.mjs";

describe("wc/string/escapeRe", function() {
	it("testFromDate", function () {
		const date = new Date(2000, 0, 1),
			expected = "2000-01-01",
			actual = interchange.fromDate(date);
		expect(actual).toBe(expected);
	});
	it("testToDate", function () {
		const date = "2000-12-31",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(2000);
		expect(actual.getMonth()).toBe(11);
		expect(actual.getDate()).toBe(31);
	});
	it("testToDateWithNoSeparators", function () {
		const date = "20000101",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(2000);
		expect(actual.getMonth()).toBe(0);
		expect(actual.getDate()).toBe(1);
	});
	it("testToDateNoYear", function () {
		const now = today.get(),
			date = "????-01-01",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(now.getFullYear());
		expect(actual.getMonth()).toBe(0);
		expect(actual.getDate()).toBe(1);
	});
	it("testToDateNoMonth", function () {
		const date = "1999-??-31",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(1999);
		expect(actual.getMonth()).toBe(0);
		expect(actual.getDate()).toBe(31);
	});
	it("testToDateNoDay", function () {
		const date = "1999-12-??",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(1999);
		expect(actual.getMonth()).toBe(11);
		expect(actual.getDate()).toBe(1);
	});
	it("testToDateNoMonthNoDay", function () {
		const date = "1999-??-??",
			actual = interchange.toDate(date);
		expect(actual.getFullYear()).toBe(1999);
		expect(actual.getMonth()).toBe(0);
		expect(actual.getDate()).toBe(1);
	});
	it("testFromValuesWithNumbers", function () {
		const date = { year: 2000, month: 1, day: 1 },
			expected = "2000-01-01",
			actual = interchange.fromValues(date);
		expect(actual).toBe(expected);
	});
	it("testFromValuesWithStrings", function () {
		const date = {year: "2000", month: "01", day: "01"},
			expected = "2000-01-01",
			actual = interchange.fromValues(date);
		expect(actual).toBe(expected);
	});
	it("testToValues", function () {
		const date = "1999-12-31",
			actual = interchange.toValues(date);
		expect(actual.year).toBe("1999");
		expect(actual.month).toBe("12");
		expect(actual.day).toBe("31");
	});
	it("testToValuesNoYear", function () {
		const date = "????-12-31",
			actual = interchange.toValues(date);
		expect(actual.year).toBeNull();
		expect(actual.month).toBe("12");
		expect(actual.day).toBe("31");
	});
	it("testToValuesNoMonth", function () {
		const date = "1999-??-31",
			actual = interchange.toValues(date);
		expect(actual.year).toBe("1999");
		expect(actual.month).toBeNull();
		expect(actual.day).toBe("31");
	});
	it("testToValuesNoDay", function () {
		const date = "1999-06-??",
			actual = interchange.toValues(date);
		expect(actual.year).toBe("1999");
		expect(actual.month).toBe("06");
		expect(actual.day).toBeNull();
	});
	it("testToValuesNoYearNoDay", function () {
		const date = "????-06-??",
			actual = interchange.toValues(date);
		expect(actual.year).toBeNull();
		expect(actual.month).toBe("06");
		expect(actual.day).toBeNull();
	});
	it("testToValuesWithNoSeparators", function () {
		const date = "19991231",
			actual = interchange.toValues(date);
		expect(actual.year).toBe("1999");
		expect(actual.month).toBe("12");
		expect(actual.day).toBe("31");
	});
	it("testIsComplete", function () {
		const date = "2000-01-01";
		expect(interchange.isComplete(date)).toBeTrue();
	});
	it("testIsCompleteNoSeparators", function () {
		const date = "20000101";
		expect(interchange.isComplete(date)).toBeTrue();
	});
	it("testIsCompleteNoYear", function () {
		const date = "????-01-01";
		expect(interchange.isComplete(date)).toBeFalse();
	});
	it("testIsCompleteNoMonth", function () {
		const date = "2000-??-01";
		expect(interchange.isComplete(date)).toBeFalse();
	});
	it("testIsCompleteNoMonthNoSeparators", function () {
		const date = "2000??01";
		expect(interchange.isComplete(date)).toBeFalse();
	});
	it("testIsCompleteNoDate", function () {
		const date = "2000-01-??";
		expect(interchange.isComplete(date)).toBeFalse();
	});
	it("testIsValid", function () {
		const date = "2000-01-01";
		expect(interchange.isValid(date)).toBeTrue();
	});
	it("testIsValidNoSeparators", function () {
		const date = "20000101";
		expect(interchange.isValid(date)).toBeTrue();
	});
	it("testIsValidPartial", function () {
		const date = "2000-??-01";
		expect(interchange.isValid(date)).toBeTrue();
	});
	it("testIsValidPartialNoSeparators", function () {
		const date = "200001??";
		expect(interchange.isValid(date)).toBeTrue();
	});
	it("testIsValidWithInvalidString", function () {
		const date = "2000--01";
		expect(interchange.isValid(date)).toBeFalse();
	});
	it("testIsValidPartialWithInvalidString", function () {
		const date = "200001";
		expect(interchange.isValid(date)).toBeFalse();
	});
	it("testIsValidWithEmptyString", function () {
		const date = "";
		expect(interchange.isValid(date)).toBeFalse();
	});
});
