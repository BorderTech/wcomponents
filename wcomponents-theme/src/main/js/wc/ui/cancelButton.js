/**
 * Simple module to add cancelUpdate functionality to a form if a WCancelButton is added with an unsavedChanges
 * flag. This can be done in XSLT if the WCancelButton exists in the initial page load but not if it is inserted
 * via Ajax. We d not do it in XSLT because teh JavaScript is so much faster. We only need to do this once for
 * any screen with such a WCancelButton.
 *
 * @module
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/cancelUpdate
 */
define(["wc/dom/classList", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/ajax/processResponse", "wc/ui/cancelUpdate"],
	/* cancelUpdate is added as a requirement because any cancel button will need it implicitly */
	function(classList, initialise, Widget, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/cancelButton~CancelButton
		 * @private
		 */
		function CancelButton() {
			var CANCEL_BUTTON = new Widget("button", "wc_btn_cancel"),
				UNSAVED = "wc_unsaved",
				UNSAVED_BUTTON = CANCEL_BUTTON.extend(UNSAVED),
				FORM = new Widget("form");

			/**
			 * Mark the form as having unsaved changes if a falgged cancel button is inserted via ajax.
			 * @function
			 * @private
			 * @param {Element} element The AJAX target element.
			 */
			function ajaxSubscriber(element) {
				var form;
				if (element && (UNSAVED_BUTTON.isOneOfMe(element) || UNSAVED_BUTTON.findDescendant(element)) && (form = FORM.findAncestor(element))) {
					classList.add(form, UNSAVED);
				}
			}

			/**
			 * Get the {@link module:wc/dom/Widget} description of a cancel button.
			 * @function module:wc/ui/cancelButton.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget}
			 */
			this.getWidget = function() {
				return CANCEL_BUTTON;
			};

			/**
			 * Late initialisation to process any falgged cancel buttons and set up ajax subscribers.
			 * @function module:wc/ui/cancelButton.postInit
			 * @public
			 */
			this.postInit = function() {
				var button, form;
				if ((button = UNSAVED_BUTTON.findDescendant(document.body)) && (form = FORM.findAncestor(button))) {
					classList.add(form, UNSAVED);
				} else {
					processResponse.subscribe(ajaxSubscriber, true);
				}
			};
		}

		var /** * @alias module:wc/ui/cancelButton */ instance = new CancelButton();
		initialise.register(instance);

		return instance;
	});
