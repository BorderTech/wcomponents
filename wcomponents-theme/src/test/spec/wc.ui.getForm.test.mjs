import getForm from "wc/ui/getForm.mjs";

describe("wc/ui/getForm", ()=> {
	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const testContent = `
			<div id="examplewrapper"><form id='form1'>
				<input id='input1' name='foo' type='text'>
				<span id='spaninform'>content</span></form>
				<input id='input2' readonly>
				<span id='spanoutform'>content</span>
			</div>`;
		ownerDocument = document;
		testHolder = ownerDocument.getElementById("testholder");
		if (!testHolder) {
			ownerDocument.body.insertAdjacentHTML("beforeend", "<div id='testholder'></div>");
			testHolder = ownerDocument.getElementById("testholder");
		}
		testHolder.innerHTML = testContent;
	});

	afterAll(() => {
		testHolder.innerHTML = "";
	});

	it("testGetWithForm", function() {
		const start = ownerDocument.getElementById("form1"),
			expected = ownerDocument.getElementById("form1");
		expect(getForm(start)).withContext("Did not find correct form").toBe(expected);
	});

	it("testGetWithInputInForm", function() {
		const start = ownerDocument.getElementById("input1"),
			expected = ownerDocument.getElementById("form1");
		expect(getForm(start)).withContext("Did not find correct form").toBe(expected);
	});

	it("testGetWithSpanInForm", function() {
		const start = ownerDocument.getElementById("spaninform"),
			expected = ownerDocument.getElementById("form1");
		expect(getForm(start)).withContext("Did not find correct form").toBe(expected);
	});

	it("testGetWithInputOutsideForm", function() {
		const start = ownerDocument.getElementById("input2");
		expect(getForm(start)).withContext("Did not find correct form").toBeNull();
	});

	it("testGetWithSpanOutsideForm", function() {
		const start = ownerDocument.getElementById("spanoutform");
		expect(getForm(start)).withContext("Did not find correct form").toBeNull();
	});

	it("testgetWithNothing", function() {
		const expected = ownerDocument.getElementById("form1");
		expect(getForm()).withContext("Did not find correct form").toBe(expected);
	});

	it("testgetWithNothingAncestorOnly", function() {
		// @ts-ignore
		expect(getForm("", true)).withContext("Did not find correct form").toBeNull();
	});

});
