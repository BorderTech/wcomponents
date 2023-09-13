/**
 * This module is responsible for working out which elements should trigger an AJAX request and when.
 *
 * The response processing is in {@link module:wc/ui/ajax/processResponse} to solve some unpleasant circular dependencies and potential races.
 *
 */

import event from "wc/dom/event.mjs";
import debounce from "wc/debounce.mjs";
import isSuccessfulElement from "wc/dom/isSuccessfulElement.mjs";
import Trigger from "wc/ajax/Trigger.mjs";
import triggerManager from "wc/ajax/triggerManager.mjs";
import shed from "wc/dom/shed.mjs";
import initialise from "wc/dom/initialise.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import mixin from "wc/mixin.mjs";
import timers from "wc/timers.mjs";

let triggers = [];
let ignoreChange = false;

const instance = {
	/**
	 * Register an ajax trigger. This method is a wrapper for {@link module:wc/ajax/triggerManager#addTrigger} and UI controls will usually
	 * use this module rather than going straight to the trigger manager module.
	 * @see {@link module:wc/ajax/triggerManager}
	 * @function module:wc/ui/ajaxRegion.register
	 * @public
	 * @param {Object} obj The registration object
	 */
	register: function (obj) {
		const _register = (next) => {
			const trigger = new Trigger(next, processResponse.processResponseXml, processResponse.processError),
				delay = next.delay,
				triggerId = next.id;
			triggerManager.addTrigger(trigger);
			triggers.push(triggerId);
			if (delay) {
				initialise.addCallback(() => timers.setTimeout(fireAfterDelay, delay, triggerId));
			}
			return trigger;
		};
		if (Array.isArray(obj)) {
			obj.forEach(_register);
		} else {
			_register(obj);
		}
	},

	/**
	 * Get an ajax trigger associated with an element or id.
	 * @function module:wc/ui/ajaxRegion.getTrigger
	 * @public
	 * @param {String|Element} arg The ID of the trigger to retrieve OR a DOM element which may be associated with a trigger.
	 * @param {boolean} [ignoreAncestor] If true will not search in DOM ancestry for an element with a trigger.
	 * @returns {module:wc/ajax/Trigger} The trigger, if found.
	 * @see {@link module:wc/ajax/triggerManager#getTrigger} for full details.
	 */
	getTrigger: function(arg, ignoreAncestor) {
		return triggerManager.getTrigger(arg, ignoreAncestor);
	},

	/**
	 * Register and fire an ajaxTrigger only when required. This is used when we do not want to fire an
	 * ajaxTrigger on change or click (eg WShuffler, WMultiSelectPair) but in some other circumstance.
	 * @function module:wc/ui/ajaxRegion.requestLoad
	 * @public
	 * @param {Element} element The element which is being changed.
	 * @param {Object} [obj] A trigger definition dto.
	 * @param {Boolean} [ignoreAncestor] Indicates to not look up the tree when trying to find a trigger.
	 */
	requestLoad: function(element, obj, ignoreAncestor) {
		let trigger = triggerManager.getTrigger(element, ignoreAncestor);

		if (!trigger) {
			if (obj) {
				this.register(obj);
			} else {
				const id = element.id;
				const alias = element.getAttribute("data-wc-ajaxalias");
				const controls = element.getAttribute("aria-controls");
				const loads = controls ? controls.split(" ") : [id];
				this.register({ id, loads, alias });
			}

			trigger = triggerManager.getTrigger(element);
		} else if (obj) {
			mixin(obj, trigger);  // QC158630
		}

		if (trigger) {
			fireThisTrigger(element, trigger);
		}
	},

	/**
	 * Allow external scripts to clear their own prevent submit on next change event before any change event is fired.
	 * @function module:wc/ui/ajaxRegion.clearIgnoreChange
	 * @public
	 */
	clearIgnoreChange: () => ignoreChange = false,

	/**
	 * Set a flag to ignore a change event, useful if doing a lot of changes and only want to fire one AJAX request.
	 * @function  module:wc/ui/ajaxRegion.ignoreNextChange
	 * @public
	 */
	ignoreNextChange: () => ignoreChange = true
};

function fireThisTrigger(element, trigger) {
	let result = false;
	if (trigger) {
		if (trigger.successful === null) {
			result = true;
		} else {
			const isSuccessful = isSuccessfulElement(element, true);
			if ((trigger.successful === true && isSuccessful) || (trigger.successful === false && !isSuccessful)) {
				result = true;
			}
		}
	}
	if (result) {
		trigger._submitTriggerElement = true;
		// the trigger may still decide not to fire (eg all shots are used up)
		trigger.fire();
	}
	return result;
}

/**
 * Checks if an element is an ajax trigger and if so fires it.
 *
 * @function
 * @private
 * @param {Element} element The element we consider a candidate for being an AJAX trigger. If the element is indeed an AJAX trigger then
 * it will be fired by this function.
 */
function checkActivateTrigger(element) {
	const trigger = instance.getTrigger(element);
	return trigger ? fireThisTrigger(element, trigger) : false;
}

/**
 * Is an element a form submitting element?
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {Boolean} true if the element is a type that submits a form when clicked (ie a submit button).
 */
function isSubmitElement(element) {
	const selectors = ["input[type='submit']", "input[type='submit']", "button[type='submit']","button:not([type])"];
	return element?.matches(selectors.join());
}

/**
 *
 * @param {CustomEvent & { target: Element }} $event
 */
function shedSubscriber($event) {
	const selectors = ["input[type='checkbox']", "input[type='radio']"];
	if ($event.target?.matches(selectors.join())) {
		checkActivateTrigger($event.target);
	}
}

/**
 * Does an element trigger an ajax request when it changes? Some elements should not do ajax stuff on click,
 * instead it makes sense for them to use the change event.
 *
 * @function
 * @private
 * @param {Element} element The element to check whether it does ajax on change.
 * @returns {Boolean} true if this element should ajax on change.
 */
function triggersOnChange(element) {
	const triggersOnChangeSelectors = ["select", "textarea", "input:not([type='file'])"];
	// NOTE: a standalone listbox or dropdown is an ajax trigger, a select element as a sub element of a compund controller is not
	if (shed.isSelectable(element) || element.classList.contains("wc-noajax")) {
		return false;
	}
	// Don't allow file to trigger on change it breaks multiFileUploader when large number of files are selected
	return element.matches(triggersOnChangeSelectors.join());
}

/**
 * @param {MouseEvent & { target: Element }} $event An event
 */
function clickEvent($event) {
	const { defaultPrevented, target } = $event;
	if (!defaultPrevented) {
		const element = target.closest(["button", "a"].join());
		if (element && !shed.isDisabled(element) && checkActivateTrigger(element) &&
			(isSubmitElement(element) || isNavLink(element))) {
			$event.preventDefault();
		}
	}
}

/*
 * Some elements should fire trigger on change event, for example SELECT elements.
 * @param {Event} $event An event
 */
function changeEvent($event) {
	if (ignoreChange) {
		return;
	}
	checkActivateTrigger($event.target);
}

/**
 * Focus event listener adds a change event to triggers which trigger on change.
 *
 * @function
 * @private
 * @param {Event & {target: Element}} $event A focus event.
 */
function focusEvent($event) {
	const INITED_FLAG = "wc.ui.ajaxRegion.inited";
	const element = $event.target;
	if (!$event.defaultPrevented && !element[INITED_FLAG] && triggersOnChange(element)) {
		element[INITED_FLAG] = true;
		event.add(element, "change", debounce(changeEvent, 250), 100);
	}
}

/**
 * Check whether this element is an anchor element itself OR if it is nested within an anchor element.
 * Only returns true if the link will navigate the page. If it is a link that will target another frame
 * or window, or will launch an external app (via a custom protocol e.g. mailto:) then it will return
 * false.
 *
 * @function
 * @private
 * @param {Element} element
 * @returns {boolean} true if the element is a link which will navigate the page
 */
function isNavLink(element) {
	let result = false;
	const PSEUDO_PROTOCOL_RE = /^\w+:[^/].*$/;
	/** @type {HTMLAnchorElement} */
	const link = element.closest("a:not([aria-haspopup='true']):not([target])");
	if (link && !PSEUDO_PROTOCOL_RE.test(link.href)) {
		result = true;
	}
	return result;
}

/**
 * Accessibility helper. Any target of an ajax trigger should be marked as an aria-live region.
 * @function
 * @private
 */
function setControlsAttribute() {
	if (triggers && triggers.length) {
		try {
			triggers.forEach(function(next) {
				const trigger = triggerManager.getTrigger(next);
				if (!trigger) {
					return;
				}
				const controllerId = trigger.alias || trigger.id;
				const controller = controllerId ?  document.getElementById(controllerId) : null;
				const loads = trigger.loads;
				if (controller && loads.length) {
					controller.setAttribute("aria-controls", loads.join(" "));
					loads.map(id => document.getElementById(id)).forEach(el => {
						el.setAttribute("aria-live", "polite");
					});
				}
			});
		} finally {
			triggers = [];
		}
	}
}

/**
 * Fire a delayed trigger.
 * @function
 * @private
 * @param {String} triggerId the ID of the trigger to fire
 */
function fireAfterDelay(triggerId) {
	const trigger = triggerManager.getTrigger(triggerId);
	if (trigger) {
		try {
			trigger.method = trigger.METHODS.GET;
			trigger.serialiseForm = false;
			trigger.oneShot = 1;
			trigger.fire();
		} catch (ex) {
			console.log("error in delayed ajax trigger for id " + triggerId, ex.message);
		}
	}
}

initialise.register({
	/**
	 * Set up event and {@link module:wc/dom/shed} subscribers.
	 * @function module:wc/ui/ajaxRegion.initialise
	 * @public
	 * @param {Element} element document body.
	 */
	initialise: function(element) {
		event.add(element, "click", clickEvent, 50); // Trigger ajax AFTER other events to avoid submitting form fields before they can be updated.
		if (event.canCapture) {
			event.add(element, { type: "focus", listener: focusEvent, capture: true });
		} else {
			event.add(element, "focusin", focusEvent);
		}
		console.log("Initialising trigger listeners");
	},

	/**
	 * Late initialisation. We set the aria-live regions late to give everything time to register its triggers.
	 * @function
	 * @public
	 */
	postInit: function() {
		setControlsAttribute();
		processResponse.subscribe(setControlsAttribute, true);
		event.add(document.body, shed.events.SELECT, shedSubscriber);
		event.add(document.body, shed.events.DESELECT, shedSubscriber);
	},
});

export default instance;
