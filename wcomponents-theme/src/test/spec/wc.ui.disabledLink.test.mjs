import "wc/ui/disabledLink.mjs";
import domTesting from "@testing-library/dom";

describe("wc/ui/disabledLink", () => {
	const linkId = "ui-disabledlink-1",
		testContent = `<a data-testid="${linkId}" href="#" aria-disabled="true">Click me</a>`;
	let testHolder;

	beforeAll(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = testContent;
	});

	it("prevents default on disabled links", function() {
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
