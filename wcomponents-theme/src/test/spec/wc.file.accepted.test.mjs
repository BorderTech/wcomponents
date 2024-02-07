import accepted from "wc/file/accepted.mjs";
import {addFilesToInput, getInput} from "../helpers/specUtils.mjs";

describe("wc/file/accepted", function() {
	/** @type {HTMLElement} */
	let testHolder;
	const html = `
		<input type="file" data-testid="file1"/>
		<input type="file" data-testid="file2" accept="text/html"/>
		<input type="file" data-testid="file3" accept="image/*"/>
		<input type="file" data-testid="file4" accept="text/html, text/plain"/>
		<input type="file" data-testid="file5"/>
		<input type="file" data-testid="file6" accept="image/png, image/gif"/>
		<input type="file" data-testid="file7" accept="image/*"/>
		<input type="file" data-testid="file8" accept="text/plain"/>
		<input type="file" data-testid="file9" accept="image/*"/>`;

	beforeEach(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = html;
	});

	afterEach(() => {
		testHolder.parentElement.removeChild(testHolder);
	});

	afterAll(function() {
		document.body.innerHTML = "";
	});

	it("testAcceptedWithNoValueNoAccept", function() {
		const element = getInput(testHolder, "file1");
		expect(accepted(element)).toBeTrue();
	});

	/* NOTE for next three if no value in the file input then they must be "accepted" as the test array is empty */
	it("testAcceptedWithNoValueSingleMimeType", function() {
		const element = getInput(testHolder, "file2");
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithNoValueWildCardMimeType", function() {
		const element = getInput(testHolder, "file3");
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithNoValueMultipleMimeTypes", function() {
		const element = getInput(testHolder, "file4");
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithNoAccept", function() {
		const element = getInput(testHolder, "file1");
		addFilesToInput(element, [{ value: "foo.gif", type: "image/gif" }]);
		expect(accepted(element)).toBeTrue();
	});

	it("testAccepted", function() {
		const element = getInput(testHolder, "file5");
		addFilesToInput(element, [{ value: "foo.gif", type: "image/gif" }]);
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithMultiple", function() {
		const element = getInput(testHolder, "file6");
		addFilesToInput(element, [{ value: "foo.gif", type: "image/gif" }]);
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithWildcard", function() {
		const element = getInput(testHolder, "file7");
		addFilesToInput(element, [{ value: "foo.gif", type: "image/gif" }]);
		expect(accepted(element)).toBeTrue();
	});

	it("testAcceptedWithMismatch", function() {
		const element = getInput(testHolder, "file8");
		addFilesToInput(element, [{ value: "foo.gif", type: "image/gif" }]);
		expect(accepted(element)).toBeFalse();
	});

	it("testAcceptedWithMismatchAndWildcard", function() {
		const element = getInput(testHolder, "file9");
		addFilesToInput(element, [{ value: "foo.txt", type: "text/plain" }]);
		expect(accepted(element)).toBeFalse();
	});
});
