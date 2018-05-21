define(["wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/i18n/i18n",
	"wc/ui/getFirstLabelForElement",
	"wc/dom/Widget",
	"wc/ui/validation/isComplete",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/required",
	"wc/ui/multiFileUploader"],
	function(attribute, event, initialise, shed, i18n, getFirstLabelForElement, Widget, isComplete, validationManager, required, multiFileUploader) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/fileUpload~ValidationFileUpload
		 * @private
		 */
		function ValidationFileUpload() {
			var INITED_KEY = "validation.multiFileUploader.inited",
				CONTAINER = multiFileUploader.getWidget(),
				INPUT_ELEMENT = multiFileUploader.getInputWidget(),
				FILE_ELEMENT = new Widget("", "wc-file");

			/**
			 * Validates all file upload controls within a container.
			 * @function
			 * @private
			 * @param {Element} container The element being validated.
			 * @returns {boolean} true if valid.
			 */
			function validate(container) {
				var obj;

				function _messageFunc(element) {
					var legend = getFirstLabelForElement(element, true) || element.title;
					return i18n.get("validation_multifile_incomplete", legend);
				}

				obj = {container: container,
					widget: CONTAINER,
					constraint: required.CONSTRAINTS.CLASSNAME,
					position: "beforeEnd",
					messageFunc: _messageFunc};
				return required.complexValidationHelper(obj);
			}

			/**
			 * A WMultiFileWidget which is required will be valid if a "file" is present, even if unselected.  A
			 * WMultiFileWidget is complete if it has any file checkboxes in it or if the file input has a value
			 * @function
			 * @private
			 * @param {Element} element The WMultiFileWidget to test.
			 * @returns {boolean} true if complete.
			 */
			function amIComplete(element) {
				var result = false,
					upload = INPUT_ELEMENT.findDescendant(element);
				if (upload.value || FILE_ELEMENT.findDescendant(element)) {
					result = true;
				}
				return result;
			}

			/**
			 * Subscriber to {@link ./isComplete} used to indicate that the file uploads within a particular
			 * container are complete.
			 *
			 * @function
			 * @private
			 * @param {Element} container The element being tested.
			 * @returns {boolean} true if complete.
			 */
			function isThisComplete(container) {
				return isComplete.isCompleteHelper(container, CONTAINER, amIComplete);
			}


			/**
			 * Change event on the file input. Somebody wants to upload a file... So we need to re-validate.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event A change event as wrapped by the WComponent event manager.
			 */
			function changeEvent($event) {
				var element = $event.target;
				if (validationManager.isValidateOnChange()) {
					if (validationManager.isInvalid(element)) {
						validationManager.revalidationHelper(element, validate);
						return;
					}
					validate(element);
					return;
				}
				validationManager.revalidationHelper(element, validate);
			}

			function blurEvent($event) {
				var element = $event.target;
				if (!element.value && shed.isMandatory(element)) {
					validate(element);
				}
			}

			/**
			 * Focus handler for browsers which do not capture.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event A focus[in] event as wrapped by the WComponent event manager.
			 */
			function focusEvent($event) {
				var element = $event.target;

				if (INPUT_ELEMENT.isOneOfMe(element) && !attribute.get(element, INITED_KEY)) {
					attribute.set(element, INITED_KEY, true);
					event.add(element, event.TYPE.change, changeEvent);
					if (validationManager.isValidateOnBlur()) {
						if (event.canCapture) {
							event.add(element, event.TYPE.blur, blurEvent, 1, null, true);
						} else {
							event.add(element, event.TYPE.focusout, blurEvent);
						}
					}
				}
			}

			/**
			 * Initialisation callback to attach events.
			 * @function module:wc/ui/validation/fileUpload.initialise
			 * @param {Element} element The element being initialised.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Late initialisation callback to attach subscribers.
			 * @function module:wc/ui/validation/fileUpload.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
				isComplete.subscribe(isThisComplete);
			};
		}

		/**
		 * Provides functionality to undertake client validation of WFileWidget and WMultiFileWidget.
		 *
		 * @module
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/ui/validation/isComplete
		 * @requires module:wc/ui/validation/validationManager
		 * @requires module:wc/ui/validation/required
		 * @requires module:wc/ui/multiFileUploader
		 */
		var instance = new ValidationFileUpload();
		initialise.register(instance);
		return instance;
	});
