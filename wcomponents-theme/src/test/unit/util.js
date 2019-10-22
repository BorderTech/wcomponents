/* eslint-env node, es6  */
const path = require("path");
const fs = require("fs-extra");

/**
 * Finds the project root from a subdirectory whereever.
 * Two reasons:
 * 1. This sucks: require("../../../../../somedir/somemodule")
 * 2. The above is not portable, if the script is copied or moved then it will break.
 *
 * @param {string} here The starting point.
 * @returns {string} The project root.
 */
function getProjectRoot(here) {
	let landmarks = ["package.json", "node_modules"].map(landmark => path.join(here, landmark));
	let found = landmarks.every(landmark => fs.existsSync(landmark));
	if (!found) {
		let parent = path.dirname(here);
		if (parent && parent !== here) {
			return getProjectRoot(parent);
		}
		console.warn("Could not find project root");
	} else {
		console.log("Found project root at", here);
	}
	return here;
}


let basedir = getProjectRoot(__dirname);

/**
 * Require a module relative to the project root dir.
 * @param {string} modulePath Exactly what you would pass to require, but relative to the project root.
 * @returns {object} The result of calling require.
 */
function requireRoot(modulePath) {
	return require(path.join(basedir, modulePath));
}

module.exports = {
	requireRoot
};
