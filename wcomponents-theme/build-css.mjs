/* eslint-env node, es6  */
import fs from "fs-extra";
import path from "path";
// const { dirs: { style: dirs } } = require("./scripts/build-util");
import { dirs } from "./scripts/build-util.mjs";
import sass from "sass";
import themeLinter from "./scripts/lintfile.mjs";
import { fileURLToPath } from 'url';
const { images: styleDirs } = dirs;
const __filename = fileURLToPath(import.meta.url);

const entryFile = process.argv?.[1];
if (entryFile === __filename) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single file.
 *    Note: this currently only affects the linting.
 */
function build(singleFile) {
	return new Promise(function (win, lose) {
		try {
			console.time("buildCss");
			themeLinter.runSass(singleFile);
			clean();
			fs.mkdirpSync(styleDirs.target);
			let result = compileAllSass();
			console.timeEnd("buildCss");
			win(result);
		} catch (ex) {
			lose(ex);
		}
	});
}

function compileAllSass() {
	let compiled = [];
	let errors = [];
	let files = fs.readdirSync(styleDirs.src);
	files.forEach(function (file) {
		if (/^[^_].+\.scss$/.test(file)) {
			let sassFile = path.join(styleDirs.src, file);
			let cssFile = path.basename(file, ".scss");
			compiled.push(cssFile);
			cssFile = path.join(styleDirs.target, cssFile + ".css");
			try {
				compileSass(sassFile, cssFile, file.indexOf("debug") >= 0);
			} catch (ex) {
				errors.push(ex);
			}
		}
	});
	if (errors.length) {
		throw (errors);
	}
	return compiled;
}

function compileSass(sassFile, cssFile, isDebug) {
	let result = sass.renderSync({
		sourceMap: false,
		file: sassFile,
		outputStyle: isDebug ? "expanded" : "compressed"
	});
	if (result && result.css) {
		result = result.css.toString();
		if (cssFile) {
			console.log("Compiling", cssFile);
			fs.writeFileSync(cssFile, result, "utf8");
		}
	} else {
		console.warn("No result from sass for ", sassFile);
	}
	return result;
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(styleDirs.target);
}

export default {
	build
};
