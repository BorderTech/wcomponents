import rtf from "wc/ui/rtf.mjs";
import initialise from "wc/dom/initialise.mjs";
import tinymce from "../resource/mock-modules/tinymce.js";

describe("wc/ui/rtf", () => {
	let origHead;

	beforeAll(() => {
		origHead = window.document.head.innerHTML;

		let importMap = window.document.createElement("script");
		importMap.setAttribute("type", "importmap");
		importMap.innerHTML =
			`{
				"imports": {
					"tinymce/": "../../../../test/resource/mock-modules/"
				}
			}`;
		window.document.head.append(importMap);
	});

	afterAll(() => {
		window.document.head.innerHTML = origHead;
		delete document.defaultView.tinymce;
	});

	it("sets a callback for every nonempty invocation of register", (done) => {
		spyOn(initialise, "addCallback");
		rtf.register(["exID", "anotherEx"]);
		rtf.register(["exID"]);
		rtf.register([]);

		expect(initialise.addCallback).toHaveBeenCalledTimes(2);
		done();
	});

	it("passes the correct selectors to TinyMCE on editor initialisation", (done) => {
		const selectors = ["textarea#exID_input", "textarea#anotherEx_input"];
		const expectedCalls = 2;
		let callCount = 0;

		const initSpy = spyOn(tinymce, "init").and.callFake(() => {
			let givenSelector = initSpy.calls.mostRecent().args[0].selector;
			expect(selectors).toContain(givenSelector);

			if (selectors.includes(givenSelector)) {
				if (++callCount === expectedCalls) {
					done();
				}

				const idx = selectors.indexOf(givenSelector);
				if (idx > -1) {
					selectors.splice(idx, 1);
				}
			}
		});

		rtf.register(["exID"]);
		rtf.register(["anotherEx"]);
	});

	it("passes the correct default plugins to TinyMCE on editor initialisation", (done) => {
		const initSpy = spyOn(tinymce, "init").and.callFake(() => {
			const defaultPlugins = ["autolink", "link", "lists", "advlist", "preview", "help"];

			for (const plugin of defaultPlugins) {
				expect(initSpy.calls.mostRecent().args[0].plugins).toContain(plugin);
			}
			done();
		});

		rtf.register(["exID"]);
	});

	it("passes the correct default tools to TinyMCE on editor initialisation", (done) => {
		const initSpy = spyOn(tinymce, "init").and.callFake(() => {
			const defaultTools = ["undo", "redo", "formatselect", "bold", "italic", "alignleft", "aligncenter",
				"alignright", "alignjustify", "bullist", "numlist", "outdent", "indent", "removeformat", "help"];

			for (const tool of defaultTools) {
				expect(initSpy.calls.mostRecent().args[0].toolbar).toContain(tool);
			}
			done();
		});

		rtf.register(["exID"]);
	});
});
