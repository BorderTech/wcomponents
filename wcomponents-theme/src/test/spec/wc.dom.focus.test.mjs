import domFocus from "wc/dom/focus.mjs";
import domTesting from "@testing-library/dom";
import {fudgeDimensions} from "../helpers/specUtils.mjs";

describe("wc/dom/focus", () => {
	let testHolder, ownerDocument;
	const NATIVE_TRUE = "<button type='button' data-testid='button1' style='width: 5em'>button</button>",
		NATIVE_FALSE = "<div data-testid='div1' style='width: 5em'>hello</div>",
		NATIVE_LINK = "<a href='#' data-testid='a1' style='width: 5em'>link</a>",
		RADIOS_NO_SELECTION = `<input type='radio' name='radio1' data-testid='r1' value='true' style='width: 5em'>
			<input type='radio' name='radio1' data-testid='r2' value='false' style='width: 5em'>`,
		RADIOS_WITH_SELECTION = `<input type='radio' name='radio2' data-testid='r3' value='true' style='width: 5em'>
			<input type='radio' name='radio2' data-testid='r4' value='false' checked style='width: 5em'>`,
		FOCUSABLE_CONTAINER = `<div data-testid='hasfocusable' style='width: 5em'>
			<p style='width: 5em'>start placeholder</p>
			<button type='button' data-testid='hf1' style='width: 5em'>button</button>
			<p style='width: 5em'>between</p>
			<button type='button' data-testid='hf2' style='width: 5em'>button</button>
			<p style='width: 5em'>end placeholder</p></div>`,
		NOT_FOCUSABLE_CONTAINER = "<div data-testid='nofocus' style='width: 5em'><p data-testid='nf1' style='width: 5em'>not focusable</p></div>",
		FOCUSABLE_ANCESTOR = `<button type='button' data-testid='button1' style='width: 5em'>
			<span data-testid='span1' style='width: 5em'>start</span></button>`;

	beforeAll(() => {
		ownerDocument = document;
		testHolder = ownerDocument.body.appendChild(ownerDocument.createElement("div"));
		fudgeDimensions(ownerDocument.defaultView);
	});

	beforeEach(() => {
		testHolder.innerHTML = "";
	});

	afterAll(() => {
		testHolder.innerHTML = "";
	});

	it("testIsTabstopNative", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeTrue();
	});

	it("testIsTabstopNotNative", () => {
		const id = "div1";
		testHolder.innerHTML = NATIVE_FALSE;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeFalse();
	});

	it("testIsTabstopNativeDisabled", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = /** @type HTMLButtonElement */(domTesting.getByTestId(testHolder, id));
		element.disabled = true;
		expect(domFocus.isTabstop(element)).toBeFalse();
	});

	it("testIsTabstopNativeLink", () => {
		const id = "a1";
		testHolder.innerHTML = NATIVE_LINK;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeTrue();
	});

	it("testIsTabstopNativeAriaDisabled", () => {
		const id = "a1";
		testHolder.innerHTML = NATIVE_LINK;
		const element = domTesting.getByTestId(testHolder, id);
		element.setAttribute("aria-disabled", "true");
		expect(domFocus.isTabstop(element)).toBeFalse();
	});

	it("testIsTabstopTabindex", () => {
		const id = "div1";
		testHolder.innerHTML = NATIVE_FALSE;
		const element = domTesting.getByTestId(testHolder, id);
		element.tabIndex = 0;
		expect(domFocus.isTabstop(element)).toBeTrue();
	});

	it("testIsTabstopTabindexFalse", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.tabIndex = -1;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeFalse();
	});

	it("testIsTabstopNativeHidden", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.hidden = true;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeFalse();
	});

	it("testIsTabstopTabindexHidden", () => {
		const id = "div1";
		testHolder.innerHTML = NATIVE_FALSE;
		const element = domTesting.getByTestId(testHolder, id);
		element.tabIndex = 0;
		element.hidden = true;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, id))).toBeFalse();
	});

	it("testIsTabstopRadiosNoSelection", () => {
		testHolder.innerHTML = RADIOS_NO_SELECTION;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, "r1"))).toBeTrue();
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, "r2"))).toBeTrue();
	});

	it("testIsTabstopRadiosWithSelection", () => {
		testHolder.innerHTML = RADIOS_WITH_SELECTION;
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, "r3"))).toBeFalse();
		expect(domFocus.isTabstop(domTesting.getByTestId(testHolder, "r4"))).toBeTrue();
	});

	it("testIsTabstopNullArg", () => {
		// @ts-ignore
		expect(domFocus.isTabstop()).toBeFalse();
	});

	it("testIsNativelyFocusable", () => {
		const focusable = ["a", "area", "audio", "button", "frame", "iframe", "input", "object", "select", "textarea", "video"];
		for (let i = 0; i < focusable.length; ++i) {
			expect(domFocus.isNativelyFocusable(focusable[i])).toBeTrue();
		}
	});

	it("testIsNativelyFocusableFalse", () => {
		// a cross-section of not focusable tagNames:
		const focusable = ["p", "div", "span", "ul", "li", "html", "body", "header", "footer", "h1", "form"];
		for (let i = 0; i < focusable.length; ++i) {
			expect(domFocus.isNativelyFocusable(focusable[i])).toBeFalse();
		}
	});

	it("testIsNativelyFocusableNullArg", () => {
		// @ts-ignore
		expect(domFocus.isNativelyFocusable()).toBeFalse();
	});

	it("testCanFocusNativeYes", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		expect(domFocus.canFocus(domTesting.getByTestId(testHolder, id))).toBeTrue();
	});

	it("testCanFocusHiddenNo", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.hidden = true;
		expect(domFocus.canFocus(element)).toBeFalse();
	});

	it("testCanFocusDisabledNo", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = /** @type HTMLButtonElement */(domTesting.getByTestId(testHolder, id));
		element.disabled = true;
		expect(domFocus.canFocus(element)).toBeFalse();
	});

	it("testCanFocusInvisibleNo", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.style.visibility = "hidden";
		expect(domFocus.canFocus(element)).toBeFalse();
	});

	it("testCanFocusNoDisplayNo", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.style.display = "none";
		expect(domFocus.canFocus(element)).toBeFalse();
	});

	it("testCanFocusZeroDimensionNo", () => {
		const id = "button1";
		testHolder.innerHTML = NATIVE_TRUE;
		const element = domTesting.getByTestId(testHolder, id);
		element.style.width = "0";
		element.style.height = "0";
		element.style.overflow = "hidden";
		// don't forget buttons have borders and padding which give them dimension!
		element.style.border = "0 none";
		element.style.padding = "0";
		expect(domFocus.canFocus(element)).withContext("expected zero dimension element to not be focusable , offsetWidth: " + element.offsetWidth + ", offsetHeight: " + element.offsetHeight).toBeFalse();
	});

	// setFocusRequest needs async tests
	it("testFocusFirstTabstop", () => {
		const expected = "hf1";
		testHolder.innerHTML = FOCUSABLE_CONTAINER;
		const target = domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "hasfocusable"));
		expect(target.dataset["testid"]).toBe(expected);
	});

	it("testFocusFirstTabstopReverse", () => {
		const expected = "hf2";
		testHolder.innerHTML = FOCUSABLE_CONTAINER;
		const target = domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "hasfocusable"), null, true);
		expect(target.dataset["testid"]).toBe(expected);
	});

	it("testFocusFirstTabstopCallback", () => {
		let result = false;
		function callback() {
			result = true;
		}
		testHolder.innerHTML = FOCUSABLE_CONTAINER;
		domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "hasfocusable"), callback);
		expect(result).toBeTrue();
	});

	it("testFocusFirstTabstopCallbackReverse", () => {
		let result = false;
		testHolder.innerHTML = FOCUSABLE_CONTAINER;
		domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "hasfocusable"), () => result = true, true);
		expect(result).toBeTrue();
	});

	it("testFocusFirstTabstopNoFocus", () => {
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		expect(domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "nofocus"))).toBeNull();
	});

	it("testFocusFirstTabstopNoFocusReverse", () => {
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		expect(domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "nofocus"), null, true)).toBeNull();
	});

	it("testFocusFirstTabstopNoFocusCallback", () => {
		let result = false;
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		function callback() {
			result = true;
		}
		domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "nofocus"), callback);
		expect(result).toBeFalse();
	});

	it("testFocusFirstTabstopNoFocusCallbackReverse", () => {
		let result = false;
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		function callback() {
			result = true;
		}
		domFocus.focusFirstTabstop(domTesting.getByTestId(testHolder, "nofocus"), callback, true);
		expect(result).toBeFalse();
	});

	it("testCanFocusInside", () => {
		testHolder.innerHTML = FOCUSABLE_CONTAINER;
		expect(domFocus.canFocusInside(domTesting.getByTestId(testHolder, "hasfocusable"))).toBeTrue();
	});

	it("testCanFocusInsideFalse", () => {
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		expect(domFocus.canFocusInside(domTesting.getByTestId(testHolder, "nofocus"))).toBeFalse();
	});

	it("testGetFocusableAncestorSelf", () => {
		testHolder.innerHTML = NATIVE_TRUE;
		expect(domFocus.getFocusableAncestor(domTesting.getByTestId(testHolder, "button1"))).toBe(domTesting.getByTestId(testHolder, "button1"));
	});

	it("testGetFocusableAncestorNotSelfIsNull", () => {
		testHolder.innerHTML = NATIVE_TRUE;
		expect(domFocus.getFocusableAncestor(domTesting.getByTestId(testHolder, "button1"), true)).toBeNull();
	});

	it("testGetFocusableAncestor", () => {
		testHolder.innerHTML = FOCUSABLE_ANCESTOR;
		expect(domFocus.getFocusableAncestor(domTesting.getByTestId(testHolder, "span1"))).toBe(domTesting.getByTestId(testHolder, "button1"));
	});

	it("testGetFocusableAncestorNotFocusable", () => {
		testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
		expect(domFocus.getFocusableAncestor(domTesting.getByTestId(testHolder, "nf1"))).toBeNull();
	});

	it("testGetFocusableAncestorNullArg", () => {
		// @ts-ignore
		expect(domFocus.getFocusableAncestor()).toBeNull();
	});
});
