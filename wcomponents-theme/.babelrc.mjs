/* eslint-env node, es2020  */
import { dirs } from "./scripts/build-util.mjs";

export default function (api) {
	api.cache(true);
	const presets = [];
	const plugins = [
		["transform-commonjs", {}]
	];

	return {
		presets,
		plugins
	};
}
