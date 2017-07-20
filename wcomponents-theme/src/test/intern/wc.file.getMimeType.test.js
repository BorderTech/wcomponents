define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var TEST_MODULE = "wc/file/getMimeType",
		controller, loader;

	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			return testutils.setupHelper([TEST_MODULE, "wc/loader/resource"]).then(function(arr) {
				controller = arr[0];
				loader = arr[1];
			});
		},
		testGetMimeType: function() {
			var element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16),
				result = controller(element)[0];
			assert.strictEqual(result.mime, "text/plain");
		},
		testGetExtension: function() {
			var element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16),
				result = controller(element)[0];
			assert.strictEqual(result.ext, "txt");
		},
		testGuess: function() {
			var extension = "txt",
				expected = "text/plain",
				actual = controller._guess(extension);
			assert.strictEqual(actual, expected);
		},
		testGuessEmptyExtension: function() {
			var extension = "",
				actual = controller._guess(extension);
			assert.isNull(actual);
		},
		testGuessBadExtensionThrowsTypeError: function() {
			try {
				controller._guess({}); // any truthy not String/string will do.
				assert.isTrue(false);
			} catch (e) {
				assert.strictEqual(TypeError, e.constructor);
			}
		},
		testGuessSillyExtension: function() {
			// This is an exception test of sorts. If the extension is a non-empty string but not a known file
			// extension we expect undefined.
			var extension = "********",
				actual = controller._guess(extension);
			assert.isUndefined(actual);
		},
		testPretendToBeIE_MIME: function() {
			var mockFileInfo = {value: "@HERE@\\resources\\note.txt"},
				expected = "text/plain",
				result = controller(mockFileInfo),
				actual = result[0].mime;
			assert.strictEqual(actual, expected);
		},
		testPretendToBeIE_ext: function() {
			var mockFileInfo = {value: "@HERE@\\resources\\note.txt"},
				expected = "txt",
				result = controller(mockFileInfo),
				actual = result[0].ext;
			assert.strictEqual(actual, expected);
		},
		testPretendToBeIE_noExtension: function() {
			var mockFileInfo = {value: "@HERE@\\resources\\note"},
				result = controller(mockFileInfo),
				actual = result[0];
			assert.isNull(actual.mime);
			assert.strictEqual(actual.ext, "");
		},
		testGuessEveryMimeType: function() {
			// just because we can, but it may take a while.
			var mimemap = loader.load("mimemap.json", true),
				jsonmap;
			if (mimemap) {
				jsonmap = JSON.parse(mimemap);
				Object.keys(jsonmap).forEach(function(key) {
					var extensions = jsonmap[key];
					extensions.forEach(function(next) {
						var actual= controller._guess(next.toLowerCase());
						assert.strictEqual(actual, key);
					});
				});
			}
		}
	});
});
