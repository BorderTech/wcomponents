import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import uid from "wc/dom/uid";
import i18n from "wc/i18n/i18n";
import ajaxRegion from "wc/ui/ajaxRegion";
import processResponse from "wc/ui/ajax/processResponse";
import eagerLoader from "wc/ui/containerload";
import timers from "wc/timers";
import dialogFrame from "wc/ui/dialogFrame";
import getForm from "wc/ui/getForm";

const buttonSelector = "button";
const anchorSelector = "a";

var // OPENER = BUTTON.extend("", {"data-wc-dialogconf": null}),
	BASE_CLASS = "wc-dialog",
	registry = {},
	registryByDialogId = {},
	keepContentOnClose = false,
	openOnLoadTimer,
	openThisDialog,
	GET_ATTRIB = "data-wc-get";

/**
 * Provides "dialog" functionality.  NOTE: we currently use a custom dialog because IE native dialog does not call and
 * parse xslt (as of IE10). This is not an issue in ff3.6+ or Chrome 6 but these do not support showModelessDialog.
 *
 * The custom dialog also provides somewhat better options for mobile use and cross-platform consistency.
 *
 */
const instance = {
	/**
	 * Add the object definitions of each dialog to the registry.
	 * @function module:wc/ui/dialog.register
	 * @public
	 * @param {module:wc/ui/dialog~regObject[]} array An array of dialog definition objects.
	 */
	register: function(array) {
		if (array && array.length) {
			array.forEach(_register);
			initialise.addCallback(function() {
				for (let o in registry) {
					if (registry.hasOwnProperty(o)) {
						setHasPopup(o);
					}
				}
				if (openThisDialog) {
					if (openOnLoadTimer) {
						timers.clearTimeout(openOnLoadTimer);
					}
					openOnLoadTimer = timers.setTimeout(openDlg, 0, openThisDialog);
				}
			});
		}
	},

	/**
	 * Open a dialog for a given trigger.
	 * @function module:wc/ui/dialog.open
	 * @public
	 * @param {HTMLElement} trigger an element which _should_ be a dialog trigger.
	 * @returns {boolean} `true` if the element will trigger a dialog on change or click.
	 */
	open: function(trigger) {
		const element = getTrigger(trigger);
		if (element) {
			openDlg(element.id);
			return true;
		}
		return false;
	}
};

/**
 * Ensure a dialog trigger element has the aria-haspopup attribute.
 *
 * @param {string} id the id of the element to manipulate
 */
function setHasPopup(id) {
	const popupAttr = "aria-haspopup",
		el = document.getElementById(id);
	if (el && !el.getAttribute(popupAttr)) {
		el.setAttribute(popupAttr, "true");
	}
}

/**
 * Array.forEach function to add each dialog definition object to the registry.
 * @see module:wc/ui/dialog#register
 * @param {module:wc/ui/dialog~regObject} dialogObj The dialog dto.
 */
function _register(dialogObj) {
	const triggerId = dialogObj.triggerid || dialogObj.id;

	/**
	 * @param {string} title
	 */
	const add = function(title) {
		registry[triggerId] = {
			id: dialogObj.id,
			className: BASE_CLASS + (dialogObj.className ? (" " + dialogObj.className) : ""),
			width: dialogObj.width,
			height: dialogObj.height,
			modal: dialogObj.modal || false,
			openerId: dialogObj.triggerid,
			title: dialogObj.title || title
		};
		registryByDialogId[dialogObj.id] = triggerId;

		if (dialogObj.open) {
			openThisDialog = triggerId;
		}
	};

	if (triggerId) {
		if (dialogObj.title) {
			add(dialogObj.title);
		} else {
			i18n.translate("dialog_noTitle").then(add);  // This is called too early for a synchronous i18n call
		}
	}
}

/**
 * Is this element inside the dialog content?
 * @function
 * @private
 * @param {string} id The id of the element to test.
 * @return {boolean} If this ID is within the dialog content.
 */
function isInsideDialog(id) {
	const content = id ? dialogFrame.getContent() : null;
	const element = content ? document.getElementById(id) : null;
	if (element) {
		return !!(content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY);
	}
	return false;
}

/**
 * Find a dialog opener from a given start point.
 *
 * @function
 * @private
 * @param {HTMLElement} element the start element
 * @param {boolean} [ignoreAncestor] if `true` then stop without checking ancestors for a trigger
 * @returns {HTMLElement} a dialog trigger element if found
 */
function getTrigger(element, ignoreAncestor) {
	if (!element || element instanceof HTMLFormElement) {
		return null;
	}
	const id = element.id;
	if (id) {
		let regObj = registry[id];
		if (regObj) {
			if (regObj.id === id) {
				// Auto open on load dialogs are their own trigger
				return null;
			}
			return element;
		}
	}
	if (ignoreAncestor) {
		return null;
	}
	return getTrigger(element.parentElement, false);
}

/**
 * We need to know if an element is a submit element so that we can prevent the submit action if it opens a dialog.
 * @function
 * @private
 * @param {HTMLElement} element the element to test
 * @returns {boolean} `true` if the element is a submitting element
 */
function isSubmitElement(element) {
	const submitters = ["[type='submit']", "[type='image']", "button:not([type])"].join();
	return element.matches(submitters);
}

/**
 * Action click events on a dialog trigger or within a dialog.
 * @function
 * @private
 * @param {HTMLElement} element The element which was clicked.
 * @returns {boolean} {@code true} if the click is activated and we _may_ want to prevent the default action
 */
function activateClick(element) {
	// Are we opening a dialog?
	let _element = getTrigger(element);
	if (_element && !isInsideDialog(element.id)) {
		if (shed.isDisabled(_element)) {  // This is needed because IE is broken, and we have a potential race with the global fix.
			return false;
		}
		instance.open(_element);
		return isSubmitElement(_element);
	}
	const dialog = dialogFrame.getDialog();
	const content = !dialog || shed.isHidden(dialog, true) ? null : dialogFrame.getContent();
	if (!(content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY)) {
		// we are not inside a dialog's content.
		return false;
	}

	keepContentOnClose = false;
	// we need to know if a click is on an ajax trigger inside a dialog
	let trigger = ajaxRegion.getTrigger(element, true);
	if (!trigger) {
		// this is a chrome thing: it honours clicks on img elements and does not pass them through to the underlying link/button
		_element = element.closest(`${buttonSelector},${anchorSelector}`);
		if (_element) {
			trigger = ajaxRegion.getTrigger(_element, true);
		}
	}

	if (!trigger) {
		return false;
	}
	const targets = trigger.loads;

	if (targets && targets.length && !targets.some(isInsideDialog)) {
		keepContentOnClose = true;
		dialogFrame.close();
	}
	return false;
}

/**
 * Open a dialog.
 * @function
 * @private
 * @param {string} triggerId The id of the trigger.
 */
function openDlg(triggerId) {
	const populateOnLoad = () => {
		const content = dialogFrame.getContent();
		if (content) {
			content.id = regObj.id;
			const openerId = !(openThisDialog && openThisDialog === triggerId) ? regObj.openerId : "";
			if (openerId) {
				const opener = document.getElementById(openerId);
				content.setAttribute(GET_ATTRIB, `${openerId}=${opener ? encodeURIComponent(opener.value) : "x"}`);
			} else {
				content.removeAttribute(GET_ATTRIB);
			}
			content.classList.add("wc_magic");
			content.classList.add("wc_dynamic");
			eagerLoader.load(content, false, false);
		} else {
			console.warn("Could not find dialog content wrapper.");
		}
		openThisDialog = null;
	};

	const regObj = registry[triggerId];
	if (regObj) {
		if (!regObj.formId) {
			const trigger = document.getElementById(triggerId);
			const form = trigger ? getForm(trigger) : null;
			if (form) {
				regObj["formId"] = form.id || (form.id = uid());
			}
		}
		dialogFrame.open(regObj).then(populateOnLoad).catch(function(err) {
			console.warn(err);
			openThisDialog = null; // belt **and** braces
		});
	}
}

/**
 * Set the focus into the dialog after the AJAX malarkey has finished. This looks at the dialog content
 * component which is an unnamed wrapper in the Java side so will ONLY grab focus when a dialog is opened and
 * will not continually grab it each time an ajax action occurs inside a dialog even if the target is the
 * WComponent (most commonly a WPanel) which is used as the "content" of the dialog. Tricky that.
 *
 * @param {HTMLElement} element The root element from the ajax response.
 */
function postOpenSubscriber(element) {
	if (element?.id && element === dialogFrame.getContent()) {
		const regObj = getRegistryObjectByDialogId(element.id);
		if (regObj) {
			// set the initial position
			if (!(regObj.top || regObj.left || regObj.top === 0 || regObj.left === 0)) {
				dialogFrame.reposition({ width: regObj.width, height: regObj.height });
			}
		}
	}
}

/**
 * Get a registry object based on a WDialog id attribute.
 * @param {String} id the ID of the WDialog to get.
 * @returns {module:wc/ui/dialog~regObject} the registry object if found.
 */
function getRegistryObjectByDialogId(id) {
	const triggerId = registryByDialogId[id];
	if (triggerId) {
		return registry[triggerId];
	}
	return null;
}

/**
 * Listen for shed.hide and clear out the transient aspects of the dialog.
 *
 * @function
 * @private
 * @param {CustomEvent} $event A shed hide event.
 */
function shedSubscriber({ target }) {
	const content = (target && target === dialogFrame.getDialog()) ? dialogFrame.getContent() : null;
	const id = content?.id;
	const regObj = id ? getRegistryObjectByDialogId(id) : null;
	if (regObj) {  // we are ONLY interested in WDialog inited dialogs.
		try {
			dialogFrame.unsetAllDimensions();
			dialogFrame.resetContent(keepContentOnClose, (keepContentOnClose ? "" : regObj.id));
		} finally {
			keepContentOnClose = false;
		}
	}
}

/**
 * Click listener for dialog opening buttons and controls within a dialog.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event a click event.
 */
function clickEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	if (activateClick($event.target)) {
		$event.preventDefault();
	}
}

initialise.register({
	/**
	 * Component initialisation.
	 * @param {HTMLBodyElement} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, "click", clickEvent);
		event.add(element, shed.events.HIDE, shedSubscriber);
	},

	/**
	 * Late initialisation.
	 */
	postInit: function() {
		processResponse.subscribe(postOpenSubscriber, true);
	}
});

/**
 * @typedef {Object} module:wc/ui/dialog~regObject An object which stores information about a dialog.
 * @property {String} id The WDialog id.
 * @property {String} [triggerid] The id of the control that opens the dialog.
 * @property {number} [width] The dialog width in px.
 * @property {number} [height] The dialog height in px.
 * @property {number} [top] The dialog top position.
 * @property {number} [left] The dialog left position.
 * @property {Boolean} [modal] Is the dialog modal?
 * @property {String} [title] The WDialog title. If not set a default title is used.
 * @property {String} [className] Additional WDialog css class (will be appended to base class).
 * @property {Boolean} [open] If true then the dialog is to be open on page load. This is passed in as part of
 *    the registration object but is not stored in the registry.
 */
