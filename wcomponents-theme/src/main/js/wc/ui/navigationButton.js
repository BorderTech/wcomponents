/**
 * Allows a button to be a link by using its value as the href. The reason we do this instead of just styling a link to
 * look like a button is because we do not know how the button will appear - do we style to button to look like a button
 * in Gnome? KDE? Windows XP? Mac? So we need to use a real button and make it behave like a link.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/popup
 * @requires module:wc/dom/cancelUpdate
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/launchLink
 * @requires module:wc/ui/redirect
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/popup", "wc/dom/cancelUpdate", "wc/dom/shed", "wc/ui/launchLink", "wc/ui/redirect"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param popup wc/ui/popup @param cancelUpdate wc/dom/cancelUpdate @param shed wc/dom/shed @param launchLink wc/ui/launchLink @param redirect wc/ui/redirect @ignore */
	function(event, initialise, Widget, popup, cancelUpdate, shed, launchLink, redirect) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/navigationButton~NavigationButton
		 * @private
		 */
		function NavigationButton() {
			var BUTTON = new Widget("button", "", {"${wc.ui.link.attrib.url.standin}": null}),
				CANCEL_LINK = BUTTON.extend("wc_btn_cancel"),
				FORM = new Widget("form");

			/**
			 * Click listener which intercepts the button action and instigates navigation as the action.
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var element, url, form;
				if (!$event.defaultPrevented && (element = BUTTON.findAncestor($event.target)) && !shed.isDisabled(element) && (url = element.getAttribute("${wc.ui.link.attrib.url.standin}")) && !popup.isOneOfMe(element) && !launchLink.isInlineLink(element)) {
					if (CANCEL_LINK.isOneOfMe(element) && (form = FORM.findAncestor(element)) && cancelUpdate.cancelSubmission(form)) {
						$event.preventDefault();
					}
					else if (redirect.isLaunchUrl(url)) {
						$event.preventDefault();  // since we have prevented the link action we're going to have to do it ourselves
						redirect.register(url);  // redirect (poorly named) knows how to "do stuff" with URLs.
					}
					else {
						window.location.href = url;
						$event.preventDefault();
					}
				}
			}

			/**
			 * Initialise the functionality by adding a click listener.
			 * @function  module:wc/ui/navigationButton.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
			};
		}

		var /** @alias module:wc/ui/navigationButton */ instance = new NavigationButton();
		initialise.register(instance);
		return instance;
	});
