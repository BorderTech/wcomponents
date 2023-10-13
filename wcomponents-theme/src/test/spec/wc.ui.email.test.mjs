import "wc/ui/email.mjs";
import domTesting from "@testing-library/dom";
import {getInput} from "../helpers/specUtils.mjs";

describe("wc/ui/email", () => {

	let emailFieldId = "my-email-field";
	let testHolder;

	beforeAll(function() {
		testHolder = document.body;
	});

	beforeEach(function() {
		testHolder.innerHTML = `<label>Your email<input type="email" data-testid="${emailFieldId}"/></label>`;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("testGetWidget_correctWidget", function() {
		const field = getInput(testHolder, emailFieldId);
		field.value = "scott.morrison@example.com";
		const event = new UIEvent("change", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		field.dispatchEvent(event);
	});

});
