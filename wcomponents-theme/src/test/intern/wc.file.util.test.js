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
				var blob = new Blob(["This is my blob content"], {type: "text/plain"});
				var file = fileUtil.blobToFile(blob, {
					name: "test.file"
				});
				assert.strictEqual(file.name, "test.file");
				assert.strictEqual(file.type, "text/plain");
			},
			testDataURItoBlob: function () {
				var dataurl = 'data:text/plain;base64,aGVsbG8gd29ybGQ=',
					blob;

				blob = fileUtil.dataURItoBlob(dataurl);
				assert.strictEqual(blob.type, "text/plain");
			},
			testFixFileExtension: function () {
				var mimeType = "text/plain";
				var file = new Blob(["Text file content"], {type : mimeType});
				file.lastModifiedDate = new Date();
				file.name = "testfile.jpg";
				fileUtil.fixFileExtension(file);
				assert.strictEqual(file.type, "text/plain");
				assert.strictEqual(file.name.split(".")[2], fileUtil.mimeToExt[mimeType][0]);
				
				// Register custom mimetypes, this will override default mimetypes
				wcconfig.set({
					"application/json": ["json"]
				}, "wc/file/myMimeTypes");
				var blob = {hello: "world"};
				file = new Blob([JSON.stringify(blob)], {type : 'application/json'});
				file.lastModifiedDate = new Date();
				file.name = "jsonfile";
				fileUtil.fixFileExtension(file);
				assert.strictEqual(file.type, "application/json");
				assert.strictEqual(file.name, "jsonfile.json");

			}
		});
	});
