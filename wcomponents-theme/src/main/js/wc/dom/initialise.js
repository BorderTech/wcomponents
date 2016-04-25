/**
 * A module which provides initialisation mechanisms so that other (ui) modules can do stuff when the DOM is ready.
 * Basically a glorified Observer subscriber with a few smarts.
 *
 * @module
 * @requires module:wc/Observer
 * @requires module:wc/timers
 * @requires external:lib/dojo/domReady
 * @todo re-order code, document private members.
 */
define(["wc/Observer", "wc/timers", "lib/dojo/domReady"],
	/** @param Observer wc/Observer @param timers wc/timers @param domReady lib/dojo/domReady @ignore */
	function(Observer, timers, domReady) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/initialise~Initialise
		 * @private
		 */
		function Initialise() {
			var observer,
				queue;

			/**
			 * Has the DOM been loaded yet?
			 *
			 * @var module:wc/dom/initialise.domLoaded
			 * @type Boolean
			 */
			this.domLoaded = false;

			/**
			 * Register an initialise routine.
			 *
			 * @function  module:wc/dom/initialise.register
			 * @param {Object} control An instance of a component which is being initialised.
			 */
			this.register = function(control) {
				if (control.initialise) {
					this.addBodyListener(control);
				}
				if (control.postInit) {
					this.addCallback(control.postInit);
				}
				if (control.preInit) {
					this.addInitRoutine(control.preInit);
				}
			};

			/**
			 * Add a subscriber to the initialise observer. Bound to one of the following public functions:
			 *
			 * * {@link module:wc/dom/initialise.addBodyListener}: Listeners will be called back with the body
			 * element when it becomes ready.
			 * * {@link module:wc/dom/initialise.addInitRoutine}: Add a function that will be executed BEFORE the
			 * initialise does its nodeListener initialisation work
			 * * {@link module:wc/dom/initialise.addCallback}: add a function that will be executed AFTER the
			 * initialise does its nodeListener initialisation work
			 *
			 * @function
			 * @private
			 *
			 * @param {module:wc/Observer#PRIORITY} priority High is init routines, medium is bodylisteners, low is
			 *    callbacks.
			 * @param {String} [method] The name of the method to call if listener is an object rather than a function.
			 * @param {(Function|Object)} listener A function or an object which implements the "initialise" interface.
			 * @returns {?Function} Returns listener if it was able to be subscribed to an instance of {@link module:wc/Observer}.
			 */
			function add(priority, method, listener) {
				var result,
					config = {priority: priority, method: method};
				if (observer || (observer = new Observer())) {
					if (listener && (typeof listener === "function" || method)) {
						if (instance.domLoaded) {
							// if the page has already loaded
							queueGo();
						}
						result = observer.subscribe(listener, config);
					}
					else {
						console.error("Could not add ", listener);
						result = null;
					}
				}
				return result;
			}

			/**
			* Call this instead of "go" directly when a short delay is not critical
			* (e.g. any time after page load - i.e. handling ajax responses)
			*
			* @function
			* @private
			*/
			function queueGo() {
				if (queue) {
					timers.clearTimeout(queue);
				}
				queue = timers.setTimeout(instance.go, 100, document.body);
			}

			/**
			* Execute the initialisation routines (init routines, bodylisteners, callbacks) NOTE: all routines are executed
			* ONLY ONCE and are purged after execution. It is safe for a subscriber to add new subscribers.
			* @todo This function is public for use by the domLoaded callback, it should not be called directly. Maybe
			* a rename is called for?
			*
			* @function  module:wc/dom/initialise.go
			* @param {Element} element document.body
			* @param {Finction} [callback] Function which will be called after all the routines are executed.
			*/
			this.go = function(element, callback) {
				var goingObserver = observer;
				try {
					if (goingObserver) {
						observer = null;  // any calls to add while executing will be placed into a new observer
						goingObserver.notify(element);
						if (callback && typeof callback === "function") {
							callback();
						}
					}
				}
				finally {
					if (observer === null) {  // if no new subscribers were added while were were executing the existing subscribers
						goingObserver.reset();  // clear all the subscribers we have just finished calling
						observer = goingObserver;  // put the empty observer instance back ready for new subscribers
					}
				}
			};

			/**
			 * Get a meaningful String representation of the subscribers.
			 *
			 * @function module:wc/dom/initialise.toString
			 * @public
			 * @returns {String}
			 */
			this.toString = function() {
				var result;
				if (observer) {
					result = observer.toString();
				}
				else {
					result = "no subscribers";
				}
				return result;
			};

			/**
			 * Add a subscriber for the earliest phase of initialisation.
			 *
			 * @function module:wc/dom/initialise.addInitRoutine
			 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
			 */
			this.addInitRoutine = add.bind(this, Observer.priority.HIGH, null);

			/**
			 * Add a subscriber for the middle phase of initialisation.
			 *
			 * @function module:wc/dom/initialise.addBodyListener
			 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
			 */
			this.addBodyListener = add.bind(this, Observer.priority.MED, "initialise");

			/**
			 * Add a callback subscriber for the last phase of initialisation.
			 *
			 * @function module:wc/dom/initialise.addCallback
			 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
			 */
			this.addCallback = add.bind(this, Observer.priority.LOW, null);
		}

		var /** @alias module:wc/dom/initialise */ instance = new Initialise();

		domReady(function() {
			timers.setTimeout(function() {
				try {
					instance.go(document.body);
				}
				finally {
					instance.domLoaded = true;
				}
			}, 0);
		});

		return instance;
	});
