/* eslint-env node, es6  */
/*
 * This module is responsible for third party library code in the build.
 * This is essentially a straight copy of run-time node modules.
 *
 * @author Rick Brown
 */
import fs from "fs-extra";
import path from "path";
import { runtimeDeps } from "./build-util.mjs";

const libDir = "lib";

/**
 * Copies or links the libs into the provided directory.
 * @param {string} rootDir The path to the project root. This allows this script to be "portable".
 * @param {string} moduleDir The path to the module directory where the "lib" will be located.
 */
export function build(rootDir, moduleDir) {
	console.time("buildLibs");
	const deps = Object.keys(runtimeDeps());
	deps.forEach(dep => fs.copySync(path.join(rootDir, "node_modules", dep), path.join(moduleDir, libDir, dep)));
	console.timeEnd("buildLibs");
}


export default {
	build,
	doMinify: (s) => console.log(`Not implemented ${s}`)
};
