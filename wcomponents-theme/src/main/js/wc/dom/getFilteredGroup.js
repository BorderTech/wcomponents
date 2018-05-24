/**
 * @module
 * @requires module:wc/dom/group
 * @requires module:wc/dom/shed
 */
define(["wc/dom/group", "wc/dom/shed"],
	/** @param $group wc/dom/group @param shed wc/dom/shed @ignore */
	function($group, shed) {
		"use strict";

		var
			/** @constant {String[]} FILTERS The properties which may be used to filter a group. @private */
			FILTERS = ["selected", "deselected", "disabled", "enabled", "hidden", "visible", "expanded", "collapsed"],
			/** @constant {String[]} SHED_FILTERS {@link module:wc/dom.shed} functions which may be used to filter a group. @private */
			SHED_FILTERS = ["isSelected", "isDisabled", "isHidden", "isExpanded"];

		/**
		 * Build an object defining states as bit masks from a string array.
		 *
		 * @function buildBitMask
		 * @private
		 * @param {Array} keys An array of strings that will become the keys of the bitmask.
		 * @returns {Object} An object where the strings in the array form the properties and each property has a value
		 *    which is a bitmask.
		 * @see {@link module:wc/dom/getFilteredGroup.FILTERS} for the supported options.
		 */
		function buildBitMask(keys) {
			var i, result = {};
			for (i = 0; i < keys.length; i++) {
				result[keys[i]] = 1 << i;
			}
			return result;
		}

		/**
		 * Encapsulates common usages of {@link module:wc/dom/group}. By default gets selected items in a group. This is
		 * changed by passing in an object containing a filter property to select items according to other states (
		 * see {@link module:wc/dom/getFilteredGroup.FILTERS} for supported states).
		 *
		 * Note: subgroups are honored, for example if you pass an option element the result set will be confined to
		 * sibling options within the same optgroup (if the option is in an optgroup). If not contained in an optgroup
		 * then the entire select list will be searched.
		 *
		 * @function module:wc/dom/getFilteredGroup
		 *
		 * @example  // the simplest usage, gets selected items
		 * var radio = document.getElementById("radioId"),
		 *     selectedItems = getFilteredGroup(radio);
		 *
		 * @example  // get disabled items in a radio group
		 * var radio = document.getElementById("radioId"),
		 *     disabledItems = getFilteredGroup(radio, {filter:getFilteredGroup.FILTERS.disabled});
		 *
		 * @example  // get items which are both selected and disabled in a radio group (silly I know)
		 * var radio = document.getElementById("radioId"),
		 *     disabledSelectedItems = getFilteredGroup(radio, {
		 *         filter:getFilteredGroup.FILTERS.selected + getFilteredGroup.FILTERS.disabled
		 *     });
		 *
		 * @param {Element|Element[]} element An element which belongs to (or defines) the group OR the group itself as
		 *    an array.
		 * @param {module:wc/dom/getFilteredGroup~config} [config] Arguments to tweak the default behavior of this
		 *    function.
		 * @returns {(Element[]|module:wc/dom/getFilteredGroup~groupAsObject)} A nullable array of elements which match
		 *    the filters (or are "selected" if no custom filter supplied); or an object encapsulating both the filtered
		 *    and unfiltered groups.
		 * @throws {TypeError} Throws a TypeError if element is false-y.
		 */
		function getFilteredGroup(element, config) {
			var result,
				group,
				itemWd,
				containerWd,
				filter,
				ignoreInnerGroups,
				asObject,
				shedAttributeOnly = false,
				mask = getFilteredGroup.FILTERS,
				filterFunc = function(el) {
					var _result = true,
						i,
						nextMask,
						positive,
						negative,
						flags,
						reverse;
					for (i = 0; (_result && i < FILTERS.length); i += 2) {
						positive = mask[FILTERS[i]];
						negative = mask[FILTERS[i | 1]];
						nextMask = positive + negative;  // combine flags that relate to this property
						flags = filter & nextMask;  // extract the relevant flags from the provided filter
						if (flags && flags !== nextMask) {  // if one flag is set (but not BOTH flags)
							reverse = !!(flags & negative);  // do we need to reverse the results from SHED?
							_result = reverse ^ shed[SHED_FILTERS[Math.floor(i / 2)]](el, shedAttributeOnly);
						}
					}
					return !!_result;  // XOR returns a bitmask not === true
				};
			if (element) {
				if (config) {
					itemWd = config.itemWd;
					containerWd = config.containerWd;
					asObject = config.asObject;
					filter = config.filter || (mask.selected | mask.enabled);
					ignoreInnerGroups = config.ignoreInnerGroups;
					shedAttributeOnly = !! config.shedAttributeOnly;
				} else {
					filter = mask.selected | mask.enabled;
				}

				if (Array.isArray(element)) {
					group = element;
				} else if (itemWd) {
					group = $group.getGroup(element, itemWd, containerWd);
				} else {
					group = $group.get(element, ignoreInnerGroups);
				}
				if (asObject) {
					result = {
						unfiltered: group,
						filtered: group.filter(filterFunc)
					};
				} else {
					result = group.filter(filterFunc);
				}
			} else {
				throw new TypeError("Element can not be null");
			}
			return result;
		}

		/**
		 * An object each property of which is a bitmask representing one of the available filters. These are then used
		 * to build a filter property to pass in the configuration object {@link module:wc/dom/getFilteredGroup~config}.
		 *
		 * @var module:wc/dom/getFilteredGroup.FILTERS
		 * @public
		 * @static
		 * @type {Object}
		 * @property {number} selected Used to filter for members of the group in any selected state.
		 * @property {number} deselected Used to filter for members of the group in any deselected state.
		 * @property {number} disabled Used to filter for members of the group in a disabled state.
		 * @property {number} enabled Used to filter for members of the group not in a disabled state.
		 * @property {number} hidden Used to filter for members of the group in a hidden state.
		 * @property {number} visible Used to filter for members of the group not in a hidden state.
		 * @property {number} expanded Used to filter for members of the group in any expanded state.
		 * @property {number} collapsed Used to filter for members of the group in any collapsed state.
		 */
		getFilteredGroup.FILTERS = buildBitMask(FILTERS);

		return getFilteredGroup;

		/**
		 * @typedef {Object} module:wc/dom/getFilteredGroup~config
		 * @property {boolean} [asObject] If true return an Object: {module:wc/dom/getFilteredGroup~groupAsObject}.
		 * @property {module:wc/dom/Widget} [itemWd] description Describes the type of item you are looking for. This
		 *    only works if the "element" parameter is a container (not an item itself and not an array). It is useful
		 *    when you have a container which could contain any sort of item (like a form or a fieldset) and you want to
		 *    find all selected items of a given type within that container.
		 * @property {number} [filter={module:wc/dom/getFilteredGroup.FILTERS}#selected] Bitmask comprised of flags in
		 *    {@link module:wc/dom/getFilteredGroup.FILTERS} If not provided the mask will default to "selected". Note
		 *    that setting BOTH flags for a given property (e.g. hidden + visible) is the same as setting NEITHER of the
		 *    flags so don't bother.
		 * @property {boolean} shedAttributeOnly If true use only the simple attribute test in shedFilters (at present
		 *    this applies only to isHidden).
		 */

		/**
		 * The return type when set as an object.
		 * @typedef {Object} module:wc/dom/getFilteredGroup~groupAsObject
		 * @property {Element[]} filtered The filtered group; may be empty but will always be an array.
		 * @property {Element[]} unfiltered The whole group from which the filtered group was extracted; may be empty
		 *    but will always be an array.
		 */
	});
