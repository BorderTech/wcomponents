import getLabelsForElement from "wc/dom/getLabelsForElement.mjs";
import {JSDOM} from "jsdom";
import {getResoucePath} from "../helpers/specUtils.mjs";
import domTesting from "@testing-library/dom";

describe("wc/dom/getLabelsForElement", () => {
	let testHolder;

	beforeAll(() => {
		return JSDOM.fromFile(getResoucePath("domGetLabelsForElement.html", false)).then(dom => {
			testHolder = dom.window.document.body;
		});
	});
	
	it("testGetLabel", function() {
		const element = domTesting.getByTestId(testHolder, "male"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "maleLabel");
		expect(labels[0]).toBe(expected);
	});

	it("testGetLabelNested", function() {
		const element = domTesting.getByTestId(testHolder, "female"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "femaleLabel");
		expect(labels[0]).toBe(expected);
	});

	it("testGetLabelWrapped", function() {
		const element = domTesting.getByTestId(testHolder, "wrappedinput"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "wrappedlabel");
		expect(labels[0]).toBe(expected);
	});

	it("testGetLabelForFieldset", function() {
		const element = domTesting.getByTestId(testHolder, "fs1"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "leg1");
		expect(labels[0]).toBe(expected);
	});

	it("testGetLabelCountMoreThanOne", function() {
		const element = domTesting.getByTestId(testHolder, "male"),
			labels = getLabelsForElement(element),
			expected = 3;

		expect(labels.length).toBe(expected);
	});

	it("testGetReadOnly", function() {
		const element = domTesting.getByTestId(testHolder, "rofield"),
			labels = getLabelsForElement(element, true),
			expected = domTesting.getByTestId(testHolder, "rolabel");

		expect(labels[0]).toBe(expected);
	});

	it("testGetWrappedReadOnly", function() {
		const element = domTesting.getByTestId(testHolder, "wrappedroinput"),
			labels = getLabelsForElement(element, true),
			expected = domTesting.getByTestId(testHolder, "wrappedrolabel");

		expect(labels[0]).toBe(expected);
	});

	it("testUnlabelled", function() {
		const result = getLabelsForElement(domTesting.getByTestId(testHolder, "unlabelled"));
		expect(Array.isArray(result)).toBeTrue();
		expect(result.length).toBe(0);
	});

	it("testAriaLabel", function() {
		const element = domTesting.getByTestId(testHolder, "aria-text"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "aria-label");
		expect(labels[0]).toBe(expected);
	});

	it("testAriaLabelUnavailable", function() {
		const element = domTesting.getByTestId(testHolder, "aria-text2"),
			labels = getLabelsForElement(element);
		expect(labels.length).toBe(0);
	});

	it("testAriaMultipleLabel", function() {
		const element = domTesting.getByTestId(testHolder, "aria-text3"),
			labels = getLabelsForElement(element),
			expected1 = domTesting.getByTestId(testHolder, "aria-label3"),
			expected2 = domTesting.getByTestId(testHolder, "aria-label4");
		expect(labels.length).toBe(2);
		expect(labels[0]).toBe(expected1);
		expect(labels[1]).toBe(expected2);
	});

	it("testAriaMultipleLabel2", function() {
		const element = domTesting.getByTestId(testHolder, "aria-input"),
			labels = getLabelsForElement(element),
			expected1 = domTesting.getByTestId(testHolder, "billing"),
			expected2 = domTesting.getByTestId(testHolder, "name");
		expect(labels.length).toBe(2);
		expect(labels[0]).toBe(expected1);
		expect(labels[1]).toBe(expected2);
	});

	it("testDivElementAriaLabel", function() {
		const element = domTesting.getByTestId(testHolder, "main"),
			labels = getLabelsForElement(element),
			expected = domTesting.getByTestId(testHolder, "foo");
		expect(labels[0]).toBe(expected);
	});

	it("testNullElement", function() {
		const labels = getLabelsForElement(null);
		expect(labels).toBeFalsy();
	});

});
