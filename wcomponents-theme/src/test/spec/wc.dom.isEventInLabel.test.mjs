import isEventInLabel from "wc/dom/isEventInLabel.mjs";
import {fudgeDimensions} from "../helpers/specUtils.mjs";

describe("wc/dom/isEventInLabel", () => {

	const noLabelId = "iseventinlabeltest-nolabel",
		inlabelNoFocusId = "iseventinlabeltest-nofocus",
		inLabelWithFocusId = "iseventinlabeltest-focusable",
		testContent = `
			<div style="width: 5em"><button type="button" id="${noLabelId}" style="width: 5em">button 1</button></div>
			<div style="width: 5em"><label style="width: 5em">label content <span id="${inlabelNoFocusId}" style="width: 5em">event target</span></label></div>
			<div style="width: 5em">
				<label style="width: 5em">label content 
					<span tabindex="0" role="button" style="width: 5em">focusable span <span id="${inLabelWithFocusId}" style="width: 5em">event target</span></span>
				</label>
			</div>`;

	let testHolder = document.body;

	beforeAll(() => fudgeDimensions(window));

	beforeEach(function() {
		testHolder.innerHTML = testContent;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("testIsEventInLabel_noLabel", function() {
		const target = document.getElementById(noLabelId);
		expect(isEventInLabel(target)).toBeFalse();
	});

	it("testIsInLabel_notFocusable", function() {
		const target = document.getElementById(inlabelNoFocusId);
		expect(isEventInLabel(target)).toBeTrue();
	});

	it("testIsInLabel_focusable", function() {
		const target = document.getElementById(inLabelWithFocusId);
		expect(isEventInLabel(target)).toBeFalse();
	});
});
