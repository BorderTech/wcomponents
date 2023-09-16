import getDifference from "wc/date/getDifference.mjs";

describe("wc/date/getDifference", function() {

	it("testAreSameDay", () => {
		const date1 = new Date(),
			date2 = new Date();
		expect(getDifference(date1, date2)).toBe(0);
	});
	it("testAreSameDaySameInstance", () => {
		const date1 = new Date();

		expect(getDifference(date1, date1)).toBe(0);
	});
	it("testAreSameDayWithDifferentTime", () => {
		const date1 = new Date(79, 5, 24, 0, 0, 0),
			date2 = new Date(79, 5, 24, 23, 59, 59);

		expect(getDifference(date1, date2)).toBe(0);
	});
	it("testAreSameDayWithDifferentYear", () => {
		const date1 = new Date(78, 5, 24),
			date2 = new Date(79, 5, 24);

		expect(getDifference(date1, date2)).not.toBe(0);
	});
	it("testAreSameDayWithDifferentMonth", () => {
		const date1 = new Date(79, 5, 24),
			date2 = new Date(79, 6, 24);

		expect(getDifference(date1, date2)).not.toBe(0);
	});
	it("testAreSameDayWithDifferentDay", () => {
		const date1 = new Date(79, 5, 25),
			date2 = new Date(79, 5, 24);

		expect(getDifference(date1, date2)).not.toBe(0);
	});
	it("testDateDifferencePast", () => {
		const date1 = new Date(79, 5, 25),
			date2 = new Date(79, 5, 24);

		expect(getDifference(date1, date2)).toBeGreaterThan(0);
	});
	it("testDateDifferenceFuture", () => {
		const date1 = new Date(79, 5, 25),
			date2 = new Date(79, 5, 26);
		expect(getDifference(date1, date2)).toBeLessThan(0);
	});
	it("testNumericDateDifferencePast", () => {
		const date1 = new Date(1979, 5, 25),
			date2 = new Date(1978, 5, 25);
		expect(getDifference(date1, date2)).toBe(365);
	});
	it("testNumericDateDifferenceFuture", () => {
		const date1 = new Date(1978, 5, 25),
			date2 = new Date(1979, 5, 25);

		expect(getDifference(date1, date2)).toBe(-365);
	});
	/**
		 * this test uses the dateDiff function as an arg in Array.sort(). As a
		 * comparison it creates an identical array then converts the Date objects
		 * to numbers. The sort on number (a - b) should then be the same as the
		 * sort on Dates and when the numbers are converted back to Dates the
		 * two arrays should be identical.
		 */
	it("Sorted arrays should have identical dates", function () {
		const a = [
			new Date(78, 5, 25),
			new Date(78, 6, 25),
			new Date(78, 5, 20),
			new Date(78, 5, 25),
			new Date(78, 3, 25),
			new Date(78, 3, 22),
			new Date(78, 11, 25)
		];

		let i = 0;
		// add a few randoms for fun (dates between 19750101 and 20253112)
		while (i++ < 20) {
			a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
		}
		// make a copy of a converted to numbers, sort the numbers, then convert them back to dates
		const e = a.map(next => next.valueOf()).sort((x, y) => x - y).map(next => new Date(next));
		a.sort(getDifference);
		expect(e).toEqual(a);
	});

	/**
	 * getDifference returns a number. for any pair of dates this number should be the
	 * same as the difference between their values divided by the millisecond constant (86400000).
	 */
	it("numeric arrays should have identical values", function () {
		const a = [
			new Date(78, 5, 25),
			new Date(78, 6, 25),
			new Date(78, 5, 20),
			new Date(78, 5, 25),
			new Date(78, 3, 25),
			new Date(78, 3, 22),
			new Date(78, 11, 25)
		];
		let i = 0;
		// add a few randoms for fun (dates between 19750101 and 20253112)
		while (i++ < 20) {
			a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
		}
		// make a copy of a converted to numbers
		const MC = 86400000;
		const e = a.map(next => next.valueOf());
		const eMapped = [], aMapped = [];
		for (i = 0; i < a.length; ++i) {
			if (i === a.length - 1) {
				aMapped.push(getDifference(a[i], a[0]));
				eMapped.push((e[i] - e[0]) / MC);
			} else {
				aMapped.push(getDifference(a[i], a[i + 1]));
				eMapped.push((e[i] - e[i + 1]) / MC);
			}
		}
		expect(eMapped).toEqual(aMapped);
	});

	/**
		 * this test uses getDifference function as an arg in Array.sort(). As a
		 * comparison it creates an identical array then converts the Date objects
		 * to numbers. The sort on number (a - b) should then be the same as the
		 * sort on Dates and when the numbers are converted back to Dates the
		 * two arrays should be identical.
		 */
	it("Sorted arrays should have identical dates with times", () => {
		const a = [
			new Date(78, 5, 25, 18, 30, 29),
			new Date(78, 6, 25, 18, 20, 29),
			new Date(78, 5, 20, 18, 57),
			new Date(78, 5, 25, 18, 30, 29, 400),
			new Date(78, 3, 25, 18, 30, 29, 2),
			new Date(78, 3, 22, 6, 0, 0, 3),
			new Date()
		];

		function sorter(_a, _b) {
			return getDifference(_a, _b, true);
		}

		let i = 0;
		// add a few randoms for fun (dates between 19750101 and 20253112)
		while (i++ < 20) {
			a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29), Math.floor(Math.random() * 24), Math.floor(Math.random() * 60), Math.floor(Math.random() * 60), Math.floor(Math.random() * 1000)));
		}
		// make a copy of a converted to numbers, sort the numbers, then convert them back to dates
		const e = a.map(next => next.valueOf()).sort((x, y) => x - y).map(next => new Date(next));
		a.sort(sorter);
		expect(e).toEqual(a);
	});

	/**
	 * getDifference returns a number. for any pair of dates this number should be the
	 * same as the difference between their values.
	 */
	it("numeric arrays should have identical values", () => {
		const a = [];
		// add a few randoms for fun (dates between 19750101 and 20253112)
		let i = 0;
		while (i++ < 20) {
			a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
		}
		// make a copy of a converted to numbers
		const e = a.map(next => next.valueOf());
		const aMapped = [], eMapped = [];
		for (i = 0; i < a.length; ++i) {
			if (i === a.length - 1) {
				aMapped.push(getDifference(a[i], a[0], true));
				eMapped.push(e[i] - e[0]);
			} else {
				aMapped.push(getDifference(a[i], a[i + 1], true));
				eMapped.push(e[i] - e[i + 1]);
			}
		}
		expect(eMapped).toEqual(aMapped);
	});
});

