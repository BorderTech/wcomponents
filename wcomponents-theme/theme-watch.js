/* eslint-env node, es6  */
/**
 * You can use this when working on theme JS to speed up development.
 * After running the initial complete build once you may then run this file:
 *
 * `node theme-watch.js`
 *
 * As you save changes to JS files they will be immediately copied to the "scripts_debug" folder in the target directory.
 * You should merely need to reload your browser to see the changes.
 *
 * Note, you will generally be running in debug mode while developing: https://github.com/BorderTech/wcomponents/wiki/Debugging-a-theme
 */
const fs = require("fs");
const { dirs } = require("./build-util");
const buildJs = require("./build-js");
const themeLinter = require("./lintfile");
const grunt = require("grunt");
const path = require("path");
const hotReload = require("./scripts/hotReloadServer");

hotReload.listen();

watchDir(dirs.script, handleJsChange);
watchDir(dirs.test, handleTestChange);

/**
 * Sets up a filesystem watch on the given source directory and copies any changed files to the corresponding subdirectory in targetRoot.
 * @param dir.src The path to the source root directory to watch.
 * @param {Function(String)} [processFunc] The function that will handle the file change.
 */
function watchDir(dir, processFunc) {
	console.log("Watching ", dir.src);
	fs.watch(dir.src, { recursive: true }, (event, filename) => {
		if (filename && event === "change") {
			console.log("File Changed ", filename);
			processFunc(dir.src, filename).then(function(moduleName) {
				if (moduleName) {
					hotReload.notify(moduleName);
				}
			});
		}
	});
}

/**
 * Knows how to respond when a JS source module is changed.
 * @param {string} dir The path to the directory being watched.
 * @param {string} filename The relative path to the file that changed.
 * @returns {Promise} resolved when the change has been handled.
 */
function handleJsChange(dir, filename) {
	let filePath = path.join(dir, filename);
	return buildJs.build(filePath).then(function() {
		return buildJs.pathToModule(filename);
	});
}

/**
 * Knows how to respond when a test suite is changed.
 * @param {string} dir The path to the directory being watched.
 * @param {string} filename The relative path to the file that changed.
 * @returns {Promise} resolved when the change has been handled.
 */
function handleTestChange(dir, filename) {
	return new Promise(function(win) {
		themeLinter.run(path.join(dir, filename));
		grunt.option("filename", filename);
		grunt.tasks(["copy:test"], { filename: filename }, win);
	});
}
