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

console.log("Watching ", srcDir);

watchDir(srcDir, targetDir);
watchDir(testDir, targetTestDir);

function watchDir(sourceRoot, targetRoot) {
	fs.watch(sourceRoot, { recursive: true }, (event, filename) => {
		if (filename && event === "change") {
			let targetPath = path.join(targetRoot, filename);
			let sourcePath = path.join(sourceRoot, filename);
			let targetDir = path.dirname(targetPath);
			console.log("File Changed ", sourcePath);
			exists(targetDir, copyFile(sourcePath, targetPath));
		}
	});
}


function copyFile(sourcePath, targetPath) {
	return function(exists) {
		if (exists) {
			fs.copyFile(sourcePath, targetPath, (err) => {
				if (err) console.error(err);
				console.log(`${sourcePath} was copied to ${targetPath}`);
			});
		} else {
			console.log("Nothing to overwrite, please complete a full build first", targetPath);
		}
	};
}

function exists(fsPath, callback) {
	fs.stat(fsPath, function(err, stat) {
		if(err == null) {
			callback(true);
		} else {
			callback(false);
		}
	});
}
