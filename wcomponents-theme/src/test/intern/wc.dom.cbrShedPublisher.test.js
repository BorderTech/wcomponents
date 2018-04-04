define(["intern!object", "intern/chai!assert", "./resources/test.utils!", "wc/dom/cbrShedPublisher", "wc/dom/shed", "wc/dom/event"],
	function (registerSuite, assert, testutils, controller, shed, event) {
		"use strict";

		var testHolder,
			testContent = "<label id='l1' for='r1'><input type='radio' id='r1' name='rg1'> one</label>\n\
<label id='l2' for='r2'><input type='radio' id='r2' name='rg1' checked> two</label>\n\
<label id='l3' for='cb1'><input type='checkbox' id='cb1' name='cbg1'> cb one</label>\n\
<label id='l4' for='cb2'><input type='checkbox' id='cb2' name='cbg2' checked> cb two</label>",
			done = false;


		function subscriber() {
			done = true;
		}

		function doClick(id) {
			var element = document.getElementById(id);
			if (!element) {
				assert.isTrue(false, "no element to click");
			}
			event.fire(element, event.TYPE.click);
		}

		function focusThenClick(id, otherId) {
			var element = document.getElementById(id),
				idToClick = otherId || id;
			if (!element) {
				assert.isTrue(false, "no element to focus");
			}
			event.fire(element, event.TYPE.focus);
			doClick(idToClick);
		}

		registerSuite({
			name: "wc/dom/cbrShedPublisher",
			setup: function() {
				testHolder = testutils.getTestHolder();
				controller.initialise(testHolder);
				shed.subscribe(shed.actions.SELECT, subscriber);
				shed.subscribe(shed.actions.DESELECT, subscriber);
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
				done = false;
			},
			tests : {
				"click checkbox to select publishes" : function() {
					focusThenClick("cb1");
					assert.isTrue(done);
				},
				"click checkbox to deselect publishes" : function() {
					focusThenClick("cb2");
					assert.isTrue(done);
				},
				"click checked radio does not publish" : function() {
					focusThenClick("r2");
					assert.isFalse(done);
				},
				"click unchecked radio publishes" : function() {
					focusThenClick("r1");
					assert.isTrue(done);
				},
				"click label publishes": function() {
					focusThenClick("r1", "l3");
					assert.isTrue(done);
				},
				"click label of unchecked radio publishes": function() {
					focusThenClick("r1", "l1");
					assert.isTrue(done);
				},
				"click label of checked radio does not publish": function() {
					focusThenClick("r2", "l2");
					assert.isFalse(done);
				},
				"click checkbox without focus still publishes": function() {
					doClick("cb1");
					assert.isTrue(done);
				},
				"click disabled checkbox does not publish": function() {
					var id = "cb1",
						element = document.getElementById(id);
					try {
						shed.disable(element, true);
						doClick(id);
						assert.isFalse(done);
					} finally {
						shed.enable(element, true);
					}
				}
			}
		});
	});
