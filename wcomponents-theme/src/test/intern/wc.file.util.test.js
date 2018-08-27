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
					blob = new Blob(["This is my blob content"], {type: expectedMime}),
					file = fileUtil.blobToFile(blob, {
						name: expectedFile
					});
				assert.strictEqual(file.name, expectedFile);
				assert.strictEqual(file.type, expectedMime);
			},
			testDataURItoBlob: function () {
				var expectedText = "hello world",
					expectedMime = "text/plain",
					dataurl = 'data:'+ expectedMime +';base64,' + btoa(expectedText),
					fileReader = new FileReader(),
					blob;

				blob = fileUtil.dataURItoBlob(dataurl);
				new Promise(function (win) {
					fileReader.onload = function () {
						assert.strictEqual(fileReader.result, expectedText);
						win();
					};
					fileReader.readAsText(blob);
				});
				assert.strictEqual(blob.type, expectedMime);
			},
			testFixFileExtensionMultipleExt: function () {
				var expectMime = "text/plain",
					file = new Blob(["Text file content"], {type: expectMime}),
					expectedExt = fileUtil.mimeToExt[expectMime][0],
					actualExt;
				file.lastModifiedDate = new Date();
				file.name = "testfile.jpg";
				fileUtil.fixFileExtension(file);
				assert.strictEqual(file.type, expectMime);
				actualExt = file.name.split(".");
				assert.strictEqual(actualExt[2], expectedExt);
			},
			testFixFileExtensionCustomMime: function () {
				var expectMime = "application/json",
					blob = {hello: "world"},
					expectedFile = "jsonfile",
					expectedExt = "json",
					file;
				// Register custom mimetypes, this will override default mimetypes
				wcconfig.set({
					"application/json": ["json"]
				}, "wc/file/myMimeTypes");
				file = new Blob([JSON.stringify(blob)], {type: expectMime});
				file.lastModifiedDate = new Date();
				file.name = expectedFile;
				fileUtil.fixFileExtension(file);
				assert.strictEqual(file.type, expectMime);
				assert.strictEqual(file.name, expectedFile + "." + expectedExt);
			}
		});
	});
