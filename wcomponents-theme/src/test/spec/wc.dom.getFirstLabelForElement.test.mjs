import getFirstLabelForElement from "wc/ui/getFirstLabelForElement.mjs";
import {setUpExternalHTML} from "../helpers/specUtils.mjs";
import domTesting from "@testing-library/dom";

describe("wc/ui/getFirstLabelForElement", () => {
	let testHolder;

	beforeAll(() => {
		return setUpExternalHTML("domTest.html").then(dom => {
			testHolder = dom.window.document.body;
		});
	});
	
	it("testGetFirstLabel", function() {
		const element = domTesting.getByTestId(testHolder, "male"),
			label = getFirstLabelForElement(element),
			expected = domTesting.getByTestId(testHolder, "maleLabel");
		expect(label).toBe(expected);
	});

	it("testGetFirstLabelForFieldset", function() {
		const element = domTesting.getByTestId(testHolder, "fs1"),
			label = getFirstLabelForElement(element),
			expected = domTesting.getByTestId(testHolder, "leg1");
		expect(label).toBe(expected);
	});

	it("testGetFirstLabelContentOnly", function() {
		const element = domTesting.getByTestId(testHolder, "male"),
			label = getFirstLabelForElement(element, true),
			expected = "Male";
		expect(label).toBe(expected);
	});

	it("testGetFirstAriaLabel", function() {
		const element = domTesting.getByTestId(testHolder, "aria-text"),
			label = getFirstLabelForElement(element),
			expected = domTesting.getByTestId(testHolder, "aria-label");
		expect(label).toBe(expected);
	});
});
