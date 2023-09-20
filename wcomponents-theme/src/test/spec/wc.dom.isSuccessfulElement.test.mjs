import isSuccessfulElement from "wc/dom/isSuccessfulElement.mjs";
import {getInput, getSelect} from "../helpers/specUtils.mjs";

describe("wc/dom/isSuccessfulElement", function() {
	let testHolder;

	const formBuilder = (content) => `<form method='get' action='#' id='form1'>${content}</form>`;
	const INPUT = formBuilder("<input id='input1' name='i1' type='text'>"),
		INPUT_BUTTON = formBuilder("<input id='input1' name='i1' type='button'>"),
		INPUT_SUBMIT = formBuilder("<input id='input1' name='i1' type='submit'>"),
		INPUT_IMAGE = formBuilder("<input id='input1' name='i1' type='image'>"),
		INPUT_RESET = formBuilder("<input id='input1' name='i1' type='reset'>"),
		SELECT = formBuilder("<select id='sel1' name='s1'><option value='0'>zero</option><option value='1'>one</option></select>"),
		BUTTON = formBuilder("<button type='button' id='button1' name='b1' value='x'>button</button>"),
		BUTTON_SUBMIT = formBuilder("<button type='submit' id='button1' name='b1' value='x'>button</button>"),
		HTML = formBuilder(`<input id='success0' name='i1' type='text' value='x'>
			<input id='success1' name='i2' type='text'>
			<input id='not0' name='i3' type='text' value='x' disabled>
			<input id='success2' name='r1' type='radio' value='false' checked>
			<input id='not1' name='r1' type='radio' value='true'>
			<input id='success3' name='cb1' type='checkbox' value='x' checked>
			<input id='not2' name='cb2' type='checkbox' value='y'>
			<input id='not3' name='cb3' type='checkbox' value='y' checked disabled>
			<select id='success4' name='sel1'><option value='0'>zero</option><option value='1' selected>one</option></select>
			<select id='success5' name='sel2'><option value='0'>zero</option><option value='1'>one</option></select>
			<select id='not4' name='sel3' multiple><option value='0'>zero</option><option value='1'>one</option></select>
			<button type='button' name='b1' value='x'>button</button>`);

	beforeEach(function() {
		testHolder = document.body;
		testHolder.innerHTML = "";
	});
	
	afterEach(function() {
		testHolder.innerHTML = "";
	});
	
	it("testInputNoValue", function() {
		testHolder.innerHTML = INPUT;
		const element = getInput("input1");
		expect(isSuccessfulElement(element)).toBeTrue();
	});

	it("testIsSuccessfulElementInput", function() {
		testHolder.innerHTML = INPUT;
		const element = getInput("input1");
		element.value = "x";
		expect(isSuccessfulElement(element)).toBeTrue();
	});

	it("testDisabled", function() {
		testHolder.innerHTML = INPUT;
		const element = getInput("input1");
		element.value = "x";
		element.disabled = true;
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testNoName", function() {
		testHolder.innerHTML = INPUT;
		const element = getInput("input1");
		element.value = "x";
		element.name = "";
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testWithSingleSelect", function() {
		testHolder.innerHTML = SELECT;
		const element = getSelect("sel1");
		expect(isSuccessfulElement(element)).toBeTrue();
	});

	it("testWithSingleSelectSelected", function() {
		testHolder.innerHTML = SELECT;
		const element = getSelect("sel1");
		element.selectedIndex = 0;
		expect(isSuccessfulElement(element)).toBeTrue();
	});

	it("testWithSingleSelectNegSelectIndex", function() {
		testHolder.innerHTML = SELECT;
		const element = getSelect("sel1");
		element.selectedIndex = -1;
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testWithMultiSelect", function() {
		testHolder.innerHTML = SELECT;
		const element = getSelect("sel1");
		element.multiple = true;
		for (let i = 0; i < element.options.length; ++i) {
			element.options[i].selected = false;
		}
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testWithMultiSelectWithSelection", function() {
		testHolder.innerHTML = SELECT;
		const element = getSelect("sel1");
		element.multiple = true;
		element.options[0].selected = true;
		expect(isSuccessfulElement(element)).toBeTrue();
	});

	it("testWithButtonNotAlwaysSuccessful", function() {
		testHolder.innerHTML = BUTTON;
		const element = document.getElementById("button1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testWithButtonAlwaysSuccessful", function() {
		testHolder.innerHTML = BUTTON;
		const element = document.getElementById("button1");
		expect(isSuccessfulElement(element, true)).toBeTrue();
	});

	it("testWithButtonSubmitNotAlwaysSuccessful", function() {
		testHolder.innerHTML = BUTTON_SUBMIT;
		const element = document.getElementById("button1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testWithButtonSubmitAlwaysSuccessful", function() {
		testHolder.innerHTML = BUTTON_SUBMIT;
		const element = document.getElementById("button1");
		expect(isSuccessfulElement(element, true)).toBeFalse(); // submit never succeeds.
	});

	it("testInputButtonNotSuccessful", function() {
		testHolder.innerHTML = INPUT_BUTTON;
		const element = getInput("input1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testInputButtonAlwaysSuccessful", function() {
		testHolder.innerHTML = INPUT_BUTTON;
		const element = getInput("input1");
		expect(isSuccessfulElement(element, true)).toBeTrue();
	});

	it("testInputSubmitNotSuccessful", function() {
		testHolder.innerHTML = INPUT_SUBMIT;
		const element = getInput("input1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testInputSubmitAlwaysSuccessful", function() {
		testHolder.innerHTML = INPUT_SUBMIT;
		const element = getInput("input1");
		expect(isSuccessfulElement(element, true)).toBeFalse();
	});

	it("testInputResetButtonsNotSuccessful", function() {
		testHolder.innerHTML = INPUT_RESET;
		const element = getInput("input1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testInputResetButtonsAlwaysSuccessful", function() {
		testHolder.innerHTML = INPUT_RESET;
		const element = getInput("input1");
		expect(isSuccessfulElement(element, true)).toBeFalse();
	});

	it("testInputImageButtonsNotSuccessful", function() {
		testHolder.innerHTML = INPUT_IMAGE;
		const element = getInput("input1");
		expect(isSuccessfulElement(element)).toBeFalse();
	});

	it("testInputImageButtonsAlwaysSuccessful", function() {
		testHolder.innerHTML = INPUT_IMAGE;
		const element = getInput("input1");
		expect(isSuccessfulElement(element, true)).toBeFalse();
	});

	// tests of get all
	it("testGetAllSimple", function() {
		testHolder.innerHTML = BUTTON;
		const start = document.getElementById("form1");
		expect(Array.isArray(isSuccessfulElement.getAll(start))).toBeTrue();
	});

	it("testGetAllSimpleEmpty", function() {
		const expected = 0;
		testHolder.innerHTML = BUTTON;
		const start = document.getElementById("form1");
		expect(isSuccessfulElement.getAll(start).length).toBe(expected);
	});

	it("testGetAllSimpleWithButtons", function() {
		const expected = 1;
		testHolder.innerHTML = BUTTON;
		const start = document.getElementById("form1");
		expect(isSuccessfulElement.getAll(start, true).length).toBe(expected);
	});

	it("testGetAllNoButtons", function() {
		const expected = 6;
		testHolder.innerHTML = HTML;
		const start = document.getElementById("form1");
		expect(isSuccessfulElement.getAll(start).length).toBe(expected);
	});

	it("testGetAllWithButtons", function() {
		const expected = 7;
		testHolder.innerHTML = HTML;
		const start = document.getElementById("form1");
		expect(isSuccessfulElement.getAll(start, true).length).toBe(expected);
	});
});
