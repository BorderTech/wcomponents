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
const { dirs } = require("./build-util");
const testSrcDir = path.join(dirs.test.src, "intern");

const CLIEngine = require("eslint").CLIEngine;
const eslintCli = new CLIEngine({
	useEslintrc: true,
	ignore: true,
	configFile: path.join(__dirname, ".eslintrc"),
	ignorePath: path.join(__dirname, ".eslintignore")
});

if (require.main === module) {
	let len = process.argv.length,
		target = len > 2 ? process.argv[len - 1] : "";
	runEslint(target, true);
}


/**
 * What are we linting?
 * If no target is provided will fall back to linting the theme js.
 * @returns {String[]} Paths to lint.
 */
function getLintTarget(target) {
	let lintTarget = target;
	if (!lintTarget) {
		return ["*.js", dirs.script.src, testSrcDir];
	} else if (!Array.isArray(lintTarget)) {
		lintTarget = [lintTarget];
	}
	return lintTarget;
}

/**
 * Runs ESLint rules on the file in question and logs any warnings or errors discovered.
 * @param {string} target The path to the file to lint
 * @param {boolean} if true the process will be terminated if any errors are encountered.
 * @returns The raw ESLint report when done.
 */
function runEslint(target, failOnErr) {
	let lintTarget = getLintTarget(target);
	let uglyReport =  eslintCli.executeOnFiles(lintTarget);
	let formatter = eslintCli.getFormatter();
	let prettyReport = formatter(uglyReport.results);
	if (prettyReport) {
		console.log(prettyReport);
		if (failOnErr && uglyReport && uglyReport.errorCount > 0) {
			process.exitCode = 1;
			if (failOnErr) {
				process.exit();
			}
		} else {
			console.log("THEME LINTER: Nothing to report besides the fact that you are awesome!");
		}
	}
	return uglyReport;
}

module.exports = {
	run: runEslint
};
