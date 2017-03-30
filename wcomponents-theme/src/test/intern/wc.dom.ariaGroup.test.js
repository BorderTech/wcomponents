define(["intern!object", "intern/chai!assert", "wc/dom/ariaGroup", "wc/dom/Widget", "./resources/test.utils!"],
	function (registerSuite, assert, controller, Widget, testutils) {
		"use strict";

		var testHolder,
			testContent = "<span role='radiogroup' id='owner-1' aria-owns='owned1 owned2' class='some-group'>owner\
<span role='radio' id='owned1' aria-checked='true'>1</span>\
<span role='radio' id='owned2' aria-checked='false'>2</span>\
<span role='radio' id='owned5' aria-checked='false'>5</span>\
</span>\
<span role='listbox' id='owner-2' aria-owns='owned3 owned4'>owned not nested</span>\
<span role='option' id='owned3' aria-selected='true'>3</span>\
<span role='option' id='owned4' aria-selected='false'>4</span>\
<span role='checkbox' id='notowned' aria-checked='false'>5</span>\
<span role='menu' id='menu1' class='widget-menu'>\
	<span role='menuitem' id='mi0'>item 0</span>\
	<span role='menuitemradio' id='mi1'>item 1</span>\
	<span role='menuitemcheckbox' id='mi2'>item 2</span>\
	<span role='menu' id='submenu'>submenu\
		<span role='menuitemradio' id='mi3'>item 3</span>\
		<span role='menuitemcheckbox' id='mi4'>item 4</span>\
		<span role='menuitem' id='mi5'>item 5</span>\
	</span>\
	<span role='menuitem' id='mi6'>item 6</span>\
</span>\
<span role='radiogroup' id='owner-3' aria-owns='owned5 owned6'>unnested owner\
<span role='radio' id='owned6' aria-checked='false'>6</span>\
</span>",
			allItemIds = ["mi0", "mi1", "mi2", "mi3", "mi4", "mi5", "mi6"],
			outerItemIds = ["mi0", "mi1", "mi2", "mi6"],
			innerItemIds = ["mi3", "mi4", "mi5"];

		registerSuite({
			name: "wc/dom/ariaGroup",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testGetOwner: function() {
				var expected = "owner-1",
					start = document.getElementById("owned1");
				assert.strictEqual(controller.getOwner(start).id, expected, "did not find expected aria- owner");
			},
			testGetOwnerNotNested: function() {
				var expected = "owner-2",
					start = document.getElementById("owned3");
				assert.strictEqual(controller.getOwner(start).id, expected, "did not find expected aria- owner");
			},
			testGetOwnerNotOwned: function() {
				var start = document.getElementById("notowned");
				assert.isNull(controller.getOwner(start), "should not have found owner");
			},
			testGetOwned: function() {
				var start = document.getElementById("owner-1"),
					expected = ["owned1", "owned2"],
					result = controller.getOwned(start), i;
				assert.strictEqual(result.length, 2);
				for (i = 0; i < result.length; ++i) {
					assert.strictEqual(result[i].id, expected[i], "Did not find expected owned element");
				}
			},
			testGetOwnedNotNested: function() {
				var start = document.getElementById("owner-2"),
					expected = ["owned3", "owned4"],
					result = controller.getOwned(start), i;
				for (i = 0; i < result.length; ++i) {
					assert.strictEqual(result[i].id, expected[i], "Did not find expected owned element");
				}
			},
			testGetOwnedNotOwner: function() {
				var start = document.getElementById("notowned");
				assert.strictEqual(controller.getOwned(start).length, 0);
			},
			testGetGroupFromContainer: function() {
				var start = document.getElementById("menu1"),
					expected = outerItemIds,
					group = controller.getGroup(start), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetGroupFromContainerWithRole: function() {
				var start = document.getElementById("menu1"),
					expected = outerItemIds,
					group = controller.getGroup(start, "menu"), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetGroupFromContainerWithIgnoreInner: function() {
				var start = document.getElementById("menu1"),
					expected = allItemIds,
					group = controller.getGroup(start, null, true), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetGroupFromContainerWithRoleIgnoreInner: function() {
				var start = document.getElementById("menu1"),
					expected = allItemIds,
					group = controller.getGroup(start, "menu", true), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetGroupFromMember: function() {
				var start = document.getElementById(allItemIds[0]),
					expected = outerItemIds,
					group = controller.getGroup(start), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetGroupFromMemberIgnoreInner: function() {
				var start = document.getElementById(allItemIds[0]),
					expected = allItemIds,
					group = controller.getGroup(start, "menuitem", true), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetInnerGroupFromMember: function() {
				var start = document.getElementById(innerItemIds[0]),
					expected = innerItemIds,
					group = controller.getGroup(start), i,
					foundIds = [];

				for (i = 0; i < group.length; ++i) {
					foundIds.push(group[i].id);
				}
				assert.strictEqual(group.length, expected.length, foundIds.join(" "));
				for (i = 0; i < group.length; ++i) {
					assert.strictEqual(group[i].id, expected[i]);
				}
			},
			testGetContainer : function() {
				var start = document.getElementById(allItemIds[0]),
					expected = "menu1",
					result = controller.getContainer(start);
				assert.strictEqual(result.id, expected);
			},
			testGetInnerContainer : function() {
				var start = document.getElementById(innerItemIds[0]),
					expected = "submenu",
					result = controller.getContainer(start);
				assert.strictEqual(result.id, expected);
			},
			testGetContainerNestedOwned: function() {
				var start = document.getElementById("owned5"),
					expected = "owner-3",
					result = controller.getContainer(start);
				assert.strictEqual(result.id, expected);
			},
			testGetContainerNestedIgnoreOwned: function() {
				var start = document.getElementById("owned5"),
					expected = "owner-1",
					result = controller.getContainer(start, null, true);
				assert.strictEqual(result.id, expected);
			},
			testGetContainerWithWidget: function() {
				var start = document.getElementById(innerItemIds[0]),
					widget = new Widget("", "widget-menu"),
					expected = "menu1",
					result = controller.getContainer(start, widget);
				assert.strictEqual(result.id, expected);
			},
			testGetContainerWithWidgetAndOwned: function() {
				var start = document.getElementById("owned5"),
					widget = new Widget("", "some-group"),
					expected = "owner-3",
					result = controller.getContainer(start, widget);
				assert.strictEqual(result.id, expected);
			},
			testGetContainerWithWidgetAndIgnoreOwned: function() {
				var start = document.getElementById("owned5"),
					widget = new Widget("", "some-group"),
					expected = "owner-1",
					result = controller.getContainer(start, widget, true);
				assert.strictEqual(result.id, expected);
			}
		});
	}
);

