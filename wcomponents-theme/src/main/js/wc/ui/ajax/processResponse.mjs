/**
 * This part of ajaxRegion is responsible for processing the AJAX response and updating the page accordingly.
 */

import Observer from "wc/Observer";
import toDocFragment from "wc/dom/toDocFragment";
import timers from "wc/timers";

let observer;
const ACTIONS = { FILL: "replaceContent", REPLACE: "replace", APPEND: "append", IN: "in" };
const errorUtils = {
		ajaxAttr: "data-wc-ajaxalias",
		replaceElement: function(element) {
			// not all elements can contain an error message (e.g. img, iframe, input) so replace it
			const errorElement = document.createElement("span");
			element.parentNode.replaceChild(errorElement, element);
			errorElement.id = element.id;
			errorElement.className = "wc_magic";  // if this happened to be lazy, let it be so once more
			if (element.hasAttribute(this.ajaxAttr)) {
				errorElement.setAttribute(this.ajaxAttr, element.getAttribute(this.ajaxAttr));
			}
			return errorElement;
		},
		flagError: function(args) {
			// fake flagError if there is a problem loading the helper
			const element = this.replaceElement(args.element);
			element.innerHTML = args.message;
		},
		fetch: function(callback) {
			const cb = function(errors) {
				if (errors?.flagError) {
					errorUtils._errors = errors;
				}
				if (callback) {  // could be a prefetch
					callback(errorUtils._errors || errorUtils);
				}
			};
			if (this._errors) {
				cb(this._errors);
			}
		}
	},
	OBSERVER_GROUP = "after";

const instance = {
	/**
	 * @var {Object} module:wc/ui/ajax/processResponse.actions The ajax action types : FILL, REPLACE or APPEND.
	 * @property {String} FILL Indicates the action will replace the content of the target.
	 * @property {String} REPLACE Indicates the action will replace the target.
	 * @property {String} APPEND Indicates the action will append its payload to the content of the target.
	 */
	actions: ACTIONS,

	/**
	 * Subscribers can chose to be notified before the DOM is updated with new content
	 * loaded via AJAX.
	 *
	 * @function module:wc/ui/ajax/processResponse.subscribe
	 * @param {Function} subscriber A callback function, will be passed the args: (element, content, action).
	 * @param {Boolean} [after] Indicates that the subscriber is to the post-insertion publisher.
	 * @returns {Function} The result of observer.subscribe
	 */
	subscribe: function(subscriber, after) {
		let group = null;
		observer = observer || new Observer();
		if (after) {
			group = { group: OBSERVER_GROUP };
		}
		return observer.subscribe(subscriber, group);
	},

	/**
	 * Removes a subscriber. Not usually used outside of testing (where it is indispensable).
	 * @function module:wc/ui/ajax/processResponse.unsubscribe
	 * @param {Function} subscriber the subscriber to remove
	 * @param {Boolean} [after] remove from the post-insertion subscribers.
	 */
	unsubscribe: function(subscriber, after) {
		if (observer) {
			const group = after ? OBSERVER_GROUP : null;
			observer.unsubscribe(subscriber, group);
		}
	},

	/**
	 * Expects an XML document where each child of the documentElement is an ajaxRegion "target".
	 *
	 * Note: do not "getElementsByTagName" due to IE8 bugs and limitations.
	 *
	 * Limitations: no "getElementsByTagNameNS".
	 *
	 * Bugs: Using some XMLHTTP engines (I tested Msxml2.XMLHTTP.6.0 and Msxml2.XMLHTTP.4.0)
	 * getElementsByTagName does not work on XML documents with a default and multiple namespaces. For example,
	 * in IE8 this would fail to return a result: `doc.getElementsByTagName(doc.firstChild.tagName);`
	 *
	 * Continue to use xpath!
	 *
	 * @function module:wc/ui/ajax/processResponse.processResponseXml
	 * @public
	 * @param {Document} response The ajax response.
	 * @param {module:wc/ajax/Trigger} trigger The trigger which triggered the ajax request.
	 */
	processResponseXml: function(response, trigger) {
		let promise;
		if (response) {
			promise = new Promise(function(resolve, reject) {
				if (typeof response === "string") {
					const doc = toDocFragment(response);
					processResponseHtml(doc, trigger);
					resolve();
				} else {
					reject("Unknown response type");
				}
			});
		} else {
			promise = Promise.reject("Response is empty");
		}
		return promise;
	},

	/**
	 * If there was an error attempt to inform the user of this.
	 * @function module:wc/ui/ajax/processResponse.processError
	 * @public
	 * @param {String} response An error message.
	 * @param {module:wc/ajax/Trigger} trigger The trigger which triggered the ajax request.
	 */
	processError: function(response, trigger) {
		const ids = trigger.loads,
			callback = function(feedback) {
				for (const id of ids) {
					let element = document.getElementById(id);
					if (element) {
						let errorElement = errorUtils.replaceElement(element);
						feedback.flagError({
							element: errorElement,
							message: response
						});
					}
				}
			};
		if (ids && response) {
			errorUtils.fetch(callback);
		}
	}
};



function processResponseHtml(documentFragment, trigger) {
	const onError = function() {

		// @ts-ignore
		require(["wc/ajax/handleError"], function(handleError) {
			// The AJAX response was malformed BUT reported as being successful.
			const message = handleError.getErrorMessage({ status: 200 });
			// This may not display perfectly, but it's better than literally nothing
			trigger.onerror(message, trigger);
		});
	};
	if (documentFragment) {
		let doc;
		if (documentFragment.querySelector) {
			doc = documentFragment.querySelector(".wc-ajaxresponse");
		} else {
			doc = documentFragment.firstElementChild || documentFragment.firstChild;
		}
		if (doc) {
			const targets = doc.querySelectorAll(".wc-ajaxtarget");
			for (const next of targets) {
				next.parentNode.removeChild(next);  // remove the target wrapper
				if (next.nodeType === Node.ELEMENT_NODE) {
					let targetId = next.getAttribute("data-id");
					let element = document.getElementById(targetId);
					if (element) {
						/* Since the ui:ajaxresponse is essentially thrown away we need to move any of its interesting attributes to the target element.
						 * In reality this is to catch the onLoadFocusId attribute, but we'll try to pretend it's generic. */
						mergeAttributes(doc, next);
						let action = next.getAttribute("data-action");
						let content = document.createDocumentFragment();
						while (next.firstChild) {
							content.appendChild(next.firstChild);
						}
						insertPayloadIntoDom(element, content, action, trigger, false);
					} else {
						console.warn("Could not find element", targetId);
					}
				}
			}
			// anything left after all the "target" wrappers are done can probably be inserted straight into the DOM (it's probably debug scripts)
			if (doc.children.length) {
				document.body.appendChild(documentFragment);
			}
		} else {
			console.warn("Response does not appear well formed");
			onError();
		}
	} else {
		console.warn("Response is empty");
		onError();
	}
}

/*
 * Copy attributes from one element to another, ignoring xmlns:* attributes.
 * @param {Element} source The element which has the attributes we want to copy from.
 * @param {Element} dest The element we will copy the attributes to.
 */
function mergeAttributes(source, dest) {
	for (const next of source.attributes) {
		if (next.name.indexOf("xmlns:") < 0) {
			// no point copying over boring old xmlns attributes
			dest.setAttribute(next.name, next.value);
		}
	}
}

function insertPayloadIntoDom(element, content, action, trigger, doNotPublish) {
	let actionMethod;
	const triggerId = (trigger && trigger.id) ? trigger.id : null;
	switch (action) {
		case ACTIONS.REPLACE:
			if (!element.matches("body")) {
				actionMethod = replaceElement;
			} else {
				console.warn("Refuse to replace BODY element, use action", ACTIONS.FILL);
			}
			break;
		case ACTIONS.FILL:
			actionMethod = replaceElementContent;
			break;
		case ACTIONS.APPEND:
			actionMethod = appendElementContent;
			break;
		case ACTIONS.IN:
			actionMethod=replaceIn;
			break;
		default:
			console.warn("Unknown action", action);
			break;
	}
	if (actionMethod) {
		// Pre-Insertion subscribers called here
		if (observer && !doNotPublish) {
			observer.notify(element, content, action, triggerId);
		}
		const _element = actionMethod(element, content);

		// Post-Insertion subscribers called here (document fragment content now irrelevant)
		if (observer && !doNotPublish && _element) {
			if (Array.isArray(_element)) {
				_element.forEach(function(nextElement) {
					observer.setFilter(OBSERVER_GROUP);
					observer.notify(nextElement, action, triggerId);
				});
			} else {
				observer.setFilter(OBSERVER_GROUP);
				observer.notify(_element, action, triggerId);
			}
		}
	}
}

/*
 * Populate element with content replacing any child nodes that may already exist
 * @param element The element to populate with content
 * @param content A documentFragment node
 */
function replaceElementContent(element, content) {
	const _content = content;
	element.innerHTML = "";  // have to blat the contents first, otherwise browser doesn't reliably pick up changes
	const duplicates = checkDuplicateIds(_content);
	removeDuplicateElements(duplicates);
	const scripts = extractScriptsFromContent(_content);
	let child;
	while ((child = _content.firstChild)) {
		element.appendChild(child);
	}
	insertScripts(scripts, element);
	return element;
}

function appendElementContent(element, content) {
	const _content = content;
	const duplicates = checkDuplicateIds(_content);
	removeDuplicateElements(duplicates);
	const scripts = extractScriptsFromContent(_content);
	let child;
	while ((child = _content.firstChild)) {
		element.appendChild(child);
	}
	insertScripts(scripts, element);
	return element;
}

/*
 * Replace element with content
 * @param element The element to replace with content
 * @param content A documentFragment node
 */
function replaceElement(element, content) {
	const parent = element.parentNode,
		_content = content;
	let result = [];
	let duplicates = [element];

	element.removeAttribute("id");
	duplicates = duplicates.concat(checkDuplicateIds(_content));
	const scripts = extractScriptsFromContent(_content);
	// insert the content documentfragment's child/ren (currently only one child in replace ) in position to replace element
	let child;
	while ((child = _content.firstChild)) {
		if (child.nodeType === Node.ELEMENT_NODE) {
			result[result.length] = parent.insertBefore(child, element);
		} else {
			_content.removeChild(child);
		}
	}

	removeDuplicateElements(duplicates);

	if (result.length === 1) {
		result = result[0];
	} else if (!result.length) {
		result = null;
	}
	insertScripts(scripts, parent);
	return result;
}

/**
 * Replace specified elements within a given element in the originating document with the contents of the
 * ajax response. If the elements which are immediate children of content are not in the originating
 * document's version of element then they are appended to element.
 *
 * @function
 * @private
 * @param {Element} element The containing element in the original document.
 * @param {DocumentFragment} content The document fragment containing the replacement(s).
 * @returns {Element}
 */
function replaceIn(element, content) {
	let child,
		result;
	const wrapper = document.createElement("div");

	while ((child = content.firstChild)) {
		let _element;
		wrapper.appendChild(child);
		let id = child.id;
		if (id) {
			if ((_element = document.getElementById(id))) {
				result = replaceElement(_element, wrapper);
			} else {
				result = appendElementContent(element, wrapper);
			}
		} else {
			result = appendElementContent(element, wrapper);
		}

		if (wrapper.firstChild) { // should have been removed.
			wrapper.removeChild(wrapper.firstChild);
		}
	}
	return result;
}

function removeDuplicateElements(duplicates) {
	duplicates.forEach(function(next) {
		next.parentNode.removeChild(next);
	});
}

/**
 * Dynamic injection of scripts is NOT as easy as you may imagine so don't change this
 * unless you are prepared to test extensively.
 * @param {HTMLScriptElement[]} scripts Array of script elements.
 * @param {Element} relativeTo use this as the basis for finding to form into which we want to insert the scripts.
 */
function insertScripts(scripts, relativeTo) {
	const srcAttr = "src", defer = "defer";
	let ownerElement = document.body;

	if (relativeTo && relativeTo.nodeType === Node.ELEMENT_NODE) {
		ownerElement = relativeTo.closest("form") || document.body;
	}

	while (scripts.length) {
		let next = scripts.shift();
		let newScript = document.createElement("script");
		newScript.setAttribute(defer, defer);  // newScript.defer='defer' results in defer='' in IE8
		let clazzName = next.className;
		if (clazzName) {
			newScript.className = clazzName;
		}
		let src = next.getAttribute(srcAttr);
		if (src) {
			newScript.setAttribute(srcAttr, src);
		} else {
			let textProp = next.textContent ? "textContent" : "text";
			newScript[textProp] = next[textProp];
		}
		let scrId = next.id;
		if (scrId) {
			newScript.id = scrId;
		}
		ownerElement.appendChild(newScript);
	}
}

/*
 * Remove and return all script elements from within the content
 * @param content A DOM node
 * @returns Array of script elements which have been REMOVED from content.
 */
function extractScriptsFromContent(content) {
	const result = [];
	try {
		const scriptSelector = "script";
		let scripts;
		if (typeof content.querySelectorAll !== "undefined") {
			scripts = content.querySelectorAll(scriptSelector);
		} else {
			scripts = content.getElementsByTagName(scriptSelector);
		}

		for (const element of scripts) {
			result[result.length] = element.parentNode.removeChild(element);
		}
	} catch (ex) {
		console.error("Could not extract scripts from content ", ex.message);
	}
	return result;
}

/*
 * Scan the HTML for ids that already exist in the document and would
 * cause duplicates if the html is inserted into the document.
 *
 * NOTE: This does jobs like automatically clean up DDs if the element
 * we are replacing is a DT.  It will only help if the DDs have an ID.
 * We could blat all DDs adjacent to the DT but there may be legitimate
 * cases where they actually want to append a new DD rather than replace
 * the existing ones.
 */
function checkDuplicateIds(content) {
	const result = [];

	if (content) {
		if (typeof content.querySelectorAll !== "undefined") {
			checkDuplicateIdsElement(content);
		} else if (content.constructor === String) {
			checkDuplicateIdsHtml(content);
		}
	}

	function checkDuplicateIdsHtml(html) {
		const idRe = /id=['|"]?([a-zA-Z0-9_-]+)['|"]/g;
		let matches;
		while ((matches = idRe.exec(html))) {
			let nextId = matches[1];
			let nextElement = document.getElementById(nextId);
			if (nextElement) {
				console.info("Removing element to prevent duplicate ID", nextId);
				removeDuplicate(nextElement);
			}
		}
	}

	/**
	 * Remove elements from DOM with duplicate IDs in the documentFragment.
	 * @param {DocumentFragment} documentFragment
	 */
	function checkDuplicateIdsElement(documentFragment) {
		const candidates = Array.from(documentFragment.querySelectorAll("[id]"));
		for (const item of candidates) {
			let nextId = item.id;
			let nextElement = nextId ? document.getElementById(nextId) : null;
			if (nextElement) {
				console.info("Removing element to prevent duplicate ID", nextId);
				removeDuplicate(nextElement);
			}
		}
	}

	function removeDuplicate(dup) {
		result[result.length] = dup;
		dup.removeAttribute("id");
	}

	return result;
}

/*
 * Prefetch the error handler.
 *
 * - Why not fetch it right up front? Because it is only required under exceptional conditions.
 * - Why not wait until an error occurs to fetch it? Because the error may also prevent modules being loaded.
 * - Why not use HTML5 link preloading to fetch it (loader/prefetch.js)?
 * Technical reasons: this would fetch the module but not its dependencies.
 * Non-technical reasons: "request counters" and "byte counters" do not understand that the preload would
 * utilize browser idle time to asynchronously load resources in a way that does not adversly affect the user.
 */
timers.setTimeout(errorUtils.fetch, 60000);

export default instance;
