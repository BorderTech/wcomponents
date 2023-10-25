import event from "wc/dom/event.mjs";
import {getInput, setUpExternalHTML} from "../helpers/specUtils.mjs";
import domTesting from "@testing-library/dom";

describe("wc/dom/event", () => {
	const ids = {
		TEXTFIELD: "textField",
		TEXTFIELD2: "textField2",
		RADIO1: "radio1",
		RADIO2: "radio2",
		CHKBOX: "chkbox",
		ANCHOR: "anchor",
		PASSWD: "passwd",
		TXTAREA: "txtarea",
		BUTTONINP: "btninp",
		BUTTON: "btn" };

	let ownerDocument, eventContainer;

	beforeEach(function() {
		// This totally resets the entire DOM before each test
		return setUpExternalHTML("domEvent.html").then(dom => {
			ownerDocument = dom.window.document;
			eventContainer = domTesting.getByTestId(ownerDocument, "event_test_container");
		});
	});

	it("will fire a DOM Level 1 click handler", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.TEXTFIELD);
		expect(element.getAttribute("data-clicked")).not.toBe("true");
		// With JSDom we need to wire up the onclick here
		element.onclick = function() {
			const button = /** @type HTMLElement */(this);
			button.setAttribute("data-clicked", "true");
		};
		event.fire(element, "click");
		expect(element.getAttribute("data-clicked")).toBe("true");
	});

	it("fires an event on a text input element using bubble", function() {
		const handler = jasmine.createSpy("testAddFireEventTextField");
		const element = domTesting.getByTestId(ownerDocument, ids.TEXTFIELD2);
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("fires an event on a text input element using bubble and config api", function() {
		const handler = jasmine.createSpy("testAddFireEventTextFieldWithEventArgs");
		const element = domTesting.getByTestId(ownerDocument, ids.TEXTFIELD2);
		event.add(element, { type: "click", listener: handler });
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("fires an event on a textarea element using bubble", function() {
		const handler = jasmine.createSpy("testAddFireEventTextArea");
		const element = domTesting.getByTestId(ownerDocument, ids.TXTAREA);
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	/**
	 * This tests that events fire synchronously even if another event is firing.
	 */
	it("fires event synchronously while different event already firing", function() {
		const handles = [],
			element = domTesting.getByTestId(ownerDocument, ids.TXTAREA);
		try {
			let wasCalled = false;
			handles.push(event.add(element, "kungfu", function($event) {
				handles.push(event.add(element, "click", () => wasCalled = true));
				event.fire($event.target, "click");
			}));
			event.fire(element, "kungfu", { detail: "foo" });
			expect(wasCalled).toBeTrue();
		} finally {
			event.remove(handles);
		}
	});

	/**
	 * This tests that events fire synchronously even if THE SAME event is firing.
	 * This would fail on all versions of event manager before Sep 2019.
	 */
	it("fires event synchronously while same event already firing", function() {
		const handles = [],
			element = domTesting.getByTestId(ownerDocument, ids.TXTAREA);
		try {
			let wasCalled = false;
			handles.push(event.add(element, "kungfu", function($event) {
				handles.push(event.add(element, "kungfu", () => wasCalled = true));
				event.fire($event.target, "kungfu", { detail: "bar" });
			}));
			event.fire(element, "kungfu", { detail: "foo" });
			expect(wasCalled).toBeTrue();
		} finally {
			event.remove(handles);
		}
	});

	it("fires a custom event type", function() {
		const dataExpected = { kung: "fu" },
			element = domTesting.getByTestId(ownerDocument, ids.TXTAREA);
		let kungActual;
		event.add(element, "kungfu", function($event) {
			kungActual = $event.detail.kung;
		});
		event.fire(element, "kungfu", { detail: dataExpected });
		expect(kungActual).toBe(dataExpected.kung);
	});

	it("removes a custom event type", function() {
		const dataExpected = { kung: "fu" },
			element = domTesting.getByTestId(ownerDocument, ids.TXTAREA);
		let kungActual;
		const handle = event.add(element, "kungfu", function($event) {
			kungActual = $event.detail.kung;
		});
		event.fire(element, "kungfu", { detail: dataExpected });
		expect(kungActual).toBe(dataExpected.kung);
		kungActual = null;
		event.remove(handle);
		event.fire(element, "kungfu", { detail: dataExpected });
		expect(kungActual).toBeNull();
	});

	it("fires an event on a button input element using bubble", function() {
		const handler = jasmine.createSpy("testAddFireEventButtonInput");
		const element = domTesting.getByTestId(ownerDocument, ids.BUTTONINP);
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("fires an event on a button element using bubble", function() {
		const handler = jasmine.createSpy("testAddFireEventButton");
		const element = domTesting.getByTestId(ownerDocument, ids.BUTTON);
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();

	});

	it("fires an event on a button element using capture", function() {
		const handler = jasmine.createSpy("testAddCaptureFireEventButton");
		const element = domTesting.getByTestId(ownerDocument, ids.BUTTON);
		event.add(element, "click", handler, null, null, true);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("fires an event on a password field", function() {
		const handler = jasmine.createSpy("testAddFireEventPassword");
		const element = domTesting.getByTestId(ownerDocument, ids.PASSWD);
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});


	it("toggles the state of a checkbox when fire is called", function() {
		const element = getInput(ownerDocument, ids.CHKBOX),
			checked = !!element.checked;
		event.fire(element, "click");
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("calls the event handler once and once only when fired", function() {
		const handler = jasmine.createSpy("testFireEventCount");
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);

		event.add(element, "click", handler);
		event.fire(element, "click");
		event.remove(element, "click", handler);
		event.fire(element, "click");
		expect(handler).withContext("Event should be fired once and only once").toHaveBeenCalledTimes(1);
	});

	it("updates the radio button group when click is fired on a single button", function() {
		const element = getInput(ownerDocument, ids.RADIO1),
			element2 = getInput(ownerDocument, ids.RADIO2),
			checked = !!element2.checked;
		event.fire(element2, "click");
		expect(element2.checked).withContext("Two radio buttons in same group can not both be checked").not.toBe(element.checked);
		expect(!!element2.checked).withContext("Radio state should be toggled").toBe(!checked);
	});

	// would be nice to check that the page would navigate but not really possible
	it("testAddFireEventAnchor", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.ANCHOR);
		const handler = jasmine.createSpy("testAddFireEventAnchor");
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalledTimes(1);
	});

	it("removes an event listener using the listener iteself", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testAddRemoveEvent");
		let checked;
		event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
		event.remove(element, "click", handler);
		checked = !!element.checked;
		event.fire(element, "click");
		expect(handler).withContext("Event was removed and should not have fired again").toHaveBeenCalledTimes(1);
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("removes an event listener using the returned handle from add", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testAddRemoveEventWithHandle");
		let checked, handle = event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
		event.remove(handle);
		checked = !!element.checked;
		event.fire(element, "click");
		expect(handler).withContext("Event was removed and should not have fired again").toHaveBeenCalledTimes(1);
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("removes an event listener using the returned handle from add with config arg", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy();
		let checked, handle = event.add(element, { type: "click", listener: handler });
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
		event.remove(handle);
		checked = !!element.checked;
		event.fire(element, "click");
		expect(handler).withContext("Event was removed and should not have fired again").toHaveBeenCalledTimes(1);
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("removes an event listener using an array of returned handles from add", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy();
		let checked, handle = event.add(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
		event.remove([handle]);
		checked = !!element.checked;
		event.fire(element, "click");
		expect(handler).withContext("Event was removed and should not have fired again").toHaveBeenCalledTimes(1);
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("testAddRemoveEventWithCapture", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testAddRemoveEventWithCapture");
		event.add(element, "click", handler, null, null, true);
		event.fire(element, "click");
		event.remove(element, "click", handler, true);
		let checked = !!element.checked;
		event.fire(element, "click");
		expect(handler).withContext("Event was removed and should not have fired again").toHaveBeenCalledTimes(1);
		expect(!!element.checked).withContext("Checkbox state should be toggled").toBe(!checked);

	});

	it("leaves bubble listener in place when removing as capture listener", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testRemoveCaptureIgnoresBubble");
		event.add(element, "click", handler);
		event.remove(element, "click", handler, true);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("leaves capture listener in place when removing as bubble listener", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testRemoveBubbleIgnoresCapture");
		event.add(element, "click", handler, null, null, true);
		event.remove(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("leaves capture listener in place when removing as bubble listener using config API", function() {
		const element = getInput(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testRemoveBubbleIgnoresCaptureWithEventArgs");
		event.add(element, { type: "click", listener: handler, capture: true });
		// event.remove(element, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("will remove an event handler while it is currently firing", function() {
		let wasCalled = false;
		const element = getInput(ownerDocument, ids.CHKBOX);

		const handler = function() {
			wasCalled = true;
			event.remove(this, "click", handler);
		};

		event.add(element, "click", handler);
		event.fire(element, "click");
		let checked = !!element.checked;
		expect(wasCalled).toBeTrue();
		wasCalled = false;
		event.fire(element, "click");
		expect(wasCalled).withContext("Event was removed and should not have fired").toBeFalse();
		expect((!!element.checked)).withContext("Checkbox state should be toggled").toBe(!checked);
	});

	it("testEventPropertiesAndBubbles", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testEventPropertiesAndBubbles").and.callFake(function ($event) {
			expect($event.target).withContext("target should be the element we clicked on").toBe(element);
			expect($event.currentTarget).withContext("currentTarget should be the element we listened on").toBe(eventContainer);
			expect(this).withContext("this should be the currentTarget").toBe($event.currentTarget);
			expect(typeof $event.preventDefault).withContext("The event should have preventDefault method").toBe("function");
			expect(typeof $event.stopPropagation).withContext("The event should have stopPropagation method").toBe("function");
			expect($event.eventPhase).withContext("eventPhase should be bubbling").toBe(ownerDocument.defaultView.Event.BUBBLING_PHASE);
		});
		event.add(eventContainer, "click", handler);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();


	});

	it("testEventPropertiesAndCaptureAndEventArgs", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testEventPropertiesAndCaptureAndEventArgs").and.callFake(function ($event) {
			expect($event.target).withContext("target should be the element we clicked on").toBe(element);
			expect($event.currentTarget).withContext("currentTarget should be the element we listened on").toBe(eventContainer);
			expect(this).withContext("this should be the currentTarget").toBe($event.currentTarget);
			expect(typeof $event.preventDefault).withContext("The event should have preventDefault method").toBe("function");
			expect(typeof $event.stopPropagation).withContext("The event should have stopPropagation method").toBe("function");
			expect($event.eventPhase).withContext("eventPhase should be capturing").toBe(ownerDocument.defaultView.Event.CAPTURING_PHASE);
		});

		event.add(eventContainer, { type: "click", listener: handler, capture: true });
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("testEventPropertiesAtTarget", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		const handler = jasmine.createSpy("testEventPropertiesAndCaptureAndEventArgs").and.callFake(function ($event) {
			expect($event.target).withContext("target should be the element we clicked on").toBe(element);
			expect($event.currentTarget).withContext("currentTarget should be the element we listened on").toBe(element);
			expect(this).withContext("this should be the currentTarget").toBe($event.currentTarget);
			expect(typeof $event.preventDefault).withContext("The event should have preventDefault method").toBe("function");
			expect(typeof $event.stopPropagation).withContext("The event should have stopPropagation method").toBe("function");
			expect($event.eventPhase).withContext("eventPhase should be at target").toBe(ownerDocument.defaultView.Event.AT_TARGET);
		});

		event.add(element, "click", handler, null, null, true);
		event.fire(element, "click");
		expect(handler).toHaveBeenCalled();
	});

	it("testEventPos", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		let count = 0, first, second, third;

		event.add(eventContainer, "click", clickEventCheckSecond);  // fire mid
		event.add(eventContainer, "click", clickEventCheckThird, 50);  // fire last
		event.add(eventContainer, "click", clickEventCheckFirst, -50);  // fire first
		event.fire(element, "click");

		expect(first).toBe(0);
		expect(second).toBe(1);
		expect(third).toBe(2);


		function clickEventCheckFirst() {
			first = count++;
		}

		function clickEventCheckSecond() {
			second = count++;
		}

		function clickEventCheckThird() {
			third = count++;
		}
	});

	it("testEventOrderBubbleAndCaptureAtTarget", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		let count = 0, first, second, third;

		event.add(element, "click", clickEventCheckFirst);  // fire first
		event.add(element, "click", clickEventCheckSecond, null, null, true);  // fire mid (attached with capture)
		event.add(element, "click", clickEventCheckThird);  // fire last
		event.fire(element, "click");

		expect(first).toBe(0);
		expect(second).toBe(1);
		expect(third).toBe(2);


		function clickEventCheckFirst() {
			first = count++;
		}

		function clickEventCheckSecond() {
			second = count++;
		}

		function clickEventCheckThird() {
			third = count++;
		}
	});

	it("testEventOrderWithPosAndBubbleAndCaptureAtTarget", function() {
		// note, using the count also checks that these listeners aren't called more than once
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		let first, second, third, fourth, count = 0;

		event.add(element, "click", clickEventCheckSecond, null, null, true);
		event.add(element, "click", clickEventCheckThird, 50);
		event.add(element, { type: "click", listener: clickEventCheckFourth, pos: 50, capture: true });
		event.add(element, { type: "click", listener: clickEventCheckFirst, pos: -50 });

		event.fire(element, "click");

		expect(first).toBe(0);
		expect(second).toBe(1);
		expect(third).toBe(2);
		expect(fourth).toBe(3);


		function clickEventCheckFirst() {
			first = count++;
		}

		function clickEventCheckSecond() {
			second = count++;
		}

		function clickEventCheckThird() {
			third = count++;
		}

		function clickEventCheckFourth() {
			fourth = count++;
		}
	});

	it("testCancelEventAndPos", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		const clickEventCheckCancel = jasmine.createSpy("clickEventCheckCancel");
		event.add(eventContainer, "click", clickEventCheckCancel, 50);  // fire AFTER (without the 50 this test will likely fail, but we make no guarantee on order unless it is specified)
		event.add(eventContainer, "click", function clickEventCancels($event) {
			$event.preventDefault();
		});
		event.fire(element, "click");
		expect(clickEventCheckCancel).withContext("event should have been cancelled").toHaveBeenCalledWith(jasmine.objectContaining({ defaultPrevented: true }));
	});

	it("should cancel an event when a listener returns false", function() {
		const outerListener = jasmine.createSpy("stopPropagationOuterListenerByReturnVal");
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		event.add(eventContainer, "click", outerListener, 50);  // fire AFTER (without the 50 this test will likely fail, but we make no guarantee on order unless it is specified)
		event.add(eventContainer, "click", () => false);
		event.fire(element, "click");
		expect(outerListener).withContext("event should have been cancelled").toHaveBeenCalledWith(jasmine.objectContaining({ defaultPrevented: true }));
	});

	it("should report that an event has been cancelled during propagation", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		const outerListener = jasmine.createSpy("preventDefaultOuterListener");

		event.add(eventContainer, "click", outerListener);
		event.add(element, "click", $event => $event.preventDefault());
		event.fire(element, "click");

		expect(outerListener).withContext("event should have been cancelled").toHaveBeenCalledWith(jasmine.objectContaining({ defaultPrevented: true }));
	});

	it("stops propagation when stopPropagation is called", function() {
		const outerListener = jasmine.createSpy("stopPropagationOuterListener");
		const element = domTesting.getByTestId(ownerDocument, ids.CHKBOX);
		let innerCalled = false;
		event.add(eventContainer, "click", outerListener);
		event.add(element, "click", function innerListener($event) {
			$event.stopPropagation();
			innerCalled = true;
		});
		event.fire(element, "click");
		expect(outerListener).withContext("event should have been stopped").not.toHaveBeenCalled();
		expect(innerCalled).withContext("event should have been called").toBe(true);
	});

	it("sets the listener 'this' when scope is provided", function() {
		let scopeChecked = false;
		const scope = {
				gremlin: "gremlin"
			},
			element = domTesting.getByTestId(ownerDocument, ids.BUTTONINP);

		function clickEventScopeCheck() {
			expect(this).toBe(scope);
			scopeChecked = true;
		}

		event.add(element, "click", clickEventScopeCheck, null, scope);
		event.fire(element, "click");
		expect(scopeChecked).toBeTrue();
	});

	it("sets the listener 'this' when scope is provided via config arg", function() {
		let scopeChecked = false;
		const scope = {
				gremlin: "gremlin"
			},
			element = domTesting.getByTestId(ownerDocument, ids.BUTTONINP),
			clickEventScopeCheck = function() {
				expect(this).toBe(scope);
				scopeChecked = true;
			};

		try {
			event.add(element, { type: "click", listener: clickEventScopeCheck, scope: scope });
			event.fire(element, "click");
			expect(scopeChecked).toBeTrue();
		} finally {
			event.remove(element, "click", clickEventScopeCheck);
		}
	});

	/**
	 * Tests that the return value of addEvent is what it should be
	 */
	it("testAddEventReturnValue", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.BUTTONINP);
		const handler = () => {};
		expect(event.add(element, "click", handler)).withContext("Should return true(ish) when event add succeeds").toBeTruthy();
		expect(event.add(element, "click", handler)).withContext("Should return false(ish) when event add fails").toBeFalsy();
	});

	/**
	 * This checks that our event manager does not think cloned nodes have inherited
	 * any events attached directly to the node they were cloned from.
	 * If this test fails it means the following:
	 * - You have removed the use of "custom attributes" in the event manager class to store the elid
	 * - You are in Internet Explorer
	 * - You are not quite as clever as you thought.
	 *
	 * Note, this test only tests "cloneNode" but the same problem occurs with copying innerHTML.
	 */
	it("does not treat cloned nodes as the original node with events", function() {
		const element = domTesting.getByTestId(ownerDocument, ids.BUTTONINP);
		const handler = () => {};
		let clone;
		try {
			event.add(element, "click", handler);
			clone = /** @type HTMLElement */(element.cloneNode(true));
			element.parentNode.replaceChild(clone, element);
			expect(event.add(clone, "click", handler)).withContext("The cloned node should not have any events attached").toBeTruthy();
		} finally {
			event.remove(element, "click", handler);
			event.remove(clone, "click", handler);
		}
	});

	/**
	 * Truth be told I am writing this test to get the test line coverage increased...
	 * Nevertheless, it IS a valid test, so there.
	 */
	it("testEventToString", function() {
		const before = event.toString();
		event.add(ownerDocument.body, "click", function() {}, null, null, false);
		const after = event.toString();
		expect(before).withContext("adding an event should be reflected in 'toString'").not.toBe(after);
	});

	/**
	 * Check same outcome with the new EventArgs API
	 */
	it("testEventToStringWithEventArgs", function() {
		const before = event.toString();
		event.add(ownerDocument.body, { type: "click", listener: function() {}, capture: false });
		const after = event.toString();
		expect(before).withContext("adding an event should be reflected in 'toString'").not.toBe(after);
	});
});
