import monthName from "wc/date/monthName.mjs";
describe("wc/date/monthName", function() {
	const expectedEn = ["January", "February", "March", "April", "May", "June", "July",
		"August", "September", "October", "November", "December"];

	const expectedFr = ["janvier", "février", "mars", "avril", "mai", "juin", "juillet",
		"août", "septembre", "octobre", "novembre", "décembre"];

	const lang = globalThis.document.documentElement.lang;


	beforeEach(() => {
		globalThis.document.documentElement.lang = lang;
	});

	it("testmonthNameEn", function() {
		globalThis.document.documentElement.lang = "en";
		const actual = monthName.get();
		expect(actual).toEqual(expectedEn);
	});

	it("testmonthNameFr", function() {
		globalThis.document.documentElement.lang = "fr";
		const actual = monthName.get();
		expect(actual).toEqual(expectedFr);
	});
});
