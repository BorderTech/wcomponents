define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var $group, Widget, urlResource = "@RESOURCES@/domUsefulDom.html",
		testHolder;

	/**
	 * @param {string} id the ID of the element to use as the reference element (to pass to group.get)
	 * @param {string|null} prefix the prefix of ids (if there is a naming convention) or null
	 * @param {number|array} offset if the expected ids do not start at 0 give the offset here
	 * OR an array of IDs we expect to find.
	 * @param {number} expected The number of items we expect to find in this group
	 */
	function helper(id, prefix, offset, expected) {
		var i, element = document.getElementById(id),
			usePrefix = !!prefix,
			group = $group.get(element),
			innerExpected;
		if (!group) {
			assert.fail(null, !null, "could not get group");
		}
		else {
			expected = expected || offset.length;
			assert.strictEqual(group.length, expected, "did not find expected group length");
			for (i = 0; i < group.length; i++) {
				if (usePrefix) {
					innerExpected = prefix + (i + offset);
					assert.strictEqual(group[i].id, innerExpected, "Did not get expected element id: " + innerExpected);
				}
				else {
					innerExpected = group[i].id;
					assert.isTrue(offset.indexOf(innerExpected) >= 0, "Did not find element id (" + innerExpected + ") in group.");
				}
			}
		}
		return group;
	}

	function getContainerHelper(id, expected) {
		var htmlElement = document.getElementById(id),
			container = $group.getContainer(htmlElement) || null,
			result = container ? container.id : null;
		assert.strictEqual(result, expected, "Did not find expectedContainer");
	}

	registerSuite({
		name: "getFilteredGroup",
		setup: function() {
			var result = testutils.setupHelper(["wc/dom/group", "wc/dom/Widget"]).then(function(arr) {
				$group = arr[0];
				Widget = arr[1];
				testHolder = testutils.getTestHolder();
				return testutils.setUpExternalHTML(urlResource, testHolder);
			});
			return result;
		},
		teardown: function() {
			testHolder.innerHTML = "";
		},


		testGetWithAriaRadio: function() {
			helper("fauxRad5", "fauxRad", 3, 6);
		},

		testGetWithAriaRadioWithAriaOwns: function() {
			helper("rO3", "rO", 1, 6);
		},

		testGetWithAriaOption: function() {
			helper("fauxOpt7", "fauxOpt", 1, 7);
		},

		testGetWithNativeCheckBoxByName: function() {
			helper("chk3", "chk", 1, 3);
		},

		testGetWithNativeCheckBoxByNameInFieldSet: function() {
			helper("chk7", "chk", 7, 2);
		},

		testGetWithNativeSelect: function() {
			helper("select1", "opt", 1, 8);
		},

		testGetWithNativeOption: function() {
			helper("opt1", "opt", 1, 8);
		},

		testGetWithNativeOptGroup: function() {
			helper("optgrp1", "opt", 5, 4);
		},

		testGetWithNativeOptionInOptGroup: function() {
			helper("opt5", "opt", 5, 4);
		},

		testGetWithAriaRadioItem: function() {
			helper("radItem6", "radItem", 1, 6);
		},

		testGetWithAriaRadioItemInBar: function() {
			helper("radBarItem5", "radBarItem", 1, 6);
		},

		testGetWithAriaCheckboxItem: function() {
			helper("chkItem6", "chkItem", 1, 6);
		},

		/* In a tree the group is the tree, not the nested groups */
		// ["fruits","oranges","pinapples","apples","macintosh","granny_smith","Washington","Michigan","New_York","fuji","bananas","pears","vegetables","broccoli","carrots","lettuce","lettuce1","lettuce2","lettuce3","spinach","squash","acorn","ambercup","autumn_cup","hubbard"]
		testGetWithAriaTreeItemTerminalGrp: function() {
			helper("lettuce2", null, ["fruits", "oranges", "pinapples", "apples", "macintosh", "granny_smith", "Washington", "Michigan", "New_York", "fuji", "bananas", "pears", "vegetables", "broccoli", "carrots", "lettuce", "lettuce1", "lettuce2", "lettuce3", "spinach", "squash", "acorn", "ambercup", "autumn_cup", "hubbard"], 25);
		},

		testGetWithAriaTreeItemContainerGrp: function() {
			helper("lettuce", null, ["fruits", "oranges", "pinapples", "apples", "macintosh", "granny_smith", "Washington", "Michigan", "New_York", "fuji", "bananas", "pears", "vegetables", "broccoli", "carrots", "lettuce", "lettuce1", "lettuce2", "lettuce3", "spinach", "squash", "acorn", "ambercup", "autumn_cup", "hubbard"]);
		},

		testGetWithAriaCheckboxItemInBar: function() {
			helper("chkBarItem5", "chkBarItem", 1, 6);
		},

		testGetContainerWithCheckBoxGroupedByName: function() {
			getContainerHelper("chk7", null);
		},

		testGetContainerOwned: function() {
			getContainerHelper("rO1", "radGrp2");
		},

		testGetContainerRadioAnalog: function() {
			getContainerHelper("fauxRad3", "radGrp1");
		},

		testGetContainerOptionAnalog: function() {
			getContainerHelper("fauxOpt1", "fauxSelect1");
		},

		testGetContainerMenuItemRadio: function() {
			getContainerHelper("radItem1", "menu1");
		},

		testGetContainerMenuItemCheckbox: function() {
			getContainerHelper("chkItem1", "menu2");
		},

		testGetContainerTab: function() {
			getContainerHelper("tab_1", "tablist_1");
		},

		testGetContainerNativeOption: function() {
			getContainerHelper("opt1", "select1");
		},

		testGetContainerNativeOptionInOptGroup: function() {
			getContainerHelper("opt5", "optgrp1");
		},

		testGetContainerFauxCheckboxNoGroup: function() {
			getContainerHelper("fauxChk1", null);
		},

		testGetWithTbody: function() {
			helper("wctbody1", "wctr", 1, 2);
		},

		testGetGroupAriaGroup: function() {
			var WD, id = "fauxSelect1",
				expected = 7,
				element = document.getElementById(id),
				result;
			WD = new Widget("", "", {
				"role": "option"
			});
			result = $group.getGroup(element, WD);
			assert.strictEqual(result.length, expected, "did not find expected group using getGroup");
		}
	});
});
