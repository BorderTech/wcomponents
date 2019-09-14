/*
 * This module handles hot module reloading for fast development.
 *
 * @author Rick Brown
 */
define(["lib/socketio/socket.io"], function (io) {
	var socketHotReload;

	/**
	 * Establish a socket connection with the hot reload server.
	 * @returns The socket connection.
	 */
	function getConnection() {
		if (!socketHotReload) {
			socketHotReload = io.connect("http://127.0.0.1:3002");
			socketHotReload.on("wcjschange", handleJsChange);
		}
		return socketHotReload;
	}

	/**
	 * Called when a JS module needs to be hot reloaded.
	 * @param moduleName The module to reload.
	 */
	function handleJsChange(moduleName) {
		console.log("Hot reloading", moduleName);
		reloadModule(moduleName, function(err, result) {
			if (err) {
				console.error(err);
			} else {
				console.log(result);
			}
		});
	}

	/**
	 * Force requirejs to reload a module.
	 * @param {string} moduleName The name of the module to reload.
	 * @param {Function} callback called when the module is reloaded.
	 */
	function reloadModule(moduleName, callback) {
		require.undef(moduleName);
		require([moduleName], function (module) {
			callback(null, module);
		});
	}

	return {
		getConnection: getConnection
	};
});
