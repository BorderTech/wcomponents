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
	"wc/has",
	"wc/i18n/i18n",
	"wc/file/size",
	"wc/file/accepted",
	"wc/ui/prompt",
	"wc/dom/Widget",
	"wc/dom/focus",
	"wc/isNumeric",
	"wc/ui/ajaxRegion"],
function(attribute, event, initialise, has, i18n, size, accepted, prompt, Widget) {
	"use strict";

	/**
	 * @constructor
	 * @alias module:wc/ui/fileUpload~FileUpload
	 * @private
	 */
	function FileUpload() {
		var INITED_KEY = "wc.ui.fileUpload.inited",
			CONTAINER = new Widget("", "wc-fileupload"),
			inputElementWd = new Widget("INPUT", "", { type: "file"});

		inputElementWd.descendFrom(CONTAINER, true);

		/**
		 * The user would like to upload a file via a file input, this is the entry point to the process.
		 * @param {Element} element The file input the user is interacting with.
		 * @param {boolean} suppressEdit If true then image editing will be turned off for this upload.
		 */
		function upload(element, suppressEdit) {
			var editorId = element.getAttribute("data-wc-editor"),
				skipEdit = suppressEdit || (has("ie") > 0 && has("ie") < 10);
			if (!skipEdit && editorId) {
				require(["wc/ui/imageEdit"], function (imageEdit) {
					imageEdit.editFiles(element, checkDoUpload, onError);
				});
			} else {
				checkDoUpload(element);
			}
		}

		/**
		 * Validate the file chosen and commence the upload if all is well.
		 * @function
		 * @private
		 * @param {Element} element A file input element.
		 * @param {File[]} [files] A collection of File items to use instead of element.files.
		 */
		function checkDoUpload(element) {
			var message;
			if (!element.value) {
				// nothing to do
				return;
			}
			message = size.check(element);
			if (!message && !accepted(element)) {
				message = i18n.get("file_wrongtype", element.accept);
			}
			if (message) {
				onError(element, message);
			}
		}

		/**
		 * Something is not right with the upload request.
		 * @param {Element} element The file input element that triggered the upload.
		 * @param {string} [message] A message to display to the user.
		 */
		function onError(element, message) {
			if (message) {
				prompt.alert(message);
			}
			instance.clearInput(element);
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
				upload(element);
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
			} else {
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
			if (element) {
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
