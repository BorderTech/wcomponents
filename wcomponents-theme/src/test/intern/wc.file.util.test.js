define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var fileUtil, dataurl;
		registerSuite({
			name: "wc/file/util",
			setup: function() {
				return testutils.setupHelper(["wc/file/util"], function(obj) {
					fileUtil = obj;
					dataurl = 'data:text/plain;base64,aGVsbG8gd29ybGQ=';
				});
			},
			testBlobToFile: function() {
				var blob = fileUtil.dataURItoBlob(dataurl);
				blob = fileUtil.blobToFile(blob, {
					name: "test.file"
				});
				assert.strictEqual(blob.name, "test.file");
			},
			testDataURItoBlob: function() {
				var blob = fileUtil.dataURItoBlob(dataurl);
				console.info(blob);
				assert.strictEqual(blob.type, "text/plain");
			}
		});
	});
