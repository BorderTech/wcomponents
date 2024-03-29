/**
 * This is a trigger specifically designed for our AJAX Region construct ({@link module:wc/ui/ajaxRegion}). While
 * the separation is not necessarily pure in essence this class handles the "request" side of AJAX Regions while
 * ajaxRegion itself handles the "response" side.
 *
 * It is not intended to be a general purpose AJAX trigger however it does contain some useful and potentially
 * reusable functionality.
 *
 */

import serialize from "wc/dom/serialize.mjs";
import ajax from "wc/ajax/ajax.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import initialise from "wc/dom/initialise.mjs";
import timers from "wc/timers.mjs";
import setLoading from "wc/ajax/setLoading.mjs";
import Observer from "wc/Observer.mjs";

const
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
	 * @constant {number} REQUEST_DELAY The delay, in milliseconds, before sending a request to prevent rapid fire
	 * requests (double clickers).
	 * @see {@link module:wc/ajax/Trigger~requestTimer}
	 * @private
	 * @default 500
	 */
	REQUEST_DELAY = 500,
	/**
	 * @constant {String} UNDEFINED  Undefined. Here to improve compression.
	 * @private
	 * @default "undefined"
	 * @ignore
	 */
	UNDEFINED = "undefined";

let
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
	 * @var {number} requestTimer  A {@link module:wc/timers} timeout used for preventing rapid fire requests.
	 * @see {@link module:wc/ajax/Trigger~REQUEST_DELAY}
	 * @private
	 */
	requestTimer = null,
	/**
	 * @var {module:wc/Observer} observer An Observer used to publish profile information if required.
	 * @private
	 */
	observer;

initialise.addInitRoutine(function() {
	const afterCallback = function (trigger) {
		setLoading({
			trigger: trigger
		}, true);
	};
	Trigger.subscribe(afterCallback, 1);
});

/**
 * Joins strings with the "&" character.
 *
 * @function addToQueryString
 * @private
 * @param {String} [queryString] The existing query string ("" is OK).
 * @param {String} [newArgs] The String to add to the query string.
 * @returns {String} An amended queryString
 */
function addToQueryString(queryString, newArgs) {
	let result = queryString;
	if (newArgs) {
		if (queryString) {
			result += "&";
			result += newArgs;
		} else {
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
	if (trigger.id) {
		let element = document.getElementById(trigger.id);
		if (element) {
			return element;
		}
		element = trigger.alias ? document.getElementById(trigger.alias) : null;
		if (element) {
			return element;
		}
		element = trigger.loads.length ? document.getElementById(trigger.loads[0]) : null;
		return element;
	}
	return null;
}

/**
 * Find the form ancestor (or self) of any element.
 *
 * @function getForm
 * @private
 * @param {Element} element the start element.
 * @returns {HTMLFormElement} the ancestor form element if any or undefined.
 */
function getForm(element) {
	return element["form"] || element.closest("form");
}

/**
 * @constructor
 * @alias module:wc/ajax/Trigger
 * @param {module:wc/ajax/Trigger~TriggerConfig} obj The configuration object.
 * @param {Function} [onsuccess] The function to which the AJAX response payload will be passed.
 * @param {Function} [onerror] The function which will be called if there is an error communicating with the server.
 */
function Trigger(obj, onsuccess, onerror) {
	if (obj.id) {
		this.id = obj.id;
		this.alias = (typeof obj.alias === UNDEFINED) ? null : obj.alias;
		this.successful = (typeof obj.successful === UNDEFINED) ? null : obj.successful;
		this.formRegion = obj.formRegion;
		this.callback = function() {
			const scope = this;
			let cbresult;
			try {
				if (onsuccess) {
					cbresult = onsuccess.apply(scope, arguments);
				}
			} finally {
				// The purpose of the Promise.resolve here is to WAIT for the callback to complete, ESPECIALLY if the callback returns a promise itself
				Promise.resolve(cbresult).then(function(result) {
					notify(scope, "after", { success: result });
				});
			}
		};
		this.onerror = function(err) {
			const trigger = this;
			try {
				if (onerror) {
					onerror.apply(trigger, arguments);
				}
			} finally {
				notify(trigger, "after", { error: err });
			}
		};
		this.urlFromForm = (typeof obj.urlFromForm === UNDEFINED) ? null : obj.urlFromForm;
		this.url = (typeof obj.url === UNDEFINED) ? null : obj.url;
		this.getData = obj.getData;
		this.method = (typeof obj.method === UNDEFINED) ? this.METHODS.POST : obj.method;
		this.serialiseForm = (typeof obj.serialiseForm === UNDEFINED) ? true : obj.serialiseForm;
		this._submitTriggerElement = (typeof obj._submitTriggerElement === UNDEFINED) ? false : obj._submitTriggerElement;  // if true the element that fired the trigger will be serialised in the form data
		if (obj?.loads.length) {  // either string or array
			this.loads = Array.isArray(obj.loads) ? obj.loads : [obj.loads];
		} else {
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
		} else if (/^\d+$/.test(obj.oneShot)) {  // if oneshot is an integer set it to that number
			this.oneShot = obj.oneShot;
		} else {  // if oneshot is anything else it is true (it probably is the string "true")
			this.oneShot = 1;
		}
	} else {
		throw new TypeError("Trigger id can not be empty");
	}
}

/**
 * Subscribe to profile information.
 * The first argument to the subscriber will be the firing trigger, do not modify it or its properties unless you know what you are doing.
 * The second will be a boolean, true if there are pending triggers, false if there are none.
 * @param {Function} subscriber
 * @param {number} [phase] Subscriber will be called:
 *	if phase is a negative number when a trigger is fired
 *	if phase is falsy then after the response is received
 *	if positive number, then after the response callback has been called
 */
Trigger.subscribe = function(subscriber, phase) {
	let group = null;
	if (phase) {
		if (phase < 0) {
			group = { group: "before" };
		} else {
			group = { group: "after" };
		}
	}
	if (!observer) {
		observer = new Observer();
	}
	return observer.subscribe(subscriber, group);
};

/**
 * Unsubscribe from profile information.
 * This is for use by testing / monitoring tools and does not form a core part of the functionality of this module.
 * @param {Function} subscriber
 * @param {number} [phase] If a negative number is provided the subscriber will be removed from the "before" phase.
 */
Trigger.unsubscribe = function(subscriber, phase) {
	let group;
	if (observer) {
		if (phase) {
			if (phase < 0) {
				group = "before";
			} else {
				group = "after";
			}
		}
		observer.unsubscribe(subscriber, group);
	}
};

/**
 * Related to the subscribe method above.
 * @param {Trigger} trigger The trigger that is firing.
 * @param {string} [groupName] The group to notify.
 * @param {object} [cbresult] The result of the trigger callback, if relevant to this phase.
 */
function notify(trigger, groupName, cbresult) {
	let pending;
	trigger.profile.received = Date.now();
	if (observer) {
		pending = pendingList.length > 0;
		if (groupName) {
			observer.setFilter(groupName);
			if (groupName === "before") {
				// This special case is not ideal but necessary.
				pending = true;
			}
		}
		const proxyObj = Object.create(trigger);
		proxyObj.cbresult = cbresult;
		observer.notify(proxyObj, pending);
	}
}

/**
 * Find the url this trigger should use when sending ajax requests. This will remove the HASH for browsers with
 * special needs.
 *
 * @function
 * @public
 * @static
 * @param {module:wc/ajax/Trigger|Element} trigger The trigger instance or element to use as a reference point
 *	 for finding the ajax URL.
 * @returns {String} The url.
 */
Trigger.getUrl = function(trigger) {
	const ampCheckRE =	/&amp;/gi;
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
	 * @param {module:wc/ajax/Trigger|Element} trig An instance of Trigger or a DOM Element.
	 * @returns {String} The AJAX URL associated with the trigger.
	 */
	function getUrlHtml5(trig) {
		const URL_DATA_ATTRIBUTE = "data-wc-ajaxurl";
		let result;
		if (trig) {
			if (trig.constructor === Trigger) {
				result = trig.url;
				if (!result) {
					console.log("Could not find URL on trigger Instance");
					const element = getElement(trig);
					if (element) {
						result = getUrlHtml5(element);
					}
				}
			} else { // trigger must be a dom element
				result = trig.getAttribute(URL_DATA_ATTRIBUTE);
				if (!result) {
					console.log("Could not find URL for element ", trig.id);
					const form = getForm(trig);
					if (form) {
						result = getUrlHtml5(form);
						if (!result) {
							console.log("Could not find URL on form");
						}
					}
				}
			}
		} else {
			throw new TypeError("trigger can not be null");
		}
		return result;
	}

	let url = getUrlHtml5(trigger);
	if (url) {
		/* Note that XMLHttpRequest can not send the anchor part of a URL (ie the hash and anything following)
		 * See: http://www.w3.org/TR/XMLHttpRequest/#the-open-method
		 * The correct behaviour is for the browser to drop the anchor part of the URL. Firefox does this
		 * correctly IE8 gets it wrong and instead encodes the hash (which will usually confuse the server).*/
		url = url.replace(ampCheckRE, "&");
		// url = url.replace(/#.+$/g, "");  // a little help for the "otherwise enabled"
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
Trigger.prototype.getTriggersFor = function(id, requests, stopAtFirstMatch) {
	const result = [],
		len = requests.length;
	for (let i = 0; i < len; i++) {
		const trigger = requests[i].trigger;
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
		const trigger = request.trigger,
			ids = trigger.loads,
			len = ids.length;

		for (let i = 0; i < len; i++) {
			const conflict = trigger.getTriggersFor(ids[i], pendingList, true);
			if (conflict.length) {
				return false;
			}
			const next = document.getElementById(ids[i]);
			if (next) {
				const busySelector = "[aria-busy='true']";
				const busy = next.closest(busySelector) || next.querySelector(busySelector);
				if (busy && busy !== next) {  // the element itself will ALWAYS be busy
					// this element is contained in or contains a "busy" region
					return false;
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
		while (requestBuffer.length) {
			if (canSendRequest(requestBuffer[0])) {
				queueBlocked = false;
				let request = requestBuffer.shift();
				request.send();
			} else {
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
Trigger.prototype.fire = function() {
	const trigger = this;
	let promise;

	if (trigger.oneShot) {  // will be a negative number if it is not oneshot, therefore will equate to true
		notify(trigger, "before");
		if (trigger.oneShot > 0) {
			trigger.oneShot--;
		}
		// queueRequest();
		const endOfQueue = (requestBuffer.length - 1);
		trigger.profile.fired = Date.now();
		const request = new Request(trigger);
		if (!requestBuffer[endOfQueue] || requestBuffer[endOfQueue].trigger.id !== trigger.id) {  // yes, use id for equality
			requestBuffer.push(request);
			setLoading(request);  // do this AFTER the form has been serialized (because it will disable stuff)
		} else {
			requestBuffer[endOfQueue] = request;
			console.log("Cancelling consecutive request for ", trigger.id);
		}
		trigger.scheduleQueueProcessing();
		promise = getFirePromise(trigger);
	} else {
		promise = Promise.reject("Trigger has no more shots left: " + trigger.id);
	}
	return promise;
};

function getFirePromise(trigger) {
	return new Promise(function(resolve, reject) {
		const subscriber = function (triggerArg) {
			if (triggerArg && trigger.id === triggerArg.id) {
				Trigger.unsubscribe(subscriber, 1);
				const result = triggerArg.cbresult;
				if (result) {
					if (result.error) {
						reject(result.error);
					} else {
						resolve(result.success);
					}
				}
			}
		};
		Trigger.subscribe(subscriber, 1);
	});
}

/**
 * Returns the data that should be sent in the AJAX request. Includes the following:
 *  - Serialized form data.
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
Trigger.prototype.getParams = function() {
	const element = getElement(this);
	let result = "";
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

		if (this["_triggerParams"]) {
			result = addToQueryString(result, this["_triggerParams"]);
		}

		let triggerId;
		if (this.alias) {
			triggerId = this.alias;
			console.log("Reporting alias to server");
		} else {
			triggerId = this.id;
		}
		triggerId = encodeURIComponent(triggerId);
		result = addToQueryString(result, "wc_ajax=" + triggerId);
	} finally {
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
	let params = "", triggerName = element["name"];
	if (triggerName) {
		triggerName = encodeURIComponent(triggerName);
		if (element.matches("button[type='submit']")) {
			params = triggerName + "=";
			params += /** @type HTMLButtonElement */(element).value;
		} else if (element.matches("input[type='submit']") || element.matches("input[type='image']")) {
			params = triggerName + "=";
			if (element.hasAttribute && !element.hasAttribute("value")) {
				params += EMPTY_VALUE;
			} else {
				params += element["value"];
			}
		}
	}
	return params;
}

/**
 * Serialize the form (or region of the form if set).
 * If trigger is linked to a DOM element the form will be the one the element "belongs to";
 *    otherwise too bad, so sad, you don't get a serialized form in the request payload.
 * @function
 * @private
 * @param {Element} element The trigger element.
 * @param {Trigger} instance The trigger instance being fired.
 * @returns {String} The serialized parameters or "".
 */
function getFormParams(element, instance) {
	let result = "";
	const form = getForm(element);
	/**
	 * @param {Element} context
	 * @param {string} qs
	 * @return {string}
	 */
	const serializeElements = (context, qs) => {
		const elements = context.querySelectorAll(qs);
		return /** @type String */(serialize.serialize(elements));
	};

	if (form) {
		let region;
		if (typeof instance.formRegion !== UNDEFINED) {
			region = document.getElementById(instance.formRegion);
		}
		if (region) {
			formUpdateManager.update(form, region);
			const stateContainer = formUpdateManager.getStateContainer(form);
			result = addToQueryString(result, serializeElements(region, "input"));
			result = addToQueryString(result, serializeElements(region, "select"));
			result = addToQueryString(result, serializeElements(region, "textarea"));
			result = addToQueryString(result, serializeElements(stateContainer, "input"));
		} else {
			formUpdateManager.update(form);
			result = /** @type String */(serialize.serialize(form));
		}
	} else {
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
	const $self = this;
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
	 * The responsetype of an AJAX request.
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
		const payload = (response?.documentElement) ? response : this["responseText"];
		handleResponse($self, payload, trigger, false);
	};

	this.onError = function(response) {
		handleResponse($self, response, trigger, true);
	};
}

function handleResponse($self, response, trigger, isError) {
	const done = function () {
		notify(trigger);
	};
	console.log("Got response for trigger", trigger.id);
	try {
		/*
		 * The same trigger will never be pending more than once because "canSendRequest"
		 * will always return false for a trigger that is already pending.
		 *
		 * Therefore it is safe to delete it from the pending queue - no need for counters.
		 */
		const idx = pendingList.indexOf($self);
		if (idx >= 0) {
			pendingList.splice(idx, 1);
		} else {
			console.warn("Got response for trigger that was not in pending queue", trigger.id);
		}
		try {
			if (!isError) {
				trigger.callback(response, trigger);
			} else if (trigger.onerror) {
				trigger.onerror(response, trigger);
			}
			// Remove "aria-busy" AFTER the new content is loaded to avoid collapsing to zero pixels
			done();
		} catch (ex) {
			console.error(ex);
		}
	} finally {
		if (queueBlocked) {
			trigger.scheduleQueueProcessing();  // if the queue was blocked check again now
		}
	}
}

/**
 * Send the AJAX request NOW.
 * No second guessing, no buffering, no delays, call this when you are ready to go.
 *
 * @function
 */
Request.prototype.send = function () {
	const trigger = this.trigger;
	this.url = Trigger.getUrl(trigger);
	if (this.url) {
		try {
			if (trigger.method === trigger.METHODS.GET && this.postData) {
				if (this.url.indexOf("?") > 0) {
					this.url = addToQueryString(this.url, this.postData);
				} else {
					this.url += "?" + this.postData;
				}
				this.postData = "";
			}
			pendingList.push(this);  // we must do this before sending cos we can't guarantee what AJAX will do (could be forced into synchronous mode)
			trigger.profile.sent = Date.now();
			ajax.simpleRequest(this);
		} catch (ex) {
			pendingList.pop();  // error so assume the request is not pending - pop it off the queue
			notify(trigger);
			console.error(ex);
		}
	} else {
		console.warn("Could not find URL for trigger", this.trigger.id);
	}
};

export default Trigger;

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

