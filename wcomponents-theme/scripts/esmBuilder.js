/* eslint-env node, es2020  */
/*
 * This module is responsible for transpiling ES6 modules to AMD.

 * This is a temporary measure allowing us to migrate slowly to ESM without a big-bang approach.
 * Once all modules are ES6 we can delete this build and run native modules in the browser.
 *
 * The hardest part will not really be this syntactical change but probably around lifecycle.
 * We will probably also need an `importmap` https://github.com/WICG/import-maps
 * or restructure the modules to use actual relative paths.
 *
 * @author Rick Brown
 */
const { default: babel } = require('@babel/cli/lib/babel/dir');

async function transform(sourcePath, outputDir) {
	await babel({
		babelOptions: {},
		cliOptions: {
			filenames: [sourcePath],
			extensions: ['.mjs'],
			outDir: outputDir,
			copyFiles: true,
			copyIgnored: false
		},
	});
}

async function build(sourcePath, targetDir) {
	console.time("buildEsm");
	await transform(sourcePath, targetDir);
	console.timeEnd("buildEsm");
}

module.exports = {
	build
};
