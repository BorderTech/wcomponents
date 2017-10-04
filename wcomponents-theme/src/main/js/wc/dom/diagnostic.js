define(["wc/dom/Widget", "wc/dom/tag"], function(Widget, tag) {
	"use strict";

	function Diagnostic() {
		var SPAN = tag.SPAN,
			CLASS = {
				DIAGNOSTIC: "wc-diagnostic",
				TYPE_SUFFIX: "-type-",
				MESSAGE: "wc-message"
			},
			DIAGNOSTIC = new Widget(SPAN, CLASS.DIAGNOSTIC),
			ERROR_DIAGNOSTIC,
			WARNING_DIAGNOSTIC,
			INFO_DIAGNOSTIC,
			SUCCESS_DIAGNOSTIC,
			MESSAGE;

		/**
		 * Gets the string extension applied to the id of an element when creating its diagnostic box. This should not be widely used but must be
		 * public for use in {@link module:wc/ui/diagnostic}.
		 * @function
		 * @public
		 * @param {int} [level=1] the diagnostic box level
		 * @returns {String} an extension appropriate to the level
		 */
		this.getIdExtension = function(level) {
			var baseExtension = "_err";
			if (!level || level === instance.LEVEL.ERROR) {
				return baseExtension;
			}
			switch (level) {
				case instance.LEVEL.WARN:
					return "_wrn";
				case instance.LEVEL.INFO:
					return "_nfo";
				case instance.LEVEL.SUCCESS:
					return "_scc";
				default:
					return baseExtension;
			}
		};

		/**
		 * @constant {Object} describes the types of diagnostic widget available
		 * @public
		 *
		 */
		this.LEVEL = {
			"ERROR": 1,
			"WARN": 2,
			"INFO": 4,
			"SUCCESS": 8
		};

		/**
		 * Get the HTML class attribute which defines a diagnostic box.
		 * @function
		 * @public
		 * @param {type} [level] the severity level, one of {@link module:wc/dom/diagnostic.LEVEL} if not set then get the basic diagnostic box class
		 * @returns {String} the value of the HTML class attribute for the required diagnostic box.
		 */
		this.getBoxClass = function (level) {
			var baseclass = CLASS.DIAGNOSTIC,
				levelclass;
			if (!level) {
				return baseclass;
			}
			levelclass = baseclass + CLASS.TYPE_SUFFIX;
			switch (level) {
				case this.LEVEL.ERROR:
					return levelclass + "error";
				case this.LEVEL.WARN:
					return levelclass + "warn";
				case this.LEVEL.INFO:
					return levelclass + "info";
				case this.LEVEL.SUCCESS:
					return levelclass + "success";
				default:
					return null;
			}
		};

		/**
		 * Get the HTML class which is applied to messages in an inline diagnostic box.
		 * @function
		 * @public
		 * @returns {String} the value of the HTML class attribute for the diagnostic message.
		 */
		this.getMessageClass = function() {
			return CLASS.MESSAGE;
		};

		/**
		 * Gets the widget for a generic inline diagnostic box.
		 * @function
		 * @public
		 * @returns {diagnosticL#1.Widget}
		 */
		this.getWidget = function() {
			return DIAGNOSTIC;
		};

		/**
		 * Gets the widget for an inline diagnostic's message(s).
		 * @function
		 * @public
		 * @returns {diagnosticL#1.Widget}
		 */
		this.getMessage = function() {
			if (!MESSAGE) {
				MESSAGE = new Widget(SPAN, CLASS.MESSAGE);
				MESSAGE.descendFrom(DIAGNOSTIC, true);
			}
			return MESSAGE;
		};

		/**
		 * Gets the widget for an inline diagnostic box of a particular severity level.
		 * @function
		 * @public
		 * @param {int} [level] the severity level, one of {@link module:wc/dom/diagnostic.LEVEL} if not set then test for any diagnostic level
		 * @returns {diagnosticL#1.Widget}
		 */
		this.getByType = function(level) {
			if (!level) {
				return DIAGNOSTIC;
			}
			switch (level) {
				case this.LEVEL.ERROR:
					return (ERROR_DIAGNOSTIC = ERROR_DIAGNOSTIC || DIAGNOSTIC.extend(this.getBoxClass(this.LEVEL.ERROR)));
				case this.LEVEL.WARN:
					return (WARNING_DIAGNOSTIC = WARNING_DIAGNOSTIC || DIAGNOSTIC.extend(this.getBoxClass(this.LEVEL.WARN)));
				case this.LEVEL.INFO:
					return (INFO_DIAGNOSTIC = INFO_DIAGNOSTIC || DIAGNOSTIC.extend(this.getBoxClass(this.LEVEL.INFO)));
				case this.LEVEL.SUCCESS:
					return (SUCCESS_DIAGNOSTIC = SUCCESS_DIAGNOSTIC || DIAGNOSTIC.extend(this.getBoxClass(this.LEVEL.SUCCESS)));
				default:
					return null;
			}
		};

		/**
		 * Indicates if an element is an inline diagnostic message box.
		 * @function
		 * @public
		 * @param {type} element the element to test
		 * @param {type} [level] the severity level, one of {@link module:wc/dom/diagnostic.LEVEL} if not set then test for any diagnostic level
		 * @returns {Boolean}
		 */
		this.isOneOfMe = function(element, level) {
			var widget;
			if (!element) {
				return false;
			}
			if (!level) {
				return DIAGNOSTIC.isOneOfMe(element);
			}
			if ((widget = this.getByType(level))) {
				return widget.isOneOfMe(element);
			}
			return false;
		};

		/**
		 * Indicates if an element is a message within an inline diagnostic message box.
		 * @function
		 * @public
		 * @param {type} element the element to test
		 * @param {type} [level] the severity level, one of {@link module:wc/dom/diagnostic.LEVEL} if not set then test for any diagnostic level
		 * @returns {Boolean}
		 */
		this.isMessage = function(element, level) {
			var widget, message;
			if (!element) {
				return false;
			}
			// firstly, do we even have a message?
			message = this.getMessage().isOneOfMe(element);
			// if we don't have a message _or_ we don't care what type just return what we have
			if (!(message && level)) {
				return message;
			}
			widget = this.getByType(level);
			if (widget === DIAGNOSTIC) {
				// we have already checked for an un-typed diagnostic message, so just return it.
				return message;
			}
			// if we get here we have a diagnostic message _and_ we want a message of a particular type
			// so we need to check the message's diagnostic ancestor.
			return !!widget.getAncestor(element);
		};

		/**
		 * Find all diagnostics belonging to an element.
		 * @function
		 * @public
		 * @param {Element|String} element the element being diagnosed (or its id)
		 * @param {int} [level=1] the diagnostic level, if not set get ERROR diagnostic box. Set to -1 to get the first of any type.
		 * @returns {Element?} the diagnostic box of the required level (if any).
		 */
		this.getBox = function (element, level) {
			var target,
				id;
			if (!element) {
				throw new TypeError("element must not be falsey");
			}

			target = (element.constructor === String) ? document.getElementById(element) : element;

			if (target.nodeType !== Node.ELEMENT_NODE) {
				throw new TypeError("element does not represent an HTML Element");
			}

			if (level === -1) {
				return DIAGNOSTIC.findDescendant(element);
			}

			if ((id = target.id)) {
				// shortcut as this is most used
				id += this.getIdExtension(level);
				return document.getElementById(id);
			}
			return null;
		};

		/**
		 * Gets the messages already inside a given diagnostic box.
		 * @function
		 * @public
		 * @param {Element} diag the diagnostic box
		 * @returns {NodeList?} messages inside the diagnostic box, if any
		 */
		this.getMessages = function(diag) {
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				return null;
			}
			return this.getMessage().findDescendants(diag);
		};

		/**
		 * Get the diagnostic level (e.g. LEVEL.ERROR) for a given diagnostic box.
		 * @function
		 * @public
		 * @param {Element} diag the box to test
		 * @throws {TypeError} if `diag` is not a diagnostic box
		 * @returns {Number|diagnosticL#1.Diagnostic.LEVEL} the diagnostic level from module:wc/dom/diagnostic.LEVEL or -1 if not found
		 */
		this.getLevel = function(diag) {
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				throw new TypeError("Argument must be a diagnostic box");
			}
			for (var lvl in this.LEVEL) {
				if (this.LEVEL.hasOwnProperty(lvl) && this.isOneOfMe(diag, this.LEVEL[lvl])) {
					return this.LEVEL[lvl];
				}
			}
			return -1;
		};

		/**
		 * Remove all messages from a diagnostic box.
		 * @function
		 * @public
		 * @param {Element} diag
		 * @throws {TypeError} if `diag` is not a diagnostic box
		 */
		this.clear = function(diag) {
			var messages;
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				throw new TypeError("Argument must be a diagnostic box");
			}
			if ((messages = this.getMessages(diag))) {
				Array.prototype.forEach.call(messages, function(next) {
					diag.removeChild(next);
				});
			}
		};

		/**
		 * Get the target element of a diagnostic message box.
		 * @function
		 * @public
		 * @param {Element} diag the diagnostic box
		 * @returns {Element?} the target element of the diagnostic box
		 */
		this.getTarget = function(diag) {
			var targetId,
				level,
				suffix,
				id;
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				return null;
			}
			id = diag.id;
			if (!id) {
				// should never be here!
				throw new ReferenceError("Should not have a diagnostic box without an ID");
			}
			level = this.getLevel(diag);
			suffix = this.getIdExtension(level);
			if (!suffix) {
				return null;
			}

			if ((targetId = id.replace(new RegExp(suffix.concat("$")), ""))) {
				return document.getElementById(targetId);
			}
			return null;
		};
	}

	/**
	 * Provides widgets for describing inline diagnostic messages.
	 * @module
	 * @requires wc/dom/Widget
	 */
	var instance = new Diagnostic();
	return instance;
});
