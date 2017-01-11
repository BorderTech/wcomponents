define(["intern!object", "intern/chai!assert", "wc/ui/getVisibleText"],
	function (registerSuite, assert, controller) {
		"use strict";

		var testHolder,
			testContent = "\
<div id='div1'>text</div>\
<div id='div2'><p>yes</p><p>yes</p></div>\
<div id='div3'><p>yes</p><p hidden='hidden'>no</p></div>\
<div id='div4'><p>yes</p><p hidden>no</p></div>\
<label id='withhint'>maincontent<span class='wc-label-hint'>hint</span></label>\
<button id='withtooltip'><span role='tooltip'>H</span>hello</button>\
<label id='withhinttooltip'><span role='tooltip'>M</span>maincontent<span class='wc-label-hint'>hint</span></label>";

		registerSuite({
			name: "getVisibleText",
			setup: function() {
				testHolder = document.getElementById("testholder");
				if (!testHolder) {
					document.body.insertAdjacentHTML("beforeend", "<div id='testholder'></div>");
					testHolder = document.getElementById("testholder");
				}
			},
			teardown: function () {
				testHolder.innerHTML = "";
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			testGetVisibleText: function() {
				var testId = "div1",
					element = document.getElementById(testId),
					expected = "text";
				assert.strictEqual(controller(element), expected, "Did not get correct text");

			},
			testGetVisibleTextNested: function() {
				var testId = "div2",
					element = document.getElementById(testId),
					expected = "yesyes";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextNestedHidden: function() {
				var testId = "div3",
					element = document.getElementById(testId),
					expected = "yes";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextNestedHiddenHTMLSyntax: function() {
				var testId = "div4",
					element = document.getElementById(testId),
					expected = "yes";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextWithHint: function() {
				var testId = "withhint",
					element = document.getElementById(testId),
					expected = "maincontenthint";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextRemoveHint: function() {
				var testId = "withhint",
					element = document.getElementById(testId),
					expected = "maincontent";
				assert.strictEqual(controller(element, true), expected, "Did not get correct text");
			},
			testGetVisibleTextWithTooltip: function() {
				var testId = "withtooltip",
					element = document.getElementById(testId),
					expected = "hello";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextWithHintTooltip: function() {
				var testId = "withhinttooltip",
					element = document.getElementById(testId),
					expected = "maincontenthint";
				assert.strictEqual(controller(element), expected, "Did not get correct text");
			},
			testGetVisibleTextKeepHintTooltip: function() {
				var testId = "withhinttooltip",
					element = document.getElementById(testId, true),
					expected = "maincontent";
				assert.strictEqual(controller(element, true), expected, "Did not get correct text");
			}
		});
	}
);