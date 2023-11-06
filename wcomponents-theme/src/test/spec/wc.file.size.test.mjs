import fileSize from "wc/file/size.mjs";
import {addFilesToInput, getInput} from "../helpers/specUtils.mjs";
describe("wc/file/size", () => {
	let testHolder;

	beforeEach(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = `
			<input type="file" data-testid="file1"/>
			<input type="file" data-wc-maxfilesize="18" data-testid="file2"/>
			<input type="file" data-testid="file3"/>
			<input type="file" data-wc-maxfilesize="16" data-testid="file4"/>
			<input type="file" data-wc-maxfilesize="15" data-testid="file5"/>
			<input type="file" data-wc-maxfilesize="2000" data-testid="file6"/>`;
	});

	it("can get the file size", function() {
		const element = getInput(testHolder, "file1");
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);
		expect(fileSize.get(element)[0]).toBe(16);
	});

	it("getMax returns falsy where there is no max file size constraint", function() {
		const element = getInput(testHolder, "file1");
		expect(fileSize.getMax(element)).toBeFalsy();
	});

	it("can get the max file size constraint", function() {
		const element = getInput(testHolder, "file2");
		expect(fileSize.getMax(element)).toBe(18);
	});

	it("check returns falsy if no size constraint", function() {
		const element = getInput(testHolder, "file3");
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);
		expect(fileSize.check({ element })).toBeFalsy();
	});

	it("check returns falsy if size constraint not exceeded", function() {
		const element = getInput(testHolder, "file4");
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);
		expect(fileSize.check({ element })).toBeFalsy();
	});

	it("check returns message if size constraint exceeded, bytes", function() {
		const element = getInput(testHolder, "file5");
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);
		expect(fileSize.check({ element })).toMatch(/16 bytes.+15 bytes/);
	});

	it("check returns message if size constraint exceeded, KB", function() {
		const element = getInput(testHolder, "file6");
		addFilesToInput(element, [{ value: "A".repeat(2100), type: "image/gif" }]);
		expect(fileSize.check({ element })).toMatch(/2.1 kilobytes.+2 kilobytes/);
	});
});

