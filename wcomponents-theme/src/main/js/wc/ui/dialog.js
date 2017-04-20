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
				// OPENER = BUTTON.extend("", {"data-wc-dialogconf": null}),
				BASE_CLASS = "wc-dialog",
				registry = {},
				registryByDialogId = {},
				UNIT = "px",
				keepContentOnClose = false,
				openOnLoadTimer,
				openThisDialog,
				GET_ATTRIB = "data-wc-get";

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
				var triggerId = dialogObj.triggerid || dialogObj.id;

				if (triggerId) {
					registry[triggerId] = {
						id: dialogObj.id,
						className: BASE_CLASS + (dialogObj.className ? (" " + dialogObj.className) : ""),
						formId: dialogObj.form,
						width: dialogObj.width,
						height: dialogObj.height,
						initWidth: dialogObj.width,  // useful if we do not allow resize below initial size
						initHeight: dialogObj.height,
						modal: dialogObj.modal || false,
						openerId: dialogObj.triggerid,
						title: dialogObj.title || i18n.get("dialog_noTitle")
					};
					registryByDialogId[dialogObj.id] = triggerId;

					if (dialogObj.open) {
						openThisDialog = triggerId;
					}
				}
			}

			/*
			 * Is this element inside the dialog content?
			 * @function
			 * @private
			 * @param {String} id The id of the element to test.
			 */
			function isInsideDialog(id) {
				var element, content = dialogFrame.getContent();
				if (content && id && (element = document.getElementById(id))) {
					return !!(content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY);
				}
				return false;
			}

			/**
			 * Find a dialog opener from a given start point.
			 *
			 * @function
			 * @private
			 * @param {Element} element the start element
			 * @param {boolean} ignoreAncestor if {@code} true then stop without checking ancestors for a trigger
			 * @returns {?Element} a dialog trigger element if found
			 */
			function getTrigger(element, ignoreAncestor) {
				var parent = element,
					id = parent.id;
				if (registry[id]) {
					return element;
				}
				if (ignoreAncestor) {
					return null;
				}
				while ((parent = parent.parentNode) && parent.nodeType === Node.ELEMENT_NODE) {
					if ((id = parent.id)) {
						if (registry[id]) {
							return parent;
						}
					}
				}
				return null;
			}

			/**
			 * We need to know if an element is a submit element so that we can prevent the submit action if it opens a dialog.
			 * @function
			 * @private
			 * @param {Element} element the element to test
			 * @returns {Boolean} {@code true} if the element is a submitting element
			 */
			function isSubmitElement(element) {
				var result = false,
					type = element.type;
				if (type === "submit" || type === "image") {
					result = true;
				}
				return result;
			}

			/**
			 * Action click events on a dialog trigger or within a dialog.
			 * @function
			 * @private
			 * @param {Element} element The element which was clicked.
			 * @returns {boolean} {@code true} if the click is activated and we _may_ want to prevent the default action
			 */
			function activateClick(element) {
				var _element,
					content,
					trigger,
					targets,
					dialog = dialogFrame.getDialog();

				// Are we opening a dialog?
				if ((_element = getTrigger(element)) && !isInsideDialog(element.id)) {
					if (shed.isDisabled(_element)) {
						return false;
					}
					instance.open(_element);
					return isSubmitElement(_element);
				}

				if (!(
					dialog &&
					!shed.isHidden(dialog, true) &&
					(content = dialogFrame.getContent()) &&
					(content.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY)
				)) {
					// we are not inside a dialog's content.
					return false;
				}

				keepContentOnClose = false;
				// we need to know if a click is on an ajax trigger inside a dialog
				if ((trigger = ajaxRegion.getTrigger(element, true))) {
					_element = element;
				}
				else {
					// this is a chrome thing: it honours clicks on img elements and does not pass them through to the underlying link/button
					ANCHOR = ANCHOR || new Widget("A");
					_element = Widget.findAncestor(element, [ BUTTON, ANCHOR ]);
					if (_element) {
						trigger = ajaxRegion.getTrigger(_element, true);
					}
				}

				if (!trigger) {
					return false;
				}
				targets = trigger.loads;

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
			 * @param {String} triggerId The id of the trigger.
			 */
			function openDlg(triggerId) {
				var regObj = registry[triggerId];

				function populateOnLoad() {
					var content = dialogFrame.getContent(),
						openerId,
						opener;
					if (content) {
						content.id = regObj.id;
						if (!(openThisDialog && openThisDialog === triggerId) && (openerId = regObj.openerId)) {
							opener = document.getElementById(openerId);
							content.setAttribute(GET_ATTRIB, openerId + "=" + (opener ? encodeURIComponent(opener.value) : "x"));
						}
						else {
							content.removeAttribute(GET_ATTRIB);
						}
						classList.add(content, "wc_magic");
						classList.add(content, "wc_dynamic");
						eagerLoader.load(content, false, false);
					}
					else {
						console.warn("Could not find dialog content wrapper.");
					}
					openThisDialog = null;
				}

				if (regObj) {
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
			 * @param {Element} element The root element from the ajax response.
			 */
			function postOpenSubscriber(element) {
				var regObj;
				if (element && element === dialogFrame.getContent() && element.id && (regObj = getRegistryObjectByDialogId(element.id))) {
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
			}

			/**
			 * Get a registry object based on a WDialog id attribute.
			 * @param {String} id the ID of the WDialog to get.
			 * @returns {?module:wc/ui/dialog~regObject} the registry object if found.
			 */
			function getRegistryObjectByDialogId(id) {
				var triggerId = registryByDialogId[id];
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
			 * @param {Element} element The element being hidden.
			 */
			function shedSubscriber(element) {
				var content,
					id,
					regObj;
				if (element && element === dialogFrame.getDialog() && (content = dialogFrame.getContent()) && (id = content.id) && (regObj = getRegistryObjectByDialogId(id))) { // we are ONLY interested in WDialog inited dialogs.
					try {
						saveDialogDimensions(element, regObj);
						/*
						 * NOTE: clear the content and dimensions AFTER resetting all the registry settings.
						 */
						dialogFrame.unsetAllDimensions();
						dialogFrame.resetContent(keepContentOnClose, (keepContentOnClose ? "" : regObj.id));
					}
					finally {
						keepContentOnClose = false;
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
				if ($event.defaultPrevented) {
					return;
				}
				if (activateClick($event.target)) {
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

			/**
			 * Open a dialog for a given trigger.
			 * @function module:wc/ui/dialog.open
			 * @public
			 * @param {Element} trigger an element which _should_ be a dialog trigger.
			 * @returns {boolean} true if the element will trigger a dialog on change or click.
			 */
			this.open = function(trigger) {
				var element = getTrigger(trigger);
				if (element) {
					openDlg(element.id);
					return true;
				}
				return false;
			};
		}
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
		var instance = new Dialog();

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
		 * @property {Boolean} [modal] Is the dialog modal?
		 * @property {String} [title] The WDialog title. If not set a default title is used.
		 * @property {Boolean} [open] If true then the dialog is to be open on page load. This is passed in as part of
		 *    the registration object but is not stroed in the registry.
		 */
	});
