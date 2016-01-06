/**
 * Provides functionality to implement a resizeable component.
 *
 * @typedef {Object} module:wc/ui.resizeable.config() Optional module configuration.
 * @property {?int} min The minimum size, in px, any element is allowed to be.
 * @default 0
 * @proprty {?int} step The number of pixels to increase/decrease per keypress when resizing with the arrow keys.
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
 * @requires module:wc/dom/getStyle
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/Widget
 * @requires module:wc/has
 * @requires module:wc/ui/ajax/processResponse
 *
 * @todo check source order.
 */
define(["wc/dom/attribute",
		"wc/dom/classList",
		"wc/dom/clearSelection",
		"wc/dom/event",
		"wc/dom/getEventOffset",
		"wc/dom/isAcceptableTarget",
		"wc/dom/getBox",
		"wc/dom/getStyle",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/has",
		"wc/ui/ajax/processResponse",
		"module"],
	/** @param attribute wc/dom/attribute @param classList wc/dom/classList @param clearSelection wc/dom/clearSelection @param event wc/dom/event @param getMouseEventOffset wc/dom/getEventOffset @param isAcceptableTarget wc/dom/isAcceptableTarget @param getBox wc/dom/getBox @param getStyle wc/dom/getStyle @param initialise wc/dom/initialise @param shed wc/dom/shed @param uid wc/dom/uid @param Widget wc/dom/Widget @param has wc/has @param processResponse wc/ui/ajax/processResponse @param module @ignore */
	function(attribute, classList, clearSelection, event, getMouseEventOffset, isAcceptableTarget, getBox, getStyle, initialise, shed, uid, Widget, has, processResponse, module) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/resizeable~Resizeable
		 * @private
		 */
		function Resizeable() {
			var RESIZE = new Widget("button", "wc_resize"),
				CLASS_MAX_CONTROL = "wc_maxcont",
				MAX = new Widget("button", CLASS_MAX_CONTROL),
				MAX_BAR = new Widget("", CLASS_MAX_CONTROL),
				RESIZEABLE_HAS_ANIMATION_CLASS = "wc_resizeflow",
				CLASS_REMOVED_ATTRIB = "data-wc-resizeableremovedanimation",
				CLASS_MAX = "wc_max",
				conf = module.config(),
				MIN_SIZE = ((conf && conf.min) ? conf.min : 0), // set this to any sensible size but will cause errors in IE if < 0
				resizing,
				offsetX = {},
				offsetY = {},
				UNIT = "px",
				KEY_RESIZE = ((conf && conf.step) ? conf.step : 6),  // the number of pixels by which a resizable is resized by keyboard
				ns = "wc.ui.resizeable",
				BS = ns + ".inited",
				MM_EVENT = ns + ".move.inited",
				TRUE = "true",
				FONT_SIZE;


			/**
			 * Get the default font size of the BODY element in pixels.
			 * @function
			 * @private
			 * @returns {number} The font size in pixels.
			 */
			function getFontSize() {
				var css = "fontSize",
					d = document,
					v = d.defaultView,
					needUnits = !(v && v.getComputedStyle),
					size = getStyle(d.body, css, needUnits, true) || 0,
					_s;

				if (isNaN(size)) {
					_s = parseFloat(size);
					if (size.indexOf(UNIT)) {
						return _s;
					}
					// IE8 will return the style rule eg 75% or 0.75em or even 12pt if you are silly.
					else if (size.indexOf("%")) {
						return (16 * _s / 100);
					}
					else if (size.indexOf("em")) {
						return (16 * _s);
					}
					// if you are going to set your default font size in points, picas or exes you deserve what you get
					return _s;
				}
				return size;
			}

			/**
			 * Usually we set a sub-control of a complete component to be an event
			 * target for resizing (such as a resize handle or a max/restore button.
			 * This helper finds the component we acrually want to resize based on such
			 * a control.
			 * @function
			 * @private
			 * @param {Element} element A resize event target.
			 * @returns {Element} The resizeable component.
			 */
			function getResizeTarget(element) {
				var result = element, targetId = element.getAttribute("data-wc-resize");
				if (targetId) {
					result = document.getElementById(targetId);
				}
				return result;
			}

			/**
			 * Convert a (size related) CSS style rule to pixels.
			 * @function
			 * @private
			 * @param {String} size The rule to convert.
			 * @returns {float} the number value of the CSS rule.
			 */
			function styleToPx(size) {
				var _size = size;
				FONT_SIZE = FONT_SIZE || getFontSize();

				if (_size && isNaN(_size)) {
					if (_size.indexOf(UNIT) > -1) {
						_size = parseInt(_size, 10);
					}
					else {
						// someone specified the size in ems or maybe even points, but we will guess ems and getStyle() returned that style
						_size = Math.round(parseFloat(_size) * FONT_SIZE);
					}
				}
				return _size;
			}

			/**
			 * Get the width and height of an element.
			 * @function
			 * @private
			 * @param {Element} element the resizeable component.
			 * @returns {Object} a POJSO with properties {float} width and {float} height.
			 */
			function getSize(element) {
				var height = element.style.height,
					width = element.style.width,
					box = getBox(element);
				height = height ? parseFloat(height.replace(UNIT, "")) : box.height;
				width = width ? parseFloat(width.replace(UNIT, "")) : box.width;
				return {"width": width, "height": height};
			}

			/**
			 * Get the min-height or min-width style of an element.
			 * @function
			 * @private
			 * @param {Element} element The element we are investigating for min-height/width.
			 * @param {boolean} [isHeight] Indicates we should get the min-height, otherwise we get min-width.
			 * @returns The style as a string or number.
			 */
			function getSizeContraint(element, isHeight) {
				var css = "min-" + (isHeight ? "height" : "width"),
					needUnits = !(document.defaultView && document.defaultView.getComputedStyle);
				return getStyle(element, css, needUnits, true) || 0;
			}

			/**
			 * Changes the component size.
			 * @function
			 * @private
			 * @param {Element} element the resizeable component
			 * @param {float} deltaX change in width
			 * @param {float} deltaY change in height
			 */
			function resize(element, deltaX, deltaY) {
				var box, min;
				if (element && (box = getSize(element))) {
					min = getSizeContraint(element);
					min = min ? styleToPx(min) : MIN_SIZE;
					element.style.width = Math.max(box.width + deltaX, min) + UNIT;

					min = getSizeContraint(element, true);
					min = min ? styleToPx(min) : MIN_SIZE;
					element.style.height = Math.max(box.height + deltaY, min) + UNIT;

					clearSelection();
				}
			}

			/**
			 * Mousedown event handler for determining start position for drag initiated resize.
			 * @function
			 * @private
			 * @param {event} $event The mousedown event.
			 */
			function mousedownEvent($event) {
				var target = $event.target, element, id, offset, resizeTarget;
				if (!$event.defaultPrevented && (element = RESIZE.findAncestor(target)) && isAcceptableTarget(element, target) && (resizeTarget = getResizeTarget(element))) {
					id = resizeTarget.id || (resizeTarget.id = uid());

					if (classList.contains(resizeTarget, RESIZEABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(resizeTarget, RESIZEABLE_HAS_ANIMATION_CLASS);
						resizeTarget.setAttribute(CLASS_REMOVED_ATTRIB, TRUE);
					}
					offset = getMouseEventOffset($event);
					resizing = id;
					offsetY[id] = offset.Y;
					offsetX[id] = offset.X;
				}
			}

			/**
			 * touchstart event handler for determining start position for drag initiated resize on touch devices.
			 * @function
			 * @private
			 * @param {event} $event A touchstart event.
			 */
			function touchstartEvent($event) {
				var touch, target, element, id, resizeTarget;
				if (!$event.defaultPrevented && $event.touches.length === 1 && (touch = $event.touches[0]) && (target = touch.target) &&
					(element = RESIZE.findAncestor(target)) && isAcceptableTarget(element, target) && (resizeTarget = getResizeTarget(element))) {
					id = resizeTarget.id || (resizeTarget.id = uid());
					resizing = id;
					if (classList.contains(resizeTarget, RESIZEABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(resizeTarget, RESIZEABLE_HAS_ANIMATION_CLASS);
						resizeTarget.setAttribute(CLASS_REMOVED_ATTRIB, TRUE);
					}
					offsetX[id] = touch.pageX;
					offsetY[id] = touch.pageY;
					$event.preventDefault();
				}
			}


			/**
			 * keydown event handler for keyboard driven resize.
			 * @function
			 * @private
			 * @param {event} $event A keydown event
			 */
			function keydownEvent($event) {
				var target = $event.target,
					element,
					result = false,
					x,
					y,
					keyCode = $event.keyCode,
					resizeTarget;
				if (!$event.defaultPrevented && (element = RESIZE.findAncestor(target)) && (resizeTarget = getResizeTarget(element))) {
					switch (keyCode) {
						case KeyEvent.DOM_VK_RIGHT:
							x = KEY_RESIZE;
							break;
						case KeyEvent.DOM_VK_LEFT:
							x = 0 - KEY_RESIZE;
							break;
						case KeyEvent.DOM_VK_DOWN:
							y = KEY_RESIZE;
							break;
						case KeyEvent.DOM_VK_UP:
							y = 0 - KEY_RESIZE;
							break;
					}
					// this is the bit that does the key driven "drag"
					if (x || y) {
						resize(resizeTarget, x || 0, y || 0);
						result = true;
					}
				}
				if (result) {
					$event.preventDefault();
				}
			}


			/**
			 * Helper function for touch and mouse driven resizing of a component.
			 * @function
			 * @private
			 * @param {Element} element the resizeable DOM element
			 * @param {int} x the x coordinate of the move event
			 * @param {int} y the y coord of the move event
			 */
			function resizeHandleHelper(element, x, y) {
				var id = element.id,
					deltaX = x - offsetX[id],
					deltaY = y - offsetY[id];
				if (element && (deltaX || deltaY)) {
					resize(element, deltaX, deltaY);
				}
				offsetX[id] = x;
				offsetY[id] = y;
				clearSelection();
			}

			/**
			 * mousemove event handler for mouse driven resize. NOTE: we do not use drag events due to user agent
			 * implementation issues.
			 * @function
			 * @private
			 * @param {event} $event A mousemove event.
			 */
			function mousemoveEvent($event) {
				var element, offset;
				if (resizing && !$event.defaultPrevented && (element = document.getElementById(resizing))) {
					if (classList.contains(element, RESIZEABLE_HAS_ANIMATION_CLASS)) {
						classList.remove(element, RESIZEABLE_HAS_ANIMATION_CLASS);
						element.setAttribute(CLASS_REMOVED_ATTRIB, TRUE);
					}
					offset = getMouseEventOffset($event);
					resizeHandleHelper(element, offset.X, offset.Y);
				}
			}

			/**
			 * touchmove event handler for touch driven resize.
			 * @function
			 * @private
			 * @param {event} $event A touchmove event.
			 */
			function touchmoveEvent($event) {
				var touch, element;
				if (resizing && !$event.defaultPrevented && $event.changedTouches.length === 1 && (touch = $event.changedTouches[0]) &&
						touch.target && (element = document.getElementById(resizing))) {
					resizeHandleHelper(element, touch.pageX, touch.pageY);
					$event.preventDefault();
				}
			}

			/**
			 * Click event handler for clicks on a max/restore button.
			 * @function
			 * @private
			 * @param {event} $event A  click event.
			 */
			function clickEvent($event) {
				var target = $event.target, element;
				if (!$event.defaultPrevented && (element = MAX.findAncestor(target))) {
					shed.toggle(element, shed.actions.SELECT);
					$event.preventDefault();  // prevent double click invocation by over-exuberant users.
				}
			}

			/**
			 * Double-click event handler for clicks on a max/restore header bar.
			 * @function
			 * @private
			 * @param {event} $event A double-click event.
			 */
			function doubleClickEvent($event) {
				var target = $event.target, bar, button;
				if (!$event.defaultPrevented && (bar = MAX_BAR.findAncestor(target)) && isAcceptableTarget(bar, target) && (button = MAX.findDescendant(bar))) {
					shed.toggle(button, shed.actions.SELECT);
					clearSelection();
					$event.preventDefault();
				}
			}

			/**
			 * mouseup, touchend and touchcancel event handler to replace the resize animation class if required.
			 * @function
			 * @private
			 */
			function mouseupTouchendTouchcancelEvent() {
				var element;
				if (resizing && (element = document.getElementById(resizing)) && element.getAttribute(CLASS_REMOVED_ATTRIB) === TRUE) {
					classList.add(element, RESIZEABLE_HAS_ANIMATION_CLASS);
					element.removeAttribute(CLASS_REMOVED_ATTRIB);
				}
				resizing = null;
			}

			/**
			 * initialise subscriber to attach event handlers.
			 * @function
			 * @public
			 * @param {Element} element The element being initialised, usually document.body
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.mouseup, mouseupTouchendTouchcancelEvent);
				event.add(element, event.TYPE.click, clickEvent);
				event.add(element, event.TYPE.dblclick, doubleClickEvent);

				if (has("event-ontouchend")) {
					event.add(element, event.TYPE.touchend, mouseupTouchendTouchcancelEvent);
				}

				if (has("event-ontouchcancel")) {
					event.add(document.body, event.TYPE.touchcancel, mouseupTouchendTouchcancelEvent);
				}
			};


			/**
			 * Adds event listeners to a resize handle and a mousemove event on the document body when the first resize
			 * handle is present and visible.
			 * @function
			 * @private
			 * @param {Element} element A resize handle.
			 */
			function bootstrap(element) {
				var body = document.body;
				if (!(attribute.get(element, BS) || shed.isHidden(element) || shed.hasHiddenAncestor(element))) {
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

					if (has("event-ontouchmove")) {
						event.add(body, event.TYPE.touchmove, touchmoveEvent);
					}
				}
			}

			/**
			 * bootstrap resize handles on shed.SHOW or AJAX insert. NOTE: we are probably showing/inserting an ancestor
			 * of the actual resize handle.
			 * @function
			 * @private
			 * @param {Element} element The element being shown/inserted.
			 */
			function shedAjaxSubscriber(element) {
				if (element) {
					if (RESIZE.isOneOfMe(element)) {
						bootstrap(element);
					}
					else {
						Array.prototype.forEach.call(RESIZE.findDescendants(element), bootstrap);
					}
				}
			}

			/**
			 * Provides the max/restore functionality by toggling a class on the resize target.
			 * @function
			 * @private
			 * @param {Element} element A dom node, we are only interested in max/restore buttons.
			 * @param {String} action The shed action:  shed.actions.SELECT or shed.actions.DESELECT.
			 */
			function shedSelectSubscriber(element, action) {
				var target;
				if (element && MAX.isOneOfMe(element) && (target = getResizeTarget(element))) {
					classList[(action === shed.actions.SELECT ? "add" : "remove")](target, CLASS_MAX);
				}
			}

			/**
			 * Get the widget which describes the component.
			 * @function module:wc/ui/resizeable.getWidget
			 * @returns {Object} A POJSO with {@link module:wc/dom/Widget} "handle" and {@link module:wc/dom/Widget} "maximise"
			 */
			this.getWidget = function() {
				return {"handle": RESIZE, "maximise": MAX};
			};

			/**
			 * Makes a given element into a double-click enabled maximise bar.
			 * @function module:wc/ui/resizeable.setMaxBar
			 * @param {Element} element The element we wish to change.
			 */
			this.setMaxBar = function(element) {
				classList.add(element, CLASS_MAX_CONTROL);
			};

			/**
			 * Removes double-click enabled maximise bar functionality from a given element.
			 * @function module:wc/ui/resizeable.clearMaxBar
			 * @param {Element} element The element we wish to change.
			 */
			this.clearMaxBar = function(element) {
				classList.remove(element, CLASS_MAX_CONTROL);
			};

			/**
			 * Late initialisation for ajax and shed subscribers.
			 * @function module:wc/ui/resizeable.postInit
			 * @public
			 */
			this.postInit = function() {
				Array.prototype.forEach.call(RESIZE.findDescendants(document.body), bootstrap);
				shed.subscribe(shed.actions.SHOW, shedAjaxSubscriber);
				shed.subscribe(shed.actions.SELECT, shedSelectSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSelectSubscriber);
				processResponse.subscribe(shedAjaxSubscriber, true);
			};
		}

		var /** @alias module:wc/ui/resizeable */ instance = new Resizeable();
		initialise.register(instance);
		return instance;
	}
);
