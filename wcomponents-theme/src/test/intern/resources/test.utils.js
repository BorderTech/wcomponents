define(["wc/dom/event", "wc/Observer", "wc/compat/compat!"], function(event, Observer) {
	"use strict";
	function WcTestUtils() {
		var html5FileSelector,
			ajax,
			setupTimeout = 1000;

		/* If you want to test IE then you must ensure compat is loaded before trying to load ajax. */
		require(["wc/ajax/ajax", "wc/has", "wc/fixes"], function (a, has) {
			ajax = a;
			if (has("edge") || has("trident")) {
				setupTimeout = 1000;
			}
		});

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

		this.setupHelper = function(deps, cb) {
			var result = new this.LamePromisePolyFill();
			require(["wc/compat/compat!"], function () {
				require(deps, function() {
					cb.apply(this, arguments);
					window.setTimeout(function() {
						result._resolve();
					}, setupTimeout);
				});
			});
			return result;
		};

		this.getTestHolder = function(keepContent) {
			var testHolderId = this.TRANFORM_CONTAINER_ID,
				testHolder = document.getElementById(testHolderId);

			if (!testHolder) {
				testHolder = document.createElement("${wc.dom.html5.element.section}");
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
			var result = new this.LamePromisePolyFill();
			result.then(callback);
			result.catch(onerror);
			ajax.simpleRequest({
				url: url,
				callback: result._resolve,
				onError: result._reject
			});
			return result;
		};

		this.objectEqual = function (obj1, obj2) {
			var s1 = JSON.stringify(obj1),
				s2 = JSON.stringify(obj2);
			return s1 === s2;
		};

		this.LamePromisePolyFill = function() {
			var observer = new Observer();

			this._resolve = function() {
				observer.setFilter("resolve");
				observer.notify.apply(observer, arguments);
			};

			this._reject = function() {
				observer.setFilter("reject");
				observer.notify.apply(observer, arguments);
			};

			this.then = function(cb) {
				observer.subscribe(cb, {group: "resolve"});
			};

			this.catch = function(cb) {
				observer.subscribe(cb, {group: "reject"});
			};
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
			var result = new this.LamePromisePolyFill();
			this.loadResource(urlResource, function(response) {
				testHolder = testHolder || utils.getTestHolder();
				testHolder.innerHTML = response;
				window.setTimeout(function() {
					result._resolve();
				}, 0);
			}, result._reject);
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

	return new WcTestUtils();
});
