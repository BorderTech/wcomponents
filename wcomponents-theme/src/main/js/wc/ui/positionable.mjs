import getViewportSize from "wc/dom/getViewportSize.mjs";
import getBox from "wc/dom/getBox.mjs";
import getStyle from "wc/dom/getStyle.mjs";
import uid from "wc/dom/uid.mjs";
import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import debounce from "wc/debounce.mjs";
import resizeable from "wc/ui/resizeable.mjs";

const UNIT = "px",
	ZERO = `0${UNIT}`,
	positionedBySize = { length: 0 },
	/**
	 * Delay before firing resize event helper. Used to prevent the handler firing continually whilst
	 * dragging the window frame.
	 * @var
	 * @type Number
	 * @private
	 */
	RESIZE_TIME = 100;

/**
 * Resize event helper to reposition any component which is positioned relative to viewport when the
 * viewport dimensions change.
 *
 * @function
 * @private
 */
const resizeEventHelper = debounce(() => Object.keys(positionedBySize).forEach(resizeIteratorFunc), RESIZE_TIME);

/**
 * Provides functionality used to absolutely position a component. Components may be positioned relative to the viewport
 * or another component.
 *
 * @module
 */
const instance = {
	/**
	 * Sets the absolute position of an element but does not allow the left or top to be negative.
	 * @function module:wc/ui/positionable.setPosition
	 * @public
	 * @param {HTMLElement|string} element The element being positioned.
	 * @param {number} left The requested position of the left edge of the element.
	 * @param {number} top The requested position of the top edge of the element.
	 */
	setPosition: function(element, left, top) {
		// @ts-ignore
		const _el = handleIdOrElement(element);
		if (_el?.style) {
			if (left || left === 0) {
				_el.style.left = left + UNIT;
			}
			if (top || top === 0) {
				_el.style.top = top + UNIT;
			}
			forceToViewPort(_el);
		}
	},
	/**
	 * Allow an external module to set a component as positioned by size without actually positioning it yet.
	 *
	 * @function module:wc/ui/positionable.storePosBySize
	 * @public
	 * @param {Element} element The element which will be positioned.
	 * @param {module:wc/ui/positionable~setBySizeConfig} [conf] Position configuration.
	 */
	storePosBySize: function(element, conf) {
		const id = element.id || (element.id = uid());
		if (!positionedBySize[id]) {
			++positionedBySize.length;
		}
		positionedBySize[id] = { id, conf };
	},
	/**
	 * Position an element relative to another element or the viewport where the size of the element being
	 * positioned determines the location relative to the target.
	 *
	 * @function module:wc/ui/positionable.setBySize
	 * @public
	 * @param {HTMLElement} element The element to position.
	 * @param {module:wc/ui/positionable~setBySizeConfig} conf The configuration for this
	 *    position.
	 */
	setBySize: function(element, conf) {
		const _el = handleIdOrElement(element),
			id = _el.id || (_el.id = uid());
		let topOffset = 0.5,  // may be overridden by conf.topOffsetPC
			leftOffset = 0.5;  // may be overridden by conf.leftOffsetPC
		let width, height;

		if (conf) {
			width = conf.width;
			height = conf.height;
			// if the top offset is not specified then position the element so that it is at the top of the relative component
			topOffset = (conf.topOffsetPC !== undefined) ? conf.topOffsetPC : topOffset;
			// if the left offset is not specified then position the element so that it is in the middle of the relative component
			leftOffset = (conf.leftOffsetPC !== undefined) ? conf.leftOffsetPC : leftOffset;
		}

		const relSize = getViewportSize(true);
		if (!positionedBySize[id]) {
			++positionedBySize.length;
		}
		positionedBySize[id] = { id, conf };

		_el.style.top = "";
		_el.style.left = "";
		const box = getBox(_el);
		if (box) {
			width = box.width || width;
			height = box.height || height;
		}
		if (!width && _el.style.width) {
			width = parseFloat(_el.style.width.replace(UNIT, ""));
		}
		if (!height && _el.style.height) {
			height = parseFloat(_el.style.height.replace(UNIT, ""));
		}

		width = width || 0;
		const left = (relSize.width - width) * leftOffset;
		height = height || 0;
		const top = (relSize.height - height) * topOffset;
		this.setPosition(_el, left, top);
	},
	/**
	 * Removes position styles if they are ZERO.
	 * @function module:wc/ui/positionable.clearZeros
	 * @public
	 * @param {HTMLElement} element The element to reset.
	 * @param {Boolean} [ignoreTopLeft] If true then do not reset top or left, just bottom and right. Why? because we sometimes need to keep
	 * these as they are used rather a lot elsewhere. Why not bottom and right? Because they are only set during collision detection or
	 * explicit pinning and are never part of the underlying component's default position model.
	 */
	clearZeros: function(element, ignoreTopLeft) {
		if (!ignoreTopLeft && element.style.top === ZERO) {
			element.style.top = "";
		}
		if (element.style.bottom === ZERO) {
			element.style.bottom = "";
		}
		if (!ignoreTopLeft && element.style.left === ZERO) {
			element.style.left = "";
		}
		if (element.style.right === ZERO) {
			element.style.right = "";
		}
	},
	/**
	 * Clear inline positions.
	 *
	 * @function
	 * @public
	 * @param {HTMLElement} element the element being cleared
	 */
	clear: function(element) {
		element.style.top = "";
		element.style.left = "";
		element.style.right = "";
		element.style.bottom = "";
	}
};
/**
 * An iterator function which will loop through all known elements which are positioned by size and
 * reposition them if they are visible.
 *
 * @function
 * @private
 * @param {String} key An object key from positionedBySize.
 */
function resizeIteratorFunc(key) {
	if (key === "length") {
		return;
	}
	const next = positionedBySize[key];
	const element = (next.conf ? document.getElementById(key) : null);
	if (!element || shed.isHidden(element)) {
		return;
	}
	let reStore = false;
	if (element.style.width) {
		next.conf.width = Number(element.style.width.replace(UNIT, ""));
		reStore = true;
	}
	if (element.style.height) {
		next.conf.height = Number(element.style.height.replace(UNIT, ""));
		reStore = true;
	}
	if (reStore) {
		instance.storePosBySize(element, next.conf);
	}
	instance.setBySize(element, next.conf);
}

/**
 * Elements which are positioned relative to the viewport should be repositioned if the viewport resizes.
 *
 * @function
 * @private
 */
function resizeEvent() {
	if (positionedBySize.length) {
		resizeEventHelper();
	}
}

/**
 * Subscribe to {@link module:wc/ui/resizeable} to reposition components when they are resized.
 *
 * @function
 * @private
 * @param {HTMLElement} element The element being resized.
 */
function resizeableSubscriber(element) {
	const id = element.id;
	const key = id ? positionedBySize[id] : null;
	const conf = key?.conf;
	if (conf) {
		if (element.style.width) {
			conf.width = Number(element.style.width.replace(UNIT, ""));
		} else {
			delete conf.width;
		}
		if (element.style.height) {
			conf.height = Number(element.style.height.replace(UNIT, ""));
		} else {
			delete conf.height;
		}
		instance.storePosBySize(element, conf);
		instance.setBySize(element, conf);
	}
}

/**
 * Make sure an element is completely in view.
 * @function
 * @private
 * @param {HTMLElement} el the positionable element we want inside the viewport.
 */
function forceToViewPort(el) {
	const vpSize = getViewportSize(true);

	if (el.style.top && parseFloat(el.style.top) < 0) {
		el.style.top = ZERO;
	}

	if (el.style.left && parseFloat(el.style.left) < 0) {
		el.style.left = ZERO;
	}

	let box = getBox(el);
	let recalc;
	if (box.width > vpSize.width || box.height > vpSize.height) {
		let max;
		recalc = true;

		if (box.width > vpSize.width) {
			el.style.left = ZERO;
			max = getStyle(el, "maxWidth", true, true);
			if (max !== "100%") {
				el.style.maxWidth = "100%";
			}
		}

		if (box.height > vpSize.height) {
			el.style.top = ZERO;
			max = getStyle(el, "maxHeight", true, true);
			if (max !== "100%") {
				el.style.maxHeight = "100%";
			}
		}

		const overflow = getStyle(el, "overflow", false, true);
		if (!overflow || overflow === "visible") {
			el.style.overflow = "auto";
		}
	} else if (el.style.overflow === "auto") {
		el.style.overflow = "";
	}

	if (recalc) {
		box = getBox(el);
	}

	if (box.left < 0) {
		el.style.left = ZERO;
	}
	if (box.top < 0) {
		el.style.top = ZERO;
	}
}


/**
 * Handle annoying polymorphic arg, helps with type checking.
 * @param {HTMLElement|string} arg An element or ID
 * @return {HTMLElement}
 */
function handleIdOrElement(arg) {
	if (typeof arg === "string") {
		return document.getElementById(arg);
	}
	if (arg["nodeType"] === Node.ELEMENT_NODE) {
		return arg;
	}
	return null;
}

initialise.register({
	/**
	 * Late initialisation to add resize event handler and subscriber to {@link module:wc/ui/resizeable}.
	 * @function module:wc/ui/positionable.postInit
	 * @public
	 */
	postInit: function() {
		event.add(globalThis, { type: "resize", listener: resizeEvent, passive: true, pos: 1 });
		resizeable.subscribe(resizeableSubscriber);
	}
});

export default instance;

/**
 * @typedef {Object} module:wc/ui/positionable~setBySizeConfig
 * @property {number} [width] The width of the element being positioned. If not set then this is calculated.
 * @property {number} [height] The height of the element being positioned. If not set then this is calculated.
 * @property {number} [topOffsetPC] If set then the element is positioned such that the top of the element is below the top of the relative
 *   element/viewport by this much if this is less than 0 then the top of the positioned element will be above the relative element.
 * @property {number} [leftOffsetPC] If set then the element is positioned such that the left edge of the element is to the right of the left
 *   edge of relative element/viewport by this much. If this is less than 0 then the left of the positioned element will be left of the left
 *   edge of the relative element.
 */
