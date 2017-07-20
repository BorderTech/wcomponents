define(["wc/dom/getViewportSize", "wc/dom/getBox", "wc/dom/getStyle", "wc/dom/uid", "wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/timers", "wc/ui/resizeable"],
	function(getViewportSize, getBox, getStyle, uid, event, initialise, shed, timers, resizeable) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/positionable~Positionable
		 * @private
		 */
		function Positionable() {
			var UNIT = "px",
				ZERO = "0" + UNIT,
				positionedBySize = {"length": 0},
				resizeTimeout,
				/**
				 * Delay before firing resize event helper. Used to prevent the handler firing continually whilst
				 * dragging the window frame.
				 * @var
				 * @type Number
				 * @private
				 */
				RESIZE_TIME = 100;

			/**
			 * An iterator function which will loop through all known elements which are positioned by size and
			 * reposition them if they are visible.
			 *
			 * @function
			 * @private
			 * @param {String} key An object key from positionedBySize.
			 */
			function resizeIteratorFunc(key) {
				var element, next, reStore;
				if (key === "length") {
					return;
				}
				next = positionedBySize[key];

				if (!(next.conf && (element = document.getElementById(key))) || shed.isHidden(element)) {
					return;
				}

				if (element.style.width) {
					next.conf.width = element.style.width.replace(UNIT, "");
					reStore = true;
				}
				if (element.style.height) {
					next.conf.height = element.style.height.replace(UNIT, "");
					reStore = true;
				}
				if (reStore) {
					instance.storePosBySize(element, next.conf);
				}
				instance.setBySize(element, next.conf);
			}

			/**
			 * Resize event helper to reposition any component which is positioned relative to viewport when the
			 * viewport dimensions change.
			 *
			 * @function
			 * @private
			 */
			function resizeEventHelper() {
				Object.keys(positionedBySize).forEach(resizeIteratorFunc);
			}

			/**
			 * Elements which are positioned relative to the viewport should be repositioned if the viewport resizes.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The resize event.
			 */
			function resizeEvent($event) {
				if (positionedBySize.length) {
					if (resizeTimeout) {
						timers.clearTimeout(resizeTimeout);
					}
					resizeTimeout = timers.setTimeout(resizeEventHelper, RESIZE_TIME, $event);
				}
			}

			/**
			 * Sets the absolute position of an element but does not allow the left or top to be negative.
			 * @function module:wc/ui/positionable.setPosition
			 * @public
			 * @param {Element} element The element being positioned.
			 * @param {float} left The requested position of the left edge of the element.
			 * @param {float} top The requested position of the top edge of the element.
			 */
			this.setPosition = function(element, left, top) {
				var _el = element.nodeType ? element : document.getElementById(element);
				if (_el && _el.style) {
					if (left || left === 0) {
						_el.style.left = left + UNIT;
					}
					if (top || top === 0) {
						_el.style.top = top + UNIT;
					}
					forceToViewPort(_el);
				}
			};

			/**
			 * Subscribe to {@link module:wc/ui/resizeable} to reposition components when they are resized.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being resized.
			 */
			function resizeableSubscriber(element) {
				var id = element.id,
					key,
					conf;

				if (id && (key = positionedBySize[id]) && (conf = key.conf)) {
					if (element.style.width) {
						conf.width = element.style.width.replace(UNIT, "");
					} else {
						delete conf.width;
					}
					if (element.style.height) {
						conf.height = element.style.height.replace(UNIT, "");
					} else {
						delete conf.height;
					}
					instance.storePosBySize(element, conf);
					instance.setBySize(element, conf);
				}
			}

			/**
			 * Allow an external module to set a component as positioned by size without actually positioning it yet.
			 *
			 * @function module:wc/ui/positionable.storePosBySize
			 * @public
			 * @param {Element} element The element which will be positioned.
			 * @param {module:wc/ui/positionable~setBySizeConfig} [conf] Position configuration.
			 */
			this.storePosBySize = function(element, conf) {
				var id = element.id || (element.id = uid());
				if (!positionedBySize[id]) {
					++positionedBySize.length;
				}
				positionedBySize[id] = {id: id, conf: conf};
			};

			/**
			 * Position an element relative to another element or the viewport where the size of the element being
			 * positioned determines the location relative to the target.
			 *
			 * @function module:wc/ui/positionable.setBySize
			 * @public
			 * @param {Element} element The element to position.
			 * @param {module:wc/ui/positionable~setBySizeConfig} conf The configuration for this
			 *    position.
			 */
			this.setBySize = function(element, conf) {
				var _el = element.nodeType ? element : document.getElementById(element),
					id = _el.id || (_el.id = uid()),
					relSize,
					topOffset = 0.5, // may be overridden by conf.topOffsetPC
					leftOffset = 0.5, // may be overridden by conf.leftOffsetPC
					width, height, box, top, left;

				if (conf) {
					width = conf.width;
					height = conf.height;
					// if the top offset is not specified then position the element so that it is at the top of the relative component
					topOffset = (conf.topOffsetPC !== undefined) ? conf.topOffsetPC : topOffset;
					// if the left offset is not specified then position the element so that it is in the middle of the relative component
					leftOffset = (conf.leftOffsetPC !== undefined) ? conf.leftOffsetPC : leftOffset;
				}

				relSize = getViewportSize(true);
				if (!positionedBySize[id]) {
					++positionedBySize.length;
				}
				positionedBySize[id] = {id: id, conf: conf};

				_el.style.top = "";
				_el.style.left = "";
				box = getBox(_el);
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
				left = (relSize.width - width) * leftOffset;
				height = height || 0;
				top = (relSize.height - height) * topOffset;
				this.setPosition(_el, left, top);
			};

			/**
			 * Make sure an element is completely in view.
			 * @function
			 * @private
			 * @param {Element} el the positionable element we want inside the viewport.
			 */
			function forceToViewPort(el) {
				var vpSize = getViewportSize(true),
					box, recalc, max, overflow;

				if (el.style.top && parseFloat(el.style.top) < 0) {
					el.style.top = ZERO;
				}

				if (el.style.left && parseFloat(el.style.left) < 0) {
					el.style.left = ZERO;
				}

				box = getBox(el);

				if (box.width > vpSize.width || box.height > vpSize.height) {
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

					overflow = getStyle(el, "overflow", false, true);
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
			 * Removes position styles if they are ZERO.
			 * @function module:wc/ui/positionable.clearZeros
			 * @public
			 * @param {Element} element The element to reset.
			 * @param {Boolean} [ignoreTopLeft] If true then do not reset top or left, just bottom and right. Why? because we sometimes need to keep
			 * these as they are used rather a lot elsewhere. Why not bottom and right? Because they are only set during collision detection or
			 * explicit pinning and are never part of the underlying component's default position model.
			 */
			this.clearZeros = function(element, ignoreTopLeft) {
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
			};

			/**
			 * Clear inline positions.
			 *
			 * @function
			 * @public
			 * @param {Element} element the element being cleared
			 */
			this.clear = function(element) {
				element.style.top = "";
				element.style.left = "";
				element.style.right = "";
				element.style.bottom = "";
			};

			/**
			 * Late initialisation to add resize event handler and subscriber to {@link module:wc/ui/resizeable}.
			 * @function module:wc/ui/positionable.postInit
			 * @public
			 */
			this.postInit = function() {
				event.add(window, event.TYPE.resize, resizeEvent, 1);
				resizeable.subscribe(resizeableSubscriber);
			};
		}

		/**
		 * Provides functionality used to absolutely position a component. Components may be positioned relative to the viewport
		 * or another component.
		 *
		 * @module
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/getViewportSize
		 * @requires module:wc/dom/getBox
		 * @requires module:wc/dom/getStyle
		 * @requires module:wc/dom/uid
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/timers
		 *
		 * @todo check source order, document private members.
		 */
		var instance = new Positionable();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/positionable~setBySizeConfig
		 * @property {int} [width] The width of the element being positioned. If not set then this is calculated.
		 * @property {int} [height] The height of the element being positioned. If not set then this is calculated.
		 * @property {float} [topOffsetPC] If set then the element is positioned such that the top of the element is below the top of the relative
		 *   element/viewport by this much if this is less than 0 then the top of the positioned element will be above the relative element.
		 * @property {float} [leftOffsetPC] If set then the element is positioned such that the left edge of the element is to the right of the left
		 *   edge of relative element/viewport by this much. If this is less than 0 then the left of the positioned element will be left of the left
		 *   edge of the relative element.
		 */
	});
