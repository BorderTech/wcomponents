/**
 * @module
 * @requires module:wc/string/escapeRe
 */
define(["wc/string/escapeRe"], /** @param escapeRe wc/string/escapeRe @ignore */ function(escapeRe) {
	"use strict";
	var SORT_IDX_PROP = "__sort__",
		FUNCTION = "function";

	/**
	 * General purpose Publish/Subscribe implementation.
	 *
	 * After instantiating an observer instance subsribe using {@link module:wc/Observer#subscribe} and publish using
	 * {@link module:wc/Observer#notify}
	 *
	 * @constructor
	 * @alias module:wc/Observer
	 * @example  // the simplest usage
	 * var observer = new Observer();
	 * observer.subscribe(window.alert);
	 * observer.notify("foo");  // alerts "foo"
	 *
	 */
	function Observer() {
		var registry = new SubscriberRegistry(),
			filterFn = null,
			callback = null,
			compiledGroups = {};  // this results in HUGE performance benefits, up to 800ms saved in FF3.5 (on a 1050ms page with profiling turned on)

		/**
		 * Remove a subscriber.
		 * @function
		 * @public
		 * @see module:wc/Observer~SubscriberRegistry#deregister
		 */
		this.unsubscribe = registry.deregister;

		/**
		 * Remove all subscribers and reset the publish registry.
		 * @function
		 * @public
		 * @see module:wc/Observer~SubscriberRegistry#reset
		 */
		this.reset = registry.reset;

		/**
		 * Get the number of subscribers to this publisher.
		 * @function
		 * @public
		 * @see module:wc/Observer~SubscriberRegistry#subscriberCount
		 */
		this.subscriberCount = registry.subscriberCount;

		/**
		 * Is a particular subscriber already registered?
		 * @function
		 * @public
		 * @see module:wc/Observer~SubscriberRegistry#isRegistered
		 */
		this.isSubscribed = registry.isRegistered;

		/**
		 * Get the subscriber registry as a string.
		 * @function
		 * @public
		 * @see module:wc/Observer~SubscriberRegistry#toString
		 */
		this.toString = registry.toString;

		/**
		 * Subscribe to this Observer instance.
		 *
		 * Note that duplicate instances of a subscriber cannot be added to the same "group"
		 * (see the config.group argument below).
		 *
		 * @function
		 * @public
		 * @param {(Function|Object)} subscriber A callback which will be called when the notify method is called or
		 * an object which provides a public method with the name specified in config.method.
		 * @param {Object} [config] An object containing configuration option.
		 * @param {String} [config.group] Associate the subscriber with the given group. When notify is called the
		 *    subscribers to be notified can be filtered based on their group. Default value is DEFAULT_GROUP. See
		 *    {@link module:wc/Observer#setFilter} and {@link module:wc/Observer#notify} for more info.
		 * @param {(Object|Function)} [config.context] When the subscriber is called its "this" reference will be the
		 *    "context" object if provided; otherwise if "method" is set will be "subscriber" else it will be default
		 *    scope (probably window).
		 * @param {{module:wc/Observer.priority}} [config.priority] When subscribers are being notified all subscribers that were added as important
		 *    will be notified before non-important subscribers. Default value is observer.priority.LOW. Accepts any of
		 *    these:
		 *    <ul>
		 *        <li>observer.priority.HIGH</li>
		 *        <li>observer.priority.MED</li>
		 *        <li>observer.priority.LOW</li>
		 *    </ul>
		 * @param {String} [config.method] Name of public method to call on this subscriber (eg if this is set to "bar" will
		 *    call subscriber.bar()). This allows you to subscribe "dynamic" listeners where the function called can
		 *    change from that you originally registered (or it need not even exist at the time you registered). As long
		 *    as the correct interface is present at the time it is called then all's good. Note this is especially
		 *    useful in that the function does not have to exist on the object itself, it could be anywhere up the
		 *    prototype chain.
		 * @returns {(Function|Object)} a reference to the subscriber.
		 * @example
		 *    var dog = {bark:function() {console.log("woof!");}},
		 *        observer = new Observer();
		 *    observer.subscribe(dog, {group: "canine", context: dog, priority: Observer.priority.HIGH, method: "bark"});
		 */
		this.subscribe = function(subscriber, config) {
			var result,
				newSubscriber,
				group,
				context,
				priority,
				method;
			if (subscriber) {
				if (config) {
					group = config.group;
					context = config.context;
					priority = config.priority;
					method = config.method;
				}
				if (!registry.isRegistered(subscriber, group)) {
					newSubscriber = new Subscriber(subscriber, context, method);
					registry.register(newSubscriber, group, priority);
				}
				result = subscriber;  // this is stupid, maybe we should return newSubscriber and that can be used to unsubscribe?
			}
			else {
				throw new ReferenceError("Call to Observer.subscribe without a subscriber");
			}
			return result;
		};

		/**
		 * Call all subscribers with the arguments passed to notify. The arguments provided will be transparently
		 * passed on to subscribers.
		 *
		 * By default only subscribers added without a group (i.e. they are part of the DEFAULT_GROUP group) will be
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
		 * @param {...*} [args] 0..n additional arguments to supply to the subscriber.
		 * @example
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
		this.notify = function() {
			try {
				var result,
					next,
					i,
					subscribers = registry.getSubscribers(filterFn),
					len = subscribers ? subscribers.length : 0;
				// notify each subscriber
				// if a callback is set we will notify the callback after each subscriber
				// the callback can short-circuit the process by returning true
				for (i = 0; i < len; i++) {
					next = subscribers[i];
					result = next.notify.call(this, arguments);  // "call" so caller can pass thru scope
					try {
						if (typeof callback === FUNCTION) {
							if (callback(result) === true) {
								break;
							}
						}
					}
					catch (ex) {
						console.error("Error in callback: ", callback, ex.message);
					}
				}
			}
			finally {
				// reset instance variables
				filterFn = null;
				callback = null;
			}
		};

		/**
		 * A filter function should be set before each call to notify if it is desired, as notify will reset the
		 * filter.
		 *
		 * Filters allow you to have control over which subscriber groups are to be notified.
		 *
		 * @function
		 * @public
		 * @param {(String|Function)} arg A filter function will called during notify, once for each subscriber group.
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
		 * // foo.bar
		 * @example // for simple equality checks the above can be rewritten to:
		 * var observer = new Observer();
		 * observer.subscribe(function() {console.log("foo.*.bar");}, {group:"foo.*.bar"});
		 * observer.subscribe(function() {console.log("foo.bar");}, {group:"foo.bar"});
		 * observer.subscribe(function() {console.log("foo.ultra");}, {group:"foo.ultra"});
		 * observer.setFilter("foo.bar");
		 * observer.notify();
		 * // outputs the following to the console:
		 * // foo.bar
		 */
		this.setFilter = function(arg) {
			if (arg === null || typeof arg === "undefined") {
				throw new TypeError("arg must not be null or undefined");
			}
			if (arg.constructor === String) {
				// default filter tests for equality
				filterFn = function (group) {
					return group === arg;
				};
			}
			// custom filter provided by caller
			else if (arg.constructor === Function) {
				filterFn = arg;
			}
			else {
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
		 * @param {Function} fn This function will be called after each subscriber has been notified.
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
			}
			else {
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
		 * @returns {Function} A filter function.
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
			/*
				BEWARE: This is by far and away the most called function in the codebase!
				Optimisation here is critical. An extra millisecond here could amount to 10 seconds
				on page load!  Average execution time is currently 0.003ms in FF3.5
			*/
			function groupAsWildcardFilter(group) {
				var groupAsWildcardRe = compiledGroups[group];
				if (!groupAsWildcardRe) {
					// escape all regexp characters except *. Replace * with .* to give it wildcard behaviour
					groupAsWildcardRe = new RegExp("^" + escapeRe(group, true) + "$");
					compiledGroups[group] = groupAsWildcardRe;
				}
				// return filter.match(groupAsWildcardRe) ? true : false;
				return groupAsWildcardRe.test(filter);
			}
			if (!filter || filter.constructor !== String) {
				throw new TypeError("filter cannot be null");
			}
			return groupAsWildcardFilter;
		};
	}  // END OBSERVER CLASS

	/**
	 * Observer.priority (and/or observer.priority) contains the preferred values
	 * to be passed to {@link module:wc/Observer#subscribe} in the "config.priority" argument.
	 * @var module:wc/Observer.priority
	 * @type {Object}
	 * @property {int} HIGH Run first (value is 1)
	 * @property {int} MED Run after all HIGHs (value is 0)
	 * @property {int} LOW Run last (value is -1)
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
	 * Manages registering and deregistering subscribers in different groups and
	 * at different priorities. Knows about all the groups in this Observer instance.
	 *
	 * @alias module:wc/Observer~SubscriberRegistry
	 * @constructor
	 * @private
	 */
	function SubscriberRegistry() {
		var
			/**
			 * The group to which all subscribers belong unless otherwise added to an explicit group as part of
			 * subscribing.
			 * @constant
			 * @type String
			 * @private
			 * @default "__default__"
			 */
			DEFAULT_GROUP = "__default__",
			idx = 0,
			store = {};

		/**
		 * Register an instance of subscriber to the given group at the given priority.
		 * @function
		 * @public
		 * @param {module:wc/Observer~Subscriber} subscriber An instance of Subscriber.
		 * @param {String} [group] The name of the group in which this subscriber is to be stored.
		 * @param {int} priority The priority of this subscriber.
		 */
		this.register = function(subscriber, group, priority) {
			var store = getGroupStore(group, true);
			subscriber[SORT_IDX_PROP] = idx++;
			store.add(subscriber, priority);
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
			var result,
				store = getGroupStore(group);
			if (store) {
				result = store.remove(subscriber);
			}
			return result || null;
		};

		/**
		 * Get a non-live array of subscribers matching the filter if set, otherwise the default group will be used. By
		 * non-live we mean that if subscribers are added or removed to this group the array will not be updated to
		 * reflect the changes. This means a subscriber can safely add or remove subscribers, even itself.
		 * @function
		 * @public
		 * @param {Function} filterFn The function used to filter the potential subscribers.
		 * @returns {module:wc/Observer~Subscriber[]} An array of subscribers matching the filter.
		 */
		this.getSubscribers = function(filterFn) {
			var result,
				groupStore,
				groups = getFilteredGroupNames(filterFn);
			if (groups) {
				if (groups.constructor !== Array) {
					// it must be a string
					groupStore = getGroupStore(groups);
				}
				else {  // multiple entries in the group store are involved
					groupStore = new GroupStore();
					// loop the groups checking and add their subscribers
					groups.forEach(function(groupName) {
						var next = getGroupStore(groupName);
						if (next) {
							groupStore.merge(next);
						}
					});
				}
				if (groupStore) {
					result = groupStore.getSubscribers();
				}
			}
			return result;
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
			var result,
				groupStore = getGroupStore(group);
			if (groupStore) {
				result = groupStore.contains(subscriber);
			}
			else {
				result = false;
			}
			return result;
		};

		/**
		* Completely purge all subscribers from this observer group.
		* @function
		* @public
		* @param {String} [group] The name of the group to reset. Defaults to the the DEFAULT_GROUP group.
		*/
		this.reset = function(group) {
			var groupStore = getGroupStore(group);
			if (groupStore) {
				groupStore.reset();
			}
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
			var groupStore = getGroupStore(group),
				result = groupStore ? groupStore.getLength() : -1;
			return result;
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
		 * @param {String} [group] The group to get, defaults to the the DEFAULT_GROUP group.
		 * @param {Boolean} [createNew] If true then will create and return new group store if one is not found.
		 * @returns {!Array} The existing group store or null or a newly created one instead of null if createNew is true.
		 */
		function getGroupStore(group, createNew) {
			var result;
			if (!group) {
				group = DEFAULT_GROUP;
			}
			result = store[group];
			if (!result && createNew) {
				result = store[group] = new GroupStore();
			}
			return result;
		}

		/**
		 * Returns group names that match the filterFn.
		 * @function
		 * @private
		 * @param {Function} [filterFn] Get the group names that match this filter. If not provided then the default
		 *    group will be used and the return value will be a string.
		 * @returns {!(string|Array)} Group names that can be used to retrieve subscribers from the registry.
		 */
		function getFilteredGroupNames(filterFn) {
			var result, groups;
			if (!filterFn) {
				result = DEFAULT_GROUP;
			}
			else {
				groups = Object.keys(store).filter(filterFn);
				if (groups.length === 1) {
					result = groups[0];
				}
				else {
					result = groups;
				}
			}
			return result;
		}
	}  // END SubscriberRegistry CLASS

	/**
	 * Represents a named group of subscribers - knows which subscribers are registered and
	 * what priority they are registered in within this group.
	 *
	 * Note: does not know its own name.
	 *
	 * @alias module:wc/Observer~GroupStore
	 * @constructor
	 * @private
	 */
	function GroupStore() {
		var unsorted = 0,
			HIGH = 1,
			MED = 2,
			LOW = 4;

		/*
		 * The subscriber arrays are only public for the merge.
		 * getters would provide an unnecessary performance overhead for this class which is not part
		 * of the general purpose API - only developers working on Observer ever use this class.
		 */
		/**
		 * @ignore
		 */
		this[HIGH] = [];

		/**
		 * @ignore
		 */
		this[MED] = [];

		/**
		 * @ignore
		 */
		this[LOW] = [];

		/**
		 * Add a subscriber to this group at the given priority.
		 * @function
		 * @public
		 * @param {module:wc/Observer~Subscriber} subscriber The instance to add to this group.
		 * @param {number} priority 1 = high, 0 = default, -1 = low
		 */
		this.add = function(subscriber, priority) {
			var arr;
			if (subscriber instanceof Subscriber) {
				if (!priority || isNaN(priority)) {  // zero or null or false or undefined or a non-numeric string
					arr = this[MED];
				}
				else if (priority < 0) {  // negative number is low priority
					arr = this[LOW];
				}
				else {  // anything else, e.g. positive number, true
					arr = this[HIGH];
				}
				arr[arr.length] = subscriber;
			}
			else {
				throw new TypeError("Can not subscribe " + subscriber);
			}
		};

		/**
		 * Remove a subscriber from this group.
		 * @function
		 * @public
		 * @param {Function} subscriber The function subscribed to this group. We expect that the function will not be
		 *    subscribed to a group more than once.
		 * @returns {!Function[]} The subscriber being removed. An array as it is the output of Array.filter.
		 */
		this.remove = function(subscriber) {
			var result = null,
				filter = function(next) {
					var equals = next.equals(subscriber);
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
		 * @param {Function} subscriber the function we are looking for in this group.
		 * @returns {Boolean} true if the subscriber is found in this group.
		 */
		this.contains = function(subscriber) {
			var func = function(next) {
					return next.equals(subscriber);
				},
				result = this[MED].some(func);
			if (!result) {
				result = this[HIGH].some(func);
				if (!result) {
					result = this[LOW].some(func);
				}
			}
			return result;
		};

		/**
		 * Get a sorted collection of all subscribers in this group.
		 * @function
		 * @public
		 * @returns {module:wc/Observer~Subscriber[]} An array of ALL Subscriber instances in this group, sorted from
		 * high priority to low priority where index zero is the first high priority subscriber added and the highest
		 * index is the last low priority subscriber added.
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
			return this[HIGH].concat(this[MED], this[LOW]);
		};

		/**
		 * This is a performance friendly way of getting a count of all subscribers. Do not call "getSubscribers" just
		 * to get the length (because that will cause a sort).
		 * @function
		 * @public
		 * @returns {number} The count of all subscribers in this group.
		 */
		this.getLength = function() {
			return this[HIGH].length + this[MED].length + this[LOW].length;
		};

		/**
		 * Reset the group store.
		 * @see module:wc/Observer~SubscriberRegistry#reset
		 * @function
		 * @public
		 */
		this.reset = function() {
			this[HIGH].length = this[MED].length = this[LOW].length = 0;
		};

		/**
		 * Permanently merges all of the subscribers in the provided group into this group.
		 * @function
		 * @public
		 * @param {module:wc/Observer~GroupStore} groupStore The GroupStore instaance to merge into this instance.
		 */
		this.merge = function(groupStore) {
			if (groupStore instanceof GroupStore) {
				this[HIGH] = mergeGroup(this[HIGH], groupStore[HIGH], HIGH);
				this[MED] = mergeGroup(this[MED], groupStore[MED], MED);
				this[LOW] = mergeGroup(this[LOW], groupStore[LOW], LOW);
			}
			else {
				throw new TypeError("Can not merge " + groupStore);
			}
		};

		/**
		 * Get a string representation of the group store instance.
		 * @function
		 * @public
		 * @returns {Atring} A string that provides a useful/meaningful representation of this instance.
		 */
		this.toString = function() {
			var result = "High: " + this[HIGH].length;
			result += " Med: " + this[MED].length;
			result += " Low: " + this[LOW].length;
			return result;
		};

		/**
		 * A private helper for this.merge. Merges the two arrays and sets the "flag" bit in the "unsorted" bitmask if \
		 * the resulting array will need sorting before it is used.
		 * @function
		 * @private
		 * @param {module:wc/Observer~Subscriber[]} arr1 an array of subscribers.
		 * @param {module:wc/Observer~Subscriber[]} arr2 another array of subscribers.
		 * @param {int} flag The bit to set in "unsorted" if the resulting array needs sorting (HIGH, MED or LOW)
		 * @returns {module:wc/Observer~Subscriber[]} The result of merging the two arrays (could be one of the
		 *    original arrays if no merge was needed).
		 */
		function mergeGroup(arr1, arr2, flag) {
			var result,
				len1 = arr1.length,
				len2 = arr2.length;
			if (len1 && len2) {
				result = arr1.concat(arr2);
				unsorted += flag;
			}
			else {
				result = len2 ? arr2 : arr1;
			}
			return result;
		}

		/**
		 * An array sort function for subscribers.
		 * @function
		 * @private
		 * @param {module:wc/Observer~Subscriber} a
		 * @param {module:wc/Observer~Subscriber} b
		 * @returns {number} The difference of the sort index property of the subscribers.
		 */
		function sortSubscribers(a, b) {
			return a[SORT_IDX_PROP] - b[SORT_IDX_PROP];
		}
	}  // END GroupStore CLASS

	/**
	 * Knows about a single subscriber to the observer class, and how to notify it (what
	 * function to call, what context to call it in).
	 * @param {Function|Object} subscriber The subscriber, as passed to {@link module:wc/Observer#subscribe}.
	 * @param {Object} [context] The context in which to call the subscriber (see config.context in
	 *    {@link module:wc/Observer#subscribe}).
	 * @param {string} [method] The name of the method to call if subscriber is an object.
	 * @constructor
	 * @alias module:wc/Observer~Subscriber
	 * @private
	 */
	function Subscriber(subscriber, context, method) {
		/**
		 * representation of this so we can keep context.
		 * @var
		 * @private
		 * @type module:wc/Observer~Subscriber
		 */
		var $self = this;

		/**
		 * Notify all subscribers (i.e. publish).
		 * @function module:wc/Observer~Subscriber#notify
		 * @public
		 * @param {...*} args An array-like collection of arguments to apply to the listener.
		 * @returns {!*} The result of applying the listener function.
		 */
		$self.notify = function(args) {
			var result,
				func = getListener();
			if (typeof func === FUNCTION) {
				try {
					result = func.apply(getContext(this), args);
				}
				catch (ex) {
					console.error("Error in subscriber: ", $self, ex.message);
				}
			}
			else {
				console.warn("Could not notify: ", $self);
			}
			return result;
		};

		/**
		 * Get the scope in which to call the subscriber.
		 * @function
		 * @private
		 * @param {Object} callerScope The caller's current scope.
		 * @returns {!Object} The context for the caller.
		 */
		function getContext(callerScope) {
			var result = context;  // explicitly overridden scope - trumps all
			if (!result) {
				if (method) {  // if we are calling a public method on an object
					result = subscriber;  // return the object to which the method is bound
				}
				else if (callerScope !== $self && !(callerScope instanceof Observer)) {// if the caller has been called with "call" or "apply"
					result = callerScope;  // pass through scope
				}
				else {  // scope is not set in any way, it should be the global scope
					result = window.self;  // global scope (this window)
				}
			}
			return result;
		}

		/**
		 * Get the subscriber function to notify.
		 * @function
		 * @private
		 * @returns {Function} The function we need to notify.
		 */
		function getListener() {
			return method ? subscriber[method] : subscriber;
		}

		/**
		 * Get a string representation of theSubscriber instance.
		 * @function module:wc/Observer~Subscriber#toString
		 * @public
		 * @returns {String} Aa string that provides a useful/meaningful representation of this instance.
		 */
		$self.toString = function() {
			var func = getListener();
			return func ? func.toString() : "invalid Subscriber";
		};

		/**
		 * A logical equivalence test.
		 * @function module:wc/Observer~Subscriber#equals
		 * @public
		 * @param {Object} obj The object to test for equality.
		 * @returns {Boolean} true if obj is logically equivalent to this instance of Subscriber.
		 */
		$self.equals = function(obj) {
			return obj === subscriber;
		};
	}  // END Subscriber CLASS
	return Observer;
});
