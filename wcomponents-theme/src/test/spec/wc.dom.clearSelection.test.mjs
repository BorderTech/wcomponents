import clearSelection from "wc/dom/clearSelection.mjs";
describe("wc/dom/clearSelection", function() {

	const TEXT = "This is some known text",
		SELECT_ID = "clearSelectionTestContainer1",
		testHolder = document.body;

	function getSelectedText() {
		return testHolder.ownerDocument.defaultView.getSelection().toString();
	}

	beforeEach(() => {
		testHolder.innerHTML = `<p id="${SELECT_ID}">${TEXT}</p>`;
		const el = testHolder.ownerDocument.getElementById(SELECT_ID);
		if (el) {
			if (testHolder.ownerDocument.createRange) {
				testHolder.ownerDocument.defaultView.getSelection().removeAllRanges();
				let range = testHolder.ownerDocument.createRange();
				range.selectNode(el);
				testHolder.ownerDocument.defaultView.getSelection().addRange(range);
			}
		} else {
			fail(`Cannot find element with id ${SELECT_ID}`);
		}
	});

	afterAll(function() {
		if (testHolder) {
			testHolder.innerHTML = "";
		}
	});

	it("doClearSelectionTest", function() {
		expect(getSelectedText()).toBe(TEXT);
		clearSelection();
		expect(getSelectedText()).toBe("");
	});
});
