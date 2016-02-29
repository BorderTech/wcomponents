/* eslint no-new-wrappers:0 */
define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		/**
		 * Note
		 * You can't test get without testing set and vice versa
		 * You can't test remove without testing get and set
		 * You can't test has without testing set
		 */
		var attribute,
			KEY = "test.attribute.key",
			ID = "semperfi",
			VAL = "foobar",
			OBJ = {"foo": "bar"},
			testHolder,
			urlResource = "@RESOURCES@/domAttribute.html";

		registerSuite({
			name: "domAttribute",
			setup: function() {
				return testutils.setupHelper(["wc/dom/attribute"], function(obj) {
					attribute = obj;
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				return testutils.setUpExternalHTML(urlResource, testHolder);
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			teardown: function() {
				/*
				 * this is a bit superfluous since we blow away each iteration of element but
				 * that assumption is based on the test testHasAttributeNewDomNodeInnerHTML
				 * below always passing. This will at least clean up the mess for other
				 * tests if attribute changes in such a way that an element with the same
				 * id retains custom attributes even when it is not the same element.
				 */
				var element = document.getElementById(ID);
				if (element) {
					attribute.remove(element, KEY, true);
				}
				testHolder.innerHTML = "";
			},
			testElementExists: function() {
				/*
				 * OK, so this is really pointless but is is still an assumption in
				 * all following tests. If the innerHTML of testHolder changes or if
				 * the utility function to get testHolder changes such that setting its
				 * innerHTML fails then this test may fail and it is better to have
				 * an explicit fail than just get errors in the following tests.
				 */
				var element = document.getElementById(ID);
				assert.isNotNull(element);
			},
			testHasNotSet: function() {
				var element = document.getElementById(ID);
				assert.isFalse(attribute.has(element, KEY));
			},
			testHasAfterSet: function() {
				var element = document.getElementById(ID);
				attribute.set(element, KEY, false);
				assert.isTrue(attribute.has(element, KEY));
			},
			testSetReturnsSetValue: function() {
				var element = document.getElementById(ID),
					darts = Math.round(Math.random() * 180);
				assert.strictEqual(darts, attribute.set(element, KEY, darts));
			},
			testSetReturnsSetValueString: function() {
				var element = document.getElementById(ID),
					expected = Math.random().toString(36).substr(2, 5);
				assert.strictEqual(expected, attribute.set(element, KEY, expected));
			},
			testSetReturnsSetValueObject: function() {
				var element = document.getElementById(ID),
					expected = OBJ;
				assert.strictEqual(OBJ, attribute.set(element, KEY, expected));
			},
			testSetReturnsSetValueObjectExact: function() {
				var element = document.getElementById(ID),
					expected = OBJ;
				assert.isTrue(OBJ === attribute.set(element, KEY, expected));
			},
			testGetNotSet: function() {
				/* no element should have an attribute without it being set and get shoudl return null*/
				var element = document.getElementById(ID);
				assert.isNull(attribute.get(element, KEY));
			},
			testGetSetAttributePrimitive: function() {
				var element = document.getElementById(ID);
				attribute.set(element, KEY, false);
				assert.isFalse(attribute.get(element, KEY));
			},
			testGetSetAttributePrimitiveNumber: function() {
				var element = document.getElementById(ID);
				attribute.set(element, KEY, 0);
				assert.strictEqual(0, attribute.get(element, KEY));
			},
			testGetSetAttributeObject: function() {
				var element = document.getElementById(ID),
					expected = OBJ;
				attribute.set(element, KEY, expected);
				assert.strictEqual(expected, attribute.get(element, KEY));
			},
			testGetSetAttributeObjectExactInstance: function() {
				var element = document.getElementById(ID),
					expected = OBJ;
				attribute.set(element, KEY, expected);
				assert.isTrue(expected === attribute.get(element, KEY));
			},
			testGetAttributeWithObjectReturnsSameInstance: function() {
				/* set of set/get tests with simple object to test that we get a particular instance of an object */
				var val = new String(VAL),
					element = document.getElementById(ID);
				attribute.set(element, KEY, val);
				assert.isTrue(val === attribute.get(element, KEY));
			},
			testGetAttributeWithObjectReturnsSameInstanceWithDifferentInstance: function() {
				var val = new String(VAL),
					element = document.getElementById(ID);
				attribute.set(element, KEY, val);
				assert.isTrue( VAL !== attribute.get(element, KEY));
			},
			testGetAttributeSameInstanceSimpleMatch: function() {
				/* the objects are different but are they still the same string? */
				var val = new String(VAL),
					element = document.getElementById(ID);
				attribute.set(element, KEY, val);
				// assert.isTrue(VAL == attribute.get(element, KEY));  // double equals
				assert.equal(VAL, attribute.get(element, KEY));
			},
			testSetAttributeNewValue: function() {
				/* change the set value */
				var element = document.getElementById(ID);
				attribute.set(element, KEY, false);
				// assert.assertFalse(attribute.get(element, KEY)); - from earlier test
				attribute.set(element, KEY, VAL);
				assert.strictEqual(VAL, attribute.get(element, KEY));
			},
			testRemoveNotSetReturnsFalse: function() {
				var element = document.getElementById(ID);
				assert.isFalse(attribute.remove(element, KEY));
			},
			testRemoveAfterSetReturnsTrue: function() {
				var element = document.getElementById(ID);
				attribute.set(element, KEY, false);
				assert.isTrue(attribute.remove(element, KEY));
			},
			testGetAfterRemoveAttribute: function() {
				var element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				attribute.remove(element, KEY);
				assert.isNull(attribute.get(element, KEY));
			},
			testHasAttributeNewDomNodeInnerHTML: function() {
				/* --------------------------------------------------------------------
				* inheritance (or lack of)
				* When an element is cloned, replaced (even with same HTML) it should
				* not 'inherit' custom attributes.
				*/
				var element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				// Commented below - Check with Rick
				element.parentNode.innerHTML = element.parentNode.innerHTML;
				element = document.getElementById(ID);
				assert.isFalse(attribute.has(element, KEY), "element should not have inherited custom attribute");
			},
			testHasAttributeNewDomNodeDomMethodsCloneNode: function() {
				var newElement, element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				newElement = element.cloneNode(true);
				assert.isFalse(attribute.has(newElement, KEY), "new element should not have inherited custom attribute (clone)");
			},
			testHasAttributeNewDomNodeDomMethodsReplaceChild: function() {
				var newElement, element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				newElement = element.cloneNode(true);
				element.parentNode.replaceChild(newElement, element);
				assert.isFalse(attribute.has(newElement, KEY), "new element should not have inherited custom attribute (clone and replace node)");
			},
			testGetAttributeNewDomNodeDomMethodsClone: function() {
				var newElement, element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				newElement = element.cloneNode(true);
				assert.isNull(attribute.get(newElement, KEY), "new element should not have inherited custom attribute (clone)");
			},
			testGetAttributeNewDomNodeDomMethodsCloneAndReplace: function() {
				var newElement, element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				newElement = element.cloneNode(true);
				element.parentNode.replaceChild(newElement, element);
				assert.isNull(attribute.get(newElement, KEY), "new element should not have inherited custom attribute (clone and replace node)");
			},
			testGetAttributeNewDomNodeDomMethodsOriginalKeepsAttribute: function() {
				var newElement, element = document.getElementById(ID);
				attribute.set(element, KEY, VAL);
				newElement = element.cloneNode(true);
				element.parentNode.replaceChild(newElement, element);
				assert.strictEqual(VAL, attribute.get(element, KEY), "original element should still have a custom attribute");
			},
			testGetAttributeClonedNodesNotSame: function() {
				/*
				* Test of assumption in following tests.
				*
				* It should be obvious that when an element is replaced (directly or by
				* replacing its parent) that the new element with the same ID as the
				* previous element is not the same as the previous element.
				* This test is used to check that cloning a parentNode and replacing the
				* original with its clone causes the new parent's children to be different
				* from the original parent's children. This is normal behaviour but
				* we need to be certain that the behaviour has not changed because if it
				* ever does then the subsequent tests will be invalid. */
				var newElement,
					element = document.getElementById(ID),
					parent = element.parentNode;
				parent.parentNode.replaceChild(parent.cloneNode(true), parent);
				newElement = document.getElementById(ID);
				assert.isFalse(element === newElement);
			},
			testGetAttributeNewDomNodeDomMethodsOnParentClone: function() {
				var newElement, element = document.getElementById(ID),
					parent = element.parentNode;
				attribute.set(element, KEY, VAL);
				parent.parentNode.replaceChild(parent.cloneNode(true), parent);
				newElement = document.getElementById(ID);
				assert.isNull(attribute.get(newElement, KEY), "new element should not have inherited custom attribute (cloned parent)");
			},
			testGetAttributeNewDomNodeDomMethodsOnParentOriginalKeepsAttribute: function() {
				var element = document.getElementById(ID),
					parent = element.parentNode;
				attribute.set(element, KEY, VAL);
				parent.parentNode.replaceChild(parent.cloneNode(true), parent);
				// newElement = document.getElementById(ID);
				assert.strictEqual(VAL, attribute.get(element, KEY), "original element should still have a custom attribute");
			}
		});
	});
