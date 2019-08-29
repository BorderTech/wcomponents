/**
 * You can use this when working on theme JS to speed up development.
 * After running the initial complete build once you may then run this file:
 *
 * `node theme-watch.js`
 *
 * As you save changes to JS files they will be immediately copied to the "scripts_debug" folder in the target directory.
 * You should merely need to reload your browser to see the changes.
 *
 * Note, you will generally be running in debug mode while developing: https://github.com/BorderTech/wcomponents/wiki/Debugging-a-theme
 */
const fs = require("fs");
const path = require("path");
const pjson = require("./package.json");
const themeLinter = require("./lintfile");
const srcDir = path.join(__dirname, pjson.directories.src, "js");
const targetDir = path.join(__dirname, pjson.directories.target, "classes/theme/wcomponents-theme/scripts_debug");
const testDir = path.join(__dirname, pjson.directories.test, "intern");
const targetTestDir = path.join(__dirname, pjson.directories.target, "test-classes/wcomponents-theme/intern");
const sassSrcDir = path.join(__dirname, pjson.directories.src, "sass");
const sassTargetDir = path.join(__dirname, pjson.directories.target, "classes/theme/wcomponents-theme/style");

const CLIEngine = require("eslint").CLIEngine;
const eslintCli = new CLIEngine();

const sass = require("sass");
const sassLint = require("sass-lint");

watchDir(srcDir, targetDir, fileProcessCopy.bind(null, regularCopy));
watchDir(testDir, targetTestDir, fileProcessCopy.bind(null, regularCopy));
watchDir(sassSrcDir, sassTargetDir, fileProcessCopy.bind(null, processSassFile));

/**
 * Sets up a filesystem watch on the given source directory and copies any changed files to the corresponding subdirectory in targetRoot.
 * @param {String} sourceRoot The path to the source root directory to watch.
 * @param {String} targetRoot The path to the root target directory where files will be copied as they change (honoring subdirectory structure relative to sourceRoot).
 *     For example ${sourceRoot}/foo/bar/kungfoo.js will be copied to ${targetRoot}/foo/bar/kungfoo.js
 * @param {Function(String)} [processFunc] Optionally provide a function to process file contents before it is copied.
 */
function watchDir(sourceRoot, targetRoot, processFunc) {
	console.log("Watching ", sourceRoot);
	fs.watch(sourceRoot, { recursive: true }, (event, filename) => {
		if (filename && event === "change") {
			let targetPath = path.join(targetRoot, filename);
			let sourcePath = path.join(sourceRoot, filename);
			let targetDir = path.dirname(targetPath);
			console.log("File Changed ", sourcePath);
			lintFile(sourcePath);
			if (fs.existsSync(targetDir)) {
				processFunc(sourcePath, targetPath);
			} else {
				console.log("Nothing to overwrite, please complete a full build first", targetDir);
			}
		}
	});
}

function lintFile(sourcePath) {
	let ext = path.extname(sourcePath);
	try {
		if (ext === ".js") {
			runEslint(sourcePath);
		} else if (ext === ".scss") {
			runSassLint(sourcePath);
		}
	} catch(ex) {
		console.warn(ex);
	}
}

function runSassLint(sourcePath) {
	var results = sassLint.lintFiles(sourcePath, { formatter: "stylish" });
	if (results) {
		results.forEach(logLintReport);
	}
}

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
 * Runs ESLint rules on the file in question and logs any warnings or errors discovered.
 * @param {String} filePath The file to scan with ESLint
 */
function runEslint(filePath) {
	let report = themeLinter.run(filePath);
	if (report && report.results) {
		report.results.forEach(logLintReport);
	}
}

function processSassFile(sourcePath, targetPath, callback) {
	// TODO this should not actually compile the changed sass file but instead should recompile the top level file/s each time any sass file is changed
	let sassProcess = function(err, result) {
			let data;
			try {
				if (result && result.css) {
					data = result.css.toString();
				} else {
					console.warn("No result from sass for ", sourcePath);
				}
			} finally {
				callback(err, data);
			}
		};
	sass.render({ file: sourcePath }, sassProcess);
}

//function processTestFile(sourcePath, targetPath, callback) {
//	let replacer = function(data) {
//			return data.replace(/@RESOURCES@/g, "/target/test-classes/wcomponents-theme/intern/resources");  // this is a relative URI
//		};
//	fs.readFile(sourcePath, "utf8", function(err, data) {
//		var fileData;
//		try {
//			if (!err) {
//				fileData = replacer(data);
//			}
//		} finally {
//			callback(err, fileData);
//		}
//	});
//}


/**
 * Performs a file simple copy.
 * @param {String} sourcePath The path to the source file.
 * @param {String} targetPath The path to the destination file (will overwrite, parent directory must exist).
 * @param {Function} callback Called when done.
 */
function regularCopy(sourcePath, targetPath, callback) {
	fs.copyFile(sourcePath, targetPath, (err) => {
		if (!err) {
			console.log(`${sourcePath} was copied to ${targetPath}`);
		}
		callback(err);
	});
}

/**
 * Performs a copy but allows a function to process the file first.
 * @param {Function(String)} processFunc Will be passed the content of sourcePath, the return value will be copied to targetPath.
 * @param {String} sourcePath The path to the source file.
 * @param {String} targetPath The path to the destination file (will overwrite, parent directory must exist).
 */
function fileProcessCopy(processFunc, sourcePath, targetPath) {
	processFunc(sourcePath, targetPath, function (err, data) {
		if (err) {
			console.error(err);
		} else if (data) {
			fs.writeFile(targetPath, data, "utf8", function (err) {
				if (err){
					console.error(err);
				} else {
					console.log(`${sourcePath} was processed and written to ${targetPath}`);
				}
			});
		}
	});
}
