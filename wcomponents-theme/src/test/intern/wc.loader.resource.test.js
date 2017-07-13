define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var resourceLoader;

		registerSuite({
			name: "ResourceLoader",
			setup: function() {
				var result = testutils.setupHelper(["wc/loader/resource"]).then(function(arr) {
					resourceLoader = arr[0];
					resourceLoader._theRealFetch = resourceLoader._fetch;
				});
				return result;
			},
			afterEach: function() {
				resourceLoader._fetch = resourceLoader._theRealFetch;
			},
			testLoader: function() {
				var resolvers = [], promises = [],
					i, fetchCount = {
						foo: 0,
						bar: 0
					}, called = {
						foo: 0,
						bar: 0
					}, calledExpected = {
						foo: 4,
						bar: 2
					};
				resourceLoader._fetch = function(name) {
					var simpleName;
					if (name.indexOf("foo") > 0) {
						simpleName = "foo";
					} else if (name.indexOf("bar") > 0) {
						simpleName = "bar";
					}
					fetchCount[simpleName]++;
					return new Promise(function(win) {
						resolvers.push(function() {
							win(simpleName);
						});
					});
				};
				function callback(reponse) {
					return called[reponse]++;
				}
				for (i = 0; i < calledExpected.foo; i++) {
					promises.push(resourceLoader.load("foo", true, true).then(callback));
				}
				for (i = 0; i < calledExpected.bar; i++) {
					promises.push(resourceLoader.load("bar", true, true).then(callback));
				}
				window.setTimeout(function() {
					for (i = 0; i < resolvers.length; i++) {
						resolvers[i]();
					}
				}, 100);
				return Promise.all(promises).then(function() {
					assert.strictEqual(1, fetchCount.foo, "Should have throttled AJAX calls foo");
					assert.strictEqual(1, fetchCount.bar, "Should have throttled AJAX calls bar");
					assert.strictEqual(calledExpected.foo, called.foo, "Should have called every subscriber foo");
					assert.strictEqual(calledExpected.bar, called.bar, "Should have called every subscriber bar");
				});
			},
			testLoaderRepeatedCalls: function() {
				// This tests that requests are passed through as normal when there is no matching request inflight.
				var fetchCount = 0, called = 0;
				resourceLoader._fetch = function() {
					fetchCount++;
					return Promise.resolve();
				};
				function callback() {
					return called++;
				}
				return resourceLoader.load("foo", true, true).then(callback).then(function() {
					return resourceLoader.load("foo", true, true).then(callback).then(function() {
						assert.strictEqual(2, fetchCount, "Should have fetched twice");
						assert.strictEqual(2, called, "Should have been called twice");
					});
				});
			}
		});
	});
