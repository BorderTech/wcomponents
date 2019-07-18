define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var fileUtil,
			wcconfig;
		registerSuite({
			name: "wc/file/util",
			setup: function () {
				return testutils.setupHelper(["wc/file/util", "wc/config"], function (util, config) {
					fileUtil = util;
					wcconfig = config;
				});
			},
			testBlobToFile: function () {
				var expectedMime = "text/plain",
					expectedFile = "test.file",
					content = "This is my blob content",
					blob = new Blob([content], {type: expectedMime}),
					file = fileUtil.blobToFile(blob, {
						name: expectedFile
					});
				assert.strictEqual(file.name, expectedFile);
				assert.strictEqual(file.type, expectedMime);
				assert.strictEqual(file.size, content.length);
				// https://developer.mozilla.org/en-US/docs/Web/API/File#Properties
				assert.isDefined(file.lastModified, "Check this is a file (not a blob).");
				assert.strictEqual(file.lastModified, file.lastModifiedDate.getTime());
				return new Promise(function (win) {
					var fileReader = new FileReader();
					fileReader.onload = function () {
						try {
							assert.strictEqual(fileReader.result, content);
							assert.notStrictEqual(fileReader.result, "content");
						} finally {
							win();
						}
					};
					fileReader.readAsText(file);
				});
			},
			testBlobToFileOptionalConfig: function () {
				var expectedMime = "text/csv",
					content = "This, is, my, blob, content",
					blob = new Blob([content], {type: expectedMime}),
					file = fileUtil.blobToFile(blob);
				assert.isTrue(file.name.indexOf("uid") === 0);
				assert.isTrue(!!(file.name.match(/csv$/)));
			},
			testDataURItoBlob: function () {
				var expectedText = "hello world\u00DC",
					expectedMime = "text/plain",
					dataurl = "data:" + expectedMime + ";base64," + b64EncodeUnicode(expectedText),
					blob;
				// https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding#The_Unicode_Problem
				function b64EncodeUnicode(str) {
					// first we use encodeURIComponent to get percent-encoded UTF-8,
					// then we convert the percent encodings into raw bytes which
					// can be fed into btoa.
					return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
					function toSolidBytes(match, p1) {
						return String.fromCharCode("0x" + p1);
					}));
				}
				blob = fileUtil.dataURItoBlob(dataurl);
				assert.strictEqual(blob.type, expectedMime);
				// each char maybe more than 1 byte in length, so blob size in bytes could be greater than or equal to it's content
				assert.isAtLeast(blob.size, expectedText.length);
				// https://developer.mozilla.org/en-US/docs/Web/API/Blob#Properties
				assert.isUndefined(blob.lastModified, "Check this is a blob (not a file).");
				function readFile(file) {
					return new Promise(function (win) {
						var fileReader = new FileReader();
						fileReader.onload = function (e) {
							win(e.target.result);
						};
						fileReader.readAsText(file);
					});
				}
				return readFile(blob).then(function (result) {
					assert.strictEqual(result, expectedText);
					assert.notStrictEqual(result, "expectedText");
				});
			},
			testFixFileExtensionCustomMime: function () {
				var expectMime = "application/json",
					blob = {hello: "world"},
					expectedFile = "jsonfile",
					expectedExt = "json",
					file;
				try {
					// Register custom mimetypes, this will override default mimetypes
					wcconfig.set({
						"application/json": ["json"]
					}, "wc/file/customMimeToExt");
					// verify default overridden
					assert.isUndefined(fileUtil.getMimeToExtMap()["text/plain"]);
					file = new Blob([JSON.stringify(blob)], {type: expectMime});
					file.name = expectedFile;
					fileUtil.fixFileExtension(file);
					assert.strictEqual(file.name, expectedFile + "." + expectedExt);
				} finally {
					// reset to mimetypes to default
					wcconfig.set(null, "wc/file/customMimeToExt");
				}
			},
			testFixFileExtensionMultipleExt: function () {
				var expectMime = "text/plain",
					file = new Blob(["Text file content"], {type: expectMime}),
					expectedExt = fileUtil.getMimeToExtMap()[expectMime][0],
					actualExt;
				file.name = "testfile.jpg";
				// verify default exists
				assert.isDefined(fileUtil.getMimeToExtMap()["text/plain"]);
				fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[2], expectedExt);
			},
			testFixFileExtensionCorrectName: function () {
				var expectMime = "text/plain",
					file = new Blob(["Text file correct extenstion"], {type: expectMime}),
					expectedExt = fileUtil.getMimeToExtMap()[expectMime][0],
					actualExt;
				file.name = "testfile." + expectedExt;
				fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[1], expectedExt);

				expectedExt = fileUtil.getMimeToExtMap()[expectMime][4];
				file.name = "testfile." + expectedExt;
				fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[1], expectedExt);
			},
			testFixFileExtensionUnknownMime: function () {
				var expectMime = "application/x-csh",
					file = new Blob(["Unknown mime type in mime map"], {type: expectMime}),
					expectedExt = "csh",
					actualExt;
				file.name = "testfile." + expectedExt;
				fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[1], expectedExt);
			},
			testFixFileExtensionUnknownMimeReturnVal: function () {
				var expectMime = "application/x-csh",
					file = new Blob(["Unknown mime type in mime map"], {type: expectMime}),
					expectedExt = "csh",
					actualExt;
				file.name = "testfile." + expectedExt;
				file = fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[1], expectedExt);
			}
		});
	});
