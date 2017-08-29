define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var wcdebounce;

		registerSuite({
			name: "debounce",
			setup: function () {
				return testutils.setupHelper(["wc/debounce"]).then(function (arr) {
					wcdebounce = arr[0];
				});
			},
			testDebounce: function () {
				return new Promise(function (win, lose) {
					var total = 20,
						results = {
							called: 0
						}, i,
						debounced = wcdebounce(function(arg) {
							arg.called++;
							assert.strictEqual(arg, results, "Expect args to be passed through to wrapped function");
							assert.strictEqual(results.called, 1, "Expect debounced function to be called once and once only");
							assert.strictEqual(i, total, "The debounced function should have been called mucho times");
							win();
						}, 100);
					try {
						for (i = 0; i < total; i++) {
							debounced(results);
						}
					} catch (ex) {
						lose(ex);
					}
				});
			}
		});
	});
