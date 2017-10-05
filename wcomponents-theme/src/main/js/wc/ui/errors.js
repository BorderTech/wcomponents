define(["wc/ui/diagnostic",
	"wc/i18n/i18n"],
	function(diagnostic, i18n) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/errors~ErrorWriter
		 * @private
		 */
		function ErrorWriter() {
			/**
			 * Flag a component with an error message and put it into an invalid state..
			 * @param {module:wc/ui/errors~flagDto} args a config dto
			 * @returns {String?} the id of the error container (if one is present/created)
			 */
			this.flagError = function(args) {
				var target = args.element,
					messages = args.message,
					level = args.level || diagnostic.LEVEL.ERROR,
					errorContainer,
					result;
				if (!(target && messages)) {
					return null;
				}

				if (target.constructor === String) {
					target = document.getElementById(target);
					if (!target) {
						return;
					}
				}

				// if the target already has an error box then use it
				if ((errorContainer = diagnostic.getBox(target))) {
					diagnostic.change(errorContainer, level);
					diagnostic.addMessages(errorContainer, level);
					return errorContainer.id;
				}
				result = diagnostic.add({
					target: target,
					messages: messages,
					level: level,
					position: args.position
				});
				if (result) {
					return result.boxId;
				}

				return null;
			};

			/**
			 * Remove an error diagnostic.
			 * @function
			 * @public
			 * @param {Element} element either an error diagnostic or an element with an error diagnostic
			 * @param {Element} [target] an element with a diagnostic **if** element is a diagnostic and we have already found its "owner".
			 */
			this.clearError = function(element, target) {
				var errorContainer;
				if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
					return;
				}
				if (diagnostic.isOneOfMe(element)) {
					diagnostic.remove(element, target);
				} else if ((errorContainer = diagnostic.getBox(element))) {
					diagnostic.remove(errorContainer, element);
				}
			};

			/**
			 * Indicates whether a component is associated with a message indicating that an error has been resolved.
			 *
			 * @function
			 * @param {Element} element The HTML element to test.
			 * @returns {boolean} `true` if the element is associated with a success message.
			 */
			this.isMarkedOK = function(element) {
				return !!diagnostic.getBox(element, diagnostic.LEVEL.SUCCESS);
			};

			/**
			 * Updates an error box to a succcess box and its error box once an error is corrected.
			 *
			 * @function
			 * @public
			 * @param {Element} element the HTML element which was in an error state.
			 */
			this.setOK = function(element) {
				var errorBox = diagnostic.getBox(element, -1);
				if (errorBox) {
					diagnostic.change(errorBox, diagnostic.LEVEL.SUCCESS);
					diagnostic.set(errorBox, i18n.get("validation_ok"));
				}
			};
		}

		/**
		 * This module knows how to provide feedback to the user about error states and invalid input.
		 * Note: I have removed the dependency on handlebars to increase this chance this module can continue to
		 * operate in error conditions for example the network cable being unplugged.
		 * @module
		 * @requires wc/dom/diagnostic
		 * @requires wc/ui/diagnostic
		 */
		var instance = new ErrorWriter();
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/errors~flagDto The properties used to describe a custom error message.
		 * @property {String|String[]} message The message to display.
		 * @property {Element} element The element which is to be flagged with the error message.
		 * @property {String} [position=afterEnd] The position for the message as a `insertAdjacentHTML` position.
		 *
		 * @typedef {Object} module:wc/ui/errors~config Optional run-time configuration for this module.
		 * @property {String} [icon=fa-times-circle] The font-awesome classname for the icon to display in the error box.
		 */
	});
