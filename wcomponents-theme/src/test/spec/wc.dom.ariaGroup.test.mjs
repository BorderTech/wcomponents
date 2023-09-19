import ariaGroup from "wc/dom/ariaGroup.mjs";

describe("wc/dom/ariaGroup", () => {
	const testContent = `
		<span role='radiogroup' id='owner-1' aria-owns='owned1 owned2' class='some-group'>owner
			<span role='radio' id='owned1' aria-checked='true'>1</span>
			<span role='radio' id='owned2' aria-checked='false'>2</span>
			<span role='radio' id='owned5' aria-checked='false'>5</span>
			</span>
			<span role='listbox' id='owner-2' aria-owns='owned3 owned4'>owned not nested</span>
			<span role='option' id='owned3' aria-selected='true'>3</span>
			<span role='option' id='owned4' aria-selected='false'>4</span>
			<span role='checkbox' id='notowned' aria-checked='false'>5</span>
			<span role='menu' id='menu1' class='widget-menu'>
				<span role='menuitem' id='mi0'>item 0</span>
				<span role='menuitemradio' id='mi1'>item 1</span>
				<span role='menuitemcheckbox' id='mi2'>item 2</span>
				<span role='menu' id='submenu'>submenu
					<span role='menuitemradio' id='mi3'>item 3</span>
					<span role='menuitemcheckbox' id='mi4'>item 4</span>
					<span role='menuitem' id='mi5'>item 5</span>
				</span>
				<span role='menuitem' id='mi6'>item 6</span>
			</span>
			<span role='radiogroup' id='owner-3' aria-owns='owned5 owned6'>unnested owner
			<span role='radio' id='owned6' aria-checked='false'>6</span>
		</span>`,
		allItemIds = ["mi0", "mi1", "mi2", "mi3", "mi4", "mi5", "mi6"],
		outerItemIds = ["mi0", "mi1", "mi2", "mi6"],
		innerItemIds = ["mi3", "mi4", "mi5"];

	beforeEach(function() {
		document.body.innerHTML = testContent;
	});
	
	afterEach(function() {
		document.body.innerHTML = "";
	});
	
	it("testGetOwner", function() {
		const expected = "owner-1",
			start = document.getElementById("owned1");
		expect(ariaGroup.getOwner(start).id).withContext("did not find expected aria- owner").toBe(expected);
	});

	it("testGetOwnerNotNested", function() {
		const expected = "owner-2",
			start = document.getElementById("owned3");
		expect(ariaGroup.getOwner(start).id).withContext("did not find expected aria- owner").toBe(expected);
	});

	it("testGetOwnerNotOwned", function() {
		const start = document.getElementById("notowned");
		expect(ariaGroup.getOwner(start)).withContext("should not have found owner").toBeNull();
	});

	it("testGetOwned", function() {
		const start = document.getElementById("owner-1"),
			expected = ["owned1", "owned2"],
			result = ariaGroup.getOwned(start);
		expect(result.length).toBe(2);
		for (let i = 0; i < result.length; ++i) {
			expect(result[i].id).withContext("Did not find expected owned element").toBe(expected[i]);
		}
	});

	it("testGetOwnedNotNested", function() {
		const start = document.getElementById("owner-2"),
			expected = ["owned3", "owned4"],
			result = ariaGroup.getOwned(start);
		for (let i = 0; i < result.length; ++i) {
			expect(result[i].id).withContext("Did not find expected owned element").toBe(expected[i]);
		}
	});

	it("testGetOwnedNotOwner", function() {
		const start = document.getElementById("notowned");
		expect(ariaGroup.getOwned(start).length).toBe(0);
	});

	it("testGetGroupFromContainer", function() {
		const start = document.getElementById("menu1"),
			expected = outerItemIds,
			group = ariaGroup.getGroup(start),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetGroupFromContainerWithRole", function() {
		const start = document.getElementById("menu1"),
			expected = outerItemIds,
			group = ariaGroup.getGroup(start, "menu"),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetGroupFromContainerWithIgnoreInner", function() {
		const start = document.getElementById("menu1"),
			expected = allItemIds,
			group = ariaGroup.getGroup(start, null, true),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetGroupFromContainerWithRoleIgnoreInner", function() {
		const start = document.getElementById("menu1"),
			expected = allItemIds,
			group = ariaGroup.getGroup(start, "menu", true),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetGroupFromMember", function() {
		const start = document.getElementById(allItemIds[0]),
			expected = outerItemIds,
			group = ariaGroup.getGroup(start),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetGroupFromMemberIgnoreInner", function() {
		const start = document.getElementById(allItemIds[0]),
			expected = allItemIds,
			group = ariaGroup.getGroup(start, "menuitem", true),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetInnerGroupFromMember", function() {
		const start = document.getElementById(innerItemIds[0]),
			expected = innerItemIds,
			group = ariaGroup.getGroup(start),
			foundIds = [];

		for (let i = 0; i < group.length; ++i) {
			foundIds.push(group[i].id);
		}
		expect(group.length).withContext(foundIds.join(" ")).toBe(expected.length);
		for (let i = 0; i < group.length; ++i) {
			expect(group[i].id).toBe(expected[i]);
		}
	});

	it("testGetContainer ", function() {
		const start = document.getElementById(allItemIds[0]),
			expected = "menu1",
			result = ariaGroup.getContainer(start);
		expect(result.id).toBe(expected);
	});

	it("testGetInnerContainer ", function() {
		const start = document.getElementById(innerItemIds[0]),
			expected = "submenu",
			result = ariaGroup.getContainer(start);
		expect(result.id).toBe(expected);
	});

	it("testGetContainerNestedOwned", function() {
		const start = document.getElementById("owned5"),
			expected = "owner-3",
			result = ariaGroup.getContainer(start);
		expect(result.id).toBe(expected);
	});

	it("testGetContainerNestedIgnoreOwned", function() {
		const start = document.getElementById("owned5"),
			expected = "owner-1",
			result = ariaGroup.getContainer(start, null, true);
		expect(result.id).toBe(expected);
	});

	it("testGetContainerWithWidget", function() {
		const start = document.getElementById(innerItemIds[0]),
			widget = ".widget-menu",
			expected = "menu1",
			result = ariaGroup.getContainer(start, widget);
		expect(result.id).toBe(expected);
	});

	it("testGetContainerWithWidgetAndOwned", function() {
		const start = document.getElementById("owned5"),
			widget = ".some-group",
			expected = "owner-3",
			result = ariaGroup.getContainer(start, widget);
		expect(result.id).toBe(expected);
	});

	it("testGetContainerWithWidgetAndIgnoreOwned", function() {
		const start = document.getElementById("owned5"),
			widget = ".some-group",
			expected = "owner-1",
			result = ariaGroup.getContainer(start, widget, true);
		expect(result.id).toBe(expected);
	});
});
