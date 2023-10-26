import removeElement from "wc/dom/removeElement.mjs";

describe("wc/dom/removeElement", () => {
	const testHolder = document.body,
		removeId = "removeelementtest-target",
		testContent = `<span id="${removeId}">content</span>`;

	beforeEach(() => testHolder.innerHTML = testContent);
	afterEach(() => testHolder.innerHTML = "");

	it("testRemoveElement_noId", function() {
		expect(document.getElementById(removeId)).toBeTruthy();
		// @ts-ignore
		removeElement();
		expect(document.getElementById(removeId)).toBeTruthy();
	});

	it("testRemoveElement", function() {
		expect(document.getElementById(removeId)).toBeTruthy();
		removeElement(removeId);
		expect(document.getElementById(removeId)).toBeFalsy();
	});
});
