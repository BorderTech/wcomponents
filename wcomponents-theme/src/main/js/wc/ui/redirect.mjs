/**
 * Module to provide client side redirects.
 * Preferable not to use this but sometimes one has to.
 * This module also makes it possible and safe to use custom protocols
 * and application launchers in inline links and is used for this by
 * {@link module:wc/ui/launchLink} and {@link module:wc/ui/navigationButton}.
 *
 */
import uid from "wc/dom/uid.mjs";
import debounce from "wc/debounce.mjs";

const redirectFrameId = uid(),
	launchLinkTests = [isPseudoProtocol, isAttachmentLink],
	ATTACHMENT_FLAG = "wc_content=attach",
	PSEUDO_PROTOCOL_RE = /^\w+:[^/].*$/;

const doRedirect = debounce(/**
	 * Do the redirect.
	 * @function
	 * @private
	 * @param {string} url The url to redirect to.
	 */
	function(url) {
		if (instance.isLaunchUrl(url)) {
			const target = getRedirectFrame();
			target.src = url;
		} else {
			/* we use window.parent here to break out of iframe in multiFileUploader
			MDC: https://developer.mozilla.org/en/window.parent
			If a window does not have a parent, its parent property is a reference to itself.
			*/
			window.parent.location.replace(url);
		}
	}, 50);

const instance = {
	/**
	 * Do the redirect. The intended use case is to launch custom protocols or attachments on page load.
	 * @param {String} url The url to redirect to.
	 */
	register: url => {
		if (url) {
			doRedirect(url);
		}
	},

	/**
	 * Tries to determine if this URL will launch another application (attachment or pseudo protocol) or
	 * navigate the page itself. "Launch" is a pretty loose term here, it pretty much means anything beside navigate.
	 * @param {String} url A URL.
	 * @returns {Boolean} true if the URL seems to be a "launch" URL rather than a navigate URL.
	 */
	isLaunchUrl: function(url) {
		let result;
		for (let i = 0; i < launchLinkTests.length; i++) {
			let next = launchLinkTests[i];
			try {
				result = next(url);
				if (result) {
					break;
				}
			} catch (ex) {
				console.error(ex);
			}
		}
		return result;
	}
};

/**
 * Is this url a link to a (known) attachment?
 * @function
 * @private
 * @param {string} url A URL.
 * @returns {boolean} true if this url is an attachment (prompts to save)
 */
function isAttachmentLink(url) {
	return url.indexOf(ATTACHMENT_FLAG) > 0;  // the url will never start with the attachment flag
}

/**
 * Is this url a link to a (known) pseudo protocol? A pseudo protocol has no "/" characters after the protocol/scheme
 * segment of the URL.
 * @function
 * @private
 * @param {string} url A URL.
 * @returns {boolean} true if this url is a pseudo protocol (like mailto:)
 */
function isPseudoProtocol(url) {
	return PSEUDO_PROTOCOL_RE.test(url);
}

/**
 * Gets the redirect iframe, creates one on first use.
 * @function
 * @private
 * @returns {HTMLIFrameElement} The redirect iframe.
 */
function getRedirectFrame() {
	let result = /** @type {HTMLIFrameElement} */ (document.getElementById(redirectFrameId));
	if (!result) {
		result = document.createElement("iframe");
		result.setAttribute("id", redirectFrameId);
		result.style.display = "none";
		document.body.appendChild(result);
	}
	return result;
}

const redirectTag = "wc-redirect";
class WRedirect extends HTMLElement {
	connectedCallback() {
		instance.register(this.getAttribute("url"));
	}
}

if (!customElements.get(redirectTag)) {
	customElements.define(redirectTag, WRedirect);
}

export default instance;
