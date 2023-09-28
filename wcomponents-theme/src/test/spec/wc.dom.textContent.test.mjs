import textContent from "wc/dom/textContent.mjs";

describe("wc/dom/textContent", () => {
	const CONTENT = "this is the content",
		html = `<p id="p1">${CONTENT}</p>`;
	let testHolder;

	beforeEach(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
	});

	afterEach(() => {
		testHolder.innerHTML = "";
	});

	it("testGet", function() {
		testHolder.innerHTML = html;
		expect(textContent.get(testHolder.ownerDocument.getElementById("p1"))).toBe(CONTENT);
	});

	it("testSet", function() {
		const expected = `<p>${CONTENT.toLowerCase()}</p>`,
			outer = testHolder.ownerDocument.createElement("div"),
			inner = testHolder.ownerDocument.createElement("p");
		outer.appendChild(inner);
		textContent.set(inner, CONTENT);
		expect(outer.innerHTML.toLowerCase()).toBe(expected);
	});
});
