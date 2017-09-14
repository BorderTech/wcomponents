define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function (registerSuite, assert, testutils) {
	"use strict";

	/**
	 * Surprising how often we need to get teh next element sibling.
	 * @param {Element} el the start element
	 * @param {boolean} [prev] if `true` get previous element sibling, otherwise get next element sibling
	 * @returns {? Element} the  element sibling or null.
	 */
	function getElSib(el, prev) {
		var next;

		if (!el || el.nodeType !== Node.ELEMENT_NODE) {
			throw new TypeError("argument `el` must be an element");
		}
		if (prev) {
			if (typeof el.previousElementSibling !== "undefined") {
				return el.previousElementSibling;
			}
			next = el.previousSibling;
			while (next) {
				if (next.nodeType === Node.ELEMENT_NODE) {
					return next;
				}
				next = next.previousSibling;
			}
			return null;
		}
		if (typeof el.nextElementSibling !== "undefined") {
			return el.nextElementSibling;
		}
		next = el.nextSibling;
		while (next) {
			if (next.nodeType === Node.ELEMENT_NODE) {
				return next;
			}
			next = next.nextSibling;
		}
		return null;
	}

	var
		/**
		 * The module name of the module being tested eg "wc/ui/foo".
		 * @type String
		 */
		TEST_MODULE = "wc/ui/label",
		/**
		 * A human readable name for the suite. This could be as simpl as TEST_MODULE.
		 * @type String
		 */
		suiteName = TEST_MODULE,// .match(/\/([^\/]+)$/)[1],
		/**
		 * An options array of dependency names in addition to TEST_MODULE, Define a String Array here and setup will convert it to a module array.
		 * @type arr
		 */
		deps = ["wc/dom/shed", "wc/dom/initialise", "wc/dom/classList", "wc/dom/tag"],
		/**
		 * Load test UI froman external resource e.g. "@RESOURCES@/SOME_PAGE.html". Leave undefined if not required. Simple test UIs may be set inline
		 * using testContent instead. If both are set testContent takes precedence and urlResource is ignored.
		 * Note that the property `@RESOURCES@` will be mapped to the test/intern/resources directory as a URL.
		 * @type URL
		 */
		urlResource = "@RESOURCES@/uiLabel.html",
		// If you have extra dependencies you will want a way to reference them.
		shed,
		initialise,
		classList,
		tag,
		inited,
		CLASS_REQ = "wc_req",
		//
		// END CONFIGURATION VARS
		//
		// the next two are not settable.
		controller, // This will be the actual module named above. Tests of public functions use this e.g. `controller.getSomething()`.
		testHolder; // This will hold any UI needed for the tests. It is left undefined if testContent & urlResource are both falsey.

	registerSuite({
		name: suiteName,

		setup: function() {
			var allDeps = (deps && deps.length) ? deps : [];
			allDeps.unshift(TEST_MODULE);
			var result = testutils.setupHelper(allDeps).then(function(arg) {

				// The module to be tested is the controller
				controller = arg[0];
				// The other dependencies
				// If you want to have named dependencies the vars are assigned here
				shed = arg[1];
				initialise = arg[2];
				classList = arg[3];
				tag = arg[4];
				testHolder = testutils.getTestHolder();
				return testutils.setUpExternalHTML(urlResource, testHolder);
			});
			return result;
		},

		beforeEach: function () {
			if (!inited) {
				controller.preInit(testHolder);
				inited = true;
			}
			initialise.go();
		},

		teardown: function () {
			if (testHolder) {
				testHolder.innerHTML = "";
			}
		},

		testGotController: function () {
			if (!TEST_MODULE) {
				assert.isOk(TEST_MODULE, "Cannot test an undefined module you tailless monkey!");
			}
			assert.typeOf(controller, "object", "Expected the test module to be available as an object otherwise the tests won't work.");
		},
		testGotShed: function() {
			assert.typeOf(shed, "object", "Expected to load shed.");
			assert.isOk(shed.show, "shed is not what you think it is."); // rough but good enough
		},
		testMoveLabelTrue: function() {
			var input,
				found,
				inputIds = ["wcuilabel-i2", "wcuilabel-i2a", "wcuilabel-i2b"];
			inputIds.forEach(function(nextId) {
				input = document.getElementById(nextId);
				found = getElSib(input);
				assert.strictEqual(found.getAttribute("for"), nextId, "label should have been moved in pre-init");
			});
		},
		testMoveLabelFalse: function() {
			var input,
				found,
				inputIds = ["wcuilabel-i3", "wcuilabel-i3a", "wcuilabel-i3b"];
			inputIds.forEach(function(nextId) {
				input = document.getElementById(nextId);
				found = getElSib(input, true);
				assert.strictEqual(found.getAttribute("for"), nextId, "label should not have been moved in pre-init");
			});
		},
		testGetHint: function() {
			var label = document.getElementById("wcuilabel-l10"),
				hint = controller.getHint(label);
			assert.isOk(hint, "expected to find hint");
		},
		testGetHintCorrectContent: function() {
			var label = document.getElementById("wcuilabel-l10"),
				expected = "this is the hint",
				hint = controller.getHint(label);
			assert.strictEqual(hint.innerHTML, expected, "expected to find hint content");
		},
		testGetHintNoHint: function() {
			var label = document.getElementById("wcuilabel-l11"),
				hint = controller.getHint(label);
			assert.isNotOk(hint, "expected to not find hint");
		},
		// setHint
		testSetHint: function() {
			var hint = "I am the walrus",
				label = document.getElementById("wcuilabel-l9"),
				labelHint;
			assert.isNotOk(controller.getHint(label));
			controller.setHint(label, hint);
			labelHint = controller.getHint(label);
			assert.isOk(labelHint);
			assert.isTrue(labelHint.innerHTML.indexOf(hint) === 0);
		},
		testMandate: function() {
			var input = document.getElementById("wcuilabel-i6"),
				label = document.getElementById("wcuilabel-l6");
			assert.isFalse(classList.contains(label, CLASS_REQ));
			assert.isFalse(shed.isMandatory(input));
			shed.mandatory(input);
			assert.isTrue(classList.contains(label, CLASS_REQ));
		},
		testMandateRadio: function() {
			// note: we do not decorate labels for mandatory radios
			var input = document.getElementById("wcuilabel-i6a"),
				label = document.getElementById("wcuilabel-l6a");
			assert.isFalse(classList.contains(label, CLASS_REQ));
			assert.isFalse(shed.isMandatory(input));
			shed.mandatory(input);
			assert.isFalse(classList.contains(label, CLASS_REQ));
			assert.isTrue(shed.isMandatory(input));
		},
		testOptionalise: function() {
			var input = document.getElementById("wcuilabel-i5"),
				label = document.getElementById("wcuilabel-l5");
			assert.isTrue(classList.contains(label, CLASS_REQ));
			shed.optional(input);
			assert.isFalse(classList.contains(label, CLASS_REQ));
		},
		testOptionaliseRemovesMessage: function() {
			var input = document.getElementById("wcuilabel-i5a"),
				label = document.getElementById("wcuilabel-l5a");
			assert.isTrue(label.innerHTML.indexOf("wc-off") > 0);
			shed.optional(input);
			assert.isTrue(label.innerHTML.indexOf("wc-off") === -1);
		},
		testHide: function() {
			var input = document.getElementById("wcuilabel-i7"),
				label = document.getElementById("wcuilabel-l7");
			assert.isFalse(shed.isHidden(label));
			shed.hide(input);
			assert.isTrue(shed.isHidden(label));
		},
		testShow: function() {
			var input = document.getElementById("wcuilabel-i8"),
				label = document.getElementById("wcuilabel-l8");
			assert.isTrue(shed.isHidden(label));
			shed.show(input);
			assert.isFalse(shed.isHidden(label));
		},
		testConvertInputToRO: function() {
			var input = document.getElementById("wcuilabel-i12"),
				label = document.getElementById("wcuilabel-l12");
			assert.isTrue(label.tagName === tag.LABEL, "wrong start tagname");
			assert.isOk(label.getAttribute("for"), "should have for attribute");
			assert.isNotOk(label.getAttribute("data-wc-rofor"), "should not have data-wc-for attribute");
			controller._convert(input, label, true);
			label = document.getElementById("wcuilabel-l12");
			assert.isTrue(label.tagName === tag.SPAN, "wrong end tagname");
			assert.isNotOk(label.getAttribute("for"), "for attribute should have been removed");
			assert.isOk(label.getAttribute("data-wc-rofor"), "data-ro-for should have been added");
			assert.strictEqual(label.getAttribute("data-wc-rofor"), "wcuilabel-i12");
		},
		testConvertROtoInput: function() {
			var input = document.getElementById("wcuilabel-i13"),
				label = document.getElementById("wcuilabel-l13");
			assert.isTrue(label.tagName === tag.SPAN, "wrong start tagname");
			assert.isNotOk(label.getAttribute("for"), "should not have for attribute");
			assert.isOk(label.getAttribute("data-wc-rofor"), "should have data-wc-for attribute");
			assert.strictEqual(label.getAttribute("data-wc-rofor"), "wcuilabel-i13");
			controller._convert(input, label, false);
			label = document.getElementById("wcuilabel-l13");
			assert.isTrue(label.tagName === tag.LABEL, "wrong end tagname");
			assert.isOk(label.getAttribute("for"), "for attribute should have been added");
			assert.isNotOk(label.getAttribute("data-wc-rofor"), "data-ro-for should have been removed");
			assert.strictEqual(label.getAttribute("for"), "wcuilabel-i13");
		},
		testAjaxSubscriber: function() {
			// this is a fake just to test the actual subscriber, not to test AJAX
			var container = document.getElementById("wcuilabel-fake-ajax"),
				label;
			controller._ajax(container);

			label = document.getElementById("wcuilabel-fake-ajax-l1");
			assert.strictEqual(label.tagName, tag.LABEL);
			assert.isNotOk(label.getAttribute("data-wc-rofor"), "data-ro-for should have been removed");
			assert.strictEqual(label.getAttribute("for"), "wcuilabel-fake-ajax-i1_input", "for attribute should have been added");

			label = document.getElementById("wcuilabel-fake-ajax-l2");
			assert.strictEqual(label.tagName, tag.SPAN);
			assert.isNotOk(label.getAttribute("for"), "data-ro-for should have been removed");
			assert.strictEqual(label.getAttribute("data-wc-rofor"), "wcuilabel-fake-ajax-i2", "data-wc-rofor attribute should have been added");

			label = document.getElementById("wcuilabel-fake-ajax-l3");
			assert.isTrue(shed.isHidden(label), "real label should be hidden");

			label = document.getElementById("wcuilabel-fake-ajax-l4");
			assert.isTrue(shed.isHidden(label), "fake label should be hidden");

			label = document.getElementById("wcuilabel-fake-ajax-l5");
			assert.isTrue(classList.contains(label, CLASS_REQ), "real label should be flagged required");

			label = document.getElementById("wcuilabel-fake-ajax-l6");
			assert.isFalse(classList.contains(label, CLASS_REQ), "fake label should not be flagged required");

			// change of property but not of read-only state
			// mandatory
			label = document.getElementById("wcuilabel-fake-ajax-l7");
			assert.isTrue(classList.contains(label, CLASS_REQ), "label should now be flagged required");
			label = document.getElementById("wcuilabel-fake-ajax-l8");
			assert.isFalse(classList.contains(label, CLASS_REQ), "label should no longer be flagged required");
			label = document.getElementById("wcuilabel-fake-ajax-l9");
			assert.isTrue(shed.isHidden(label), "label should now be hidden");
			label = document.getElementById("wcuilabel-fake-ajax-l10");
			assert.isFalse(shed.isHidden(label), "label should no longer be hidden");
		}
	});
});
