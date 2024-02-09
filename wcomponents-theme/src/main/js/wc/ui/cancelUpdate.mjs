import i18n from "wc/i18n/i18n.mjs";
import triggerManager from "wc/ajax/triggerManager.mjs";
import uid from "wc/dom/uid.mjs";
import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import serialize from "wc/dom/serialize.mjs";
import isSuccessfulElement from "wc/dom/isSuccessfulElement.mjs";
import sprintf from "wc/string/sprintf.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import focus from "wc/dom/focus.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";

/*
 * TODO: we have a lot of form ID testing. Obviously if the form does not have an id then the whole
 * unsaved changes registry malarkey will fail. All forms created by WApplication have an id, so maybe we do not
 * need these tests?
 */

let loading = false,  // if cancel button && unsavedOnServer() get dialog twice without this
	buttonClicked,
	CANCEL_TITLE = "This page",
	CANCEL_MESSAGE = "'%s' has unsaved changes. Would you like to discard them?",
	registry = {};

const RECALC = "-recalc";

const instance = {
	/**
	 * Allow any other class to do a forced reset of the initial form state. This is required if the initialise
	 * or post-init functions of a class cause an update to the form state before the user interacts with the
	 * form. See {@link module:wc/ui/dateField~processNow}.
	 *
	 * @function
	 * @alias module:wc/ui/cancelUpdate.resetAllFormState
	 */
	resetAllFormState: () => Array.from(document.forms).forEach(_resetForm),

	/**
	 * Adds these elements to the "initial" state of the form.
	 * Call this carefully - it does not replace existing elements with the same name.
	 *
	 * @param {Element} element  A form control or container.
	 */
	addElements: function(element) {
		const elements = isSuccessfulElement.getAll(element, true);
		for (const next of elements) {
			this.addElement(next);
		}
	},

	/**
	 * Adds this element to the "initial" state of the form.
	 * Call this carefully - it does not replace existing elements with the same name.
	 *
	 * @param {Element} element A form control.
	 */
	addElement: function (element) {
		const form = element ? element["form"] : null;
		const oldState = form?.id ? registry[form.id] : null;
		if (oldState) {
			const elements = /** @type {HTMLElement[]} */([element]);
			const newState = serialize.serialize(elements, true, true, isDirty);
			const newKeys = Object.keys(newState);
			for (const next of newKeys) {
				if (oldState.hasOwnProperty(next)) {
					oldState[next] = oldState[next].concat(newState[next]);
				} else {
					oldState[next] = newState[next];
				}
			}
		} else {
			console.log("Could not add state for element", element);
		}
	},

	/**
	 * Remove these elements from the "initial" state of the form.
	 *
	 * @param {Element} element A form control or container.
	 */
	removeElements: function(element) {
		const elements = isSuccessfulElement.getAll(element, true);
		for (const next of elements) {
			this.removeElement(next);
		}
	},

	/**
	 * Removes this element's current state from the "initial" state of the form.
	 *
	 * @param {Element} element A form control.
	 */
	removeElement: function (element) {
		const form = element ? element["form"] : null;
		const oldState = form?.id ? registry[form.id] : null;
		if (oldState) {
			const nodeList = /** @type {HTMLElement[]} */([element]);
			const delState = serialize.serialize(nodeList, true, true, isDirty);
			const newKeys = Object.keys(delState);
			for (const next of newKeys) {
				if (oldState.hasOwnProperty(next)) {
					while (delState[next].length > 0) {
						let nextVal = delState[next].pop();
						let delIdx = oldState[next].indexOf(nextVal);
						if (delIdx > -1) {
							oldState[next].splice(delIdx, 1);
						}
					}
				}
			}
		} else {
			console.log("Could not remove state for element", element);
		}
	},

	/**
	 * Determines if we should cancel a form submission (not a submit event). If a cancelUpdateButton has been
	 * clicked this function checks to see if the form has been changed and if so it will confirm with the user
	 * that they wish to continue.
	 *
	 * @function
	 * @alias module:wc/ui/cancelUpdate.cancelSubmission
	 * @param {Element} container An element which is, or is within, a FORM element.
	 * @returns {boolean} true if the user wishes to cancel or if the form is not valid.
	 */
	cancelSubmission: function(container) {
		if (buttonClicked) {
			const submitter = document.getElementById(buttonClicked);
			if (submitter && focus.canFocus(submitter)) {
				/** @type HTMLFormElement */
				const form = container.closest("form");
				if (form && isCancelUpdateButton(submitter) && hasUnsavedChanges(form) && cancelSubmit(form, submitter)) {
					formUpdateManager.clean(form);
					focus.setFocusRequest(submitter);
					return true;
				}
			}
		}
		return false;
	}
};

/**
 * Get the current (not stored) state of a form.
 *
 * @function
 * @private
 * @param {HTMLFormElement} form The form whose state we want.
 * @returns {Object.<string, string[]>}} The serialized state of the form.
 */
function getCurrentState(form) {
	return /** @type {Object.<string, string[]>} */ (serialize.serialize(form, true, true, isDirty));
}

/**
 * Filters out "clean" elements from the serialization.
 * @param {Element} element A state field.
 * @returns {Boolean} false if the element should be vetoed.
 */
function isDirty(element) {
	return !element.hasAttribute("data-wc-clean");
}

/**
 * Determines if the form has unsaved changes.
 *
 * @function
 * @private
 * @param {HTMLFormElement} form The form we are going to test for unsaved changes.
 * @returns {boolean} True if the form state has changed, false if the state has not changed or has never
 *    been calculated.
 */
function hasUnsavedChanges(form) {
	let result = !!document.body.querySelector("form.wc_unsaved");
	if (!result) {
		const formId = form.id;
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
 * @param {HTMLFormElement} form The form of which we are going to save state.
 */
function storeFormState(form) {
	const formId = form?.id;
	if (formId) {  // if the form does not have an ID then the initial state has not been set
		registry[formId] = getCurrentState(form);
	}
}

/**
 * Determines if an element is a cancel button.
 *
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {string} The element id if the element is the kind of button that triggers a cancelUpdate check.
 */
function isCancelUpdateButton(element) {
	if (element && triggerManager.getTrigger(element)) {
		return "";
	}
	// do not extend SUBMIT_CONTROL else navigation link buttons cease to trigger unsaved changes warnings
	const cancelButton = "button.wc_btn_cancel[type='button']";
	/** @type HTMLButtonElement */
	const control = element.closest(cancelButton);
	if (control) {
		return control.id;
	}
	return "";
}

/**
 * Click event listener to store the last clicked element in case we need to use it to determine if we have
 * a cancel button when we are inside a submit event or other function which calls cancelSubmission.
 *
 * @function
 * @private
 * @param {MouseEvent & {target: HTMLButtonElement}} $event
 */
function clickEvent($event) {
	const target = $event.target;
	if (!$event.defaultPrevented) {
		const id = isCancelUpdateButton(target);
		if (id) {
			buttonClicked = id;
			const form = target.form;
			if (form && instance.cancelSubmission(form)) {
				buttonClicked = null;
				$event.preventDefault();
			}
		} else if (buttonClicked) {
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
	let title = CANCEL_TITLE,
		keep = true,
		result;
	if (!loading) {
		let msg = (submitter ? submitter.getAttribute("data-wc-btnmsg") : "");
		if (!msg) {
			const form = element["form"] || submitter["form"] || element.closest("form");
			const formTitle = form?.getAttribute("title");
			if (formTitle) {
				title = formTitle;
			}
			msg = sprintf(CANCEL_MESSAGE, title);
		}
		keep = confirm(msg);
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
	const form = element ? element.closest("form") : null;
	// missing id not likely, but not serialized as the serialize routine will add an id to the form.
	if (form?.id) {
		let key = form.id;
		if (!registry[key]) {  // not yet serialized, so no need to recalculate the initial state
			return;
		}

		key = form.id + RECALC;
		if (hasUnsavedChanges(form)) {
			if (registry[key]) {
				delete registry[key];
			}
		} else {
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
	const form = element ? element.closest("form") : null;
	if (form) {
		const key = form.id + RECALC;
		if (registry[key]) {
			formUpdateManager.clean(form);  // clear the write state info left over from the ajax request
			storeFormState(form);
			delete registry[key];
		} else {
			instance.addElements(element);
		}
	}
}

/**
 * Iterator function for resetAllFormState's forEach.
 * Resets and re-stores the "initial" state of a form if it has previously been stored.
 * @function
 * @private
 * @param {HTMLFormElement} form A HTML form element.
 */
function _resetForm(form) {
	const key = form.id;
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
 * @param {HTMLFormElement} form The form (or form segment) we need to store.
 */
function storeInitialFormState(form) {
	if (form) {
		const formId = form.id || (form.id = uid());
		if (!registry[formId]) {
			storeFormState(form);
		}
	}
}

/**
 * Provides a mechanism to warn a user of cancel invocation which may result in user initiated changes from being lost or discarded.
 *
 * I suggest erring on the side of NOT nagging. Yes, the user may lose work if we get it wrong this way but the alternative is that they get
 * used to seeing the warning message and ignoring it because it is wrong. The user needs to know that if we show that dialog we really mean
 * it.
 *
 * @todo to a large extent we could probably use Element.defaultValue instead.
 */

initialise.register({
	/**
	 * Set up the cancel update controller.
	 * @function
	 * @alias module:wc/ui/cancelUpdate.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, "click", clickEvent, -100);
		return i18n.translate(["cancel_title", "cancel_message"]).then((translations) => {
			CANCEL_TITLE = translations[0];
			CANCEL_MESSAGE = translations[1];
		});
	},

	/**
	 * Late initialisation to store the initial state of all forms in a document and set up any subscribers.
	 * @function
	 * @alias module:wc/ui/cancelUpdate.postInit
	 */
	postInit: () => {
		Array.from(document.forms).forEach(storeInitialFormState);
		processResponse.subscribe(ajaxSubscriber);  // when ajax occurs, but before stuff is added to the DOM, determine if we need to recalculate the 'initial' state
		processResponse.subscribe(postAjaxSubscriber, true);  // listen for ajax completion and determine if we need to recalculate 'initial' state
	},

	/**
	 * Unsubscribes event listeners etc.
	 * @param {Element} element The element being deinitialised, usually document.body.
	 */
	deinit: element => {
		event.remove(element, "click", clickEvent);
		processResponse.unsubscribe(ajaxSubscriber);
		processResponse.unsubscribe(postAjaxSubscriber, true);
	}
});

export default instance;
