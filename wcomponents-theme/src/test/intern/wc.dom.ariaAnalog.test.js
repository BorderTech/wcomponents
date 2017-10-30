define(["intern!object", "intern/chai!assert", "wc/dom/ariaAnalog", "wc/dom/shed", "./resources/test.utils"],
	function (registerSuite, assert, controller, shed, testutils) {
		"use strict";

		var testHolder,
			testContent,
			allDeps = ["wc/ui/checkboxAnalog", "wc/ui/listboxAnalog", "wc/ui/radioAnalog"],
			cbController,
			listController,
			radioController,
			urlResource = "@RESOURCES@/ariaAnalog.html";

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
				if (typeof Object.freeze !== "undefined") {
					assert.isFrozen(controller);
				} else {
					this.skip("no freeze to test");
				}
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
			testFocusEvent_defaultPrevented: function() {
				var testTarget = document.getElementById("lb0-1"),
					focusTarget = document.getElementById("lb0-0"),
					fakeEvent = {defaultPrevented: true, target: focusTarget};
				assert.isFalse(testTarget.hasAttribute("tabindex"), "expect tabindex not set");
				listController.focusEvent(fakeEvent);
				assert.isFalse(testTarget.hasAttribute("tabindex"), "tabindex should still not be set");
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
			}
		});
	}
);

