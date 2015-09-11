/**
 * Provides a browser agnostic event wrapper.
 *
 * If the HTML5 "write once, run anywhere" dream comes true then this class can hopefully be deleted. The support for
 * DOM Level 2 events in Internet Explorer 9 is a major step in the right direction.
 *
 * Features implemented:
 *
 * * this keyword applies correctly in listener functions (it is the element the event is attached to);
 * * this keyword can be overridden when adding event listener (only somewhat useful, the scope of the listener could
 * be bound anyway using currying or bind. The minor disadvantage to these methods is:
 * ** a small memory hit because you are creating new instances of those listeners each time;
 * ** the event manager can not tell if a listener is attached twice.
 * * implemented numerous polyfills to ensure events have standard properties in all browsers;
 * * event order is guaranteed, events will be fired in the order they are added except you can add an event at **different
 *   priorities**: HIGH, MED, LOW (see {@link module:wc/dom/event.add} for more detail);
 * * a listener is prevented from being attached to the same element for than particular event type more than once;
 * * can programatically fire an event on an element even if that is a custom event.
 *
 * Historically this class had some other concerns, such as helping prevent memory leaks in IE. It was originally
 * loosely based on this: {@link http://therealcrisp.xs4all.nl/upload/addEvent_dean.html} but has since been reworked
 * and rewritten to the point that it is completely unique.
 *
 * @module
 *
 * @requires module:wc/Observer
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/uid
 * @requires module:wc/has
 * @requires module:wc/timers
 *
 * @todo re-order the code. Fix the public member mechanism, maybe move or get rid of $this as per more recent modules.
 */
define(["wc/Observer", "wc/dom/tag", "wc/dom/attribute", "wc/dom/uid", "wc/has", "wc/timers"],
	/** @param Observer wc/Observer @param tag wc/dom/tag @param attribute wc/dom/attribute @param uid wc/dom/uid @param has wc/has @param timers wc/timers @ignore */
	function(Observer, tag, attribute, uid, has, timers) {
		"use strict";
		var UNDEFINED = (typeof undefined);

		/**
		 * Set up the event types we can handle. If you need one that is not here then add it. Keep this list
		 * alphabetically sorted! Note, to help prevent typos the TYPE map is generated programatically off this array.
		 *
		 * @function initialise
		 * @private
		 * @returns {Array} An array of strings representing the event types such as "touchstart" or "click".
		 */
		function initialise() {
			/*
			 * If you need one that is not here then add it. Keep this list alphabetically sorted!
			 * Note, to help prevent typos the TYPE map is generated programatically off this array.
			 *
			 * Using these constants slightly increases the size of the minified script but reduces the memory footprint.
			 * Good for mobile devices.
			 */
			var i,
				len,
				next,
				types = ["animationstart", "animationend", "animationiteration", "beforeunload", "blur", "canplay", "change", "click", "dblclick", "ended", "focus", "focusin", "focusout", "input",
				"keydown", "keypress", "keyup", "load", "loadeddata", "loadedmetadata", "mousedown", "mousemove", "mouseout", "mouseover",
				"mouseup", "mousewheel", "paste", "pause", "play", "playing", "progress", "propertychange", "readystatechange", "resize", "scroll", "seeked", "seeking",
				"stop", "submit", "timeupdate", "touchcancel", "touchend", "touchmove", "touchstart", "transitionend", "unload", "volumechange", "waiting"];
			for (i = 0, len = types.length; i < len; i++) {
				next = types[i];
				types[next] = next;
			}
			return types;
		}

		/**
		 * @constructor
		 * @alias module:wc/dom/event~EventManager
		 * @private
		 */
		function EventManager() {
			var $this = this,
				BUBBLE_SUFFIX = ".bubble",
				CAPTURE_SUFFIX = ".capture",
				PRI = Observer.priority,
				fixEvent = new FixEvent(),
				ELID_ATTR = "elid",
				events = {},
				currentEvent = {},  // the type of the event currently being processed or null when no event being processed
				atTargetEvent,  // used to prevent eventListener firing twice in the target phase if attached using bubble and capture
				dom2 = has("dom-addeventlistener"),
				isFirefox = has("ff"),
				IE = has("ie");

			/**
			 * Provides a wrapper for an event listener. This listens for events then uses an instance of
			 * {@link module:wc/Observer} to mamage the calls the the event "listeners" in the ui modules.
			 *
			 * Major gotcha here that you need to be careful of if you are stomping around in here breaking all of my
			 * hard work. If an element has eventListener attached with both capture true and capture false AND an event
			 * originates at that element (i.e. it is the target of the event) then BOTH the capture and bubble
			 * eventListeners will be called (because that's what happens in the AT_TARGET phase). Furthermore, the
			 * event listeners must be called in the order they were added regardless of whether they were added using
			 * capture or bubble - in the target phase they effectively ignore capture and bubble and must be treated as
			 * one whole group.
			 *
			 * @function
			 * @private
			 * @throws {Error} Throws a generic error if the currentEvent[type] property is set (not false) as this
			 *    would mean an event calling itself for example by calling element.onXXXX().
			 */
			function eventListener(/* $event */) {
				var $event = arguments[0] || window.event,
					observer, phase = $event.eventPhase,
					type = $event.type,
					filter,
					elementElid;

				if (!currentEvent[type]) {
					currentEvent[type] = true;
				}
				else {
					throw new Error("eventListener calling itself? calling element.onXXXX() directly?");
				}
				if (fixEvent.fixInstance) {
					fixEvent.fixInstance($event, this);
				}
				try {
					if (phase === window.Event.BUBBLING_PHASE) {  // if both are undefined or if it actually is bubbling phase
						filter = type + BUBBLE_SUFFIX;
						atTargetEvent = null;
					}
					else if (phase === window.Event.CAPTURING_PHASE) {
						filter = type + CAPTURE_SUFFIX;
						atTargetEvent = null;
					}
					else if (phase === window.Event.AT_TARGET && atTargetEvent !== $event) {
						filter = targetPhaseFilterFactory(type);
						atTargetEvent = $event;  // flag that this event has already been handled in the target phase
					}
					if (filter) {
						elementElid = attribute.get($event.currentTarget, ELID_ATTR);
						observer = events[elementElid];
						observer.setCallback(function (result) {
							if ((result === false || $event.returnValue === false) && !$event.defaultPrevented) {
								$event.preventDefault();
							}
						});
						observer.setFilter(filter);
						observer.notify.call(this, $event);  // "call" so we pass through the scope
						if (type === $this.TYPE.beforeunload && IE > 0) {
							// we get a confirm dialog in IE if onbeforeunload returnValue is not undefined
							$event.returnValue = undefined;
						}
					}
					/*
					 * Returning ANYTHING from a beforeUnload event in IE will cause a confirmation dialog to
					 * be presented to the user every time they try to leave the page.
					 * I have decided to remove the return value mainly because of this. However I can see no
					 * reason why we should return anything when the event should be cancelled using preventDefault
					 * not by returning false.
					 *
					 * If it must be re-instated please conditionally return undefined if the event type is beforeunload.
					 * When you are testing this note that some versions of IE are MUCH worse than others when it comes to
					 * displaying this annoying confirmation dialog. IE9 seems to be particularly annoying in this regard.
					 */
					// return !($event.defaultPrevented);
				}
				finally {
					currentEvent[type] = false;
				}
			}

			/**
			 * Used as a filter for Observer when we are in the AT_TARGET phase and therefore need to
			 * notify listeners attached with and without capture.
			 * Two choices here: cache these filter functions (and use more memory) or leave them
			 * uncached (and use more CPU). There is no one true correct answer here.
			 *
			 * @function
			 * @private
			 * @param {String} type The event type.
			 * @returns {Function} A function which is used to filter based on target phase.
			 */
			function targetPhaseFilterFactory(type) {
				var bubble = type + BUBBLE_SUFFIX,
					capture = type + CAPTURE_SUFFIX;
				return function(group) {
					return group === bubble || group === capture;
				};
			}


			/**
			 * Add an event listener and subscribes a function to {@link module:wc/Observer} instance to handle the
			 * event. NOTE: we no longer support dom0 binding: get over it.
			 *
			 * @function module:wc/dom/event.add
			 * @param {Element} element The element to which the event listener will be associated.
			 * @param {string} type The type of event (eg 'click', 'focus' NOT 'onclick', 'onfocus')
			 * @param {Function} listener The event listener that will be called on the event
			 * @param {number} [pos] positive number = runs later, negative number = runs earlier
			 *    Note, the weird numbering convention is due to backwards compatibility support. Think of the
			 *    numbers as belonging to a timeline: |-ve ---- 0 ---- +ve|
			 * @param {object} [scope] The scope in which to call the listener (ie override the 'this')
			 * @param {boolean} [capture] If true the event will listen at the capture phase. Default is false
			 *    (listens at the bubble phase). If you set capture to true in a browser that does not support
			 *    capture an exception will be thrown.
			 * @returns {Boolean} true if the listener was able to be added as an event subscriber.
			 * @throws {TypeError} Thrown if the capture parameter is set true and the browser is not dom2 compliant.
			 */
			this.add = function (element, type, listener, pos, scope, capture) {
				var result, observer, group,
					priority = pos ? ((pos > 0) ? PRI.LOW : PRI.HIGH) : PRI.MED,
					elementElid = attribute.get(element, ELID_ATTR);

				if (!elementElid) {
					elementElid = attribute.set(element, ELID_ATTR, uid());
				}
				if ((capture = !!capture)) {  // test and cast to keep it pure for addEventListener
					if (dom2) {
						group = type + CAPTURE_SUFFIX;
					}
					else {
						throw new TypeError("Can not use capture in this browser");
					}
				}
				else {
					group = type + BUBBLE_SUFFIX;
				}
				observer = events[elementElid] || (events[elementElid] = new Observer());
				if (observer.isSubscribed(listener, group)) {
					console.warn("listener: ", listener, " already bound to: ", type, " on element: ", element);
					result = false;
				}
				else {
					if (observer.subscriberCount(group) < 0) {
						// if less than zero this is the first subscriber for this type on this element
						if (dom2) {
							// wham bam lighting fast test for modern browsers
							element.addEventListener(type, eventListener, capture);
						}
						else {
							// WARNING: with attachEvent "this" is ALWAYS "window" so we must bind it to the element
							element.attachEvent("on" + type, eventListener.bind(element));
						}
						// could fall back to dom 0 binding but meh, get with the program
					}
					observer.subscribe(listener, {group: group, context: scope, priority: priority});
					result = true;
				}
				return result;
			};

			/**
			 * Remove an event subscription from a particular element.
			 *
			 * Note, I removed the constraint which prevented you from removing an event listener that was currently
			 * being fired (ie it removed itself) as I think the problem being 'solved' here is already solved in the
			 * eventListener() code where a static snapshot of event listeners is taken before any of them are notified.
			 *
			 * @function module:wc/dom/event.remove
			 * @param {Element} element The element from which the event is removed.
			 * @param {string} type The type we are removing.
			 * @param {Function} listener The subscriber (listener) for the event.
			 * @param {boolean} capture True if the event is to be removed from the capture phase. Make sure this
			 *    matches where it was attached!
			 * @returns {Boolean} Returns true if the event was removed, false indicates the event was never on the
			 *    element in the first place.
			 */
			this.remove = function (element, type, listener, capture) {
				var result = false,
					group = capture ? type + CAPTURE_SUFFIX : type + BUBBLE_SUFFIX,
					elementElid = attribute.get(element, ELID_ATTR),
					observer = events[elementElid];
				if (observer) {
					result = !!observer.unsubscribe(listener, group);
				}
				return result;
			};

			/**
			 * Fire an event. **IMPORTANT** If you want to fire FOCUS then use
			 * {@link module:wc/dom/focus#setFocusRequest}.
			 *
			 * Note that text type controls are fired in a different way to other controls. This is necessary in some
			 * browsers but not in others; we'll keep it consistent unless there is a need not to.
			 *
			 * I have prevented events from firing while another event is currently firing to help prevent infinite
			 * loops (change call click which calls change). May be overly protective, could reduce it so that you can't
			 * fire the same event (eg click can't fire while click is firing).
			 *
			 * @function module:wc/dom/event.fire
			 * @param {Element} element The element to fire the event on.
			 * @param {Event} $event The event to fire (eg 'click')
			 * @returns {?Boolean} Should probably be undefined: use defaultPrevented to check if an event has ceased.
			 */
			this.fire = function (element, $event) {
				var rval, evt, tagName, type;
				if (!currentEvent[$event]) {
					if (element && $event) {
						tagName = element.tagName;
						type = element.type;
						if ($event !== $this.TYPE.submit && element[$event] &&
								!(type === "text" || type === "password" || tagName === tag.TEXTAREA || tagName === tag.SELECT)) {
							element[$event]();
						}
						else if (document.createEvent) {
							// won't fully simulate a click (ie naviagate a link)
							evt = document.createEvent("HTMLEvents");
							evt.initEvent($event, true, true); // type, bubbling, cancelable
							rval = !element.dispatchEvent(evt);
							if (!isFirefox && $event === $this.TYPE.submit) {
								// webkit browsers AND IE9 and above need this, firefox doesn't
								element[$event]();
							}
						}
						else {
							// won't fully simulate a click (ie naviagate a link)
							evt = document.createEventObject();
							rval = element.fireEvent("on" + $event, evt);
							if (rval !== false) {
								if ($event === $this.TYPE.submit) {
									element[$event]();
								}
							}
						}
					}
					else {
						throw new TypeError("arguments can not be null");
					}
				}
				else {
					console.log("Not firing ", $event, " while firing ", currentEvent, " Action queued.");
					timers.setTimeout($this.fire, 0, element, $event);
				}
				return rval;
			};

			/**
			 * Get a string that represents the state of this object for diagnostic purposes.
			 *
			 * @function module:wc/dom/event.toString
			 * @public
			 * @returns {String}
			 */
			this.toString = function() {
				var result = "";
				Object.keys(events).forEach(function(elid) {
					result += elid + ": " + events[elid].toString() + "\n";
				});
				return result;
			};

			/**
			 * Indicates if the browser supports dom2 event capture phase.
			 *
			 * @var
			 * @type {Boolean}
			 * @alias module:wc/dom/event.canCapture
			 * @public
			 */
			this.canCapture = dom2;
		}  // END EventManager

		/**
		 * A map of the event types able to be handled by the event manager.
		 * @var module:wc/dom/event.TYPE
		 * @type {Object}
		 * @public
		 * @todo is this really necessary?
		 */
		EventManager.prototype.TYPE = initialise();

		/**
		 * Event fixes for "older" browsers.
		 * Older browsers means:
		 * - FF5 and earlier
		 * - IE8 and earlier
		 * - Internet Explorer ANY VERSION where events are attached using old methods (like attachEvent)
		 *
		 * This functionality is kept separate from the main event manager code so it is easy to delete
		 * fixes for browsers we no longer support.
		 *
		 * Webkit browsers (Chrome / Safari) are sweet, need none of this. Same with FF6.
		 *
		 * @todo Fix constructors in frames?
		 * @todo Fix instances in ALL internet explorers to handle mixed event models on page? For example,
		 * some events could be attached using addEventListener some using attachEvent.
		 *
		 * @constructor
		 * @alias module:wc/dom/event~FixEvent
		 * @private
		 */
		function FixEvent() {
			var $this = this;

			/**
			 * If not false-y a function which can be used to 'fix' event instances.
			 * @var
			 * @type {(Boolean|Function)}
			 * @public
			 */
			$this.fixInstance = false;

/**
			 * Curry to create a default event prevention function polyfill.
			 * @function
			 * @private
			 * @param {Function} [truePreventDefault] The real (native code) preventDefault function if it is available.
			 * @returns {Function} A polyfill for preventDefault().
			 */
			function preventDefaultFactory(truePreventDefault) {
				/*
				 * @this {Event}
				 */
				return function () {
					if (truePreventDefault) {
						truePreventDefault.call(this);
					}
					/*
					 * Set returnValue for the benefit of IE8 and any version of IE if the event
					 * is not attached using addEventListener.
					 * In other browsers it should amount to a NOOP.
					 */
					this.returnValue = false;
					/**
					 * defaultPrevented must be set because this is the way we expect developers
					 * to check if an event has been cancelled or not. Opera needs the test since
					 * defaultPrevented is frozen
					 */
					if (!this.defaultPrevented) {
						this.defaultPrevented = true;
					}
				};
			}

			/**
			 * FF5 or earlier fixes.
			 *
			 * @function
			 * @private
			 * @param {String} sConstructor The name of the Event constructor to fix.
			 */
			function fixEventFirefox(sConstructor) {
				var proto, truePreventDefault, $constructor = window[sConstructor], propDescriptor;
				if ($constructor && (proto = $constructor.prototype)) {
					if ((propDescriptor = Object.getOwnPropertyDescriptor(proto, "preventDefault"))) {
						truePreventDefault = propDescriptor.value;
					}

					if (truePreventDefault) {
						Object.defineProperty(proto, "preventDefault", {
							value: preventDefaultFactory(truePreventDefault)
						});
					}
				}
			}

			/**
			 * Fix IE events.
			 * @function
			 * @private
			 * @param {Object} proto The event prototype to fix.
			 */
			function fixEventInternetExplorer(proto) {
				var iePreventDefault;
				// currentTarget needs to be set, so still need to fix each instance in IE8
				$this.fixInstance = fixInstance;

				if (proto) {
					// Internet Explorer 8 - has window.Event but non-standard event model
					addMethods(proto);
					Object.defineProperty(proto, "defaultPrevented", {
						get: function() {
							return (this.returnValue === false);
						}
					});
					Object.defineProperty(proto, "target", {
						get: function() {
							return this.srcElement;
						},
						set: function(arg) {
							this.srcElement = arg;
						}
					});
					proto = null;
				}

				/**
				 * Internet Explorer specific event fixes.
				 * Also "fixes" the target elements (current target etc).
				 *
				 * "Fixes" the event object so that it implements the expected interface.
				 * Has no effect (beyond wasting clock cycles) if called on an event that does not
				 * need fixing.
				 *
				 * @function module:wc/dom/event~FixEvent~fixEventInternetExplorer~fixInstance
				 * @private
				 * @param {Event} $event The instance of Event to fix
				 * @param {Element} $context The "this" the browser passed to the event handler.
				 * Note, we keep this "dumb" so that it works in Internet Explorer 7 and earlier.
				 *
				 */
				function fixInstance($event, $context) {
					addMethods($event);
					try {
						if (!$event.target && $event.srcElement) {
							$event.target = $event.srcElement;
						}
						if (!$event.currentTarget) {
							$event.currentTarget = $context;
						}
						/*
						 * About event.returnValue...
						 * Other browsers are now implementing it, however often a little different to IE8 and earlier.
						 * Although this is a boolean property IE actually has 3 states (undefined, true, false) while
						 * other browsers (like chrome) may only have two (true or false).
						 *
						 * This gets weird because if you set returnValue to undefined in IE it is treated as true,
						 * but in Chrome it is treated as false.
						 *
						 * Bottom line, you need to only use true and false with this property, not undefined.
						 */
						if (typeof $event.defaultPrevented === UNDEFINED) {
							$event.defaultPrevented = ($event.returnValue === false);
						}
					}
					catch (e) {
						console.warn(e);
					}
					return $event;
				}

				/**
				 * Stops propogation of an event.
				 * @function module:wc/dom/event~FixEvent~fixEventInternetExplorer~stopPropagation
				 * @private
				 * @this {Event}
				 */
				function stopPropagation() {
					this.cancelBubble = true;
				}

				/**
				 * Add stopPropagation and/or preventDefault methods to an event instance.
				 * @function module:wc/dom/event~FixEvent~fixEventInternetExplorer~addMethods
				 * @private
				 * @param {Object} obj The event object being fixed.
				 */
				function addMethods(obj) {
					if (typeof obj.preventDefault === UNDEFINED) {
						obj.preventDefault = iePreventDefault || (iePreventDefault = preventDefaultFactory(null));
					}
					if (typeof obj.stopPropagation === UNDEFINED) {
						obj.stopPropagation = stopPropagation;
					}
				}
			}

			// Do we need to handle constructors in frames?
			/**
			 * Fixes for event constructors
			 * @function
			 * @private
			 */
			function fixConstructors() {
				var evt,
					proto,
					eventSubClasses;
				if (window.Event && (proto = window.Event.prototype)) {
					if (!proto.preventDefault) {
						fixEventInternetExplorer(proto);
					}
					else if (!("defaultPrevented" in proto)) {  // quick test to skip FF6 and IE9
						/*
						 * If we are in here we are either in Firefox 5 (or ealier) OR Chrome.
						 * We want to weed out Chrome but "do stuff" if it is FF5.
						 * In FF6 and IE9 or greater you can check Event.prototype for "defaultPrevented",
						 * but in chrome you need to check an actual instance of event.
						 */
						if (typeof document.createEvent !== UNDEFINED) {
							evt = document.createEvent("HTMLEvents");
							if (!("defaultPrevented" in evt) || !("returnValue" in evt)) {
								fixEventFirefox("Event");
								// In Firefox many subclasses of Event override preventDefault
								eventSubClasses = ["KeyboardEvent", "MouseEvent", "StorageEvent", "MutationEvent"];
								eventSubClasses.forEach(fixEventFirefox);
							}
						}
					}
				}
			}

			fixConstructors();
		}

		has.add("event-ontouchstart", function(g) {
			return ("ontouchstart" in g);
		});

		has.add("event-ontouchend", function(g) {
			return ("ontouchend" in g);
		});

		has.add("event-ontouchcancel", function(g) {
			return ("ontouchcancel" in g);
		});

		has.add("event-ontouchmove", function(g) {
			return ("ontouchmove" in g);
		});

		return /** @alias module:wc/dom/event */ new EventManager();
	});
