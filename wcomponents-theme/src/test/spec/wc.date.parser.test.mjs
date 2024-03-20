/*
 * Note, some of these tests will break in the future, like 20 years from the time of
 * writing (2010).  If you are still using these tests in 20 years something went horribly
 * wrong with the IT revolution.
 */
describe("wc/date/Parser", function() {
	const standardMasks = ["ytm", "+-", "d M yy", "d M yyyy", "d MON yy", "d MON yyyy", "ddMMyy", "ddMMyyyy", "dMONyy", "dMONyyyy", "yyyy-MM-dd", "yyyyMMdd"],
		partialMasks = standardMasks.concat([" M y", " MON y", "M y", "MON y", "MONy", "MMyy", "Myyyy", "y", "ddMM"]),
		extendedPartialMasks = partialMasks.concat(["d M", "M", "d MON", "d M", "MON", "d", "dd yyyy", "ddyyyy", "ddyy", "dd yy"]),
		getParser = (masks, past, rolling) => {
			const parser = new Parser();
			parser.setRolling(!!rolling);
			parser.setMasks(masks);
			parser.setExpandYearIntoPast(!!past);
			return parser;
		},
		doPlusMinusTest = (days, rolling) => {
			const parser = getParser(standardMasks, false, rolling),
				dayString = (days >= 0 ? "+" : "") + days.toString(),
				result = parser.parse(dayString);
			now.setDate(now.getDate() + days);
			expect(result.length).toBe(1);
			expect(result[0].day).toBe(now.getDate());
			expect(result[0].month).toBe((now.getMonth() + 1));
			expect(result[0].year).toBe(now.getFullYear());
		};

	let now, pivotVal, realToday, Parser, pivot, today;

	beforeEach(() => {
		now = new Date();
		const deps = ["wc/date/Parser.mjs", "wc/date/pivot.mjs", "wc/date/today.mjs"];
		return Promise.all(deps.map(dep => import(dep))).then(([a, b, c]) => {
			Parser = a.default;
			pivot = b.default;
			today = c.default;
			pivotVal = pivotVal || pivot.get();
			realToday = realToday || today.get();
		});
	});

	afterEach(() => {
		pivot.set(pivotVal);
		today.set(realToday);
	});

	it("testParserStnd", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("28101973");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserDayOfWeek", function() {
		const parser = getParser(["E MON dd yyyy"], false, false),
			result = parser.parse("Tue Nov 04 2003");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(4);
		expect(result[0].month).toBe(11);
		expect(result[0].year).toBe(2003);
	});
	it("testParserStndIsoDate", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("1973-10-28");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserCompressedIsoDate", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("19731028");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserStndFwdSlashes", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("28/10/1973");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserStndHyphens", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("28-10-1973");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserStndExpandYear", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("281025");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(2025);
	});
	it("testParserStndExpandYearPast", function() {
		const parser = getParser(standardMasks, true, false),
			result = parser.parse("281025");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1925);
	});
	it("testParserStndMonthAbbr", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("28 OCT 1973");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserStndMonthFull", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("28 OCTOBER 1973");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(28);
		expect(result[0].month).toBe(10);
		expect(result[0].year).toBe(1973);
	});
	it("testParserPartial", function() {
		today.set(new Date(2000, 0, 1));
		pivot.set(15);
		const parser = getParser(partialMasks, false, false);
		const result = parser.parse("1111");
		expect(result.length).toBe(2);
		const xfers = result.map(next => next.toXfer());
		expect(xfers).toContain("????-11-11");
		expect(xfers).toContain("2011-11-??");
	});
	it("testParserXtnd", function() {
		today.set(new Date(2000, 0, 1));
		pivot.set(15);
		const parser = getParser(extendedPartialMasks, false, false);
		const result = parser.parse("1111");
		expect(result.length).toBe(3);
		// debugger;
		const xfers = result.map(next => next.toXfer());
		expect(xfers).toContain("????-11-11");
		expect(xfers).toContain("2011-??-11");
		expect(xfers).toContain("2011-11-??");
	});
	it("testParserToday", function() {
		const parser = getParser(standardMasks, false, false);
		const result = parser.parse("t");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(now.getDate());
		expect(result[0].month).toBe((now.getMonth() + 1));
		expect(result[0].year).toBe(now.getFullYear());
	});
	it("testParserYesterday", function() {
		const parser = getParser(standardMasks, false, false);
		const result = parser.parse("y");
		now.setDate(now.getDate() - 1);
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(now.getDate());
		expect(result[0].month).toBe((now.getMonth() + 1));
		expect(result[0].year).toBe(now.getFullYear());
	});
	it("testParserTomorrow", function() {
		const parser = getParser(standardMasks, false, false);
		const result = parser.parse("m");
		now.setDate(now.getDate() + 1);
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(now.getDate());
		expect(result[0].month).toBe((now.getMonth() + 1));
		expect(result[0].year).toBe(now.getFullYear());
	});
	it("testParserTomorrowPastDate", function() {
		const parser = getParser(standardMasks, true, false);
		const result = parser.parse("m");
		now.setDate(now.getDate() + 1);
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(now.getDate());
		expect(result[0].month).toBe((now.getMonth() + 1));
		expect(result[0].year).toBe((now.getFullYear() - 100));
	});
	it("testRollingDate", function() {
		const parser = getParser(standardMasks, false, true),
			result = parser.parse("30 Feb 2011");
		expect(result.length).toBe(1);
		expect(result[0].day).toBe(2);
		expect(result[0].month).toBe(3);
		expect(result[0].year).toBe(2011);
	});
	it("testInvalidDateNoRolling", function() {
		const parser = getParser(standardMasks, false, false),
			result = parser.parse("30 Feb 2011");
		expect(result.length).toBe(0);
	});
	it("testParserTodayPlus100", function() {
		doPlusMinusTest(100, false);
	});
	it("testParserTodayMinus100", function() {
		doPlusMinusTest(-100, false);
	});
	it("testParserTodayPlus100Rolling", function() {
		doPlusMinusTest(100, true);
	});
	it("testParserTodayMinus100Rolling", function() {
		doPlusMinusTest(-100, true);
	});
	it("testParserNull", function() {
		const parser = new Parser(),
			result = parser.getMasks();
		expect(result).toBe(null);
	});
});
