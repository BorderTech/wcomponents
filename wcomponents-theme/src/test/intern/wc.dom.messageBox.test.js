define(["intern!object", "intern/chai!assert", "wc/dom/messageBox", "wc/dom/classList", "wc/ui/icon", "wc/i18n/i18n", "./resources/test.utils!"],
	function (registerSuite, assert, controller, classList, icon, i18n, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/dom/messageBox
		 */
		var testHolder,
			testBoxId = "messageboxtest-box1",
			testMessageBoxHTML = "<section id='" + testBoxId + "' class='wc-messagebox'><h1></h1><div class='messages'></div></section>",
			testContent;

		function getTestBox(type) {
			var box = document.getElementById(testBoxId),
				iconName, title, boxHeading;
			if (!box) {
				testHolder.insertAdjacentHTML("beforeend", testMessageBoxHTML);
				box = document.getElementById(testBoxId);
			}

			boxHeading = box.firstElementChild;

			if (type && !icon.get(boxHeading)) {
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
				if (iconName) {
					icon.add(boxHeading, iconName);
					icon.add(boxHeading, "fa-fw");
					boxHeading.insertAdjacentHTML("beforeend", "<span>" + title + "</span>");
				}
			}

			return box;
		}

		registerSuite({
			name: "wc/dom/messageBox",
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
					assert.isTrue(!!widget.isOneOfMe && !!widget.constructor.isOneOfMe);
				}
				assert.isTrue(widget.isOneOfMe(getTestBox()));
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
			testGetErrorBoxWidget: function() {
				var widget = controller.getErrorBoxWidget(),
					box = getTestBox("wc-messagebox-type-error");
				assert.isOk(widget);
				assert.isTrue(widget.isOneOfMe(box));
			}
		});
	}
);
