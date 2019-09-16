/* eslint-env node, es6  */

/*
 * This module is responsible for hot module reloading on the server side.
 *
 * @author Rick Brown
 */
const socketio = require("socket.io");
let io;

/**
 * Begin listening for hot reload clients.
 * @param config Override default configuration if you wish.
 */
function listen(config = { port: "3002"}) {
	if (!io) {
		io = socketio.listen(config.port, function (err) {
			if (err) {
				console.error(err);
			}
		});

		console.log("Hot reload server listening on port", config.port);

		io.on("connection", function (socket) {
			console.log("A client connected");
			socket.on("disconnect", function () {
				console.log("A client disconnected");
			});
		});
	}
}

/**
 * Call this when a module has changed.
 * @param {string|string[]} changed The name of the module or modules that have changed.
 * @param {string} [type] The type of change.
 */
function notify(changed, type=null) {
	if (io && io.engine.clientsCount > 0) {
		let payload = { changed, type };
		console.log("Hot reloading", payload);
		io.sockets.emit("wc-change", payload);
	}
}

module.exports = {
	listen,
	notify
};
