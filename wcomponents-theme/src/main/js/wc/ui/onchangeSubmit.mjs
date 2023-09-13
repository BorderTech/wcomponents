import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import triggerManager from "wc/ajax/triggerManager.mjs";
import serialize from "wc/dom/serialize.mjs";
import timers from "wc/timers.mjs";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement.mjs";
import label from "wc/ui/label.mjs";
import i18n from "wc/i18n/i18n.mjs";
import textContent from "wc/dom/textContent.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";

const submitterselector = ".wc_soc",
	load_selectselector = `${submitterselector}[data-wc-list]`,
	triggers = [
		`${submitterselector}[type='checkbox']`,
		`${submitterselector}[type='radio']`,
		`${submitterselector}[role='checkbox']`,
		`${submitterselector}[role='radio']`].join(),
	optionOnLoad = {},
	DEP_WARNING = "DEPRECATION WARNING: onChangeSubmit is deprecated as it causes accessibility problems. Use AJAX or a submit button.";

let submitting = false,  // this is a safety net to prevent double submits if both the change event and shed subscriber fire.
	ignoreChange = false;

/**
 * Provides a means to invoke a form submission directly from the change event of a form control. If a form control is
 * marked as 'submitOnChange' then we need to queue up a form submission request when it changes.
 *
 * **NOTE:** this has certain negative accessibility implications around unexpectedly changing context. As a consequence
 * we recommend submitOnChange not be used, and it may be removed from future releases.
 *
 * @module
 */
const instance = {
	/**
	 * Allow an external module which manipulates labels to be able to set the SoC warning.
	 * @function module:wc/ui/onchangeSubmit.warn
	 * @public
	 * @param {Element} el THe element which may be able to "submit on change"
	 * @param {Element} [lbl] The element's label/legend/labelling element if it is already available - just prevents us having to do double
	 * look-ups.
	 */
	warn: function(el, lbl) {
		if (!el?.matches(submitterselector) || triggerManager.getTrigger(el)) {
			return;
		}
		const myLabel = lbl || /** @type {HTMLElement} */(getFirstLabelForElement(el));
		if (myLabel) {
			i18n.translate("submitOnChange").then(/** @param {string} submitOnChangeHint */(submitOnChangeHint) => {
				// do not allow an application to override i18n in order to make this warning empty
				const realSoCHint = submitOnChangeHint || "Changing the value of this field will cause immediate save.",
					hint = label.getHint(myLabel);
				if (hint) {
					const hintContent = textContent.get(hint);
					if (hintContent.indexOf(realSoCHint) === -1) {
						label.setHint(myLabel, realSoCHint);
					}
				} else {
					label.setHint(myLabel, realSoCHint);
				}
				// if the label is off-screen force it back on.
				myLabel.classList.remove("wc-off");
			});
		}
	},

	/**
	 * Set a flag to ignore a change event.
	 * @function module:wc/ui/onchangeSubmit.ignoreNextChange
	 * @public
	 */
	ignoreNextChange: () => ignoreChange = true,

	/**
	 * Allow external scripts to clear their own prevent submit on next change event before any change event is
	 * fired.
	 * @function module:wc/ui/onchangeSubmit.clearIgnoreChange
	 * @public
	 */
	clearIgnoreChange: () => ignoreChange = false,
};

/**
 * Registry setter helper for selects which are loaded dynamically via a datalist. Stores the option which
 * was selected on load.
 * @function
 * @private
 * @param {Element} element The select element to store.
 */
function setLoadedOptionRegistry(element) {
	optionOnLoad[element.id] = getElementValue(element);
}

/**
 * Registry getter helper for selects which are loaded dynamically via a datalist. Get the option which was
 * selected on load.
 * @function
 * @private
 * @param {Element} element A select element.
 */
function getLoadedOptionRegistry(element) {
	return optionOnLoad[element.id];
}

/**
 * Registry un-setter helper for selects which are loaded dynamically via a datalist: removes the reference
 * to the option which was selected on load.
 * @function
 * @private
 * @param {Element} element The select to unset.
 */
function removeLoadedOptionRegistry(element) {
	optionOnLoad[element.id] = null;
}

/**
 * Get the serialized value of an element if it is a select which is loaded dynamically via a datalist.
 * @function
 * @private
 * @param {Element} element The element to serialize.
 * @returns {String} The serialized value of element if it is a cacheable SELECT.
 */
function getElementValue(element) {
	if (element.matches(load_selectselector)) {
		return /** @type {string} */(serialize.serialize([element]));
	}
	return "";
}

/**
 * Fire the custom submit event if needed. Start the custom event rolling by checking if we have an ajax
 * trigger if so, don't go any further. Otherwise, check if we have a submitOnChange element and queue a
 * form submission if we do.
 * @function
 * @private
 * @param {Element} element The element firing the submitOnChange.
 */
function fireElement(element) {
	if (!submitting) {
		if (!triggerManager?.getTrigger(element)) {
			const form = element.matches(submitterselector) ? element.closest("form") : null;
			if (form) {
				if (element.matches(load_selectselector)) {
					const loadedOption = getLoadedOptionRegistry(element);
					const testValue = getElementValue(element);

					if (loadedOption !== testValue) {
						console.warn(DEP_WARNING);
						timers.setTimeout(event.fire, 0, form, "submit");
					}
					removeLoadedOptionRegistry(element);
				} else {
					submitting = true;
					console.warn(DEP_WARNING);
					timers.setTimeout(event.fire, 0, form, "submit");
				}
			}
		}
	} else {
		console.warn("onchange submit fired twice");  // this is going to be hard to spot when the page is submitting
	}
}

/**
 * Focus event listener for focus for browsers which can wire directly on focus.
 * Needed by FF & chrome and used to set up the loaded option of a select with options dynamically loaded
 * from a datalist.
 * @function
 * @private
 * @param {Event & { target: HTMLElement }} $event The focus event.
 */
function domFocusEvent({ target }) {
	if (target?.matches(load_selectselector)) {
		setLoadedOptionRegistry(target);
	}
}

/**
 * Change event listener to start the submit process rolling. This is not for checkboxes and radiobuttons
 * which will fire through the shed observer it is essentially for dropdowns.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event the change event.
 */
function changeEvent({ target, defaultPrevented }) {
	try {
		if (!defaultPrevented && !ignoreChange && target.matches(submitterselector) && !target.matches(triggers)) {
			fireElement(target);
		}
	} finally {
		ignoreChange = false;
	}
}

/**
 * Listens to select, deselect, collapse state changes to fire submit on change as required.
 *
 * @function
 * @private
 * @param {Element} element The element being acted upon.
 */
function shedObserver(element) {
	if (element?.matches(triggers)) {
		fireElement(element);
	}
}

/**
 * @param {Element} container
 */
function addAllWarnings(container) {
	if (container.matches(submitterselector)) {
		instance.warn(container);
	} else {
		const submitters = /** @type {NodeListOf<HTMLElement>} */(container.querySelectorAll(submitterselector));
		Array.from(submitters).forEach(next => instance.warn(next));
	}
}


initialise.register({
	/**
	 * Set up the core body listeners for submit on change.
	 * @function module:wc/ui/onchangeSubmit.initialise
	 * @public
	 * @param {Element} element The element being initialised - document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: domFocusEvent, capture: true });
		event.add(element, { type: "change", listener: changeEvent, capture: true });
		timers.setTimeout(addAllWarnings, 0, element);
	},

	/**
	 * Call to initialise this instance - wires up boostrap listeners.
	 * @function module:wc/ui/onchangeSubmit.postInit
	 * @public
	 */
	postInit: function() {
		shed.subscribe(shed.actions.SELECT, shedObserver);
		shed.subscribe(shed.actions.DESELECT, shedObserver);
		shed.subscribe(shed.actions.COLLAPSE, shedObserver);
		processResponse.subscribe(addAllWarnings, true);
	}
});

export default instance;
