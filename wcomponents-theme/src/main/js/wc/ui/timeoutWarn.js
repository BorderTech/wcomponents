define(["lib/sprintf", "wc/dom/event", "wc/dom/Widget", "wc/i18n/i18n", "wc/loader/resource",
	"wc/dom/shed", "wc/timers", "wc/dom/classList", "wc/ui/icon", "wc/config"],
	function(sprintf, event, Widget, i18n, loader, shed, timers, classList, icon, wcconfig) {
		"use strict";
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
				CONTAINER_ID = "wc_session_container",
				TIMEOUT_CONTAINER = new Widget("div", "", { id: CONTAINER_ID });

			/**
			 * Close timeout warning dialog and remove event handlers.
			 * @function
			 * @private
			 * @param {Element} element The warning container.
			 */
			function closeWarning(element) {
				if (element && !shed.isHidden(element, true)) {
					event.remove(document.body, event.TYPE.keydown, keydownEvent);
					event.remove(element, event.TYPE.click, clickEvent);
					shed.hide(element);
				}
			}

			function clickEvent($event) {
				var target;
				if ((target = TIMEOUT_CONTAINER.findAncestor($event.target))) {
					closeWarning(target);
				}
			}

			function keydownEvent($event) {
				if ($event.keyCode === KeyEvent.DOM_VK_ESCAPE) {
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
				event.add(document.body, event.TYPE.keydown, keydownEvent);
				event.add(container, event.TYPE.click, clickEvent);
				shed.show(container);
			}

			/**
			 * Create a message box for display to the user. NOTE: this resuses structures and styles from WMessageBox
			 * for convenience and consistency.
			 * @function
			 * @private
			 * @returns {Promise} resolved with the messagebox documentFragment.
			 */
			function getDialog() {
				return loader.load("wc.ui.timeoutWarn.handlebars", true, true);
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
				function showWarn(title, header, body) {
					getDialog().then(function(warningDf) {
						var container = getContainer(),
							minutes = expiresAt.getMinutes(),
							readableMins, secs, mins;
						if (container) {
							container.innerHTML = "";

							mins = parseInt(minsRemaining);
							secs = minsRemaining - mins;
							readableMins = (secs === 0 ? minsRemaining : (mins + (Math.round(secs * 100)) / 100));

							body = sprintf.sprintf(body, readableMins, (expiresAt.getHours() + ":" + ((minutes < 10) ? "0" + minutes : minutes)));
							container.innerHTML = sprintf.sprintf(warningDf, title, header, body);
							showDialog(container);
							console.info("warning shown at", new Date());
						}
					});
				}
				getTranslations(["messagetitle_warn", "timeout_warn_header", "timeout_warn_body"], showWarn);
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
				function showExpire(title, header, body) {
					getDialog().then(function (errorDf) {
						var container = getContainer(), section;
						if (container) {
							container.innerHTML = "";
							container.innerHTML = sprintf.sprintf(errorDf, title, header, body);
							if ((section = container.firstChild)) {
								classList.remove(section, "wc-messagebox-type-warn");
								classList.add(section, "wc-messagebox-type-error");
								icon.change(section, "fa-times-circle", "fa-exclamation-triangle");
							}
							if (shed.isHidden(container, true)) {
								showDialog(container);  // re-show it if the warning was closed by the user
							}
							console.info("expired shown at", new Date());
						}
					});
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
			 * @param {number} [warnAt] wSet the number of seconds before the warning is shown. If not set then a
			 *    default (20) is used. This can also NEVER be less than 20 (WCAG 2.0 requirement). It is in seconds to
			 *    match seconds and make the XML smaller!
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
		 * Display a warning to the user before their session expires. This is an accessibility requirement.
		 *
		 * Things to consider:
		 *
		 * * What renews the session? Requesting an XSL file via AJAX? Requesting an image?
		 * * Can we be sure that those actions will always renew the session? What if the resource is loaded from cache
		 *   instead of hitting the server... then the session will not renew.
		 *
		 * There will always be a chance of getting the session timeout wrong, but there is wrong and then
		 * there is WRONG. It is better to warn the user too early rather than too late.
		 *
		 *
		 * @module
		 * @requires external:lib/sprintf
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/loader/resource
		 * @requires module:wc/dom/shed
		 * @requires module:wc/timers
		 * @requires module:wc/dom/classList
		 * @requires module:wc/config
		 *
		 * @todo Document private members, check source order.
		 */
		return new TimeoutWarner();

		/**
		 * @typedef {Object} module:wc/ui/timeoutWarn.config() Optional module configuration.
		 * @property {int} min The minimum timeout (in seconds). If the requested session timeout is less than this we will not
		 * attempt to warn the user.
		 * @default 60
		 */
	});
