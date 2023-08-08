/**
 * Display a warning to the user before their session expires. This is an accessibility requirement.
 *
 * Things to consider:
 *
 * * What renews the session? An AJAX request? Loading an image?
 * * Can we be sure that those actions will always renew the session? What if the resource is loaded from cache
 *   instead of hitting the server... then the session will not renew.
 *
 * There will always be a chance of getting the session timeout wrong, but there is wrong and then
 * there is WRONG. It is better to warn the user too early rather than too late.
 *
 */
import sprintf from "lib/sprintf";
import i18n from "wc/i18n/i18n";
import timers from "wc/timers";
import wcconfig from "wc/config";

const instance = new TimeoutWarner();

class TimeoutWarn extends HTMLElement {
	// These static properties are too new for the current build tools to handle them (revisit when r.js is gone)
	// static tagName = "wc-session";
	// static observedAttributes = ["src", "title"];

	static get observedAttributes() {
		return [ "timeout", "warn", "hidden" ];
	}

	constructor() {
		super();
		/*
			alertdialog is the correct aria role for a session expiry warning.
			In fact that is "Example 1" on the MDN alertdialog page:
			https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Roles/alertdialog_role#example_1_a_basic_alert_dialog
		 */
		this.setAttribute("role", "alertdialog");
		this.setAttribute("hidden", "hidden");  // It will always start hidden
		this.classList.add("wc_session");
		this.addEventListener("click", (event) => {
			dismissAlert(event.currentTarget);  // assuming it is visible, otherwise how else did you click it?
		});
	}

	connectedCallback() {
		const sessionLengthSeconds = this.getAttribute("timeout") * 1;
		const warnAt = this.hasAttribute("warn") ? this.getAttribute("warn") * 1 : 0;

		instance.initTimer(sessionLengthSeconds, warnAt);
	}

	attributeChangedCallback(attrName, oldVal, newVal) {
		console.log('attrName', attrName, 'oldVal', oldVal, 'newVal', newVal);
	}


	get timeout() {
		return this.getAttribute("timeout");
	}

	set timeout(val) {
		this.setAttribute("timeout", val);
	}

	get warn() {
		return this.getAttribute("warn");
	}

	set warn(val) {
		this.setAttribute("warn", val);
	}
}
TimeoutWarn.tagName = "wc-session";

if (!customElements.get(TimeoutWarn.tagName)) {
	customElements.define(TimeoutWarn.tagName, TimeoutWarn);
	window.addEventListener("keydown", keydown);
}

/**
 * Listener for keydown events.
 * @param {KeyboardEvent} event The keydown event.
 */
function keydown(event) {
	if (!event.defaultPrevented && event.key === "Escape") {
		findAndDismissAlert();
	}
}

/**
 * Hides any currently visible wc-session alert.
 */
function findAndDismissAlert() {
	const alerts = document.querySelectorAll(`${TimeoutWarn.tagName}:not([hidden])`);  // There will only be one but no harm finding more?
	Array.prototype.forEach.call(alerts, dismissAlert);
}

/**
 * Dismisses the given alert.
 * @param {HTMLElement} element A wc-session element.
 */
function dismissAlert(element) {
	element.hidden = true;
}

/**
 * Shows the alert.
 * @param {HTMLElement} container The top level container element of the container.
 */
function showAlert(container) {
	if (container.hidden) {
		container.hidden = false;
	}
}

const getWarnDialog = getDialog.bind(this, false);
const getExpiredDialog = getDialog.bind(this, true);

/**
 * Create a message box for display to the user. NOTE: this reuses structures and styles from WMessageBox
 * for convenience and consistency.
 * @function
 * @private
 * @returns {String} The HTML content of the dialog.
 */
function getDialog(error, title, header, body) {
	let type, icon;
	if (error) {
		type = "error";
		icon = "fa-times-circle";
	} else {
		type = "warn";
		icon = "fa-exclamation-triangle";
	}
	return `<section class="wc-messagebox wc-messagebox-type-${type} wc-timeoutwarning">
		<h1>
			<i aria-hidden="true" class="fa fa-window-close-o" style="float:right;"></i>
			<i aria-hidden="true" class="fa ${icon}"></i>
			<span>${title}</span>
		</h1>
		<div class="wc_messages">
			<strong>${header}</strong>
			<br/>
			${body}
		</div>
		</section>`;
}

/**
 * @constructor
 * @alias module:wc/ui/timeoutWarning~TimeoutWarner
 * @private
 */
function TimeoutWarner() {
	var expiresAt,
		WARN_AT = 20000,  // warn user when this many milliseconds remaining, this default is the WCAG 2.0 minimum of 20 seconds
		conf = wcconfig.get("wc/ui/timeoutWarn", {
			min: 30
		}),
		timerWarn,
		timerExpired,
		CONTAINER_ID = "wc_session_container";







	/**
	 * Call when you want to warn the user about an imminent session expiry.
	 * Note: due to poor implementation of WAI-ARIA in IE the timeout warning is only announced if
	 * the containing element exists on the page at load time and is populated when required.
	 * @function
	 * @private
	 * @param {number} minsRemaining The number of minutes until the session will expire.
	 */
	function warn(minsRemaining) {
		function showWarn(title, header, body) {
			const mins = parseInt(minsRemaining);
			const secs = minsRemaining - mins;

			const readableMins = (secs === 0 ? minsRemaining : (mins + (Math.round(secs * 100)) / 100));
			const readableTime = expiresAt.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
			const fullBody = sprintf.sprintf(body, readableMins, readableTime);
			const warningDf = getWarnDialog(title, header, fullBody);
			const container = getContainer();
			if (container) {
				container.innerHTML = warningDf;
				showAlert(container);
				console.info("warning shown at", new Date());
			}
		}
		getTranslations(["messagetitle_warn", "timeout_warn_header", "timeout_warn_body"], showWarn);
	}

	/**
	 * Gets the container in which to show the timeout messages.
	 * If not found it creates one.
	 * @returns {Element} The timeout warning container.
	 */
	function getContainer() {
		let container = document.querySelector(TimeoutWarn.tagName);
		if (!container) {
			container = document.createElement(TimeoutWarn.tagName);
			container.id = CONTAINER_ID;
			document.body.appendChild(container);
		}
		return container;
	}

	/**
	 * Call when the session has expired.
	 * @function
	 * @private
	 */
	function expire() {
		function showExpire(title, header, body) {
			const container = getContainer();

			if (container) {
				container.innerHTML = getExpiredDialog(title, header, body);
				showAlert(container);
				console.info("expired shown at", new Date());
			}
		}
		getTranslations(["messagetitle_error", "timeout_expired_header", "timeout_expired_body"], showExpire);
	}

	/**
	 * Helper for warn and expire
	 * @param {string[]} keys The i18n keys to look up
	 * @param {function} callback Called with translations in order they were found in the keys array.
	 * @private
	 */
	function getTranslations(keys, callback) {
		return i18n.translate(keys).then(function(vals) {
			callback.apply(this, vals);
		});
	}

	/**
	 * Call to start or restart the timer.
	 * @function module:wc/ui/timeoutWarning.initTimer
	 * @param {number} seconds The number of seconds until the HTTPSession expires. Using seconds because that
	 *    is what Java HttpSession.getMaxInactiveInterval() uses.
	 *
	 * @param {number} [warnAt] Set the number of seconds before the warning is shown. If not set then a
	 *    default (20) is used. This can also NEVER be less than 20 (WCAG 2.0 requirement).
	 */
	this.initTimer = function(seconds, warnAt) {
		var millis,
			warning = (warnAt) ? Math.max((warnAt * 1000), WARN_AT) : WARN_AT;  // never let the timeout be less than the default

		if (seconds >= conf.min) {
			millis = seconds * 1000;

			if (timerWarn) {

				timers.clearTimeout(timerWarn);
			}
			if (timerExpired) {
				timers.clearTimeout(timerExpired);

			}
			expiresAt = new Date();

			expiresAt.setTime(expiresAt.getTime() + millis);
			expiresAt.setSeconds(0);  // round down, we can't be that precise, expire on the turn of the minute
			millis = expiresAt.getTime() - Date.now();
			timerWarn = timers.setTimeout(warn, (millis - warning), warning / 60000);
			timerExpired = timers.setTimeout(expire, millis);
			console.log("Session will expire at ", expiresAt);
		} else {
			console.warn("Timeout invalid or too short: ", seconds);
		}
	};
}

/**
 * @typedef {Object} module:wc/ui/timeoutWarn.config() Optional module configuration.
 * @property {int} min The minimum timeout (in seconds). If the requested session timeout is less than this we will not
 * attempt to warn the user.
 * @default 60
 */

export default instance;
