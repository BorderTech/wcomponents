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
define(["wc/dom/classList",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/dom/getLabelsForElement",
		"wc/ui/ajax/processResponse",
		"wc/i18n/i18n",
		"wc/dom/role",
		"wc/ui/internalLink"],
	/** @param classList wc/dom/classList @param initialise wc/dom/initialise @param shed wc/dom/shed @param tag wc/dom/tag @param Widget wc/dom/Widget @param getLabelsForElement wc/dom/getLabelsForElement @param processResponse wc/ui/ajax/processResponse @param i18n wc/i18n/i18n @param $role wc/dom/role @ignore */
	function(classList, initialise, shed, tag, Widget, getLabelsForElement, processResponse, i18n, $role) {
		"use strict";
		/*
		 * Implicit dependencies:
		 * wc/ui/internalLink is used to add label like functionality (click to focus) to the visible pseudo-label when
		 * a fieldset outputting component is labelled by a WLabel. It is included here as it is almost always required
		 * since many extensions of WInput are compound controls where the label is actually a combo of a legend and a
		 * pseudo-label.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/label~Label
		 * @private
		 */
		function Label() {
			var LABEL,
				LEGEND,
				FAUX,
				TAGS = [tag.INPUT, tag.TEXTAREA, tag.SELECT, tag.PROGRESS],
				MANDATORY_SPAN = new Widget("span", "wc_off");

			/**
			 * Function to do label manipulation when a labelled element is shed'ed.
			 * @function
			 * @private
			 * @param {Element} element The component which has undergone a state change.
			 * @param {Function} func The function to apply to the label(s).
			 * @param {String} [_arg] Second argument used when func is classList: one of "add" or "remove". Used for
			 *     mandatory/optional.
			 * @param {boolean} includeReadOnly include labels for readOnly controls (currently only show/hide need this).
			 */
			function mungeLabels(element, func, _arg, includeReadOnly) {
				var labels = getLabelsForElement(element, includeReadOnly);

				function _doItToMeBaby(next) {
					var mandatorySpan;
					if (next.tagName !== tag.LEGEND) {
						if (_arg) {
							classList[func](next, _arg);
						}
						else {
							shed[func](next);
						}
					}
					if (_arg) {
						mandatorySpan = MANDATORY_SPAN.findDescendant(next);
						if (func === "add") {
							if (!mandatorySpan) {
								mandatorySpan = document.createElement("span");
								mandatorySpan.className = "wc_off";
								mandatorySpan.innerHTML = i18n.get("${wc.common.i18n.requiredPlaceholder}");
								next.appendChild(mandatorySpan);
							}
						}
						else if (mandatorySpan) {
							mandatorySpan.parentNode.removeChild(mandatorySpan);
						}
					}
				}
				if (labels && labels.length) {
					Array.prototype.forEach.call(labels, _doItToMeBaby);
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
				if (element && !(element.tagName === tag.INPUT && element.type === "radio")) {
					// this does not apply to making a radio button mandatory (that just leads to confusion)
					if (TAGS.indexOf(element.tagName) > -1 || $role.has(element)) {
						// read-only components should not be mandatory/optional
						mungeLabels(element, (action === shed.actions.OPTIONAL ? "remove" : "add"), "wc_req");
					}
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
				if (element) {
					// anything, even read-only, can be hidden/shown
					mungeLabels(element, (action === shed.actions.SHOW ? "show" : "hide"), null, true);
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
			 * @param {Element} label A label (or read-only analogue) for element.
			 * @param {boolean} [fromActive] Indicates that element was originally active (true) or originally readOnly
			 *    (false).
			 */
			function convertLabel(element, label, fromActive) {
				var newLabellingElement,
					parent = label.parentNode,
					style,
					STYLE = "style",
					mandatorySpan;
				if (fromActive) {
					newLabellingElement = document.createElement("span");
					newLabellingElement.className = "label";
					newLabellingElement.setAttribute("${wc.ui.label.attribute.readonlyFor}", element.id);
				}
				else {
					newLabellingElement = document.createElement("label");
					newLabellingElement.setAttribute("for", element.id);

					if (shed.isMandatory(element)) {
						classList.add(newLabellingElement, "wc_req");
						mandatorySpan = document.createElement("span");
					}
				}

				newLabellingElement.innerHTML = label.innerHTML;
				if (mandatorySpan) {
					mandatorySpan.className = "wc_off";
					mandatorySpan.innerHTML = i18n.get("${wc.common.i18n.requiredPlaceholder}");
					newLabellingElement.appendChild(mandatorySpan);
				}
				else if ((mandatorySpan = MANDATORY_SPAN.findDescendand(newLabellingElement))) {
					mandatorySpan.parentNode.removeChild(mandatorySpan);
				}
				newLabellingElement.id = label.id;

				if (shed.isHidden(element)) {
					shed.hide(newLabellingElement, true);  // nothing depends on the hidden state of a label and we are replicating a load-time state.
				}
				if (classList.contains(label, "wc_off")) {
					classList.add(newLabellingElement, "wc_off");
				}
				if ((style = label.getAttribute(STYLE))) {
					newLabellingElement.setAttribute(STYLE, style);
				}

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
				var labels;
				if (element && element.tagName !== tag.FIELDSET && (labels = getLabelsForElement(element, true)) && labels.length) {
					/* We can use shed to re-shed the same state. This will make sure the labels/legends/stand-ins are
					 * set according to the rather convoluted rules which govern radio buttons and check boxes etc as
					 * appropriate.*/
					if (shed.isMandatory(element)) {
						shed.mandatory(element);
					}
					else {
						shed.optional(element);
					}

					if (shed.isHidden(element)) {
						shed.hide(element);
					}
					else {
						shed.show(element);
					}

					/*
					 * For "readOnly" <-> "editable" we need to be a little more cunning.
					 */
					labels.forEach(function (label) {
						/* forEach function to (possibly) convert a label for an interactive control to the read-only
						 * counterpart dummy label and vice-versa. */
						if (TAGS.indexOf(element.tagName) > -1) {
							if (label.tagName !== tag.LABEL) {
								convertLabel(element, label, false);
							}
						}
						else if (label.tagName === tag.LABEL) {
							convertLabel(element, label, true);
						}
					});
				}
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
			this.isOneOfMe = function(el) {
				LABEL = LABEL || new Widget("label");
				LEGEND = LEGEND || new Widget("legend");
				FAUX = FAUX || new Widget("", "", {"data-wc-for": null});
				return Widget.isOneOfMe(el, [LABEL, LEGEND, FAUX]);
			};
		}

		var /** @alias module:wc/ui/label */ instance = new Label();
		initialise.register(instance);
		return instance;
	});
