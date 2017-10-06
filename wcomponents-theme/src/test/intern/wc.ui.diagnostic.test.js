define(["intern!object", "intern/chai!assert", "wc/ui/diagnostic", "wc/dom/diagnostic", "./resources/test.utils!"],
	function (registerSuite, assert, controller, diagnostic, testutils) {
		"use strict";

		var testHolder,
			targetId = "wrapper",
			inputId = "wrappedinput",
			testBoxId = "wcdiagnostictest1_err",
			testTargetHTML = "<span class='wc-input-wrapper' id='" + targetId + "'><input id='" + inputId + "' type='text'></span>",
			testContent = "<span id='" + testBoxId + "' class='wc-diagnostic wc-diagnostic-type-error'>\n\
<i aria-hidden='true' class='fa fa-times-circle'></i>\n\
<span class='wc-message'>Message one</span>\n\
<span class='wc-message'>Message two</span>\n\
<span class='wc-message'>Message three</span></span>" + testTargetHTML;

		function getTestBox() {
			return document.getElementById(testBoxId);
		}

		function getTestTarget() {
			return document.getElementById(targetId);
		}

		function getTestInput() {
			return document.getElementById(inputId);
		}

		function getSimpleAddDTO(messages, target) {
			var realTarget = target || getTestTarget();
			return {
				messages: messages,
				target: realTarget
			};
		}

		function doAddNotInvalidTest(level) {
			var input = getTestInput(),
				dto = getSimpleAddDTO("error message");
			dto.level = level;
			assert.isFalse(input.hasAttribute("aria-invalid"), "should not be invalid");
			assert.isFalse(input.hasAttribute("aria-describedBy"), "should not have described-by");
			controller.add(dto);
			assert.isFalse(input.hasAttribute("aria-invalid"), "should still not be invalid");
			assert.isTrue(input.hasAttribute("aria-describedBy"), "should have described-by");
		}

		function prepareDiagnosticBoxInTestTarget(level) {
			var dto = getSimpleAddDTO("message");
			if (level) {
				dto.level = level;
			}
			controller.add(dto);
		}

		function doGetBoxWithLevelTest(level) {
			var lvl;
			prepareDiagnosticBoxInTestTarget(level);
			assert.isOk(controller.getBox(getTestTarget(), level));
			for (lvl in controller.LEVEL) {
				if (controller.LEVEL.hasOwnProperty(lvl) && controller.LEVEL[lvl] !== level) {
					assert.isNull(controller.getBox(getTestTarget(), controller.LEVEL[lvl]), "shouldn't find box with level " + level + " using " + controller.LEVEL[lvl]);
				}
			}
		}

		registerSuite({
			name: "wc/ui/diagnostic",
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
			testAdd_noMessages: function() {
				assert.isNull(controller.add(getSimpleAddDTO()));
			},
			testAdd_noTarget: function() {
				var dto = getSimpleAddDTO("hello!");
				dto.target = null;
				assert.isNull(controller.add(dto));
			},
			testAdd_targetNotElement: function() {
				var dto = getSimpleAddDTO("hello!", "I am not a target");
				assert.isNull(controller.add(dto));
			},
			testAdd_noLevel: function() {
				var dto = getSimpleAddDTO("message"),
					expected = targetId + "_err",
					actual = controller.add(dto);
				assert.strictEqual(actual, expected);
			},
			testAdd_noLevel_ActuallyAdds: function() {
				var dto = getSimpleAddDTO("message"),
					box;
				controller.add(dto);
				box = document.getElementById(targetId + "_err");
				assert.isOk(box);
				assert.strictEqual(box.nodeType, Node.ELEMENT_NODE);
			},
			testAdd_ERROR: function() {
				var dto = getSimpleAddDTO("message"),
					box;
				dto.level = controller.LEVEL.ERROR;
				controller.add(dto);
				box = document.getElementById(targetId + "_err");
				assert.isOk(box);
				assert.strictEqual(box.nodeType, Node.ELEMENT_NODE);
			},
			testAdd_WARN: function() {
				var dto = getSimpleAddDTO("message"),
					box;
				dto.level = controller.LEVEL.WARN;
				controller.add(dto);
				box = document.getElementById(targetId + "_wrn");
				assert.isOk(box);
				assert.strictEqual(box.nodeType, Node.ELEMENT_NODE);
			},
			testAdd_INFO: function() {
				var dto = getSimpleAddDTO("message"),
					box;
				dto.level = controller.LEVEL.INFO;
				controller.add(dto);
				box = document.getElementById(targetId + "_nfo");
				assert.isOk(box);
				assert.strictEqual(box.nodeType, Node.ELEMENT_NODE);
			},
			testAdd_SUCCESS: function() {
				var dto = getSimpleAddDTO("message"),
					box;
				dto.level = controller.LEVEL.SUCCESS;
				controller.add(dto);
				box = document.getElementById(targetId + "_scc");
				assert.isOk(box);
				assert.strictEqual(box.nodeType, Node.ELEMENT_NODE);
			},
			testAdd_toWrappedTarget: function() {
				var target = getTestInput(),
					dto = getSimpleAddDTO("hello", target);
				assert.strictEqual(controller.add(dto), targetId + "_err");
			},
			testAdd_targetCheckbox: function() {
				var testId = "test-uidiagnostic-checkbox-foo",
					cb = "<input id='" + testId + "' type='checkbox'>",
					target,
					dto;
				testHolder.insertAdjacentHTML("beforeend", cb);
				target = document.getElementById(testId);
				dto = getSimpleAddDTO("message", target);
				assert.strictEqual(controller.add(dto), testId + "_err");
				// message box should follow target in this case
				target = target.nextElementSibling;
				assert.isTrue(controller.isOneOfMe(target));
			},
			testAdd_targetRadio: function() {
				var testId = "test-uidiagnostic-radio-foo",
					cb = "<input id='" + testId + "' type='radio'>",
					target,
					dto;
				testHolder.insertAdjacentHTML("beforeend", cb);
				target = document.getElementById(testId);
				dto = getSimpleAddDTO("message", target);
				assert.strictEqual(controller.add(dto), testId + "_err");
				// message box should follow target in this case
				target = target.nextElementSibling;
				assert.isTrue(controller.isOneOfMe(target));
			},
			testAdd_targetCheckboxWithLabel: function() {
				var testId = "test-uidiagnostic-checkbox-foo",
					labelId = testId + "-label",
					html = "<input id='" + testId + "' type='checkbox'><label for='" + testId + "' id='" + labelId + "'>label</label>",
					target,
					label,
					dto;
				testHolder.insertAdjacentHTML("beforeend", html);
				target = document.getElementById(testId);
				dto = getSimpleAddDTO("message", target);
				controller.add(dto);
				// message box should be within the label
				label = document.getElementById(labelId);
				target = label.lastElementChild;
				assert.isTrue(controller.isOneOfMe(target));
			},
			testAdd_targetRadioWithLabel: function() {
				var testId = "test-uidiagnostic-radio-foo",
					labelId = testId + "-label",
					html = "<input id='" + testId + "' type='radio'><label for='" + testId + "' id='" + labelId + "'>label</label>",
					target,
					label,
					dto;
				testHolder.insertAdjacentHTML("beforeend", html);
				target = document.getElementById(testId);
				dto = getSimpleAddDTO("message", target);
				controller.add(dto);
				// message box should be within the label
				label = document.getElementById(labelId);
				target = label.lastElementChild;
				assert.isTrue(controller.isOneOfMe(target));
			},
			testAdd_targetNoId: function () {
				// convoluted test to go down a little used path in `add`
				var target = getTestTarget(),
					dto;
				target.id = ""; // this is the thing to test: we have a target but it doesn't have an id
				dto = getSimpleAddDTO("message", target);
				assert.isNull(controller.add(dto));
			},
			testAddMakesInvalid: function() {
				var input = getTestInput();
				assert.isFalse(input.hasAttribute("aria-invalid"), "should not be invalid");
				assert.isFalse(input.hasAttribute("aria-describedBy"), "should not have described-by");
				controller.add(getSimpleAddDTO("error message"));
				assert.isTrue(input.hasAttribute("aria-invalid"), "should be invalid");
				assert.isTrue(input.hasAttribute("aria-describedBy"), "should have described-by");
			},
			testAdd_WARN_notInvalid: function() {
				doAddNotInvalidTest(controller.LEVEL.WARN);
			},
			testAdd_INFO_notInvalid: function() {
				doAddNotInvalidTest(controller.LEVEL.INFO);
			},
			testAdd_SUCCESS_notInvalid: function() {
				doAddNotInvalidTest(controller.LEVEL.SUCCESS);
			},
			testRemoveNoArgs: function() {
				try {
					controller.remove();
					assert.isTrue(false, "expected an error to be thrown");
				} catch (e) {
					assert.strictEqual(e.message, "You forgot the args");
				}
			},
			testRemove: function() {
				var box = document.getElementById(testBoxId);
				assert.isOk(box);
				controller.remove(box);
				assert.isNotOk(document.getElementById(testBoxId));
			},
			testRemoveClearsInvalid: function() {
				var input = getTestInput(),
					boxId = controller.add(getSimpleAddDTO("error message")),
					box = document.getElementById(boxId);
				assert.isTrue(input.hasAttribute("aria-invalid"), "should have invalid");
				assert.isTrue(input.hasAttribute("aria-describedBy"), "should have described-by");
				controller.remove(box);
				assert.isFalse(input.hasAttribute("aria-invalid"), "should not be invalid");
				assert.isFalse(input.hasAttribute("aria-describedBy"), "should not have described-by");
			},
			testRemove_usingTarget: function() {
				var target = getTestTarget(),
					dto = getSimpleAddDTO("message", target),
					testId = target.id + "_err";
				// this is the set up
				controller.add(dto);
				assert.isOk(document.getElementById(testId));
				controller.remove(null, target);
				assert.isNotOk(document.getElementById(testId));
			},
			testRemove_usingBothArgs: function() {
				var target = getTestTarget(),
					dto = getSimpleAddDTO("message", target),
					testId = target.id + "_err",
					boxId = controller.add(dto);
				// this is the set up
				assert.isOk(document.getElementById(testId));
				controller.remove(document.getElementById(boxId), target);
				assert.isNotOk(document.getElementById(testId));
			},
			testGetBox_noElement: function() {
				try {
					controller.getBox();
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must not be falsey");
				}
			},
			testGetBox_stringNotId: function() {
				try {
					controller.getBox("_____hello_____");
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element does not represent an HTML Element");
				}
			},
			testGetBox_targetNotElement: function() {
				try {
					controller.getBox({});
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element does not represent an HTML Element");
				}
			},
			testGetBox_noLevel_created_ERROR: function() {
				var box;
				prepareDiagnosticBoxInTestTarget();
				box = controller.getBox(getTestTarget());
				assert.strictEqual(box.id, targetId + "_err");
			},
			testGetBox_noLevel_createdOther: function() {
				prepareDiagnosticBoxInTestTarget(controller.LEVEL.WARN);
				assert.isNull(controller.getBox(getTestTarget()));
			},
			testGetBox_negOne: function() {
				prepareDiagnosticBoxInTestTarget();
				assert.isOk(controller.getBox(getTestTarget(), -1));
			},
			testGetBox_negOneNotNested: function() {
				var testId = "test-uidiagnostic-checkbox-foo",
					cb = "<input id='" + testId + "' type='checkbox'>",
					target,
					dto,
					box;
				// set up a test with the diagnostic NOT nested in the target.
				testHolder.insertAdjacentHTML("beforeend", cb);
				target = document.getElementById(testId);
				dto = getSimpleAddDTO("message", target);
				controller.add(dto);
				box = controller.getBox(target, -1);
				assert.isOk(box);
				assert.strictEqual(box.id, testId + "_err");
			},
			testGetBox_ERROR: function() {
				doGetBoxWithLevelTest(controller.LEVEL.ERROR);
			},
			testGetBox_WARN: function() {
				doGetBoxWithLevelTest(controller.LEVEL.WARN);
			},
			testGetBox_INFO: function() {
				doGetBoxWithLevelTest(controller.LEVEL.INFO);
			},
			testGetBox_SUCCESS: function() {
				doGetBoxWithLevelTest(controller.LEVEL.SUCCESS);
			},
			testGetBox_forWrappedInput: function() {
				var start, box;
				prepareDiagnosticBoxInTestTarget();
				start = getTestInput();
				box = controller.getBox(start);
				assert.strictEqual(box.id, targetId + "_err");
			},
			testGetMessages_noArg: function() {
				// this is a test of the private function `check(diag, lenient)` with lenient true
				assert.isNull(controller.getMessages(null));
			},
			testGetMessages_badArg: function() {
				// this is a test of the private function `check(diag, lenient)` with lenient true
				assert.isNull(controller.getMessages(getTestTarget()));
			},
			testGetMessages: function() {
				var box = getTestBox(),
					expected = box.querySelectorAll("span.wc-message").length;
				assert.strictEqual(controller.getMessages(box).length, expected);
			},
			testClear: function() {
				var box = getTestBox(),
					start = box.querySelectorAll("span.wc-message").length;
				controller.clear(box);
				assert.notStrictEqual(box.querySelectorAll("span.wc-message").length, start);
			},
			testDiagnosticCheckerSecondHand_failNoArg: function() {
				// this is a test of the private function `check(diag, lenient)` with diag and lenient false
				try {
					controller.change();
					assert.isTrue(false, "expected an error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testDiagnosticCheckerSecondHand_failNotCorrectElement: function() {
				// this is a test of the private function `check(diag, lenient)` with lenient false
				try {
					controller.change(getTestTarget());
					assert.isTrue(false, "expected an error");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testChange_noLevel: function() {
				// calling change with no level just falls out. Nothing should change
				var box = getTestBox();
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
				controller.change(box);
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
			},
			testChange_stupidLevel: function() {
				// calling change with level < 1 just falls out. Nothing should change
				var box = getTestBox();
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
				controller.change(box, -1);
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
			},
			testChange_sameLevel: function() {
				var box = getTestBox();
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
				controller.change(box, controller.LEVEL.ERROR); // change nothing
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
			},
			testChange_nonExistentLevel: function() {
				// calling change with level < 1 just falls out. Nothing should change
				var box = getTestBox();
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
				controller.change(box, 10);
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
			},
			testChange: function() {
				var box = getTestBox();
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.ERROR);
				controller.change(box, controller.LEVEL.WARN);
				assert.strictEqual(diagnostic.getLevel(box), controller.LEVEL.WARN);
			},
			testChangeChangesId: function() {
				var box = getTestBox(),
					id = box.id;
				controller.change(box, controller.LEVEL.WARN);
				assert.notStrictEqual(box.id, id);
			},
			testChangeClears: function() {
				var box = getTestBox();
				assert.strictEqual(controller.getMessages(box).length, 3);
				controller.change(box, controller.LEVEL.WARN);
				assert.strictEqual(controller.getMessages(box).length, 0);
			},
			testChangeFromErrorClearsInvalid: function () {
				var target = getTestTarget(),
					dto = getSimpleAddDTO("message", target),
					boxId = controller.add(dto),
					box = document.getElementById(boxId),
					input = getTestInput();
				assert.isTrue(input.hasAttribute("aria-invalid"));
				controller.change(box, controller.LEVEL.SUCCESS);
				assert.isFalse(input.hasAttribute("aria-invalid"));
			},
			testChangeToErrorAddsInvalid: function () {
				var target = getTestTarget(),
					dto = getSimpleAddDTO("message", target),
					boxId,
					box,
					input;
				dto.level = controller.LEVEL.SUCCESS;
				boxId = controller.add(dto);
				box = document.getElementById(boxId);
				input = getTestInput();
				assert.isFalse(input.hasAttribute("aria-invalid"));
				controller.change(box, controller.LEVEL.ERROR);
				assert.isTrue(input.hasAttribute("aria-invalid"));
			},
			testAddMessages_noBox: function() {
				try {
					controller.addMessages();
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "Argument must be a diagnostic box");
				}
			},
			testAddMessages_noMessage: function() {
				var box = getTestBox();
				try {
					controller.addMessages(box);
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "Message must be a string");
				}
			},
			testAddMessages_emptyStringMessageArray: function() {
				var box = getTestBox();
				try {
					controller.addMessages(box, [""]);
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "Message must be a string");
				}
			},
			testAddMessages_string: function() {
				var box = getTestBox(),
					msg = "Hello message from testAddMessages_string",
					messages;
				assert.strictEqual(controller.getMessages(box).length, 3);
				controller.addMessages(box, msg);
				messages = controller.getMessages(box);
				assert.strictEqual(messages.length, 4);
				assert.strictEqual(messages[messages.length - 1].innerHTML, msg);
			},
			testAddMessages_array: function() {
				var box = getTestBox(),
					msgs = ["Hello message 1 from testAddMessages_array",
						"Hello message 2 from testAddMessages_array",
						"Hello message 3 from testAddMessages_array"],
					messages,
					start,
					expected;
				start = controller.getMessages(box).length;
				expected = start + msgs.length;
				controller.addMessages(box, msgs);
				messages = controller.getMessages(box);
				assert.strictEqual(messages.length, expected);
				assert.strictEqual(messages[messages.length - 1].innerHTML, msgs[2]);
				assert.strictEqual(messages[messages.length - 2].innerHTML, msgs[1]);
				assert.strictEqual(messages[messages.length - 3].innerHTML, msgs[0]);
			},
			testAddMessagesDoesntDuplicate: function() {
				var box = getTestBox(),
					original = controller.getMessages(box);
				Array.prototype.forEach.call(original, function(next) {
					controller.addMessages(box, next.innerHTML);
				});
				assert.strictEqual(controller.getMessages(box).length, original.length);
			},
			testAddMessagesDoesntDuplicate_arrayVariant: function() {
				var box = getTestBox(),
					original = controller.getMessages(box),
					msgs = [];
				Array.prototype.forEach.call(original, function(next) {
					msgs.push(next.innerHTML);
				});
				controller.addMessages(box, msgs);
				assert.strictEqual(controller.getMessages(box).length, original.length);
			},
			testAddMessages_emptyMessageArray: function() {
				// should do nothing
				var box = getTestBox();
				assert.strictEqual(controller.getMessages(box).length, 3);
				controller.addMessages(box, []);
				assert.strictEqual(controller.getMessages(box).length, 3);
			},
			testSet_empty: function() {
				var box = getTestBox();
				controller.set(box);
				assert.strictEqual(controller.getMessages(box).length, 0);
			},
			testSet_string: function() {
				var box = getTestBox(), messages;
				controller.set(box, "foo");
				messages = controller.getMessages(box);
				assert.strictEqual(messages.length, 1);
				assert.strictEqual(messages[0].innerHTML, "foo");
			},
			testSet_array: function() {
				var box = getTestBox(), messages;
				controller.set(box, ["foo", "bar"]);
				messages = controller.getMessages(box);
				assert.strictEqual(messages.length, 2);
				assert.strictEqual(messages[0].innerHTML, "foo");
				assert.strictEqual(messages[1].innerHTML, "bar");
			}
		});
	});
