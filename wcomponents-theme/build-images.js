/* eslint-env node, es6  */
const { buildMax, dirs: { images: dirs } } = require("./scripts/build-util");
const fs = require("fs-extra");

if (require.main === module) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single file.
 */
function build(singleFile) {
	return new Promise(function (win, lose) {
		try {
			console.time("buildImages");
			if (!singleFile) {
				clean();
			}
			buildMax(dirs, singleFile);
			console.timeEnd("buildImages");
			win(singleFile);
		} catch (ex) {
			console.error("\n ----- IMAGE BUILD FAILED -----");
			console.error(ex);
			console.error("---------\n");
			lose(ex);
		}
	});
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
