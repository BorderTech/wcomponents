/**
 * Module to provide client side redirects. Preferable not to use this but sometimes one has to. This module also makes
 * it possible and safe to use custom protocols and application launchers in inline links and is used for this by
 * {@link module:wc/ui/launchLink} and {@link module:wc/ui/navigationButton}.
 *
 * @module
 * @requires module:wc/dom/uid
 * @requires module:wc/timers
 */
define(["wc/dom/uid", "wc/timers"], /** @param uid wc/dom/uid @param timers wc/timers @ignore */ function(uid, timers) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/ui/redirect
	 * @private
	 */
	function Redirect() {
		var redirectFrameId = uid(),
			launchLinkTests = [isPseudoProtocol, isAttachmentLink],
			timer,
			ATTACHMENT_FLAG = "wc_content=attach",
			PSEUDO_PROTOCOL_RE = /^[\w]+\:[^\/].*$/;

		/**
		 * Is a url a link to a (known) atachment?
		 * @function
		 * @private
		 * @param {string} url A URL.
		 * @returns {boolean} true if this url is an attachment (prompts to save)
		 */
		function isAttachmentLink(url) {
			return url.indexOf(ATTACHMENT_FLAG) > 0;  // the url will never start with the attachment flag
		}

		/**
		 * Is a url a link to a (known) pseduo protocol? A pseudo protocol has no "/" characters after the protocol/scheme
		 * segment of the URL.
		 * @function
		 * @private
		 * @param {string} url A URL.
		 * @returns {boolean} true if this url is a pseduo protocol (like mailto:)
		 */
		function isPseudoProtocol(url) {
			return PSEUDO_PROTOCOL_RE.test(url);
		}

		/**
		 * Gets the redirect iframe, creates one on first use.
		 * @function
		 * @private
		 * @returns {Element} The redirect iframe.
		 */
		function getRedirectFrame() {
			var result = document.getElementById(redirectFrameId);
			if (!result) {
				result = document.createElement("iframe");
				result.setAttribute("id", redirectFrameId);
				result.style.display = "none";
				document.body.appendChild(result);
			}
			return result;
		}

		/**
		 * Do the redirect.
		 * @function
		 * @private
		 * @param {string} url The url to redirect to.
		 */
		function doRedirect(url) {
			var target;
			if (url) {
				if (timer) {
					timers.clearTimeout(timer);
				}
				timer = timers.setTimeout(function() {
					if (instance.isLaunchUrl(url)) {
						target = getRedirectFrame();
						target.src = url;
					}
					else {
						/* we use window.parent here to break out of iframe in multiFileUploader
						MDC: https://developer.mozilla.org/en/window.parent
						If a window does not have a parent, its parent property is a reference to itself.
						*/
						window.parent.location.href = url;
					}
				}, 50);
			}
		}

		/**
		 * Do the redirect. The intended use case is to launch custom protocols or attachments on page load.
		 * @function module:wc/ui/redirect.register
		 * @param {String} url The url to redirect to.
		 */
		this.register = function(url) {
			doRedirect(url);
		};

		/**
		 * Tries to determine if this URL will launch another application (attachment or pseudo protocol) or
		 * navigate the page itself. "Launch" is a pretty loose term here, it pretty much menas anything beside navigate.
		 * @function module:wc/ui/redirect.isLaunchUrl
		 * @param {String} url A URL.
		 * @returns {Boolean} true if the URL seems to be a "launch" URL rather than a navigate URL.
		 */
		this.isLaunchUrl = function(url) {
			var i, next, result;
			for (i = 0; i < launchLinkTests.length; i++) {
				next = launchLinkTests[i];
				try {
					result = next(url);
					if (result) {
						break;
					}
				}
				catch (ex) {
					console.error(ex);
				}
			}
			return result;
		};

		/**
		 * Adds a new launch link url test.
		 * @function module:wc/ui/redirect.addlaunchUrlTest
		 * @param {Function} test A function that takes a single string arg (url) and returns true if the url should be
		 * considered a launch link. In other words, do you want the URL to be loaded in the top (window) frame or a
		 * hidden iframe. This may change from platform to platform.
		 */
		this.addlaunchUrlTest = function(test) {
			if (test && test.call) {
				launchLinkTests.push(test);
			}
		};
	}
	var /** @alias module:wc/ui/redirect */ instance = new Redirect();  // keep instance - it is needed to make sense of an applied function.
	return instance;
});
