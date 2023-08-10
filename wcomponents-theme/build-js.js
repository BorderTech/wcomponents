/* eslint-env node, es2020  */
/*
 * This script is responsible for the JS build.
 *
 * @author Rick Brown
 */
const requirejs = require("requirejs");
const fs = require("fs-extra");
const path = require("path");
const esmBuilder = require("./scripts/esmBuilder");
const libBuilder = require("./scripts/libs");
const { paths, getConfig, buildMax, dirs } = require("./scripts/build-util");
const UglifyJS = require("uglify-js");
const themeLinter = require("./scripts/lintfile");
const verbose = getConfig("verbose");

let config = {
	keepBuildDir: true,  // well not really but we'll manage this ourselves thank you
	preserveLicenseComments: false,
	// appDir: `${pkgJson.directories.src}/js`,
	baseUrl: dirs.script.max,
	optimize: "none",
	optimizeAllPluginResources: true,
	normalizeDirDefines: "all",
	// fileExclusionRegExp: /\.mjs$/,
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
	dir: dirs.script.min,
	logLevel: 2,
	modules: [{
		name: "wc/common"
	}],
	paths: paths
};

if (require.main === module) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single JS file.
 */
async function build(singleFile) {
	console.time("buildJS");
	if (!singleFile) {
		themeLinter.run("", true);
		clean();
		await esmBuilder.build(dirs.script.src, dirs.script.max);
		libBuilder.build(dirs.project.basedir, dirs.script.max);
		buildMax(dirs.script);
		return optimize(config);
	}
	return await buildSingle(singleFile);
}

/*
 * Entry point for building a single file.
 * @param {string} singleFile If you want to build a single JS file.
 */
async function buildSingle(singleFile) {
	let fileName = singleFile;
	themeLinter.run(singleFile);
	fileName = fileName.replace(dirs.script.src, "");
	let conf = config;
	Object.assign({}, conf);
	delete conf.modules;
	conf.dir = "";
	conf.name = pathToModule(fileName);
	conf.out = path.join(dirs.script.min, fileName);
	if (singleFile.endsWith('.mjs')) {
		const targetDir = path.dirname(path.join(dirs.script.max, conf.name));
		await esmBuilder.build(singleFile, targetDir);
	} else {
		buildMax(dirs.script, fileName);
	}
	return optimize(conf);
}

/**
 * Generate optimized and minified version of the scripts.
 * @param conf Configuration options for r.js.
 */
function optimize(conf) {
	noisyLog("r.js config", conf);
	return new Promise(function(win, lose) {
		requirejs.optimize(conf, function (buildResponse) {
			noisyLog(buildResponse);
			console.timeEnd("buildJS");
			win();
		}, function(err) {
			console.error(err);
			lose(err);
		});
	});
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(dirs.script.max);
	fs.removeSync(dirs.script.min);
}

/**
 * Particularly noisy logging can go through here as it is off by default.
 */
function noisyLog() {
	if (verbose) {
		console.log.apply(console, arguments);
	}
}

/**
 * Determines a module name from a filesystem path.
 * @param {string} modulePath The full path to a JS file on the filesystem.
 * @returns {string} The module name.
 */
function pathToModule(modulePath) {
	let moduleName = modulePath.replace(dirs.script.target, "");
	moduleName = moduleName.replace(/\\/g, "/").replace(/^\/|\.m?js$/g, "");
	return moduleName;
}

module.exports = {
	build,
	pathToModule
};
