define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var classlist,
			testHolder,
			DUMMY_CLASS = "handsallaroundjingjang",
			SPACE = /\s+/,
			urlResource = "@RESOURCES@/domClassList.html";

		function getElementWithClass(multiple) {
			var candidates = testHolder.getElementsByTagName("*"), i, next, result,
				className;
			for (i = 0; i < candidates.length; ++i) {
				next = candidates[i];
				if (!(className = next.className)) {
					continue;
				}
				if (!(multiple ^ SPACE.test(className))) {
					result = next;
					break;
				}
			}
			if (!result) {
				assert.fail("Could not find an element without a class name");
			}
			return result;
		}

		function getElementWithNoClass() {
			var candidates = testHolder.getElementsByTagName("*"), i, next, result;
			for (i = 0; i < candidates.length; ++i) {
				next = candidates[i];
				if (!next.className) {
					result = next;
					break;
				}
			}
			if (!result) {
				assert.fail("Could not find an element without a class name");
			}
			return result;
		}

		registerSuite({
			name: "domClassList",
			setup: function() {
				return testutils.setupHelper(["wc/dom/classList"], function(obj) {
					classlist = obj;
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				return testutils.setUpExternalHTML(urlResource, testHolder);
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testLength: function() {
				var element = getElementWithClass(true),
					expected = element.className.split(SPACE).length;
				assert.strictEqual(expected, element.classList.length, "getLength returned unexpected result");
			},
			testLengthClassNoValue: function() {
				var id = "test_par",  // element with class attribute with no value
					expected = 0,
					element = document.getElementById(id);
				assert.strictEqual(expected, element.classList.length, "getLength returned unexpected result");
			},
			testLengthNoClass: function() {
				var element = getElementWithNoClass();
				assert.strictEqual(0, element.classList.length, "getLength returned unexpected result");
			},
			testItemMultiple: function() {
				var element = getElementWithClass(true), i = 0,
					expected = element.className.split(SPACE);
				do {
					assert.strictEqual(expected[i], element.classList.item(i), "item returned unexpected result");
				} while (expected[ ++i ]);
			},
			testItemNoClassValue: function() {
				var id = "test_par",  // element with class attribute with no value
					element = document.getElementById(id);
				assert.isNull(element.classList.item(0), "item returned unexpected result");
			},
			testContains: function() {
				var element = getElementWithClass(false);  // element with single class value in className
				assert.isTrue(element.classList.contains(element.className));
			},
			testContainsPartialNoMatch: function() {
				var element = getElementWithNoClass(),
					testFor = "foo";
				element.className = "food foot fool";
				assert.isFalse(element.classList.contains(testFor));
			},
			testContainsInvalidCharacterError: function() {
				var className = "foo bar",  // contains whitespace
					element = getElementWithNoClass();
				try {
					element.className = className;
					element.classList.contains(className);
					assert.fail("testing expected with token that contains whitespace should throw an error");
				}
				catch (e) {
					assert.isTrue(true);
				}
			},
			testContainsSyntaxError: function() {
				var className = "",  // token is empty string
					element = getElementWithNoClass();
				try {
					element.setAttribute("class", className);
					element.classList.contains(className);
					assert.fail("testing expected with empty string should throw an error");
				}
				catch (e) {
					assert.isTrue(true);
				}
			},
			testHasClassSimpleNoMatch: function() {
				var element = getElementWithClass(false),
					className = DUMMY_CLASS;
				assert.isFalse(element.classList.contains(className));
			},
			testHasClassPreciseMatchSingle: function() {
				var element = getElementWithClass(false);
				assert.isTrue(element.classList.contains(element.className));
			},
			testHasClassPreciseMatchMultiple: function() {
				var element = getElementWithClass(true),
					className = element.className.split(SPACE), i;
				for (i = 0; i < className.length; ++i) {
					assert.isTrue(element.classList.contains(className[i]));
				}
			},
			testHasClassPreciseNoMatch: function() {
				var element = getElementWithClass(true);
				assert.isFalse(element.classList.contains(DUMMY_CLASS));
			},
			testHasClassPreciseNoMatchNoClass: function() {
				var element = getElementWithNoClass();  // an element with no classes set on it
				assert.isFalse(element.classList.contains(DUMMY_CLASS));
			},
			testGetClassSimple: function() {
				var element = getElementWithClass(false);
				assert.strictEqual(String(element.className), String(element.classList));
			},
			testGetClassMultiple: function() {
				var element = getElementWithClass(true);
				assert.strictEqual(String(element.className), String(element.classList));
			},
			testGetClassNone: function() {
				var element = getElementWithNoClass();
				assert.strictEqual(String(element.className), String(element.classList));
			},
			testAddClassSimple: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				element.classList.add(className);  // add the new class
				assert.isTrue(element.classList.contains(className));
			},
			testAddClassTwice: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				element.classList.add(className);  // add the new class
				element.classList.add(className);  // add the new class again should not have any affect
				assert.strictEqual(element.className.indexOf(className), element.className.lastIndexOf(className), "Expected to only set the same class once");
			},
			testAddTwoClasses: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS,
					className1 = "HandsallaroundjingjanG";
				element.classList.add(className);  // add the new class
				element.classList.add(className1);  // add the new class
				assert.isTrue(element.classList.contains(className));
			},
			testAddTwoClassesHasBoth: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS,
					className1 = "HandsallaroundjingjanG";
				element.classList.add(className);  // add the new class
				element.classList.add(className1);  // add the new class
				assert.isTrue(element.classList.contains(className1));
			},
			testAddTwoClassesKeepsPrevious: function() {
				var element = getElementWithClass(false),
					previous = element.className,
					className = DUMMY_CLASS,
					className1 = "HandsallaroundjingjanG";
				element.classList.add(className);  // add the new class
				element.classList.add(className1);  // add the new class
				assert.isTrue(element.classList.contains(previous), "Expected to hasClass to leave existing class in place");
			},
			testRemoveClassSimple: function() {
				var element = getElementWithClass(false),
					className = element.className;
				element.classList.remove(className);
				assert.isFalse(element.classList.contains(className));
			},
			testRemoveClassTwice: function() {
				var element = getElementWithClass(false),
					// className = element.className,
					newClass = DUMMY_CLASS;
				element.classList.add(newClass);
				element.classList.remove(newClass);
				element.classList.remove(newClass);
				assert.isFalse(element.classList.contains(newClass));
			},
			testRemoveClassTwiceKeepsPrevious: function() {
				var element = getElementWithClass(false),
					className = element.className,
					newClass = DUMMY_CLASS;
				element.classList.add(newClass);
				element.classList.remove(newClass);
				element.classList.remove(newClass);
				assert.isTrue(element.classList.contains(className));
			},
			testToggleClassToAdd: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				element.classList.toggle(className);
				assert.isTrue(element.classList.contains(className), "Expected the classname to be toggled");
			},
			testToggleTwice: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				element.classList.toggle(className);
				element.classList.toggle(className);
				assert.isFalse(element.classList.contains(className), "Expected the classname to be toggled");
			},
			testToggleToRemove: function() {
				var element = getElementWithClass(false),
					className = element.className;
				element.classList.toggle(className);
				assert.isFalse(element.classList.contains(className), "Expected the classname to be toggled");
			},
			testToString: function() {
				var element = getElementWithClass(true),
					expected = element.className;
				assert.strictEqual(expected, element.classList.toString(), "toString returned unexpected result");
			},
			testToStringNoValue: function() {
				var id = "test_par",
					expected = "",
					element = document.getElementById(id);
				assert.strictEqual(expected, element.classList.toString(), "toString returned unexpected result");
			},
			testToStringNoClass: function() {
				var expected = "",
					element = getElementWithNoClass();
				assert.strictEqual(expected, element.classList.toString(), "toString returned unexpected result");
			},
			testLibraryContains: function() {
				var element = getElementWithClass(false);
				assert.isTrue(classlist.contains(element, element.className));
			},
			testLibraryContainsPartialNoMatch: function() {
				var element = getElementWithNoClass();
				element.className = "food foot fool";
				assert.isFalse(classlist.contains(element, "foo"));
				assert.isFalse(classlist.contains(element, "ood"));
			},
			testLibraryHasClassSimpleNoMatch: function() {
				var element = getElementWithClass(false),
					className = DUMMY_CLASS;
				assert.isFalse(classlist.contains(element, className));
			},
			testLibraryHasClassPreciseMatch: function() {
				var element = getElementWithClass(false);
				assert.isTrue(classlist.contains(element, element.className));
			},
			testLibraryHasClassPreciseMatchMultiple: function() {
				var element = getElementWithClass(true),
					className = element.className.split(SPACE), i;
				for (i = 0; i < className.length; ++i) {
					assert.isTrue(classlist.contains(element, className[i]));
				}
			},
			testLibraryHasClassPreciseNoMatch: function() {
				var element = getElementWithClass(true);
				assert.isFalse(classlist.contains(element, DUMMY_CLASS));
			},
			testLibraryHasClassPreciseNoMatchNoClass: function() {
				var element = getElementWithNoClass();  // an element with no classes set on it
				assert.isFalse(classlist.contains(element, DUMMY_CLASS));
			},
			testLibraryGetClassSimple: function() {
				var element = getElementWithClass(false);
				assert.strictEqual(String(element.className), String(classlist.toString(element)));
			},
			testLibraryGetClassMultiple: function() {
				var element = getElementWithClass(true);
				assert.strictEqual(String(element.className), String(classlist.toString(element)));
			},
			testLibraryGetClassNone: function() {
				var element = getElementWithNoClass();
				assert.strictEqual(String(element.className), String(classlist.toString(element)));
			},
			testLibraryAddClassSimple: function() {
				var element = getElementWithNoClass(),
					// returnedValue = null,
					className = DUMMY_CLASS;
				classlist.add(element, className);  // add the new class
				assert.strictEqual(className, element.className);
			},
			testLibraryAddClassTwice: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				classlist.add(element, className);  // add the new class
				classlist.add(element, className);  // add the new class again should not have any affect
				assert.strictEqual(element.className.indexOf(className), element.className.lastIndexOf(className), "Expected to only set the same class once");
			},
			testLibraryAddTwoClassesConsecutively: function() {
				var element = getElementWithClass(false),
					className = DUMMY_CLASS,
					className1 = "HandsallaroundjingjanG";
				classlist.add(element, className);
				classlist.add(element, className1);
				assert.isTrue(classlist.contains(element, className1));
			},
			testLibraryAddTwoClassesConsecutivelyKeepsPrevious: function() {
				var element = getElementWithClass(false),
					elClass = element.className,
					className = DUMMY_CLASS,
					className1 = "HandsallaroundjingjanG";
				classlist.add(element, className);  // add the new class
				classlist.add(element, className1);  // add the new class
				assert.isTrue(classlist.contains(element, elClass), "Expected to hasClass to leave existing class in place");
			},
			testLibraryRemoveClassSimple: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				element.className = className;
				classlist.remove(element, className);
				assert.isFalse(classlist.contains(element, className));
			},
			testLibraryRemoveClassTwice: function() {
				var element = getElementWithClass(false),
					// elClass = element.className,
					className = DUMMY_CLASS;
				classlist.add(element, className);  // add the new class
				classlist.remove(element, className);
				classlist.remove(element, className);
				assert.isFalse(classlist.contains(element, className));
			},
			testLibraryRemoveClassTwiceKeepsExisting: function() {
				var element = getElementWithClass(false),
					elClass = element.className,
					className = DUMMY_CLASS;
				classlist.add(element, className);  // add the new class
				classlist.remove(element, className);
				classlist.remove(element, className);
				assert.isTrue(classlist.contains(element, elClass));
			},
			testLibraryToggleClassAdds: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				classlist.toggle(element, className);
				assert.isTrue(classlist.contains(element, className), "Expected the classname to be toggled");
			},
			testLibraryDoubleToggleClassRemoves: function() {
				var element = getElementWithNoClass(),
					className = DUMMY_CLASS;
				classlist.toggle(element, className);
				classlist.toggle(element, className);
				assert.isFalse(classlist.contains(element, className), "Expected the classname to be toggled");
			},
			testLibraryToggleClassRemovesIfThere: function() {
				var element = getElementWithClass(false),
					className = element.className;
				classlist.toggle(element, className);
				assert.isFalse(classlist.contains(element, className), "Expected the classname to be toggled");
			},
			testLibraryDoubleToggleClassAddsIfThere: function() {
				var element = getElementWithClass(false),
					className = element.className;
				classlist.toggle(element, className);
				classlist.toggle(element, className);
				assert.isTrue(classlist.contains(element, className), "Expected the classname to be toggled");
			},
			testLibraryContainsWithObjectElement: function() {
				// essential test in IE
				var element = document.getElementById("obj1");
				assert.isTrue(classlist.contains(element, "cowa"));
			},
			testLibraryLength: function() {
				var element = getElementWithClass(true),
					expected = element.className.split(SPACE).length;
				assert.strictEqual(expected, classlist.getLength(element), "getLength returned unexpected result");
			},
			testLibraryLengthClassNoValue: function() {
				var id = "test_par",
					expected = 0,
					element = document.getElementById(id);
				assert.strictEqual(expected, classlist.getLength(element), "getLength returned unexpected result");
			},
			testLibraryLengthNoClass: function() {
				var element = getElementWithNoClass();
				assert.strictEqual(0, classlist.getLength(element), "getLength returned unexpected result");
			},
			testLibraryItem: function() {
				var element = getElementWithClass(true), i = 0,
					expected = element.className.split(SPACE);
				do {
					assert.strictEqual(expected[i], classlist.item(element, i), "item returned unexpected result");
				} while (expected[ ++i ]);
			},
			testLibraryItemNoClassValue: function() {
				var id = "test_par",
					element = document.getElementById(id);
				assert.isNull(classlist.item(element, 0), "item returned unexpected result");
			},
			testLibraryContainsNoValueIsNotEmptyString: function() {
				var id = "test_par",
					expected = "",
					element = document.getElementById(id);
				try {
					classlist.contains(element, expected);
					assert.fail("testing expected with empty string should throw an error");
				}
				catch (e) {
					assert.isTrue(true);
				}
			},
			testLibraryToString: function() {
				var element = getElementWithClass(true);
				assert.strictEqual(element.className, classlist.toString(element), "toString returned unexpected result");
			},
			testLibraryToStringNoValue: function() {
				var id = "test_par",
					expected = "",
					element = document.getElementById(id);
				assert.strictEqual(expected, classlist.toString(element), "toString returned unexpected result");
			},
			testLibraryToStringNoClass: function() {
				var element = getElementWithNoClass();
				assert.strictEqual("", classlist.toString(element), "toString returned unexpected result");
			},
			testInternalGetElementWithNoClass: function() {
				var element = getElementWithNoClass();
				assert.isTrue(element.className === "");
			},
			testInternalGetElementWithClassSingle: function() {
				var element = getElementWithClass();
				assert.isTrue(element.className.length > 0);
				assert.isFalse(SPACE.test(element.className));
				assert.isTrue(element.className.split(SPACE).length === 1);
			},
			testInternalGetElementWithClassMultiple: function() {
				var element = getElementWithClass(true);
				assert.isTrue(element.className.length > 0);
				assert.isTrue(SPACE.test(element.className));
				assert.isTrue(element.className.split(SPACE).length > 1);
			}
		});
	});
