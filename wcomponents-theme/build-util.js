/* eslint-env node, es6  */
const pkgJson = require("./package.json");
const path = require("path");
const fs = require("fs-extra");
const dirs = {
	script: {
		src: path.join(__dirname, pkgJson.directories.src, "js"),
		target: path.join(__dirname, pkgJson.directories.target, "classes", "theme", pkgJson.name),
		get max() {
			return path.join(this.target, "scripts_debug");
		},
		get min() {
			return path.join(this.target, "scripts");
		}
	},
	test: {
		src: path.join(__dirname, pkgJson.directories.test),
		target: path.join(__dirname, pkgJson.directories.target, "test-classes", pkgJson.name)
	},
	style: {
		src: path.join(__dirname, pkgJson.directories.src, "sass"),
		target: path.join(__dirname, pkgJson.directories.target, "classes", "theme", pkgJson.name, "style")
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
	console.time("buildMax");
	let src = dirPaths.src,
		dest = dirPaths.max;
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
	console.timeEnd("buildMax");
}

// Note that `join` with `__dirname` better than `resolve` as it cwd agnostic

module.exports = {
	dirs,
	logLintReport,
	buildMax
};
