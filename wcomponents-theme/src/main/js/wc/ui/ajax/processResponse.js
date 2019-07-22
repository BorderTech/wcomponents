define(["wc/Observer",
	"wc/dom/tag",
	"wc/dom/toDocFragment",
	"wc/dom/Widget",
	"wc/timers"],
	function(Observer, tag, toDocFragment, Widget, timers) {
		"use strict";
		var ACTIONS = { FILL: "replaceContent", REPLACE: "replace", APPEND: "append", IN: "in" };
		/**
		 * @constructor
		 * @alias module:wc/ui/ajax/processResponse~AjaxProcessor
		 * @private
		 */
		function AjaxProcessor() {
			var observer,
				/*
				 * The module that can provide ADVANCED error feedback, otherwise rudimentary fallback used.
				 * We fetch this lazily because it is only used if errors occur (therefore should not always be needed).
				 * If the network is down then it will not be available in which case we use fallback.
				 */
				feedbackModule = "wc/ui/feedback",
				errorUtils = {
					ajaxAttr: "data-wc-ajaxalias",
					replaceElement: function(element) {
						// not all elements can contain an error message (e.g. img, iframe, input) so replace it
						var errorElement,
							state = { id: element.id },
							errorWd = ERROR_WD || (ERROR_WD = new Widget("span", "wc_magic"));  // if this happened to be lazy, let it be so once more
						state[this.ajaxAttr] = element.getAttribute(this.ajaxAttr);
						errorElement = errorWd.render({ state: state });
						element.parentNode.replaceChild(errorElement, element);
						return errorElement;
					},
					flagError: function(args) {
						// fake flagError if there is a problem loading the helper
						var element = this.replaceElement(args.element);
						element.innerHTML = args.message;
					},
					/**
					 * Loads module that knows how to present error feedback to user.
					 *
					 * @param {Function} callback Will be called with module instance when loaded.
					 * @param {boolean} [forceFallback] If true the rudimentary feedback utilities will be returned to simulate a network error in
					 *    loading the advanced module. This is for testing purposes because code that only runs in error conditions is hard to test.
					 */
					fetch: function(callback, forceFallback) {
						/*
						 * Module loader callback for both success and failure conditions.
						 * @param {module:wc/ui/feedback} [feedback] If not present (network down?) will fall back to errorUtils.
						 */
						var cb = function(feedback) {
							if (feedback && feedback.flagError) {
								errorUtils._feedback = feedback;  // store the feedback module for later use
							}
							if (callback) {  // could be a prefetch
								callback(errorUtils._feedback || errorUtils);  // continue with whichever feedback utility we have
							}
						};
						if (forceFallback) {
							callback(errorUtils);
						} else if (this._feedback) {
							cb(this._feedback);  // the feedback module has already been loaded, reuse it.
						} else {
							require([feedbackModule], cb, cb);  // try to load the feedback module but go ahead either way
						}
					}
				},
				FORM,
				ERROR_WD,
				OBSERVER_GROUP = "after";

			/**
			 * @var {Object} module:wc/ui/ajax/processResponse.actions The ajax action types : FILL, REPLACE or APPEND.
			 * @property {String} FILL Indicates the action will replace the content of the target.
			 * @property {String} REPLACE Indicates the action will replace the target.
			 * @property {String} APPEND Indicates the action will append its payload to the content of the target.
			 */
			this.actions = ACTIONS;

			/**
			 * Subscribers can chose to be notified before the DOM is updated with new content
			 * loaded via AJAX.
			 *
			 * @function module:wc/ui/ajax/processResponse.subscribe
			 * @param {Function} subscriber A callback function, will be passed the args: (element, content, action).
			 * @param {Boolean} [after] Indicates that the subscriber is to the post-insertion publisher.
			 * @returns {Function} The result of observer.subscribe
			 */
			this.subscribe = function(subscriber, after) {
				var group = null;
				observer = observer || new Observer();
				if (after) {
					group = { group: OBSERVER_GROUP };
				}
				return observer.subscribe(subscriber, group);
			};

			/**
			 * Removes a subscriber. Not usually used outside of testing (where it is indispensable).
			 * @function module:wc/ui/ajax/processResponse.unsubscribe
			 * @param {Function} subscriber the subscriber to remove
			 * @param {Boolean} [after] remove from the post-insertion subscribers.
			 */
			this.unsubscribe = function(subscriber, after) {
				var group;
				if (observer) {
					group = after ? OBSERVER_GROUP : null;
					observer.unsubscribe(subscriber, group);
				}
			};

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
			this.processResponseXml = function(response, trigger) {
				var promise;
				if (response) {
					promise = new Promise(function(resolve, reject) {
						var doc;
						if (typeof response === "string") {
							doc = toDocFragment(response);
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
			};

			function processResponseHtml(documentFragment, trigger) {
				var content, targets,
					next, targetId, element, doc, action, i,
					onError = function() {
						require(["wc/ajax/handleError"], function(handleError) {
							// The AJAX response was malformed BUT reported as being successful.
							var message = handleError.getErrorMessage({ status: 200 });
							// This may not display perfectly but it's better than literally nothing
							trigger.onerror(message, trigger);
						});
					};
				if (documentFragment) {
					if (documentFragment.querySelector) {
						doc = documentFragment.querySelector(".wc-ajaxresponse");
					} else {
						doc = documentFragment.firstElementChild || documentFragment.firstChild;
					}
					if (doc) {
						targets = doc.querySelectorAll(".wc-ajaxtarget");
						for (i = 0; i < targets.length; ++i) {
							next = targets[i];
							next.parentNode.removeChild(next);  // remove the target wrapper
							if (next.nodeType === Node.ELEMENT_NODE) {
								targetId = next.getAttribute("data-id");
								element = document.getElementById(targetId);
								if (element) {
									/* Since the ui:ajaxresponse is essentially thrown away we need to move any of its interesting attributes to the target element.
									 * In reality this is to catch the onLoadFocusId attribute but we'll try to pretend it's generic. */
									mergeAttributes(doc, next);
									action = next.getAttribute("data-action");
									content = document.createDocumentFragment();
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

			/**
			 * If there was an error attempt to inform the user of this.
			 * @function module:wc/ui/ajax/processResponse.processError
			 * @public
			 * @param {String} response An error message.
			 * @param {module:wc/ajax/Trigger} trigger The trigger which triggered the ajax request.
			 * @param {boolean} [forceFallback] If true the rudimentary feedback utilities will be returned to simulate a network error in
			 *    loading the advanced module. This is for testing purposes because code that only runs in error conditions is hard to test.
			 */
			this.processError = function(response, trigger, forceFallback) {
				return new Promise(function(win) {
					var i, element, ids = trigger.loads,
						callback = function(feedback) {
							var errorElement;
							for (i = 0; i < ids.length; i++) {
								element = document.getElementById(ids[i]);
								if (element) {
									errorElement = errorUtils.replaceElement(element);
									feedback.flagError({
										element: errorElement,
										message: response
									});
								}
							}
							win();
						};
					if (ids && response) {
						errorUtils.fetch(callback, forceFallback);
					} else {
						win();
					}
				});
			};

			/*
			 * Copy attributes from one element to another, ignoring xmlns:* attributes.
			 * @param {Element} source The element which has the attributes we want to copy from.
			 * @param {Element} dest The element we will copy the attributes to.
			 */
			function mergeAttributes(source, dest) {
				var i, next;
				for (i = 0; i < source.attributes.length; i++) {
					next = source.attributes[i];
					if (next.name.indexOf("xmlns:") < 0) {
						// no point copying over boring old xmlns attributes
						dest.setAttribute(next.name, next.value);
					}
				}
			}

			function insertPayloadIntoDom(element, content, action, trigger, doNotPublish) {
				var actionMethod, _element, triggerId = (trigger && trigger.id) ? trigger.id : null;
				switch (action) {
					case ACTIONS.REPLACE:
						if (element.tagName !== tag.BODY) {
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
					_element = actionMethod(element, content);

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
				var scripts, duplicates, _content = content, child;
				element.innerHTML = "";  // have to blat the contents first, otherwise browser doesn't reliably pick up changes
				duplicates = checkDuplicateIds(_content);
				removeDuplicateElements(duplicates);
				scripts = extractScriptsFromContent(_content);
				while ((child = _content.firstChild)) {
					element.appendChild(child);
				}
				insertScripts(scripts, element);
				return element;
			}

			function appendElementContent(element, content) {
				var scripts, duplicates, _content = content, child;
				// have to blat the contents first, otherwise browser doesn't reliably pick up changes
				duplicates = checkDuplicateIds(_content);
				removeDuplicateElements(duplicates);
				scripts = extractScriptsFromContent(_content);
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
				var parent = element.parentNode,
					_content = content,
					scripts, duplicates = [element],
					result = [], child;

				element.removeAttribute("id");
				duplicates = duplicates.concat(checkDuplicateIds(_content));
				scripts = extractScriptsFromContent(_content);
				// insert the content documentfragment's child/ren (currently only one child in replace ) in position to replace element
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
				var child,
					_element,
					id,
					result,
					wrapper = document.createElement("div");

				while ((child = content.firstChild)) {
					wrapper.appendChild(child);
					if ((id = child.id)) {
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

			/*
			 * Dynamic injection of scripts is NOT as easy as you may imagine so don't change this
			 * unless you are prepared to test extensively.
			 * @param scripts Array of script elements.
			 * @param {Element} relativeTo use this as the basis for finding to form into which we want to insert the scripts.
			 */
			function insertScripts(scripts, relativeTo) {
				var next, newScript, srcAttr = "src", textProp, src, defer = "defer", clazzName, ownerElement = document.body, scrId;

				FORM = FORM || new Widget("form");

				if (relativeTo && relativeTo.nodeType === Node.ELEMENT_NODE) {
					ownerElement = FORM.findAncestor(relativeTo) || document.body;
				}

				while (scripts.length) {
					next = scripts.shift();
					newScript = document.createElement(tag.SCRIPT);
					newScript.setAttribute(defer, defer);  // newScript.defer='defer' results in defer='' in IE8
					if ((clazzName = next.className)) {
						newScript.className = clazzName;
					}
					if ((src = next.getAttribute(srcAttr))) {
						newScript.setAttribute(srcAttr, src);
					} else {
						textProp = next.textContent ? "textContent" : "text";
						newScript[textProp] = next[textProp];
					}
					if ((scrId = next.id)) {
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
				var scripts, i, result = [];
				try {
					if (typeof content.querySelectorAll !== "undefined") {
						scripts = content.querySelectorAll(tag.SCRIPT);
					} else {
						scripts = content.getElementsByTagName(tag.SCRIPT);
					}

					for (i = 0; i < scripts.length; i++) {
						result[result.length] = scripts[i].parentNode.removeChild(scripts[i]);
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
				var result = [];

				if (content) {
					if (typeof content.querySelectorAll !== "undefined") {
						checkDuplicateIdsElement(content);
					} else if (content.constructor === String) {
						checkDuplicateIdsHtml(content);
					}
				}

				function checkDuplicateIdsHtml(html) {
					var idRe = /id=[\'|\"]?(([a-zA-Z0-9_*\-*]+))[\'|\"]/g,
						matches,
						nextElement, nextId;
					while ((matches = idRe.exec(html))) {
						nextId = matches[1];
						nextElement = document.getElementById(nextId);
						if (nextElement) {
							console.info("Removing element to prevent duplicate ID", nextId);
							removeDuplicate(nextElement);
						}
					}
				}

				function checkDuplicateIdsElement(documentFragment) {
					var candidates = documentFragment.querySelectorAll("*[id]"),
						i, len = candidates.length, nextId, nextElement;
					for (i = 0; i < len; i++) {
						if ((nextId = candidates[i].id) && (nextElement = document.getElementById(nextId))) {
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
		}

		/**
		 * This part of ajaxRegion is responsible for processing the AJAX response and updating the page accordingly.
		 * @module
		 * @requires module:wc/Observer
		 * @requires module:wc/dom/tag
		 * @requires module:wc/dom/toDocFragment
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/timers
		 *
		 * @todo re-order code, document private memebers.
		 */
		return new AjaxProcessor();
	});
