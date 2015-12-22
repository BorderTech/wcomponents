/**
 * Provides a mechanism to warn a user of pending navigation or cancel invokation which may result in user initiated
 * changes from being lost or discarded.
 *
 * @todo this should be merged into wc/dom/formUpdateManager in to solve a complex circular dependency.
 * @todo sort out the method order.
 * @todo to a large extent we could probably use Element.defaultValue instead.
 *
 * @module
 * @requires module:wc/i18n/i18n
 * @requires module:wc/ajax/triggerManager
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/serialize
 * @requires external:lib/sprintf
 * @requires module:wc/dom/Widget
 * @requires module:wc/urlParser
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/focus
 */
define(["wc/i18n/i18n",
		"wc/ajax/triggerManager",
		"wc/dom/uid",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/serialize",
		"lib/sprintf",
		"wc/dom/Widget",
		"wc/dom/formUpdateManager",
		"wc/dom/focus"],
	/** @param i18n wc/i18n/i18n @param triggerManager wc/ajax/triggerManager @param uid wc/dom/uid @param event wc/dom/event @param initialise wc/dom/initialise @param serialize wc/dom/serialize @param sprintf lib/sprintf @param Widget wc/dom/Widget @param formUpdateManager wc/dom/formUpdateManager @param focus wc/dom/focus @ignore */
	function(i18n, triggerManager, uid, event, initialise, serialize, sprintf, Widget, formUpdateManager, focus) {
		"use strict";

		/*
		 * TODO: we have a lot of form ID testing. Obviously if the form does not have an id then the whole
		 * unsaved changes registry malarkey will fail. All forms created by WApplication have an id, so maybe we do not
		 * need these tests?*/

		 /**
		  * @constructor
		  * @private
		  * @alias module:wc/dom/cancelUpdate~CancelUpdateControl */
		 function CancelUpdateControl() {
			var loading = false,  // if cancel button && unsavedOnServer() get dialog twice without this
				buttonClicked,
				FORM = new Widget("FORM"),
				FORM_UNSAVED,
				// SUBMIT_CONTROL = new Widget("BUTTON", "", {"type":"submit"}),
				CANCEL_BUTTON,
				registry = {},
				RECALC = "-recalc",
				MESSAGE;

			/**
			 * Get the current (not stored) state of a form.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form whise state we want.
			 * @returns {String} The serialized state of the form.
			 */
			function getCurrentState(form) {
				return serialize.serialize(form, true, true);
			}

			/**
			 * Determines if the form has unsaved changes.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form we are going to test for unsaved changes.
			 * @returns {boolean} True if the form state has changed, false if the state has not changed or has never
			 *    been calculated.
			 */
			function hasUnsavedChanges(form) {
				var formId, result = false;
				FORM_UNSAVED = FORM_UNSAVED || FORM.extend("wc_unsaved");
				if (!(result = !!FORM_UNSAVED.findDescendant(document.body))) {
					formId = form.id;
					if (!(formId && registry[formId])) {  // the form was never stored so we can assume it has not changed.
						return false;
					}
					formUpdateManager.clean(form);  // this clears out any previously written states (such as from an AJAX update) which will always make the form appear to have changed
					result = serialize.areDifferent(registry[formId], getCurrentState(form));
				}
				return result;
			}

			/**
			 * Stores the state of a form for later comparison. This is called after AJAX.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form of which we are going to save state.
			 */
			function storeFormState(form) {
				var formId;
				if (form && (formId = form.id)) {  // if the form does not have an ID then the initial state has not been set
					registry[formId] = getCurrentState(form);
				}
			}

			/**
			 * Determines if an element is a cancel button.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is the kind of button or link that triggers a cancelUpdate check.
			 */
			function isCancelUpdateButton(element) {
				var result, control;
				if (element && triggerManager.getTrigger(element)) {
					return false;
				}
				CANCEL_BUTTON = CANCEL_BUTTON || new Widget("BUTTON", "wc_btn_cancel");  // do not extend SUBMIT_CONTROL else navigation link buttons cease to trigger unsaved changes warnings
				if ((control = CANCEL_BUTTON.findAncestor(element))) {
					result = control.id;
				}
				return result;
			}

			/**
			 * Click event listener to store the last clicked element in case we need to use it to determine if we have
			 * a cancel button when we are inside a submit event or other function which calls cancelSubmission.
			 *
			 * @function
			 * @private
			 * @param {type} $event
			 * @returns {undefined}
			 */
			function clickEvent($event) {
				var element = $event.target, form, id;
				if (!$event.defaultPrevented) {
					if ((id = isCancelUpdateButton(element))) {
						buttonClicked = id;
						if ((form = FORM.findAncestor(element)) && instance.cancelSubmission(form)) {
							buttonClicked = null;
							$event.preventDefault();
						}
					}
					else if (buttonClicked) {
						buttonClicked = null;
					}
				}
			}

			/**
			 * Cancels a form submission based on user response to an unsaved changes warning.
			 *
			 * @function
			 * @private
			 * @param {Element} element Any element within a form.
			 * @param {Element} submitter The element which originated the submission event.
			 * @returns {Boolean} true if the user wants to keep their unsaved changes and cancel the submission, false
			 *    to continue with the submission/navigation.
			 */
			function cancelSubmit(element, submitter) {
				var title = i18n.get("${wc.dom.cancelUpdate.i18n.fallbackTitle}"),
					keep = true,
					result,
					form,
					formTitle,
					msg;
				if (!loading) {
					msg = (submitter ? submitter.getAttribute("${wc.ui.button.attrib.confirmMessage}") : "");
					if (!msg) {
						MESSAGE = MESSAGE || "'%s' " + i18n.get("${wc.dom.cancelUpdate.i18n.message}");
						if ((form = FORM.findAncestor(element)) && (formTitle = form.getAttribute("title"))) {
							title = formTitle;
						}
						msg = (sprintf.sprintf(MESSAGE, title, "foo"));
					}
					keep = window.confirm(msg);
				}
				// if they didn't mean the change, cancel the "cancel" event.
				result = !keep;
				loading = keep;
				return result;
			}

			/**
			 * This AJAX subscriber runs before any content is added to the DOM and tests
			 * all forms in the page to determine if we have to recalculate the initial
			 * state of a form after the ajax action finishes. If the form ancestor of
			 * the ajax target element does not have unsaved changes prior to the AJAX
			 * action then we set a flag to recalculate the 'initial' state allowing for
			 * the changes made by the AJAX action.
			 *
			 * This is to cover the situation where an AJAX transaction occurs which
			 * adds or removes form fields. This will always cause an unsavedChanges warning
			 * because the serialization is different, even if the user does not actually
			 * change anything. This will occur, for example, if a WCancelButton is
			 * triggered in a WDialog before the user makes any changes.
			 *
			 * @function
			 * @private
			 * @param {Element} element The AJAX target element in the DOM prior to the ajax action.
			 */
			function ajaxSubscriber(element/* , documentFragment, action */) {
				var form, key;
				if (element && (form = FORM.findAncestor(element))) {
					if (!form.id) {  // not likely, but not serialized as the serialize routine will add an id to the form.
						return;
					}

					key = form.id;
					if (!registry[key]) {  // not yet serialized, so no need to recalculate the initial state
						return;
					}

					key = form.id + RECALC;
					if (registry[key]) {  // already going to recalculate so no need to do more
						return;
					}

					if (!hasUnsavedChanges(form)) {  // no changes yet so we need to recalc the initial state after we get the response back
						registry[key] = true;
					}
				}
			}

			/**
			 * This AJAX subscriber fires after the AJAX action has added components to
			 * the DOM. If a RECALC flag has been set for a form then recalculate the
			 * 'initial' state to allow for the changes made by the AJAX action.
			 *
			 * @function
			 * @private
			 * @param {Element} element The AJAX target element in the DOM prior to the AJAX action.
			 */
			function postAjaxSubscriber(element/* , action */) {
				var form, key;
				if (element && (form = FORM.findAncestor(element))) {
					key = form.id + RECALC;
					if (registry[key]) {
						formUpdateManager.clean(form);  // clear the write state info left over from the ajax request
						storeFormState(form);
						delete registry[key];
					}
				}
			}

			/**
			 * Iterator function for resetAllFormState's forEach. Resets and re-stores the "initial" state of a form if
			 * it has previously been stored.
			 * @function
			 * @private
			 * @param {Element} form A HTML form element.
			 */
			function _resetForm(form) {
				var key = form.id;
				if (!key) {  // form never serialized
					return;
				}
				if (registry[key]) {
					storeFormState(form);
				}
			}

			/**
			 * Stores the form state on page load. A specialisation of storeFormState
			 * which only triggers once per form on page load and checks if the state has
			 * been stored already before storing. This test will prevent the form state
			 * being updated on AJAX initiated postInit without the necessary tests
			 * incorporated in the AJAX subscribers.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form (or form segment) we need to store.
			 */
			function storeInitialFormState(form) {
				var formId;
				if (form) {
					formId = form.id || (form.id = uid());
					if (!registry[formId]) {
						storeFormState(form);
					}
				}
			}

			/**
			 * Allow any other class to do a forced reset of the initial form state. This is required if the initialise
			 * or post-init functions of a class cause an update to the form state before the user interacts with the
			 * form. See {@link module:wc/ui/dateField~processNow}.
			 *
			 * @function
			 * @alias module:wc/dom/cancelUpdate.resetAllFormState
			 */
			this.resetAllFormState = function() {
				Array.prototype.forEach.call(FORM.findDescendants(document), _resetForm);
			};

			/**
			 * Adds these element to the "initial" state of the form.
			 * Call this carefully - it does not replace existing elements with the same name.
			 *
			 * @param {NodeList} elements A collection of elements (array or array-like).
			 */
			this.addElements = function(elements) {
				var i;
				for (i = 0; i < elements.length; i++) {
					this.addElement(elements[i]);
				}
			};

			/**
			 * Adds this element to the "initial" state of the form.
			 * Call this carefully - it does not replace existing elements with the same name.
			 *
			 * @param {Element} element A form element.
			 */
			this.addElement = function (element) {
				var form, nodeList, oldState, newState, newKeys, next, i;
				if (element && (form = element.form) && form.id && (oldState = registry[form.id])) {
					nodeList = [element];
					newState = serialize.serialize(nodeList, true, true);
					newKeys = Object.keys(newState);
					for (i = 0; i < newKeys.length; i++) {
						next = newKeys[i];
						if (oldState.hasOwnProperty(next)) {
							oldState[next] = oldState[next].concat(newState[next]);
						}
						else {
							oldState[next] = newState[next];
						}
					}
				}
				else {
					console.log("Could not add state for element", element);
				}
			};

			/**
			 * Remove these element from the "initial" state of the form.
			 *
			 * @param {NodeList} elements A collection of elements (array or array-like).
			 */
			this.removeElements = function(elements) {
				var i;
				for (i = 0; i < elements.length; i++) {
					this.removeElement(elements[i]);
				}
			};

			/**
			 * Removes this element's current state from the "initial" state of the form.
			 *
			 * @param {Element} element A form element.
			 */
			this.removeElement = function (element) {
				var form, nodeList, oldState, delState, newKeys, next, i, nextVal, delIdx;
				if (element && (form = element.form) && form.id && (oldState = registry[form.id])) {
					nodeList = [element];
					delState = serialize.serialize(nodeList, true, true);
					newKeys = Object.keys(delState);
					for (i = 0; i < newKeys.length; i++) {
						next = newKeys[i];
						if (oldState.hasOwnProperty(next)) {
							while (delState[next].length > 0) {
								nextVal = delState[next].pop();
								delIdx = oldState[next].indexOf(nextVal);
								if (delIdx > -1) {
									oldState[next].splice(delIdx, 1);
								}
							}
						}
					}
				}
				else {
					console.log("Could not remove state for element", element);
				}
			};

			/**
			 * Set up the cancel update controller.
			 * @function
			 * @alias module:wc/dom/cancelUpdate.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, -100);
			};

			/**
			 * Late initialisation to store the initial state of all forms in a document and set up any subscribers.
			 * @function
			 * @alias module:wc/dom/cancelUpdate.postInit
			 */
			this.postInit = function() {
				Array.prototype.forEach.call(FORM.findDescendants(document), storeInitialFormState);
				require(["wc/ui/ajax/processResponse"], function(processResponse) {
					processResponse.subscribe(ajaxSubscriber);  // when ajax occurs, but before stuff is added to the DOM, determine if we need to recalculate the 'initial' state
					processResponse.subscribe(postAjaxSubscriber, true);  // listen for ajax completion and determine if we need to recalculate 'initial' state
				});
			};

			/**
			 * Determines if we should cancel a form submission (not a submit event). If a cancelUpdateButton has been
			 * clicked this function checks to see if the form has been changed and if so it will confirm with the user
			 * that they wish to continue.
			 *
			 * @function
			 * @alias module:wc/dom/cancelUpdate.cancelSubmission
			 * @param {Element} form An element which is, or is within, a FORM element.
			 * @returns {Boolean} true if the user wishes to cancel or if the form is not valid.
			 */
			this.cancelSubmission = function(form) {
				var result = false,
					submitter;
				if (buttonClicked && (submitter = document.getElementById(buttonClicked)) && focus.canFocus(submitter) && (form = FORM.findAncestor(form))) {
					if (isCancelUpdateButton(submitter) && hasUnsavedChanges(form)) {
						if ((result = cancelSubmit(form, submitter))) {
							focus.setFocusRequest(submitter);
						}
					}
				}
				return result;
			};
		}
		var /** @alias module:wc/dom/cancelUpdate */ instance = new CancelUpdateControl();
		initialise.register(instance);
		return instance;
	});
