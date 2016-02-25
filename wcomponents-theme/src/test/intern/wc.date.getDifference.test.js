define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
		function (registerSuite, assert, testutils) {
			"use strict";
			var controller;
			registerSuite({
				name: "dateGetDifference",
				setup: function() {
					return testutils.setupHelper(["wc/date/getDifference"], function(obj) {
						controller = obj;
					});
				},
				testAreSameDay: function () {
					var date1 = new Date(),
						date2 = new Date();

					assert.isTrue(controller(date1, date2) === 0);
				},
				testAreSameDaySameInstance: function () {
					var date1 = new Date();

					assert.isTrue(controller(date1, date1) === 0);
				},
				testAreSameDayWithDifferentTime: function () {
					var date1 = new Date(79, 5, 24, 0, 0, 0),
						date2 = new Date(79, 5, 24, 23, 59, 59);

					assert.isTrue(controller(date1, date2) === 0);
				},
				testAreSameDayWithDifferentYear: function () {
					var date1 = new Date(78, 5, 24),
						date2 = new Date(79, 5, 24);

					assert.isFalse(controller(date1, date2) === 0);
				},
				testAreSameDayWithDifferentMonth: function () {
					var date1 = new Date(79, 5, 24),
						date2 = new Date(79, 6, 24);

					assert.isFalse(controller(date1, date2) === 0);
				},
				testAreSameDayWithDifferentDay: function () {
					var date1 = new Date(79, 5, 25),
						date2 = new Date(79, 5, 24);

					assert.isFalse(controller(date1, date2) === 0);
				},
				testDateDifferencePast: function () {
					var date1 = new Date(79, 5, 25),
						date2 = new Date(79, 5, 24);

					assert.isTrue(controller(date1, date2) > 0);
				},
				testDateDifferenceFuture: function () {
					var date1 = new Date(79, 5, 25),
						date2 = new Date(79, 5, 26);

					assert.isTrue(controller(date1, date2) < 0);
				},
				testNumericDateDifferencePast: function () {
					var date1 = new Date(1979, 5, 25),
						date2 = new Date(1978, 5, 25);

					assert.strictEqual(365, controller(date1, date2));
				},
				testNumericDateDifferenceFuture: function () {
					var date1 = new Date(1978, 5, 25),
						date2 = new Date(1979, 5, 25);

					assert.strictEqual(-365, controller(date1, date2));
				},
				/**
				 * this test uses the dateDiff function as an arg in Array.sort(). As a
				 * comparison it creates an identical array then converts the Date objects
				 * to numbers. The sort on number (a - b) should then be the same as the
				 * sort on Dates and when the numbers are converted back to Dates the
				 * two arrays should be identical.
				 */
				testWithSort: function () {
					var a = [new Date(78, 5, 25), new Date(78, 6, 25), new Date(78, 5, 20), new Date(78, 5, 25), new Date(78, 3, 25), new Date(78, 3, 22), new Date(78, 11, 25)],
						e = [], i = 0;

					// add a few randoms for fun (dates between 19750101 and 20253112)
					while (i++ < 20) {
						a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
					}
					// make a copy of a converted to numbers, sort the numbers, then convert them back to dates
					e = a.map(function (next) {
						return next.valueOf();
					});
					e.sort(function (x, y) {
						return x - y;
					});
					e = e.map(function (next) {
						return new Date(next);
					});
					a.sort(controller);
					assert.isTrue(compareArrays(e, a), "Sorted arrays should have identical dates");
				},
				/**
				 * getDifference returns a number. for any pair of dates this number should be the
				 * same as the difference between their values divided by the millisecond constant (86400000).
				 */
				testNumericResult: function () {
					var a = [new Date(78, 5, 25), new Date(78, 6, 25), new Date(78, 5, 20), new Date(78, 5, 25), new Date(78, 3, 25), new Date(78, 3, 22), new Date(78, 11, 25)],
						e = [], i = 0, aMapped = [], eMapped = [], MC = 86400000;
					// add a few randoms for fun (dates between 19750101 and 20253112)
					while (i++ < 20) {
						a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
					}
					// make a copy of a converted to numbers
					e = a.map(function (next) {
						return next.valueOf();
					});

					for (i = 0; i < a.length; ++i) {
						if (i === a.length - 1) {
							aMapped[aMapped.length] = controller(a[i], a[0]);
							eMapped[eMapped.length] = (e[i] - e[0]) / MC;
						}
						else {
							aMapped[aMapped.length] = controller(a[i], a[i + 1]);
							eMapped[eMapped.length] = (e[i] - e[i + 1]) / MC;
						}
					}
					assert.isTrue(compareArrays(eMapped, aMapped), "numeric arrays should have identical values");
				},
				/**
				 * this test uses getDifference function as an arg in Array.sort(). As a
				 * comparison it creates an identical array then converts the Date objects
				 * to numbers. The sort on number (a - b) should then be the same as the
				 * sort on Dates and when the numbers are converted back to Dates the
				 * two arrays should be identical.
				 */
				testWithSortAndTime: function () {
					var a = [new Date(78, 5, 25, 18, 30, 29), new Date(78, 6, 25, 18, 20, 29), new Date(78, 5, 20, 18, 57), new Date(78, 5, 25, 18, 30, 29, 400), new Date(78, 3, 25, 18, 30, 29, 2), new Date(78, 3, 22, 6, 0, 0, 3), new Date()],
						e = [], i = 0;


					function sorter(_a, _b) {
						return controller(_a, _b, true);
					}

					// add a few randoms for fun (dates between 19750101 and 20253112)
					while (i++ < 20) {
						a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29), Math.floor(Math.random() * 24), Math.floor(Math.random() * 60), Math.floor(Math.random() * 60), Math.floor(Math.random() * 1000)));
					}
					// make a copy of a converted to numbers, sort the numbers, then convert them back to dates
					e = a.map(function (next) {
						return next.valueOf();
					});
					e.sort(function (x, y) {
						return x - y;
					});
					e = e.map(function (next) {
						return new Date(next);
					});
					a.sort(sorter);
					assert.isTrue(compareArrays(e, a), "Sorted arrays should have identical dates");
				},
				/**
				 * getDifference returns a number. for any pair of dates this number should be the
				 * same as the difference between their values.
				 */
				testNumericResultWithTime: function () {
					var a = [], e = [], i = 0, aMapped = [], eMapped = [];
					// add a few randoms for fun (dates between 19750101 and 20253112)
					while (i++ < 20) {
						a.push(new Date(Math.floor(Math.random() * 51) + 1975, Math.floor(Math.random() * 12), Math.floor(Math.random() * 29)));
					}
					// make a copy of a converted to numbers
					e = a.map(function (next) {
						return next.valueOf();
					});

					for (i = 0; i < a.length; ++i) {
						if (i === a.length - 1) {
							aMapped[aMapped.length] = controller(a[i], a[0], true);
							eMapped[eMapped.length] = e[i] - e[0];
						}
						else {
							aMapped[aMapped.length] = controller(a[i], a[i + 1], true);
							eMapped[eMapped.length] = e[i] - e[i + 1];
						}
					}
					assert.isTrue(compareArrays(eMapped, aMapped), "numeric arrays should have identical values");
				}
			});

			/**
			 * compares two arrays returns true if they are identical
			 * @param a first array to compare
			 * @param b second array to compare
			 */
			function compareArrays(a, b) {
				var result = false, i;
				if (Array.isArray(a) && Array.isArray(b) && a.length === b.length) {
					for (i = 0; i < a.length; ++i) {
						if (a[i].constructor === Date) {
							if (!(result = typeof a[i] === typeof b[i])) {
								console.log(a[i].toString() + " is not the same type as " + b[i].toString());
								break;
							}

							if (a[i].valueOf && !(result = a[i].valueOf() === b[i].valueOf())) {
								console.log(a[i].toString() + " does not have the same valueOf as " + b[i].toString());
								break;
							}

							if (a[i].toString && !(result = a[i].toString() === b[i].toString())) {
								console.log(a[i] + " does not have the same toString as " + b[i] + " i = " + i);
								break;
							}
						}
						else if (!(result = a[i] === b[i])) {
							break;
						}
					}
				}
				return result;
			}
		});
