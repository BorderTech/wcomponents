/* eslint-env node, es2020  */
const { dirs } = require("./scripts/build-util");

module.exports = function (api) {
	api.cache(true);
	const presets = [];
	const plugins = [
		["transform-import-as-amd", {
			"moduleName": false,
			basePath: dirs.script.src
		}]
	];

	return {
		presets,
		plugins
	};
}
