import wrappedInput from "wc/dom/wrappedInput.mjs";

describe("wc/dom/wrappedInput", () => {
	/*
	 * Unit tests for wc/dom/wrappedInput
	 * NOTE for IDs there is a convention for wrapped input IDs which is based on the XML for the Input components
	 * which are wrapped. The wrapper has ID "foo" and the input has ID "foo_input". The example below uses this convention.
	 */
	let testHolder;
	let ownerDocument;
	const testContent = `
		<div id='wrappedinputtestcontent'>
			<span class='wc-input-wrapper' id='wrapper'><input id='wrapper_input' type='text'></span>
			<span class='wc-ro-input' id='rowrapper'><span id='rowrapper_input'>value</span></span>
			<input id='notwrapped_input' type='text'>
			<span id='notwrapper'><input id='notwrapper_input' type='text'></span>
		</div>`;

	beforeEach(function() {
		ownerDocument = document;
		testHolder = ownerDocument.body.appendChild(ownerDocument.createElement("div"));
		testHolder.innerHTML = testContent;
	});
	
	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("testIsOneOfMeWithWrapper", function() {
		expect(wrappedInput.isOneOfMe(ownerDocument.getElementById("wrapper"))).toBeTrue();
	});

	it("testIsNotOneOfMeWithROWrapper", function() {
		expect(wrappedInput.isOneOfMe(ownerDocument.getElementById("rowrapper"))).toBeFalse();
	});

	it("testIsOneOfMeWithROWrapper", function() {
		expect(wrappedInput.isOneOfMe(ownerDocument.getElementById("rowrapper"), true)).toBeTrue();
	});

	it("testIsNotOneOfMeWithROWrapperNotWrapper", function() {
		expect(wrappedInput.isOneOfMe(ownerDocument.getElementById("notwrapper"))).toBeFalse();
	});

	it("testIsReadOnlyTrue", function() {
		expect(wrappedInput.isReadOnly(ownerDocument.getElementById("rowrapper"))).toBeTrue();
	});

	it("testIsReadOnlyFalse", function() {
		expect(wrappedInput.isReadOnly(ownerDocument.getElementById("wrapper"))).toBeFalse();
	});

	it("testIsReadOnlyFalseNotAWrapper", function() {
		expect(wrappedInput.isReadOnly(ownerDocument.getElementById("notwrapper"))).toBeFalse();
	});

	it("testGetInput", function() {
		const expected = ownerDocument.getElementById("wrapper_input"),
			actual = wrappedInput.getInput(ownerDocument.getElementById("wrapper"));
		expect(actual).toEqual(expected);
	});

	it("testGetInputRO", function() {
		expect(wrappedInput.getInput(ownerDocument.getElementById("rowrapper"))).toBeNull();
	});

	it("testGetInputNotWrapper", function() {
		expect(wrappedInput.getInput(ownerDocument.getElementById("notwrapper"))).toBeNull();
	});

	it("testGetWrapper", function() {
		const expected = ownerDocument.getElementById("wrapper"),
			actual = wrappedInput.getWrapper(ownerDocument.getElementById("wrapper_input"));
		expect(actual).toEqual(expected);
	});

	it("testGetWrapperRO", function() {
		expect(wrappedInput.getWrapper(ownerDocument.getElementById("rowrapper_input"))).toBeNull();
	});

	it("testGetWrapperNotWrapped", function() {
		expect(wrappedInput.getWrapper(ownerDocument.getElementById("notwrapper_input"))).toBeNull();
	});

	it("testGet", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("wrappedinputtestcontent")).length).toBe(1);
	});

	it("testGetWithWrapper", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("wrapper")).length).toBe(1);
	});

	it("testGetWithRO", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("wrappedinputtestcontent"), true).length).toBe(2);
	});

	it("testGetWithROWrapperNotRO", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("rowrapper")).length).toBe(0);
	});

	it("testGetWithROWrapper", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("rowrapper"), true).length).toBe(1);
	});

	it("testGetNothing", function() {
		expect(wrappedInput.get(ownerDocument.getElementById("notwrapper")).length).toBe(0);
		expect(wrappedInput.get(ownerDocument.getElementById("notwrapper"), true).length).toBe(0);
	});

	it("testGetWrappedId", function() {
		const expected = "wrapper_input",
			actual = wrappedInput.getWrappedId(ownerDocument.getElementById("wrapper"));
		expect(actual).toEqual(expected);
	});

	it("testGetWrappedIdFromInput", function() {
		const expected = "wrapper_input",
			actual = wrappedInput.getWrappedId(ownerDocument.getElementById("wrapper_input"));
		expect(actual).toEqual(expected);
	});

	it("testGetWrappedIdRO", function() {
		const expected = "rowrapper_input",
			actual = wrappedInput.getWrappedId(ownerDocument.getElementById("rowrapper"));
		expect(actual).toEqual(expected);
	});

	it("testGetWrappedIdNotWrapper", function() {
		expect(wrappedInput.getWrappedId(ownerDocument.getElementById("notwrapper_input"))).toBeNull();
	});
});
