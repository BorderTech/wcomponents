/**
 * Provides a mechanism to set, test and manipulate focus. This is not as facile as it may appear.
 *
 * If you have a module which has to set focus then it really should use this module rather than attempting to focus
 * by itself (for example by using element.focus()).
 *
 * @module
 *
 * @requires module:wc/array/toArray
 * @requires module:wc/dom/tag
 * @requires module:wc/Observer
 * @requires module:wc/dom/getStyle
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/timers
 *
 * @todo re-order code, comment private members.
 */
define(["wc/array/toArray",
		"wc/dom/tag",
		"wc/Observer",
		"wc/dom/getStyle",
		"wc/dom/shed",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/dom/getFilteredGroup",
		"wc/timers"],
	/** @param toArray wc/array/toArray @param tag wc/dom/tag @param Observer wc/Observer @param getStyle wc/dom/getStyle @param shed wc/dom/shed @param uid wc/dom/uid @param Widget wc/dom/Widget @param getFilteredGroup wc/dom/getFilteredGroup @param timers wc/timers @ignore */
	function(toArray, tag, Observer, getStyle, shed, uid, Widget, getFilteredGroup, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/focus~Focus
		 * @private
		 */
		function Focus() {
			var focusObserver = null,  // instance of Observer();
				tabstopObserver = null,
				timeout = null,
				focusElementId = null,
				setFocusCallback = null,
				PRIMARY_TABSTOPS,
				// constants for the focusFilter functions
				ACCEPT,
				REJECT,
				SKIP,
				inited = false,
				widgetMap;

			/**
			 * Indicates that an element is a viable tab stop: ie, may be focusable
			 *
			 * @function module:wc/dom/focus.isTabstop
			 * @param {Element} element The element to test.
			 * @returns {Boolean} true if the element is a viable tab stop.
			 */
			this.isTabstop = function(element) {
				initialise();
				var result = null,
					observer = getTabstopObserver();
				result = filterHelper(element, observer, this);
				return result;
			};


			/**
			 * Indicates that an element natively focusable in a browser. This is based on the HTML5 spec but explicitly
			 * excludes IMG.
			 *
			 * @function module:wc/dom/focus.isNativelyFocusable
			 * @param {String} tagName The name of the element to test.
			 * @returns {Boolean} true if the element is natively focusable.
			 */
			this.isNativelyFocusable = function(tagName) {
				initialise();
				return !!(~PRIMARY_TABSTOPS.indexOf(tagName));
			};

			/**
			 * Determine if an element could receive focus via the keyboard.
			 *
			 * @function module:wc/dom/focus.canFocus
			 * @param {Element} element The element node to test.
			 * @returns {Boolean} true if the element can receive focus
			 * @todo does not work back through its ancestors to determine if one of them excludes the element from
			 *    receiving focus this could be implemented as a filter.
			 */
			this.canFocus = function(element) {
				initialise();
				var result = false,
					observer = getFocusObserver();
				result = filterHelper(element, observer, this);
				return result;
			};

			/**
			 * Sets up a timeout to focus an element. Optional callback function
			 * Only one element will gain focus so several calls to setFocusRequest
			 * will result in only the last call being honoured
			 *
			 * Callbacks are cancelled if the call is cancelled. We used to queue them up
			 * but this caused horrendous performance issues, even in extremely fast browsers.
			 * Don't ever restore that behaviour.
			 *
			 * @function module:wc/dom/focus.setFocusRequest
			 * @throws TypeError if element is not an Element node
			 * @param {Element} element the element to focus
			 * @param {Function} [callback] a function to call when the element has focus.
			 * @todo Make this return a promise.
			 */
			this.setFocusRequest = function(element, callback) {
				var focusElement;
				if (element && element.nodeType === Node.ELEMENT_NODE) {
					initialise();
					focusElementId = (element.id || (element.id = uid()));  // instance variable
					if (timeout) {
						timers.clearTimeout(timeout);
						timeout = null;
					}
					timeout = timers.setTimeout(function() {
						try {
							if (focusElementId) {
								focusElement = document.getElementById(focusElementId);
								if (focusElement) {
									setFocusCallback = callbackFactory(focusElement, callback);
									if (document.activeElement !== focusElement) {  // do not refocus
										focusElement.focus();
									}
									setFocusCallback();
								}
							}
						}
						catch(err) {
							console.log("cannot focus element with id " + focusElementId + " " + err.message);
						}
					}, 0);
				}
				else {
					throw new TypeError("Cannot focus something that ain't an element!");
				}
			};

			 /**
			 * Focus the first (or last) visible non-disabled field.
			 * If the list of input types are omitted then by default DEFAULT_TABSTOPS are used.
			 * In the case of radio buttons the field must also be checked, otherwise we would be changing the state of the
			 * form as focussing it also checks it
			 * Once the list of tabstop eligible elements has been determined, we proceed to
			 * call them against the canFocus() public method until one passes.
			 *
			 * @function module:wc/dom/focus.focusFirstTabstop
			 * @param {Element} container The element in which we want to place the focus.
			 * @param {Function} [callback] Callback function which will be notified when we (attempt to) set
			 *    focus on a particular element.
			 * @param {Boolean} [reverse] If true then elements will be tried in reverse order. In other words this
			 *    function becomes "focusLastTabstop".
			 * @returns {?Element} The element that received focus.
			 */
			this.focusFirstTabstop = function (container, callback, reverse) {
				var next,
					result = null,
					tw = document.createTreeWalker(container, NodeFilter.SHOW_ELEMENT, acceptNode, false);  // NOTE: yes passing a function rather than a NodeFilter object is non-standard but IE's treewalker is broken and others are happy with this
				initialise();
				next = reverse ? tw.lastChild() : tw.firstChild();
				do {
					try {
						setFocusCallback = callbackFactory(next, callback);
						next.focus();
						setFocusCallback();
						result = next;
						break;
					}
					catch(e) {
						result = null;
					}
				}
				while ((next = reverse ? tw.previousNode() : tw.nextNode()));

				return result;
			};

			/**
			 * Determine if an element's descendant elements contain at least one focusable element.
			 *
			 * @function module:wc/dom/focus.canFocusInside
			 * @param {Element} element The element node to test.
			 * @returns {Boolean} true if the element has at least one child which can receive focus.
			 */
			this.canFocusInside = function(element) {
				var candidates = null,
					result = false;

				if (element.children) {  // no point doing any of this if we do not have any child elements
					initialise();
					if (!widgetMap) {
						widgetMap = (PRIMARY_TABSTOPS.map(function(next) {
							return new Widget(next);
						})).concat(new Widget("", "", { "tabindex": null }));  // note: tabIndex 0 could be OK here as it is the only non-negative tabIndex we support
					}
					candidates = Widget.findDescendants(element, widgetMap);
					if (candidates && candidates.length) {
						candidates = toArray(candidates);
						result = candidates.some(this.canFocus, this);
					}
				}
				return result;
			};

			/**
			 * Get the first ancestor element which can accept focus.
			 *
			 * @function module:wc/dom/focus.getFocusableAncestor
			 * @param {Element} element The element from which to start the focusable hunt.
			 * @param {Boolean} [ignoreSelf] set true if we want to explicitly ignore the current element otherwise will
			 *    return element if it is itself focusable.
			 * @returns {?Element} the first ancestor element which can receive focus (if any).
			 */
			this.getFocusableAncestor = function(element, ignoreSelf) {
				var result, tw, filter;

				if (!ignoreSelf && this.canFocus(element)) {
					result = element;
				}
				else {
					filter = function (node) {
						var result = SKIP;
						if (instance.isTabstop(node) && instance.canFocus(node)) {
							result = ACCEPT;
						}
						return result;
					};

					tw = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, filter, false);
					initialise();
					tw.currentNode = element;
					result = tw.parentNode();
				}
				return result;
			};

			/**
			 * Prepare this class for its first use.
			 * - Set up PRIMARY_TABSTOPS array.
			 * @function
			 * @private
			 */
			function initialise() {
				if (!inited) {
					inited = true;
					PRIMARY_TABSTOPS = [tag.A, tag.AREA, tag.AUDIO, tag.BUTTON, tag.FRAME, tag.IFRAME, tag.INPUT, tag.OBJECT, tag.SELECT, tag.TEXTAREA, tag.VIDEO];
					ACCEPT = NodeFilter.FILTER_ACCEPT;
					REJECT = NodeFilter.FILTER_REJECT;
					SKIP = NodeFilter.FILTER_SKIP;
				}
			}

			function filterHelper(element, observer, instance) {
				var result = null;

				// if this filter explicitly accepts the node, we can focus it
				// if the node isnt accepted as focus eligible, then see if it was explicitly rejected.
				function callback(decision) {
					result = (decision === ACCEPT || decision === REJECT) ? decision : SKIP;
				}

				observer.setCallback(callback, null);
				observer.notify(element, instance);

				// make result a boolean decision
				//    if the element was accepted explicitly (ACCEPT)
				return (result === ACCEPT);
			}

			function addTabstopFilter(fn) {
				var subscriber = null,
					observer = getTabstopObserver();
				subscriber = observer.subscribe(fn);
				return subscriber;
			}

			function getTabstopObserver() {
				if (!tabstopObserver) {
					tabstopObserver = new Observer();
					// add default filters
					addTabstopFilter(standardTabstopFilter);
				}
				return tabstopObserver;
			}


			// default tabstops are elements that will be checked to determine if
			//    they can be focussed
			// http://msdn.microsoft.com/en-us/library/ms534654(VS.85).aspx

			// accepts primary and secondary tabstops for IE
			// TODO: add in code that says if (IE). Then add another filter for
			//    if (!IE) for other browsers, if they differ in logic.
			function standardTabstopFilter(element, instance) {
				var result = SKIP, tagName;
				if (element) {
					tagName = element.tagName;
					/*
					 * if the element is a radio button that is not selected, it is not actually a tabstop
					 * unless the radio button is the first radio button in a group and none of the group's
					 * radio buttons are checked.
					 */
					if (shed.isDisabled(element) || shed.isHidden(element) || element.type === "hidden" || getStyle(element, "visibility", false, true) === "hidden") {
						result = REJECT;
					}
					else if (tagName === tag.INPUT && element.type === "radio" && !shed.isSelected(element)) {
						if (getFilteredGroup(element).length) {
							result = REJECT;
						}
						else {
							result = ACCEPT;
						}
					}
					else {
						result = focusTabHelper(element, instance);
					}
				}
				return result;
			}

			/**
			* Whenever an element is being tested by canFocus
			* focus filters are called to determine if the element is eligible for focusing
			* Thi method allows adding custom filters to that process.
			* The filter function must return one of the following constants:
			*  - ACCEPT
			*  - REJECT
			*  - SKIP
			* SKIP means you are unsure if the element should not receive focus. This should be used
			* in preference to ACCEPT/REJECT, which is for cases when
			* you KNOW WITHOUT A DOUBT that the element may/not receive focus.
			* In cases where both ACCEPT and REJECT are returned by different filter functions,
			* ACCEPT takes precedence.
			*
			* @function
			* @private
			* @param {Function} fn The function which is subscribed.
			*/
			function addFocusFilter(fn) {
				var subscriber = null,
					observer = getFocusObserver();
				subscriber = observer.subscribe(fn);
				return subscriber;
			}

			function getFocusObserver() {
				if (!focusObserver) {
					focusObserver = new Observer();
					// add default filters
					addFocusFilter(standardFocusFilter);
					addFocusFilter(zeroDimensionFocusFilter);
				}
				return focusObserver;
			}

			// default focus filter rejects
			//   - input[type=hidden]
			//   - elements with a disabled attribute
			//   - elements that are invisible or hidden via CSS
			function standardFocusFilter(element, instance) {
				var result = SKIP;
				if (element) {
					if ((element.type === "hidden") ||
						(shed.isDisabled(element)) ||
						(getStyle(element, "visibility", false, true) === "hidden") ||
						(getStyle(element, "display", false, true) === "none")) {
						result = REJECT;
					}
					else {
						result = focusTabHelper(element, instance);
					}
				}
				return result;
			}

			// zeroDimensionsFocusFilter
			// rejects elements that are 0:0 in width:height
			function zeroDimensionFocusFilter(element, instance) {
				var result = standardFocusFilter(element, instance);
				if (element.offsetWidth === 0 && element.offsetHeight === 0) {
					result = REJECT;
				}
				return result;
			}

			/*
			 * Generate a callback function for setFocusRequest.
			 * The callback wraps the actual callback and calls it in accordance with the expected behavior.
			 */
			function callbackFactory(element, callback) {
				var called = false;
				return function() {
					if (!called && typeof callback === "function") {
						called = true;
						callback(element);
					}
				};
			}

			/**
			 * Treewalker filter for focusFirstTabstop.
			 * @function
			 * @private
			 * @param {Node} node The node to test.
			 * @returns {number} One of NodeFilter.FILTER_ACCEPT, NodeFilter.FILTER_REJECT or NodeFilter.FILTER_SKIP.
			 */
			function acceptNode(node) {
				var result = SKIP;
				if (instance.isTabstop(node) && instance.canFocus(node)) {
					if (node !== document.activeElement) {
						result = ACCEPT;
					}
					else {
						result = REJECT;
					}
				}
				return result;
			}

			function focusTabHelper(element, instance) {
				var tagName = element.tagName.toUpperCase(),
					tabIndex = element.getAttribute("tabindex"),
					result = SKIP;
				/*
				 * NOTE! Read the comments below before you consider changing this code.
				 *
				 * We need to be able to focus non-primary tab stop elements which have
				 * an explicit tabIndex of 0 (since this is the 'I am a tab stop but not out of
				 * flow' setting. So now we use getAttribute("tabindex") and if we get something
				 * parseInt it. If we do not have an explicit tabIndex then the assumed tabIndex
				 * is 0 for an element with native focusabilty.
				 */
				if (tabIndex || tabIndex === "0") {
					tabIndex = parseInt(tabIndex, 10);
				}
				else if (instance.isNativelyFocusable(tagName)) {
					tabIndex = 0;
				}
				if (tabIndex || tabIndex === 0) {
					if (tabIndex >= 0) {
						result = ACCEPT;
					}
					else {
						result = REJECT;
					}
				}
				return result;
			}
		}

		/*
		 * NOTE for Rick:
		 * I would prefer to not use an instance here as it is really only being used to
		 * work around a funky bind() problem with focusFirstTabstop which would require a
		 * fleet of bindings right through the calls.
		 */
		var /** @alias module:wc/dom/focus */ instance = new Focus();
		return instance;
	});
