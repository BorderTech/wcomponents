import color from "wc/dom/color.mjs";

describe("wc/dom/color", function() {

	it("testHex2rgb3", function() {
		const start = "#000",
			expected = { r: 0, g: 0, b: 0 },
			result = color.hex2rgb(start);
		expect(result.r).toBe(expected.r);
		expect(result.g).toBe(expected.g);
		expect(result.b).toBe(expected.b);
	});

	it("testHex2rgb6", function() {
		const start = "#00ff00",
			expected = {r: 0, g: 255, b: 0},
			result = color.hex2rgb(start);
		expect(result.r).toBe(expected.r);
		expect(result.g).toBe(expected.g);
		expect(result.b).toBe(expected.b);
	});

	it("testHex2rgb3NoHash", function() {
		const start = "000",
			expected = {r: 0, g: 0, b: 0},
			result = color.hex2rgb(start);
		expect(result.r).toBe(expected.r);
		expect(result.g).toBe(expected.g);
		expect(result.b).toBe(expected.b);
	});

	it("testHex2rgb6NoHash", function() {
		const start = "00ff00",
			expected = {r: 0, g: 255, b: 0},
			result = color.hex2rgb(start);
		expect(result.r).toBe(expected.r);
		expect(result.g).toBe(expected.g);
		expect(result.b).toBe(expected.b);
	});

	// exception tests for hex2rgb
	it("testHex2rgbNumberArg", function() {
		// @ts-ignore
		expect(() => color.hex2rgb(0x0)).toThrowError();  // this is a hex number
	});

	it("testHex2rgbNullArg", function() {
		// @ts-ignore
		expect(() => color.hex2rgb()).toThrowError();
	});

	// Maybe we need better rubbish input guards? - Yes, done
	it("testGetLiteral", function() {
		const HTML4Colors = {
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
		};

		for (let c in HTML4Colors) {
			if (HTML4Colors.hasOwnProperty(c)) {
				expect(color.getLiteral(c).toUpperCase()).toBe(HTML4Colors[c]);
			}
		}
	});
	// I am not happy with these I expect garbage in undefined/null out.
	// Agreed, have changed this behavior

	it("testGetLiteralNotAColor", function() {
		const result = color.getLiteral("not-a-color");
		expect(result).toBeNull();
	});


	it("testGetLiteralNullArg", function() {
		// @ts-ignore
		const result = color.getLiteral();

		expect(result).toBeNull();
	});

	it("testRgb2HexString", function() {
		const start = "rgb(0,0,0)",
			expected = "#000000";
		expect(color.rgb2hex(start)).toBe(expected);
	});

	it("testRgb2HexArray", function() {
		const start = [0, 0, 0],
			expected = "#000000";
		expect(color.rgb2hex(start)).toBe(expected);
	});

	it("testRgb2HexObj", function() {
		const start = { r: 0, g: 0, b: 0 },
			expected = "#000000";
		expect(color.rgb2hex(start)).toBe(expected);
	});

	it("testRgb2HexNullarg", function() {
		// @ts-ignore
		expect(color.rgb2hex()).toBeNull();
	});

	it("testRgb2HexBadString", function() {
		expect(color.rgb2hex("not-rgb-string")).toBeNull();
	});

	it("testRgb2HexBadString2", function() {
		const rgb = "rgb";
		const badVal = `${rgb}()`;  // hiding this from IDE
		expect(color.rgb2hex(badVal)).toBeNull();
	});

	it("testIsHex3", function() {
		expect(color.isHex("#123")).toBeTrue();
	});

	it("testIsHex6", function() {
		expect(color.isHex("#123123")).toBeTrue();
	});

	it("testIsHexNotHexString", function() {
		expect(color.isHex("foo")).toBeFalse();
	});

	it("testIsHexNullArg", function() {
		// @ts-ignore
		expect(color.isHex()).toBeFalse();
	});

	it("testIsHexNotString", function() {
		// @ts-ignore
		expect(color.isHex(0x0)).toBeFalse();
	});

});

