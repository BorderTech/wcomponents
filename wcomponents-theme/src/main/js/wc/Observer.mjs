import escapeRe from "wc/string/escapeRe.mjs";

const SORT_IDX_PROP = "__sort__",
	FUNCTION = "function";

/**
 * General purpose Publish/Subscribe implementation.
 *
 * After instantiating an observer instance subscribe using {@link module:wc/Observer#subscribe} and publish using
 * {@link module:wc/Observer#notify}
 * @param {boolean} [notifyInStages] If true then lower priority subscribers will only be notified once all higher
 *     subscribers in a higher priority have resolved. This only makes sense when subscribers return a promise.
 *     Beware that it effectively makes the notify call asynchronous!
 * @constructor
 * @alias module:wc/Observer
 * @example  // the simplest usage
 * var observer = new Observer();
 * observer.subscribe(window.alert);
 * observer.notify("foo");  // alerts "foo"
 */
function Observer(notifyInStages) {
	const registry = new SubscriberRegistry(),
		compiledGroups = {};  // this results in HUGE performance benefits, up to 800ms saved in FF3.5 (on a 1050ms page with profiling turned on)
	let filterFn = null,
		callback = null;

	/**
	 * Unsubscribe from this Observer instance.
	 *
	 * @function
	 * @public
	 * @param {{ ref: Subscriber, grp: string } | Array<{ ref: Subscriber, grp: string }>} subscriber The subscriber (as passed to the subscribe method).
	 *    Alternatively simply pass the result from the subscribe method.
	 *    You may also pass an array of results from the subscribe method, it will be emptied which is likely what you want.
	 * @param {String} [group] The group from which to unsubscribe (otherwise defaults will be used). There is
	 *    currently no way to remove a subscriber from all groups without multiple calls.
	 */
	this.unsubscribe = function(subscriber, group) {
		if (Array.isArray(subscriber)) {
			while (subscriber.length) {
				this.unsubscribe(subscriber.pop());
			}
		} else if (subscriber) {
			const ref = subscriber.ref || subscriber;
			const grp = subscriber.grp || group;
			registry.deregister(ref, grp);
		}
	};

	/**
	 * Remove all subscribers and reset the "publish registry".
	 * @function
	 * @public
	 * @see SubscriberRegistry#reset
	 */
	this.reset = registry.reset;

	/**
	 * Get the number of subscribers to this publisher.
	 * @function
	 * @public
	 * @see SubscriberRegistry#subscriberCount
	 */
	this.subscriberCount = registry.subscriberCount;

	/**
	 * Is a particular subscriber already registered?
	 * @function
	 * @public
	 * @see SubscriberRegistry#isRegistered
	 */
	this.isSubscribed = registry.isRegistered;

	/**
	 * Get the subscriber registry as a string.
	 * @function
	 * @public
	 * @see SubscriberRegistry#toString
	 */
	this.toString = registry.toString;

	/**
	 * Subscribe to this Observer instance.
	 *
	 * Note that duplicate instances of a subscriber cannot be added to the same "group"
	 * (see the `config.group` argument below).
	 *
	 * @function
	 * @public
	 * @param {function|Object} subscriber A callback which will be called when the notify method is called or
	 *    an object which provides a public method with the name specified in config.method.
	 * @param {Object} [config] An object containing configuration option.
	 * @param {String} [config.group] Associate the subscriber with the given group. When notify is called the
	 *    subscribers to be notified can be filtered based on their group. Default value is DEFAULT_GROUP. See
	 *    {@link module:wc/Observer#setFilter} and {@link module:wc/Observer#notify} for more info.
	 * @param {Object|function} [config.context] When the subscriber is called its "this" reference will be the
	 *    "context" object if provided; otherwise if "method" is set will be "subscriber" else it will be default
	 *    scope (probably window).
	 * @param {Observer.priority} [config.priority] When subscribers are being notified all subscribers that were added as important
	 *    will be notified before non-important subscribers. Default value is observer.priority.MED. Accepts any of
	 *    these:
	 *    <ul>
	 *        <li>observer.priority.HIGH</li>
	 *        <li>observer.priority.MED</li>
	 *        <li>observer.priority.LOW</li>
	 *    </ul>
	 * @param {String} [config.method] Name of public method to call on this subscriber (e.g. if this is set to "bar" will
	 *    call subscriber.bar()). This allows you to subscribe "dynamic" listeners where the function called can
	 *    change from that you originally registered (or it need not even exist at the time you registered). As long
	 *    as the correct interface is present at the time it is called then all's good. Note this is especially
	 *    useful in that the function does not have to exist on the object itself, it could be anywhere up the
	 *    prototype chain.
	 * @returns {{ ref: Subscriber, grp: string }} a reference to the subscriber.
	 * @example
	 *    var dog = {bark:function() {console.log("woof!");}},
	 *        observer = new Observer();
	 *    observer.subscribe(dog, {group: "canine", context: dog, priority: Observer.priority.HIGH, method: "bark"});
	 */
	this.subscribe = function(subscriber, config) {
		let result;
		const {
			group = "",
			context = null,
			priority = Observer.priority.MED,
			method = ""
		} = (config || {});
		if (subscriber) {
			if (!registry.isRegistered(subscriber, group)) {
				registry.register(new Subscriber(subscriber, context, method), group, priority);
			}
			result = {
				ref: subscriber,
				grp: group
			};
		} else {
			throw new ReferenceError("Call to Observer.subscribe without a subscriber");
		}
		return result;
	};

	// noinspection GrazieInspection
	/**
	 * Call all subscribers with the arguments passed to notify. The arguments provided will be transparently
	 * passed on to subscribers.
	 *
	 * By default, only subscribers added without a group (i.e. they are part of the DEFAULT_GROUP group) will be
	 * notified. To notify subscribers from other groups you first need call setFilter.
	 *
	 * Subscribers are notified in order of priority, first "important" then "medium/default" finally "low".
	 * Within each priority grouping subscribers are notified in the order they were added.
	 *
	 * After calling notify:
	 *
	 * * Any filter set by setFilter will be cleared.
	 * * Any callback set by setCallback will be cleared.
	 *
	 * @function
	 * @public
	 * @param {...*} [args] 0...n additional arguments to supply to the subscriber.
	 * @returns {Promise} resolved when all subscribers are resolved (if they returned a "thenable").
	 * @example
	 *
	 * var observer = new Observer();
	 * observer.subscribe(function() {var i=0; while (i < arguments.length)console.log(arguments[i++]);});
	 * observer.notify("moo", "woof", "meow", "hiss", "tweet");
	 * // outputs the following to the console:
	 * // moo
	 * // woof
	 * // meow
	 * // hiss
	 * // tweet
	 */
	this.notify = function(...args) {
		try {
			const subscribers = registry.getSubscribers(filterFn);
			if (!subscribers) {
				return Promise.resolve();
			}
			const promiseFactories = [
				() => notify(subscribers[0], this, args),
				() => notify(subscribers[1], this, args),
				() => notify(subscribers[2], this, args)
			];
			if (notifyInStages) {
				// notify sequentially
				let result = Promise.resolve();
				promiseFactories.forEach((promiseFactory) => {
					result = result.then(promiseFactory, promiseFactory);
				});
				return result;
			}
			// notify in parallel
			return Promise.all(promiseFactories.map(promiseFactory => promiseFactory()));

		} finally {
			// reset instance variables
			filterFn = null;
			callback = null;
		}
	};

	/**
	 * Helper for notify, takes an array of subscribers and calls them with the correct scope and arguments.
	 * Ensures that all subscribers are called, ignoring accidental issues (such as exceptions) but honoring
	 * callbacks.
	 * @param {Subscriber[]} subscribers
	 * @param {Object} scope The "this" to pass through to the subscriber.
	 * @param {any[]} args Any array-like which contains the arguments to pass to the subscriber.
	 * @returns {Promise}
	 */
	function notify(subscribers, scope, args) {
		const promises = [];
		// notify each subscriber
		// if a callback is set we will notify the callback after each subscriber
		// the callback can short-circuit the process by returning true
		if (subscribers?.length) {
			for (let i = 0; i < subscribers.length; i++) {
				let next = subscribers[i];
				let nextResult = next.notify.call(scope, args);  // "call" so caller can pass thru scope
				try {
					if (typeof callback === FUNCTION) {
						if (callback(nextResult) === true) {
							promises[promises.length] = Promise.reject("Subscriber aborted notify chain");
							break;
						}
					}
				} catch (ex) {
					console.error("Error in callback: ", callback, ex.message);
				}
				if (nextResult && typeof nextResult.then === FUNCTION) {
					// We accept any "thenable" whether it's a real Promise or not.
					promises[promises.length] = nextResult;  // Promise.all will wrap "thenable" objects in a Promise
				}
			}
		}
		return Promise.all(promises).catch(() => promises);  // will be immediately resolved if zero length
	}

	/**
	 * A filter function should be set before each call to notify if it is desired, as notify will reset the
	 * filter.
	 *
	 * Filters allow you to have control over which subscriber groups are to be notified.
	 *
	 * @function
	 * @public
	 * @param {String|function} arg A filter function will be called during notify, once for each subscriber group.
	 *    If the function returns true the group is accepted and its listeners will be notified. A string may be
	 *    passed instead of a function, in which case only the group exactly matching the string will be notified.
	 *    If you need wildcard matching see {@link module:wc/Observer#getGroupAsWildcardFilter}.
	 * @example var observer = new Observer();
	 * observer.subscribe(function() {console.log("foo.*.bar");}, {group:"foo.*.bar"});
	 * observer.subscribe(function() {console.log("foo.bar");}, {group:"foo.bar"});
	 * observer.subscribe(function() {console.log("foo.ultra");}, {group:"foo.ultra"});
	 * observer.setFilter(function(s) {return(s=="foo.bar");});
	 * observer.notify();
	 * // outputs the following to the console:
	 * // `foo.bar`
	 * @example // for simple equality checks the above can be rewritten to:
	 * var observer = new Observer();
	 * observer.subscribe(function() {console.log("foo.*.bar");}, {group:"foo.*.bar"});
	 * observer.subscribe(function() {console.log("foo.bar");}, {group:"foo.bar"});
	 * observer.subscribe(function() {console.log("foo.ultra");}, {group:"foo.ultra"});
	 * observer.setFilter("foo.bar");
	 * observer.notify();
	 * // outputs the following to the console:
	 * // `foo.bar`
	 */
	this.setFilter = function(arg) {
		if (arg === null || typeof arg === "undefined") {
			throw new TypeError("arg must not be null or undefined");
		}
		if (arg.constructor === String) {
			// default filter tests for equality
			/**
			 * @param {string} group
			 * @return {boolean}
			 */
			filterFn = group => group === arg;
		} else if (arg.constructor === Function) {
			// custom filter provided by caller
			filterFn = arg;
		} else {
			throw new TypeError("arg must be a String or Function");
		}
	};

	/**
	 * Set a callback which will be called after each subscriber is notified and will be passed
	 * the return value of each subscriber.
	 *
	 * The callback may return true to short-circuit (stop) the notification process.
	 *
	 * The callback function will be cleared after each call to notify and therefore must be set again as required.
	 *
	 * @function
	 * @public
	 * @param {function} fn This function will be called after each subscriber has been notified.
	 * @example var observer = new Observer();
	 * observer.subscribe(function() {return "android";});
	 * observer.subscribe(function() {return "ios";});
	 * observer.subscribe(function() {return "win8";});
	 * observer.setCallback(function(result) {console.log(result);});
	 * observer.notify();
	 * // outputs the following to the console:
	 * // android
	 * // ios
	 * // win8
	 * @example var observer = new Observer();
	 * observer.subscribe(function() {return "android";});
	 * observer.subscribe(function() {return "ios";});
	 * observer.subscribe(function() {return "win8";});
	 * observer.setCallback(function(result) {if (result=="ios")return true; console.log(result);});
	 * observer.notify();
	 * // outputs the following to the console:
	 * // android
	 */
	this.setCallback = function(fn) {
		if (fn) {
			callback = fn;
		} else {
			throw new TypeError("Callback function cannot be null.");
		}
	};

	/**
	 * Get a filter function that can be provided to setFilter to match group names which contain wildcards
	 * (asterisks).
	 *
	 * @function
	 * @public
	 * @param {string} filter The filter to match, honoring any wildcards in group names.
	 * @returns {function} A filter function.
	 * @example var observer = new Observer();
	 * observer.subscribe(function() {console.log("foo.*.bar");}, {group:"foo.*.bar"});
	 * observer.subscribe(function() {console.log("foo.bar");}, {group:"foo.bar"});
	 * observer.subscribe(function() {console.log("foo.ultra");}, {group:"foo.ultra"});
	 * observer.setFilter(observer.getGroupAsWildcardFilter("foo.ultra.bar"));
	 * observer.notify();
	 * // outputs the following to the console:
	 * // foo.*.bar
	 */
	this.getGroupAsWildcardFilter = function(filter) {
		if (typeof filter !== "string") {
			throw new TypeError("filter cannot be null");
		}
		/**
		 * BEWARE: This is by far and away the most called function in the codebase!
		 * Optimisation here is critical. An extra millisecond here could amount to 10 seconds
		 * on page load!  Average execution time is currently 0.003ms in FF3.5.
		 * @param {string} group
		 */
		return (group) => {
			// escape all regexp characters except *. Replace * with .* to give it wildcard behaviour
			const groupAsWildcardRe = compiledGroups[group] ||
				(compiledGroups[group] = new RegExp(`^${escapeRe(group, true)}$`)) ;
			// return filter.match(groupAsWildcardRe) ? true : false;
			return groupAsWildcardRe.test(filter);
		};
	};
}  // END OBSERVER CLASS

/**
 * Observer.priority (and/or `observer.priority`) contains the preferred values
 * to be passed to {@link module:wc/Observer#subscribe} in the "config.priority" argument.
 * @var Observer.priority
 * @type {Object}
 * @property {number} HIGH Run first (value is 1)
 * @property {number} MED Run after all HIGHs (value is 0)
 * @property {number} LOW Run last (value is -1)
 * @public
 * @static
 */
Observer.prototype.priority = Observer.priority = {
	HIGH: 1,
	MED: 0,
	LOW: -1
};

/*
 * Below are the classes Observer uses "under the hood".
 * They are separate from the main Observer class mainly so that:
 * - encapsulation/abstraction remains pure
 * - new copies of them are not created each time Observer is instantiated
 */

/**
 * Manages registering and de-registering subscribers in different groups and
 * at different priorities. Knows about all the groups in this Observer instance.
 *
 * @alias SubscriberRegistry
 * @constructor
 * @private
 */
function SubscriberRegistry() {

	/**
	 * The group to which all subscribers belong unless otherwise added to an explicit group as part of
	 * subscribing.
	 * @constant
	 * @type String
	 * @private
	 * @default "__default__"
	 */
	const DEFAULT_GROUP = "__default__";
	const store = {};
	let idx = 0;

	/**
	 * Register an instance of subscriber to the given group at the given priority.
	 * @function
	 * @public
	 * @param {Subscriber} subscriber An instance of Subscriber.
	 * @param {String} [group] The name of the group in which this subscriber is to be stored.
	 * @param {number} [priority] The `Observer.priority` of this subscriber.
	 */
	this.register = function(subscriber, group, priority) {
		const toStore = getGroupStore(group, true);
		subscriber[SORT_IDX_PROP] = idx++;
		toStore.add(subscriber, priority);
	};

	/**
	 * Unsubscribe from this Observer instance.
	 *
	 * @function
	 * @public
	 * @param {Function|Object} subscriber The subscriber (as passed to the subscribe method).
	 * @param {String} group The group from which to unsubscribe (otherwise defaults will be used). There is
	 *    currently no way to remove a subscriber from all groups without multiple calls.
	 * @returns {!Function|Object} A reference to the removed subscriber if found and unsubscribed, otherwise null.
	 */
	this.deregister = function(subscriber, group) {
		const fromStore = getGroupStore(group);
		if (fromStore) {
			return fromStore.remove(subscriber);
		}
		return null;
	};

	/**
	 * Get a non-live array of subscribers matching the filter if set, otherwise the default group will be used. By
	 * non-live we mean that if subscribers are added or removed to this group the array will not be updated to
	 * reflect the changes. This means a subscriber can safely add or remove subscribers, even itself.
	 * @function
	 * @public
	 * @param {function} filterFn The function used to filter the potential subscribers.
	 * @returns {Subscriber[][]} An array of subscribers matching the filter.
	 */
	this.getSubscribers = function(filterFn) {
		const groupNames = getFilteredGroupNames(filterFn);
		const groupStore = new GroupStore();
		groupNames.forEach(groupName => {  // loop the groups checking and add their subscribers
			const next = getGroupStore(groupName);
			if (next) {
				groupStore.merge(next);
			}
		});
		return groupStore.getSubscribers();
	};

	/**
	* Determine if the given subscriber is already subscribed to this group.
	* @function
	* @public
	* @param {Function|Object} subscriber The subscriber (as passed to the subscribe method).
	* @param {String} [group] The group in which to search (otherwise defaults will be used).
	* @returns {Boolean} true if the subscriber is already subscribed to this group.
	*/
	this.isRegistered = function(subscriber, group) {
		const groupStore = getGroupStore(group);
		return groupStore?.contains(subscriber);
	};

	/**
	* Completely purge all subscribers from this observer group.
	* @function
	* @public
	* @param {String} [group] The name of the group to reset. Defaults to the DEFAULT_GROUP group.
	*/
	this.reset = function(group) {
		const groupStore = getGroupStore(group);
		groupStore?.reset();
	};

	/**
	 * Determine the number of subscribers in any given group.
	 * @function
	 * @public
	 * @param {String} [group] The group in question. If not provided the default group will be used.
	 * @returns {number} The count of subscribers in the group, or -1 if the group does not exist.
	 *
	 * @example var observer = new Observer();
	 * observer.subscribe(function() {}, {group:"cows"});
	 * observer.subscribe(function() {}, {group:"cows"});
	 * observer.subscribe(function() {});
	 * console.log(observer.subscriberCount("dogs")); // outputs -1
	 * console.log(observer.subscriberCount()); // outputs 1
	 * console.log(observer.subscriberCount("cows")); // outputs 2
	 */
	this.subscriberCount = function(group) {
		const groupStore = getGroupStore(group);
		return groupStore ? groupStore.getLength() : -1;
	};

	/**
	 * Create a string representation of this instance.
	 * @function
	 * @public
	 * @returns {String} A string representation of this instance.
	 */
	this.toString = function() {
		return Object.keys(store).map(function(group) {
			return group + ": " + store[group].toString();
		}).join("\n");
	};

	/**
	 * Get the group store for a given group.
	 * @function
	 * @private
	 * @param {String} [group] The group to get, defaults to the DEFAULT_GROUP group.
	 * @param {Boolean} [createNew] If true then will create and return new group store if one is not found.
	 * @returns {GroupStore} The existing group store or null or a newly created one instead of null if createNew is true.
	 */
	function getGroupStore(group, createNew) {
		if (!group) {
			group = DEFAULT_GROUP;
		}
		let result = store[group];
		if (!result && createNew) {
			result = store[group] = new GroupStore();
		}
		return result;
	}

	/**
	 * Returns group names that match the filterFn.
	 * @function
	 * @private
	 * @param {function} [filterFn] Get the group names that match this filter. If not provided then the default
	 *    group will be used and the return value will be a string.
	 * @returns {string[]} Group names that can be used to retrieve subscribers from the registry.
	 */
	function getFilteredGroupNames(filterFn) {
		if (!filterFn) {
			return [DEFAULT_GROUP];
		}
		return Object.keys(store).filter((key) => filterFn(key));
	}
}  // END SubscriberRegistry CLASS

/**
 * Represents a named group of subscribers - knows which subscribers are registered and
 * what priority they are registered in within this group.
 *
 * Note: does not know its own name.
 *
 * @alias GroupStore
 * @constructor
 * @private
 */
function GroupStore() {
	let unsorted = 0;
	const HIGH = 1,
		MED = 2,
		LOW = 4;

	/*
	 * The subscriber arrays are only public for the merge.
	 * getters would provide an unnecessary performance overhead for this class which is not part
	 * of the general purpose API - only developers working on Observer ever use this class.
	 */
	/**
	 * @ignore
	 * @type {Subscriber[]}
	 */
	this[HIGH] = [];

	/**
	 * @ignore
	 * @type {Subscriber[]}
	 */
	this[MED] = [];

	/**
	 * @ignore
	 * @type {Subscriber[]}
	 */
	this[LOW] = [];

	/**
	 * Add a subscriber to this group at the given priority.
	 * @function
	 * @public
	 * @param {Subscriber} subscriber The instance to add to this group.
	 * @param {Observer.priority|number} priority One of `Observer.priority`
	 */
	this.add = function(subscriber, priority) {
		if (subscriber instanceof Subscriber) {
			let arr;
			if (!priority || isNaN(priority)) {  // zero or null or false or undefined or a non-numeric string
				arr = this[MED];
			} else if (priority < 0) {  // negative number is low priority
				arr = this[LOW];
			} else {  // anything else, e.g. positive number, true
				arr = this[HIGH];
			}
			arr[arr.length] = subscriber;
		} else {
			throw new TypeError("Can not subscribe " + subscriber);
		}
	};

	/**
	 * Remove a subscriber from this group.
	 * @function
	 * @public
	 * @param {function} subscriber The function subscribed to this group. We expect that the function will not be
	 *    subscribed to a group more than once.
	 * @returns {!function[]} The subscriber being removed. An array as it is the output of Array.filter.
	 */
	this.remove = function(subscriber) {
		let result = null;
		/**
		 * @param {Subscriber} next
		 * @return {boolean}
		 */
		const filter = next => {
			let equals = next.equals(subscriber);
			if (equals) {
				result = subscriber;
			}
			return !equals;
		};
		// filter is overkill, the subscriber should only be in there once, but unsubscribe is not performance critical
		this[MED] = this[MED].filter(filter);
		if (!result) {
			this[HIGH] = this[HIGH].filter(filter);
			if (!result) {
				this[LOW] = this[LOW].filter(filter);
			}
		}
		return result;
	};

	/**
	 * Determine if this group contains the given subscriber. Note this is performance friendly because it does not
	 * trigger a sort.
	 * @function
	 * @public
	 * @param {function} subscriber the function we are looking for in this group.
	 * @returns {Boolean} true if the subscriber is found in this group.
	 */
	this.contains = function(subscriber) {
		/**
		 * @param {Subscriber} next
		 * @return {boolean}
		 */
		const matcher = next => next.equals(subscriber);
		return this[MED].some(matcher) || this[HIGH].some(matcher) || this[LOW].some(matcher);
	};

	/**
	 * Get a sorted collection of all subscribers in this group.
	 * @function
	 * @public
	 * @returns {Subscriber[][]} An array of ALL Subscriber instances in this group.
	 *     index 0 is high priority
	 *     index 1 is medium priority
	 *     index 2 is low priority
	 * Each array is sorted according to when the subscriber was added for example:
	 * [0][0] is the first high priority subscriber added
	 * [1][0] is the first medium priority subscriber added
	 * [2][0] is the first low priority subscriber added
	 *
	 */
	this.getSubscribers = function() {
		if (unsorted & HIGH) {
			this[HIGH].sort(sortSubscribers);
		}
		if (unsorted & MED) {
			this[MED].sort(sortSubscribers);
		}
		if (unsorted & LOW) {
			this[LOW].sort(sortSubscribers);
		}
		unsorted = 0;
		return [this[HIGH].concat(), this[MED].concat(), this[LOW].concat()];
	};

	/**
	 * This is a performance friendly way of getting a count of all subscribers.
	 * Do not call "getSubscribers" just to get the length (because that will cause a sort).
	 * @function
	 * @public
	 * @returns {number} The count of all subscribers in this group.
	 */
	this.getLength = function() {
		return this[HIGH].length + this[MED].length + this[LOW].length;
	};

	/**
	 * Reset the group store.
	 * @see SubscriberRegistry#reset
	 * @function
	 * @public
	 */
	this.reset = function() {
		this[HIGH].length = this[MED].length = this[LOW].length = 0;
	};

	/**
	 * Permanently merges all the subscribers in the provided group into this group.
	 * @function
	 * @public
	 * @param {GroupStore} groupStore The GroupStore instance to merge into this instance.
	 */
	this.merge = function(groupStore) {
		if (groupStore instanceof GroupStore) {
			this[HIGH] = mergeGroup(this[HIGH], groupStore[HIGH], HIGH);
			this[MED] = mergeGroup(this[MED], groupStore[MED], MED);
			this[LOW] = mergeGroup(this[LOW], groupStore[LOW], LOW);
		} else {
			throw new TypeError("Can not merge " + groupStore);
		}
	};

	/**
	 * Get a string representation of the group store instance.
	 * @function
	 * @public
	 * @returns {string} A string that provides a useful/meaningful representation of this instance.
	 */
	this.toString = function() {
		let result = "High: " + this[HIGH].length;
		result += " Med: " + this[MED].length;
		result += " Low: " + this[LOW].length;
		return result;
	};

	/**
	 * A private helper for this.merge. Merges the two arrays and sets the "flag" bit in the "unsorted" bitmask if \
	 * the resulting array will need sorting before it is used.
	 * @function
	 * @private
	 * @param {Subscriber[]} arr1 an array of subscribers.
	 * @param {Subscriber[]} arr2 another array of subscribers.
	 * @param {number} flag The bit to set in "unsorted" if the resulting array needs sorting (HIGH, MED or LOW)
	 * @returns {Subscriber[]} The result of merging the two arrays (could be one of the
	 *    original arrays if no merge was needed).
	 */
	function mergeGroup(arr1, arr2, flag) {
		const len1 = arr1.length,
			len2 = arr2.length;
		if (len1 && len2) {
			unsorted += flag;
			return arr1.concat(arr2);
		}
		return len2 ? arr2 : arr1;
	}

	/**
	 * An array sort function for subscribers.
	 * @function
	 * @private
	 * @param {Subscriber} a
	 * @param {Subscriber} b
	 * @returns {number} The difference of the sort index property of the subscribers.
	 */
	function sortSubscribers(a, b) {
		return a[SORT_IDX_PROP] - b[SORT_IDX_PROP];
	}
}  // END GroupStore CLASS

/**
 * Knows about a single subscriber to the observer class, and how to notify it (what
 * function to call, what context to call it in).
 * @param {function|Object} subscriber The subscriber, as passed to {@link module:wc/Observer#subscribe}.
 * @param {Object} [context] The context in which to call the subscriber (see `config.context` in
 *    {@link module:wc/Observer#subscribe}).
 * @param {string} [method] The name of the method to call if subscriber is an object.
 * @constructor
 * @alias Subscriber
 * @private
 */
function Subscriber(subscriber, context, method) {
	/**
	 * representation of this so we can keep context.
	 * @var
	 * @private
	 * @type Subscriber
	 */
	const $self = this;

	/**
	 * Notify all subscribers (i.e. publish).
	 * @function Subscriber#notify
	 * @public
	 * @param {...*} args An array-like collection of arguments to apply to the listener.
	 * @returns {!*} The result of applying the listener function.
	 */
	this.notify = function(args) {
		const func = getListener();
		if (typeof func === FUNCTION) {
			try {
				return func.apply(getContext(this), args);
			} catch (ex) {
				console.error("Error in subscriber: ", $self, ex.message);
			}
		} else {
			console.warn("Could not notify: ", $self);
		}
	};

	/**
	 * Get the scope in which to call the subscriber.
	 * @private
	 * @param {Object} callerScope The caller's current scope.
	 * @returns {!Object} The context for the caller.
	 */
	function getContext(callerScope) {
		let result = context;  // explicitly overridden scope - trumps all
		if (!result) {
			if (method) {  // if we are calling a public method on an object
				result = subscriber;  // return the object to which the method is bound
			} else if (callerScope !== $self && !(callerScope instanceof Observer)) {  // if the caller has been called with "call" or "apply"
				result = callerScope;  // pass through scope
			} else {  // scope is not set in any way, it should be the global scope
				result = globalThis;  // global scope
			}
		}
		return result;
	}

	/**
	 * Get the subscriber function to notify.
	 * @private
	 * @returns {function} The function we need to notify.
	 */
	function getListener() {
		return method ? subscriber[method] : subscriber;
	}

	/**
	 * Get a string representation of theSubscriber instance.
	 * @returns {String} Aa string that provides a useful/meaningful representation of this instance.
	 */
	this.toString = function() {
		const func = getListener();
		return func ? func.toString() : "invalid Subscriber";
	};

	/**
	 * A logical equivalence test.
	 * @param {Object} obj The object to test for equality.
	 * @returns {boolean} true if obj is logically equivalent to this instance of Subscriber.
	 */
	this.equals = function(obj) {
		return obj === subscriber;
	};
}  // END Subscriber CLASS
export default Observer;
