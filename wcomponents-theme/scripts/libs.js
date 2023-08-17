/* eslint-env node, es6  */
/*
 * This module is responsible for third party library code in the build.
 * This may include:
 * - acquiring
 * - copying
 * - linking
 * - pre-processing
 *
 * It is a separate module because this is where much of the build complexity resides.
 * The intention is that one day we will simplify this and this module can be cleanly deleted.
 * This would be a highly probable outcome of moving away from requirejs, for example.
 *
 * @author Rick Brown
 */
const fs = require("fs-extra");
const path = require("path");
const libDir = "lib";
let minList;  // initialised on first use

/**
 * This is a map of resources from node_modules/ to lib/.
 * Each map "key" is relative to "node_modules" and each map "dest" is relative to lib/.
 * It can handle either directories or individual files.
 * If "min" is true the resource will be minified, defaults to false.
 *
 * When this map is processed the resources from node_modules will be copied or linked to lib/.
 */
let libs = {
	tinymce: {
		dest: "tinymce"
	},
	mustache: {
		dest: "mustache"
	},
	"axe-core/axe.min.js": {
		dest: "axe.min.js"
	},
	"dojo": {
		dest: "dojo",
		incl: ["has.js", "global.js", "sniff.js"],
		min: true
	},
	"sprintf-js/dist/sprintf.min.js": {
		dest: "sprintf.min.js"
	},
	"i18next/i18next.min.js": {
		dest: "i18next.js"
	},
	"requirejs/require.js": {
		dest: "require.js",
		min: true
	},
	"fabric/dist/fabric.min.js": {
		dest: "fabric.js"
	},
	"mailcheck/src/mailcheck.min.js": {
		dest: "mailcheck.js"
	},
	"socket.io-client/dist" : {
		dest: "socketio"
	}
};

/**
 * Copies or links the libs into the provided directory.
 * @param {string} rootDir The path to the project root. This allows this script to be "portable".
 * @param {string} moduleDir The path to the module directory where the "lib" will be located.
 */
function build(rootDir, moduleDir) {
	console.time("buildLibs");
	let libPaths = Object.keys(libs);
	libPaths.forEach(nextLib => {
		let item = libs[nextLib];
		let fsLibPath = path.join(rootDir, "node_modules", ...nextLib.split("/"));  // the path to the lib in node_modules
		let fsTargetPath = path.join(moduleDir, libDir, ...item.dest.split("/"));

		if (item.incl) {
			item.incl.forEach(incl => {
				let inclSubpath = path.join(...incl.split("/"));
				ensureLib(path.join(fsLibPath, inclSubpath), path.join(fsTargetPath, inclSubpath));
			});
		} else {
			ensureLib(fsLibPath, fsTargetPath);
		}
	});
	console.timeEnd("buildLibs");
}

/**
 * Ensures lib in node_modules is present in lib directory.
 * @param {string} nodeModule the path to the lib in node_modules.
 * @param {string} libTarget The location where the resource will be created in the lib directory.
 */
function ensureLib(nodeModule, libTarget) {
	if (!fs.existsSync(libTarget)) {
		console.log(nodeModule, "->", libTarget);
		let targetDir = path.dirname(libTarget);
		if (!fs.existsSync(targetDir)) {
			fs.mkdirpSync(targetDir);
		}
		// fs.symlinkSync(nodeModule, libTarget);
		fs.copySync(nodeModule, libTarget);
		sourceMap(nodeModule, libTarget);
	}
}

/**
 * Some of the minified libs will have sourcemap entries.
 * We shouldn't need them but having them prevents errors in the dev tools.
 * @param {{string} nodeModule the path to the lib in node_modules.
 * @param {string} libTarget The location where the resource will be created in the lib directory.
 */
function sourceMap(nodeModule, libTarget) {
	let mapFile = nodeModule + ".map";
	if (fs.existsSync(mapFile)) {
		let mapTarget = path.join(path.dirname(libTarget), path.basename(mapFile));
		fs.copySync(nodeModule, mapTarget);
	}
}

/**
 * By default all libs should be pre-minified and the minifier will ignore them.
 * Not only does this result in a faster build, it also ensures licence comments are preserved.
 * In some cases the libs are only provided unminified and we must minify these ones.
 * @returns {string[]} module paths that must be minified
 * e.g. "lib/dojo" means everything under "lib/dojo" will be minified
 */
function buildMinList() {
	let result = [];
	let libPaths = Object.keys(libs);
	libPaths.forEach(nextLib => {
		let item = libs[nextLib];
		if (item.min) {
			let moduleName = libDir + "/" + nextLib.replace(/\.js$/, "");
			result.push(moduleName);  // this path or any subpaths will be minified
		}
	});
	return result;
}

/**
 * Returns true if the module should be minified.
 * @param {string} module A library module, e.g. "lib/dojo/has".
 *    If the module is not a library module then the result will be true.
 * @returns {boolean} true if this module needs to be minified (as far as build-libs is concerned).
 */
function doMinify(module) {
	if (!minList) {
		minList = buildMinList();
	}
	return module.indexOf(libDir + "/") < 0 || minList.some(nextPath => module.indexOf(nextPath) === 0);
}

module.exports = {
	build,
	doMinify
};
