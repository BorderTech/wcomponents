import "wc/ui/validation/textArea.mjs";
import {getInput} from "../helpers/specUtils.mjs";

describe("wc/ui/validation/textArea", ()=> {
	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const testContent = `
			<div>
				<form id="form1">
					<span class="wc-input-wrapper" id="ta1" style="width:10em">
						<textarea title="Life Story" id="ta1_input" data-testid="ta1" name="ta1" required style="width:5em"></textarea>
					</span>
					<span class="wc-input-wrapper" id="ta2" style="width:10em">
						<textarea title="Life Story" id="ta2_input" data-testid="ta2" name="ta2" style="width:5em"></textarea>
					</span>
					<span class="wc-input-wrapper" id="ta3" style="width:10em">
						<textarea minlength="4" title="Life Story" id="ta3_input" data-testid="ta3" name="ta3" style="width:5em">123</textarea>
					</span>
					<span class="wc-input-wrapper" id="ta4" style="width:10em">
						<textarea maxlength="6" title="Life Story" id="ta4_input" data-testid="ta4" name="ta4" style="width:5em">1234567</textarea>
					</span>
					<span class="wc-input-wrapper" id="ta5" style="width:10em">
						<textarea data-wc-min="4" title="Life Story" id="ta5_input" data-testid="ta5" name="ta5" style="width:5em">123</textarea>
					</span>
					<span class="wc-input-wrapper" id="ta6" style="width:10em">
						<textarea data-wc-maxlength="6" title="Life Story" id="ta6_input" data-testid="ta6" name="ta6" style="width:5em">1234567</textarea>
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


	it("Should flag a required field as invalid when no value is present", function() {
		const element = getInitedTextArea("ta1");
		element.value = "";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "Alpha\nBravo\nCharlie\nDelta";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag an optional field as invalid when no value is present", function() {
		const element = getInitedTextArea("ta2");
		element.value = "";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when value is below minlength", function() {
		const element = getInitedTextArea("ta3");
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value += "4";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when value is above maxlength", function() {
		const element = getInitedTextArea("ta4");
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "123456";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when value is below custom minlength", function() {
		const element = getInitedTextArea("ta5");
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value += "4";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when value is above custom maxlength", function() {
		const element = getInitedTextArea("ta6");
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "123456";
		fireChangeOnTextArea(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});


	/**
	 * Helper for tests, fires a change event on the dropdown.
	 * @param element
	 */
	function fireChangeOnTextArea(element) {
		const changeEvent = new window.Event("change", {
			bubbles: false,
			cancelable: false
		});
		element.dispatchEvent(changeEvent);
	}

	/**
	 * Helper for tests, gets a field from the DOM and initialises it.
	 * @param testId The data-testid of the element you want.
	 * @return {HTMLInputElement}
	 */
	function getInitedTextArea(testId) {
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
