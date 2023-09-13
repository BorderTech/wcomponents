/* eslint-env node, es2020  */
/*
 * This script is responsible for the JS build.
 *
 * @author Rick Brown
 */
import fs from "fs-extra";
import path from "path";
// import esmBuilder from "./scripts/esmBuilder.js";
import libBuilder from "./scripts/libs.mjs";
import { paths, getConfig, buildMax, dirs } from "./scripts/build-util.mjs";
import UglifyJS from "uglify-js";
import themeLinter from "./scripts/lintfile.mjs";
import { fileURLToPath } from "url";
const verbose = getConfig("verbose");
const __filename = fileURLToPath(import.meta.url);
const entryFile = process.argv?.[1];

let config = {
	keepBuildDir: true,  // well not really, but we'll manage this ourselves thank you
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

if (entryFile === __filename) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single JS file.
 */
async function build(singleFile) {
	console.time("buildJS");
	try {
		if (!singleFile) {
			themeLinter.run("");
			clean();
			// await esmBuilder.build(dirs.script.src, dirs.script.max);
			libBuilder.build(dirs.project.basedir, dirs.script.max);
			buildMax(dirs.script);
			// return optimize(config);
			return fs.copy(config.baseUrl, config.dir);  // TODO rewrite optimisation without r.js
		}
		return await buildSingle(singleFile);
	} finally {
		console.timeEnd("buildJS");
	}
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
	conf.name = pathToModule(fileName.replace(/.mjs$/, '.js'));
	conf.out = path.join(dirs.script.min, fileName);
	if (singleFile.endsWith('.mjs')) {
		const targetDir = path.dirname(path.join(dirs.script.max, conf.name));
		// await esmBuilder.build(singleFile, targetDir);
		buildMax(dirs.script, fileName);
	} else {
		buildMax(dirs.script, fileName);
	}
	// return optimize(conf);  // TODO rewrite optimisation without r.js
}

/**
 * Generate optimized and minified version of the scripts.
 * @param conf Configuration options for r.js.
 */
function optimize(conf) {
	noisyLog("r.js config", conf);
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

export default {
	build,
	pathToModule
};
