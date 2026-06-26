/*
 * This module is responsible for hot module reloading on the server side.
 *
 * @author Rick Brown
 */

import console from 'node:console';

import { Server } from "socket.io";

/** @type {any} */
let io;

/**
 * Begin listening for hot reload clients.
 * @param {any} config - Override default configuration if you wish.
 */
function listen(config = { port: 3002 }) {
	if (!io) {
		io = new Server();

		console.log("Hot reload server listening on port", config.port);

		io.on("connection",
			/**
			 * ?
			 * @param {any} socket - ?
			 */
			function (socket) {
				console.log("A client connected");
				socket.on("disconnect", function () {
					console.log("A client disconnected");
				});
			}
		);

		io.listen(config.port);
	}
}

/**
 * Call this when a module has changed.
 * @param {string | string[]} changed The name of the module or modules that have changed.
 * @param {string | null} [type] The type of change.
 */
function notify(changed, type = null) {
	if (io && io.engine.clientsCount > 0) {
		let payload = { changed, type };
		console.log("Hot reloading", payload);
		io.sockets.emit("wc-change", payload);
	}
}

export default { listen, notify };
