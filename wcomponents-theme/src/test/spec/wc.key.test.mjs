import key from "wc/key.mjs";
import domTesting from "@testing-library/dom";

describe("wc/key", function() {
	let testId = "test-element";
	let container;

	beforeEach(() => {
		container = document.createElement("div");
		container.innerHTML = `<button type="button" data-testid="${testId}"></button>`;
		document.body.appendChild(container);
	});

	afterEach(() => {
		document.body.removeChild(container);
	});


	it("testIsMetaWithNoMeta", function() {
		return new Promise((win, lose) => {
			container.addEventListener("keydown", (event) => {
				try {
					expect(key.isMeta(event.key)).toBeFalse();
					expect(key.isMeta(event.code)).toBeFalse();
					expect(key.isMeta(event)).toBeFalse();
					win();
				} catch (ex) {
					lose(ex.message);
				}
			});
			const element = domTesting.getByTestId(container, testId);
			domTesting.fireEvent.keyDown(element, {
				key: 'A', code: 'KeyA'
			});
		});
	});

	it("testIsMetaWithLeftShift", function() {
		return metaTest('Shift', 'ShiftLeft');
	});

	it("testIsMetaWithRightShift", function() {
		return metaTest('Shift', 'ShiftRight');
	});

	it("testIsMetaWithRightAlt", function() {
		return metaTest('Alt', 'AltRight');
	});

	it("testIsMetaWithLeftAlt", function() {
		return metaTest('Alt', 'AltLeft');
	});

	it("testIsMetaWithLeftMeta", function() {
		return metaTest('Meta', 'MetaLeft');
	});

	it("testIsMetaWithRightMeta", function() {
		return metaTest('Meta', 'MetaRight');
	});

	it("testIsMetaWithRightControl", function() {
		return metaTest('Control', 'ControlRight');
	});

	it("testIsMetaWithLefttControl", function() {
		return metaTest('Control', 'ControlLeft');
	});

	function metaTest(eventKey, eventCode) {
		return new Promise((win, lose) => {
			container.addEventListener("keydown", event => {
				try {
					expect(key.isMeta(event.key)).toBeTrue();
					expect(key.isMeta(event.code)).toBeTrue();
					expect(key.isMeta(event)).toBeTrue();
					win();
				} catch (ex) {
					lose(ex.message);
				}

			});
			const element = domTesting.getByTestId(container, testId);
			domTesting.fireEvent.keyDown(element, {
				key: eventKey, code: eventCode
			});
		});
	}
});
