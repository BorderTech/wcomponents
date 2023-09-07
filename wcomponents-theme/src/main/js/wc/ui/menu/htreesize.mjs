import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import tree from "wc/ui/menu/tree";
import resizeable from "wc/ui/resizeable";
import processResponse from "wc/ui/ajax/processResponse";

const handleSelector = ".wc_branch_resize_handle";
const resized = {},
	STORE_KEY = "wc-treesize";

/**
 * When a tree component is resized store the preferred size for later use.  A subscriber for
 * {@link module:wc/ui/resizeable}.
 *
 * @function
 * @private
 * @param {Element} element The element which was resized.
 */
function resizeSubscriber(element) {
	if (element) {
		let store;
		const { id, style} = element;

		if (style.width || style.height) {
			resized[id] = { width: style.width, height: style.height} ;
			store = true;
		} else if (resized[id]) {
			delete resized[id];
			store = true;
		}

		if (store) {
			globalThis.localStorage[STORE_KEY] = JSON.stringify(resized);
		}
	}
}

/**
 * Set height and width on segments of a htree if these were stored previously.
 *
 * @function
 * @private
 * @param {String} id The id of the component to be sized.
 * @param {module:wc/ui/menu/htreesize~dto} obj A dto containing height and/or width.
 */
function doReapplySize(id, obj) {
	const element = document.getElementById(id);
	if (element && tree.isHTree(tree.getRoot(element))) {

		const { width, height } = obj;

		if (width) {
			element.style.width = width;
		}
		if (height) {
			element.style.height = height;
		}

		if (width || height) {
			resized[id] = { width, height };
		}
	}
}

/**
 * An iterator function to set sizes on groups in a htree.
 * @function
 * @private
 * @param {Element} element The tree or group being adjusted.
 * @param {Object} stored The object version of the stored size info.
 */
function reapplySizeOnTreeGroup(element, stored) {
	const id = element.id;
	const obj = stored ? stored[id] : null;
	if (obj) {
		doReapplySize(id, obj);
	}

}

/**
 * Apply user's previously stored sizes if htree or group is inserted via ajax.
 * @function
 * @private
 * @param {Element} element The ajax target.
 */
function ajaxSubscriber(element) {
	let stored = globalThis.localStorage[STORE_KEY];
	if (stored) {
		stored = JSON.parse(stored);

		if ((tree.isSubMenu(element) && tree.isHTree(tree.getRoot(element)))) {
			reapplySizeOnTreeGroup(element, stored);
		}

		const submenus = element.querySelectorAll(tree._wd.submenu.toString());
		Array.from(submenus).forEach(function(next) {
			if (tree.isHTree(tree.getRoot(next))) {
				reapplySizeOnTreeGroup(next, stored);
			}
		});
	}
}

/**
 * Reset size on doubleclick on the handle.
 * @function
 * @private
 * @param {MouseEvent} $event A dblclick event.
 */
function dblClickEvent($event) {
	const { target, defaultPrevented } = $event;
	if (!defaultPrevented) {
		const element = target.closest(handleSelector);
		if (element) {
			if (resizeable.clearSize(element)) {
				$event.preventDefault();
			}
		}
	}
}

/**
 * Handle key down.
 * @param {KeyboardEvent} $event
 */
function keydownEvent($event) {
	if (!$event.defaultPrevented && $event.key === "Enter") {
		if (resizeable.clearSize($event.currentTarget)) {
			$event.preventDefault();
		}
	}
}

/**
 *
 * @param {FocusEvent} $event
 */
function focusEvent($event) {
	const { target } = $event;
	const BS = "htreesize.inited";
	if (target.matches(handleSelector) && !target[BS]) {
		event.add(target, "keydown", keydownEvent);
		target[BS] = true;
	}
}

initialise.register({
	/**
	 * initialise htrees by setting any stored sizes and registering subscribers.
	 *
	 * @function module:wc/ui/menu/htreesize.initialise
	 * @public
	 * @param {HTMLBodyElement} element The element being initialised.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, capture: true });

		event.add(element, "dblclick", dblClickEvent);

		processResponse.subscribe(ajaxSubscriber, true);
		resizeable.subscribe(resizeSubscriber);
		let stored = globalThis.localStorage[STORE_KEY];
		if (stored) {
			stored = JSON.parse(stored);
			for (let o in stored) {
				doReapplySize(o, stored[o]);
			}
		}
	}
});

/**
 * @typedef {Object} module:wc/ui/menu/htreesize~dto An object which stores information about a htree or a htree
 * submenu.
 * @property {String} [height] The CSS height of the item including units.
 * @property {String} [width] The CSS width of the item including units.
 */

