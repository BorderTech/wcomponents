/* eslint-env node  */
// const { buildMax, dirs: { images: dirs } } = require("./scripts/build-util");
import { buildMax, dirs } from "./scripts/build-util.mjs";
import fs from "fs-extra";
import { fileURLToPath } from "url";
const { images: imageDirs } = dirs;
const __filename = fileURLToPath(import.meta.url);
const entryFile = process.argv?.[1];
if (entryFile === __filename) {
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
			buildMax(imageDirs, singleFile);
			console.timeEnd("buildImages");
			win(singleFile);
		} catch (ex) {
			lose(ex);
		}
	});
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(imageDirs.target);
}

export default {
	build
};
