define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var key;

		function getMockKeyEvent(code, alt, ctrl, shift, altGr, meta) {
			return {
				"altGraphKey": !!altGr,
				"metaKey": !!meta,
				"altKey": !!alt,
				"shiftKey": !!shift,
				"ctrlKey": !!ctrl,
				"which": code,
				"pageY": 0,
				"pageX": 0,
				"layerY": 0,
				"layerX": 0,
				"charCode": code,
				"keyCode": code,
				"detail": 0,
				"cancelBubble": false,
				"returnValue": true,
				"defaultPrevented": false,
				"timeStamp": Date.now(),
				"cancelable": true,
				"bubbles": true,
				"eventPhase": 0,
				"type": "keypress"
			};
		}

		registerSuite({
			name: "key",
			setup: function() {
				return testutils.setupHelper(["wc/key"], function(obj) {
					key = obj;
				});
			},
			testGetLiteral: function() {
				var code = 88,
					expected = "DOM_VK_X",
					actual = key.getLiteral(code);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressed: function() {
				var evt = getMockKeyEvent(88),
					expected = "DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithAlt: function() {
				var evt = getMockKeyEvent(88, true),
					expected = "DOM_VK_ALT+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithCtrl: function() {
				var evt = getMockKeyEvent(88, false, true),
					expected = "DOM_VK_CONTROL+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithShift: function() {
				var evt = getMockKeyEvent(88, false, false, true),
					expected = "DOM_VK_SHIFT+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithAltAndShift: function() {
				var evt = getMockKeyEvent(88, true, false, true),
					expected = "DOM_VK_ALT+DOM_VK_SHIFT+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithCtrlAndShift: function() {
				var evt = getMockKeyEvent(88, false, true, true),
					expected = "DOM_VK_CONTROL+DOM_VK_SHIFT+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			},
			testGetKeysPressedWithAltAndCtrlAndShift: function() {
				var evt = getMockKeyEvent(88, true, true, true),
					expected = "DOM_VK_ALT+DOM_VK_CONTROL+DOM_VK_SHIFT+DOM_VK_X",
					actual = key.getKeysPressed(evt);
				assert.strictEqual(expected, actual);
			}
		});
	});
