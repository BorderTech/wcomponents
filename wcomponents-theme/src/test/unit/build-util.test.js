/* eslint-env node, es6  */
const { registerSuite } = intern.getPlugin("interface.object");
const { requireRoot } = require("./util");
const buildUtil = requireRoot("scripts/build-util");
const assert = require("assert");
// const { assert } = intern.getPlugin("chai");

registerSuite("build-util", {
	tests: {
		/**
		 * Test requireAmd on our build utils.
		 * @returns {Promise} resolved when done.
		 */
		testRequireAmd: function () {
			return new Promise(function (win) {
				buildUtil.requireAmd(["wc/global"], function(global) {
					assert.strictEqual(global, this);
					win();
				});
			});
		}
	}
});
