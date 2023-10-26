import i18n from "wc/i18n/i18n.mjs";

describe("wc/i18n", function() {

	beforeAll(function() {
		const docEl = document.documentElement;
		if (docEl) {
			const lang = docEl.getAttribute("lang");
			if (lang) {
				docEl.setAttribute("data-wci18ntest-lang", lang);
				docEl.removeAttribute("lang");
			}
		}
	});

	afterEach(function() {
		const docEl = document.documentElement;
		if (docEl) {
			docEl.removeAttribute("lang");
		}
	});

	afterAll(function() {
		const docEl = document.documentElement;
		if (docEl) {
			const lang = docEl.getAttribute("data-wci18ntest-lang");
			if (lang) {
				docEl.setAttribute("lang", lang);
			}
		}
	});

	it("testGet", function() {
		/*
		 * In this test we simply test that we are getting a message when we ask
		 * for one that we know exists.
		 */
		const key = "chars_remaining",
			result = i18n.get(key);
		expect(result.length > 0).toBeTrue();
	});

	it("testGetWithFormattingArgs", function() {
		/*
		 * In this test we chose a message that accepts at least one printf arg
		 * and check that it is inserted into the string.
		 */
		const arg = "3",
			key = "chars_remaining";
		let result = i18n.get(key);
		expect(result).not.toContain(arg);
		result = i18n.get(key, arg);
		expect(result).toContain(arg);
	});

	it("testGetWithSuperfluousFormattingArgs", function() {
		/*
		 * In this test we chose a message that doesn't accept a printf arg,
		 * pass it one anyway, and check that it is not inserted into the string.
		 */
		const arg = "3",
			key = "day4";
		let result = i18n.get(key);
		expect(result).not.toContain(arg);
		result = i18n.get(key, arg);
		expect(result).not.toContain(arg);
	});

	it("testGetNoMessageFoundReturnsKey", function() {
		/*
		 * In this test we test that the key is returned when we
		 * ask for a message that does not exist.
		 */
		const key = "fukung_kungfu",
			result = i18n.get(key);
		expect(result === key).toBeTrue();
	});

	it("testGetWithFormattingArgsAndZero", function() {
		/*
		 * This test checks that primative zero is accepted as an arg.
		 * This is based on a real-world defect we encountered.
		 */
		const arg = 0,
			key = "chars_remaining";
		let result = i18n.get(key);
		expect(result).not.toContain(`${arg}`);
		result = i18n.get(key, arg);
		expect(result).toContain(`${arg}`);
	});


	it("testGetKeyArray", function() {
		return new Promise(function(win, lose) {
			try {
				i18n.translate("chars_remaining", 2).then(function(charsRemaining) {
					i18n.translate("day4").then(function(day4) {
						/*
						 * Check that we receive an array of translations in the correct order.
						 */
						const expected = [day4, charsRemaining],
							key = ["day4", "chars_remaining"],
							actual = i18n.get(key, 2);
						if (Array.isArray(actual)) {
							expect(actual.join()).toEqual(expected.join());
						} else {
							lose(`${actual} should be an array`);
						}
						win();
					}, lose);
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslate", function() {
		return new Promise(function(win, lose) {
			const key = "chars_remaining";
			try {
				/*
				 * In this test we simply test that we are getting a message when we ask
				 * for one that we know exists.
				 */
				i18n.translate(key).then(function(result) {
					expect(result.length > 0).toBeTrue();
					win();
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslateKeyArray", function() {
		return new Promise(function(win, lose) {
			try {
				i18n.translate("chars_remaining", 2).then(function(charsRemaining) {
					i18n.translate("day4").then(function(day4) {
						/*
						 * Check that we receive an array of translations in the correct order.
						 */
						const expected = [day4, charsRemaining],
							key = ["day4", "chars_remaining"];
						i18n.translate(key, 2).then(function(result) {
							if (Array.isArray(result)) {
								expect(result.join()).toEqual(expected.join());
							} else {
								lose(`${result} should be an array`);
							}

							win();
						}, lose);
					}, lose);
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslateKeyArrayDodgyKeys", function() {
		return new Promise(function(win, lose) {
			try {
				i18n.translate("chars_remaining").then(function(charsRemaining) {
					i18n.translate("day4").then(function(day4) {
						/*
						 * Check that we receive an array of translations in the correct order.
						 */
						const key = ["day4", null, "chars_remaining", "fukung_kungfu"];
						i18n.translate(key).then(function(result) {
							expect(result.length).withContext("should get a translation for each key").toEqual(key.length);
							expect(result[0]).toEqual(day4);
							expect(result[2]).toEqual(charsRemaining);
							win();
						}, lose);
					}, lose);
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslateWithFormattingArgs", function() {
		return new Promise(function(win, lose) {
			const arg = 3,
				key = "chars_remaining";
			try {
				/*
				 * In this test we chose a message that accepts at least one printf arg
				 * and check that it is inserted into the string.
				 */
				i18n.translate(key).then(function(result) {
					expect(result).not.toContain(`${arg}`);
				}, lose).then(function() {
					i18n.translate(key, arg).then(function(result) {
						expect(result).toContain(`${arg}`);
						win();
					}, lose);
				});

			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslateWithSuperfluousFormattingArgs", function() {
		return new Promise(function(win, lose) {
			const arg = 3,
				key = "day4";
			/*
			 * In this test we chose a message that doesn't accept a printf arg,
			 * pass it one anyway, and check that it is not inserted into the string.
			 */
			try {
				i18n.translate(key).then(function (result) {
					expect(result).not.toContain(`${arg}`);
				}).then(function () {
					i18n.translate(key, arg).then(function (result) {
						expect(result).not.toContain(`${arg}`);
						win();
					}, lose);
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testTranslateNoMessageFoundReturnsKey", function() {
		return new Promise(function(win, lose) {
			const key = "fukung_kungfu";
			try {
				/*
				 * In this test we test that the key is returned when we
				 * ask for a message that does not exist.
				 */
				i18n.translate(key).then(function(result) {
					expect(result === key).toBeTrue();
					win();
				}, lose);
			} catch (ex) {
				lose(ex);
			}
		});
	});


	it("testTranslateWithFormattingArgsAndZero", function() {
		return new Promise(function(win, lose) {
			const arg = 0,
				key = "chars_remaining";
			try {
				/*
				 * This test checks that primitive zero is accepted as an arg.
				 * This is based on a real-world defect we encountered.
				 */
				i18n.translate(key).then(function(result) {
					expect(result).not.toContain(`${arg}`);
				}, lose).then(function() {
					i18n.translate(key, arg).then(function(result) {
						expect(result).toContain(`${arg}`);
						win();
					}, lose);
				});
			} catch (ex) {
				lose(ex);
			}
		});
	});

	it("testGetLang", function() {
		const expected = "de", element = document.createElement("span");
		element.setAttribute("lang", expected);
		const actual = i18n._getLang(element);
		expect(actual).toEqual(expected);
		expect(i18n._DEFAULT_LANG).withContext("This test should not test the fallback language").not.toEqual(expected);
	});

	it("testGetLangDefaultFallback", function() {
		const expected = i18n._DEFAULT_LANG, element = document.createElement("span");
		const actual = i18n._getLang(element);
		expect(actual).toEqual(expected);
	});

	it("testGetLangDocumentFallback", function() {
		const expected = "wc", element = document.createElement("span");
		document.documentElement.setAttribute("lang", expected);
		const actual = i18n._getLang(element);
		expect(actual).toEqual(expected);
	});

	it("testGetLangDocumentFallbackWithNoScope", function() {
		const expected = "wc";
		document.documentElement.setAttribute("lang", expected);
		const actual = i18n._getLang();
		expect(actual).toEqual(expected);
	});

	it("testGetLangGoogleTranslate", function() {
		const expected = "it", element = document.createElement("span");
		element.setAttribute("lang", "it-x-mtfrom-en");  // see https://github.com/BorderTech/wcomponents/issues/994
		const actual = i18n._getLang(element);
		expect(actual).toEqual(expected);
		expect(i18n._DEFAULT_LANG).withContext("This test should not test the fallback language").not.toEqual(expected);
	});

});
