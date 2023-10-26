import getMimeType from "wc/file/getMimeType.mjs";
import domTesting from "@testing-library/dom";
import {addFilesToInput} from "../helpers/specUtils.mjs";

describe("wc/file/getMimeType", () => {
	const testId = "mary1";
	const html = `<input type="file" data-testid="${testId}"/>`;

	beforeEach(() => {
		document.body.innerHTML = html;
	});

	it("Can find the mime time from a file input", function() {
		const type = "text/plain";
		const file = { value: "@HERE@\\resources\\note.txt", type };
		const element = addFilesToInput(domTesting.getByTestId(document.body, testId), [file]);
		const result = getMimeType(element)[0];
		expect(result.mime).toBe(type);
	});
});
