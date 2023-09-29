import fileUtil from "wc/file/util.mjs";
import wcconfig from "wc/config.mjs";

describe("wc/file/util", () => {
	let view;

	beforeAll(() => {
		view = window;
	});

	it("testBlobToFile", function() {
		const expectedMime = "text/plain",
			expectedFile = "test.file",
			content = "This is my blob content",
			blob = new view.Blob([content], { type: expectedMime }),
			file = fileUtil.blobToFile(blob, {
				name: expectedFile
			});
		expect(file.name).toBe(expectedFile);
		expect(file.type).toBe(expectedMime);
		expect(file.size).toBe(content.length);
		// https://developer.mozilla.org/en-US/docs/Web/API/File#Properties
		expect(file.lastModified).withContext("Check this is a file (not a blob).").toBeDefined();
		expect(file.lastModified).toBe(file["lastModifiedDate"].getTime());
		return new Promise(function (win) {
			const fileReader = new view.FileReader();
			fileReader.onload = function () {
				try {
					expect(fileReader.result).toBe(content);
					expect(fileReader.result).not.toBe("content");
				} finally {
					win();
				}
			};
			fileReader.readAsText(file);
		});
	});

	it("testBlobToFileOptionalConfig", function() {
		const expectedMime = "text/csv",
			content = "This, is, my, blob, content",
			blob = new view.Blob([content], {type: expectedMime}),
			file = fileUtil.blobToFile(blob);
		expect(file.name).toMatch(/.+\.csv$/);
	});

	it("testDataURItoBlob", function () {
		const expectedText = "hello world\u00DC",
			expectedMime = "text/plain",
			dataurl = "data:" + expectedMime + ";base64," + b64EncodeUnicode(expectedText);
		// https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding#The_Unicode_Problem
		function b64EncodeUnicode(str) {
			// first we use encodeURIComponent to get percent-encoded UTF-8,
			// then we convert the percent encodings into raw bytes which
			// can be fed into btoa.
			return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
				function toSolidBytes(match, p1) {
					// @ts-ignore
					return String.fromCharCode(`0x${p1}`);
				}));
		}
		const blob = fileUtil.dataURItoBlob(dataurl);
		expect(blob.type).toBe(expectedMime);
		// each char maybe more than 1 byte in length, so blob size in bytes could be greater than or equal to it's content
		expect(blob.size).toBeGreaterThanOrEqual(expectedText.length);
		// https://developer.mozilla.org/en-US/docs/Web/API/view.Blob#Properties
		expect(blob["lastModified"]).withContext("Check this is a blob (not a file).").toBeUndefined();
		function readFile(file) {
			return new Promise(function (win) {
				const fileReader = new view.FileReader();
				fileReader.onload = function (e) {
					win(e.target.result);
				};
				fileReader.readAsText(file);
			});
		}
		return readFile(blob).then(function (result) {
			expect(result).toBe(expectedText);
			expect(result).not.toBe("expectedText");
		});
	});

	it("testFixFileExtensionCustomMime", function() {
		const expectMime = "application/json",
			blob = {hello: "world"},
			expectedFile = "jsonfile",
			expectedExt = "json";
		try {
			// Register custom mimetypes, this will override default mimetypes
			wcconfig.set({
				"application/json": ["json"]
			}, "wc/file/customMimeToExt");
			// verify default overridden
			expect(fileUtil.getMimeToExtMap()["text/plain"]).toBeUndefined();
			const file = new view.Blob([JSON.stringify(blob)], {type: expectMime});
			file.name = expectedFile;
			fileUtil.fixFileExtension(file);
			expect(file.name).toBe(expectedFile + "." + expectedExt);
		} finally {
			// reset to mimetypes to default
			wcconfig.set(null, "wc/file/customMimeToExt");
		}
	});

	it("testFixFileExtensionMultipleExt", function() {
		const expectMime = "text/plain",
			file = new view.Blob(["Text file content"], {type: expectMime}),
			expectedExt = fileUtil.getMimeToExtMap()[expectMime][0];
		file.name = "testfile.jpg";
		// verify default exists
		expect(fileUtil.getMimeToExtMap()["text/plain"]).toBeDefined();
		fileUtil.fixFileExtension(file);
		const actualExt = file.name.split(".");
		expect(actualExt[2]).toBe(expectedExt);
	});

	it("testFixFileExtensionCorrectName", function() {
		const expectMime = "text/plain",
			file = new view.Blob(["Text file correct extenstion"], {type: expectMime});
		let expectedExt = fileUtil.getMimeToExtMap()[expectMime][0];
		file.name = "testfile." + expectedExt;
		fileUtil.fixFileExtension(file);
		let actualExt = file.name.split(".");
		expect(actualExt[1]).toBe(expectedExt);

		expectedExt = fileUtil.getMimeToExtMap()[expectMime][4];
		file.name = "testfile." + expectedExt;
		fileUtil.fixFileExtension(file);
		actualExt = file.name.split(".");
		expect(actualExt[1]).toBe(expectedExt);
	});

	it("testFixFileExtensionUnknownMime", function() {
		const expectMime = "application/x-csh",
			file = new view.Blob(["Unknown mime type in mime map"], {type: expectMime}),
			expectedExt = "csh";
		file.name = "testfile." + expectedExt;
		fileUtil.fixFileExtension(file);
		const actualExt = file.name.split(".");
		expect(actualExt[1]).toBe(expectedExt);
	});

	it("testFixFileExtensionUnknownMimeReturnVal", function() {
		const expectMime = "application/x-csh",
			file = new view.Blob(["Unknown mime type in mime map"], {type: expectMime}),
			expectedExt = "csh";
		file.name = "testfile." + expectedExt;
		fileUtil.fixFileExtension(file);
		const actualExt = file.name.split(".");
		expect(actualExt[1]).toBe(expectedExt);
	});
});
