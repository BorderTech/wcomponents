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
const scriptSrcDir = path.join(__dirname, pkgJson.directories.src, "js");  // `join` with `__dirname` better than `resolve` as it cwd agnostic
const scriptRootDir = path.join(__dirname, pkgJson.directories.target, "classes", "theme", pkgJson.name);
const scriptDebugDir = path.join(scriptRootDir, "scripts_debug");
const scriptMinDir = path.join(scriptRootDir, "scripts");
const UglifyJS = require("uglify-es");
let config = {
	keepBuildDir: true,  // well not really but we'll manage this ourselves thank you
	preserveLicenseComments: false,
	// appDir: `${pkgJson.directories.src}/js`,
	baseUrl: scriptDebugDir,
	optimize: "none",
	optimizeAllPluginResources: true,
	normalizeDirDefines: "all",
	generateSourceMaps: false,
	onBuildWrite(moduleName, fsPath, contents) {
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
	dir: scriptMinDir,
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

build();

/**
 * The entry point to kick off the entire build.
 */
function build() {
	console.time("build");
	clean();
	createDebugVersion();
	libBuilder.build(__dirname, scriptDebugDir);
	optimize();
}

/**
 * Creates the unoptimized, unminified verion of the build.
 */
function createDebugVersion() {
	console.time("createDebugVersion");
	console.log(scriptSrcDir, "->", scriptDebugDir);
	/*
	 * The symlink was lightning fast and meant changes in the src were instantly available with a browser reload.
	 * It was a little annoying when I deleted the content of target directory and deleted my entire src accidentially.
	 */
	// fs.symlinkSync(scriptSrcDir, scriptDebugDir);
	fs.copySync(scriptSrcDir, scriptDebugDir);
	console.timeEnd("createDebugVersion");
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(scriptDebugDir);
	fs.removeSync(scriptMinDir);
}

/**
 * Generate optimized and minified version of the scripts.
 */
function optimize() {
	requirejs.optimize(config, function (buildResponse) {
		noisyLog(buildResponse);
		console.timeEnd("build");
	}, function(err) {
		console.error(err);
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
