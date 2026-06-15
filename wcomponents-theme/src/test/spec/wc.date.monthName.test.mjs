
import process from "node:process";

import monthName from "wc/date/monthName.mjs";

const { afterAll, beforeAll, beforeEach, console, describe, document, expect, it } = globalThis;

describe("wc/date/monthName", function() {
	const expectedEn = ["January", "February", "March", "April", "May", "June", "July",
		"August", "September", "October", "November", "December"];

	const expectedEnAbbr = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
		"Aug", "Sep", "Oct", "Nov", "Dec"];

	const expectedFr = ["janvier", "février", "mars", "avril", "mai", "juin", "juillet",
		"août", "septembre", "octobre", "novembre", "décembre"];

	const expectedFrAscii = ["janvier", "fevrier", "mars", "avril", "mai", "juin", "juillet",
		"aout", "septembre", "octobre", "novembre", "decembre"];

	const expectedFrAbbrAscii = ["janv", "fevr", "mars", "avr", "mai", "juin", "juil",
		"aout", "sept", "oct", "nov", "dec"];

	const lang = document.documentElement.lang;
	const tz = process.env.TZ;

	beforeAll(() => {
		// PLEASE LEAVE THE TIMEZONE SWITCH IN PLACE even if you don't understand why
		process.env.TZ = "America/Los_Angeles";
	});

	afterAll(() => {
		console.log("Resetting timezone to", tz);
	});

	beforeEach(() => {
		document.documentElement.lang = lang;
	});

	it("testmonthNameEn", function() {
		document.documentElement.lang = "en";
		const actual = monthName.get();

		expect(actual).toEqual(expectedEn);
	});

	it("testmonthNameFr", function() {
		document.documentElement.lang = "fr";
		const actual = monthName.get();

		expect(actual).toEqual(expectedFr);
	});

	it("testmonthNameEnAbbr", function() {
		document.documentElement.lang = "en";
		const actual = monthName.get(true);

		expect(actual).toEqual(expectedEnAbbr);
	});

	it("testmonthNameFrAscii", function() {
		document.documentElement.lang = "fr";
		const actual = monthName.get(false, true);

		expect(actual).toEqual(expectedFrAscii);
	});

	it("testmonthNameFrAbbrAscii", function() {
		document.documentElement.lang = "fr";
		const actual = monthName.get(true, true);

		expect(actual).toEqual(expectedFrAbbrAscii);
	});

	it("testhasAscii", function() {
		document.documentElement.lang = "fr";
		monthName.get(false, true);
		const actual = monthName.hasAsciiVersion();

		expect(actual).toEqual(true);
	});
});
