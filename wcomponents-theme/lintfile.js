/* eslint-env node, es6  */
/*
 * Runs eslint on the theme js.
 * This can be used directly from commandline:
 *
 * node lintfile.js [somejs]
 *
 * If you do not provide an arg it will simply lint the theme.
 * If there is an error the exit code will not be 0.
 *
 * Or it can imported as a nodejs module via require.
 *
 */
const path = require("path");
const pjson = require("./package.json");
const srcDir = path.join(pjson.directories.src, "js");
const testSrcDir = path.join(pjson.directories.test, "intern");

const CLIEngine = require("eslint").CLIEngine;
const eslintCli = new CLIEngine({
	useEslintrc: true,
	ignore: true,
	configFile: path.join(__dirname, ".eslintrc"),
	ignorePath: path.join(__dirname, ".eslintignore")
});

if (require.main === module) {
	let report = main();
	if (report && report.errorCount > 0) {
		process.exit(1);
	}
	process.exit();
}


/**
 * What are we linting?
 * Either the last arg to the script OR will fall back to simply linting the theme js.
 * @returns {String[]} Paths to lint.
 */
function getLintTarget() {
	let lintTarget = process.argv[process.argv.length - 1];
	if (!lintTarget || /lintfile\.js$/.test(lintTarget)) {
		return ["*.js", srcDir, testSrcDir];
	}
	return [lintTarget];
}

/**
 * Run the linting - this is invoked when running as an executable directly (not imported as a module).
 * @returns The raw ESLint report.
 */
function main() {
	let lintTarget = getLintTarget();
	let report = runEslint(lintTarget);
	let formatter = eslintCli.getFormatter();
	let prettyReport = formatter(report.results);
	if (prettyReport) {
		console.log(prettyReport);
	} else {
		console.log("THEME LINTER: Nothing to report besides the fact that you are awesome!");
	}
	return report;
}


/**
 * Runs ESLint rules on the file in question and logs any warnings or errors discovered.
 * @param {String} filePath The file to scan with ESLint
 * @returns The raw ESLint report when done.
 */
function runEslint(filePath) {
	let lintTarget = filePath;
	if (lintTarget && !Array.isArray(lintTarget)) {
		lintTarget = [lintTarget];
	}
	return eslintCli.executeOnFiles(lintTarget);

}

module.exports = {
	run: runEslint
};
