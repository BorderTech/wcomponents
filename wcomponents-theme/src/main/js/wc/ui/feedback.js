define(["wc/array/toArray",
	"wc/dom/diagnostic",
	"wc/dom/classList",
	"wc/dom/tag",
	"wc/dom/wrappedInput",
	"wc/ui/icon",
	"wc/dom/getLabelsForElement",
	"wc/config"],
	function(toArray, diagnostic, classList, tag, wrappedInput, icon, getLabelsForElement, wcconfig) {
		"use strict";

		function Feedback() {
			var writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA],
				BEFORE_END = "beforeend";

			/**
			 * For the convenience of consuming UI modules.
			 */
			this.LEVEL = diagnostic.LEVEL;
			this.isOneOfMe = function(element, level) {
				return diagnostic.isOneOfMe(element, level);
			};

			function checkandGetElement(element) {
				var target;
				if (!element) {
					throw new TypeError("element must not be falsey");
				}
				target = (element.constructor === String) ? document.getElementById(element) : element;

				if (!(target && target.tagName)) {
					throw new TypeError("element does not represent an HTML Element");
				}
				return target;
			}

			/**
			 * Type check for diagnostic boxes.
			 * @param {Element} diag the element to test
			 * @param {type} lenient if `true` do not error on a failed test, instread return false
			 * @returns {Boolean} `true` if `diag` is a diagnostic box, otherwise `false` if `lenient` is `true`.
			 * @throws {TypeError} if `diag` is not a diagnostic box and `lenient` is not `true`.
			 */
			function check(diag, lenient) {
				if (!(diag && diagnostic.isOneOfMe(diag))) {
					if (lenient) {
						return false;
					}
					throw new TypeError("Argument must be a feedback box");
				}
				return true;
			}

			function changeIcon(diag, fromLevel, toLevel) {
				var oldClass = getIconName(fromLevel),
					newClass = getIconName(toLevel);
				icon.change(diag, newClass, oldClass);
			}

			/**
			 * Mark/unmark a component as invalid.
			 * @function
			 * @private
			 * @param {Element} target the component to mark
			 * @param {boolean} [clear] if `true` set the component to be valid **and** remove its `aria-describedby` attribute
			 */
			function toggleValidity(target, clear) {
				var INVALID_ATTRIB = "aria-invalid",
					DESCRIBED_ATTRIB = "aria-describedby",
					element,
					diag;
				if (!(target && target.tagName)) {
					return;
				}
				element =  wrappedInput.getInput(target) || target;
				if (element) {
					if (clear) {
						element.removeAttribute(INVALID_ATTRIB);
						element.removeAttribute(DESCRIBED_ATTRIB);
						return;
					}
					if ((diag = instance.getBox(target, diagnostic.LEVEL.ERROR))) {
						element.setAttribute(INVALID_ATTRIB, "true");
						element.setAttribute(DESCRIBED_ATTRIB, diag.id);
					} else if ((diag = instance.getBox(target, -1))) {
						element.removeAttribute(INVALID_ATTRIB);
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
					config = wcconfig.get("wc/ui/feedback");

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

			function getMessageHTML(message) {
				var tagName,
					attrib = "class='",
					className,
					widget;
				if (!message) {
					throw new TypeError("Message must not be falsey");
				}
				if (message.constructor !== String) {
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

			function addHelper(box, message) {
				var i,
					current;
				if (!(message && message.constructor === String)) {
					throw new TypeError("Message must be a string");
				}
				if ((current = instance.getMessages(box))) {
					for (i = 0; i < current.length; ++i) {
						if (message.toLocaleLowerCase() === current[i].innerHTML.toLocaleLowerCase()) {
							// already have this message
							return;
						}
					}
				}
				box.insertAdjacentHTML(BEFORE_END, getMessageHTML(message));
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
					targetId,
					boxId,
					tagName,
					classAttrib = "class='",
					className,
					idAttrib = "id='",
					roleAttrib = "role='alert'",
					forAttrib = "data-wc-dfor='",
					html,
					levelIcon;

				targetId = args.id || (el ? el.id : null);
				if (!targetId) {
					throw new TypeError("Cannot get error box without an id.");
				}
				boxWidget = diagnostic.getByType(level);
				tagName = boxWidget.tagName;
				boxId = targetId + diagnostic.getIdExtension(level);
				idAttrib += boxId + "'";
				forAttrib += targetId + "'";
				className = boxWidget.className;
				if (Array.isArray(className)) {
					className = className.join(" ");
				}
				classAttrib += className + "'";
				html = tag.toTag(tagName, false, [idAttrib, classAttrib, roleAttrib, forAttrib].join(" "));
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
				return {html: html, id: boxId};
			}

			/**
			 * Change the doagnostic level of an existing diagnostic box.
			 * @function
			 * @public
			 * @param {Element} box the diagnostic box to change
			 * @param {module:wc/dom/diagnostic.LEVEL} toLevel the level to change to
			 * @param {Element} [target] the diagnostic box's target element if already known - saves us finding it twice.
			 */
			this.change = function(box, toLevel, target) {
				var oldLevel,
					oldClass,
					newClass,
					realTarget,
					oldIdExtension,
					newIdExtension,
					testId;
				check(box);
				if (!toLevel || toLevel < 1) {
					console.log("twit");
					return;
				}
				oldLevel = diagnostic.getLevel(box);
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
				testId = box.id.replace(oldIdExtension, newIdExtension);
				// if we already have a diagnostic box at the requested level we cannot create a new one
				if (document.getElementById(testId)) {
					console.log("cannot create diagnostic box with duplicate id");
					// this.remove(diag, target);
					this.clear(box);
					return;
				}
				classList.add(box, newClass);
				classList.remove(box, oldClass);
				box.id = testId;
				this.clear(box);
				// now change the icon
				changeIcon(box, oldLevel, toLevel);
				if ((realTarget = target || diagnostic.getTarget(box))) {
					toggleValidity(realTarget);
				}
			};

			/**
			 * Remove all messages from a diagnostic box.
			 * @function
			 * @public
			 * @param {Element} box the diagnostic box to change
			 * @throws {TypeError} if `diag` is not a diagnostic box
			 */
			this.clear = function(box) {
				var messages;
				check(box);
				if ((messages = this.getMessages(box))) {
					Array.prototype.forEach.call(messages, function(next) {
						box.removeChild(next);
					});
				}
			};

			/**
			 * Add messages to an existing diagnostic box.
			 * @function
			 * @public
			 * @param {Element} box the disgnostic box
			 * @param {String|String[]} messages the message(s) to add
			 */
			this.addMessages = function(box, messages) {
				check(box);
				if (Array.isArray(messages)) {
					messages.forEach(function(next) {
						addHelper(box, next);
					});
				} else {
					addHelper(box, messages);
				}
			};

			/**
			 * Gets the messages already inside a given diagnostic box.
			 * @function
			 * @public
			 * @param {Element} box the diagnostic box
			 * @returns {NodeList} messages inside the diagnostic box, if any
			 */
			this.getMessages = function(box) {
				if (!check(box, true)) {
					return null;
				}
				return diagnostic.getMessage().findDescendants(box);
			};

			/**
			 * Set the messages inside an existing message box to a new message or set of messages.
			 * @function
			 * @public
			 * @param {Element} box the diagnostic box
			 * @param {String|String[]} messages
			 */
			this.set = function(box, messages) {
				check(box);
				this.clear(box);
				if (messages) {
					this.addMessages(box, messages);
				}
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
			 * Find a diagnostic box belonging to an element.
			 * @function
			 * @public
			 * @param {Element|String} element the element being diagnosed (or its id)
			 * @param {int} [ofLevel=1] the diagnostic level, if not set get ERROR diagnostic box. Set to -1 to get one of any type.
			 * @returns {Element} the diagnostic box of the required level (if any).
			 */
			this.getBox = function (element, ofLevel) {
				var target,
					id,
					result,
					level = ofLevel || this.LEVEL.ERROR,
					lvl,
					transientWidget;
				target = checkandGetElement(element);
				if (wrappedInput.isWrappedInput(target)) {
					target = wrappedInput.getWrapper(target);
				}
				if (!target.id) {
					return null;
				}
				id = target.id;

				if (level === -1) {
					transientWidget = diagnostic.getWidget().clone().extend("", { "data-wc-dfor": id });
					if ((result = transientWidget.findDescendant(target))) { // fast but insufficient
						return result;
					}
					for (lvl in this.LEVEL) {
						if ((result = this.getBox(element, this.LEVEL[lvl]))) {
							return result;
						}
					}
					return null;
				}
				transientWidget = diagnostic.getByType(level).clone().extend("", { "data-wc-dfor": id });
				return transientWidget.findDescendant(target) || transientWidget.findDescendant(document.body);
			};

			/**
			 * Get the last diagnostic box WITHIN (or withing the wrapper of) a
			 * @param {Element|String} element the element being tested or an id of an element
			 * @returns {Element} the last diagnostic box if any.
			 */
			this.getLast = function(element) {
				var target,
					candidates;

				target = checkandGetElement(element);

				if (!target.id) {
					return null;
				}

				if (wrappedInput.isWrappedInput(target)) {
					target = wrappedInput.getWrapper(target);
				}
				candidates = diagnostic.getWidget().findDescendants(target);
				return (candidates && candidates.length) ? candidates[candidates.length - 1] : null;
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
					// don't throw: just do nothing
					console.warn("trying to add nothing or to nothing");
					return null;
				}

				if (wrappedInput.isWrappedInput(target)) {
					target = wrappedInput.getWrapper(target);
				}

				if (target.tagName === tag.INPUT && (target.type === "radio" || target.type === "checkbox")) {
					flagTarget = getLabelsForElement(target);
					flagTarget = (flagTarget && flagTarget.length) ? flagTarget[0] : null;
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
					toggleValidity(target);
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
			function removeDiagnostic(diag, target) {
				var realTarget, realDiag, parent;
				if (!(diag || target)) {
					console.error("What! I am magic now? What do you want to remove you womble?");
					throw new TypeError("You forgot the args");
				}

				if (diag && check(diag, true)) {
					realTarget = target || diagnostic.getTarget(diag);
					realDiag = diag;
				} else if (target && !diag) {
					realDiag = instance.getBox(target, -1);
					realTarget = target;
				}

				if (realDiag) {
					if ((parent = realDiag.parentNode)) {
						parent.removeChild(realDiag);
					}
					if (realTarget) {
						toggleValidity(realTarget, true);
					}
				}
			}

			/**
			 * Public for testing
			 * @ignore
			 */
			this._removeDiagnostic = removeDiagnostic;

			/**
			 * Remove a feedback message.
			 * @function
			 * @public
			 * @param {Element} element either an error diagnostic or an element with an error diagnostic
			 * @param {Element} [target] an element with a diagnostic **if** element is a diagnostic and we have already found its "owner".
			 * @param {int} [level=1] the diagnostic level to remove if element is not a diagnostic box
			 * @returns {boolean} `true` if a diagnostic box was found and removed.
			 */
			this.remove = function(element, target, level) {
				var errorContainer,
					lvl = level || this.LEVEL.ERROR;
				if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
					return false;
				}
				// read carefully before you try merging these two to be more 'efficient'/
				if (this.isOneOfMe(element)) {
					removeDiagnostic(element, target);
					return true;
				}
				if ((errorContainer = this.getBox(element, lvl))) {
					removeDiagnostic(errorContainer, element);
					return true;
				}
				return false;
			};

			/**
			 * Flag a component with a message.
			 * @function
			 * @private
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String} the id of the message container (if one is present/created)
			 */
			function flag(args) {
				var target,
					messages,
					level,
					errorContainer;
				if (!args) {
					return null;
				}
				target = args.element;
				messages = args.message;
				level = args.level;
				if (!(target && messages && level)) {
					return null;
				}
				if (target.constructor === String) {
					target = document.getElementById(target);
					if (!target) {
						return null;
					}
				}

				// if the target already has an appropriate box then use it
				if ((errorContainer = instance.getBox(target, level))) {
					instance.change(errorContainer, level);
					instance.addMessages(errorContainer, messages);
					return errorContainer.id;
				} // Success and failure are mutually exclusive
				if ((level === diagnostic.LEVEL.ERROR && (errorContainer = instance.getBox(target, diagnostic.LEVEL.SUCCESS))) ||
					(level === diagnostic.LEVEL.SUCCESS && (errorContainer = instance.getBox(target, diagnostic.LEVEL.ERROR)))) {
					instance.change(errorContainer, level);
					instance.addMessages(errorContainer, messages);
					return errorContainer.id;
				}

				return instance.add({
					target: target,
					messages: messages,
					level: level
				});
			}

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._flag = flag;

			/**
			 * Flag a component with an error message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String} the id of the error container (if one is present/created)
			 */
			this.flagError = function(args) {
				var dto = args;
				dto.level = this.LEVEL.ERROR;
				return flag(dto);
			};

			/**
			 * Flag a component with a warning message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String} the id of the message container (if one is present/created)
			 */
			this.flagWarning = function (args) {
				var dto = args;
				dto.level = this.LEVEL.WARN;
				return flag(dto);
			};

			/**
			 * Flag a component with an info message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String} the id of the message container (if one is present/created)
			 */
			this.flagInfo = function (args) {
				var dto = args;
				dto.level = this.LEVEL.INFO;
				return flag(dto);
			};

			/**
			 * Flag a component with a success message.
			 * @function
			 * @public
			 * @param {module:wc/ui/feedback~flagDto} args a config dto
			 * @returns {String} the id of the message container (if one is present/created)
			 */
			this.flagSuccess = function (args) {
				var dto = args;
				dto.level = this.LEVEL.SUCCESS;
				return flag(dto);
			};
		}

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
