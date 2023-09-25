import getVisibleText from "wc/ui/getVisibleText.mjs";

describe("wc/ui/getVisibleText", () => {
	let ownerDocument;
	let testHolder,
		testContent = `
			<div id='div1'>text</div>
			<div id='div2'><p>yes</p><p>yes</p></div>
			<div id='div3'><p>yes</p><p hidden='hidden'>no</p></div>
			<div id='div4'><p>yes</p><p hidden>no</p></div>
			<label id='withhint'>maincontent<span class='wc-label-hint'>hint</span></label>
			<button id='withtooltip'><span role='tooltip'>H</span>hello</button>
			<label id='withhinttooltip'><span role='tooltip'>M</span>maincontent<span class='wc-label-hint'>hint</span></label>`;
	
	beforeAll(function() {
		ownerDocument = document;
		testHolder = ownerDocument.getElementById("testholder");
		if (!testHolder) {
			ownerDocument.body.insertAdjacentHTML("beforeend", "<div id='testholder'></div>");
			testHolder = ownerDocument.getElementById("testholder");
		}
	});

	afterAll(function () {
		testHolder.innerHTML = "";
	});

	beforeEach(function() {
		testHolder.innerHTML = testContent;
	});

	it("testGetVisibleText", function() {
		const testId = "div1",
			element = ownerDocument.getElementById(testId),
			expected = "text";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextNested", function() {
		const testId = "div2",
			element = ownerDocument.getElementById(testId),
			expected = "yesyes";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextNestedHidden", function() {
		const testId = "div3",
			element = ownerDocument.getElementById(testId),
			expected = "yes";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextNestedHiddenHTMLSyntax", function() {
		const testId = "div4",
			element = ownerDocument.getElementById(testId),
			expected = "yes";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextWithHint", function() {
		const testId = "withhint",
			element = ownerDocument.getElementById(testId),
			expected = "maincontenthint";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextRemoveHint", function() {
		const testId = "withhint",
			element = ownerDocument.getElementById(testId),
			expected = "maincontent";
		expect(getVisibleText(element, true)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextWithTooltip", function() {
		const testId = "withtooltip",
			element = ownerDocument.getElementById(testId),
			expected = "hello";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextWithHintTooltip", function() {
		const testId = "withhinttooltip",
			element = ownerDocument.getElementById(testId),
			expected = "maincontenthint";
		expect(getVisibleText(element)).withContext("Did not get correct text").toBe(expected);
	});

	it("testGetVisibleTextKeepHintTooltip", function() {
		const testId = "withhinttooltip",
			element = ownerDocument.getElementById(testId, true),
			expected = "maincontent";
		expect(getVisibleText(element, true)).withContext("Did not get correct text").toBe(expected);
	});
});
