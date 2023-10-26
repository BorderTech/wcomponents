import listLoader from "wc/ui/listLoader.mjs";

describe("wc/ui/listLoader", () => {
	const responseHtmlUrl = "src/test/resource/icao.html",
		elementId = "txt1";
	let ownerDocument, testHolder;

	function listLoaderTestCallback(datalist) {
		if (datalist) {
			let options;
			if (typeof datalist.querySelector !== "undefined") {
				options = datalist.querySelectorAll("option");
			} else if (typeof datalist.getElementsByTagName !== "undefined") {
				options = datalist.getElementsByTagName("option");
			} else {
				fail("CBF");  // i don't think any browser will end up here...
			}
			expect(options.length).toEqual(262);
		} else {
			fail("Did not load datalist");
		}
	}

	beforeAll(function() {
		ownerDocument = document;
		testHolder = ownerDocument.body;
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	it("testLoadHtml", function() {
		testHolder.innerHTML = `
			<form data-wc-datalisturl="${responseHtmlUrl}">
				<input id="${elementId}">
			</form>`;
		return listLoader.load("icao", ownerDocument.getElementById(elementId)).then(listLoaderTestCallback, ex => fail(ex));
	});
});
