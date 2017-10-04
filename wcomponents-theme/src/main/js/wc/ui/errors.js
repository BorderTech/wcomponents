define(["wc/dom/diagnostic", "wc/ui/diagnostic"],
	function(diagnostic, uiDiagnostic) {
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
				// if the target already has an error box then use it
				if ((errorContainer = diagnostic.getBox(target))) {
					uiDiagnostic.change(errorContainer, level);
					uiDiagnostic.addMessages(errorContainer, level);
					return errorContainer.id;
				}
				result = uiDiagnostic.add({
					target: target,
					messages: messages,
					level: diagnostic.LEVEL.ERROR,
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
					uiDiagnostic.remove(element, target);
				} else if ((errorContainer = diagnostic.getBox(element))) {
					uiDiagnostic.remove(errorContainer, element);
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
