define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function(registerSuite, assert, testutils) {
		"use strict";

		var controller, testHolder,
			urlResource = "@RESOURCES@/domShed.html";

		function _withSubscribeHelper(id, action) {
			var element = document.getElementById(id),
				actionIGot, elementIGot,
				subscriber = function($element, $action) {
					elementIGot = $element;
					actionIGot = $action;
				};
			try {
				controller.subscribe(action, subscriber);
				controller[action](element);
				assert.strictEqual(elementIGot, element);
				assert.strictEqual(actionIGot, action);
			}
			finally {
				controller._unsubscribe(action, subscriber);
			}
		}

		registerSuite({
			name: "Shed",
			setup: function() {
				var result = testutils.setupHelper(["wc/dom/shed"]).then(function(arr) {
					controller = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;

			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testIsDisabledInput: function() {
				assert.isTrue(controller.isDisabled(document.getElementById("inp1")));
			},
			testIsDisabledInputNotXML: function() {
				testHolder.insertAdjacentHTML("afterBegin", "<input id='inp2' disabled>");
				assert.isTrue(controller.isDisabled(document.getElementById("inp2")));
			},
			testIsNotDisabledInput: function() {
				assert.isFalse(controller.isDisabled(document.getElementById("inp3")));
			},
			testIsDisabledAnchor: function() {
				assert.isTrue(controller.isDisabled(document.getElementById("anchor2")));
			},
			testIsNotDisabledAnchor: function() {
				assert.isFalse(controller.isDisabled(document.getElementById("anchor1")));
			},
			testIsNotDisabledSelect: function() {
				assert.isFalse(controller.isDisabled(document.getElementById("select1")));
			},
			testIsDisabledSelect: function() {
				assert.isTrue(controller.isDisabled(document.getElementById("select2")));
			},
			testIsDisabledElementNotDisableable: function() {
				assert.isFalse(controller.isDisabled(document.getElementById("subscriberDiv1")));
			},
			testIsDisabledElementNotNativelyDisableableWithRole: function() {
				assert.isTrue(controller.isDisabled(document.getElementById("fauxChk4")));
			},
			testDisableInput: function() {
				var element = document.getElementById("inp3");
				assert.isFalse(controller.isDisabled(element), "element not should be disabled");
				controller.disable(element);
				assert.isTrue(controller.isDisabled(element), "element should be disabled");
			},
			testDisableSelect: function() {
				var element = document.getElementById("select1");
				assert.isFalse(controller.isDisabled(element), "element not should be disabled");
				controller.disable(element);
				assert.isTrue(controller.isDisabled(element), "element should be disabled");
			},
			testDisableElementNotDisableable: function() {
				var element = document.getElementById("subscriberDiv1");
				assert.isFalse(controller.isDisabled(element));
				controller.disable(element);
				assert.isFalse(controller.isDisabled(element));
			},
			testDisableElementNotNativelyDisableableWithRole: function() {
				var element = document.getElementById("fauxChk1");
				assert.isFalse(controller.isDisabled(element));
				controller.disable(element);
				assert.isTrue(controller.isDisabled(element));
			},
			testEnableInput: function() {
				var element = document.getElementById("inp1");
				assert.isTrue(controller.isDisabled(element), "element should be disabled");
				controller.enable(element);
				assert.isFalse(controller.isDisabled(element), "element should not be disabled");
			},
			testEnableElementNotNativelyDisableableWithRole: function() {
				var element = document.getElementById("fauxChk4");
				assert.isTrue(controller.isDisabled(element), "element should be disabled");
				controller.enable(element);
				assert.isFalse(controller.isDisabled(element));
			},
			testIsSelectedToggleButton: function() {
				assert.isTrue(controller.isSelected(document.getElementById("togglebtn2")), "Button should be selected");
			},
			testIsSelectedToggleButtonFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("togglebtn1")), "Button should not be selected");
			},
			testIsSelectedNativeButtonFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("btn1")), "Button should not be selected");
			},
			testIsSelectedWithChkBoxFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("chk1")));
			},
			testIsSelectedWithChkBoxTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("chk2")));
			},
			testIsSelectedWithRadioFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("rad1")));
			},
			testIsSelectedWithRadioTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("rad2")));
			},
			testIsSelectedWithOptionFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("sel1").options[0]));
			},
			testIsSelectedWithOptionTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("sel1").options[1]));
			},
			testIsSelectedWithFauxChkBoxFalse: function() {
				var element = document.getElementById("fauxChk1");
				assert.isFalse(controller.isSelected(element));
				assert.strictEqual(controller.state.DESELECTED, controller.isSelected(element));
			},
			testStateDESELECTED: function() {
				assert.strictEqual(controller.state.DESELECTED, controller.isSelected(document.getElementById("fauxChk1")));
			},
			testIsSelectedWithFauxChkBoxMixed: function() {
				var element = document.getElementById("fauxChk3");
				assert.isFalse(!!controller.isSelected(element));
			},
			testStateMIXED: function() {
				assert.strictEqual(controller.state.MIXED, controller.isSelected(document.getElementById("fauxChk3")));
			},
			testIsSelectedWithFauxChkBoxTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("fauxChk2")));
			},
			testStateSELECTED: function() {
				assert.strictEqual(controller.state.SELECTED, controller.isSelected(document.getElementById("fauxChk2")));
			},
			testIsSelectedWithFauxRadioFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("fauxRad1")));
			},
			testIsSelectedWithFauxRadioTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("fauxRad2")));
			},
			testIsSelectedWithNoRole: function() {
				assert.isFalse(controller.isSelected(document.getElementById("justAnLi")));
			},
			testIsSelectedWithUnsupportedRole: function() {
				assert.isFalse(controller.isSelected(document.getElementById("presLi")));
			},
			testIsSelectedWithFauxOptionFalse: function() {
				assert.isFalse(controller.isSelected(document.getElementById("fauxOpt1")));
			},
			testIsSelectedWithFauxOptionTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("fauxOpt2")));
			},
			testIsSelectedUnpreferredWithFauxOptionTrue: function() {
				assert.isTrue(controller.isSelected(document.getElementById("fauxOpt3")));
			},
			testSelectToggleButton: function() {
				var element = document.getElementById("togglebtn1");
				assert.isFalse(controller.isSelected(element), "Button should not be selected");
				controller.select(element);
				assert.isTrue(controller.isSelected(element), "Button should be selected");
			},
			testSelectNativeButton: function() {
				var element = document.getElementById("btn1");
				assert.isFalse(controller.isSelected(element), "Button should not be selected");
				controller.select(element);
				assert.isTrue(controller.isSelected(element), "Button should be selected");
			},
			testSelectChkBox: function() {
				var element = document.getElementById("chk1");
				assert.isFalse(controller.isSelected(element), "check box should not be selected");
				controller.select(element);
				assert.isTrue(controller.isSelected(element), "check box should be selected");
			},
			testSelectRadio: function() {
				var element = document.getElementById("rad1");
				assert.isFalse(controller.isSelected(element), "radio should not be selected");
				controller.select(element);
				assert.isTrue(controller.isSelected(element));
			},
			testSelectOption: function() {
				var element = document.getElementById("sel1").options[0];
				assert.isFalse(controller.isSelected(element));
				controller.select(element);
				assert.isTrue(controller.isSelected(element));
			},
			testSelectFauxChkBox: function() {
				var element = document.getElementById("fauxChk1");
				assert.isFalse(controller.isSelected(element));
				controller.select(element);
				assert.isTrue(controller.isSelected(element));
			},
			testSelectFauxRadio: function() {
				var element = document.getElementById("fauxRad1");
				if (controller.isSelected(element)) {
					// order of tests should not be important
					controller.deselect(element);
				}
				assert.isFalse(controller.isSelected(element));
				controller.select(element);
				assert.isTrue(controller.isSelected(element));
			},
			testSelectFauxOption: function() {
				var element = document.getElementById("fauxOpt1");
				assert.isFalse(controller.isSelected(element));
				controller.select(element);
				assert.isTrue(controller.isSelected(element));
			},
			testDeselectToggleButton: function() {
				var element = document.getElementById("togglebtn2");
				assert.isTrue(controller.isSelected(element), "Button should be selected");
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element), "Button should not be selected");
			},
			testDeselectChkBox: function() {
				var element = document.getElementById("chk2");
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectRadio: function() {
				var element = document.getElementById("rad2");
				if (!controller.isSelected(element)) {
					controller.select(element, true);
				}
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectUnpreferredFauxOption: function() {
				var element = document.getElementById("fauxOpt4");
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectOption: function() {
				var element = document.getElementById("sel1").options[1];
				if (!controller.isSelected(element)) {
					controller.select(element, true);
				}
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectFauxChkBox: function() {
				var element = document.getElementById("fauxChk2");
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectFauxRadio: function() {
				var element = document.getElementById("fauxRad2");
				// race condition: depends on order of tests
				// assert.isTrue(controller.isSelected(element));
				if (!controller.isSelected(element)) {
					controller.select(element);
				}
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testDeselectFauxOption: function() {
				var element = document.getElementById("fauxOpt2");
				assert.isTrue(controller.isSelected(element));
				controller.deselect(element);
				assert.isFalse(controller.isSelected(element));
			},
			testMixChkBox: function() {
				var element = document.getElementById("chk1");
				assert.notStrictEqual(controller.isSelected(element), controller.state.MIXED);
				controller.mix(element);
				assert.strictEqual(controller.isSelected(element), controller.state.MIXED);
			},
			testMixFauxChkBox: function() {
				var element = document.getElementById("fauxChk1");
				assert.notStrictEqual(controller.isSelected(element), controller.state.MIXED);
				controller.mix(element);
				assert.strictEqual(controller.isSelected(element), controller.state.MIXED);
			},
			testMixFauxChkBoxWasSelected: function() {
				var element = document.getElementById("fauxChk2");
				assert.notStrictEqual(controller.isSelected(element), controller.state.MIXED);
				controller.mix(element);
				assert.strictEqual(controller.isSelected(element), controller.state.MIXED);
			},
			testToggleSelected: function() {
				var element = document.getElementById("togglebtn1"),
					selected = controller.isSelected(element);
				controller.toggle(element, controller.actions.SELECT);
				assert.notStrictEqual(controller.isSelected(element), selected);
			},
			testToggleDisabled: function() {
				var element = document.getElementById("togglebtn1"),
					disabled = controller.isDisabled(element);
				controller.toggle(element, controller.actions.DISABLE);
				assert.notStrictEqual(controller.isDisabled(element), disabled);
			},
			testToggleHidden: function() {
				var element = document.getElementById("togglebtn1"),
					hidden = controller.isHidden(element);
				controller.toggle(element, controller.actions.HIDE);
				assert.notStrictEqual(controller.isHidden(element), hidden);
			},
			testIsHiddenFalse: function() {
				assert.isFalse(controller.isHidden(document.getElementById("hide1")));
			},
			testIsHiddenTrue: function() {
				assert.isTrue(controller.isHidden(document.getElementById("hide2")));
			},
			testHide: function() {
				var element = document.getElementById("hide1");
				assert.isFalse(controller.isHidden(element));
				controller.hide(element);
				assert.isTrue(controller.isHidden(element));
			},
			testShow: function() {
				var element = document.getElementById("hide2");
				assert.isTrue(controller.isHidden(element));
				controller.show(element);
				assert.isFalse(controller.isHidden(element));
			},
			testSubscribe: function() {
				var subscriberHideRval,
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
					subscriberHideRval = controller.subscribe(controller.actions.HIDE, subscriber);
					subscriberShowRval = controller.subscribe(controller.actions.SHOW, subscriber);
					while (i < repeat) {
						action = (i % 2) ? controller.actions.SHOW : controller.actions.HIDE;
						controller[action](element);
						i++;
					}
					assert.strictEqual(called, repeat);
				}
				finally {  // clean up subscribers
					controller._unsubscribe(controller.actions.HIDE, subscriberHideRval);
					controller._unsubscribe(controller.actions.SHOW, subscriberShowRval);
				}
			},
			testUnsubscribe: function() {
				var subscriberHideRval,
					subscriberShowRval,
					called = 0,
					repeat = 4,
					action,
					i = 0,
					element = document.getElementById("subscriberDiv1"),
					subscriber = function() {
						called++;
					};
				subscriberHideRval = controller.subscribe(controller.actions.HIDE, subscriber);
				subscriberShowRval = controller.subscribe(controller.actions.SHOW, subscriber);
				while (i < repeat) {
					action = (i % 2) ? controller.actions.SHOW : controller.actions.HIDE;
					controller[action](element);
					i++;
				}
				controller._unsubscribe(controller.actions.HIDE, subscriberHideRval);
				controller._unsubscribe(controller.actions.SHOW, subscriberShowRval);
				i = 0;
				while (i <= repeat) {
					action = (i % 2) ? controller.actions.SHOW : controller.actions.HIDE;
					controller[action](element);
					i++;
				}
				assert.strictEqual(called, repeat, "unsubscribed shed subscribers should not have been called");
			},
			testSubscribeWithHide: function() {
				_withSubscribeHelper("subscriberDiv1", controller.actions.HIDE);
			},
			testSubscribeWithShow: function() {
				_withSubscribeHelper("subscriberDiv1", controller.actions.SHOW);
			},
			testSubscribeWithEnable: function() {
				_withSubscribeHelper("subscriberDiv1", controller.actions.ENABLE);
			},
			testSubscribeWithDisable: function() {
				_withSubscribeHelper("subscriberDiv1", controller.actions.DISABLE);
			},
			testSubscribeWithSelect: function() {
				_withSubscribeHelper("fauxOpt1", controller.actions.SELECT);
			},
			testSubscribeWithDeselect: function() {
				_withSubscribeHelper("fauxOpt1", controller.actions.DESELECT);
			},
			testIsExpandedFalse: function() {
				assert.isFalse(controller.isExpanded(document.getElementById("exp1")));
			},
			testIsExpandedTrue: function() {
				assert.isTrue(controller.isExpanded(document.getElementById("exp2")));
			},
			testIsExpandedOpenFalse: function() {
				assert.isFalse(controller.isExpanded(document.getElementById("details1")));
			},

			testIsExpandedOpenTrue: function() {
				assert.isTrue(controller.isExpanded(document.getElementById("details2")));
			},
			testExpand: function() {
				var element = document.getElementById("exp1");
				assert.isFalse(controller.isExpanded(element));
				controller.expand(element);
				assert.isTrue(controller.isExpanded(element));
			},
			testExpandWithOpen: function() {
				var element = document.getElementById("details1");
				assert.isFalse(controller.isExpanded(element));
				controller.expand(element);
				assert.isTrue(controller.isExpanded(element));
			},
			testCollapse: function() {
				var element = document.getElementById("exp2");
				assert.isTrue(controller.isExpanded(element));
				controller.collapse(element);
				assert.isFalse(controller.isExpanded(element));
			},
			testCollapseWithOpen: function() {
				var element = document.getElementById("details2");
				assert.isTrue(controller.isExpanded(element));
				controller.collapse(element);
				assert.isFalse(controller.isExpanded(element));
			},
			testSubscribeWithExpand: function() {
				_withSubscribeHelper("exp1", controller.actions.EXPAND);
			},
			testSubscribeWithCollapse: function() {
				_withSubscribeHelper("exp2", controller.actions.COLLAPSE);
			},
			testIsMandatoryFalse: function() {
				assert.isFalse(controller.isMandatory(document.getElementById("inp7")));
			},
			testIsMandatoryTrue: function() {
				assert.isTrue(controller.isMandatory(document.getElementById("inp8")));
			},
			testMandatory: function() {
				var element = document.getElementById("inp7");
				assert.isFalse(controller.isMandatory(element));
				controller.mandatory(element);
				assert.isTrue(controller.isMandatory(element));
			},
			testMandatoryOnNonInput: function() {
				var element = document.getElementById("radioButtonGroup2");
				assert.isFalse(controller.isMandatory(element));
				controller.mandatory(element);
				assert.isTrue(controller.isMandatory(element));
			},
			testOptional: function() {
				var element = document.getElementById("inp8");
				assert.isTrue(controller.isMandatory(element));
				controller.optional(element);
				assert.isFalse(controller.isMandatory(element));
			},
			testOptionalOnNonInput: function() {
				var element = document.getElementById("radioButtonGroup1");
				assert.isTrue(controller.isMandatory(element));
				controller.optional(element);
				assert.isFalse(controller.isMandatory(element));
			},
			testSubscribeWithMandatory: function() {
				_withSubscribeHelper("inp7", controller.actions.MANDATORY);
			},
			testSubscribeWithOptional: function() {
				_withSubscribeHelper("inp8", controller.actions.OPTIONAL);
			},
			testIsSelectableCheckBox: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("chk1")));
			},
			testIsSelectableRadio: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("rad1")));
			},
			testIsSelectableOption: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("sel1").options[0]));
			},
			testIsSelectableAriaCheckBox: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("fauxChk1")));
			},
			testIsSelectableAriaRadio: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("fauxRad1")));
			},
			testIsSelectableAriaOption: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("fauxOpt1")));
			},
			testIsSelectableButton: function() {
				var element = testHolder.getElementsByTagName("button");
				if (!element && element.length) {
					assert.fail(element, !null, "Could not find a button to test");
				}
				assert.isTrue(controller.isSelectable(element[0]));
			},
			testIsSelectableAriaButton: function() {
				assert.isTrue(controller.isSelectable(document.getElementById("fauxButton1")));
			},
			/* could be an infinite number of isNotSelectables... should add more unselectable roled elements */
			testIsSelectableDivFalse: function() {
				assert.isFalse(controller.isSelectable(testHolder));
			},
			testIsSelectableLiFalse: function() {
				assert.isFalse(controller.isSelectable(document.getElementById("justAnLi")));
			},
			testIsSelectableAriaRadioGroupFalse: function() {
				assert.isFalse(controller.isSelectable(document.getElementById("radioButtonGroup1")));
			},
			testHasDisabledAncestor: function() {
				assert.isTrue(controller.hasDisabledAncestor(document.getElementById("radioButtonGroup3_1")));
			},
			testHasDisabledAncestorFalse: function() {
				assert.isFalse(controller.hasDisabledAncestor(document.getElementById("radioButtonGroup4_1")));
			},
			testIsHiddenByAncestor: function() {
				var test = document.getElementById("visibletests1");
				assert.isFalse(controller.isHidden(test));
			},
			testIsHiddenByAncestorWithDisplay: function() {
				var parent = document.getElementById("visibletests"),
					test = document.getElementById("visibletests1");
				try {
					parent.style.display = "none";
					assert.isTrue(controller.isHidden(test));
				}
				finally {
					parent.style.display = "";
				}
			},
			testIsHiddenByAncestorWithVisibility: function() {
				var parent = document.getElementById("visibletests"),
					test = document.getElementById("visibletests1");
				try {
					parent.style.visibility = "hidden";
					assert.isTrue(controller.isHidden(test));
				}
				finally {
					parent.style.visibility = "";
				}
			},
			testIsHiddenByAncestorWithHidden: function() {
				var parent = document.getElementById("visibletests"),
					test = document.getElementById("visibletests1");
				try {
					parent.setAttribute("hidden", "hidden");
					assert.isTrue(controller.isHidden(test));
				}
				finally {
					parent.removeAttribute("hidden");
				}
			},
			testIsHiddenByAncestorWithShedHide: function() {
				var parent = document.getElementById("visibletests"),
					test = document.getElementById("visibletests1");
				try {
					controller.hide(parent);
					assert.isTrue(controller.isHidden(test));
				}
				finally {
					controller.show(parent);
				}
			},
			testIsHiddenHTMLSyntax: function() {
				var element = document.getElementById("hiddenhtmlsyntax");
				assert.isTrue(controller.isHidden(element));
			}
		});
	});
