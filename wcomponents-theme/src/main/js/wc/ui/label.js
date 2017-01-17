define(["wc/dom/classList",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/tag",
	"wc/dom/Widget",
	"wc/dom/getLabelsForElement",
	"wc/ui/ajax/processResponse",
	"wc/i18n/i18n",
	"wc/dom/role",
	"wc/dom/textContent",
	"wc/dom/wrappedInput"],
	function (classList, initialise, shed, tag, Widget, getLabelsForElement, processResponse, i18n, $role, textContent, wrappedInput) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/label~Label
		 * @private
		 */
		function Label() {
			var LABEL = new Widget("label"),
				LEGEND,
				FAUX,
				TAGS = [tag.INPUT, tag.TEXTAREA, tag.SELECT, tag.FIELDSET],
				MANDATORY_SPAN = new Widget("span", "wc-off"),
				CLASS_HINT = "wc-label-hint",
				MOVE_WIDGETS = [new Widget("", "wc-checkbox"), new Widget("", "wc-radiobutton"), new Widget("button", "wc-selecttoggle")],
				HINT;

			/**
			 * Helper to do label manipulation.
			 * @function
			 * @private
			 * @param {Element} label the label to manipulate
			 * @param {Function} func the function to apply to the label
			 */
			function mandateLabel(label, func) {
				var mandatorySpan;
				if (label.tagName !== tag.LEGEND) {
					classList[func](label, "wc_req");
				}

				mandatorySpan = MANDATORY_SPAN.findDescendant(label);
				if (func === "add") {
					if (!mandatorySpan) {
						mandatorySpan = document.createElement(MANDATORY_SPAN.tagName);
						mandatorySpan.className = MANDATORY_SPAN.className;
						mandatorySpan.innerHTML = i18n.get("requiredPlaceholder");
						label.appendChild(mandatorySpan);
					}
				}
				else if (mandatorySpan) {
					mandatorySpan.parentNode.removeChild(mandatorySpan);
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
				}
				else {
					newLabellingElement = document.createElement("label");
					if ((input = wrappedInput.getInput(element))) { // should always be found
						newLabellingElement.setAttribute("for", input.id);
					}
				}
				newLabellingElement.className = label.className;
				newLabellingElement.innerHTML = label.innerHTML;
				input = input || element;
				mandateLabel(newLabellingElement, (!isRO && shed.isMandatory(input) ? "add" : "remove"));
				if (shed.isHidden(element, true)) {
					shed.hide(newLabellingElement, true); // nothing depends on the hidden state of a label and we are replicating a load-time state.
				}
				newLabellingElement.id = label.id;
				label.id = "";
				parent.insertBefore(newLabellingElement, label);
				parent.removeChild(label);
			}

			/**
			 * AJAX subscriber to convert labels from a HTML label element to its read-only analogue and vice-versa when
			 * a labelled element is replaced via AJAX.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced).
			 */
			function ajaxSubscriber(element) {
				if (!element) {
					return;
				}
				moveLabels(element);

				Array.prototype.forEach.call(wrappedInput.get(element, true), function (next) {
					var isRO = wrappedInput.isReadOnly(next);
					getLabelsForElement(next, true).forEach(function (label) {
						var isLabel = label.tagName === tag.LABEL,
							input;
						// if the new element is readOnly and the old one
						if (isRO && isLabel || !(isRO || isLabel)) {
							convertLabel(next, label, isRO);
							return;
						}
						// only have to do this if we are not converting the labels.
						if ((input = wrappedInput.getInput(next))) {
							mandateLabel(label, !isRO && shed.isMandatory(input) ? "add" : "remove");
						}
						if (shed.isHidden(next, true)) {
							shed.hide(label, true);
						}
						else {
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
				processResponse.subscribe(ajaxSubscriber, true);
			};

			/**
			 * Used to indicate if a given element is a labelling element of some kind.
			 * @function  module:wc/ui/label.isOneOfMe
			 * @public
			 * @param {Element} el The element being tested.
			 * @returns {Boolean} Returns true if the element is a labelling element, including faux-label stand-ins.
			 * @todo The label surrogate widget should come from {@link module:wc/ui/internalLink}.
			 */
			this.isOneOfMe = function (el) {
				LEGEND = LEGEND || new Widget("legend");
				FAUX = FAUX || new Widget("", "", {"data-wc-for": null});
				return Widget.isOneOfMe(el, [LABEL, LEGEND, FAUX]);
			};

			/**
			 * Get the hint from a given label.
			 *
			 * @function module:wc/ui/label.getHint
			 * @public
			 * @param {Element} label the label to test
			 * @returns {?Element} the label's hint, if any
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
				var hint = this.getHint(label);
				if (hint) {
					if (content) {
						if (textContent.get(hint)) {
							hint.insertAdjacentHTML("beforeEnd", "<br>");
						}
						hint.insertAdjacentHTML("beforeEnd", content);
					}
					else {
						hint.parentNode.removeChild(hint);
					}
				}
				else if (content) {
					hint = document.createElement("span");
					hint.className = CLASS_HINT;
					hint.innerHTML = content;
					label.appendChild(hint);
				}
			};

			function moveLabel(el) {
				var labels = getLabelsForElement(el, true),
					label, wrapper, parent, sibling;
				if (labels && labels.length) {
					label = labels[0];
					if (label === el.nextSibling) {
						return;
					}
					wrapper = wrappedInput.getWrapper(el) || el; // WSelectToggle is its own wrapper.
					parent = wrapper.parentNode;
					if ((sibling = wrapper.nextSibling)) {
						parent.insertBefore(label, sibling);
					}
					else {
						parent.appendChild(label);
					}
				}
			}

			function moveLabels(element) {
				var el = element || document.body;
				if (element && Widget.isOneOfMe(element, MOVE_WIDGETS)) {
					moveLabel(el);
				}
				else {
					Array.prototype.forEach.call(Widget.findDescendants(el, MOVE_WIDGETS), moveLabel);
				}
			}

			this.initialise = function(element) {
				moveLabels(element);
			};
		}

		/**
		 * A module which provides functionality peculiar to control labelling elements (labels and label-surrogates). This
		 * module is mainly concerned with ensuring that as controls are replaced using AJAX that any labelling components for
		 * those controls are always kept in the right state.
		 *
		 * @module
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/tag
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/getLabelsForElement
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/internalLink
		 * @requires module:wc/dom/role
		 */
		var instance = new Label();
		initialise.register(instance);
		return instance;
	});
