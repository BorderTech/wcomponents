/*
 * This module handles hot module reloading for fast development.
 * If you are wondering at the value of this, I have now used it extensively during JS development and it is AWESOME!
 * It saves me hours a week.
 *
 * @author Rick Brown
 */
define([
	"lib/socketio/socket.io",
	"wc/debounce",
	"wc/urlParser",
	"wc/dom/cookie"
], function (io, debounce, urlParser, cookie) {
	var socketHotReload,
		handlers = {
			images: /**
				 * Force an image to reload.
				 * @param {object} payload The data received from the update event.
				 */
				function(payload) {
					var i, images, imgHref = require.toUrl(payload.changed);
					imgHref = urlParser.parse(imgHref);
					imgHref = imgHref.pathnameArray.join("/");
					images = document.querySelectorAll("img[src*='" + imgHref + "']");
					for (i = 0; i < images.length; i++) {
						bumpCacheBuster(images[i]);
					}
				},
			script: /**
				 * Force requirejs to reload a module.
				 * @param {object} payload The data received from the update event.
				 */
				function (payload) {
					var component,
						moduleName = payload.changed,
						callback = function(err, result) {
							if (err) {
								console.error(err);
							} else {
								console.log(result);
							}
						};
					resetConsoleColor();
					if (require.defined(moduleName)) {
						try {
							component = require(moduleName);
							if (component && component.deinit) {
								console.log("deinitialising", moduleName);
								component.deinit(document.body);
							}
							require.undef(moduleName);
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
					var i, myLinks = document.querySelectorAll("link[data-wc-loader]");
					for (i = 0; i < myLinks.length; i++) {
						bumpCacheBuster(myLinks[i]);
					}
				}, 333)
		};

	/**
	 * Since the point of hot module reloading is not to refresh the page often it makes sense to reset concoleColor flags.
	 */
	function resetConsoleColor() {
		var mod = "wc/debug/consoleColor";
		if (require.defined(mod)) {
			mod = require(mod);
			if (mod && mod.reset) {
				mod.reset();
			}
		}
	}

	/**
	 * Force cache bypass by manipulating queryString.
	 * @param {Element} element An element with a src or href
	 */
	function bumpCacheBuster(element) {
		var forceParam = "wcforce=" + Date.now(),
			attr = element.hasAttribute("href") ? "href" : "src",
			parsedUrl = urlParser.parse(element.getAttribute(attr));

		if (parsedUrl.search) {
			element[attr] += "&" + forceParam;
		} else {
			element[attr] += "?" + forceParam;
		}
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
		return cookie.read("wchotmod");
	}

	/**
	 * Establish a socket connection with the hot reload server.
	 * @param {boolean} force If true will bypass regular checks and try to give you a connection.
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

	return {
		getConnection: getConnection,
		/**
		 * The hot reload client can hot reload itself!
		 */
		deinit: function() {
			var socket = this.getConnection();
			if (socket) {
				socket.close();
			}
		}
	};
});
