/**
 * Provides a means to load CSS files for particular user agents/platforms etc.
 *
 * The following extensions produce CSS which is always (conditionally) added:
 *
 * * 'mob' if mobile; or
 * * 'dt' if not mobile.
 *
 * You may be asking why I split IE out from "screen". It was simply to make changing the defaults easier.
 * For most browsers which aren't IE you only need one (or no) CSS overrides because old versions fall out of
 * usage pretty quickly and have been generally pretty good at CSS for a long time. This allows us to use simple
 * has tests for most major modern browsers (has("ff"), has("chrome") etc) without bothering too much about
 * versions. Safari may be a candidate for version testing though, and that is why the "screen" config property
 * allows an extension to have an object value.
 *
 * <h5>Configuration</h5>
 *
 * There is a default set of supported browsers including IE versions. These may be overridden using module
 * config. There is an XSLT helper for this to include these overrides in the existing config. Go look at
 * wc.ui.root.n.styleLoaderConfig.xslt. The module config addition, if required, is of the form:
 *
 * <pre><code>"ie": [string array of required ie versions],
 * "screen": {
 *     "ext": "hasTest",
 *     "ext": {
 *         "test": "hasTest",
 *         "version": versionInteger,
 *         "media": "css media selector"}}</code></pre>
 *
 * Go take a look at {@link module:wc/loader/style~config} and {@link module:wc/loader/style~configValueObject}.
 *
 * @example
 * // The module config object is like this if we support Custom CSS
 * //  only for ie10, ie11, Firefox, and Safari 8:
 * "wc/loader/style": {
 *    cssBaseUrl:"// url/to/css/dir/css/",// automatic
 *    cachebuster: "someStringThing",// automatic
 *    debug: 1,// automatic if in debug mode
 *    ie: ["ie11", "ie10"],
 *    css: {
 *        "ff": "ff",
 *        "saf8": {
 *            "test": "safari",
 *            "version": 8
 *        }
 *    }
 * }
 *
 * @module
 * @requires module:wc/has
 * @todo Maybe allow load to accept an Object or Object[] arg so it can be called from within another module?
 */
define(["wc/has", "module"], /** @param has wc/has @param module module @ignore */ function(has, module) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/loader/style~StyleLoader
	 * @private
	 */
	function StyleLoader() {
		var
			/**
			 * The supported versions of IE below 10. The versions are of the form 'ie#' and are comma separated. The
			 * default can be overridden using module.config().ie.
			 *
			 * @var
			 * @type {String[]}
			 * @private
			 * @default ["ie11","ie10", "ie9", "ie8"]
			 */
			ieVersionsToSupport = ["ie11", "ie10", "ie9", "ie8"],
			/**
			 * <p>A JSON object containing a list of file name 'extensions' which are to be included. This is obtained
			 * from a module config if you want implementation specific styles. The default/fallback includes only the
			 * Firefox fixes and some bits of ios specific CSS for demo purposes. If you use a config override it must
			 * include any of these defaults you want to keep because the config will replace the defaults, not add to
			 * them.</p>
			 *
			 * <p><string>DO NOT include IE specific files here</strong>. IE versions (e.g. ie8 or ie9) are included in
			 * {@link module:wc/loader/style~ieVersionsToSupport}.</p>
			 *
			 * <p>Some popular tests (see dojo/sniff for more):</p>
			 * <ul><li>has("ios")</li>
			 * <li>has("android")</li>
			 * <li>has("safari")</li>
			 * <li>has("mac")</li></ul>
			 *
			 * <p>Hard coded file name extensions used in WComponents default theme include:
			 * "dt" for desktop (ie not mobile: included by default, no need to add these);
			 * "mob" for generic mobile (also included by default);
			 * "ios" for iOS specific CSS;
			 * "safari" for Safari; or
			 * "ff" for Firefox.</p>
			 *
			 * @var
			 * @type {module:wc/loader/style~config}
			 * @private
			 * @default {ff: "ff", safari: "safari", ios: "ios"}
			 */
			screenStylesToAdd = ((module.config && module.config().screen) ? module.config().css : {ff: "ff", safari: "safari", ios: "ios"}),

			/* NOTE TO SELF: the vars below which are only used once are used in a function which is called many times.
			 * leave them here you twit!*/

			/**
			 * The BASE URL for the CSS
			 * @constant
			 * @type {String}
			 * @private
			 */
			CSS_BASE_URL = module.config().cssBaseUrl,
			/**
			 * The query string of the XSLT url is used as the query string for the CSS as it contains the version number and cache buster.
			 * @constant
			 * @type {String}
			 * @private
			 */
			CACHEBUSTER = module.config().cachebuster,
			/**
			 * Indicates if we are in debug mode.
			 * @var
			 * @type {boolean}
			 * @private
			 */
			isDebug = !!module.config().debug,
			/**
			 * The part of the CSS url which comes after the browser specific 'extension'.
			 * @var
			 * @type {String}
			 * @private
			 */
			cssFileNameAndUrlExtension = (isDebug ? "${debug.target.file.name.suffix}" : "") + ".css" + (CACHEBUSTER ? ("?" + CACHEBUSTER) : ""),
			/**
			 * Used to access keys in the screenStylesToAdd JSON object.
			 * @var
			 * @type {String}
			 * @private
			 */
			ext,
			/**
			 * The id of the main CSS link element produced in the XSLT. The browser specific CSS is added after this.
			 * @var
			 * @type {String}
			 * @private
			 */
			mainCss = document.getElementById("${wc_css_main_id}"),
			/**
			 * The DOM node immediately following the main CSS link element produced in the XSLT.
			 * @var
			 * @type {Node}
			 * @private
			 */
			sibling = mainCss ? mainCss.nextSibling : null;

		/**
		 * Create a link element for a particular stylesheet.
		 * @function
		 * @private
		 * @param {String} shortName The css file name without extension.
		 * @param {String} [media] An optional media query.
		 */
		function addStyle(shortName, media) {
			var id = "${wc_css_link_id_prefix}" + shortName,
				el,
				head;

			if (!document.getElementById(id)) {
				head = document.head || document.getElementsByTagName("head")[0];
				if (!head) {  // you gotta be kidding me...
					return;
				}
				el = document.createElement("link");
				el.id = id;
				el.type = "text/css";
				el.setAttribute("rel", "stylesheet");
				if (media) {
					el.setAttribute("media", media);
				}
				el.setAttribute("href", CSS_BASE_URL + shortName + cssFileNameAndUrlExtension);
				if (sibling) {
					head.insertBefore(el, sibling);
				}
				else {
					head.appendChild(el);
				}
			}
		}

		/**
		 * Write link elements for all required CSS files.
		 * @function module:wc/loader/style.load
		 */
		this.load = function() {
			var isMobile = has("ios") || has("android") || has("iemobile") || has("operamobi") || has("operamini") || has("bb"),
				IE_PREFIX = "ie",
				key,
				value,
				media,
				CSS_FILE_NAME = "${css.target.file.name}",// The common file name used to build the CSS files. The individual 'extension' extends this.
				i,
				next,
				vNum,
				j,
				version,
				_v;  // I hate IE8! All these vars are for the array iteration because I cannot rely on forEach being loaded in time.

			// add generic mobile or desktop styles before browser specific styles
			if (isMobile) {
				addStyle(CSS_FILE_NAME + "mob");
			}
			else {
				addStyle(CSS_FILE_NAME + "dt");
			}

			if (has("ie") || has("trident")) {
				if (module.config && module.config().ie) {
					ieVersionsToSupport = module.config().ie;
				}
				/*
				 * This module is loaded very early via XSLT and we cannot guarantee that IE8 has received, parsed and
				 * processed the whole compat layer. This makes it hard to catch some things but mostly foreEach is
				 * unreliable so I had to replace it with a simple iteration.
				 */
				for (i = 0; i < ieVersionsToSupport.length; ++i) {
					next = ieVersionsToSupport[i];
					vNum = next.match(/[0-9]{1,2}$/);
					if (vNum) {
						for (j = 0; j < vNum.length; ++j) {
							version = vNum[j];
							if (isNaN(version)) {
								break;
							}
							_v = version * 1;
							if (has("ie") && has("ie") <= _v) {
								addStyle(CSS_FILE_NAME + IE_PREFIX + version);
							}
							else if (_v >= 10) {
								/*
								 * WARNING... DANGER WILL ROBINSON
								 * ie10+ use trident version, which is non-linear compared to ieVerion but we are going
								 * to assume ONLY ie10 and maybe 11 need special CSS... This is a BAD assumption.
								 *
								 * Later... turns out to be not so bad since MS Edge does not identify as trident.
								 */
								if (has("trident") < 7) {
									addStyle(CSS_FILE_NAME + IE_PREFIX + "10");
								}
								else if (has("trident") <= _v - 4) {
									addStyle(CSS_FILE_NAME + IE_PREFIX + version);
								}
								else if (_v >= 11) {
									addStyle(CSS_FILE_NAME + IE_PREFIX + "11");
								}
							}
						}
					}
				}
			}
			if (screenStylesToAdd) {
				for (ext in screenStylesToAdd) {
					key = value = media = null;

					if (typeof screenStylesToAdd[ext] === "string") {
						if (has(screenStylesToAdd[ext])) {
							addStyle(CSS_FILE_NAME + ext);
						}
					}
					else {
						key = screenStylesToAdd[ext].test;
						value = screenStylesToAdd[ext].version;
						media = screenStylesToAdd[ext].media;
						if (value || value === 0) {
							if (has(key) <= value) {
								addStyle(CSS_FILE_NAME + ext, media);
							}
						}
						else if (has(key)) {
							addStyle(CSS_FILE_NAME + ext, media);
						}
					}
				}
			}
			if (isDebug) {
				addStyle("debug", "screen");
			}
		};
	}
	return /** @alias module:wc/loader/style */ new StyleLoader();


	/**
	 * @typedef {Object} module:wc/loader/style~configValueObject
	 * @property {String} test The string arg passed to has to sniff user agent, eg "safari" or "ff".
	 * @property {int} [version] The version of the browser to test. If set then the has test is compared to this
	 *    and is deemed successful if the browser version is <= version.
	 * @property {String} [media] A CSS media selector. If set then the CSS link will include this media selector
	 * @example
	 * // To test for Safari 8 or below and a screen with a lot of horizontal pixels:
	 * {
	 *   "test": "safari",
	 *   "version": 8,
	 *   "media": "@media only screen and (min-device-width:2560px)"
	 * }
	 */

	/**
	 * @typedef {Object} module:wc/loader/style~config
	 * @property {String} key The file name extension used in the CSS build. This is the bit immediately before the
	 *    '.css' part of the built artifact's file name (eg 'ff').
	 * @property {(String|module:wc/loader/style~configValueObject)} value The has test argument and optional comparison
	 *    value(s). If this property is a string then it is a simple has test. Otherwise see
	 *    {@link module:wc/loader/style~configValueObject}
	 * @example
	 * // the following includes Firefox of any version, Safari version 6, print styles for any
	 * // mac and styles for safari version 8 including a media selector for large screens:
	 * {
	 *   "ff": "ff",
	 *   "safari6": {
	 *      "test": "safari",
	 *      "version": 6
	 *   },
	 *   "macprint": {
	 *      "test": "mac",
	 *      "media": "print"
	 *   },
	 *   "saf8big": {
	 *      "test": "safari",
	 *      "version": 8,
	 *      "media": "@media only screen and (min-device-width:2560px)"
	 *   }
	 * }
	 */
});
