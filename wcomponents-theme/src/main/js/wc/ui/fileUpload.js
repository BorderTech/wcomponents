/**
 * Provides functionality associated with uploading single files using a WFileWidget.
 * *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires external:lib/sprintf
 * @requires module:wc/has
 * @requires module:wc/i18n/i18n
 * @requires module:wc/file/getFileSize
 * @requires module:wc/file/accepted
 * @requires module:wc/dom/Widget
 * @requires module:wc/timers
 *
 */
define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"lib/sprintf",
		"wc/has",
		"wc/i18n/i18n",
		"wc/file/getFileSize",
		"wc/file/accepted",
		"wc/dom/Widget",
		"wc/timers",
		"wc/dom/focus",
		"wc/isNumeric",
		"wc/ui/ajaxRegion"],
	/** @param attribute @param event @param initialise @param sprintf @param has @param i18n @param getFileSize @param accepted @param Widget @param timers @ignore */
function(attribute, event, initialise, sprintf, has, i18n, getFileSize, accepted, Widget, timers) {
	"use strict";

	/**
	 * @constructor
	 * @alias module:wc/ui/fileUpload~FileUpload
	 * @private
	 */
	function FileUpload() {
		var INITED_KEY = "wc.ui.fileUpload.inited",
			ROUND_SIG_FIG = 1,
			KB = Math.pow(10, 3),  /* NOTE: see IEC 80000-13 a kilo-byte is 1000 bytes, NOT 1024 bytes */
			MB = Math.pow(10, 6),
			GB = Math.pow(10, 9),
			inputElementWd = new Widget("INPUT", "", { type: "file", "data-wc-maxfiles": "1" }),
			messageTimer;



		/**
		 * Rounds a numerical filesize value to something acceptable to display to the user.
		 * @param {Number} value The number to round.
		 * @returns {Number} The rounded version of the value.
		 */
		function round(value) {
			var intPart = parseInt(value, 10),
				modPart,
				exp;
			if (intPart === value) {
				return value;
			}
			exp = Math.pow(10, ROUND_SIG_FIG);
			modPart = Math.round((value % 1) * exp);
			return intPart + (modPart / exp);
		}

		/**
		 * Presents the message to the user.
		 * @function
		 * @private
		 * @param {String} message The message to present.
		 */
		function showMessage(message) {
			if (messageTimer) {
				timers.clearTimeout(messageTimer);
			}
			messageTimer = timers.setTimeout(function() {
				window.alert(message);
			}, 250);
		}

		/**
		 * Validate the file chosen and commence the asynchronous upload if all is well.
		 * @function
		 * @private
		 * @param {Element} element A file input element.
		 * @param {File[]} [files] A collection of File items to use instead of element.files.
		 */
		function checkDoUpload(element) {
			var maxFileSize, fileSize;
			if (!element.value) {
				// nothing to do
				return;
			}
			maxFileSize = parseInt(element.getAttribute("data-wc-maxfilesize"), 10);
			fileSize = getFileSize(element);
			if (fileSize && fileSize.length) {
				fileSize = fileSize[0];
				if (maxFileSize < fileSize) {
					handleFileTooLarge(maxFileSize, fileSize);
					instance.clearInput(element);
				}
				else if (!accepted(element)) {
					showMessage(i18n.get("${wc.ui.multiFileUploader.i18n.wrongtype}", element.accept));
					instance.clearInput(element);
				}
			}
		}

		/**
		 * Helper for checkDoUpload, called if the file is too large.
		 * @function
		 * @private
		 * @param {number} maxFileSize The maximum allowed file size in bytes.
		 * @param {number} fileSize The actual file size in bytes.
		 */
		function handleFileTooLarge(maxFileSize, fileSize) {
			var maxFileSizeHR, fileSizeHR, roundTo, units;

			/* make the units human readable */
			if (maxFileSize >= GB) {
				roundTo = GB;
				units = i18n.get("${wc.ui.multiFileUploader.i18n.fileDesc.size.gb}");
			}
			else if (maxFileSize >= MB) {
				roundTo = MB;
				units = i18n.get("${wc.ui.multiFileUploader.i18n.fileDesc.size.mb}");
			}
			else if (maxFileSize >= KB) {
				roundTo = KB;
				units = i18n.get("${wc.ui.multiFileUploader.i18n.fileDesc.size.kb}");
			}

			if (roundTo) {
				maxFileSizeHR = round(maxFileSize / roundTo);
				fileSizeHR = round(fileSize / roundTo);
			}
			else {
				maxFileSizeHR = maxFileSize;
				fileSizeHR = fileSize;
				units = i18n.get("${wc.ui.multiFileUploader.i18n.fileDesc.size}");
			}
			showMessage(sprintf.sprintf(i18n.get("${wc.ui.multiFileUploader.i18n.toolarge}"), fileSizeHR, maxFileSizeHR, units));
		}


		/**
		 * Change event on the file input.
		 * Somebody wants to upload a file... Is it the right size and type?
		 * @function
		 * @private
		 * @param {Event} $event The change event.
		 */
		function changeEvent($event) {
			var element = $event.target;
			if (!$event.defaultPrevented && inputElementWd.isOneOfMe(element)) {
				checkDoUpload(element);
			}
		}

		/**
		 * Set up a file selector on first use.
		 * @function
		 * @private
		 * @param {Element} element A file input.
		 */
		function initialiseFileInput(element) {
			var isLowIE = has("ie") < 9,
				_el = element;
			if (inputElementWd.isOneOfMe(element)) {
				_el = isLowIE ? element : element.form;
				if (!attribute.get(_el, INITED_KEY)) {
					attribute.set(_el, INITED_KEY, true);
					event.add(_el, event.TYPE.change, changeEvent);
				}
			}
		}

		/**
		 * Handles the event/s that trigger bootstrapping of this widget.
		 * @funtion
		 * @private
		 * @param {Event} $event The event that triggers bootstrapping.
		 */
		function focusEvent($event) {
			initialiseFileInput($event.target);
		}

		/**
		 * Initialise file upload functionality by adding a focus listener.
		 * @function module:wc/ui/fileUpload.initialise
		 * @param {Element} element The element being initialised - usually document.body.
		 */
		this.initialise = function(element) {
			if (event.canCapture) {
				event.add(element, event.TYPE.focus, focusEvent, null, null, true);
			}
			else {
				event.add(element, event.TYPE.focusin, focusEvent);
			}
		};

		/**
		 * Get the {@link module:wc/dom/Widget} descriptor of the multi file upload component.
		 * @function module:wc/ui/fileUpload.getWidget
		 * @returns {module:wc/dom/Widget} The widget descriptor.
		 */
		this.getWidget = function() {
			return inputElementWd;
		};

		/**
		 * Tests if an element is a file upload.
		 * @function module:wc/ui/fileUpload.isOneOfMe
		 * @param {Element} element The DOM element to test
		 * @returns {Boolean} true if element is the Widget type rewuested
		 */
		this.isOneOfMe = function(element) {
			if (element)	{
				return inputElementWd.isOneOfMe(element);
			}
			return false;
		};

		/**
		 * Sets a file selector to an empty value.
		 * As usual this apparently simple task is made complex due to Internet Explorer.
		 * @param {Element} element A file input.
		 */
		this.clearInput = function (element) {
			var myClone;
			element.value = "";
			if (element.value !== "") {
				myClone = element.cloneNode(false);
				element.parentNode.replaceChild(myClone, element);
				initialiseFileInput(myClone);
			}
		};
	}


	var /** @alias module:wc/ui/fileUpload */ instance = new FileUpload();
	initialise.register(instance);
	return instance;
});
