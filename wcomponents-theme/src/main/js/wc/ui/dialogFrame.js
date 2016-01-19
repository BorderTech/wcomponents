define(["wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/uid",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/loader/resource",
		"wc/ui/ajax/processResponse",
		"wc/ui/modalShim",
		"wc/timers",
		"wc/has",
		"wc/ui/resizeable",
		"wc/ui/positionable",
		"wc/dom/role",
		"Mustache",
		"wc/ui/draggable"],
	function(classList, event, focus, initialise, shed, tag, uid, Widget, i18n, loader, processResponse,
		modalShim, timers, has, resizeable, positionable, $role, Mustache) {
		"use strict";

		/*
		 * IMPLICIT dependencies when not mobile Dialog implements draggable but does not need to reference it.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/dialogFrame~DialogFrame
		 * @private
		 */
		function DialogFrame() {
			var DIALOG_ID = "${wc.ui.dialog.id}",
				CONTENT_BASE_CLASS = "content",
				INITIAL_TOP_PROPORTION = 0.33,  // when setting the initial position offset the dialog so that the gap at the top is this proportion of the difference between the dialog size and viewport size
				openerId,
				DIALOG = new Widget("${wc.dom.html5.element.dialog}"),
				DIALOG_CONTENT_WRAPPER = new Widget("div", CONTENT_BASE_CLASS, {"aria-live": "assertive"}),
				BUTTON = new Widget("button"),
				CLOSE_WD = BUTTON.extend("wc_dialog_close"),
				RESIZERS,
				RESIZE_WD,
				MAX_BUTTON,
				FOOTER,
				HEADER_WD = new Widget("${wc.dom.html5.element.header}"),
				TITLE_WD = new Widget("h1"),
				FORM = new Widget("form"),
				BASE_CLASS = "wc_dragflow wc_resizeflow",
				UNIT = "px",
				repositionTimer,
				notMobile = !has("device-mobile"),
				FORM,
				REJECT = {
					ALREADY_OPEN: "Cannot open a dialog whilst another dialog is open",
					CANNOT_BUILD: "Cannot create the dialog frame",
					NO_FORM: "Cannot find a form to which to attach the dialog",
					UNKNOWN: "Failed to open dialog: readon unknown"
				};

			TITLE_WD.descendFrom(HEADER_WD);
			DIALOG_CONTENT_WRAPPER.descendFrom(DIALOG, true);

			if (notMobile) {
				RESIZERS = resizeable.getWidget();
				RESIZE_WD = RESIZERS.handle;
				MAX_BUTTON = RESIZERS.maximise;
			}


			/**
			 * Indicates that the dialog is modal.
			 * @function
			 * @private
			 * @param {Element} dialog the dialog element to test
			 * @returns {Boolean} true if the dialog is modal.
			 */
			function isModalDialog(dialog) {
				return $role.get(dialog) === "alertdialog";
			}


			/**
			 * Get the form into which we want to place the dialog.
			 *
			 * @param {wc/ui/dialogFrame~dto} [dto] The config options for the dialog (if any).
			 * @returns {?Element} The form element.
			 */
			function getForm(dto) {
				var formId = (dto ? (dto.formId || dto.openerId) : null),
					candidate, forms;

				if (formId && (candidate = document.getElementById(formId))) {
					if (candidate.tagName === tag.FORM) {
						return candidate;
					}
					else if ((formId = candidate.form)) {
						return document.getElementById(formId);
					}
					else {
						FORM = FORM || new Widget("form");
						return FORM.getAncestor(candidate);
					}
				}
				else { // no clue to the form get the last form in the view
					forms = document.getElementsByTagName("form");
					if (forms && forms.length) {
						return forms[forms.length -1];
					}
				}
				return null;
			}

			/**
			 * Request a dialog be opened.
			 *
			 * @function
			 * @public
			 * @param {module:wc/ui/dialogFrame~dto} dto The config options for the dialog to be opened.
			 * @returns {Promise} The promise will be a rejection if the dialog is not able to be opened.
			 */
			this.open = function(dto) {
				var dialog = this.getDialog(),
					form, formId;

				if (dialog) {
					if (shed.isHidden(dialog)) {
						return Promise.resolve(openDlgHelper(dto));
					}
					return Promise.reject(REJECT.ALREADY_OPEN);
				}
				else if ((form = getForm(dto))) {
					formId = form.id || (form.id = uid());

					if (formId) {
						return buildDialog(formId).then(function() {
							openDlgHelper(dto);
						});
					}
					return Promise.reject(REJECT.NO_FORM);
				}
				return Promise.reject(REJECT.UNKNOWN);
			};

			/**
			 * Helper for `openDlg`.
			 * This does the actual heavy lifting of opening a dialog.
			 *
			 * @param dto The configuration data for this dialog.
			 * @private
			 * @function
			 */
			function openDlgHelper(dto) {
				var isModal,
					dialog = instance.getDialog();

				if (dialog && shed.isHidden(dialog)) {
					if (dto && dto.openerId) {
						openerId = dto.openerId;
					}
					else {
						openerId = document.activeElement ? document.activeElement.id : null;
					}

					reinitializeDialog(dialog, dto);

					if (notMobile) {
						// mobile browsers dialog is auto max'ed and not resizeable or positionable
						initDialogControls(dialog, dto);
						initDialogDimensions(dialog, dto);
						setModality(dialog, true); // all dialogs are modal on mobile as non-modal dialogs make no sense when they are full screen
					}
					else {
						isModal = (dto && typeof dto.modal !== "undefined") ? dto.modal : true;
						setModality(dialog, isModal);
					}
					// show the dialog
					shed.show(dialog);
					initDialogPosition(dialog, dto);
					return true;
				}
				return false;
			}

			/**
			 * Helper for `openDlg`.
			 * Applies the dialog "mode" either modal or non-modal.
			 *
			 * @param dialog The dialog container.
			 * @param isModal Indicates if this dialog is modal.
			 * @private
			 * @function
			 */
			function setModality(dialog, isModal) {
				if (isModal) {
					dialog.setAttribute("role", "alertdialog");
					modalShim.setModal(dialog);
				}
				else {
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
			 * @param dialog The dialog container.
			 * @param {Object} obj The registry item that contains configuration data for this dialog.
			 */
			function reinitializeDialog(dialog, obj) {
				var title;

				instance.unsetAllDimensions(dialog);
				dialog.className = BASE_CLASS + ((obj && obj.className) ? (" " + obj.className) : "");
				instance.resetContent(false, (obj ? obj.id : "")) ;

				// set the dialog title
				if ((title = TITLE_WD.findDescendant(dialog))) {
					title.innerHTML = ""; // ??? This _cannot_ really still be needed?
					title.innerHTML = (obj && obj.title) ? obj.title : i18n.get("${wc.ui.dialog.title.noTitle}");
				}

			}

			/**
			 * Helper for `openDlg`.
			 * Initializes the dialog's control buttons.
			 * @param dialog The dialog container.
			 * @param obj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogControls(dialog, obj) {
				var control;
				FOOTER = FOOTER || new Widget("${wc.dom.html5.element.footer}");
				if ((control = FOOTER.findDescendant(dialog))) {
					if (obj && obj.resizable) {
						shed.show(control, true);
					}
					else {
						shed.hide(control, true);
					}
				}

				if (RESIZE_WD && (control = RESIZE_WD.findDescendant(dialog))) {
					if (obj && obj.resizable) {
						shed.show(control, true);
					}
					else {
						shed.hide(control, true);
					}
				}

				// maximise/restore button
				if (MAX_BUTTON && (control = MAX_BUTTON.findDescendant(dialog))) {
					if (obj && obj.resizable) {
						shed.show(control, true);
						if (obj.max) {
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
					if (obj.resizable) {
						resizeable.setMaxBar(control);
					}
					else {
						resizeable.clearMaxBar(control);
					}
				}
			}

			/**
			 * Remove all inline dimension styles from the dialog.
			 *
			 * @function module:wc/ui/dialogFrame.unsetAllDimensions
			 * @public
			 * @param {Element} [dlg] The dialog wrapper element if known.
			 */
			this.unsetAllDimensions = function(dlg) {
				var dialog = dlg || this.getDialog();
				if (dialog) {
					dialog.style.width = "";
					dialog.style.height = "";
					dialog.style.top = "";
					dialog.style.left = "";
					dialog.style.minWidth = "";
					dialog.style.minHeight = "";
					dialog.style.margin = "";
				}
			};

			/**
			 * Helper for `openDlg`.
			 * Sets the dialog's width and height ready for opening.
			 *
			 * @private
			 * @function
			 * @param {Element} dialog The dialog container.
			 * @param {module:wc/ui/dialogFrame~dto} obj The configuration settings for this dialog.
			 */
			function initDialogDimensions(dialog, obj) {
				if (obj.width) {
					dialog.style.width = obj.width + UNIT;
				}
				if (obj.height) {
					dialog.style.height = obj.height + UNIT;
				}
				if (!"${wc.ui.dialog.allowSmallerThanInitial}") {
					if (obj.initWidth || obj.initWidth === 0) {
						dialog.style.minWidth = obj.initWidth + UNIT;
					}
					if (obj.initHeight || obj.initHeight === 0) {
						dialog.style.minHeight = obj.initHeight + UNIT;
					}
				}
				if (obj.top || obj.top === 0) {
					dialog.style.top = obj.top + UNIT;
				}
				if (obj.left || obj.left === 0) {
					dialog.style.left = obj.left + UNIT;
					dialog.style.margin = "0";
				}
			}

			/**
			 * Helper for `openDlg`.
			 * Positions the dialog immediately after it has been opened.
			 * @param dialog The dialog container.
			 * @param obj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogPosition(dialog, obj) {
				var removeDragAnimClass,
					removeResizeAnimClass;
				if (notMobile && obj) {
					// set the initial position. If the position (top, left) is set in the config object we do not need to calculate position.
					if (!(obj.top || obj.left || obj.top === 0 || obj.left === 0)) {
						if (obj.resizable && classList.contains(dialog, "wc_resizeflow")) {
							removeResizeAnimClass = true;
							classList.remove(dialog, "wc_resizeflow");
						}
						if (classList.contains(dialog, "wc_dragflow")) {
							removeDragAnimClass = true;
							classList.remove(dialog, "wc_dragflow");
						}
						positionable.setBySize(dialog, {width: obj.width, height: obj.height, topOffsetPC: INITIAL_TOP_PROPORTION});

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

					if ((dialog = instance.getDialog())) {
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
			function preOpenSubscriber(element, docFragment) {
				var removeShim = false,
					dialog;
				if (docFragment.querySelector) {
					if (docFragment.querySelector("#" + DIALOG_ID)) {
						removeShim = true;
					}
				}
				else if (docFragment.getElementById && docFragment.getElementById(DIALOG_ID)) {
					removeShim = true;
				}
				if (removeShim && (dialog = instance.getDialog()) && !shed.isHidden(dialog)) {
					modalShim.clearShim(dialog);
				}
			}

			/**
			 * If there is an AJAX replace inside a dialog we may need to reposition the dialog.
			 * @param {Element} element The AJAX target element.
			 * @returns {undefined}
			 */
			function ajaxSubscriber(element) {
				var content, dialog;

				if (element && (content = instance.getContent()) && content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY) {
					dialog = instance.getDialog();
					// if we are refreshing inside the dialog we may need to reposition
					if (!shed.isHidden(dialog)) {  // it damn well better not be
						if (!(dialog.style.width && dialog.style.height)) {
							// we have not got a fixed or user-created size so we will resize automatically
							instance.reposition();
						}
					}
				}
			}

			/**
			 * Reposition an auto-size dialog after ajax.
			 * @function
			 * @public
			 * @param {int} width The widthof the dialog.
			 * @param {int} height The widthof the dialog.
			 */
			this.reposition = function (width, height) {
				var dialog = this.getDialog(),
					removeResizeAnimClass,
					removeDragAnimClass;
				if (!dialog) {
					return;
				}

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
						positionable.setBySize(dialog, {width: width, height: height, topOffsetPC: INITIAL_TOP_PROPORTION});
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
			};

			/**
			 * Close a dialog.
			 * @function
			 * @public
			 */
			this.close = function() {
				var dialog = this.getDialog();
				if (dialog && !shed.isHidden(dialog)) {
					shed.hide(dialog);
					return true;
				}
				return false;
			};

			/**
			 * Listen for hide and clear out the transient aspects ofthe dialog. Do not remove any attributes or
			 * settings which may be needed by a consuming module (such as dimensions).
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being hidden.
			 */
			function shedHideSubscriber(element) {
				var control;
				try {
					if (element && element.id === DIALOG_ID) {
						modalShim.clearModal(element);

						// remove maximise from dialog so that the next dialog does not open maximised
						/*
						 * NOTE: this could be moved to wc/ui/resizeable.js which owns the max button. However, the
						 * behaviour of the maximised component should depend on the individual component. It is unlikely
						 * one would want to keep a control maximised once it had been closed but it is not impossible.
						 * Maybe this should be added to the regObject so we can re-maximise on open on a dialog-by-dialog
						 * basis.
						 */
						if (notMobile && MAX_BUTTON && (control = MAX_BUTTON.findDescendant(element)) && shed.isSelected(control)) {
							shed.deselect(control);
						}

						if (openerId && (control = document.getElementById(openerId))) {
							focus.setFocusRequest(control);
						}
					}
				}
				finally {
					openerId = null;
				}
			}

			/**
			 * Listen for shed.show and focus the dialog.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being shown.
			 */
			function shedShowSubscriber(element) {
				if (element && element === instance.getDialog()) {
					focus.focusFirstTabstop(element);
				}
			}

			/**
			 * Click listener for dialog opening buttons and controls within a dialog.
			 * @function
			 * @private
			 * @param {Event} $event a click event.
			 */
			function clickEvent($event) {
				var dialog;
				if (!$event.defaultPrevented && CLOSE_WD.findAncestor($event.target)) {
					dialog = document.getElementById(DIALOG_ID);
					if (dialog && !shed.isHidden(dialog)) {
						instance.close();
						$event.preventDefault();
					}
				}
			}

			/**
			 * A focus filter helper for tabKeyHelper.
			 * @function
			 * @private
			 * @param {Node} node The Node being tested.
			 * @returns {Number} the NodeFilter value for the tested node.
			 */
			function tabstopNodeFilter(node) {
				return focus.isTabstop(node) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
			}

			/**
			 * Helper for keydown on TAB.
			 *
			 * @param {Element} element The target element.
			 * @param {Elelemt} dialog A dialog frame.
			 * @param {Boolean} hasShift Was teh SHIFT key down during the event?
			 * @returns {Boolean} Returns true if we are refocussing the dialog (due to trying to TAB out of it).
			 */
			function tabKeyHelper(element, dialog, hasShift) {
				var tw,
					result = false;
				if (!hasShift && isModalDialog(dialog)) {
					tw = document.createTreeWalker(dialog, NodeFilter.SHOW_ELEMENT, tabstopNodeFilter, false);
					tw.lastChild();
					if (element === tw.currentNode) {
						result = true;
						focus.focusFirstTabstop(dialog);
					}
				}
				return result;
			}

			/**
			 * Key down listener. Inplements key patterns as per http://www.w3.org/TR/wai-aria-practices/#dialog_modal
			 * and http://www.w3.org/TR/wai-aria-practices/#dialog_nonmodal.
			 * @function
			 * @private
			 * @param {Event} $event A keydown event.
			 */
			function keydownEvent($event) {
				var element = $event.target,
					dialog,
					result = false,
					keyCode = $event.keyCode;
				if (!$event.defaultPrevented && (dialog = document.getElementById(DIALOG_ID)) && !shed.isHidden(dialog)) {
					switch (keyCode) {
						case KeyEvent.DOM_VK_ESCAPE:
							result = instance.close();
							break;
						case KeyEvent.DOM_VK_TAB:
							result = tabKeyHelper(element, dialog, $event.shiftKey);
							break;
						case KeyEvent.DOM_VK_F6:
							if (!isModalDialog(dialog) && openerId) {
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
			 * @function module:wc/ui/dialogFrame.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
			};

			/**
			 * Late initialisation to add ajax and shed subscribers.
			 * @function module:wc/ui/dialogFrame.postInit
			 * @public
			 */
			this.postInit = function() {
				processResponse.subscribe(preOpenSubscriber);
				processResponse.subscribe(ajaxSubscriber, true);
				shed.subscribe(shed.actions.SHOW, shedShowSubscriber);
				shed.subscribe(shed.actions.HIDE, shedHideSubscriber);
			};

			/**
			 * Get the widget which describes a dialog frame.
			 * @function module:wc/ui/dialogFrame.getWidget
			 * @public
			 * @returns {module:wc/dom.Widget} The Widget describing a dialog frame.
			 */
			this.getWidget = function() {
				return DIALOG;
			};

			/**
			 * Get a dialog if one exists.
			 * @function  module:wc/ui/dialogFrame.getDialog
			 * @public
			 * @returns {?Element} The dialog.
			 */
			this.getDialog = function() {
				return document.getElementById(DIALOG_ID);
			};

			/**
			 * Get the dialog content wrapper div.
			 *
			 * @function module:wc/ui/dialogFrame.getContent
			 * @public
			 * @returns {?Element} The content wrapper if present.
			 */
			this.getContent = function() {
				var dialog = this.getDialog();
				if (dialog) {
					return DIALOG_CONTENT_WRAPPER.findDescendant(dialog);
				}
				return null;
			};

			/**
			 * Reset the dialog content wrapper.
			 *
			 * @function
			 * @public
			 * @param {Boolean} [keepContent] Do we want to reset the content of the dialog?
			 * @param {String} [id] The id to set on the content.
			 */
			this.resetContent = function(keepContent, id) {
				var content = this.getContent();

				if (content) {
					content.removeAttribute("${wc.ui.ajax.attribute.getData}");
					content.id = id || "";
					content.className = CONTENT_BASE_CLASS;

					if (!keepContent) {
						content.innerHTML = ""; // Do we really still need this IE 6 fix?
						content.innerHTML = i18n.get("${wc.ui.loading.loadMessage}");
					}
				}
			};
		}

		var /** @alias module:wc/ui/dialog */ instance = new DialogFrame(),
			repainter;

		initialise.register(instance);

		if (has("ie") === 8) {
			require([ "wc/fix/inlineBlock_ie8" ], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}

		return instance;

		/**
		 * @typedef {Object} module:wc/ui/dialogFrame~dto An object which stores information about a dialog.
		 * @property {String} id The content id. If this is not set everything will fail.
		 * @property {String} [formId] The id of the form the dialog is in (more useful than you may think). If this is not set we will use the LAST form
		 * in the current view. You may not want this!
		 * @property {String} openerId The ID of the control which is opening the dialog.
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
		 *    the registration object but is not stored in the registry.
		 */

	});
