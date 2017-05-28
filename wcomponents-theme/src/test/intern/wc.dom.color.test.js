define(["intern!object", "intern/chai!assert", "wc/dom/color"],
	function (registerSuite, assert, controller) {
		"use strict";

		registerSuite({
			name: "wc/dom/color",
			testHex2rgb3: function() {
				var start = "#000",
					expected = {r: 0, g: 0, b: 0},
					result = controller.hex2rgb(start);
				assert.strictEqual(expected.r, result.r);
				assert.strictEqual(expected.g, result.g);
				assert.strictEqual(expected.b, result.b);
			},
			testHex2rgb6: function() {
				var start = "#00ff00",
					expected = {r: 0, g: 255, b: 0},
					result = controller.hex2rgb(start);
				assert.strictEqual(expected.r, result.r);
				assert.strictEqual(expected.g, result.g);
				assert.strictEqual(expected.b, result.b);
			},
			testHex2rgb3NoHash: function() {
				var start = "000",
					expected = {r: 0, g: 0, b: 0},
					result = controller.hex2rgb(start);
				assert.strictEqual(expected.r, result.r);
				assert.strictEqual(expected.g, result.g);
				assert.strictEqual(expected.b, result.b);
			},
			testHex2rgb6NoHash: function() {
				var start = "00ff00",
					expected = {r: 0, g: 255, b: 0},
					result = controller.hex2rgb(start);
				assert.strictEqual(expected.r, result.r);
				assert.strictEqual(expected.g, result.g);
				assert.strictEqual(expected.b, result.b);
			},
			// exception tests for hex2rgb
			testHex2rgbNumberArg: function() {
				try {
					controller.hex2rgb(0x0); // this is a hex
					assert.isFalse(true, "expected an exception.");
				} catch (e) {
					assert.isTrue(e instanceof TypeError, e.message);
				}
			},
			testHex2rgbNullArg: function() {
				try {
					controller.hex2rgb();
					assert.isFalse(true, "expected an exception.");
				} catch (e) {
					assert.isTrue(e instanceof TypeError, e.message);
				}
			},
			// Maybe we need better rubbish input guards?
			testGetLiteral: function() {
				var HTML4Colors = {
						"Black": "#000000",
						"Green": "#008000",
						"Silver": "#C0C0C0",
						"Lime": "#00FF00",
						"Gray": "#808080",
						"Olive": "#808000",
						"White": "#FFFFFF",
						"Yellow": "#FFFF00",
						"Maroon": "#800000",
						"Navy": "#000080",
						"Red": "#FF0000",
						"Blue": "#0000FF",
						"Purple": "#800080",
						"Teal": "#008080",
						"Fuchsia": "#FF00FF",
						"Aqua": "#00FFFF"
					}, c;
				for (c in HTML4Colors) {
					if (HTML4Colors.hasOwnProperty(c)) {
						assert.strictEqual(HTML4Colors[c], controller.getLiteral(c).toUpperCase());
					}
				}
			}, // todo: I am not happy with these I expect garbage in undefined/null out.
			testGetListeralNotAColor: function() {
				var result = controller.getLiteral("not-a-color");
				if (typeof window.getComputedStyle !== "undefined") {
					assert.strictEqual("#000000", result);
				} else {
					assert.isNull(result);
				}
			},
			testGetLiteralNullArg: function() {
				var result = controller.getLiteral();
				if (typeof window.getComputedStyle !== "undefined") {
					assert.strictEqual("#000000", result);
				} else {
					assert.isNull(result);
				}
			},
			testRgb2HexString: function() {
				var start = "rgb(0,0,0)",
					expected = "#000000";
				assert.strictEqual(expected, controller.rgb2hex(start));
			},
			testRgb2HexArray: function() {
				var start = [0, 0, 0],
					expected = "#000000";
				assert.strictEqual(expected, controller.rgb2hex(start));
			},
			testRgb2HexObj: function() {
				var start = {r: 0, g: 0, b: 0},
					expected = "#000000";
				assert.strictEqual(expected, controller.rgb2hex(start));
			},
			testRgb2HexNullarg: function() {
				assert.isNull(controller.rgb2hex());
			},
			testRgb2HexBadString: function() {
				assert.isNull(controller.rgb2hex("not-rgb-string"));
			},
			testRgb2HexBadString2: function() {
				assert.isNull(controller.rgb2hex("rgb()"));
			},
			testIsHex3: function() {
				assert.isTrue(controller.isHex("#123"));
			},
			testIsHex6: function() {
				assert.isTrue(controller.isHex("#123123"));
			},
			testIsHexNotHexString: function() {
				assert.isFalse(controller.isHex("foo"));
			},
			testIsHexNullArg: function() {
				assert.isFalse(controller.isHex());
			},
			testIsHexNotString: function() {
				assert.isFalse(controller.isHex(0x0));
			}
		});
	});

