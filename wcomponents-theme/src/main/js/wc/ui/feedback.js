define(["wc/ui/diagnostic",
	"wc/i18n/i18n"],
	function(diagnostic, i18n) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/feedback~Feedback
		 * @private
		 */
		function Feedback() {
			/**
			 * Flag a component with a message.
			 * @function
			 * @private
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String?} the id of the message container (if one is present/created)
			 */
			function flag(args) {
				var target = args.element,
					messages = args.message,
					level = args.level,
					errorContainer,
					result;
				if (!(target && messages && level)) {
					return null;
				}

				if (target.constructor === String) {
					target = document.getElementById(target);
					if (!target) {
						return;
					}
				}

				// if the target already has an appropriate box then use it
				if ((errorContainer = diagnostic.getBox(target, level))) {
					diagnostic.change(errorContainer, level);
					diagnostic.addMessages(errorContainer, messages);
					return errorContainer.id;
				} // Success and failure are mutually exclusive
				if ((level === diagnostic.LEVEL.ERROR && (errorContainer = diagnostic.getBox(target, diagnostic.LEVEL.SUCCESS))) ||
					(level === diagnostic.LEVEL.SUCCESS && (errorContainer = diagnostic.getBox(target, diagnostic.LEVEL.ERROR)))) {
					diagnostic.change(errorContainer, level);
					diagnostic.addMessages(errorContainer, messages);
					return errorContainer.id;
				}

				result = diagnostic.add({
					target: target,
					messages: messages,
					level: level
				});
				if (result) {
					return result.boxId;
				}

				return null;
			}

			/**
			 * Flag a component with an error message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String?} the id of the error container (if one is present/created)
			 */
			this.flagError = function(args) {
				var dto = args;
				dto.level = diagnostic.LEVEL.ERROR;
				return flag(dto);
			};

			/**
			 * Remove an error diagnostic.
			 * @function
			 * @public
			 * @param {Element} element either an error diagnostic or an element with an error diagnostic
			 * @param {Element} [target] an element with a diagnostic **if** element is a diagnostic and we have already found its "owner".
			 * @oaram {int} [level=1] the diagnostic level to remove if element is not a diagnostic box
			 */
			this.clear = function(element, target, level) {
				var errorContainer,
					lvl = level || diagnostic.LEVEL.ERROR;
				if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
					return;
				}
				if (diagnostic.isOneOfMe(element)) {
					diagnostic.remove(element, target);
				} else if ((errorContainer = diagnostic.getBox(element, lvl))) {
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
			 * Flag a component with a success message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String?} the id of the message container (if one is present/created)
			 */
			this.flagSuccess = function (args) {
				var dto = args;
				dto.level = diagnostic.LEVEL.SUCCESS;
				return flag(dto);
			};

			/**
			 * Updates an error box to a succcess box and its error box once an error is corrected.
			 *
			 * @function
			 * @public
			 * @param {Element} element the HTML element which was in an error state.
			 */
			this.setOK = function(element) {
				return flag({
					element: element,
					message: i18n.get("validation_ok"),
					level: diagnostic.LEVEL.SUCCESS
				});
			};

			/**
			 * Flag a component with a warning message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String?} the id of the message container (if one is present/created)
			 */
			this.flagWarning = function (args) {
				var dto = args;
				dto.level = diagnostic.LEVEL.WARN;
				return flag(dto);
			};

			/**
			 * Flag a component with an info message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String?} the id of the message container (if one is present/created)
			 */
			this.flagInfo = function (args) {
				var dto = args;
				dto.level = diagnostic.LEVEL.INFO;
				return flag(dto);
			};
		}

		/**
		 * This module knows how to provide feedback to the user about error states and invalid input.
		 * Note: I have removed the dependency on handlebars to increase this chance this module can continue to
		 * operate in error conditions for example the network cable being unplugged.
		 * @module
		 * @requires wc/ui/diagnostic
		 * @requires wc/i18n/i18n
		 */
		var instance = new Feedback();
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/feedback~flagDto The properties used to describe a custom error message.
		 * @property {String|String[]} message The message to display.
		 * @property {Element} element The element which is to be flagged with the error message.
		 *
		 * @typedef {Object} module:wc/ui/feedback~config Optional run-time configuration for this module.
		 * @property {String} [icon=fa-times-circle] The font-awesome classname for the icon to display in the error box.
		 */
	});
