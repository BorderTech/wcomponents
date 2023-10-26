import uiIcon from "wc/ui/icon.mjs";

describe("wc/ui/icon", () => {
	const noIconId = "uiicontest1",
		withIconId = "uiicontest2",
		startIconClass = "fa-bars", // any non-empty String will work for these tests
		newIconClass = "fa-circle", // any non-empty String will work for these tests so long as it is different from startIconClass
		testContent = `<span id="${noIconId}">content</span><span id="${withIconId}"><i class="fa ${startIconClass}" aria-hidden="true"></i>content</span>`;
	
	let ownerDocument, testHolder;
	
	function getElement(withIcon) {
		return ownerDocument.getElementById(withIcon ? withIconId : noIconId);
	}

	beforeAll(function() {
		ownerDocument = document;
		testHolder = ownerDocument.body.appendChild(ownerDocument.createElement("div"));
	});

	beforeEach(function() {
		testHolder.innerHTML = testContent;
	});
	
	afterEach(function() {
		testHolder.innerHTML = "";
	});
	
	it("testGetWidget_correctWidget", function() {
		const widget = uiIcon.getWidget(),
			target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.matches(widget)).toBeTrue();
	});

	it("testGet_noArg", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.get();
		expect(doBadThing).toThrowError("element must be an HTML element");
	});

	it("testGet_noElementArg", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.get("I am not an element");
		expect(doBadThing).toThrowError("element must be an HTML element");
	});

	it("testGet", function() {
		const target = getElement(true),
			expected = target.firstElementChild;
		expect(uiIcon.get(target)).toEqual(expected);
	});

	it("testAdd", function() {
		const target = getElement();
		expect(target.firstElementChild).toBeFalsy();
		uiIcon.add(target, newIconClass);
		expect(target.firstElementChild.matches(uiIcon.getWidget())).toBeTrue();
	});

	it("testAdd_noArgs", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.add();
		expect(doBadThing).toThrowError("arguments must be defined");
	});

	it("testAdd_noIconClass", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.add(getElement());
		expect(doBadThing).toThrowError("arguments must be defined");
	});

	it("testAdd_elementNotElement", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.add("I am an element", newIconClass);
		expect(doBadThing).toThrowError("element must be an HTML element");
	});

	it("testAdd_iconClassNotString", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.add(getElement(), {});
		expect(doBadThing).toThrowError("icon to add argument must be a String");
	});

	it("testAdd_toIcon", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.add(icon, newIconClass);
		expect(icon.classList.contains(newIconClass)).toBeTrue();
	});

	it("testAdd_toContainingElementWithIcon", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.add(target, newIconClass);
		expect(icon.classList.contains(newIconClass)).toBeTrue();
	});

	it("testAdd_toContainingElementNoIcon", function() {
		const target = getElement();
		let icon = target.firstElementChild;
		expect(icon).toBeFalsy();
		uiIcon.add(target, newIconClass);
		icon = target.firstElementChild;
		expect(icon).toBeTruthy();
		expect(icon.classList.contains(newIconClass)).toBeTrue();
		expect(icon.classList.contains("fa")).toBeTrue();
	});

	it("testRemove_noArgs", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.remove();
		expect(doBadThing).toThrowError("arguments must be defined");
	});

	it("testRemove_noIconClass", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.remove(getElement());
		expect(doBadThing).toThrowError("arguments must be defined");
	});

	it("testRemove_elementNotElement", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.remove("I am an element", startIconClass);
		expect(doBadThing).toThrowError("element must be an HTML element");
	});

	it("testRemove_iconClassNotString", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.remove(getElement(), {});
		expect(doBadThing).toThrowError("icon to remove argument must be a String");
	});

	it("testRemove_multipleClasses", function() {
		const target = getElement(true);
		let icon = target.firstElementChild;
		icon.classList.add(newIconClass);
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		uiIcon.remove(icon, startIconClass);
		// remove only the class, not the icon element
		icon = target.firstElementChild;
		expect(icon).toBeTruthy();
		expect(icon.classList.contains(startIconClass)).toBeFalse();
	});

	it("testRemove_lastIconClass", function() {
		const target = getElement(true);
		let icon = target.firstElementChild;
		expect(icon).toBeTruthy();
		uiIcon.remove(icon, startIconClass);
		icon = target.firstElementChild;
		expect(icon).toBeFalsy();
	});

	it("testRemove_withContainingElement", function() {
		const target = getElement(true);
		let icon = target.firstElementChild;
		icon.classList.add(newIconClass);
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		uiIcon.remove(target, startIconClass);
		// remove only the class, not the icon element
		icon = target.firstElementChild;
		expect(icon).toBeTruthy();
		expect(icon.classList.contains(startIconClass)).toBeFalse();
	});

	it("testRemove_lastIconClassWithContainingElement", function() {
		const target = getElement(true);
		let icon = target.firstElementChild;
		expect(icon).toBeTruthy();
		uiIcon.remove(target, startIconClass);
		icon = target.firstElementChild;
		expect(icon).toBeFalsy();
	});

	it("testRemove_classNotPresent", function() {
		const target = getElement(true);
		let icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.remove(icon, newIconClass);
		icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
	});

	it("testChange_noIconArgs", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		// @ts-ignore
		expect(uiIcon.change(target)).toBeUndefined();
		expect(icon.classList.contains(startIconClass)).toBeTrue();
	});

	it("testChange_noElement", function() {
		// @ts-ignore
		const doBadThing = () => uiIcon.change(null, "a", "b");
		expect(doBadThing).toThrowError("element must be an HTML element");
	});

	it("testChange", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.change(icon, newIconClass, startIconClass);
		expect(icon.classList.contains(newIconClass)).toBeTrue();
		expect(icon.classList.contains(startIconClass)).toBeFalse();
	});

	it("testChange_withContainingElement", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.change(target, newIconClass, startIconClass);
		expect(icon.classList.contains(newIconClass)).toBeTrue();
		expect(icon.classList.contains(startIconClass)).toBeFalse();
	});

	it("testChange_noRemove", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.change(target, newIconClass);
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeTrue();
	});

	it("testChange_noAdd", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		icon.classList.add("someOtherClass");
		uiIcon.change(target, null, startIconClass);
		expect(icon.classList.contains(startIconClass)).toBeFalse();
	});

	it("testChange_removeNotPresent", function() {
		const target = getElement(true),
			icon = target.firstElementChild;
		expect(icon.classList.contains(startIconClass)).toBeTrue();
		expect(icon.classList.contains(newIconClass)).toBeFalse();
		uiIcon.change(icon, newIconClass, "someOtherClass");
		expect(icon.classList.contains(newIconClass)).toBeTrue();
		expect(icon.classList.contains(startIconClass)).toBeTrue();
	});
});
