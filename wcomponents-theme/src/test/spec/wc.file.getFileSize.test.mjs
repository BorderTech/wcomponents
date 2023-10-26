import getFileSize from "wc/file/getFileSize.mjs";
import {addFilesToInput, getInput} from "../helpers/specUtils.mjs";
describe("wc/file/getFileSize", () => {
	let testHolder;

	beforeEach(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = `<input type="file" data-testid="file1"/>`;
	});

	it("can get the file size", function() {
		const element = getInput(testHolder, "file1");
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);
		expect(getFileSize(element)[0]).toBe(16);
	});
});

