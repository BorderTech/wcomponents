import subordinate, {initialiser} from "wc/ui/subordinate.mjs";
import {findInput, findSelect, getInput, getSelect, setUpExternalHTML} from "../helpers/specUtils.mjs";
import shed from "wc/dom/shed.mjs";
import timers from "wc/timers.mjs";
import {findByTestId} from "@testing-library/dom";

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

describe("wc/ui/subordinate Rule Tests", () => {
	const delay = 1;  // milliseconds to wait for events and stuff to be actioned
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
			})
		});
	}

});
