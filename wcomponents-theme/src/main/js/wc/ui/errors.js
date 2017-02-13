define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/array/toArray",
	"wc/loader/resource",
	"lib/handlebars/handlebars",
	"wc/dom/tag",
	"wc/ui/getFirstLabelForElement"],
	function(initialise, Widget, toArray, loader, handlebars, tag, getFirstLabelForElement) {
		"use strict";

		function ErrorWriter() {
			var ERROR_BOX = new Widget("section", "wc-validationerrors"),
				ERROR = new Widget("","wc-error"),
				LINK = new Widget("a"),
				writeOutsideThese = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.TABLE],
				CONTAINER_TEMPLATE,
				ERROR_TEMPLATE,
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

			function getContainerTemplate() {
				if (!CONTAINER_TEMPLATE) {
					return loader.load("validationerrors.html", true, true).then(function (template) {
						CONTAINER_TEMPLATE = handlebars.compile(template);
					});
				}
				return Promise.resolve();
			}

			function getErrorTemplate() {
				if (!ERROR_TEMPLATE) {
					return loader.load("validationerror.html", true, true).then(function (template) {
						ERROR_TEMPLATE = handlebars.compile(template);
					});
				}
				return Promise.resolve();
			}


			function writeError(err) {
				var targetId,
					props,
					target,
					tagName,
					errorBoxId,
					errorContainer,
					writeWhere,
					html;

				if (!err && err.innerHTML) {
					return;
				}

				targetId = err.getAttribute("href");
				if (!targetId || targetId.indexOf("#") !== 0) {
					return; // not my error, should never get here
				}
				targetId = targetId.substr(1);

				target = document.getElementById(targetId);
				if (!target) {
					return; // linked to something not in the UI.
				}

				errorBoxId = targetId + "_err";
				// if the target already has an error box then use it:
				if ((errorContainer = document.getElementById(errorBoxId))) {
					getErrorTemplate().then(function() {
						var errorhtml,
							innerprops;
						if (ERROR_TEMPLATE) {
							innerprops = {
								error: err.innerHTML
							};
							errorhtml = ERROR_TEMPLATE(innerprops);

							if (errorhtml) {
								errorContainer.insertAdjacentHTML("beforeend", errorhtml);
							}
						}
					});
					return;
				}

				props = {
					id: errorBoxId,
					errors: err.innerHTML
				};
				if ((html = CONTAINER_TEMPLATE(props))) {
					tagName = target.tagName;
					if (tagName === tag.INPUT && (target.type === "radio" || target.type === "checkbox")) {
						target = getFirstLabelForElement(target) || target;
					}
					writeWhere = ~writeOutsideThese.indexOf(target.tagName) ? "afterend" : "beforeEnd";
					target.insertAdjacentHTML(writeWhere, html);
					markInvalid(target);
				}
			}

			function writeErrors(container) {
				var errors = getAllErrors(container);
				if (errors && errors.length) {
					getContainerTemplate().then(function() {
						if (CONTAINER_TEMPLATE) {
							errors.forEach(writeError);
						}
					});
				}
			}

			initialise.addCallback(function(element) {
				writeErrors(element);
			});
		}

		return new ErrorWriter();
	});
