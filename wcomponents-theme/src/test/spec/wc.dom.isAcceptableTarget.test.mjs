import  isAcceptableTarget from "wc/dom/isAcceptableTarget.mjs";
import domTesting from "@testing-library/dom";

describe("wc/dom/isAcceptableTarget", () => {
	const sameElementTestId = "isacceptabletargettest-button",
		buttonHolderId = "isacceptabletargettest-buttonholder",
		target1id = "isacceptabletargettest-insidenofocus",
		element1id = "isacceptabletargettest-nofocuscontainer",
		target2id = "isacceptabletargettest-insidefocusable",
		element2id = "isacceptabletargettest-focusablefocus",
		testContent = `
			<div data-testid="${buttonHolderId}">potential event handler
				<button type="button" data-testid="${sameElementTestId}">event target</button>
			</div>
			<div>
				<span data-testid="${element1id}">potential event handler
					<span data-testid="${target1id}">event target</span>
				</span>
			</div>
			<div>
				<span tabindex="0" role="row" data-testid="${element2id}">potential event handler
					<span data-testid="${target2id}" tabindex="0" role="button">event target</span><
				</span>
			</div>`;
	
	const getElement = function (id) {
		const result = /** @type HTMLElement */(domTesting.getByTestId(testHolder, id));
		result.style.width = "5em";
		return result;
	};
	
	let testHolder;

	beforeAll(function() {
		testHolder = document.body;
	});
	
	beforeEach(function() {
		testHolder.innerHTML = testContent;
	});
	
	afterEach(function() {
		testHolder.innerHTML = "";
	});
	
	it("testIsAcceptableTarget_same", function() {
		const element = getElement(sameElementTestId);
		expect(isAcceptableTarget(element, element)).toBeTrue();
	});

	it("testIsAcceptableTarget_notActiveTarget", function() {
		const testElement = getElement(element1id),
			testEventTarget = getElement(target1id);
		expect(isAcceptableTarget(testElement, testEventTarget)).toBeTrue();
	});

	it("testIsAcceptableTarget_activeTarget", function() {
		const testElement = getElement(element2id),
			testEventTarget = getElement(target2id);
		expect(isAcceptableTarget(testElement, testEventTarget)).toBeFalse();
	});

	it("testIsAcceptableTarget_SelfFirstFocusableElement", function() {
		const testElement = getElement(sameElementTestId),
			testEventTarget = getElement(buttonHolderId);
		expect(isAcceptableTarget(testElement, testEventTarget)).toBeTrue();
	});
});
