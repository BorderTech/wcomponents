import subordinate from "wc/ui/subordinate.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";

describe("wc/ui/subordinate", () => {
	beforeAll(() => {
		return setUpExternalHTML("domUsefulDom.html").then(dom => {
			window.document.body.innerHTML = dom.window.document.body.innerHTML;
		});
	});

	it("testIsConditionTrueWithTextInputById", () => {
		expect(subordinate._isConditionTrue("form3txt1", "AbC123", "eq")).toBeTrue();
	});

	it("testIsConditionTrueWithTextAndNe", () => {
		expect(subordinate._isConditionTrue("form3txt1", "AbC123", "ne")).toBeFalse();
	});

	it("testIsConditionTrueWithTextInputByName", () => {
		expect(subordinate._isConditionTrue("form3txt2", "dEF456", "eq")).toBeTrue();
	});

	it("testIsConditionTrueWithTextCaseSensitive", () => {
		expect(subordinate._isConditionTrue("form3txt1", "abc123", "eq")).toBeFalse();
	});

	/**
	 * Tests that string comparison rules are applied.
	 * In this case the string "10" is not greater than "2".
	 */
	it("testIsConditionTrueWithTextAndGt", () => {
		expect(subordinate._isConditionTrue("form3txtTen", "2", "gt")).toBeFalse();
	});

	/**
	 * Tests that string comparison rules are applied.
	 * In this case the string "10" is not less than "2".
	 */
	it("testIsConditionTrueWithTextAndLt", () => {
		expect(subordinate._isConditionTrue("form3txtTen", "2", "lt")).toBeTrue();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case the number 10 is greater than 2.
	 */
	it("testIsConditionTrueWithNumAndGt", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "2", "gt")).toBeTrue();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case the number 10 is not less than 2.
	 */
	it("testIsConditionTrueWithTextAndLt", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "2", "lt")).toBeFalse();
	});


	it("testIsConditionTrueWithTextAndRegex", () => {
		expect(subordinate._isConditionTrue("form3txt1", "AbC123", "rx")).toBeTrue();
	});

	it("testIsConditionTrueWithTextAndRegexNoMatch", () => {
		expect(subordinate._isConditionTrue("form3txt1", "abc123", "rx")).toBeFalse();
	});

	it("testIsConditionTrueWithTextAndRegexIgnoreCase", () => {
		expect(subordinate._isConditionTrue("form3txt1", "(?i)abc123", "rx")).toBeTrue();
	});

	it("testIsConditionTrueWithChkBox", () => {
		expect(subordinate._isConditionTrue("chk2", "true")).toBeTrue();
	});

	it("testIsConditionTrueWithChkBoxNotChecked", () => {
		expect(subordinate._isConditionTrue("chk3", "false")).toBeTrue();
	});

	it("testIsConditionTrueWithChkBoxCheckedNotFalse", () => {
		expect(subordinate._isConditionTrue("chk2", "false")).toBeFalse();
	});

	it("testIsConditionTrueWithTextAndComplexRegex", () => {
		expect(subordinate._isConditionTrue("form3txt1", "^[A-Za-z]{3}[0-9]{3}$", "rx")).toBeTrue();
	});


	// it("testAnythingToTest", () => {
	// 	const query = "wc-subordinate",
	// 		sourceElements = ownerDocument.querySelector(query);
	// 	expect(sourceElements.length).withContext("Nothing to test").toBeGreaterThan(0);
	// });

	/*
	 * This test covers off a real bug we encountered when the compare value and the selected option value attribute are empty strings
	 * but the option text is not empty.
	 */
	it("testIsConditionTrueSelectValueAndCompareEmptyString", () => {
		expect(subordinate._isConditionTrue("select4", "", "eq")).toBeTrue();
	});

	it("testIsConditionTrueSelectNoValueMatchText", () => {
		expect(subordinate._isConditionTrue("select5", "No Value", "eq")).toBeTrue();
	});
});
