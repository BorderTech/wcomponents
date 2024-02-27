import validate from "wc/file/validate.mjs";
import {addFilesToInput, getInput} from "../helpers/specUtils.mjs";
describe("wc/file/validate", () => {
	let testHolder;

	function runWithEmptyCallbacks(extraArgs, testId, valid) {
		let emptyCallback, emptyErrback;

		const element = getInput(testHolder, testId);
		addFilesToInput(element, [{ value: "SixteenChars.gif", type: "image/gif" }]);

		emptyCallback = jasmine.createSpy("emptyCallback");
		emptyErrback = jasmine.createSpy("emptyErrback");
		validate.check({
			selector: element,
			callback: emptyCallback,
			errback: emptyErrback,
			...extraArgs
		});

		if (valid) {
			expect(emptyCallback).toHaveBeenCalledTimes(1);
			expect(emptyErrback).not.toHaveBeenCalled();
		} else {
			expect(emptyCallback).not.toHaveBeenCalled();
			expect(emptyErrback).toHaveBeenCalledTimes(1);
		}
	}

	function runWithFilesArg(files, testId, valid) {
		let emptyCallback, emptyErrback;

		const element = getInput(testHolder, testId);

		emptyCallback = jasmine.createSpy("emptyCallback");
		emptyErrback = jasmine.createSpy("emptyErrback");
		validate.check({
			selector: element,
			callback: emptyCallback,
			errback: emptyErrback,
			files: files
		});

		if (valid) {
			expect(emptyCallback).toHaveBeenCalledTimes(1);
			expect(emptyErrback).not.toHaveBeenCalled();
		} else {
			expect(emptyCallback).not.toHaveBeenCalled();
			expect(emptyErrback).toHaveBeenCalledTimes(1);
		}
	}

	beforeEach(() => {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = `
			<input type="file" accept="image/png, image/gif" data-testid="file1"/>
			<input type="file" data-testid="file2"/>
			<input type="file" accept="image/jpeg" data-testid="file3"/>
			<input type="file" data-wc-maxfilesize="15" data-testid="file4"/>`;
	});

	it("accepts files given matching accepted attribute", function() {
		runWithEmptyCallbacks({}, "file1", true);
	});

	it("accepts files when element has no accepted attribute", function() {
		runWithEmptyCallbacks({}, "file2", true);
	});

	it("calls the given error callback when the file is not accepted", function() {
		runWithEmptyCallbacks({}, "file3", false);
	});

	it("calls the given error callback when there are file size issues", function() {
		runWithEmptyCallbacks({}, "file4", false);
	});

	it("calls the given error callback when there are size issues only and stopAtFirst is set", function() {
		runWithEmptyCallbacks({stopAtFirst: true}, "file4", false);
	});

	it("accepts files when valid files are given as an argument", function() {
		runWithFilesArg([new File(["SixteenChars.gif"], "SixteenChars", {type: "image/gif"})],
			"file1", true);
	});

	it("rejects files when files are too large and given as an argument", function() {
		runWithFilesArg([new File(["SixteenChars.gif"], "SixteenChars", {type: "image/gif"})],
			"file4", false);
	});

	it("rejects files when files are not accepted and given as an argument", function() {
		runWithFilesArg([new File(["SixteenChars.gif"], "SixteenChars", {type: "image/gif"})],
			"file3", false);
	});
});

