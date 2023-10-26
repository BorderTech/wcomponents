/* eslint-env node  */
import path from "path";
import os from "os";
import fs from "fs-extra";
import mixin from "wc/mixin.mjs";
import { fileURLToPath } from 'url';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const pkgJson = JSON.parse(fs.readFileSync("./package.json", "utf8"));
const projectRoot = path.normalize(path.join(__dirname, ".."));
const srcRoot = path.join(projectRoot, pkgJson.directories.src);
const buildRoot = path.join(projectRoot, pkgJson.directories.target);
const targetRoot = path.join(buildRoot, "classes", "theme", pkgJson.com_github_bordertech.name);

export const paths = {
	"lib/sprintf": `lib/sprintf.min`,
	tinyMCE: "lib/tinymce/tinymce.min",
	mailcheck: "lib/mailcheck",
	fabric: "empty:",
	axs: "empty:",
	axe: "empty:"
};

export const runtimeDeps = () => pkgJson.dependencies;

/**
 * These are used all over the place.
 * It's brittle to keep calculating them everywhere, it is done once here and available for resuse.
 */
export const dirs = {
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
export function logLintReport(reportItem) {
	if (reportItem.messages?.length) {
		console.log("Style issues found in ", reportItem.filePath);
		reportItem.messages.forEach(message => {
			const logString = `\t${message.message} - Ln ${message.line}, Col ${message.column}`;
			console.log(logString);
		});
	}
}

/**
 * Creates the unoptimized, unminified version of the build.
 * @param {Object} dirPaths One of `dir.script`, `dir.style` etc
 * @param {string} [singleFile] If you simply want to build a single file.
 * @param {function(src, dest): boolean} [filter] Function to filter copied files. Return true to include, false to exclude.
 */
export function buildMax(dirPaths, singleFile, filter) {
	let src = dirPaths.src,
		dest = dirPaths.max || dirPaths.target;
	if (singleFile) {
		src = path.join(src, singleFile);
		dest = path.join(dest, singleFile);
	}
	console.log(src, "->", dest);
	/*
	 * The symlink was lightning fast and meant changes in the src were instantly available with a browser reload.
	 * It was a little annoying when I deleted the content of target directory and deleted my entire src accidentally.
	 */
	// fs.symlinkSync(src, dest);
	fs.copySync(src, dest, { filter });
}

/**
 * Returns the project configuration, this is the section in the package.json under "com_github_bordertech".
 * @param {string} [prop] Optionally look up a specific property.
 * @returns {Object} Project specific configuration.
 */
export function getConfig (prop) {
	let result = Object.assign({}, pkgJson.com_github_bordertech);
	let username = os.userInfo().username;
	let userFile = path.join(projectRoot, `${username}.json`);
	if (fs.existsSync(userFile)) {
		let overrides = JSON.parse(fs.readFileSync(userFile, "utf8"));
		mixin(overrides, result);
	}
	if (prop) {
		return result[prop];
	}
	return result;
}

export const version = pkgJson.version;

// Note that `join` with `__dirname` better than `resolve` as it cwd agnostic

export default {
	dirs,
	logLintReport,
	buildMax,
	getConfig,
	paths,
	version
};
