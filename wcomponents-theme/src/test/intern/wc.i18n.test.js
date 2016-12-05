define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var i18n;
		registerSuite({
			name: "i18n",
			setup: function() {
				var lang, docEl = document.documentElement;
				if (docEl) {
					lang = docEl.getAttribute("lang");
					if (lang) {
						docEl.setAttribute("data-wci18ntest-lang", lang);
						docEl.removeAttribute("lang");
					}
				}
				return testutils.setupHelper(["wc/i18n/i18n"], function(obj) {
					i18n = obj;
				});
			},
			afterEach: function() {
				var docEl = document.documentElement;
				if (docEl) {
					docEl.removeAttribute("lang");
				}
			},
			teardown: function() {
				var lang, docEl = document.documentElement;
				if (docEl) {
					lang = docEl.getAttribute("data-wci18ntest-lang");
					if (lang) {
						docEl.setAttribute("lang", lang);
					}
				}
			},
			testGet: function() {
				/*
				* In this test we simply test that we are getting a message when we ask
				* for one that we know exists.
				*/
				var key = "chars_remaining",
					result = i18n.get(key);
				assert.isTrue(result.length > 0);
			},
			testGetWithFormattingArgs: function() {
				/*
				* In this test we chose a message that accepts at least one printf arg
				* and check that it is inserted into the string.
				*/
				var arg = 3,
					key = "chars_remaining",
					result = i18n.get(key);
				assert.isTrue(result.indexOf(arg) === -1);
				result = i18n.get(key, arg);
				assert.isTrue(result.indexOf(arg) >= 0);
			},
			testGetWithSuperfluousFormattingArgs: function() {
				/*
				* In this test we chose a message that doesn't accept a printf arg,
				* pass it one anyway, and check that it is not inserted into the string.
				*/
				var arg = 3,
					key = "day4",
					result = i18n.get(key);
				assert.isTrue(result.indexOf(arg) === -1);
				result = i18n.get(key, arg);
				assert.isTrue(result.indexOf(arg) === -1);
			},
			testGetNoMessageFoundReturnsKey: function() {
				/*
				* In this test we test that the key is returned when we
				* ask for a message that does not exist.
				*/
				var key = "fukung_kungfu",
					result = i18n.get(key);
				assert.isTrue(result === key);
			},
			testGetWithFormattingArgsAndZero: function() {
				/*
				 * This test checks that primative zero is accepted as an arg.
				* This is based on a real-world defect we encountered.
				*/
				var arg = 0,
					key = "chars_remaining",
					result = i18n.get(key);
				assert.isTrue(result.indexOf(arg) === -1);
				result = i18n.get(key, arg);
				assert.isTrue(result.indexOf(arg) >= 0);
			},
			testGetLang: function() {
				var actual, expected = "de", element = document.createElement("span");
				element.setAttribute("lang", expected);
				actual = i18n._getLang(element);
				assert.equal(actual, expected);
				assert.notEqual(i18n._DEFAULT_LANG, expected, "This test should not test the fallback language");
			},
			testGetLangDefaultFallback: function() {
				var actual, expected = i18n._DEFAULT_LANG, element = document.createElement("span");
				actual = i18n._getLang(element);
				assert.equal(actual, expected);
			},
			testGetLangDocumentFallback: function() {
				var actual, expected = "wc", element = document.createElement("span");
				document.documentElement.setAttribute("lang", expected);
				actual = i18n._getLang(element);
				assert.equal(actual, expected);
			},
			testGetLangDocumentFallbackWithNoScope: function() {
				var actual, expected = "wc";
				document.documentElement.setAttribute("lang", expected);
				actual = i18n._getLang();
				assert.equal(actual, expected);
			},
			testGetLangGoogleTranslate: function() {
				var actual, expected = "it", element = document.createElement("span");
				element.setAttribute("lang", "it-x-mtfrom-en");  // see https://github.com/BorderTech/wcomponents/issues/994
				actual = i18n._getLang(element);
				assert.equal(actual, expected);
				assert.notEqual(i18n._DEFAULT_LANG, expected, "This test should not test the fallback language");
			}
		});
	});
