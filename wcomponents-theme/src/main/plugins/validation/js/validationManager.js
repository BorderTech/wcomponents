/**
 * Generic client side validation manager. This is the publisher for client side validation. Any component which
 * requires validation subscribes to this using validationManager.subscribe.
 *
 * @module ${validation.core.path.name}/validationManager
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/getBox
 * @requires module:wc/has"
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/Observer
 * @requires external:lib/sprintf
 * @requires module:wc/i18n/i18n
 */
define(["wc/dom/classList",
		"wc/dom/getBox",
		"wc/has",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/Observer",
		"wc/i18n/i18n"],
	/** @param classList @param getBox @param has @param initialise @param shed @param tag @param Widget @param Observer @param i18n @ignore*/
	function(classList, getBox, has, initialise, shed, tag, Widget, Observer, i18n) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/validationManager~ValidationManager
		 * @private
		 */
		function ValidationManager() {
			var
				/**
				 * At its heart validationManager is just an observer surrogate and this is the instance of
				 * {@link module:wc/Observer} used to subscribe and publish.
				 *
				 * @var
				 * @type {module:wc/Observer}
				 * @private
				 */
				observer,
				/**
				 * The description of a FORM. Instantiated on first use.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				FORM,
				/**
				 * Handle for the aria-labelledby attribute.
				 * @constant
				 * @type {String}
				 * @private */
				LABEL_ATTRIB = "aria-labelledby",
				/**
				 * The class used to indicate an error: the type attribute of the ui:messagebox for errors.
				 * @constant
				 * @type {String}
				 * @private */
				ERROR = "error",
				/**
				 * The class used to indicate a success: the type attribute of the ui:messagebox for success.
				 * @constant
				 * @type {String}
				 * @private */
				SUCCESS = "success",
				/**
				 * The description of the component used to hold a validation error/success message. Instantiated on
				 * first use.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private */
				MESSAGE_HOLDER,
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
				 * The description of a component in an invalid state.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				INVALID_COMPONENT = new Widget("", "", {"aria-invalid": TRUE}),
				/**
				 * ID suffix applied to an inline error message.
				 * @constant
				 * @type {String}
				 * @private */
				ERROR_BOX_SUFFIX = "${wc.ui.messageBox.id.inlineErrorBox.suffix}",
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

			/**
			 * <p>Removes any existing validation error messages. This is safe because a field cannot have more than one
			 * error at a time since the constraint validators require content which excludes the required validator.</p>
			 *
			 * <p>Individual components should not clearError but should call setOK instead and the validationManager can
			 * determine whether an error should be removed or modified.</p>
			 *
			 * @function
			 * @private
			 * @param {Element} element The element from which to remove the error messages.
			 */
			function clearError (element) {
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
			}

			/**
			 * <p>Determines if a component is associated with a message indicating that an error has been resolved.</p>
			 *
			 * <p>If an element was invalid but then marked as OK (after user update) it will have an "error box" which
			 * is a success box. These may need to be removed in some cases or exchanged for real errors if the user
			 * subsequently updates the input again to an invalid state.</p>
			 *
			 * @function
			 * @private
			 * @param {Element} element The HTML element to test.
			 * @returns {Boolean} true if the element is associated with a success message.
			 */
			function isMarkedOK(element) {
				var errorBox = getErrorBox(element), result = false;
				if (errorBox && classList.contains(errorBox, SUCCESS)) {
					result = true;
				}
				return result;
			}

			/**
			 * Tests if the current element is actually visible.
			 * NOTE: this is only guranteed to work for bounded elements such as INPUT, TEXTAREA etc but is close enough
			 * for our purposes for other elements since when an element is hidden its descendants have no dimensions.
			 * @function
			 * @private
			 * @param {Element} element The HTML element to test.
			 * @returns {Boolean} true if the element is associated with a success message.
			 */
			function isNotVisible(element) {
				var box = getBox(element);
				return !(box.width && box.height);
			}

			/**
			 * Remove a link to a component which was in an error state when the page was loaded (using
			 * WValidationErrors) but which was subsequently corrected.
			 * @function
			 * @private
			 * @param {Element} element The HTML element which was in an error state
			 */
			function removeWValidationErrorLink(element) {
				var validationErrors, errorLinkWidget, errorLink, errorLinkParent;
				VALIDATION_ERRORS = VALIDATION_ERRORS || new Widget("${wc.dom.html5.element.section}", ["wc_msgbox", "error"]);

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
			 * Listen for DISABLE, HIDE or OPTIONAL actions and clear any error message for the component.
			 * @function
			 * @private
			 * @param {Element} element The element being acted upon.
			 */
			function shedSubscriber(element) {
				if (element && INVALID_COMPONENT.isOneOfMe(element)) {
					clearError(element);
				}
			}

			/**
			 * Flags a component in an error state with an appropriate error message.
			 * @function module:${validation.core.path.name}/validationManager.flagError
			 * @param {module:${validation.core.path.name}/validationManager~flagconfig} obj Configuration object.
			 */
			this.flagError = function(obj) {
				var element = obj["element"],
					message = obj["message"],
					position = obj["position"] || "afterEnd",
					attachTo = obj["attachTo"] || element,
					labelledBy, error, errorBoxId, errorBox;

				if ((errorBox = getErrorBox(element))) {
					MESSAGE_HOLDER = MESSAGE_HOLDER || new Widget("li");
					error = MESSAGE_HOLDER.findDescendant(errorBox);
					error.innerHTML = "";
					error.innerHTML = message;
					if (classList.contains(errorBox, SUCCESS)) {
						classList.remove(errorBox, SUCCESS);
						classList.add(errorBox, ERROR);
						element.setAttribute(INVALID, TRUE);
					}
				}
				else {
					errorBoxId = element.id + ERROR_BOX_SUFFIX;
					errorBox = "<ul id='" + errorBoxId + "' class='error'><li>" + message + "</li></ul>";
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
			 * An element is exempt from participating in client side validation if:
			 * <ol><li>the element in INPUT type hidden;</li>
			 * <li>the element is disabled; or</li>
			 * <li>the element is not 'visible' (do shed test first - it is quicker).</li>
			 * </ol>
			 *
			 * @function module:${validation.core.path.name}/validationManager.isExempt
			 * @param {Element} element The component to test.
			 * @returns {Boolean} true if the component is exempt from client side validation.
			 */
			this.isExempt = function(element) {
				var result = false;
				if ((element.tagName === tag.INPUT && element.type === "hidden") || shed.isDisabled(element) ||
					(shed.isHidden(element) || isNotVisible(element))) {
					result = true;
				}
				return result;
			};


			/**
			 * Is an element currently in an invalid state? This is used to indicate that revalidation may be needed
			 * (commonly for a change event listener). NOTE: this does not test the validity of the element, merely
			 * returns whether anything has put the element into an invalid state previously.
			 *
			 * @function module:${validation.core.path.name}/validationManager.isInvalid
			 * @param {Element} element The component to test for validity.
			 * @returns {Boolean} true if the element is invalid.
			 */
			this.isInvalid = function(element) {
				return INVALID_COMPONENT.isOneOfMe(element);
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
			 * @function module:${validation.core.path.name}/validationManager.setOK
			 * @param {Element} element the HTML element which was in an error state.
			 */
			this.setOK = function(element) {
				var errorBox = getErrorBox(element), next;
				if (errorBox) {
					classList.remove(errorBox, ERROR);
					classList.add(errorBox, SUCCESS);
					element.removeAttribute(INVALID);

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
						next.innerHTML = i18n.get("${validation.core.i18n.nowOK}");
					}
					removeWValidationErrorLink(element);
				}
			};

			/**
			 * Most validating components have a pretty similar mechanism to revalidate whern their input changes so
			 * this helper exists to take care of it.
			 *
			 * @function module:${validation.core.path.name}/validationManager.revalidationHelper
			 * @param {Element} element The component being re-validated.
			 * @param {Function} _validateFunc The component's validation function.
			 */
			this.revalidationHelper = function(element, _validateFunc) {
				var initiallyInvalid = this.isInvalid(element),
					isNowInvalid = initiallyInvalid;

				if (initiallyInvalid) {
					if ((_validateFunc(element))) {
						this.setOK(element);
						isNowInvalid = false;
					}
					else {
						isNowInvalid = true;
					}
				}
				else if (isMarkedOK(element, this)) {
					isNowInvalid = !_validateFunc(element);
				}

				if (fieldset && isNowInvalid !== initiallyInvalid) {  // if the current component's validity has changed
					fieldset.revalidateFieldset(element);
				}
			};


			/**
			 * Tests the validity of form bound elements within a specified container.
			 *
			 * @function module:${validation.core.path.name}/validationManager.isValid
			 * @param {Element} [container] A DOM node (preferably containing form controls). If the container is not
			 *                   specified finds the form containing the activeElement (this is for use with controls
			 *                   with submitOnchange).
			 * @returns {Boolean} true if the container is in a valid state (all components in the container which
			 *                   support validation are valid).
			 */
			this.isValid = function (container) {
				var result = true;

				/**
				 * Observer callback function to keep track of validity from all subscribers. A container is only valid
				 * if all of its subscribers return true.
				 * @function
				 * @private
				 * @param {Boolean} decision true if valid.
				 * @returns {bitmap}
				 */
				function _callback(decision) {
					result &= decision;  // we are only valid if all observers are valid
				}

				if (!container) {
					FORM = FORM || new Widget("form");
					container = FORM.findAncestor(document.activeElement);
				}
				if (container && observer) {
					observer.setCallback(_callback);
					observer.notify(container);
				}

				result = !!result;  // convert the potentially bitwise result to a Boolean

				if (!result && repainter) {  // IE8 has repaint issues when validation errors are inserted into columns
					repainter.checkRepaint(container);
				}
				return result;
			};

			/**
			 * Late intialisation callback to subscribe to shed to listen for state changes which impact any existing
			 * validation error messages.
			 * @function module:${validation.core.path.name}/validationManager.postInit
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
			};

			/**
			 * Allows a component to subscribe to client side validation.
			 * @function module:${validation.core.path.name}/validationManager.subscribe
			 * @see {@link module:wc/Observer#subscribe}
			 *
			 * @param {Function} subscriber The function that will be notified by validationManager. This function MUST
			 *                   be present at "publish" time, but need not be preset at "subscribe" time.
			 * @returns {?Function} A reference to the subscriber.
			 */
			this.subscribe = function(subscriber) {
				function _subscribe(_subscriber) {
					return observer.subscribe(_subscriber);
				}

				if (!observer) {
					observer = new Observer();
					this.subscribe = _subscribe;
				}
				return _subscribe(subscriber);
			};

		}

		var repainter,
			/** @alias module:${validation.core.path.name}/validationManager */ instance,
			fieldset;

		/* circular dependency on fieldset validation. */
		require(["${validation.core.path.name}/fieldset"], function(f) {
			fieldset = f;
		});

		/* ie8's interesting inline-block bug means we need to force a repaint after all validating activites.*/
		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		instance = new ValidationManager();
		initialise.register(instance);
		return instance;

		/**
		 * Configuration object for flagging errors.
		 *
		 * @typedef module:${validation.core.path.name}/validationManager~flagconfig
		 * @property {Element} element An element with message to show.</dd>
		 * @property {String} message The message to display.</dd>
		 * @property {String} [position] Argument for insertAdjacentHTML. Defaults to "afterEnd".
		 * @property {Element} [attachTo] An alternate element on which we call insertAdjacentHTML. If not set the
		 *    flag is attached to element.
		 */
	});
