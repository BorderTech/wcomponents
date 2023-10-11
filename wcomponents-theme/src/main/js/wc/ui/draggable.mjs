import clearSelection from "wc/dom/clearSelection.mjs";
import event from "wc/dom/event.mjs";
import getMouseEventOffset from "wc/dom/getEventOffset.mjs";
import isAcceptableEventTarget from "wc/dom/isAcceptableTarget.mjs";
import getBox from "wc/dom/getBox.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import uid from "wc/dom/uid.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import positionable from "wc/ui/positionable.mjs";
import resizeable from "wc/ui/resizeable.mjs";
import wcconfig from "wc/config.mjs";

const TRUE = "true",
	DRAGGABLE = "[data-wc-draggable='true']",
	ns = "wc.ui.draggable",
	DRAGGABLE_ATTRIB = "data-wc-draggable",
	DRAG_FOR_ATTRIB = "data-wc-dragfor",
	offsetX = {},
	offsetY = {},
	conf = wcconfig.get("wc/ui/draggable", {
		step: 8  // the number of pixels by which a draggable is moved by keyboard
	}),
	BS = ns + ".inited";

let dragging;

const instance = {
	/**
	 * Make an element a drag control
	 *
	 * @function module:wc/ui/draggable.makeDraggable
	 * @public
	 * @param {Element} element The element which will be draggable.
	 * @param {String} [forId] The id of the element which is actually being controlled if it is not `element`.
	 */
	makeDraggable: function(element, forId) {
		element.setAttribute(DRAGGABLE_ATTRIB, TRUE);
		addRemoveEvents(element, false);
		if (forId) {
			element.setAttribute(DRAG_FOR_ATTRIB, forId);
		}
	},

	/**
	 * Make an element no longer draggable.
	 *
	 * @function module:wc/ui/draggable.clearDraggable
	 * @public
	 * @param {Element} element The element to change.
	 */
	clearDraggable: function(element) {
		element.removeAttribute(DRAGGABLE_ATTRIB);
		addRemoveEvents(element, true);
		element.removeAttribute(DRAG_FOR_ATTRIB);
	}
};

/**
 * We usually need to move a complex component but only want a sub-component to be the move handle. This
 * function gets a moveable component from an event target.
 *
 * @function
 * @private
 * @param {Element} element The target of an event which causes a move.
 * @returns {HTMLElement} The component we actually want to move.
 */
function getMoveTarget(element) {
	let result = element;
	const targetId = element.getAttribute(DRAG_FOR_ATTRIB);
	if (targetId) {
		result = document.getElementById(targetId);
	}
	return /** @type {HTMLElement}  */(result);
}

/**
 * Mousedown event listener. mousedown on a draggable target sets the start point for move.
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement}} $event The mousedown event.
 */
function mousedownEvent($event) {
	const { target, defaultPrevented } = $event;
	const element = defaultPrevented ? null : /** @type {HTMLElement}  */(target.closest(DRAGGABLE));
	const moveTarget =  element && isAcceptableEventTarget(element, target) ? getMoveTarget(element) : null;
	if (moveTarget) {
		const id = moveTarget.id || (moveTarget.id = uid());
		dragging = id;
		const offset = getMouseEventOffset($event);
		const position = getBox(moveTarget, true);
		offsetX[id] = offset.X - position.left;
		offsetY[id] = offset.Y - position.top;
	}
}

/**
 * Helper for the keydown event to move the "draggable" item.
 *
 * @param {Element} element The draggable element.
 * @param {number} x The amount to move in the x-axis.
 * @param {number} y The amount to move in the y-axis.
 * @returns {Boolean} true if the move is able to take place.
 */
function keydownHelper(element, x, y) {
	if (!(x || y)) {
		return false;
	}

	const moveTarget = getMoveTarget(element);
	if (!moveTarget) {
		return false;
	}
	let animationsDisabled;
	try {
		resizeable.disableAnimation(moveTarget); // do not animate key-bound move.
		animationsDisabled = true;
		x = x || 0;
		y = y || 0;
		const position = getBox(moveTarget, true);
		positionable.clearZeros(moveTarget, true);
		positionable.setPosition(moveTarget, position.left + x, position.top + y);
		return true;
	} finally {
		if (animationsDisabled) {
			resizeable.restoreAnimation(moveTarget);
		}
	}
}

/**
 * keydown event listener which provides keyboard driven move using arrow keys when a move target (or its
 * descendant) has focus.
 *
 * @function
 * @private
 * @param {KeyboardEvent & { target: HTMLElement }} $event The keydown event.
 */
function keydownEvent($event) {
	const target = $event.target;
	let x, y;
	const element = $event.defaultPrevented ? null : target.querySelector(DRAGGABLE);
	if (element) {
		switch ($event.key) {
			case "ArrowRight":
				x = conf.step;
				break;
			case "ArrowLeft":
				x = 0 - conf.step;
				break;
			case "ArrowDown":
				y = conf.step;
				break;
			case "ArrowUp":
				y = 0 - conf.step;
				break;
		}
	}
	if (keydownHelper(element, x, y)) {
		$event.preventDefault();
	}
}

/**
 * Sets initial position if move is initiated by a touch event.
 *
 * @function
 * @private
 * @param {TouchEvent} $event The touchstart event.
 */
function touchstartEvent($event) {
	if ($event.defaultPrevented || $event.touches.length !== 1) {
		return;
	}
	const touch = $event.touches[0];
	const target = /** @type {HTMLElement} */(touch.target);
	if (!target) {
		return;
	}
	const element = /** @type {HTMLElement} */(target.querySelector(DRAGGABLE));
	const moveTarget = (element && isAcceptableEventTarget(element, target)) ? getMoveTarget(element) : null;
	if (moveTarget) {
		const id = moveTarget.id || (moveTarget.id = uid());
		dragging = id;
		const position = getBox(moveTarget, true);
		offsetX[id] = touch.pageX - position.left;
		offsetY[id] = touch.pageY - position.top;
		$event.preventDefault();
	}
}

/**
 * Clear move setup on mouseup, touchend or touchcancel.
 *
 * @function
 * @private
 */
function mouseupTouchendTouchcancelEvent() {
	dragging = null;
}

/**
 * Moves an element as a helper for mousemove and touchmove.
 *
 * @function
 * @private
 * @param {HTMLElement} element The component to move.
 * @param {number} x The amount to move the component on the x-axis.
 * @param {number} y the amount to move the component on the y-axis.
 */
function moveTo(element, x, y) {
	const id = element.id;
	let animationsDisabled = false;
	try {
		if (!id) {
			return;
		}
		// NOTE: looks like these were being calculated then ignored. X and Y cannot be negative because
		// that would mean the drag/touch had gone out of viewport.
		// top = Math.max((y - offsetY[id]), 0),
		// left = Math.max((x - offsetX[id]), 0);

		const top = y - offsetY[id];
		const left = x - offsetX[id];

		if (top || left) {
			resizeable.disableAnimation(element);
			animationsDisabled = true;
			positionable.clearZeros(element, true);
			positionable.setPosition(element, left, top);
		}
	} finally {
		clearSelection();
		if (animationsDisabled) {
			resizeable.restoreAnimation(element);
		}
	}
}

/**
 * Undertake the move based on a mousemove event.
 *
 * @function
 * @private
 * @param {MouseEvent} $event The mousemove event.
 */
function mousemoveEvent($event) {
	if (!dragging || $event.defaultPrevented) {
		return;
	}
	const element = document.getElementById(dragging);
	if (element) {
		const offset = getMouseEventOffset($event);
		moveTo(element, offset.X, offset.Y);
	}
}

/**
 * Undertake the move based on a touchmove event.
 *
 * @function
 * @private
 * @param {TouchEvent} $event The touchmove event.
 */
function touchmoveEvent($event) {
	if (!dragging || $event.defaultPrevented || $event.changedTouches.length !== 1) {
		return;
	}
	const touch = $event.changedTouches[0];
	const element = touch ? document.getElementById(dragging) : null;
	if (element) {
		moveTo(element, touch.pageX, touch.pageY);
		$event.preventDefault();
	}
}

/**
 * Add and remove events from a draggable element.
 *
 * @function
 * @private
 * @param {Element} element The draggable element.
 * @param {boolean} remove If true then remove event listeners rather than adding them.
 */
function addRemoveEvents(element, remove) {
	let func = remove ? "remove" : "add";
	if (!remove && element[BS]) {
		return; // do not add more than once
	}
	try {
		event[func](element, "mousedown", mousedownEvent);
		event[func](element, "keydown", keydownEvent);
		event[func](element, "touchstart", touchstartEvent);
	} finally {
		element[BS] = !remove;
	}
}

/**
 * Add and remove drag actions on show/hide. NOTE: we are probably
 * showing/inserting an ancestor of the actual draggable element.
 *
 * @function
 * @private
 * @param {Element} element the element being shown.
 * @param {String} action The shed action SHOW or HIDE.
 */
function shedAjaxSubscriber(element, action) {
	if (element) {
		if (element.matches(DRAGGABLE)) {
			addRemoveEvents(element, false);
		}
		const draggables = element.querySelectorAll(DRAGGABLE);
		Array.from(draggables).forEach(next => addRemoveEvents(next, action === shed.actions.HIDE));
	}
}

/**
 * Provides functionality used to move a component around the screen. Components may be moved using a mouse or keyboard.
 *
 * @module
 */
initialise.register({
	/**
	 * A subscriber to set up early event listeners.
	 *
	 * @function module:wc/ui/draggable.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, "mouseup", mouseupTouchendTouchcancelEvent);
		event.add(element, "mousemove", mousemoveEvent);
		event.add(element, "touchmove", touchmoveEvent);
		event.add(element, "touchend", mouseupTouchendTouchcancelEvent);
		event.add(element, "touchcancel", mouseupTouchendTouchcancelEvent);
	},

	/**
	 * A subscriber to do late set up of event listeners on individual components and shed & ajax subscribers.
	 * @function module:wc/ui/draggable.postInit
	 */
	postInit: function() {
		const draggables = /** @type {Element[]} */(Array.from(document.body.querySelectorAll(DRAGGABLE)));
		draggables.forEach((el) => addRemoveEvents(el, false));
		shed.subscribe(shed.actions.SHOW, shedAjaxSubscriber);
		shed.subscribe(shed.actions.HIDE, shedAjaxSubscriber);
		processResponse.subscribe(shedAjaxSubscriber, true);
	}
});

/**
 * @typedef {Object} module:wc/ui/draggable~config Optional module configuration
 * @property {number} step The number of pixels to move the draggable element per key press.
 * @default 8
 */

export default instance;
