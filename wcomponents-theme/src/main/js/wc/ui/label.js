define(["wc/dom/classList",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/tag",
	"wc/dom/Widget",
	"wc/dom/getLabelsForElement",
	"wc/ui/ajax/processResponse",
	"wc/dom/role",
	"wc/dom/textContent",
	"wc/dom/wrappedInput",
	"wc/ui/checkBox",
	"wc/ui/feedback"],
	function (classList, initialise, shed, tag, Widget, getLabelsForElement, processResponse, $role, textContent, wrappedInput, checkBox, feedback) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/label~Label
		 * @private
		 */
		function Label() {
			var TAGS = [tag.INPUT, tag.TEXTAREA, tag.SELECT, tag.FIELDSET],
				CLASS_HINT = "wc-label-hint",
				CHECKBOX_WRAPPER = checkBox.getWrapper(),
				MOVE_WIDGETS = [CHECKBOX_WRAPPER, new Widget("", "wc-radiobutton"), new Widget("button", "wc-selecttoggle")],
				HINT,
				// @ricksbrown this is a workaround for a flaw in our whole rendering system - we should fix the underlying flaw
				movedCbLabelReg = {};

			/**
			 * Helper to do label manipulation.
			 * @function
			 * @private
			 * @param {Element} label the label to manipulate
			 * @param {Function} func the function to apply to the label
			 */
			function mandateLabel(label, func) {
				if (label.tagName !== tag.LEGEND) {
					classList[func](label, "wc_req");
				}
			}

			/**
			 * Helper to do label manipulation.
			 * @function
			 * @private
			 * @param {Element} label the label to manipulate
			 * @param {Function} func the function to apply to the label
			 */
			function showHideLabel(label, func) {
				if (label.tagName !== tag.LEGEND) {
					shed[func](label);
				}
			}

			/**
			 * Manipulate a label when a labelled element is made mandatory or optional.
			 * @function
			 * @private
			 * @param {Element} element The element being made optional/mandatory.
			 * @param {String} action The shed action shed.actions.MANDATORY or shed.actions.OPTIONAL.
			 */
			function shedMandatorySubscriber(element, action) {
				var input, func;
				if (!element) {
					return;
				}
				input = wrappedInput.isOneOfMe(element) ? wrappedInput.getInput(element) : element;
				if (input && input.type !== "radio" && (TAGS.indexOf(input.tagName) > -1 || $role.has(input))) {
					func = action === shed.actions.OPTIONAL ? "remove" : "add";
					getLabelsForElement(element).forEach(function (next) {
						mandateLabel(next, func);
					});
				}
			}

			/**
			 * Show/hide label[s] when a labelled element (even readOnly) is shown/hidden.
			 * @function
			 * @private
			 * @param {Element} element The element being made optional/mandatory
			 * @param {String} action The shed action shed.actions.SHOW or shed.actions.HIDE
			 */
			function shedHideSubscriber(element, action) {
				var func;
				if (element) {
					func = action === shed.actions.SHOW ? "show" : "hide";
					// anything, even read-only, can be hidden/shown
					getLabelsForElement(element, true).forEach(function (next) {
						showHideLabel(next, func);
					});
				}
			}

			/**
			 * This is the function which does the heavy lifting of converting a label into and out of its read-only
			 * analogue state when a labelled element is converted. An element may have more than one label (though this
			 * is not a good thing) and this function is a forEach iterator function manipulating a single specific
			 * labelling element. This function is called only if the labelled element (element) has converted between
			 * its active and read-only states.
			 * @function
			 * @private
			 * @param {Element} element The DOM element which is being converted to/from its read-only state via AJAX.
			 * @param {Element} label a label (or read-only analogue) for element
			 * @param {boolean} [isRO] indicates the element is readOnly, already calculated in the caller so pass it thru.
			 */
			function convertLabel(element, label, isRO) {
				var newLabellingElement,
					parent = label.parentNode,
					input;
				if (isRO) {
					newLabellingElement = document.createElement("span");
					newLabellingElement.setAttribute("data-wc-rofor", element.id);
				} else {
					newLabellingElement = document.createElement("label");
					if ((input = wrappedInput.getInput(element))) { // should always be found
						newLabellingElement.setAttribute("for", input.id);
					} else if (TAGS.indexOf(element.tagName) > -1) {
						newLabellingElement.setAttribute("for", element.id);
					}
				}
				newLabellingElement.className = label.className;
				newLabellingElement.innerHTML = label.innerHTML;
				input = input || element;
				if (input && input.type !== "radio") {
					mandateLabel(newLabellingElement, (!isRO && shed.isMandatory(input) ? "add" : "remove"));
				}
				if (shed.isHidden(element, true)) {
					shed.hide(newLabellingElement, true); // nothing depends on the hidden state of a label and we are replicating a load-time state.
				}
				newLabellingElement.id = label.id;
				label.id = "";
				parent.insertBefore(newLabellingElement, label);
				parent.removeChild(label);
				// Add submitOnChange warnings.
				if (!isRO) {
					// this cannot be a module level dependency as it would cause a circular
					// dependency. It is also not really important how long this takes.
					require(["wc/ui/onchangeSubmit"], function (soc) {
						soc.warn(element, newLabellingElement);
					});
				}
			}

			/**
			 * Helper to put checkbox labels in the right place. This is far more complex than it needs to be.
			 * @function
			 * @private
			 * @param {Element} input the labelled WCheckBox
			 * @param {Element|String} label the label or its HTML (single element root)
			 */
			function checkboxLabelPositionHelper(input, label) {
				var labelElement,
					refElement,
					parent;
				if (!(input && label)) {
					throw new TypeError("Input and label must be defined.");
				}
				if (!(input && input.nodeType === Node.ELEMENT_NODE && label)) {
					throw new TypeError("Input must be an element.");
				}

				if (label.constructor === String) {
					labelElement = document.createElement("span");
					labelElement.innerHTML = label.trim();
					labelElement = labelElement.firstElementChild;
				} else {
					labelElement = label;
				}

				if (!(labelElement && labelElement.nodeType === Node.ELEMENT_NODE)) {
					console.error("label arg must be an Element or HTML String representing a single element");
					// do not throw, this function is not that important
					return;
				}

				if (wrappedInput.isReadOnly(input)) {
					parent = input.parentNode;
					refElement = input.nextSibling;
					if (refElement) {
						parent.insertBefore(labelElement, refElement);
					} else {
						parent.appendChild(labelElement);
					}
					return;
				}

				refElement = feedback.getBox(input, -1);
				if (refElement && refElement.parentNode === input) {
					input.insertBefore(labelElement, refElement);
				} else {
					input.appendChild(labelElement);
				}
			}

			function isActiveWCheckBox(el) {
				if (!(el && el.nodeType === Node.ELEMENT_NODE)) {
					return false;
				}
				return CHECKBOX_WRAPPER.isOneOfMe(el) && !wrappedInput.isReadOnly(el);
			}

			/**
			 * Move an individual element's label if required.
			 * @function
			 * @private
			 * @param {Element} el a WRadioButton, WCheckBox or WSelectToggle-button
			 */
			function moveLabel(el) {
				var labels = getLabelsForElement(el, true),
					label, parent, refElement;
				if (!(labels && labels.length)) {
					return;
				}

				label = labels[0];
				// We **almost** always have to move interactive checkbox labels because otherwise error messages make the label go nuts.
				if (isActiveWCheckBox(el)) {
					// We want to put the label inside the wrapper but before any diagnostics.
					// but did we already put it there as part of the ajaxSubscriber?
					if (el.compareDocumentPosition(label) & document.DOCUMENT_POSITION_CONTAINED_BY) {
						// label already inside the wraspper so do nothing
						return;
					}
					checkboxLabelPositionHelper(el, label);
					return;
				}

				// read-only, WRadioButton and WSelectToggle labels go after their labelled component
				if (label === el.nextElementSibling) {
					// already in the right place
					return;
				}
				parent = el.parentNode;
				if ((refElement = el.nextSibling)) {
					parent.insertBefore(label, refElement);
				} else {
					parent.appendChild(label);
				}
			}

			/**
			 * Move labels to their correct position.
			 * TODO: This _should_ be done in the Java Renderers.
			 * @function
			 * @private
			 * @param {Element} [element] a container element
			 */
			function moveLabels(element) {
				var el = element || document.body;
				if (element && Widget.isOneOfMe(element, MOVE_WIDGETS)) {
					moveLabel(el);
				} else {
					Array.prototype.forEach.call(Widget.findDescendants(el, MOVE_WIDGETS), moveLabel);
				}
			}

			/**
			 * Check if we need to restore a label from the moved label registry.
			 * The label for a WCheckBox has to be inside the labelled component's input wrapper to allow for error messages.
			 * @function
			 * @private
			 * @param {Element} element the potentially unlabelled labelled WCheckBox
			 */
			function checkRestoreLabel(element) {
				var refId = element.id,
					missingLabelContent = movedCbLabelReg[refId],
					notMissingLabels;
				try {
					if (missingLabelContent && CHECKBOX_WRAPPER.isOneOfMe(element)) {
						notMissingLabels = getLabelsForElement(element, wrappedInput.isReadOnly(element));
						if (!(notMissingLabels && notMissingLabels.length)) {
							// yep, we don't have a label for this check box any more
							checkboxLabelPositionHelper(element, missingLabelContent);
						}
					}
				} finally {
					if (missingLabelContent) {
						delete movedCbLabelReg[refId];
					}
				}
			}

			/**
			 * Store a nested label before we blow away a WCheckBox. Only needed if the WCheckBox is EXPLICITLY targeted via AJAX.
			 * @param {type} element
			 * @returns {undefined}
			 */
			function preInsertionAjaxSubscriber(element) {
				var labels, label;
				if (!(element && isActiveWCheckBox(element))) {
					return;
				}
				labels = getLabelsForElement(element);
				if (!(labels && labels.length)) {
					return;
				}
				label = labels[0];
				if (!(element.compareDocumentPosition(label) & document.DOCUMENT_POSITION_CONTAINED_BY)) {
					// label not inside the wraspper so do nothing
					return;
				}
				movedCbLabelReg[element.id] = label.outerHTML;
			}

			/**
			 * Post-insertion AJAX subscriber to convert labels from a HTML label element to its read-only analogue and vice-versa when
			 * a labelled element is replaced via AJAX.
			 *
			 * @function
			 * @private
			 * @param {Element} element the new element.
			 */
			function ajaxSubscriber(element) {
				if (!element) {
					return;
				}

				checkRestoreLabel(element);
				moveLabels(element);
				Array.prototype.forEach.call(wrappedInput.get(element, true), function (next) {
					var isRO = wrappedInput.isReadOnly(next),
						labels = getLabelsForElement(next, true);
					labels.forEach(function (label) {
						var isLabel = label.tagName === tag.LABEL,
							input;
						// if the new element is readOnly and the old one
						if (isRO && isLabel || !(isRO || isLabel)) {
							convertLabel(next, label, isRO);
							return;
						}
						// only have to do this if we are not converting the labels.
						if ((input = wrappedInput.getInput(next))) {
							if (input.type !== "radio") {
								mandateLabel(label, !isRO && shed.isMandatory(input) ? "add" : "remove");
							}
						}
						if (shed.isHidden(next, true)) {
							shed.hide(label, true);
						} else {
							shed.show(next);
						}
					});
				});
			}

			/**
			 * Initialiser callback to subscribe to {@link module:wc/dom/shed} and
			 * {@link module:wc/ui/ajax/processResponse}.
			 *
			 * @function module:wc/ui/label.postInit
			 * @public
			 */
			this.postInit = function () {
				shed.subscribe(shed.actions.MANDATORY, shedMandatorySubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedMandatorySubscriber);
				shed.subscribe(shed.actions.SHOW, shedHideSubscriber);
				shed.subscribe(shed.actions.HIDE, shedHideSubscriber);
				processResponse.subscribe(preInsertionAjaxSubscriber);
				processResponse.subscribe(ajaxSubscriber, true);
			};

			/**
			 * Get the hint from a given label.
			 *
			 * @function module:wc/ui/label.getHint
			 * @public
			 * @param {Element} label the label to test
			 * @returns {Element} the label's hint, if any
			 */
			this.getHint = function (label) {
				if (label) {
					HINT = HINT || new Widget("", CLASS_HINT);
					return HINT.findDescendant(label);
				}
				return null;
			};

			/**
			 * Set (add to or remove) a hint on a label.
			 *
			 * @function module:wc/ui/label.setHint
			 * @public
			 * @param {Element} label the label to which we are modifying hint content
			 * @param {String} [content] the hint content to add; if falsey then an existing hint (if any) is removed
			 */
			this.setHint = function(label, content) {
				var hint = this.getHint(label),
					BEFORE_END = "beforeend";
				if (hint) {
					if (content) {
						if (textContent.get(hint)) {
							hint.insertAdjacentHTML(BEFORE_END, "<br>");
						}
						hint.insertAdjacentHTML(BEFORE_END, content);
					} else {
						hint.parentNode.removeChild(hint);
					}
				} else if (content) {
					hint = tag.toTag(tag.SPAN, false, "class='" + CLASS_HINT + "'") + content + tag.toTag(tag.SPAN, true);
					label.insertAdjacentHTML(BEFORE_END, hint);
				}
			};

			this.preInit = function(element) {
				moveLabels(element);
			};

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._convert = convertLabel;
			/**
			 * Public for testing.
			 * @ignore
			 */
			this._ajax = ajaxSubscriber;

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._checkboxLabelPositionHelper = checkboxLabelPositionHelper;
		}

		/**
		 * A module which provides functionality peculiar to control labelling elements (labels and label-surrogates). This
		 * module is mainly concerned with ensuring that as controls are replaced using AJAX that any labelling components for
		 * those controls are always kept in the right state.
		 *
		 * @module
		 * @requires wc/dom/classList
		 * @requires wc/dom/initialise
		 * @requires wc/dom/shed
		 * @requires wc/dom/tag
		 * @requires wc/dom/Widget
		 * @requires wc/dom/getLabelsForElement
		 * @requires wc/ui/ajax/processResponse
		 * @requires wc/dom/role
		 * @requires wc/dom/textContent
		 * @requires wc/dom/wrappedInput
		 * @requires wc/ui/checkBox
		 * @requires wc/ui/feedback
		 */
		var instance = new Label();
		initialise.register(instance);
		return instance;
	});
