define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var trig, trig2, trig3, trig4,
			resourceUrl = "@RESOURCES@/",
			manager, testHolder, Trigger,
			urlResource = resourceUrl + "ajaxTriggerManager.html";

		registerSuite({
			name: "AjaxTriggerManager",
			setup: function() {
				var result = testutils.setupHelper(["wc/ajax/triggerManager", "wc/ajax/Trigger"]).then(function(arr) {
					manager = arr[0];
					Trigger = arr[1];
					testHolder = testutils.getTestHolder();
					trig = new Trigger({id: "foo", loads: ["fred"]});
					trig2 = new Trigger({id: "bar", loads: ["fred"]});
					trig3 = new Trigger({id: "adam", loads: ["fred"]});
					trig4 = new Trigger({id: "gamma", loads: ["fred"]});
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testTriggerManagerAddTrigger: function() {
				manager.addTrigger(trig);
				var actual = manager.getTrigger("foo");
				assert.strictEqual(trig, actual);
			},

			// This test fails. Returns "undefined" instead.
			testTriggerManaagerRemoveTrigger: function() {
				manager.addTrigger(trig);
				manager.removeTrigger(trig.id);
				var actual = !!(manager.getTrigger(trig.id));
				assert.strictEqual(false, actual);
			},

			// This test fails. Returns "undefined" instead.
			testTriggerManagerAddTwoSameTriggers: function() {
				manager.addTrigger(trig);
				manager.addTrigger(trig);
				manager.removeTrigger(trig.id);
				var actual = !!(manager.getTrigger(trig.id));
				assert.strictEqual(false, actual);
			},

			testTriggerManagerAddManyGetOne: function() {
				manager.addTrigger(trig);
				manager.addTrigger(trig2);
				manager.addTrigger(trig3);
				var actual = manager.getTrigger(trig2.id);
				assert.strictEqual(trig2, actual);
			},

			testTriggerManagerElementIDTrigger: function() {
				manager.addTrigger(trig);
				var element = document.getElementById("foo");
				var actual = manager.getTrigger(element);
				assert.strictEqual(trig, actual);
			},

			testTriggerManagerElementNameTrigger: function() {
				manager.addTrigger(trig4);
				var element = document.getElementById("swan");
				var actual = manager.getTrigger(element);
				assert.strictEqual(trig4, actual);
			},

			testTriggerManagerNestedTrigger: function() {
				manager.addTrigger(trig3);
				var element = document.getElementById("jim");
				var actual = manager.getTrigger(element);
				assert.strictEqual(trig3, actual);
			}
		});
	});