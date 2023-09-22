import convertDynamicContent from "wc/dom/convertDynamicContent.mjs";
import shed from "wc/dom/shed.mjs";
import domTesting from "@testing-library/dom";

describe("wc/dom/convertDynamicContent", () => {
	const formId = "abc123";
	const CONVERSION_TARGET_ID = "conversionTarget";
	const html = `
		<form data-testid="${formId}">
			<div data-testid="${CONVERSION_TARGET_ID}">
				<input type="text" id="field1" name="field1name" value="foo"/>
				<span id="notAField">something to be ignored</span>
			</div>
			<input type="text" id="field4" name="field4name" value="foobar"/>
		</form>`;

	const CONVERTIBLES = ["[name]", "[data-wc-name]", "[data-wc-value]"].join(),
		HIDDEN_FIELDS = "input[type='hidden']";

	let testHolder;

	beforeEach(() => {
		testHolder = document.body;
		testHolder.innerHTML = html;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("testBeforeConvert", function() {
		const form = domTesting.getByTestId(testHolder, formId);
		expect(form.querySelector("#notAField")).not.toBeNull();
	});

	it("testConvertRemovesNoneFormNodes", function() {
		const form = domTesting.getByTestId(testHolder, formId);
		convertDynamicContent(form);
		expect(form.querySelector("#notAField")).toBeNull();
	});

	it("testRemovalBeforeConvert", function() {
		const form = domTesting.getByTestId(testHolder, formId);
		domTesting.getByTestId(form, CONVERSION_TARGET_ID);  // getBy throws error if no elements match or more than one element matches
	});

	it("testConvertRemovesNoneFormNodesContainingFormNodes", function() {
		const form = domTesting.getByTestId(testHolder, formId);
		convertDynamicContent(form);
		expect(domTesting.queryByTestId(form, CONVERSION_TARGET_ID)).toBeNull();  // queryBy returns null if none match
	});

	it("testConvertAllFormNodesBecomeHiddenFields", function() {
		const form = domTesting.getByTestId(testHolder, formId);
		const candidates = form.querySelectorAll(CONVERTIBLES);
		convertDynamicContent(form);

		for (let i = 0; i < candidates.length; ++i) {
			let next = candidates[i];
			let nextName = next["name"] || next.getAttribute("data-wc-name");
			let expected = next["value"] || next.getAttribute("data-wc-value");
			let target = form.querySelector("input[type = 'hidden'][name = '" + nextName + "']") || fail("Did not find target with name " + nextName);
			expect(target["value"]).toBe(expected);
		}

	});

	it("testConvertTargetNotForm", function() {
		const target = domTesting.getByTestId(testHolder, CONVERSION_TARGET_ID),
			candidates = target.querySelectorAll(CONVERTIBLES);
		let expected = 0;
		for (let i = 0; i < candidates.length; ++i) {
			if (!shed.isDisabled(candidates[i])) {
				expected++;
			}
		}

		convertDynamicContent(target);
		expect(target.querySelectorAll(HIDDEN_FIELDS).length).withContext("All non disabled candidates should be converted " + target.innerHTML).toBe(expected);
	});
});

