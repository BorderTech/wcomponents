/**
 * Provides functionality to implement a resizeable component.
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
 * @requires module:wc/Observer
 * @requires module:wc/timers
 * @requires module:wc/config
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
		"wc/Observer",
		"wc/timers",
		"wc/config"],
	/** @param attribute @param classList @param clearSelection @param event @param getMouseEventOffset @param isAcceptableTarget @param getBox @param getStyle @param initialise @param shed @param uid @param Widget @param has @param processResponse @param Observer @param timers @param wcconfig @ignore */
	function(attribute, classList, clearSelection, event, getMouseEventOffset, isAcceptableTarget, getBox, getStyle,
		initialise, shed, uid, Widget, has, processResponse, Observer, timers, wcconfig) {

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
				conf = wcconfig.get("wc/ui/resizeable"),
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
				FONT_SIZE,
				observer,
				notifyTimer,
				/**
				 * @var {number} notifyTimeout The delay between resizing and notifying the resize observers. This can
				 * be small but is handy to prevent continual notification during dragging.
				 * @private
				 */
				notifyTimeout = ((conf && conf.delay) ? conf.delay : 100),
				STORED_SIZE_ATTRIB = "data-wc-storedsize";

			/**
			 * In which direction can the element be resized?
			 *
			 * @function
			 * @private
			 * @param {Element} element The resizeable component.
			 * @returns {String} Values are "v" for only vertical, "h" for only horizontal or "" for both.
			 */
			function getAllowedDirections(element) {
				return element.getAttribute("data-wc-resizedirection");
			}

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
						return parseInt(_size, 10);
					}
					// someone specified the size in ems or maybe even points, but we will guess ems and getStyle() returned that style
					_size = Math.round(parseFloat(_size) * FONT_SIZE);
				}
				if (isNaN(_size)) {
					return 0;
				}
				return _size;
			}

			/**
			 * Get the width and height of an element.
			 *
			 * If we are getting the "native" size it is without inline styles. This is usually because we need to work
			 * out how big "auto" or "fit-content" is.
			 *
			 * @function
			 * @private
			 * @param {Element} element the resizeable component.
			 * @param {Boolean} native If true remove any inline styles before calculating the size. If a min/max
			 *    width/height is "auto" or one of the "-content" settings (eg fit-content, -moz-max-content etc) then
			 *    we need to make a guess at the native box size in pixels. This is a bit experimental.
			 * @returns {Object} a POJSO with properties {float} width and {float} height.
			 */
			function getSize(element, native) {
				var height = element.style.height,
					width = element.style.width,
					_width, _height,
					box;

				try {
					if (native) {
						_width = width;
						_height = height;
						element.style.width = "";
						element.style.height = "";
						width = 0;
						height = 0;
					}
					box = getBox(element);
					height = height ? parseFloat(height.replace(UNIT, "")) : box.height;
					width = width ? parseFloat(width.replace(UNIT, "")) : box.width;

					return {width: width, height: height};
				}
				finally {
					if (_width) {
						element.style.width = _width;
					}
					if (_height) {
						element.style.height = _height;
					}
				}
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
					needUnits = !(document.defaultView && document.defaultView.getComputedStyle),
					result = getStyle(element, css, needUnits, true) || 0,
					box;

				if (isNaN(result)) {
					// we have something like auto or fit-content.
					if (result === "auto" || result.indexOf("-content")) {
						box = getSize(element, true);
						return isHeight ? parseFloat(box.height) : parseFloat(box.width);
					}
					return 0;
				}
				return result;
			}

			/**
			 * Changes the component size.
			 * @function
			 * @private
			 * @param {Element} element The resizeable component.
			 * @param {float} deltaX Change in width in pixels.
			 * @param {float} deltaY Change in height in pixels.
			 * @param {boolean} [notify] If true notify subscribers from here. This would usually be done in an
			 * `end-of-event` handler like mouseup or touchend.
			 */
			function resize(element, deltaX, deltaY, notify) {
				var box, min, _notify, width, height;
				try {
					if (element && (box = getSize(element))) {
						if (deltaX) {
							min = getSizeContraint(element);
							min = min ? styleToPx(min) : MIN_SIZE;
							width = Math.round(Math.max(box.width + deltaX, min));
							if (width > min && width !== parseInt(element.style.width)) {
								element.style.width = width + UNIT;
								_notify = true;
							}
						}
						if (deltaY) {
							min = getSizeContraint(element, true);
							min = min ? styleToPx(min) : MIN_SIZE;
							height = Math.round(Math.max(box.height + deltaY, min));
							if (height > min && height !== parseInt(element.style.height)) {
								element.style.height = height + UNIT;
								_notify = true;
							}
						}
					}
				}
				finally {
					clearSelection();
					if (notify && _notify && observer) {
						if (notifyTimer) {
							timers.clearTimeout(notifyTimer);
							notifyTimer = null;
						}
						notifyTimer = timers.setTimeout(observer.notify, notifyTimeout, element);
					}
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

					instance.disableAnimation(resizeTarget);
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
					instance.disableAnimation(resizeTarget);
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
					x = 0,
					y = 0,
					keyCode = $event.keyCode,
					resizeTarget,
					allowed;

				if ($event.defaultPrevented) {
					return;
				}

				if (!(element = RESIZE.findAncestor(target))) {
					return;
				}

				if (!(resizeTarget = getResizeTarget(element))) {
					return;
				}

				allowed =  getAllowedDirections(resizeTarget);
				switch (keyCode) {
					case KeyEvent.DOM_VK_RIGHT:
						if (allowed !== "v") {
							x = KEY_RESIZE;
						}
						break;
					case KeyEvent.DOM_VK_LEFT:
						if (allowed !== "v") {
							x = 0 - KEY_RESIZE;
						}
						break;
					case KeyEvent.DOM_VK_DOWN:
						if (allowed !== "h") {
							y = KEY_RESIZE;
						}
						break;
					case KeyEvent.DOM_VK_UP:
						if (allowed !== "h") {
							y = 0 - KEY_RESIZE;
						}
						break;
				}
				// this is the bit that does the key driven "drag"
				if (x || y) {
					resize(resizeTarget, x , y, true);
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
					allowed = getAllowedDirections(element),
					deltaX = allowed === "v" ? 0 : x - offsetX[id],
					deltaY = allowed === "h" ? 0 : y - offsetY[id];

				if (element && (deltaX || deltaY)) {
					resize(element, deltaX, deltaY);
				}
				offsetX[id] = x;
				offsetY[id] = y;
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
					instance.disableAnimation(element);
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
				if (resizing && (element = document.getElementById(resizing))) {
					if (observer) {
						if (notifyTimer) {
							timers.clearTimeout(notifyTimer);
							notifyTimer = null;
						}
						notifyTimer = timers.setTimeout(observer.notify, notifyTimeout, element);
					}
					instance.restoreAnimation(element);
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
			 * @public
			 * @returns {Object} A POJSO with {@link module:wc/dom/Widget} "handle" and {@link module:wc/dom/Widget} "maximise"
			 */
			this.getWidget = function() {
				return {"handle": RESIZE, "maximise": MAX};
			};

			/**
			 * Makes a given element into a double-click enabled maximise bar.
			 * @function module:wc/ui/resizeable.setMaxBar
			 * @public
			 * @param {Element} element The element we wish to change.
			 */
			this.setMaxBar = function(element) {
				classList.add(element, CLASS_MAX_CONTROL);
			};

			/**
			 * Removes double-click enabled maximise bar functionality from a given element.
			 * @function module:wc/ui/resizeable.clearMaxBar
			 * @public
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

			/**
			 * Allows a component to subscribe to resizing.
			 * @function module:wc/ui/resizeable.subscribe
			 * @see {@link module:wc/Observer#subscribe}
			 *
			 * @param {Function} subscriber The function that will be notified. This function MUST be present at
			 *    "publish" time, but need not be preset at "subscribe" time.
			 * @returns {?Function} A reference to the subscriber.
			 */
			this.subscribe = function(subscriber) {
				function _subscribe(_subscriber) {
					return observer.subscribe(_subscriber);
				}

				if (!observer) {
					observer = new Observer();
					this.subscribe = _subscribe;
				}
				return _subscribe(subscriber);
			};

			/**
			 * Get the target component being resized.
			 * @function module:wc/ui/resizeable.getTarget
			 * @alias module:wc/ui/resizeable.getTarget
			 * @public
			 * @param {Element} element The resize handle.
			 */
			this.getTarget = getResizeTarget;

			/**
			 * Remove size from the target of a resize control and optionally store the old size for later re-use.
			 *
			 * @function module:wc/ui/resizeable.clearSize
			 * @public
			 * @public
			 * @param {Element} element The resize handle.
			 * @param {boolean} keep If true store the size for later use.
			 * @return {Boolean} true if a resizeable target was found and reset.
			 */
			this.clearSize = function(element, keep) {
				var target = getResizeTarget(element),
					style;
				if (target) {
					style = target.style;
					if (keep) {
						element.setAttribute(STORED_SIZE_ATTRIB, style.width + "," + style.height);
					}
					style.width = "";
					style.height = "";

					if (observer) {
						observer.notify(target);
					}
					return true;
				}
				return false;
			};

			/**
			 * Reset size to a previously stored set of values.
			 *
			 * @function module:wc/ui/resizeable.resetSize
			 * @public
			 * @param {Element} element The element we are restoring.
			 */
			this.resetSize = function(element) {
				var stored = element.getAttribute(STORED_SIZE_ATTRIB);
				if (stored) {
					stored = stored.split(",");
					element.style.width = stored[0];
					element.style.height = stored[1];
					if (observer) {
						observer.notify(element);
					}
				}
			};

			/**
			 * Allow an element to display resize animations.
			 *
			 * @function module:wc/ui/resizeable.makeAnimatable
			 * @public
			 * @param {Element} element The element to animate.
			 */
			this.makeAnimatable = function(element) {
				classList.add(element, RESIZEABLE_HAS_ANIMATION_CLASS);
			};

			/**
			 * Prevent an element from displaying resize animations.
			 *
			 * @function module:wc/ui/resizeable.clearAnimatable
			 * @public
			 * @param {Element} element The element to stop animating.
			 */
			this.clearAnimatable = function(element) {
				classList.remove(element, RESIZEABLE_HAS_ANIMATION_CLASS);
			};

			/**
			 * Prevent resize animations but store the fact that they used to be allowed so they can be turned back on.
			 *
			 * @function module:wc/ui/resizeable.disableAnimation
			 * @public
			 * @param {Element} element The resizeable element to manipulate.
			 */
			this.disableAnimation = function(element) {
				if (classList.contains(element, RESIZEABLE_HAS_ANIMATION_CLASS)) {
					this.clearAnimatable(element);
					element.setAttribute(CLASS_REMOVED_ATTRIB, TRUE);
				}
			};

			/**
			 * Restore resize animations previously disabled.
			 *
			 * @function module:wc/ui/resizeable.restoreAnimation
			 * @public
			 * @param {Element} element The resizeable element to manipulate.
			 */
			this.restoreAnimation = function(element) {
				if (element.getAttribute(CLASS_REMOVED_ATTRIB) === TRUE) {
					this.makeAnimatable(element);
					element.removeAttribute(CLASS_REMOVED_ATTRIB);
				}
			};
		}
		var /** @alias module:wc/ui/resizeable */
		instance = new Resizeable();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/resizeable.config() Optional module configuration.
		 * @property {?int} min The minimum size, in px, any element is allowed to be.
		 * @default 0
		 * @property {?int} step The number of pixels to increase/decrease per keypress when resizing with the arrow keys.
		 * @default 6
		 * @property {?int} delay The delay, in milliseconds, between resizing an element and notifying the observers.
		 */
	}
);
