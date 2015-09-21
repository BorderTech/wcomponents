/**
 * @module
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/array/toArray
 */
define(["wc/dom/tag",
		"wc/dom/Widget",
		"wc/array/toArray"],
	/** @param tag wc/dom/tag @param Widget wc/dom/Widget @param toArray wc/array/toArray @ignore */
	function(tag, Widget, toArray) {
		"use strict";

		var LABELABLE = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.PROGRESS];

		/**
		 * Gets labelling element/s (label, legend or pseudo-label) for a control.
		 *
		 * @function module:wc/dom/getLabelsForElement
		 * @param {Element} element The element for which we want to find labels.
		 * @param {Boolean} [includeReadOnly] If true then also search for "labelling" elements for read only controls.
		 * @returns {Element[]} An array of elements which label element. If element has no 'labels' then an empty array is
		 *    returned.
		 */
		function getLabels(element, includeReadOnly) {
			var result = [],
				labels,
				FIELDSET = new Widget("fieldset"),
				LEGEND,
				LABEL,
				tagName,
				labelFor,
				query;

			if (FIELDSET.isOneOfMe(element)) {
				LEGEND = LEGEND || new Widget("legend");
				if ((labels = LEGEND.findDescendant(element, true))) {
					result.push(labels);
				}
			}

			if (element.id) {  // include fieldsets for finding legend stand-in pseudo-labels
				query = "label[for=\"" + element.id + "\"],[data-wc-for=\"" + element.id + "\"]";
				if (includeReadOnly) {
					query += ",[${wc.ui.label.attribute.readonlyFor}=\"" + element.id + "\"]";
				}
				result = result.concat(toArray(document.querySelectorAll(query)));
			}

			if (!(result && result.length)) {
				// try getting an ancestor label element ONLY if element is input, textarea or select
				LABEL = LABEL || new Widget("label");
				tagName = element.tagName;
				if (~LABELABLE.indexOf(tagName) && (labels = LABEL.findAncestor(element))) {
					labelFor = labels.getAttribute("for");
					if ((labelFor && labelFor === element.id) || !labelFor) {
						result = [labels];
					}
					else {
						result = [];
					}
				}
				else {
					result = [];
				}
			}
			return result;
		}

		return getLabels;
	});
