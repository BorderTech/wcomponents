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
import debounce from "wc/debounce";

const minimumWarnAt =  20000;  // warn user when this many milliseconds remaining, this default is the WCAG 2.0 minimum of 20 seconds
const pendingTimers = [];
let expiresAt;

const template = (isExpired, title, header, body) => {
	let type, icon;
	if (isExpired) {
		type = "error";
		icon = "fa-times-circle";
	} else {
		type = "warn";
		icon = "fa-exclamation-triangle";
	}
	return `
		<style>
			:host {
				width: fit-content;
				bottom: 0;
				max-width: 100%;
				position: fixed;
				right: 0;
				z-index: 27;
			}
		</style>
		<section class="wc-messagebox wc-messagebox-type-${type} wc-timeoutwarning">
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
};

/**
 * Sets or resets all the timers using the value stored in `expiresAt`.
 * @param warnBeforeMillis How long before expiresAt should the warning be shown.
 */
const resetTimers = debounce(function resetTimers(warnBeforeMillis) {
	const remainingMillis = calculateRemaining(true);
	pendingTimers.forEach(timers.clearTimeout);  // That's right, clears all timers for all instances by design
	if (remainingMillis >= 0) {
		const millisToWarn = remainingMillis - warnBeforeMillis;
		if (millisToWarn >= 0) {
			pendingTimers.push(timers.setTimeout(findAndShowAlert, millisToWarn));
			pendingTimers.push(timers.setTimeout(getWarnDialog, millisToWarn / 2));  // To prefetch any translations, images etc
		}
		pendingTimers.push(timers.setTimeout(findAndShowAlert, remainingMillis));
		console.log("Session will expire at", expiresAt);
		console.log(`Preload will be in ${millisToWarn / 2} milliseconds`);
		console.log(`Warning will be shown in ${millisToWarn} milliseconds`);
		console.log(`Expired will be shown in ${remainingMillis} milliseconds`);
	}
}, 500);

/**
 * Call to start or restart the timer.
 * @param {TimeoutWarn} element The attributes on this element will be used to determine session length etc.
 */
function initTimer(element) {
	if (element.hasAttribute("timeout")) {
		const sessionLengthSeconds = element.getAttribute("timeout") * 1;
		let warnAt = 0;
		if (element.hasAttribute("warn")) {
			warnAt = element.getAttribute("warn") * 1;
		}
		setupTimers(sessionLengthSeconds, warnAt);
	}

	function setupTimers(seconds, warnAt) {
		let warning = minimumWarnAt;

		const conf = wcconfig.get("wc/ui/timeoutWarn", {
			min: 30
		});

		try {
			warning = Math.max(warnAt * 1000, warning);  // never let the timeout be less than the default
		} catch (ex) {
			console.warn("Could not use warning interval", warnAt, ex.message);
			warning = minimumWarnAt;
		}

		if (seconds >= conf.min) {
			let millisToExpiry = seconds * 1000;
			expiresAt = new Date();
			expiresAt.setTime(expiresAt.getTime() + millisToExpiry);
			expiresAt.setSeconds(0);  // round down, we can't be that precise, expire on the turn of the minute
			resetTimers(warning);
		} else {
			console.warn("Timeout invalid or too short: ", seconds);
		}
	}
}


/**
 * Get dialog HTML to warn the user about an imminent session expiry.
 * @return {Promise<String>} resolved with the error dialog HTML.
 */
function getExpiredDialog() {
	const messageKeys = ["messagetitle_error", "timeout_expired_header", "timeout_expired_body"];
	return getTranslations(messageKeys).then(([title, header, body]) => {
		return template(true, title, header, body);
	});
}

/**
 * Get dialog HTML to warn the user about an imminent session expiry.
 * The dialog content will "react" to being shown.
 * @return {Promise<String>} resolved with the warning dialog HTML.
 */
function getWarnDialog() {
	const messageKeys = ["messagetitle_warn", "timeout_warn_header", "timeout_warn_body"];
	return getTranslations(messageKeys).then(([title, header, body]) => {
		const minsRemaining = calculateRemaining();
		const readableMins = minsRemaining < 1 ? '< 1' : minsRemaining;
		const readableTime = expiresAt.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
		const fullBody = sprintf.sprintf(body, readableMins, readableTime);
		return template(false, title, header, fullBody);
	});
}

/**
 * Gets the appropriate dialog content based on how much session time is remaining..
 * @return {Promise<String>} resolved with the dialog HTML.
 */
function getDialog() {
	const minsRemaining = calculateRemaining();
	if (minsRemaining > 0) {
		return getWarnDialog();
	} else if (minsRemaining === 0) {
		return getExpiredDialog();
	}
	return Promise.resolve('');
}

class TimeoutWarn extends HTMLElement {
	// These static properties are too new for the current build tools to handle them (revisit when r.js is gone)
	// static tagName = "wc-session";
	// static observedAttributes = ["timeout", "warn", "hidden"];

	static get observedAttributes() {
		return [ "timeout", "warn", "hidden" ];
	}

	constructor() {
		super();
		/*
			alertdialog is the correct aria role for a session expiry warning.
			In fact that is "Example 1" on the MDN alertdialog page:
			https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA/Roles/alertdialog_role#example_1_a_basic_alert_dialog

			It is also "Example 2" here:
			https://www.digitala11y.com/alertdialog-role/
		 */
		this.setAttribute("role", "alertdialog");
		this.setAttribute("hidden", "hidden");  // It will always start hidden
		this.classList.add("wc_session");
		this.addEventListener("click", (event) => {
			dismissAlert(event.currentTarget);  // assuming it is visible, otherwise how else did you click it?
		});
	}


	connectedCallback() {
		if (!this.hasAttribute("timeout")) {
			console.log(`${this.tagName} missing required attribute 'timeout'`);
		}
	}

	attributeChangedCallback(attrName /* , oldVal, newVal */) {
		if (attrName === "timeout" || attrName === "warn") {
			initTimer(this);
		} else if (attrName === "hidden") {
			if (this.hasAttribute("hidden")) {
				this.innerHTML = "";
			} else {
				getDialog().then((html) => {
					if (html) {
						this.innerHTML = html;
					}
				});
			}
		}
	}

	get timeout() {
		return this.getAttribute("timeout");
	}

	set timeout(seconds) {
		// Using seconds because this is the unit used in Java HttpSession.getMaxInactiveInterval()
		this.setAttribute("timeout", seconds);
	}

	get warn() {
		return this.getAttribute("warn");
	}

	set warn(seconds) {
		this.setAttribute("warn", seconds);
	}

	get expiresat() {
		return expiresAt ? new Date(expiresAt) : null;
	}
}

/**
 * @static
 */
TimeoutWarn.tagName = "wc-session";


/**
 * Call to start or restart the timer.
 * This is provided for legacy compatibility. It is probably now easier to reset the timers through HTML using the
 * <wc-session> element. That's all this does under the hood anyway.
 * @static
 * @param {number} seconds The number of seconds until the HTTPSession expires. Using seconds because that
 *    is what Java HttpSession.getMaxInactiveInterval() uses.
 *
 * @param {number} [warnAt] Set the number of seconds before the warning is shown. If not set then a
 *    default (20) is used. This can also NEVER be less than 20 (WCAG 2.0 requirement).
 */
TimeoutWarn.initTimer = function(seconds, warnAt) {
	if (typeof seconds === "number") {
		const alert = document.querySelector(TimeoutWarn.tagName);  // Find the first one, there's only meant to be one
		if (alert) {
			alert.setAttribute("timeout", `${seconds}`);
			if (warnAt && typeof warnAt === "number") {
				alert.setAttribute("warn", `${warnAt}`);
			}
		}
	} else {
		console.warn("'seconds' must be a number");
	}

};


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
 * Shows the session alert dialog.
 */
function findAndShowAlert() {
	const alert = document.querySelector(TimeoutWarn.tagName);
	if (alert.hidden) {
		showAlert(alert);
	} else {
		dismissAlert(alert);
		timers.setTimeout(findAndShowAlert, 0);
	}
}

/**
 * How long between right now and the current session expiry.
 * @param {boolean} asMillis If true the result is milliseconds.
 * @return {number} How much time remaining until the session expires.
 *    Zero means there is no time remaining.
 *    A negative number means there is no session expiry.
 */
function calculateRemaining(asMillis = false) {
	if (expiresAt) {
		let millisRemaining = Math.max(expiresAt.getTime() - Date.now(), 0);
		if (millisRemaining) {
			if (asMillis) {
				return millisRemaining;
			}
			let minutesRemaining = millisRemaining / 60000;
			if (minutesRemaining > 1) {
				minutesRemaining = Math.floor(minutesRemaining);  // Part minutes aren't important
			}
			return minutesRemaining;
		}
		return 0;
	}
	return -1;
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

/**
 * Helper for warn and expire.
 * @param {string[]} keys The i18n keys to look up
 * @return {Promise<String[]>} resolved with translations in order they were found in the keys array.
 */
function getTranslations(keys) {
	return i18n.translate(keys);
}

/**
 * @typedef {Object} module:wc/ui/timeoutWarn.config() Optional module configuration.
 * @property {int} min The minimum timeout (in seconds). If the requested session timeout is less than this we will not
 * attempt to warn the user.
 * @default 30
 */

export default TimeoutWarn;
