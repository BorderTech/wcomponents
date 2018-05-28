define(["wc/dom/event",
	"wc/dom/focus",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/uid",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/ui/ajax/processResponse",
	"wc/ui/modalShim",
	"wc/timers",
	"wc/has",
	"wc/ui/resizeable",
	"wc/ui/positionable",
	"wc/ui/draggable",
	"wc/dom/role",
	"wc/template",
	"wc/ui/viewportUtils",
	"wc/ui/getForm",
	"wc/config"],
	function (event, focus, initialise, shed, uid, Widget, i18n, processResponse, modalShim, timers, has, resizeable, positionable, draggable, $role,
		template, viewportUtils, getForm, wcconfig) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/dialogFrame~DialogFrame
		 * @private
		 */
		function DialogFrame() {
			var DIALOG_ID = "wc_dlgid",
				CONTENT_BASE_CLASS = "content",
				// when setting the initial position offset the dialog so that the gap at the top is this proportion of the difference between
				// the dialog size and viewport size
				INITIAL_TOP_PROPORTION = 0.33,
				openerId,
				subscriber = {
					close: null
				},
				DIALOG = new Widget("dialog"),
				DIALOG_CONTENT_WRAPPER = new Widget("div", CONTENT_BASE_CLASS, {"aria-live": "assertive"}),
				BUTTON = new Widget("button"),
				CLOSE_WD = BUTTON.extend("wc_dialog_close"),
				RESIZERS,
				RESIZE_WD,
				MAX_BUTTON,
				HEADER_WD = new Widget("header"),
				TITLE_WD = new Widget("h1"),
				BUSY,
				UNIT = "px",
				repositionTimer,
				REJECT = {
					ALREADY_OPEN: "Cannot open a dialog whilst another dialog is open",
					CANNOT_BUILD: "Cannot create the dialog frame",
					NO_FORM: "Cannot find a form to which to attach the dialog",
					UNKNOWN: "Failed to open dialog: readon unknown"
				},
				resizeTimeout;

			TITLE_WD.descendFrom(HEADER_WD);
			DIALOG_CONTENT_WRAPPER.descendFrom(DIALOG, true);
			RESIZERS = resizeable.getWidget();
			RESIZE_WD = RESIZERS.handle;
			MAX_BUTTON = RESIZERS.maximise;

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
			 * Indicates if the dialogFrame may support move and resize based on viewport size.
			 *
			 * @function
			 * @private
			 * @returns {Boolean} true is move/resize are supportable.
			 */
			function canMoveResize() {
				var conf = wcconfig.get("wc/ui/dialogFrame", {
						vpUtil: null
					}),
					func = "isPhoneLike";

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
				var content = instance.getContent();
				if (content) {
					BUSY = BUSY || new Widget("","",{"aria-busy": "true"});
					return !!BUSY.findDescendant(content);
				}
				return false;
			}

			/**
			 * Get the form into which we want to place the dialog.
			 *
			 * @function
			 * @private
			 * @param {wc/ui/dialogFrame~dto} [dto] The config options for the dialog (if any).
			 * @returns {Element} The form element.
			 */
			function getDlgForm(dto) {
				var formId = (dto ? (dto.formId || dto.openerId) : null),
					el;

				if (formId) {
					el = document.getElementById(formId);
				}
				return getForm(el);
			}

			/**
			 * Request a dialog be opened.
			 *
			 * @function module:wc/ui/dialogFrame.open
			 * @public
			 * @param {module:wc/ui/dialogFrame~dto} dto The config options for the dialog to be opened.
			 * @returns {Promise} The promise will be a rejection if the dialog is not able to be opened.
			 */
			this.open = function (dto) {
				var dialog = this.getDialog(),
					form, formId;

				if (dialog) {
					if (!this.isOpen(dialog)) {
						return openDlgHelper(dto);
					}
					return Promise.reject(REJECT.ALREADY_OPEN);
				} else if ((form = getDlgForm(dto))) {
					formId = form.id || (form.id = uid());

					if (formId) {
						return buildDialog(formId).then(function () {
							return openDlgHelper(dto);
						});
					}
					return Promise.reject(REJECT.NO_FORM);
				}
				return Promise.reject(REJECT.UNKNOWN);
			};

			/**
			 * Determine if the dialog is already open.
			 * @param {Element} [element] Optionally provide the dialog element.
			 * @returns {boolean} true if the dialog is open.
			 */
			this.isOpen = function (element) {
				var dialog = element || this.getDialog();
				return (dialog && !shed.isHidden(dialog, true));
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
				return i18n.translate("dialog_noTitle").then(function(defaultTitle) {
					var effectiveDto = dto || {},
						dialog = instance.getDialog();

					if (dialog && !instance.isOpen(dialog)) {
						if (effectiveDto.openerId) {
							openerId = effectiveDto.openerId;
						} else {
							openerId = document.activeElement ? document.activeElement.id : null;
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
			 * @param dialog The dialog container.
			 * @param isModal Indicates if this dialog is modal.
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
			 * @param {Element} dialog The dialog container.
			 * @param {module:wc/ui/dialogFrame~dto} obj The registry item that contains configuration data for this dialog.
			 */
			function reinitializeDialog(dialog, obj) {
				var title, isModal;

				instance.unsetAllDimensions(dialog);
				dialog.className = (obj.className || "");
				instance.resetContent(false, (obj.id  || ""));
				// set the dialog title
				if ((title = TITLE_WD.findDescendant(dialog))) {
					title.innerHTML = ""; // ??? This _cannot_ really still be needed?
					title.innerHTML = obj.title;
				}
				subscriber.close = obj.onclose;
				initDialogControls(dialog, obj);
				initDialogDimensions(dialog, obj);
				isModal = (typeof obj.modal !== "undefined") ? obj.modal : true;
				setModality(dialog, isModal);
			}

			/**
			 * Show and hide resizeable and draggable controls based on the dialogFrame's properties and the current
			 * viewport size.
			 *
			 * @function
			 * @private
			 * @param {Element} dialog The dialogFrame being manipulated.
			 */
			function setUpMoveResizeControls(dialog) {
				var control;

				if ((control = HEADER_WD.findDescendant(dialog, true))) {
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
			 * @param {Element} dialog The dialogFrame being manipulated.
			 */
			function setUnsetDimensionsPosition(dialog) {
				var animationsDisabled;

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
			 * Initializes the dialog's control buttons.
			 * @param dialog The dialog container.
			 * @param obj The registry item that contains configuration data for this dialog.
			 * @private
			 * @function
			 */
			function initDialogControls(dialog, obj) {
				var control;

				setUpMoveResizeControls(dialog);
				if (obj.max && (control = MAX_BUTTON.findDescendant(dialog))) {
					shed.select(control);
				}
			}

			/**
			 * Remove all inline dimension styles from the dialog.
			 *
			 * @function module:wc/ui/dialogFrame.unsetAllDimensions
			 * @public
			 * @param {Element} [dlg] The dialog wrapper element if known.
			 */
			this.unsetAllDimensions = function (dlg) {
				var dialog = dlg || this.getDialog();
				if (dialog) {
					dialog.style.width = "";
					dialog.style.height = "";
					dialog.style.margin = "";
					positionable.clear(dialog);
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
				if (obj.top || obj.top === 0) {
					dialog.style.top = obj.top + UNIT;
				}
				if (obj.left || obj.left === 0) {
					dialog.style.left = obj.left + UNIT;
					dialog.style.margin = "0";
				}
				setUnsetDimensionsPosition(dialog);
			}

			function getResizeConfig(width, height) {
				var globalConf = wcconfig.get("wc/ui/dialogFrame", {}),
					offset = INITIAL_TOP_PROPORTION;

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
				return { width: width, height: height, topOffsetPC: offset };
			}

			/**
			 * Helper for `openDlg`.
			 * Positions the dialog immediately after it has been opened.
			 *
			 * @private
			 * @function
			 * @param {Element} dialog The dialog container.
			 * @param {module:wc/ui/dialogFrame~dto} obj The registry item that contains configuration data for this dialog.
			 */
			function initDialogPosition(dialog, obj) {
				var disabledAnimations, configObj;
				try {
					if (obj) {
						configObj = getResizeConfig(obj.width, obj.height);
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
			 * @returns {Promise} resolved with {Element} dialog The dialog element.
			 */
			function buildDialog(formId) {
				return new Promise(function(win, lose) {
					i18n.translate(["dialog_maxRestore", "dialog_close", "loading", "dialog_move", "dialog_resize"]).then(function(translations) {
						var done = function () {
								var dialog,
									dialogHeader,
									resizeHandle,
									headerTitle,
									resizeHandleTitle;
								if ((dialog = instance.getDialog())) {
									event.add(dialog, event.TYPE.keydown, keydownEvent);
									if ((dialogHeader = HEADER_WD.findDescendant(dialog, true)) && (headerTitle = translations[3])) {
										dialogHeader.title = headerTitle;
									}

									if (RESIZE_WD && (resizeHandle = RESIZE_WD.findDescendant(dialog)) && (resizeHandleTitle = translations[4])) {
										resizeHandle.title = resizeHandleTitle;
									}
									win(dialog);
								} else {
									lose(null);
								}
							},
							form, dialogProps = {
								heading: {
									maxRestore: translations[0],
									close: translations[1]
								},
								message: {
									loading: translations[2]
								}
							};

						if (formId) {
							form = document.getElementById(formId);
						}
						form = getForm(form);
						if (!form) {
							console.error("Cannot find form for dialog frame");
							lose(null);
							return null;
						}

						template.process({
							source: "dialog.xml",
							loadSource: true,
							target: form,
							context: dialogProps,
							position: "beforeEnd",
							callback: done,
							errback: lose
						});
					});
				});
			}

			/**
			 * If a dialog with content is inserted via ajax we have to unshim any existing dialog before we insert the
			 * new one. NOTE: the duplicate id check in processResponse will remove the dialog itself during its insert
			 * phase so we do not have to do that here.
			 *
			 * @function
			 * @private
			 * @param {Element} element Not used here.
			 * @param {documentFragment} docFragment The content of the AJAX response.
			 */
			function preOpenSubscriber(element, docFragment) {
				var removeShim = false,
					dialog;
				if (docFragment.querySelector) {
					if (docFragment.querySelector("#" + DIALOG_ID)) {
						removeShim = true;
					}
				} else if (docFragment.getElementById && docFragment.getElementById(DIALOG_ID)) {
					removeShim = true;
				}
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
				var content, dialog;

				if (element && (content = instance.getContent()) && content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY) {
					dialog = instance.getDialog();
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
			 * @param {Element} element the dialog frame to reposition.
			 * @param {Object} obj a description of the dialog.
			 * @param {int} [obj.width] the dialog width
			 * @param {int} [obj.height] the dialog height
			 * @param {int} [obj.topOffsetPC] the offset from the top of the dialog
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
					if (repainter) {
						repainter.checkRepaint(element);
					}
				} finally {
					if (!animate) {
						resizeable.restoreAnimation(element);
					}
				}
			}
			/**
			 * Ask to reposition a dialog frame (usually after Ajax).
			 *
			 * @function module:wc/ui/dialogFrame.reposition
			 * @public
			 * @param {int} [width] The width of the dialog.
			 * @param {int} [height] The height of the dialog.
			 */
			this.reposition = function (width, height, animate) {
				var dialog;

				if (repositionTimer) {
					timers.clearTimeout(repositionTimer);
					repositionTimer = null;
				}
				if (!(dialog = this.getDialog())) {
					return;
				}

				if (canMoveResize()) {
					repositionTimer = timers.setTimeout(setPositionBySize, 100, dialog, getResizeConfig(width, height), animate);
				}
			};

			/**
			 * Close a dialog frame.
			 * @function module:wc/ui/dialogFrame.close
			 * @public
			 * @returns {boolean} true if there is a dialog to hide.
			 */
			this.close = function () {
				var dialog = this.getDialog();
				if (dialog && this.isOpen(dialog)) {
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
				var control, callback, clearOpener;
				try {
					if (element && element.id === DIALOG_ID) {
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
						if (MAX_BUTTON && (control = MAX_BUTTON.findDescendant(element)) && shed.isSelected(control)) {
							shed.deselect(control);
						}

						if (openerId && (control = document.getElementById(openerId))) {
							focus.setFocusRequest(control);
						}
						if (subscriber.close) {
							try {
								callback = subscriber.close;
								subscriber.close = null;
								callback();
							} catch (ex) {
								console.warn(ex);
							}
						}
					}
				} finally {
					if (clearOpener) {
						openerId = null;
					}
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
			 *
			 * @function
			 * @private
			 * @param {Event} $event a click event.
			 */
			function clickEvent($event) {
				var dialog;
				if (!$event.defaultPrevented && CLOSE_WD.findAncestor($event.target)) {
					dialog = document.getElementById(DIALOG_ID);
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
			 * @param {Node} node The Node being tested.
			 * @returns {Number} the NodeFilter value for the tested node.
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
				if (!$event.defaultPrevented && (dialog = document.getElementById(DIALOG_ID)) && instance.isOpen(dialog)) {
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
			 * Do the heavy lifting of the resize event. Called in a timeout so we do not do constant updates as a
			 * window frame is dragged.
			 *
			 * @function
			 * @private
			 */
			function resizeEventHelper() {
				var dialog = document.getElementById(DIALOG_ID);

				if (!dialog || !instance.isOpen(dialog)) {
					return;
				}
				setUnsetDimensionsPosition(dialog);
				setUpMoveResizeControls(dialog);
			}

			/**
			 * Adjust dialog to the screen.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The resize event.
			 */
			function resizeEvent($event) {
				if ($event.defaultPrevented) {
					return;
				}

				if (resizeTimeout) {
					timers.clearTimeout(resizeTimeout);
				}
				resizeTimeout = timers.setTimeout(resizeEventHelper, 10);
			}

			/**
			 * Component initialisation simply attaches a click event handler
			 * @function module:wc/ui/dialogFrame.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function (element) {
				event.add(element, event.TYPE.click, clickEvent);
				event.add(window, event.TYPE.resize, resizeEvent, -1);
			};

			/**
			 * Late initialisation to add ajax and shed subscribers.
			 * @function module:wc/ui/dialogFrame.postInit
			 * @public
			 */
			this.postInit = function () {
				processResponse.subscribe(preOpenSubscriber);
				processResponse.subscribe(ajaxSubscriber, true);
				shed.subscribe(shed.actions.SHOW, shedShowSubscriber);
				shed.subscribe(shed.actions.HIDE, shedHideSubscriber);
			};

			/**
			 * Get the widget which describes a dialog frame.
			 * @function module:wc/ui/dialogFrame.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget} The Widget describing a dialog frame.
			 */
			this.getWidget = function () {
				return DIALOG;
			};

			/**
			 * Get a dialog if one exists.
			 * @function  module:wc/ui/dialogFrame.getDialog
			 * @public
			 * @returns {Element} The dialog.
			 */
			this.getDialog = function () {
				return document.getElementById(DIALOG_ID);
			};

			/**
			 * Get the dialog content wrapper element.
			 *
			 * @function module:wc/ui/dialogFrame.getContent
			 * @public
			 * @returns {Element} The content wrapper if present.
			 */
			this.getContent = function () {
				var dialog = this.getDialog();
				if (dialog) {
					return DIALOG_CONTENT_WRAPPER.findDescendant(dialog);
				}
				return null;
			};

			/**
			 * Reset the dialog content wrapper.
			 *
			 * @function module:wc/ui/dialogFrame.resetContent
			 * @public
			 * @param {Boolean} [keepContent] Do we want to reset the content of the dialog?
			 * @param {String} [id] The id to set on the content.
			 */
			this.resetContent = function (keepContent, id) {
				var content = this.getContent();

				if (content) {
					content.removeAttribute("data-wc-get");
					content.id = id || "";
					content.className = CONTENT_BASE_CLASS;

					if (!keepContent) {
						content.innerHTML = ""; // Do we really still need this IE 6 fix?
						i18n.translate("loading").then(function(loadingText) {
							content.innerHTML = loadingText;
						});
					}
				}
			};
		}

		/**
		 * Provides a re-usable frame for floating dialog-like controls.
		 *
		 * * Implements WAI-ARIA practies for:
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
		 *
		 *
		 * @module
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/uid
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/ui/modalShim
		 * @requires module:wc/timers
		 * @requires module:wc/has
		 * @requires module:wc/ui/resizeable
		 * @requires module:wc/ui/positionable
		 * @requires module:wc/ui/draggable
		 * @requires module:wc/dom/role
		 * @requires module:wc/template
		 * @requires module:wc/ui/viewportUtils
		 * @requires module:wc/ui/getForm
		 */
		var instance = new DialogFrame(),
			repainter;

		initialise.register(instance);

		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function (inlineBlock) {
				repainter = inlineBlock;
			});
		}

		return instance;

		/**
		 * @typedef {Object} module:wc/ui/dialogFrame~dto An object which stores information about a dialog.
		 * @property {String} id The content id. If this is not set everything will fail.
		 * @property {String} [formId] The id of the form the dialog is in (more useful than you may think). If this is not set we will use the LAST
		 *   form in the current view. You may not want this!
		 * @property {String} openerId The ID of the control which is opening the dialog.
		 * @property {int} [width] The dialog width in px.
		 * @property {int} [height] The dialog height in px.
		 * @property {Boolean} [resizeable] Is the dialog resizeable?
		 * @property {Boolean} [modal] Is the dialog modal?
		 * @property {String} [title] The WDialog title. If not set a default title is used.
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

	});
