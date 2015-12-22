/*
 * TODO This is a mess! What's with lib/dojo/Deferred???? Use the damn promises polyfill!
 * Agreed. I got rid of lib/dojo/Deferred but leaving this comment as a flag that this test needs some serious review.
 *
 */
define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		/*
		 * NOTE: why are these asynchronous? because even Chrome has trouble selecting a
		 * range in time to test its selection.
		 */
		var clearSelection, timers,
			TEXT = "This is some known text",
			testHolder,
			DELAY = 10,
			TIMEOUT = 1000;

		function getSelectedText() {
			var result;
			if (window.getSelection) {
				result = window.getSelection().toString();
			}
			else if (document.selection) {
				result = document.selection.createRange().text;
			}
			return result;
		}

		registerSuite({
			name: "clearSelection",
			setup: function() {
				return testutils.setupHelper(["wc/dom/clearSelection", "wc/timers"], function(c, t) {
					clearSelection = c;
					timers = t;
				});
			},
			beforeEach: function() {
				var el, range,
					SELECT_ID = "clearSelectionTestContainer1",
					testHolder = testutils.getTestHolder();
				testHolder.innerHTML = "<p id='" + SELECT_ID + "'>" + TEXT + "</p>";

				if ((el = document.getElementById(SELECT_ID))) {
					if (document.createRange) {
						window.getSelection().removeAllRanges();
						range = document.createRange();
						range.selectNode(el);
						window.getSelection().addRange(range);
					}
					else {
						range = document.body.createTextRange();
						range.moveToElementText(el);
						range.select();
					}
				}
				else {
					assert.fail("Cannot find element with id " + SELECT_ID);
				}
			},
			teardown: function() {
				if (testHolder) {
					testHolder.innerHTML = "";
				}
			},
			doSetRangeTest: function() {
				/* This test is to make sure we have a selection before we test if we can clear it
				var deferred = new doh.Deferred();
				timers.setTimeout(
					deferred.getTestCallback(function() {assert.strictEqual(TEXT, getSelectedText());}),
					DELAY);
				return deferred; */

				var dfd = this.async(TIMEOUT);
				timers.setTimeout(function() {
					var wait = function(ms) {
						var waitDfd = new testutils.LamePromisePolyFill();
						setTimeout(function() {
							waitDfd._resolve(getSelectedText());
						}, ms);
						return waitDfd;
					};

					wait(DELAY).then(dfd.callback(function(data) {
						assert.strictEqual(data, TEXT);
					}));
				}, DELAY);
				return dfd;
			},
			doClearSelectionTest: function() {
				/* var deferred = new doh.Deferred();
				timers.setTimeout(deferred.getTestCallback(
					function() {
						clearSelection();
						assert.notStrictEqual(TEXT, getSelectedText());
				}), DELAY);
				return deferred; */
				var dfd = this.async(TIMEOUT);
				timers.setTimeout(function() {
					var wait = function(ms) {
						var waitDfd = new testutils.LamePromisePolyFill();
						setTimeout(function() {
							waitDfd._resolve(clearSelection());
						}, ms);
						return waitDfd;
					};

					wait(DELAY).then(dfd.callback(function() {
						assert.notStrictEqual(TEXT, getSelectedText());
					}));
				}, DELAY);
				return dfd;
			},
			doClearSelectionReallyClears: function() {
				/* var deferred = new doh.Deferred();
				timers.setTimeout(deferred.getTestCallback(
					function() {
						clearSelection();
						assert.strictEqual("", getSelectedText());
				}), DELAY);
				return deferred; */

				var dfd = this.async(TIMEOUT);
				timers.setTimeout(function() {
					var wait = function(ms) {
						var waitDfd = new testutils.LamePromisePolyFill();
						setTimeout(function() {
							waitDfd._resolve(clearSelection());
						}, ms);
						return waitDfd;
					};

					wait(DELAY).then(dfd.callback(function() {
						assert.strictEqual("", getSelectedText());
					}));
				}, DELAY);
				return dfd;
			}
		});
	});
