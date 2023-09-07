import ariaGroup from "wc/dom/ariaGroup";
import $role from "wc/dom/role";

const elementGroup = {};

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
 */

/**
 * Get a group based on an element. The element is either a member of the group (for example a checkbox) or
 * defines a group (for example a select).
 *
 * @function module:wc/dom/group.get
 * @param {Element} element An element which belongs to (or defines) the group. BEWARE OF AMBIGUOUS
 *    CONTAINERS. Think about it, if you pass a fieldset or a "menu" to this function what do you expect to
 *    get as the group? For example an element with role of "menu" could contain `menuitem`, `menuitemradio`
 *    or `menuitemcheckbox`. Ambiguous contains will currently return as a group ALL the possible
 *    matches, for example if you pass a menu the result may contain a mix of all different types mentioned
 *    above.
 * @param {Boolean} [ignoreInnerGroups] see {@link module:wc/dom/ariaGroup.getGroup}
 * @return {HTMLElement[]} An array containing the members of this dom group. If the element is not part of
 *    any group the array is empty.
 */
elementGroup.get = function (element, ignoreInnerGroups) {
	// owned groups take precedence
	let group = ariaGroup.getOwned(element);
	if (!group?.length) {
		const owner = ariaGroup.getOwner(element);
		group = owner ? ariaGroup.getOwned(owner) : [];
	}


	// native groups are easy
	if (!group?.length) {
		group = getNativeGroup(element);
	}

	// if all else fails get an aria based group
	if (!group.length) {
		const role = $role.get(element);
		group = role ? ariaGroup.getGroup(element, role, ignoreInnerGroups) : [];
	}

	if (group) {
		if (!Array.isArray(group)) {
			group = Array.from(group);
		}
	} else {
		group = [];
	}
	return group;
};

/**
 * For groups that are defined by ownership in a "container" (such as aria options in an aria listbox) this
 * function will return the members of the group. This function will find elements that are owned implicitly
 * (by being descendants of the container) BUT NOT explicitly (using the "aria-owns" attribute).
 *
 * @function module:wc/dom/group.getGroup
 * @param {Element} element The container/owner itself unless containerWd is specified in which case any
 *    descendant of a container/owner.
 * @param {string} itemWd The widget that describes the items in the group
 * @param {string} [containerWd] A widget that describes a group container.
 * @return {HTMLElement[]} An array of elements in the group.
 * @todo This is used rather than this.get when we start at a known group container (such as a fieldset or
 * a known ARIA container) but the naming is a bit ambiguous. Maybe we should change it?
 */
elementGroup.getGroup = function(element, itemWd, containerWd) {
	let result;
	const itemSelector = itemWd.toString();
	if (containerWd) {
		const containerSelector = containerWd.toString();
		const container = element.closest(containerSelector);
		result = Array.from(container.querySelectorAll(itemSelector));
		result = result.filter(next => container === next.closest(containerSelector));  // why is this needed?
	} else {
		result = Array.from(element.querySelectorAll(itemSelector));
	}
	return /** @type {HTMLElement[]} */(result);
};

/**
 * Get the grouping container, if any, for a particular element. A group element could be an ARIA grouping
 * or an aria owner, both of these are covered by ariaGroup. For native DOM elements which are not owned
 * they could be grouped by name (which has no container) or by container. This is not a good thing!
 *
 * @function module:wc/dom/group.getContainer
 * @param {Element} element An element which may be a group container or a member of a group or neither.
 * @param {string} [containerWd] A container widget for a subclass of
 *    {@link module:wc/dom/AriaAnalog}.
 * @return {HTMLElement} The element which contains the group.
 */
elementGroup.getContainer = function(element, containerWd) {
	let container;
	if (containerWd) {
		const containerSelector = containerWd.toString();
		container = element.closest(containerSelector);
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
	return /** @type {HTMLElement} */(container);
};

/**
 * Get a "native" DOM group.
 * @param {Element} element The reference element.
 * @returns {HTMLElement[]} The group, if found.
 * @private
 * @function
 */
function getNativeGroup(element) {
	let group;
	if (element.tagName) {
		if (element.matches("input[type='radio'], input[type='checkbox']")) {
			if (element.hasAttribute("name")) {
				group = Array.from(document.getElementsByName(element.getAttribute("name")));
			}
		} else if (element instanceof HTMLSelectElement) {
			group = Array.from(element.options);
		} else if (element.matches("optgroup")) {
			group = Array.from(element.querySelectorAll("option"));
		} else if (element.matches("option")) {
			let container = elementGroup.getContainer(element);
			if (container) {
				group = elementGroup.get(container);
			}
		} else if (element.matches("tbody")) {  // yes a tbody is a grouping element for trs
			group = Array.from(element.querySelectorAll("tr"));
		}
	}
	return group || [];
}

export default elementGroup;
