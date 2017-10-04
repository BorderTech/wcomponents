define(["wc/dom/diagnostic",
	"wc/dom/classList",
	"wc/ui/icon",
	"wc/dom/tag",
	"wc/dom/wrappedInput",
	"wc/ui/getFirstLabelForElement"],
	function(diagnostic, classList, icon, tag, wrappedInput, getFirstLabelForElement) {
		"use strict";

		function Diagnostic() {
			var writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA];

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
				var oldClass = diagnostic.getIconName(fromLevel),
					newClass = diagnostic.getIconName(toLevel);
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
					} else if ((diag = diagnostic.getDiagnostic(target, diagnostic.LEVEL.ERROR))) {
						element.setAttribute(INVALID_ATTRIB, "true");
						element.setAttribute(DESCRIBED_ATTRIB, diag.id);
					}
				}
			}

			this.change = function(diag, toLevel, target) {
				var oldLevel,
					oldClass,
					newClass,
					realTarget;
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
				classList.add(diag, newClass);
				classList.remove(diag, oldClass);
				// now change the icon
				changeIcon(diag, oldLevel, toLevel);
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
						diagnostic.add(diag, next);
					});
				} else {
					diagnostic.add(diag, messages);
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
				return diagnostic.getHTML({
					id: targetId,
					messages: msgArray,
					level: level
				});
			}

			this.add = function(args) {
				var BEFORE_END = "beforeEnd",
					AFTER_END = "afterEnd",
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

			this.remove = function(diag, target) {
				var realTarget, realDiag;

				if (diag && check(diag, true)) {
					realTarget = target || diagnostic.getTarget(diag);
					realDiag = diag;
				} else if (target && !diag) {
					realDiag = diagnostic.getDiagnostic(target, -1);
					realTarget = target;
				}

				if (realDiag) {
					this.set(realDiag, null); // removes the diagnostic box
					if (realTarget) {
						toggleValidity(realTarget, true);
					}
				}
			};
		}

		return new Diagnostic();
	});
