define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/array/toArray",
	"wc/dom/tag",
	"wc/ui/getFirstLabelForElement"],
	function(initialise, Widget, toArray, tag, getFirstLabelForElement) {
		"use strict";
		var instance = new ErrorWriter();
		/**
		 * This module knows how to provide feedback to the user about error states and invalid input.
		 * Note: I have removed the dependency on handlebars to increase this chance this module can continue to
		 * operate in error conditions for example the network cable being unplugged.
		 * @constructor
		 */
		function ErrorWriter() {
			var ERROR_BOX = new Widget("section", "wc-validationerrors"),
				ERROR = new Widget("", "wc-error"),
				LINK = new Widget("a"),
				writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.TABLE],
				CONTAINER_TEMPLATE = function(args) {
					var result =  "<span class=\"wc-fieldindicator wc-fieldindicator-type-error\" id=\"" + args.id + "\">";
					if (args.errors) {
						result += "<span class=\"wc-error\">" + args.errors + "</span>";
					}
					result += "</span>";
					return result;
				},
				ERROR_TEMPLATE = function(args) {
					return "<span class=\"wc-error\">" + args.error + "</span>";
				},
				INPUT_WRAPPER,
				INPUT;

			ERROR.descendFrom(ERROR_BOX);
			LINK.descendFrom(ERROR);

			function markInvalid(target) {
				var tagName,
					invalidElement;
				if (!target && (tagName = target.tagName)) {
					return;
				}
				INPUT_WRAPPER = INPUT_WRAPPER || new Widget("", "wc-input-wrapper");
				if (~writeOutsideThese.indexOf(tagName)) {
					invalidElement = target;
				}
				else if (INPUT_WRAPPER.isOneOfMe(target)) {
					INPUT = INPUT || new Widget("input");
					invalidElement = INPUT.findDescendant(target);
				}
				else {
					invalidElement = target;
				}
				if (invalidElement) {
					invalidElement.setAttribute("aria-invalid", "true");
					invalidElement.setAttribute("aria-describedby", target.id + "_err");
				}
			}

			/**
			 * Get all validation error boxes from a container element.
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

			function getAllErrors(container) {
				var errorBoxes = getErrorBoxes(container),
					candidates = [];

				errorBoxes.forEach(function(next) {
					candidates = candidates.concat(toArray(LINK.findDescendants(next)));
				});
				return candidates;
			}

			this.flagError = function(args) {
				var props,
					target,
					tagName,
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
					tagName = target.tagName;
					if (tagName === tag.INPUT && (target.type === "radio" || target.type === "checkbox")) {
						target = getFirstLabelForElement(target) || target;
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
		}

		return instance;
	});
