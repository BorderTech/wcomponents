import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import cbrShedPublisher from "wc/dom/cbrShedPublisher.mjs";

const CONTROLS = "aria-controls";
const checkboxSelector = cbrShedPublisher.getWidget("cb").toString();
const wrapperSelector = ".wc-checkbox";
const wCheckboxSelector = `${wrapperSelector} > ${checkboxSelector}`;

/**
 * when a checkBox is added using AJAX it may need to find out if it is controlled and if so add its ID to
 * the controllers' aria-controls attribute.
 *
 * @function
 * @private
 * @param {Element} element The reference element (element being replaced).
 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
 */
function ajaxSubscriber(element, documentFragment) {
	const GROUP_ATTRIB = "data-wc-cbgroup";

	if (element && !element.matches(checkboxSelector)) {  // can only replace like-for-like and checkboxes have no content
		const checkboxes = Array.from(documentFragment.querySelectorAll(checkboxSelector));
		checkboxes.forEach(_el => {
			const { id: myId, type } = _el;
			let localController = `[aria-controls='${myId}']`;

			// we are only interested in ui:checkbox which have a groupName
			// if I existed in the document prior to ajax I do not need to do anything
			if (type === "checkbox" && !document.getElementById(myId) && !document.body.querySelector(localController)) {
				// ok, so we need to get a handle on other checkboxes with my group name
				const myGroupName = _el.getAttribute(GROUP_ATTRIB);
				const refElementWd = `[data-wc-cbgroup='${myGroupName}']`;
				const refElement = document.body.querySelector(refElementWd);
				if (refElement) {
					localController = `[${CONTROLS}='${refElement.id}']`;
					const controllers = Array.from(document.body.querySelectorAll(localController));
					controllers.forEach(ensureControls(myId));
				}
			}
		});
	}
}

/**
 * Returns a curried helper function for `ajaxSubscriber`.
 * @param myId
 * @return {(function(HTMLElement): void)|*}
 */
function ensureControls(myId) {
	return next => {
		let controlled = next.getAttribute(CONTROLS);
		if (controlled) {
			controlled = controlled.split(/\s+/);
			if (controlled.indexOf(myId) === -1) {
				controlled.push(myId);
				controlled = controlled.join(" ");
				next.setAttribute(CONTROLS, controlled);
			}
		} else {
			next.setAttribute(CONTROLS, myId);
		}
	};
}

/**
 * This is a writeState for standalone WCheckBox elements (not part of a WCheckBoxSelect) which are not
 * checked.
 * TODO: get rid of this one way or another it should never have been written.
 *
 * @function
 * @private
 * @param {Element} form The form or form segment which is having its state written.
 * @param {Element} container The HTML element into which the state is written.
 */
function writeState(form, container) {
	const checkboxes = Array.from(
		form.querySelectorAll(wCheckboxSelector)
	).filter(next => !(shed.isSelected(next) || shed.isDisabled(next)));
	checkboxes.forEach(next => formUpdateManager.writeStateField(container, next.name, ""));
}

/**
 * Provides Ajax and state writing functionality for check boxes.
 * @todo Get rid of the state writing: it is nuts!
 */
const instance = {
	/**
	 * Provides the description of a CHECKBOX.
	 * @function module:wc/ui/checkbox.getWidget
	 * @public
	 * @param {boolean} [onlyWcb] if `true` return the Widget to explicitly match WCheckBox
	 * @returns {string} the description of a CHECKBOX; or WCHECKBOX is onlyWcb is `truthy`.
	 */
	getWidget: (onlyWcb) => onlyWcb ? wCheckboxSelector : checkboxSelector,

	/**
	 * Provides the description of a WCheckBox wrapper element
	 * @function module:wc/ui/checkbox.getWrapper
	 * @public
	 * @returns {string}
	 */
	getWrapper: () => wrapperSelector
};

initialise.register({
	/**
	 * Wire up subscribers after initialisation.
	 * @function module:wc/ui/checkbox.postInit
	 * @public
	 */
	postInit: () => {
		formUpdateManager.subscribe(writeState);
		processResponse.subscribe(ajaxSubscriber);
	},

	/**
	 * Unsubscribes event listeners etc.
	 */
	deinit: () => {
		formUpdateManager.unsubscribe(writeState);
		processResponse.unsubscribe(ajaxSubscriber);
	}
});

export default instance;
