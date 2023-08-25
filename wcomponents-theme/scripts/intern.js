/* eslint-env node, es6  */
const path = require("path");
const { getConfig, dirs, requireAmd } = require("./build-util");
const scriptDir = path.relative(dirs.script.target, dirs.script[getConfig("testMinOrMax")]);  // dirs.script.min will test minifed code, dirs.script.max tests debug code
const targetDir = path.relative(dirs.project.basedir, dirs.project.build);
const mixin = requireAmd("wc/mixin");
const internOverrides = getConfig("internOverrides");
let testRootPath = path.join(dirs.test.target, "intern");
let srcRootPath = dirs.script.target;

testRootPath = path.relative(dirs.project.basedir, testRootPath);
srcRootPath = path.relative(dirs.project.basedir, srcRootPath);

console.log("Testing", srcRootPath, scriptDir);

let requireJsOptions = {
	baseUrl: `/${srcRootPath}/`,
	paths: {
		wc: `${scriptDir}/wc`,
		lib: `${scriptDir}/lib`,
		"lib/sprintf": `${scriptDir}/lib/sprintf.min`,
		translation: `resource/translation`,
		"intern/chai": `/${testRootPath}/resources/intern-chai`,
		"intern/resources": `/${testRootPath}/resources/`,
		intern: `/${testRootPath}/resources/intern-shim`,
		target: `/${targetDir}`
	},
	config: {
		"wc/loader/resource": {
			"resourceBaseUrl": `/${srcRootPath}/resource/`
		}
	}
};

/**
 * This is essentially a JS version of intern.json:
 * https://theintern.io/docs.html#Intern/4/docs/docs%2Fconfiguration.md/config-file
 *
 * The problem is that intern.json is static, we do not want to hard-code the directories (except in one place, the package.json).
 * Therefore this JS file provides the configuration in a way that can be manipulated.
 *
 * Another problem is that JSON has no comment syntax which rapidly becomes annoying when setting up arcane configuration.
 *
 * Note: most developer overrides can be achieved without hacking this file by setting config overrides in INTERN_ARGS
 * https://theintern.io/docs.html#Intern/4/docs/docs%2Fconfiguration.md/environment-variable
 */
let internConfig = {
	browser: {
		suites: [`${testRootPath}/*.test.js`],
		loader: {
			script: `${testRootPath}/resources/intern-loader.js`,
			options: requireJsOptions
		}
	},
	node: {
		suites: [path.join(dirs.test.target, "unit", "*.test.js")]
	},
	"configs": {
		"local": {
			"description": "Run tests on local system, automatically starting webdriver"
		},
		"local-headless": {
			"environments": [
				{
					"browserName": "firefox",
					"moz:firefoxOptions": {
						"args": ["-headless", "--window-size=1024,768"]
					}
				}
			]
		},
		"sauce": {
			"description": "Run tests on SauceLabs",
			"environments": [
				{ "browserName": "firefox" },
				{ "browserName": "chrome" }
			],
			"proxyPort": 9000,
			"proxyUrl": "http://localhost:9000/",
			"maxConcurrency": 5,
			"coverage": false,
			"tunnel": "saucelabs",
			"tunnelOptions": {
				"verbose": true
			},
			"capabilities": {
				"recordVideo": false,
				"recordScreenshots": false
			}
		},
		"sauce-edge": {
			"extends": ["sauce"],
			"environments": [
				{ "browserName": "MicrosoftEdge", "name": "wc-theme-tests" }
			]
		},
		"grid": {
			"description": "Run against a running selenium server",
			"tunnel": "null",
			"environments": [
				{ "browserName": "firefox" },
				{ "browserName": "chrome" }
			]
		}
	},
	"runnerClientReporter": {
		"writeHtml": false
	},
	"coverage": [
		`${srcRootPath}/${requireJsOptions.paths.wc}/**/*.js`,
		`${srcRootPath}/${requireJsOptions.paths.wc}/*.js`,
		"!test-classes/**",
		"!node_modules/**",
		"!lib/**"
	],
	"tunnelOptions": {
		"drivers": [
			{ "name": "firefox" },
			{ "name": "chrome" }
		]
	},
	"defaultTimeout": 240000
};

console.log("Intern coverage", internConfig.coverage);

if (internOverrides) {
	console.log("Got internOverrides from config", internOverrides);
	mixin(internOverrides, internConfig);
}

module.exports = {
	config: internConfig
};
