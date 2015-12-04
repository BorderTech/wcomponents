/**
 * Provides keyboard navigation functionality.
 * @module
 */
define(function() {
	"use strict";
	var
		/**
		 * An object holding property names used in keyWalker configuration.
		 * @constant
		 * @type {module:wc/dom/keyWalker~options}
		 * @private
		 */
		OPTIONS = {
			CYCLE: "cycle",
			DEPTH_FIRST: "depthFirst"
		},
		/**
		 * An object holding move locations and their value for bitwise calculations.
		 * @constant
		 * @type {module:wc/dom/keyWalker~moveTo}
		 * @private
		 */
		MOVE_TO = {
			FIRST: 1,  // first sibling
			LAST: 2,  // last sibling
			PREVIOUS: 4,  // previous sibling
			NEXT: 8,  // next sibling
			PARENT: 16,  // parent element
			TOP: 32,  // first accessible node in hierarchy
			END: 64,  // the very last accessible node in the hierarchy
			CHILD: 128,  // the first accessible child node
			LAST_CHILD: 256  // the last accessible child node
		};

	/**
	 * @constructor
	 * @alias module:wc/dom/keyWalker~KeyWalker
	 * @private
	 */
	function KeyWalker() {
		/**
		 * Helper for navigating between elements in a tree structure (for example a menu). This function will only
		 * find the required target element (if any). What you do with that knowledge is up to you!
		 *
		 * @function
		 * @private
		 * @param {module:wc/dom/keyWalker~config} conf The configuration object for this navigation.
		 * @param {Element} element where we are starting.
		 * @param {integer} whichWay value from {@link module:wc/dom/keyWalker.MOVE_TO}.
		 * @returns {?Element} The destination element, if any.
		 * @throws {ReferenceError} If the value of whichWay is not a known value in {@link module:wc/dom/keyWalker.MOVE_TO}.
		 */
		function treeWalkerNavHelper(conf, element, whichWay) {
			var action,
				tw,
				sibling,
				result = null;
			if (whichWay && element) {
				tw = getTreewalker(conf);
				tw.currentNode = element;
				action = getAction(conf, element, whichWay);
				if (action.next && tw[action.next]) {
					sibling = tw[action.next]();
					// we may not have a sibling if we are at the ends of a list so cycle unless specifically prevented
					if (!sibling && conf[OPTIONS.CYCLE] && (whichWay & (MOVE_TO.PREVIOUS | MOVE_TO.NEXT))) {
						result = treeWalkerNavHelper(conf, action.current, ((whichWay === MOVE_TO.PREVIOUS) ? MOVE_TO.LAST : MOVE_TO.FIRST));
					}
					else {
						result = sibling;
					}
				}
			}
			return result;
		}

		/*
		 * Helper for treeWalkerNavHelper
		 * @function
		 * @private
		 */
		function getAction(conf, current, whichWay) {
			var result = {
				next: "",
				current: current
			};
			switch (whichWay) {
				case MOVE_TO.PARENT:
					result.next = "parentNode";
					break;
				case MOVE_TO.TOP:
					conf.tw.currentNode = conf.root;
					result.next = "firstChild";
					break;
				case MOVE_TO.END:
					conf.tw.currentNode = conf.root;
					result.next = "lastChild";
					break;
				case MOVE_TO.PREVIOUS:
					result.next = conf[OPTIONS.DEPTH_FIRST] ? "previousNode" : "previousSibling";
					break;
				case MOVE_TO.NEXT:
					result.next = conf[OPTIONS.DEPTH_FIRST] ? "nextNode" : "nextSibling";
					break;
				case MOVE_TO.FIRST:
					result.current = (conf.tw.currentNode = current.parentNode);
					result.next = "firstChild";
					break;
				case MOVE_TO.LAST:
					result.current = (conf.tw.currentNode = current.parentNode);
					result.next = "lastChild";
					break;
				case MOVE_TO.CHILD:
					result.next = "firstChild";
					break;
				case MOVE_TO.LAST_CHILD:
					result.next = "lastChild";
					break;
				default:
					throw new ReferenceError("keyWalker.treeWalkerNavHelper cannot move to where you think you want to move to.");
			}
			return result;
		}

		/*
		 * Helper for treeWalkerNavHelper
		 * @function
		 * @private
		 */
		function getTreewalker(conf) {
			var tw = conf.tw || (conf.tw = document.createTreeWalker(conf.root, NodeFilter.SHOW_ELEMENT, conf.filter, false));
			return tw;
		}

		/**
		 * Simple walking of linear grouped components such as aria radio groups (see single selectable table rows
		 * for an example). KeyWalker.MOVE_TO is limited to a single dimension: FIRST, LAST, PREVIOUS, NEXT, TOP,
		 * END
		 *
		 * @function
		 * @private
		 * @param {module:wc/dom/keyWalker~config} conf The configuration object for this navigation.
		 * @param {Element} element The start point.
		 * @param {integer} whichWay value from {@link module:wc/dom/keyWalker.OVE_TO}.
		 * @returns {?Element} The destination element, if any.
		 * @throws {ReferenceError} If the value of whichWay is one reserved for two domensional navigation.
		 */
		function groupBasedNavHelper(conf, element, whichWay) {
			var target,
				cycled = false,
				currentIndex,
				result = null,
				direction = whichWay,
				group = conf.root;

			if (direction === MOVE_TO.PARENT || direction === MOVE_TO.CHILD || direction === MOVE_TO.LAST_CHILD) {
				throw new ReferenceError("Groups have no notion of parent or child, you probably don't want to do this!");
			}
			else if (direction && group && group.length) {
				currentIndex = Array.prototype.indexOf.call(group, element);
				do {
					switch (direction) {
						case MOVE_TO.NEXT:
							/*eslint-disable */  // remove when this bug is fixed https://github.com/eslint/eslint/issues/2248
							if (currentIndex === group.length - 1) {
								if (conf[OPTIONS.CYCLE]) {
									if (!cycled) {
										cycled = true;
										target = group[0];
										currentIndex = 0;
									}
									else {
										target = null;
									}
								}
							}
							else {
								target = group[++currentIndex];
							}
							break;
						case MOVE_TO.PREVIOUS:
							if (currentIndex === 0) {
								if (conf[OPTIONS.CYCLE]) {
									if (!cycled) {
										cycled = true;
										currentIndex = group.length - 1;
										target = group[currentIndex];
									}
									else {
										target = null;
									}
								}
							}
							else {
								target = group[--currentIndex];
							}
							/*eslint-enable */
							break;
						case MOVE_TO.FIRST:
						case MOVE_TO.TOP:
							target = group[0];
							currentIndex = 0;
							direction = MOVE_TO.NEXT;
							break;
						case MOVE_TO.LAST:
						case MOVE_TO.END:
							currentIndex = group.length - 1;
							target = group[currentIndex];
							direction = MOVE_TO.PREVIOUS;
							break;
					}
				}
				while (target && conf.filter(target) === NodeFilter.FILTER_REJECT);
				if (target) {
					result = target;
				}
			}
			return result;
		}

		/**
		 * Publicise the map of locations to move to.
		 * @constant module:wc/dom/keyWalker.MOVE_TO
		 * @type {module:wc/dom/keyWalker~moveTo}
		 */
		this.MOVE_TO = MOVE_TO;

		/**
		 * Publicise the property names used in a keyWalker configuration object.
		 * @constant module:wc/dom/keyWalker.OPTIONS
		 * @type {module:wc/dom/keyWalker~options}
		 */
		this.OPTIONS = OPTIONS;

		/**
		 * Get the destination. This function will only find the required target element (if any). What you do with
		 * that knowledge is up to you!
		 *
		 * @function module:wc/dom/keyWalker.getTarget
		 * @param {module:wc/dom/keyWalker~config} conf The configurationfor this particular walk.
		 * @param {Element} element The element we are on at the start of the navigation.
		 * @param {integer} direction The direction to hunt in as a property of {@link module:wc/dom/keyWalker.MOVE_TO}.
		 * @returns {?Element} The end point of navigation (though we do not actually do the navigation here).
		 */
		this.getTarget = function (conf, element, direction) {
			var result = null;

			if (conf.root.nodeType) {
				result = treeWalkerNavHelper(conf, element, direction);
			}
			else {
				result = groupBasedNavHelper(conf, element, direction);
			}
			return result;
		};
	}
	return /** @alias module:wc/dom/keyWalker */ new KeyWalker();


	/**
	 * An object holding move locations and their value for bitwise calculations.
	 * @typedef {Object} module:wc/dom/keyWalker~moveTo
	 * @property {number} FIRST Move to the first item in the group.
	 * @property {number} LAST Move to the last item in the group.
	 * @property {number} PREVIOUS Move to the previous item in the group.
	 * @property {number} NEXT Move to the next item in the group.
	 * @property {number} PARENT Move to the current item's parent item in the group.
	 * @property {number} TOP Move to the first accessible item in the group heirarchy.
	 * @property {number} END Move to the last accessible item in the group heirarchy.
	 * @property {number} CHILD Move to the first accessible child item of the current item in the group.
	 * @property {number} LAST_CHILD Move to the last accessible child item of the current item in the group.
	 */

	/**
	 * Configuration object used by keywalkers.
	 * @typedef {Object} module:wc/dom/keyWalker~config
	 * @property {Function} filter A function used to determine a valid target match.
	 * @property {(Element|NodeList|Array)} root The key walker root; if this is a single element we assume a tree
	 *    walk otherwise it is a group.
	 * @property {Boolean} [depthFirst] If true then treeWalker uses depther first in determiing the next node, not
	 *    used in group based key walking.
	 * @property {Boolean} [cycle] Set to true if nextSibling on last in branch/group or previousSibling on first in
	 *    branch/group results in cycling to the first/last respectively.
	 */

	/**
	 * An object holding property names used in keyWalker configuration. This is publicised as an aide-memoire to
	 * consuming classes.
	 *
	 * @typedef {Object} module:wc/dom/keyWalker~options
	 * @property {String} CYCLE "cycle" used to set the config.cycle Boolean property.
	 * @property {String} DEPTH_FIRST "depthFirst" used to set the config.depthFirst Boolean property.
	 * @see {@link module:wc/dom/keyWalker~config}
	 */

});
