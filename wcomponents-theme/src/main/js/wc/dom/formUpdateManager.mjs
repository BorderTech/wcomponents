/**
 * FormUpdateManager is responsible for orchestrating state writing of other classes. UI widgets which need to write
 * state should not individually listen to submit events and AJAX posts, instead they should subscribe to
 * FormUpdateManager.
 *
 * FormUpdateManager is responsible for:
 * * when to write state (subscribers will be notified at the relevant times);
 * * where to write state (subscribers will be passed a container element in which to write state fields); and
 * * cleanup (the state container will be cleaned up before each state writing event).
 *
 */
import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import Observer from "wc/Observer.mjs";
import uid from "wc/dom/uid.mjs";
import shed from "wc/dom/shed.mjs";

const STATE_CONTAINER_SUFFIX = "_state_container",
	SUB_METHOD = "writeState",
	INITED_ATTR = "fuminited",
	subscriberMthd = { method: SUB_METHOD };

let observer;

/**
 * @var
 * @alias module:wc/dom/formUpdateManager
 */
const formUpdateManager = {
	/**
	 * Subscribe to formUpdateManager so a module can take care of its own state writing needs.
	 *
	 * @see {@link module:wc/Observer#subscribe}
	 * @function module:wc/dom/formUpdateManager.subscribe
	 * @param {Object} subscriber An object a subscriber to FormUpdateManager (an object which has a writeState method)
	 *    OR simple the writeState method itself.
	 * @param {Function} [subscriber.writeState] The function that will be notified by FormUpdateManager if
	 *    subscriber is an object. The subscriber function MUST be present at "publish" time, but need not be preset
	 *    at "subscribe" time (i.e. when subscribe() is called).
	 * @returns {Function} The subscriber function is returned unchanged.
	 */
	subscribe: function(subscriber) {
		function _subscribe(_subscriber) {
			let result = _subscriber;
			if (typeof _subscriber === "function") {
				observer.subscribe(_subscriber);
			} else {
				result = observer.subscribe(_subscriber, subscriberMthd);
			}
			return result;
		}
		if (!observer) {
			observer = new Observer();
			formUpdateManager.subscribe = _subscribe;
		}
		return _subscribe(subscriber);
	},
	/**
	 * Remove a subscriber to formUpdateManager. You probably don't want to use this but it is vital for sane unit
	 * testing because subscribers are global.
	 *
	 * @function module:wc/dom/formUpdateManager.unsubscribe
	 * @param {Function} subscriber The subscriber function to unsubscribe.
	 */
	unsubscribe: function(subscriber) {
		if (observer) {
			observer.unsubscribe(subscriber);
		}
	},

	/**
	 * This triggers the "publish" which will ask all subscribers to write their state.
	 *
	 * @function  module:wc/dom/formUpdateManager.update
	 * @param {Element} container The form (or form segment container element) to which any state will attached.
	 *    Will also be passed to subscribers unless region is set.
	 * @param {Element} [region] A dom element "region" to be passed to subscribers instead of the form. This is the
	 *    section of the view which is being written. If not provided then `container` is used.
	 * @param {Boolean} [ignoreForm] if set then do not do a form lookup just accept the container. This arg should
	 *    only be set if the calling class is going to clean up after itself.
	 * @returns {Boolean} true if not cancelled by the user.
	 *
	 * @todo The observer test here means that the encType check will fail if nothing has subscribed. This is
	 * actually very unlikely in reality but is possible.
	 * @todo I know why I rewrote form to container and allow the ambiguity with region (mainly for cancelUpdate
	 * tests and small segment AJAX state writing) but this is not necessarily a good thing so we may want to
	 * revisit it and just make sure it is sane. Ambiguity is bad. The ignoreForm arg adds to this ambiguity and
	 * is used by {@link module:wc/dom/convertDynamicContent}.
	 */
	update: function(container, region, ignoreForm) {
		/*
		 * A possible enhancement would be to allow observers to return a value to indicate that the update
		 * should be cancelled (also we should pass an arg to subscribers to tell them if another subscriber
		 * has already cancelled).  If a subscriber cancels continue to notify all other observers (a la submit
		 * event) but don't allow 'uncancelling' 20090904
		 */
		let result = true;
		const form = /** @type HTMLFormElement */(ignoreForm ? container.closest("form") : container);

		checkEnctype(form);
		const stateContainer = this.getStateContainer(form);
		this.clean(form);
		if (observer) {
			observer.notify((region || form), stateContainer);  // arg1 = "from", arg2 ="to"
		}
		return result;
	},

	/**
	 * Function to be called when the DOM is ready. Adds the submitEvent handler used to initiate state writing of
	 * custom controls.
	 *
	 * @function module:wc/dom/formUpdateManager.initialise
	 * @param {HTMLBodyElement} element The body element of the document.
	 */
	initialise: function(element) {
		event.add(element, "submit", submitEvent);
	},

	/**
	 * A helper for other classes when writing state fields. Adds a new hidden input field to the container with the
	 * provided name and value.
	 *
	 * @function module:wc/dom/formUpdateManager.writeStateField
	 * @param {Element} container The state container to which the state field will be added.
	 * @param {string} name The name of the parameter when the form is serialized.
	 * @param {string} [value] The value of the parameter when the form is serialized.
	 * @param {boolean} [unique] If true then state field must not already exist in container.
	 * @param {boolean} [clean] If true then this state field is ignored for the purposes of determining "dirty" state (i.e. cancelUpdate).
	 * @returns {HTMLElement} The state field if it was created.
	 */
	writeStateField: function(container, name, value, unique, clean) {
		let stateField;
		if (container && name) {
			if (!unique || !(this.getStateField(container, name))) {
				stateField = document.createElement("input");
				stateField.type = "hidden";
				stateField.name = name;
				if (clean) {
					stateField.setAttribute("data-wc-clean", clean.toString());
				}
				if (value !== undefined && value !== null) {  // don't write null or undefined, really, don't
					stateField.value = value;
				}
				container.appendChild(stateField);
			} else {
				console.warn("Not writing duplicate state field", name);
			}
		}
		return stateField || null;
	},

	/**
	 * Removes previously written state fields
	 * @function module:wc/dom/formUpdateManager.clean
	 * @param {HTMLFormElement} form The form element which we want to clean.
	 */
	clean: function (form) {
		this.getStateContainer(form).innerHTML = "";
	},

	/**
	 * Finds the "state container" for this form. The state container is the container element descendant of the form
	 * which contains state fields (where state fields are generally hidden input elements written by subscribers on
	 * previous updates). If no state container exists it will be created.
	 *
	 * @function module:wc/dom/formUpdateManager.getStateContainer
	 * @param {Element} form The form element for which we wish to retrieve the state container (does not strictly have to be a form).
	 * @returns {HTMLElement} The state container.
	 */
	getStateContainer: function(form) {
		const formId = form.id || (form.id = uid()),
			stateContainerId = formId + STATE_CONTAINER_SUFFIX;
		let stateContainer = document.getElementById(stateContainerId);
		if (!stateContainer) {
			stateContainer = document.createElement("div");
			shed.hide(stateContainer, true);
			stateContainer.id = stateContainerId;
			form.appendChild(stateContainer);
		}
		return stateContainer;
	},
	/**
	 * Get a named field from the state container
	 *
	 * @function module:wc/dom/formUpdateManager.getStateField
	 * @param {Element} container The state container.
	 * @param {String} name  The field name to find.
	 * @returns {HTMLElement} The existing state field for this name in this container if it exists.
	 * If more than one state field exists for this name the first one will be returned.
	 */
	getStateField: function (container, name) {
		return container.querySelector(`input[type='hidden'][name='${name}']`);
	}
};

/**
 * An event listener to cancel events. Needs to be wired up VERY early.
 *
 * @see {@link module:wc/dom/formUpdateManager~addRemoveEvents}
 * @function
 * @private
 * @param {Event} $event The event.
 */
function genericEventCancel($event) {
	$event.preventDefault();
}

/**
 * Adds and removes event listeners to prevent multiple submits.
 *
 * @function
 * @private
 * @param {Element} el A form element or body in good browsers.
 * @param {boolean} [add] Indicates if we are adding or removing the event listeners.
 */
function addRemoveEvents(el, add) {
	const func = add ? "add" : "remove";
	if (add) {
		event[func](el, "click", genericEventCancel, -1, null, true);
		event[func](el, "change", genericEventCancel, -1, null, true);
		event[func](el, "keydown", genericEventCancel, -1, null, true);
		event[func](el, "keyup", genericEventCancel, -1, null, true);
	} else {
		event[func](el, "click", genericEventCancel, true);
		event[func](el, "change", genericEventCancel, true);
		event[func](el, "keydown", genericEventCancel, true);
		event[func](el, "keyup", genericEventCancel, true);
	}
}

/**
 * Listener for this event type
 * @function
 * @private
 * @param {SubmitEvent & { target: HTMLElement }} $event The instance of Event currently firing
 */
function submitEvent($event) {
	const form = $event.target;
	if (!$event.defaultPrevented) {
		const inited = form[INITED_ATTR];
		try {
			/*
			 * `inited` could be true if the form targets another frame
			 * and therefore the page does not round trip on submit.
			 */
			if (!inited) {
				addRemoveEvents(form, true);
				form[INITED_ATTR] = true;
			}
			if (!(formUpdateManager.update(form))) {
				$event.preventDefault();
				addRemoveEvents(form);
				form[INITED_ATTR] = false;
				console.info("Submit event was cancelled AFTER subscribers were notified.");
			}
		} catch (ex) {
			addRemoveEvents(form);
			form[INITED_ATTR] = false;
			console.error("error in subscriber", ex);
		}
	} else {
		console.log("Submit event cancelled. Subscribers not notified.");
	}
}

/**
 * Ensure that if there is a file selector in the form then the form enctype is set correctly.
 * While the XSLT can build the form correctly this does not help if a file selector is added
 * to the page at a later stage (e.g. via AJAX).
 *
 * @function
 * @private
 * @param {HTMLFormElement} form A HTML form.
 */
function checkEnctype(form) {
	const enctype = "multipart/form-data",
		enctypeAttr = "enctype",
		_form = form.closest("form");
	if (_form) {
		const fileInputSelector = "input[type='file']";
		if (_form[enctypeAttr] !== enctype && _form.querySelector(fileInputSelector)) {
			// there is a file selector in the form
			_form[enctypeAttr] = enctype;  // browsers are happy with this
			_form.setAttribute(enctypeAttr, enctype);  // IE8 seems to need this
			// console.log("Fixing form enctype ", enctype);
		}
	}
}

initialise.addBodyListener(formUpdateManager);
export default formUpdateManager;
