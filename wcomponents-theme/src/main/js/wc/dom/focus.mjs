/**
 * Provides a mechanism to set, test and manipulate focus. This is not as facile as it may appear.
 *
 * If you have a module which has to set focus then it really should use this module rather than attempting to focus
 * by itself (for example by using element.focus()).
 */
import Observer from "wc/Observer";
import getStyle from "wc/dom/getStyle";
import shed from "wc/dom/shed";
import uid from "wc/dom/uid";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import timers from "wc/timers";

const PRIMARY_TABSTOPS = ["a", "area", "audio", "button", "frame", "iframe", "input", "object", "select", "textarea", "video"];
const {
	FILTER_ACCEPT: ACCEPT,
	FILTER_REJECT: REJECT,
	FILTER_SKIP: SKIP
} = NodeFilter;

let focusObserver,
	tabstopObserver,
	timeout,
	focusElementId,
	setFocusCallback;

const focusInstance = {
	/**
	 * Indicates that an element is a viable tab stop: ie, may be focusable
	 *
	 * @function module:wc/dom/focus.isTabstop
	 * @param {HTMLElement} element The element to test.
	 * @returns {Boolean} true if the element is a viable tab stop.
	 */
	isTabstop: function(element) {
		if (!element?.nodeType) {
			return false;
		}
		return filterHelper(element, getTabstopObserver(), this);
	},

	/**
	 * Indicates that an element natively focusable in a browser. This is based on the HTML5 spec but explicitly
	 * excludes IMG.
	 *
	 * @function module:wc/dom/focus.isNativelyFocusable
	 * @param {String} tagName The name of the element to test.
	 * @returns {Boolean} true if the element is natively focusable.
	 */
	isNativelyFocusable: function(tagName) {
		return !!(tagName && ~PRIMARY_TABSTOPS.indexOf(tagName.toLowerCase()));
	},

	/**
	 * Determine if an element could receive focus via the keyboard.
	 *
	 * @function module:wc/dom/focus.canFocus
	 * @param {HTMLElement} element The element node to test.
	 * @returns {Boolean} true if the element can receive focus
	 * @todo does not work back through its ancestors to determine if one of them excludes the element from
	 *    receiving focus this could be implemented as a filter.
	 */
	canFocus: function(element) {
		const observer = getFocusObserver();
		return filterHelper(element, observer, this);
	},

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
	 * @param {HTMLElement} element the element to focus
	 * @param {Function} [callback] a function to call when the element has focus.
	 * @todo Make this return a promise.
	 */
	setFocusRequest: function(element, callback) {
		if (element?.nodeType === Node.ELEMENT_NODE) {
			focusElementId = (element.id || (element.id = uid()));  // instance variable
			if (timeout) {
				timers.clearTimeout(timeout);
				timeout = null;
			}
			timeout = timers.setTimeout(function() {
				try {
					if (focusElementId) {
						const focusElement = document.getElementById(focusElementId);
						if (focusElement) {
							setFocusCallback = callbackFactory(focusElement, callback);
							if (document.activeElement !== focusElement) {  // do not refocus
								focusElement.focus();
							}
							setFocusCallback();
						}
					}
				} catch (err) {
					console.log("cannot focus element with id " + focusElementId + " " + err.message);
				}
			}, 0);
		} else {
			throw new TypeError("Cannot focus something that ain't an element!");
		}
	},

	/**
	 * Focus the first (or last) visible non-disabled field.
	 * If the list of input types are omitted then by default DEFAULT_TABSTOPS are used.
	 * In the case of radio buttons the field must also be checked, otherwise we would be changing the state of the
	 * form as focussing it also checks it
	 * Once the list of tabstop eligible elements has been determined, we proceed to
	 * call them against the canFocus() public method until one passes.
	 *
	 * @function module:wc/dom/focus.focusFirstTabstop
	 * @param {HTMLElement} container The element in which we want to place the focus.
	 * @param {Function} [callback] Callback function which will be notified when we (attempt to) set
	 *    focus on a particular element.
	 * @param {Boolean} [reverse] If true then elements will be tried in reverse order. In other words this
	 *    function becomes "focusLastTabstop".
	 * @returns {HTMLElement} The element that received focus.
	 */
	focusFirstTabstop: function (container, callback, reverse) {
		let result;
		const tw = document.createTreeWalker(container, NodeFilter.SHOW_ELEMENT, acceptNode);  // NOTE: yes passing a function rather than a NodeFilter object is non-standard but IE's treewalker is broken and others are happy with this
		let next = reverse ? tw.lastChild() : tw.firstChild();
		do {
			try {
				setFocusCallback = callbackFactory(next, callback);
				next.focus();
				setFocusCallback();
				result = next;
				break;
			} catch (e) {
				result = null;
			}
		}
		while ((next = reverse ? tw.previousNode() : tw.nextNode()));

		return result;
	},

	/**
	 * Determine if an element's descendant elements contain at least one focusable element.
	 *
	 * @function module:wc/dom/focus.canFocusInside
	 * @param {HTMLElement} element The element node to test.
	 * @returns {Boolean} true if the element has at least one child which can receive focus.
	 */
	canFocusInside: function(element) {
		let result = false;
		if (element.children) {  // no point doing any of this if we do not have any child elements
			const widgetMap = ["[tabindex]"].concat(PRIMARY_TABSTOPS).join();  // note: tabIndex 0 could be OK here as it is the only non-negative tabIndex we support
			const candidates = element.querySelectorAll(widgetMap);
			if (candidates.length) {
				result = Array.from(candidates).some(this.canFocus, this);
			}
		}
		return result;
	},

	/**
	 * Get the first ancestor element which can accept focus.
	 *
	 * @function module:wc/dom/focus.getFocusableAncestor
	 * @param {HTMLElement} element The element from which to start the focusable hunt.
	 * @param {Boolean} [ignoreSelf] set true if we want to explicitly ignore the current element otherwise will
	 *    return element if it is itself focusable.
	 * @returns {HTMLElement} the first ancestor element which can receive focus (if any).
	 */
	getFocusableAncestor: function(element, ignoreSelf) {
		if (!element) {
			return null;
		}

		if (!ignoreSelf && this.canFocus(element)) {
			return element;
		}
		const filter = function (node) {
			let result = SKIP;
			if (focusInstance.isTabstop(node) && focusInstance.canFocus(node)) {
				result = ACCEPT;
			}
			return result;
		};

		const tw = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, filter);
		tw.currentNode = element;
		return tw.parentNode();
	}
};

function filterHelper(element, observer, instance) {
	let result = null;

	// if this filter explicitly accepts the node, we can focus it
	// if the node isn't accepted as focus eligible, then see if it was explicitly rejected.
	function callback(decision) {
		result = (decision === ACCEPT || decision === REJECT) ? decision : SKIP;
	}

	observer.setCallback(callback);
	observer.notify(element, instance);

	// make result a boolean decision
	//    if the element was accepted explicitly (ACCEPT)
	return (result === ACCEPT);
}

function addTabstopFilter(fn) {
	const observer = getTabstopObserver();
	return observer.subscribe(fn);
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
function standardTabstopFilter(element, instance) {
	let result = SKIP;
	if (element) {
		/*
		 * if the element is a radio button that is not selected, it is not actually a tabstop
		 * unless the radio button is the first radio button in a group and none of the group's
		 * radio buttons are checked.
		 */
		if (shed.isDisabled(element) || shed.isHidden(element)) {
			result = REJECT;
		} else if (element.matches("input[type='radio']") && !shed.isSelected(element)) {
			if (getFilteredGroup(element).length) {
				result = REJECT;
			} else {
				result = ACCEPT;
			}
		} else {
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
	const observer = getFocusObserver();
	return  observer.subscribe(fn);
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
	let result = SKIP;
	if (element) {
		if ((element.type === "hidden") ||
			(shed.isDisabled(element)) ||
			(getStyle(element, "visibility", false, true) === "hidden") ||
			(getStyle(element, "display", false, true) === "none")) {
			result = REJECT;
		} else {
			result = focusTabHelper(element, instance);
		}
	}
	return result;
}

// zeroDimensionsFocusFilter
// rejects elements that are 0:0 in width:height
function zeroDimensionFocusFilter(element, instance) {
	let result = standardFocusFilter(element, instance);
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
	let called = false;
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
	let result = SKIP;
	if (focusInstance.isTabstop(node) && focusInstance.canFocus(node)) {
		if (node !== document.activeElement) {
			result = ACCEPT;
		} else {
			result = REJECT;
		}
	}
	return result;
}

function focusTabHelper(element, instance) {
	let tabIndex = element.getAttribute("tabindex"),
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
	} else if (instance.isNativelyFocusable(element.tagName)) {
		tabIndex = 0;
	}
	if (tabIndex || tabIndex === 0) {
		if (tabIndex >= 0) {
			result = ACCEPT;
		} else {
			result = REJECT;
		}
	}
	return result;
}

export default focusInstance;
