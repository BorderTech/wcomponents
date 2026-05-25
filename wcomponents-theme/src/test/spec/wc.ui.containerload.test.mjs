import Trigger from "wc/ajax/Trigger.mjs";

describe("wc/dom/containerload", () => {

	let testHolder;

	beforeAll(function() {
		testHolder = document.body.appendChild(document.createElement("div"));
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	it("sends a separate ajax request for each eager component", function(done) {
		let callCount = 0;
		const expectedCalls = 3;
		const ids = ["someFakeID", "anotherFakeID", "aThirdFakeID"];

		let html = '';

		for (let i = 0; i < ids.length; i++) {
			html += `
				<div id="${ids[i]}">
					<wc-ajax-eager container-id="${ids[i]}"></wc-ajax-eager>
				</div>`;
		}

		spyOn(Trigger.prototype, "fire").and.callFake(() => {
			callCount++;
			if (callCount === expectedCalls) {
				done();
			}
			return Promise.resolve();
		});

		testHolder.innerHTML = html;
	});
});
