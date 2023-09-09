import clearSelection from "wc/dom/clearSelection";
import event from "wc/dom/event";
import getMouseEventOffset from "wc/dom/getEventOffset";
import isAcceptableTarget from "wc/dom/isAcceptableTarget";
import getBox from "wc/dom/getBox";
import getStyle from "wc/dom/getStyle";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import uid from "wc/dom/uid";
import processResponse from "wc/ui/ajax/processResponse";
import Observer from "wc/Observer";
import debounce from "wc/debounce";
import icon from "wc/ui/icon";
import wcconfig from "wc/config";

const CLASS_MAX_CONTROL = "wc_maxcont";
const resizeSelector = ".wc_resize";
const maxSelector = `button.${CLASS_MAX_CONTROL}`;
const maxbarSelector = `.${CLASS_MAX_CONTROL}`;
const DEFAULT_MIN_SIZE = 0,  // set this to any sensible size but will cause errors in IE if < 0
	RESIZEABLE_HAS_ANIMATION_CLASS = "wc_resizeflow",
	CLASS_REMOVED_ATTRIB = "data-wc-resizeableremovedanimation",
	CLASS_MAX = "wc_max",
	UNIT = "px",
	DEFAULT_KEY_RESIZE = 6,  // the number of pixels by which a resizable is resized by keyboard
	ns = "wc.ui.resizeable",
	BS = ns + ".inited",
	MM_EVENT = ns + ".move.inited",
	/**
	 * @var {number} DEFAULT_NOTIFY_TIMEOUT The delay between resizing and notifying the resize observers. This can
	 * be small but is handy to prevent continual notification during dragging.
	 * @private
	 */
	DEFAULT_NOTIFY_TIMEOUT = 100,
	STORED_SIZE_ATTRIB = "data-wc-storedsize";

const instance= new Resizeable();

/**
 * Provides functionality to implement a resizeable component.
 * @constructor
 * @alias module:wc/ui/resizeable~Resizeable
 * @private
 */
function Resizeable() {
	const offsetX = {},
		offsetY = {};

	let resizing,
		FONT_SIZE,
		observer;

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

	function getNotifyTimeout() {
		let result;
		const conf = wcconfig.get("wc/ui/resizeable", {
			delay: DEFAULT_NOTIFY_TIMEOUT
		});
		if (!isNaN(conf.delay) && conf.delay >= 0) {
			result = conf.delay;
		} else {
			result = DEFAULT_NOTIFY_TIMEOUT;
		}
		return result;
	}

	function getResizer(element) {
		return element.closest(resizeSelector);
	}

	/**
	 * Get the default font size of the BODY element in pixels.
	 * @function
	 * @private
	 * @returns {number} The font size in pixels.
	 */
	function getFontSize() {
		const css = "fontSize",
			needUnits = !(document.defaultView?.getComputedStyle),
			size = getStyle(document.body, css, needUnits, true) || 0;

		if (isNaN(size)) {
			const _s = parseFloat(size);
			if (size.indexOf(UNIT)) {
				return _s;
			} else if (size.indexOf("%")) {
				// IE8 will return the style rule e.g. 75% or 0.75em or even 12pt if you are silly.
				return (16 * _s / 100);
			} else if (size.indexOf("em")) {
				return (16 * _s);
			}
			// if you are going to set your default font size in points, picas or exes you deserve what you get
			return _s;
		}
		return size;
	}

	/**
	 * Usually we set a sub-control of a complete component to be an event
	 * target for resizing (such as a resize handle or a max/restore button).
	 * This helper finds the component we actually want to resize based on such
	 * a control.
	 * @function
	 * @private
	 * @param {Element} element A resize event target.
	 * @returns {HTMLElement} The resizeable component.
	 */
	function getResizeTarget(element) {
		let result = element;
		const targetId = element.getAttribute("data-wc-resize");
		if (targetId) {
			result = document.getElementById(targetId);
		}
		return /** @type {HTMLElement} */(result);
	}

	/**
	 * Convert a (size related) CSS style rule to pixels.
	 * @function
	 * @private
	 * @param {String} size The rule to convert.
	 * @returns {number} the number value of the CSS rule.
	 */
	function styleToPx(size) {
		let _size = size;
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
	 * @param {HTMLElement} element the resizeable component.
	 * @param {Boolean} [native] If true remove any inline styles before calculating the size. If a min/max
	 *    width/height is "auto" or one of the "-content" settings (e.g. fit-content, -moz-max-content etc.) then
	 *    we need to make a guess at the native box size in pixels. This is a bit experimental.
	 * @returns {Object} a POJO with properties {float} width and {float} height.
	 */
	function getSize(element, native) {

		let _width, _height;
		try {
			let { height, width } = element.style;

			if (native) {
				_width = width;
				_height = height;
				element.style.width = "";
				element.style.height = "";
				width = "0";
				height = "0";
			}
			const box = getBox(element);
			height = height ? parseFloat(height.replace(UNIT, "")) : box.height;
			width = width ? parseFloat(width.replace(UNIT, "")) : box.width;

			return { width, height };
		} finally {
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
	 * @returns {String|Number} The style as a string or number.
	 */
	function getSizeConstraint(element, isHeight) {
		const css = "min-" + (isHeight ? "height" : "width"),
			needUnits = !(document.defaultView?.getComputedStyle),
			result = getStyle(element, css, needUnits, true) || 0;

		if (isNaN(result)) {
			// we have something like auto or fit-content.
			if (result === "auto" || result.indexOf("-content")) {
				const box = getSize(element, true);
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
	 * @param {HTMLElement} element The resizeable component.
	 * @param {number} deltaX Change in width in pixels.
	 * @param {number} deltaY Change in height in pixels.
	 * @param {boolean} [notify] If true notify subscribers from here. This would usually be done in an
	 * `end-of-event` handler like mouseup or touchend.
	 */
	function resize(element, deltaX, deltaY, notify) {
		let _notify;
		try {
			let box = element ? getSize(element) : null;
			if (box) {
				let min, minSize;
				const conf = wcconfig.get("wc/ui/resizeable", {
					min: DEFAULT_MIN_SIZE
				});
				if (conf.min && !isNaN(conf.min) && conf.min > 0) {
					minSize = conf.min;
				} else {
					minSize = DEFAULT_MIN_SIZE;
				}
				if (deltaX) {
					min = getSizeConstraint(element);
					min = min ? styleToPx(min) : minSize;
					const width = Math.round(Math.max(box.width + deltaX, min));
					if (width > min && width !== parseInt(element.style.width)) {
						element.style.width = width + UNIT;
						_notify = true;
					}
				}
				if (deltaY) {
					min = getSizeConstraint(element, true);
					min = min ? styleToPx(min) : minSize;
					const height = Math.round(Math.max(box.height + deltaY, min));
					if (height > min && height !== parseInt(element.style.height)) {
						element.style.height = height + UNIT;
						_notify = true;
					}
				}
			}
		} finally {
			clearSelection();
			if (notify && _notify && observer) {
				observer.debouncedNotify(element);
			}
		}
	}

	/**
	 * Mousedown event handler for determining start position for drag initiated resize.
	 * @function
	 * @private
	 * @param {MouseEvent & { target: Element }} $event The mousedown event.
	 */
	function mousedownEvent($event) {
		if ($event.defaultPrevented) {
			return;
		}
		const target = $event.target;
		const element = getResizer(target);
		const resizeTarget = element && isAcceptableTarget(element, target) ? getResizeTarget(element) : null;
		if (resizeTarget) {
			const id = resizeTarget.id || (resizeTarget.id = uid());
			instance.disableAnimation(resizeTarget);
			const offset = getMouseEventOffset($event);
			resizing = id;
			offsetY[id] = offset.Y;
			offsetX[id] = offset.X;
		}
	}

	/**
	 * touchstart event handler for determining start position for drag initiated resize on touch devices.
	 * @function
	 * @private
	 * @param {TouchEvent} $event A touchstart event.
	 */
	function touchstartEvent($event) {
		if ($event.defaultPrevented || $event.touches.length !== 1) {
			return;
		}
		const touch = $event.touches[0];
		const target = touch?.target;
		const element = target ? getResizer(target) : null;
		const resizeTarget = element && isAcceptableTarget(element, target) ? getResizeTarget(element) : null;

		if (resizeTarget) {
			const id = resizeTarget.id || (resizeTarget.id = uid());
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
	 * @param {KeyboardEvent} $event A keydown event
	 */
	function keydownEvent($event) {
		const {target, key, defaultPrevented} = $event;
		if (defaultPrevented) {
			return;
		}

		const element = getResizer(target);
		if (!element) {
			return;
		}
		const resizeTarget = getResizeTarget(element);
		if (!resizeTarget) {
			return;
		}

		const conf = wcconfig.get("wc/ui/resizeable", {
			step: DEFAULT_KEY_RESIZE
		});

		const step = (conf.step && !isNaN(conf.step) && conf.step > 0) ? conf.step : DEFAULT_KEY_RESIZE;
		const allowed = getAllowedDirections(resizeTarget);
		let x = 0,
			y = 0;

		switch (key) {
			case "ArrowRight":
				if (allowed !== "v") {
					x = step;
				}
				break;
			case "ArrowLeft":
				if (allowed !== "v") {
					x = 0 - step;
				}
				break;
			case "ArrowDown":
				if (allowed !== "h") {
					y = step;
				}
				break;
			case "ArrowUp":
				if (allowed !== "h") {
					y = 0 - step;
				}
				break;
		}
		// this is the bit that does the key driven "drag"
		if (x || y) {
			instance.disableAnimation(resizeTarget);
			resize(resizeTarget, x, y, true);
			instance.restoreAnimation(element);
			$event.preventDefault();
		}
	}

	/**
	 * Helper function for touch and mouse driven resizing of a component.
	 * @function
	 * @private
	 * @param {Element} element the resizeable DOM element
	 * @param {number} x the x coordinate of the move event
	 * @param {number} y the y coord of the move event
	 */
	function resizeHandleHelper(element, x, y) {
		const id = element.id,
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
	 * @param {MouseEvent} $event A mousemove event.
	 */
	function mousemoveEvent($event) {
		if (!resizing || $event.defaultPrevented) {
			return;
		}
		const element = document.getElementById(resizing);
		if (element) {
			instance.disableAnimation(element);
			const offset = getMouseEventOffset($event);
			resizeHandleHelper(element, offset.X, offset.Y);
		}
	}

	/**
	 * touchmove event handler for touch driven resize.
	 * @function
	 * @private
	 * @param {TouchEvent} $event A touchmove event.
	 */
	function touchmoveEvent($event) {
		if (!resizing || $event.defaultPrevented || $event.changedTouches.length !== 1) {
			return;
		}
		const touch = $event.changedTouches[0];
		const element = touch?.target ? document.getElementById(resizing) : null;
		if (element) {
			resizeHandleHelper(element, touch.pageX, touch.pageY);
			$event.preventDefault();
		}
	}

	/**
	 * Click event handler for clicks on a max/restore button.
	 * @function
	 * @private
	 * @param {MouseEvent} $event A  click event.
	 */
	function clickEvent($event) {
		const {target, defaultPrevented} = $event;
		if (defaultPrevented) {
			return;
		}
		const element = target.closest(maxSelector);
		if (element) {
			shed.toggle(element, shed.actions.SELECT);
			$event.preventDefault();  // prevent double click invocation by over-exuberant users. (plain exuberant is ok)
		}
	}

	/**
	 * Double-click event handler for clicks on a max/restore header bar.
	 * @function
	 * @private
	 * @param {MouseEvent} $event A double-click event.
	 */
	function doubleClickEvent($event) {
		const {target, defaultPrevented} = $event;
		if (defaultPrevented) {
			return;
		}
		const bar = target.closest(maxbarSelector);
		const button = bar && isAcceptableTarget(bar, target) ? bar.querySelector(maxSelector) : null;
		if (button) {
			$event.preventDefault();
			shed.toggle(button, shed.actions.SELECT);
			clearSelection();
		}
	}

	/**
	 * mouseup, touchend and touchcancel event handler to replace the resize animation class if required.
	 * @function
	 * @private
	 */
	function mouseupTouchendTouchcancelEvent() {
		const element = resizing ? document.getElementById(resizing) : null;
		if (element) {
			observer?.debouncedNotify(element);
			instance.restoreAnimation(element);
		}
		resizing = null;
	}

	/**
	 * initialise subscriber to attach event handlers.
	 * @function
	 * @public
	 * @param {Element} element The element being initialised, usually `document.body`
	 */
	this.initialise = function (element) {
		event.add(element, "mouseup", mouseupTouchendTouchcancelEvent);
		event.add(element, "click", clickEvent);
		event.add(element, "dblclick", doubleClickEvent);
		event.add(element, "touchend", mouseupTouchendTouchcancelEvent);
		event.add(document.body, "touchcancel", mouseupTouchendTouchcancelEvent);
	};

	/**
	 * Adds event listeners to a resize handle and a mousemove event on the document body when the first resize
	 * handle is present and visible.
	 * @function
	 * @private
	 * @param {Element} element A resize handle.
	 */
	function bootstrap(element) {
		const body = document.body;
		if (!element[BS]) {
			element[BS] = true;
			event.add(element, "mousedown", mousedownEvent);
			event.add(element, "keydown", keydownEvent);
			event.add(element, "touchstart", touchstartEvent);
		}
		if (!body[MM_EVENT]) {
			body[MM_EVENT] = true;
			event.add(body, "mousemove", mousemoveEvent);
			event.add(body, "touchmove", touchmoveEvent);
		}
	}

	/**
	 * bootstrap resize handles on `shed.SHOW` or AJAX insert. NOTE: we are probably showing/inserting an ancestor
	 * of the actual resize handle.
	 * @function
	 * @private
	 * @param {Element} element The element being shown/inserted.
	 */
	function ajaxSubscriber(element) {
		if (element) {
			if (element.matches(resizeSelector)) {
				bootstrap(element);
			} else {
				setup(element);
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
		const target = element?.matches(maxSelector) ? getResizeTarget(element) : null;
		if (target) {
			if (action === shed.actions.SELECT) {
				target.classList.add(CLASS_MAX);
				icon.change(element, "fa-minus", "fa-plus");
			} else {
				target.classList.remove(CLASS_MAX);
				icon.change(element, "fa-plus", "fa-minus");
			}
		}
	}

	/**
	 * Get the selectors which describe the component.
	 * @function module:wc/ui/resizeable.getWidget
	 * @public
	 * @returns {Object} A POJO with {string} "handle" and {string} "maximise" selectors
	 */
	this.getWidget = function () {
		return {"handle": resizeSelector, "maximise": maxSelector};
	};

	/**
	 * Makes a given element into a double click enabled maximise bar.
	 * @function module:wc/ui/resizeable.setMaxBar
	 * @public
	 * @param {Element} element The element we wish to change.
	 */
	this.setMaxBar = function (element) {
		element.classList.add(CLASS_MAX_CONTROL);
	};

	/**
	 * Removes double-click enabled maximise bar functionality from a given element.
	 * @function module:wc/ui/resizeable.clearMaxBar
	 * @public
	 * @param {Element} element The element we wish to change.
	 */
	this.clearMaxBar = function (element) {
		element.classList.remove(CLASS_MAX_CONTROL);
	};

	function setup(element) {
		const el = element || document.body;
		Array.prototype.forEach.call(el.querySelectorAll(resizeSelector), bootstrap);
	}

	/**
	 * Late initialisation for ajax and shed subscribers.
	 * @function module:wc/ui/resizeable.postInit
	 * @public
	 */
	this.postInit = function () {
		setup();
		shed.subscribe(shed.actions.SELECT, shedSelectSubscriber);
		shed.subscribe(shed.actions.DESELECT, shedSelectSubscriber);
		shed.subscribe(shed.actions.SHOW, setup);
		processResponse.subscribe(ajaxSubscriber, true);
	};

	/**
	 * Allows a component to subscribe to resizing.
	 * @see {@link module:wc/Observer#subscribe}
	 *
	 * @param {Function} subscriber The function that will be notified. This function MUST be present at
	 *    "publish" time, but need not be preset at "subscribe" time.
	 * @returns {Function} A reference to the subscriber.
	 */
	this.subscribe = function (subscriber) {
		function _subscribe(_subscriber) {
			return observer.subscribe(_subscriber);
		}

		if (!observer) {
			observer = new Observer();
			observer.debouncedNotify = debounce(observer.notify, getNotifyTimeout());
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
	 * @param {boolean} [keep] If true store the size for later use.
	 * @returns {Boolean} true if a resizeable target was found and reset.
	 */
	this.clearSize = function (element, keep) {
		const target = getResizeTarget(element);
		if (target) {
			const style = target.style;
			if (keep && !element[STORED_SIZE_ATTRIB]) {
				element[STORED_SIZE_ATTRIB] = [style.width, style.height, style.maxWidth, style.maxHeight].join();
			}
			style.width = "";
			style.height = "";
			style.minWidth = "";
			style.minHeight = "";

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
	 * @param {boolean} [ignoreSubscribers] if `true` then do not notify via observer
	 */
	this.resetSize = function (element, ignoreSubscribers) {
		let stored = element[STORED_SIZE_ATTRIB];
		if (stored) {
			delete element[STORED_SIZE_ATTRIB];
			stored = stored.split(",");
			element.style.width = stored[0];
			element.style.height = stored[1];
			element.style.maxWidth = stored[2];
			element.style.maxHeight = stored[3];
			if (observer && !ignoreSubscribers) {
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
	this.makeAnimatable = function (element) {
		element.classList.add(RESIZEABLE_HAS_ANIMATION_CLASS);
	};

	/**
	 * Prevent an element from displaying resize animations.
	 *
	 * @function module:wc/ui/resizeable.clearAnimatable
	 * @public
	 * @param {Element} element The element to stop animating.
	 */
	this.clearAnimatable = function (element) {
		element.classList.remove(RESIZEABLE_HAS_ANIMATION_CLASS);
	};

	/**
	 * Prevent resize animations but store the fact that they used to be allowed, so they can be turned back on.
	 *
	 * @function module:wc/ui/resizeable.disableAnimation
	 * @public
	 * @param {Element} element The resizeable element to manipulate.
	 */
	this.disableAnimation = function (element) {
		if (element.classList.contains(RESIZEABLE_HAS_ANIMATION_CLASS)) {
			instance.clearAnimatable(element);
			element.setAttribute(CLASS_REMOVED_ATTRIB, "true");
		}
	};

	/**
	 * Restore resize animations previously disabled.
	 *
	 * @function module:wc/ui/resizeable.restoreAnimation
	 * @public
	 * @param {Element} element The resizeable element to manipulate.
	 */
	this.restoreAnimation = function (element) {
		if (element.getAttribute(CLASS_REMOVED_ATTRIB) === "true") {
			instance.makeAnimatable(element);
			element.removeAttribute(CLASS_REMOVED_ATTRIB);
		}
	};
}
export default initialise.register(instance);

/**
 * @typedef {Object} module:wc/ui/resizeable.config() Optional module configuration.
 * @property {?int} min The minimum size, in px, any element is allowed to be.
 * @default 0
 * @property {?int} step The number of pixels to increase/decrease per keypress when resizing with the arrow keys.
 * @default 6
 * @property {?int} delay The delay, in milliseconds, between resizing an element and notifying the observers.
 */
