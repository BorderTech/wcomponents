// TODO make this module use wc/ui/errors
define(["wc/dom/Widget", "wc/dom/classList", "wc/i18n/i18n", "wc/ui/icon"], function(Widget, classList, i18n, icon) {
	var instance = new Feedback(),
		/** Handle for the string "true".
		 * @constant {String}
		 * @private*/
		TRUE = "true",
		/**
		 * Handle for the aria-invalid attribute.
		 * @constant
		 * @type {String}
		 * @private */
		INVALID = "aria-invalid",
		/**
		 * The description of the component used to hold a validation error/success message. Instantiated on
		 * first use.
		 * @constant
		 * @type {module:wc/dom/Widget}
		 * @private */
		MESSAGE_HOLDER,
		/**
		 * The class used to indicate a success: the type attribute of the ui:messageBox for success.
		 * @constant
		 * @type {String}
		 * @private */
		SUCCESS = "wc-fieldindicator-type-success",
		/**
		 * Handle for the aria-labelledby attribute.
		 * @constant
		 * @type {String}
		 * @private */
		LABEL_ATTRIB = "aria-describedby",
		/**
		 * The class used to indicate an error: the type attribute of the ui:messageBox for errors.
		 * @constant
		 * @type {String}
		 * @private */
		ERROR = "wc-fieldindicator-type-error",
		/**
		 * ID suffix applied to an inline error message.
		 * @constant
		 * @type {String}
		 * @private */
		ERROR_BOX_SUFFIX = "_err",
		/**
		 * The description of the component which holds a list of WValidationErrors. Instantiated on first use.
		 * @constant
		 * @type {module:wc/dom/Widget}
		 * @private */
		VALIDATION_ERRORS,
		/**
		 * The description of the component which is each error link in a WValidationErrors container.
		 * Instantiated on first use.
		 * @constant
		 * @type {module:wc/dom/Widget}
		 * @private */
		ERROR_LINK;

	/**
	 * This module knows how to provide feedback to the user about error states and invalid input.
	 * @constructor
	 */
	function Feedback() {
		/**
		 * Flags a component in an error state with an appropriate error message.
		 * @function module:wc/ui/validation/validationManager.flagError
		 * @param {module:wc/ui/validation/feedback~flagconfig} obj Configuration object.
		 */
		this.flagError = function(obj) {
			var element = obj["element"],
				message = obj["message"],
				position = obj["position"] || "afterEnd",
				attachTo = obj["attachTo"] || element,
				labelledBy, error, errorBoxId, errorBox;

			if ((errorBox = getErrorBox(element))) {
				MESSAGE_HOLDER = MESSAGE_HOLDER || new Widget("span");
				error = MESSAGE_HOLDER.findDescendant(errorBox);
				error.innerHTML = "";
				error.innerHTML = message;
				if (classList.contains(errorBox, SUCCESS)) {
					classList.remove(errorBox, SUCCESS);
					classList.add(errorBox, ERROR);
					element.setAttribute(INVALID, TRUE);
					icon.change(errorBox, "fa-times-circle", "fa-check-circle");
				}
			}
			else {
				errorBoxId = element.id + ERROR_BOX_SUFFIX;
				errorBox = "<span id='" + errorBoxId + "' class='wc-fieldindicator "+ ERROR +"' role='alert'><i aria-hidden='true' class='fa fa-times-circle'></i><span>" + message + "</span></span>";
				attachTo.insertAdjacentHTML(position, errorBox);
				element.setAttribute(INVALID, TRUE);
				if ((labelledBy = element.getAttribute(LABEL_ATTRIB))) {
					element.setAttribute(LABEL_ATTRIB, labelledBy + " " + errorBoxId);
				}
				else {
					element.setAttribute(LABEL_ATTRIB, errorBoxId);
				}
			}
			// whenever we get a new client side validation error remove any existing server side error
			removeWValidationErrorLink(element);
		};

		/**
		 * <p>Updates an element and its error box once an error is corrected.</p>
		 *
		 * <p>This should not usually be called directly from a validating component since the components should use
		 * a revalidation mechanism and let the validationManager take care of setting the error state. For simple
		 * mandatory selectable controls though where any selection makes the control valid (such as checkBox or
		 * radioButtonSelect) then it is much more economic to call this in a shed subscriber than to run through
		 * the, sometimes complex, validator.</p>
		 *
		 * <p>TODO: revisit the above assertion and possibly make this private.</p>
		 *
		 * @function module:wc/ui/validation/validationManager.setOK
		 * @param {Element} element the HTML element which was in an error state.
		 */
		this.setOK = function(element) {
			var errorBox = getErrorBox(element), next;
			if (errorBox) {
				classList.remove(errorBox, ERROR);
				classList.add(errorBox, SUCCESS);
				element.removeAttribute(INVALID);
				icon.change(errorBox, "fa-check-circle", "fa-times-circle");

				if (!(next = errorBox.firstElementChild)) {
					while ((next = errorBox.firstChild)) {
						if (next.nodeType === Node.ELEMENT_NODE) {
							break;
						}
						else {
							errorBox.removeChild(next);
						}
					}
				}
				if (next) {
					classList.remove(next, ERROR);
					next.innerHTML = i18n.get("validation_ok");
				}
				removeWValidationErrorLink(element);
			}
		};

		/**
		 * <p>Determines if a component is associated with a message indicating that an error has been resolved.</p>
		 *
		 * <p>If an element was invalid but then marked as OK (after user update) it will have an "error box" which
		 * is a success box. These may need to be removed in some cases or exchanged for real errors if the user
		 * subsequently updates the input again to an invalid state.</p>
		 *
		 * @function
		 * @param {Element} element The HTML element to test.
		 * @returns {Boolean} true if the element is associated with a success message.
		 */
		this.isMarkedOK = function(element) {
			var errorBox = getErrorBox(element), result = false;
			if (errorBox && classList.contains(errorBox, SUCCESS)) {
				result = true;
			}
			return result;
		};

		/**
		 * Remove a link to a component which was in an error state when the page was loaded (using
		 * WValidationErrors) but which was subsequently corrected.
		 * @function
		 * @private
		 * @param {Element} element The HTML element which was in an error state
		 */
		function removeWValidationErrorLink(element) {
			var validationErrors, errorLinkWidget, errorLink, errorLinkParent;
			VALIDATION_ERRORS = VALIDATION_ERRORS || new Widget("", ["wc_msgbox", "wc-messagebox-type-error"]);

			if ((validationErrors = VALIDATION_ERRORS.findDescendant(document.body))) {
				if (!ERROR_LINK) {
					ERROR_LINK = new Widget("a");
					ERROR_LINK.descendFrom(VALIDATION_ERRORS);
				}
				errorLinkWidget = ERROR_LINK.extend("", {href: ("#" + element.id)});
				while ((errorLink = errorLinkWidget.findDescendant(validationErrors)) && (errorLinkParent = errorLink.parentNode)) {
					errorLinkParent.parentNode.removeChild(errorLinkParent);
				}

				if (!ERROR_LINK.findDescendant(validationErrors)) {
					validationErrors.parentNode.removeChild(validationErrors);
				}
			}
		}

		/**
		 * <p>Removes any existing validation error messages. This is safe because a field cannot have more than one
		 * error at a time since the constraint validators require content which excludes the required validator.</p>
		 *
		 * <p>Individual components should not clearError but should call setOK instead and the validationManager can
		 * determine whether an error should be removed or modified.</p>
		 *
		 * @function
		 * @param {Element} element The element from which to remove the error messages.
		 */
		this.clearError = function(element) {
			var labelledBy = element.getAttribute(LABEL_ATTRIB), errorBox, i;

			if ((errorBox = getErrorBox(element)) && (labelledBy = element.getAttribute(LABEL_ATTRIB).split( /\s+/))) {
				i = labelledBy.indexOf(errorBox.id);
				if (i || i === 0) {
					labelledBy.splice(i, 1);
				}

				errorBox.parentNode.removeChild(errorBox);
				element.removeAttribute(INVALID);

				if ((labelledBy = labelledBy.sort().join(" ").trim())) {  // sorting before joining makes sure trim gets rid of the excess space since all the nulls will be at one end
					element.setAttribute(LABEL_ATTRIB, labelledBy);
				}
				else {
					element.removeAttribute(LABEL_ATTRIB);
				}
			}
		};

		/**
		 * Get the error box associated with a given component.
		 * @function
		 * @private
		 * @param {Element} element The HTML element for which we need to get an error box.
		 * @returns {Element} The error box if one already exists.
		 */
		function getErrorBox(element) {
			var id, result;
			if ((id = element.id)) {
				result = document.getElementById(id + ERROR_BOX_SUFFIX);
			}
			return result;
		}
	}

	/**
	 * Configuration object for flagging errors.
	 *
	 * @typedef module:wc/ui/validation/feedback~flagconfig
	 * @property {Element} element An element with message to show.</dd>
	 * @property {String} message The message to display.</dd>
	 * @property {String} [position] Argument for insertAdjacentHTML. Defaults to "afterEnd".
	 * @property {Element} [attachTo] An alternate element on which we call insertAdjacentHTML. If not set the
	 *    flag is attached to element.
	 */

	return instance;
});
