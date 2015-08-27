/**
 * Core module for web analytics integration: ultimately this is really just an AMD loaded wrapper.
 *
 * It is common for web analytics tools to place one or more JavaScript element(s) into the HTML and run all the scripts
 * synchronously. This can be problematic if it interferes with the AMD loader so we provide a helper which can be
 * overridden per implementation.
 *
 * The main aim of this module is to be able to load large, poorly written synchronous script files from third party
 * servers without running into too many issues with our asynchronous loader and with as little blocking as possible.
 * Finally, it should be able to be loaded on as as-needs basis so that applications, or even individual screens, which
 * do not need analytics do not have to load anything.
 *
 * The XSLT in this plugin is directly related to the JavaScript wrapper and provides a bunch of JSON objects which
 * can be used to track 'stuff'. We have made this purposely vague and general-purpose. If you use a clever analytics
 * tool which already has AMD loading and does not require the sort of massaging and help needed by (for example) IBM
 * Web Analytics, then simply do not build this plugin into your theme. Add the necessary scripts directly to an
 * override of the relevant XSLT or build a WComponent with a HTML renderer which can be placed into your output tree
 * wherever you need it.
 *
 * @todo we still need to build internal metrics gathering.
 *
 * @module ${analytics.core.path.name}/core
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/timers
 */
define(["wc/dom/initialise", "wc/dom/shed", "wc/timers"],
	/** @param initialise wc/dom/initialise @param shed wc/dom/shed @param timers wc/timers @ignore */
	function(initialise, shed, timers) {
		"use strict";

		/**
		 * A timer for delayed processing of the component registry.
		 * @var {int}
		 * @private
		 */
		var processTimer;

		/**
		 * Provides core but abstract analytics tool integration functionality. To provide an implementation of a
		 * particular web analytics package you must provide a module which extends this.
		 *
		 * @constructor
		 * @alias module:${analytics.core.path.name}/core~WebAnalytics
		 * @private
		 */
		function WebAnalytics() {}

		/**
		 * The page registry is used to store information about the current page. It is used in postInit to do page view
		 * tracking without relying on a synchronous public function.
		 * @var
		 * @type {Object}
		 * @protected
		 */
		WebAnalytics.prototype.page = null;

		/**
		 * The registry is used to store info about trackable objects. This is then used by the subclass to wire up any
		 * required tracking for a particular component. It is the glue between WComponents and the analytics libraries.
		 * Make this an empty object in your sub class to make it a concrete sub class if you need event/element
		 * tracking. NOTE: if you do not, and you rely on this.register([objArr]) your class <strong>will</strong> fail.
		 * @var
		 * @type {Object}
		 * @protected
		 * @abstract
		 */
		WebAnalytics.prototype.registry = null;

		/**
		 * We have allowed for up to five parameter types in the schema (four enumerated types and type not set). This
		 * allows us quite granular splitting of the name:value pairs based on these types.
		 *
		 * We may have to increase the types in the future but this more than covers most common web analytics tools
		 * currently around. IBM web analytics (formerly core metrics) for example requires two of these.
		 * @constant
		 * @protected
		 * @property {String} ONE The first custom parameter.
		 * @property {String} TWO The second custom parameter.
		 * @property {String} THREE The third custom parameter.
		 * @property {String} FOUR The fourth custom parameter.
		 */
		WebAnalytics.prototype.PARAM_TYPES = {
			ONE: "custom1",
			TWO: "custom2",
			THREE: "custom3",
			FOUR: "custom4"
		};

		/**
		 * A delay on calling the registry processing. Completely optional. If zero then it is not applied. If undefined
		 * it won't be missed. Use it is you need to wait a while before calling all the component level analytics code.
		 * @var
		 * @type {int}
		 * @protected
		 * @default 0
		 */
		WebAnalytics.prototype.processDelay = 0;

		/**
		 * Allow component level analytics.
		 * @var
		 * @type {boolean}
		 * @protected
		 */
		WebAnalytics.prototype.hasComponentAnalytics = false;

		/**
		 * Adds an object to the registry keyed on the objects id property.
		 * @function
		 * @private
		 * @param {Object} obj The JSON object being added.
		 */
		function _register(obj) {
			var id = obj.id;
			if (id) {
				this.registry[id] = obj;
			}
		}

		/**
		 * Registers controls which are to be tracked.
		 * @function
		 * @public
		 * @param {Object[]} arr An array of objects in the following format:
		 *	<code>{
		 *		"id": String The _tracked_ WComponent id,
		 *		"name": String The tracking name (optional) if not set then the
		 *				id is used for tracking,
		 *		"cat": String The tracking category (optional),
		 *		"events": String[] The events to track (optional) if this is not
		 *				set then all events are tracked. These are in the format
		 *				used by wc/dom/event.TYPE.
		 *		"params":Object a set of name:value pairs which are added to the
		 *				tracking info for the component.
		 *	}</code>
		 */
		WebAnalytics.prototype.register = function(arr) {
			var $this;
			if (!Array.isArray(arr)) {
				arr = [arr];
			}
			arr.forEach(_register, this);

			if (arr.length && typeof this.processRegistry === "function") {
				$this = this;  // just makes the binding a bit clearer
				initialise.addCallback(function() {
					if ($this.processDelay) {
						if (processTimer) {
							timers.clearTimeout(processTimer);
						}
						processTimer = timers.setTimeout($this.processRegistry.bind($this), $this.processDelay);
					}
					else {
						$this.processRegistry();
					}
				});
			}
		};

		/**
		 * Registers controls which are to be tracked.
		 *
		 * @function
		 * @public
		 * @param {Object} obj A single object in the following format:
		 * <code>{
		 *		"name": String The tracking name (required),
		 *		"cat": String The tracking category (optional),
		 *		"search": String the search terms (optional),
		 *		"results": int the search results (optional),
		 *		"params":Object a set of name:value pairs which are added to the
		 *				tracking info for the component (optional),.
		 *	}</code>
		 */
		WebAnalytics.prototype.registerPage = function(obj) {
			this.page = obj;
		};

		/**
		 * If you want page-view-level analytics your sub class will need one of these.
		 *
		 * @function
		 * @protected
		 * @abstract
		 */
		WebAnalytics.prototype.trackPageView = null;

		/**
		 * If you want component-level analytics rather than just page-level your sub class will need one of these. It
		 * takes items out of the registry and does stuff with them. It will need to be invoked in initialise and as a
		 * post-insertion ajax subscriber. All concrete sub classes will, therefore, have to require and register with
		 * {@link module:wc/dom/initialise}. It will only work if you have set {@link hasComponentAnalytics} to true.
		 * Make sure your processor removes items as they are processed otherwise you may end up sending multiple
		 * tacking signals if you use AJAX.
		 *
		 * @function
		 * @protected
		 * @abstract
		 */
		WebAnalytics.prototype.processRegistry = null;

		/**
		 * Subscriber to {@link module:wc/dom/shed} to do component level analytics on SHOW.
		 *
		 * @function
		 * @protected
		 * @abstract
		 */
		WebAnalytics.prototype.shedSubscriber = null;

		/**
		 * Initialise routine. Used to process the registry and set up some subscribers if they are defined.
		 * @function
		 * @public
		 */
		WebAnalytics.prototype.postInit = function() {
			if (this.trackPageView) {
				this.trackPageView();
			}
			if (this.hasComponentAnalytics) {
				if (this.shedSubscriber) {
					shed.subscribe(shed.actions.SHOW, this.shedSubscriber.bind(this));
				}
				// anything else???
			}
		};


		var /** @alias module:${analytics.core.path.name}/core */ webAnalytics = new WebAnalytics();

		if (typeof Object.freeze !== "undefined") {
			Object.freeze(webAnalytics);
		}
		return webAnalytics;
	});
