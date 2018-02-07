define(["intern!object", "intern/chai!assert", "wc/dom/ariaAnalog", "wc/dom/shed", "wc/dom/event", "./resources/test.utils"],
	function (registerSuite, assert, controller, shed, event, testutils) {
		"use strict";

		var testHolder,
			testContent,
			allDeps = ["wc/ui/checkboxAnalog", "wc/ui/listboxAnalog", "wc/ui/radioAnalog"],
			cbController,
			listController,
			radioController,
			urlResource = "@RESOURCES@/ariaAnalog.html";

		function getDummyKeydownEvent(target, keyCode, ALT, SHIFT, CTRL) {
			return {
				target: target,
				type: event.TYPE.keydown,
				defaultPrevented: false,
				preventDefault: function() {
					this.defaultPrevented = true;
				},
				keyCode: keyCode,
				altKey: ALT,
				shiftKey: SHIFT,
				ctrlKey: CTRL
			};
		}

		registerSuite({
			name: "wc/dom/ariaAnalog",
			setup: function() {
				var result = testutils.setupHelper(allDeps).then(function(arg) {
					cbController = arg[0];
					listController = arg[1];
					radioController = arg[2];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder).then(function(response) {
						testContent = response;
						return Promise.resolve();
					});
				});
				return result;
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testDefaults: function() {
				// default property values
				assert.strictEqual(controller.VALUE_ATTRIB, "data-wc-value", "VALUE_ATTRIBUTE default not as expected.");
				assert.deepEqual(controller.SELECT_MODE, { MULTIPLE: 0, SINGLE: 1, MIXED: 2 }, "SELECT_MODE default not as expected.");
				assert.deepEqual(controller.KEY_DIRECTION, { PREVIOUS: 1, NEXT: 2, FIRST: 4, LAST: 8 }, "KEY_DIRECTION default not as expected.");
				assert.isFalse(controller._cycle, "_cycle default not as expected");
				assert.isNull(controller.actionable, "actionable default not as expected");
				assert.strictEqual(controller.exclusiveSelect, 0, "exclusiveSelect default not as expected");
				assert.isTrue(controller.groupNavigation, "groupNavigation default not as expected");
				assert.isNull(controller.lastActivated, "lastActivated default not as expected");
				assert.isFalse(controller.allowSelectSelected, "allowSelectSelected default not as expected");
				assert.isFalse(controller.simpleSelection, "simpleSelection default not as expected");
				assert.isNull(controller._extendedInitialisation, "_extendedInitialisation default not as expected");
			},
			testFreeze: function() {
				if (typeof Object.freeze === "undefined") {
					this.skip("no freeze to test");
				}
				assert.isFrozen(controller);
			},
			testITEM: function() {
				assert.isUndefined(controller.ITEM);
			},
			testSelectOnNavigate_noElement: function() {
				try {
					controller.selectOnNavigate();
					assert.isTrue(false, "Expected an error to be thrown.");
				} catch (e) {
					assert.strictEqual(e.message, "Argument must not be null");
				}
			},
			testSelectOnNavigate: function() {
				assert.isFalse(controller.selectOnNavigate(document.body));
			}
			/* NOTE: below this is a proxy test of module wc/dom/ariaAnalog using some of its real descendant modules. */
			,testGetGroupContainer_noContainer: function() {
				// use checkboxAnalog to get a group container as that analog does not have a natural container.
				var start = document.getElementById("cb0");
				// using ariaAnalog directly - we do not determine the null/undefined return type for this function - it is a pass-thru
				assert.isNotOk(controller.getGroupContainer(start), "did not expect to find container using ariaAnalog");
				// using checkboxAnalog
				assert.isNotOk(cbController.getGroupContainer(start), "did not expect to find container using checkboxAnalog");
			},
			testGetGroupContainer_byRole: function() {
				// use an analog with a defined container scoped by the ARAI rdf - in this case radioAnalog
				var start = document.getElementById("rb0-0"),
					expected = "rb0";
				assert.strictEqual(radioController.getGroupContainer(start).id, expected, "did not find expected container using ariaAnalog");
			},
			testGetGroupContainer_withWidget: function() {
				// use an analog with a container not defined container scoped by the ARAI rdf - in this case listboxAnalog
				var start = document.getElementById("lb0-0"),
					expected = "lb0";
				assert.strictEqual(listController.getGroupContainer(start).id, expected, "did not find expected container using ariaAnalog");
			},
			testShedObserver_cb: function() {
				var start = document.getElementById("cb1"),
					initialSelection = document.getElementById("cb0");
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should be selected");
				cbController.shedObserver(start, shed.actions.SELECT);
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should still be selected");
			},
			testShedObserver_rb: function() {
				var start = document.getElementById("rb0-1"),
					initialSelection = document.getElementById("rb0-0");
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should be selected");
				radioController.shedObserver(start, shed.actions.SELECT);
				assert.isFalse(shed.isSelected(initialSelection), "initial selection should not be selected");
			},
			testShedObserver_listSingle: function() {
				var start = document.getElementById("lb0-1"),
					initialSelection = document.getElementById("lb0-0");
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should be selected");
				listController.shedObserver(start, shed.actions.SELECT);
				assert.isFalse(shed.isSelected(initialSelection), "initial selection should not be selected");
			},
			testShedObserver_listSingle_explicit: function() {
				var start = document.getElementById("lb0-1"),
					initialSelection = document.getElementById("lb0-0"),
					container = document.getElementById("lb0");
				container.setAttribute("aria-multiselectable", "false");
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should be selected");
				listController.shedObserver(start, shed.actions.SELECT);
				assert.isFalse(shed.isSelected(initialSelection), "initial selection should not be selected");
			},
			testShedObserver_listMulti: function() {
				var start = document.getElementById("lb0-1"),
					initialSelection = document.getElementById("lb0-0"),
					container = document.getElementById("lb0");
				container.setAttribute("aria-multiselectable", "true");
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should be selected");
				listController.shedObserver(start, shed.actions.SELECT);
				assert.isTrue(shed.isSelected(initialSelection), "initial selection should still be selected");
			},
			testWriteState: function() {
				var stateContainer = document.createElement("div"),
					testForm = document.getElementById("aria-analog-writestate-test-content"),
					stateField;
				stateContainer.id = "ariaanalogtest-statecontainer";
				testHolder.appendChild(stateContainer);
				assert.isNotOk(stateContainer.firstElementChild, "state container should not have child nodes before writing state");
				radioController.writeState(testForm, stateContainer);
				assert.isOk(stateContainer.firstElementChild, "state container should have child nodes after writing state");
				// expect only one statefield written
				assert.strictEqual(stateContainer.childNodes.length, 1);
				stateField = stateContainer.firstElementChild;
				assert.isOk(stateField);
				assert.strictEqual(stateField.name, "rb1name");
				assert.strictEqual(stateField.value, "0");
			},
			testWriteState_changeSelection: function() {
				// all this does is show Mark he is not just writing the state of the first item.
				var stateContainer = document.createElement("div"),
					testForm = document.getElementById("aria-analog-writestate-test-content"),
					stateField;
				stateContainer.id = "ariaanalogtest-statecontainer";
				testHolder.appendChild(stateContainer);
				assert.isNotOk(stateContainer.firstElementChild, "state container should not have child element(s) before writing state");
				shed.select(document.getElementById("rb1-1"));
				radioController.writeState(testForm, stateContainer);
				assert.isOk(stateContainer.firstElementChild, "state container should have child element(s) after writing state");
				// expect only one statefield written
				assert.strictEqual(stateContainer.childNodes.length, 1);
				stateField = stateContainer.firstElementChild;
				assert.isOk(stateField);
				assert.strictEqual(stateField.name, "rb1name");
				assert.strictEqual(stateField.value, "1");
			},
			testFocusEvent: function() {
				var testTarget = document.getElementById("lb0-1"),
					focusTarget = document.getElementById("lb0-0"),
					fakeEvent = {defaultPrevented: false, target: focusTarget};
				assert.isFalse(testTarget.hasAttribute("tabindex"), "expect tabindex not set");
				listController.focusEvent(fakeEvent);
				assert.isTrue(testTarget.hasAttribute("tabindex"), "tabindex should now be set");
			},
			testFocusEvent_differentController: function() {
				var testTarget = document.getElementById("lb0-1"),
					focusTarget = document.getElementById("lb0-0"),
					fakeEvent = {defaultPrevented: false, target: focusTarget};
				assert.isFalse(testTarget.hasAttribute("tabindex"), "expect tabindex not set");
				radioController.focusEvent(fakeEvent);
				assert.isFalse(testTarget.hasAttribute("tabindex"), "tabindex should still not be set");
			},
			testClickEvent: function() {
				var target = document.getElementById("cb0"),
					fakeEvent = {defaultPrevented: false, target: target};
				assert.isTrue(shed.isSelected(target), "target should be initially selected");
				cbController.clickEvent(fakeEvent);
				assert.isFalse(shed.isSelected(target), "target should not be selected");
			},
			testClickEvent_defaultPrevented: function() {
				var target = document.getElementById("cb0"),
					fakeEvent = {defaultPrevented: true, target: target};
				assert.isTrue(shed.isSelected(target), "target should be initially selected");
				cbController.clickEvent(fakeEvent);
				assert.isTrue(shed.isSelected(target), "target should still be selected");
			},
			testClickEvent_differentController: function() {
				var target = document.getElementById("cb0"),
					fakeEvent = {defaultPrevented: false, target: target};
				assert.isTrue(shed.isSelected(target), "target should be initially selected");
				listController.clickEvent(fakeEvent);
				assert.isTrue(shed.isSelected(target), "target should still be selected");
			},
			testKeydownEvent: function() {
				var start = document.getElementById("rb0-0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				radioController.keydownEvent(evt);
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
			},
			testKeydownEvent_alt: function() {
				var start = document.getElementById("rb0-0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN, true);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				radioController.keydownEvent(evt);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented should still not be true");
			},
			testKeydownEvent_selectOnNavigate: function() {
				var start = document.getElementById("rb0-0"),
					expectedEnd = document.getElementById("rb0-1"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN);

				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			},
			testKeydownEvent_SPACE: function() {
				var target = document.getElementById("rb0-1"),
					evt = getDummyKeydownEvent(target, KeyEvent.DOM_VK_SPACE);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isFalse(shed.isSelected(target));
				radioController.keydownEvent(evt);
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
				assert.isTrue(shed.isSelected(target), "target should now be selected");
			},
			testKeydownEvent_RETURN: function() {
				var target = document.getElementById("rb0-1"),
					evt = getDummyKeydownEvent(target, KeyEvent.DOM_VK_RETURN);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isFalse(shed.isSelected(target));
				radioController.keydownEvent(evt);
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
				assert.isTrue(shed.isSelected(target), "target should now be selected");
			},
			testKeydownEvent_END: function() {
				var start = document.getElementById("rb0-0"),
					expectedEnd = document.getElementById("rb0-4"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_END);

				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			},
			testKeydownEvent_HOME: function() {
				var start = document.getElementById("rb0-4"),
					expectedEnd = document.getElementById("rb0-0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_HOME);

				shed.select(start); // changes radio selection
				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			},
			testKeydownEvent_DOWN_ctrl: function() {
				var start = document.getElementById("rb0-0"),
					expectedEnd = document.getElementById("rb0-1"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN, false, false, true);
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isFalse(shed.isSelected(expectedEnd), "Should not select if ctrl key pressed");
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
			},
			testKeydownEvent_skip_disabled: function() {
				var start = document.getElementById("rb0-4"),
					expectedEnd = document.getElementById("rb0-2"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_UP);

				shed.select(start); // changes radio selection
				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			},
			testKeydownEvent_LEFT_is_previous: function() {
				var start = document.getElementById("rb0-4"),
					expectedEnd = document.getElementById("rb0-2"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_LEFT);

				shed.select(start); // changes radio selection
				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			},
			testKeydownEvent_RIGHT_is_next: function() {
				var start = document.getElementById("rb0-0"),
					expectedEnd = document.getElementById("rb0-1"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_RIGHT);

				assert.isFalse(shed.isSelected(expectedEnd));
				radioController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd));
			}, // tests of multi selection based on chordal strokes need a multi-selectable grouped analog, listAnalog calls ariaAnalog#keydownEvent
			testKeydownEvent_shift_multiSelect: function() {
				var start = document.getElementById("lb0-0"),
					expectedEnd = document.getElementById("lb0-1"),
					container = document.getElementById("lb0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN, false, true);
				container.setAttribute("aria-multiselectable", "true");
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isTrue(shed.isSelected(start), "start element not in expected selected state");
				assert.isFalse(shed.isSelected(expectedEnd), "end element not in expected selected state");
				listController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd), "Should select if shift key pressed");
				assert.isTrue(shed.isSelected(start), "start element should still be selected");
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
			},
			testKeydownEvent_shift_notMulti: function() {
				var start = document.getElementById("lb0-0"),
					expectedEnd = document.getElementById("lb0-1"),
					container = document.getElementById("lb0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN, false, true);
				container.removeAttribute("aria-multiselectable"); // just in case
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isTrue(shed.isSelected(start), "start element not in expected selected state");
				assert.isFalse(shed.isSelected(expectedEnd), "end element not in expected selected state");
				listController.keydownEvent(evt);
				assert.isTrue(shed.isSelected(expectedEnd), "Should select if shift key pressed");
				assert.isFalse(shed.isSelected(start), "start element should no longer be selected as not multi-selectable");
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
			},
			testKeydownEvent_shift_ctrl_withMultiSelect: function() {
				var start = document.getElementById("lb0-0"),
					expectedEnd = document.getElementById("lb0-1"),
					container = document.getElementById("lb0"),
					evt = getDummyKeydownEvent(start, KeyEvent.DOM_VK_DOWN, false, true, true);
				container.setAttribute("aria-multiselectable", "true");
				assert.isFalse(evt.defaultPrevented, "evt.defaultPrevented not as expected");
				assert.isTrue(shed.isSelected(start), "start element not in expected selected state");
				assert.isFalse(shed.isSelected(expectedEnd), "end element not in expected selected state");
				listController.keydownEvent(evt);
				assert.isFalse(shed.isSelected(expectedEnd), "Should not select if ctrl key pressed with shift");
				assert.isTrue(shed.isSelected(start), "start element should still be selected");
				assert.isTrue(evt.defaultPrevented, "evt.defaultPrevented should be true");
			},
			testDoGroupSelect: function() {
				var lastSelected = document.getElementById("lb0-0"),
					target = document.getElementById("lb0-4"),
					container = document.getElementById("lb0"),
					options = listController.ITEM.findDescendants(container);
				container.setAttribute("aria-multiselectable", "true");

				Array.prototype.forEach.call(options, function(next) {
					if (next === lastSelected) {
						assert.isTrue(shed.isSelected(next), "Selected option " + next.id + " not in expected initial selected state");
					} else {
						assert.isFalse(shed.isSelected(next), "Option " + next.id + " not in expected initial selected state");
					}
				});
				listController.doGroupSelect(target, lastSelected);

				Array.prototype.forEach.call(options, function(next) {
					if (shed.isDisabled(next)) {
						assert.isFalse(shed.isSelected(next), "Disabled option " + next.id + " not in expected selected state");
					} else {
						assert.isTrue(shed.isSelected(next), "Option " + next.id + " not in expected selected state");
					}
				});
			},
			testDoGroupSelect_deselect: function() {
				var lastSelected = document.getElementById("lb0-0"),
					target = document.getElementById("lb0-4"),
					container = document.getElementById("lb0"),
					options = listController.ITEM.findDescendants(container);
				container.setAttribute("aria-multiselectable", "true");

				Array.prototype.forEach.call(options, function(next) {
					if (!shed.isDisabled(next)) {
						shed.select(next, true); // silently select - no prblish - just to get everything into the expected state.
					}
				});
				shed.deselect(lastSelected, true);
				Array.prototype.forEach.call(options, function(next) {
					if (next === lastSelected || shed.isDisabled(next)) {
						assert.isFalse(shed.isSelected(next), "Deselected option " + next.id + " not in expected initial selected state");
					} else {
						assert.isTrue(shed.isSelected(next), "Option " + next.id + " not in expected initial selected state");
					}
				});
				listController.doGroupSelect(target, lastSelected);

				Array.prototype.forEach.call(options, function(next) {
					assert.isFalse(shed.isSelected(next), "Option " + next.id + " should not be selected");
				});
			},
			testDoGroupSelect_deselect_outsideSelection: function() {
				var lastSelected = document.getElementById("lb0-0"),
					target = document.getElementById("lb0-2"),
					outsider = document.getElementById("lb0-4"),
					container = document.getElementById("lb0"),
					options = listController.ITEM.findDescendants(container);
				container.setAttribute("aria-multiselectable", "true");
				shed.select(outsider, true);

				Array.prototype.forEach.call(options, function(next) {
					if (next === lastSelected || next === outsider) {
						assert.isTrue(shed.isSelected(next), "Selected option " + next.id + " not in expected initial selected state");
					} else {
						assert.isFalse(shed.isSelected(next), "Option " + next.id + " not in expected initial selected state");
					}
				});

				listController.doGroupSelect(target, lastSelected);

				Array.prototype.forEach.call(options, function(next) {
					if (next === outsider || shed.isDisabled(next)) {
						assert.isFalse(shed.isSelected(next), "Deselected option " + next.id + " should be deselected");
					} else {
						assert.isTrue(shed.isSelected(next), "Option " + next.id + " should be selected");
					}
				});
			} /* ,
			 *
			 * NOTE to SELF: I know what I should be testing here but this doesn't seem to test it
			 * What was I thinking?
			testDoGroupSelect_noDeselect_outsideSelection_with_CTRL: function() {
				var lastSelected = document.getElementById("lb0-0"),
					target = document.getElementById("lb0-2"),
					outsider = document.getElementById("lb0-4"),
					container = document.getElementById("lb0"),
					options = listController.ITEM.findDescendants(container);
				container.setAttribute("aria-multiselectable", "true");
				shed.select(outsider, true);

				Array.prototype.forEach.call(options, function(next) {
					if (next === lastSelected || next === outsider) {
						assert.isTrue(shed.isSelected(next), "Selected option " + next.id + " not in expected initial selected state");
					} else {
						assert.isFalse(shed.isSelected(next), "Option " + next.id + " not in expected initial selected state");
					}
				});

				listController.doGroupSelect(target, lastSelected);

				Array.prototype.forEach.call(options, function(next) {
					if (shed.isDisabled(next)) {
						assert.isFalse(shed.isSelected(next), "Deselected option " + next.id + " should be deselected");
					} else {
						assert.isTrue(shed.isSelected(next), "Option " + next.id + " should be selected");
					}
				});
			} */
		});
	}
);

