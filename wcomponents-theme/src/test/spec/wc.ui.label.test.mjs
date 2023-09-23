import uiLabel, {initialiser} from "wc/ui/label.mjs";
import shed from "wc/dom/shed.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";

describe("wc/ui/label", () => {
	const CLASS_REQ = "wc_req";
	let ownerDocument;
	let testHolder;

	beforeEach(() => {
		return setUpExternalHTML("uiLabel.html").then(dom => {
			ownerDocument = dom.window.document;
			ownerDocument._getElementById = ownerDocument.getElementById;
			/**
			 * @param id
			 * @return {HTMLElement}
			 */
			ownerDocument.getElementById = function(id) {
				const result = this._getElementById(id);
				result.style.width = "5em";
				return result;
			};
			testHolder = ownerDocument.body;
			uiLabel.moveLabels(testHolder);
			initialiser.postInit(testHolder);
		});
	});
	
	afterEach(() => {
		testHolder.innerHTML = "";
	});
	
	it("testMoveLabelWCheckBox", function() {
		const input = ownerDocument.getElementById("wcuilabel-i2"),
			expected = "wcuilabel-l2",
			target = input.lastElementChild;
		expect(target.id).withContext("WCheckBox label should have been moved in pre-init").toBe(expected);
	});

	it("testMoveLabelWRadioButton", function() {
		const input = ownerDocument.getElementById("wcuilabel-i2a"),
			expected = "wcuilabel-l2a",
			target = input.nextElementSibling;
		expect(target.id).withContext("WRadioButton label should have been moved in pre-init").toBe(expected);
	});

	it("testMoveLabelWSelectToggle", function() {
		const input = ownerDocument.getElementById("wcuilabel-i2b"),
			expected = "wcuilabel-l2b",
			target = input.nextElementSibling;
		expect(target.id).withContext("WSelectToggle label should have been moved in pre-init").toBe(expected);
	});

	it("testMoveLabelWCheckBoxRO", function() {
		const input = ownerDocument.getElementById("wcuilabel-i2c"),
			expected = "wcuilabel-l2c",
			target = input.nextElementSibling;
		expect(target.id).withContext("Read-only WCheckBox label should have been moved in pre-init").toBe(expected);
	});

	it("testMoveLabelWRadioButtonRO", function() {
		const input = ownerDocument.getElementById("wcuilabel-i2d"),
			expected = "wcuilabel-l2d",
			target = input.nextElementSibling;
		expect(target.id).withContext("Read-only WRadioButton label should have been moved in pre-init").toBe(expected);
	});

	it("testMoveLabelFalse", function() {
		const inputIds = ["wcuilabel-i3", "wcuilabel-i3a", "wcuilabel-i3b"];
		inputIds.forEach(function(nextId) {
			const input = ownerDocument.getElementById(nextId);
			const found = input.previousElementSibling;
			expect(found.getAttribute("for")).withContext("label should not have been moved in pre-init").toBe(nextId);
		});
	});

	it("testGetHint", function() {
		var label = ownerDocument.getElementById("wcuilabel-l10"),
			hint = uiLabel.getHint(label);
		expect(hint).withContext("expected to find hint").toBeTruthy();
	});

	it("testGetHintCorrectContent", function() {
		var label = ownerDocument.getElementById("wcuilabel-l10"),
			expected = "this is the hint",
			hint = uiLabel.getHint(label);
		expect(hint.innerHTML).withContext("expected to find hint content").toBe(expected);
	});

	it("testGetHintNoHint", function() {
		var label = ownerDocument.getElementById("wcuilabel-l11"),
			hint = uiLabel.getHint(label);
		expect(hint).withContext("expected to not find hint").toBeFalsy();
	});

	// setHint
	it("testSetHint", function() {
		var hint = "I am the walrus",
			label = ownerDocument.getElementById("wcuilabel-l9"),
			labelHint;
		expect(uiLabel.getHint(label)).toBeFalsy();
		uiLabel.setHint(label, hint);
		labelHint = uiLabel.getHint(label);
		expect(labelHint).toBeTruthy();
		expect(labelHint.innerHTML.indexOf(hint) === 0).toBeTrue();
	});

	it("testSetHint_existingHint", function() {
		var label = ownerDocument.getElementById("wcuilabel-l10"),
			hint = uiLabel.getHint(label).innerHTML.toLowerCase(),
			content = " some more hint";
		uiLabel.setHint(label, content);
		expect(uiLabel.getHint(label).innerHTML.toLowerCase()).toBe(hint + "<br>" + content);
	});

	it("testMandate", function() {
		const input = ownerDocument.getElementById("wcuilabel-i6"),
			label = ownerDocument.getElementById("wcuilabel-l6");
		expect(label.classList.contains(CLASS_REQ)).toBeFalse();
		expect(shed.isMandatory(input)).toBeFalse();
		shed.mandatory(input);
		expect(label.classList.contains(CLASS_REQ)).toBeTrue();
	});

	it("testMandateRadio", function() {
		// note: we do not decorate labels for mandatory radios
		const input = ownerDocument.getElementById("wcuilabel-i6a"),
			label = ownerDocument.getElementById("wcuilabel-l6a");
		expect(label.classList.contains(CLASS_REQ)).toBeFalse();
		expect(shed.isMandatory(input)).toBeFalse();
		shed.mandatory(input);
		expect(label.classList.contains(CLASS_REQ)).toBeFalse();
		expect(shed.isMandatory(input)).toBeTrue();
	});

	it("testOptionalise", function() {
		const input = ownerDocument.getElementById("wcuilabel-i5"),
			label = ownerDocument.getElementById("wcuilabel-l5");
		expect(label.classList.contains(CLASS_REQ)).toBeTrue();
		shed.optional(input);
		expect(label.classList.contains(CLASS_REQ)).toBeFalse();
	});

	it("testHide", function() {
		const input = ownerDocument.getElementById("wcuilabel-i7"),
			label = ownerDocument.getElementById("wcuilabel-l7");
		expect(shed.isHidden(label)).toBeFalse();
		shed.hide(input);
		expect(shed.isHidden(label)).toBeTrue();
	});

	it("testShow", function() {
		const input = ownerDocument.getElementById("wcuilabel-i8"),
			label = ownerDocument.getElementById("wcuilabel-l8");
		expect(shed.isHidden(label)).toBeTrue();
		shed.show(input);
		expect(shed.isHidden(label)).toBeFalse();
	});

	it("testConvertInputToRO", function() {
		const input = ownerDocument.getElementById("wcuilabel-i12");
		let label = ownerDocument.getElementById("wcuilabel-l12");
		expect(label.matches("label")).withContext("wrong start tagname").toBeTrue();
		expect(label.getAttribute("for")).withContext("should have for attribute").toBeTruthy();
		expect(label.getAttribute("data-wc-rofor")).withContext("should not have data-wc-for attribute").toBeFalsy();
		uiLabel._convert(input, label, true);
		label = ownerDocument.getElementById("wcuilabel-l12");
		expect(label.matches("span")).withContext("wrong end tagname").toBeTrue();
		expect(label.getAttribute("for")).withContext("for attribute should have been removed").toBeFalsy();
		expect(label.getAttribute("data-wc-rofor")).withContext("data-ro-for should have been added").toBeTruthy();
		expect(label.getAttribute("data-wc-rofor")).toBe("wcuilabel-i12");
	});

	it("testConvertROtoInput", function() {
		const input = ownerDocument.getElementById("wcuilabel-i13");
		let label = ownerDocument.getElementById("wcuilabel-l13");
		expect(label.matches("span")).withContext("wrong start tagname").toBeTrue();
		expect(label.getAttribute("for")).withContext("should not have for attribute").toBeFalsy();
		expect(label.getAttribute("data-wc-rofor")).withContext("should have data-wc-for attribute").toBeTruthy();
		expect(label.getAttribute("data-wc-rofor")).toBe("wcuilabel-i13");
		uiLabel._convert(input, label, false);
		label = ownerDocument.getElementById("wcuilabel-l13");
		expect(label.matches("label")).withContext("wrong end tagname").toBeTrue();
		expect(label.getAttribute("for")).withContext("for attribute should have been added").toBeTruthy();
		expect(label.getAttribute("data-wc-rofor")).withContext("data-ro-for should have been removed").toBeFalsy();
		expect(label.getAttribute("for")).toBe("wcuilabel-i13");
	});

	it("testAjaxSubscriber", function() {
		// this is a fake just to test the actual subscriber, not to test AJAX
		var container = ownerDocument.getElementById("wcuilabel-fake-ajax"),
			label;
		uiLabel._ajax(container);

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l1");
		expect(label.matches("label")).toBeTrue();
		expect(label.getAttribute("data-wc-rofor")).withContext("data-ro-for should have been removed").toBeFalsy();
		expect(label.getAttribute("for")).withContext("for attribute should have been added").toBe("wcuilabel-fake-ajax-i1_input");

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l2");
		expect(label.matches("span")).toBeTrue();
		expect(label.getAttribute("for")).withContext("data-ro-for should have been removed").toBeFalsy();
		expect(label.getAttribute("data-wc-rofor")).withContext("data-wc-rofor attribute should have been added").toBe("wcuilabel-fake-ajax-i2");

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l3");
		expect(shed.isHidden(label)).withContext("real label should be hidden").toBeTrue();

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l4");
		expect(shed.isHidden(label)).withContext("fake label should be hidden").toBeTrue();

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l5");
		expect(label.classList.contains(CLASS_REQ)).withContext("real label should be flagged required").toBeTrue();

		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l6");
		expect(label.classList.contains(CLASS_REQ)).withContext("fake label should not be flagged required").toBeFalse();

		// change of property but not of read-only state
		// mandatory
		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l7");
		expect(label.classList.contains(CLASS_REQ)).withContext("label should now be flagged required").toBeTrue();
		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l8");
		expect(label.classList.contains(CLASS_REQ)).withContext("label should no longer be flagged required").toBeFalse();
		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l9");
		expect(shed.isHidden(label)).withContext("label should now be hidden").toBeTrue();
		label = ownerDocument.getElementById("wcuilabel-fake-ajax-l10");
		expect(shed.isHidden(label)).withContext("label should no longer be hidden").toBeFalse();
	});

	it("testCheckboxLabelPositionHelper_noArgs", function() {
		// @ts-ignore
		const doBadThing = () => uiLabel._checkboxLabelPositionHelper();
		expect(doBadThing).toThrowError("Input and label must be defined.");
	});

	it("testCheckboxLabelPositionHelper_inputNotElement", function() {
		// @ts-ignore
		const doBadThing = () => uiLabel._checkboxLabelPositionHelper("I am not an element", true);
		expect(doBadThing).toThrowError("Input must be an element.");
	});

	it("testCheckboxLabelPositionHelper_noLabel", function() {
		// @ts-ignore
		const doBadThing = () => uiLabel._checkboxLabelPositionHelper(ownerDocument.getElementById("wcuilabel-i1"));
		expect(doBadThing).toThrowError("Input and label must be defined.");
	});

	it("testCheckboxLabelPositionHelper_labelNotElement", function() {
		// @ts-ignore
		expect(uiLabel._checkboxLabelPositionHelper(ownerDocument.getElementById("wcuilabel-i1"), {})).toBeUndefined();
	});

	it("testCheckboxLabelPositionHelper_stringLabel", function() {
		const inputId = "wclabeltest-testinput",
			input = "<span class='wc-checkbox wc-input-wrapper' id='" + inputId + "'><input id='" + inputId + "_input' type='checkbox'></span>",
			labelId = inputId + "-label";
		testHolder.insertAdjacentHTML("beforeend", input);
		uiLabel._checkboxLabelPositionHelper(ownerDocument.getElementById(inputId), "<label id='" + labelId + "' for='" + inputId + "_input'>I am a label</label>");
		expect(ownerDocument.getElementById(inputId).lastChild.id).toBe(labelId);
		expect(ownerDocument.getElementById(inputId).lastChild.tagName.toLowerCase()).toBe("label");
	});

	it("testCheckboxLabelPositionHelper_stringNotElement", function() {
		const inputId = "wclabeltest-testinput",
			input = "<span class='wc-checkbox wc-input-wrapper' id='" + inputId + "'><input id='" + inputId + "_input' type='checkbox'></span>";

		testHolder.insertAdjacentHTML("beforeend", input);
		expect(uiLabel._checkboxLabelPositionHelper(ownerDocument.getElementById(inputId), "I am a label")).toBeUndefined();
		expect(ownerDocument.getElementById(inputId).lastChild.tagName.toLowerCase()).not.toBe("label");
	});
});
