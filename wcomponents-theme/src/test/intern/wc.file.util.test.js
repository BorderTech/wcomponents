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
				assert.strictEqual(file.lastModified, file.lastModifiedDate.getTime());
			},
			testDataURItoBlob: function () {
				var expectedText = "hello world",
					expectedMime = "text/plain",
					dataurl = 'data:' + expectedMime + ';base64,' + btoa(expectedText),
					blob;

				blob = fileUtil.dataURItoBlob(dataurl);
				assert.strictEqual(blob.type, expectedMime);
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
				try {
					var expectMime = "application/json",
						blob = {hello: "world"},
						expectedFile = "jsonfile",
						expectedExt = "json",
						file;
					// Register custom mimetypes, this will override default mimetypes
					wcconfig.set({
						"application/json": ["json"]
					}, "wc/file/customMimeToExt");
					// verify to make sure it is overridden
					assert.isUndefined(fileUtil.getMimeToExtMap()["image/jpeg"]);
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
				// verify to make sure default exists
				assert.isDefined(fileUtil.getMimeToExtMap()["image/jpeg"]);
				fileUtil.fixFileExtension(file);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[2], expectedExt);
			}
		});
	});
