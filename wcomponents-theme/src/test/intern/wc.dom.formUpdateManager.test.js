define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var formUpdateManager,
			ns = "wc/dom/formUpdateManager",
			testName = "xcrmnt",
			testVal = "eee",
			formId = "aFormByAnyOtherName",
			testHolder;

		registerSuite({
			name: ns,
			setup: function() {
				return testutils.setupHelper(["wc/dom/formUpdateManager"], function(obj) {
					formUpdateManager = obj;
					testHolder = testutils.getTestHolder();
				});
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			beforeEach: function() {
				testHolder.innerHTML = "<form id='" + formId + "' name='aFormByAnyOtherName'></form>";
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			/**
			 * Check that existing fields in the stateContainer are blown away on update
			 */
			testStateContainerCleanedOnUpdate: function() {
				var sContainer, subscriber = function() {},
					form = document.getElementById(formId),
					markerElement = document.createElement("input");
				try {
					sContainer = formUpdateManager.getStateContainer(form);
					formUpdateManager.subscribe(subscriber);
					sContainer.appendChild(markerElement);
					assert.strictEqual(sContainer, markerElement.parentNode);
					formUpdateManager.update(form);
					assert.isNull(markerElement.parentNode);
				}
				finally {
					formUpdateManager.unsubscribe(subscriber);
				}
			},
			/**
			 * Check that existing fields in the stateContainer are blown away on update
			 */
			testClean: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					markerElement = document.createElement("input");
				sContainer.appendChild(markerElement);
				assert.strictEqual(sContainer, markerElement.parentNode);
				formUpdateManager.clean(form);
				assert.isNull(markerElement.parentNode);
			},
			testGetStateFieldAndWriteStateFieldRval: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName,
					state = formUpdateManager.writeStateField(sContainer, name),
					result = formUpdateManager.getStateField(sContainer, name);
				assert.strictEqual(state, result);
				assert.strictEqual(testutils._getElementsByName(form, name)[0], result);
			},
			testWriteStateFieldWithNameAndVal: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length);
				formUpdateManager.writeStateField(sContainer, name, val);
				result = formUpdateManager.getStateField(sContainer, name);
				assert.strictEqual(val, result.value);
			},
			testWriteStateFieldWithName: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName, val = "",
					result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length);
				formUpdateManager.writeStateField(sContainer, name);
				result = formUpdateManager.getStateField(sContainer, name);
				assert.strictEqual(val, result.value);
			},
			testWriteStateFieldDuplicateWithNameAndVal: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length);
				formUpdateManager.writeStateField(sContainer, name, val);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(1, result.length);
				formUpdateManager.writeStateField(sContainer, name, val);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(2, result.length);
			},
			testWriteStateFieldWithNameAndValAndUnique: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length);
				formUpdateManager.writeStateField(sContainer, name, val, true);
				result = formUpdateManager.getStateField(sContainer, name);
				assert.strictEqual(val, result.value);
			},
			testWriteStateFieldDuplicateWithNameAndValAndUnique: function() {
				var form = document.getElementById(formId),
					sContainer = formUpdateManager.getStateContainer(form),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length);
				formUpdateManager.writeStateField(sContainer, name, val, true);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(1, result.length);
				formUpdateManager.writeStateField(sContainer, name, val, true);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(1, result.length);
			},
			testSubscribeAndUpdate: function() {
				var form = document.getElementById(formId),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name),
					subscriber = {
						writeState: function(form, stateContainer) {
							formUpdateManager.writeStateField(stateContainer, name, val, true);
						}
					};
				try {
					assert.strictEqual(0, result.length, "Clean up not working, found remnant field with name " + name);
					formUpdateManager.subscribe(subscriber);
					formUpdateManager.update(form);
					result = testutils._getElementsByName(form, name);
					assert.strictEqual(1, result.length);
					assert.strictEqual(val, result[0].value);
				}
				finally {
					formUpdateManager.unsubscribe(subscriber);
				}
			},
			testSubscribeWithFunction: function() {
				var form = document.getElementById(formId),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name),
					subscriber = function(form, stateContainer) {
						formUpdateManager.writeStateField(stateContainer, name, val, true);
					};
				try {
					assert.strictEqual(0, result.length, "Clean up not working, found remnant field with name " + name);
					formUpdateManager.subscribe(subscriber);
					formUpdateManager.update(form);
					result = testutils._getElementsByName(form, name);
					assert.strictEqual(1, result.length);
					assert.strictEqual(val, result[0].value);
				}
				finally {
					formUpdateManager.unsubscribe(subscriber);
				}
			},
			testUnsubscribeAndUpdate: function() {
				var form = document.getElementById(formId),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name),
					subscriber = {
						writeState: function(form, stateContainer) {
							formUpdateManager.writeStateField(stateContainer, name, val, true);
						}
					};
				assert.strictEqual(0, result.length, "Clean up not working, found remnant field with name " + name);
				formUpdateManager.subscribe(subscriber);
				formUpdateManager.unsubscribe(subscriber);
				formUpdateManager.update(form);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length, "Unsubscribe should have prevented field state being written");
			},
			testUnsubscribeWithFunction: function() {
				var form = document.getElementById(formId),
					name = testName, val = testVal,
					result = testutils._getElementsByName(form, name),
					subscriber = function(form, stateContainer) {
						formUpdateManager.writeStateField(stateContainer, name, val, true);
					};
				assert.strictEqual(0, result.length, "Clean up not working, found remnant field with name " + name);
				formUpdateManager.subscribe(subscriber);
				formUpdateManager.unsubscribe(subscriber);
				formUpdateManager.update(form);
				result = testutils._getElementsByName(form, name);
				assert.strictEqual(0, result.length, "Unsubscribe should have prevented field state being written");
			}
		});
	});
