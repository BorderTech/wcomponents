define(["intern!object", "intern/chai!assert", "wc/ui/modalShim", "wc/dom/shed"],
	function (registerSuite, assert, controller, shed) {
		"use strict";

		/*
		 * Unit tests for wc/ui/modalShim
		 * TODO: Add functional tests to test event handlers.
		 */

		var testHolder,
			SHIM_ID = "wc-shim", // must be the same as the id used in wc/ui/modalShim
			testContent = "\
				<div id='" + SHIM_ID + "'>\n\
					<div id='inneractiveregion'>\n\
						<button type='button' id='activewhenopen' accesskey='B'>button</button>\n\
					</div>\n\
				</div>\n\
				<div id='outsideshim'>\
					<label id='lblWithAccessKey' accesskey='L'>Label<input type='text' id='textinputforfocustest'></label>\n\
				</div>\n\
				<div id='regionwithnoaccesskeys'>nothing here</div>",
			notified = false;

		/**
		 * Get the modal shim.
		 * @returns {Element} the modal shim
		 */
		function getShim() {
			var shim = document.getElementById(SHIM_ID);
			if (!shim) {
				assert.fail("shim", null, "Modal shim not found");
			}
			return shim;
		}

		/**
		 * Heper to test if accesskeys have been remove/reinstated
		 * @param {boolean} notL if true we expect the label accesskey to have been removed
		 * @param {boolean} notB if true we expect the button accesskey to have been removed
		 */
		function testAccessKeys(notL, notB) {
			var akey = document.getElementById("lblWithAccessKey");
			if (notL) {
				assert.isFalse(!!akey.getAttribute("accesskey"), "Did not expect to find accesskey on label");
			}
			else {
				assert.strictEqual(akey.getAttribute("accesskey"), "L", "Expected to find accesskey 'L'");
			}
			akey = document.getElementById("activewhenopen");
			if (notB) {
				assert.isFalse(!!akey.getAttribute("accesskey"), "Did not expect to find accesskey on button");
			}
			else {
				assert.strictEqual(akey.getAttribute("accesskey"), "B", "Expected to find accesskey 'B'");
			}
		}

		/**
		 * A function to test subscribe/unsubscribe.
		 */
		function subscriber() {
			notified = true;
		}

		registerSuite({
			name: "modalShim",
			setup: function() {
				var shim = document.getElementById(SHIM_ID);
				if (shim) {
					shim.parentNode.removeChild(shim);
				}
				testHolder = document.getElementById("testholder");
				if (!testHolder) {
					document.body.insertAdjacentHTML("beforeend", "<div id='testholder'></div>");
					testHolder = document.getElementById("testholder");
				}
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			beforeEach: function() {
				// we call clearModal twice - once to reset all of the old settings, remove events etc then again after resetting the innerHTML
				// to ensure the new modal is in the cleared state.
				controller.clearModal();
				testHolder.innerHTML = testContent;
				controller.clearModal(); // set up he new shim and make sure it is hidden
				notified = false;
			},
			afterEach: function() {
				controller.unsubscribe(subscriber);
				controller.unsubscribe(subscriber, true);
			},
			testDefaultAccessKeys: function() {
				testAccessKeys();
			},
			testClearModal: function() {
				assert.isTrue(shed.isHidden(getShim(), "modal shim should be hidden."));
			},
			testSetModal: function() {
				controller.setModal();
				assert.isFalse(shed.isHidden(getShim(), "modal shim should not be hidden."));
			},
			testSetModalWithCreate: function() {
				var shim = document.getElementById(SHIM_ID);
				if (shim) {
					shim.parentNode.removeChild(shim);
				}
				assert.isNull(document.getElementById(SHIM_ID));
				controller.setModal();
				assert.isNotNull(document.getElementById(SHIM_ID));
			},
			testSetModalClassName: function() {
				var className = "shimclass", shim = getShim();
				controller.setModal(null, className);
				assert.strictEqual(className, shim.className, "modal shim should have classname '" + className + "'");
			},
			testSetModalAccessKeyUnchanged: function() {
				controller.setModal();
				testAccessKeys();
			},
			testSetModalActiveRegionUnsetsAccessKeys: function() {
				var activeRegion = document.getElementById("inneractiveregion");
				controller.setModal(activeRegion);
				testAccessKeys(true);
			},
			testSetModalAlternateActiveRegionUnsetsAccessKeys: function() {
				var activeRegion = document.getElementById("outsideshim");
				controller.setModal(activeRegion);
				testAccessKeys(false, true);
			},
			testSetModalThirdActiveRegionUnsetsAllAccessKeys: function() {
				var activeRegion = document.getElementById("regionwithnoaccesskeys");
				controller.setModal(activeRegion);
				testAccessKeys(true, true);
			},
			testClearModalResetsAccessKeys: function() {
				var activeRegion = document.getElementById("regionwithnoaccesskeys");
				controller.setModal(activeRegion);
				controller.clearModal();
				testAccessKeys();
			},
			testSubscribe: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber);
				controller.setModal();
				assert.isFalse(notified, "notifed should be false after setting modal");
				controller.clearModal();
				assert.isTrue(notified, "notifed should be true after clearing modal");
			},
			testSubscribeToShow: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber, true);
				controller.setModal();
				assert.isTrue(notified, "notifed should be true after setting modal");
			},
			testUnsubscribe: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber);
				controller.setModal();
				controller.unsubscribe(subscriber);
				controller.clearModal();
				assert.isFalse(notified, "notifed should still be false after clearing modal");
			},
			testUnsubscribeToShow: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber, true);
				controller.unsubscribe(subscriber, true);
				controller.setModal();
				assert.isFalse(notified, "notifed should still be false after setting modal");
			},
			testUnsubscribeMisMatch: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber);
				controller.setModal();
				controller.unsubscribe(subscriber, true);
				controller.clearModal();
				assert.isTrue(notified, "notifed should be true after clearing modal because we unsubscribed from the wrong group");
			},
			testUnsubscribeToShowMisMatch: function() {
				assert.isFalse(notified, "notifed should be false to start");
				controller.subscribe(subscriber, true);
				controller.unsubscribe(subscriber);
				controller.setModal();
				assert.isTrue(notified, "notifed should be true after setting modal because we unsubscribed from the wrong group");
			},
			testSubscribeToShowWithArg: function() {
				var localSubscriber = function(arg) {
						actual = arg;
					},
					actual = null;
				try {
					controller.subscribe(localSubscriber, true);
					controller.setModal();
					assert.isNotNull(actual, "Expected to be notified with an arg");
				}
				finally {
					controller.unsubscribe(localSubscriber, true);
				}
			},
			testSubscribeToShowWithArgReturnsShim: function() {
				var localSubscriber = function(arg) {
						actual = arg;
					},
					actual = null,
					shim = getShim();
				try {
					controller.subscribe(localSubscriber, true);
					controller.setModal();
					if (!actual) {
						assert.fail(actual, shim, "Expected subscriber to notify with shim element");
					}
					assert.strictEqual(actual.id, shim.id, "Expected subscriber to notify with shim as arg");
				}
				finally {
					controller.unsubscribe(localSubscriber, true);
				}
			},
			testSubscribeToShowWithArgSetWithRegion: function() {
				var localSubscriber = function(arg) {
						actual = arg;
					},
					actual = null,
					activeRegion = document.getElementById("outsideshim");
				try {
					controller.subscribe(localSubscriber, true);
					controller.setModal(activeRegion);
					assert.isNotNull(actual, "Expected to be notified with an arg");
				}
				finally {
					controller.unsubscribe(localSubscriber, true);
				}
			},
			testSubscribeToShowWithArgSetWithRegionReturnsRegion: function() {
				var localSubscriber = function(arg) {
						actual = arg;
					},
					actual = null,
					activeRegion = document.getElementById("outsideshim");
				try {
					controller.subscribe(localSubscriber, true);
					controller.setModal(activeRegion);
					if (!actual) {
						assert.fail(actual, activeRegion, "Expected subscriber to notify with active region");
					}
					assert.strictEqual(actual.id, "outsideshim", "Expected subscriber to notify with active region as arg");
				}
				finally {
					controller.unsubscribe(localSubscriber, true);
				}
			}
		});
	}
);

