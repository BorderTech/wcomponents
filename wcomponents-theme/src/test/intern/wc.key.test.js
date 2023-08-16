define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!", "/node_modules/@testing-library/dom/dist/@testing-library/dom.umd.js"],
	function (registerSuite, assert, testutils, domTesting) {
		"use strict";

		let key, container, testId = "test-element";

		registerSuite({
			name: "key",
			setup: function() {
				return testutils.setupHelper(["wc/key"], function(obj) {
					key = obj;
					container = testutils.getTestHolder();
					container.innerHTML = `<button type="button" data-testid="${testId}"></button>`;
				});
			},
			"testIsMetaWithNoMeta": function() {
				return new Promise((win, lose) => {
					container.addEventListener("keydown", (event) => {
						try {
							assert.isFalse(key.isMeta(event.key));
							assert.isFalse(key.isMeta(event.code));
							assert.isFalse(key.isMeta(event));
							win();
						} catch (ex) {
							lose(ex.message);
						}
					});
					const element = domTesting.getByTestId(container, testId);
					domTesting.fireEvent.keyDown(element, {
						key: 'A', code: 'KeyA'
					});
				});
			},
			"testIsMetaWithLeftShift": function() {
				return metaTest('Shift', 'ShiftLeft');
			},
			"testIsMetaWithRightShift": function() {
				return metaTest('Shift', 'ShiftRight');
			},
			"testIsMetaWithRightAlt": function() {
				return metaTest('Alt', 'AltRight');
			},
			"testIsMetaWithLeftAlt": function() {
				return metaTest('Alt', 'AltLeft');
			},
			"testIsMetaWithLeftMeta": function() {
				return metaTest('Meta', 'MetaLeft');
			},
			"testIsMetaWithRightMeta": function() {
				return metaTest('Meta', 'MetaRight');
			},
			"testIsMetaWithRightControl": function() {
				return metaTest('Control', 'ControlRight');
			},
			"testIsMetaWithLefttControl": function() {
				return metaTest('Control', 'ControlLeft');
			}
		});

		function metaTest(eventKey, eventCode) {
			return new Promise((win, lose) => {
				container.addEventListener("keydown", (event) => {
					try {
						assert.isTrue(key.isMeta(event.key));
						assert.isTrue(key.isMeta(event.code));
						assert.isTrue(key.isMeta(event));
						win();
					} catch (ex) {
						lose(ex.message);
					}

				});
				const element = domTesting.getByTestId(container, testId);
				domTesting.fireEvent.keyDown(element, {
					key: eventKey, code: eventCode
				});
			});
		}
	});
