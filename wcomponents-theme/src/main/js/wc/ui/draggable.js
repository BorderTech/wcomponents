/**
 * Provides functionality used to move a component around the screen. Components may be moved using a mouse or keyboard.
 *
 * @typedef {Object} module:wc/ui/draggable.config() Optional module configuration
 * @property {int} step The number of pixels to move the draggable element per key press.
 * @default 8
 *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/clearSelection
 * @requires module:wc/dom/event
 * @requires module:wc/dom/getEventOffset
 * @requires module:wc/dom/isAcceptableTarget
 * @requires module:wc/dom/getBox
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/Widget
 * @requires module:wc/has
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/positionable
 * @requires module:wc/ui/resizeable
 * @requires module:wc/config
 */
define(["wc/dom/attribute",
		"wc/dom/clearSelection",
		"wc/dom/event",
		"wc/dom/getEventOffset",
		"wc/dom/isAcceptableTarget",
		"wc/dom/getBox",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/has",
		"wc/ui/ajax/processResponse",
		"wc/ui/positionable",
		"wc/ui/resizeable",
		"wc/config"],
	/** @param attribute wc/dom/attribute @param classList wc/dom/classList @param clearSelection wc/dom/clearSelection @param event wc/dom/event @param getMouseEventOffset wc/dom/getEventOffset @param isAcceptableEventTarget wc/dom/isAcceptableTarget @param getBox wc/dom/getBox @param initialise wc/dom/initialise @param shed wc/dom/shed @param uid wc/dom/uid @param Widget wc/dom/Widget @param has wc/has @param processResponse wc/ui/ajax/processResponse @param positionable wc/ui/positionable @param wcconfig wc/config @ignore */
	function(attribute, clearSelection, event, getMouseEventOffset, isAcceptableEventTarget, getBox, initialise, shed, uid, Widget, has, processResponse, positionable, resizeable, wcconfig) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/draggable~Draggable
		 * @private
		 */
		function Draggable() {
			var TRUE = "true",
				DRAGGABLE = new Widget("", "", {"data-wc-draggable": TRUE}),
				ns = "wc.ui.draggable",
				DRAGGABLE_ATTRIB = "data-wc-draggable",
				DRAG_FOR_ATTRIB = "data-wc-dragfor",
				dragging,
				offsetX = {},
				offsetY = {},
				conf = wcconfig.get("wc/ui/draggable"),
				KEY_MOVE = ((conf && conf.step) ? conf.step : 8),  // the number of pixels by which a draggable is moved by keyboard
				BS = ns + ".inited";

			/**
			 * We usually need to move a complex component but only want a sub-component to be the move handle. This
			 * function gets a moveable component from an event target.
			 *
			 * @function
			 * @private
			 * @param {Element} element The target of an event which causes a move.
			 * @returns {Element} The component we actually want to move.
			 */
			function getMoveTarget(element) {
				var result = element, targetId = element.getAttribute(DRAG_FOR_ATTRIB);
				if (targetId) {
					result = document.getElementById(targetId);
				}
				return result;
			}

			/**
			 * Mousedown event listener. mousedown on a draggable target sets the start point for move.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The mousedown event.
			 */
			function mousedownEvent($event) {
				var target = $event.target, element, position, id, offset, moveTarget;
				if (!$event.defaultPrevented && (element = DRAGGABLE.findAncestor(target)) && isAcceptableEventTarget(element, target) && (moveTarget = getMoveTarget(element))) {
					id = moveTarget.id || (moveTarget.id = uid());
					dragging = id;
					offset = getMouseEventOffset($event);
					position = getBox(moveTarget, true);
					offsetX[id] = offset.X - position.left;
					offsetY[id] = offset.Y - position.top;
				}
			}

			/**
			 * Helper for the keydown event to move the "draggable" item.
			 *
			 * @param {Element} element The draggable element.
			 * @param {int} x The amount to move in the x axis.
			 * @param {int} y The amount to move in the y axis.
			 * @returns {Boolean} true if the move is able to take place.
			 */
			function keydownHelper(element, x, y) {
				var moveTarget,
					position,
					animationsDisabled;

				if (!(x || y)) {
					return false;
				}

				moveTarget = getMoveTarget(element);
				if (!moveTarget) {
					return false;
				}

				try {
					resizeable.disableAnimation(moveTarget); // do not animate key-bound move.
					animationsDisabled = true;
					x = x || 0;
					y = y || 0;
					position = getBox(moveTarget, true);
					positionable.clearZeros(moveTarget, true);
					positionable.setPositionInView(moveTarget, position.left + x, position.top + y);
					return true;
				}
				finally {
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
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var target = $event.target,
					element,
					x,
					y,
					keyCode = $event.keyCode;
				if (!$event.defaultPrevented && (element = DRAGGABLE.findAncestor(target))) {
					switch (keyCode) {
						case KeyEvent.DOM_VK_RIGHT:
							x = KEY_MOVE;
							break;
						case KeyEvent.DOM_VK_LEFT:
							x = 0 - KEY_MOVE;
							break;
						case KeyEvent.DOM_VK_DOWN:
							y = KEY_MOVE;
							break;
						case KeyEvent.DOM_VK_UP:
							y = 0 - KEY_MOVE;
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
			 * @param {Event} $event The touchstart event.
			 */
			function touchstartEvent($event) {
				var touch, target, position, element, id, moveTarget;
				if ($event.defaultPrevented || $event.touches.length !== 1) {
					return;
				}

				touch = $event.touches[0];
				target = touch.target;

				if (!target) {
					return;
				}

				if ((element = DRAGGABLE.findAncestor(target)) && isAcceptableEventTarget(element, target) && (moveTarget = getMoveTarget(element))) {
					id = moveTarget.id || (moveTarget.id = uid());
					dragging = id;
					position = getBox(moveTarget, true);
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
			 * @param {Element} element The component to move.
			 * @param {float} x The amount to move the component on the x axis.
			 * @param {float} y the amount to move the component on the y axis.
			 */
			function moveTo(element, x, y) {
				var id = element.id,
					top,
					left,
					animationsDisabled;

				try {
					if (!id) {
						return;
					}
					// NOTE: looks like these were being calculated then ignored. X and Y cannot be negative because
					// that would mean the drag/touch had gone out of viewport.
					// top = Math.max((y - offsetY[id]), 0),
					// left = Math.max((x - offsetX[id]), 0);

					top = y - offsetY[id];
					left = x - offsetX[id];

					if (top || left) {
						resizeable.disableAnimation(element);
						animationsDisabled = true;
						positionable.clearZeros(element, true);
						positionable.setPositionInView(element, left, top);
						positionable.clearPositionBySize(id);
					}
				}
				finally {
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
			 * @param {Event} $event The mousemove event.
			 */
			function mousemoveEvent($event) {
				var element, offset;
				if (!dragging || $event.defaultPrevented) {
					return;
				}

				if ((element = document.getElementById(dragging))) {
					offset = getMouseEventOffset($event);
					moveTo(element, offset.X, offset.Y);
				}
			}

			/**
			 * Undertake the move based on a touchmove event.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The touchmove event.
			 */
			function touchmoveEvent($event) {
				var touch, element;
				if (!dragging || $event.defaultPrevented || $event.changedTouches.length !== 1) {
					return;
				}

				if ((touch = $event.changedTouches[0]) && (element = document.getElementById(dragging))) {
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
				var func = remove ? "remove" : "add";

				if (!remove && attribute.get(element, BS)) {
					return; // do not add more than once
				}
				try {
					event[func](element, event.TYPE.mousedown, mousedownEvent);
					event[func](element, event.TYPE.keydown, keydownEvent);
					if (has("event-ontouchstart")) {
						event[func](element, event.TYPE.touchstart, touchstartEvent);
					}
				}
				finally {
					func = remove ? "remove" : "set";
					attribute[func](element, BS, true);
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
					if (DRAGGABLE.isOneOfMe(element)) {
						addRemoveEvents(element);
					}
					Array.prototype.forEach.call(DRAGGABLE.findDescendants(element), function (next) {
						addRemoveEvents(next, action === shed.actions.HIDE);
					});
				}
			}

			/**
			 * Make an element a drag control
			 *
			 * @function module:wc/ui/draggable.makeDraggable
			 * @public
			 * @param {Element} element The element which will be draggable.
			 * @param {String} [forId] The id of the element which is actually being controlled if it is not `element`.
			 */
			this.makeDraggable = function(element, forId) {
				element.setAttribute(DRAGGABLE_ATTRIB, TRUE);
				addRemoveEvents(element);
				if (forId) {
					element.setAttribute(DRAG_FOR_ATTRIB, forId);
				}
			};

			/**
			 * Make an element no longer draggable.
			 *
			 * @function module:wc/ui/draggable.clearDraggable
			 * @public
			 * @param {Element} element The element to change.
			 */
			this.clearDraggable = function(element) {
				element.removeAttribute(DRAGGABLE_ATTRIB);
				addRemoveEvents(element, true);
				element.removeAttribute(DRAG_FOR_ATTRIB);
			};

			/**
			 * A subscriber to set up early event listeners.
			 *
			 * @function module:wc/ui/draggable.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.mouseup, mouseupTouchendTouchcancelEvent);
				event.add(element, event.TYPE.mousemove, mousemoveEvent);

				if (has("event-ontouchmove")) {
					event.add(element, event.TYPE.touchmove, touchmoveEvent);
				}
				if (has("event-ontouchend")) {
					event.add(element, event.TYPE.touchend, mouseupTouchendTouchcancelEvent);
				}
				if (has("event-ontouchcancel")) {
					event.add(element, event.TYPE.touchcancel, mouseupTouchendTouchcancelEvent);
				}
			};

			/**
			 * A subscriber to do late setup of event listeners on individual components and shed & ajax subscribers.
			 * @function module:wc/ui/draggable.postInit
			 */
			this.postInit = function() {
				Array.prototype.forEach.call(DRAGGABLE.findDescendants(document.body), addRemoveEvents);
				shed.subscribe(shed.actions.SHOW, shedAjaxSubscriber);
				shed.subscribe(shed.actions.HIDE, shedAjaxSubscriber);
				processResponse.subscribe(shedAjaxSubscriber, true);
			};
		}

		var /** @alias module:wc/ui/draggable */ instance = new Draggable();
		initialise.register(instance);
		return instance;
	});
