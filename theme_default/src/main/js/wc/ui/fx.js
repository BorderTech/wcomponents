/**
 * A module to provide visual effects. Much reduced - if you need a visual effect try it in CSS first!
 *
 * The last remaining JavaScript fx is yellow fade and it is only applied to subordinate targets (since they do not
 * get aria-busy nor have a loading indicator).
 *
 * @todo replace with CSS transition effect.
 *
 * @module
 * @require module:wc/has
 * @require module:wc/dom/tag
 * @require module:wc/dom/attribute
 * @require module:wc/dom/getStyle
 * @require module:wc/dom/color
 * @require module:wc/timers
 *
 * @todo re-order source, document private members.
 */
define(["wc/has", "wc/dom/tag", "wc/dom/attribute", "wc/dom/getStyle", "wc/dom/color", "wc/timers"],
	/** @param has wc/has @param tag wc/dom/tag @param attribute wc/dom/attribute @param getStyle wc/dom/getStyle @param color wc/dom/color @param timers wc/timers @ignore */
	function(has, tag, attribute, getStyle, color, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/fx~Effect
		 * @private
		 */
		function Effect() {
			var OUT = 0,
				IN = 1;
			/**
			 * The 'speed' of the effect transition in 'frames per second'.
			 * @var module:wc/ui/fx.fps
			 * @public
			 * @type Number
			 * @default 60
			 */
			this.fps = 60;


			/**
			 * The background color of the element will immediately turn to the fadeColor and then quickly fade to
			 * the color you tell it to. Or if you don't tell it which color to fade to it will fade to the current
			 * backgroundColor if it is set, otherwise will fade to white.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to apply the yellow-fade to.
			 * @param {Object} fadeColor the color to fade to as rgba object for example see {@link module:wc/ui/fx#yellowFade}.
			 */
			function fade(element, fadeColor) {
				var ORIGINAL_COLOR = "originalColor",
					transparent = {r: 0, g: 0, b: 0, a: 0},
					white = {r: 255, g: 255, b: 255, a: 1},
					targetColor, originalColor;

				/** @ignore */
				function callback() {
					element.style.backgroundColor = "";  // originalColor
					if (element.hasAttribute("style") && !element.getAttribute("style")) {
						element.removeAttribute("style");
					}
					element = null;
				}

				// need to store the target color in case this routine tries to determine the target color during a yellow fade
				originalColor = attribute.get(element, ORIGINAL_COLOR);
				if (!originalColor) {
					originalColor = getBackgroundColor(element);
					if (!originalColor || originalColor.a === transparent.a) {
						// if it can't be found set it to white...???
						originalColor = white;
					}
					attribute.set(element, ORIGINAL_COLOR, originalColor);
				}
				targetColor = originalColor;
				bgBlend(element, 800, fadeColor, (targetColor === "transparent" ? transparent : targetColor), callback);
			}

			/**
			 * Get the (virtual) background colour of an element. Tries not to return "transparent" (walks up through
			 * parent nodes looking for non-transparent value). Tries to return a hex value, ie "white" will be
			 * converted to #FFFFFF.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element for which we need the background colour.
			 * @returns {?String} The background colour as a hex string.
			 */
			function getBackgroundColor(element) {
				var BG_COLOR = "background-color",
					bgcolor = getStyle(element, BG_COLOR);  // see if we can determine the target color
				while (element && !bgcolor) {
					element = element.parentNode;
					if (element.tagName === tag.HTML) {
						break;
					}
					else if (element) {
						bgcolor = getStyle(element, BG_COLOR);
					}
				}
				return bgcolor;
			}

			function bgBlend(element, duration, colorA, colorB, callback) {
				var result;

				function step(element, percent) {
					var bg = color.blend(colorA, colorB, percent);
					element.style.backgroundColor = color.rgb2hex(bg);
				}

				result = pulseEffect(element, duration, 0.5, IN, step, callback);
				return result;
			}

			/*
			 * Possible enhancements:
			 * allow to set a range, e.g. min opacity max opacity
			 * allow to set the initial direction, e.g. out / in
			 * allow to set an initial value
			 */
			function pulseEffect(element, duration, iterations, dir, fnStep, fnEnd) {
				var result, timesPerIteration, timesPerSlope, start, interval;

				duration = duration || 1700;
				iterations = iterations || 3;
				dir = dir || OUT;
				timesPerIteration = duration / iterations;
				timesPerSlope = timesPerIteration / 2;
				start = new Date();

				interval = timers.setInterval(function() {
					var elapsed = new Date() - start,
						positionOnSlope, direction, opacity;
					if (elapsed > duration) {
						timers.clearInterval(interval);
						if (fnEnd) {
							fnEnd(element);
						}
					}
					else {
						positionOnSlope = (elapsed % timesPerSlope) / timesPerSlope * 100;
						direction = Math.floor(elapsed / timesPerSlope) % 2;
						opacity = Math.round((dir ? direction : !direction) ? 100 - positionOnSlope : positionOnSlope);
						fnStep(element, opacity);
					}
				}, 1000 / instance.fps);
				result = interval;
				return result;
			}

			/**
			 * The background color of the element will immediately turn yellow and then quickly fade to the color you
			 * tell it to. Or if you don't tell it which color to fade to it will fade to the current backgroundColor if
			 * it is set, otherwise will fade to white.
			 *
			 * @function module:wc/ui/fx.yellowFade
			 * @public
			 * @param {Element} element The element to apply the yellow-fade to
			 * @param {Object} [targetColor] If provided the yellow fade will fade out to this color.
			 * @throws {ReferenceError} If applied to snow.
			 */
			this.yellowFade = function(element) {
				var yellow = {r: 255, g: 255, b: 0, a: 1};
				fade(element, yellow);
			};
		}

		var /** @alias module:wc/ui/fx */ instance = new Effect();

		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function() {
				// Do nothing
			});
		}
		return instance;
	});
