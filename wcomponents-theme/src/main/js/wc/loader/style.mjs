import has from "wc/has.mjs";
import mixin from "wc/mixin.mjs";
import wcconfig from "wc/config.mjs";

const DOT_EX = ".css",
	DEFAULT_FILE_NAME_PREFIX = "wc-";
/**
 * A JSON object containing a list of file name 'extensions' or dtos which define what CSS files are to be included. This may be
 * overwritten using module config if you want implementation specific styles. See {@link module:wc/loader/style~cssConfig} and
 * {@link module:wc/loader/style~loadRules}.
 *
 * Note that when adding style fixes for IE one usually wants to load them in descending order of version (ie11 then ie10 for example).
 * We add the link element as the next sibling of the main CSS. SO to get the desired CSS order one ought to have any IE versions in
 * ascending order (e.g. ie10 then ie11)
 *
 * The default/fallback includes fixes for IE11 and MS Edge. These will be overridden completely by a custom config `css`
 * object unless `config.inherit` is `true`.
 *			 *
 * @var
 * @type {module:wc/loader/style~cssConfig}
 * @private
 */
const defaultStylesToAdd = {
	edge: "edge",
	ie11: {
		test: "trident",
		version: 7
	}
};

let CSS_BASE_URL = null,
	CACHEBUSTER = null;

/**
 * Provides a means to load CSS files for particular user agents/platforms etc.
 *
 * ### Configuration
 *
 * There is a default set of supported browsers including IE versions. These may be overridden using module config.
 *
 * ``` js
 * "cacheBuster": "fixedForever",
 * "inherit": true,
 * "css": {
 *     "ext": "hasTest",
 *     "ext": {
 *         "test": "hasTest",
 *         "version": versionInteger,
 *         "media": "css media selector"}
 * }
 * ```
 *
 * Go take a look at {@link module:wc/loader/style~cssConfig} and {@link module:wc/loader/style~loadRules}.
 *
 * @example
 * // The module config object is like this if we support Custom CSS
 * //  only for ie10, ie11, Firefox, and Safari 8:
 * "wc/loader/style": {
 *    cachebuster: null, // cache forever and never check again - you don't want to do this
 *    css: {
 *        "ie10": {
 *            "test": "trident",
 *            "version": 6
 *            },
 *        "ie11": {
 *            "test": "trident",
 *            "version": 7
 *            },
 *        "ff": "ff",
 *        "saf8": {
 *            "test": "safari",
 *            "version": 8
 *        }
 *    }
 * }
 *
 * @module
 */
const instance = {
	/**
	 * Get the main CSS link element produced in the XSLT. The browser specific CSS is added after this.
	 * @returns {String} The URL of the main CSS.
	 */
	getMainCss: function () {
		const mainCss = /** @type HTMLLinkElement */ (document.getElementById("wc_css_screen"));
		return mainCss?.href;
	},

	/**
	 * Write link elements for all required CSS files. Should only be called from ui:root XSLT. To add CSS from a
	 * module use {@link module:wc/loader/style.add}.
	 *
	 * @function module:wc/loader/style.load
	 * @public
	 * @param {module:wc/loader/style~config} [config] a dto describing the CSS to load. If not defined use module config
	 */
	load: function (config) {
		const what = configure(config);

		if (!what) {
			return;
		}
		for (let ext in what) {
			let key, value, media;
			if (!what.hasOwnProperty(ext)) {
				continue;
			}
			key = value = media = null;
			let obj = what[ext];

			if (typeof obj === "string") {
				if (has(obj)) {
					addByName(DEFAULT_FILE_NAME_PREFIX + ext);
				}
				continue;
			}

			key = obj.test;
			checkIsStringOrFalsy(key, "has test");

			let name = obj.name || (DEFAULT_FILE_NAME_PREFIX + ext);
			checkIsStringOrFalsy(name, "name");

			value = obj.version;
			if (value && isNaN(value)) {
				throw new TypeError("Invalid version");
			}

			media = obj.media;
			checkIsStringOrFalsy(media, "media query");

			if (!key) { // This is "add this CSS without testing the UA"
				addByName(name, media);
				continue;
			}

			if (value || value === 0) {  // do we need the `=== 0` ? Is there a version 0 we care about?
				if (has(key) <= value) {
					addByName(name, media);
				}
				continue;
			}

			if (has(key)) {
				addByName(name, media);
			}
		}
	},

	/**
	 * Allow any module to load a CSS file. If your module wants to add custom CSS use this function.
	 *
	 * @function module:wc/loader/style.add
	 * @public
	 * @param {String} nameOrUrl The file name (with or without extension) or URL to a CSS file.
	 *
	 *   1. Supported URLs are of the form "//blah", "/blah", "http[s]://blah" or ".[.]/blah".
	 *   2. If the String is not in one of the URL patterns we assume you are getting a CSS file built from your
	 *     theme in the /style/ directory.
	 *     1. If the String contains ".css" we do not add the extension or cache-buster.
	 *     2. If the file name is not a URL and does not contain .css we add the extension (including the debug
	 *       name extension if in debug mode) and the cache-buster.
	 *
	 *   Therefore, we suggest using a URL (and _I recommend_ the `//blah` form) or a simple file name if you are
	 *   building CSS files which are not able to be implemented using the _pattern and autoloader mechanisms
	 *   (including the ability to override the style loadre config). So in reality this is almost always going to
	 *   be a URL unless you are particularly odd. Being particularly odd I tested this function using the debug CSS
	 *   and loading it from {@link module:wc/debug/a11y}.
	 *
	 * @param {String} [media] A CSS media query appropriate to the link element.
	 * @param {module:wc/loader/style~config} [config] a dto describing the CSS to load. If not defined use module config
	 */
	add: function (nameOrUrl, media, config) {
		configure(config);
		addByName(nameOrUrl, media);
	}
};

/**
 * Use the URL to the main CSS file to get the path to the theme CSs directory.
 * We can make some assumptions about the parsed URL to the main CSS file
 * 1. it has a hostname
 * 2. it has a pathArray
 * 3. the last item in the pathArray is the file name, which we do not want.
 * @returns {String} the URL to the main CSS file excluding the file name and query string with the terminal SLASH.
 */
function getBaseUrlFromMainCss() {
	const cssUrl = instance.getMainCss(),
		SEPARATOR = "/";
	if (cssUrl) {
		const parsedUrl = new URL(cssUrl, location.origin);
		let baseUrl = parsedUrl.origin;
		const pathnameArray = parsedUrl.pathname.split(SEPARATOR);
		if (pathnameArray.length) {
			pathnameArray.pop();
			// do not include the last part of the pathname array: it is the filename of the main CSS file
			baseUrl += pathnameArray.join(SEPARATOR);

		}
		return baseUrl + SEPARATOR;
	}
	return "";  // this would basically be a disaster
}

/**
 * One time function to get the default cache-buster off of the main CSS built by XSLT.
 * @function
 * @private
 * @returns {String} the cache-buster querystring on the main CSS link's URL.
 */
function getCachebusterFromMainCss() {
	const cssUrl = instance.getMainCss();
	if (cssUrl) {
		const parsedUrl = new URL(cssUrl);
		const parsedSearch = parsedUrl?.search;
		if (parsedSearch) {
			// If parsed URL has a search queryString it is always prefixed with a ?
			return parsedSearch.substring(1);
		}
	}



	return "";  // no cache buster
}

/**
 * @function
 * @private
 * @param {module:wc/loader/style~config} [obj] An alternate configuration object. If not set then use module config.
 * @returns {module:wc/loader/style~cssConfig} an object which describes the CSS files to be loaded.
 */
function configure(obj) {
	const config = obj || wcconfig.get("wc/loader/style");
	let result = defaultStylesToAdd;
	if (CSS_BASE_URL === null) {  // first call to configure
		CSS_BASE_URL = getBaseUrlFromMainCss();
	}
	if (config) {
		if (typeof config.css !== "undefined") {
			if (config.inherit && (typeof config.css === "object")) {
				result = mixin(config.css, result);
			} else {
				result = config.css;
			}
		}
		if (typeof config.cachebuster !== "undefined") {
			CACHEBUSTER = config.cachebuster;
		}
	}
	if (CACHEBUSTER === null) {
		CACHEBUSTER = getCachebusterFromMainCss();
	}
	return result;
}

/**
 * Create a link element for a CSS file in the head element unless we already have one for this URL.
 *
 * @function
 * @private
 * @param {String} url The CSS url to add.
 * @param {String} [media] A CSS media query for the link element.
 */
function addLinkElement(url, media) {
	if (document.querySelector(`link[href='${url}']`)) {  // Do not add the same link URL twice.
		return;
	}
	let linkAttribs = `data-wc-loader="style" type="text/css" rel="stylesheet" hrefurl="${url}"`;
	if (media) {
		linkAttribs += ` media="${media}"`;
	}
	const el = `<link ${linkAttribs}/>`;
	const lastCss = getLastCssLink();
	const sibling = lastCss ? lastCss.nextElementSibling : null;
	if (sibling) {
		sibling.insertAdjacentHTML("afterend", el);
	} else {
		document.head.insertAdjacentHTML("beforeend", el);
	}
}

/**
* @param {string} arg
* @param {string} msg
* @return {boolean} true if the arg is falsy or a string
*/
function checkIsStringOrFalsy(arg, msg) {
	if (!arg || typeof arg === "string") {
		return true;
	}
	throw new TypeError(`Invalid ${msg}`);
}

/**
 * @function
 * @private
 * @returns {Element} the last CSS link element in the current page
 */
function getLastCssLink() {
	const candidates = document.querySelectorAll("link[rel='stylesheet']");
	if (!candidates?.length) {
		return null;
	}
	return candidates[(candidates.length - 1)];
}

/**
 * Creates a link element from a string input which could be a simple CSS file name in a WComponents theme or a URL to any CSS file.
 * @function
 * @private
 * @param {String} nameOrUrl the basis of the CSS url, or a complete URL
 * @param {String} [media] a CSS media query
 */
function addByName(nameOrUrl, media) {
	const isUrl = nameOrUrl.indexOf("/") === 0 || nameOrUrl.indexOf("http") === 0 || nameOrUrl.indexOf(".") === 0;

	if (isUrl) {
		addLinkElement(nameOrUrl, media);
		return;
	}
	let fullUrl = CSS_BASE_URL + nameOrUrl;
	if (nameOrUrl.indexOf(DOT_EX) < 0) {
		fullUrl += DOT_EX;
	}
	if (CACHEBUSTER) {
		fullUrl += "?" + CACHEBUSTER;
	}
	addLinkElement(fullUrl, media);
}

/**
 * @typedef {Object} module:wc/loader/style~config
 * @property {String} [cachebuster] The cache key for the loaded CSS, generally generated from the XSLT and not overridden.
 * @property {module:wc/loader/style~cssConfig} [css] An object describing other CSS patches to load based on optional `has` and/or media queries.
 * @property {boolean} [inherit] if `true` then inherit the default CSS include object as a mixin target. Only used if `css` is an object.
 *
 * @example
 * // add configuration to inherit the default CSS and add support for small screen CSS `wc-phone.css` in all browsers and CSS file `wc-ff.css`
 * // in all versions of Firefox:
 * require(["wc/config"], function (wconfig) {
 *   var cssLoaderConfig = {
 *		inherit: true,
 *		css: {
 *		  phone: {
 *		    test: null,
 *		    media: "only screen and (max-width: 773px)"
 *		  },
 *		  "ff": "ff"
 *		}
 *   };
 *   wcconfig.set()
 * });
 */

/**
 * @typedef {Object} module:wc/loader/style~cssConfig
 * @property {String} key The file name extension used in the CSS build. This is the bit immediately after the `wc-` part and before the `.css`
 *    part of the CSS file's name (eg 'ff'). This can be anything (well, anything which is a valid object property name) if the value is a
 *    `loadRules` object and that object defines both the `test` and `name` properties.
 * @property {module:wc/loader/style~loadRules|string} value The rules for describing and load testing for the CSS. If this property is
 *    a string then it is a simple has test passing in that string and using the key as the building block for the CSS file name as described
 *    above. Otherwise, see {@link module:wc/loader/style~loadRules}
 *
 * @example
 * // the following includes Firefox of any version, Safari version 6, print styles for any mac and styles for safari version 8 including a media
 * // selector for large screens:
 * "css": {
 *   "ff": "ff", // loads `wc-ff.css` from the theme's style directory
 *   "safari6": { // loads `wc-safari6.css` from the theme's style directory
 *      "test": "safari",
 *      "version": 6
 *      },
 *   "macprint": { // loads `wc-macprint.css` from the theme's style directory
 *      "test": "mac",
 *      "media": "print"
 *      },
 *   "saf8big": { // loads `wc-saf8big.css` from the theme's style directory
 *      "test": "safari",
 *      "version": 8,
 *      "media": "@media only screen and (min-device-width:2560px)"
 *   }
 * }
 */

/**
 * @typedef {Object} {module:wc/loader/style~loadRules}
 * @property {String} [test] The string arg passed to has to sniff user agent, eg "safari" or "ff". If falsy the style will be added without an
 *   `has` test.
 * @property {number} [version] The version of the browser to test. If set then the has test is compared to this
 *   and is deemed successful if the browser version is <= version.
 * @property {String} [media] A CSS media selector. If set then the CSS link will include this media selector
 * @property {String} [name] the CSS file URL/name (with or without path) to load. If not set then the file to load will be based on the key
 *   {@see module:wc/loader/style~cssConfig) in the form of CSS_BASE_URL + "wc-" + key + ".css? + CACHEBUSTER
 *
 * @example
 * // To test for Safari 8 or below and a screen with a lot of horizontal pixels:
 * saf8big: { // will load file "wc-saf8big.css" from the theme style directory.
 *   "test": "safari",
 *   "version": 8,
 *   "media": "@media only screen and (min-device-width:2560px)"
 * }
 * // or
 * saf8bigScreen: { // will load file "s8big.css" from the theme style directory.
 *   "test": "safari",
 *   "version": 8,
 *   "name": "s8big.css",
 *   "media": "@media only screen and (min-device-width:2560px)"
 * }
 *
 * @example
 * // TO load a print stylesheet from URL "https://example.com/css/print.css" in Firefox:
 * ffPrint: {
 *   "test": "ff",
 *   "media": "print"
 *   "name": "https://example.com/css/print.css"
 * }
 *
 * @example
 * // TO load a stylesheet called "foo.css" from the theme style directory in Firefox:
 * foo: {
 *   "test": "ff",
 *   "name": "foo.css" // note: "name": "foo" will also work and will add the cachebuster
 * }
 *
 * @example
 * // TO load a print stylesheet called "print.css" from the theme style directory in all browsers and use the standard cache buster:
 * printCss: {
 *   "test": null, // may be omitted completely
 *   "name": "print",
 *   "media": "print"
 * }
 * // NOTE that any falsy value for property `test` will have the same result as `null`.
 *
 * @example
 * // To load the `wc-phone.css` fle built during theme build in _any_ small screen
 * "phone": {
 *  "media": "only screen and (max-width: 773px)" // note `test` can be omitted as falsy works.
 * }
 */

export default instance;
