define(["intern!object", "intern/chai!assert", "wc/dom/messageBox", "wc/dom/classList", "wc/ui/icon", "wc/i18n/i18n", "./resources/test.utils!"],
	function (registerSuite, assert, controller, classList, icon, i18n, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/dom/messageBox
		 */
		var testHolder,
			wcMessageBoxClass = "wc-messagebox",
			wcValidationErrorsClass = "wc-validationerrors",
			testBoxId = "messageboxtest-box1",
			testMessageBoxHTML = "<section id='" + testBoxId + "' class='wc_msgbox'><h1></h1><div class='messages'></div></section>",
			testContent;

		function getTestBox(component, type) {
			var box = document.getElementById(testBoxId),
				iconName, title, boxHeading;
			if (!box) {
				testHolder.insertAdjacentHTML("beforeend", testMessageBoxHTML);
				box = document.getElementById(testBoxId);
			}
			if (component) {
				classList.add(box, component);
			}

			boxHeading = box.firstElementChild;

			if ((type || component === "wc-validationerrors") && !icon.get(boxHeading)) {
				if (component === "wc-validationerrors") {
					iconName = "fa-minus-circle";
					title = i18n.get("messagetitle_error");
				} else if (type) {
					classList.add(box, type);
					switch (type) {
						case "wc-messagebox-type-error" :
							iconName = "fa-minus-circle";
							title = i18n.get("messagetitle_error");
							break;
						case "wc-messagebox-type-warn" :
							iconName = "fa-exclamation-triangle";
							title = i18n.get("messagetitle_warn");
							break;
						case "wc-messagebox-type-info" :
							iconName = "fa-info-circle";
							title = i18n.get("messagetitle_info");
							break;
						case "wc-messagebox-type-success" :
							iconName = "fa-check-circle";
							title = i18n.get("messagetitle_success");
							break;
					}
				}
				if (iconName) {
					icon.add(boxHeading, iconName);
					icon.add(boxHeading, "fa-fw");
					boxHeading.insertAdjacentHTML("beforeend", "<span>" + title + "</span>");
				}
			}

			return box;
		}

		registerSuite({
			name: "wc/dom/wrappedInput",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testGetWidget: function() {
				var widget = controller.getWidget();
				assert.isOk(widget);
				if (widget.constructor && widget.constructor.name) {
					assert.strictEqual(widget.constructor.name, "Widget");
				} else {
					// rough but (barely) adequate test
					// once we stop supporting IE we should be able to remove this
					assert.containsAllKeys(widget, ["findAncestor", "findDescendant", "findDescendants", "isOneOfMe"]);
				}
				assert.isTrue(widget.isOneOfMe(getTestBox()));
			},
			testGetWMessageBoxWidget: function() {
				var widget = controller.getWMessageBoxWidget(),
					box = getTestBox(wcMessageBoxClass);
				assert.isOk(widget);
				assert.isTrue(widget.isOneOfMe(box));
			},
			testGetWValidationErrorsWidget: function() {
				var widget = controller.getWValidationErrorsWidget(),
					box = getTestBox(wcValidationErrorsClass);
				assert.isOk(widget);
				assert.isTrue(widget.isOneOfMe(box));
			},
			testIsOneOfMe_noArgs: function() {
				assert.isFalse(controller.isOneOfMe());
			},
			testIsOneOfMe_notElementArgs: function() {
				assert.isFalse(controller.isOneOfMe({}));
			},
			testIsOneOfMe_elementNotMessageBox: function() {
				var element = document.createElement("span");
				testHolder.appendChild(element);
				assert.isFalse(controller.isOneOfMe(element));
			},
			testIsOneOfMe_generic: function() {
				assert.isTrue(controller.isOneOfMe(getTestBox()));
			},
			testIsOneOfMe_WMessageBox: function() {
				assert.isTrue(controller.isOneOfMe(getTestBox(wcMessageBoxClass)));
			},
			testIsOneOfMe_ValidationErrors: function() {
				assert.isTrue(controller.isOneOfMe(getTestBox(wcValidationErrorsClass)));
			},
			testIsWMessageBox_noArgs: function() {
				assert.isFalse(controller.isWMessageBox());
			},
			testIsWMessageBox_notElementArgs: function() {
				assert.isFalse(controller.isWMessageBox({}));
			},
			testIsWMessageBox_elementNotMessageBox: function() {
				var element = document.createElement("span");
				testHolder.appendChild(element);
				assert.isFalse(controller.isWMessageBox(element));
			},
			testIsWMessageBox_generic: function() {
				assert.isFalse(controller.isWMessageBox(getTestBox()));
			},
			testIsWMessageBox_WMessageBox: function() {
				assert.isTrue(controller.isWMessageBox(getTestBox(wcMessageBoxClass)));
			},
			testIsWMessageBox_ValidationErrors: function() {
				assert.isFalse(controller.isWMessageBox(getTestBox(wcValidationErrorsClass)));
			},
			testIsWValidationErrors_noArgs: function() {
				assert.isFalse(controller.isWValidationErrors());
			},
			testIsWValidationErrors_notElementArgs: function() {
				assert.isFalse(controller.isWValidationErrors({}));
			},
			testIsWValidationErrors_elementNotMessageBox: function() {
				var element = document.createElement("span");
				testHolder.appendChild(element);
				assert.isFalse(controller.isWValidationErrors(element));
			},
			testIsWValidationErrors_generic: function() {
				assert.isFalse(controller.isWValidationErrors(getTestBox()));
			},
			testIsWValidationErrors_WMessageBox: function() {
				assert.isFalse(controller.isWValidationErrors(getTestBox(wcMessageBoxClass)));
			},
			testIsValidationErrors_ValidationErrors: function() {
				assert.isTrue(controller.isWValidationErrors(getTestBox(wcValidationErrorsClass)));
			},
			testGet_noContainer: function() {
				var box = getTestBox(); // set up the box to find
				assert.equal(controller.get(), box);
			},
			testGet_inContainer: function() {
				var box = getTestBox(); // set up the box to find
				assert.equal(controller.get(testHolder), box);
			},
			testGet_noContainerAll: function() {
				var box = getTestBox(),
					found = controller.get(null, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGet_inContainerAll: function() {
				var box = getTestBox(),
					found = controller.get(testHolder, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGetValidationErrors_noContainer_noValidationErrors: function() {
				var box = getTestBox(),
					found = controller.getValidationErrors();
				assert.isOk(box); // we really created box
				assert.isNull(found);
			},
			testGetValidationErrors_noContainer_noValidationErrors_all: function() {
				var box = getTestBox(),
					found = controller.getValidationErrors(null, true);
				assert.isOk(box); // we really created box
				assert.strictEqual(found.length, 0);
			},
			testGetValidationErrors_noContainer: function() {
				var box = getTestBox(wcValidationErrorsClass),
					found = controller.getValidationErrors();
				assert.equal(found, box);
			},
			testGetValidationErrors_inContainer: function() {
				var box = getTestBox(wcValidationErrorsClass);
				assert.equal(controller.getValidationErrors(testHolder), box);
			},
			testGetValidationErrors_noContainerAll: function() {
				var box = getTestBox(wcValidationErrorsClass),
					found = controller.getValidationErrors(null, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGetValidationErrors_inContainerAll: function() {
				var box = getTestBox(wcValidationErrorsClass),
					found = controller.getValidationErrors(testHolder, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGetValidationErrors_doesNotMatchMessageBox: function() {
				var box = getTestBox(wcMessageBoxClass),
					found = controller.getValidationErrors(testHolder);
				assert.isOk(box); // we really created box
				assert.isNull(found);
			},
			testGetMessageBoxes_noContainer_noMessageBoxes: function() {
				var box = getTestBox(),
					found = controller.getMessageBoxes();
				assert.isOk(box); // we really created box
				assert.isNull(found);
			},
			testGetMessageBoxes_noContainer_noMessageBoxes_all: function() {
				var box = getTestBox(),
					found = controller.getMessageBoxes(null, true);
				assert.isOk(box); // we really created box
				assert.strictEqual(found.length, 0);
			},
			testGetMessageBoxes_noContainer: function() {
				var box = getTestBox(wcMessageBoxClass),
					found = controller.getMessageBoxes();
				assert.equal(found, box);
			},
			testGetMessageBoxes_inContainer: function() {
				var box = getTestBox(wcMessageBoxClass);
				assert.equal(controller.getMessageBoxes(testHolder), box);
			},
			testGetMessageBoxes_noContainerAll: function() {
				var box = getTestBox(wcMessageBoxClass),
					found = controller.getMessageBoxes(null, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGetMessageBoxes_inContainerAll: function() {
				var box = getTestBox(wcMessageBoxClass),
					found = controller.getMessageBoxes(testHolder, true);
				assert.equal(found.length, 1);
				assert.equal(found[0], box);
			},
			testGetMessageBoxes_doesNotMatchValidationErrors: function() {
				var box = getTestBox(wcValidationErrorsClass),
					found = controller.getMessageBoxes(testHolder);
				assert.isOk(box); // we really created box
				assert.isNull(found);
			},
			testGetErrorBoxWidget: function() {
				var widget = controller.getErrorBoxWidget(),
					box = getTestBox(wcMessageBoxClass, "wc-messagebox-type-error");
				assert.isOk(widget);
				assert.isTrue(widget.isOneOfMe(box));
			}
		});
	}
);
