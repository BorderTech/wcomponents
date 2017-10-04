define(["wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/dom/diagnostic",
	"wc/ui/diagnostic",
	"wc/ui/errors",
	"wc/dom/messageBox"],
	function(Widget, i18n, diagnostic, uiDiagnostic, errors, messageBox) {
		var VALIDATION_ERRORS,
			ERROR_LINK;

		/**
		 * This module knows how to provide feedback to the user about error states and invalid input.
		 * @constructor
		 */
		function Feedback() {
			/**
			 * Remove a link to a component which was in an error state when the page was loaded (using
			 * WValidationErrors) but which was subsequently corrected.
			 * @function
			 * @private
			 * @param {Element} element The HTML element which was in an error state
			 */
			function removeWValidationErrorLink(element) {
				var validationErrors, errorLinkWidget, errorLink, errorLinkParent;

				if ((validationErrors = messageBox.getErrorBoxes(document.body, true))) {
					VALIDATION_ERRORS = messageBox.getErrorBoxWidget().clone;
					if (!ERROR_LINK) {
						ERROR_LINK = new Widget("a");
						ERROR_LINK.descendFrom(VALIDATION_ERRORS);
					}
					errorLinkWidget = ERROR_LINK.extend("", {href: ("#" + element.id)});
					while ((errorLink = errorLinkWidget.findDescendant(document.body)) && (errorLinkParent = errorLink.parentNode)) {
						errorLinkParent.parentNode.removeChild(errorLinkParent);
					}

					Array.prototype.forEach.call(validationErrors, function (validErr) {
						if (!ERROR_LINK.findDescendant(validErr)) {
							validErr.parentNode.removeChild(validErr);
						}
					});
				}
			}

			/**
			 * Flags a component in an error state with an appropriate error message.
			 * @function
			 * @param {module:wc/ui/validation/feedback~flagconfig} dto Configuration object.
			 */
			this.flagError = function(dto) {
				var element = dto.element,
					result = errors.flagError({
						element: element,
						message: dto.message,
						level: diagnostic.LEVEL.ERROR
					});
				// whenever we get a new client side validation error remove any existing server side error
				removeWValidationErrorLink(element);
				return result;
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
			 * @function
			 * @public
			 * @param {Element} element the HTML element which was in an error state.
			 */
			this.setOK = function(element) {
				var errorBox = diagnostic.getDiagnostic(element, diagnostic.LEVEL.ERROR);
				if (errorBox) {
					uiDiagnostic.change(errorBox, diagnostic.LEVEL.SUCCESS);
					uiDiagnostic.set(errorBox, i18n.get("validation_ok"));
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
				return !!diagnostic.getDiagnostic(element, diagnostic.TYPE.SUCCESS);
			};

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
				uiDiagnostic.remove(null, element);
			};
		}

		/**
		 * Configuration object for flagging errors.
		 *
		 * @typedef module:wc/ui/validation/feedback~flagconfig
		 * @property {Element} element An element with message to show.
		 * @property {String} message The message to display.
		 */

		return new Feedback();
	});
