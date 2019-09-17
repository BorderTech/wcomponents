/* eslint-env node, es6  */
const fs = require("fs-extra");
const path = require("path");
const { dirs: {style: dirs}} = require("./build-util");
const sass = require("sass");
const themeLinter = require("./lintfile");

if (require.main === module) {
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
			console.time("build");
			themeLinter.runSass(singleFile);
			clean();
			fs.mkdirpSync(dirs.target);
			win(compileAllSass());
		} catch (ex) {
			lose(ex);
		}
	});
}

function compileAllSass() {
	let compiled = [];
	let files = fs.readdirSync(dirs.src);
	files.forEach(function (file) {
		if (/^[^_].+\.scss$/.test(file)) {
			let sassFile = path.join(dirs.src, file);
			let cssFile = path.basename(file, ".scss");
			compiled.push(cssFile);
			cssFile = path.join(dirs.target, cssFile + ".css");
			compileSass(sassFile, cssFile, file.indexOf("debug") >= 0);
		}
	});
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
	fs.removeSync(dirs.target);
}

module.exports = {
	build
};
