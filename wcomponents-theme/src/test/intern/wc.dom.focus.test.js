define(["intern!object", "intern/chai!assert", "wc/dom/focus", "./resources/test.utils!", "wc/dom/tag"],
	function (registerSuite, assert, controller, testutils, tag) {
		"use strict";

		var testHolder,
			NATIVE_TRUE = "<button type='button' id='button1'>button</button>",
			NATIVE_FALSE = "<div id='div1'>hello</div>",
			NATIVE_LINK = "<a href='#' id='a1'>link</a>",
			RADIOS_NO_SELECTION = "<input type='radio' name='radio1' id='r1' value='true'><input type='radio' name='radio1' id='r2' value='false'>",
			RADIOS_WITH_SELECTION = "<input type='radio' name='radio2' id='r3' value='true'><input type='radio' name='radio2' id='r4' value='false' checked>",
			FOCUSABLE_CONTAINER = "<div id='hasfocusable'><p>start placeholder</p><button type='button' id='hf1'>button</button><p>between</p><button type='button' id='hf2'>button</button><p>end placeholder</p></div>",
			NOT_FOCUSABLE_CONTAINER = "<div id='nofocus'><p id='nf1'>not focusable</p></div>",
			FOCUSABLE_ANCESTOR = "<button type='button' id='button1'><span id='span1'>start</span></button>";

		registerSuite({
			name: "wc/dom/focus",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				if (!testHolder) {
					assert.fail(true, undefined, "did not create testHolder.");
				}
				testHolder.innerHTML = "";
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testIsTabstopNative: function() {
				var id = "button1";
				testHolder.innerHTML = NATIVE_TRUE;
				assert.isTrue(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopNotNative: function() {
				var id = "div1";
				testHolder.innerHTML = NATIVE_FALSE;
				assert.isFalse(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopNativeDisabled: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.disabled = true;
				assert.isFalse(controller.isTabstop(element));
			},
			testIsTabstopNativeLink: function() {
				var id = "a1";
				testHolder.innerHTML = NATIVE_LINK;
				assert.isTrue(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopNativeAriaDisabled: function() {
				var id = "a1", element;
				testHolder.innerHTML = NATIVE_LINK;
				element = document.getElementById(id);
				element.setAttribute("aria-disabled", "true");
				assert.isFalse(controller.isTabstop(element));
			},
			testIsTabstopTabindex: function() {
				var id = "div1", element;
				testHolder.innerHTML = NATIVE_FALSE;
				element = document.getElementById(id);
				element.tabIndex = "0";
				assert.isTrue(controller.isTabstop(element));
			},
			testIsTabstopTabindexFalse: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.tabIndex = "-1";
				assert.isFalse(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopNativeHidden: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.hidden = true;
				assert.isFalse(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopTabindexHidden: function() {
				var id = "div1", element;
				testHolder.innerHTML = NATIVE_FALSE;
				element = document.getElementById(id);
				element.tabIndex = "0";
				element.hidden = true;
				assert.isFalse(controller.isTabstop(document.getElementById(id)));
			},
			testIsTabstopRadiosNoSelection: function() {
				testHolder.innerHTML = RADIOS_NO_SELECTION;
				assert.isTrue(controller.isTabstop(document.getElementById("r1")));
				assert.isTrue(controller.isTabstop(document.getElementById("r2")));
			},
			testIsTabstopRadiosWithSelection: function() {
				testHolder.innerHTML = RADIOS_WITH_SELECTION;
				assert.isFalse(controller.isTabstop(document.getElementById("r3")));
				assert.isTrue(controller.isTabstop(document.getElementById("r4")));
			},
			testIsTabstopNullArg: function() {
				assert.isFalse(controller.isTabstop());
			},
			testIsNativelyFocusable: function() {
				var focusable = [tag.A, tag.AREA, tag.AUDIO, tag.BUTTON, tag.FRAME, tag.IFRAME, tag.INPUT, tag.OBJECT, tag.SELECT, tag.TEXTAREA, tag.VIDEO],
					i;
				for (i = 0; i < focusable.length; ++i) {
					assert.isTrue(controller.isNativelyFocusable(focusable[i]));
				}
			},
			testIsNativelyFocusableFalse: function() {
				// a cross section of not focusable tagNames:
				var focusable = [tag.P, tag.DIV, tag.SPAN, tag.UL, tag.LI, tag.HTML, tag.BODY, tag.HEADER, tag.FOOTER, tag.H1, tag.FORM],
					i;
				for (i = 0; i < focusable.length; ++i) {
					assert.isFalse(controller.isNativelyFocusable(focusable[i]));
				}
			},
			testIsNativelyFocusableNullArg: function() {
				assert.isFalse(controller.isNativelyFocusable());
			},
			testCanFocusNativeYes: function() {
				var id = "button1";
				testHolder.innerHTML = NATIVE_TRUE;
				assert.isTrue(controller.canFocus(document.getElementById(id)));
			},
			testCanFocusHiddenNo: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.hidden = "true";
				assert.isFalse(controller.canFocus(element));
			},
			testCanFocusDisabledNo: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.disabled = "true";
				assert.isFalse(controller.canFocus(element));
			},
			testCanFocusInvisibleNo: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.style.visibility = "hidden";
				assert.isFalse(controller.canFocus(element));
			},
			testCanFocusNoDisplayNo: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.style.display = "none";
				assert.isFalse(controller.canFocus(element));
			},
			testCanFocusZeroDimensionNo: function() {
				var id = "button1", element;
				testHolder.innerHTML = NATIVE_TRUE;
				element = document.getElementById(id);
				element.style.width = "0";
				element.style.height = "0";
				element.style.overflow = "hidden";
				// don't forget buttons have borders and padding which give them dimension!
				element.style.border = "0 none";
				element.style.padding = "0";
				assert.isFalse(controller.canFocus(element), "expected zero dimension element to not be focusable , offsetWidth: " + element.offsetWidth + ", offsetHeight: " + element.offsetHeight);
			},
			// setFocusRequest needs async tests
			testFocusFirstTabstop: function() {
				var expected = "hf1",
					target;
				testHolder.innerHTML = FOCUSABLE_CONTAINER;
				target = controller.focusFirstTabstop(document.getElementById("hasfocusable"));
				assert.strictEqual(expected, target.id);
			},
			testFocusFirstTabstopReverse: function() {
				var expected = "hf2",
					target;
				testHolder.innerHTML = FOCUSABLE_CONTAINER;
				target = controller.focusFirstTabstop(document.getElementById("hasfocusable"), null, true);
				assert.strictEqual(expected, target.id);
			},
			testFocusFirstTabstopCallback: function() {
				var result = false;
				function callback() {
					result = true;
				}
				testHolder.innerHTML = FOCUSABLE_CONTAINER;
				controller.focusFirstTabstop(document.getElementById("hasfocusable"), callback);
				assert.isTrue(result);
			},
			testFocusFirstTabstopCallbackReverse: function() {
				var result = false;
				function callback() {
					result = true;
				}
				testHolder.innerHTML = FOCUSABLE_CONTAINER;
				controller.focusFirstTabstop(document.getElementById("hasfocusable"), callback, true);
				assert.isTrue(result);
			},
			testFocusFirstTabstopNoFocus: function() {
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				assert.isNull(controller.focusFirstTabstop(document.getElementById("nofocus")));
			},
			testFocusFirstTabstopNoFocusReverse: function() {
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				assert.isNull(controller.focusFirstTabstop(document.getElementById("nofocus"), null, true));
			},
			testFocusFirstTabstopNoFocusCallback: function() {
				var result = false;
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				function callback() {
					result = true;
				}
				controller.focusFirstTabstop(document.getElementById("nofocus"), callback);
				assert.isFalse(result);
			},
			testFocusFirstTabstopNoFocusCallbackReverse: function() {
				var result = false;
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				function callback() {
					result = true;
				}
				controller.focusFirstTabstop(document.getElementById("nofocus"), callback, true);
				assert.isFalse(result);
			},
			testCanFocusInside: function() {
				testHolder.innerHTML = FOCUSABLE_CONTAINER;
				assert.isTrue(controller.canFocusInside(document.getElementById("hasfocusable")));
			},
			testCanFocusInsideFalse: function() {
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				assert.isFalse(controller.canFocusInside(document.getElementById("nofocus")));
			},
			testGetFocusableAncestorSelf: function() {
				testHolder.innerHTML = NATIVE_TRUE;
				assert.strictEqual(document.getElementById("button1"),
					controller.getFocusableAncestor(document.getElementById("button1")));
			},
			testGetFocusableAncestorNotSelfIsNull: function() {
				testHolder.innerHTML = NATIVE_TRUE;
				assert.isNull(controller.getFocusableAncestor(document.getElementById("button1"), true));
			},
			testGetFocusableAncestor: function() {
				testHolder.innerHTML = FOCUSABLE_ANCESTOR;
				assert.strictEqual(document.getElementById("button1"),
					controller.getFocusableAncestor(document.getElementById("span1")));
			},
			testGetFocusableAncestorNotFocusable: function() {
				testHolder.innerHTML = NOT_FOCUSABLE_CONTAINER;
				assert.isNull(controller.getFocusableAncestor(document.getElementById("nf1")));
			},
			testGetFocusableAncestorBullArg: function() {
				assert.isNull(controller.getFocusableAncestor());
			}
		});
	});

