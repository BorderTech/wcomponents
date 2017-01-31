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
			var TAGS = [tag.INPUT, tag.TEXTAREA, tag.SELECT, tag.FIELDSET],
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
						mandatorySpan = tag.toTag(MANDATORY_SPAN.tagName, false, "class='" + MANDATORY_SPAN.className + "'");
						mandatorySpan += i18n.get("requiredPlaceholder");
						mandatorySpan += tag.toTag(MANDATORY_SPAN.tagName, true);
						label.insertAdjacentHTML("beforeend", mandatorySpan);
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
					hint = tag.toTag(tag.SPAN, false, "class='" + CLASS_HINT + "'") + content + tag.toTag(tag.SPAN, true);
					label.insertAdjacentHTML("beforeend", hint);
				}
			};

			/**
			 * Move an individual element's label if required.
			 * @function
			 * @private
			 * @param {Element} el a radio button, checkbox or selectToggle-button
			 */
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
				}
				else {
					Array.prototype.forEach.call(Widget.findDescendants(el, MOVE_WIDGETS), moveLabel);
				}
			}

			this.preInit = function(element) {
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
