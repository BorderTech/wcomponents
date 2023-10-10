import modalShim, { MODAL_BACKGROUND_ID } from "wc/ui/modalShim.mjs";
import shed from "wc/dom/shed.mjs";
import domTesting from "@testing-library/dom";

/*
 * Unit tests for wc/ui/modalShim
 * TODO: Add functional tests to test event handlers.
 */

const testContent = `
	<div data-testid="${MODAL_BACKGROUND_ID}" id="${MODAL_BACKGROUND_ID}" style="width:5em">
		<div data-testid="inneractiveregion" style="width:5em">
			<button type="button" data-testid="activewhenopen" accesskey="B" style="width:5em">button</button>
		</div>
	</div>
	<div data-testid="outsideshim" style="width:5em">
		<label data-testid="lblWithAccessKey" accesskey="L" style="width:5em">Label<input type="text" data-testid="textinputforfocustest" style="width:5em"></label>
	</div>
	<div data-testid="regionwithnoaccesskeys" style="width:5em">nothing here</div>`;

describe("wc/ui/modalShim", () => {
	let testHolder;

	beforeAll(function() {
		testHolder = document.body;
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	beforeEach(function() {
		// we call clearModal twice - once to reset all the old settings, remove events etc. then again after resetting the innerHTML
		// to ensure the new modal is in the cleared state.
		modalShim.clearModal();
		testHolder.innerHTML = testContent;
		modalShim.clearModal();  // set up the new shim and make sure it is hidden
	});

	it("testDefaultAccessKeys", function() {
		testAccessKeys();
	});

	it("testClearModal", function() {
		const shim = getShim(true);
		expect(shed.isHidden(shim)).withContext("modal shim should be hidden.").toBeTrue();
	});

	it("testSetModal", function() {
		modalShim.setModal();
		const shim = getShim(true);
		expect(shed.isHidden(shim)).withContext("modal shim should not be hidden.").toBeFalse();
	});

	it("testSetModalWithCreate", function() {
		const shim = getShim(false);
		if (shim) {
			shim.parentNode.removeChild(shim);
		}
		expect(getShim(false)).toBeNull();
		modalShim.setModal();
		getShim(true);
	});

	it("testSetModalClassName", function() {
		const className = "shimclass";
		modalShim.setModal(null, className);
		const shim = getShim(true);
		expect(shim).toHaveClass(className);
	});

	it("testSetModalAccessKeyUnchanged", function() {
		modalShim.setModal();
		testAccessKeys();
	});

	it("testSetModalActiveRegionUnsetsAccessKeys", function() {
		const activeRegion = domTesting.getByTestId(testHolder, "inneractiveregion");
		modalShim.setModal(activeRegion);
		testAccessKeys(true);
	});

	it("testSetModalAlternateActiveRegionUnsetsAccessKeys", function() {
		const activeRegion = domTesting.getByTestId(testHolder, "outsideshim");
		modalShim.setModal(activeRegion);
		testAccessKeys(false, true);
	});

	it("testSetModalThirdActiveRegionUnsetsAllAccessKeys", function() {
		const activeRegion = domTesting.getByTestId(testHolder, "regionwithnoaccesskeys");
		modalShim.setModal(activeRegion);
		testAccessKeys(true, true);
	});

	it("testClearModalResetsAccessKeys", function() {
		const activeRegion = domTesting.getByTestId(testHolder, "regionwithnoaccesskeys");
		modalShim.setModal(activeRegion);
		modalShim.clearModal();
		testAccessKeys();
	});

	it("should default to notify on clearModal not setModal", function() {
		const subscriber = jasmine.createSpy();
		try {
			modalShim.subscribe(subscriber);
			modalShim.setModal();
			expect(subscriber).not.toHaveBeenCalled();
			modalShim.clearModal();
			expect(subscriber).toHaveBeenCalled();
		} finally {
			modalShim.unsubscribe(subscriber);
		}
	});

	it("testSubscribeToShow", function() {
		const subscriber = jasmine.createSpy();
		try {
			modalShim.subscribe(subscriber, true);
			modalShim.setModal();
			expect(subscriber).toHaveBeenCalled();
		} finally {
			modalShim.unsubscribe(subscriber);
		}
	});

	it("should unsubscribe", function() {
		const subscriber = jasmine.createSpy();
		modalShim.subscribe(subscriber);
		modalShim.setModal();
		modalShim.unsubscribe(subscriber);
		modalShim.clearModal();
		expect(subscriber).not.toHaveBeenCalled();
	});

	it("should unsubscribe from show", function() {
		const subscriber = jasmine.createSpy();
		modalShim.subscribe(subscriber, true);
		modalShim.unsubscribe(subscriber, true);
		modalShim.setModal();
		expect(subscriber).not.toHaveBeenCalled();
	});

	it("should notify after clearing modal because we unsubscribed from the wrong group", () => {
		const subscriber = jasmine.createSpy();
		try {
			modalShim.setModal();
			modalShim.subscribe(subscriber);
			modalShim.unsubscribe(subscriber, true);
			modalShim.clearModal();
			expect(subscriber).toHaveBeenCalled();
		} finally {
			modalShim.unsubscribe(subscriber);
		}
	});

	it("should notify after setting modal because we unsubscribed from the wrong group", function() {
		const subscriber = jasmine.createSpy();
		try {
			modalShim.subscribe(subscriber, true);
			modalShim.unsubscribe(subscriber);
			modalShim.setModal();
			expect(subscriber).toHaveBeenCalled();
		} finally {
			modalShim.unsubscribe(subscriber, true);
		}
	});

	it("testSubscribeToShowWithArg", function() {
		const localSubscriber = jasmine.createSpy("testSubscribeToShowWithArg");

		try {
			modalShim.subscribe(localSubscriber, true);
			modalShim.setModal();
			expect(localSubscriber).withContext("Expected to be notified with an arg").toHaveBeenCalledWith(jasmine.anything());
		} finally {
			modalShim.unsubscribe(localSubscriber, true);
		}
	});

	it("testSubscribeToShowWithArgReturnsShim", function() {
		const shim = getShim(true);
		const localSubscriber = jasmine.createSpy("testSubscribeToShowWithArgReturnsShim");
		try {
			modalShim.subscribe(localSubscriber, true);
			modalShim.setModal();
			expect(localSubscriber).withContext("Expected subscriber to notify with shim element").toHaveBeenCalledWith(shim);
		} finally {
			modalShim.unsubscribe(localSubscriber, true);
		}
	});

	it("testSubscribeToShowWithArgSetWithRegion", function() {
		const localSubscriber = jasmine.createSpy("testSubscribeToShowWithArgSetWithRegion"),
			activeRegion = domTesting.getByTestId(testHolder, "outsideshim");
		try {
			modalShim.subscribe(localSubscriber, true);
			modalShim.setModal(activeRegion);
			expect(localSubscriber).withContext("Expected to be notified with an arg").toHaveBeenCalledWith(jasmine.anything());
		} finally {
			modalShim.unsubscribe(localSubscriber, true);
		}
	});

	it("testSubscribeToShowWithArgSetWithRegionReturnsRegion", function() {
		const localSubscriber = jasmine.createSpy("testSubscribeToShowWithArgSetWithRegionReturnsRegion");
		const activeRegion = domTesting.getByTestId(testHolder, "outsideshim");
		try {
			modalShim.subscribe(localSubscriber, true);
			modalShim.setModal(activeRegion);
			expect(localSubscriber).withContext("Expected subscriber to notify with active region").toHaveBeenCalledWith(activeRegion);
		} finally {
			modalShim.unsubscribe(localSubscriber, true);
		}
	});

	/**
	 * Get the modal shim.
	 * @param {boolean} failIfAbsent If not found, fail the test.
	 * @returns {HTMLElement} the modal shim
	 */
	function getShim(failIfAbsent) {
		return failIfAbsent ? domTesting.getByTestId(testHolder, MODAL_BACKGROUND_ID) : domTesting.queryByTestId(testHolder, MODAL_BACKGROUND_ID);
	}

	/**
	 * Helper to test if access keys have been remove/reinstated
	 * @param {boolean} [notL] if true we expect the label access key to have been removed
	 * @param {boolean} [notB] if true we expect the button access key to have been removed
	 */
	function testAccessKeys(notL, notB) {
		let akey = domTesting.getByTestId(testHolder, "lblWithAccessKey");
		if (notL) {
			expect(akey.getAttribute("accesskey")).withContext("Did not expect to find accesskey on label").toBeFalsy();
		} else {
			expect(akey.getAttribute("accesskey")).withContext("Expected to find accesskey 'L'").toBe("L");
		}
		akey = domTesting.getByTestId(testHolder, "activewhenopen");
		if (notB) {
			expect(akey.getAttribute("accesskey")).withContext("Did not expect to find accesskey on button").toBeFalsy();
		} else {
			expect(akey.getAttribute("accesskey")).withContext("Expected to find accesskey 'B'").toBe("B");
		}
	}
});
