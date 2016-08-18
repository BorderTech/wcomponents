"use strict";
/**
 * This script facilitates the conversion of a gettext translation (.po file) to i18next JSON.
 * The intention is to hide some of the rough edges and make the process relatively simple.
 *
 * First time setup would go a little something like this ( make sure you have nodejs installed https://nodejs.org/ ):

	git clone https://github.com/BorderTech/wcomponents.git
	cd wcomponents/wcomponents-i18n
	npm install

 * Note that you could also download the source zip instead of using git to clone the project.
 * Then you can use it like so:

	node po2json.js lang sourcePath destPath

 * for example:

	node po2json.js fr-CA /path/to/fr-CA.po /path/to/fr-CA.json

 */
const CONTEXT_SEP = "@";
var i18nextConv = require("i18next-conv");
var fs = require("fs");

main(process.argv);

/**
 * The entry point to this script.
 * @param {string[]} argv Command line arguments, the first two are expected to be node invocation and are ignored.
 */
function main(argv) {
	if (argv && argv.length > 4 && argv[2] && argv[3] && argv[4]) {
		po2json(argv[2], argv[3], argv[4]);
	} else {
		console.log("Example usage: node po2json.js fr-CA /path/to/fr-CA.po /path/to/fr-CA.json");
		if (argv) {
			console.log("Got ", argv.join());
		}
	}
}

/**
 * The conversion done by i18nextConv seems a little buggy, this routine cleans up known issues.
 * @param {string} resultJSON The result after conversion (just before it is written to file).
 * @returns {string} The fixed result JSON string, go ahead and write it to the filesystem.
 */
function fixResult(resultJSON) {
	var result = JSON.parse(resultJSON);
	let keys = Object.keys(result);
	keys.forEach(function(key) {
		var sepIdx = key.indexOf(CONTEXT_SEP);
		if (sepIdx > 0) {
			let newKey = key.substr(sepIdx + 1);
			if (newKey) {
				result[newKey] = result[key];
				delete result[key];
			}
		}
	});
	return JSON.stringify(result, null, 1);
}

/**
 * Invoke i18nextConv with necessary options.
 * @param {string} lang The domain.
 * @param {string} sourceFile The path to the gettext file to convert.
 * @param {string} destFile The path to the json file to write
 */
function po2json(lang, sourceFile, destFile) {
	var options = { keyasareference: true, ctxSeparator: CONTEXT_SEP };
	var save = saveFactory(destFile);
	var gettextFile = fs.readFileSync(sourceFile);

	i18nextConv.gettextToI18next(lang, gettextFile, options).then(save);
}

/**
 * Generates a save function curried with the destination path.
 * @param {type} target
 * @returns {Function}
 */
function saveFactory(target) {
	/**
	 * Saves the result string to the filesystem.
	 * @param {string} result The string to write to file.
	 */
	return function(result) {
		var fixedResult = fixResult(result);
		if (fixResult) {
			fs.writeFileSync(target, fixedResult);
		}
	};
}
