/**
 * Provides "dialog" functionality.  NOTE: we currently use a custom dialog because IE native dialog does not call and
 * parse xslt (as of IE10). This is not an issue in ff3.6+ or Chrome 6 but these do not support showModelessDialog.
 *
 * The custom dialog also provides somewhat better options for mobile use and cross platform consistency.
 *
 * @module
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/timers
 * @requires module:wc/ui/dialogFrame
 *
 * @todo Re-order source, document private members.
 */
define(["wc/dom/classList",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/ui/ajaxRegion",
		"wc/ui/ajax/processResponse",
		"wc/ui/containerload",
		"wc/timers",
		"wc/ui/dialogFrame"],

	function(classList, event, initialise, shed, Widget, i18n, ajaxRegion, processResponse, eagerLoader, timers, dialogFrame) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/dialog~Dialog
		 * @private
		 */
		function Dialog() {
			var BUTTON = new Widget("button"),
				ANCHOR,
				OPENER = BUTTON.extend("", {"data-wc-dialogconf": null}),
				registry = {},
				UNIT = "px",
				emptyOnClose = true,
				openOnLoadTimer,
				openThisDialog,
				GET_ATTRIB = "${wc.ui.ajax.attribute.getData}";

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
			 * Action click events within the dialog.
			 * @function
			 * @private
			 * @param {Element} element The element which was clicked.
			 */
			function activateClick(element) {
				var isTrigger,
					_element,
					content,
					trigger,
					targets,
					dialog = dialogFrame.getDialog();

				/*
				 * array.some filter function for ajax targets
				 * @function
				 * @private
				 * @param {String} id The id of the target element.
				 * @returns {Boolean} true if the target element is inside the dialog content.
				 */
				function _targetInsideDialog(id) {
					var element;
					if (id && (element = document.getElementById(id))) {
						return !!(content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY);
					}
					return false;
				}

				if (dialog && !shed.isHidden(dialog)) {
					content = dialogFrame.getContent();
					if (!content) {
						console.error("Found open dialog but not its content");
						return false;
					}

					if (content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY) { // we are inside a dialog's content
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
							emptyOnClose = false;
							dialogFrame.close();  // NOTE: do not set result to true or you will prevent the AJAX action!
						}
					}
				}
				else if ((_element = OPENER.findAncestor(element)) && !shed.isDisabled(_element)) {
					openDlg(_element.getAttribute("data-wc-dialogconf"), _element.id);
					return true; // prevent the click default action.
				}
				return false;
			}

			/**
			 * Open a dialog.
			 * @function
			 * @private
			 * @param {String} id The id of the WDialog to open.
			 * @param {String} [_openerId] The id of the button used to launch the dialog if known.
			 * @param {String} [openThisDialog] The id of a particular dialog to open.
			 */
			function openDlg(id, _openerId) {
				var regObj = registry[id];

				function populateOnLoad() {
					var content = dialogFrame.getContent(),
						openerId,
						opener;
					if (content) {
						content.id = regObj.id;
						if ((openerId = regObj.openerId)) {
							opener = document.getElementById(openerId);
							content.setAttribute(GET_ATTRIB, openerId + "=" + (opener ? opener.value : "x"));
						}
						else {
							content.removeAttribute(GET_ATTRIB);
						}
						classList.add(content, "wc_magic");
						classList.add(content, "wc_dynamic");
						eagerLoader.load(content, false, true);
					}
					else {
						console.warn("Could not find dialog content wrapper.");
					}
				}

				if (regObj) {
					if (_openerId !== regObj.openerId) {
						regObj.openerId = _openerId;
					}

					dialogFrame.open(regObj).then(populateOnLoad).catch(function(err) {
						console.warn(err);
					});
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
				var regObj;
				if (element && element === dialogFrame.getContent() && element.id && (regObj = registry[element.id])) {
					// set the initial position
					if (!(regObj.top || regObj.left || regObj.top === 0 || regObj.left === 0)) {
						dialogFrame.reposition({width: regObj.width, height: regObj.height});
					}
				}
			}

			function saveDialogDimensions(element, regObj) {
				if (element.style.width) {
					regObj["width"] = element.style.width.replace(UNIT, "");
				}
				if (element.style.height) {
					regObj["height"] = element.style.height.replace(UNIT, "");
				}
				if (element.style.left) {
					regObj["left"] = element.style.left.replace(UNIT, "");
				}
				if (element.style.top) {
					regObj["top"] = element.style.top.replace(UNIT, "");
				}
			}

			/**
			 * Listen for shed.hide and clear out the transient aspects of the dialog.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being hidden.
			 */
			function shedSubscriber(element) {
				var content,
					id,
					regObj;
				if (element && element === dialogFrame.getDialog() && (content = dialogFrame.getContent()) && (id = content.id) && (regObj = registry[id])) { // we are ONLY interested in WDialog inited dialogs.
					try {
						saveDialogDimensions(element, regObj);
						/*
						 * NOTE: clear the content and dimensions AFTER resetting all the registry settings.
						 */
						dialogFrame.unsetAllDimensions();
						dialogFrame.resetContent(emptyOnClose, (emptyOnClose ? "" : regObj.id));
					}
					finally {
						emptyOnClose = true;
					}
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
		}

		var /** @alias module:wc/ui/dialog */ instance = new Dialog();

		initialise.register(instance);

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
