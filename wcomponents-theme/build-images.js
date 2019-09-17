/* eslint-env node, es6  */
const { buildMax, dirs: { images: dirs } } = require("./build-util");

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
			buildMax(dirs,singleFile);
			console.timeEnd("buildImages");
			win(singleFile);
		} catch (ex) {
			lose(ex);
		}
	});
}
