/*
 * This module is used by low level ajax functionality to handle error situations.
 * It is in a separate module because we should not need to load many of these module dependencies under normal conditions.
 * The intention is that it is loaded lazily, on demand, with an errback to requirejs.
 */
import wcconfig from "wc/config.mjs";
import i18n from "wc/i18n/i18n.mjs";
import mixin from "wc/mixin.mjs";

/**
 * Get an error message for the given response.
 * Allows for customized error messages based on HTTP status code by setting a config object like so:
 * @example
 require(["wc/config"], function(wcconfig){
  wcconfig.set({ messages: {
  403:"Oh noes! A 403 occurred!",
  404: "I can't find it!",
  418: function(response) { return "Short and stout"; },
  200: "Some gateway proxies don't know basic HTTP",
  error: "An error occurred and I have not set a specific message for it!"
 }
 },"wc/ui/xhr");
 });
 * Note that you can provide either a string or function that will be passed the raw XHR response
 * and is expected to return a string.
 *
 *
 * @param {Object} [messages] Optionally provide the messages object directly to this function.
 *
 */

/**
 *
 * @param {{responseText: string, statusText: string, status: number }} response An XHR response.
 * @returns {string} An error message, in order of preference:
 * - A custom message specific to the status code, provided in the module configuration
 * - A custom default error message
 * - The response "statusText"
 * - The default WComponents error message for this.
 */
function getErrorMessage(response) {
	let message;
	if (response) {
		if (response.status || response.status === 0) {  // I have seen response status 0 when, for example, a network cable is unplugged
			const msgs = getMessageOverrides();
			message = msgs[response.status] || msgs.error;
			if (typeof message === "function") {
				// a message override has been provided, and it's a function which will provide the actual message.
				try {
					message = message(response);
				} catch (ex) {
					console.warn(ex);  // consume this error and continue
				}
			}
		}
		/*
		 * The response could be 200 in an error condition, real world example:
		 * a badly configured gateway rejects an ajax file upload but sends a 200 response.
		 * While this is not really our problem we do need to tell the user something.
		 * The something we tell them should NOT be "OK" or "Success".
		 */
		if (!message && response.status !== 200) {
			// No application specific message
			message = response.responseText || response.statusText;
		}
	}
	if (!message) {
		message = i18n.get("xhr_errormsg");
	}
	return message;
}

/**
 * Gets application specific message overrides, if configured.
 * @returns {Object} Message overrides for specific status codes, if set.
 * If there is a conflict then the message set in the messages argument takes precedence over those in module config.
 */
function getMessageOverrides() {
	const result = {}, config = wcconfig.get("wc/ui/xhr"),
		mfuConfig = wcconfig.get("wc/ui/multiFileUploader");  // this is for legacy support, the functionality was introduced in multiFileUploader
	if (config?.messages) {
		mixin(config.messages, result);
	}
	if (mfuConfig?.messages) {
		mixin(mfuConfig.messages, result);
	}
	return result;
}

export default {
	getErrorMessage
};
