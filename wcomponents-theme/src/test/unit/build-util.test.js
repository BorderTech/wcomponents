/* eslint-env node, es6  */
const { registerSuite } = intern.getPlugin("interface.object");
const { requireRoot } = require("./util");
const buildUtil = requireRoot("scripts/build-util");
// const { assert } = intern.getPlugin("chai");

registerSuite("build-util", {
	tests: {
		/**
		 * Test requireAmd on our build utils.
		 * @returns {Promise} resolved when done.
		 */
		"testRequireAmd": function () {
			return new Promise(function (win) {
				buildUtil.requireAmd(["wc/debounce"], function(debounce) {

					debounce(win, 0);
				});
			});
		}
	}
});
