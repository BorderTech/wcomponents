/* eslint-env node, es6  */
const { buildMax, dirs } = require("./scripts/build-util");
const fs = require("fs-extra");
const path = require("path");
const properties = require ("properties");
const defaultLocale = "en";
const themei18n = path.join(dirs.i18n.src, "com", "github", "bordertech", "wcomponents", "theme-messages.properties");

if (require.main === module) {
	build();
}

/**
 * The entry point to kick off the build.
 * TODO trim whitespace from **.xml, **.rdf, **.html, **.svg, **.mustache, **.handlebars
 * Given that these resources are small, ought to be cached and are generally not in the critical page load phase
 * I do not see this as a significant performance issue and leaving it for now. Also most of these need to be revisited
 * and probaly should not exist at all, or should be "packed" into the code where needed.
 */
function build() {
	return new Promise(function (win, lose) {
		try {
			console.time("buildResource");
			clean();
			buildMax(dirs.resource);
			console.timeEnd("buildResource");
			buildI18n();
			win();
		} catch (ex) {
			lose(ex);
		}
	});
}

/**
 * Make sure i18n message bundles are present and accounted for.
 */
function buildI18n() {
	let targetDir = path.join(dirs.i18n.target, "translation"),
		targetFile = path.join(targetDir, defaultLocale + ".json");
	fs.mkdirpSync(targetDir);

	readPropertiesFile(themei18n, targetFile);
}

/**
 * This is used to transform a java properties file to its JSON equivalent.
 */
function readPropertiesFile(propertiesFile, jsonFile) {
	properties.parse(propertiesFile, { path: true }, function (error, obj) {
		var jsonString;
		if (error) return console.error (error);
		jsonString = JSON.stringify(obj, null, 1);
		fs.writeFileSync(jsonFile, jsonString);
	});
}


/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(dirs.resource.target);
}

module.exports = {
	build
};
