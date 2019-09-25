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
const { logLintReport, dirs } = require("./build-util");
const sassLint = require("sass-lint");

const CLIEngine = require("eslint").CLIEngine;
const eslintCli = new CLIEngine({
	useEslintrc: true,
	ignore: true,
	configFile: path.join(dirs.project.basedir, ".eslintrc"),
	ignorePath: path.join(dirs.project.basedir, ".eslintignore")
});

if (require.main === module) {
	let len = process.argv.length,
		target = len > 2 ? process.argv[len - 1] : "";
	runSassLint();
	runEslint(target, true);

}


/**
 * What are we linting?
 * If no target is provided will fall back to linting the entire theme.
 * *@param {target} target The path to the file or dir to lint.
 * @returns {String[]} Paths to lint.
 */
function getLintTarget(target) {
	let lintTarget = target;
	if (!lintTarget) {
		return [path.join(dirs.project.basedir, "*.js"), dirs.script.src, dirs.test.src, path.join(dirs.project.basedir, "scripts", "*.js")];
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
		if (uglyReport && uglyReport.errorCount > 0) {
			if (failOnErr) {
				process.exitCode = 1;
				process.exit();
			}
		} else {
			console.log("THEME LINTER: Nothing to report besides the fact that you are awesome!");
		}
	}
	return uglyReport;
}

/**
 * Runs sass lint.
 * @param {string} [sourcePath] The path to a single sass file, if not provided the entire sass directory will be linted.
 * @returns The raw lint report.
 */
function runSassLint(sourcePath) {
	let glob = sourcePath || path.join(dirs.project.basedir, "**/!(fa)/*.scss");
	let results = sassLint.lintFiles(glob, { formatter: "stylish" }, path.join(dirs.project.basedir, ".sass-lint.yml"));
	if (results) {
		results.forEach(logLintReport);
	}
	return results;
}

module.exports = {
	run: runEslint,
	runSass: runSassLint
};
