define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/array/toArray",
	"wc/dom/tag",
	"wc/ui/getFirstLabelForElement",
	"wc/dom/wrappedInput"],
	function(initialise, Widget, toArray, tag, getFirstLabelForElement, wrappedInput) {
		"use strict";
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
		/**
		 * @constructor
		 * @alias module:wc/ui/errors~ErrorWriter
		 * @private
		 */
		function ErrorWriter() {
			var ERROR_BOX = new Widget("section", "wc-validationerrors"),
				ERROR = new Widget("", "wc-error"),
				LINK = new Widget("a"),
				writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.TABLE],
				CONTAINER_TEMPLATE = function(args) {
					var result =  "<span class=\"wc-fieldindicator wc-fieldindicator-type-error\" id=\"" + args.id + "\"><i aria-hidden='true' class='fa fa-times-circle'></i>";
					if (args.errors) {
						result += "<span class=\"wc-error\">" + args.errors + "</span>";
					}
					result += "</span>";
					return result;
				},
				ERROR_TEMPLATE = function(args) {
					return "<span class=\"wc-error\">" + args.error + "</span>";
				};

			ERROR.descendFrom(ERROR_BOX);
			LINK.descendFrom(ERROR);

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
				var _container = container || document.body;
				if (container && ERROR_BOX.isOneOfMe(container)) {
					return [container];
				}
				return toArray(ERROR_BOX.findDescendants(_container));
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

			/**
			 * Flag a component with an error message and put it into an invalid state..
			 * @param {module:wc/ui/errors.flagDto} args a config dto
			 */
			this.flagError = function(args) {
				var props,
					target,
					inputTarget,
					errorBoxId,
					errorContainer,
					writeWhere = args.position,
					html,
					doWriteError = function() {
						var errorhtml,
							innerprops;
						innerprops = {
							error: args.message
						};
						errorhtml = ERROR_TEMPLATE(innerprops);
						if (errorhtml) {
							errorContainer.insertAdjacentHTML("beforeEnd", errorhtml);
						}
					};
				target = args.element;
				if (!target) {
					return; // linked to something not in the UI.
				}
				errorBoxId = target.id + "_err";
				// if the target already has an error box then use it:
				if ((errorContainer = document.getElementById(errorBoxId))) {
					doWriteError();
					return;
				}

				props = {
					id: errorBoxId,
					errors: args.message
				};
				if ((html = CONTAINER_TEMPLATE(props))) {
					if (wrappedInput.isOneOfMe(target)) {
						if ((inputTarget = wrappedInput.getInput(target)) && inputTarget.tagName === tag.INPUT && (inputTarget.type === "radio" || inputTarget.type === "checkbox")) {
							target = getFirstLabelForElement(inputTarget) || target;
						} else {
							target = wrappedInput.getWrapper(target) || target;
						}
						writeWhere = writeWhere || "afterEnd";
					} else if (target.tagName === tag.INPUT && (target.type === "radio" || target.type === "checkbox")) {
						target = getFirstLabelForElement(target) || target;
						writeWhere = writeWhere || "afterEnd";
					}

					if (!writeWhere) {
						writeWhere = ~writeOutsideThese.indexOf(target.tagName) ? "afterEnd" : "beforeEnd";
					}
					target.insertAdjacentHTML(writeWhere, html);
					markInvalid(target);
				}
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

			initialise.addCallback(function(element) {
				writeErrors(element);
			});

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._writeErrors = writeErrors;
		}

		return instance;

		/**
		 * @typedef {Object} module:wc/ui/errors.flagDto The properties used to describe a custom error message.
		 * @property {String} message The message to display.
		 * @property {Element} element The element which is to be flagged with the error message.
		 * @property {String} [position=afterEnd] The position for the message as a `insertAdjacentHTML` position.
		 */
	});
