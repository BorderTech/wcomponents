/* eslint-env node, es6  */
import { version, buildMax, dirs } from "./scripts/build-util.mjs";
import fs from "fs-extra";
import path from "path";
import {fileURLToPath} from "url";
const __filename = fileURLToPath(import.meta.url);
const entryFile = process.argv?.[1];

import properties from "properties";
const defaultLocale = "en";
const themei18n = path.join(dirs.i18n.src, "com", "github", "bordertech", "wcomponents", "theme-messages.properties");

if (entryFile === __filename) {
	build();
}

/**
 * The entry point to kick off the build.
 * TODO trim whitespace from **.xml, **.rdf, **.html, **.svg, **.mustache, **.handlebars
 * Given that these resources are small, ought to be cached and are generally not in the critical page load phase
 * I do not see this as a significant performance issue and leaving it for now. Also most of these need to be revisited
 * and probably should not exist at all, or should be "packed" into the code where needed.
 */
function build() {
	return new Promise(function (win, lose) {
		try {
			console.time("buildResource");
			clean();
			buildMax(dirs.resource);
			console.timeEnd("buildResource");
			buildI18n();
			buildProps();
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
		if (error) return console.error (error);
		const jsonString = JSON.stringify(obj, null, 1);
		fs.writeFileSync(jsonFile, jsonString);
	});
}

/**
 * This is largely superfluous and a legacy of debugging builds many years in the past.
 * It used to dump many different properties.
 * HOWEVER one part of it has found new life (in ThemeUtil.java) and we must rewrite that
 * before we get rid of this.
 */
function buildProps() {
	let propFile = path.join(dirs.project.target, "version.properties");
	fs.writeFileSync(propFile, `build.number=${version}\n`, "utf8");
}

/**
 * Clean the output of previous builds.
 */
function clean() {
	fs.removeSync(dirs.resource.target);
}

export default {
	build
};
