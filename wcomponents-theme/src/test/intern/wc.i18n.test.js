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
				return i18n.initialize().then(function() {
					/*
					 * In this test we simply test that we are getting a message when we ask
					 * for one that we know exists.
					 */
					var key = "chars_remaining",
						result = i18n.get(key);
					assert.isTrue(result.length > 0);
				});
			},
			testGetWithFormattingArgs: function() {
				return i18n.initialize().then(function() {
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
				});
			},
			testGetWithSuperfluousFormattingArgs: function() {
				return i18n.initialize().then(function() {
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
				});
			},
			testGetNoMessageFoundReturnsKey: function() {
				return i18n.initialize().then(function() {
					/*
					 * In this test we test that the key is returned when we
					 * ask for a message that does not exist.
					 */
					var key = "fukung_kungfu",
						result = i18n.get(key);
					assert.isTrue(result === key);
				});
			},
			testGetWithFormattingArgsAndZero: function() {
				return i18n.initialize().then(function() {
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
				});
			},
			testGetKeyArray: function() {
				return new Promise(function(win, lose) {
					try {
						i18n.translate("chars_remaining", 2).then(function(charsRemaining) {
							i18n.translate("day4").then(function(day4) {
								/*
								 * Check that we receive an array of translations in the correct order.
								 */
								var expected = [day4, charsRemaining],
									key = ["day4", "chars_remaining"],
									actual = i18n.get(key, 2);
								assert.equal(actual.join(), expected.join());
								win();
							}, lose);
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
			},
			testTranslate: function() {
				return new Promise(function(win, lose) {
					try {
						/*
						 * In this test we simply test that we are getting a message when we ask
						 * for one that we know exists.
						 */
						var key = "chars_remaining";
						i18n.translate(key).then(function(result) {
							assert.isTrue(result.length > 0);
							win();
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
			},
			testTranslateKeyArray: function() {
				return new Promise(function(win, lose) {
					try {
						i18n.translate("chars_remaining", 2).then(function(charsRemaining) {
							i18n.translate("day4").then(function(day4) {
								/*
								 * Check that we receive an array of translations in the correct order.
								 */
								var expected = [day4, charsRemaining],
									key = ["day4", "chars_remaining"];
								i18n.translate(key, 2).then(function(result) {
									assert.equal(result.join(), expected.join());
									win();
								}, lose);
							}, lose);
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
			},
			testTranslateKeyArrayDodgyKeys: function() {
				return new Promise(function(win, lose) {
					try {
						i18n.translate("chars_remaining").then(function(charsRemaining) {
							i18n.translate("day4").then(function(day4) {
								/*
								 * Check that we receive an array of translations in the correct order.
								 */
								var key = ["day4", null, "chars_remaining", "fukung_kungfu"];
								i18n.translate(key).then(function(result) {
									assert.equal(result.length, key.length, "should get a translation for each key");
									assert.equal(result[0], day4);
									assert.equal(result[2], charsRemaining);
									win();
								}, lose);
							}, lose);
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
			},
			testTranslateWithFormattingArgs: function() {
				return new Promise(function(win, lose) {
					try {
						/*
						 * In this test we chose a message that accepts at least one printf arg
						 * and check that it is inserted into the string.
						 */
						var arg = 3,
							key = "chars_remaining";
						i18n.translate(key).then(function(result) {
							assert.isTrue(result.indexOf(arg) === -1);
						}, lose).then(function() {
							i18n.translate(key, arg).then(function(result) {
								assert.isTrue(result.indexOf(arg) >= 0);
								win();
							}, lose);
						});

					} catch (ex) {
						lose(ex);
					}
				});

			},
			testTranslateWithSuperfluousFormattingArgs: function() {
				return new Promise(function(win, lose) {
					/*
					 * In this test we chose a message that doesn't accept a printf arg,
					 * pass it one anyway, and check that it is not inserted into the string.
					 */
					var arg = 3,
						key = "day4";
					try {
						i18n.translate(key).then(function (result) {
							assert.isTrue(result.indexOf(arg) === -1);
						}).then(function () {
							i18n.translate(key, arg).then(function (result) {
								assert.isTrue(result.indexOf(arg) === -1);
								win();
							}, lose);
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
			},
			testTranslateNoMessageFoundReturnsKey: function() {
				return new Promise(function(win, lose) {
					try {
						/*
						 * In this test we test that the key is returned when we
						 * ask for a message that does not exist.
						 */
						var key = "fukung_kungfu";
						i18n.translate(key).then(function(result) {
							assert.isTrue(result === key);
							win();
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});

			},
			testTranslateWithFormattingArgsAndZero: function() {
				return new Promise(function(win, lose) {
					try {
						/*
						 * This test checks that primative zero is accepted as an arg.
						 * This is based on a real-world defect we encountered.
						 */
						var arg = 0,
							key = "chars_remaining";

						i18n.translate(key).then(function(result) {
							assert.isTrue(result.indexOf(arg) === -1);
						}, lose).then(function() {
							i18n.translate(key, arg).then(function(result) {
								assert.isTrue(result.indexOf(arg) >= 0);
								win();
							}, lose);
						});
					} catch (ex) {
						lose(ex);
					}
				});
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
