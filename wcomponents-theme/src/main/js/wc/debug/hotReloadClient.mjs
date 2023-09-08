/*
 * This module handles hot module reloading for fast development.
 * If you are wondering at the value of this, I have now used it extensively during JS development, and it is AWESOME!
 * It saves me hours a week.
 *
 * @author Rick Brown
 */

import io from "lib/socketio/socket.io";
import debounce from "wc/debounce";
import cookie from "wc/dom/cookie";

const handlers = {
	images: /**
		 * Force an image to reload.
		 * @param {object} payload The data received from the update event.
		 */
		function(payload) {
			// @ts-ignore
			let imgHref = new URL(require.toUrl(payload.changed)).pathname;
			const images = document.querySelectorAll("img[src*='" + imgHref + "']");
			for (let i = 0; i < images.length; i++) {
				bumpCacheBuster(images[i]);
			}
		},
	script: /**
		 * Force requirejs to reload a module.
		 * @param {object} payload The data received from the update event.
		 */
		function (payload) {
			const moduleName = payload.changed,
				callback = (err, result) => {
					if (err) {
						console.error(err);
					} else {
						console.log(result);
					}
				};
			resetConsoleColor();
			// @ts-ignore
			if (require.defined(moduleName)) {
				try {
					const component = require(moduleName);
					if (component && component.deinit) {
						console.log("deinitialising", moduleName);
						component.deinit(document.body);
					}
					// @ts-ignore
					require.undef(moduleName);
					// @ts-ignore
					require([moduleName], function (module) {
						callback(null, module);
						console.log("Reloaded", moduleName);
					});
				} catch (ex) {
					console.warn(ex);
				}
			} else {
				console.log("Module not loaded, skipping hot reload", moduleName);
			}
		},
	style: /**
		 * Force style loader to reload CSS.
		 * Note that all loaded CSS is forced to reload regardless, hence the debounce, it could be called heaps.
		 * It is not feasible to detect what actually needs to be updated when Sass source is modified.
		 */
		debounce(function() {
			const myLinks = document.querySelectorAll("link[data-wc-loader]");
			for (let i = 0; i < myLinks.length; i++) {
				bumpCacheBuster(myLinks[i]);
			}
		}, 333)
};

/**
 * Since the point of hot module reloading is not to refresh the page often it makes sense to reset concoleColor flags.
 */
function resetConsoleColor() {
	var mod = "wc/debug/consoleColor";
	// @ts-ignore
	if (require.defined(mod)) {
		// @ts-ignore
		mod = require(mod);
		// @ts-ignore
		if (mod && mod.reset) {
			// @ts-ignore
			mod.reset();
		}
	}
}

/**
 * Force cache bypass by manipulating queryString.
 * @param {Element} element An element with a src or href
 */
function bumpCacheBuster(element) {
	const forceParam = "wcforce=" + Date.now(),
		attr = element.hasAttribute("href") ? "href" : "src",
		parsedUrl = new URL(element.getAttribute(attr));
	element[attr] += (parsedUrl.search ? "&" : "?") + forceParam;
}

/**
 * Determines if we should try to connect.
 * @param {boolean} force If true will bypass regular checks and try to give you a connection.
 * @returns {boolean} true if we should try to connect.
 */
function shouldConnect(force) {
	if (force) {
		// this allows you to `require("wc/debug/hotReloadClient").getConnection(true)`
		return true;
	}
	if (navigator.webdriver) {
		// by default don't connect when running selenium tests
		return false;
	}
	// If you want hot module reloading to autoconnect then `require("wc/dom/cookie").create("wchotmod", "true", 800)`
	return cookie.read("wchotmod") === "true";
}

let socketHotReload;
/**
 * Establish a socket connection with the hot reload server.
 * @param {boolean} [force] If true will bypass regular checks and try to give you a connection.
 * @returns The socket connection.
 */
function getConnection(force) {
	if (!shouldConnect(force)) {
		return null;
	}
	try {
		if (!socketHotReload) {
			socketHotReload = io.connect("//" + window.location.hostname + ":3002", {
				reconnectionAttempts: 6,
				reconnectionDelay: 10000
			});
			socketHotReload.on("wc-change", handleModuleChange);

			socketHotReload.on("connect_error", function(err) {
				console.log("Hot reload client could not connect", err);
			});

			socketHotReload.on("reconnect_failed", function () {
				console.log("Hot reload client given up retrying");
			});
		}
		return socketHotReload;
	} catch (ex) {
		console.error(ex);
	}
	return null;
}

/**
 * Called when a module needs to be hot reloaded.
 * @param payload The event payload including module names to reload.
 */
function handleModuleChange(payload) {
	if (payload.type && typeof handlers[payload.type] === "function") {
		try {
			handlers[payload.type](payload);
		} catch (ex) {
			console.error(ex);
		}
	} else {
		console.info("Could not hot reload", payload);
	}
}

export default {
	getConnection,
	/**
	 * The hot reload client can hot reload itself!
	 */
	deinit: function() {
		const socket = this.getConnection();
		if (socket) {
			socket.close();
		}
	}
};

