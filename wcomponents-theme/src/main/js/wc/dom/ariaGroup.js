/**
 * Provides a mechanism to get elements which are grouped by WAI-ARIA roles or by being aria-owned.
 *
 * @module
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/aria
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/role
 *
 * @todo document private members, clean up code.
 */
define(["wc/array/toArray",
		"wc/dom/aria",
		"wc/dom/Widget",
		"wc/dom/role"],
	/** @param toArray wc/array/toArray @param aria wc/dom/aria @param Widget wc/dom/Widget @param $role wc/dom/r4ole @ignore */
	function(toArray, aria, Widget, $role) {
		"use strict";

		/**
		 * @constructor
		 * @private
		 * @alias module:wc/dom/ariaGroup~AriaGroup
		 */
		function AriaGroup() {
			/**
			* Gets the element that "aria-owns" another element.
			*
			* @function module:wc/dom/ariaGroup.getOwner
			* @param {Element} element The element to test.
			* @returns {?Element} The element which owns the passed in element.
			*/
			this.getOwner = function(element) {
				var id = element.id,
					ownerWd,
					result;
				if (id) {
					ownerWd = new Widget("", "", { "aria-owns": id });
					result = ownerWd.findDescendant(document.body);  // something may not be aria-owned by more than one element at a time
				}
				return result;
			};

			/**
			 * Gets elements that are indirectly owned by a DOM element with "aria-owns".
			 *
			 * @function module:wc/dom/ariaGroup.getOwned
			 * @param {Element} element The start element.
			 * @returns {array} An array of elements owned by the element. If the element does not own any then the
			 * array is empty.
			 */
			this.getOwned = function(element) {
				var ids = element.getAttribute("aria-owns"),
					result = [],
					i,
					len,
					owned;
				if (ids) {
					ids = ids.split(/\s+/);
					for (i = 0, len = ids.length; i < len; ++i) {
						owned = document.getElementById(ids[i]);
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
			 * @param {Element} element The reference element.
			 * @param {String} role The element role (we have already calculated before we get here).
			 * @param {Boolean} [ignoreInnerGroups] If true then all members of the group will be included even if they
			 *    are also members of another group nested within the current one. This is most commonly found in menus
			 *    where we may want all items (for example in a tree) whether or not they are also in a submenu.
			 * @returns {?Array} An array of group members.
			 */
			this.getGroup = function (element, role, ignoreInnerGroups) {
				var container,
					_role = role || $role.get(element, true),
					containerWd,
					scopedRoles,
					candidates,
					result,
					widgets,
					rescope = true;

				if (_role) {
					scopedRoles = aria.getMustContain(_role);
				}
				if (!scopedRoles.length) {
					scopedRoles = aria.getScopedTo(_role);
				}

				// a group is defined by an element if the element role has something "scopedTo" it
				if ((scopedRoles && scopedRoles.length)) {
					container = element;
					rescope = false;
				}
				else {
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
						containerWd = new Widget("", "", { role: _role });
						widgets = buildWidgetArrayFromRoles(scopedRoles);

						if (widgets.length) {
							candidates = toArray(Widget.findDescendants(container, widgets));

							if (candidates && candidates.length) {
								if (ignoreInnerGroups) {
									result = candidates;
								}
								else {
									result = candidates.filter(function(next) {
										return this.getContainer(next, containerWd, true) === container;
									}, this);
								}
							}
						}
					}
					else {
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
			 * @param {Element} element the reference element
			 * @param {module:wc/dom/Widget} [containerWd] a Widget describing the container, if any, for the
			 *    subclass of {@link module:wc/dom/AriaAnalog}
			 * @param {Boolean} [ignoreOwner] If true then do not look for a WAI-ARIA owner (aria-owns) element.
			 * @returns {?Element} The group container element, if any.
			 */
			this.getContainer = function(element, containerWd, ignoreOwner) {
				var scope, widgets, result, role;

				if (!ignoreOwner) {
					result = this.getOwner(element);
				}
				if (!result) {
					if (containerWd) {
						result = containerWd.findAncestor(element);
					}
					else if ((role = $role.get(element, true))) {
						scope = aria.getScope(role);
						if (!(scope && scope.length)) {
							scope = aria.getScopedBy(role);
						}
						if (scope && scope.length) {
							widgets = buildWidgetArrayFromRoles(scope);
							if (widgets.length === 0) {
								widgets = null;
							}
							else if (widgets.length === 1) {
								widgets = widgets[0];
							}

							if (widgets) {
								if (Array.isArray(widgets)) {
									result = Widget.findAncestor(element, widgets);
								}
								else {
									result = widgets.findAncestor(element);
								}
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
			 * @returns {module:wc/dom/Widget[]} An array of Widgets or an empty array if no Widgets are constructed.
			 */
			function buildWidgetArrayFromRoles(roles) {
				var widgets = [],
					uselessRole = "group",  // this role is not used to build a group as it is a
					uselessIndex;

				if (roles.length > 1 && ~(uselessIndex = roles.indexOf(uselessRole))) {
					roles.splice(uselessIndex, 1);
				}

				if (roles.length) {
					widgets = roles.map(function(next) {
						return new Widget("", "", { role: next });
					});
				}
				return widgets;
			}
		}
		return /** @alias module:wc/dom/ariaGroup */ new AriaGroup();
	});
