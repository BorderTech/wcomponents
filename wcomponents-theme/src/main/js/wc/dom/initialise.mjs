/**
 * A module which provides initialisation mechanisms so that other (ui) modules can do stuff when the DOM is ready.
 * Basically a glorified Observer subscriber with a few smarts.
 */

import Observer from "wc/Observer.mjs";
import timers from "wc/timers.mjs";
import eventMgr from "wc/dom/event.mjs";

let observer,
	queue;

const instance = {
	/**
	 * Has the DOM been loaded yet?
	 *
	 * @var module:wc/dom/initialise.domLoaded
	 * @type Boolean
	 */
	domLoaded: false,

	/**
	 * Register an initialise routine.
	 *
	 * @function  module:wc/dom/initialise.register
	 * @param {Object} control An instance of a component which is being initialised.
	 * @returns {Object} The control that was passed in.
	 */
	register: function(control) {
		if (control.initialise) {
			this.addBodyListener(control);
		}
		if (control.postInit) {
			this.addCallback(control.postInit);
		}
		if (control.preInit) {
			this.addInitRoutine(control.preInit);
		}
		return control;
	},

	/**
	 * Execute the initialisation routines (init routines, bodylisteners, callbacks) NOTE: all routines are executed
	 * ONLY ONCE and are purged after execution. It is safe for a subscriber to add new subscribers.
	 * @todo This function is public for use by the domLoaded callback, it should not be called directly. Maybe
	 * a rename is called for?
	 *
	 * @function  module:wc/dom/initialise.go
	 * @param {HTMLBodyElement} element document.body
	 * @param {Function} [callback] Function which will be called after all the routines are executed.
	 */
	go: function(element, callback) {
		const goingObserver = observer,
			gone = function() {
				try {
					if (callback && typeof callback === "function") {
						callback();
					}
				} finally {
					if (observer === null) {  // if no new subscribers were added while were were executing the existing subscribers
						goingObserver.reset();  // clear all the subscribers we have just finished calling
						observer = goingObserver;  // put the empty observer instance back ready for new subscribers
					}
				}
			};
		if (goingObserver) {
			observer = null;  // any calls to add while executing will be placed into a new observer
			goingObserver.notify(element).then(gone, gone);
		}
	},

	/**
	 * Get a meaningful String representation of the subscribers.
	 *
	 * @function module:wc/dom/initialise.toString
	 * @public
	 * @returns {String}
	 */
	toString: () => observer?.toString() || "no subscribers",

	/**
	 * Add a subscriber for the earliest phase of initialisation.
	 *
	 * @function module:wc/dom/initialise.addInitRoutine
	 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
	 */
	addInitRoutine: add.bind(this, Observer.priority.HIGH, null),

	/**
	 * Add a subscriber for the middle phase of initialisation.
	 *
	 * @function module:wc/dom/initialise.addBodyListener
	 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
	 */
	addBodyListener: add.bind(this, Observer.priority.MED, "initialise"),

	/**
	 * Add a callback subscriber for the last phase of initialisation.
	 *
	 * @function module:wc/dom/initialise.addCallback
	 * @see {@link module:wc/dom/initialise~Initialise~add} for documentation.
	 */
	addCallback: add.bind(this, Observer.priority.LOW, null)
};

domReady(function() {
	if (globalThis.document) {
		timers.setTimeout(function() {
			try {
				instance.go(globalThis.document.body);
			} finally {
				instance.domLoaded = true;
			}
		}, 0);
	}
});

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
 * @returns {Function} Returns listener if it was able to be subscribed to an instance of {@link module:wc/Observer}.
 */
function add(priority, method, listener) {
	let result;
	const config = { priority: priority, method: method };
	if (observer || (observer = new Observer(true))) {
		if (listener && (typeof listener === "function" || method)) {
			if (instance.domLoaded) {
				// if the page has already loaded
				queueGo();
			}
			result = observer.subscribe(listener, config);
		} else {
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
	queue = timers.setTimeout(instance.go, 100, globalThis.document.body);
}

/**
 * With all the old IE pain behind us this should work in every browser.
 * @param {Function} cb Called when dom is interactive / loaded.
 */
function domReady(cb) {
	if (window?.document?.readyState !== "loading") {
		cb(window.document);
	} else if (window?.addEventListener) {
		eventMgr.add(window, "DOMContentLoaded", cb);
	}
}

export default instance;
