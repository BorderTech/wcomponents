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
const { requireAmd, dirs } = require("./scripts/build-util");
const themeLinter = require("./lintfile");
const buildCss = require("./build-css");
const buildImages = require("./build-images");
const buildJs = require("./build-js");
const buildResources = require("./build-resource");
const grunt = require("grunt");
const path = require("path");
const hotReload = require("./scripts/hotReloadServer");

const handlers = {
	images: /**
		 * Knows how to respond when an image is changed - this is possibly only useful when editing SVGs
		 * and showing off how cool our dev environment is.
		 * @param {string} dir The path to the directory being watched.
		* @param {string} filename The relative path to the file that changed.
		* @returns {Promise} resolved when the change has been handled.
		*/
		function(dir, filename) {
			return buildImages.build(filename).then(() => {
				return path.join(path.basename(dir), filename);
			});
		},
	resource: function(dir, filename) {
		let filePath = path.join(dir, filename);
		return buildResources.build(filePath);
	},
	script: /**
		 * Knows how to respond when a JS source module is changed.
		 * @param {string} dir The path to the directory being watched.
		* @param {string} filename The relative path to the file that changed.
		* @returns {Promise} resolved when the change has been handled.
		*/
		function(dir, filename) {
			let filePath = path.join(dir, filename);
			return buildJs.build(filePath).then(function() {
				return buildJs.pathToModule(filename);
			});
		},
	style: /**
		 * Knows how to respond when a sass source file is changed.
		 * @param {string} dir The path to the directory being watched.
		 * @param {string} filename The relative path to the file that changed.
		 * @returns {Promise} resolved when the change has been handled.
		 */
		function(dir, filename) {
			let filePath = path.join(dir, filename);
			return buildCss.build(filePath);
		},
	test: /**
		 * Knows how to respond when a test suite is changed.
		 * @param {string} dir The path to the directory being watched.
		 * @param {string} filename The relative path to the file that changed.
		 * @returns {Promise} resolved when the change has been handled.
		 */
		function(dir, filename) {
			return new Promise(function(win) {
				themeLinter.run(path.join(dir, filename));
				grunt.option("filename", filename);
				grunt.tasks(["copy:test"], { filename: filename }, win);
			});
		}
};

hotReload.listen();
Object.keys(handlers).forEach(watchDir);

/**
 * Sets up a filesystem watch on the given source directory and copies any changed files to the corresponding subdirectory in targetRoot.
 * @param {string} type "build-util dirs" key defining The path to the source root directory to watch.
 */
function watchDir(type) {
	let dir = dirs[type];
	if (dir && dir.src) {
		requireAmd(["wc/debounce"], function (debounce) {
			console.log("Watching ", type, dir.src);
			fs.watch(dir.src, { recursive: true }, debounce(function(event, filename) {
				if (filename && event === "change") {
					console.log("File Changed ", filename);
					handlers[type](dir.src, filename).then(function(moduleName) {
						if (moduleName) {
							hotReload.notify(moduleName, type);
						}
					});
				}
			}, 200));
		});
	} else {
		console.warn("Cannot find dirs, not watching", type);
	}
}
