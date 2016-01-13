/**
 * Provides "dialog" functionality.  NOTE: we currently use a custom dialog because IE native dialog does not call and
 * parse xslt (as of IE10). This is not an issue in ff3.6+ or Chrome 6 but these do not support showModelessDialog.
 *
 * The custom dialog also provides somewhat better options for mobile use and cross platform consistency.
 *
 * @module
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/loader/resource
 * @requires external:lib/sprintf
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/modalShim
 * @requires module:wc/timers
 * @requires module:wc/ui/containerload
 * @requires module:wc/has
 * @requires module:wc/ui/resizeable
 * @requires module:wc/ui/positionable
 * @requires module:wc/ui/draggable
 * @requires module:wc/dom/role
 * @requires module:Mustache
 *
 * @todo Re-order source, document private members.
 */
define(["wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/loader/resource",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/modalShim",
		"wc/timers",
		"wc/ui/containerload",
		"wc/has",
		"wc/ui/resizeable",
		"wc/ui/positionable",
		"wc/dom/role",
		"Mustache",
		"wc/ui/draggable"],

	function(classList, event, focus, initialise, shed, Widget, i18n, loader, ajaxRegion, processResponse,
		modalShim, timers, eagerLoader, has, resizeable, positionable, $role, Mustache) {
		"use strict";

		/*
		 * IMPLICIT dependencies when not mobile Dialog implements draggable but does not need to reference it.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/dialog~Dialog
		 * @private
		 */
		function Dialog() {
			var DIALOG_ID = "${wc.ui.dialog.id}",
				INITIAL_TOP_PROPORTION = 0.33,  // when setting the initial position offset the dialog so that the gap at the top is this proportion of the difference between the dialog size and viewport size
				openerId,
				DIALOG,
				BUTTON = new Widget("button"),
				ANCHOR,
				CLOSE_WD = BUTTON.extend("wc_dialog_close"),
				RESIZERS,
				RESIZE_WD,
				MAX_BUTTON,
				FOOTER,
				CONTENT_WD = new Widget("div", "", {"aria-live": "assertive"}),
				OPENER = BUTTON.extend("", {"data-wc-dialogconf": null}),
				HEADER_WD = new Widget("${wc.dom.html5.element.header}"),
				TITLE_WD = new Widget("h1"),
				FORM = new Widget("form"),
				BASE_CLASS = "wc_dragflow wc_resizeflow",
				registry = {},
				UNIT = "px",
				emptyOnClose = true,
				openOnLoadTimer,
				openThisDialog,
				opening = false,
				cleanUpLater = false,
				GET_ATTRIB = "${wc.ui.ajax.attribute.getData}",
				repositionTimer,
				notMobile = !(has("ios") || has("android") || has("iemobile") || has("operamobi") || has("operamini") || has("bb")),
				TESTING_URL_REG = {};  // TODO: REMOVE THIS IT IS FOR LOCAL STATIC TESTING ONLY

			TITLE_WD.descendFrom(HEADER_WD);

			if (notMobile) {
				RESIZERS = resizeable.getWidget();
				RESIZE_WD = RESIZERS.handle;
				MAX_BUTTON = RESIZERS.maximise;
			}

			function getContent(dialog) {
				return CONTENT_WD.findDescendant(dialog);
			}

			/**
			 * Opens a dialog on page load.
			 * @function
			 * @private
			 */
			function openOnLoad() {
				if (openThisDialog) {
					if (openOnLoadTimer) {
						timers.clearTimeout(openOnLoadTimer);
					}
					openOnLoadTimer = timers.setTimeout(openDlg, 100, openThisDialog);
					openThisDialog = null;
				}
			}

			/**
			 * Array.forEach function to add each dialog definition object to the registry.
			 * @see module:wc/ui/dialog#register
			 * @function
			 * @private
			 * @param {module:wc/ui/dialog~regObject} dialogObj The dialog dto.
			 */
			function _register(dialogObj) {
				var id = dialogObj.id;
				if (id) {
					registry[id] = {
						id: id,
						className: dialogObj.className,
						formId: dialogObj.form,
						width: dialogObj.width,
						height: dialogObj.height,
						initWidth: dialogObj.width,  // useful if we do not allow resize below initial size
						initHeight: dialogObj.height,
						resizable: dialogObj.resizable || false,
						modal: dialogObj.modal || false,
						title: dialogObj.title || i18n.get("${wc.ui.dialog.title.noTitle}")
					};
					if (dialogObj.open) {
						openThisDialog = id;
					}
				}
			}


			/**
			 * Indicates that the dialog is modal.
			 * @function
			 * @private
			 * @param {Element} dialog the dialog element to test
			 * @returns {Boolean} true if the dialog is modal.
			 */
			function isModal(dialog) {
				var content,
					result = false;
				if ((content = getContent(dialog)) && $role.get(content) === "alertdialog") {
					result = true;
				}
				return result;
			}

			/**
			 * Action click events within the dialog.
			 * @function
			 * @private
			 * @param {Element} element The element which was clicked.
			 */
			function activateClick(element) {
				var result = false,
					isTrigger,
					_element,
					dialogId,
					trigger,
					targets,
					dialog = document.getElementById(DIALOG_ID);

				/*
				 * array.some filter function for ajax targets
				 * @function
				 * @private
				 * @param {String} id The id of the target element.
				 * @returns {Boolean} true if the target element is inside the dialog content.
				 */
				function _targetInsideDialog(id) {
					var _result = false, element;
					if (id && (element = document.getElementById(id))) {
						_result = !!(dialog.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY);
					}
					return _result;
				}

				if (dialog && !shed.isHidden(dialog)) {
					if ((CLOSE_WD.findAncestor(element))) {
						close(dialog, true);
					}
					else if (CONTENT_WD.findAncestor(element)) {  // we are inside a dialog's content
						// we need to know if a click is on an ajax trigger inside a dialog
						if (ajaxRegion.isTrigger(element)) {
							isTrigger = true;
							_element = element;
						}
						else {
							// this is a chrome thing: it honours clicks on img elements and does not pass them through to the underlying link/button
							ANCHOR = ANCHOR || new Widget("A");
							_element = Widget.findAncestor(element, [ BUTTON, ANCHOR ]);
							if (_element && ajaxRegion.isTrigger(_element)) {
								isTrigger = true;
							}
						}
						if (isTrigger && _element && (trigger = ajaxRegion.getTrigger(_element, true)) && (targets = trigger.loads) && targets.length && !targets.some(_targetInsideDialog)) {
							close(dialog, false);  // NOTE: do not set result to true or you will prevent the AJAX action!
							cleanUpLater = _element.id;
						}
					}
				}
				else if ((_element = OPENER.findAncestor(element)) && !shed.isDisabled(_element)) {
					// beware, if the attribute is "value" then IE7 and earlier will break
					if ((dialogId = _element.getAttribute("data-wc-dialogconf"))) {
						openDlg(dialogId, _element.id);
						result = true;
					}
				}
				return result;
			}

			function tabstopNodeFilter(node) {
				return focus.isTabstop(node) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
			}

			/**
			 * Open a dialog.
			 * @function
			 * @private
			 * @param {String} id The id of the WDialog to open.
			 * @param {String} [_openerId] The id of the button used to launch the dialog if known.
			 */
			function openDlg(id, _openerId) {
				var regObj = registry[id],
					element = document.getElementById(DIALOG_ID),
					doOpen = function (dialog) {
						openDlgHelper(dialog, regObj, id, _openerId);
					};

				if (regObj) {
					if (element) {
						doOpen(element);
					}
					else {
						buildDialog(regObj.formId).then(doOpen);
					}
				}
			}

			/**
			 * Helper for `openDlg`.
			 * This does the actual heavy lifting of opening a dialog.
			 *
			 * @param dialog The dialog container.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @param id The id of the WDialog being opened.
			 * @param _openerId The id of the button used to launch the dialog if known.
			 * @private
			 * @function
			 */
			function openDlgHelper(dialog, regObj, id, _openerId) {
				var content;

				if (dialog && shed.isHidden(dialog)) {
					opening = true;
					// set up the content receptacle
					content = getContent(dialog);

					reinitializeDialog(dialog, content, regObj, id);

					if (notMobile) {
						// mobile browsers dialog is auto max'ed and not resizeable or positionable
						initDialogControls(dialog, regObj);
						initDialogDimensions(dialog, regObj);
					}

					setModality(dialog, content, regObj);

					/* TODO: REMOVE THIS BIT IT IS FOR LOCAL STATIC TESTING ONLY */
					if (TESTING_URL_REG[id]) {
						content.setAttribute("data-wc-ajaxurl", TESTING_URL_REG[id]);
					}

					rememberOpener(content, _openerId);

					// content is half magic: it needs to be called specifically because we do not shed.show() it
					if (content.className) {
						eagerLoader.load(content, true, false);
					}

					// show the dialog
					shed.show(dialog);
					initDialogPosition(dialog, regObj);
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Applies the dialog "mode" either modal or non-modal as defined in regObj.
			 *
			 * @param dialog The dialog container.
			 * @param content The dialog content.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function setModality(dialog, content, regObj) {
				if (regObj.modal) {
					content.setAttribute("role", "alertdialog");
					modalShim.setModal(dialog);
				}
				else {
					content.removeAttribute("role");
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Once the dialog has been built it needs to be configured each time it is opened.
			 * For example the correct title must be displayed for this specific dialog. CSS classes may need to be set, removed etc.
			 *
			 * @param dialog The dialog container.
			 * @param content The dialog content.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @param id The id of the WDialog being opened.
			 * @private
			 * @function
			 */
			function reinitializeDialog(dialog, content, regObj, id) {
				var title;
				if (regObj.className) {
					dialog.className = BASE_CLASS = " " + regObj.class;
				}
				else {
					dialog.className = BASE_CLASS;
				}

				if (content.className) {  // if we have a dialog which has content on load and has not been "closed" it will not have its ajax class names
					content.id = id;
					content.innerHTML = i18n.get("${wc.ui.loading.loadMessage}");
				}
				else {
					// dialog built in XSLT because it has content
					event.add(dialog, event.TYPE.keydown, keydownEvent);
				}

				// set the dialog title
				if ((title = TITLE_WD.findDescendant(dialog))) {
					title.innerHTML = "";
					title.innerHTML = regObj.title;
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Initializes the dialog's control buttons.
			 * @param dialog The dialog container.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogControls(dialog, regObj) {
				var control;
				FOOTER = FOOTER || new Widget("${wc.dom.html5.element.footer}");
				if ((control = FOOTER.findDescendant(dialog))) {
					if (regObj.resizable) {
						shed.show(control, true);
					}
					else {
						shed.hide(control, true);
					}
				}
				if (RESIZE_WD && (control = RESIZE_WD.findDescendant(dialog))) {
					if (regObj.resizable) {
						shed.show(control, true);
					}
					else {
						shed.hide(control, true);
					}
				}

				// maximise/restore button
				if (MAX_BUTTON && (control = MAX_BUTTON.findDescendant(dialog))) {
					if (regObj.resizable) {
						shed.show(control, true);
						if (regObj.max) {
							shed.select(control);
						}
					}
					else {
						shed.deselect(control);
						shed.hide(control, true);
					}
				}
				if ((control = HEADER_WD.findDescendant(dialog, true))) {
					control.setAttribute("data-wc-draggable", "true");
					control.setAttribute("data-wc-dragfor", DIALOG_ID);
					// maximise restore double click on header
					if (regObj.resizable) {
						resizeable.setMaxBar(control);
					}
					else {
						resizeable.clearMaxBar(control);
					}
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Stores information about the button used to launch the dialog.
			 * @param content The dialog content.
			 * @param _openerId The id of the button used to open the dialog.
			 * @private
			 * @function
			 */
			function rememberOpener(content, _openerId) {
				var opener;
				if (_openerId) {
					opener = document.getElementById(_openerId);
					content.setAttribute(GET_ATTRIB, _openerId + "=" + (opener ? opener.value : "x"));
				}
				else {
					content.removeAttribute(GET_ATTRIB);
				}

				openerId = _openerId || ((document.activeElement) ? document.activeElement.id : null);
			}

			/**
			 * Helper for `openDlg`.
			 * ets the dialog's width and height ready for opening.
			 * @param dialog The dialog container.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogDimensions(dialog, regObj) {
				if (regObj.width) {
					dialog.style.width = regObj.width + UNIT;
				}
				else {
					dialog.style.width = "";
				}
				if (regObj.height) {
					dialog.style.height = regObj.height + UNIT;
				}
				else {
					dialog.style.height = "";
				}
				if (!"${wc.ui.dialog.allowSmallerThanInitial}") {
					if (regObj.initWidth || regObj.initWidth === 0) {
						dialog.style.minWidth = regObj.initWidth + UNIT;
					}
					if (regObj.initHeight || regObj.initHeight === 0) {
						dialog.style.minHeight = regObj.initHeight + UNIT;
					}
				}
				if (regObj.top || regObj.top === 0) {
					dialog.style.top = regObj.top + UNIT;
				}
				else {
					dialog.style.top = "";
				}
				if (regObj.left || regObj.left === 0) {
					dialog.style.left = regObj.left + UNIT;
					dialog.style.margin = "0";
				}
				else {
					dialog.style.left = "";
					dialog.style.margin = "";
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Positions the dialog immediately after it has been opened.
			 * @param dialog The dialog container.
			 * @param regObj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogPosition(dialog, regObj) {
				var removeDragAnimClass,
					removeResizeAnimClass;
				if (notMobile) {
					// set the initial position
					if (!(regObj.top || regObj.left || regObj.top === 0 || regObj.left === 0)) {
						if (regObj.resizable && classList.contains(dialog, "wc_resizeflow")) {
							removeResizeAnimClass = true;
							classList.remove(dialog, "wc_resizeflow");
						}
						if (classList.contains(dialog, "wc_dragflow")) {
							removeDragAnimClass = true;
							classList.remove(dialog, "wc_dragflow");
						}
						positionable.setBySize(dialog, {width: regObj.width, height: regObj.height, topOffsetPC: INITIAL_TOP_PROPORTION});

						if (removeResizeAnimClass) {
							classList.add(dialog, "wc_resizeflow");
						}
						if (removeDragAnimClass) {
							classList.add(dialog, "wc_dragflow");
						}
					}
				}
			}

			/**
			 * Builds the dialog shell when required. This is only called once when the first dialog is first opened.
			 * @function
			 * @private
			 * @param {String} [formId] The id of the WApplication (HTML FORM) to which the dialog belongs.
			 * @returns {Promise} resolved with {Element} dialog The dialog element.
			 */
			function buildDialog(formId) {
				return loader.load("dialog.xml", true, true).then(function(template) {
					/*
					 * sprintf replacements
					 * 1: maximise button title ${wc.ui.dialog.title.maxRestore}
					 * 2: close button title ${wc.ui.dialog.title.close}
					 * 3: content loading message ${wc.ui.loading.loadMessage}
					 */
					var form,
						dialog,
						dialogHeader,
						resizeHandle,
						headerTitle,
						resizeHandleTitle,
						dialogProps = {
							heading :{
								maxRestore: i18n.get("${wc.ui.dialog.title.maxRestore}"),
								close: i18n.get("${wc.ui.dialog.title.close}")
							},
							message: {
								loading: i18n.get("${wc.ui.loading.loadMessage}")
							}
						},
						dialogHTML = Mustache.to_html(template, dialogProps);

					if (formId && (form = document.getElementById(formId)) && !FORM.isOneOfMe(form)) {
						form = FORM.findAncestor(form);
					}

					// fallback: will only work if there is only one form in a screen
					if (!form) {
						form = FORM.findDescendant(document.body);
					}

					form.insertAdjacentHTML("beforeEnd", dialogHTML);

					if ((dialog = document.getElementById(DIALOG_ID))) {
						event.add(dialog, event.TYPE.keydown, keydownEvent);
						if ((dialogHeader = HEADER_WD.findDescendant(dialog, true)) && (headerTitle = i18n.get("${wc.ui.dialog.title.move}"))) {
							dialogHeader.title = headerTitle;
						}

						if (RESIZE_WD && (resizeHandle = RESIZE_WD.findDescendant(dialog)) && (resizeHandleTitle = i18n.get("${wc.ui.dialog.title.resize}"))) {
							resizeHandle.title = resizeHandleTitle;
						}
					}
					return dialog;
				});
			}

			/*
			 * If a dialog with content is inserted via ajax we have to unshim any existing dialog before we insert the new
			 * one. NOTE: the duplicate id check in processResponse will remove the dialog itself during its insert phase so
			 * we do not have to do that here.
			 */
			function preOpenSubscriber(element, docFragment, action, triggerId) {
				var removeShim = false, dialog, content;
				if (docFragment.querySelector) {
					if (docFragment.querySelector("#" + DIALOG_ID)) {
						removeShim = true;
					}
				}
				else if (docFragment.getElementById && docFragment.getElementById(DIALOG_ID)) {
					removeShim = true;
				}
				if (removeShim && (dialog = document.getElementById(DIALOG_ID)) && !shed.isHidden(dialog)) {
					modalShim.clearShim(dialog);
				}
				// if we kept the content during close it was to include the dialog content in an ajax request. We can remove the content when we get the response
				if (cleanUpLater && triggerId === cleanUpLater && (dialog = document.getElementById(DIALOG_ID)) && shed.isHidden(dialog) && (content = getContent(dialog)) && content.firstChild) {
					cleanUpLater = null;
					content.innerHTML = "";
					content.id = "";
				}
			}

			/**
			 * Reposition an auto-size dialog after ajax.
			 * @function
			 * @private
			 * @param {Element} dialog The dialog to reposition.
			 * @param {Object} obj The reposition information being width, height and vertical proportion.
			 */
			function reposition(dialog, obj) {
				var removeResizeAnimClass, removeDragAnimClass;
				if (notMobile) {
					if (repositionTimer) {
						timers.clearTimeout(repositionTimer);
						repositionTimer = null;
					}

					if (classList.contains(dialog, "wc_resizeflow")) {
						removeResizeAnimClass = true;
						classList.remove(dialog, "wc_resizeflow");
					}
					if (classList.contains(dialog, "wc_dragflow")) {
						removeDragAnimClass = true;
						classList.remove(dialog, "wc_dragflow");
					}

					repositionTimer = timers.setTimeout(function() {
						positionable.setBySize(dialog, obj);
						if (removeResizeAnimClass) {
							classList.add(dialog, "wc_resizeflow");
						}
						if (removeDragAnimClass) {
							classList.add(dialog, "wc_dragflow");
						}
						if (repainter) {
							repainter.checkRepaint(dialog);
						}
					}, 100);
				}
			}

			/**
			 * Set the focus into the dialog after the AJAX malarkey has finished. This looks at the dialog content
			 * component which is an unnamed wrapper in the Java side so will ONLY grab focus when a dialog is opened and
			 * will not continually grab it each time an ajax action occurs inside a dialog even if the target is the
			 * WComponent (most commonly a WPanel) which is used as the "content" of the dialog. Tricky that.
			 *
			 * @param {Element} element The root element from the ajax response.
			 */
			function postOpenSubscriber(element) {
				var dialog,
					control,
					content,
					regObj;
				if (element) {
					if (opening && CONTENT_WD.isOneOfMe(element) && (dialog = document.getElementById(DIALOG_ID))) {
						opening = false;
						// set the initial position
						if ((regObj = registry[element.id]) && !(regObj.top || regObj.left || regObj.top === 0 || regObj.left === 0)) {
							reposition(dialog, {width: regObj.width, height: regObj.height, topOffsetPC: INITIAL_TOP_PROPORTION});
						}

						if (MAX_BUTTON && (control = MAX_BUTTON.findDescendant(dialog)) && focus.canFocus(control)) {
							// max restore may be available but hidden
							focus.setFocusRequest(control);
						}
						else if ((control = CLOSE_WD.findDescendant(dialog))) {
							// close button is always available, resize is not
							focus.setFocusRequest(control);
						}
						else {
							focus.focusFirstTabstop(element);
						}
					}
					else if ((content = CONTENT_WD.findAncestor(element)) && (regObj = registry[content.id])) {
						// if we are refreshing inside the dialog we may need to reposition
						dialog = document.getElementById(DIALOG_ID);
						if (!shed.isHidden(dialog)) {  // it damn well better not be
							if (!(dialog.style.width && dialog.style.height)) {
								// we have not got a fixed or user-created size so we will resize automatically
								reposition(dialog, {topOffsetPC: INITIAL_TOP_PROPORTION});
							}
						}
					}
				}
			}

			/**
			 * Close a dialog.
			 * @function
			 * @private
			 * @param {Element} dialog The dialog to close.
			 * @param {Boolean} [removeContent] If true remove the content from the dialog after it closes.
			 */
			function close(dialog, removeContent) {
				if (dialog && !shed.isHidden(dialog)) {
					emptyOnClose = removeContent;
					// delete the field used to tell the server the id of the opener button (it should not be here but I am paranoid).
					shed.hide(dialog);
				}
			}

			/*
			 * list for hide and clear out the transient aspects ofthe dialog.
			 */
			function shedSubscriber(element) {
				var contentDiv, control;
				if (element && element.id === DIALOG_ID) {
					try {
						modalShim.clearModal(element);
						if (element.style.width) {
							setRegObjAttribute(element, "width", element.style.width.replace(UNIT, ""));
						}
						if (element.style.height) {
							setRegObjAttribute(element, "height", element.style.height.replace(UNIT, ""));
						}
						if (element.style.left) {
							setRegObjAttribute(element, "left", element.style.left.replace(UNIT, ""));
						}
						if (element.style.top) {
							setRegObjAttribute(element, "top", element.style.top.replace(UNIT, ""));
						}

						// remove minWidth and minHeight
						element.style.minWidth = "";
						element.style.minHeight = "";
						element.style.margin = "";

						// remove maximise from dialog so that the next dialog does not open maximised
						/*
						 * NOTE: this could be moved to wc/ui/resizeable.js which owns the max button. However, the
						 * behaviour of the maximised component should depend on the individual component. It is unlikely
						 * one would want to keep a control maximised once it had been closed but it is not impossible.
						 * Maybe this should be added to the regObject so we can re-maximise on open on a dialog-by-dialog
						 * basis.
						 */
						if (notMobile && MAX_BUTTON && (control = MAX_BUTTON.findDescendant(element))) {
							if (shed.isSelected(control)) {
								shed.deselect(control);
								setRegObjAttribute(element, "max", true);
							}
							else {
								setRegObjAttribute(element, "max", false);
							}
						}

						/*
						 * NOTE: clear the content AFTER resetting all the registry settings because we need the content id
						 * to apply regObject changes. So this should be the last thing you do in the close subscriber.
						 */
						if ((contentDiv = getContent(element))) {
							// remove the magic button reference (just in case it has not already been removed).
							contentDiv.removeAttribute(GET_ATTRIB);

							if (emptyOnClose) {
								contentDiv.innerHTML = "";
								contentDiv.id = "";
							}
						}

						if (openerId && (control = document.getElementById(openerId)) && focus.canFocus(control)) {
							focus.setFocusRequest(control);
						}
					}
					finally {
						emptyOnClose = true;
						openerId = null;
					}
				}
			}

			/**
			 * Sets any property on a dialogs registry object, such as to store size on close.
			 * @function
			 * @private
			 * @param {Element} dialog The dialog being closed.
			 * @param {String} name The registry object property to set.
			 * @param {*} value The value to set the property to
			 */
			function setRegObjAttribute(dialog, name, value) {
				var content,
					id,
					regObj;
				if ((content = getContent(dialog)) && (id = content.id) && (regObj = registry[id])) {
					regObj[name] = value;
				}
			}



			/**
			 * Click listener for dialog opening buttons and controls within a dialog.
			 * @function
			 * @private
			 * @param {Event} $event a click event.
			 */
			function clickEvent($event) {
				if (!$event.defaultPrevented && activateClick($event.target)) {
					$event.preventDefault();
				}
			}

			/**
			 * Key down listener. Inplements key patterns as per http://www.w3.org/TR/wai-aria-practices/#dialog_modal
			 * and http://www.w3.org/TR/wai-aria-practices/#dialog_nonmodal.
			 * @function
			 * @private
			 * @param {Event} $event A keydown event.
			 * @todo fix the TAB case - the nested ifs can be flattened.
			 */
			function keydownEvent($event) {
				var element = $event.target,
					dialog,
					result = false,
					keyCode = $event.keyCode,
					tw;
				if (!$event.defaultPrevented && (dialog = document.getElementById(DIALOG_ID)) && !shed.isHidden(dialog)) {
					switch (keyCode) {
						case KeyEvent.DOM_VK_ESCAPE:
							close(dialog, true);
							result = true;
							break;
						case KeyEvent.DOM_VK_TAB:
							if (!$event.shiftKey) {
								if (isModal(dialog)) {
									tw = document.createTreeWalker(dialog, NodeFilter.SHOW_ELEMENT, tabstopNodeFilter, false);
									tw.lastChild();
									if (element === tw.currentNode) {
										result = true;
										focus.focusFirstTabstop(dialog);
									}
								}
							}
							break;
						case KeyEvent.DOM_VK_F6:
							if (!isModal(dialog) && openerId) {
								result = true;
								focus.setFocusRequest(openerId);
							}
							break;
					}
					if (result) {
						$event.preventDefault();
					}
				}
			}

			/**
			 * Component initialisation simply attaches a click event handler
			 * @function module:wc/ui/dialog.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
			};

			/**
			 * Late initialisation to add ajax and shed subscribers.
			 * @function module:wc/ui/dialog.postInit
			 * @public
			 */
			this.postInit = function() {
				processResponse.subscribe(preOpenSubscriber);
				processResponse.subscribe(postOpenSubscriber, true);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);
			};

			/**
			 * Add the object definitions of each dialog to the registry.
			 * @function module:wc/ui/dialog.register
			 * @public
			 * @param {module:wc/ui/dialog~regObject[]} array An array of dialog definition objects.
			 */
			this.register = function(array) {
				if (array && array.length) {
					array.forEach(_register);
					initialise.addCallback(openOnLoad);
				}
			};

			/**
			 * Get the widget which describes a dialog.
			 * @function module:wc/ui/dialog.getWidget
			 * @public
			 * @param {Boolean} [content] if true return the content widget;
			 */
			this.getWidget = function(content) {
				DIALOG = DIALOG || new Widget("${wc.dom.html5.element.dialog}");
				return content ? CONTENT_WD : DIALOG;
			};


			/**
			 * public for testing.
			 * @ignore
			 */
			this._keydownEventHandler = keydownEvent;

			/**
			 * TODO: REMOVE THIS IT IS FOR LOCAL STATIC TESTING ONLY.
			 * @function
			 * @public
			 * @param {String} id The dialog ID.
			 * @param {String} url The dummy ajax url.
			 * @ignore
			 */
			this.fiddleAjaxResponse = function(id, url) {
				TESTING_URL_REG[id] = url;
			};
		}

		var /** @alias module:wc/ui/dialog */ instance = new Dialog(),
			repainter;

		initialise.register(instance);

		if (has("ie") === 8) {
			require([ "wc/fix/inlineBlock_ie8" ], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		return instance;

		/**
		 * @typedef {Object} module:wc/ui/dialog~regObject An object which stores information about a dialog.
		 * @property {String} id The WDialog id.
		 * @property {String} formId The id of the form the dialog is in (more useful than you may think).
		 * @property {int} [width] The dialog width in px.
		 * @property {int} [height] The dialog height in px.
		 * @property {int} [initWidth] The dialog width in px as set by the Java. This is used if the theme allows
		 *    resizing but prevents a dialog being made smaller than its intial size. This property is not in the
		 *    registration object passed in to the module.
		 * @property {int} [initHeight] The dialog height in px as set by the Java. This is used if the theme allows
		 *    resizing but prevents a dialog being made smaller than its intial size. This property is not in the
		 *    registration object passed in to the module.
		 * @property {Boolean} [resizeable] Is the dialog resizeable?
		 * @property {Boolean} [modal] Is the dialog modal?
		 * @property {String} [title] The WDialog title. If not set a default title is used.
		 * @property {Boolean} [open] If true then the dialog is to be open on page load. This is passed in as part of
		 *    the registration object but is not stroed in the registry.
		 */

	});
