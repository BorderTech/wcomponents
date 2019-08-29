define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var trig, trig2, trig3, trig4,
			manager, testHolder, Trigger,
			urlResource = require.toUrl("intern/resources/ajaxTriggerManager.html");

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
				var actual;
				manager.addTrigger(trig);
				actual = manager.getTrigger("foo");
				assert.strictEqual(trig, actual);
			},

			// This test fails. Returns "undefined" instead.
			testTriggerManaagerRemoveTrigger: function() {
				var actual;
				manager.addTrigger(trig);
				manager.removeTrigger(trig.id);
				actual = !!(manager.getTrigger(trig.id));
				assert.strictEqual(false, actual);
			},

			// This test fails. Returns "undefined" instead.
			testTriggerManagerAddTwoSameTriggers: function() {
				var actual;
				manager.addTrigger(trig);
				manager.addTrigger(trig);
				manager.removeTrigger(trig.id);
				actual = !!(manager.getTrigger(trig.id));
				assert.strictEqual(false, actual);
			},

			testTriggerManagerAddManyGetOne: function() {
				var actual;
				manager.addTrigger(trig);
				manager.addTrigger(trig2);
				manager.addTrigger(trig3);
				actual = manager.getTrigger(trig2.id);
				assert.strictEqual(trig2, actual);
			},

			testTriggerManagerElementIDTrigger: function() {
				var actual, element;
				manager.addTrigger(trig);
				element = document.getElementById("foo");
				actual = manager.getTrigger(element);
				assert.strictEqual(trig, actual);
			},

			testTriggerManagerElementNameTrigger: function() {
				var actual, element;
				manager.addTrigger(trig4);
				element = document.getElementById("swan");
				actual = manager.getTrigger(element);
				assert.strictEqual(trig4, actual);
			},

			testTriggerManagerNestedTrigger: function() {
				var actual, element;
				manager.addTrigger(trig3);
				element = document.getElementById("jim");
				actual = manager.getTrigger(element);
				assert.strictEqual(trig3, actual);
			}
		});
	});