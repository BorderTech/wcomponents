define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var getAncestorOrSelf,
			testHolder,
			urlResource = "../../target/test-classes/wcomponents-theme/intern/resources/domTest.html";

		registerSuite({
			name: "domGetAncestorOrSelf",
			setup: function() {
				var result = new testutils.LamePromisePolyFill();
				testutils.setupHelper(["wc/dom/getAncestorOrSelf"], function(obj) {
					getAncestorOrSelf = obj;
					testHolder = testutils.getTestHolder();
					testutils.setUpExternalHTML(urlResource, testHolder).then(result._resolve);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetAncestorOrSelfGetImmediateParent: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "div");
				assert.strictEqual("subcontainer", result.id);
			},
			testGetAncestorOrSelfGetAncestor: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "ul");
				assert.strictEqual("ul1", result.id);
			},
			testGetAncestorOrSelfGetSelf: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "p");
				assert.strictEqual(foo, result);
			},
			testGetAncestorOrSelfNotFound: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "span");
				assert.isNull(result);
			},
			testGetAncestorOrSelfLimitNotFound: function() {
				var foo = document.getElementById("innerTbody"),
					result = getAncestorOrSelf(foo, "tr", "table");
				assert.isNull(result);
			},
			testGetAncestorOrSelfWrongLimitWrongTagnameNotFound: function() {
				/* Real world errors */
				var foo = document.getElementById("moocowDiv"),
					result = getAncestorOrSelf(foo, "blah", "blah");
				assert.isNull(result);
			},
			testGetAncestorOrSelfLimitFound: function() {
				var foo = document.getElementById("innerCell"),
					result = getAncestorOrSelf(foo, "tr", "table");
				assert.strictEqual("inner", result.id);
			},
			testGetAncestorOrSelfClassName: function() {
				var foo = document.getElementById("innerCell"),
					result = getAncestorOrSelf(foo, null, "body", "outerContainer");
				assert.strictEqual("outer", result.id);
			},
			testGetAncestorOrSelfClassNameWrongLimitNoMatch: function() {
				/*
				 * Real world errors:
				 * test that if it walks all the way up to DOCUMENT_NODE it will not try to use classList on it
				 */
				var foo = document.getElementById("moocowDiv"),
					result = getAncestorOrSelf(foo, null, "table", "outerContainer");
				assert.strictEqual(null, result);
			},
			testGetAncestorOrSelfElementAndClassName: function() {
				var foo = document.getElementById("innerCell"),
					result = getAncestorOrSelf(foo, "tbody", "body", "outerContainer");
				assert.strictEqual("outerTbody", result.id);
			},
			testGetAncestorOrSelfClassNameArray: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, null, "body", ["cow", "moo"]);
				assert.strictEqual("moocowDiv", result.id);
			},
			testGetAncestorOrSelfElementAndClassNameArray: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "div", "body", ["cow", "moo"]);
				assert.strictEqual("moocowDiv", result.id);
			},
			testGetAncestorOrSelfClassNameArrayNoMatch: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, null, "body", ["cow", "moo", "who"]);
				assert.isNull(result);
			},
			testGetAncestorOrSelfClassNameArrayWithSome: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, null, "body", ["cow", "moo"], "some");
				assert.strictEqual("mooDiv", result.id);
			},
			testGetAncestorOrSelfElementAndClassNameArrayWithSome: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, "div", "body", ["cow", "moo"], "some");
				assert.strictEqual("mooDiv", result.id);
			},
			testGetAncestorOrSelfClassNameArrayNoMatchWithSome: function() {
				var foo = document.getElementById("foo"),
					result = getAncestorOrSelf(foo, null, "body", ["who"], "some");
				assert.isNull(result);
			},
			testGetAncestorOrSelfInvalidArgs: function() {
				try {
					var foo = document.getElementById("doesntExist");
					getAncestorOrSelf(foo, "p");
					assert.fail("This line should not be executed");
				}
				catch (ex) {
					assert.isTrue(true);
				}
			},
			testWithTagNameAndAttributes: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {rel: "static"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithTagNameAndAttributesMultiple: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {rel: "static", name: "inky"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithTagNameAndAttributesMultipleNoMatchOne: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {rel: "static", name: "pinky"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithTagNameAndAttributesValueNoMatch: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {rel: "dynamic"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithTagNameAndAttributesNameNoMatch: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {href: "static"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithTagNameAndAttributePresent: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo, tagName: "A", attributes: {rel: null}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithTagNameAndAttributesAndClassName: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						tagName: "A",
						attributes: {rel: "static"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithTagNameAndAttributesAndClassNameNoMatchClass: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						tagName: "A",
						attributes: {rel: "static"},
						className: "daddy"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithTagNameAndAttributesAndClassNameNoMatchAttr: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						tagName: "A",
						attributes: {rel: "dynamic"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithAttributesAndClassName: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						attributes: {rel: "static"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithAttributesAndClassNameNoMatchClass: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						attributes: {rel: "static"},
						className: "daddy"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithAttributesAndClassNameNoMatchAttr: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						attributes: {rel: "dynamic"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithTagNameAndAttributesAndClassNameAndLimit: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						tagName: "A",
						limitTagName: "A",
						attributes: {rel: "static"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithTagNameAndAttributesAndClassNameAndLimitNoMatch: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						tagName: "A",
						limitTagName: "SPAN",
						attributes: {rel: "static"},
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testWithAttributesAndClassNameAndLimit: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						attributes: {rel: "static"},
						limitTagName: "A",
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("inky", result.id);
			},
			testWithAttributesAndClassNameAndLimitNoMatch: function() {
				var foo = document.getElementById("pinky"),
					dto = {element: foo,
						attributes: {rel: "static"},
						limitTagName: "SPAN",
						className: "daddyHadADonkey"},
					result = getAncestorOrSelf(dto);
				assert.isNull(result);
			},
			testTopLevel: function() {
				var foo = document.getElementById("foo"),
					dto = {element: foo, tagName: "div", limitTagName: "${wc.dom.html5.element.section}", outermost: true},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("moocowDiv", result.id);
			},
			testTopLevelWithClassName: function() {
				var foo = document.getElementById("outerStart"),
					dto = {element: foo, tagName: "div", className: "hoo", outermost: true},
					result = getAncestorOrSelf(dto);
				assert.strictEqual("boo2", result.id);
			},
			testGetAncestorMultiAttributeSingleLookup: function() {
				var start = document.getElementById("multistart"),
					expected = "multi",
					dto = {element: start, attributes: {"data-multi": "foo"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual(expected, result.id, "did not find the correct ancestor with one attribute from many");

			},
			testGetAncestorMultiAttributeSingleLookupNoMatch: function() {
				var start = document.getElementById("multistart"),
					dto = {element: start,
							attributes: {"data-multi": "foobar"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result, "did not fail to find the correct ancestor with one attribute from many" + result);
			},
			testGetAncestorMultiAttributeMultiLookup: function() {
				var start = document.getElementById("multistart"), expected = "multi",
					dto = {element: start,
							attributes: {"data-multi": "foo pong"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual(expected, result.id, "did not find the correct ancestor with some attributes from many");
			},
			testGetAncestorMultiAttributeMultiLookupAllOutOfOrder: function() {
				var start = document.getElementById("multistart"), expected = "multi",
					dto = {element: start,
							attributes: {"data-multi": "bar ping foo pong"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual(expected, result.id, "did not find the correct ancestor with all attributes from many in different order");
			},
			testGetAncestorMultiAttributeMultiLookupNoMatch: function() {
				var start = document.getElementById("multistart"),
					dto = {element: start,
							attributes: {"data-multi": "foobar foo bar"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result, "did not fail to find the correct ancestor with several attributes from many" + result);
			},
			testGetAncestorMultiAttributeMultiLookupNoMatchTooManyAttributes: function() {
				var start = document.getElementById("multistart"),
					dto = {element: start,
							attributes: {"data-multi": "foobar ping pong foo bar"}},
					result = getAncestorOrSelf(dto);
				assert.isNull(result, "did not fail to find the correct ancestor with several attributes from many" + result);
			},
			testGetSelfMultiAttributeSingleLookup: function() {
				var start = document.getElementById("multi"), expected = "multi",
					dto = {element: start,
							attributes: {"data-multi": "foo"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual(expected, result.id, "did not find the correct ancestor with one attribute from many");
			},
			testGetSelfMultiAttributeMultiLookup: function() {
				var start = document.getElementById("multi"), expected = "multi",
					dto = {element: start,
							attributes: {"data-multi": "foo pong"}},
					result = getAncestorOrSelf(dto);
				assert.strictEqual(expected, result.id, "did not find the correct ancestor with one attribute from many");
			}
		});
	});
