// const { buildMax, dirs: { images: dirs } } = require("./scripts/build-util");
import console from 'node:console';
import process from "node:process";
import { fileURLToPath } from "node:url";

import fs from "fs-extra";

import { buildMax, dirs } from "./scripts/build-util.mjs";

const { images: imageDirs } = dirs;
const __filename = fileURLToPath(import.meta.url);
const entryFile = process.argv?.[1];
if (entryFile === __filename) {
	build();
}

/**
 * The entry point to kick off the entire build.
 * @param {string} [singleFile] If you want to build a single file.
 * @returns {Promise<any>} ?
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
