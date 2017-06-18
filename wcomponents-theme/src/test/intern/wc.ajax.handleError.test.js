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
		// Load test UI content if required: only one of these is needed and testContent will take precedence over urlResource
		/**
		 * HTML test UI e.g. "<div>Test HTML</div>". Only needed ig the tests use common HTML. Optionally use urlResource instead if the test HTML
		 * is complex. If both are set testContent takes precedence and urlResource is ignored.
		 * @type String
		 */
		testContent,
		/**
		 * Load test UI froman external resource e.g. "@RESOURCES@/SOME_PAGE.html". Leave undefined if not required. Simple test UIs may be set inline
		 * using testContent instead. If both are set testContent takes precedence and urlResource is ignored.
		 * Note that the property `@RESOURCES@` will be mapped to the test/intern/resources directory as a URL.
		 * @type URL
		 */
		urlResource,
		/**
		 * If true and either testContent or urlResource is used to set test UI then the test UI will be reset to its original state before each test.
		 * @type Boolean
		 */
		resetBeforeEach = false,
		/**
		 * If true and either testContent or urlResource is used to set test UI then the test UI will be reset to its original state after each test.
		 * @type Boolean
		 */
		resetAfterEach = true,
		// END CONFIGURATION VARS
		// the next two are not settable.
		controller, // This will be the actual module named above. Tests of public functions use this e.g. `controller.getSomething()`.
		testHolder, // This will hold any UI needed for the tests. It is left undefined if testContent & urlResource are both falsey.
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

				// Set up initial test UI if needed
				if (testContent || urlResource) {
					testHolder = testutils.getTestHolder();
				}

				// Set up the test content if required
				if (testContent) {
					testHolder.innerHTML = testContent;
				} else if (urlResource) {
					testutils.setUpExternalHTML(urlResource, testHolder);
					if (resetBeforeEach || resetAfterEach) {
						// Hold the initial HTML for future use
						testContent = testHolder.innerHTML;
					}
				}

				// Custom set up
				if ((realConfig = wcconfig.get("wc/ui/xhr")) && realConfig.messages) {
					wcconfig.set({ messages: null}, "wc/ui/xhr");
				}
				if ((realConfig = wcconfig.get("wc/ui/multiFileUploader")) && realConfig.messages) {
					wcconfig.set({ messages: null}, "wc/ui/multiFileUploader");
				}
			});
		},
		beforeEach: function () {
			if (testHolder && resetBeforeEach) {
				testHolder.innerHTML = testContent;
			}
		},
		afterEach: function() {
			if (testHolder && resetAfterEach) {
				testHolder.innerHTML = testContent;
			}
		},
		teardown: function () {
			if (testHolder) {
				testHolder.innerHTML = "";
			}
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