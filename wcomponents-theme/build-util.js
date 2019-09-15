/* eslint-env node, es6  */
const pkgJson = require("./package.json");
const path = require("path");
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

// Note that `join` with `__dirname` better than `resolve` as it cwd agnostic

module.exports = {
	dirs,
	logLintReport
};
