import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";
import domTesting from "@testing-library/dom";

describe("wc/ui/getFirstLabelForElement", () => {
	let testHolder;

	beforeAll(() => {
		return setUpExternalHTML("domUsefulDom.html").then(dom => {
			testHolder = dom.window.document.body;
		});
	});

	/**
	 * Get the selected radio elements in a radio group
	 */
	it("testGetSelectedRadio", function() {
		const element = domTesting.getByTestId(testHolder, "radio1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(1);
	});


	it("testGetSelectedRadioValue", function() {
		const element = domTesting.getByTestId(testHolder, "radio1"),
			result = getFilteredGroup(element);
		expect(result[0].value).toBe("Butter");
	});

	it("testGetUnselectedItemsRadio", function() {
		const result = /** @type HTMLInputElement[] */(
			getFilteredGroup(domTesting.getByTestId(testHolder, "radio1"), {
				filter: getFilteredGroup.FILTERS.deselected
			}));
		expect(result.length).toBe(2);
		expect(result[0].value).toBe("Milk");
		expect(result[1].value).toBe("Cheese");
	});

	/**
	 * Get the selected radio elements in a radio group
	 * but none are selected
	 */
	it("testGetSelectedRadioNoneSelected", function() {
		const element = domTesting.getByTestId(testHolder, "radio3"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(0);
	});

	/**
	 * Get the selected radio elements in a radio group
	 * the selected radio is disabled
	 */
	it("testGetSelectedRadioDisabled", function() {
		const element = domTesting.getByTestId(testHolder, "radio4"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(0);
	});

	it("testGetDisabledRadio", function() {
		const element = domTesting.getByTestId(testHolder, "radio4"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.disabled
			}));
		expect(result.length).toBe(1);
	});

	it("testGetEnabledRadio", function() {
		const element = domTesting.getByTestId(testHolder, "radio4"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.enabled
			}));
		expect(result.length).toBe(2);
	});

	it("testGetHiddenCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.hidden
			}));
		expect(result.length).toBe(2);
		expect(result[0].id).toBe("chkItem3");
		expect(result[1].id).toBe("chkItem6");
	});

	it("testGetVisibleCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.visible
			}));
		expect(result.length).toBe(4);
	});

	it("testFilterSelectedCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.selected
			}));
		expect(result[0].id).toBe("chkItem2");
		expect(result[1].id).toBe("chkItem4");
		expect(result[2].id).toBe("chkItem6");
		expect(result.length).toBe(3);
	});

	it("testFilterSelectedDisabledCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.selected + getFilteredGroup.FILTERS.disabled
			}));
		expect(result[0].id).toBe("chkItem2");
		expect(result[1].id).toBe("chkItem6");
		expect(result.length).toBe(2);
	});

	it("testFilterSelectedHiddenDisabledCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.selected + getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled
			}));
		expect(result[0].id).toBe("chkItem6");
		expect(result.length).toBe(1);
	});

	it("testFilterHiddenDisabledDeselectedCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled + getFilteredGroup.FILTERS.deselected
			}));
		expect(result.length).toBe(0);
	});

	it("testFilterHiddenDisabledCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled
			}));
		expect(result[0].id).toBe("chkItem6");
		expect(result.length).toBe(1);
	});

	it("testFilterHiddenUncheckedCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "chkItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.deselected
			}));
		expect(result[0].id).toBe("chkItem3");
		expect(result.length).toBe(1);
	});

	/**
	 * Get the selected radio elements in a checkbox group
	 */
	it("testGetSelectedCheckbox", function() {
		const element = domTesting.getByTestId(testHolder, "cb1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(2);
	});

	/**
	 * Get the selected radio elements in a checkbox group
	 */
	it("testGetSelectedCheckboxValue", function() {

		const element = domTesting.getByTestId(testHolder, "cb1"),
			result = getFilteredGroup(element);
		if (result[0].value === "Bike") {
			expect(result[1].value).toBe("Airplane");
		} else if (result[0].value === "Airplane") {
			expect(result[1].value).toBe("Bike");
		} else {
			fail("getFilteredGroup did not find the selected checkboxes in the group");
		}
	});

	/**
	 * Get the selected options in a select
	 */
	it("testGetSelectedSelect", function() {

		const element = domTesting.getByTestId(testHolder, "select1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(1);
	});

	it("testGetSelectedSelectValue", function() {

		const element = domTesting.getByTestId(testHolder, "select1"),
			result = /** @type HTMLInputElement[] */(getFilteredGroup(element));
		expect(result[0].value).toBe("mercedes");
	});

	/**
	 * Get the selected options in a multi select
	 */
	it("testGetSelectedSelectMulti", function() {

		const element = domTesting.getByTestId(testHolder, "select2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(2);
	});

	/**
	 * Get the selected options in a multi select
	 */
	it("testGetSelectedSelectMultiValue", function() {

		const element = domTesting.getByTestId(testHolder, "select2"),
			result = getFilteredGroup(element);
		if (result[0].value === "volvo") {
			expect(result[1].value).toBe("mercedes");
		} else if (result[0].value === "mercedes") {
			expect(result[1].value).toBe("volvo");
		} else {
			fail("getFilteredGroup did not find the selected options in the multi-select");
		}
	});

	/**
	 * Get the selected options in a multi select which has optgroups
	 */
	it("testGetSelectedSelectMultiOptgroup", function() {

		const element = domTesting.getByTestId(testHolder, "select3"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(2);
	});

	it("testGetSelectedSelectMultiOptgroupValue", function() {

		const element = domTesting.getByTestId(testHolder, "select3"),
			result = getFilteredGroup(element);
		if (result[0].value === "volvo") {
			expect(result[1].value).toBe("audi");
		} else if (result[0].value === "audi") {
			expect(result[1].value).toBe("volvo");
		} else {
			fail("getFilteredGroup did not find the selected options in the multi-select");
		}
	});

	/**
	 * Get the selected options in a select by passing in one of the option elements
	 */
	it("testGetSelectedOption", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select1")),
			result = /** @type HTMLElement[] */(getFilteredGroup(element.options[0]));
		expect(result.length).toBe(1);
	});

	it("testGetSelectedOptionValue", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select1")),
			result = getFilteredGroup(element.options[0]);
		expect(result[0].value).toBe("mercedes");
	});

	it("testGetSelectedOptionInOptgroup", function() {
		const element = domTesting.getByTestId(testHolder, "opt5"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(0);
	});

	/**
	 * Get the selected options in a multi select by passing in one of the option elements
	 */
	it("testGetSelectedOptionMulti", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select2")),
			result = /** @type HTMLElement[] */(getFilteredGroup(element.options[0]));
		expect(result.length).toBe(2);
	});

	it("testGetSelectedOptionMultiValue", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select2")),
			result = getFilteredGroup(element.options[0]);
		if (result[0].value === "volvo") {
			expect(result[1].value).toBe("mercedes");
		} else if (result[0].value === "mercedes") {
			expect(result[1].value).toBe("volvo");
		} else {
			fail("getFilteredGroup did not find the selected options in the multi-select");
		}
	});

	/**
	 * Get the selected options in a multi select by passing in one of the option elements
	 * which is contained in an optgroup
	 */
	it("testGetSelectedOptionMultiOptgroup", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select3")),
			result = /** @type HTMLElement[] */(getFilteredGroup(element.options[0]));
		expect(result.length).toBe(1);
	});

	it("testGetSelectedOptionMultiOptgroupValue", function() {
		const element = /** @type HTMLSelectElement */(domTesting.getByTestId(testHolder, "select3")),
			result = /** @type HTMLOptionElement[] */(getFilteredGroup(element.options[0]));
		expect(result[0].value).toBe("volvo");
	});

	/**
	 * Get the selected options in a multi select by passing in an optgroup
	 * only the selected options within the optgroup should be returned
	 */
	it("testGetSelectedOptgroupMulti", function() {
		const element = domTesting.getByTestId(testHolder, "select3"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element.getElementsByTagName("optgroup")[1]));
		expect(result.length).toBe(1);
	});

	it("testGetSelectedOptgroupMultiValue", function() {
		const element = domTesting.getByTestId(testHolder, "select3"),
			result = /** @type HTMLOptionElement[] */(getFilteredGroup(element.getElementsByTagName("optgroup")[1]));
		expect(result[0].value).toBe("audi");
	});

	/**
	 * Get the selected options in an aria select
	 */
	it("testGetSelectedAriaSelect", function() {

		const element = domTesting.getByTestId(testHolder, "fauxSelect1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(1);
	});

	/**
	 * Get the selected options in an aria multi select
	 */
	it("testGetSelectedAriaSelectMulti", function() {

		const element = domTesting.getByTestId(testHolder, "fauxSelect2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result.length).toBe(5);
	});


	/**
	 * Get the unselected options in an aria multi select
	 */
	it("testGetUnselectedItemsAriaSelectMulti", function() {

		const element = domTesting.getByTestId(testHolder, "fauxSelect2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				filter: getFilteredGroup.FILTERS.deselected
			}));
		expect(result.length).toBe(2);
	});

	/**
	 * Get the selected radio elements in an aria radio group by container
	 */
	it("testGetSelectedRadioByContainer", function() {
		const element = domTesting.getByTestId(testHolder, "radGrp1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("fauxRad4");
	});

	/**
	 * Get the selected radio elements in an aria radio group
	 */
	it("testGetSelectedAriaRadio", function() {
		const element = domTesting.getByTestId(testHolder, "fauxRad5"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("fauxRad4");
	});

	it("testGetSelectedAriaRadioAsObject", function() {
		const element = domTesting.getByTestId(testHolder, "fauxRad5"),
			result = /** @type { { filtered: HTMLElement[], unfiltered: HTMLElement[] }} */(getFilteredGroup(element, {
				asObject: true
			}));
		expect(result.filtered[0].id).toBe("fauxRad4");
		expect(result.unfiltered.length).toBe(6);
	});

	/**
	 * Get the selected radio elements in an aria radio group by container with aria-owns
	 */
	it("testGetSelectedRadioByContainerWithAriaOwns", function() {
		const element = domTesting.getByTestId(testHolder, "radGrp2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("rO2");
	});

	/**
	 * Get the selected radio elements in an aria radio group where the container is not
	 * a direct ancestor of the radio buttons
	 */
	it("testGetSelectedRadioWithAriaOwns", function() {
		const element = domTesting.getByTestId(testHolder, "rO3"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("rO2");
	});


	it("testGetSelectedAriaRadioItemByContainer", function() {
		const element = domTesting.getByTestId(testHolder, "menu1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("radItem2");
		expect(result.length).toBe(1);
	});

	it("testGetSelectedSeededGroupAriaRadioItems", function() {
		const group = [];
		for (let i = 1; i <= 6; i++) {
			group.push(testHolder.ownerDocument.getElementById("radItem" + i));
			group.push(testHolder.ownerDocument.getElementById("radBarItem" + i));
		}
		const result = /** @type HTMLElement[] */(getFilteredGroup(group));
		expect(result.length).toBe(2);
	});

	it("testGetDisablededSeededGroupAriaRadioItems", function() {
		const group = [];
		for (let i = 1; i <= 6; i++) {
			group.push(testHolder.ownerDocument.getElementById("radItem" + i));
			group.push(testHolder.ownerDocument.getElementById("radBarItem" + i));
		}
		const result = /** @type HTMLElement[] */(getFilteredGroup(group, {
			filter: getFilteredGroup.FILTERS.disabled
		}));
		expect(result.length).toBe(0);
	});

	it("testGetSelectedAriaRadioItem", function() {
		const element = domTesting.getByTestId(testHolder, "radItem4"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("radItem2");
		expect(result.length).toBe(1);
	});

	it("testGetSelectedAriaCheckboxItemByContainer", function() {
		const element = domTesting.getByTestId(testHolder, "menubar2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("chkBarItem2");
		expect(result.length).toBe(1);
	});

	it("testGetSelectedAriaCheckboxItem", function() {
		const element = domTesting.getByTestId(testHolder, "chkBarItem1"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element));
		expect(result[0].id).toBe("chkBarItem2");
		expect(result.length).toBe(1);
	});

	it("testGetSelectedFauxCheckboxesByFormWithFilterWd", function() {
		const element = domTesting.getByTestId(testHolder, "form2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				itemWd: "[role='checkbox']"
			}));
		expect(result.length).toBe(2);
		expect(result[0].id).toBe("form2Chk2");
		expect(result[1].id).toBe("form2Chk4");
	});

	it("testGetSelectedFauxCheckablesByFormWithFilterWd", function() {
		const element = domTesting.getByTestId(testHolder, "form2"),
			result = /** @type HTMLElement[] */(getFilteredGroup(element, {
				itemWd: "[role]"
			}));
		expect(result.length).toBe(3);
		expect(result[0].id).toBe("form2rad2");
		expect(result[1].id).toBe("form2Chk2");
		expect(result[2].id).toBe("form2Chk4");
	});

	it("testGetSelectedFauxCheckablesByFormWithFilterWdAsObject", function() {
		const element = domTesting.getByTestId(testHolder, "form2"),
			result = /** @type { { filtered: HTMLElement[], unfiltered: HTMLElement[] }} */(getFilteredGroup(element, {
				itemWd: "[role]",
				asObject: true
			}));
		expect(result.filtered.length).toBe(3);
		expect(result.unfiltered.length).toBe(12);
	});

	/**
	 * Exception tests are good for line coverage reports...
	 */
	it("testGetSelectedWithNullElement", function() {
		expect(() => getFilteredGroup(null)).toThrowError();
	});
});
