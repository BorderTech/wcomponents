/* eslint-env node, es6  */
define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!", "wc/ui/timeoutWarn", "/node_modules/@testing-library/dom/dist/@testing-library/dom.umd.js"],
	function (registerSuite, assert, testutils, timeoutWarn, domTesting) {
		let container, uid = 0;

		registerSuite({
			name: "wc/ui/timeoutWarn",
			setup: function () {
				container = testutils.getTestHolder();
			},
			afterEach: function () {
				container.innerHTML = "";
			},
			testBasicRender: function () {
				const testId = `uid-${Date.now()}-${uid++}`;
				container.innerHTML = `<${timeoutWarn.tagName} data-testid='${testId}'></${timeoutWarn.tagName}>`;
				assert.isOk(domTesting.within(container).getByTestId(testId));
			}
		});
	});
