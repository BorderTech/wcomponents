define(["intern!object", "intern/chai!assert", "./resources/test.utils!", "wc/dom/cbrShedPublisher", "wc/dom/shed", "wc/dom/event", "wc/dom/Widget"],
	function (registerSuite, assert, testutils, controller, shed, event, Widget) {
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

		// only need to focus the input element if the browser does not implement event capture phase
		function testDoFocus(element) {
			if (!event.canCapture) {
				element.focus();
			}
		}

		function focusThenClick(id, otherId) {
			var element = document.getElementById(id),
				idToClick;
			if (!element) {
				assert.isTrue(false, "no element to focus");
			}
			testDoFocus(element);
			idToClick = otherId || id;
			element = document.getElementById(idToClick);
			if (!element) {
				assert.isTrue(false, "no element to click");
			}
			// event.fire(element, event.TYPE.click);
			element.click();
		}

		registerSuite({
			name: "wc/dom/cbrShedPublisher",
			setup: function() {
				testHolder = testutils.getTestHolder();
				testHolder.innerHTML = testContent;
				shed.subscribe(shed.actions.SELECT, subscriber);
				shed.subscribe(shed.actions.DESELECT, subscriber);
				controller.initialise(testHolder);
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
				done = false;
			},
			teardown: function() {
				testHolder.innerHTML = "";
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
				"click disabled checkbox does not publish": function() {
					var id = "cb1",
						element = document.getElementById(id);
					try {
						testDoFocus(element); // focus (if required) before disabling to ensure changeEvent is wired up
						shed.disable(element, true);
						element.click();
						assert.isFalse(done);
					} finally {
						shed.enable(element, true);
						element.click();
						assert.isTrue(done); // nothing up my sleeves - this is to check the call to focus worked ok
					}
				},
				"change event on checkbox publishes": function() {
					var id = "cb1",
						element = document.getElementById(id);
					testDoFocus(element);
					event.fire(element, event.TYPE.change);
					assert.isTrue(done);
				},
				"change event on unchecked radio publishes": function() {
					var id = "r1",
						element = document.getElementById(id);
					testDoFocus(element);
					event.fire(element, event.TYPE.change);
					assert.isTrue(done);
				},
				"change event on checked radio publishes": function() {
					var id = "r2",
						element = document.getElementById(id);
					testDoFocus(element);
					event.fire(element, event.TYPE.change);
					assert.isTrue(done);
				},
				"change event on unfocussed checkbox does not publish if event cannot capture": function() {
					if (event.canCapture) {
						this.skip("test for non-capturing browsers only");
					}
					var id = "cb1",
						element = document.getElementById(id);
					event.fire(element, event.TYPE.change);
					assert.isFalse(done);
				},
				"change event on unfocussed radio does not publish if event cannot capture": function() {
					if (event.canCapture) {
						this.skip("test for non-capturing browsers only");
					}
					var id = "r1",
						element = document.getElementById(id);
					event.fire(element, event.TYPE.change);
					assert.isFalse(done);
				},
				"get radio widget": function() {
					var w = controller.getWidget("r"),
						element = document.getElementById("r1");
					assert.isOk(w);
					assert.isTrue(w.isOneOfMe(element));
				},
				"get checkbox widget": function() {
					var w = controller.getWidget("cb"),
						element = document.getElementById("cb1");
					assert.isOk(w);
					assert.isTrue(w.isOneOfMe(element));
				},
				"get widgets": function() {
					var w = controller.getWidget(),
						element = document.getElementById("cb1");
					assert.isTrue(Array.isArray(w));
					assert.isTrue(Widget.isOneOfMe(element, w), "Expected a check box to be a match for the Widget array");
					element = document.getElementById("r1");
					assert.isTrue(Widget.isOneOfMe(element, w), "Expected a radio button to be a match for the Widget array");
				}
			}
		});
	});
