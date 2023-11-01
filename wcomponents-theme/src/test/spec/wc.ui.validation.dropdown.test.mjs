import "wc/ui/validation/dropdown.mjs";
import {getSelect} from "../helpers/specUtils.mjs";
import wcconfig from "wc/config.mjs";

describe("wc/ui/validation/dropdown", ()=> {
	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const testContent = `
			<div>
				<form id="form1">
					<span class="wc-input-wrapper" id="dd1" style="width:10em">
						<select id="dd1_input" data-testid="dd1" name="dd1" required style="width:5em">
							<option>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd2" style="width:10em">
						<select id="dd2_input" data-testid="dd2" name="dd2" required style="width:5em">
							<option>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd3" style="width:10em">
						<select id="dd3_input" data-testid="dd3" name="dd3" style="width:5em">
							<option>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd4" style="width:10em">
						<select id="dd4_input" data-testid="dd4" name="dd4" style="width:5em" multiple data-wc-min="2" data-wc-max="3">
							<option selected>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd5" style="width:10em">
						<select id="dd5_input" data-testid="dd5" name="dd5" style="width:5em" multiple data-wc-min="2" data-wc-max="3">
							<option selected>apple</option>
							<option selected>orange</option>
							<option selected>banana</optionselected>
							<option selected>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd6" style="width:10em">
						<select id="dd6_input" data-testid="dd6" name="dd6" style="width:5em" multiple data-wc-min="2" data-wc-max="3">
							<option selected>apple</option>
							<option selected>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd7" style="width:10em">
						<select id="dd7_input" data-testid="dd7" name="dd7" required style="width:5em">
							<option>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
					</span>

					<span class="wc-input-wrapper" id="dd8" style="width:10em">
						<select id="dd8_input" data-testid="dd8" name="dd8" required style="width:5em">
							<option>apple</option>
							<option>orange</option>
							<option>banana</option>
							<option>pear</option>
						</select>
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
			"doOnChange": true,
			"doOnBlur": false
		}, "validationManager");
	});

	it("Should flag a required dropdown as invalid when none are selected", function() {
		const dropdown = getInitedDropdown("dd1");
		dropdown.selectedIndex = -1;
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).toBe("true");
		dropdown.options[dropdown.options.length - 1].selected = true;
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag an optional dropdown as invalid when none is selected", function() {
		const dropdown = getInitedDropdown("dd3");
		dropdown.selectedIndex = -1;
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag a required dropdown as invalid when something is selected", function() {
		const dropdown = getInitedDropdown("dd2");
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a constrained dropdown as invalid when less than 'min' items are selected", function() {
		const dropdown = getInitedDropdown("dd4");
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).toBe("true");
		dropdown.options[dropdown.options.length - 1].selected = true;
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should flag a constrained dropdown as invalid when more than 'max' items are selected", function() {
		const dropdown = getInitedDropdown("dd5");
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).toBe("true");
		dropdown.selectedOptions[dropdown.selectedOptions.length - 1].selected = false;
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should not flag a constrained dropdown as invalid when 'min' and 'max' constraints aren't violated", function() {
		const dropdown = getInitedDropdown("dd6");
		fireChangeOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	it("Should validate on blur when configured to do so", function() {
		wcconfig.set({
			"doOnChange": true,
			"doOnBlur": true
		}, "validationManager");
		const dropdown = getInitedDropdown("dd7");
		dropdown.selectedIndex = -1;
		fireBlurOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).toBe("true");
	});

	it("Should not validate on blur when not configured to do so", function() {
		const dropdown = getInitedDropdown("dd8");
		dropdown.selectedIndex = -1;
		fireBlurOnDropdown(dropdown);
		expect(dropdown.getAttribute("aria-invalid")).not.toBe("true");
	});

	/**
	 * Helper for tests, fires a change event on the dropdown.
	 * @param dropdown
	 */
	function fireChangeOnDropdown(dropdown) {
		const changeEvent = new window.Event("change", {
			bubbles: false,
			cancelable: false
		});
		dropdown.dispatchEvent(changeEvent);
	}

	/**
	 * Helper for tests, fires a blur event on the dropdown.
	 * @param dropdown
	 */
	function fireBlurOnDropdown(dropdown) {
		const blurEvent = new window.UIEvent("blur", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		dropdown.dispatchEvent(blurEvent);
	}

	/**
	 * Helper for tests, gets a dropdown from the DOM and initialises it.
	 * @param testId The data-testid of the dropdown you want.
	 * @return {HTMLSelectElement}
	 */
	function getInitedDropdown(testId) {
		const focusEvent = new window.UIEvent("focus", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		const dropdown = getSelect(testHolder, testId);
		dropdown.dispatchEvent(focusEvent);
		return dropdown;
	}
});
