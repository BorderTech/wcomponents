import shed from "wc/dom/shed.mjs";
import {getSelect} from "../helpers/specUtils.mjs";

describe("wc/dom.shed", () => {
	const html = `
	<form data-testid="testForm" id="testForm" action="#" method="get">
		<input data-testid="inp1" id="inp1" disabled="disabled"/>
		<input data-testid="inp2" id="inp2" disabled/>
		<input data-testid="inp3" id="inp3"/>
		<a data-testid="anchor1" id="anchor1">a1</a>
		<a data-testid="anchor2" id="anchor2" aria-disabled="true">a2</a>
		<select data-testid="select1" id="select1"><option>foo</option></select>
		<select data-testid="select2" id="select2" disabled="disabled"><option>bar</option></select>
		<div data-testid="subscriberDiv1" id="subscriberDiv1">
			<input data-testid="subscriberDiv1Input1" id="subscriberDiv1Input1"/>
		</div>
		<div data-testid="selectTests" id="selectTests">
			<input type="button" data-testid="togglebtn1" id="togglebtn1" aria-pressed="false"/>
			<input type="button" data-testid="togglebtn2" id="togglebtn2" aria-pressed="true"/>
			<input type="button" data-testid="btn1" id="btn1"/>
			<input type="checkbox" name="chk1" data-testid="chk1" id="chk1"/>
			<input type="checkbox" checked="checked" name="chk2" data-testid="chk2" id="chk2"/>
			<input type="checkbox" name="chk3" data-testid="chk3" id="chk3" indeterminate="true"/>
			<input type="radio" name="rad1" data-testid="rad1" id="rad1"/>
			<input type="radio" checked="checked" name="rad1" data-testid="rad2" id="rad2"/>
			<select name="sel1" data-testid="sel1" id="sel1">
				<option value="opt1">opt1</option>
				<option selected="selected" value="opt2">opt2</option>
			</select>
			<ul>
				<li data-testid="fauxChk1" id="fauxChk1" role="checkbox" aria-checked="false">0</li>
				<li data-testid="fauxChk2" id="fauxChk2" role="checkbox" aria-checked="true">1</li>
				<li data-testid="presLi" id="presLi" role="presentation">0</li>
				<li data-testid="justAnLi" id="justAnLi">0</li>
				<li data-testid="fauxChk3" id="fauxChk3" role="checkbox" aria-checked="mixed">0</li>
				<li data-testid="fauxChk4" id="fauxChk4" role="checkbox" aria-disabled="true">0</li>
			</ul>
			<ul role="radiogroup">
				<li data-testid="fauxRad1" id="fauxRad1" role="radio" aria-checked="false">0</li>
				<li data-testid="fauxRad2" id="fauxRad2" role="radio" aria-checked="true">1</li>
			</ul>
			<ul role="listbox" aria-multiselectable="true">
				<li data-testid="fauxOpt1" id="fauxOpt1" role="option" aria-selected="false">0</li>
				<li data-testid="fauxOpt2" id="fauxOpt2" role="option" aria-selected="true">1</li>
				<li data-testid="fauxOpt3" id="fauxOpt3" role="option" aria-checked="true">1</li>
				<li data-testid="fauxOpt4" id="fauxOpt4" role="option" aria-checked="true">1</li>
			</ul>
		</div>
		<span role="button" data-testid="fauxButton1" id="fauxButton1" aria-pressed="false">FauxButton</span>
		<div data-testid="expandTests" id="expandTests">
			<button data-testid="exp1" id="exp1" type="button" aria-expanded="false">Expand</button>
			<button data-testid="exp2" id="exp2" type="button" aria-expanded="true">Expand</button>
			<details data-testid="details1" id="details1">
				<summary data-testid="details1a" id="details1a">More details</summary>
				<div data-testid="details1content" id="details1content">Content</div>
			</details>
			<details data-testid="details2" id="details2" open="open">
				<summary data-testid="details2a" id="details2a">More details</summary>
				<div data-testid="details2content" id="details2content">Content</div>
			</details>
		</div>
		<div data-testid="hideTests" id="hideTests">
			<button style="width:5em" data-testid="hide1" id="hide1" type="button">Hide</button>
			<button style="width:5em" data-testid="hide2" id="hide2" type="button" hidden="hidden">Show</button>
		</div>
		<div data-testid="mandatoryTests" id="mandatoryTests">
			<input data-testid="inp7" id="inp7" type="text"/>
			<input data-testid="inp8" id="inp8" type="text" required="required"/>
			<input data-testid="inp9" id="inp9" type="text" required/>
			<fieldset role="radiogroup" class="radiobuttonselect" data-testid="radioButtonGroup1" id="radioButtonGroup1" aria-required="true">
				<input data-testid="radioButtonGroup1_1" id="radioButtonGroup1_1" type="radio" required="required" value="0">
				<input data-testid="radioButtonGroup1_2" id="radioButtonGroup1_2" type="radio" required="required" value="1">
			</fieldset>
			<fieldset role="radiogroup" class="radiobuttonselect" data-testid="radioButtonGroup2" id="radioButtonGroup2">
				<input data-testid="radioButtonGroup2_1" id="radioButtonGroup2_1" type="radio" value="0">
				<input data-testid="radioButtonGroup2_2" id="radioButtonGroup2_2" type="radio" value="1">
			</fieldset>
		</div>
		<div data-testid="disabledAncestorTests" id="disabledAncestorTests">
			<fieldset role="radiogroup" class="radiobuttonselect" data-testid="radioButtonGroup3" id="radioButtonGroup3" aria-disabled="true">
				<input data-testid="radioButtonGroup3_1" id="radioButtonGroup3_1" type="radio" value="0">
				<input data-testid="radioButtonGroup3_2" id="radioButtonGroup3_2" type="radio" value="1">
			</fieldset>
		</div>
		<div data-testid="hiddenAncestorTests" id="hiddenAncestorTests" hidden="hidden">
			<fieldset role="radiogroup" class="radiobuttonselect" data-testid="radioButtonGroup4" id="radioButtonGroup4">
				<input data-testid="radioButtonGroup4_1" id="radioButtonGroup4_1" type="radio" value="0">
				<input data-testid="radioButtonGroup4_2" id="radioButtonGroup4_2" type="radio" value="1">
			</fieldset>
		</div>
		<div data-testid="visibletests" id="visibletests">
			<span data-testid="visibletests1" id="visibletests1">hello<span>
		</div>
		<div data-testid="hiddenhtmlsyntax" id="hiddenhtmlsyntax" hidden>hello</div>
	</form>`;

	function _withSubscribeHelper(id, action) {
		const element = document.getElementById(id);
		let actionIGot, elementIGot;
		const subscriber = function($element, $action) {
			elementIGot = $element;
			actionIGot = $action;
		};
		try {
			shed.subscribe(action, subscriber);
			shed[action](element);
			expect(elementIGot).toBe(element);
			expect(actionIGot).toBe(action);
		} finally {
			shed.unsubscribe(action, subscriber);
		}
	}

	beforeEach(() => {
		document.body.innerHTML = html;
	});

	afterEach(function() {
		document.body.innerHTML = "";
	});

	it("testIsDisabledInput", function() {
		expect(shed.isDisabled(document.getElementById("inp1"))).toBeTrue();
	});

	it("testIsDisabledInputNotXML", function() {
		document.body.insertAdjacentHTML("afterbegin", "<input id='inp2' disabled>");
		expect(shed.isDisabled(document.getElementById("inp2"))).toBeTrue();
	});

	it("testIsNotDisabledInput", function() {
		expect(shed.isDisabled(document.getElementById("inp3"))).toBeFalse();
	});

	it("testIsDisabledAnchor", function() {
		expect(shed.isDisabled(document.getElementById("anchor2"))).toBeTrue();
	});

	it("testIsNotDisabledAnchor", function() {
		expect(shed.isDisabled(document.getElementById("anchor1"))).toBeFalse();
	});

	it("testIsNotDisabledSelect", function() {
		expect(shed.isDisabled(document.getElementById("select1"))).toBeFalse();
	});

	it("testIsDisabledSelect", function() {
		expect(shed.isDisabled(document.getElementById("select2"))).toBeTrue();
	});

	it("testIsDisabledElementNotDisableable", function() {
		expect(shed.isDisabled(document.getElementById("subscriberDiv1"))).toBeFalse();
	});

	it("testIsDisabledElementNotNativelyDisableableWithRole", function() {
		expect(shed.isDisabled(document.getElementById("fauxChk4"))).toBeTrue();
	});

	it("testDisableInput", function() {
		const element = document.getElementById("inp3");
		expect(shed.isDisabled(element)).toBeFalse();
		shed.disable(element);
		expect(shed.isDisabled(element)).toBeTrue();
	});

	it("testDisableSelect", function() {
		const element = document.getElementById("select1");
		expect(shed.isDisabled(element)).toBeFalse();
		shed.disable(element);
		expect(shed.isDisabled(element)).toBeTrue();
	});

	it("testDisableElementNotDisableable", function() {
		const element = document.getElementById("subscriberDiv1");
		expect(shed.isDisabled(element)).toBeFalse();
		shed.disable(element);
		expect(shed.isDisabled(element)).toBeFalse();
	});

	it("testDisableElementNotNativelyDisableableWithRole", function() {
		const element = document.getElementById("fauxChk1");
		expect(shed.isDisabled(element)).toBeFalse();
		shed.disable(element);
		expect(shed.isDisabled(element)).toBeTrue();
	});

	it("testEnableInput", function() {
		const element = document.getElementById("inp1");
		expect(shed.isDisabled(element)).toBeTrue();
		shed.enable(element);
		expect(shed.isDisabled(element)).toBeFalse();
	});

	it("testEnableElementNotNativelyDisableableWithRole", function() {
		const element = document.getElementById("fauxChk4");
		expect(shed.isDisabled(element)).toBeTrue();
		shed.enable(element);
		expect(shed.isDisabled(element)).toBeFalse();
	});

	it("testIsSelectedToggleButton", function() {
		expect(shed.isSelected(document.getElementById("togglebtn2"))).toBeTrue();
	});

	it("testIsSelectedToggleButtonFalse", function() {
		expect(shed.isSelected(document.getElementById("togglebtn1"))).toBeFalse();
	});

	it("testIsSelectedNativeButtonFalse", function() {
		expect(shed.isSelected(document.getElementById("btn1"))).toBeFalse();
	});

	it("testIsSelectedWithChkBoxFalse", function() {
		expect(shed.isSelected(document.getElementById("chk1"))).toBeFalse();
	});

	it("testIsSelectedWithChkBoxTrue", function() {
		expect(shed.isSelected(document.getElementById("chk2"))).toBeTrue();
	});

	it("testIsSelectedWithRadioFalse", function() {
		expect(shed.isSelected(document.getElementById("rad1"))).toBeFalse();
	});

	it("testIsSelectedWithRadioTrue", function() {
		expect(shed.isSelected(document.getElementById("rad2"))).toBeTrue();
	});

	it("testIsSelectedWithOptionFalse", function() {
		const selElement = getSelect(document.body, "sel1");
		expect(shed.isSelected(selElement.options[0])).toBeFalse();
	});

	it("testIsSelectedWithOptionTrue", function() {
		const selElement = getSelect(document.body, "sel1");
		expect(shed.isSelected(selElement.options[1])).toBeTrue();
	});

	it("testIsSelectedWithFauxChkBoxFalse", function() {
		const element = document.getElementById("fauxChk1");
		expect(shed.isSelected(element)).toBeFalse();
		expect(shed.isSelected(element)).toBe(shed.state.DESELECTED);
	});

	it("testStateDESELECTED", function() {
		expect(shed.isSelected(document.getElementById("fauxChk1"))).toBe(shed.state.DESELECTED);
	});

	it("testIsSelectedWithFauxChkBoxMixed", function() {
		const element = document.getElementById("fauxChk3");
		expect(!!shed.isSelected(element)).toBeFalse();
	});

	it("testStateMIXED", function() {
		expect(shed.isSelected(document.getElementById("fauxChk3"))).toBe(shed.state.MIXED);
	});

	it("testIsSelectedWithFauxChkBoxTrue", function() {
		expect(shed.isSelected(document.getElementById("fauxChk2"))).toBeTrue();
	});

	it("testStateSELECTED", function() {
		expect(shed.isSelected(document.getElementById("fauxChk2"))).toBe(shed.state.SELECTED);
	});

	it("testIsSelectedWithFauxRadioFalse", function() {
		expect(shed.isSelected(document.getElementById("fauxRad1"))).toBeFalse();
	});
	it("testIsSelectedWithFauxRadioTrue", function() {
		expect(shed.isSelected(document.getElementById("fauxRad2"))).toBeTrue();
	});

	it("testIsSelectedWithNoRole", function() {
		expect(shed.isSelected(document.getElementById("justAnLi"))).toBeFalse();
	});

	it("testIsSelectedWithUnsupportedRole", function() {
		expect(shed.isSelected(document.getElementById("presLi"))).toBeFalse();
	});

	it("testIsSelectedWithFauxOptionFalse", function() {
		expect(shed.isSelected(document.getElementById("fauxOpt1"))).toBeFalse();
	});

	it("testIsSelectedWithFauxOptionTrue", function() {
		expect(shed.isSelected(document.getElementById("fauxOpt2"))).toBeTrue();
	});

	it("testIsSelectedUnpreferredWithFauxOptionTrue", function() {
		expect(shed.isSelected(document.getElementById("fauxOpt3"))).toBeTrue();
	});

	it("testSelectToggleButton", function() {
		const element = document.getElementById("togglebtn1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectNativeButton", function() {
		const element = document.getElementById("btn1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectChkBox", function() {
		const element = document.getElementById("chk1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectRadio", function() {
		const element = document.getElementById("rad1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectOption", function() {
		const selElement = getSelect(document.body, "sel1");
		const element = selElement.options[0];
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectFauxChkBox", function() {
		const element = document.getElementById("fauxChk1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectFauxRadio", function() {
		const element = document.getElementById("fauxRad1");
		if (shed.isSelected(element)) {
			// order of tests should not be important
			shed.deselect(element);
		}
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testSelectFauxOption", function() {
		const element = document.getElementById("fauxOpt1");
		expect(shed.isSelected(element)).toBeFalse();
		shed.select(element);
		expect(shed.isSelected(element)).toBeTrue();
	});

	it("testDeselectToggleButton", function() {
		const element = document.getElementById("togglebtn2");
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectChkBox", function() {
		const element = document.getElementById("chk2");
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectRadio", function() {
		const element = document.getElementById("rad2");
		if (!shed.isSelected(element)) {
			shed.select(element, true);
		}
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectUnpreferredFauxOption", function() {
		const element = document.getElementById("fauxOpt4");
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectOption", function() {
		const selElement = getSelect(document.body, "sel1");
		const element = selElement.options[1];
		if (!shed.isSelected(element)) {
			shed.select(element, true);
		}
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectFauxChkBox", function() {
		const element = document.getElementById("fauxChk2");
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectFauxRadio", function() {
		const element = document.getElementById("fauxRad2");
		// race condition: depends on order of tests
		// expect(controller.isSelected(element)).toBeTrue();
		if (!shed.isSelected(element)) {
			shed.select(element);
		}
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testDeselectFauxOption", function() {
		const element = document.getElementById("fauxOpt2");
		expect(shed.isSelected(element)).toBeTrue();
		shed.deselect(element);
		expect(shed.isSelected(element)).toBeFalse();
	});

	it("testMixChkBox", function() {
		const element = document.getElementById("chk1");
		expect(shed.isSelected(element)).not.toBe(shed.state.MIXED);
		shed.mix(element);
		expect(shed.isSelected(element)).toBe(shed.state.MIXED);
	});

	it("testMixFauxChkBox", function() {
		const element = document.getElementById("fauxChk1");
		expect(shed.isSelected(element)).not.toBe(shed.state.MIXED);
		shed.mix(element);
		expect(shed.isSelected(element)).toBe(shed.state.MIXED);
	});

	it("testMixFauxChkBoxWasSelected", function() {
		const element = document.getElementById("fauxChk2");
		expect(shed.isSelected(element)).not.toBe(shed.state.MIXED);
		shed.mix(element);
		expect(shed.isSelected(element)).toBe(shed.state.MIXED);
	});

	it("testToggleSelected", function() {
		const element = document.getElementById("togglebtn1"),
			selected = shed.isSelected(element);
		shed.toggle(element, shed.actions.SELECT);
		expect(shed.isSelected(element)).not.toBe(selected);
	});

	it("testToggleDisabled", function() {
		const element = document.getElementById("togglebtn1"),
			disabled = shed.isDisabled(element);
		shed.toggle(element, shed.actions.DISABLE);
		expect(shed.isDisabled(element)).not.toBe(disabled);
	});

	it("testToggleHidden", function() {
		const element = document.getElementById("togglebtn1");
		element.style.width = "100px";  // JSDom cannot calculate offsetWidth
		const hidden = shed.isHidden(element);
		shed.toggle(element, shed.actions.HIDE);
		expect(shed.isHidden(element)).not.toBe(hidden);
	});

	it("testIsHiddenFalse", function() {
		expect(shed.isHidden(document.getElementById("hide1"))).toBeFalse();
	});

	it("testIsHiddenTrue", function() {
		expect(shed.isHidden(document.getElementById("hide2"))).toBeTrue();
	});

	it("testHide", function() {
		const element = document.getElementById("hide1");
		expect(shed.isHidden(element)).toBeFalse();
		shed.hide(element);
		expect(shed.isHidden(element)).toBeTrue();
	});

	it("testShow", function() {
		const element = document.getElementById("hide2");
		expect(shed.isHidden(element)).toBeTrue();
		shed.show(element);
		expect(shed.isHidden(element)).toBeFalse();
	});

	it("testSubscribe", function() {
		let subscriberHideRval,
			subscriberShowRval,
			called = 0,
			repeat = 4,
			action,
			i = 0,
			element = document.getElementById("subscriberDiv1"),
			subscriber = function() {
				called++;
			};
		try {
			subscriberHideRval = shed.subscribe(shed.actions.HIDE, subscriber);
			subscriberShowRval = shed.subscribe(shed.actions.SHOW, subscriber);
			while (i < repeat) {
				action = (i % 2) ? shed.actions.SHOW : shed.actions.HIDE;
				shed[action](element);
				i++;
			}
			expect(called).toBe(repeat);
		} finally {  // clean up subscribers
			shed.unsubscribe(shed.actions.HIDE, subscriberHideRval);
			shed.unsubscribe(shed.actions.SHOW, subscriberShowRval);
		}
	});

	it("testUnsubscribe", function() {
		let called = 0,
			repeat = 4,
			action,
			i = 0,
			element = document.getElementById("subscriberDiv1"),
			subscriber = function() {
				called++;
			};
		let subscriberHideRval = shed.subscribe(shed.actions.HIDE, subscriber);
		let subscriberShowRval = shed.subscribe(shed.actions.SHOW, subscriber);
		while (i < repeat) {
			action = (i % 2) ? shed.actions.SHOW : shed.actions.HIDE;
			shed[action](element);
			i++;
		}
		shed.unsubscribe(shed.actions.HIDE, subscriberHideRval);
		shed.unsubscribe(shed.actions.SHOW, subscriberShowRval);
		i = 0;
		while (i <= repeat) {
			action = (i % 2) ? shed.actions.SHOW : shed.actions.HIDE;
			shed[action](element);
			i++;
		}
		expect(called).withContext("unsubscribed shed subscribers should not have been called").toBe(repeat);
	});

	it("testSubscribeWithHide", function() {
		_withSubscribeHelper("subscriberDiv1", shed.actions.HIDE);
	});

	it("testSubscribeWithShow", function() {
		_withSubscribeHelper("subscriberDiv1", shed.actions.SHOW);
	});

	it("testSubscribeWithEnable", function() {
		_withSubscribeHelper("subscriberDiv1", shed.actions.ENABLE);
	});

	it("testSubscribeWithDisable", function() {
		_withSubscribeHelper("subscriberDiv1", shed.actions.DISABLE);
	});

	it("testSubscribeWithSelect", function() {
		_withSubscribeHelper("fauxOpt1", shed.actions.SELECT);
	});

	it("testSubscribeWithDeselect", function() {
		_withSubscribeHelper("fauxOpt1", shed.actions.DESELECT);
	});

	it("testIsExpandedFalse", function() {
		expect(shed.isExpanded(document.getElementById("exp1"))).toBeFalse();
	});

	it("testIsExpandedTrue", function() {
		expect(shed.isExpanded(document.getElementById("exp2"))).toBeTrue();
	});

	it("testIsExpandedOpenFalse", function() {
		expect(shed.isExpanded(document.getElementById("details1"))).toBeFalse();
	});


	it("testIsExpandedOpenTrue", function() {
		expect(shed.isExpanded(document.getElementById("details2"))).toBeTrue();
	});

	it("testExpand", function() {
		const element = document.getElementById("exp1");
		expect(shed.isExpanded(element)).toBeFalse();
		shed.expand(element);
		expect(shed.isExpanded(element)).toBeTrue();
	});

	it("testExpandWithOpen", function() {
		const element = document.getElementById("details1");
		expect(shed.isExpanded(element)).toBeFalse();
		shed.expand(element);
		expect(shed.isExpanded(element)).toBeTrue();
	});

	it("testCollapse", function() {
		const element = document.getElementById("exp2");
		expect(shed.isExpanded(element)).toBeTrue();
		shed.collapse(element);
		expect(shed.isExpanded(element)).toBeFalse();
	});

	it("testCollapseWithOpen", function() {
		const element = document.getElementById("details2");
		expect(shed.isExpanded(element)).toBeTrue();
		shed.collapse(element);
		expect(shed.isExpanded(element)).toBeFalse();
	});

	it("testSubscribeWithExpand", function() {
		_withSubscribeHelper("exp1", shed.actions.EXPAND);
	});

	it("testSubscribeWithCollapse", function() {
		_withSubscribeHelper("exp2", shed.actions.COLLAPSE);
	});

	it("testIsMandatoryFalse", function() {
		expect(shed.isMandatory(document.getElementById("inp7"))).toBeFalse();
	});

	it("testIsMandatoryTrue", function() {
		expect(shed.isMandatory(document.getElementById("inp8"))).toBeTrue();
	});

	it("testIsMandatoryTrueNotXML", function() {
		expect(shed.isMandatory(document.getElementById("inp9"))).toBeTrue();
	});

	it("testMandatory", function() {
		const element = document.getElementById("inp7");
		expect(shed.isMandatory(element)).toBeFalse();
		shed.mandatory(element);
		expect(shed.isMandatory(element)).toBeTrue();
	});

	it("testMandatoryOnNonInput", function() {
		const element = document.getElementById("radioButtonGroup2");
		expect(shed.isMandatory(element)).toBeFalse();
		shed.mandatory(element);
		expect(shed.isMandatory(element)).toBeTrue();
	});

	it("testOptional", function() {
		const element = document.getElementById("inp8");
		expect(shed.isMandatory(element)).toBeTrue();
		shed.optional(element);
		expect(shed.isMandatory(element)).toBeFalse();
	});

	it("testOptionalOnNonInput", function() {
		const element = document.getElementById("radioButtonGroup1");
		expect(shed.isMandatory(element)).toBeTrue();
		shed.optional(element);
		expect(shed.isMandatory(element)).toBeFalse();
	});

	it("testSubscribeWithMandatory", function() {
		_withSubscribeHelper("inp7", shed.actions.MANDATORY);
	});

	it("testSubscribeWithOptional", function() {
		_withSubscribeHelper("inp8", shed.actions.OPTIONAL);
	});

	it("testIsSelectableCheckBox", function() {
		expect(shed.isSelectable(document.getElementById("chk1"))).toBeTrue();
	});

	it("testIsSelectableRadio", function() {
		expect(shed.isSelectable(document.getElementById("rad1"))).toBeTrue();
	});

	it("testIsSelectableOption", function() {
		const element = getSelect(document.body, "sel1");
		expect(shed.isSelectable(element.options[0])).toBeTrue();
	});

	it("testIsSelectableAriaCheckBox", function() {
		expect(shed.isSelectable(document.getElementById("fauxChk1"))).toBeTrue();
	});

	it("testIsSelectableAriaRadio", function() {
		expect(shed.isSelectable(document.getElementById("fauxRad1"))).toBeTrue();
	});

	it("testIsSelectableAriaOption", function() {
		expect(shed.isSelectable(document.getElementById("fauxOpt1"))).toBeTrue();
	});

	it("testIsSelectableButton", function() {
		const element = document.body.getElementsByTagName("button");
		expect(element?.length).withContext("Could not find a button to test").toBeGreaterThan(0);
		expect(shed.isSelectable(element[0])).toBeTrue();
	});

	it("testIsSelectableAriaButton", function() {
		expect(shed.isSelectable(document.getElementById("fauxButton1"))).toBeTrue();
	});

	/* could be an infinite number of isNotSelectables... should add more unselectable roled elements */
	it("testIsSelectableDivFalse", function() {
		expect(shed.isSelectable(document.body.appendChild(document.createElement("div")))).toBeFalse();
	});

	it("testIsSelectableLiFalse", function() {
		expect(shed.isSelectable(document.getElementById("justAnLi"))).toBeFalse();
	});

	it("testIsSelectableAriaRadioGroupFalse", function() {
		expect(shed.isSelectable(document.getElementById("radioButtonGroup1"))).toBeFalse();
	});

	it("testHasDisabledAncestor", function() {
		expect(shed.hasDisabledAncestor(document.getElementById("radioButtonGroup3_1"))).toBeTrue();
	});

	it("testHasDisabledAncestorFalse", function() {
		expect(shed.hasDisabledAncestor(document.getElementById("radioButtonGroup4_1"))).toBeFalse();
	});

	it("testIsHiddenByAncestor", function() {
		const test = document.getElementById("visibletests1");
		test.style.width = "3em";
		expect(shed.isHidden(test)).toBeFalse();
	});

	it("testIsHiddenTextNodeChildOfElement", function() {
		const parent = document.createElement("span");
		const child = parent.appendChild(document.createTextNode("tn"));
		expect(shed.isHidden(parent)).toBeFalse();
		// @ts-ignore
		expect(shed.isHidden(child)).toBeFalse();
		shed.hide(parent);
		expect(shed.isHidden(parent)).toBeTrue();
		// @ts-ignore
		expect(shed.isHidden(child)).toBeTrue();
	});

	it("testIsHiddenTextNodeChildOfDocumentFragment", function() {
		const parent = document.createDocumentFragment();
		const child = parent.appendChild(document.createTextNode("tn"));
		// @ts-ignore
		expect(shed.isHidden(parent)).toBeFalse();
		// @ts-ignore
		expect(shed.isHidden(child)).toBeFalse();
	});

	it("testIsHiddenCommentNodeChildOfDocumentFragment", function() {
		const parent = document.createDocumentFragment();
		const child = parent.appendChild(document.createComment("comment"));
		// @ts-ignore
		expect(shed.isHidden(parent)).toBeFalse();
		// @ts-ignore
		expect(shed.isHidden(child)).toBeFalse();
	});

	it("testIsHiddenByAncestorWithDisplay", function() {
		const parent = document.getElementById("visibletests"),
			test = document.getElementById("visibletests1");
		try {
			parent.style.display = "none";
			expect(shed.isHidden(test)).toBeTrue();
		} finally {
			parent.style.display = "";
		}
	});

	it("testIsHiddenByAncestorWithVisibility", function() {
		const parent = document.getElementById("visibletests"),
			test = document.getElementById("visibletests1");
		try {
			parent.style.visibility = "hidden";
			expect(shed.isHidden(test)).toBeTrue();
		} finally {
			parent.style.visibility = "";
		}
	});

	it("testIsHiddenByAncestorWithHidden", function() {
		const parent = document.getElementById("visibletests"),
			test = document.getElementById("visibletests1");
		try {
			parent.setAttribute("hidden", "hidden");
			expect(shed.isHidden(test)).toBeTrue();
		} finally {
			parent.removeAttribute("hidden");
		}
	});

	it("testIsHiddenByAncestorWithShedHide", function() {
		const parent = document.getElementById("visibletests"),
			test = document.getElementById("visibletests1");
		try {
			shed.hide(parent);
			expect(shed.isHidden(test)).toBeTrue();
		} finally {
			shed.show(parent);
		}
	});

	it("testIsHiddenHTMLSyntax", function() {
		const element = document.getElementById("hiddenhtmlsyntax");
		expect(shed.isHidden(element)).toBeTrue();
	});
});
