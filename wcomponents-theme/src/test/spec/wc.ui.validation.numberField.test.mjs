import "wc/ui/validation/numberField.mjs";
import {getInput} from "../helpers/specUtils.mjs";

describe("wc/ui/validation/numberField", ()=> {
	let ownerDocument;
	let testHolder;
	const waitForI18n = 50;

	beforeAll(function() {
		const testContent = `
			<div>
				<form id="form1">
					<span class="wc-input-wrapper" id="nf1" style="width:10em">
						<input title="The digits" id="nf1_input" data-testid="nf1" name="nf1" required style="width:5em" type="number"/>
					</span>
					<span class="wc-input-wrapper" id="nf2" style="width:10em">
						<input title="The digits" id="nf2_input" data-testid="nf2" name="nf2" style="width:5em" type="number"/>
					</span>
					<span class="wc-input-wrapper" id="nf3" style="width:10em">
						<input min="4" value="3" title="The digits" id="nf3_input" data-testid="nf3" name="nf3" style="width:5em" type="number"/>
					</span>
					<span class="wc-input-wrapper" id="nf4" style="width:10em">
						<input max="6" value="7" title="The digits" id="nf4_input" data-testid="nf4" name="nf4" style="width:5em" type="number"/>
					</span>
					<span class="wc-input-wrapper" id="nf5" style="width:10em">
						<input value="7" title="The digits" id="nf5_input" data-testid="nf5" name="nf5" style="width:5em" type="number"/>
					</span>
					<span class="wc-input-wrapper" id="nf6" style="width:10em">
						<input value="7" title="The digits" id="nf6_input" data-testid="nf6" name="nf6" style="width:5em" type="number"/>
					</span>
				</form>
			</div>`;
		ownerDocument = document;
		testHolder = ownerDocument.body;
		testHolder.innerHTML = testContent;
	});

	afterAll(() => {
		testHolder.innerHTML = "";
	});


	it("Should flag a required field as invalid when no value is present", function(done) {
		const element = getInitedNumberField("nf1");
		setTimeout(() => {
			element.value = "";
			fireChangeOnNumberField(element);
			expect(element.getAttribute("aria-invalid")).toBe("true");
			element.value = "6";
			fireChangeOnNumberField(element);
			setTimeout(() => {
				expect(element.getAttribute("aria-invalid")).not.toBe("true");
				done();
			}, waitForI18n);
		}, waitForI18n);
	});

	it("Should not flag an optional field as invalid when no value is present", function(done) {
		const element = getInitedNumberField("nf2");
		fireChangeOnNumberField(element);
		setTimeout(() => {
			element.value = "";
			fireChangeOnNumberField(element);
			expect(element.getAttribute("aria-invalid")).not.toBe("true");
			done();
		}, waitForI18n);
	});

	it("Should flag as invalid when value is below min", function(done) {
		const element = getInitedNumberField("nf3");
		fireChangeOnNumberField(element);
		setTimeout(() => {
			expect(element.getAttribute("aria-invalid")).toBe("true");
			element.value = element.getAttribute("min");
			fireChangeOnNumberField(element);
			setTimeout(() => {
				expect(element.getAttribute("aria-invalid")).not.toBe("true");
				done();
			}, waitForI18n);
		}, waitForI18n);
	});

	it("Should flag as invalid when value is above max", function(done) {
		const element = getInitedNumberField("nf4");
		fireChangeOnNumberField(element);
		setTimeout(() => {
			expect(element.getAttribute("aria-invalid")).toBe("true");
			element.value = element.getAttribute("max");
			fireChangeOnNumberField(element);
			setTimeout(() => {
				expect(element.getAttribute("aria-invalid")).not.toBe("true");
				done();
			}, waitForI18n);
		}, waitForI18n);
	});

	it("Should not flag as invalid when max attribute is bad", function(done) {
		const element = getInitedNumberField("nf5");
		element.setAttribute("max", "bad");
		fireChangeOnNumberField(element);
		setTimeout(() => {
			expect(element.getAttribute("aria-invalid")).not.toBe("true");
			done();
		}, waitForI18n);
	});

	it("Should not flag as invalid when min attribute is bad", function(done) {
		const element = getInitedNumberField("nf6");
		element.setAttribute("min", "bad");
		fireChangeOnNumberField(element);
		setTimeout(() => {
			expect(element.getAttribute("aria-invalid")).not.toBe("true");
			done();
		}, waitForI18n);
	});


	/**
	 * Helper for tests, fires a change event on the field.
	 * @param {HTMLElement} element
	 */
	function fireChangeOnNumberField(element) {
		const changeEvent = new window.Event("change", {
			bubbles: false,
			cancelable: false
		});
		element.dispatchEvent(changeEvent);
	}

	/**
	 * Helper for tests, gets a field from the DOM and initialises it.
	 * @param  {string} testId The data-testid of the element you want.
	 * @return {HTMLInputElement}
	 */
	function getInitedNumberField(testId) {
		const focusEvent = new window.UIEvent("focus", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		const element = getInput(testHolder, testId);
		element.dispatchEvent(focusEvent);
		return element;
	}
});
