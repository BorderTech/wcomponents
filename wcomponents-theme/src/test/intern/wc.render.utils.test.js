define(["intern!object", "intern/chai!assert", "wc/render/utils", "wc/has", "./resources/test.utils!"],
	function (registerSuite, assert, renderUtils, has, testutils) {
		"use strict";
		var testHolder,
			testSuite = {
				name: "wc/render/utils",
				setup: function() {
					testHolder = testutils.getTestHolder();
				},
				beforeEach: function() {

				},
				afterEach: function() {

				},
				testCreateElement: function() {
					var element = renderUtils.createElement("input", { name: "wc-radio1", type: "radio" }),
						props = renderUtils.getProps(element);
					assert.equal("input", element.tagName.toLowerCase());
					assert.equal("radio", props.type);
					assert.equal("wc-radio1", props.name);
				},
				testCreateElementWithTextChildNodes: function() {
					var i, nextChild, nextKid,
						kids = [document.createElement("span"), "Hello", document.createElement("div")],
						element = renderUtils.createElement("div", { "data-wc-foo": "kungfu" }, kids),
						props = renderUtils.getProps(element);
					assert.equal("div", element.tagName.toLowerCase());
					assert.equal("kungfu", props["data-wc-foo"]);
					assert.equal(kids.length, element.childNodes.length);
					for (i = 0; i < element.childNodes.length; i++) {
						nextChild = element.childNodes[i];
						nextKid = kids[i];
						if (typeof nextKid === "string") {
							assert.equal(nextKid, nextChild.nodeValue);
							assert.equal(Node.TEXT_NODE, nextChild.nodeType);
						} else {
							assert.equal(nextKid, nextChild);
						}
					}
				},
				testCreateElementWithFunkyPropname: function() {
					var element = renderUtils.createElement("input", { "data-wc-foo": "kungfu" }),
						props = renderUtils.getProps(element);
					assert.equal("kungfu", props["data-wc-foo"]);
				},
				testCreateElementWithCssClass: function() {
					var classNameExpected = ["kung", "fu"],
						element = renderUtils.createElement("input", { className: classNameExpected.join(" ") });
					classNameExpected.forEach(function(next) {
						assert.isTrue(element.classList.contains(next), "Cannae find class " + next);
					});
				},
				testCreateElementWithHtmlFor: function() {
					var element = renderUtils.createElement("label", { htmlFor: "abc123xyz" });
					assert.equal("abc123xyz", element.getAttribute("for"));
				},
				testGetPropsWithProp: function() {
					var actual,
						element = document.createElement("input");
					element.type = "radio";
					actual = renderUtils.getProps(element);
					assert.equal("radio", actual.type);
				},
				testGetPropsWithAttr: function() {
					var actual,
						element = document.createElement("input");
					element.setAttribute("type", "radio");
					actual = renderUtils.getProps(element);
					assert.equal("radio", actual.type);
				},
				testGetPropsWithFunkyPropname: function() {
					var actual,
						element = document.createElement("input");
					element.setAttribute("data-wc-foo", "kungfu");
					actual = renderUtils.getProps(element);
					assert.equal("kungfu", actual["data-wc-foo"]);
				},
				testGetPropsWithCssClass: function() {
					var actual, classNameExpected = ["kung", "fu"],
						element = document.createElement("input");
					classNameExpected.forEach(function(next) {
						element.classList.add(next);
					});
					actual = renderUtils.getProps(element);
					assert.sameMembers(classNameExpected, actual.className.split(" "));
				},
				testGetPropsWithForAttr: function() {
					var actual,
						element = document.createElement("label");
					element.setAttribute("for", "abc123xyz");
					actual = renderUtils.getProps(element);
					assert.equal("abc123xyz", actual.htmlFor);
				},
				testGetPropsWithDisabledProp: function() {
					var actual,
						element = document.createElement("input");
					element.disabled = true;
					actual = renderUtils.getProps(element);
					assert.isOk(actual.disabled);  // We really shouldn't care what beyond the fact that it's truthy
				},
				testGetPropsWithDisabledAttr: function() {
					var actual,
						element = document.createElement("input");
					element.setAttribute("disabled", "disabled");
					actual = renderUtils.getProps(element);
					assert.isOk(actual.disabled);  // We really shouldn't care what beyond the fact that it's truthy
				}
			};

		registerSuite(testSuite);
	}
);
