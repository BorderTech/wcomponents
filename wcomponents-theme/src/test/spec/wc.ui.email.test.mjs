import "wc/ui/email.mjs";
import domTesting from "@testing-library/dom";
import {getInput} from "../helpers/specUtils.mjs";
import feedback from "wc/ui/feedback.mjs";

describe("wc/ui/email", () => {
	const emailFieldId = "my-email-field",
		emailInputId = `${emailFieldId}_input`;
	let testHolder;

	beforeAll(function() {
		testHolder = document.body;
	});

	beforeEach(function() {
		testHolder.innerHTML = `
			<span class="wc-input-wrapper" id="${emailFieldId}" data-testid="${emailFieldId}">
				<label for="${emailInputId}">Your email</label>
				<input id="${emailInputId}" type="email" data-testid="${emailInputId}"/>
			</span>`;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("prompts the user when there is a typo", (done) => {
		const field = getInput(testHolder, emailInputId);
		field.setAttribute("value", "scott.morrison@hotmale.com");
		const event = new UIEvent("change", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		field.dispatchEvent(event);
		setTimeout(function() {
			const message = feedback.getBox(emailFieldId, -1);
			expect(message).withContext("There should be an email suggestion").toBeTruthy();
			done();
		}, 100);
	});

	it("does not prompt the user when there is a good email", (done) => {
		const field = getInput(testHolder, emailInputId);
		field.setAttribute("value", "scott.morrison@hotmail.com");
		const event = new UIEvent("change", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		field.dispatchEvent(event);
		setTimeout(function() {
			const message = feedback.getBox(emailFieldId, -1);
			expect(message).withContext("There should be no email suggestion").toBeFalsy();
			done();
		}, 100);
	});

	it("suggests a fix for a typo in a common email domain", function() {
		const field = getInput(testHolder, emailInputId);
		field.setAttribute("value", "scott.morrison@hotmale.com");
		const event = new UIEvent("change", {
			bubbles: false,
			cancelable: false,
			view: window
		});
		field.dispatchEvent(event);
		return domTesting.findByText(testHolder, "scott.morrison@hotmail.com", { exact: false });
	});
});
