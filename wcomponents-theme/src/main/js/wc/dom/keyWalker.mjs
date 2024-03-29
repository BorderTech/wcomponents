/**
 * An object holding property names used in keyWalker configuration.
 *
 * @type {module:keywalker~options}
 */
const OPTIONS = {
		CYCLE: "cycle",
		DEPTH_FIRST: "depthFirst"
	},
	/**
	 * An object holding move locations and their value for bitwise calculations.
	 *
	 * @constant
	 * @type {module:keywalker~moveTo}
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
 * Provides keyboard navigation functionality.
 * @module
 */
const instance = {
	/** @property {String} version the module version, just in case you want to know. */
	version: "1.0.1",

	/**
	 * Publicise the map of locations to move to.
	 * @constant module:keywalker.MOVE_TO
	 * @type {module:keywalker~moveTo}
	 */
	MOVE_TO: MOVE_TO,

	/**
	 * Publicise the property names used in a keyWalker configuration object.
	 * @constant module:keywalker.OPTIONS
	 * @type {module:keywalker~options}
	 */
	OPTIONS: OPTIONS,

	/**
	 * Get the destination. This function will only find the required target element (if any). What you do with
	 * that knowledge is up to you!
	 *
	 * @function module:keywalker.getTarget
	 * @param {module:keywalker~config} conf the configuration for this particular walk.
	 * @param {Element} element The element we are on at the start of the navigation.
	 * @param {Number} direction The direction to hunt in as a property of {@link module:keywalker.MOVE_TO}.
	 * @returns {Element} The end point of navigation (though we do not actually do the navigation here).
	 */
	getTarget: function (conf, element, direction) {
		if (typeof conf !== "object") {
			throw new TypeError("conf must be an object.");
		}
		const root = conf.root;
		if (!root) {
			throw new TypeError("conf.root must be an element, NodeList or array of elements.");
		}

		if (root.nodeType) {
			if (root.nodeType === Node.ELEMENT_NODE) {
				return treeWalkerNavHelper(conf, element, direction);
			}
			throw new TypeError("conf.root must be an element node to traverse a tree.");
		}
		return groupBasedNavHelper(conf, element, direction);
	}
};

/**
 * This is a simple TreeWalker filter for traversing elements and accepting **anything** unless it is disabled or hidden. You probably don't
 * want to use this, but it will be used if your configuration object does not include a filter function.
 *
 * @param {Element} element the element which the TreeWalker instance is investigating
 * @returns {number} a NodeFilter filter value
 */
function simpleFilter (element) {
	if (element.hasAttribute("disabled") ||
		element.hasAttribute("hidden") ||
		element.getAttribute("aria-disabled") === "true" ||
		element.getAttribute("aria-hidden") === "true") {
		return NodeFilter.FILTER_REJECT;
	}
	return NodeFilter.FILTER_ACCEPT;
}

/**
 * Find:
 *   1. the function name of the appropriate function to pass to a TreeWalker instance for traversal in any allowed direction;
 *   2. a node to pass to a recursion of treeWalkerNavHelper if the walk cycles.
 *
 * This is a helper for treeWalkerNavHelper.
 *
 * @function
 * @private
 * @param {module:keywalker~config} conf the configuration object for this navigation
 * @param {Element} current the element being tested
 * @param {number} whichWay the direction we are going
 * @returns {{ func: string, node: Element }}
 */
function getAction(conf, current, whichWay) {
	const result = {
		func: "",
		node: current
	};

	switch (whichWay) {
		case MOVE_TO.PARENT:
			result.func = "parentNode";
			break;
		case MOVE_TO.TOP:
			conf.tw.currentNode = conf.root;
			result.func = "firstChild";
			break;
		case MOVE_TO.END:
			conf.tw.currentNode = conf.root;
			result.func = "lastChild";
			break;
		case MOVE_TO.PREVIOUS:
			result.func = conf[OPTIONS.DEPTH_FIRST] ? "previousNode" : "previousSibling";
			break;
		case MOVE_TO.NEXT:
			result.func = conf[OPTIONS.DEPTH_FIRST] ? "nextNode" : "nextSibling";
			break;
		case MOVE_TO.FIRST:
			result.node = (conf.tw.currentNode = current.parentElement);
			result.func = "firstChild";
			break;
		case MOVE_TO.LAST:
			result.node = (conf.tw.currentNode = current.parentElement);
			result.func = "lastChild";
			break;
		case MOVE_TO.CHILD:
			result.func = "firstChild";
			break;
		case MOVE_TO.LAST_CHILD:
			result.func = "lastChild";
			break;
		default:
			throw new ReferenceError("Unknown direction for traversing a node tree.");
	}
	return result;
}

/**
 * Get a TreeWalker instance based on a particular configuration. Helper for treeWalkerNavHelper.
 *
 * @function
 * @private
 * @param {module:keywalker~config} conf the configuration object for this navigation
 * @returns {TreeWalker} an instance of TreeWalker
 */
function getTreeWalker(conf) {
	const filter = conf.filter || simpleFilter;
	const ownerDocument = conf.ownerDocument || document;
	return conf.tw || (conf.tw = ownerDocument.createTreeWalker(conf.root, NodeFilter.SHOW_ELEMENT, filter));
}

/**
 * Helper for groupBasedNavHelperLoopHelper (so a helper's helper's helper). This is to abstract cycling and reduce the paths though what are
 * otherwise extremely complex functions.
 *
 * @function
 * @private
 * @param {number} currentIndex A group (array) index - where we are at the moment in the group.
 * @param {Boolean} cycled Have we already cycled in this group?
 * @param {Boolean} useCycle Does this group support cycling at the extremities?
 * @param {Element[]} group The group of elements we are traversing.
 * @param {Boolean} next Are we going forwards (true) or backwards?
 * @returns {Object} A DTO to transfer info back to the calling function has target, idx and cycled properties.
 */
function groupNextPreviousHelper(currentIndex, cycled, useCycle, group, next) {
	let idx = currentIndex;
	const testIndex = next ? group.length - 1 : 0;
	if (idx === testIndex) {
		if (useCycle && !cycled) {
			idx = next ? 0 : group.length - 1;
			return { target: group[idx], idx, cycled: true };
		}
	} else {
		idx = currentIndex + (next ? 1 : -1);
		return {
			target: group[idx],
			idx
		};
	}
	return { target: null, idx: currentIndex, cycled };
}

/**
 * Helper for navigating between elements in a tree structure (for example a menu). This function will only find the required target element
 * (if any). What you do with that knowledge is up to you!
 *
 * @function
 * @private
 * @param {module:keywalker~config} conf the configuration object for this navigation.
 * @param {Element} element where we are starting
 * @param {number} whichWay value from {@link module:keywalker.MOVE_TO}
 * @returns {Element} the destination element
 * @throws {ReferenceError} if the value of whichWay is not a known value in {@link module:keywalker.MOVE_TO}
 */
function treeWalkerNavHelper(conf, element, whichWay) {
	if (whichWay && element) {
		const tw = getTreeWalker(conf);
		tw.currentNode = element;
		const action = getAction(conf, element, whichWay);
		if (action.func && tw[action.func]) {
			const target = tw[action.func]();
			// we may not have a sibling if we are at the ends of a list so cycle unless specifically prevented
			if (!target && action.node && conf[OPTIONS.CYCLE] && (whichWay & (MOVE_TO.PREVIOUS | MOVE_TO.NEXT))) {
				return treeWalkerNavHelper(conf, action.node, ((whichWay === MOVE_TO.PREVIOUS) ? MOVE_TO.LAST : MOVE_TO.FIRST));
			}
			return target;
		}
	}
	return null;
}

/**
 * Helper for groupBasedNavHelper to bring the do loop into a _slightly_ more readable form.
 *
 * @function
 * @private
 * @param {Object} conf The keyWalker config.
 * @param {Element} element The start element.
 * @param {number} whichWay The direction to walk.
 * @returns {Element} The key walk target element if any.
 */
function groupBasedNavHelperLoopHelper(conf, element, whichWay) {
	const group = Array.from(conf.root),
		filter = conf.filter || simpleFilter;
	let target,
		direction = whichWay,  // To not change the arg if we are going to an extremity.
		currentIndex = group.indexOf(element),
		cycled = false;
	do {
		let moveHelperObj;
		switch (direction) {
			case MOVE_TO.NEXT:
			case MOVE_TO.PREVIOUS:
				moveHelperObj = groupNextPreviousHelper(currentIndex, cycled, conf[OPTIONS.CYCLE], group, (direction === MOVE_TO.NEXT));
				target = moveHelperObj.target;
				currentIndex = moveHelperObj.idx;
				cycled = moveHelperObj.cycled;
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
	while (target && filter(target) === NodeFilter.FILTER_REJECT);
	return target;
}

/**
 * Simple walking of linear grouped components such as aria radio groups (see single selectable table rows
 * for an example). KeyWalker.MOVE_TO is limited to a single dimension: FIRST, LAST, PREVIOUS, NEXT, TOP,
 * END
 *
 * @function
 * @private
 * @param {module:keywalker~config} conf the configuration object for this navigation.
 * @param {Element} element The start point.
 * @param {number} whichWay value from {@link module:keywalker.OVE_TO}.
 * @returns {Element} The destination element, if any.
 * @throws {ReferenceError} If the value of whichWay is one reserved for two-dimensional navigation.
 */
function groupBasedNavHelper(conf, element, whichWay) {
	if (whichWay === MOVE_TO.PARENT || whichWay === MOVE_TO.CHILD || whichWay === MOVE_TO.LAST_CHILD) {
		throw new ReferenceError("Direction not supported for grouped elements.");
	}

	if (whichWay && conf.root && conf.root.length) { // the group we are traversing is defined in conf.root
		return groupBasedNavHelperLoopHelper(conf, element, whichWay);
	}
	return null;
}


export default instance;


/**
 * An object holding move locations and their value for bitwise calculations.
 *
 * @typedef {Object} module:keywalker~moveTo
 * @property {number} FIRST Move to the first item in the group.
 * @property {number} LAST Move to the last item in the group.
 * @property {number} PREVIOUS Move to the previous item in the group.
 * @property {number} NEXT Move to the next item in the group.
 * @property {number} PARENT Move to the current item's parent item in the group.
 * @property {number} TOP Move to the first accessible item in the group hierarchy.
 * @property {number} END Move to the last accessible item in the group hierarchy.
 * @property {number} CHILD Move to the first accessible child item of the current item in the group.
 * @property {number} LAST_CHILD Move to the last accessible child item of the current item in the group.
 */

/**
 * Configuration object.
 *
 * @typedef {Object} module:keywalker~config
 * @property {Element|NodeList|Array<Element>} root The key walker root; if this is a single element we assume a tree walk otherwise it is
 * deemed to be a group.
 * @property {Function} filter a TreeWalker filter function used to determine a valid target match.
 * @property {Boolean} [depthFirst] If truthy then treeWalker uses depth first in determining the next node, not used in group based key walking.
 * @property {Boolean} [cycle] if truthy nextSibling on last in branch/group or previousSibling on first in branch/group results in cycling to
 *  the first/last respectively.
 * @property {TreeWalker} [tw]
 * @property {Document} [ownerDocument] optionally provide a document to use instead of `global.document`.
 */

/**
 * An object holding property names used in keyWalker configuration. This is publicised as an aide-mémoire to consuming modules.
 *
 * @typedef {Object} module:keywalker~options
 * @property {String} CYCLE "cycle" used to set the `config.cycle` Boolean property.
 * @property {String} DEPTH_FIRST "depthFirst" used to set the config.depthFirst Boolean property.
 * @see {@link module:keywalker~config}
 */
