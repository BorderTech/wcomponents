/* eslint-env node, es2020  */
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
import path from "path";
import { logLintReport, dirs } from "./build-util.mjs";
import sassLint from "sass-lint";
import { fileURLToPath } from 'url';
import { ESLint } from "eslint";
const __filename = fileURLToPath(import.meta.url);
const eslintCli = new ESLint({
	useEslintrc: true,
	ignore: true,
	extensions: ['.js', '.mjs']
});


const entryFile = process.argv?.[1];
if (entryFile === __filename) {
	let len = process.argv.length,
		target = len > 2 ? process.argv[len - 1] : "";
	runSassLint();
	runEslint(target).catch((error) => {
		process.exitCode = 1;
		console.error(error);
	});
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
		return [
			path.join(dirs.project.basedir, "*.*js"),
			dirs.script.src,
			dirs.test.src,
			path.join(dirs.project.basedir, "scripts", "*.*js")
		];
	} else if (!Array.isArray(lintTarget)) {
		lintTarget = [lintTarget];
	}
	return lintTarget;
}

/**
 * Runs ESLint rules on the file in question and logs any warnings or errors discovered.
 * @param {string} target The path to the file to lint
 * @returns The raw ESLint results when done.
 */
async function runEslint(target) {
	const lintTarget = getLintTarget(target);
	const uglyReport =  await eslintCli.lintFiles(lintTarget);
	const formatter = await eslintCli.loadFormatter();
	const prettyReport = formatter.format(uglyReport);
	const message = "THEME LINTER: No fatal lint errors.";
	if (prettyReport) {
		console.log(prettyReport);
		let fatalErrorResults = ESLint.getErrorResults(uglyReport).filter((result) => {
			return result.fatalErrorCount > 0;
		});
		if (!fatalErrorResults.length) {
			console.log(message);
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

export default {
	run: runEslint,
	runSass: runSassLint
};
