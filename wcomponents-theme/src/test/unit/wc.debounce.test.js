/* eslint-env node, es6  */
const { registerSuite } = intern.getPlugin("interface.object");
const { requireRoot } = require("./util");
const buildUtil = requireRoot("scripts/build-util");
const assert = require("assert");
// const { assert } = intern.getPlugin("chai");

/**
 * This is actually a really important test...
 * Some of our developer tools use some of our AMD source code as node modules.
 * It would be easy for these modules to become "browser only" if we do not test in nodejs.
 */
registerSuite("wc/debounce", {
	tests: {
		testDebounce: function () {
			return new Promise(function (win, lose) {
				buildUtil.requireAmd(["wc/debounce"], function(wcdebounce) {
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
			});
		}
	}
});
