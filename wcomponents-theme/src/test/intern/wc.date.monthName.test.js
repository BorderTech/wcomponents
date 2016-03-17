define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
		function (registerSuite, assert, testutils) {
			"use strict";
			/* NOTE: this relies on i18n working properly */
			var monthName, i18n,
				expected = [
					"mnth0",
					"mnth1",
					"mnth2",
					"mnth3",
					"mnth4",
					"mnth5",
					"mnth6",
					"mnth7",
					"mnth8",
					"mnth9",
					"mnthA",
					"mnthB"];

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
