/* eslint-env node, es6  */
const pkgJson = require("./package.json");

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
	suites: [`${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/*.test.js`],
	"node": {
		"loader": {
			"script": `${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/resources/intern-loader.js`,
			"options": {
				"baseUrl": `/${pkgJson.directories.target}/classes/theme/${pkgJson.name}/`,
				"packages": [
					{
						"name": "intern-requirejs-example",
						"location": "."
					}
				]
			}
		}
	},
	browser: {
		loader: {
			script: `${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/resources/intern-loader.js`,
			options: {
				baseUrl: `/${pkgJson.directories.target}/classes/theme/${pkgJson.name}/`,
				urlArgs: `build=${pkgJson.version}&theme=${pkgJson.name}`,
				paths: {
					wc: `scripts_debug/wc`,
					lib: `scripts_debug/lib`,
					dojo: `scripts_debug/lib/dojo`,
					fabric: `scripts_debug/lib/fabric`,
					ccv: `scripts_debug/lib/ccv`,
					face: `scripts_debug/lib/face`,
					sprintf: `scripts_debug/lib/sprintf`,
					Promise: `scripts_debug/lib/Promise`,
					compat: `scripts_debug/wc/compat`,
					translation: `resource/translation`,
					"intern/chai": `/${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/resources/intern-chai`,
					"intern/resources": `/${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/resources/`,
					intern: `/${pkgJson.directories.target}/test-classes/${pkgJson.name}/intern/resources/intern-shim`,
					target: `/${pkgJson.directories.target}`
				},
				config: {
					"wc/i18n/i18n": {
						"options": {
							"backend": {
								"cachebuster": `build=${pkgJson.version}&theme=${pkgJson.name}`
							}
						}
					},
					"wc/loader/resource": {
						"resourceBaseUrl": `/${pkgJson.directories.target}/classes/theme/${pkgJson.name}/resource/`,
						"cachebuster": `build=${pkgJson.version}&theme=${pkgJson.name}`
					}
				}
			}
		}
	},
	"configs": {
		"local": {
			"description": "Run tests on local system, automatically starting chromedriver"
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
				{ "browserName": "internet explorer", "version": "11.0", "platform": "Windows 10" },
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
		"test-classes/**",
		"!node_modules/**",
		"!lib/**"
	],
	"tunnelOptions": {
		"drivers": [
			{ "name": "firefox" },
			{ "name": "internet explorer" },
			{ "name": "chrome", "version": "76.0.3809.12" }
		]
	},
	"defaultTimeout": 240000
};

module.exports = {
	config: internConfig
};
