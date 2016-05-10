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

		var LABELABLE = [tag.INPUT, tag.SELECT, tag.TEXTAREA, tag.PROGRESS],
			FIELDSET,
			LEGEND,
			WRAPPER,
			INPUT,
			LABEL;


		/**
		 * Get labels and/or standins suing querySelector.
		 *
		 * @function
		 * @private
		 * @param {Element} element The labelled element.
		 * @param {Element[]} [labelArr] Labels we have already found.
		 * @param {Boolean} readOnly If true also get labels for element in its read-only state.
		 * @returns {Array} If element has no 'labels' then an empty array is returned.
		 */
		function doLabelQuery(element, labelArr, readOnly) {
			var result = labelArr || [],
				id = element.id,
				query = "label[for=\"" + id + "\"],[data-wc-for=\"" + id + "\"]";

			if (readOnly) {
				query += ",[data-wc-rofor=\"" + id + "\"]";
			}
			if (id) {
				result = result.concat(toArray(document.querySelectorAll(query)));
			}

			return result;
		}

		function getLabelsForWrapper(element, includeReadOnly) {
			var result = [], _input;
			if (includeReadOnly) {
				result = doLabelQuery(element, result, true);
			}
			INPUT = INPUT || new Widget(tag.INPUT);
			_input = INPUT.findDescendant(element);
			if (_input) {
				return doLabelQuery(_input, result);
			}
			return result;
		}

		function getAncestorLabel(element) {
			var label;
			LABEL = LABEL || new Widget(tag.LABEL);

			if (!(label = LABEL.findAncestor(element))) {
				return [];
			}

			if (!label.hasAttribute("for") || (label.getAttribute("for") === element.id)) {
				return [label];
			}
			return [];
		}

		/**
		 * Gets labelling element/s (label, legend or pseudo-label) for a control.
		 *
		 * @function module:wc/dom/getLabelsForElement
		 * @param {Element} element The element for which we want to find labels.
		 * @param {Boolean} [includeReadOnly] If true then also search for "labelling" elements for read only controls.
		 * @returns {Array} An array of elements which 'label' element. If element has no 'labels' then an empty array
		 *  is returned.
		 */
		function getLabels(element, includeReadOnly) {
			var result = [],
				label,
				tagName;

			FIELDSET = FIELDSET || new Widget(tag.FIELDSET);
			WRAPPER = WRAPPER ||  new Widget("", "wc_input_wrapper");

			if (WRAPPER.isOneOfMe(element)) {
				return getLabelsForWrapper(element, includeReadOnly);
			}

			if (FIELDSET.isOneOfMe(element)) {
				LEGEND = LEGEND || new Widget("legend");
				if ((label = LEGEND.findDescendant(element, true))) {
					result = [label];
				}
			}

			result = doLabelQuery(element, result, includeReadOnly);

			if (result && result.length) {
				return result;
			}

			// try getting an ancestor label element ONLY if element is input, textarea, select or progress.
			tagName = element.tagName;
			if (~LABELABLE.indexOf(tagName)) {
				return getAncestorLabel(element);
			}

			return [];
		}

		return getLabels;
	});
