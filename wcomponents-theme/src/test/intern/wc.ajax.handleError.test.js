define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function (registerSuite, assert, testutils) {
	"use strict";

	var
		/**
		 * The module name of the module being tested eg "wc/ui/foo".
		 * @type String
		 */
		TEST_MODULE = "wc/ajax/handleError",
		/**
		 * A human readable name for the suite. This could be as simpl as TEST_MODULE.
		 * @type String
		 */
		suiteName = TEST_MODULE.match(/\/([^\/]+)$/)[1],
		/**
		 * An options array of dependency names in addition to TEST_MODULE, Define a String Array here and setup will convert it to a module array.
		 * @type arg
		 */
		deps = ["wc/i18n/i18n", "wc/config"],
		// END CONFIGURATION VARS
		// the next two are not settable.
		controller, // This will be the actual module named above. Tests of public functions use this e.g. `controller.getSomething()`.
		// Now, if you have extra dependencies you will want a way to reference them.
		i18n,
		wcconfig;

	function MockResponse(status, responseText, statusText) {
		this.status = status;
		this.responseText = responseText;
		this.statusText = statusText;
	}

	registerSuite({
		name: suiteName,

		setup: function() {
			var allDeps, realConfig;
			if (!TEST_MODULE) {
				console.error("Your tests won't work!");
				return Promise.resolve();
			}

			allDeps = (deps && deps.length) ? deps : [];
			allDeps.unshift(TEST_MODULE);
			return testutils.setupHelper(allDeps).then(function(arg) {

				// The module to be tested is the controller
				controller = arg[0];
				// The other dependencies
				// If you want to have named dependencies the vars are assigned here
				i18n = arg[1];
				wcconfig = arg[2];
				// Custom set up
				if ((realConfig = wcconfig.get("wc/ui/xhr")) && realConfig.messages) {
					wcconfig.set({ messages: null}, "wc/ui/xhr");
				}
				if ((realConfig = wcconfig.get("wc/ui/multiFileUploader")) && realConfig.messages) {
					wcconfig.set({ messages: null}, "wc/ui/multiFileUploader");
				}
			});
		},
		testGotController: function () {
			if (!TEST_MODULE) {
				assert.isOk(TEST_MODULE, "Cannot test an undefined module you tailless monkey!");
			}
			assert.typeOf(controller, "object", "Expected the test module to be available as an object otherwise the tests won't work.");
		},
		testFaux500: function() {
			var expected = "500 response text",
				response = new MockResponse(500, expected),
				actual = controller.getErrorMessage(response);
			assert.strictEqual(actual, expected);
		},
		testFaux500WithStatusText: function() {
			var expected = "500 status text",
				response = new MockResponse(500, null, expected),
				actual = controller.getErrorMessage(response);
			assert.strictEqual(actual, expected);
		},
		testSilly200: function() {
			var expected = i18n.get("xhr_errormsg"),
				response = new MockResponse(200, "foo", "bar"),
				actual = controller.getErrorMessage(response);
			assert.strictEqual(actual, expected);
		},
		testConfig: function() {
			var expected, response, actual;
			try {
				wcconfig.set({ messages: {
					403: "Oh noes! A 403 occurred!",
					404: "I can't find it!",
					418: function(resp) {
						// this is an example of handling a JSON response body
						var data;
						try {
							data = JSON.parse(resp.responseText);
							data = data.message;
						} catch (ex) {
							data = resp.responseText;
						}
						return data + " " + resp.status;
					},
					200: "Some gateway proxies don't know basic HTTP",
					error: "An error occurred and I have not set a specific message for it!"
				}}, "wc/ui/xhr");
				// 403
				expected = "Oh noes! A 403 occurred!";
				response = new MockResponse(403, "foo", "bar");
				actual = controller.getErrorMessage(response);
				assert.strictEqual(actual, expected);
				// 404
				expected = "I can't find it!";
				response = new MockResponse(404, "foo", "bar");
				actual = controller.getErrorMessage(response);
				assert.strictEqual(actual, expected);
				// 418
				expected = "Short and stout 418";
				response = new MockResponse(418, "{ \"message\": \"Short and stout\" }", "I'm a teapot");
				actual = controller.getErrorMessage(response);
				assert.strictEqual(actual, expected);
				// 200
				expected = "Some gateway proxies don't know basic HTTP";
				response = new MockResponse(200, "foo", "bar");
				actual = controller.getErrorMessage(response);
				assert.strictEqual(actual, expected);
				// 500
				expected = "An error occurred and I have not set a specific message for it!";
				response = new MockResponse(500, "foo", "bar");
				actual = controller.getErrorMessage(response);
				assert.strictEqual(actual, expected);

			} finally {
				wcconfig.set(null, "wc/ui/xhr");
			}
		}
	});
});
