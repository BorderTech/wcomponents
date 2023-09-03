define(["intern!object", "intern/chai!assert", "wc/ui/icon", "intern/resources/test.utils"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			noIconId = "uiicontest1",
			withIconId = "uiicontest2",
			startIconClass = "fa-bars", // any non-empty String will work for these tests
			newIconClass = "fa-circle", // any non-empty String will work for these tests so long as it is different from startIconClass
			testContent = "<span id='" + noIconId + "'>content</span><span id='" + withIconId + "'><i class='fa " + startIconClass + "' aria-hidden='true'></i>content</span>";

		function getElement(withIcon) {
			return document.getElementById(withIcon ? withIconId : noIconId);
		}

		registerSuite({
			name: "wc/ui/icon",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testGetWidget_correctWidget: function() {
				const widget = controller.getWidget(),
					target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.matches(widget));
			},
			testGet_noArg: function() {
				try {
					controller.get();
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must be an HTML element");
				}
			},
			testGet_noElementArg: function() {
				try {
					controller.get("I am not an element");
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must be an HTML element");
				}
			},
			testGet: function() {
				var target = getElement(true),
					expected = target.firstElementChild;
				assert.equal(controller.get(target), expected);
			},
			testAdd: function() {
				var target = getElement();
				assert.isNotOk(target.firstElementChild);
				controller.add(target, newIconClass);
				assert.isTrue(target.firstElementChild.matches(controller.getWidget()));
			},
			testAdd_noArgs: function() {
				try {
					controller.add();
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "arguments must be defined");
				}
			},
			testAdd_noIconClass: function() {
				try {
					controller.add(getElement());
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "arguments must be defined");
				}
			},
			testAdd_elementNotElement: function() {
				try {
					controller.add("I am an element", newIconClass);
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must be an HTML element");
				}
			},
			testAdd_iconClassNotString: function() {
				try {
					controller.add(getElement(), {});
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "icon to add argument must be a String");
				}
			},
			testAdd_toIcon: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.add(icon, newIconClass);
				assert.isTrue(icon.classList.contains(newIconClass));
			},
			testAdd_toContainingElementWithIcon: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.add(target, newIconClass);
				assert.isTrue(icon.classList.contains(newIconClass));
			},
			testAdd_toContainingElementNoIcon: function() {
				var target = getElement(),
					icon = target.firstElementChild;
				assert.isNotOk(icon);
				controller.add(target, newIconClass);
				icon = target.firstElementChild;
				assert.isOk(icon);
				assert.isTrue(icon.classList.contains(newIconClass));
				assert.isTrue(icon.classList.contains("fa"));
			},
			testRemove_noArgs: function() {
				try {
					controller.remove();
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "arguments must be defined");
				}
			},
			testRemove_noIconClass: function() {
				try {
					controller.remove(getElement());
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "arguments must be defined");
				}
			},
			testRemove_elementNotElement: function() {
				try {
					controller.remove("I am an element", startIconClass);
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must be an HTML element");
				}
			},
			testRemove_iconClassNotString: function() {
				try {
					controller.remove(getElement(), {});
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "icon to remove argument must be a String");
				}
			},
			testRemove_multipleClasses: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				icon.classList.add(newIconClass);
				assert.isTrue(icon.classList.contains(startIconClass));
				controller.remove(icon, startIconClass);
				// remove only the class, not the icon element
				icon = target.firstElementChild;
				assert.isOk(icon);
				assert.isFalse(icon.classList.contains(startIconClass));
			},
			testRemove_lastIconClass: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isOk(icon);
				controller.remove(icon, startIconClass);
				icon = target.firstElementChild;
				assert.isNotOk(icon);
			},
			testRemove_withContainingElement: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				icon.classList.add(newIconClass);
				assert.isTrue(icon.classList.contains(startIconClass));
				controller.remove(target, startIconClass);
				// remove only the class, not the icon element
				icon = target.firstElementChild;
				assert.isOk(icon);
				assert.isFalse(icon.classList.contains(startIconClass));
			},
			testRemove_lastIconClassWithContainingElement: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isOk(icon);
				controller.remove(target, startIconClass);
				icon = target.firstElementChild;
				assert.isNotOk(icon);
			},
			testRemove_classNotPresent: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.remove(icon, newIconClass);
				icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
			},
			testChange_noIconArgs: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isUndefined(controller.change(target));
				assert.isTrue(icon.classList.contains(startIconClass));
			},
			testChange_noElement: function() {
				try {
					controller.change(null, "a", "b");
					assert.isTrue(false);
				} catch (e) {
					assert.strictEqual(e.message, "element must be an HTML element");
				}
			},
			testChange: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.change(icon, newIconClass, startIconClass);
				assert.isTrue(icon.classList.contains(newIconClass));
				assert.isFalse(icon.classList.contains(startIconClass));
			},
			testChange_withContainingElement: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.change(target, newIconClass, startIconClass);
				assert.isTrue(icon.classList.contains(newIconClass));
				assert.isFalse(icon.classList.contains(startIconClass));
			},
			testChange_noRemove: function () {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.change(target, newIconClass);
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isTrue(icon.classList.contains(newIconClass));
			},
			testChange_noAdd: function () {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				icon.classList.add("someOtherClass");
				controller.change(target, null, startIconClass);
				assert.isFalse(icon.classList.contains(startIconClass));
			},
			testChange_removeNotPresent: function() {
				var target = getElement(true),
					icon = target.firstElementChild;
				assert.isTrue(icon.classList.contains(startIconClass));
				assert.isFalse(icon.classList.contains(newIconClass));
				controller.change(icon, newIconClass, "someOtherClass");
				assert.isTrue(icon.classList.contains(newIconClass));
				assert.isTrue(icon.classList.contains(startIconClass));
			}
		});
	});
