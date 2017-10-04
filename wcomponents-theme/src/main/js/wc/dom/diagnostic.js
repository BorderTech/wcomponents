define(["wc/dom/Widget", "wc/dom/tag", "wc/array/toArray", "wc/dom/classList", "wc/config"], function(Widget, tag, toArray, classList, wcconfig) {
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
		 * Get the font awesome icon name for a diagnostic box of a given level.
		 * @param {type} level
		 * @returns {.wcconfig@call;get.successIcon|.wcconfig@call;get.infoIcon|String|.wcconfig@call;get.errorIcon|.wcconfig@call;get.warnIcon}
		 */
		this.getIconName = function(level) {
			var defaultIcon = "fa-times-circle",
				config = wcconfig.get("wc/dom/diagnostic");

			if (config && config.errorIcon) {
				defaultIcon = config.errorIcon;
			}
			if (!level || level === this.LEVEL.ERROR) {
				return defaultIcon;
			}
			switch (level) {
				case this.LEVEL.WARN:
					if (config && config.warnIcon) {
						return config.warnIcon;
					}
					return "fa-exclamation-triangle";
				case this.LEVEL.INFO:
					if (config && config.infoIcon) {
						return config.infoIcon;
					}
					return "fa-info-circle";
				case this.LEVEL.SUCCESS:
					if (config && config.successIcon) {
						return config.successIcon;
					}
					return "fa-check-circle";
				default:
					return defaultIcon;
			}
		};

		function getIdExtension(level) {
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
		}

		this.getMessageHTML = function(message) {
			var tagName,
				attrib = "class='",
				widget;
			if (message && message.constructor !== String) {
				if (message.toString) {
					message = message.toString();
				} else {
					throw new TypeError("Message must be a string");
				}
			}
			widget = this.getMessage();
			tagName = widget.tagName;
			attrib += widget.className + "'";
			return tag.toTag(tagName, false, [attrib]) + message + tag.toTag(tagName, true);
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
		 * @returns {String} the value of the HTML class attribute for the diagnostic message.
		 */
		this.getMessageClass = function() {
			return CLASS.MESSAGE;
		};

		/**
		 * Generate the HTML to create a diagnostic box.
		 * @param {Object} args
		 * @param {Element} [args.el] The element which is the diagnostic target if not set then args.id must be set.
		 * @param {String} [args.id] The base id for the diagnostic box. If not set then args.el must be an element with an id.
		 * @param {int} [args.level=1] the diagnostic level, defaults to ERROR
		 * @param {String|String[]|NodeList} [args.messages] If `falsey` then the diagnostic box will be empty. If a String the diagnostic will
		 *   contain one message containing this String. If a NodeList then the diagnostic messages will be the innerHTML of each element node in the
		 *   NodeList and the textContent of each text node in the NodeList. If something else the messages are treated as a single "thing" and the
		 *   diagnostic box will attempt to call toString() on it.
		 * @returns {Object} property html: The HTML which creates a complete diagnostic box, property id: the id of the added box
		 */
		this.getHTML = function(args) {
			var el = args.el,
				level = args.level || this.LEVEL.ERROR,
				messages = args.messages,
				boxWidget,
				id,
				tagName,
				classAttrib = "class='",
				idAttrib = "id='",
				roleAttrib = "role='alert'",
				html,
				icon;

			id = args.id || (el ? el.id : null);
			if (!id) {
				throw new TypeError("Cannot get error box without an id.");
			}
			id += + getIdExtension(level);

			boxWidget = this.getByType(level);
			tagName = boxWidget.tagName;
			idAttrib += id + "'";
			classAttrib += boxWidget.className + "'";
			html = tag.toTag(tagName, false, [idAttrib, classAttrib, roleAttrib]);
			if ((icon = this.getIconName(level))) {
				html += "<i aria-hidden='true' class='fa-fw " + icon + "'></i>";
			}
			if (messages) {
				if (messages.constructor === NodeList) {
					messages = (toArray(messages)).map(function(next) {
						if (next.nodeType === Node.ELEMENT_NODE) {
							return next.innerHTML;
						}
						if (next.textContent) {
							return next.textContent;
						}
						return null;
					});
				}
				if (Array.isArray(messages)) {
					messages.forEach(function(next) {
						html += this.getMessageHTML(next);
					}, this);
				} else {
					html += instance.getMessageHTML(messages);
				}
			}
			html += tag.toTag(tagName, true);
			return {html: html, id: id};
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
		 * @param {Element|String} element the element being diagnosed (or its id)
		 * @param {int} [level=1] the diagnostic level, if not set get ERROR diagnostic box. Set to -1 to get the first of any type.
		 * @returns {Element?} the diagnostic box of the required level (if any).
		 */
		this.getDiagnostic = function (element, level) {
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
				id += getIdExtension(level);
				return document.getElementById(id);
			}
			return null;
		};

		/**
		 * Gets the messages already inside a given diagnostic box
		 * @param {Element} diag the diagnostic box
		 * @returns {NodeList?} messages inside the diagnostic box if any
		 */
		this.getMessages = function(diag) {
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				return null;
			}
			return this.getMessage().findDescendants(diag);
		};

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

		this.add = function(diag, message) {
			var i,
				current;
			if (!(diag && DIAGNOSTIC.isOneOfMe(diag))) {
				throw new TypeError("Argument must be a diagnostic box");
			}
			if ((current = this.getMessages(diag))) {
				for (i = 0; i < current.length; ++i) {
					if (message.toLocaleLowerCase() === current[i].innerHTML.toLocaleLowerCase()) {
						// already have this message
						return;
					}
				}
			}
			diag.insertAdjacentHTML("beforeEnd", this.getMessageHTML(message));
		};

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
			suffix = getIdExtension(level);
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
