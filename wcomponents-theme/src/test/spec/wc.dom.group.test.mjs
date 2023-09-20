import $group from "wc/dom/group.mjs";
import {JSDOM} from "jsdom";
import {getResoucePath} from "../helpers/specUtils.mjs";

describe("getFilteredGroup", () => {
	let testHolder;

	/**
	 * @param {string} id the ID of the element to use as the reference element (to pass to group.get)
	 * @param {string|null} prefix the prefix of ids (if there is a naming convention) or null
	 * @param {number|array} offset if the expected ids do not start at 0 give the offset here
	 * OR an array of IDs we expect to find.
	 * @param {number} [expected] The number of items we expect to find in this group
	 */
	function helper(id, prefix, offset, expected=0) {
		const element = testHolder.ownerDocument.getElementById(id),
			usePrefix = !!prefix,
			group = $group.get(element);

		expect(group).withContext("could not get group").toBeTruthy();
		let $expected, offsetNum;
		if (Array.isArray(offset)) {
			$expected = offset.length;
			offsetNum = 0;
		} else {
			$expected = expected;
			offsetNum = /** @type {number} */(offset);
		}
		expect(group.length).withContext("did not find expected group length").toBe($expected);
		for (let i = 0; i < group.length; i++) {
			if (usePrefix) {
				let innerExpected = prefix + (i + offsetNum);
				expect(group[i].id).withContext("Did not get expected element id: " + innerExpected).toBe(innerExpected);
			} else {
				let innerExpected = group[i].id;
				expect(offset).withContext("Did not find element id (" + innerExpected + ") in group.").toContain(innerExpected);
			}
		}
		return group;
	}

	function getContainerHelper(id, expected) {
		const htmlElement = testHolder.ownerDocument.getElementById(id),
			container = $group.getContainer(htmlElement) || null,
			result = container ? container.id : null;
		expect(result).withContext("Did not find expectedContainer").toBe(expected);
	}


	beforeAll(() => {
		return JSDOM.fromFile(getResoucePath("domUsefulDom.html", false)).then((dom) => {
			testHolder = dom.window.document.body;
		});
	});

	it("testGetWithAriaRadio", function() {
		helper("fauxRad5", "fauxRad", 3, 6);
	});


	it("testGetWithAriaRadioWithAriaOwns", function() {
		helper("rO3", "rO", 1, 6);
	});


	it("testGetWithAriaOption", function() {
		helper("fauxOpt7", "fauxOpt", 1, 7);
	});

	it("testGetWithNativeCheckBoxByName", function() {
		helper("chk3", "chk", 1, 3);
	});

	it("testGetWithNativeCheckBoxByNameInFieldSet", function() {
		helper("chk7", "chk", 7, 2);
	});

	it("testGetWithNativeSelect", function() {
		helper("select1", "opt", 1, 8);
	});

	it("testGetWithNativeOption", function() {
		helper("opt1", "opt", 1, 8);
	});

	it("testGetWithNativeOptGroup", function() {
		helper("optgrp1", "opt", 5, 4);
	});

	it("testGetWithNativeOptionInOptGroup", function() {
		helper("opt5", "opt", 5, 4);
	});

	it("testGetWithAriaRadioItem", function() {
		helper("radItem6", "radItem", 1, 6);
	});

	it("testGetWithAriaRadioItemInBar", function() {
		helper("radBarItem5", "radBarItem", 1, 6);
	});

	it("testGetWithAriaCheckboxItem", function() {
		helper("chkItem6", "chkItem", 1, 6);
	});

	/* In a tree the group is the tree, not the nested groups */
	// ["fruits","oranges","pinapples","apples","macintosh","granny_smith","Washington","Michigan","New_York","fuji","bananas","pears","vegetables","broccoli","carrots","lettuce","lettuce1","lettuce2","lettuce3","spinach","squash","acorn","ambercup","autumn_cup","hubbard"]
	it("testGetWithAriaTreeItemTerminalGrp", function() {
		helper("lettuce2", null, ["fruits", "oranges", "pinapples", "apples", "macintosh", "granny_smith", "Washington", "Michigan", "New_York", "fuji", "bananas", "pears", "vegetables", "broccoli", "carrots", "lettuce", "lettuce1", "lettuce2", "lettuce3", "spinach", "squash", "acorn", "ambercup", "autumn_cup", "hubbard"], 25);
	});

	it("testGetWithAriaTreeItemContainerGrp", function() {
		helper("lettuce", null, ["fruits", "oranges", "pinapples", "apples", "macintosh", "granny_smith", "Washington", "Michigan", "New_York", "fuji", "bananas", "pears", "vegetables", "broccoli", "carrots", "lettuce", "lettuce1", "lettuce2", "lettuce3", "spinach", "squash", "acorn", "ambercup", "autumn_cup", "hubbard"]);
	});

	it("testGetWithAriaCheckboxItemInBar", function() {
		helper("chkBarItem5", "chkBarItem", 1, 6);
	});

	it("testGetContainerWithCheckBoxGroupedByName", function() {
		getContainerHelper("chk7", null);
	});

	it("testGetContainerOwned", function() {
		getContainerHelper("rO1", "radGrp2");
	});

	it("testGetContainerRadioAnalog", function() {
		getContainerHelper("fauxRad3", "radGrp1");
	});

	it("testGetContainerOptionAnalog", function() {
		getContainerHelper("fauxOpt1", "fauxSelect1");
	});

	it("testGetContainerMenuItemRadio", function() {
		getContainerHelper("radItem1", "menu1");
	});

	it("testGetContainerMenuItemCheckbox", function() {
		getContainerHelper("chkItem1", "menu2");
	});

	it("testGetContainerTab", function() {
		getContainerHelper("tab_1", "tablist_1");
	});

	it("testGetContainerNativeOption", function() {
		getContainerHelper("opt1", "select1");
	});

	it("testGetContainerNativeOptionInOptGroup", function() {
		getContainerHelper("opt5", "optgrp1");
	});

	it("testGetContainerFauxCheckboxNoGroup", function() {
		getContainerHelper("fauxChk1", null);
	});

	it("testGetWithTbody", function() {
		helper("wctbody1", "wctr", 1, 2);
	});

	it("testGetGroupAriaGroup", function() {
		const id = "fauxSelect1",
			expected = 7,
			element = testHolder.ownerDocument.getElementById(id),
			WD = "[role='option']",
			result = $group.getGroup(element, WD);
		expect(result.length).withContext("did not find expected group using getGroup").toBe(expected);
	});
});
