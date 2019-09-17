/* eslint-env node, es6  */
const pkgJson = require("../package.json");
const path = require("path");
const fs = require("fs-extra");
const projectRoot = path.normalize(path.join(__dirname, ".."));
const srcRoot = path.join(projectRoot, pkgJson.directories.src);
const targetRoot = path.join(projectRoot, pkgJson.directories.target, "classes", "theme", pkgJson.name);
const dirs = {
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

	style: {
		src: path.join(srcRoot, "sass"),
		target: path.join(targetRoot, "style")
	},
	test: {
		src: path.join(projectRoot, pkgJson.directories.test),
		target: path.join(projectRoot, pkgJson.directories.target, "test-classes", pkgJson.name)
	}
};

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
 */
function buildMax(dirPaths, singleFile) {
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
	fs.copySync(src, dest);
}

// Note that `join` with `__dirname` better than `resolve` as it cwd agnostic

module.exports = {
	dirs,
	logLintReport,
	buildMax
};
