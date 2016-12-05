define(["wc/compat/compat!"], function() {
	"use strict";
	var instance = new WcTestUtils();

	function WcTestUtils() {
		var html5FileSelector,
			ajax,
			event,
			setupTimeout = 1000;

		/*
		 * Handles the requirejs plugin lifecycle.
		 * For information {@see http://requirejs.org/docs/plugins.html#apiload}
		 */
		this.load = function (id, parentRequire, callback/* , config */) {
			/* If you want to test IE then you must ensure compat is loaded before trying to load ajax. */
			parentRequire(["wc/ajax/ajax", "wc/dom/event", "wc/has", "wc/fixes", "wc/i18n/i18n!"], function (a, evt, has) {
				ajax = a;
				event = evt;
				if (has("edge") || has("trident")) {
					setupTimeout = 1000;
				}
				callback(instance);
			});
		};

		function useHtml5FileSelectors() {
			var element;
			if (typeof html5FileSelector === "undefined") {
				element = document.createElement("input");
				element.type = "file";
				html5FileSelector = (typeof element.files !== "undefined");
			}
			return html5FileSelector;
		}

		this.TRANFORM_CONTAINER_ID = "transformContainer";

		this.setupHelper = function(deps, callback) {
			var result = new Promise(function(win, lose) {
				try {
					require(deps, function() {
						var args = arguments;
						if (callback) {
							callback.apply(this, args);
						}
						window.setTimeout(function() {
							win(args);
						}, setupTimeout);
					});
				}
				catch (ex) {
					lose(ex);
				}
			});
			return result;
		};

		this.getTestHolder = function(keepContent) {
			var testHolderId = this.TRANFORM_CONTAINER_ID,
				testHolder = document.getElementById(testHolderId);

			if (!testHolder) {
				testHolder = document.createElement("section");
				testHolder.id = testHolderId;
				document.body.appendChild(testHolder);
			}
			else if (!keepContent) {
				testHolder.innerHTML = "";
			}

			if (event.canCapture) {
				event.remove(testHolder, event.TYPE.submit, submitEvent, true);
				event.add(testHolder, event.TYPE.submit, submitEvent, 1, null, true);
			}
			return testHolder;
		};

		this.loadResource = function (url, callback, onerror) {
			var result = new Promise(function(win, lose) {
				ajax.simpleRequest({
					url: url,
					callback: win,
					onError: lose
				});
			});
			return result.then(callback, onerror);
		};

		this.objectEqual = function (obj1, obj2) {
			var s1 = JSON.stringify(obj1),
				s2 = JSON.stringify(obj2);
			return s1 === s2;
		};

		/*
		 * @returns An array-like collection of all form elements which match
		 * the name argument.
		 * This helper exists to iron out xBrowser issues.
		 */
		this._getElementsByName = function(form, name) {
			/**
			 * In IE7 form elements that are added dynamically do not
			 * reflect their name attribute properly (they are not picked up
			 * by getElementsByName OR form[name])
			 */
			return form.querySelectorAll("[name=\"" + name + "\"]");
		};

		/*
		 * Grab some HTML and dump it into the testHolder
		 */
		this.setUpExternalHTML = function(urlResource, testHolder) {
			var utils = this;
			var result = new Promise(function(win, lose) {
				utils.loadResource(urlResource, function(response) {
					testHolder = testHolder || utils.getTestHolder();
					testHolder.innerHTML = response;
					window.setTimeout(function() {
						win();
					}, 0);
				}, lose);
			});
			return result;


		};

		/*
		 * If you set the value make sure you set the mimeType accordingly
		 * Note that mimeType can only be used in browsers that support
		 * HTML5 file input features
		 */
		this.MockFileSelector = function(accept, value, mimeType, size) {
			this.accept = accept || "";
			this.value = value || "";
			if (value && !mimeType) {
				throw new TypeError("If you set the value make sure you set the mimeType accordingly");
			}
			if (useHtml5FileSelectors()) {
				// Mock file selector using HTML5 features
				this.files = [{name: value, type: mimeType, size: (size || 0)}];
				this.files.item = function(i) {
					return this[i];
				};
			}
		};

		function submitEvent($event) {
			/*
			 * cancel all form submits - they should never happen in the tests but when something goes wrong they
			 * can happen and that means BAAD things.
			 */
			if (!$event.defaultPrevented) {
				$event.preventDefault();
				var err = new ReferenceError("Trying to submit form in tests ");
				throw err;
			}
		}

	}

	return instance;
});
