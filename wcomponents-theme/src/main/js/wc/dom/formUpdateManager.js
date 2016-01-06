/**
 * FormUpdateManager is responsible for orchestrating state writing of other classes. UI widgets which need to write
 * state should not individually listen to submit events and AJAX posts, instead they should subscribe to
 * FormUpdateManager.
 *
 * FormUpdateManager is responsible for:
 * * when to write state (subscribers will be notified at the releavnt times);
 * * where to write state (subscribers will be passed a container element in which to write state fields); and
 * * cleanup (the state container will be cleaned up before each state writing event).
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/Observer
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/getAncestorOrSelf
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/cancelUpdate
 *
 * @todo reorder code, document private members.
 * @todo OK so we have a melange, a trifle, a veritable stone soup of functions on the prototype chain and public
 * members inside the constructor. We use a singleton and do not extend formUpdateManager EVER so we shopuld just choose
 * one method of public function members and live with it. Maybe follow the Google guidelines: properties in the
 * constructor, methods on the proto chain.
 */
define(["wc/dom/event",
		"wc/dom/initialise",
		"wc/Observer",
		"wc/dom/uid",
		"wc/dom/getAncestorOrSelf",
		"wc/dom/shed",
		"wc/dom/attribute",
		"wc/dom/Widget"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Observer wc/Observer @param uid wc/dom/uid @param getAncestorOrSelf wc/dom/getAncestorOrSelf @param shed wc/dom/shed @param attribute wc/dom/attribute @param Widget wc/dom/Widget @ignore */
	function(event, initialise, Observer, uid, getAncestorOrSelf, shed, attribute, Widget) {
		"use strict";
		var STATE_CONTAINER_SUFFIX = "_state_container",
			SUB_METHOD = "writeState",
			INITED_ATTR = "fuminited",
			subscriberMthd = {method: SUB_METHOD},
			/**
			 * @var
			 * @alias module:wc/dom/formUpdateManager
			 */
			formUpdateManager,
			cancelUpdate,
			FILESELECTORWD;

		require(["wc/dom/cancelUpdate"], function(canUp) {
			cancelUpdate = canUp;  // prevent circular dependencies
		});

		/**
		 * @constructor
		 * @alias module:wc/dom/formUpdateManager~FormUpdateManager
		 * @private
		 */
		function FormUpdateManager() {
			var observer,
				FORM = "FORM";

			/**
			 * Subscribe to formUpdateManager so a module can take care of its own state writing needs.
			 *
			 * @see {@link module:wc/Observer#subscribe}
			 * @function module:wc/dom/formUpdateManager.subscribe
			 * @param {(Object|Function)} subscriber An object a subscriber to FormUpdateManager (an object which has a
			 *    writeState method) OR simple the writeState method itself.
			 * @param {Function} [subscriber.writeState] The function that will be notified by FormUpdateManager if
			 *    subscriber is an object. The subscriber function MUST be present at "publish" time, but need not be preset
			 *    at "subscribe" time (i.e. when subscribe() is called).
			 * @returns {Function} The subscriber function is returned unchanged.
			 */
			this.subscribe = function(subscriber) {
				function _subscribe(_subscriber) {
					var result = _subscriber;
					if (typeof _subscriber === "function") {
						observer.subscribe(_subscriber);
					}
					else {
						result = observer.subscribe(_subscriber, subscriberMthd);
					}
					return result;
				}
				if (!observer) {
					observer = new Observer();
					this.subscribe = _subscribe;
					// console.log("Configuring FormUpdateManager on first use");
				}
				return _subscribe(subscriber);
			};

			/**
			 * Remove a subscriber to formUpdateManager. You probably don't want to use this but it is vital for sane unit
			 * testing because subscribers are global.
			 *
			 * @function module:wc/dom/formUpdateManager.unsubscribe
			 * @param {Function} subscriber The subscriber function to unsubscribe.
			 */
			this.unsubscribe = function(subscriber) {
				if (observer) {
					observer.unsubscribe(subscriber);
				}
			};

			/**
			 * This triggers the "publish" which will ask all subscribers to write their state.
			 *
			 * @function  module:wc/dom/formUpdateManager.update
			 * @param {Element} container The form (or form segment container element) to which any state will attached.
			 *    Will also be passed to subscribers unless region is set.
			 * @param {Element} region A dom element "region" to be passed to subscribers instead of the form. This is the
			 *    section of the view which is being written.
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
			this.update = function(container, region, ignoreForm) {
				/*
				 * A possible enhancement would be to allow observers to return a value to indicate that the update
				 * should be cancelled (also we should pass an arg to subscribers to tell them if another subscriber
				 * has already cancelled).  If a subscriber cancels continue to notify all other observers (a la submit
				 * event) but don't allow 'uncancelling' 20090904
				 */
				var result = true,
					stateContainer,
					_container = container;
				if (observer) {
					if (!ignoreForm) {
						_container = getAncestorOrSelf(_container, FORM);
					}
					result = cancelUpdate ? (!(cancelUpdate.cancelSubmission(_container))) : true;
					if (result) {
						checkEnctype(_container);
						stateContainer = this.getStateContainer(_container);
						this.clean(_container);
						observer.notify((region || _container), stateContainer);  // arg1 = "from", arg2 ="to"
					}
				}
				return result;
			};

			/**
			 * Function to be called when the DOM is ready. Adds the submitEvent handler used to initiate state writing of
			 * custom controls.
			 *
			 * @function module:wc/dom/formUpdateManager.initialise
			 * @param {Element} element The body element of the document.
			 */
			this.initialise = function(element) {
				var forms, i, len;
				if (event.canCapture) {
					// ok i did a lazy test for non-ie by checking to see if capture was available even tho we not using it
					event.add(element, event.TYPE.submit, submitEvent);
				}
				else {  // Internet Explorer
					/*
					 * This block handles Internet Explorer < 9.
					 * The problem we need to deal with is that submit events should bubble
					 * (http://www.w3.org/TR/DOM-Level-2-Events/events.html)
					 * but in Internet Explorer they don't.
					 *
					 * This has two consequences for IE:
					 * 1. We need to loop over every form on page load.
					 * 2. Dynamically added forms are not wired up and this will cause bugs.
					 * If this becomes a problem in real systems the solution is:
					 * a. enhance this initialise function to handle a form element and to be called
					 * multiple times.
					 * b. get ajaxRegion to check newly inserted DOM regions for form elements
					 * and pass them to this function to initialise (in IE only).
					 */
					forms = document.forms;
					for (i = 0, len = forms.length; i < len; i++) {
						event.add(forms[i], event.TYPE.submit, submitEvent);
					}
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
				var func = add ? "add" : "remove";
				if (event.canCapture) {
					if (add) {
						event[func](el, event.TYPE.click, genericEventCancel, -1, null, true);
						event[func](el, event.TYPE.change, genericEventCancel, -1, null, true);
						event[func](el, event.TYPE.keydown, genericEventCancel, -1, null, true);
						event[func](el, event.TYPE.keypress, genericEventCancel, -1, null, true);
					}
					else {
						event[func](el, event.TYPE.click, genericEventCancel, true);
						event[func](el, event.TYPE.change, genericEventCancel, true);
						event[func](el, event.TYPE.keydown, genericEventCancel, true);
						event[func](el, event.TYPE.keypress, genericEventCancel, true);
					}
				}
				else  if (add) {
					event[func](el, event.TYPE.click, genericEventCancel, -1);
					event[func](el, event.TYPE.keydown, genericEventCancel, -1);
					event[func](el, event.TYPE.keypress, genericEventCancel, -1);
				}
				else {
					event[func](el, event.TYPE.click, genericEventCancel);
					event[func](el, event.TYPE.keydown, genericEventCancel);
					event[func](el, event.TYPE.keypress, genericEventCancel);
				}
			}

			/**
			 * Listener for this event type
			 * @function
			 * @private
			 * @param {Event} $event The instance of Event currently firing
			 */
			function submitEvent($event) {
				var inited, form = $event.target;
				if (!$event.defaultPrevented) {
					inited = attribute.has(form, INITED_ATTR);
					try {
						/*
						 * `inited` could be true if the form targets another frame
						 * and therefore the page does not roundtrip on submit.
						 */
						if (!inited) {
							addRemoveEvents(form, true);
							attribute.set(form, INITED_ATTR, true);
						}
						if (!(formUpdateManager.update(form))) {
							$event.preventDefault();
							addRemoveEvents(form);
							attribute.remove(form, INITED_ATTR);
							console.info("Submit event was cancelled AFTER subscribers were notified.");
						}
					}
					catch (ex) {
						addRemoveEvents(form);
						attribute.remove(form, INITED_ATTR);
						console.error("error in subscriber", ex);
						debugger;
					}
				}
				else {
					console.log("Submit event cancelled. Suscribers not notified.");
				}
			}

			/**
			 * Ensure that if there is a file selector in the form then the form enctype is set correctly.
			 * While the XSLT can build the form correctly this does not help if a file selector is added
			 * to the page at a later stage (eg via AJAX).
			 *
			 * @function
			 * @private
			 * @param {Element} form A HTML form.
			 */
			function checkEnctype(form) {
				var enctype = "multipart/form-data",
					enctypeAttr = "enctype",
					_form;
				if ((_form = getAncestorOrSelf(form, FORM))) {
					FILESELECTORWD = FILESELECTORWD || new Widget("input", "", {type: "file"});
					if (form[enctypeAttr] !== enctype && FILESELECTORWD.findDescendant(_form)) {
						// there is a file selector in the form
						_form[enctypeAttr] = enctype;  // browsers are happy with this
						_form.setAttribute(enctypeAttr, enctype);  // IE8 seems to need this
						// console.log("Fixing form enctype ", enctype);
					}
				}
			}
		}

		/**
		 * A helper for other classes when writing state fields. Adds a new hidden input field to the container with the
		 * provided name and value.
		 *
		 * @function module:wc/dom/formUpdateManager.writeStateField
		 * @param {Element} container The state container to which the state field will be added.
		 * @param {string} name The name of the parameter when the form is serialized.
		 * @param {Variant} [value] The value of the parameter when the form is serialized.
		 * @param {boolean} [unique] If true then state field must not already exist in container.
		 * @returns {?Element} The state field if it was created.
		 */
		FormUpdateManager.prototype.writeStateField = function(container, name, value, unique) {
			var state;
			if (container && name) {
				if (!unique || !(this.getStateField(container, name))) {
					state = document.createElement("input");
					state.type = "hidden";
					state.name = name;
					if (value !== undefined && value !== null) {  // don't write null or undefined, really, don't
						state.value = value;
					}
					container.appendChild(state);
				}
				else {
					console.warn("Not writing duplicate state field", name);
				}
			}
			return state || null;
		};

		/**
		 * Removes previously written state fields
		 * @function module:wc/dom/formUpdateManager.clean
		 * @param {Element} form The form element which we want to clean.
		 */
		FormUpdateManager.prototype.clean = function (form) {
			var stateContainer = this.getStateContainer(form);
			stateContainer.innerHTML = "";
		};

		/**
		 * Finds the "state container" for this form. The state container is the container element descendant of the form
		 * which contains state fields (where state fields are generally hidden input elements written by subscribers on
		 * previous updates). If no state container exists it will be created.
		 *
		 * @function module:wc/dom/formUpdateManager.getStateContainer
		 * @param {Element} form The form element for which we wish to retrieve the state container.
		 * @returns {Element} The state container.
		 */
		FormUpdateManager.prototype.getStateContainer = function(form) {
			var formId = form.id || (form.id = uid()),
				stateContainerId = formId + STATE_CONTAINER_SUFFIX,
				stateContainer = document.getElementById(stateContainerId);
			if (!stateContainer) {
				stateContainer = document.createElement("div");
				shed.hide(stateContainer);
				stateContainer.id = stateContainerId;
				form.appendChild(stateContainer);
			}
			return stateContainer;
		};

		/**
		 * Get a named field from the state container
		 *
		 * @function module:wc/dom/formUpdateManager.getStateField
		 * @param {Element} container The state container.
		 * @param {String} name  The field name to find.
		 * @returns {Element} The existing state field for this name in this container if it exists.
		 * If more than one state field exists for this name the first one will be returned.
		 */
		FormUpdateManager.prototype.getStateField = function (container, name) {
			return container.querySelector("input[type='hidden'][name='" + name + "']");
		};

		formUpdateManager = new FormUpdateManager();
		initialise.addBodyListener(formUpdateManager);
		return formUpdateManager;
	});
