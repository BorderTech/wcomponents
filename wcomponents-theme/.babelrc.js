/* eslint-env node, es2020  */
import { dirs } from "./scripts/build-util.mjs";

module.exports = function (api) {
	api.cache(true);
	const presets = [];
	const plugins = [
		["transform-import-as-amd", {
			"moduleName": false,
			basePath: dirs.script.src
		}],
		["transform-commonjs", {}]
	];

	return {
		presets,
		plugins
	};
}
