define(["intern!object", "intern/chai!assert", "wc/dom/diagnostic", "intern/resources/test.utils!"],
	function (registerSuite, assert, diagnostic, testutils) {
		"use strict";

		var testHolder,
			testContent = "<span id='wcdiagnostictest1' class='wc-fieldindicator'><span class='wc-message'>Message one</span></span>\n\
<span id='wcdiagnostictest_notdiagnostic'>placeholder</span>\n\
<span id='wcdiagnostictest_notmessage' class='wc-message'>not a message</span>";

		function getTestBox() {
			return document.getElementById("wcdiagnostictest1");
		}

		function getDummyElement() {
			return document.getElementById("wcdiagnostictest_notdiagnostic");
		}

		function getMessageElement() {
			var box = getTestBox();
			return box.firstElementChild;
		}

		function getFakeMessage() {
			return document.getElementById("wcdiagnostictest_notmessage");
		}

		function doIdExtensionTest(level, expected) {
			assert.strictEqual(diagnostic.getIdExtension(level), expected);
		}

		function doBoxClassType(level, expected) {
			assert.strictEqual(diagnostic.getBoxClass(level), expected);
		}

		function doGetByTypeTest(level) {
			var widget = diagnostic.getByType(),
				qs = widget.toString(),
				expected = qs + "." + diagnostic.getBoxClass(level),
				actual = diagnostic.getByType(level);
			assert.strictEqual(actual.toString(), expected);
		}

		function doIsOneOfMeTest(level) {
			var box = getTestBox(),
				other = getDummyElement(),
				className = diagnostic.getBoxClass(level),
				lvl;
			try {
				box.classList.add(className);
				other.classList.add(className);

				// a diagnostic box is a diagnostic box
				assert.isTrue(diagnostic.isOneOfMe(box), "Level class should still be a diagnostic");
				// matching level
				assert.isTrue(diagnostic.isOneOfMe(box, level), "Expected a diagnostic at a specific level");

				assert.isFalse(diagnostic.isOneOfMe(other, "Not a diagnistic box should never be a diagnostic box"));
				assert.isFalse(diagnostic.isOneOfMe(other, level), "The level classes should not be sufficient");

				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.hasOwnProperty(lvl)) {
						if (diagnostic.LEVEL[lvl] === level) {
							continue;
						}
						assert.isFalse(diagnostic.isOneOfMe(box, diagnostic.LEVEL[lvl]), "A diagnostic at one level should not be a diagnostic at other levels");
					}
				}
			} finally {
				box.classList.remove(className);
				other.classList.remove(className);
			}
		}

		function doIsMessageTest(level) {
			var box = getTestBox(),
				msg,
				className = diagnostic.getBoxClass(level),
				lvl;
			try {
				box.classList.add(className);
				msg = getMessageElement();

				// a diagnostic box is a diagnostic box
				assert.isTrue(diagnostic.isMessage(msg), "box with level should still hold a message");
				// matching level
				assert.isTrue(diagnostic.isMessage(msg, level), "Expected a message at a specific level");

				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.hasOwnProperty(lvl)) {
						if (diagnostic.LEVEL[lvl] === level) {
							continue;
						}
						assert.isFalse(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl]), "A message at one level should not be a message at other levels");
					}
				}
			} finally {
				box.classList.remove(className);
			}
		}

		function doGetLevelTest(level) {
			var box = getTestBox(),
				className = diagnostic.getBoxClass(level),
				lvl;
			try {
				box.classList.add(className);
				assert.strictEqual(diagnostic.getLevel(box), level, "Level class should be sufficient");
				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.hasOwnProperty(lvl)) {
						if (diagnostic.LEVEL[lvl] === level) {
							continue;
						}
						assert.notStrictEqual(diagnostic.getLevel(box), diagnostic.LEVEL[lvl], "Unexpected level match");
					}
				}
			} finally {
				box.classList.remove(className);
			}
		}

		function doGetTargetTest(level) {
			var box = getTestBox(),
				className = diagnostic.getBoxClass(level),
				target = document.createElement("span"),
				expected = "foo";

			target.id = expected;
			testHolder.appendChild(target);
			box.setAttribute("data-wc-dfor", expected);
			box.classList.add(className);
			assert.strictEqual((diagnostic.getTarget(box).id), expected);
		}

		registerSuite({
			name: "wc/dom/diagnostic",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},

			testGotController: function () {
				assert.typeOf(diagnostic, "object", "Expected the test module to be available as an object otherwise the tests won't work.");
			},
			testGetIdExtensionERROR: function() {
				doIdExtensionTest(diagnostic.LEVEL.ERROR, "_err");
			},
			testGetIdExtensionWARN: function() {
				doIdExtensionTest(diagnostic.LEVEL.WARN, "_wrn");
			},
			testGetIdExtensionINFO: function() {
				doIdExtensionTest(diagnostic.LEVEL.INFO, "_nfo");
			},
			testGetIdExtensionSUCCESS: function() {
				doIdExtensionTest(diagnostic.LEVEL.SUCCESS, "_scc");
			},
			testGetIdExtensionFalsey: function() {
				doIdExtensionTest(false, "_err");
			},
			testGetIdExtensionOther: function() {
				doIdExtensionTest(-1, "_err");
			},
			testGetBoxClassERROR: function() {
				doBoxClassType(diagnostic.LEVEL.ERROR, "wc-fieldindicator-type-error");
			},
			testGetBoxClassWARN: function() {
				doBoxClassType(diagnostic.LEVEL.WARN, "wc-fieldindicator-type-warn");
			},
			testGetBoxClassINFO: function() {
				doBoxClassType(diagnostic.LEVEL.INFO, "wc-fieldindicator-type-info");
			},
			testGetBoxClassSUCCESS: function() {
				doBoxClassType(diagnostic.LEVEL.SUCCESS, "wc-fieldindicator-type-success");
			},
			testGetBoxClassFalsey: function() {
				doBoxClassType(false, "wc-fieldindicator");
			},
			testGetBoxClassOther: function() {
				assert.isNull(diagnostic.getBoxClass(-1));
			},
			testGetWidget: function() {
				var widget = diagnostic.getWidget();
				assert.isOk(widget);
				// the folowing fails in IE 11 (of course)
				// assert.strictEqual(widget.constructor.name, "Widget");
			},
			testGetWidgetIsCorrectWidget: function() {
				var widget = diagnostic.getWidget(),
					element = getTestBox();
				assert.isTrue(widget.isOneOfMe(element));
			},
			testGetMessage: function() {
				var widget = diagnostic.getMessage();
				assert.isOk(widget);
				// the folowing fails in IE 11 (of course)
				// assert.strictEqual(widget.constructor.name, "Widget");
			},
			testGetMessageIsCorrectWidget: function() {
				var widget = diagnostic.getMessage(),
					element = getMessageElement();
				assert.isTrue(widget.isOneOfMe(element));
			},
			testGetByTypeNoType: function() {
				var expected = diagnostic.getWidget(),
					actual = diagnostic.getByType();
				assert.strictEqual(actual, expected);
			},
			testGetByTypeInvalidType: function() {
				assert.isNull(diagnostic.getByType(-1));
			},
			testGetByType: function() {
				var lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						doGetByTypeTest(diagnostic.LEVEL[lvl]);
					}
				}
			},
			testIsOneOfMeNoElement: function() {
				assert.isFalse(diagnostic.isOneOfMe(null));
			},
			testIsOneOfMeNoLevel_withBox: function() {
				var box = getTestBox();
				assert.isTrue(diagnostic.isOneOfMe(box));
			},
			testIsOneOfMeNoLevel_notDiagnostic: function() {
				var box = getDummyElement();
				assert.isFalse(diagnostic.isOneOfMe(box));
			},
			testIsOneOfMeWithLevel_false: function() {
				var box = getTestBox(),lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(diagnostic.isOneOfMe(box, diagnostic.LEVEL[lvl]), "Unexpected match '" + box.className +
							"' should not match '" + diagnostic.getBoxClass(diagnostic.LEVEL[lvl]));
					}
				}
			},
			testIsOneOfMe: function() {
				var lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						doIsOneOfMeTest(diagnostic.LEVEL[lvl]);
					}
				}
			},
			testIsMessage_noElement: function() {
				assert.isFalse(diagnostic.isMessage(null));
			},
			testIsMessage_notAnElement: function() {
				assert.isFalse(diagnostic.isMessage("I am a message"), "Only an Element can be a message");
			},
			testIsMessage_noLevel_notAMessage: function() {
				var msg = getFakeMessage();
				assert.isFalse(diagnostic.isMessage(msg));
			},
			testIsMessage_withLevel_notAMessage: function() {
				var msg = getFakeMessage(), lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl]));
					}
				}
			},
			testIsMessage_noLevel_message: function() {
				var msg = getMessageElement();
				assert.isTrue(diagnostic.isMessage(msg));
			},
			testIsMessage_level_messageInDefaultBox: function() {
				var msg = getMessageElement(), lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl]));
					}
				}
			},
			testIsMessage_level_messageInLevelBox: function() {
				var lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						doIsMessageTest(diagnostic.LEVEL[lvl]);
					}
				}
			},
			testIsMessage_badLevel: function() {
				var msg = getMessageElement();
				assert.isFalse(diagnostic.isMessage(msg, -1));
			},
			testGetLevel_noArg: function() {
				try {
					diagnostic.getLevel();
					assert.isTrue(false, "expect error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testGetLevel_badArg: function() {
				try {
					diagnostic.getLevel(getDummyElement());
					assert.isTrue(false, "expect error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testGetLevel_noLevel: function() {
				assert.strictEqual(diagnostic.getLevel(getTestBox()), -1);
			},
			testGetLevel: function() {
				var lvl;
				for (lvl in diagnostic.LEVEL) {
					if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
						doGetLevelTest(diagnostic.LEVEL[lvl]);
					}
				}
			},
			testGetTarget_noArg: function() {
				assert.isNull(diagnostic.getTarget(null));
			},
			testGetTarget_notElement: function() {
				assert.isNull(diagnostic.getTarget("I am an element"));
			},
			testGetTarget_notDiagnostic: function() {
				assert.isNull(diagnostic.getTarget(getDummyElement()));
			},
			testGetTarget_diagnosticNoId: function() {
				var box = getTestBox();
				box.removeAttribute("data-wc-dfor");
				assert.isNull(diagnostic.getTarget(box), "well that was unexpected");
			},
			// get target tests are cumbersome so they are separated into different functions to allow the base html to be reset between tests
			testGetTarget_ERROR: function() {
				doGetTargetTest(diagnostic.LEVEL.ERROR);
			},
			testGetTarget_WARN: function() {
				doGetTargetTest(diagnostic.LEVEL.WARN);
			},
			testGetTarget_INFO: function() {
				doGetTargetTest(diagnostic.LEVEL.INFO);
			},
			testGetTarget_SUCCESS: function() {
				doGetTargetTest(diagnostic.LEVEL.SUCCESS);
			},
			testGetTarget_noTarget: function() {
				var box = getTestBox();
				assert.isNull(diagnostic.getTarget(box));
			},
			testGetBoxNoTarget: function() {
				var box = getTestBox();
				box.id = box.id + "_err"; // make a real diagnostic box with no target element.
				assert.isNull(diagnostic.getTarget(box));
			},
			testStupidMadeUpTestForGetTargetBranchConverage: function() {
				var box = getTestBox();
				box.id = "_err"; // the silly default which should not be a default
				assert.isNull(diagnostic.getTarget(box));
			}
		});
	});
