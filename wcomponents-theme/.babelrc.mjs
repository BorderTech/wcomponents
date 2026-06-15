
/**
 * Babel configuration
 * @param {import('@babel/core').ConfigAPI} api - API
 * @returns {import('@babel/core').ConfigFunction} Config function
 */
export default function (api) {
	api.cache(true);
	const presets = [];
	const plugins = [
		["transform-commonjs", {}]
	];

	return { presets, plugins };
}
