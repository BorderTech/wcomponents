/**
 * Display a warning to the user before their session expires. This is an accessibility requirement.
 *
 * <p>Things to consider:</p>
 * <ul><li>What renews the session? Requesting an XSL file via AJAX? Requesting an image?</li>
 * <li>Can we be sure that those actions will always renew the session? What if the resource is loaded from cache
 * instead of hitting the server... then the session will not renew.</li></ul>
 *
 * <p>There will always be a chance of getting the session timeout wrong, but there is wrong and then
 * there is WRONG. It is better to warn the user too early rather than too late.</p>
 *
 * @typedef {Object} module:wc/ui/timeoutWarn.config() Optional module configuration.
 * @property {int} min The minimum timeout (in seconds). If the requested session timeout is less than this we will not
 * attempt to warn the user.
 * @default 60
 *
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/xml/xslTransform
 * @requires module:wc/dom/event
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/loader/resource
 * @requires module:wc/dom/shed
 * @requires module:wc/timers
 * @requires module:wc/config
 *
 * @todo Document private members, check source order.
 */
define(["lib/sprintf", "wc/xml/xslTransform", "wc/dom/event", "wc/dom/Widget", "wc/i18n/i18n", "wc/loader/resource", "wc/dom/shed", "wc/timers", "wc/config"],
	function(sprintf, xslTransform, event, Widget, i18n, loader, shed, timers, wcconfig) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/timeoutWarning~TimeoutWarner
		 * @private
		 */
		function TimeoutWarner() {
			var expiresAt,
				WARN_AT = 20000,  // warn user when this many milliseconds remaining, this default is the WCAG 2.0 minimum of 20 seconds
				conf = wcconfig.get("wc/ui/timeoutWarn"),
				MIN_TIMEOUT = (conf ? (conf.min || 30) : 30),
				timerWarn,
				timerExpired,
				CONTAINER_ID = "wc_session_container",
				TIMEOUT_CONTAINER = new Widget("div", "", { id: CONTAINER_ID });

			/**
			 * Close timeout warning dialog and remove event handlers.
			 * @function
			 * @private
			 * @param {Element} element The warning container.
			 */
			function closeWarning(element) {
				if (element && !shed.isHidden(element)) {
					event.remove(document.body, event.TYPE.keydown, keyDownEvent);
					event.remove(element, event.TYPE.click, clickEvent);
					shed.hide(element);
				}
			}

			function clickEvent($event) {
				var target;
				if (!$event.defaultPrevented && (target = TIMEOUT_CONTAINER.findAncestor($event.target))) {
					closeWarning(target);
				}
			}

			function keyDownEvent($event) {
				if (!$event.defaultPrevented && $event.altKey && $event.keyCode === KeyEvent.DOM_VK_9) {
					closeWarning(getContainer());
				}
			}

			/**
			 * Shows the warning container and wires up the requisite event listeners so that the user can dismiss it
			 * again.
			 * @function
			 * @private
			 * @param {Element} container The top level container element of the warning container.
			 */
			function showDialog(container) {
				event.add(document.body, event.TYPE.keydown, keyDownEvent);
				event.add(container, event.TYPE.click, clickEvent);
				shed.show(container);
			}

			/**
			 * Create a message box for display to the user. NOTE: this resuses structures and styles from WMessageBox
			 * for convenience and consistency.
			 * @function
			 * @private
			 * @param {String} level The warning level either "error" (for when the timeout has occurred) or "warn" (for
			 * showing the warning of imminent timeout).
			 * @returns {Promise} resolved with the messagebox documentFragment.
			 */
			function getDialog(level) {
				return loader.load("wc.ui.timeoutWarn.xml", false, true).then(function(xml) {
					xml.documentElement.setAttribute("type", level);
					return xslTransform.transform({xmlDoc: xml});
				});
			}

			/**
			 * Call when you want to warn the user about an imminent session expiry.
			 * Note: due to poor implementation of WAI-ARIA in IE the timeout warning is only announced if
			 * the containing element exists on the page at load time and is populated when required.
			 * @function
			 * @private
			 * @param {number} minsRemaining The number of minutes until the session will expire.
			 */
			function warn(minsRemaining) {
				getDialog("warn").then(function(warningDf) {
					var container = getContainer(),
						minutes = expiresAt.getMinutes(),
						readableMins, secs, mins,
						body, header;
					if (container) {
						container.innerHTML = "";
						header = i18n.get("${wc.ui.timeoutWarn.message.warn.header}");
						body = i18n.get("${wc.ui.timeoutWarn.message.warn.body}");

						mins = parseInt(minsRemaining);
						secs = minsRemaining - mins;
						readableMins = (secs === 0 ? minsRemaining : (mins + (Math.round(secs * 100)) / 100));

						body = sprintf.sprintf(body, readableMins, (expiresAt.getHours() + ":" + ((minutes < 10) ? "0" + minutes : minutes)));
						container.appendChild(warningDf);
						container.innerHTML = sprintf.sprintf(container.innerHTML, header, body);
						showDialog(container);
						console.info("warning shown at", new Date());
					}
				});
			}

			/**
			 * Gets the container in which to show the timeout messages.
			 * If not found it creates one.
			 * @returns {Element} The timeout warning container.
			 */
			function getContainer() {
				var container = document.getElementById(CONTAINER_ID);
				if (!container) {
					container = document.createElement(TIMEOUT_CONTAINER.tagName);
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
				getDialog("error").then(function(errorDf) {
					var body, header, container = getContainer();
					if (container) {
						container.innerHTML = "";
						header = i18n.get("${wc.ui.timeoutWarn.message.expired.header}");
						body = i18n.get("${wc.ui.timeoutWarn.message.expired.body}");
						container.appendChild(errorDf);
						container.innerHTML = sprintf.sprintf(container.innerHTML, header, body);
						if (shed.isHidden(container)) {
							showDialog(container);  // re-show it if the warning was closed by the user
						}
						console.info("expired shown at", new Date());
					}
				});
			}


			/**
			 * Call to start or restart the timer.
			 * @function module:wc/ui/timeoutWarning.initTimer
			 * @param {number} seconds The number of seconds until the HTTPSession expires. Using seconds because that
			 *    is what Java HttpSession.getMaxInactiveInterval() uses.
			 *
			 * @param {number} [warnAt] wSet the number of seconds before the warning is shown. If not set then a
			 *    default (20) is used. This can also NEVER be less than 20 (WCAG 2.0 requirement). It is in seconds to
			 *    match seconds and make the XML smaller!
			 */
			this.initTimer = function(seconds, warnAt) {
				var millis,
					warning = (warnAt) ? Math.max((warnAt * 1000), WARN_AT) : WARN_AT;  // never let the timeout be less than the default

				if (seconds >= MIN_TIMEOUT) {
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
				}
				else {
					console.warn("Timeout invalid or too short: ", seconds);
				}
			};
		}
		return /** @alias module:wc/ui/timeoutWarning */ new TimeoutWarner();
	});
