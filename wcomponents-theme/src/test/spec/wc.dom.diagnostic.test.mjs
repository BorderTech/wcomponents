import diagnostic from "wc/dom/diagnostic.mjs";


const testBoxId = "wcdiagnostictest1";
const dummyElementId = "wcdiagnostictest_notdiagnostic";
const fakeMessageId = "wcdiagnostictest_notmessage";

const testContent = `
	<span id='${testBoxId}' class='wc-fieldindicator'>
		<span class='wc-message'>Message one</span>
	</span>
	<span id='${dummyElementId}'>placeholder</span>
	<span id='${fakeMessageId}' class='wc-message'>not a message</span>`;

let testHolder;

describe("wc/dom/diagnostic", () => {

	beforeAll(() => {
		testHolder = document.body;
	});

	beforeEach(() => {
		testHolder.innerHTML = testContent;
	});

	afterEach(() => {
		testHolder.innerHTML = "";
	});

	it("testGetIdExtensionERROR", function() {
		doIdExtensionTest(diagnostic.LEVEL.ERROR, "_err");
	});

	it("testGetIdExtensionWARN", function() {
		doIdExtensionTest(diagnostic.LEVEL.WARN, "_wrn");
	});

	it("testGetIdExtensionINFO", function() {
		doIdExtensionTest(diagnostic.LEVEL.INFO, "_nfo");
	});

	it("testGetIdExtensionSUCCESS", function() {
		doIdExtensionTest(diagnostic.LEVEL.SUCCESS, "_scc");
	});

	it("testGetIdExtensionFalsey", function() {
		doIdExtensionTest(false, "_err");
	});

	it("testGetIdExtensionOther", function() {
		doIdExtensionTest(-1, "_err");
	});

	it("testGetBoxClassERROR", function() {
		doBoxClassType(diagnostic.LEVEL.ERROR, "wc-fieldindicator-type-error");
	});

	it("testGetBoxClassWARN", function() {
		doBoxClassType(diagnostic.LEVEL.WARN, "wc-fieldindicator-type-warn");
	});

	it("testGetBoxClassINFO", function() {
		doBoxClassType(diagnostic.LEVEL.INFO, "wc-fieldindicator-type-info");
	});

	it("testGetBoxClassSUCCESS", function() {
		doBoxClassType(diagnostic.LEVEL.SUCCESS, "wc-fieldindicator-type-success");
	});

	it("testGetBoxClassFalsey", function() {
		doBoxClassType(false, "wc-fieldindicator");
	});

	it("testGetBoxClassOther", function() {
		expect(diagnostic.getBoxClass(-1)).toBeNull();
	});

	it("testGetWidget", function() {
		const widget = diagnostic.getWidget();
		expect(widget).toBeTruthy();
	});

	it("testGetWidgetIsCorrectWidget", function() {
		const widget = diagnostic.getWidget(),
			element = getTestBox();
		expect(element.matches(widget.toString())).toBeTrue();
	});

	it("testGetMessage", function() {
		const widget = diagnostic.getMessage();
		expect(widget).toBeTruthy();
	});

	it("testGetMessageIsCorrectWidget", function() {
		const widget = diagnostic.getMessage(),
			element = getMessageElement();
		expect(element.matches(widget.toString())).toBeTrue();
	});

	it("testGetByTypeNoType", function() {
		const expected = diagnostic.getWidget(),
			actual = diagnostic.getByType();
		expect(actual).toBe(expected);
	});

	it("testGetByTypeInvalidType", function() {
		expect(diagnostic.getByType(-1)).toBeNull();
	});

	it("testGetByType", function() {
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				doGetByTypeTest(diagnostic.LEVEL[lvl]);
			}
		}
	});

	it("testIsOneOfMeNoElement", function() {
		expect(diagnostic.isOneOfMe(null)).toBeFalse();
	});

	it("testIsOneOfMeNoLevel_withBox", function() {
		const box = getTestBox();
		expect(diagnostic.isOneOfMe(box)).toBeTrue();
	});

	it("testIsOneOfMeNoLevel_notDiagnostic", function() {
		const box = getDummyElement();
		expect(diagnostic.isOneOfMe(box)).toBeFalse();
	});

	it("testIsOneOfMeWithLevel_false", function() {
		const box = getTestBox();
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				let msg = `Unexpected match ${box.className}" should not match "${diagnostic.getBoxClass(diagnostic.LEVEL[lvl])}"`;
				expect(diagnostic.isOneOfMe(box, diagnostic.LEVEL[lvl])).withContext(msg).toBeFalse();
			}
		}
	});

	it("testIsOneOfMe", function() {
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				doIsOneOfMeTest(diagnostic.LEVEL[lvl]);
			}
		}
	});

	it("testIsMessage_noElement", function() {
		expect(diagnostic.isMessage(null)).toBeFalse();
	});

	it("testIsMessage_notAnElement", function() {
		// @ts-ignore
		expect(diagnostic.isMessage("I am a message")).withContext("Only an Element can be a message").toBeFalse();
	});

	it("testIsMessage_noLevel_notAMessage", function() {
		const msg = getFakeMessage();
		expect(diagnostic.isMessage(msg)).toBeFalse();
	});

	it("testIsMessage_withLevel_notAMessage", function() {
		const msg = getFakeMessage();
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				expect(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl])).toBeFalse();
			}
		}
	});

	it("testIsMessage_noLevel_message", function() {
		const msg = getMessageElement();
		expect(diagnostic.isMessage(msg)).toBeTrue();
	});

	it("testIsMessage_level_messageInDefaultBox", function() {
		const msg = getMessageElement();
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				expect(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl])).toBeFalse();
			}
		}
	});

	it("testIsMessage_level_messageInLevelBox", function() {
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				doIsMessageTest(diagnostic.LEVEL[lvl]);
			}
		}
	});

	it("testIsMessage_badLevel", function() {
		const msg = getMessageElement();
		expect(diagnostic.isMessage(msg, -1)).toBeFalse();
	});

	it("testGetLevel_noArg", function() {
		// @ts-ignore
		const doBadThing = () => diagnostic.getLevel();
		expect(doBadThing).toThrowError("Argument must be a diagnostic box");
	});

	it("testGetLevel_badArg", function() {
		const doBadThing = () => diagnostic.getLevel(getDummyElement());
		expect(doBadThing).toThrowError("Argument must be a diagnostic box");
	});

	it("testGetLevel_noLevel", function() {
		expect(diagnostic.getLevel(getTestBox())).toBe(-1);
	});

	it("testGetLevel", function() {
		for (let lvl in diagnostic.LEVEL) {
			if (diagnostic.LEVEL.hasOwnProperty(lvl)) {
				doGetLevelTest(diagnostic.LEVEL[lvl]);
			}
		}
	});

	it("testGetTarget_noArg", function() {
		expect(diagnostic.getTarget(null)).toBeNull();
	});

	it("testGetTarget_notElement", function() {
		// @ts-ignore
		expect(diagnostic.getTarget("I am an element")).toBeNull();
	});

	it("testGetTarget_notDiagnostic", function() {
		expect(diagnostic.getTarget(getDummyElement())).toBeNull();
	});

	it("testGetTarget_diagnosticNoId", function() {
		const box = getTestBox();
		box.removeAttribute("data-wc-dfor");
		expect(diagnostic.getTarget(box)).withContext("well that was unexpected").toBeNull();
	});

	// get target tests are cumbersome so they are separated into different functions to allow the base html to be reset between tests
	it("testGetTarget_ERROR", function() {
		doGetTargetTest(diagnostic.LEVEL.ERROR);
	});

	it("testGetTarget_WARN", function() {
		doGetTargetTest(diagnostic.LEVEL.WARN);
	});

	it("testGetTarget_INFO", function() {
		doGetTargetTest(diagnostic.LEVEL.INFO);
	});

	it("testGetTarget_SUCCESS", function() {
		doGetTargetTest(diagnostic.LEVEL.SUCCESS);
	});

	it("testGetTarget_noTarget", function() {
		const box = getTestBox();
		expect(diagnostic.getTarget(box)).toBeNull();
	});

	it("testGetBoxNoTarget", function() {
		const box = getTestBox();
		box.id = box.id + "_err"; // make a real diagnostic box with no target element.
		expect(diagnostic.getTarget(box)).toBeNull();
	});

	it("testStupidMadeUpTestForGetTargetBranchConverage", function() {
		const box = getTestBox();
		box.id = "_err"; // the silly default which should not be a default
		expect(diagnostic.getTarget(box)).toBeNull();
	});

	function getTestBox() {
		return document.getElementById(testBoxId);
	}

	function getDummyElement() {
		return document.getElementById(dummyElementId);
	}

	function getMessageElement() {
		const box = getTestBox();
		return box.firstElementChild;
	}

	function getFakeMessage() {
		return document.getElementById(fakeMessageId);
	}

	function doIdExtensionTest(level, expected) {
		expect(diagnostic.getIdExtension(level)).toBe(expected);
	}

	function doBoxClassType(level, expected) {
		expect(diagnostic.getBoxClass(level)).toBe(expected);
	}

	function doGetByTypeTest(level) {
		const widget = diagnostic.getByType(),
			qs = widget.toString(),
			expected = qs + "." + diagnostic.getBoxClass(level),
			actual = diagnostic.getByType(level);
		expect(actual.toString()).toBe(expected);
	}

	function doIsOneOfMeTest(level) {
		const box = getTestBox(),
			other = getDummyElement(),
			className = diagnostic.getBoxClass(level);
		try {
			box.classList.add(className);
			other.classList.add(className);

			// a diagnostic box is a diagnostic box
			expect(diagnostic.isOneOfMe(box)).withContext("Level class should still be a diagnostic").toBeTrue();
			// matching level
			expect(diagnostic.isOneOfMe(box, level)).withContext("Expected a diagnostic at a specific level").toBeTrue();

			expect(diagnostic.isOneOfMe(other, "Not a diagnostic box should never be a diagnostic box. What never? No never!")).toBeFalse();
			expect(diagnostic.isOneOfMe(other, level)).withContext("The level classes should not be sufficient").toBeFalse();

			// a diagnostic box at any one level is NOT a box at other levels
			for (let lvl in diagnostic.LEVEL) {
				if (diagnostic.hasOwnProperty(lvl)) {
					if (diagnostic.LEVEL[lvl] === level) {
						continue;
					}
					expect(diagnostic.isOneOfMe(box, diagnostic.LEVEL[lvl])).withContext("A diagnostic at one level should not be a diagnostic at other levels").toBeFalse();
				}
			}
		} finally {
			box.classList.remove(className);
			other.classList.remove(className);
		}
	}

	function doIsMessageTest(level) {
		const box = getTestBox(),
			className = diagnostic.getBoxClass(level);
		try {
			box.classList.add(className);
			const msg = getMessageElement();

			// a diagnostic box is a diagnostic box
			expect(diagnostic.isMessage(msg)).withContext("box with level should still hold a message").toBeTrue();
			// matching level
			expect(diagnostic.isMessage(msg, level)).withContext("Expected a message at a specific level").toBeTrue();

			// a diagnostic box at any one level is NOT a box at other levels
			for (let lvl in diagnostic.LEVEL) {
				if (diagnostic.hasOwnProperty(lvl)) {
					if (diagnostic.LEVEL[lvl] === level) {
						continue;
					}
					expect(diagnostic.isMessage(msg, diagnostic.LEVEL[lvl])).withContext("A message at one level should not be a message at other levels").toBeFalse();
				}
			}
		} finally {
			box.classList.remove(className);
		}
	}

	function doGetLevelTest(level) {
		const box = getTestBox(),
			className = diagnostic.getBoxClass(level);
		try {
			box.classList.add(className);
			expect(diagnostic.getLevel(box)).withContext("Level class should be sufficient").toBe(level);
			// a diagnostic box at any one level is NOT a box at other levels
			for (let lvl in diagnostic.LEVEL) {
				if (diagnostic.hasOwnProperty(lvl)) {
					if (diagnostic.LEVEL[lvl] === level) {
						continue;
					}
					expect(diagnostic.getLevel(box)).withContext("Unexpected level match").not.toBe(diagnostic.LEVEL[lvl]);
				}
			}
		} finally {
			box.classList.remove(className);
		}
	}

	function doGetTargetTest(level) {
		const box = getTestBox(),
			className = diagnostic.getBoxClass(level),
			target = document.createElement("span"),
			expected = "foo";

		target.id = expected;
		testHolder.appendChild(target);
		box.setAttribute("data-wc-dfor", expected);
		box.classList.add(className);
		expect(diagnostic.getTarget(box).id).toBe(expected);
	}
});
