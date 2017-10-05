define(["wc/array/toArray",
	"wc/dom/diagnostic",
	"wc/dom/classList",
	"wc/dom/messageBox",
	"wc/dom/tag",
	"wc/dom/wrappedInput",
	"wc/ui/icon",
	"wc/ui/getFirstLabelForElement",
	"wc/config"],
	function(toArray, diagnostic, classList, messageBox, tag, wrappedInput, icon, getFirstLabelForElement, wcconfig) {
		"use strict";

		function Diagnostic() {
			var writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA],
				BEFORE_END = "beforeend",
				VALIDATION_ERRORS,
				ERROR_LINK;

			/**
			 * Remove a link to a component which was in an error state when the page was loaded (using
			 * WValidationErrors) but which was subsequently corrected.
			 * @function
			 * @private
			 * @param {Element} element The HTML element which was in an error state
			 */
			function removeWValidationErrorLink(element) {
				var validationErrors,
					errorLinkWidget,
					errorLink,
					errorLinkParent,
					target;

				if ((validationErrors = messageBox.getErrorBoxes(document.body, true))) {
					VALIDATION_ERRORS = messageBox.getErrorBoxWidget().clone;
					if (!ERROR_LINK) {
						ERROR_LINK = new Widget("a");
						ERROR_LINK.descendFrom(VALIDATION_ERRORS);
					}

					target = wrappedInput.isWrappedInput(element) ? wrappedInput.getWrapper(element) : element;

					errorLinkWidget = ERROR_LINK.extend("", {href: ("#" + target.id)});
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
			 * For the convenience of consuming UI modules.
			 */
			this.LEVEL = diagnostic.LEVEL;
			this.isOneOfMe = function(element, level) {
				return diagnostic.isOneOfMe(element, level);
			};

			function check(diag, lenient) {
				if (!(diag && diagnostic.isOneOfMe(diag))) {
					if (lenient) {
						return false;
					}
					throw new TypeError("Argument must be a diagnostic box");
				}
				return true;
			}

			function changeIcon(diag, fromLevel, toLevel) {
				var oldClass = getIconName(fromLevel),
					newClass = getIconName(toLevel);
				icon.change(diag, oldClass, newClass);
			}

			/**
			 * Mark/unmark a component as invalid.
			 * @function
			 * @private
			 * @param {Element} target the component to mark
			 * @param {boolean} [isValid] if `true` set the component to be valid
			 */
			function toggleValidity(target, isValid) {
				var INVALID_ATTRIB = "aria-invalid",
					DESCRIBED_ATTRIB = "aria-describedby",
					element,
					diag;
				if (!target && target.tagName) {
					return;
				}
				element =  wrappedInput.getInput(target) || target;
				if (element) {
					if (isValid) {
						element.removeAttribute(INVALID_ATTRIB);
						element.removeAttribute(DESCRIBED_ATTRIB);
						return;
					} else if ((diag = diagnostic.getBox(target, diagnostic.LEVEL.ERROR))) {
						element.setAttribute(INVALID_ATTRIB, "true");
						element.setAttribute(DESCRIBED_ATTRIB, diag.id);
					}
				}
			}

			/**
			 * Get the font awesome icon name for a diagnostic box of a given level.
			 * @param {type} level
			 * @returns {String}
			 */
			function getIconName(level) {
				var defaultIcon = "fa-times-circle",
					config = wcconfig.get("wc/ui/diagnostic");

				if (config && config.errorIcon) {
					defaultIcon = config.errorIcon;
				}
				if (!level || level === diagnostic.LEVEL.ERROR) {
					return defaultIcon;
				}
				switch (level) {
					case diagnostic.LEVEL.WARN:
						if (config && config.warnIcon) {
							return config.warnIcon;
						}
						return "fa-exclamation-triangle";
					case diagnostic.LEVEL.INFO:
						if (config && config.infoIcon) {
							return config.infoIcon;
						}
						return "fa-info-circle";
					case diagnostic.LEVEL.SUCCESS:
						if (config && config.successIcon) {
							return config.successIcon;
						}
						return "fa-check-circle";
					default:
						return defaultIcon;
				}
			}

			function addHelper(diag, message) {
				var i,
					current;
				if (!(diag && diagnostic.getWidget().isOneOfMe(diag))) {
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
				diag.insertAdjacentHTML(BEFORE_END, getMessageHTML(message));
			}

			function getMessageHTML(message) {
				var tagName,
					attrib = "class='",
					className,
					widget;
				if (message && message.constructor !== String) {
					if (message.toString) {
						message = message.toString();
					} else {
						throw new TypeError("Message must be a string");
					}
				}
				widget = diagnostic.getMessage();
				tagName = widget.tagName;
				className = widget.className;
				if (Array.isArray(className)) {
					className = className.join(" ");
				}
				attrib += className + "'";
				return tag.toTag(tagName, false, attrib) + message + tag.toTag(tagName, true);
			}

			/**
			 * Generate the HTML to create a diagnostic box.
			 * @function
			 * @private
			 * @param {Object} args
			 * @param {Element} [args.el] The element which is the diagnostic target if not set then args.id must be set.
			 * @param {String} [args.id] The base id for the diagnostic box. If not set then args.el must be an element with an id.
			 * @param {int} [args.level=1] the diagnostic level, defaults to ERROR
			 * @param {String|String[]|NodeList} [args.messages] If `falsey` then the diagnostic box will be empty. If a String the diagnostic will
			 *   contain one message containing this String. If a NodeList then the diagnostic messages will be the innerHTML of each element node
			 *   in the NodeList and the textContent of each text node in the NodeList. If something else the messages are treated as a single
			 *   "thing" and the diagnostic box will attempt to call toString() on it.
			 * @returns {Object} property html: The HTML which creates a complete diagnostic box, property id: the id of the added box
			 */
			function getHTML(args) {
				var el = args.el,
					level = args.level || diagnostic.LEVEL.ERROR,
					messages = args.messages,
					boxWidget,
					id,
					tagName,
					classAttrib = "class='",
					className,
					idAttrib = "id='",
					roleAttrib = "role='alert'",
					html,
					levelIcon;

				id = args.id || (el ? el.id : null);
				if (!id) {
					throw new TypeError("Cannot get error box without an id.");
				}
				id += diagnostic.getIdExtension(level);

				boxWidget = diagnostic.getByType(level);
				tagName = boxWidget.tagName;
				idAttrib += id + "'";
				className = boxWidget.className;
				if (Array.isArray(className)) {
					className = className.join(" ");
				}
				classAttrib += className + "'";
				html = tag.toTag(tagName, false, [idAttrib, classAttrib, roleAttrib].join(" "));
				if ((levelIcon = getIconName(level))) {
					html += "<i aria-hidden='true' class='fa " + levelIcon + "'></i>";
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
							html += getMessageHTML(next);
						});
					} else {
						html += getMessageHTML(messages);
					}
				}
				html += tag.toTag(tagName, true);
				return {html: html, id: id};
			}

			this.change = function(diag, toLevel, target) {
				var oldLevel,
					oldClass,
					newClass,
					realTarget,
					oldIdExtension,
					newIdExtension,
					testId;
				if (!toLevel || toLevel < 1) {
					console.log("twit");
					return;
				}
				check(diag);
				oldLevel = diagnostic.getLevel(diag);
				if (oldLevel === toLevel) {
					return; // nothing to do
				}
				newClass = diagnostic.getBoxClass(toLevel);
				oldClass = diagnostic.getBoxClass(oldLevel);
				if (oldClass === newClass) {
					// we probably tried to get to a non-existent level.
					return;
				}
				newIdExtension = diagnostic.getIdExtension(toLevel);
				oldIdExtension = diagnostic.getIdExtension(oldLevel);
				testId = diag.id.replace(oldIdExtension, newIdExtension);
				// if we already have a diagnostic box at the requested level we cannot create a new one
				if (document.getElementById(testId)) {
					console.log("cannot create diagnostic box with duplicate id");
					this.remove(diag, target);
					return;
				}
				classList.add(diag, newClass);
				classList.remove(diag, oldClass);
				// now change the icon
				changeIcon(diag, oldLevel, toLevel);
				if (oldLevel === diagnostic.LEVEL.ERROR) {
					removeWValidationErrorLink(target);
				}
				if (oldLevel === diagnostic.LEVEL.ERROR || toLevel === diagnostic.LEVEL.ERROR) {
					if ((realTarget = target || diagnostic.getTarget(diag))) {
						toggleValidity(realTarget, toLevel !== diagnostic.LEVEL.ERROR);
					}
				}
			};

			this.addMessages = function(diag, messages) {
				check(diag);
				if (Array.isArray(messages)) {
					messages.forEach(function(next) {
						addHelper(diag, next);
					});
				} else {
					addHelper(diag, messages);
				}
			};

			this.set = function(diag, messages) {
				var parent;
				check(diag);
				if (!messages && (parent = diag.parentNode)) {
					parent.removeChild(diag);
					return;
				}
				diagnostic.clear(diag);
				this.addMessages(diag, messages);
			};

			/**
			 * Get the HTML which creates a diagnostic box.
			 * @function
			 * @private
			 * @param {String} targetId the id of the component to which the message box is added
			 * @param {String|String[]} messages the message(s) to add
			 * @param {int} [level=1] the diagnostic level
			 * @returns {String} the error box HTML
			 */
			function getBoxHTML(targetId, messages, level) {
				var msgArray;
				if (!(targetId && messages)) {
					return null;
				}
				msgArray = Array.isArray(messages) ? messages : [messages];
				return getHTML({
					id: targetId,
					messages: msgArray,
					level: level
				});
			}

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

				if (wrappedInput.isWrappedInput(target)) {
					target = wrappedInput.getWrapper(target);
				}

				if (level === -1) {
					return diagnostic.getWidget().findDescendant(element);
				}

				if ((id = target.id)) {
					// shortcut as this is most used
					id += diagnostic.getIdExtension(level);
					return document.getElementById(id);
				}
				return null;
			};

			this.add = function(args) {
				var AFTER_END = "afterend",
					writeWhere = args.position,
					target = args.target,
					messages = args.messages,
					level = args.level || diagnostic.LEVEL.ERROR,
					flagTarget,
					html,
					boxId;
				if (!(messages && target && target.nodeType === Node.ELEMENT_NODE)) {
					// no messages or target for the messages
					return false;
				}

				if (wrappedInput.isWrappedInput(target)) {
					target = wrappedInput.getWrapper(target);
				}

				removeWValidationErrorLink(target);

				if (target.tagName === tag.INPUT && (target.type === "radio" || target.type === "checkbox")) {
					flagTarget = getFirstLabelForElement(target);
					if (flagTarget) {
						writeWhere = BEFORE_END;
					} else {
						writeWhere = AFTER_END;
					}
				} else if (wrappedInput.isOneOfMe(target)) {
					writeWhere = BEFORE_END;
				}
				if (!writeWhere) {
					writeWhere = ~writeOutsideThese.indexOf(target.tagName) ? AFTER_END : BEFORE_END;
				}
				flagTarget = flagTarget || target;
				if ((html = getBoxHTML(target.id, messages, level))) {
					boxId = html.id;
					html = html.html;
					flagTarget.insertAdjacentHTML(writeWhere, html);
					if (level === diagnostic.LEVEL.ERROR) {
						toggleValidity(target);
					}
					return boxId;
				}
				return null;
			};

			/**
			 * Remove an existing diagnostic box.
			 * @function
			 * @public
			 * @param {Element} [diag] the box to remove if not set then target must be set
			 * @param {Element} [target] the diagnostic's target if diag is not set or if we already have it. If not set and `diag` is set we can
			 *   calculate the target element.
			 */
			this.remove = function(diag, target) {
				var realTarget, realDiag;

				if (diag && check(diag, true)) {
					realTarget = target || diagnostic.getTarget(diag);
					realDiag = diag;
				} else if (target && !diag) {
					realDiag = diagnostic.getBox(target, -1);
					realTarget = target;
				}

				if (realDiag) {
					this.set(realDiag, null); // removes the diagnostic box
					if (realTarget) {
						removeWValidationErrorLink(realTarget);
						toggleValidity(realTarget, true);
					}
				}
			};
		}

		return new Diagnostic();
	});
