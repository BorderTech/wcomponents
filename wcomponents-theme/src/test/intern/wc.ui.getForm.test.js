define(["intern!object", "intern/chai!assert", "wc/ui/getForm"],
	function (registerSuite, assert, controller) {
		"use strict";

		var testHolder;

		registerSuite({
			name: "getForm",
			setup: function() {
				var testContent = "<div id='examplewrapper'><form id='form1'>\
	<input id='input1' name='foo' type='text'>\
	<span id='spaninform'>content</span></form>\
	<input id='input2' readonly>\
	<span id='spanoutform'>content</span></div>";
				testHolder = document.getElementById("testholder");
				if (!testHolder) {
					document.body.insertAdjacentHTML("beforeend", "<div id='testholder'></div>");
					testHolder = document.getElementById("testholder");
				}
				testHolder.innerHTML = testContent;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetWithForm: function() {
				var start = document.getElementById("form1"),
					expected = document.getElementById("form1");
				assert.strictEqual(controller(start), expected, "Did not find correct form");
			},
			testGetWithInputInForm: function() {
				var start = document.getElementById("input1"),
					expected = document.getElementById("form1");
				assert.strictEqual(controller(start), expected, "Did not find correct form");
			},
			testGetWithSpanInForm: function() {
				var start = document.getElementById("spaninform"),
					expected = document.getElementById("form1");
				assert.strictEqual(controller(start), expected, "Did not find correct form");
			},
			testGetWithInputOutsideForm: function() {
				var start = document.getElementById("input2");
				assert.isNull(controller(start),  "Did not find correct form");
			},
			testGetWithSpanOutsideForm: function() {
				var start = document.getElementById("spanoutform");
				assert.isNull(controller(start), "Did not find correct form");
			},
			testgetWithNothing: function() {
				var expected = document.getElementById("form1");
				assert.strictEqual(controller(), expected, "Did not find correct form");
			},
			testgetWithNothingAncestorOnly: function() {
				assert.isNull(controller("", true), "Did not find correct form");
			}
		});
	}
);

