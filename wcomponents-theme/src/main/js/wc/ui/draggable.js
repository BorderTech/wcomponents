/**
 * Provides functionality used to move a component around the screen. Components may be moved using a mouse or keyboard.
 *
 * @typedef {Object} module:wc/ui/draggable.config() Optional module configuration
 * @property {int} step The number of pixels to move the draggable element per key press.
 * @default 6
 *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/classList
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
 */
define(["wc/dom/attribute",
		"wc/dom/classList",
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
		"module"],
	/** @param attribute wc/dom/attribute @param classList wc/dom/classList @param clearSelection wc/dom/clearSelection @param event wc/dom/event @param getMouseEventOffset wc/dom/getEventOffset @param isAcceptableEventTarget wc/dom/isAcceptableTarget @param getBox wc/dom/getBox @param initialise wc/dom/initialise @param shed wc/dom/shed @param uid wc/dom/uid @param Widget wc/dom/Widget @param has wc/has @param processResponse wc/ui/ajax/processResponse @param positionable wc/ui/positionable @param module @ignore */
	function(attribute, classList, clearSelection, event, getMouseEventOffset, isAcceptableEventTarget, getBox, initialise, shed, uid, Widget, has, processResponse, positionable, module) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/draggable~Draggable
		 * @private
		 */
		function Draggable() {
			var DRAGGABLE = new Widget("", "", {"data-wc-draggable": "true"}),
				DRAGGABLE_HAS_ANIMATION_CLASS = "wc_dragflow",
				CLASS_REMOVED_ATTRIB = "data_draggableremovedanimation",
				ns = "wc.ui.draggable",
				dragging,
				offsetX = {},
				offsetY = {},
				conf = module.config(),
				KEY_MOVE = ((conf && conf.step) ? conf.step : 6),  // the number of pixels by which a draggable is moved by keyboard
				BS = ns + ".inited",
				MM_EVENT = ns + ".move.inited";

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
				var result = element, targetId = element.getAttribute("data-wc-dragfor");
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

					if (classList.contains(moveTarget, DRAGGABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(moveTarget, DRAGGABLE_HAS_ANIMATION_CLASS);
						moveTarget.setAttribute(CLASS_REMOVED_ATTRIB, "true");
					}
				}
			}

			/**
			 * keydown event listener. Provides keyboard driven move using arrow keys when a move target (or its
			 * descendant) has focus.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keydownEvent($event) {
				var target = $event.target, element, result = false, x, y, keyCode = $event.keyCode, moveTarget, position;
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
					// this is the bit that does the key driven "drag"
					if ((x || y) && (moveTarget = getMoveTarget(element))) {
						x = x || 0;
						y = y || 0;
						position = getBox(moveTarget, true);
						positionable.reset(element, true);
						positionable.setPositionInView(moveTarget, position.left + x, position.top + y);
						result = true;
					}
				}
				if (result) {
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
					if (classList.contains(moveTarget, DRAGGABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(moveTarget, DRAGGABLE_HAS_ANIMATION_CLASS);
						moveTarget.setAttribute(CLASS_REMOVED_ATTRIB, "true");
					}
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
				var element;
				if (dragging && (element = document.getElementById(dragging)) && element.getAttribute(CLASS_REMOVED_ATTRIB) === "true") {
					classList.add(element, DRAGGABLE_HAS_ANIMATION_CLASS);
					element.removeAttribute(CLASS_REMOVED_ATTRIB);
				}
				dragging = null;
			}


			/**
			 * Moves an element as a helper for mousemove and touchmove.
			 *
			 *
			 * @param {Element} element The component to move.
			 * @param {float} x The amount to move the component on the x axis.
			 * @param {float} y the amount to move the component on the y axis.
			 */
			function moveTo(element, x, y) {
				var id = element.id,
					top = Math.max((y - offsetY[id]), 0),
					left = Math.max((x - offsetX[id]), 0);
				if (id) {
					top = y - offsetY[id];
					left = x - offsetX[id];
					if ((top || left)) {
						positionable.reset(element, true);
						positionable.setPositionInView(element, left, top);
						positionable.clearPositionBySize(id);
					}
					clearSelection();
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
					if (classList.contains(element, DRAGGABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(element, DRAGGABLE_HAS_ANIMATION_CLASS);
						element.setAttribute(CLASS_REMOVED_ATTRIB, "true");
					}
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
			 * These are some heavy duty event handlers so only wire them up if the draggable is available to drag.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element which is draggable.
			 */
			function bootstrap(element) {
				var body = document.body;
				if (!(attribute.get(element, BS) || shed.isHidden(element)) || shed.hasHiddenAncestor(element)) {
					attribute.set(element, BS, true);
					event.add(element, event.TYPE.mousedown, mousedownEvent);
					event.add(element, event.TYPE.keydown, keydownEvent);
					if (has("event-ontouchstart")) {
						event.add(element, event.TYPE.touchstart, touchstartEvent);
					}
				}
				if (!attribute.get(body, MM_EVENT)) {
					attribute.set(body, MM_EVENT, true);
					event.add(body, event.TYPE.mousemove, mousemoveEvent);

					if ("ontouchmove" in window) {
						event.add(body, event.TYPE.touchmove, touchmoveEvent);
					}
				}
			}

			/**
			 * {@link module:wc/ui/draggable~bootstrap} draggables on shed.SHOW or AJAX insert. NOTE: we are probably
			 * showing/inserting an ancestor of the actual draggable element.
			 *
			 * @function
			 * @private
			 * @param {Element} element the element being shown.
			 */
			function shedAjaxSubscriber(element) {
				if (element) {
					if (DRAGGABLE.isOneOfMe(element)) {
						bootstrap(element);
					}
					Array.prototype.forEach.call(DRAGGABLE.findDescendants(element), bootstrap);
				}
			}

			/**
			 * A subscriber to set up early event listeners.
			 *
			 * @function module:wc/ui/draggable.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.mouseup, mouseupTouchendTouchcancelEvent);
				if (has("event-ontouchend")) {
					event.add(element, event.TYPE.touchend, mouseupTouchendTouchcancelEvent);
				}
				if (has("event-ontouchcancel")) {
					event.add(document.body, event.TYPE.touchcancel, mouseupTouchendTouchcancelEvent);
				}
			};

			/**
			 * A subscriber to do late setup of event listeners on individual components and shed & ajax subscribers.
			 * @function module:wc/ui/draggable.postInit
			 */
			this.postInit = function() {
				Array.prototype.forEach.call(DRAGGABLE.findDescendants(document.body), bootstrap);
				shed.subscribe(shed.actions.SHOW, shedAjaxSubscriber);
				processResponse.subscribe(shedAjaxSubscriber, true);
			};
		}

		var /** @alias module:wc/ui/draggable */ instance = new Draggable();
		initialise.register(instance);
		return instance;
	});
