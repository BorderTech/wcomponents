/**
 * This module exists to provide AMD compatibility when moving from intern 3 to 4.
 */
define(function() {
	var internObj;
	return {
		load: function (id, parentRequire, callback/* , config */) {
			internObj = internObj || intern.getPlugin("interface.object");
			if (id === "object") {
				// I know this doesn't seem to make sense but it's right.
				callback(registerSuiteIntern3to4);
			} else {
				callback(internObj);
			}
		}
	};

	/**
	 * Adapts intern 3 register suite calls to intern 4.
	 * @param suite An intern 3 test suite.
	 * @returns Whatever registerSuite returns.
	 */
	function registerSuiteIntern3to4(suite) {
		var renameMap = {
				setup: "before",
				teardown: "after"
			},
			testProps = Object.keys(suite).filter(function(prop) {
				var notTests = ["before", "after", "beforeEach", "afterEach", "setup", "teardown", "name", "tests"],
					isTest = notTests.indexOf(prop) < 0;
				isTest = isTest && typeof suite[prop] === "function";
				return isTest;
			});

		// Move all test functions to suite.tests
		testProps.forEach(function(prop) {
			suite.tests = suite.tests || {};
			suite.tests[prop] = suite[prop];
			delete suite[prop];
		});

		// Rename certain lifecycle functions
		Object.keys(renameMap).forEach(function(prop) {
			suite[renameMap[prop]] = suite[prop];
			delete suite[prop];
		});

		return internObj.registerSuite(suite.name, suite);
	}
});
