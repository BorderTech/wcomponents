import dropdown from "wc/ui/dropdown.mjs";
import {getSelect} from "../helpers/specUtils.mjs";

describe("wc/ui/dropdown", ()=> {
	let ownerDocument;
	let testHolder;

	beforeAll(function() {
		const testContent = `
			<div>
				<form id="form1">
					<span class="wc-input-wrapper" id="dd1" style="width:10em">
						<select id="dd1_input" data-testid="dd1" name="dd1" required style="width:5em">
							<option value="a">apple</option>
							<option value="b">orange</option>
							<option value="c">banana</option>
							<option value="d">pear</option>
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

	it("Should select an option by value", function() {
		const element = getSelect(testHolder, "dd1");
		element.selectedIndex = -1;
		expect(element.selectedOptions.length).toBe(0);
		dropdown.setSelectionByValue(element, "b");
		expect(element.selectedIndex).toBe(1);
	});

	it("Should select an option by text", function() {
		const element = getSelect(testHolder, "dd1");
		element.selectedIndex = -1;
		expect(element.selectedOptions.length).toBe(0);
		dropdown.setSelectionByValue(element, "banana");
		expect(element.selectedIndex).toBe(2);
	});

	it("Should do nothing if no option found", function() {
		const element = getSelect(testHolder, "dd1");
		element.selectedIndex = -1;
		expect(element.selectedOptions.length).toBe(0);
		dropdown.setSelectionByValue(element, "apricot");
		expect(element.selectedIndex).toBe(-1);
	});

});
