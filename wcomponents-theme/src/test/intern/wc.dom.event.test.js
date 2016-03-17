define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var event,
			ids = {
				TEXTFIELD: "textField",
				TEXTFIELD2: "textField2",
				RADIO1: "radio1",
				RADIO2: "radio2",
				CHKBOX: "chkbox",
				ANCHOR: "anchor",
				PASSWD: "passwd",
				TXTAREA: "txtarea",
				BUTTONINP: "btninp",
				BUTTON: "btn"
			},
			urlResource = "@RESOURCES@/domEvent.html",
			EVENT = "click",
			called = false,
			dom1called = false,
			testHolder, eventContainer;

		function clickEvent() {
			called = true;
		}

		window.dom1clickEvent = function() {
			dom1called = true;
		};

		function clickEventSelfRemoving($event) {
			called = true;
			var element = $event.currentTarget;
			event.remove(element, EVENT, clickEventSelfRemoving);
		}
		registerSuite({
			name: "wc/dom/event",
			setup: function() {
				var result = testutils.setupHelper(["wc/dom/event"]).then(function(arr) {
					event = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			beforeEach: function() {
				eventContainer = document.getElementById("event_test_container");
				called = dom1called = false;  // belt, braces, string, glue etc
			},
			afterEach: function() {
				var id, element;
				for (id in ids) {
					if ((element = document.getElementById(ids[id]))) {
						event.remove(element, EVENT, clickEvent);
						event.remove(element, EVENT, clickEvent, true);
					}
				}
				called = dom1called = false;
			},

			testEventConstructor: function() {
				if (event.canCapture) {  // at the moment we don't polyfill this, we only rely on it if we are using capture
					assert.strictEqual(window.Event.CAPTURING_PHASE, 1);
					assert.strictEqual(window.Event.AT_TARGET, 2);
					assert.strictEqual(window.Event.BUBBLING_PHASE, 3);
				}
				else {
					assert.isTrue(true);
				}
			},

			testDomLvl1FireEvent: function() {
				var element = document.getElementById(ids.TEXTFIELD);
				if (dom1called) {
					assert.fail("tear down is not cleaning up dom1called as expected");
				}
				event.fire(element, EVENT);
				assert.isTrue(dom1called);

			},

			testAddFireEventTextField: function() {
				var element = document.getElementById(ids.TEXTFIELD2);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}
			},

			testAddFireEventTextArea: function() {
				var element = document.getElementById(ids.TXTAREA);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}

			},

			testAddFireEventButtonInput: function() {
				var element = document.getElementById(ids.BUTTONINP);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}
			},

			testAddFireEventButton: function() {
				var element = document.getElementById(ids.BUTTON);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}

			},

			testAddCaptureFireEventButton: function() {
				var element = document.getElementById(ids.BUTTON);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					if (event.canCapture) {
						event.add(element, EVENT, clickEvent, null, null, true);
						event.fire(element, EVENT);
						assert.isTrue(called);
					}
					else {
						assert.isTrue(true);  // just so IE does not feel left out.
					}
				}
				finally {
					if (event.capCapture) {
						event.remove(element, EVENT, clickEvent);
					}
				}
			},

			testAddFireEventPassword: function() {
				var element = document.getElementById(ids.PASSWD);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}

			},

			testAddFireEventCheckBox: function() {
				var element = document.getElementById(ids.CHKBOX),
					checked = element.checked ? true : false;
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.strictEqual((element.checked ? true : false), !checked, "Checkbox state should be toggled");
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}
			},

			testCanCapture: function() {
				if (window.addEventListener) {
					assert.isTrue(event.canCapture);
				}
				else {
					assert.isFalse(event.canCapture);
				}
			},

			testFireEventCount: function() {
				var count = 0,
					element = document.getElementById(ids.CHKBOX);

				event.add(element, EVENT, counter);
				event.fire(element, EVENT);
				event.remove(element, EVENT, counter);
				event.fire(element, EVENT);
				assert.strictEqual(count, 1, "Event should be fired once and only once");

				function counter() {
					count++;
				}
			},

			testAddFireEventRadio: function() {
				var element = document.getElementById(ids.RADIO1),
					element2 = document.getElementById(ids.RADIO2),
					checked = element2.checked ? true : false;
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element2, EVENT, clickEvent);
					event.fire(element2, EVENT);
					assert.notStrictEqual(element.checked, element2.checked, "Two radio buttons in same group can not both be checked");
					assert.strictEqual((element2.checked ? true : false), !checked, "Radio state should be toggled");
				}
				finally {
					event.remove(element2, EVENT, clickEvent);
				}
			},

			// would be nice to check that the page would navigate but not really possible
			testAddFireEventAnchor: function() {
				var element = document.getElementById(ids.ANCHOR);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}

			},

			testAddRemoveEvent: function() {
				var element = document.getElementById(ids.CHKBOX),
					checked = element.checked ? true : false;
				event.add(element, EVENT, clickEvent);
				event.fire(element, EVENT);
				called = false;  // reset called, the test is that removing the event means that handler does not get invoked
				event.remove(element, EVENT, clickEvent);
				checked = element.checked ? true : false;
				event.fire(element, EVENT);
				assert.isFalse(called, "Event was removed and should not have fired");
				assert.strictEqual((element.checked ? true : false), !checked, "Checkbox state should be toggled");
			},

			testAddRemoveEventWithCapture: function() {
				var element = document.getElementById(ids.CHKBOX),
					checked = element.checked ? true : false;
				if (event.canCapture) {
					event.add(element, EVENT, clickEvent, null, null, true);
					event.fire(element, EVENT);
					called = false;  // reset called, the test is that removing the event means that handler does not get invoked
					event.remove(element, EVENT, clickEvent, true);
					checked = element.checked ? true : false;
					event.fire(element, EVENT);
					assert.isFalse(called, "Event was removed and should not have fired");
					assert.strictEqual((element.checked ? true : false), !checked, "Checkbox state should be toggled");
				}

			},

			testRemoveCaptureIgnoresBubble: function() {
				var element = document.getElementById(ids.CHKBOX);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					event.add(element, EVENT, clickEvent);
					event.remove(element, EVENT, clickEvent, true);
					event.fire(element, EVENT);
					assert.isTrue(called);
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}
			},

			testRemoveBubbleIgnoresCapture: function() {
				var element = document.getElementById(ids.CHKBOX);
				try {
					if (called) {
						assert.fail("tear down is not cleaning up called as expected");
					}
					if (event.canCapture) {
						event.add(element, EVENT, clickEvent, null, null, true);
						event.remove(element, EVENT, clickEvent);
						event.fire(element, EVENT);
						assert.isTrue(called);
					}
				}
				finally {
					if (event.canCapture) {
						event.remove(element, EVENT, clickEvent, true);
					}
				}
			},

			testAddRemoveWhileFiringEvent: function() {
				var element = document.getElementById(ids.CHKBOX),
					checked = element.checked ? true : false;
				try {
					event.add(element, EVENT, clickEventSelfRemoving);
					event.fire(element, EVENT);
					called = false;
					checked = element.checked ? true : false;
					event.fire(element, EVENT);
					assert.isFalse(called, "Event was removed and should not have fired");
					assert.strictEqual((element.checked ? true : false), !checked, "Checkbox state should be toggled");
				}
				finally {
					event.remove(element, EVENT, clickEventSelfRemoving);  // just in case it fails to remove itself
				}

			},

			testEventPropertiesAndBubbles: function() {
				var element = document.getElementById(ids.CHKBOX),
					ePhase, eTarget, eCurrentTarget, eThis, ePreventDefault, eStopProp;
				if (called) {
					assert.fail("tear down is not cleaning up called as expected");
				}
				event.add(eventContainer, EVENT, clickEventCheckProps);
				event.fire(element, EVENT);
				assert.isTrue(called);
				event.remove(eventContainer, EVENT, clickEventCheckProps);
				assert.strictEqual(eTarget, element, "target should be the element we clicked on");
				assert.strictEqual(eCurrentTarget, eventContainer, "currentTarget should be the element we listened on");
				assert.strictEqual(eThis, eCurrentTarget, "this should be the currentTarget");
				assert.strictEqual(ePreventDefault.constructor, Function, "The event should have preventDefault method");
				assert.strictEqual(eStopProp.constructor, Function, "The event should have stopPropagation method");
				if (event.canCapture) {
					// don't expect older browsers to support this - if we polyfill eventPhase we can test everywhere
					assert.strictEqual(ePhase, window.Event.BUBBLING_PHASE, "eventPhase should be bubbling");
				}


				function clickEventCheckProps($event) {
					eTarget = $event.target;
					eCurrentTarget = $event.currentTarget;
					eThis = this;
					ePreventDefault = $event.preventDefault;
					eStopProp = $event.stopPropagation;
					called = true;
					ePhase = $event.eventPhase;
				}
			},

			testEventPropertiesAndCapture: function() {
				var element = document.getElementById(ids.CHKBOX),
					ePhase, eTarget, eCurrentTarget, eThis, ePreventDefault, eStopProp;
				if (event.canCapture) {
					event.add(eventContainer, EVENT, clickEventCheckProps, null, null, true);
					event.fire(element, EVENT);
					assert.isTrue(called);
					event.remove(eventContainer, EVENT, clickEventCheckProps);
					assert.strictEqual(eTarget, element, "target should be the element we clicked on");
					assert.strictEqual(eCurrentTarget, eventContainer, "currentTarget should be the element we listened on");
					assert.strictEqual(eThis, eCurrentTarget, "this should be the currentTarget");
					assert.strictEqual(ePreventDefault.constructor, Function, "The event should have preventDefault method");
					assert.strictEqual(eStopProp.constructor, Function, "The event should have stopPropagation method");
					assert.strictEqual(ePhase, window.Event.CAPTURING_PHASE, "eventPhase should be capturing");
				}

				function clickEventCheckProps($event) {
					eTarget = $event.target;
					eCurrentTarget = $event.currentTarget;
					eThis = this;
					ePreventDefault = $event.preventDefault;
					eStopProp = $event.stopPropagation;
					called = true;
					ePhase = $event.eventPhase;
				}
			},

			testEventPropertiesAtTarget: function() {
				var element = document.getElementById(ids.CHKBOX),
					ePhase, eTarget, eCurrentTarget, eThis, ePreventDefault, eStopProp;
				if (event.canCapture) {
					event.add(element, EVENT, clickEventCheckProps, null, null, true);
					event.fire(element, EVENT);
					assert.isTrue(called);
					event.remove(element, EVENT, clickEventCheckProps);
					assert.strictEqual(eTarget, element, "target should be the element we clicked on");
					assert.strictEqual(eCurrentTarget, element, "currentTarget should be the element we listened on");
					assert.strictEqual(eThis, eCurrentTarget, "this should be the currentTarget");
					assert.strictEqual(ePreventDefault.constructor, Function, "The event should have preventDefault method");
					assert.strictEqual(eStopProp.constructor, Function, "The event should have stopPropagation method");
					assert.strictEqual(ePhase, window.Event.AT_TARGET, "eventPhase should be capturing");
				}

				function clickEventCheckProps($event) {
					eTarget = $event.target;
					eCurrentTarget = $event.currentTarget;
					eThis = this;
					ePreventDefault = $event.preventDefault;
					eStopProp = $event.stopPropagation;
					called = true;
					ePhase = $event.eventPhase;
				}
			},

			testEventPos: function() {
				var element = document.getElementById(ids.CHKBOX),
					count = 0,
					first, second, third;

				event.add(eventContainer, EVENT, clickEventCheckSecond);  // fire mid
				event.add(eventContainer, EVENT, clickEventCheckThird, 50);  // fire last
				event.add(eventContainer, EVENT, clickEventCheckFirst, -50);  // fire first
				event.fire(element, EVENT);
				event.remove(eventContainer, EVENT, clickEventCheckThird);
				event.remove(eventContainer, EVENT, clickEventCheckFirst);
				event.remove(eventContainer, EVENT, clickEventCheckSecond);
				assert.strictEqual(first, 0);
				assert.strictEqual(second, 1);
				assert.strictEqual(third, 2);


				function clickEventCheckFirst() {
					first = count++;
				}

				function clickEventCheckSecond() {
					second = count++;
				}

				function clickEventCheckThird() {
					third = count++;
				}
			},

			testEventOrderBubbleAndCaptureAtTarget: function() {
				var element = document.getElementById(ids.CHKBOX),
					count = 0,
					first, second, third;

				if (event.canCapture) {
					event.add(element, EVENT, clickEventCheckFirst);  // fire first
					event.add(element, EVENT, clickEventCheckSecond, null, null, true);  // fire mid (attached with capture)
					event.add(element, EVENT, clickEventCheckThird);  // fire last
					event.fire(element, EVENT);
					event.remove(element, EVENT, clickEventCheckFirst);
					event.remove(element, EVENT, clickEventCheckSecond, true);
					event.remove(element, EVENT, clickEventCheckThird);
					assert.strictEqual(first, 0);
					assert.strictEqual(second, 1);
					assert.strictEqual(third, 2);
				}


				function clickEventCheckFirst() {
					first = count++;
				}

				function clickEventCheckSecond() {
					second = count++;
				}

				function clickEventCheckThird() {
					third = count++;
				}
			},

			testEventOrderWithPosAndBubbleAndCaptureAtTarget: function() {
				// note, using the count also checks that these listeners aren't called more than once
				var element = document.getElementById(ids.CHKBOX),
					count = 0,
					first, second, third, fourth;
				if (event.canCapture) {
					event.add(element, EVENT, clickEventCheckSecond, null, null, true);
					event.add(element, EVENT, clickEventCheckThird, 50);
					event.add(element, EVENT, clickEventCheckFourth, 50, null, true);
					event.add(element, EVENT, clickEventCheckFirst, -50);

					event.fire(element, EVENT);
					event.remove(element, EVENT, clickEventCheckThird);
					event.remove(element, EVENT, clickEventCheckFirst);
					event.remove(element, EVENT, clickEventCheckSecond, true);
					event.remove(element, EVENT, clickEventCheckFourth, true);
					assert.strictEqual(first, 0);
					assert.strictEqual(second, 1);
					assert.strictEqual(third, 2);
					assert.strictEqual(fourth, 3);
				}


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
			},

			testCancelEventAndPos: function() {

				var element = document.getElementById(ids.CHKBOX),
					eCancelled;
				event.add(eventContainer, EVENT, clickEventCheckCancel, 50);  // fire AFTER (without the 50 this test will likely fail but we make no guarantee on order unless it is specified)
				event.add(eventContainer, EVENT, clickEventCancels);
				event.fire(element, EVENT);
				event.remove(eventContainer, EVENT, clickEventCheckCancel);
				event.remove(eventContainer, EVENT, clickEventCancels);
				assert.strictEqual(eCancelled, true, "event should have been cancelled");

				function clickEventCheckCancel($event) {
					eCancelled = $event.defaultPrevented;
				}

				function clickEventCancels($event) {
					$event.preventDefault();
				}
			},

			testCancelByReturnValue: function() {
				var element = document.getElementById(ids.CHKBOX),
					eCancelled;
				event.add(eventContainer, EVENT, clickEventCheckCancel, 50);  // fire AFTER (without the 50 this test will likely fail but we make no guarantee on order unless it is specified)
				event.add(eventContainer, EVENT, clickEventCancels);
				event.fire(element, EVENT);
				event.remove(eventContainer, EVENT, clickEventCheckCancel);
				event.remove(eventContainer, EVENT, clickEventCancels);
				assert.strictEqual(eCancelled, true, "event should have been cancelled");


				function clickEventCheckCancel($event) {
					eCancelled = $event.defaultPrevented;
				}

				function clickEventCancels() {
					return false;
				}
			},

			testCancelEventPropagates: function() {
				var element = document.getElementById(ids.CHKBOX),
					eCancelled;

				function clickEventCheckCancel($event) {
					eCancelled = $event.defaultPrevented;
				}

				function clickEventCancels($event) {
					// debug("click handled on element");
					$event.preventDefault();
				}

				event.add(eventContainer, EVENT, clickEventCheckCancel);
				event.add(element, EVENT, clickEventCancels);
				event.fire(element, EVENT);
				event.remove(eventContainer, EVENT, clickEventCheckCancel);
				event.remove(element, EVENT, clickEventCancels);
				assert.strictEqual(eCancelled, true, "event should have been cancelled");


			},

			testStopPropagation: function() {
				var element = document.getElementById(ids.CHKBOX),
					eStopped = true,
					called = false;
				event.add(eventContainer, EVENT, clickEventOuter);
				event.add(element, EVENT, clickEventInner);
				event.fire(element, EVENT);
				event.remove(eventContainer, EVENT, clickEventOuter);
				event.remove(element, EVENT, clickEventInner);
				assert.strictEqual(eStopped, true, "event should have been stopped");
				assert.strictEqual(called, true, "event should have been called");


				function clickEventOuter() {
					eStopped = false;
				}

				function clickEventInner($event) {
					$event.stopPropagation();
					called = true;
				}
			},

			testEventSetScope: function() {
				var scope = {
						gremlin: "gremlin"
					},
					element = document.getElementById(ids.BUTTONINP),
					scopeChecked = false;

				function clickEventScopeCheck() {
					if (this === scope) {
						scopeChecked = true;
					}
				}
				try {
					event.add(element, EVENT, clickEventScopeCheck, null, scope);
					event.fire(element, EVENT);
					assert.isTrue(scopeChecked);
				}
				finally {
					event.remove(element, EVENT, clickEventScopeCheck);
				}

			},

			/**
			 * Tests that the return value of addEvent is what it should be
			 */
			testAddEventReturnValue: function() {
				var element = document.getElementById(ids.BUTTONINP);
				try {
					assert.ok(event.add(element, EVENT, clickEvent), "Should return true(ish) when event add succeeds");
					assert.notOk(event.add(element, EVENT, clickEvent), "Should return false(ish) when event add fails");
				}
				finally {
					event.remove(element, EVENT, clickEvent);
				}
			},

			/**
			 * This checks that our event manager does not think cloned nodes have inherited
			 * any events attached directly to the node they were cloned from.
			 * If this test fails it means the following:
			 * - You have have removed the use of "custom attributes" in the event manager class to store the elid
			 * - You are in Internet Explorer
			 * - You are not quite as clever as you thought.
			 *
			 * Note, this test only tests "cloneNode" but the same problem occurs with copying innerHTML.
			 */
			testClonedWithCloneNode: function() {
				var clone, element = document.getElementById(ids.BUTTONINP);
				try {
					event.add(element, EVENT, clickEvent);
					clone = element.cloneNode(true);
					element.parentNode.replaceChild(clone, element);
					assert.ok("The cloned node should not have any events attached", event.add(clone, EVENT, clickEvent));
				}
				finally {
					event.remove(element, EVENT, clickEvent);
					event.remove(clone, EVENT, clickEvent);
				}
			},

			/**
			 * Truth be told I am writing this test to get the test line coverage increased...
			 * Nevertheless it IS a valid test, so there.
			 */
			testEventToString: function() {
				var before = event.toString(),
					after;
				event.add(document.body, "click", function() {}, null, null, false);
				after = event.toString();
				assert.notStrictEqual(after, before, "adding an event should be reflected in 'toString'");
			}
		});
	});
