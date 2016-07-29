define(["wc/dom/getViewportSize", "wc/config"], function (getViewportSize, wcconfig) {
	"use strict";

	/**
	 * Create a viewport utils singleton.
	 * @constructor
	 * @private
	 * @alias module:wc/ui/viewportUtils~VpUtils
	 */
	function VpUtils() {

		var conf = wcconfig.get("wc/ui/viewportUtils"),
			// For device limits in Sass see _common.scss.
			// The size of the largest device considered a phone: currently 773px of a Nexus 6.
			phoneLimit = 773,
			// What is the upper limit of a small screen? This could be the 768 of a iPad in portrait orientation,
			// the 1024 of an old-school monitor or an iPad in landscape orientation or something aribrary. The default
			// 1000 was chosen as we find some things are quite usable on a tablet in landscape but are less usable in
			// portrait. We prefer to use min/max-width as a media query rather than orientation for a variety of
			// reasons.
			smallScreenLimit = 1000,
			// A large screen is bigger than 1080p.
			largeScreenLimit = 1981,
			medDefinition = 1.5,
			highDefinition = 2,
			pixelRatio = window.devicePixelRation || 1;

		if (conf) {
			phoneLimit = conf.phone || phoneLimit;
			smallScreenLimit = conf.small || smallScreenLimit;
			largeScreenLimit = conf.large || largeScreenLimit;
		}

		/**
		 * Determine if the current viewport is smaller or larger than a given limit.
		 *
		 * @function
		 * @private
		 * @param {int} limit The limit to test
		 * @param {boolean} [gtr] if true then we want to know if the viewport is at least as big as limit, otherwise
		 *   is the viewport no bigger than limit
		 * @returns {Boolean} true if the viewport is no bigger than limit (or at least as big as limit if gtr is true).
		 */
		function testViewportSize(limit, gtr) {
			var vps = getViewportSize();
			if (vps) {
				if (gtr) {
					return vps.width >= limit;
				}
				return vps.width <= limit;
			}
			return false;
		}

		/**
		 * Is the width of the current viewport similar to that of a mobile phone?
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isPhoneLike
		 * @returns {Boolean} true if the viewport width is no bigger than the configured limit for a phone.
		 */
		this.isPhoneLike = function() {
			return testViewportSize(phoneLimit);
		};


		/**
		 * Is the width of the current viewport "small"?
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isSmallScreen
		 * @returns {Boolean} true if the viewport width is no bigger than the configured limit for a small screen.
		 */
		this.isSmallScreen = function() {
			return testViewportSize(smallScreenLimit);
		};


		/**
		 * Is the width of the current viewport at least that of a large monitor?
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isLargeScreen
		 * @returns {Boolean} true if the viewport width is at least that of the configured limit for a big screen.
		 */
		this.isLargeScreen = function() {
			return testViewportSize(largeScreenLimit, true);
		};

		/**
		 * Is the current screen a high definition screen?
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isHighDef
		 * @returns {Boolean} true if the current screen device is a high definition screen.
		 */
		this.isHighDef = function () {
			return pixelRatio >= highDefinition;
		};

		/**
		 * Is the current screen a moderate definition screen? For example Samsung Galaxy 5, 6.
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isModerateDefinition
		 * @returns {Boolean} true if the current screen device is a medium definition screen.
		 */
		this.isModerateDefinition = function () {
			return pixelRatio >= medDefinition && pixelRatio < highDefinition;
		};

		/**
		 * Is the current screen definition medium def or better?
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isHigherDefinition
		 * @returns {Boolean} true if the current screen device is a medium definition screen or better.
		 */
		this.isHigherDefinition = function () {
			return pixelRatio >= medDefinition;
		};

		/**
		 * Is the current screen definition "normal" i.e. 1 or not able to be determined.
		 *
		 * @function
		 * @public
		 * @alias module:wc/ui/viewportUtils.isStandardDefinition
		 * @returns {Boolean} true if the current screen device is standard definition.
		 */
		this.isStandardDefinition = function () {
			return !pixelRatio || pixelRatio === 1;
		};
	}

	/**
	 * Provides utility methods regarding the current state of the viewport for use in synchronising responsive UI
	 * manipulation with CSS media queries.
	 *
	 * @module wc/ui/viewportUtils
	 * @requires module:wc/dom/getViewportSize
	 * @requires module:wc/config
	 */
	return new VpUtils();
});


