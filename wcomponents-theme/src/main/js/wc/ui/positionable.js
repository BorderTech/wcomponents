/**
 * Provides functionality used to absolutely position a component. Components may be positioned relative to the viewport
 * or another component.
 *
 * @module
 * @requires module:wc/dom/getViewportSize
 * @requires module:wc/dom/getBox
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/timers
 *
 * @todo check source order, document private members.
 */
define(["wc/dom/getViewportSize", "wc/dom/getBox", "wc/dom/uid", "wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/timers"],
	/** @param getViewportSize wc/dom/getViewportSize @param getBox wc/dom/getBox @param uid wc/dom/uid @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @param timers wc/timers @ignore */
	function(getViewportSize, getBox, uid, event, initialise, shed, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/positionable~Positionable
		 * @private
		 */
		function Positionable() {
			var UNIT = "px",
				ZERO = "0px",
				positionedBySize = {"length": 0},
				resizeTimeout,
				RESIZE_TIME = 100;

			/**
			 * A bit map for positions. Allows any implementing class to use common references.
			 * @constant  module:wc/ui/positionable.POS
			 * @public
			 * @type {Object}
			 * @property {int} NORTH The north (top) of an element.
			 * @property {int} EAST The east (right) of an element.
			 * @property {int} SOUTH The south (bottom) of an element.
			 * @property {int} WEST The west (left) of an element.
			 * @property {int} NW The north-west corner (top-left) of an element.
			 * @property {int} NE The north-east corner (top-right) of an element.
			 * @property {int} SE The south-east corner (bottom-right) of an element.
			 * @property {int} SW The south-west corner (bottom-left) of an element.
			 */
			this.POS = {NORTH: 1,
						EAST: 2,
						SOUTH: 4,
						WEST: 8,
						NW: 16,
						NE: 32,
						SE: 64,
						SW: 128};

			/**
			 * Remove a key from the positionedBySize register when it is no longer needed.
			 * @param {String} key An element id.
			 */
			function clearPositionBySizeKey(key) {
				delete positionedBySize[key];
				--positionedBySize.length;
			}

			/**
			 * Helper for {@link module:wc/ui/positionable~resizeEvent}, to reposition any component which is
			 * positioned relative to viewport when the viewport dimensions change.
			 * @function
			 * @private
			 */
			function resizeEventHelper() {
				var element;
				Object.keys(positionedBySize).forEach(function(key) {
					var next;
					if (key !== "length") {
						next = positionedBySize[key];
						if ((element = document.getElementById(key)) && !(shed.isHidden(element))) {
							instance.setBySize(element, next.conf);
						}
						else {
							clearPositionBySizeKey(key);
						}
					}
				});
			}

			/**
			 * Convert a position related style allowing for an offset.
			 * @function
			 * @private
			 * @param {String} pos A value of element.style[foo] where foo is a position (top, right, bottom or left).
			 * @param {int} offset The amount by which the position must be adjusted in pixels.
			 * @returns {String} The new string value of the initial position style. If offset is falsey then the
			 *    initial style is returned unchanged.
			 */
			function styleToStyle(pos, offset) {
				var result = pos, _pos;
				if (offset) {
					_pos = result.replace(UNIT, "");
					result = ((_pos ? parseFloat(_pos) : 0) + offset) + UNIT;
				}
				return result;
			}

			/**
			 * Helper for {@link module:wc/ui/positionable#pinTo} which applies any requested offset after positioning
			 * an element relative to another element.
			 * @function
			 * @private
			 * @param {Element} element The element being "pinned".
			 * @param {float} vOffset The value of the vertical offset in px.
			 * @param {type} hOffset The value of the horizontal offset in px.
			 */
			function applyOffset(element, vOffset, hOffset) {
				var placement, style = element.style;
				if (vOffset && (placement = style.top || style.bottom)) {
					placement = styleToStyle(placement, vOffset);
				}
				if (hOffset && (placement = style.left || style.right)) {
					placement = styleToStyle(placement, hOffset);
				}
			}

			/*
			 * elements which are positioned relative to the viewport
			 * should be repositioned if the viewport resizes.
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
			 * When a component is hidden its link to the resize event is broken. This is so we do not try to resize
			 * things which are no longer visible.
			 * @function
			 * @private
			 * @param {Element} element The element being hidden.
			 */
			function shedSubscriber(element) {
				Object.keys(positionedBySize).forEach(function(key) {
					var target;
					if (key && key !== "length") {
						if (element.id === key || ((target = document.getElementById(key)) && !!(element.compareDocumentPosition(target) & Node.DOCUMENT_POSITION_CONTAINED_BY))) {
							clearPositionBySizeKey(key);
						}
					}
				});
			}

			/**
			 * Sets the absolute position of an element.
			 * @function module:wc/ui/positionable.setPosition
			 * @param {Element} element The element being positioned.
			 * @param {float} left The position for the left edge of the element.
			 * @param {float} top The position for the top edge of the element.
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
				}
			};

			/**
			 * Sets the absolute position of an element but does not allow the left or top to be negative.
			 * @function module:wc/ui/positionable.setPositionInView
			 * @public
			 * @param {Element} element The element being positioned.
			 * @param {float} left The requested position of the left edge of the element.
			 * @param {float} top The requested position of the top edge of the element.
			 */
			this.setPositionInView = function(element, left, top) {
				var _el = element.nodeType ? element : document.getElementById(element);
				if (_el && _el.style) {
					instance.setPosition(_el, left, top);
					instance.forceToViewPort(_el);
				}
			};

			/**
			 * This will PIN an element to another element (so long as that other element has a measurable
			 * boundingClientRect)
			 *
			 * <p>Preconditions:</p><ul>
			 * <li>element must already have a position style which is either absolute or fixed</li>
			 * <li>element must have an explicit or notional z-index greater than that of relTo</li>
			 * <li>relTo must not be hidden or have a boundingClientRect which cannot be measured</li></ul>
			 * <p>NOTES</p><ul>
			 * <li>ALWAYS pin top and left otherwise draggable and resizeable might go funny!!</li>
			 * <li>If no config options are set then this will center element in/over relTo.</li>
			 * <li>The NW, NE, SE, SW values of POS place element on the pointy corner if outside</li>
			 * <li>The order of POS precedence is NW, NE, SE, SW then N has precedence over S and W precedence over E
			 *    so if you try to be too clever and set POS.NORTH and POS.SOUTH you will just get NORTH.</li>
			 * <li>If you want something tricky like inside relTo but 20% from the top and 8% from the left you have to
			 *    use {@link module:wc/ui/positionable#setBySize}</li>
			 * <li>If you want something really tricky like outside and not in any of the 16 static positions, tough
			 *    extend this class!!</li></ul>
			 *
			 * @function module:wc/ui/positionable.pinTo
			 * @public
			 * @param {Element} element The element to move.
			 * @param {Element} relTo The element we are pinning it to.
			 * @param {module:wc/ui/positionable~pinToConfig} config A configuration object.
 			 */
			this.pinTo = function(element, relTo, config) {
				var pos = config.pos,  // bitwise or of this.POS properties
					outside = config.outside,  // boolean
					vOffset = config.vOffset || 0,
					hOffset = config.hOffset || 0;

				if (!(outside || pos)) {
					instance.setBySize(element, {relativeTo: relTo});
				}
				else if (!outside) {
					pinInside(element, relTo, config);
				}
				else {
					pinOutside(element, relTo, config);
				}
				// now offset if necessary
				applyOffset(element, vOffset, hOffset);
			};

			/*
			 * Positioning element outside of relTo.
			 * @function
			 * @private
			 */
			function pinOutside(element, relTo, config) {
				var box, elBox,
					style = element.style,
					coords = calculatePos(config.pos);

				if (coords.boxVert || coords.boxHoriz) {
					box = getBox(relTo);
				}

				if (coords.offsetLeft || coords.offsetTop) {
					elBox = getBox(element);
				}
				if (coords.boxVert && coords.boxHoriz) {
					// setting both
					style[coords.vertPos] = (coords.offsetTop ? (elBox.height * coords.offsetTop) : 0) + box[coords.boxVert] + UNIT;
					style[coords.horizPos] = (coords.offsetLeft ? (elBox.width * coords.offsetLeft) : 0) + box[coords.boxHoriz] + UNIT;
				}
				else {
					// this will center the element
					instance.setBySize(element, {relativeTo: relTo});
					// now we just need to move it
					if (coords.boxVert) {
						style[coords.vertPos] = (coords.offsetTop ? (elBox.height * coords.offsetTop) : 0) + box[coords.boxVert] + UNIT;
					}
					else {
						// we now know boxHoriz is set
						style[coords.horizPos] = (coords.offsetLeft ? (elBox.width * coords.offsetLeft) : 0) + box[coords.boxHoriz] + UNIT;
					}
				}
			}

			/*
			 *
			 * @function
			 * @private
			 *
			 * @param pos A bitwise or of this.POS properties
			 * @returns An object with the following possible properties: boxVert, boxHoriz, offsetTop, offsetLeft, vertPos, horizPos
			 */
			function calculatePos(pos) {
				var TOP = "top", RIGHT = "right", BOTTOM = "bottom", LEFT = "left",
					result = {
						vertPos: TOP,
						horizPos: LEFT
					};
				if (instance.POS.NW & pos) {
					result.boxVert = TOP;
					result.boxHoriz = LEFT;
					result.offsetTop = -1;
					result.offsetLeft = -1;
				}
				else if (instance.POS.NE & pos) {
					result.boxVert = TOP;
					result.boxHoriz = RIGHT;
					result.offsetTop = -1;
				}
				else if (instance.POS.SE & pos) {
					result.boxVert = BOTTOM;
					result.boxHoriz = RIGHT;
				}
				else if (instance.POS.SW & pos) {
					result.boxVert = BOTTOM;
					result.boxHoriz = LEFT;
					result.offsetLeft = -1;
				}
				else {
					if (instance.POS.NORTH & pos) {
						result.vertPos = TOP;
						result.boxVert = TOP;
					}
					else if (instance.POS.SOUTH & pos) {
						result.vertPos = TOP;
						result.boxVert = BOTTOM;
						result.offsetTop = -1;
					}

					if (instance.POS.WEST & pos) {
						result.horizPos = RIGHT;
						result.boxHoriz = LEFT;
						result.offsetLeft = -1;
					}
					else if (instance.POS.EAST & pos) {
						result.horizPos = LEFT;
						result.boxHoriz = RIGHT;
					}
				}
				return result;
			}

			/*
			 *
			 * @function
			 * @private
			 */
			function pinInside(element, relTo, config) {
				var vertPos, horizPos, pos = config.pos;  // bitwise or of this.POS properties;
				if (instance.POS.NW & pos) {
					vertPos = 0;
					horizPos = 0;
				}
				else if (instance.POS.NE & pos) {
					vertPos = 0;
					horizPos = 1;
				}
				else if (instance.POS.SE & pos) {
					vertPos = 1;
					horizPos = 1;
				}
				else if (instance.POS.SW & pos) {
					vertPos = 1;
					horizPos = 0;
				}
				else {
					if (instance.POS.NORTH & pos) {
						vertPos = 0;
					}
					else if (instance.POS.SOUTH & pos) {
						vertPos = 1;
					}

					if (instance.POS.WEST & pos) {
						horizPos = 0;
					}
					else if (instance.POS.EAST & pos) {
						horizPos = 1;
					}
				}
				instance.setBySize(element, {topOffsetPC: vertPos, leftOffsetPC: horizPos, relativeTo: relTo});
			}

			/**
			 * Position an element relative to another element or the viewport where the sise of the element being
			 * positioned determines the location relative to the target.
			 *
			 * @function module:wc/ui/positionable.setBySize
			 * @param {Element} element The element to position.
			 * @param {module:wc/ui/positionable~setBySizeConfig} conf The configuration for this
			 *    position.
			 */
			this.setBySize = function(element, conf) {
				var _el = element.nodeType ? element : document.getElementById(element),
					id = _el.id || (_el.id = uid()),
					relativeTo,  // position relative (usually inside) another element
					relSize,
					topOffset = 0.5,  // may be overridden by conf.topOffsetPC
					leftOffset = 0.5,  // may be overridden by conf.leftOffsetPC
					width, height, box, top, left,
					func = "setPosition";

				if (conf) {
					width = conf.width;
					height = conf.height;
					topOffset = (conf.topOffsetPC !== undefined) ? conf.topOffsetPC : topOffset;  // if the top offset is not specified then position the element so that it is at the top of the relative component
					leftOffset = (conf.leftOffsetPC !== undefined) ? conf.leftOffsetPC : leftOffset;  // if the left offset is not specified then position the element so that it is in the middle of the relative component
					relativeTo = conf.relativeTo;
				}
				if (relativeTo) {
					relSize = getBox(relativeTo);
				}
				else {
					relSize = getViewportSize(true);
					func = "setPositionInView";  // when setting position relative to the viewport never let left or top be less than 0
					positionedBySize[id] = {id: id, conf: conf};
					++positionedBySize.length;
				}

				if (!(width && height)) {
					if (_el.style.width) {
						width = parseFloat(_el.style.width.replace(UNIT, ""));
					}
					else {
						box = getBox(_el);
						width = box.width;
					}
					if (_el.style.height) {
						height = parseFloat(_el.style.height.replace(UNIT, ""));
					}
					else {
						box = box || getBox(_el);
						height = box.height;
					}
				}

				if (relativeTo) {
					top = relSize.top + ((relSize.height - height) * topOffset);
					left = relSize.left + ((relSize.width - width) * leftOffset);
				}
				else {
					top = (relSize.height - height) * topOffset;
					left = (relSize.width - width) * leftOffset;
				}

				instance[func](_el, left, top);
			};

			/**
			 * Make sure an element is completely in view.
			 * @function module:wc/ui/positionable.forceToViewPort
			 * @public
			 * @param {Element} el the positionable element we want inside the viewport.
			 */
			this.forceToViewPort = function(el) {
				var vpSize = getViewportSize(true),
					box = getBox(el), recalc;

				if (box.width > vpSize.width) {
					el.style.width = vpSize.width + UNIT;
					recalc = true;
				}
				if (box.height > vpSize.height) {
					el.style.height = vpSize.height + UNIT;
					recalc = true;
				}
				if (recalc) {
					box = getBox(el);
				}

				if (box.left < 0) {
					el.style.left = ZERO;
					box = getBox(el);
				}
				if (box.right > vpSize.width) {
					el.style.left = (vpSize.width - box.width) + UNIT;
				}
				if (box.top < 0) {
					el.style.top = ZERO;
					box = getBox(el);
				}
				if (box.bottom > vpSize.height) {
					el.style.top = (vpSize.height - box.height) + UNIT;
				}
			};

			/**
			 * Resets all position related styles (top, bottom, left, right) to ZERO (0px).
			 * @function module:wc/ui/positionable.reset
			 * @public
			 * @param {Element} element The element to reset.
			 * @param {Boolean} [ignoreTopLeft] If true then do not reset top or left, just bottom and right. Why?
			 *    because we sometimes need to keep these as they are used rather a lot elsewhere. Why not bottom and
			 *    right? Because they are only set during collision detection or explicit pinning and are never part
			 *    of the underlying component's defaul position model.
			 */
			this.reset = function(element, ignoreTopLeft) {
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
			 * late initialisation to add shed HIDE subscriber.
			 * @function module:wc/ui/positionable.postInit
			 * @public
			 */
			this.postInit = function() {
				event.add(window, event.TYPE.resize, resizeEvent);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
			};

			/**
			 * Allow any other module to clear an element from the position by size register. This is currently only
			 * used by {@link module:wc/ui/draggable} to prevent a draggable component being repositioned by a resize
			 * event if the user has positioned it explicitly.
			 *
			 * @function module:wc/ui/positionable.clearPositionBySize
			 * @public
			 * @param {String} id The id of the element to remove from the register.
			 */
			this.clearPositionBySize = function(id) {
				if (positionedBySize[id]) {
					clearPositionBySizeKey(id);
				}
			};
		}
		var /** @alias module:wc/ui/positionable */ instance = new Positionable();
		initialise.register(instance);
		return instance;

		/**
		 * The configuration object passed as an argument to pinTo.
		 *
		 * @typedef {Object} module:wc/ui/positionable~pinToConfig
		 *
		 * @property {int} pos description a bitwise OR of {@link module:wc/ui/positionable#POS} options.
		 * @property {Boolean} [outside] Where to pin the element in the relative element. If rue it gets pinned to
		 *    the outside of the relative element, otherwise it gets pinned within the relative element.
		 * @property {int} [vOffset] The vertical offset to apply to the pined element relative to the requested
		 *    position.
		 * @property {int} [hOffset] The horizontal offset to apply to the pined element relative to the requested
		 *    position.
		 */

		/**
		 * Configuration object used to set the position of an element relative to either another element or the
		 * viewport. NOTE: if neither topOffsetPC nor leftOffsetPC are set then the element will attempt to
		 * center itself relative to the relative element/viewport.
		 *
		 * @typedef {Object} module:wc/ui/positionable~setBySizeConfig
		 *
		 * @property {int} [width] The width of the element being positioned. If not set then this is calculated.
		 * @property {int} [height] The height of the element being positioned. If not set then this is calculated.
		 * @property {Element} [relativeTo] If set then position the element relative to this target, otherwise
		 *    position it relative to the viewport.
		 * @property {float} [topOffsetPC] If set then the element is positioned such that the top of the element is
		 *    below the top of the relative element/viewport by this much if this is less than 0 then the top of the
		 *    positioned element will be above the relative element.
		 * @property {float} [leftOffsetPC] If set then the element is positioned such that the left edge of the
		 *    element is to the right of the left edge of relative element/viewport by this much. If this is less
		 *    than 0 then the left of the positioned element will be left of the left edge of the relative element.
		 */
	});
