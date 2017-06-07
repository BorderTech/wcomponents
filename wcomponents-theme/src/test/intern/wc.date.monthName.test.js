define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
		function (registerSuite, assert, testutils) {
			"use strict";
			/* NOTE: this relies on i18n working properly */
			var monthName, i18n,
				expected = [
					"month0",
					"month1",
					"month2",
					"month3",
					"month4",
					"month5",
					"month6",
					"month7",
					"month8",
					"month9",
					"monthA",
					"monthB"];

			registerSuite({
				name: "monthName",
				setup: function() {
					return testutils.setupHelper(["wc/date/monthName", "wc/i18n/i18n"], function(m, i) {
						monthName = m;
						i18n = i;
					});
				},
				testmonthName: function () {
					var result = monthName.get(),
						i = 0;
					do {
						assert.strictEqual(i18n.get(expected[i]), result[i], "monthName.get returned unexpected result");
						i++;
					}
					while (result[i]);
				}
			});
		});
