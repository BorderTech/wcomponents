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
						assert.strictEqual(fileReader.result, content);
						assert.notStrictEqual(fileReader.result, "content");
						win();
					};
					fileReader.readAsText(file);
				});
			},
			testDataURItoBlob: function () {
				var expectedText = "hello world",
					expectedMime = "text/plain",
					dataurl = 'data:' + expectedMime + ';base64,' + btoa(expectedText),
					blob;

				blob = fileUtil.dataURItoBlob(dataurl);
				assert.strictEqual(blob.type, expectedMime);
				assert.strictEqual(blob.size, expectedText.length);
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
			}
		});
	});
