/* eslint-env node, es6  */

/*
 * This module is responsible for the server side hot module reloading.
 *
 * @author Rick Brown
 */
const socketio = require("socket.io");
let io;

/**
 * Beginning listening for hot relaod clients.
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
 * @param {type} moduleName The name of the module that has changed.
 */
function notify(moduleName) {
	if (io && io.engine.clientsCount > 0) {
		console.log("Hot reloading", moduleName);
		io.sockets.emit("wcjschange", moduleName);
	}
}

module.exports = {
	listen,
	notify
};
