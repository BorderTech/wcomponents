import getViewportSize from "wc/dom/getViewportSize.mjs";
import wcconfig from "wc/config.mjs";

const defaultConf = {
		large: 1981,  // A large screen is bigger than 1080p.
		// What is the upper limit of a small screen? This could be the 768 of a iPad in portrait orientation,
		// the 1024 of an old-school monitor or an iPad in landscape orientation or something aribrary. The default
		// 1000 was chosen as we find some things are quite usable on a tablet in landscape but are less usable in
		// portrait. We prefer to use min/max-width as a media query rather than orientation for a variety of
		// reasons.
		small: 1000,
		phone: 773  // The size of the largest device considered a phone: currently 773px of a Nexus 6.
	},
	medDefinition = 1.5,
	highDefinition = 2,
	pixelRatio = globalThis.devicePixelRatio || 1;

/**
 * Determine if the current viewport is smaller or larger than a given limit.
 *
 * @function
 * @private
 * @param {number} limit The limit to test
 * @param {boolean} [gtr] if true then we want to know if the viewport is at least as big as limit, otherwise
 *   is the viewport no bigger than limit
 * @returns {Boolean} true if the viewport is no bigger than limit (or at least as big as limit if gtr is true).
 */
function testViewportSize(limit, gtr) {
	const vps = getViewportSize();
	if (vps) {
		if (gtr) {
			return vps.width >= limit;
		}
		return vps.width <= limit;
	}
	return false;
}

const instance = {
	/**
	 * Is the width of the current viewport similar to that of a mobile phone?
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isPhoneLike
	 * @returns {Boolean} true if the viewport width is no bigger than the configured limit for a phone.
	 */
	isPhoneLike: function() {
		const conf = getConfig();
		return testViewportSize(conf.phone);
	},

	/**
	 * Is the width of the current viewport "small"?
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isSmallScreen
	 * @returns {Boolean} true if the viewport width is no bigger than the configured limit for a small screen.
	 */
	isSmallScreen: function() {
		const conf = getConfig();
		return testViewportSize(conf.small);
	},

	/**
	 * Is the width of the current viewport at least that of a large monitor?
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isLargeScreen
	 * @returns {Boolean} true if the viewport width is at least that of the configured limit for a big screen.
	 */
	isLargeScreen: function() {
		const conf = getConfig();
		return testViewportSize(conf.large, true);
	},

	/**
	 * Is the current screen a high definition screen?
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isHighDef
	 * @returns {Boolean} true if the current screen device is a high definition screen.
	 */
	isHighDef: () => pixelRatio >= highDefinition,

	/**
	 * Is the current screen a moderate definition screen? For example Samsung Galaxy 5, 6.
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isModerateDefinition
	 * @returns {Boolean} true if the current screen device is a medium definition screen.
	 */
	isModerateDefinition: () => pixelRatio >= medDefinition && pixelRatio < highDefinition,

	/**
	 * Is the current screen definition medium def or better?
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isHigherDefinition
	 * @returns {Boolean} true if the current screen device is a medium definition screen or better.
	 */
	isHigherDefinition: () => pixelRatio >= medDefinition,

	/**
	 * Is the current screen definition "normal" i.e. 1 or not able to be determined.
	 *
	 * @function
	 * @public
	 * @alias module:wc/ui/viewportUtils.isStandardDefinition
	 * @returns {Boolean} true if the current screen device is standard definition.
	 */
	isStandardDefinition: () => !pixelRatio || pixelRatio === 1
};

function getConfig() {
	const conf = wcconfig.get("wc/ui/viewportUtils", defaultConf);
	Object.keys(defaultConf).forEach(prop => {
		if (!conf[prop] || isNaN(conf[prop])) {
			conf[prop] = defaultConf[prop];
		}
	});
	return conf;
}

/**
 * Provides utility methods regarding the current state of the viewport for use in synchronising responsive UI
 * manipulation with CSS media queries.
 *
 * ### Configuration
 *
 * This midule may be configured using an object {@link module:wc/ui/viewportUtils~config}.
 *
 *
 * @module wc/ui/viewportUtils
 * @requires module:wc/dom/getViewportSize
 * @requires module:wc/config
 */
export default instance;

/**
 * @typedef {Object} module:wc/ui/viewportUtils~config Optional configuration for vpUtils.
 * @property {number} [phone=773] the pixel number representing the CSS pixel width of the largest viewport considered "phone-like"
 * @property {number} [small=1000] the pixel number representing the CSS pixel width of the largest viewport considered "small-screen"
 * @property {number} [large=1981] the pixel number representing the CSS pixel width of the smallest viewport considered "large-like"
 */
