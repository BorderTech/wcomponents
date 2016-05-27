define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var Widget, allDivs, fooDiv, barDiv, mooDiv, staticDiv, monkeyDiv, barfooDiv, fooSpan, allAnchors, allBars, allMoos,
		allFooBarDivs, mooDivInFooDiv, mooDivInFooDivImmediate, barSpanInMooDivInFooDiv, allStaticAnchors,
		allStaticBartAnchors, allStaticBartAnchorsWithAName, allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo,
		staticNamedAnchor, matchId, allNamedElements, allElementsWithId, a3Owner, testHolder,
		urlResource = "@RESOURCES@/domWidget.html";


	registerSuite({
		name: "Widget",
		setup: function() {
			var result = testutils.setupHelper(["wc/dom/Widget"]).then(function(arr) {
				Widget = arr[0];
				testHolder = testutils.getTestHolder();

				allDivs = new Widget("div");
				fooDiv = allDivs.extend("foo");
				barDiv = allDivs.extend("bar");
				mooDiv = allDivs.extend("moo");
				staticDiv = allDivs.extend("static");
				monkeyDiv = allDivs.extend("monkey");
				barfooDiv = allDivs.extend("barfoo");
				fooSpan = new Widget("span", "foo");
				allAnchors = new Widget("a");
				allBars = new Widget("", "bar");
				allMoos = new Widget("", "moo");
				allFooBarDivs = allDivs.extend(["foo", "bar"]);
				mooDivInFooDiv = allDivs.extend("moo");
				mooDivInFooDivImmediate = allDivs.extend("moo");
				barSpanInMooDivInFooDiv = new Widget("span", "bar");
				allStaticAnchors = allAnchors.extend("", {
					rel: "static"
				});
				allStaticBartAnchors = allStaticAnchors.extend("bart");
				allStaticBartAnchorsWithAName = allStaticBartAnchors.extend("", {
					name: null
				});
				allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo = allStaticBartAnchors.extend("", {
					name: null
				});
				staticNamedAnchor = new Widget("a", "", {
					rel: "static",
					name: null
				});
				matchId = new Widget("", "", {
					id: "greg"
				});
				allNamedElements = new Widget("", "", {
					"name": null
				});
				allElementsWithId = new Widget("", "", {
					"id": null
				});
				a3Owner = new Widget("", "", {
					"aria-owns": "a3"
				});

				/* these constrain the ancestor lookups */
				mooDivInFooDiv.descendFrom(fooDiv);
				mooDivInFooDivImmediate.descendFrom(fooDiv, true);
				barSpanInMooDivInFooDiv.descendFrom(mooDivInFooDiv);
				allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo.descendFrom(mooDivInFooDiv, true);

				return testutils.setUpExternalHTML(urlResource, testHolder);
			});
			return result;
		},
		teardown: function() {
			testHolder.innerHTML = "";
		},

		testDescendantsWithTagAndClassName: function() {
			var result = fooDiv.findDescendants(testHolder),
				expectedLength = 8;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantsImmediateWithTagAndClassName: function() {
			var result = fooDiv.findDescendants(testHolder, true),
				expectedLength = 6;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagAndClassName: function() {
			var result = fooDiv.findDescendant(testHolder),
				expectedId = "firstFooDiv";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantImmediateWithTagAndClassName: function() {
			var result = fooDiv.findDescendant(testHolder, true),
				expectedId = "firstFooDiv";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantsWithTagAndAttributes: function() {
			var result = allStaticAnchors.findDescendants(testHolder),
				expectedLength = 4;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantsWithTagAndClassNameAndAttributes: function() {
			var result = allStaticBartAnchors.findDescendants(testHolder),
				expectedLength = 2;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantsWithTagAndClassNameAndMultipleAttributes: function() {
			var result = allStaticBartAnchorsWithAName.findDescendants(testHolder),
				expectedLength = 1;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantsWithEverything: function() {
			// multiple attributes, multiple extends, multiple descendFrom, immediate descendFrom, etc etc
			var result = allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo.findDescendants(testHolder),
				expectedLength = 1;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagAndAttributes: function() {
			var result = allStaticAnchors.findDescendant(testHolder);
			assert.strictEqual(result.id, "a1");
		},

		testDescendantWithTagAndMultipleAttributes: function() {
			var result = staticNamedAnchor.findDescendant(testHolder);
			assert.strictEqual(result.id, "a4");
		},

		testDescendantWithTagAndClassNameAndAttributes: function() {
			var result = allStaticBartAnchors.findDescendant(testHolder);
			assert.strictEqual(result.id, "a2");
		},

		testDescendantWithTagAndClassNameAndMultipleAttributes: function() {
			var result = allStaticBartAnchorsWithAName.findDescendant(testHolder);
			assert.strictEqual(result.id, "a2");
		},

		testDescendantsWithTagAndClassNameArray: function() {
			var result = allFooBarDivs.findDescendants(testHolder),
				expectedLength = 2;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagAndClassNameArray: function() {
			var result = allFooBarDivs.findDescendant(testHolder),
				expectedId = "firstFooDiv";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantsWithTagAndClassNameOneOnly: function() {
			var result = fooSpan.findDescendants(testHolder),
				expectedLength = 1;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantsImmediateWithTagAndClassNameNoMatch: function() {
			var result = fooSpan.findDescendants(testHolder, true),
				expectedLength = 0;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagAndClassNameOneOnly: function() {
			var result = fooSpan.findDescendant(testHolder),
				expectedId = "onlyFooSpan";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantsWithTagAndClassNameNoMatch: function() {
			var result = barfooDiv.findDescendants(testHolder),
				expectedLength = 0;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagAndClassNameNoMatch: function() {
			var result = barfooDiv.findDescendant(testHolder);
			assert.isNull(result);
		},

		testDescendantsWithTagName: function() {
			var result = allAnchors.findDescendants(testHolder),
				expectedLength = 6;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithTagName: function() {
			var result = allAnchors.findDescendant(testHolder),
				expectedId = "a1";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantsWithClassName: function() {
			var result = allBars.findDescendants(testHolder),
				expectedLength = 6;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendantWithClassName: function() {
			var result = allBars.findDescendant(testHolder),
				expectedId = "firstFooDiv";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantWithId: function() {
			var result = matchId.findDescendant(testHolder),
				expectedId = "greg";
			assert.strictEqual(result.id, expectedId);
		},

		testDescendantWithIdNoMatchSelf: function() {
			/*
			 * Broken in Firefox 10
			 * Reported to Mozilla 22/03/2012: https://bugzilla.mozilla.org/show_bug.cgi?id=738108
			 */
			var result = matchId.findDescendant(document.getElementById("greg"));
			assert.isNull(result);
		},

		testAncestorWithTagAndClassName: function() {
			var element = document.getElementById("cindy"),
				result = fooDiv.findAncestor(element),
				expected = "greg";
			assert.strictEqual(result.id, expected);
		},

		testAncestorWithTagAndClassNameAsTree: function() {
			var element = document.getElementById("cindy"),
				result = fooDiv.findAncestor(element, "", true),
				expected = "greg";
			assert.strictEqual(result[0].id, expected);
		},

		testAncestorWithTagAndClassNameAsTreeNoMatch: function() {
			var element = document.getElementById("onlyFooSpan"),
				result = fooDiv.findAncestor(element, "", true);
			assert.isNull(result);
		},

		testAncestorWithContainerAsTree: function() {
			var element = document.getElementById("a1"),
				result = mooDivInFooDiv.findAncestor(element, "", true),
				expected0 = "moo1",
				expected1 = "dd22";
			assert.strictEqual(result[0].id, expected0);
			assert.strictEqual(result[1].id, expected1);
		},

		testAncestorWithMultipleContainersAsTree: function() {
			var element = document.getElementById("a3"),
				result = barSpanInMooDivInFooDiv.findAncestor(element, "", true),
				expected0 = "ss44",
				expected1 = "moo3",
				expected2 = "cantThinkOfMoreIds";
			assert.strictEqual(result[0].id, expected0);
			assert.strictEqual(result[1].id, expected1);
			assert.strictEqual(result[2].id, expected2);
		},

		testAncestorWithTagAndClassNameAndLimit: function() {
			var element = document.getElementById("cindy"),
				result = fooDiv.findAncestor(element, "P");
			assert.isNull(result);
		},

		testAncestorMatchSelf: function() {
			var element = document.getElementById("a1"),
				result = allAnchors.findAncestor(element),
				expected = "a1";
			assert.strictEqual(result.id, expected);
		},

		testAncestorWithTagAndClassNameNoMatch: function() {
			var element = document.getElementById("a1"),
				result = fooSpan.findAncestor(element);
			assert.isNull(result);
		},

		testAncestorWithClassName: function() {
			var element = document.getElementById("onlyFooSpan"),
				result = allBars.findAncestor(element),
				expected = "dd33";
			assert.strictEqual(result.id, expected);
		},

		testAncestorWithClassNameArray: function() {
			var element = document.getElementById("ss11"),
				result = allFooBarDivs.findAncestor(element),
				expected = "firstFooDiv";
			assert.strictEqual(result.id, expected);
		},

		testAncestorWithExtends: function() {
			var element = document.getElementById("omfg3"),
				result = allStaticBartAnchorsWithAName.findAncestor(element),
				expected = "a2";
			assert.strictEqual(result.id, expected);
		},

		testAncestorWithEverything: function() {
			var element = document.getElementById("omfg3"),
				result = allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo.findAncestor(element),
				expected = "a2";
			assert.strictEqual(result.id, expected);
		},

		testIsOneOfMeWithTagAndClassName: function() {
			var element = document.getElementById("onlyFooSpan");
			assert.isTrue(fooSpan.isOneOfMe(element));
		},

		testIsOneOfMeWithTagAndClassNameNoMatch: function() {
			var element = document.getElementById("firstFooDiv");
			assert.isFalse(fooSpan.isOneOfMe(element));
		},

		testIsOneOfMeWithTagName: function() {
			var element = document.getElementById("a1");
			assert.isTrue(allAnchors.isOneOfMe(element));
		},

		testIsOneOfMeWithClassName: function() {
			var element = document.getElementById("a1");
			assert.isTrue(allBars.isOneOfMe(element));
		},

		testIsOneOfMeWithId: function() {
			var element = document.getElementById("greg");
			assert.isTrue(matchId.isOneOfMe(element));
		},

		testIsOneOfMeWithIdNoMatch: function() {
			var element = document.getElementById("a1");
			assert.isFalse(matchId.isOneOfMe(element));
		},

		testStaticIsOneOfMeWithSingleInstance: function() {
			var element = document.getElementById("onlyFooSpan");
			assert.isTrue(Widget.isOneOfMe(element, fooSpan));
		},

		testStaticIsOneOfMeWithArraySIngleInstance: function() {
			var element = document.getElementById("onlyFooSpan");
			assert.isTrue(Widget.isOneOfMe(element, [fooSpan]));
		},

		testStaticIsOneOfMeWithArrayMultiple: function() {
			var element = document.getElementById("onlyFooSpan");
			assert.isTrue(Widget.isOneOfMe(element, [allAnchors, fooSpan]));
		},

		testStaticIsOneOfMeWithArrayMultipleNoMatch: function() {
			var element = document.getElementById("onlyFooSpan");
			assert.isFalse(Widget.isOneOfMe(element, [allAnchors, matchId]));
		},

		/*
		 * Up to WC11 isOneOfMe would fail if the widget contained an attribute
		 * and the element had multiple space separated values in its attribute.
		 * This meant we could get into the absurd situation of:
		 *    widget.isOneOfMe(widget.findDescendant(document.body))
		 * returning false.
		 * This test has been added to explicitly fail that case and test its
		 * replacement.
		 * @returns {undefined}
		 */
		testIsOneOfMeMultipleAttributeValue: function() {
			var element = document.getElementById("a5"),
				wd = new Widget(element.tagName, "", {
					"class": "bart"
				});
			assert.isTrue(wd.isOneOfMe(element));
		},

		// START toQs TESTS
		/*
		 * Ok, the "toQs" unit tests are a bit naughty and superfluous. They used to
		 * test "toQs" when it was public. Now it is private EXCEPT at the moment the
		 * "toString" of the Widget is the output of toQs.
		 *
		 * As long as toString is just toQs we may as well unit test it. If toString
		 * changes (which it is entirely allowed to do) then just remove these unit
		 * tests as they are not really necessary.
		 */
		testToQsWithTagAndClassName: function() {
			assert.strictEqual(fooDiv.toString(), "div.foo");
		},

		testToQsWithClassName: function() {
			assert.strictEqual(allBars.toString(), ".bar");
		},

		testToQsWithClassNameArray: function() {
			assert.strictEqual(allFooBarDivs.toString(), "div.foo.bar");
		},

		testToQsWithId: function() {
			assert.strictEqual(matchId.toString(), "#greg");
		},

		testToQsWithTagName: function() {
			assert.strictEqual(allAnchors.toString(), "a");
		},

		testToQsWithTagNameAndAttributes: function() {
			assert.strictEqual(allStaticAnchors.toString(), "a[rel~=\"static\"]", "didn't find expected element");
		},

		testToQsWithTagNameAndClassNameAndAttributes: function() {
			assert.strictEqual(allStaticBartAnchors.toString(), "a.bart[rel~=\"static\"]", "didn't find expected element");
		},

		testToQsWithTagNameAndMultipleAttributes: function() {
			var qs = allStaticBartAnchorsWithAName.toString(),
				equals = (qs === "a.bart[rel~=\"static\"][name]" || qs === "a.bart[name][rel~=\"static\"]");
			assert.isTrue(equals, "query selector not as expected: " + qs);
		},

		testToQsWithEverything: function() {
			var qs = allStaticBartAnchorsWithANameAndImmediateDescendMooInFoo.toString(),
				equals = (qs === "div.foo div.moo>a.bart[rel~=\"static\"][name]" || qs === "div.foo div.moo>a.bart[name][rel~=\"static\"]");
			assert.isTrue(equals, "query selector not as expected: " + qs);
		},

		testDescendFromToQs: function() {
			assert.strictEqual(mooDivInFooDiv.toString(), "div.foo div.moo");
		},

		// END toQs TESTS
		testExtendClassName: function() {
			var subClass = fooDiv.extend("bar"),
				element = document.getElementById("ss11"),
				result = subClass.findAncestor(element),
				expected = "firstFooDiv";
			assert.strictEqual(result.id, expected);
		},

		testExtendNothing: function() {
			try {
				fooDiv.extend();
				assert.fail(null, !null, "Shoulda got an exception extending WD with nothing");
			}
			catch (ex) {
				assert.isTrue(true, "Exception expected");  // no op - we expect this failure
			}
		},

		testExtendAttribute: function() {
			var subClass = fooDiv.extend("", {
					"aria-disabled": "false",
					id: null
				}),
				element = document.getElementById("a1"),
				result = subClass.findAncestor(element),
				expected = "dd22";
			assert.strictEqual(result.id, expected);
		},

		testExtendAttributeNoMatch: function() {
			var subClass = fooDiv.extend("", {
					"aria-disabled": "false"
				}),
				element = document.getElementById("ss11"),
				result = subClass.findAncestor(element);
			assert.isNull(result);
		},

		testDescendFromFindDescendants: function() {
			var result = mooDivInFooDiv.findDescendants(testHolder),
				expectedLength = 3;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendFromImmediateFindDescendants: function() {
			var result = mooDivInFooDivImmediate.findDescendants(testHolder),
				expectedLength = 1;
			assert.strictEqual(result.length, expectedLength);
		},

		testDescendFromIsOneOfMeTrue: function() {
			var element = document.getElementById("moo1");
			assert.isTrue(mooDivInFooDiv.isOneOfMe(element));
		},

		testDescendFromIsOneOfMeFalse: function() {
			var element = document.getElementById("moo2");
			assert.isFalse(mooDivInFooDiv.isOneOfMe(element));
		},

		testDescendFromImmediateIsOneOfMeTrue: function() {
			var element = document.getElementById("marcia");
			assert.isTrue(mooDivInFooDivImmediate.isOneOfMe(element));
		},

		testDescendFromImmediateIsOneOfMeFalse: function() {
			var element = document.getElementById("moo1");
			assert.isFalse(mooDivInFooDivImmediate.isOneOfMe(element));
		},

		testDescendFromAncestor: function() {
			var element = document.getElementById("a1"),
				result = mooDivInFooDiv.findAncestor(element),
				expected = "moo1";
			assert.strictEqual(result.id, expected);
		},

		testDescendFromAncestorNotFound: function() {
			var element = document.getElementById("ss33"),
				result = mooDivInFooDiv.findAncestor(element);
			assert.isNull(result);
		},

		testDescendFromAncestorSelf: function() {
			var element = document.getElementById("moo1"),
				result = mooDivInFooDiv.findAncestor(element),
				expected = "moo1";
			assert.strictEqual(result.id, expected);
		},

		testDescendFromDeepFindDescendants: function() {
			var result = barSpanInMooDivInFooDiv.findDescendants(testHolder),
				expectedLength = 1;
			assert.strictEqual(result.length, expectedLength);
		},

		/* do not fix the type in the test name!! */
		testCnostructor: function() {
			var threw = false;
			try {
				new Widget("", "");  // eslint-disable-line no-new
			}
			catch (ex) {
				threw = true;
			}
			assert.isTrue(threw);
		},

		testStaticFindDescendant: function() {
			var result = Widget.findDescendant(testHolder, [mooDiv, barDiv]),
				expectedId = "firstFooDiv";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantImmediate: function() {
			var result = Widget.findDescendant(testHolder, [mooDiv, mooDivInFooDivImmediate], true),
				expectedId = "marcia";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantNoTag: function() {
			var result = Widget.findDescendant(testHolder, [fooSpan, mooDivInFooDivImmediate]),
				expectedId = "onlyFooSpan";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantImmediateNoTag: function() {
			var result = Widget.findDescendant(testHolder, [fooSpan, mooDivInFooDivImmediate], true),
				expectedId = "marcia";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantAttribute: function() {
			var result = Widget.findDescendant(testHolder, [allStaticAnchors, matchId]),
				expectedId = "a1";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantAttributeImmediate: function() {
			var result = Widget.findDescendant(testHolder, [allStaticAnchors, matchId], true),
				expectedId = "greg";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantMixedDescriptors: function() {
			var result = Widget.findDescendant(testHolder, [barSpanInMooDivInFooDiv, allMoos, allStaticAnchors]),
				expectedId = "moo1";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendantMixedDescriptorsImmediate: function() {
			var result = Widget.findDescendant(testHolder, [barSpanInMooDivInFooDiv, allMoos, allStaticAnchors], true),
				expectedId = "ss44";
			assert.strictEqual(result.id, expectedId);
		},

		testStaticFindDescendants: function() {
			var result = Widget.findDescendants(testHolder, [barSpanInMooDivInFooDiv, mooDivInFooDiv]),
				expectedLength = 4;
			assert.strictEqual(result.length, expectedLength);
		},

		testStaticFindDescendantsImmediate: function() {
			var result = Widget.findDescendants(testHolder, [mooDiv, fooDiv, barDiv], true),
				expectedLength = 7;
			assert.strictEqual(result.length, expectedLength);
		},

		testStaticFindDescendantsImmediateNoTagDeepNest: function() {
			var result = Widget.findDescendants(testHolder, [barSpanInMooDivInFooDiv, mooDivInFooDiv], true),
				expectedLength = 4;
			assert.strictEqual(result.length, expectedLength);
		},

		testAllNamedElements: function() {
			var result = allNamedElements.findDescendants(testHolder),
				expectedLength = 3;
			assert.strictEqual(result.length, expectedLength);
		},

		testAllElementsWithId: function() {
			var result = allElementsWithId.findDescendants(testHolder);
			assert.isTrue(result.length > 0);
			for (var i = 0; i < result.length; i++) {
				assert.isTrue(!!result[i].id);
			}
		},

		testGetFirstAncestorWithAnyName: function() {
			var startAt = document.getElementById("omfg"),
				finishAt = allNamedElements.findAncestor(startAt),
				expectedId = "a3";
			assert.strictEqual(finishAt.id, expectedId);
		},

		testStaticFindAncestor: function() {
			var start = document.getElementById("staticStart"),
				result = Widget.findAncestor(start, staticDiv),
				expectedId = "static2";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testStaticFindAncestorWithArray: function() {
			var start = document.getElementById("staticStart"),
				result = Widget.findAncestor(start, [staticDiv, monkeyDiv]),
				expectedId = "static1";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testStaticFindAncestorOutermost: function() {
			var start = document.getElementById("staticStart"),
				result = Widget.findAncestor(start, staticDiv, {
					outermost: true
				}),
				expectedId = "static3";

			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testStaticFindAncestorOutermostWithArray: function() {
			var start = document.getElementById("staticStart"),
				result = Widget.findAncestor(start, [staticDiv, monkeyDiv], {
					outermost: true
				}),
				expectedId = "static3";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testStaticFindAncestorOutermostWithClassName: function() {
			var start = document.getElementById("staticStart"),
				result = Widget.findAncestor(start, [fooDiv, monkeyDiv], {
					outermost: true
				}),
				expectedId = "static2";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testFindAncestorOutermost: function() {
			var start = document.getElementById("staticStart"),
				result = allDivs.findAncestor(start, "${wc.dom.html5.element.section}", null, true),
				expectedId = "static3";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testFindAncestorOutermostWithClassName: function() {
			var start = document.getElementById("staticStart"),
				result = monkeyDiv.findAncestor(start, null, null, true),
				expectedId = "static2";
			assert.strictEqual(result.id, expectedId, "Didn't find the expected id ");
		},

		testMultipleValuedAttributeIsOneOfMe: function() {
			var expected = document.getElementById("static3");
			assert.strictEqual(a3Owner.findDescendant(testHolder), expected, "Element with multiple valued attribute is not a Widget with single attribute");
		},

		/*
		 * define an extension object including attributes outside of array iteration method
		 * then use it inside the iteration as part of widget.extend. This used to cause errors.
		 */
		testExtendWithAttribsInArrayIteration: function() {
			var expected, inputs, i,
				types = ["number", "email"],
				INPUT = [new Widget("input", "", { "type": "number"}), new Widget("input", "", { "type": "email"})],
				exObj = { "required": null };

			function _mapExtendWidget(next) {
				return next.extend("", exObj);
			}

			inputs = INPUT.map(_mapExtendWidget);
			for (i = 0; i < inputs.length; ++i) {
				expected = "input[required][type~=\"" + types[i] + "\"]";
				assert.strictEqual(inputs[i].toString(), expected, "Widget.toString not as expected");
			}
		},

		testClone : function() {
			var widget = new Widget("div"),
				widgetClone = widget.clone();

			assert.equal(widgetClone.toString(), widget.toString(), "original and clone should have same QS");

			widgetClone.descendFrom(fooDiv);

			assert.equal(widget.toString(), "div", "original widget should not be modified");
			assert.notEqual(widgetClone.toString(), "div", "clone widget should be modified");
		}
	});
});
