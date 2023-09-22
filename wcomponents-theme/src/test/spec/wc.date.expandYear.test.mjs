import expandYear from "wc/date/expandYear.mjs";
import pivot from "wc/date/pivot.mjs";
import today from "wc/date/today.mjs";


/*
 * Tests expandYear
 * Also tests wc/date/pivot because that should really be part of expandYear.
 * Also inadvertently tests wc/date/today.
 */
describe("wc/date/expandYear", function() {
	let pivotVal = null,
		todayVal;

	beforeAll(() => {
		pivotVal = pivot.get();
		todayVal = today.get();
	});

	afterAll(() => {  /* IMPORTANT! clean up after yourself! */
		today.set(todayVal);  // reset the date in "today"
		pivot.set(pivotVal);  // reset the pivot value.
	});

	beforeEach(() => {
		pivot.set(pivotVal);
		today.set(new Date(1903, 9, 28));  // fake the date so these tests never go stale
	});

	it("testExpandYear", function() {
		expect(expandYear("18")).toBe(1918);
		expect(expandYear("02")).toBe(1902);
		expect(expandYear("99")).toBe(1899);
		expect(expandYear(18)).toBe(1918);
		expect(expandYear(2)).toBe(1902);
		expect(expandYear(99)).toBe(1899);
	});

	it("testExpandYearPivotOne", function() {
		pivot.set(1);
		expect(expandYear("18")).toBe(1818);
		expect(expandYear("02")).toBe(1902);
		expect(expandYear("99")).toBe(1899);
		expect(expandYear(18)).toBe(1818);
		expect(expandYear(2)).toBe(1902);
		expect(expandYear(99)).toBe(1899);
	});

	it("testExpandYearPivotOneHundred", function() {
		pivot.set(100);
		expect(expandYear("18")).toBe(1918);
		expect(expandYear("02")).toBe(2002);
		expect(expandYear("99")).toBe(1999);
		expect(expandYear(18)).toBe(1918);
		expect(expandYear(2)).toBe(2002);
		expect(expandYear(99)).toBe(1999);
	});

	it("testPivotLBoundOutOfRange", function() {
		expect(() => pivot.set(0)).toThrowError();
	});

	it("testPivotUBoundOutOfRange", function() {
		expect(() => pivot.set(101)).toThrowError();
	});
});
