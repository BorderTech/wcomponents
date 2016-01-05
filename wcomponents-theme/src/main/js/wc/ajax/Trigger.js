/**
 * <p>This is a trigger specifically designed for our AJAX Region construct ({@link module:wc/ui/ajaxRegion}). While
 * the separation is not necessarily pure in essence this class handles the "request" side of AJAX Regions while
 * ajaxRegion itself handles the "response" side.</p>
 *
 * <p> It is not intended to be a general purpose AJAX trigger however it does contain some useful and potentially
 * reusable functionality.</p>
 *
 * @module
 *
 * @requires external:lib/sprintf
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/event
 * @requires module:wc/dom/serialize
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/getAncestorOrSelf
 * @requires module:wc/ajax/ajax
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/has
 * @requires module:wc/dom/initialise
 * @requires module:wc/timers
 * @requires module:wc/ajax/setLoading
 * @requires module:wc/Observer
 */
define(["lib/sprintf",
	"wc/dom/tag",
	"wc/dom/event",
	"wc/dom/serialize",
	"wc/dom/Widget",
	"wc/dom/getAncestorOrSelf",
	"wc/ajax/ajax",
	"wc/dom/formUpdateManager",
	"wc/has",
	"wc/dom/initialise",
	"wc/timers",
	"wc/ajax/setLoading",
	"wc/Observer"],
	/** @param sprintf lib/sprintf @param tag wc/dom/tag @param event wc/dom/event @param serialize wc/dom/serialize @param Widget wc/dom/Widget @param getAncestorOrSelf wc/dom/getAncestorOrSelf @param ajax wc/ajax/ajax@param formUpdateManager wc/dom/formUpdateManager @param has wc/has @param initialise wc/dom/initialise @param timers wc/timers @param setLoading wc/ajax/setLoading @param Observer wc/Observer @ignore*/
	function(sprintf, tag, event, serialize, Widget, getAncestorOrSelf, ajax, formUpdateManager, has, initialise, timers, setLoading, Observer) {
		"use strict";

		var
			/**
			 * @constant {String} EMPTY_VALUE A Default value for INPUT elements in the submit button state or image
			 * button state which do not have a value set. The default value is that commonly set for such controls.
			 * This is a bit moot as we do not use inputs in these states so cannot fire AJAX requests based on them. It
			 * is here for completeness.
			 * @private
			 * @default "Submit Query"
			 */
			EMPTY_VALUE = "Submit Query",
			/**
			 * @constant {int} REQUEST_DELAY The delay, in milliseconds, before sending a request to prevent rapid fire
			 * requests (double clickers).
			 * @see {@link module:wc/ajax/Trigger~requestTimer}
			 * @private
			 * @default 500
			 */
			REQUEST_DELAY = 500,
			/**
			 * @var {Object} TAG An object used to map HTML tag names. Instantiated only when first needed.
			 * @see {@link module:wc/dom/tag}
			 * @private
			 */
			TAG,
			/**
			 * @constant {String} UNDEFINED  Undefined. Here to improve compression.
			 * @private
			 * @default "undefined"
			 * @ignore
			 */
			UNDEFINED = "undefined",
			/**
			 * @var {module:wc/dom/Widget} busyWd A {@link module:wc/dom/Widget} description of a busy element
			 * (pending a UI update). Instantiated only when and if first needed.
			 * @private
			 */
			busyWd,
			/**
			 * @var {module:wc/ajax/Trigger~Request[]} pendingList An array of AJAX requests that have been sent but
			 * not received.
			 * @private
			 */
			pendingList = [],
			/**
			 * @var {Boolean} queueBlocked Indicates that the request queue is currently blocked by a pending UI update.
			 * @private
			 */
			queueBlocked = false,
			/**
			 * @var {module:wc/ajax/Trigger~Request[]} requestBuffer An array of queued AJAX requests.
			 * @private
			 */
			requestBuffer = [],
			/**
			 * @var {int} requestTimer  A {@link module:wc/timers} timeout used for preventing rapid fire requests.
			 * @see {@link module:wc/ajax/Trigger~REQUEST_DELAY}
			 * @private
			 */
			requestTimer = null,
			/**
			 * @var {Boolean} unloading Indicates that the document is currently unloading and therefore we should not
			 * make any AJAX requests. Only needed in IE below 10.
			 * @private
			 * @deprecated Will be removed once obsolete versions of IE are no longer even notionally supported.
			 * @ignore
			 */
			unloading = false,
			/**
			 * @var {module:wc/Observer} observer An Observer used to publish profile information if required.
			 * @private
			 */
			observer;

		// add an early initialisation
		initialise.addBodyListener({initialise: function () {
			busyWd = new Widget("", "", {"aria-busy": "true"});
			if (has("ie") && has("ie") < 10) {
				event.add(window, event.TYPE.beforeunload,
					/**
					 * <p>Beforeunload event handler to set an unloading flag to prevent more triggers from firing. Only applied in
					 * IE with version below 10. IE (tested on IE8) has some serious issues when processing stale AJAX which  means
					 * we need to check whether the page is unloading before we action any AJAX requests. While I have put guard
					 * code on both the requests and the callback for the response, we could actually get by with just one of these.
					 * Since it is such a small amount of code it is best to leave it in both places.</p>
					 *
					 * <p>The bug is this:</p>
					 * <ol>
					 *	 <li>Send an AJAX request.</li>
					 *	 <li>Quite quickly submit the form (click a submit button).</li>
					 *	 <li>IE will send the form submit request first even though the AJAX request should have come first.</li>
					 * </ol>
					 *
					 * <p>That's already enough of a bug right there but it gets worse with another bug:</p>
					 *
					 * <ol start=4><li>AJAX response comes back to IE AFTER the beforeunload event fires, despite the fact that the
					 * page is being unloaded and that the response for the next page has already been received IE still honors the
					 * AJAX response and processes it fully.</li>
					 * </ol>
					 *
					 * <p>These bugs cause all sorts of massive problems, particularly with keeping track of "step counters" on the
					 * server.</p>
					 *
					 * @function
					 * @private
					 * @param {Event} $event The beforeunload event.
					 * @ignore
					 */
					function ($event) {
						if (!$event.defaultPrevented) {
							unloading = true;
						}
					}, 1);  // fire late in case is cancelled
			}
		}});

		/**
		 * Joins strings with the "&" character.
		 *
		 * @function addToQueryString
		 * @private
		 * @param {String} [queryString] The existing query string ("" is OK).
		 * @param {String} newArgs The String to add to the query string.
		 * @returns {String} An amended queryString
		 */
		function addToQueryString(queryString, newArgs) {
			var result = queryString;
			if (newArgs) {
				if (queryString) {
					result += "&";
					result += newArgs;
				}
				else {
					result = newArgs;
				}
			}
			return result;
		}

		/**
		 * Given an ajax trigger gets the trigger element or other element which is in the form so we can get the URL
		 * and serialize the form.
		 *
		 * @function getElement
		 * @private
		 * @param {module:wc/ajax/Trigger} trigger An AJAX Trigger.
		 * @returns {Element} The element which is expected to fire the trigger.
		 */
		function getElement(trigger) {
			var result, element;
			if (trigger.id) {
				if ((element = document.getElementById(trigger.id))) {
					result = element;
				}
				else if (trigger.alias && (element = document.getElementById(trigger.alias))) {
					result = element;
				}
				else if (trigger.loads.length && (element = document.getElementById(trigger.loads[0]))) {
					result = element;
				}
			}
			return result;
		}

		/**
		 * Find the form ancestor of any element.
		 *
		 * @function getForm
		 * @private
		 * @param {Element} element the start element.
		 * @returns {Element} the ancestor form element if any or undefined.
		 */
		function getForm(element) {
			var form;
			if (typeof element.form !== UNDEFINED) {
				form = element.form;
			}
			else if (element.tagName !== tag.FORM) {  // if you want an infinte loop remove this check :P
				form = getAncestorOrSelf(element, tag.FORM);
			}
			return form;
		}

		/**
		 * @constructor
		 * @alias module:wc/ajax/Trigger
		 * @param {module:wc/ajax/Trigger~TriggerConfig} obj The configuration object.
		 * @param {Function} onsuccess The function to which the AJAX response payload will be passed.
		 * @param {Function} onerror The function which will be called if there is an error communicating with the server.
		 */
		function Trigger(obj, onsuccess, onerror) {
			if (obj.id) {
				this.id = obj.id;
				this.alias = (typeof obj.alias === UNDEFINED) ? null : obj.alias;
				this.successful = (typeof obj.successful === UNDEFINED) ? null : obj.successful;
				this.formRegion = obj.formRegion;
				this._callback = onsuccess;
				this._onerror = onerror;
				this.urlFromForm = (typeof obj.urlFromForm === UNDEFINED) ? null : obj.urlFromForm;
				this.url = (typeof obj.url === UNDEFINED) ? null : obj.url;
				this.getData = obj.getData;
				this.method = (typeof obj.method === UNDEFINED) ? this.METHODS.POST : obj.method;
				this.serialiseForm = (typeof obj.serialiseForm === UNDEFINED) ? true : obj.serialiseForm;
				this._submitTriggerElement = (typeof obj._submitTriggerElement === UNDEFINED) ? false : obj._submitTriggerElement;  // if true the element that fired the trigger will be serialised in the form data
				if (obj.loads && obj.loads.length) {  // either string or array
					this.loads = Array.isArray(obj.loads) ? obj.loads : [obj.loads];
				}
				else {
					// strictly speaking we don't care but for usability and error detection purposes we lock this down a little
					throw new ReferenceError("An AJAX Trigger must target something");
				}

				// store profile information
				this.profile = {
					fired: 0,
					sent: 0,
					received: 0
				};

				if (!obj.oneShot || obj.oneShot === "false") {  // if oneshot is not defined or false then it's not oneshot
					this.oneShot = -1;
				}
				else if (/^\d+$/.test(obj.oneShot)) {  // if oneshot is an integer set it to that number
					this.oneShot = obj.oneShot;
				}
				else {  // if oneshot is anything else it is true (it probably is the string "true")
					this.oneShot = 1;
				}
			}
			else {
				throw new TypeError("Trigger id can not be empty");
			}
		}

		/**
		 * Subscribe to profile information.
		 * This is for use by testing / monitoring tools and does not form a core part of the functionality of this module.
		 * The subscriber will be notified when
		 * @param {Function} subscriber
		 */
		Trigger.subscribe = function (subscriber) {
			if (!observer) {
				observer = new Observer();
			}
			return observer.subscribe(subscriber);
		};

		/**
		 * Find the url this trigger should use when sending ajax requests. This will remove the HASH for browsers with
		 * special needs.
		 *
		 * @function
		 * @public
		 * @static
		 * @param {(module:wc/ajax/Trigger|Element)} trigger The trigger instance or element to use as a reference point
		 *	 for finding the ajax URL.
		 * @returns {String} The url.
		 */
		Trigger.getUrl = function (trigger) {
			var url,
				ampCheckRE	=	/\&amp;/gi,
				fragmentRe	=	/#.+$/g;
			/**
			 * <p>If the trigger is an instance of Trigger we will try to return, in order of preference:</p>
			 * <ol><li>trigger.url;
			 * <li>URL directly associated with the DOM element referred to by trigger.id;
			 * <li>URL directly associated with the form element containing the DOM element referred to by trigger.id;
			 * <li>URL directly associated with the DOM element referred to by trigger.alias;
			 * <li>URL directly associated with the form element containing the DOM element referred to by trigger.alias.
			 * </ol>
			 *
			 * @function
			 * @private
			 * @param {(module:wc/ajax/Trigger|Element)} trigger An instance of Trigger or a DOM Element.
			 * @returns {String} The AJAX URL associated with the trigger.
			 */
			function getUrlHtml5(trigger) {
				var element,
					result,
					form,
					URL_DATA_ATTRIBUTE = "data-wc-ajaxurl";
				if (trigger) {
					if (trigger.constructor === Trigger) {
						result = trigger.url;
						if (!result) {
							console.log("Could not find URL on trigger Instance");
							if ((element = getElement(trigger))) {
								result = getUrlHtml5(element);
							}
						}
					}
					else {  // trigger must be a dom element
						result = trigger.getAttribute(URL_DATA_ATTRIBUTE);
						if (!result) {
							console.log("Could not find URL for element ", trigger.id);
							if ((form = getForm(trigger))) {
								if (!(result = getUrlHtml5(form))) {
									console.log("Could not find URL on form");
								}
							}
						}
					}
				}
				else {
					throw new TypeError("trigger can not be null");
				}
				return result;
			}

			url = getUrlHtml5(trigger);
			if (url) {
				/* Note that XMLHttpRequest can not send the anchor part of a URL (ie the hash and anything following)
				 * See: http://www.w3.org/TR/XMLHttpRequest/#the-open-method
				 * The correct behaviour is for the browser to drop the anchor part of the URL. Firefox does this
				 * correctly IE8 gets it wrong and instead encodes the hash (which will usually confuse the server).*/
				url = url.replace(ampCheckRE, "&");
				url = url.replace(fragmentRe, ""); // a little help for the "otherwise enabled"
			}
			return url;
		};

		/**
		 * Find any triggers in the array which directly update a given id.
		 *
		 * @function
		 * @public
		 * @param {string} id An element ID
		 * @param {module:wc/ajax/Trigger~Request[]} requests An array of Request instances.
		 * @param {Boolean} [stopAtFirstMatch] If true will only return the first trigger found which updates the id.
		 * @returns {module:wc/ajax/Trigger~Request[]} An array of requests which update the id. If none found will
		 *	 return an empty array.
		 */
		Trigger.prototype.getTriggersFor = function (id, requests, stopAtFirstMatch) {
			var result = [],
				len = requests.length,
				trigger,
				i;
			for (i = 0; i < len; i++) {
				trigger = requests[i].trigger;
				if (trigger.loads.indexOf(id) >= 0) {
					result[result.length] = requests[i];
					if (stopAtFirstMatch) {
						break;
					}
				}
			}
			return result;
		};

		/**
		 * Get the current request buffer.
		 *
		 * @function
		 * @public
		 * @returns {Array}
		 */
		Trigger.prototype.getRequestBuffer = function() {
			return requestBuffer;
		};

		/**
		 * (re)schedule queue processing occur.
		 *
		 * @function
		 * @public
		 */
		Trigger.prototype.scheduleQueueProcessing = function() {
			/* Determine if it is "safe" to send the AJAX request for this trigger.
			 * What do we mean by safe? Well we prevent a trigger from sending its request if
			 * another request is pending which happens to affect the same part of the UI.
			 * If we do not prevent this then two requests may be pending which both update the same
			 * part of the UI - it is possible that these requests are processed out of order - while
			 * unlikely the impact could be severe.It would also be virtually impossible to replicate
			 * and debug.
			 * @param {module:wc/ajax/Trigger~Request} request The request we wish to send
			 * @returns {Boolean} true If the request may be sent.*/
			function canSendRequest(request) {
				var i,
					next,
					busy,
					conflict,
					trigger = request.trigger,
					ids = trigger.loads,
					len = ids.length;

				for (i = 0; i < len; i++) {
					conflict = trigger.getTriggersFor(ids[i], pendingList, true);
					if (conflict.length) {
						return false;
					}
					else {
						next = document.getElementById(ids[i]);
						if (next) {
							busy = busyWd.findAncestor(next) || busyWd.findDescendant(next);
							if (busy && busy !== next) {  // the element itself will ALWAYS be busy
								// this element is contained in or contains a "busy" region
								return false;
							}
						}
					}
				}
				return true;
			}

			if (requestTimer !== null) {
				timers.clearTimeout(requestTimer);
			}
			requestTimer = timers.setTimeout(function() {
				/* When invoked will attempt to remove the oldest item from the queue and send its AJAX request.
				 * If the oldest item can not be removed from the queue then no other items will be removed from
				 * the queue even though they themselves may not be blocked.
				 * In other words, if the front of the queue is blocked then nothing can come off the queue.*/
				console.log("Processing AJAX trigger queue");
				var request;
				while (requestBuffer.length) {
					if (canSendRequest(requestBuffer[0])) {
						queueBlocked = false;
						request = requestBuffer.shift();
						request.send();
					}
					else {
						queueBlocked = true;  // flag that we are blocked. next response will check this flag and reinvoke.
						console.log("AJAX trigger queue blocked while pending UI update");
						break;
					}
				}
			}, REQUEST_DELAY);  // schedule queue processing
		};

		/**
		 * Fires the trigger if it is in a "fireable" state.
		 *
		 * Well, actually, it queues the firing of the trigger. The same trigger may occur in the queue multiple times
		 * but never consecutively. For example the queue may look like this: [a,b,c,a,b,c] but NEVER like this
		 * [a,a,b,b,c,c]. In the case of consecutive requests only the last one of those requests is honoured. For
		 * example [a,a,b,b,c,c,a,a] would end up being [a,b,c,a].
		 *
		 * Note, if the trigger is fired by the click of a submit
		 * button we will need to include that button in the request as the server will be expecting it.  If a trigger
		 * is associated by ID with a submit button but the trigger was fired programmatically (i.e. not by clicking the
		 * button) then we will not include the button in the request (tracked using this._submitTriggerElement).
		 *
		 * @function
		 * @public
		 */
		Trigger.prototype.fire = function () {
			var promise,
				trigger = this,
				endOfQueue,
				request;

			if (trigger.oneShot) {  // will be a negative number if it is not oneshot, therefore will equate to true
				promise = new Promise(function(resolve, reject) {
					trigger.callback = function() {
						var scope = this, cbresult;
						if (trigger._callback) {
							cbresult = trigger._callback.apply(scope, arguments);
						}
						// The purpose of the Promise.resolve here is to WAIT for the callback to complete, ESPECIALLY if the callback returns a promise itself
						return Promise.resolve(cbresult).then(function() {
							resolve.apply(scope, arguments);
						});
					};
					trigger.onerror = function() {
						if (trigger._onerror) {
							trigger._onerror.apply(this, arguments);
						}
						reject.apply(this, arguments);
					};
					if (trigger.oneShot > 0) {
						trigger.oneShot--;
					}
					// queueRequest();
					endOfQueue = (requestBuffer.length - 1);
					trigger.profile.fired = Date.now();
					request = new Request(trigger);
					if (!requestBuffer[endOfQueue] || requestBuffer[endOfQueue].trigger.id !== trigger.id) {  // yes, use id for equality
						requestBuffer.push(request);
						setLoading(request);  // do this AFTER the form has been serialized (because it will disable stuff)
					}
					else {
						requestBuffer[endOfQueue] = request;
						console.log("Cancelling consecutive request for ", trigger.id);
					}
					trigger.scheduleQueueProcessing();
				});
			}
			else {
				promise = Promise.reject("Trigger has no more shots left: " + trigger.id);
			}
			return promise;
		};

		/**
		 * Returns the data that should be sent in the AJAX request. Includes the following:
		 *  - Serialised form data.
		 *  - Information about the DOM element related to the trigger (if a DOM element exists)
		 *  - Information about the instance of the Trigger class that fired the AJAX request.
		 *
		 *  QUESTION: When should you serialize the form? As soon as the trigger is fired or when the request
		 *  is de-queued and sent?
		 *  ANSWER: As soon as the trigger is fired!
		 *  WHY: Because the request should represent the state of the form as the USER perceived it when
		 *  they fired the trigger. Serializing the form at a later point may cause integrity or usability
		 *  issues.
		 *  This means that the server may receive requests that indicate the form is in a state it does
		 *  not expect, however the server must deal with this reality.
		 *
		 * @function
		 * @public
		 * @returns {String} The serialized parameters or "".
		 */
		Trigger.prototype.getParams = function () {
			var result = "",
				triggerId,
				element = getElement(this);
			try {
				if (this.serialiseForm && element) {
					result = getFormParams(element, this);
				}

				if (this._submitTriggerElement && element) {
					result = addToQueryString(result, getSubmitButtonParams(element));
				}

				if (this.getData) {
					result = addToQueryString(result, this.getData);
				}

				if (this._triggerParams) {
					result = addToQueryString(result, this._triggerParams);
				}

				if (this.alias) {
					triggerId = this.alias;
					console.log("Reporting alias to server");
				}
				else {
					triggerId = this.id;
				}
				triggerId = encodeURIComponent(triggerId);
				result = addToQueryString(result, sprintf.sprintf("%s=%s", "${wc.ui.ajax.parameter.triggerId}", triggerId));
			}
			finally {
				this._submitTriggerElement = false;  // reset, the idea is the next click event will set to true
			}

			return result;
		};

		/**
		 *
		 * If the trigger element is a submit control we must add it to the params because the server needs
		 *    to know that the form was "submitted" via this submit element.
		 * Remember, when serializing the form all buttons of all types will NOT be serialized because a button
		 *    is only successful when it is clicked, that is what we are honoring here
		 * @private
		 * @function
		 * @param {Element} element The trigger element.
		 * @returns {String} The serialized parameters or "".
		 */
		function getSubmitButtonParams(element) {
			var params = "", triggerName = element.name;
			if (triggerName) {
				triggerName = encodeURIComponent(triggerName);
				if (element.tagName === tag.BUTTON && element.type === "submit") {
					params = triggerName + "=";
					params += element.value;
				}
				else if (element.tagName === tag.INPUT && (element.type === "submit" || element.type === "image")) {
					params = triggerName + "=";
					if (element.hasAttribute && !element.hasAttribute("value")) {
						params += EMPTY_VALUE;
					}
					else {
						params += element.value;
					}
				}
			}
			return params;
		}

		/**
		 * Serialise the form (or region of the form if set).
		 * If trigger is linked to a DOM element the formcwill be the one the element "belongs to";
		 *    otherwise too bad, so sad, you don't get a serializedcform in the request payload.
		 * @function
		 * @private
		 * @param {Element} element The trigger element.
		 * @param {Trigger} instance The trigger instance being fired.
		 * @returns {String} The serialized parameters or "".
		 */
		function getFormParams(element, instance) {
			var result = "", form, region, stateContainer;
			if ((form = getForm(element))) {
				if (typeof instance.formRegion !== UNDEFINED) {
					region = document.getElementById(instance.formRegion);
				}
				if (region) {
					formUpdateManager.update(form, region);
					stateContainer = formUpdateManager.getStateContainer(form);
					TAG = TAG || { INPUT: tag.INPUT, SELECT: tag.SELECT, TEXTAREA: tag.TEXTAREA };
					result = addToQueryString(result, serialize.serialize(region.getElementsByTagName(TAG.INPUT)));
					result = addToQueryString(result, serialize.serialize(region.getElementsByTagName(TAG.SELECT)));
					result = addToQueryString(result, serialize.serialize(region.getElementsByTagName(TAG.TEXTAREA)));
					result = addToQueryString(result, serialize.serialize(stateContainer.getElementsByTagName(TAG.INPUT)));
				}
				else {
					formUpdateManager.update(form);
					result = serialize.serialize(form);
				}
			}
			else {
				console.warn("Could not find form");
			}
			return result;
		}


		/**
		 * Map of form methods.
		 * @constant {Object}
		 * @property {String} GET "get"
		 * @property {String} POST "post"
		 */
		Trigger.prototype.METHODS = {
			GET: "get",
			POST: "post" };

		/**
		 * Represents an AJAX request that has been initiated by an instance of Trigger.
		 * @constructor
		 * @private
		 * @alias module:wc/ajax/Trigger~Request
		 * @param {module:wc/ajax/Trigger} trigger The trigger to associate with this request.
		 */
		function Request(trigger) {
			/*
			 * Hold on to this reference to self as the callback function is applied... some things will break
			 * if you rely on 'this'.
			 */
			var $self = this;
			/**
			 * Cache responses to this request?
			 * @var {Boolean}
			 */
			this.cache = false;

			/**
			 * The form data posted as part of the ajax request.
			 * @var {?String}
			 */
			this.postData = trigger.getParams();

			/**
			 * The trigger which fires the request.
			 * @var {module:wc/ajax/Trigger}
			 */
			this.trigger = trigger;

			/**
			 * The response type of an AJAX request.
			 * @var {String}
			 * @default "responseXML"
			 */
			this.responseType = ajax.responseType.XML;
			/**
			 * The callback to call when the AJAX request has returned.
			 * @function
			 * @param {Object} response The ajax response.
			 */
			this.callback = function(response) {
				// response would be null if the XML has already been transformed to HTML on the server
				// or in the case of IE it will be an "empty" XML DOM.
				var payload = (response && response.documentElement) ? response : this.responseText;
				handleResponse($self, payload, trigger, false);
			};

			this.onError = function(response) {
				handleResponse($self, response, trigger, true);
			};
		}

		function handleResponse($self, response, trigger, isError) {
			var idx, cbresult, done = function() {
					setLoading($self, true);
				};
			console.log("Got response for trigger", trigger.id);
			if (!unloading) {
				try {
					/*
					 * The same trigger will never be pending more than once because "canSendRequest"
					 * will always return false for a trigger that is already pending.
					 *
					 * Therefore it is safe to delete it from the pending queue - no need for counters.
					 */
					idx = pendingList.indexOf($self);
					if (idx >= 0) {
						pendingList.splice(idx, 1);
					}
					else {
						console.warn("Got response for trigger that was not in pending queue", trigger.id);
					}
					try {
						if (!isError) {
							cbresult = trigger.callback(response, trigger);
						}
						else if (trigger.onerror) {
							cbresult = trigger.onerror(response, trigger);
						}
						// Remove "aria-busy" AFTER the new content is loaded to avoid collapsing to zero pixels
						// The Promise.resolve call allows us to "wait" for callbacks that return a promise.
						Promise.resolve(cbresult).then(done);
					}
					catch (ex) {
						console.error(ex);
					}
					trigger.profile.received = Date.now();
					if (observer) {
						observer.notify({
							profile: trigger.profile,
							id: trigger.id,
							alias: trigger.alias,
							loads: trigger.loads,
							url: trigger.url  // this will probably be null
						});
					}
				}
				finally {
					if (queueBlocked) {
						trigger.scheduleQueueProcessing();  // if the queue was blocked check again now
					}
				}
			}
			else {
				console.warn("Forbid AJAX response while unloading");
			}
		}

		/**
		 * Send the AJAX request NOW.
		 * No second guessing, no buffering, no delays, call this when you are ready to go.
		 *
		 * @function
		 */
		Request.prototype.send = function () {
			var trigger = this.trigger;
			if (!unloading) {
				this.url = Trigger.getUrl(trigger);
				if (this.url) {
					try {
						if (trigger.method === trigger.METHODS.GET && this.postData) {
							if (this.url.indexOf("?") > 0) {
								this.url = addToQueryString(this.url, this.postData);
							}
							else {
								this.url += "?" + this.postData;
							}
							this.postData = "";
						}
						pendingList.push(this);  // we must do this before sending cos we can't guarantee what AJAX will do (could be forced into synchronous mode)
						trigger.profile.sent = Date.now();
						ajax.simpleRequest(this);
					}
					catch (ex) {
						pendingList.pop();  // error so assume the request is not pending - pop it off the queue
						console.error(ex);
					}
				}
				else {
					console.warn("Could not find URL for trigger", this.trigger.id);
				}
			}
			else {
				console.warn("Forbid AJAX request while unloading");
			}
		};

		return Trigger;

		/**
		 * @typedef {Object} module:wc/ajax/Trigger~TriggerConfig The format of the object passed into the contructor.
		 * @property {String} id A unique identifier for this trigger.
		 * @property {Boolean} [oneShot] Trigger can only fire once.
		 * @property {String} [alias] Report this ID to the server instead of the real ID.
		 * @property {Boolean} [successful] Trigger fires if control is (un)successful.
		 * @property {String} [formRegion] Id of the region of the form to submit.
		 * @property {String[]} loads REQUIRED ids of elements in the page to mark as "busy" when trigger is fired, eg ["id1", "id2"].
		 * @property {Boolean} [urlFromForm] If true then the URL for this trigger will ALWAYS be the ancestor form action.
		 * @property {String} [url] The ajax url to override the normal get from form.
		 * @property {Function} [getData] Function to get extra data for this particular trigger - used for dataList requests.
		 * @property {String} [method] "post" or "get" defaults to post if not set.
		 * @property {Boolean} [serialiseForm] Indicates that the form should be serialized as part of the request, defaults to true.
		 * @property {Boolean} [_submitTriggerElement] If true the element that fired the trigger will be serialised in the form data
		 */
	});
