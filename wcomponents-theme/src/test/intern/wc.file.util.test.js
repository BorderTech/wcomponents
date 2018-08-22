define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var fileUtil;
		registerSuite({
			name: "wc/file/util",
			setup: function () {
				return testutils.setupHelper(["wc/file/util"], function (obj) {
					fileUtil = obj;
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
			}
		});
	});
