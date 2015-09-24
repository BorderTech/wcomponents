/**
 * Provides integration of IBM Digital Analytics (Core Metrics) into WComponents.
 *
 * * Module Configuration:*
 *
 * The modules is configured from a transform for ui:application {@link module:${analytics.core.path.name}/ibmcm~config}.
 *
 * The config also shims the eluminate.js file as '${analytics.core.module.name}' and exports one of its globals. We
 * will not be using the exported global here (by default it is ${analytics.core.shim.global}). This is then used as a
 * dependency so we know that the eluminate.js file is loaded before we start using it.
 *
 * @module ${analytics.core.path.name}/ibmcm
 * @extends module:${analytics.core.path.name}/core
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/dialog
 * @requires module:${analytics.core.path.name}/core
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/shed",
		"wc/ui/dialog",
		"${analytics.core.path.name}/core",
		"${analytics.core.shim.file}/${analytics.core.shim.file}",  // yes, this is right, we set the shimmed file's path to a loader path with the file's own name.
		"module"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param dialog wc/ui/dialog @param core ${analytics.core.path.name}/core @param shim ${analytics.core.shim.file}/${analytics.core.shim.file} @param module module @ignore */
	function(initialise, Widget, shed, dialog, core, shim, module) {
		"use strict";
		/*
		 * Unused dependencies
		 * shim is absolutely required. In the case of IBM Web Analytics it exports a bunch of globals which we call
		 * from this module. It is really old-school unfettered "let's put everything in the global namespace" code
		 * and makes for seriously uninteresting reading!
		 */


		/**
		 * @constructor
		 * @alias module:${analytics.core.path.name}/ibmcm~IBMWebAnalytics
		 * @extends module:${analytics.core.path.name}/core~WebAnalytics
		 * @private*/
		function IBMWebAnalytics() {
			var
				/**
				 * This is the default data collection domain for ibmcm for third party managed data collection (i.e.
				 * data collection by IBM). We use this to set the dataCollectionMethod by comparing it to the passed in
				 * configuration value for {@code module.config().dcd}.
				 * @constant {String}
				 * @default "data.coremetrics.com"
				 * @private
				 */
				IBM_DCD = "data.coremetrics.com",  // NOTE TO SELF: do not set to the property ${analytics.ibm.dataCollectionDomain} 'cause that would make it pointless.

				/**
				 * This is a really pointless parameter required by the IBM client set up script. Basically if this is
				 * false then the data collection is done by IBM. If it is true then the data collection is done by the
				 * application/site owner. It is pointless because the data collection domain also has to be set and if
				 * this is false then the dcd MUST be 'data.coremetrics.com' whereas if it is true then the dcd MUST be
				 * set to a domain which is NOT 'data.coremetrics.com'.
				 *
				 * Set to true for 1st party managed but it is recalculated by comparing {@link IBM_DCD} with the
				 * module.config().dcd (or default from build-time dataCollectionDomain) in {@link setClientId}.
				 *
				 * @var {boolean}
				 * @default false
				 * @private
				 */
				dataCollectionMethod = false,

				/**
				 * The data point separator used by IBM Core Metrics.
				 * @constant {String}
				 * @default "-_-"
				 * @private
				 */
				SEPARATOR = "-_-",

				// SOME RESTRICTIONS IN IBM'S ANALYTICS TOOL:
				/**
				 * The maximum number of allowed attributes for page tracking.
				 * @constant {int}
				 * @default
				 * @private
				 */
				MAX_ATTRIB = 50,

				/**
				 * The maximum number of allowed fields for page tracking.
				 * @constant {int}
				 * @default
				 * @private
				 */
				MAX_FIELD = 15,

				/**
				 * The maximum length of an individual argument in several of the IBM comma-separated arg lists.
				 * @constant {int}
				 * @default
				 * @private
				 */
				ARG_MAX_LENGTH = 256,

				/**
				 * The maximum length of each data point in attributes and fields.
				 * @constant {int}
				 * @default
				 * @private
				 */
				ATTRIB_MAX_LENGTH = 100,
				// END OF IBM RESTRICTIONS.

				/**
				 * If populated will hold a {@link module:wc/dom/Widget} descriptor for a WDialog. Set at first use.
				 * @see {@link module:wc/ui/dialog.getWidget}
				 * @var {wc/dom/Widget}
				 * @private
				 */
				DIALOG,

				/**
				 * If populated will hold a {@link module:wc/dom/Widget} descriptor for a HTML form. Set at first use.
				 * @var {wc/dom/Widget}
				 * @private
				 */
				FORM,

				/**
				 * An attribute placed on components which take part in element (i.e. 'I am here') tracking.
				 * @constant {String}
				 * @default
				 * @private
				 */
				ATTRIB_TRACK = "${analytics.core.attribute.shouldTrack}",

				/**
				 * You would be surprised how often we have to type "true" (rather than true).
				 * @constant {String}
				 * @default
				 * @private
				 */
				TRUE = "true",

				/**
				 * If populated will hold a {@link module:wc/dom/Widget} descriptor for all trackable components.
				 * Set at first use.
				 * @var {wc/dom/Widget}
				 * @private
				 */
				TRACKABLES;

			/**
			 * To make a useful concrete class we need a registry. This holds the objects which define trackable
			 * components within the UI.
			 * @var registry {Object}
			 * @protected
			 * @override
			 */
			this.registry = {};

			/**
			 * Allow component level analytics.
			 * @var {boolean}
			 * @override
			 * @protected
			 */
			this.hasComponentAnalytics = true;

			/**
			 * Delay component processing by 100 milliseconds just to give the initialisation room to breathe.
			 * @var {int}
			 * @protected
			 * @override
			 */
			this.processDelay = 100;

			/**
			 * Helper function for {@link trackPageView}. Parses the param array within an object.
			 *
			 * @function
			 * @private
			 * @param {Object[]} params The params to parse.
			 * @param {int} [limit] Some calls require a different character limit on attributes, this is set here.
			 * @param {Boolean} [ignoreType] If true then all params are considered attribute params.
			 * @returns {Object} An object with properties: <code>{a:String,f:String}</code>
			 */
			function parseParams(params, limit, ignoreType) {
				var lenA = 0, lenF = 0,
					attribLimit = limit || MAX_ATTRIB,
					result = {a: "", f: ""},
					_params = Array.isArray(params) ? params : [params];

				_params.forEach(function (param) {
					if (ignoreType || (!param.type || param.type === this.PARAM_TYPES.ONE)) {
						if (lenA < attribLimit) {
							if (lenA) {
								result.a += SEPARATOR;
							}
							++lenA;
							result.a += param.value.substr(0, ATTRIB_MAX_LENGTH);
						}
					}
					else if (param.type === this.PARAM_TYPES.TWO) {
						if (lenF < MAX_FIELD) {
							if (lenF) {
								result.f += SEPARATOR;
							}
							++lenF;
							result.f += param.value.substr(0, ATTRIB_MAX_LENGTH);
						}
					}
				});
				return result;
			}

			/**
			 * Wrapper for Core Metrics function "cmSetClientID". This function has arguments:
			 * <dl>
			 * <dt>clientId</dt><dd><code>{String}</code> The IBM Client Id. This is any one of:
			 * <ul><li>a numeric String (not a number) e.g. "12345678"</li>
			 *     <li>a series of such strings separated by semi-colons e.g. "12345678;98765432"</li>
			 *     <li>a numeric String and a sub-id separated by a pipe (|) e.g. "12345678|mySiteSubId"</li>
			 *     <li>any combination of the above e.g. "12345678;98765432|mySubSite1;88888888|otherSubSite1".</li></ul></dd>
			 * <dt>dataCollectionMethod</dt><dd><code>{boolean}</code> false for IBM hosted collection (see dataCollectionMethod above).</dd>
			 * <dt>dataCollectionDomain</dt><dd><code>{String}</code> the domain used to collect data (see dataCollectionMethod above).</dd>
			 * <dt>cookieDomain</dt><dd><code>{String}</code> The domain used when setting/getting cookies used in tracking.</dd></dl>
			 *
			 * @function
			 * @private
			 * @inner
			 */
			function setClientId() {
				var func = "cmSetClientID",
					clientId, dataCollectionDomain, cookieDomain,
					conf = module.config();

				if (typeof window[func] === "function") {
					clientId = "${analytics.ibm.clientId.default}";
					dataCollectionDomain = "${analytics.ibm.dataCollectionDomain}";
					cookieDomain = "${analytics.core.cookieDomain}";
					conf = module.config();

					if (conf) {
						if (conf.clientId) {
							clientId = conf.clientId;
						}
						if (conf.dcd) {
							dataCollectionDomain = conf.dcd;
						}
						if (conf.cd) {
							cookieDomain = conf.cd;
						}
					}
					// set the dataCollectionMethod, it only has to be set once.
					dataCollectionMethod = dataCollectionDomain !== IBM_DCD;

					window[func](clientId, dataCollectionMethod, dataCollectionDomain, cookieDomain);
				}
			}

			/**
			 * Wrapper for Core Metrics function "cmCreateElementTag". This function has arguments:
			 * <dl>
			 * <dt>title</dt><dd><code>{String}</code> The name for the tracked element, maximum length is 256 and this
			 * argument is required.</dd>
			 * <dt>category</dt><dd><code>{String}</code> The category of the tracked element, maximum length is 256 and
			 *  this argument is optional.</dd>
			 * <dt>attribs</dt><dd><code>{String}</code> Attributes of the tracked element. This consists of a maximum
			 * of 50 values separated by "-_-" with a max of 100 chars per value. The individual entries are position-
			 * sensitive so it is extremely unlikely that this will be of any value in an abstract form as the application
			 * developer will need intimate knowledge of the analytics attribute set-up for the particular application
			 * to be able to put any required attributes intot eh right position within 50 separators. Only IBM could
			 * think this is a good idea.</dd></dl>
			 *
			 * @function
			 * @private
			 * @param {Object} obj A JSON object from the registry.
			 * @param {String} id The id of the element to track.
			 */
			function callcmCreateElementTag(obj, id) {
				var name, cat, params, attribs,
					func = "cmCreateElementTag",
					// limits on arg length in cmCreateElementTag
					ARG_LENGTH_LIMIT = 50,
					ATTRIB_LIMIT = 50;

				try {
					if (typeof window[func] === "function") {
						name = obj.name || id;
						cat = obj.cat || "";
						name = name.substr(0, ARG_LENGTH_LIMIT);
						if (cat) {
							cat = cat.substr(0, ARG_LENGTH_LIMIT);
						}

						if ((params = obj.params)) {
							params = parseParams(params, ATTRIB_LIMIT, true);
							attribs = params.a;
						}
						else {
							attribs = "";
						}
						window[func](name, cat, attribs);
					}
				}
				finally {
					obj = null;
					delete instance.registry[id];
				}
			}

			/**
			 * Track an element if it is not hidden.
			 * NOTE: if a component is hidden then do not track it until it is shown.
			 *
			 * @function
			 * @private
			 * @param {String[]} idArr An array of ids of trackable elements.
			 */
			function setUpElementTracking(idArr) {
				idArr.forEach(function (next) {
					var obj, element;
					if (next && (obj = instance.registry[next]) && (element = document.getElementById(next))) {
						if (!(shed.isHidden(element) || shed.hasHiddenAncestor(element))) {
							element.removeAttribute(ATTRIB_TRACK);
							callcmCreateElementTag(obj, next);
						}
						else {
							element.setAttribute(ATTRIB_TRACK, TRUE);
						}
					}
				});
			}

			/**
			 * Get the containing form or dialog id for use by createClickTrack.
			 *
			 * @function
			 * @private
			 * @param {String} id The id of the element we are tracking.
			 * @returns {String} The id of the nearest dialog or form ancestor (in that order).
			 */
			function getPageId(id) {
				var element = document.getElementById(id),
					ancestor, ancestorId = "";

				if (element) {
					DIALOG = DIALOG || dialog.getWidget();
					FORM = FORM || new Widget("form");
					if ((ancestor = (DIALOG.findAncestor(element) || FORM.findAncestor(element)))) {
						ancestorId = ancestor.id;
					}
				}
				return ancestorId;
			}

			/**
			 * Wrapper for Core Metrics function "cmCreateManualLinkClickTag". This function has arguments:
			 * <dl>
			 * <dt>href</dt><dd><code>{String}</code> The 'url' used as the tracking target (note: this function is used
			 * to track clicks on items which are not links by pretending they are links), maximum length 256, required
			 * </dd>
			 * <dt>name</dt><dd><code>{String}</code> The name of the clickable component being tracked, maximum length
			 * 256, optional.</dd>
			 * <dt>pageId</dt><dd><code>{String}</code> This is needed if the page has ajax regions with 'page' tracking
			 *  or dialogs so we derive it anyway as it does no harm in all other cases. Maximum 256 optional.</dd></dl>
			 *
			 * @function
			 * @private
			 * @param {Object} obj A JSON object from the registry.
			 */
			function createClickTrack(obj) {
				var func = "cmCreateManualLinkClickTag",
					id,
					name,
					pageId;

				try {
					if (typeof window[func] === "function" && (id = obj.id)) {
						name = obj.name || "";
						pageId = getPageId(id);
						window[func](id, name, pageId);
					}
				}
				finally {
					delete instance.registry[id];
				}
			}

			/**
			 * Wrapper for Core Metrics function "cmCreatePageviewTag". This function has arguments:
			 * <dl>
			 * <dt>pageId</dt><dd><code>{String}</code> The unique 'page' identifier, max 256.</dd>
			 * <dt>categoryId</dt><dd><code>{String}</code> The category for the page, max 256.</dd>
			 * <dt>searchTerm</dt><dd><code>{String}</code> The search term if provided, max 256.</dd>
			 * <dt>searchResults</dt><dd><code>{String}</code> The number of results (if on a search results page), yet
			 * another number presented as a string and <strong>MUST BE QUOTED!</strong>, max 10.</dd>
			 * <dt>attributes</dt><dd><code>{String}</code> These are string attribute values and may be up to 50 values
			 * of 100 chars per value separated by "-_-"</dd>
			 * <dt>fields</dt><dd><code>{String}</code> These are string values and may be up to 15 values of 100 chars
			 *  per value separated by "-_-".</dd></dl>
			 *
			 * @function module:${analytics.core.path.name}/ibmcm.trackPageView
			 * @protected
			 * @override
			 */
			this.trackPageView = function () {
				var obj = this.page,
					name, cat, term, results, params, parsedParams,
					attribs = "",
					fields = "",
					func = "cmCreatePageviewTag";

				try {
					if (obj && typeof window[func] === "function") {
						name = obj.name;
						cat = obj.cat || "";
						term = obj.search || "";
						params = obj.params;

						// truncate any over-size data
						name = name.substr(0, ARG_MAX_LENGTH);
						cat = cat.substr(0, ARG_MAX_LENGTH);
						term = term.substr(0, ARG_MAX_LENGTH);

						// this is a number but it is sent as a string with a max length of 10 characters!
						results = ((obj.results || obj.results === 0) ? obj.results.toString() : "");
						if (results && results.length > 10) {
							results = "9999999999";
						}

						if (params) {
							parsedParams = parseParams(params);
							attribs = parsedParams.a || "";
							fields = parsedParams.f || "";
						}

						window[func](name, cat, term, results, attribs, fields);
					}
				}
				finally {
					obj = this.page = null;
				}
			};

			/**
			 * Subscriber for shed.actions.SHOW to make a call to track 'element' tracking enabled components when an
			 * element is shown. We need to track all visible components inside the container being shown as well as the
			 * container itself if it has element tracking.
			 * @function module:${analytics.core.path.name}/ibmcm.shedSubscriber
			 * @protected
			 * @override
			 * @param {Element} element The HTML element which is being shown.
			 */
			this.shedSubscriber = function (element) {
				var obj, id, idArr = [];
				if (element && (id = element.id)) {
					if ((obj = this.registry[id])) {
						callcmCreateElementTag(obj, id);
					}
					// get all trackable elements inside the element and
					TRACKABLES = TRACKABLES || new Widget("", "", {"${analytics.core.attribute.shouldTrack}": TRUE});
					Array.prototype.forEach.call(TRACKABLES.findDescendants(element), function (next) {
						var _el, _id = next.id;
						if (_id && (_el = document.getElementById(_id)) && !(shed.isHidden(_el) || shed.hasHiddenAncestor(_el))) {
							idArr.push(_id);
						}
					});

					if (idArr.length) {
						setUpElementTracking(idArr);
					}
				}
			};

			/**
			 * Send the processing calls to track in-page components. The registry contains 0 or more json objects which
			 * represent components to be tracked.
			 *
			 * @function
			 * @protected
			 * @override
			 */
			this.processRegistry = function() {
				var o, obj,
					_reg = this.registry,  // just for laziness and better compression
					idArray = [];
				for (o in _reg) {
					obj = null;
					if (_reg.hasOwnProperty(o)) {
						obj = _reg[o];
					}
					if (obj) {
						if (obj.type === "${analytics.core.track.type.event}") {
							createClickTrack(obj);
						}
						else {
							idArray.push(o);
						}
					}
				}
				if (idArray.length) {
					setUpElementTracking(idArray);
				}
			};

			/**
			 * Late initialisation to set up the tracking code. We are happy to delay initialisation of this module for
			 * two reasons:
			 * <ol><li>it reduces load in real initialisation; and</li>
			 * <li>it gives us a few more milliseconds to fetch the core library from IBM so we do not have to wait so
			 * long to complete our initialisation phase.</li></ol>
			 *
			 * @function module:${analytics.core.path.name}/ibmcm.postInit
			 * @override
			 */
			this.postInit = function() {
				// set up clientId
				setClientId();
				instance.constructor.prototype.postInit.call(instance);
			};
		}

		IBMWebAnalytics.prototype = core;

		var /** @alias module:${analytics.core.path.name}/ibmcm */ instance = new IBMWebAnalytics();
		instance.constructor = IBMWebAnalytics;
		initialise.register(instance);
		return instance;

	/**
	 * The module.config() object.
	 * @typedef module:${analytics.core.path.name}/ibmcm~config
	 * @property {String} [clientId=${analytics.ibm.clientId.default}] The IBM clientId with optional siteId. The
	 *    default is usually sufficient.
	 * @property {String} [cd=${analytics.ibm.dataCollectionDomain}] The cookie domain. The default is usually
	 *    sufficient.
	 * @property {String} [dcd=${analytics.core.cookieDomain}] The domain collection domain. Should never need to be set
	 *    and it would be VERY unusual to reset this during an application release's lifetime.
	 */
	});
