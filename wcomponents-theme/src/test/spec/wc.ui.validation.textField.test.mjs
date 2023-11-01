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
					<span class="wc-input-wrapper" id="tf4" style="width:10em">
						<input type="email" value="1234" id="tf4_input" data-testid="tf4" name="tf4" style="width:5em" placeholder="Email"/>
					</span>
					<span class="wc-input-wrapper" id="tf5" style="width:10em">
						<input pattern="\\d{8,8}" value="1234" id="tf5_input" data-testid="tf5" name="tf5" style="width:5em" placeholder="8 Digits"/>
					</span>
					<span class="wc-input-wrapper" id="tf6" style="width:10em">
						<input type="email" value="" id="tf6_input" data-testid="tf6" name="tf6" style="width:5em" placeholder="Email"/>
					</span>
					<span class="wc-input-wrapper" id="tf7" style="width:10em">
						<input pattern="\\d{8,8}" value="" id="tf7_input" data-testid="tf7" name="tf7" style="width:5em" placeholder="8 Digits"/>
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

	it("Should flag a field as invalid when value length is below minlength", function() {
		const element = getInitedTextField("tf3");
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value += "12345";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag an email field as invalid when the input is not an email", function() {
		const element = getInitedTextField("tf4");
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "foo.bar@example.com";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a field as invalid when the input does not match the pattern attr", function() {
		const element = getInitedTextField("tf5");
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).toBe("true");
		element.value = "12345678";
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag an email field as invalid when there is no value", function() {
		const element = getInitedTextField("tf6");
		fireChangeOnTextField(element);
		expect(element.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag a pattern field as invalid when there is no value", function() {
		const element = getInitedTextField("tf7");
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
