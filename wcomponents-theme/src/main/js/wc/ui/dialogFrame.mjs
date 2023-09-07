import event from "wc/dom/event";
import focus from "wc/dom/focus";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import uid from "wc/dom/uid";
import i18n from "wc/i18n/i18n";
import processResponse from "wc/ui/ajax/processResponse";
import modalShim from "wc/ui/modalShim";
import debounce from "wc/debounce";
import resizeable from "wc/ui/resizeable";
import positionable from "wc/ui/positionable";
import draggable from "wc/ui/draggable";
import $role from "wc/dom/role";
import viewportUtils from "wc/ui/viewportUtils";
import getForm from "wc/ui/getForm";
import wcconfig from "wc/config";

const subscriber = {
		close: null
	},
	UNIT = "px",
	// when setting the initial position offset the dialog so that the gap at the top is this proportion of the difference between
	// the dialog size and viewport size
	INITIAL_TOP_PROPORTION = 0.33;
const DIALOG_ID = "wc_dlgid";
const CONTENT_BASE_CLASS = "content";
const dialogSelector = "dialog";
const dialogContentWrapperSelector = `${dialogSelector} > div.${CONTENT_BASE_CLASS}[aria-live='assertive']`;
const buttonSelector = "button";
const closeSelector = `${buttonSelector}.wc_dialog_close`;
const headerSelector = "header";
const titleSelector = `${headerSelector} h1`;
const resizeSelectors = resizeable.getWidget();
const resizeSelector = resizeSelectors.handle;
const maxbuttonSelector = resizeSelectors.maximise;
const busySelector = "[aria-busy='true']";
const REJECT = {
	ALREADY_OPEN: "Cannot open a dialog whilst another dialog is open",
	CANNOT_BUILD: "Cannot create the dialog frame",
	NO_FORM: "Cannot find a form to which to attach the dialog",
	UNKNOWN: "Failed to open dialog: reason unknown"
};

const template = context => `
	<dialog id="${DIALOG_ID}" role="dialog">
		<header tabindex="0">
			<span>
				<button class="wc_maxcont wc_btn_icon" type="button" title="${context.heading.maxRestore}" aria-pressed="false" data-wc-resize="${DIALOG_ID}"><i aria-hidden="true" class="fa fa-plus"></i></button>
				<button class="wc_dialog_close wc_btn_icon" type="button" title="${context.heading.close}"><i aria-hidden="true" class="fa fa-times"></i></button>
			</span>
			<h1>&#x2002;</h1>
		</header>
		<div class="content" aria-live="assertive">
			${context.message.loading}
		</div>
		<footer>
			<button class="wc-nobutton wc_resize" data-wc-resize="${DIALOG_ID}" type="button"><i aria-hidden="true" class="fa fa-arrows-alt"></i></button>
		</footer>
	</dialog>`;

const instance = {
	/**
	 * Get a dialog if one exists.
	 * @function  module:wc/ui/dialogFrame.getDialog
	 * @public
	 * @returns {HTMLDialogElement} The dialog.
	 */
	getDialog: function() {
		return /** @type {HTMLDialogElement} */ (document.getElementById(DIALOG_ID));
	},

	/**
	 * Request a dialog be opened.
	 *
	 * @function module:wc/ui/dialogFrame.open
	 * @public
	 * @param {module:wc/ui/dialogFrame~dto} dto The config options for the dialog to be opened.
	 * @returns {Promise} The promise will be a rejection if the dialog is not able to be opened.
	 */
	open: function (dto) {
		const dialog = instance.getDialog();
		let form;

		if (dialog) {
			if (!this.isOpen(dialog)) {
				return openDlgHelper(dto);
			}
			return Promise.reject(REJECT.ALREADY_OPEN);
		} else if ((form = getDlgForm(dto))) {
			const formId = form.id || (form.id = uid());

			if (formId) {
				return buildDialog(formId).then(function () {
					return openDlgHelper(dto);
				});
			}
			return Promise.reject(REJECT.NO_FORM);
		}
		return Promise.reject(REJECT.UNKNOWN);
	},

	/**
	 * Determine if the dialog is already open.
	 * @param {HTMLDialogElement} [element] Optionally provide the dialog element.
	 * @returns {boolean} true if the dialog is open.
	 */
	isOpen: function (element) {
		const dialog = element || instance.getDialog();
		return (dialog && !shed.isHidden(dialog, true));
	},

	/**
	 * Remove all inline dimension styles from the dialog.
	 *
	 * @function module:wc/ui/dialogFrame.unsetAllDimensions
	 * @public
	 * @param {HTMLDialogElement} [dlg] The dialog wrapper element if known.
	 */
	unsetAllDimensions: function (dlg) {
		const dialog = dlg || instance.getDialog();
		if (dialog) {
			dialog.style.width = "";
			dialog.style.height = "";
			dialog.style.margin = "";
			positionable.clear(dialog);
		}
	},

	reposition: debounce(/**
		 * Ask to reposition a dialog frame (usually after Ajax).
		 *
		 * @function module:wc/ui/dialogFrame.reposition
		 * @public
		 * @param {number} [width] The width of the dialog.
		 * @param {number} [height] The height of the dialog.
		 * @param {boolean} [animate]
		 */
		(width, height, animate) => {
			const dialog = instance.getDialog();
			if (dialog && canMoveResize()) {
				setPositionBySize(dialog, getResizeConfig(width, height), animate);
			}
		}, 100),

	/**
	 * Close a dialog frame.
	 * @function module:wc/ui/dialogFrame.close
	 * @public
	 * @returns {boolean} true if there is a dialog to hide.
	 */
	close: function () {
		const dialog = instance.getDialog();
		if (dialog && this.isOpen(dialog)) {
			shed.hide(dialog);
			return true;
		}
		return false;
	},

	/**
	 * Get the widget which describes a dialog frame.
	 * @function module:wc/ui/dialogFrame.getWidget
	 * @public
	 * @returns {string} The selector describing a dialog frame.
	 */
	getWidget: function () {
		return dialogSelector;
	},

	/**
	 * Get the dialog content wrapper element.
	 *
	 * @function module:wc/ui/dialogFrame.getContent
	 * @public
	 * @returns {HTMLDialogElement} The content wrapper if present.
	 */
	getContent: function () {
		const dialog = instance.getDialog();
		if (dialog) {
			return dialog.querySelector(dialogContentWrapperSelector);
		}
		return null;
	},

	/**
	 * Reset the dialog content wrapper.
	 *
	 * @function module:wc/ui/dialogFrame.resetContent
	 * @public
	 * @param {Boolean} [keepContent] Do we want to reset the content of the dialog?
	 * @param {String} [id] The id to set on the content.
	 */
	resetContent: function (keepContent, id) {
		const content = this.getContent();

		if (content) {
			content.removeAttribute("data-wc-get");
			content.id = id || "";
			content.className = CONTENT_BASE_CLASS;

			if (!keepContent) {
				i18n.translate("loading").then(loadingText => content.innerHTML = loadingText);
			}
		}
	}
};

/**
 * Indicates that the dialog is modal.
 * @function
 * @private
 * @param {HTMLDialogElement} dialog the dialog element to test
 * @returns {Boolean} true if the dialog is modal.
 */
function isModalDialog(dialog) {
	return $role.get(dialog) === "alertdialog";
}

/**
 * Indicates if the dialogFrame may support move and resize based on viewport size.
 *
 * @function
 * @private
 * @returns {Boolean} true is move/resize are supportable.
 */
function canMoveResize() {
	const conf = wcconfig.get("wc/ui/dialogFrame", {
		vpUtil: null
	});
	let func = "isPhoneLike";
	if (conf.vpUtil && viewportUtils[conf.vpUtil]) {
		func = conf.vpUtil;
	}
	return !viewportUtils[func]();
}

/**
 * @function
 * @private
 * @returns {Boolean} true if the dialog has any content in an aria-busy state.
 */
function hasBusyContent() {
	const content = instance.getContent();
	if (content) {
		return !!content.querySelector(busySelector);
	}
	return false;
}

/**
 * Get the form into which we want to place the dialog.
 *
 * @function
 * @private
 * @param {module:wc/ui/dialogFrame~dto} [dto] The config options for the dialog (if any).
 * @returns {HTMLFormElement} The form element.
 */
function getDlgForm(dto) {
	const formId = (dto ? (dto.formId || dto.openerId) : null);
	const el = formId ? document.getElementById(formId) : null;
	return getForm(el);
}

/**
 * Helper for `openDlg`.
 * This does the actual heavy lifting of opening a dialog.
 *
 * @param dto The configuration data for this dialog.
 * @private
 * @function
 */
function openDlgHelper(dto) {
	return i18n.translate("dialog_noTitle").then(function(defaultTitle) {
		const effectiveDto = dto || {},
			dialog = instance.getDialog();

		if (dialog && !instance.isOpen(dialog)) {
			if (effectiveDto.openerId) {
				instance._openerId = effectiveDto.openerId;
			} else {
				instance._openerId = document.activeElement ? document.activeElement.id : null;
			}
			if (!effectiveDto.title) {
				effectiveDto.title = defaultTitle;
			}
			reinitializeDialog(dialog, effectiveDto);
			// show the dialog
			shed.show(dialog);
			initDialogPosition(dialog, dto);  // deliberately didn't use effectiveDto here to preserve behavior
			return true;
		}
		return false;
	});
}

/**
 * Helper for `openDlg`.
 * Applies the dialog "mode" either modal or non-modal.
 *
 * @param {HTMLDialogElement} dialog The dialog container.
 * @param {boolean} isModal Indicates if this dialog is modal.
 * @private
 * @function
 */
function setModality(dialog, isModal) {
	if (isModal) {
		dialog.setAttribute("role", "alertdialog");
		modalShim.setModal(dialog);
	} else {
		dialog.removeAttribute("role");
	}
}

/**
 * Helper for `openDlg`.
 * Once the dialog has been built it needs to be configured each time it is opened.
 * For example the correct title must be displayed for this specific dialog. CSS classes may need to be set, removed etc.
 *
 * @private
 * @function
 * @param {HTMLDialogElement} dialog The dialog container.
 * @param {module:wc/ui/dialogFrame~dto} obj The registry item that contains configuration data for this dialog.
 */
function reinitializeDialog(dialog, obj) {
	instance.unsetAllDimensions(dialog);
	dialog.className = (obj.className || "");
	instance.resetContent(false, (obj.id  || ""));
	// set the dialog title
	const title = dialog.querySelector(titleSelector);
	if (title) {
		title.innerHTML = obj.title;
	}
	subscriber.close = obj.onclose;
	initDialogControls(dialog, obj);
	initDialogDimensions(dialog, obj);
	const isModal = (typeof obj.modal !== "undefined") ? obj.modal : true;
	setModality(dialog, isModal);
}

/**
 * Show and hide resizeable and draggable controls based on the dialogFrame's properties and the current
 * viewport size.
 *
 * @function
 * @private
 * @param {HTMLDialogElement} dialog The dialogFrame being manipulated.
 */
function setUpMoveResizeControls(dialog) {
	const control = dialog.querySelector(`:scope > ${headerSelector}`);

	if (control) {
		if (canMoveResize()) {
			draggable.makeDraggable(control, DIALOG_ID);
			resizeable.setMaxBar(control);
			resizeable.makeAnimatable(dialog);
		} else {
			resizeable.clearAnimatable(dialog);
			draggable.clearDraggable(control);
			resizeable.clearMaxBar(control);
		}

	}
}

/**
 * Manipulate positionable and resizeable attributes based on viewport size.
 *
 * @function
 * @private
 * @param {HTMLDialogElement} dialog The dialogFrame being manipulated.
 */
function setUnsetDimensionsPosition(dialog) {
	let animationsDisabled;
	try {
		if (canMoveResize()) {
			resizeable.resetSize(dialog);
		} else {
			resizeable.disableAnimation(dialog);
			animationsDisabled = true;
			resizeable.clearSize(dialog, true);
		}
	} finally {
		if (animationsDisabled) {
			resizeable.restoreAnimation(dialog);
		}
	}
}

/**
 * Helper for `openDlg`.
 * Sets the dialog's width and height ready for opening.
 *
 * @private
 * @function
 * @param {HTMLDialogElement} dialog The dialog container.
 * @param {module:wc/ui/dialogFrame~dto} obj The configuration settings for this dialog.
 */
function initDialogDimensions(dialog, obj) {
	if (obj.width) {
		dialog.style.width = obj.width + UNIT;
	}
	if (obj.height) {
		dialog.style.height = obj.height + UNIT;
	}
	if (obj.top || obj.top === 0) {
		dialog.style.top = obj.top + UNIT;
	}
	if (obj.left || obj.left === 0) {
		dialog.style.left = obj.left + UNIT;
		dialog.style.margin = "0";
	}
	setUnsetDimensionsPosition(dialog);
}

/**
 * @param {number} width
 * @param {number} height
 * @return {{width: number, height: number, topOffsetPC: number}}
 */
function getResizeConfig(width, height) {
	const globalConf = wcconfig.get("wc/ui/dialogFrame", {});
	let offset = INITIAL_TOP_PROPORTION;

	if (globalConf.offset) {
		if (isNaN(globalConf.offset)) {
			console.log("Offset must be a number, what are you playing at?");
		} else if (globalConf.offset <= 0) {
			console.log("Offset must be greater than zero otherwise dialogs will be above the top of the screen.");
		} else if (globalConf.offset >= 1) {
			console.log("Offset must be less than one otherwise dialogs will be below the bottom of the screen.");
		} else {
			offset = globalConf.offset;
		}
	}
	return { width, height, topOffsetPC: offset };
}

/**
 * Helper for `openDlg`.
 * Positions the dialog immediately after it has been opened.
 *
 * @private
 * @function
 * @param {HTMLDialogElement} dialog The dialog container.
 * @param {module:wc/ui/dialogFrame~dto} obj The registry item that contains configuration data for this dialog.
 */
function initDialogPosition(dialog, obj) {
	let disabledAnimations;
	try {
		if (obj) {
			const configObj = getResizeConfig(obj.width, obj.height);
			// set the initial position. If the position (top, left) is set in the config object we do not need to calculate position.
			if (!((obj.top || obj.top === 0) && (obj.left || obj.left === 0))) {
				if (canMoveResize()) {
					resizeable.disableAnimation(dialog);
					disabledAnimations = true;
					positionable.setBySize(dialog, configObj);
				} else {
					positionable.storePosBySize(dialog, configObj);
					positionable.clear(dialog);
				}
			}
		}
	} finally {
		if (disabledAnimations) {
			resizeable.restoreAnimation(dialog);
		}
	}
}

/**
 * Builds the dialog shell when required. This is only called once when the first dialog is first opened.
 * @function
 * @private
 * @param {String} [formId] The id of the WApplication (HTML FORM) to which the dialog belongs.
 * @returns {Promise<HTMLDialogElement>} resolved with the dialog element.
 */
function buildDialog(formId) {
	return new Promise(function(win, lose) {
		i18n.translate(["dialog_maxRestore", "dialog_close", "loading", "dialog_move", "dialog_resize"]).then(function(translations) {
			const done = function () {
					const dialog = instance.getDialog();
					if (dialog) {
						event.add(dialog, "keydown", keydownEvent);
						/** @type {HTMLElement} */
						const dialogHeader = dialog.querySelector(`:scope > ${headerSelector}`);
						const headerTitle = dialogHeader ? translations[3] : "";
						if (headerTitle) {
							dialogHeader.title = headerTitle;
						}
						const resizeHandle = dialog.querySelector(resizeSelector);
						const resizeHandleTitle = translations[4];
						if (resizeHandle && resizeHandleTitle) {
							resizeHandle.title = resizeHandleTitle;
						}
						win(dialog);
					} else {
						lose(null);
					}
				},
				dialogProps = {
					heading: {
						maxRestore: translations[0],
						close: translations[1]
					},
					message: {
						loading: translations[2]
					}
				};
			let form = formId ? document.getElementById(formId) : null;
			form = getForm(form);
			if (!form) {
				console.error("Cannot find form for dialog frame");
				lose(null);
				return null;
			}
			try {
				const html = template(dialogProps);
				form.insertAdjacentHTML("beforeend", html);  // yep, beforeend, not beforeEnd
				done();
			} catch (ex) {
				lose();
			}
		});
	});
}

/**
 * If a dialog with content is inserted via ajax we have to unshim any existing dialog before we insert the
 * new one. NOTE: the duplicate id check in processResponse will remove the dialog itself during its insert
 * phase, so we do not have to do that here.
 *
 * @function
 * @private
 * @param {Element} element Not used here.
 * @param {DocumentFragment} docFragment The content of the AJAX response.
 */
function preOpenSubscriber(element, docFragment) {
	let removeShim = false;
	if (docFragment.querySelector) {
		if (docFragment.querySelector("#" + DIALOG_ID)) {
			removeShim = true;
		}
	} else if (docFragment.getElementById && instance.getDialog()) {
		removeShim = true;
	}
	let dialog;
	if (removeShim && (dialog = instance.getDialog()) && instance.isOpen(dialog)) {
		modalShim.clearShim(dialog);
	}
}

/**
 * If there is an AJAX replace inside a dialog we may need to reposition the dialog.
 *
 * @function
 * @private
 * @param {Element} element The AJAX target element.
 */
function ajaxSubscriber(element) {
	let content;

	if (element && (content = instance.getContent()) && content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY) {
		const dialog = instance.getDialog();
		// if we are refreshing inside the dialog we may need to reposition
		if (instance.isOpen(dialog)) {  // it damn well better be
			if (!(dialog.style.width && dialog.style.height && hasBusyContent())) {
				// we have not got a fixed or user-created size so we will resize automatically
				instance.reposition(null, null, true);
			}
		}
	}
}

/**
 * Helper for reposition. Called from a timeout to reposition the dialog frame.
 * @function
 * @private
 * @param {HTMLDialogElement} element the dialog frame to reposition.
 * @param {Object} obj a description of the dialog.
 * @param {number} [obj.width] the dialog width
 * @param {number} [obj.height] the dialog height
 * @param {number} [obj.topOffsetPC] the offset from the top of the dialog
 * @param {boolean} animate If animation should be enabled.
 */
function setPositionBySize(element, obj, animate) {
	try {
		if (!animate) {
			resizeable.disableAnimation(element);
		}
		if (canMoveResize()) {
			positionable.setBySize(element, obj);
		}
		setUnsetDimensionsPosition(element);
		setUpMoveResizeControls(element);
	} finally {
		if (!animate) {
			resizeable.restoreAnimation(element);
		}
	}
}

/**
 * Helper for `openDlg`.
 * Initializes the dialog's control buttons.
 * @param {HTMLDialogElement} dialog The dialog container.
 * @param obj The registry item that contains configuration data for this dialog.
 * @private
 * @function
 */
function initDialogControls(dialog, obj) {
	let control;

	setUpMoveResizeControls(dialog);
	if (obj.max && (control = dialog.querySelector(maxbuttonSelector))) {
		shed.select(control);
	}
}

/**
 * Listen for hide and clear out the transient aspects of the dialog. Do not remove any attributes or
 * settings which may be needed by a consuming module (such as dimensions).
 *
 * @function
 * @private
 * @param {CustomEvent & { target: HTMLElement }} $event The hide event.
 */
function shedHideSubscriber({ target }) {
	let clearOpener;
	try {
		if (target?.id === DIALOG_ID) {
			clearOpener = true;
			modalShim.clearModal();
			// remove maximise from dialog so that the next dialog does not open maximised
			/*
			 * NOTE: this could be moved to wc/ui/resizeable.js which owns the max button. However, the
			 * behaviour of the maximised component should depend on the individual component. It is unlikely
			 * one would want to keep a control maximised once it had been closed but it is not impossible.
			 * Maybe this should be added to the regObject so we can re-maximise on open on a dialog-by-dialog
			 * basis.
			 */
			let control = target.querySelector(maxbuttonSelector);
			if (control && shed.isSelected(control)) {
				shed.deselect(control);
			}
			control = instance._openerId ? document.getElementById(instance._openerId) : null;
			if (control) {
				focus.setFocusRequest(control);
			}
			if (subscriber.close) {
				try {
					const callback = subscriber.close;
					subscriber.close = null;
					callback();
				} catch (ex) {
					console.warn(ex);
				}
			}
		}
	} finally {
		if (clearOpener) {
			instance._openerId = null;
		}
	}
}

/**
 * Listen for `shed.show` and focus the dialog.
 *
 * @function
 * @private
 * @param {CustomEvent & { target: HTMLElement }} $event The show event.
 */
function shedShowSubscriber({ target: element }) {
	if (element && element === instance.getDialog()) {
		focus.focusFirstTabstop(element);
	}
}

/**
 * Click listener for dialog opening buttons and controls within a dialog.
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event a click event.
 */
function clickEvent($event) {
	if (!$event.defaultPrevented && $event.target.closest(closeSelector)) {
		const dialog = instance.getDialog();
		if (dialog && instance.isOpen(dialog)) {
			instance.close();
			$event.preventDefault();
		}
	}
}

/**
 * A focus filter helper for tabKeyHelper.
 *
 * @function
 * @private
 * @param {Element} node The Node being tested.
 * @returns {number} the NodeFilter value for the tested node.
 */
function tabstopNodeFilter(node) {
	return focus.isTabstop(node) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
}

/**
 * Helper for keydown on TAB.
 *
 * @function
 * @private
 * @param {Element} element The target element.
 * @param {HTMLDialogElement} dialog A dialog frame.
 * @param {Boolean} hasShift Was teh SHIFT key down during the event?
 * @returns {Boolean} Returns true if we are refocussing the dialog (due to trying to TAB out of it).
 */
function tabKeyHelper(element, dialog, hasShift) {
	let result = false;
	if (!hasShift && isModalDialog(dialog)) {
		const tw = document.createTreeWalker(dialog, NodeFilter.SHOW_ELEMENT, tabstopNodeFilter);
		tw.lastChild();
		if (element === tw.currentNode) {
			result = true;
			focus.focusFirstTabstop(dialog);
		}
	}
	return result;
}

/**
 * Key down listener. Implements key patterns as per http://www.w3.org/TR/wai-aria-practices/#dialog_modal
 * and http://www.w3.org/TR/wai-aria-practices/#dialog_nonmodal.
 * @function
 * @private
 * @param {KeyboardEvent & { target: HTMLElement }} $event A keydown event.
 */
function keydownEvent($event) {
	const {
		target,
		defaultPrevented,
		code,
		shiftKey
	} = $event;
	if (defaultPrevented) {
		return;
	}
	const dialog = instance.getDialog();
	if (dialog && instance.isOpen(dialog)) {
		let result = false;
		switch (code) {
			case "Escape":
				result = instance.close();
				break;
			case "Tab":
				result = tabKeyHelper(target, dialog, shiftKey);
				break;
			case "F6":
				if (!isModalDialog(dialog) && instance._openerId) {
					result = true;
					focus.setFocusRequest(instance._openerId);
				}
				break;
		}
		if (result) {
			$event.preventDefault();
		}
	}
}

/**
 * Do the heavy lifting of the resize event. Called in a timeout so we do not do constant updates as a
 * window frame is dragged.
 *
 * @function
 * @private
 */
const resizeEventHelper = debounce(() => {
	const dialog = instance.getDialog();

	if (!dialog || !instance.isOpen(dialog)) {
		return;
	}
	setUnsetDimensionsPosition(dialog);
	setUpMoveResizeControls(dialog);
}, 10);

/**
 * Adjust dialog to the screen.
 *
 * @function
 * @private
 * @param {UIEvent} $event The resize event.
 */
function resizeEvent({ defaultPrevented }) {
	if (!defaultPrevented) {
		resizeEventHelper();
	}
}

/**
 * Provides a re-usable frame for floating dialog-like controls.
 *
 * * Implements WAI-ARIA practices for:
 *   * [Modal dialogs](http://www.w3.org/TR/wai-aria-practices/#dialog_modal)
 *   * [Non-modal dialogs](http://www.w3.org/TR/wai-aria-practices/#dialog_nonmodal)
 * * Implements WAI-ARIA roles
 *   * [dialog](http://www.w3.org/TR/wai-aria/roles#dialog) and
 *   * [alertdialog](http://www.w3.org/TR/wai-aria/roles#alertdialog)
 *
 * Dialogs are positionable, resizeable and draggable (including keyboard driven facilities for each).
 *
 * ### Configuration
 *
 * Some aspects of WDialog may be set in a configuration object {@link module:wc/ui/dialogFrame~config}. See
 * [the WComponents wiki](https://github.com/BorderTech/wcomponents/wiki/WDialog#client-configuration) for more information.
 *
 * @example
 * require(["wc/config"], function(wcconfig) {
 *   wcconfig.set({
 *     vpUtil: "isSmallScreen",
 *     offset: 0.25
 *   },"wc/ui/dialogFrame");
 * });
 */

initialise.register({
	/**
	 * Component initialisation.
	 * @function module:wc/ui/dialogFrame.initialise
	 * @public
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function (element) {
		event.add(element, "click", clickEvent);
		event.add(window, "resize", resizeEvent, -1);
		event.add(element, shed.events.SHOW, shedShowSubscriber);
		event.add(element, shed.events.HIDE, shedHideSubscriber);
	},

	/**
	 * Late initialisation.
	 * @function module:wc/ui/dialogFrame.postInit
	 * @public
	 */
	postInit: function () {
		processResponse.subscribe(preOpenSubscriber);
		processResponse.subscribe(ajaxSubscriber, true);
	}
});

export default instance;

/**
 * @typedef {Object} module:wc/ui/dialogFrame~dto An object which stores information about a dialog.
 * @property {String} id The content id. If this is not set everything will fail.
 * @property {String} [formId] The id of the form the dialog is in (more useful than you may think). If this is not set we will use the LAST
 *   form in the current view. You may not want this!
 * @property {String} openerId The ID of the control which is opening the dialog.
 * @property {number} [width] The dialog width in px.
 * @property {number} [height] The dialog height in px.
 * @property {Boolean} [resizeable] Is the dialog resizeable?
 * @property {Boolean} [modal] Is the dialog modal?
 * @property {String} [title] The WDialog title. If not set a default title is used.
 * @property {String} [className] The WDialog additional css class.
 * @property {Boolean} [open] If true then the dialog is to be open on page load. This is passed in as part ofthe registration object but is
 *   not stored in the registry.
 * @property {Function} onclose Called when the dialog is closed.
 *
 * @typedef {Object} module:wc/ui/dialogFrame~config An object which allows override of aspects of the dialogFrame
 * @property {String} [vpUtil="isPhonelike"] A name of a public member of {@link module:wc/ui/viewportUtils. This should only be set if a Sass
 * override is used to change the point at which dialogs become full screen.
 * @property {number} [offset=0.33] the vertical offset to apply when opening a dialog. This must be between 0 and 1 and should be between 0.1
 * and 0.5.
 */
