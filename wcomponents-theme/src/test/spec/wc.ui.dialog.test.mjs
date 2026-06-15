import domTesting from "@testing-library/dom";

import shed from "wc/dom/shed.mjs";
import dialogFrame from "wc/ui/dialogFrame.mjs";
import "wc/ui/dialog.mjs";

const { afterAll, beforeAll, describe, document, expect, it, KeyboardEvent, MouseEvent, window } = globalThis;

const dialogId = "eg-_3i1";

describe("wc/ui/dialog", () => {
	let testHolder;

	beforeAll(function() {
		testHolder = document.body;
		testHolder.innerHTML = "";
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	it("test open dialog with button click", function() {
		const html = `
			<form>
				<wc-dialog data-id="eg-_3i" width="600" title="View list with time" triggerid="${dialogId}">
					<button data-testid="${dialogId}" id="${dialogId}" name="${dialogId}" value="x" type="submit" aria-haspopup="true">Open immediate dialog</button>
				</wc-dialog>
			</form>`;
		testHolder.innerHTML = html;
		const button = domTesting.getByTestId(testHolder, dialogId);
		const clickEvent = new MouseEvent("click", {
			bubbles: true,
			cancelable: true,
			view: window
		});
		button.dispatchEvent(clickEvent);
		return domTesting.findByText(testHolder, "View list with time").then(dialogElement => {
			const dialogWrapper = dialogFrame.getDialog();

			expect(shed.isHidden(dialogWrapper)).withContext("Dialog should now be open").toBeFalsy();
			const keyEvent = new KeyboardEvent("keydown", {
				bubbles: true,
				cancelable: true,
				code: "Escape",
				view: window
			});
			dialogElement.dispatchEvent(keyEvent);

			expect(shed.isHidden(dialogWrapper)).withContext("Escape key in dialog should close it").toBeTruthy();
		});
	});
});
