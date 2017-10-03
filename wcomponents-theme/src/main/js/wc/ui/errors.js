define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/array/toArray",
	"wc/dom/tag",
	"wc/ui/getFirstLabelForElement",
	"wc/dom/wrappedInput",
	"wc/ui/ajax/processResponse",
	"wc/config"],
	function(initialise, Widget, toArray, tag, getFirstLabelForElement, wrappedInput, processResponse, wcconfig) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/errors~ErrorWriter
		 * @private
		 */
		function ErrorWriter() {
			var ERROR_BOX = new Widget("section", "wc-validationerrors"),
				ERROR_CLASS = "wc-error",
				ERROR = new Widget("", ERROR_CLASS),
				LINK = new Widget("a"),
				INLINE_ERROR,
				writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.TABLE];

			ERROR.descendFrom(ERROR_BOX);
			LINK.descendFrom(ERROR);

			function getErrorAsHtml(message) {
				return "<span class='" + ERROR_CLASS + "'>" + message + "</span>";
			}

			function getErrorHTML(id, message) {
				var config = wcconfig.get("wc/ui/errors"),
					errIcon = (config && config.icon) ? config.icon : "fa-times-circle",
					result =  "<span class='wc-fieldindicator wc-fieldindicator-type-error' id='" + id + "'><i aria-hidden='true' class='fa " +
						errIcon + "'></i>";
				if (message) {
					result += getErrorAsHtml(message);
				}
				result += "</span>";
				return result;
			}

			/**
			 * Mark an invalid component as aria-invalid.
			 * @param {Element} target the component to mark
			 */
			function markInvalid(target) {
				var invalidElement;
				if (!target && target.tagName) {
					return;
				}
				invalidElement =  wrappedInput.getInput(target) || target;
				if (invalidElement) {
					invalidElement.setAttribute("aria-invalid", "true");
					invalidElement.setAttribute("aria-describedby", target.id + "_err");
				}
			}

			/**
			 * Get all validation error boxes from a container element.
			 * @function
			 * @private
			 * @param {Element} [container] the target container defaults to document.body
			 * @returns {Array} an array of error boxes, usually there should only be one.
			 */
			function getErrorBoxes(container) {
				var fromWhere;
				if (container && ERROR_BOX.isOneOfMe(container)) {
					return [container];
				}
				fromWhere = container || document.body;
				return toArray(ERROR_BOX.findDescendants(fromWhere));
			}

			/**
			 * Get all WValidationErrors errors in a container.
			 * @function
			 * @private
			 * @param {Element} container the container to test
			 * @returns {Array} an array of error `a` elements or an empty array if none found
			 */
			function getAllErrors(container) {
				var errorBoxes = getErrorBoxes(container),
					candidates = [];

				errorBoxes.forEach(function(next) {
					candidates = candidates.concat(toArray(LINK.findDescendants(next)));
				});
				return candidates;
			}

			function isCheckRadio(element) {
				return element.tagName === tag.INPUT && (element.type === "radio" || element.type === "checkbox");
			}

			/**
			 * Flag a component with an error message and put it into an invalid state..
			 * @param {module:wc/ui/errors.flagDto} args a config dto
			 */
			this.flagError = function(args) {
				var target,
					inputTarget,
					errorBoxId,
					errorContainer,
					writeWhere = args.position,
					html,
					i,
					message = args.message,
					currentErrors,
					BEFORE = "beforeEnd",
					AFTER = "afterEnd";
				target = args.element;
				if (!(target && message)) {
					return;
				}
				errorBoxId = target.id + "_err";
				// if the target already has an error box then use it:
				if ((errorContainer = document.getElementById(errorBoxId))) {
					INLINE_ERROR = INLINE_ERROR || new Widget("span", ERROR_CLASS); // DOES NOT DESEND FROM ERROR_BOX!!
					currentErrors = INLINE_ERROR.findDescendants(errorContainer);
					for (i = 0; i < currentErrors.length; ++i) {
						if (message.toLocaleLowerCase() === currentErrors[i].innerHTML.toLocaleLowerCase()) {
							// already have this message
							return;
						}
					}
					errorContainer.insertAdjacentHTML(BEFORE, getErrorAsHtml(message));
					return;
				}
				html = getErrorHTML(errorBoxId, message);
				if (wrappedInput.isOneOfMe(target)) {
					if ((inputTarget = wrappedInput.getInput(target)) && isCheckRadio(inputTarget)) {
						target = getFirstLabelForElement(inputTarget) || target;
					} else {
						target = wrappedInput.getWrapper(target) || target;
					}
					writeWhere = writeWhere || AFTER;
				} else if (isCheckRadio(target)) {
					target = getFirstLabelForElement(target) || target;
					writeWhere = writeWhere || AFTER;
				}

				if (!writeWhere) {
					writeWhere = ~writeOutsideThese.indexOf(target.tagName) ? AFTER : BEFORE;
				}
				target.insertAdjacentHTML(writeWhere, html);
				markInvalid(target);
			};

			function writeError(err) {
				var targetId;

				if (!err || !err.innerHTML) {
					return;
				}

				targetId = err.getAttribute("href");
				if (!targetId || targetId.indexOf("#") !== 0) {
					return; // not my error, should never get here
				}
				targetId = targetId.substr(1);

				instance.flagError({
					element: document.getElementById(targetId),
					message: err.innerHTML
				});
			}

			function writeErrors(container) {
				var errors = getAllErrors(container);
				if (errors && errors.length) {
					errors.forEach(writeError);
				}
			}

			this.postInit = function() {
				writeErrors();
				processResponse.subscribe(writeErrors, true);
			};

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._writeErrors = writeErrors;
		}

		/**
		 * This module knows how to provide feedback to the user about error states and invalid input.
		 * Note: I have removed the dependency on handlebars to increase this chance this module can continue to
		 * operate in error conditions for example the network cable being unplugged.
		 * @module
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/array/toArray
		 * @requires module:wc/dom/tag
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires module:wc/dom/wrappedInput
		 */
		var instance = new ErrorWriter();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/errors.flagDto The properties used to describe a custom error message.
		 * @property {String} message The message to display.
		 * @property {Element} element The element which is to be flagged with the error message.
		 * @property {String} [position=afterEnd] The position for the message as a `insertAdjacentHTML` position.
		 */
	});
