/* eslint-env node, es6  */
/*
 * This script is responsible for the JS build.
 *
 * @author Rick Brown
 */
const requirejs = require("requirejs");
const pkgJson = require("./package.json");
const fs = require("fs-extra");
const path = require("path");
const libBuilder = require("./scripts/libs");
const { dirs: { script: dirs } } = require("./build-util");
const UglifyJS = require("uglify-es");
const themeLinter = require("./lintfile");
let config = {
	keepBuildDir: true,  // well not really but we'll manage this ourselves thank you
	preserveLicenseComments: false,
	// appDir: `${pkgJson.directories.src}/js`,
	baseUrl: dirs.max,
	optimize: "none",
	optimizeAllPluginResources: true,
	normalizeDirDefines: "all",
	generateSourceMaps: false,
	onBuildWrite: function (moduleName, fsPath, contents) {
		// r.js overrides `require` saving the original function as `require.nodeRequire`
		let result = contents;
		if (libBuilder.doMinify(moduleName)) {  // Most libs should be pre-minified
			console.log("Minifying", moduleName);
			result = UglifyJS.minify(result).code;
		} else {
			console.log("Skipping ", moduleName);
		}
		return result;
	},
	dir: dirs.min,
	logLevel: 2,
	modules: [{
		name: "wc/common"
	}],
	paths: {
		tinyMCE: "lib/tinymce/tinymce.min",
		fabric: "empty:",
		axs: "empty:",
		axe: "empty:"
	}
};

if (require.main === module) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single JS file.
 */
function build(singleFile) {
	console.time("build");
	if (!singleFile) {
		themeLinter.run("", true);
		clean();
		libBuilder.build(__dirname, dirs.max);
		createDebugVersion();
		return optimize(config);
	} else {
		return buildSingle(singleFile);
	}
}

/*
 * Entry point for building a single file.
 * @param {string} singleFile If you want to build a single JS file.
 */
function buildSingle(singleFile) {
	let fileName = singleFile;
	themeLinter.run(singleFile);
	fileName = fileName.replace(dirs.src, "");
	let conf = config;
	Object.assign({}, conf);
	delete conf.modules;
	conf.dir = "";
	conf.name = pathToModule(fileName);
	conf.out = path.join(dirs.max, fileName + ".js");
	createDebugVersion(fileName);
	return optimize(conf);
}

/**
 * Creates the unoptimized, unminified verion of the build.
 * @param {string} [singleFile] If you simply want to build a single JS file.
 */
function createDebugVersion(singleFile) {
	console.time("createDebugVersion");
	let src = dirs.src,
		dest = dirs.max;
	if (singleFile) {
		src = path.join(src, singleFile);
		dest = path.join(dest, singleFile);
	}
	console.log(src, "->", dest);
	/*
	 * The symlink was lightning fast and meant changes in the src were instantly available with a browser reload.
	 * It was a little annoying when I deleted the content of target directory and deleted my entire src accidentially.
	 */
	// fs.symlinkSync(src, dest);
	fs.copySync(src, dest);
	console.timeEnd("createDebugVersion");
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(dirs.max);
	fs.removeSync(dirs.min);
}

/**
 * Generate optimized and minified version of the scripts.
 * @param conf Configuration options for r.js.
 */
function optimize(conf) {
	return new Promise(function(win, lose) {
		requirejs.optimize(conf, function (buildResponse) {
			noisyLog(buildResponse);
			console.timeEnd("build");
			win();
		}, function(err) {
			console.error(err);
			lose(err);
		});
	});
}

/**
 * Particularly noisy logging can go through here as it is off by default.
 */
function noisyLog() {
	if (pkgJson.com_github_bordertech.verbose) {
		console.log.apply(console, arguments);
	}
}

/**
 * Determines a module name from a filesystem path.
 * @param {string} modulePath The full path to a JS file on the filesystem.
 * @returns {string} The module name.
 */
function pathToModule(modulePath) {
	let moduleName = modulePath.replace(dirs.src, "");
	moduleName = modulePath.replace(dirs.target, "");
	moduleName = moduleName.replace(/\\/g, "/").replace(/^\/|\.js$/g, "");
	return moduleName;
}

module.exports = {
	build,
	buildSingle,
	pathToModule
};
