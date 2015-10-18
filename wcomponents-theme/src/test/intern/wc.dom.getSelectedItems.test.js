define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function(registerSuite, assert, testutils) {
	"use strict";

	var getFilteredGroup, Widget, urlResource = "../../target/test-classes/wcomponents-theme/intern/resources/domUsefulDom.html",
		testHolder;
	registerSuite({
		name: "getFilteredGroup",
		setup: function() {
			var result = new testutils.LamePromisePolyFill();
			testutils.setupHelper(["wc/dom/getFilteredGroup", "wc/dom/Widget"], function(g, W) {
				getFilteredGroup = g;
				Widget = W;
				testHolder = testutils.getTestHolder();
				testutils.setUpExternalHTML(urlResource, testHolder).then(result._resolve);
			});
			return result;
		},
		teardown: function() {
			testHolder.innerHTML = "";
		},

		/**
		 * Get the selected radio elements in a radio group
		 */
		testGetSelectedRadio: function() {
			var element = document.getElementById("radio1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedRadioValue: function() {
			var element = document.getElementById("radio1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].value, "Butter");
		},


		testGetUnselectedItemsRadio: function() {
			var result = getFilteredGroup(document.getElementById("radio1"), {
				filter: getFilteredGroup.FILTERS.deselected
			});
			assert.strictEqual(result.length, 2);
			assert.strictEqual(result[0].value, "Milk");
			assert.strictEqual(result[1].value, "Cheese");
		},

		/**
		 * Get the selected radio elements in a radio group
		 * but none are selected
		 */
		testGetSelectedRadioNoneSelected: function() {
			var element = document.getElementById("radio3"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 0);
		},

		/**
		 * Get the selected radio elements in a radio group
		 * the selected radio is disabled
		 */
		testGetSelectedRadioDisabled: function() {
			var element = document.getElementById("radio4"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedRadioDisabledValue: function() {
			var element = document.getElementById("radio4"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].value, "Wine");
		},

		testGetDisabledRadio: function() {
			var element = document.getElementById("radio4"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.disabled
				});
			assert.strictEqual(result.length, 1);
		},

		testGetEnabledRadio: function() {
			var element = document.getElementById("radio4"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.enabled
				});
			assert.strictEqual(result.length, 2);
		},

		testGetHiddenCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.hidden
				});
			assert.strictEqual(result.length, 2);
			assert.strictEqual(result[0].id, "chkItem3");
			assert.strictEqual(result[1].id, "chkItem6");
		},

		testGetVisibleCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.visible
				});
			assert.strictEqual(result.length, 4);
		},

		testFilterSelectedCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.selected
				});
			assert.strictEqual(result[0].id, "chkItem2");
			assert.strictEqual(result[1].id, "chkItem4");
			assert.strictEqual(result[2].id, "chkItem6");
			assert.strictEqual(result.length, 3);
		},

		testFilterSelectedDisabledCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.selected + getFilteredGroup.FILTERS.disabled
				});
			assert.strictEqual(result[0].id, "chkItem2");
			assert.strictEqual(result[1].id, "chkItem6");
			assert.strictEqual(result.length, 2);
		},

		testFilterSelectedHiddenDisabledCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.selected + getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled
				});
			assert.strictEqual(result[0].id, "chkItem6");
			assert.strictEqual(result.length, 1);
		},

		testFilterHiddenDisabledDeselectedCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled + getFilteredGroup.FILTERS.deselected
				});
			assert.strictEqual(result.length, 0);
		},

		testFilterHiddenDisabledCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.disabled
				});
			assert.strictEqual(result[0].id, "chkItem6");
			assert.strictEqual(result.length, 1);
		},

		testFilterHiddenUncheckedCheckbox: function() {
			var element = document.getElementById("chkItem1"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.hidden + getFilteredGroup.FILTERS.deselected
				});
			assert.strictEqual(result[0].id, "chkItem3");
			assert.strictEqual(result.length, 1);
		},

		/**
		 * Get the selected radio elements in a checkbox group
		 */
		testGetSelectedCheckbox: function() {
			var element = document.getElementById("cb1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 2);
		},
		/**
		 * Get the selected radio elements in a checkbox group
		 */
		testGetSelectedCheckboxValue: function() {

			var element = document.getElementById("cb1"),
				result = getFilteredGroup(element);
			if (result[0].value === "Bike") {
				assert.strictEqual(result[1].value, "Airplane");
			}
			else if (result[0].value === "Airplane") {
				assert.strictEqual(result[1].value, "Bike");
			}
			else {
				assert.fail(null, !null, "getFilteredGroup did not find the selected checkboxes in the group");
			}
		},

		/**
		 * Get the selected options in a select
		 */
		testGetSelectedSelect: function() {

			var element = document.getElementById("select1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 1);
		},
		testGetSelectedSelectValue: function() {

			var element = document.getElementById("select1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].value, "mercedes");
		},

		/**
		 * Get the selected options in a multi select
		 */
		testGetSelectedSelectMulti: function() {

			var element = document.getElementById("select2"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 2);
		},

		/**
		 * Get the selected options in a multi select
		 */
		testGetSelectedSelectMultiValue: function() {

			var element = document.getElementById("select2"),
				result = getFilteredGroup(element);
			if (result[0].value === "volvo") {
				assert.strictEqual(result[1].value, "mercedes");
			}
			else if (result[0].value === "mercedes") {
				assert.strictEqual(result[1].value, "volvo");
			}
			else {
				assert.fail(null, !null, "getFilteredGroup did not find the selected options in the multi-select");
			}
		},

		/**
		 * Get the selected options in a multi select which has optgroups
		 */
		testGetSelectedSelectMultiOptgroup: function() {

			var element = document.getElementById("select3"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 2);
		},
		testGetSelectedSelectMultiOptgroupValue: function() {

			var element = document.getElementById("select3"),
				result = getFilteredGroup(element);
			if (result[0].value === "volvo") {
				assert.strictEqual(result[1].value, "audi");
			}
			else if (result[0].value === "audi") {
				assert.strictEqual(result[1].value, "volvo");
			}
			else {
				assert.fail(null, !null, "getFilteredGroup did not find the selected options in the multi-select");
			}
		},

		/**
		 * Get the selected options in a select by passing in one of the option elements
		 */
		testGetSelectedOption: function() {
			var element = document.getElementById("select1"),
				result = getFilteredGroup(element.options[0]);
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedOptionValue: function() {
			var element = document.getElementById("select1"),
				result = getFilteredGroup(element.options[0]);
			assert.strictEqual(result[0].value, "mercedes");
		},

		testGetSelectedOptionInOptgroup: function() {
			var element = document.getElementById("opt5"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 0);
		},

		/**
		 * Get the selected options in a multi select by passing in one of the option elements
		 */
		testGetSelectedOptionMulti: function() {
			var element = document.getElementById("select2"),
				result = getFilteredGroup(element.options[0]);
			assert.strictEqual(result.length, 2);
		},
		testGetSelectedOptionMultiValue: function() {
			var element = document.getElementById("select2"),
				result = getFilteredGroup(element.options[0]);
			if (result[0].value === "volvo") {
				assert.strictEqual(result[1].value, "mercedes");
			}
			else if (result[0].value === "mercedes") {
				assert.strictEqual(result[1].value, "volvo");
			}
			else {
				assert.fail(null, !null, "getFilteredGroup did not find the selected options in the multi-select");
			}
		},

		/**
		 * Get the selected options in a multi select by passing in one of the option elements
		 * which is contained in an optgroup
		 */
		testGetSelectedOptionMultiOptgroup: function() {
			var element = document.getElementById("select3"),
				result = getFilteredGroup(element.options[0]);
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedOptionMultiOptgroupValue: function() {
			var element = document.getElementById("select3"),
				result = getFilteredGroup(element.options[0]);
			assert.strictEqual(result[0].value, "volvo");
		},

		/**
		 * Get the selected options in a multi select by passing in an optgroup
		 * only the selected options within the optgroup should be returned
		 */
		testGetSelectedOptgroupMulti: function() {
			var element = document.getElementById("select3"),
				result = getFilteredGroup(element.getElementsByTagName("optgroup")[1]);
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedOptgroupMultiValue: function() {
			var element = document.getElementById("select3"),
				result = getFilteredGroup(element.getElementsByTagName("optgroup")[1]);
			assert.strictEqual(result[0].value, "audi");
		},

		/**
		 * Get the selected options in an aria select
		 */
		testGetSelectedAriaSelect: function() {

			var element = document.getElementById("fauxSelect1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 1);
		},


		/**
		 * Get the selected options in an aria multi select
		 */
		testGetSelectedAriaSelectMulti: function() {

			var element = document.getElementById("fauxSelect2"),
				result = getFilteredGroup(element);
			assert.strictEqual(result.length, 5);
		},

		/**
		 * Get the unselected options in an aria multi select
		 */
		testGetUnselectedItemsAriaSelectMulti: function() {

			var element = document.getElementById("fauxSelect2"),
				result = getFilteredGroup(element, {
					filter: getFilteredGroup.FILTERS.deselected
				});
			assert.strictEqual(result.length, 2);
		},

		/**
		 * Get the selected radio elements in an aria radio group by container
		 */
		testGetSelectedRadioByContainer: function() {
			var element = document.getElementById("radGrp1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "fauxRad4");
		},

		/**
		 * Get the selected radio elements in an aria radio group
		 */
		testGetSelectedAriaRadio: function() {
			var element = document.getElementById("fauxRad5"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "fauxRad4");
		},

		testGetSelectedAriaRadioAsObject: function() {
			var element = document.getElementById("fauxRad5"),
				result = getFilteredGroup(element, {
					asObject: true
				});
			assert.strictEqual(result.filtered[0].id, "fauxRad4");
			assert.strictEqual(result.unfiltered.length, 6);
		},

		/**
		 * Get the selected radio elements in an aria radio group by container with aria-owns
		 */
		testGetSelectedRadioByContainerWithAriaOwns: function() {
			var element = document.getElementById("radGrp2"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "rO2");
		},

		/**
		 * Get the selected radio elements in an aria radio group where the container is not
		 * a direct ancestor of the radio buttons
		 */
		testGetSelectedRadioWithAriaOwns: function() {
			var element = document.getElementById("rO3"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "rO2");
		},

		testGetSelectedAriaRadioItemByContainer: function() {
			var element = document.getElementById("menu1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "radItem2");
			assert.strictEqual(result.length, 1);
		},


		testGetSelectedSeededGroupAriaRadioItems: function() {
			var group = [],
				i, result;
			for (i = 1; i <= 6; i++) {
				group.push(document.getElementById("radItem" + i));
				group.push(document.getElementById("radBarItem" + i));
			}
			result = getFilteredGroup(group);
			assert.strictEqual(result.length, 2);
		},

		testGetDisablededSeededGroupAriaRadioItems: function() {
			var group = [],
				i, result;
			for (i = 1; i <= 6; i++) {
				group.push(document.getElementById("radItem" + i));
				group.push(document.getElementById("radBarItem" + i));
			}
			result = getFilteredGroup(group, {
				filter: getFilteredGroup.FILTERS.disabled
			});
			assert.strictEqual(result.length, 0);
		},

		testGetSelectedAriaRadioItem: function() {
			var element = document.getElementById("radItem4"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "radItem2");
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedAriaCheckboxItemByContainer: function() {
			var element = document.getElementById("menubar2"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "chkBarItem2");
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedAriaCheckboxItem: function() {
			var element = document.getElementById("chkBarItem1"),
				result = getFilteredGroup(element);
			assert.strictEqual(result[0].id, "chkBarItem2");
			assert.strictEqual(result.length, 1);
		},

		testGetSelectedFauxCheckboxesByFormWithFilterWd: function() {
			var element = document.getElementById("form2"),
				result = getFilteredGroup(element, {
					itemWd: new Widget("", "", {
						role: "checkbox"
					})
				});
			assert.strictEqual(result.length, 2);
			assert.strictEqual(result[0].id, "form2Chk2");
			assert.strictEqual(result[1].id, "form2Chk4");
		},

		testGetSelectedFauxCheckablesByFormWithFilterWd: function() {
			var element = document.getElementById("form2"),
				result = getFilteredGroup(element, {
					itemWd: new Widget("", "", {
						role: null
					})
				});
			assert.strictEqual(result.length, 3);
			assert.strictEqual(result[0].id, "form2rad2");
			assert.strictEqual(result[1].id, "form2Chk2");
			assert.strictEqual(result[2].id, "form2Chk4");
		},

		testGetSelectedFauxCheckablesByFormWithFilterWdAsObject: function() {
			var element = document.getElementById("form2"),
				result = getFilteredGroup(element, {
					itemWd: new Widget("", "", {
						role: null
					}),
					asObject: true
				});
			assert.strictEqual(result.filtered.length, 3);
			assert.strictEqual(result.unfiltered.length, 12);
		},

		/**
		 * Exception tests are good for line coverage reports...
		 */
		testGetSelectedWithNullElement: function() {
			try {
				getFilteredGroup(null);
				assert.fail(null, !null, "Should have got an exception");
			}
			catch (ex) {
				assert.isTrue(true);
			}
		}
	});

});
