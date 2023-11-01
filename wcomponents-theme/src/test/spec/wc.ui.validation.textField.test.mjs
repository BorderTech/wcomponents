import "wc/ui/validation/textField.mjs";
import {getInput} from "../helpers/specUtils.mjs";
import wcconfig from "wc/config.mjs";

describe("wc/ui/validation/textField", ()=> {
	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const testContent = `
			<div>
				<form id="form1">
					<span class="wc-input-wrapper" id="tf1" style="width:10em">
						<input id="tf1_input" data-testid="tf1" name="tf1" required style="width:5em" placeholder="Search"/>
					</span>
					<span class="wc-input-wrapper" id="tf2" style="width:10em">
						<input id="tf2_input" data-testid="tf2" name="tf2" style="width:5em" placeholder="Search"/>
					</span>
					<span class="wc-input-wrapper" id="tf3" style="width:10em">
						<input  minlength="5" value="1234" id="tf3_input" data-testid="tf3" name="tf3" style="width:5em" placeholder="Search"/>
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

	afterEach(() => {
		wcconfig.set({
			rx: null
		}, "wc/ui/validation/textField");
	});

	it("Should flag a required field as invalid when no value is present", function() {
		const element = getInitedTextField("tf1");
		element.value = "";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "NJNN";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag an optional field as invalid when no value is present", function() {
		const element = getInitedTextField("tf2");
		element.value = "";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when no value is below minlength", function() {
		const element = getInitedTextField("tf3");
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value += "12345";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	/**
	 * Helper for tests, fires a change event on the dropdown.
	 * @param element
	 */
	function fireChangeOnTextField(element) {
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
	function getInitedTextField(testId) {
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
