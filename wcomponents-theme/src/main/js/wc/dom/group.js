/**
 * A module that knows how different DOM elements are grouped.
 *
 * How are DOM elements grouped?
 *
 * <dl>
 * <dt>FORM CONTROLS:</dt>
 * <dd>Grouped natively by their "name" attribute, shining example is radio buttons. SELECT elements are a special case
 * as they DEFINE a group of OPTIONS.</dd>
 * <dt>ARIA ELEMENTS:</dt>
 * <dd>Aria defines several different roles that are grouped, for example "radio". In some cases ARIA allows for
 * optional subgroups (for example "tree"). For aria roles that do not have an explicitly defined grouping role we can
 * assume these will be grouped by an element with aria-role of group (for example "checkbox").</dd>
 * </dl>
 *
 * @module
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/ariaGroup
 * @requires module:wc/dom/role
 */
define(["wc/array/toArray", "wc/dom/ariaGroup", "wc/dom/role"],
	function(toArray, ariaGroup, $role) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/group~ElementGroup
		 * @private
		 */
		function ElementGroup() {
			/**
			 * Get a group based on an element. The element is either a member of the group (for example a check box) or
			 * defines a group (for example a select).
			 *
			 * @function module:wc/dom/group.get
			 * @param {Element} element An element which belongs to (or defines) the group. BEWARE OF AMBIGUOUS
			 *    CONTAINERS. Think about it, if you pass a fieldset or a "menu" to this function what do you expect to
			 *    get as the group? For example an element with role of "menu" could contain menuitems, menuitemradios
			 *    or menuitemcheckboxes. Amibiguous contains will currently return as a group ALL of the possible
			 *    matches, for example if you pass a menu the result may contain a mix of all different types mentioned
			 *    above.
			 * @param {Boolean} [ignoreInnerGroups] see {@link module:wc/dom/ariaGroup.getGroup}
			 * @returns {Element[]} An array containing the members of this dom group. If the element is not part of
			 *    any group the array is empty.
			 */
			this.get = function (element, ignoreInnerGroups) {
				var group,
					role,
					owner;

				// owned groups take precedence
				group = ariaGroup.getOwned(element);
				if (!(group && group.length) && (owner = ariaGroup.getOwner(element))) {
					group = ariaGroup.getOwned(owner);
				}

				// native groups are easy
				if (!(group && group.length)) {
					group = getNativeGroup(this, element);
				}

				// if all else fails get an aria based group
				if (!(group && group.length) && (role = $role.get(element))) {
					group = ariaGroup.getGroup(element, role, ignoreInnerGroups);
				}

				if (group) {
					if (!Array.isArray(group)) {
						group = toArray(group);
					}
				} else {
					group = [];
				}
				return group;
			};

			/**
			 * For groups that are defined by ownership in a "container" (such as aria options in an aria listbox) this
			 * function will return the members of the group. This function will find elements that are owned implicity
			 * (by being descendants of the container) BUT NOT explicity (using the "aria-owns" attribute).
			 *
			 * @function module:wc/dom/group.getGroup
			 * @param {Element} element The container/owner itself unless containerWd is specified in which case any
			 *    descendant of a container/owner.
			 * @param {module:wc/dom/Widget} itemWd The widget that describes the items in the group
			 * @param {module:wc/dom/Widget} [containerWd] A widget that describes a group container.
			 * @returns {Element[]} An array of elements in the group.
			 * @todo This is used rather than this.get when we start at a known group container (such as a fieldset or
			 * a known ARIA container) but the naming is a bit ambiguous. Maybe we should change it?
			 */
			this.getGroup = function(element, itemWd, containerWd) {
				var result,
					container;
				if (containerWd) {
					container = containerWd.findAncestor(element);
					result = itemWd.findDescendants(container);
					result = (toArray(result)).filter(function(next) {
						return containerWd.findAncestor(next) === container;
					});
				} else {
					result = toArray(itemWd.findDescendants(element));
				}
				return result;
			};

			/**
			 * Get the grouping container, if any, for a particular element. A group element could be an ARIA grouping
			 * or an aria owner, both of these are covered by ariaGroup. For native DOM elements which are not owned
			 * they could be grouped by name (which has no container) or by container. This is not a good thing!
			 *
			 * @function module:wc/dom/group.getContainer
			 * @param {HTMLElement} element An element which may be a group container or a member of a group or neither.
			 * @param {module:wc/dom/Widget} [containerWd] A container widget for a subclass of
			 *    {@link module:wc/dom/AriaAnalog}.
			 * @returns {HTMLElement} The element which contains the group.
			 */
			this.getContainer = function(element, containerWd) {
				let container;

				if (containerWd) {
					container = containerWd.findAncestor(element);
				} else if (element.matches("option, optgroup")) {
					container = element.closest("select optgroup");
					if (!container) {
						container = element.closest("select");
					}
				}
				// else if (element.name) {
				//	if (document.getElementsByName(element.name).length > 1) {
				//		return null;  // elements grouped by name do not have a container to define the group
				//	}
				// }

				if (!container) {
					container = ariaGroup.getContainer(element, containerWd);
				}
				return container;
			};
		}

		/**
		 * Get a "native" DOM group.
		 * @param {ElementGroup} elementGroup
		 * @param {HTMLElement} element The reference element.
		 * @returns {HTMLElement[]} The group, if found.
		 * @private
		 * @function
		 */
		function getNativeGroup(elementGroup, element) {
			let group;
			if (element.tagName) {
				if (element.matches("input[type='radio'], input[type='checkbox']")) {
					if (element.hasAttribute("name")) {
						group = document.getElementsByName(element.getAttribute("name"));
					}
				} else if (element.matches("select")) {
					group = element.options;
				} else if (element.matches("optgroup")) {
					group = element.querySelectorAll("option");
				} else if (element.matches("option")) {
					let container = elementGroup.getContainer(element);
					if (container) {
						group = elementGroup.get(container);
					}
				} else if (element.matches("tbody")) {  // yes a tbody is a grouping element for trs
					group = element.querySelectorAll("tr");
				}
			}
			return group;
		}

		return /** @alias module:wc/dom/group */ new ElementGroup();
	});
