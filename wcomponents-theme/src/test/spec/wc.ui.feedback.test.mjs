import feedback from "wc/ui/feedback.mjs";
import diagnostic from "wc/dom/diagnostic.mjs";

const targetId = "wrapper",
	inputId = "wrappedinput",
	testBoxId = "wcdiagnostictest1_err",
	testContent = `
		<span id="${testBoxId}" class="wc-fieldindicator wc-fieldindicator-type-error">
		<i aria-hidden='true' class='fa fa-times-circle'></i>
		<span class='wc-message'>Message one</span>
		<span class='wc-message'>Message two</span>
		<span class='wc-message'>Message three</span></span>
		<span class="wc-input-wrapper" id="${targetId}"><input id="${inputId}" type="text"></span>`;

describe("wc/ui/feedback", () => {
	beforeAll(() => {
		testHolder = document.body;
	});

	beforeEach(() => {
		testHolder.innerHTML = testContent;
	});

	afterEach(() => {
		testHolder.innerHTML = "";
	});

	it("testAdd_noMessages", function() {
		expect(feedback.add(getSimpleAddDTO())).toBeNull();
	});

	it("testAdd_noTarget", function() {
		const dto = getSimpleAddDTO("hello!");
		dto.target = null;
		expect(feedback.add(dto)).toBeNull();
	});

	it("testAdd_targetNotElement", function() {
		const dto = getSimpleAddDTO("hello!", "I am not a target");
		expect(feedback.add(dto)).toBeNull();
	});

	it("testAdd_noLevel", function() {
		const dto = getSimpleAddDTO("message"),
			expected = targetId + "_err",
			actual = feedback.add(dto);
		expect(actual).toBe(expected);
	});

	it("testAdd_noLevel_ActuallyAdds", function() {
		const dto = getSimpleAddDTO("message");
		feedback.add(dto);
		const box = document.getElementById(targetId + "_err");
		expect(box).toBeTruthy();
		expect(box.nodeType).toBe(Node.ELEMENT_NODE);
	});

	it("testAdd_ERROR", function() {
		const dto = getSimpleAddDTO("message");
		dto.level = feedback.LEVEL.ERROR;
		feedback.add(dto);
		const box = document.getElementById(targetId + "_err");
		expect(box).toBeTruthy();
		expect(box.nodeType).toBe(Node.ELEMENT_NODE);
	});

	it("testAdd_WARN", function() {
		const dto = getSimpleAddDTO("message");
		dto.level = feedback.LEVEL.WARN;
		feedback.add(dto);
		const box = document.getElementById(targetId + "_wrn");
		expect(box).toBeTruthy();
		expect(box.nodeType).toBe(Node.ELEMENT_NODE);
	});

	it("testAdd_INFO", function() {
		const dto = getSimpleAddDTO("message");
		dto.level = feedback.LEVEL.INFO;
		feedback.add(dto);
		const box = document.getElementById(targetId + "_nfo");
		expect(box).toBeTruthy();
		expect(box.nodeType).toBe(Node.ELEMENT_NODE);
	});

	it("testAdd_SUCCESS", function() {
		const dto = getSimpleAddDTO("message");
		dto.level = feedback.LEVEL.SUCCESS;
		feedback.add(dto);
		const box = document.getElementById(targetId + "_scc");
		expect(box).toBeTruthy();
		expect(box.nodeType).toBe(Node.ELEMENT_NODE);
	});

	it("testAdd_toWrappedTarget", function() {
		const target = getTestInput(),
			dto = getSimpleAddDTO("hello", target);
		expect(feedback.add(dto)).toBe(targetId + "_err");
	});

	it("testAdd_targetCheckbox", function() {
		const testId = "test-uidiagnostic-checkbox-foo",
			cb = "<input id='" + testId + "' type='checkbox'>";
		testHolder.insertAdjacentHTML("beforeend", cb);
		const target = document.getElementById(testId);
		const dto = getSimpleAddDTO("message", target);
		expect(feedback.add(dto)).toBe(testId + "_err");
		// message box should follow target in this case
		expect(feedback.isOneOfMe(target.nextElementSibling)).toBeTrue();
	});

	it("testAdd_targetRadio", function() {
		const testId = "test-uidiagnostic-radio-foo",
			cb = "<input id='" + testId + "' type='radio'>";
		testHolder.insertAdjacentHTML("beforeend", cb);
		const target = document.getElementById(testId);
		const dto = getSimpleAddDTO("message", target);
		expect(feedback.add(dto)).toBe(testId + "_err");
		// message box should follow target in this case
		expect(feedback.isOneOfMe(target.nextElementSibling)).toBeTrue();
	});

	it("testAdd_targetCheckboxWithLabel", function() {
		const testId = "test-uidiagnostic-checkbox-foo",
			labelId = testId + "-label",
			html = "<input id='" + testId + "' type='checkbox'><label for='" + testId + "' id='" + labelId + "'>label</label>";
		testHolder.insertAdjacentHTML("beforeend", html);
		const target = document.getElementById(testId);
		const dto = getSimpleAddDTO("message", target);
		feedback.add(dto);
		// message box should be within the label
		const label = document.getElementById(labelId);
		expect(feedback.isOneOfMe(label.lastElementChild)).toBeTrue();
	});

	it("testAdd_targetRadioWithLabel", function() {
		const testId = "test-uidiagnostic-radio-foo",
			labelId = testId + "-label",
			html = "<input id='" + testId + "' type='radio'><label for='" + testId + "' id='" + labelId + "'>label</label>";
		testHolder.insertAdjacentHTML("beforeend", html);
		const target = document.getElementById(testId);
		const dto = getSimpleAddDTO("message", target);
		feedback.add(dto);
		// message box should be within the label
		const label = document.getElementById(labelId);
		expect(feedback.isOneOfMe(label.lastElementChild)).toBeTrue();
	});

	it("testAdd_targetNoId", function() {
		// convoluted test to go down a little used path in `add`
		const target = getTestTarget();
		target.id = ""; // this is the thing to test: we have a target but it doesn't have an id
		const dto = getSimpleAddDTO("message", target);
		expect(feedback.add(dto)).toBeNull();
	});

	it("testAddMakesInvalid", function() {
		const input = getTestInput();
		expect(input.hasAttribute("aria-invalid")).withContext("should not be invalid").toBeFalse();
		expect(input.hasAttribute("aria-describedBy")).withContext("should not have described-by").toBeFalse();
		feedback.add(getSimpleAddDTO("error message"));
		expect(input.hasAttribute("aria-invalid")).withContext("should be invalid").toBeTrue();
		expect(input.hasAttribute("aria-describedBy")).withContext("should have described-by").toBeTrue();
	});

	it("testAdd_WARN_notInvalid", function() {
		doAddNotInvalidTest(feedback.LEVEL.WARN);
	});

	it("testAdd_INFO_notInvalid", function() {
		doAddNotInvalidTest(feedback.LEVEL.INFO);
	});

	it("testAdd_SUCCESS_notInvalid", function() {
		doAddNotInvalidTest(feedback.LEVEL.SUCCESS);
	});

	it("testRemoveDiagnosticNoArgs", function() {
		const doBadThing = () => feedback._removeDiagnostic();
		expect(doBadThing).toThrowError("You forgot the args");
	});

	it("testRemoveDiagnostic", function() {
		const box = document.getElementById(testBoxId);
		expect(box).toBeTruthy();
		feedback._removeDiagnostic(box);
		expect(document.getElementById(testBoxId)).toBeFalsy();
	});

	it("testRemoveDiagnosticClearsInvalid", function() {
		const input = getTestInput(),
			boxId = feedback.add(getSimpleAddDTO("error message")),
			box = document.getElementById(boxId);
		expect(input.hasAttribute("aria-invalid")).withContext("should have invalid").toBeTrue();
		expect(input.hasAttribute("aria-describedBy")).withContext("should have described-by").toBeTrue();
		feedback._removeDiagnostic(box);
		expect(input.hasAttribute("aria-invalid")).withContext("should not be invalid").toBeFalse();
		expect(input.hasAttribute("aria-describedBy")).withContext("should not have described-by").toBeFalse();
	});

	it("testRemoveDiagnostic_usingTarget", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("message", target),
			testId = target.id + "_err";
		// this is the set-up
		feedback.add(dto);
		expect(document.getElementById(testId)).toBeTruthy();
		feedback._removeDiagnostic(null, target);
		expect(document.getElementById(testId)).toBeFalsy();
	});

	it("testRemoveDiagnostic_usingBothArgs", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("message", target),
			testId = target.id + "_err",
			boxId = feedback.add(dto);
		// this is the set-up
		expect(document.getElementById(testId)).toBeTruthy();
		feedback._removeDiagnostic(document.getElementById(boxId), target);
		expect(document.getElementById(testId)).toBeFalsy();
	});

	it("testGetBox_noElement", function() {
		// @ts-ignore
		const doBadThing = () => feedback.getBox();
		expect(doBadThing).toThrowError("element must not be falsy");
	});

	it("testGetBox_stringNotId", function() {
		const doBadThing = () => feedback.getBox("_____hello_____");
		expect(doBadThing).toThrowError("element does not represent an HTML Element");
	});

	it("testGetBox_targetNotElement", function() {
		// @ts-ignore
		const doBadThing = () => feedback.getBox({});
		expect(doBadThing).toThrowError("element does not represent an HTML Element");
	});

	it("testGetBox_noLevel_created_ERROR", function() {
		prepareDiagnosticBoxInTestTarget();
		const box = feedback.getBox(getTestTarget());
		expect(box.id).toBe(targetId + "_err");
	});

	it("testGetBox_noLevel_createdOther", function() {
		prepareDiagnosticBoxInTestTarget(feedback.LEVEL.WARN);
		expect(feedback.getBox(getTestTarget())).toBeNull();
	});

	it("testGetBox_negOne", function() {
		prepareDiagnosticBoxInTestTarget();
		expect(feedback.getBox(getTestTarget(), -1)).toBeTruthy();
	});

	it("testGetBox_negOneNotNested", function() {
		const testId = "test-uidiagnostic-checkbox-foo",
			cb = "<input id='" + testId + "' type='checkbox'>";
		// set up a test with the diagnostic NOT nested in the target.
		testHolder.insertAdjacentHTML("beforeend", cb);
		const target = document.getElementById(testId);
		const dto = getSimpleAddDTO("message", target);
		feedback.add(dto);
		const box = feedback.getBox(target, -1);
		expect(box).toBeTruthy();
		expect(box.id).toBe(testId + "_err");
	});

	it("testGetBox_ERROR", function() {
		doGetBoxWithLevelTest(feedback.LEVEL.ERROR);
	});

	it("testGetBox_WARN", function() {
		doGetBoxWithLevelTest(feedback.LEVEL.WARN);
	});

	it("testGetBox_INFO", function() {
		doGetBoxWithLevelTest(feedback.LEVEL.INFO);
	});

	it("testGetBox_SUCCESS", function() {
		doGetBoxWithLevelTest(feedback.LEVEL.SUCCESS);
	});

	it("testGetBox_forWrappedInput", function() {
		prepareDiagnosticBoxInTestTarget();
		const start = getTestInput();
		const box = feedback.getBox(start);
		expect(box.id).toBe(targetId + "_err");
	});

	it("testGetMessages_noArg", function() {
		// this is a test of the private function `check(diag, lenient)` with lenient true
		expect(feedback.getMessages(null)).toBeNull();
	});

	it("testGetMessages_badArg", function() {
		// this is a test of the private function `check(diag, lenient)` with lenient true
		expect(feedback.getMessages(getTestTarget())).toBeNull();
	});

	it("testGetMessages", function() {
		const box = getTestBox(),
			expected = box.querySelectorAll("span.wc-message").length;
		expect(feedback.getMessages(box).length).toBe(expected);
	});

	it("testClear", function() {
		const box = getTestBox(),
			start = box.querySelectorAll("span.wc-message").length;
		feedback.clear(box);
		expect(box.querySelectorAll("span.wc-message").length).not.toBe(start);
	});

	it("testDiagnosticCheckerSecondHand_failNoArg", function() {
		// this is a test of the private function `check(diag, lenient)` with diag and lenient false
		// @ts-ignore
		const doBadThing = () => feedback.change();
		expect(doBadThing).toThrowError("Argument must be a feedback box");
	});

	it("testDiagnosticCheckerSecondHand_failNotCorrectElement", function() {
		// this is a test of the private function `check(diag, lenient)` with lenient false
		const doBadThing = () => feedback.change(getTestTarget());
		expect(doBadThing).toThrowError("Argument must be a feedback box");
	});

	it("testChange_noLevel", function() {
		// calling change with no level just falls out. Nothing should change
		const box = getTestBox();
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
		feedback.change(box);
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
	});

	it("testChange_stupidLevel", function() {
		// calling change with level < 1 just falls out. Nothing should change
		const box = getTestBox();
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
		feedback.change(box, -1);
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
	});

	it("testChange_sameLevel", function() {
		const box = getTestBox();
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
		feedback.change(box, feedback.LEVEL.ERROR); // change nothing
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
	});

	it("testChange_nonExistentLevel", function() {
		// calling change with level < 1 just falls out. Nothing should change
		const box = getTestBox();
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
		feedback.change(box, 10);
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
	});

	it("testChange", function() {
		const box = getTestBox();
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.ERROR);
		feedback.change(box, feedback.LEVEL.WARN);
		expect(diagnostic.getLevel(box)).toBe(feedback.LEVEL.WARN);
	});

	it("testChangeChangesId", function() {
		const box = getTestBox(),
			id = box.id;
		feedback.change(box, feedback.LEVEL.WARN);
		expect(box.id).not.toBe(id);
	});

	it("testChangeClears", function() {
		const box = getTestBox();
		expect(feedback.getMessages(box).length).toBe(3);
		feedback.change(box, feedback.LEVEL.WARN);
		expect(feedback.getMessages(box).length).toBe(0);
	});

	it("testChangeFromErrorClearsInvalid", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("message", target),
			boxId = feedback.add(dto),
			box = document.getElementById(boxId),
			input = getTestInput();
		expect(input.hasAttribute("aria-invalid")).toBeTrue();
		feedback.change(box, feedback.LEVEL.SUCCESS);
		expect(input.hasAttribute("aria-invalid")).toBeFalse();
	});

	it("testChangeToErrorAddsInvalid", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("message", target);
		dto.level = feedback.LEVEL.SUCCESS;
		const boxId = feedback.add(dto);
		const box = document.getElementById(boxId);
		const input = getTestInput();
		expect(input.hasAttribute("aria-invalid")).toBeFalse();
		feedback.change(box, feedback.LEVEL.ERROR);
		expect(input.hasAttribute("aria-invalid")).toBeTrue();
	});

	it("testAddMessages_noBox", function() {
		// @ts-ignore
		const doBadThing = () => feedback.addMessages();
		expect(doBadThing).toThrowError("Argument must be a feedback box");
	});

	it("testAddMessages_noMessage", function() {
		const box = getTestBox();
		// @ts-ignore
		const doBadThing = () => feedback.addMessages(box);
		expect(doBadThing).toThrowError("Message must be a string");
	});

	it("testAddMessages_emptyStringMessageArray", function() {
		const box = getTestBox();
		const doBadThing = () => feedback.addMessages(box, [""]);
		expect(doBadThing).toThrowError("Message must be a string");
	});

	it("testAddMessages_string", function() {
		const box = getTestBox(),
			msg = "Hello message from testAddMessages_string";
		expect(feedback.getMessages(box).length).toBe(3);
		feedback.addMessages(box, msg);
		const messages = feedback.getMessages(box);
		expect(messages.length).toBe(4);
		expect(messages[messages.length - 1].innerHTML).toBe(msg);
	});

	it("testAddMessages_array", function() {
		const box = getTestBox(),
			msgs = ["Hello message 1 from testAddMessages_array",
				"Hello message 2 from testAddMessages_array",
				"Hello message 3 from testAddMessages_array"];
		const start = feedback.getMessages(box).length;
		const expected = start + msgs.length;
		feedback.addMessages(box, msgs);
		const messages = feedback.getMessages(box);
		expect(messages.length).toBe(expected);
		expect(messages[messages.length - 1].innerHTML).toBe(msgs[2]);
		expect(messages[messages.length - 2].innerHTML).toBe(msgs[1]);
		expect(messages[messages.length - 3].innerHTML).toBe(msgs[0]);
	});

	it("testAddMessagesDoesntDuplicate", function() {
		const box = getTestBox(),
			original = feedback.getMessages(box);
		Array.prototype.forEach.call(original, function(next) {
			feedback.addMessages(box, next.innerHTML);
		});
		expect(feedback.getMessages(box).length).toBe(original.length);
	});

	it("testAddMessagesDoesntDuplicate_arrayVariant", function() {
		const box = getTestBox(),
			original = feedback.getMessages(box),
			msgs = [];
		Array.prototype.forEach.call(original, function(next) {
			msgs.push(next.innerHTML);
		});
		feedback.addMessages(box, msgs);
		expect(feedback.getMessages(box).length).toBe(original.length);
	});

	it("testAddMessages_emptyMessageArray", function() {
		// should do nothing
		const box = getTestBox();
		expect(feedback.getMessages(box).length).toBe(3);
		feedback.addMessages(box, []);
		expect(feedback.getMessages(box).length).toBe(3);
	});

	it("testSet_empty", function() {
		const box = getTestBox();
		// @ts-ignore
		feedback.set(box);
		expect(feedback.getMessages(box).length).toBe(0);
	});

	it("testSet_string", function() {
		const box = getTestBox();
		feedback.set(box, "foo");
		const messages = feedback.getMessages(box);
		expect(messages.length).toBe(1);
		expect(messages[0].innerHTML).toBe("foo");
	});

	it("testSet_array", function() {
		const box = getTestBox();
		feedback.set(box, ["foo", "bar"]);
		const messages = feedback.getMessages(box);
		expect(messages.length).toBe(2);
		expect(messages[0].innerHTML).toBe("foo");
		expect(messages[1].innerHTML).toBe("bar");
	});

	it("testRemove_noarg", function() {
		// @ts-ignore
		expect(feedback.remove()).toBeFalse();
	});

	it("testRemove_notFound", function() {
		expect(feedback.remove(getTestInput())).toBeFalse();
	});

	it("testRemove_diagBox", function() {
		const box = getTestBox(),
			id = box.id;
		feedback.remove(box);
		expect(document.getElementById(id)).toBeFalsy();
	});

	it("testRemove_target", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("error", target),
			id = feedback.add(dto);
		feedback.remove(target);
		expect(document.getElementById(id)).toBeFalsy();
	});

	it("testRemove_targetAndLevel", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("error", target),
			id = feedback.add(dto);
		feedback.remove(target, null, feedback.LEVEL.ERROR);
		expect(document.getElementById(id)).toBeFalsy();
	});

	it("testRemove_targetWrongLevel", function() {
		const target = getTestTarget(),
			dto = getSimpleAddDTO("error", target),
			id = feedback.add(dto);
		feedback.remove(target, null, feedback.LEVEL.SUCCESS);
		expect(document.getElementById(id)).toBeTruthy();
	});

	it("testFlag_noArgs", function() {
		expect(feedback._flag()).toBeNull();
	});

	it("testFlag_noElement", function() {
		expect(feedback._flag(getFlagDto(null, "message", feedback.LEVEL.ERROR))).toBeNull();
	});

	it("testFlag_noMessages", function() {
		expect(feedback._flag(getFlagDto(getTestTarget(), null, feedback.LEVEL.ERROR))).toBeNull();
	});

	it("testFlag_noLevel", function() {
		expect(feedback._flag(getFlagDto(getTestTarget(), "message"))).toBeNull();
	});

	it("testFlag_targetStringNotElement", function() {
		expect(feedback._flag(getFlagDto("I_AM_NOT_AN_ELEMENT_ID", "message", feedback.LEVEL.ERROR))).toBeNull();
	});

	it("testFlag_targetString", function() {
		expect(feedback._flag(getFlagDto(targetId, "message", feedback.LEVEL.ERROR))).not.toBeNull();
	});

	it("testFlag", function() {
		expect(feedback._flag(getFlagDto(getTestTarget(), "message", feedback.LEVEL.ERROR))).not.toBeNull();
	});

	it("testFlag_sameLevel", function() {
		const target = getTestTarget(),
			message1 = "first message",
			message2 = "second message",
			level = feedback.LEVEL.INFO,
			dto = getSimpleAddDTO(message1, target);
		dto.level = level;
		const boxId = feedback.add(dto);
		const box = document.getElementById(boxId);
		const messageCount = feedback.getMessages(box).length;
		const insertedMessageBoxId = feedback._flag(getFlagDto(target, message2, level));
		expect(insertedMessageBoxId).not.toBeNull();
		expect(insertedMessageBoxId).toBe(boxId);
		expect(feedback.getMessages(box).length).toBe(messageCount + 1);
	});

	it("testFlag_successToError", function() {
		const target = getTestTarget(),
			message1 = "success message",
			message2 = "error message",
			dto = getSimpleAddDTO(message1, target);
		dto.level = feedback.LEVEL.SUCCESS;
		const boxId = feedback.add(dto);
		expect(document.getElementById(boxId)).toBeTruthy();
		const insertedMessageBoxId = feedback._flag(getFlagDto(target, message2, feedback.LEVEL.ERROR));
		expect(insertedMessageBoxId).not.toBeNull();
		expect(insertedMessageBoxId).not.toBe(boxId);
		expect(document.getElementById(boxId)).toBeFalsy();
		expect(document.getElementById(insertedMessageBoxId)).toBeTruthy();
	});

	it("testFlag_errorToSuccess", function() {
		const target = getTestTarget(),
			message1 = "error message",
			message2 = "success message",
			dto = getSimpleAddDTO(message1, target);
		dto.level = feedback.LEVEL.ERROR;
		const boxId = feedback.add(dto);
		expect(document.getElementById(boxId)).toBeTruthy();
		const insertedMessageBoxId = feedback._flag(getFlagDto(target, message2, feedback.LEVEL.SUCCESS));
		expect(insertedMessageBoxId).not.toBeNull();
		expect(insertedMessageBoxId).not.toBe(boxId);
		expect(document.getElementById(boxId)).toBeFalsy();
		expect(document.getElementById(insertedMessageBoxId)).toBeTruthy();
	});

	// tests of flagLEVEL are all replicas of the flag tests so really
	// all we need to do is test we are getting the correct level box.
	it("testFlagError", function() {
		const expected = feedback.LEVEL.ERROR,
			boxId = feedback.flagError(getFlagDto(getTestTarget(), "message"));
		expect(boxId).toBeTruthy();
		const box = document.getElementById(boxId);
		expect(diagnostic.getLevel(box)).toBe(expected);
	});

	it("testFlagWarning", function() {
		const expected = feedback.LEVEL.WARN,
			boxId = feedback.flagWarning(getFlagDto(getTestTarget(), "message"));
		expect(boxId).toBeTruthy();
		const box = document.getElementById(boxId);
		expect(diagnostic.getLevel(box)).toBe(expected);
	});

	it("testFlagInfo", function() {
		const expected = feedback.LEVEL.INFO,
			boxId = feedback.flagInfo(getFlagDto(getTestTarget(), "message"));
		expect(boxId).toBeTruthy();
		const box = document.getElementById(boxId);
		expect(diagnostic.getLevel(box)).toBe(expected);
	});

	it("testFlagSuccess", function() {
		const expected = feedback.LEVEL.SUCCESS,
			boxId = feedback.flagSuccess(getFlagDto(getTestTarget(), "message"));
		expect(boxId).toBeTruthy();
		const box = document.getElementById(boxId);
		expect(diagnostic.getLevel(box)).toBe(expected);
	});


	let testHolder;

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
		const realTarget = target || getTestTarget();
		return {
			messages: messages,
			target: realTarget
		};
	}

	function doAddNotInvalidTest(level) {
		const input = getTestInput(),
			dto = getSimpleAddDTO("error message");
		dto.level = level;
		expect(input.hasAttribute("aria-invalid")).withContext("should not be invalid").toBeFalse();
		expect(input.hasAttribute("aria-describedBy")).withContext("should not have described-by").toBeFalse();
		feedback.add(dto);
		expect(input.hasAttribute("aria-invalid")).withContext("should still not be invalid").toBeFalse();
		expect(input.hasAttribute("aria-describedBy")).withContext("should have described-by").toBeTrue();
	}

	function prepareDiagnosticBoxInTestTarget(level) {
		const dto = getSimpleAddDTO("message");
		if (level) {
			dto.level = level;
		}
		feedback.add(dto);
	}

	function doGetBoxWithLevelTest(level) {
		prepareDiagnosticBoxInTestTarget(level);
		expect(feedback.getBox(getTestTarget(), level)).toBeTruthy();
		for (let lvl in feedback.LEVEL) {
			if (feedback.LEVEL.hasOwnProperty(lvl) && feedback.LEVEL[lvl] !== level) {
				expect(feedback.getBox(getTestTarget(), feedback.LEVEL[lvl])).withContext("shouldn't find box with level " + level + " using " + feedback.LEVEL[lvl]).toBeNull();
			}
		}
	}

	function getFlagDto(target, message, level) {
		return {
			element: target,
			message: message,
			level: level
		};
	}
});
