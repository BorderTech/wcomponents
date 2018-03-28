define(["intern!object", "intern/chai!assert", "wc/dom/diagnostic", "wc/dom/classList", "./resources/test.utils!"],
	function (registerSuite, assert, controller, classList, testutils) {
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
			assert.strictEqual(controller.getIdExtension(level), expected);
		}

		function doBoxClassType(level, expected) {
			assert.strictEqual(controller.getBoxClass(level), expected);
		}

		function doGetByTypeTest(level) {
			var widget = controller.getByType(),
				qs = widget.toString(),
				expected = qs + "." + controller.getBoxClass(level),
				actual = controller.getByType(level);
			assert.strictEqual(actual.toString(), expected);
		}

		function doIsOneOfMeTest(level) {
			var box = getTestBox(),
				other = getDummyElement(),
				className = controller.getBoxClass(level),
				lvl;
			try {
				classList.add(box, className);
				classList.add(other, className);

				// a diagnostic box is a diagnostic box
				assert.isTrue(controller.isOneOfMe(box), "Level class should still be a diagnostic");
				// matching level
				assert.isTrue(controller.isOneOfMe(box, level), "Expected a diagnostic at a specific level");

				assert.isFalse(controller.isOneOfMe(other, "Not a diagnistic box should never be a diagnostic box"));
				assert.isFalse(controller.isOneOfMe(other, level), "The level classes should not be sufficient");

				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in controller.LEVEL) {
					if (controller.hasOwnProperty(lvl)) {
						if (controller.LEVEL[lvl] === level) {
							continue;
						}
						assert.isFalse(controller.isOneOfMe(box, controller.LEVEL[lvl]), "A diagnostic at one level should not be a diagnostic at other levels");
					}
				}
			} finally {
				classList.remove(box, className);
				classList.remove(other, className);
			}
		}

		function doIsMessageTest(level) {
			var box = getTestBox(),
				msg,
				className = controller.getBoxClass(level),
				lvl;
			try {
				classList.add(box, className);
				msg = getMessageElement();

				// a diagnostic box is a diagnostic box
				assert.isTrue(controller.isMessage(msg), "box with level should still hold a message");
				// matching level
				assert.isTrue(controller.isMessage(msg, level), "Expected a message at a specific level");

				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in controller.LEVEL) {
					if (controller.hasOwnProperty(lvl)) {
						if (controller.LEVEL[lvl] === level) {
							continue;
						}
						assert.isFalse(controller.isMessage(msg, controller.LEVEL[lvl]), "A message at one level should not be a message at other levels");
					}
				}
			} finally {
				classList.remove(box, className);
			}
		}

		function doGetLevelTest(level) {
			var box = getTestBox(),
				className = controller.getBoxClass(level),
				lvl;
			try {
				classList.add(box, className);
				assert.strictEqual(controller.getLevel(box), level, "Level class should be sufficient");
				// a diagnostic box at any one level is NOT a box at other levels
				for (lvl in controller.LEVEL) {
					if (controller.hasOwnProperty(lvl)) {
						if (controller.LEVEL[lvl] === level) {
							continue;
						}
						assert.notStrictEqual(controller.getLevel(box), controller.LEVEL[lvl], "Unexpected level match");
					}
				}
			} finally {
				classList.remove(box, className);
			}
		}

		function doGetTargetTest(level) {
			var box = getTestBox(),
				className = controller.getBoxClass(level),
				target = document.createElement("span"),
				expected = "foo";

			target.id = expected;
			testHolder.appendChild(target);
			box.setAttribute("data-wc-dfor", expected);
			classList.add(box, className);
			assert.strictEqual((controller.getTarget(box).id), expected);
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
				assert.typeOf(controller, "object", "Expected the test module to be available as an object otherwise the tests won't work.");
			},
			testGetIdExtensionERROR: function() {
				doIdExtensionTest(controller.LEVEL.ERROR, "_err");
			},
			testGetIdExtensionWARN: function() {
				doIdExtensionTest(controller.LEVEL.WARN, "_wrn");
			},
			testGetIdExtensionINFO: function() {
				doIdExtensionTest(controller.LEVEL.INFO, "_nfo");
			},
			testGetIdExtensionSUCCESS: function() {
				doIdExtensionTest(controller.LEVEL.SUCCESS, "_scc");
			},
			testGetIdExtensionFalsey: function() {
				doIdExtensionTest(false, "_err");
			},
			testGetIdExtensionOther: function() {
				doIdExtensionTest(-1, "_err");
			},
			testGetBoxClassERROR: function() {
				doBoxClassType(controller.LEVEL.ERROR, "wc-fieldindicator-type-error");
			},
			testGetBoxClassWARN: function() {
				doBoxClassType(controller.LEVEL.WARN, "wc-fieldindicator-type-warn");
			},
			testGetBoxClassINFO: function() {
				doBoxClassType(controller.LEVEL.INFO, "wc-fieldindicator-type-info");
			},
			testGetBoxClassSUCCESS: function() {
				doBoxClassType(controller.LEVEL.SUCCESS, "wc-fieldindicator-type-success");
			},
			testGetBoxClassFalsey: function() {
				doBoxClassType(false, "wc-fieldindicator");
			},
			testGetBoxClassOther: function() {
				assert.isNull(controller.getBoxClass(-1));
			},
			testMessageClass: function() {
				assert.strictEqual(controller.getMessageClass(), "wc-message");
			},
			testGetWidget: function() {
				var widget = controller.getWidget();
				assert.isOk(widget);
				// the folowing fails in IE 11 (of course)
				// assert.strictEqual(widget.constructor.name, "Widget");
			},
			testGetWidgetIsCorrectWidget: function() {
				var widget = controller.getWidget(),
					element = getTestBox();
				assert.isTrue(widget.isOneOfMe(element));
			},
			testGetMessage: function() {
				var widget = controller.getMessage();
				assert.isOk(widget);
				// the folowing fails in IE 11 (of course)
				// assert.strictEqual(widget.constructor.name, "Widget");
			},
			testGetMessageIsCorrectWidget: function() {
				var widget = controller.getMessage(),
					element = getMessageElement();
				assert.isTrue(widget.isOneOfMe(element));
			},
			testGetByTypeNoType: function() {
				var expected = controller.getWidget(),
					actual = controller.getByType();
				assert.strictEqual(actual, expected);
			},
			testGetByTypeInvalidType: function() {
				assert.isNull(controller.getByType(-1));
			},
			testGetByType: function() {
				var lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						doGetByTypeTest(controller.LEVEL[lvl]);
					}
				}
			},
			testIsOneOfMeNoElement: function() {
				assert.isFalse(controller.isOneOfMe(null));
			},
			testIsOneOfMeNoLevel_withBox: function() {
				var box = getTestBox();
				assert.isTrue(controller.isOneOfMe(box));
			},
			testIsOneOfMeNoLevel_notDiagnostic: function() {
				var box = getDummyElement();
				assert.isFalse(controller.isOneOfMe(box));
			},
			testIsOneOfMeWithLevel_false: function() {
				var box = getTestBox(),lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(controller.isOneOfMe(box, controller.LEVEL[lvl]), "Unexpected match '" + box.className +
							"' should not match '" + controller.getBoxClass(controller.LEVEL[lvl]));
					}
				}
			},
			testIsOneOfMe: function() {
				var lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						doIsOneOfMeTest(controller.LEVEL[lvl]);
					}
				}
			},
			testIsMessage_noElement: function() {
				assert.isFalse(controller.isMessage(null));
			},
			testIsMessage_notAnElement: function() {
				assert.isFalse(controller.isMessage("I am a message"), "Only an Element can be a message");
			},
			testIsMessage_noLevel_notAMessage: function() {
				var msg = getFakeMessage();
				assert.isFalse(controller.isMessage(msg));
			},
			testIsMessage_withLevel_notAMessage: function() {
				var msg = getFakeMessage(), lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(controller.isMessage(msg, controller.LEVEL[lvl]));
					}
				}
			},
			testIsMessage_noLevel_message: function() {
				var msg = getMessageElement();
				assert.isTrue(controller.isMessage(msg));
			},
			testIsMessage_level_messageInDefaultBox: function() {
				var msg = getMessageElement(), lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						assert.isFalse(controller.isMessage(msg, controller.LEVEL[lvl]));
					}
				}
			},
			testIsMessage_level_messageInLevelBox: function() {
				var lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						doIsMessageTest(controller.LEVEL[lvl]);
					}
				}
			},
			testIsMessage_badLevel: function() {
				var msg = getMessageElement();
				assert.isFalse(controller.isMessage(msg, -1));
			},
			testGetLevel_noArg: function() {
				try {
					controller.getLevel();
					assert.isTrue(false, "expect error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testGetLevel_badArg: function() {
				try {
					controller.getLevel(getDummyElement());
					assert.isTrue(false, "expect error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testGetLevel_noLevel: function() {
				assert.strictEqual(controller.getLevel(getTestBox()), -1);
			},
			testGetLevel: function() {
				var lvl;
				for (lvl in controller.LEVEL) {
					if (controller.LEVEL.hasOwnProperty(lvl)) {
						doGetLevelTest(controller.LEVEL[lvl]);
					}
				}
			},
			testGetTarget_noArg: function() {
				assert.isNull(controller.getTarget(null));
			},
			testGetTarget_notElement: function() {
				assert.isNull(controller.getTarget("I am an element"));
			},
			testGetTarget_notDiagnostic: function() {
				assert.isNull(controller.getTarget(getDummyElement()));
			},
			testGetTarget_diagnosticNoId: function() {
				var box = getTestBox();
				box.removeAttribute("data-wc-dfor");
				assert.isNull(controller.getTarget(box), "well that was unexpected");
			},
			// get target tests are cumbersome so they are separated into different functions to allow the base html to be reset between tests
			testGetTarget_ERROR: function() {
				doGetTargetTest(controller.LEVEL.ERROR);
			},
			testGetTarget_WARN: function() {
				doGetTargetTest(controller.LEVEL.WARN);
			},
			testGetTarget_INFO: function() {
				doGetTargetTest(controller.LEVEL.INFO);
			},
			testGetTarget_SUCCESS: function() {
				doGetTargetTest(controller.LEVEL.SUCCESS);
			},
			testGetTarget_noTarget: function() {
				var box = getTestBox();
				assert.isNull(controller.getTarget(box));
			},
			testGetBoxNoTarget: function() {
				var box = getTestBox();
				box.id = box.id + "_err"; // make a real diagnostic box with no target element.
				assert.isNull(controller.getTarget(box));
			},
			testStupidMadeUpTestForGetTargetBranchConverage: function() {
				var box = getTestBox();
				box.id = "_err"; // the silly default which should not be a default
				assert.isNull(controller.getTarget(box));
			}
		});
	});
