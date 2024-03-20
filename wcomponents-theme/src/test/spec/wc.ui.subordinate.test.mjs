import subordinate, {initialiser} from "wc/ui/subordinate.mjs";
import {findInput, findSelect, getInput, getSelect, setUpExternalHTML} from "../helpers/specUtils.mjs";
import shed from "wc/dom/shed.mjs";
import timers from "wc/timers.mjs";
import {findByTestId, getByTestId} from "@testing-library/dom";

describe("wc/ui/subordinate ye olde 'doh' tests", () => {
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
	 * In this case 10 >= 10.
	 */
	it("testIsConditionTrueWithNumAndGte", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "10", "ge")).toBeTrue();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case 10 >= 11.
	 */
	it("testIsConditionTrueWithNumAndGteFalse", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "11", "ge")).toBeFalse();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case the number 10 is not less than 2.
	 */
	it("testIsConditionTrueWithTextAndLt", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "2", "lt")).toBeFalse();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case 10 <= 10.
	 */
	it("testIsConditionTrueWithTextAndLe", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "10", "le")).toBeTrue();
	});

	/**
	 * Tests that number comparison rules are applied.
	 * In this case 10 <= 9.
	 */
	it("testIsConditionTrueWithTextAndLeFalse", () => {
		expect(subordinate._isConditionTrue("form3NumTen", "9", "le")).toBeFalse();
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

describe("wc/ui/subordinate Live DOM Rule Tests", () => {
	const delay = 50;  // milliseconds to wait for events and stuff to be actioned
	let testHolder;
	beforeAll(() => {
		return setUpExternalHTML("subordinate.html").then(dom => {
			subordinate._setView(dom.window);
			testHolder = dom.window.document.body;
		}).then(() => {
			return new Promise(win => {
				timers._subscribe((pending) => {
					if (!pending) {
						const query = "wc-subordinate",
							sourceElement = testHolder.querySelector(query);
						expect(sourceElement).withContext("Nothing to test").toBeTruthy();
						initialiser.initialise(testHolder);
						setTimeout(win, delay);
					}
				});
			});
		});
	});

	beforeEach(async () => {
		const catSelect = await findSelect(testHolder, "enable_category");
		// reset this ui element
		catSelect.selectedIndex = -1;
		const textInput = await findInput(testHolder, "enable_text");
		textInput.disabled = true;
		const colorpicker = await findSelect(testHolder, "colorpicker");
		colorpicker.selectedIndex = 0;
		const rbYesNoNo = await findInput(testHolder, "rgYesNoNo");
		rbYesNoNo.checked = true;  // Radio button, will deselect others in group
		const rbYesNoNo4a = await findInput(testHolder, "rgYesNoNo4a");
		rbYesNoNo4a.checked = true;  // Radio button, will deselect others in group
		const whiteElephant = await findByTestId(testHolder, "whiteElephant");
		whiteElephant.hidden = true;
		const greyElephant = await findByTestId(testHolder, "greyElephant");
		greyElephant.hidden = true;
		const brownElephant = await findByTestId(testHolder, "brownElephant");
		brownElephant.hidden = true;
	});

	it("should change the hidden state when checkbox checked", () => {
		return checkboxTestHelper("cb18", "cb18df1", "isHidden");
	});

	it("should change the disabled state when checkbox checked", () => {
		return checkboxTestHelper("cb18a", "cb18df1", "isDisabled");
	});

	it("should change the required state when checkbox checked", () => {
		return checkboxTestHelper("cb18b", "cb18df1", "isMandatory");
	});

	it("should honor 'or' when one is true", () => {
		return subordinate1TestHelper(true, "a");
	});

	it("should not honor 'or' conditions when none are true", () => {
		return subordinate1TestHelper(false, "b");
	});

	it("should honor 'or' conditions when the last is true", () => {
		return subordinate1TestHelper(true, "c");
	});

	it("should honor wc-and with nested wc-or and conditions nested or matches first", () => {
		return subordinate4TestHelperColorPickerChange(true, "white");
	});

	it("should honor condition sibling to wc-or", () => {
		return subordinate4TestHelperColorPickerChange(false, "white", "rgYesNoNo");
	});

	it("should honor last condition sibling to wc-or", () => {
		return subordinate4TestHelperColorPickerChange(false, "white", "rgYesNoYes", true);
	});

	it("should honor wc-and with nested wc-or and conditions nested or no match", () => {
		return subordinate4TestHelperColorPickerChange(false, "googoogoogaagaagaa");  // no match = -1
	});

	it("should honor wc-and with nested wc-or and conditions nested or second match", () => {
		return subordinate4TestHelperColorPickerChange(true, "offwhite");
	});

	it("should honor wc-and with nested wc-or and conditions nested or second match BUT and condition false", () => {
		return subordinate4TestHelperColorPickerChange(false, "offwhite", "rgYesNoNo");
	});

	it("should honor wc-and with nested wc-or and conditions nested or second match BUT last and condition false", () => {
		return subordinate4TestHelperColorPickerChange(false, "offwhite", "rgYesNoYes", true);
	});

	it("should honor wc-and with nested wc-or and conditions nested or with non matching or controller", () => {
		return subordinate4TestHelperColorPickerChange(false, "brown");
	});

	it("should honor wc-and with nested wc-or and conditions nested or with non matching or controller with and condition false", () => {
		return subordinate4TestHelperColorPickerChange(false, "brown", "rgYesNoNo");
	});

	it("should act on the components in a component group", () => {
		return simpleComponentGroupTest("rgYesNoYes4a", "rgYesNoNo4a");
	});

	it("should honor the wc-not element", () => {
		return simpleComponentGroupTest("rgYesNoNo4b", "rgYesNoYes4b");
	});

	it("should showInGroup and hideInGroup with and/or and date fields and essentially just general complexity", () => {
		const shouldChange = true;
		return findByTestId(testHolder, "greyElephant").then(greyElephant => {
			const whiteElephant = getByTestId(testHolder, "whiteElephant");
			const brownElephant = getByTestId(testHolder, "brownElephant");
			visibilityChecker({
				// All start hidden
				whiteElephant: true,
				greyElephant: true,
				brownElephant: true
			});

			const dateFieldContainer = getInput(testHolder, "dateFieldContainer");
			const dateField = getInput(testHolder, "dateMate");
			const textField = getInput(testHolder, "textMate");
			const numberMate = getInput(testHolder, "numberMate");
			dateFieldContainer.setAttribute("data-wc-value", "2028-10-28");
			textField.value = "orange";
			numberMate.value  = "0";
			dateField.dispatchEvent(new UIEvent("change", {
				bubbles: false,
				cancelable: false,
				view: window
			}));
			return new Promise(win => {
				setTimeout(() => {
					// tests the onTrue condition
					visibilityChecker({
						whiteElephant: shouldChange,
						greyElephant: !shouldChange,
						brownElephant: shouldChange
					});
					dateFieldContainer.setAttribute("data-wc-value", "2028-10-27");
					dateField.dispatchEvent(new UIEvent("change", {
						bubbles: false,
						cancelable: false,
						view: window
					}));
					setTimeout(() => {
						// tests the onFalse condition
						visibilityChecker({
							whiteElephant: !shouldChange,
							greyElephant: shouldChange,
							brownElephant: !shouldChange
						});
						win();
					}, delay);
				}, delay);
			});
		});
	});


	function simpleComponentGroupTest(showTriggerId, hideTriggerId) {
		return findInput(testHolder, showTriggerId).then(showTrigger => {
			const componentGroup = getByTestId(testHolder, "bargroup");
			const componentIds = Array.from(componentGroup.querySelectorAll("wc-component")).map(el => el.getAttribute("refid"));
			return new Promise(win => {
				shed.select(showTrigger);
				setTimeout(() => {
					componentIds.forEach(id => {
						expect(shed.isHidden(getByTestId(testHolder, id))).withContext(`${id} should have been shown`).toBeFalse();
					});
					const hideTrigger = getInput(testHolder, hideTriggerId);
					shed.select(hideTrigger);
					setTimeout(() => {
						componentIds.forEach(id => {
							expect(shed.isHidden(getByTestId(testHolder, id))).withContext(`${id} should have been hidden`).toBeTrue();
						});
						shed.select(showTrigger);
						setTimeout(() => {
							componentIds.forEach(id => {
								expect(shed.isHidden(getByTestId(testHolder, id))).withContext(`${id} should have been shown again`).toBeFalse();
							});
							win();
						}, delay);
					}, delay);
				}, delay);
			});
		});
	}

	function visibilityChecker(idStateMap) {
		for (const [id, shouldBeHidden] of Object.entries(idStateMap)) {
			expect(shed.isHidden(getByTestId(testHolder, id))).withContext(`${id} should be hidden = ${shouldBeHidden}`).toBe(shouldBeHidden);
		}
	}

	/**
	 * Darn complicated test for darn complicated subordinate rules.
	 * This is a test with two onTrue actions and rather complex condition logic.
	 */
	function subordinate4TestHelperColorPickerChange(shouldChange, selectVal, triggerId = "rgYesNoYes", forceGrey = false) {
		return findByTestId(testHolder, "whiteElephant").then(whiteElephant => {
			const forceGreyCheckbox = getInput(testHolder, "forceGrey");
			const greyElephant = getByTestId(testHolder, "greyElephant");
			const brownElephant = getByTestId(testHolder, "brownElephant");
			forceGreyCheckbox.checked = forceGrey;
			visibilityChecker({
				// All start hidden
				whiteElephant: true,
				greyElephant: true,
				brownElephant: true
			});
			greyElephant.removeAttribute("hidden");  /// now show this element
			brownElephant.removeAttribute("hidden");  /// now show this element

			const colorpicker = getSelect(testHolder, "colorpicker");
			colorpicker.selectedIndex = Array.from(colorpicker.options).findIndex(next => next.value === selectVal);
			const trigger = getInput(testHolder, triggerId);
			shed.select(trigger);
			return new Promise(win => {
				setTimeout(() => {
					visibilityChecker({
						whiteElephant: !shouldChange,
						greyElephant: shouldChange,
						brownElephant: shouldChange
					});
					shed.deselect(trigger);
					setTimeout(() => {
						visibilityChecker({
							whiteElephant: true,
							greyElephant: shouldChange,
							brownElephant: shouldChange
						});
						win();
					}, delay);
				}, delay);
			});
		});
	}


	/*
		A little helper for the basic checkbox subordinate tests with a single target.
	 */
	function checkboxTestHelper(triggerId, targetId, shedFunc) {
		return findByTestId(testHolder, targetId).then(target => {
			expect(shed[shedFunc](target)).toBeFalse();
			const trigger = getInput(testHolder, triggerId);
			shed.select(trigger);
			return new Promise(win => {
				setTimeout(() => {
					// tests the onTrue condition
					expect(shed[shedFunc](target)).withContext(`${triggerId} should change the state of ${targetId}`).toBeTrue();
					shed.deselect(trigger);
					setTimeout(() => {
						// tests the onFalse condition
						expect(shed.isHidden(target)).withContext(`${triggerId} should change the state of ${targetId}`).toBeFalse();
						win();
					}, delay);
				}, delay);
			})
		});
	}

	function subordinate1TestHelper(shouldChange, selectVal) {
		const triggerId = "enable_category";
		const targetId = "enable_text";
		const shedFunc = "isDisabled";

		return findByTestId(testHolder, targetId).then(target => {
			expect(shed[shedFunc](target)).toBeTrue();
			const trigger = getSelect(testHolder, triggerId);
			trigger.selectedIndex = Array.from(trigger.options).findIndex(next => next.value === selectVal);
			shed.select(trigger);
			return new Promise(win => {
				setTimeout(() => {
					// tests the onTrue condition
					if (shouldChange) {
						expect(shed[shedFunc](target)).withContext(`${triggerId} should change the state of ${targetId}`).toBeFalse();
					} else {
						expect(shed[shedFunc](target)).withContext(`${triggerId} should change the state of ${targetId}`).toBeTrue();
					}

					trigger.selectedIndex = -1;
					shed.deselect(trigger);
					setTimeout(() => {
						// tests the onFalse condition
						expect(shed[shedFunc](target)).withContext(`${triggerId} should change the state of ${targetId}`).toBeTrue();
						win();
					}, delay);
				}, delay);
			});
		});
	}

});
