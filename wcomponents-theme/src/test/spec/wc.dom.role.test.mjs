import controller from "wc/dom/role.mjs";

describe("wc/debounce", () => {
	const roleId = "roletest-hasrole",
		testRole = "button",
		noRoleId = "roletest-norole",
		buttonId = "roletest-button",
		testContent = `
			<span id="${roleId}" role="${testRole}">content</span>
			<span id="${noRoleId}">content</span>
			<button type="button" id="${buttonId}">button</button>`;

	beforeEach(function() {
		document.body.innerHTML = testContent;
	});

	afterEach(function() {
		document.body.innerHTML = "";
	});

	it("testGet_noElement", function() {
		// @ts-ignore
		expect(controller.get()).toBeFalsy();
	});

	it("testGet_notElement", function() {
		// @ts-ignore
		expect(controller.get({})).toBeFalsy();
	});

	it("testGet_norole", function() {
		expect(controller.get(document.getElementById(noRoleId))).toBeNull();
	});

	it("testGet_norole_implied", function() {
		expect(controller.get(document.getElementById(noRoleId), true)).toBeFalsy();
	});

	it("testGet_role", function() {
		expect(controller.get(document.getElementById(roleId))).toBe(testRole);
	});

	it("testGet_implied", function() {
		expect(controller.get(document.getElementById(buttonId), true)).toBe("button");
	});

	it("testHas_noElement", function() {
		// @ts-ignore
		expect(controller.has()).toBeFalse();
	});

	it("testHas_notElement", function() {
		// @ts-ignore
		expect(controller.has({})).toBeFalse();
	});

	it("testHas_noRole", function() {
		expect(controller.has(document.getElementById(noRoleId))).toBeFalse();
	});

	it("testHas_role", function() {
		expect(controller.has(document.getElementById(roleId))).toBeTrue();
	});

	it("testHas_notImplied", function() {
		expect(controller.has(document.getElementById(buttonId))).toBeFalse();
	});

	it("testHas_implied", function() {
		expect(controller.has(document.getElementById(buttonId), true)).toBeTrue();
	});

	it("testHas_impliedNoImplication", function() {
		expect(controller.has(document.getElementById(noRoleId), true)).toBeFalse();
	});
});
