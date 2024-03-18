import "wc/ui/printButton.mjs";
import domTesting from "@testing-library/dom";

describe("wc/ui/printButton", () => {
	const linkId = "ui-printbutton-1",
		testContent = `<button class="wc-printbutton" data-testid="${linkId}" type="button"  >Click me<button>`;
	let testHolder;

	beforeAll(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = testContent;
	});

	it("test", function() {
		const target = domTesting.getByTestId(testHolder, linkId);
		const event = new MouseEvent("click", {
			bubbles: true,
			cancelable: true,
			view: window
		});

		const clickHandler = jasmine.createSpy("clickHandler");
		testHolder.addEventListener("click", clickHandler);
		target.dispatchEvent(event);

		expect(clickHandler).toHaveBeenCalledWith(jasmine.objectContaining({ defaultPrevented: true }));
	});
});
