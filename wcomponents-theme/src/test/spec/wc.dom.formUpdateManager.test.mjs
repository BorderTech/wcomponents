import formUpdateManager from "wc/dom/formUpdateManager.mjs";

describe("wc/dom/formUpdateManager", () => {
	const ownerDocument = document,
		testHolder = ownerDocument.body,
		testName = "xcrmnt",
		testVal = "eee",
		formId = "aFormByAnyOtherName";

	const byName = (context, name) => {
		return context.querySelectorAll(`[name='${name}']`);
	};

	/**
	 * @return {HTMLFormElement}
	 */
	const getForm = () => /** @type HTMLFormElement */(ownerDocument.getElementById(formId));

	beforeEach(() => {
		testHolder.innerHTML = `<form id="${formId}" name="aFormByAnyOtherName"></form>`;
	});

	afterEach(() => {
		testHolder.innerHTML = "";
	});

	/**
	 * Check that existing fields in the stateContainer are blown away on update
	 */
	it("testStateContainerCleanedOnUpdate", function() {
		const subscriber = function() {},
			form = getForm(),
			markerElement = ownerDocument.createElement("input");
		try {
			const sContainer = formUpdateManager.getStateContainer(form);
			formUpdateManager.subscribe(subscriber);
			sContainer.appendChild(markerElement);
			expect(markerElement.parentNode).toBe(sContainer);
			formUpdateManager.update(form);
			expect(markerElement.parentNode).toBeNull();
		} finally {
			formUpdateManager.unsubscribe(subscriber);
		}
	});

	/**
	 * Check that existing fields in the stateContainer are blown away on update
	 */
	it("testClean", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			markerElement = ownerDocument.createElement("input");
		sContainer.appendChild(markerElement);
		expect(markerElement.parentNode).toBe(sContainer);
		formUpdateManager.clean(form);
		expect(markerElement.parentNode).toBeNull();
	});

	it("testGetStateFieldAndWriteStateFieldRval", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName,
			state = formUpdateManager.writeStateField(sContainer, name),
			result = formUpdateManager.getStateField(sContainer, name);
		expect(result).toBe(state);
		expect(byName(form, name)[0]).toBe(result);
	});

	it("testWriteStateFieldWithNameAndVal", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName, val = testVal;
		let result = byName(form, name);
		expect(result.length).toBe(0);
		formUpdateManager.writeStateField(sContainer, name, val);
		result = formUpdateManager.getStateField(sContainer, name);
		expect(result.value).toBe(val);
	});

	it("testWriteStateFieldWithName", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName, val = "";
		let result = byName(form, name);
		expect(result.length).toBe(0);
		formUpdateManager.writeStateField(sContainer, name);
		result = formUpdateManager.getStateField(sContainer, name);
		expect(result.value).toBe(val);
	});

	it("testWriteStateFieldDuplicateWithNameAndVal", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName, val = testVal;
		let result = byName(form, name);
		expect(result.length).toBe(0);
		formUpdateManager.writeStateField(sContainer, name, val);
		result = byName(form, name);
		expect(result.length).toBe(1);
		formUpdateManager.writeStateField(sContainer, name, val);
		result = byName(form, name);
		expect(result.length).toBe(2);
	});

	it("testWriteStateFieldWithNameAndValAndUnique", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName, val = testVal;
		let result = byName(form, name);
		expect(result.length).toBe(0);
		formUpdateManager.writeStateField(sContainer, name, val, true);
		result = formUpdateManager.getStateField(sContainer, name);
		expect(result.value).toBe(val);
	});

	it("testWriteStateFieldDuplicateWithNameAndValAndUnique", function() {
		const form = getForm(),
			sContainer = formUpdateManager.getStateContainer(form),
			name = testName, val = testVal;
		let result = byName(form, name);
		expect(result.length).toBe(0);
		formUpdateManager.writeStateField(sContainer, name, val, true);
		result = byName(form, name);
		expect(result.length).toBe(1);
		formUpdateManager.writeStateField(sContainer, name, val, true);
		result = byName(form, name);
		expect(result.length).toBe(1);
	});

	it("testSubscribeAndUpdate", function() {
		const form = getForm(),
			name = testName, val = testVal;
		let result = byName(form, name),
			subscriber = {
				writeState: function(frm, stateContainer) {
					formUpdateManager.writeStateField(stateContainer, name, val, true);
				}
			};
		try {
			expect(result.length).withContext(`Clean up not working, found remnant field with name ${name}`).toBe(0);
			formUpdateManager.subscribe(subscriber);
			formUpdateManager.update(form);
			result = byName(form, name);
			expect(result.length).toBe(1);
			expect(result[0].value).toBe(val);
		} finally {
			formUpdateManager.unsubscribe(subscriber);
		}
	});

	it("testSubscribeWithFunction", function() {
		const form = getForm(),
			name = testName, val = testVal;
		let result = byName(form, name),
			subscriber = function(frm, stateContainer) {
				formUpdateManager.writeStateField(stateContainer, name, val, true);
			};
		try {
			expect(result.length).withContext(`Clean up not working, found remnant field with name ${name}`).toBe(0);
			formUpdateManager.subscribe(subscriber);
			formUpdateManager.update(form);
			result = byName(form, name);
			expect(result.length).toBe(1);
			expect(result[0].value).toBe(val);
		} finally {
			formUpdateManager.unsubscribe(subscriber);
		}
	});

	it("testUnsubscribeAndUpdate", function() {
		const form = getForm(),
			name = testName, val = testVal;
		let result = byName(form, name),
			subscriber = {
				writeState: function(frm, stateContainer) {
					formUpdateManager.writeStateField(stateContainer, name, val, true);
				}
			};
		expect(result.length).withContext(`Clean up not working, found remnant field with name ${name}`).toBe(0);
		formUpdateManager.subscribe(subscriber);
		formUpdateManager.unsubscribe(subscriber);
		formUpdateManager.update(form);
		result = byName(form, name);
		expect(result.length).withContext("Unsubscribe should have prevented field state being written").toBe(0);
	});

	it("testUnsubscribeWithFunction", function() {
		const form = getForm(),
			name = testName, val = testVal;
		let result = byName(form, name),
			subscriber = function(frm, stateContainer) {
				formUpdateManager.writeStateField(stateContainer, name, val, true);
			};
		expect(result.length).withContext(`Clean up not working, found remnant field with name ${name}`).toBe(0);
		formUpdateManager.subscribe(subscriber);
		formUpdateManager.unsubscribe(subscriber);
		formUpdateManager.update(form);
		result = byName(form, name);
		expect(result.length).withContext("Unsubscribe should have prevented field state being written").toBe(0);
	});
});
