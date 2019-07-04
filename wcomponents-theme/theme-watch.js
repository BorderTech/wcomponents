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
const srcDir = "./src/main/js/";
const targetDir = "./target/classes/theme/wcomponents-theme/scripts_debug/";
const testDir = "./src/test/intern/";
const targetTestDir = "./target/test-classes/wcomponents-theme/intern/";

const CLIEngine = require("eslint").CLIEngine;
const eslintCli = new CLIEngine();

console.log("Watching ", srcDir);

watchDir(srcDir, targetDir);
watchDir(testDir, targetTestDir, function(data) {
	return data.replace(/@RESOURCES@/g, "/target/test-classes/wcomponents-theme/intern/resources");
});

/**
 * Sets up a filesystem watch on the given source directory and copies any changed files to the corresponding subdirectory in targetRoot.
 * @param {String} sourceRoot The path to the source root directory to watch.
 * @param {String} targetRoot The path to the root target directory where files will be copied as they change (honoring subdirectory structure relative to sourceRoot).
 *     For example ${sourceRoot}/foo/bar/kungfoo.js will be copied to ${targetRoot}/foo/bar/kungfoo.js
 * @param {Function(String)} [processFunc] Optionally provide a function to process file contents before it is copied.
 */
function watchDir(sourceRoot, targetRoot, processFunc) {
	fs.watch(sourceRoot, { recursive: true }, (event, filename) => {
		if (filename && event === "change") {
			let targetPath = path.join(targetRoot, filename);
			let sourcePath = path.join(sourceRoot, filename);
			let targetDir = path.dirname(targetPath);
			console.log("File Changed ", sourcePath);
			exists(targetDir, copyFile(sourcePath, targetPath, processFunc));
			runEslint(sourcePath);
		}
	});
}

/**
 * Runs ESLint rules on the file in question and logs any warnings or errors discovered.
 * @param {String} filePath The file to scan with ESLint
 */
function runEslint(filePath) {
	let report = eslintCli.executeOnFiles([filePath]);
	if (report && report.results) {
		report.results.forEach(function(reportItem) {
			if (reportItem.messages && reportItem.messages.length) {
				console.log("Style issues found in ", reportItem.filePath);
				reportItem.messages.forEach(function(message) {
					var logString = `\t${message.message} - Ln ${message.line}, Col ${message.column}`;
					console.log(logString);
				});
			}
		});
	}
}

function copyFile(sourcePath, targetPath, processFunc) {
	return function(exists) {
		if (exists) {
			if (processFunc) {
				processCopy(sourcePath, targetPath, processFunc);
			} else {
				regularCopy(sourcePath, targetPath);
			}
		} else {
			console.log("Nothing to overwrite, please complete a full build first", targetPath);
		}
	};
}

/**
 * Given a filesystem path will call the callback with a boolean indicating whether the path exists or not.
 * @param {String} fsPath The filesystem path to check.
 * @param {Function(Boolean)} callback Called with true if the path exists.
 */
function exists(fsPath, callback) {
	fs.stat(fsPath, function(err, stat) {
		if(err == null) {
			callback(true);
		} else {
			callback(false);
		}
	});
}

/**
 * Performs a file simple copy.
 * @param {String} sourcePath The path to the source file.
 * @param {String} targetPath The path to the destination file (will overwrite, parent directory must exist).
 */
function regularCopy(sourcePath, targetPath) {
	fs.copyFile(sourcePath, targetPath, (err) => {
		if (err) {
			console.error(err);
		} else {
			console.log(`${sourcePath} was copied to ${targetPath}`);
		}
	});
}

/**
 * Performs a copy but allows a function to process the file content first.
 * @param {String} sourcePath The path to the source file.
 * @param {String} targetPath The path to the destination file (will overwrite, parent directory must exist).
 * @param {Function(String)} processFunc Will be passed the content of sourcePath, the return value will be copied to targetPath.
 */
function processCopy(sourcePath, targetPath, processFunc) {
	fs.readFile(sourcePath, "utf8", function (err, data) {
		var fileData;
		if (err) {
			console.error(err);
		} else {
			fileData = processFunc(data);
		}

		fs.writeFile(targetPath, fileData, "utf8", function (err) {
			if (err) console.error(err);
		});
	});
}
