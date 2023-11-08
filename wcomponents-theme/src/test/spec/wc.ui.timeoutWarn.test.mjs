import TimeoutWarn from "wc/ui/timeoutWarn.mjs";
import domTesting from "@testing-library/dom";
import getDifference from "wc/date/getDifference.mjs";

describe("wc/ui/timeoutWarn", () => {
	let testHolder;
	const testId = "some-session-timer-thing";

	beforeAll(() => {
		console.log(TimeoutWarn.tagName);  // I want to import TimeoutWarn for the JSDoc comments but eslint thinks it's unused.
		jasmine.clock().install();
		testHolder = document.body;
	});

	afterAll(() => {
		jasmine.clock().uninstall();
	});

	beforeEach(function() {
		testHolder.innerHTML = `<wc-session data-testid="${testId}"></wc-session>`;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("reports the warning property as zero when the element is missing all attributes", () => {
		const element = getSessionElement(testHolder, testId);
		expect(element.warn).toBe(0);
	});

	it("reports zero when the warn attribute is invalid", () => {
		const element = getSessionElement(testHolder, testId);
		element.setAttribute("warn", "red sky morning");
		element.setAttribute("timeout", "180");  // 3 minutes
		expect(element.warn).toBe(0);
	});

	it("reports the warning property correctly when the attribute is set", () => {
		const element = getSessionElement(testHolder, testId);
		element.setAttribute("warn", "60");
		expect(element.warn).toBe(60);
	});

	it("expires property is falsy when the element is not initialised with the necessary attributes", () => {
		const element = getSessionElement(testHolder, testId);
		expect(element.expires).toBeFalsy();
	});

	it("expires property is correct when the element is initialised with the necessary attributes", () => {
		const element = getSessionElement(testHolder, testId);
		element.setAttribute("warn", "60");
		element.setAttribute("timeout", "180");  // 3 minutes
		let diff = getDifference(new Date(element.expires), new Date(), true);
		const twoMinutes = 60000 * 2;
		const threeMinutes = 60000 * 3;
		expect(diff).toBeGreaterThanOrEqual(twoMinutes);
		expect(diff).toBeLessThanOrEqual(threeMinutes);
	});

	it("expires property is reset when the timeout attribute is changed", () => {
		const element = getSessionElement(testHolder, testId);
		element.setAttribute("warn", "60");

		element.setAttribute("timeout", "180");  // 3 minutes
		let diff = getDifference(new Date(element.expires), new Date(), true);
		const twoMinutes = 60000 * 2;
		const threeMinutes = 60000 * 3;
		expect(diff).toBeGreaterThanOrEqual(twoMinutes);
		expect(diff).toBeLessThanOrEqual(threeMinutes);

		element.setAttribute("timeout", "300");  // 5 minutes
		diff = getDifference(new Date(element.expires), new Date(), true);
		const fiveMinutes = 60000 * 4;
		const sixMinutes = 60000 * 5;
		expect(diff).toBeGreaterThanOrEqual(fiveMinutes);
		expect(diff).toBeLessThanOrEqual(sixMinutes);
	});

	it("shows the warning and then the error", () => {
		const element = getSessionElement(testHolder, testId);
		const timeout = 60;  // seconds
		const warn = 20;  // seconds
		const warnAfter = timeout - warn;
		element.setAttribute("warn", warn.toString());
		element.setAttribute("timeout", timeout.toString());
		jasmine.clock().tick(warnAfter * 1000);  // milliseconds
		return domTesting.findByTitle(testHolder, "Warning").then(messageBox => {
			return domTesting.findByText(messageBox, /Your session will be automatically ended within the next/);
		});
	});

	/**
	 * Helper to aid with type checking.
	 * @param {HTMLElement} container
	 * @param {string} id
	 * @return {TimeoutWarn}
	 */
	function getSessionElement(container, id) {
		return /** @type {TimeoutWarn} */(domTesting.getByTestId(container, id));
	}
});
