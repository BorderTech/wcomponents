import getStyle from "wc/dom/getStyle.mjs";

describe("wc/dom/getStyle", function() {
	const testId = "testGetStyle-innerelement";
	const testWidthNoUnits = "200",
		testWidthWithUnits = testWidthNoUnits + "px",
		testDisplay = "block";
	const html = `
		<div id="noStyleContainer">
			<div id="noStyle">
				<input type="text" id="txt1" style="background-color: white" />
				<input type="text" id="txt2" style="background-color: black" />
				<input type="text" id="txt3" style="background-color: #DDDDDD" />
				<input type="text" id="txt4" disabled="disabled"/>
			</div>
		</div>
		<div id="InlineStyledContainer" style="background-color: red">
			<div id="InlineStyled">
			</div>
		</div>
		<div class="styled" id="styledContainer">
			<div id="styled">
			</div>
		</div>
		<div class="styled" id="styledOverrideContainer">
			<div class="styledInner" id="styledOverride">
			</div>
		</div>
		<div id="jsStyledContainer">
			<div id="jsStyled">
			</div>
		</div>
		<span id="${testId}" style="display: ${testDisplay}; width: ${testWidthWithUnits}"'>content</span>`;

	function helpCompareResults(expectedResult, result) {
		expect(result.r).toBe(expectedResult.r);
		expect(result.g).toBe(expectedResult.g);
		expect(result.b).toBe(expectedResult.b);
	}

	function getElementNotColour() {
		return document.getElementById(testId);
	}

	beforeEach(function() {
		document.body.innerHTML = html;
	});
	afterEach(function() {
		document.body.innerHTML = "";
	});

	it("testGetUnsetStyleRed", function() {
		const expectedResult = { r: 255, g: 255, b: 255 };
		const element = document.getElementById("noStyle");
		const result = getStyle(element, "background-color");
		expect(result["r"]).toBe(expectedResult.r);
	});

	it("testGetUnsetStyleGreen", function() {
		const expectedResult = {r: 255, g: 255, b: 255};
		const element = document.getElementById("noStyle");
		const result = getStyle(element, "background-color");

		expect(result["g"]).toBe(expectedResult.g);
	});

	it("testGetUnsetStyleBlue", function() {
		const expectedResult = {r: 255, g: 255, b: 255};
		const element = document.getElementById("noStyle");
		const result = getStyle(element, "background-color");

		expect(result["b"]).toBe(expectedResult.b);
	});

	it("testGetSetStyleRed", function() {
		const expectedResult = {r: 255, g: 0, b: 0};
		const element = document.getElementById("InlineStyledContainer");
		const result = getStyle(element, "background-color");

		expect(result["r"]).toBe(expectedResult.r);
	});

	it("testGetSetStyleGreen", function() {
		const expectedResult = {r: 255, g: 0, b: 0};
		const element = document.getElementById("InlineStyledContainer");
		const result = getStyle(element, "background-color");

		expect(result["g"]).toBe(expectedResult.g);
	});

	it("testGetSetStyleBlue", function() {
		const element = document.getElementById("InlineStyledContainer");
		const result = getStyle(element, "background-color");
		const expectedResult = {r: 255, g: 0, b: 0};
		expect(result["b"]).toBe(expectedResult.b);
	});

	it("testGetStyleTxtboxWhite", function() {
		const element = document.getElementById("txt1");
		const result = getStyle(element, "background-color");
		helpCompareResults({r: 255, g: 255, b: 255}, result);
	});

	it("testGetStyleTxtboxBlack", function() {
		const element = document.getElementById("txt2");
		const result = getStyle(element, "background-color");
		helpCompareResults({r: 0, g: 0, b: 0}, result);
	});

	it("testGetStyleTxtboxColor", function() {
		const element = document.getElementById("txt3");
		const result = getStyle(element, "background-color");
		helpCompareResults({r: 221, g: 221, b: 221}, result);
	});

	it("testGetStyle_JSForm", function() {
		const element = document.getElementById("txt3");
		const result = getStyle(element, "backgroundColor");
		helpCompareResults({r: 221, g: 221, b: 221}, result);
	});

	it("testGetStyle_withUnits", function() {
		const element = getElementNotColour(),
			result = getStyle(element, "width", true);
		expect(result).toBe(testWidthWithUnits);
	});

	it("testGetStyle_withoutUnits", function() {
		const element = getElementNotColour(),
			result = getStyle(element, "width");
		expect(result).toBe(testWidthNoUnits);
	});

	it("testGetStyle_NumberNotAColour", function() {
		const element = getElementNotColour();
		element.style.opacity = "0.9";
		const result = getStyle(element, "opacity", false, true);

		// @ts-ignore
		expect(Math.round(parseFloat(result) * 100)).toBe(90);
	});
});
