import aria from "wc/dom/aria";
import $role from "wc/dom/role";

/**
 * Provides a mechanism to get elements which are grouped by WAI-ARIA roles or by being aria-owned.
 */
const ariaGroup = {};

/**
* Gets the element that "aria-owns" another element.
*
* @function module:wc/dom/ariaGroup.getOwner
* @param {HTMLElement} element The element to test.
* @returns {HTMLElement} The element which owns the passed in element.
*/
ariaGroup.getOwner = function(element) {
	const { id } = element;
	let result = null;
	if (id) {
		const ownerWd = `[aria-owns~='${id}']`;
		result = document.body.querySelector(ownerWd);  // something may not be aria-owned by more than one element at a time
	}
	return result;
};

/**
 * Gets elements that are indirectly owned by a DOM element with "aria-owns".
 *
 * @function module:wc/dom/ariaGroup.getOwned
 * @param {HTMLElement} element The start element.
 * @returns {HTMLElement[]} An array of elements owned by the element. If the element does not own any then the
 * array is empty.
 */
ariaGroup.getOwned = function(element) {
	const ids = element.getAttribute("aria-owns"),
		result = [];
	if (ids) {
		for (let id of ids.split(/\s+/)) {
			let owned = document.getElementById(id);
			if (owned) {
				result.push(owned);
			}
		}
	}
	return result;
};

/**
 * Get all elements in the group which contains or is defined by a particular element. If you are using
 * owned groups then this should be called *after* testing for owned groups.
 *
 * @function module:wc/dom/ariaGroup.getGroup
 * @public
 * @param {HTMLElement} element The reference element.
 * @param {String} role The element role (we have already calculated before we get here).
 * @param {Boolean} [ignoreInnerGroups] If true then all members of the group will be included even if they
 *    are also members of another group nested within the current one. This is most commonly found in menus
 *    where we may want all items (for example in a tree) whether or not they are also in a submenu.
 * @returns {HTMLElement[]} An array of group members.
 */
ariaGroup.getGroup = function (element, role, ignoreInnerGroups) {
	let _role = role || $role.get(element, true);
	let scopedRoles;
	let rescope = true;
	let result;
	let container;



	if (_role) {
		scopedRoles = aria.getMustContain(_role);
	}
	if (!scopedRoles.length) {
		scopedRoles = aria.getScopedTo(_role);
	}

	// a group is defined by an element if the element role has something "scopedTo" it
	if (scopedRoles?.length) {
		container = element;
		rescope = false;
	} else {
		container = this.getContainer(element, null, true);
	}

	if (container) {

		if (rescope) {
			_role = $role.get(container, true);
			scopedRoles = aria.getMustContain(_role);
			if (!scopedRoles.length) {
				scopedRoles = aria.getScopedTo(_role);
			}
		}

		if (scopedRoles.length) {
			const widgets = buildWidgetArrayFromRoles(scopedRoles);

			if (widgets.length) {
				const selector = widgets.join();
				const candidates = Array.from(container.querySelectorAll(selector));
				if (candidates?.length) {
					if (ignoreInnerGroups) {
						result = candidates;
					} else {
						const containerWd = `[role='${_role}']`;
						result = candidates.filter(function(next) {
							return this.getContainer(next, containerWd, true) === container;
						}, this);
					}
				}
			}
		} else {
			console.log("could not find any scoped roles");
		}
	}
	return result;
};


/**
 * Get the element which has the role which defines/contains the group in which the current element is in.
 *
 * @function module:wc/dom/ariaGroup.getContainer
 * @public
 * @param {HTMLElement} element the reference element
 * @param {module:wc/dom/Widget} [containerWd] a Widget describing the container, if any, for the
 *    subclass of {@link module:wc/dom/AriaAnalog}
 * @param {Boolean} [ignoreOwner] If true then do not look for a WAI-ARIA owner (aria-owns) element.
 * @returns {HTMLElement} The group container element, if any.
 */
ariaGroup.getContainer = function(element, containerWd, ignoreOwner) {
	let result;
	if (!ignoreOwner) {
		result = this.getOwner(element);
	}
	if (!result) {
		let role;
		if (containerWd) {
			result = element.closest(containerWd.toString());
		} else if ((role = $role.get(element, true))) {
			let scope = aria.getScope(role);
			if (!(scope && scope.length)) {
				scope = aria.getScopedBy(role);
			}
			if (scope && scope.length) {
				let widgets = buildWidgetArrayFromRoles(scope);
				if (widgets.length === 0) {
					widgets = null;
				} else if (widgets.length === 1) {
					widgets = widgets[0];
				}

				if (widgets) {
					const selector = Array.isArray(widgets) ? widgets.join() : widgets;
					result = element.closest(selector);
				}
			}
		}
	}
	return result;
};

/**
 * Helper function to build an array of widget descriptors from an array of roles.
 *
 * @function
 * @private
 * @param {String[]} roles An array of role strings.
 * @returns {string[]} An array of selectors or an empty array if none are constructed.
 */
function buildWidgetArrayFromRoles(roles) {
	const uselessRole = "group";  // this role is not used to build a group as it is a
	let uselessIndex;
	let widgets;
	if (roles.length > 1 && ~(uselessIndex = roles.indexOf(uselessRole))) {
		roles.splice(uselessIndex, 1);
	}

	if (roles.length) {
		widgets = roles.map(role => `[role='${role}']`);
	}
	return widgets || [];
}

export default ariaGroup;
