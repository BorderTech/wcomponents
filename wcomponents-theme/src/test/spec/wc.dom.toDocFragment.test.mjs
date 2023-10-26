import controller from "wc/dom/toDocFragment.mjs";

describe("wc/dom/toDocFragment", () => {
	const SIMPLE_HTML = "<div>this is some html<span>hello</span></div>";

	it("testReturnsDocFragment", function() {
		const df = controller(SIMPLE_HTML);
		expect(df.nodeType).toBe(Node.DOCUMENT_FRAGMENT_NODE);
	});

	it("testNoMunge", function() {
		const df = controller(SIMPLE_HTML),
			container = document.createElement("div");
		container.appendChild(df);
		expect(container.innerHTML).toBe(SIMPLE_HTML);
	});

	it("testToDocFragNoElements", function() {
		const content = "text node",
			df = controller(content);
		expect(df.firstChild.nodeValue).toBe(content);
	});
});
