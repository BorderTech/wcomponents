import domTesting from "@testing-library/dom";

import popup from "wc/ui/popup.mjs";

const { afterAll, beforeAll, describe, document, expect, MouseEvent, it, spyOn, window } = globalThis;

const URL = "http://www.example.com/";
const popperId = "eg_0c11b0a";

describe("wc/ui/popup", () => {
	let testHolder;

	beforeAll(function() {
		testHolder = document.body.appendChild(document.createElement("div"));
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	it("test onload popup with whitespace only target", function(done) {
		const html = `<wc-popup url="${URL}" target=" "></wc-popup>`;
		testHolder.innerHTML = html;
		// @ts-ignore
		spyOn(window, "open").and.callFake((url, target, specs) => {
			expect(url).toBe(URL);
			expect(target.length).toBeGreaterThan(1);
			expect(specs).toBeFalsy();
			done();
		});
	});

	it("test onload popup with top", function(done) {
		const html = `<wc-popup url="${URL}" top="100"></wc-popup>`;
		testHolder.innerHTML = html;
		// @ts-ignore
		spyOn(window, "open").and.callFake((url, target, specs) => {
			expect(url).toBe(URL);
			expect(target).toBeTruthy();
			checkSpecs(specs, { top: "100px" });
			done();
		});
	});

	it("test onload popup with toolbar and target name", function(done) {
		const html = `<wc-popup url="${URL}" target="A B C" toolbar="true"></wc-popup>`;
		testHolder.innerHTML = html;
		// @ts-ignore
		spyOn(window, "open").and.callFake((url, target, specs) => {
			expect(url).toBe(URL);
			expect(target).withContext("target should be stripped of whitespace").toEqual("ABC");
			checkSpecs(specs, { toolbar: "yes" });
			done();
		});
	});

	it("test onload popup with features, ignores resizable and scrollbars", function(done) {
		const html = `<wc-popup url="${URL}" height="300" width="280" resizable="no" scrollbars="no" location="yes"></wc-popup>`;
		testHolder.innerHTML = html;
		// @ts-ignore
		spyOn(window, "open").and.callFake((url, target, specs) => {
			expect(url).toBe(URL);
			expect(target).toBeTruthy();
			checkSpecs(specs, { width: "280px", height: "300px", location: "yes" });
			done();
		});
	});

	it("test popup button click", function(done) {
		const specString = "height=300px,width=280px";
		const html = `<button data-wc-specs="${specString}" data-testid="${popperId}" type="button" data-wc-url="${URL}" aria-haspopup="true">Poppie Uppie</button>`;
		testHolder.innerHTML = html;
		const button = domTesting.getByTestId(testHolder, popperId);
		const event = new MouseEvent("click", {
			bubbles: true,
			cancelable: true,
			view: window
		});
		// @ts-ignore
		spyOn(window, "open").and.callFake((url, target, specs) => {
			expect(url).toEqual(URL);
			expect(specs).toEqual(specString);
			done();
		});

		expect(popup.isOneOfMe(button)).withContext("The test button is not correct").toBeTruthy();
		button.dispatchEvent(event);
	});

	function checkSpecs(specs, overrides = {}) {
		const defaults = {
			resizable: "yes",
			scrollbars: "yes",
			menubar: "no",
			toolbar: "no",
			location: "no",
			status: "no"
		};
		const merged = { ...defaults, ...overrides };
		const expected = Object.keys(merged).map(key => `${key}=${merged[key]}`);
		const specArray = specs.split(",");

		expect(specArray.length).withContext(specs).toEqual(expected.length);
		for (const feature of expected) {
			expect(specArray).toContain(feature);
		}
	}
});
