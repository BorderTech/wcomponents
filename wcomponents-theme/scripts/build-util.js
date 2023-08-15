/* eslint-env node, es6  */
const pkgJson = require("../package.json");
const path = require("path");
const os = require("os");
const fs = require("fs-extra");
const projectRoot = path.normalize(path.join(__dirname, ".."));
const srcRoot = path.join(projectRoot, pkgJson.directories.src);
const buildRoot = path.join(projectRoot, pkgJson.directories.target);
const targetRoot = path.join(buildRoot, "classes", "theme", pkgJson.name);
const requirejs = require("requirejs");
let mixin;

const paths = {
	"lib/sprintf": `lib/sprintf.min`,
	tinyMCE: "lib/tinymce/tinymce.min",
	mailcheck: "lib/mailcheck",
	fabric: "empty:",
	axs: "empty:",
	axe: "empty:"
};

/**
 * These are used all over the place.
 * It's brittle to keep calculating them everywhere, it is done once here and available for resuse.
 */
const dirs = {
	project: {
		basedir: projectRoot,
		src: srcRoot,
		build: buildRoot,
		target: targetRoot
	},
	i18n: {
		src: path.normalize(path.join(projectRoot, "..", "wcomponents-i18n", "src", "main", "resources")),
		target: path.join(targetRoot, "resource")
	},
	images: {
		src: path.join(srcRoot, "images"),
		target: path.join(targetRoot, "images")
	},
	script: {
		src: path.join(srcRoot, "js"),
		target: targetRoot,
		get max() {
			return path.join(this.target, "scripts_debug");
		},
		get min() {
			return path.join(this.target, "scripts");
		}
	},
	resource: {
		src: path.join(srcRoot, "resource"),
		target: path.join(targetRoot, "resource")
	},
	style: {
		src: path.join(srcRoot, "sass"),
		target: path.join(targetRoot, "style")
	},
	test: {
		src: path.join(projectRoot, pkgJson.directories.test),
		target: path.join(buildRoot, "test-classes", pkgJson.name)
	}
};

/**
 * A helper for logging the output of lint tools.
 * @param reportItem A report from a lint tool.
 */
function logLintReport(reportItem) {
	if (reportItem.messages && reportItem.messages.length) {
		console.log("Style issues found in ", reportItem.filePath);
		reportItem.messages.forEach(function(message) {
			var logString = `\t${message.message} - Ln ${message.line}, Col ${message.column}`;
			console.log(logString);
		});
	}
}

/**
 * Creates the unoptimized, unminified verion of the build.
 * @param {Object} dirPaths One of dir.script, dir.style etc
 * @param {string} [singleFile] If you simply want to build a single file.
 * @param {Function} [filter] Function to filter copied files. Return true to include, false to exclude.
 */
function buildMax(dirPaths, singleFile, filter) {
	let src = dirPaths.src,
		dest = dirPaths.max || dirPaths.target;
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
	fs.copySync(src, dest, filter);
}

/**
 * Returns the project configuration, this is the section in the package.json under "com_github_bordertech".
 * @param {string} [prop] Optionally look up a specific property.
 * @returns {Object} Project specific configuration.
 */
function getConfig (prop) {
	let result = Object.assign({}, pkgJson.com_github_bordertech);
	let username = os.userInfo().username;
	let userFile = path.join(projectRoot, `${username}.json`);
	if (fs.existsSync(userFile)) {
		mixin = mixin || requireAmd("wc/mixin");
		let overrides = require(userFile);
		mixin(overrides, result);
	}
	if (prop) {
		return result[prop];
	}
	return result;
}

/**
 * This allows you to require a module from the actual wcomponents-theme source code for use in NodeJS.
 * This is crazy madness and you have to be careful what you try to use, obviously anything that needs a DOM will not work.
 * For low-level utils, however, it is pretty handy. I wrote it so I could use "wc/debounce".
 *
 * Use it jsut like you would use "require" in AMD.
 *
 * @example
	requireAmd(["wc/debounce"], function (debounce) { var brokenLogger = debounce(console.log, 100); })
 */
function requireAmd() {
	if (!requireAmd._inited) {
		requireAmd._inited = true;
		requirejs.config({

			// baseUrl: dirs.script.src,  // Can't do this with .mjs files, so either we JIT babel or we use the build dir
			baseUrl: dirs.script.max,  // which means the build must have already run - temporary until we get rid of RequireJS
			"paths": paths,
			nodeRequire: require
		});
	}
	return requirejs.apply(this, arguments);
}

// Note that `join` with `__dirname` better than `resolve` as it cwd agnostic

module.exports = {
	dirs,
	logLintReport,
	buildMax,
	getConfig,
	requireAmd,
	paths,
	version: pkgJson.version
};
