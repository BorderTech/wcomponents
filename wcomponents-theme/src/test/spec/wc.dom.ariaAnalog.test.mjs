import ariaAnalog from "wc/dom/ariaAnalog.mjs";
import shed from "wc/dom/shed.mjs";
import checkboxAnalog from "wc/ui/checkboxAnalog.mjs";
import listboxAnalog from "wc/ui/listboxAnalog.mjs";
import radioAnalog from "wc/ui/radioAnalog.mjs";

const html = `
	<div id="aria-analog-test-content">
		<!-- checkbox -->
		<span role="checkbox" aria-checked="true" id="cb0" style="width: 5em;">selected</span>
		<span role="checkbox" aria-checked="false" id="cb1" style="width: 5em;">not selected</span>
		<span role="checkbox" aria-checked="mixed" id="cb2" style="width: 5em;">mixed</span>
	
		<!-- listbox -->
		<span id="lb0" role="listbox">
			<span id="lb0-0" role="option" aria-selected="true" style="width: 5em;">zero</span>
			<span id="lb0-1" role="option" aria-selected="false" style="width: 5em;">one</span>
			<span id="lb0-2" role="option" aria-selected="false" style="width: 5em;">two</span>
			<span id="lb0-3" role="option" aria-selected="false" aria-disabled="true" style="width: 5em;">three</span>
			<span id="lb0-4" role="option" aria-selected="false" style="width: 5em;">four</span>
		</span>
	
		<!-- radio analog -->
		<span id="rb0" role="radiogroup">
			<span id="rb0-0" role="radio" aria-checked="true" style="width: 5em;">zero</span>
			<span id="rb0-1" role="radio" aria-checked="false" style="width: 5em;">one</span>
			<span id="rb0-2" role="radio" aria-checked="false" style="width: 5em;">two</span>
			<span id="rb0-3" role="radio" aria-checked="false" aria-disabled="true" style="width: 5em;">three</span>
			<span id="rb0-4" role="radio" aria-checked="false" style="width: 5em;">four</span>
		</span>
	
	
		<div id="aria-analog-writestate-test-content">
			<!-- for generic writestate testing, use radioAnalog as it does not override writestate -->
			<span id="rb1" role="radiogroup">
				<span id="rb1-0" role="radio" aria-checked="true" data-wc-name="rb1name" data-wc-value="0" style="width: 5em;">zero</span>
				<span id="rb1-1" role="radio" aria-checked="false" data-wc-name="rb1name" data-wc-value="1" style="width: 5em;">one</span>
				<span id="rb1-2" role="radio" aria-checked="false" data-wc-name="rb1name" data-wc-value="2" style="width: 5em;">two</span>
				<span id="rb1-3" role="radio" aria-checked="false" data-wc-name="rb1name" data-wc-value="3" style="width: 5em;">three</span>
				<span id="rb1-4" role="radio" aria-checked="false" data-wc-name="rb1name" data-wc-value="4" style="width: 5em;">four</span>
			</span>
			<!-- selected option disabled so no state written -->
			<span id="rb2" role="radiogroup">
				<span id="rb2-0" role="radio" aria-checked="true" aria-disabled="true" data-wc-name="rb2name" data-wc-value="0" style="width: 5em;">zero</span>
				<span id="rb2-1" role="radio" aria-checked="false" aria-disabled="true" data-wc-name="rb2name" data-wc-value="1" style="width: 5em;">one</span>
				<span id="rb2-2" role="radio" aria-checked="false" aria-disabled="true" data-wc-name="rb2name" data-wc-value="2" style="width: 5em;">two</span>
				<span id="rb2-3" role="radio" aria-checked="false" aria-disabled="true" data-wc-name="rb2name" data-wc-value="3" style="width: 5em;">three</span>
				<span id="rb2-4" role="radio" aria-checked="false" aria-disabled="true" data-wc-name="rb2name" data-wc-value="4" style="width: 5em;">four</span>
			</span>
		</div>
	</div>`;

describe("wc/dom/ariaAnalog", () => {
	let testHolder;

	function getDummyKeydownEvent(target, key, code, ALT, SHIFT, CTRL) {
		return {
			target: target,
			type: "keydown",
			defaultPrevented: false,
			preventDefault: function() {
				this.defaultPrevented = true;
			},
			key,
			code,
			altKey: ALT,
			shiftKey: SHIFT,
			ctrlKey: CTRL
		};
	}

	beforeEach(() => {
		testHolder = document.body;
		testHolder.innerHTML = html;
	});

	afterEach(() => {
		testHolder.innerHTML = "";
	});

	it("testDefaults", function() {
		// default property values
		expect(ariaAnalog.VALUE_ATTRIB).withContext("VALUE_ATTRIBUTE default not as expected.").toBe("data-wc-value");
		expect(ariaAnalog.SELECT_MODE).withContext("SELECT_MODE default not as expected.").toEqual({ MULTIPLE: 0, SINGLE: 1, MIXED: 2 });
		expect(ariaAnalog.KEY_DIRECTION).withContext("KEY_DIRECTION default not as expected.").toEqual({ PREVIOUS: 1, NEXT: 2, FIRST: 4, LAST: 8 });
		expect(ariaAnalog._cycle).withContext("_cycle default not as expected").toBeFalse();
		expect(ariaAnalog.actionable).withContext("actionable default not as expected").toBeNull();
		expect(ariaAnalog.exclusiveSelect).withContext("exclusiveSelect default not as expected").toBe(0);
		expect(ariaAnalog.groupNavigation).withContext("groupNavigation default not as expected").toBeTrue();
		expect(ariaAnalog.lastActivated).withContext("lastActivated default not as expected").toBeNull();
		expect(ariaAnalog.allowSelectSelected).withContext("allowSelectSelected default not as expected").toBeFalse();
		expect(ariaAnalog.simpleSelection).withContext("simpleSelection default not as expected").toBeFalse();
		expect(ariaAnalog._extendedInitialisation).withContext("_extendedInitialisation default not as expected").toBeNull();
	});

	it("testFreeze", function() {
		expect(Object.isFrozen(ariaAnalog)).toBeTrue();
	});

	it("testITEM", function() {
		expect(ariaAnalog.ITEM).toBeUndefined();
	});

	it("testSelectOnNavigate_noElement", function() {
		try {
			ariaAnalog.selectOnNavigate();
			expect(false).withContext("Expected an error to be thrown.").toBeTrue();
		} catch (e) {
			expect(e.message).toBe("Argument must not be null");
		}
	});


	it("testSelectOnNavigate", function() {
		expect(ariaAnalog.selectOnNavigate(testHolder.ownerDocument.body)).toBeFalse();
	});

	/* NOTE: below this is a proxy test of module wc/dom/ariaAnalog using some of its real descendant modules. */

	it("testGetGroupContainer_noContainer", function() {
		// use checkboxAnalog to get a group container as that analog does not have a natural container.
		const start = testHolder.ownerDocument.getElementById("cb0");
		// using ariaAnalog directly - we do not determine the null/undefined return type for this function - it is a pass-thru
		expect(ariaAnalog.getGroupContainer(start)).withContext("did not expect to find container using ariaAnalog").toBeFalsy();
		// using checkboxAnalog
		expect(checkboxAnalog.getGroupContainer(start)).withContext("did not expect to find container using checkboxAnalog").toBeFalsy();
	});

	it("testGetGroupContainer_byRole", function() {
		// use an analog with a defined container scoped by the ARAI rdf - in this case radioAnalog
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			expected = "rb0";
		expect(radioAnalog.getGroupContainer(start).id).withContext("did not find expected container using ariaAnalog").toBe(expected);
	});

	it("testGetGroupContainer_withWidget", function() {
		// use an analog with a container not defined container scoped by the ARAI rdf - in this case listboxAnalog
		const start = testHolder.ownerDocument.getElementById("lb0-0"),
			expected = "lb0";
		expect(listboxAnalog.getGroupContainer(start).id).withContext("did not find expected container using ariaAnalog").toBe(expected);
	});

	it("testShedObserver_cb", function() {
		const start = testHolder.ownerDocument.getElementById("cb1"),
			initialSelection = testHolder.ownerDocument.getElementById("cb0");
		expect(shed.isSelected(initialSelection)).withContext("initial selection should be selected").toBeTrue();
		checkboxAnalog.shedObserver(start, shed.actions.SELECT);
		expect(shed.isSelected(initialSelection)).withContext("initial selection should still be selected").toBeTrue();
	});

	it("testShedObserver_rb", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-1"),
			initialSelection = testHolder.ownerDocument.getElementById("rb0-0");
		expect(shed.isSelected(initialSelection)).withContext("initial selection should be selected").toBeTrue();
		radioAnalog.shedObserver(start, shed.actions.SELECT);
		expect(shed.isSelected(initialSelection)).withContext("initial selection should not be selected").toBeFalse();
	});

	it("testShedObserver_listSingle", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-1"),
			initialSelection = testHolder.ownerDocument.getElementById("lb0-0");
		expect(shed.isSelected(initialSelection)).withContext("initial selection should be selected").toBeTrue();
		listboxAnalog.shedObserver(start, shed.actions.SELECT);
		expect(shed.isSelected(initialSelection)).withContext("initial selection should not be selected").toBeFalse();
	});

	it("testShedObserver_listSingle_explicit", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-1"),
			initialSelection = testHolder.ownerDocument.getElementById("lb0-0"),
			container = testHolder.ownerDocument.getElementById("lb0");
		container.setAttribute("aria-multiselectable", "false");
		expect(shed.isSelected(initialSelection)).withContext("initial selection should be selected").toBeTrue();
		listboxAnalog.shedObserver(start, shed.actions.SELECT);
		expect(shed.isSelected(initialSelection)).withContext("initial selection should not be selected").toBeFalse();
	});

	it("testShedObserver_listMulti", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-1"),
			initialSelection = testHolder.ownerDocument.getElementById("lb0-0"),
			container = testHolder.ownerDocument.getElementById("lb0");
		container.setAttribute("aria-multiselectable", "true");
		expect(shed.isSelected(initialSelection)).withContext("initial selection should be selected").toBeTrue();
		listboxAnalog.shedObserver(start, shed.actions.SELECT);
		expect(shed.isSelected(initialSelection)).withContext("initial selection should still be selected").toBeTrue();
	});

	it("testWriteState", function() {
		const stateContainer = testHolder.ownerDocument.createElement("div"),
			testForm = testHolder.ownerDocument.getElementById("aria-analog-writestate-test-content");
		stateContainer.id = "ariaanalogtest-statecontainer";
		testHolder.appendChild(stateContainer);
		expect(stateContainer.firstElementChild).withContext("state container should not have child nodes before writing state").toBeFalsy();
		radioAnalog.writeState(testForm, stateContainer);
		expect(stateContainer.firstElementChild).withContext("state container should have child nodes after writing state").toBeTruthy();
		// expect only one statefield written
		expect(stateContainer.childNodes.length).toBe(1);
		const stateField = stateContainer.firstElementChild;
		expect(stateField).toBeTruthy();
		expect(stateField.name).toBe("rb1name");
		expect(stateField.value).toBe("0");
	});

	it("testWriteState_changeSelection", function() {
		// all this does is show Mark he is not just writing the state of the first item.
		const stateContainer = testHolder.ownerDocument.createElement("div"),
			testForm = testHolder.ownerDocument.getElementById("aria-analog-writestate-test-content");
		stateContainer.id = "ariaanalogtest-statecontainer";
		testHolder.appendChild(stateContainer);
		expect(stateContainer.firstElementChild).withContext("state container should not have child element(s) before writing state").toBeFalsy();
		shed.select(testHolder.ownerDocument.getElementById("rb1-1"));
		radioAnalog.writeState(testForm, stateContainer);
		expect(stateContainer.firstElementChild).withContext("state container should have child element(s) after writing state").toBeTruthy();
		// expect only one statefield written
		expect(stateContainer.childNodes.length).toBe(1);
		const stateField = stateContainer.firstElementChild;
		expect(stateField).toBeTruthy();
		expect(stateField.name).toBe("rb1name");
		expect(stateField.value).toBe("1");
	});

	it("testFocusEvent", function() {
		const testTarget = testHolder.ownerDocument.getElementById("lb0-1"),
			focusTarget = testHolder.ownerDocument.getElementById("lb0-0"),
			fakeEvent = {defaultPrevented: false, target: focusTarget};
		expect(testTarget.hasAttribute("tabindex")).withContext("expect tabindex not set").toBeFalse();
		listboxAnalog.focusEvent(fakeEvent);
		expect(testTarget.hasAttribute("tabindex")).withContext("tabindex should now be set").toBeTrue();
	});

	it("testFocusEvent_differentController", function() {
		const testTarget = testHolder.ownerDocument.getElementById("lb0-1"),
			focusTarget = testHolder.ownerDocument.getElementById("lb0-0"),
			fakeEvent = {defaultPrevented: false, target: focusTarget};
		expect(testTarget.hasAttribute("tabindex")).withContext("expect tabindex not set").toBeFalse();
		radioAnalog.focusEvent(fakeEvent);
		expect(testTarget.hasAttribute("tabindex")).withContext("tabindex should still not be set").toBeFalse();
	});

	it("testClickEvent", function() {
		const target = testHolder.ownerDocument.getElementById("cb0"),
			fakeEvent = {defaultPrevented: false, target: target};
		expect(shed.isSelected(target)).withContext("target should be initially selected").toBeTrue();
		checkboxAnalog.clickEvent(fakeEvent);
		expect(shed.isSelected(target)).withContext("target should not be selected").toBeFalse();
	});

	it("testClickEvent_defaultPrevented", function() {
		const target = testHolder.ownerDocument.getElementById("cb0"),
			fakeEvent = {defaultPrevented: true, target: target};
		expect(shed.isSelected(target)).withContext("target should be initially selected").toBeTrue();
		checkboxAnalog.clickEvent(fakeEvent);
		expect(shed.isSelected(target)).withContext("target should still be selected").toBeTrue();
	});

	it("testClickEvent_differentController", function() {
		const target = testHolder.ownerDocument.getElementById("cb0"),
			fakeEvent = {defaultPrevented: false, target: target};
		expect(shed.isSelected(target)).withContext("target should be initially selected").toBeTrue();
		listboxAnalog.clickEvent(fakeEvent);
		expect(shed.isSelected(target)).withContext("target should still be selected").toBeTrue();
	});

	it("testKeydownEvent", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown");
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
	});

	it("testKeydownEvent_alt", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown", true);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should still not be true").toBeFalse();
	});

	it("testKeydownEvent_selectOnNavigate", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-1"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown");

		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});

	it("testKeydownEvent_SPACE", function() {
		const target = testHolder.ownerDocument.getElementById("rb0-1"),
			evt = getDummyKeydownEvent(target, "Space", " ");
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(target)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
		expect(shed.isSelected(target)).withContext("target should now be selected").toBeTrue();
	});

	it("testKeydownEvent_RETURN", function() {
		const target = testHolder.ownerDocument.getElementById("rb0-1"),
			evt = getDummyKeydownEvent(target, "Enter", "Enter");
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(target)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
		expect(shed.isSelected(target)).withContext("target should now be selected").toBeTrue();
	});

	it("testKeydownEvent_END", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-4"),
			evt = getDummyKeydownEvent(start, "End", "End");

		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});

	it("testKeydownEvent_HOME", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-4"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-0"),
			evt = getDummyKeydownEvent(start, "Home", "Home");

		shed.select(start); // changes radio selection
		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});

	it("testKeydownEvent_DOWN_ctrl", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-1"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown", false, false, true);
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).withContext("Should not select if ctrl key pressed").toBeFalse();
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
	});

	it("testKeydownEvent_skip_disabled", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-4"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-2"),
			evt = getDummyKeydownEvent(start, "ArrowUp", "ArrowUp");

		shed.select(start); // changes radio selection
		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});

	it("testKeydownEvent_LEFT_is_previous", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-4"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-2"),
			evt = getDummyKeydownEvent(start, "ArrowLeft", "ArrowLeft");

		shed.select(start); // changes radio selection
		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});

	it("testKeydownEvent_RIGHT_is_next", function() {
		const start = testHolder.ownerDocument.getElementById("rb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("rb0-1"),
			evt = getDummyKeydownEvent(start, "ArrowRight", "ArrowRight");

		expect(shed.isSelected(expectedEnd)).toBeFalse();
		radioAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).toBeTrue();
	});
	// tests of multi selection based on chordal strokes need a multi-selectable grouped analog, listAnalog calls ariaAnalog#keydownEvent
	it("testKeydownEvent_shift_multiSelect", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("lb0-1"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown", false, true);
		container.setAttribute("aria-multiselectable", "true");
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(start)).withContext("start element not in expected selected state").toBeTrue();
		expect(shed.isSelected(expectedEnd)).withContext("end element not in expected selected state").toBeFalse();
		listboxAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).withContext("Should select if shift key pressed").toBeTrue();
		expect(shed.isSelected(start)).withContext("start element should still be selected").toBeTrue();
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
	});

	it("testKeydownEvent_shift_notMulti", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("lb0-1"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown", false, true);
		container.removeAttribute("aria-multiselectable"); // just in case
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(start)).withContext("start element not in expected selected state").toBeTrue();
		expect(shed.isSelected(expectedEnd)).withContext("end element not in expected selected state").toBeFalse();
		listboxAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).withContext("Should select if shift key pressed").toBeTrue();
		expect(shed.isSelected(start)).withContext("start element should no longer be selected as not multi-selectable").toBeFalse();
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
	});

	it("testKeydownEvent_shift_ctrl_withMultiSelect", function() {
		const start = testHolder.ownerDocument.getElementById("lb0-0"),
			expectedEnd = testHolder.ownerDocument.getElementById("lb0-1"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			evt = getDummyKeydownEvent(start, "ArrowDown", "ArrowDown", false, true, true);
		container.setAttribute("aria-multiselectable", "true");
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented not as expected").toBeFalse();
		expect(shed.isSelected(start)).withContext("start element not in expected selected state").toBeTrue();
		expect(shed.isSelected(expectedEnd)).withContext("end element not in expected selected state").toBeFalse();
		listboxAnalog.keydownEvent(evt);
		expect(shed.isSelected(expectedEnd)).withContext("Should not select if ctrl key pressed with shift").toBeFalse();
		expect(shed.isSelected(start)).withContext("start element should still be selected").toBeTrue();
		expect(evt.defaultPrevented).withContext("evt.defaultPrevented should be true").toBeTrue();
	});

	it("testDoGroupSelect", function() {
		const lastSelected = testHolder.ownerDocument.getElementById("lb0-0"),
			target = testHolder.ownerDocument.getElementById("lb0-4"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			options = container.querySelectorAll(listboxAnalog.ITEM.toString());
		container.setAttribute("aria-multiselectable", "true");

		Array.prototype.forEach.call(options, function(next) {
			if (next === lastSelected) {
				expect(shed.isSelected(next)).withContext("Selected option " + next.id + " not in expected initial selected state").toBeTrue();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " not in expected initial selected state").toBeFalse();
			}
		});
		listboxAnalog.doGroupSelect(target, lastSelected);

		Array.prototype.forEach.call(options, function(next) {
			if (shed.isDisabled(next)) {
				expect(shed.isSelected(next)).withContext("Disabled option " + next.id + " not in expected selected state").toBeFalse();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " not in expected selected state").toBeTrue();
			}
		});
	});

	it("testDoGroupSelect_deselect", function() {
		const lastSelected = testHolder.ownerDocument.getElementById("lb0-0"),
			target = testHolder.ownerDocument.getElementById("lb0-4"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			options = container.querySelectorAll(listboxAnalog.ITEM.toString());
		container.setAttribute("aria-multiselectable", "true");

		Array.prototype.forEach.call(options, function(next) {
			if (!shed.isDisabled(next)) {
				shed.select(next, true); // silently select - no prblish - just to get everything into the expected state.
			}
		});
		shed.deselect(lastSelected, true);
		Array.prototype.forEach.call(options, function(next) {
			if (next === lastSelected || shed.isDisabled(next)) {
				expect(shed.isSelected(next)).withContext("Deselected option " + next.id + " not in expected initial selected state").toBeFalse();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " not in expected initial selected state").toBeTrue();
			}
		});
		listboxAnalog.doGroupSelect(target, lastSelected);

		Array.prototype.forEach.call(options, function(next) {
			expect(shed.isSelected(next)).withContext("Option " + next.id + " should not be selected").toBeFalse();
		});
	});

	it("testDoGroupSelect_deselect_outsideSelection", function() {
		const lastSelected = testHolder.ownerDocument.getElementById("lb0-0"),
			target = testHolder.ownerDocument.getElementById("lb0-2"),
			outsider = testHolder.ownerDocument.getElementById("lb0-4"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			options = container.querySelectorAll(listboxAnalog.ITEM.toString());
		container.setAttribute("aria-multiselectable", "true");
		shed.select(outsider, true);

		Array.prototype.forEach.call(options, function(next) {
			if (next === lastSelected || next === outsider) {
				expect(shed.isSelected(next)).withContext("Selected option " + next.id + " not in expected initial selected state").toBeTrue();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " not in expected initial selected state").toBeFalse();
			}
		});

		listboxAnalog.doGroupSelect(target, lastSelected);

		Array.prototype.forEach.call(options, function(next) {
			if (next === outsider || shed.isDisabled(next)) {
				expect(shed.isSelected(next)).withContext("Deselected option " + next.id + " should be deselected").toBeFalse();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " should be selected").toBeTrue();
			}
		});
	});
	/* ,
	 *
	 * NOTE to SELF: I know what I should be testing here but this doesn't seem to test it
	 * What was I thinking?
	"testDoGroupSelect_noDeselect_outsideSelection_with_CTRL": function() {
		const lastSelected = testHolder.ownerDocument.getElementById("lb0-0"),
			target = testHolder.ownerDocument.getElementById("lb0-2"),
			outsider = testHolder.ownerDocument.getElementById("lb0-4"),
			container = testHolder.ownerDocument.getElementById("lb0"),
			options = container.querySelectorAll(listController.ITEM.toString());
		container.setAttribute("aria-multiselectable", "true");
		shed.select(outsider, true);

		Array.prototype.forEach.call(options, function(next) {
			if (next === lastSelected || next === outsider) {
				expect(shed.isSelected(next)).withContext("Selected option " + next.id + " not in expected initial selected state").toBeTrue();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " not in expected initial selected state").toBeFalse();
			}
		});

		listController.doGroupSelect(target, lastSelected);

		Array.prototype.forEach.call(options, function(next) {
			if (shed.isDisabled(next)) {
				expect(shed.isSelected(next)).withContext("Deselected option " + next.id + " should be deselected").toBeFalse();
			} else {
				expect(shed.isSelected(next)).withContext("Option " + next.id + " should be selected").toBeTrue();
			}
		});
	} */
});
