/*
 * This module handles hot module reloading for fast development.
 *
 * @author Rick Brown
 */
define(["lib/socketio/socket.io", "wc/debounce", "wc/urlParser", "wc/dom/cookie"], function (io, debounce, urlParser, cookie) {
	var socketHotReload,
		handlers = {
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
					if (require.defined(moduleName)) {
						try {
							component = require(moduleName);
							if (component && component.deinit) {
								console.log("deinitialising", moduleName);
								component.deinit();
							}
						} catch (ex) {
							console.warn(ex);
						}
					}
					require.undef(moduleName);
					require([moduleName], function (module) {
						callback(null, module);
					});
				},
			style: /**
				 * Force style loader to reload CSS.
				 * Note that all loaded CSS is forced to reload regardless, hence the debounce, it could be called heaps.
				 * It is not feasible to detect what actually needs to be updated when Sass source is modified.
				 */
				debounce(function() {
					var i, forceParam = "wcforce=" + Date.now(),
						myLinks = document.querySelectorAll("link[data-wc-loader]");
					for (i = 0; i < myLinks.length; i++) {
						bumpCacheBuster(myLinks[i]);
					}

					function bumpCacheBuster(link) {
						var parsedUrl = urlParser.parse(link.getAttribute("href"));

						if (parsedUrl.search) {
							link.href += "&" + forceParam;
						} else {
							link.href += "?" + forceParam;
						}
					}
				}, 333)
		};

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
			console.warn("Could not hot reload", payload);
		}
	}

	return {
		getConnection: getConnection
	};
});
