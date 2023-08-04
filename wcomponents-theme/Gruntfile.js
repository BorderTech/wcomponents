/* eslint-env node, es6  */
/*
 * This Gruntfile is primarily responsible for driving the unit tests.
 * YOU DO NOT HAVE TO INSTALL GRUNT GLOBALLY TO USE IT!
 *
 * Either use the package scripts, e.g. `yarn run test`
 *
 * OR with npm:
 *
 * Use npx, e.g.`npx grunt test`
 */
let internConfig = require("./scripts/intern").config;
const path = require("path");
const { dirs } = require("./scripts/build-util");
const defaultInternArgs = "environments='{\"browserName\":\"firefox\"}'";
const coverageDir = path.join(dirs.project.build, "coverage");

module.exports = function (grunt) {
	var testSrc = (grunt.option("filename") || "**");
	grunt.initConfig({
		clean: {
			test: [dirs.test.target]
		},
		copy: {
			test: {
				cwd: dirs.test.src,
				expand: true,
				src: testSrc,
				dest: dirs.test.target
			}
		},
		intern: {
			node: {
				options: {
					suites: path.join(dirs.test.target, "unit/*.js"),
					reporters:  ["runner", {
						"name": "lcov",
						"options": {
							"directory": coverageDir,
							"filename": "node.lcov"
						}
					}]
				}
			},
			local: {
				options: {
					config: "@local",
					reporters:  ["runner", {
						"name": "lcov",
						"options": {
							"directory": coverageDir,
							"filename": "local.lcov"
						}
					}]
				}
			},
			sauce: {
				options: {
					config: "@sauce",
					reporters: "runner"
				}
			},
			grid: {
				options: {
					config: "@grid",
					reporters: "runner"
				}
			},
			serve: {
				options: {
					serveOnly: true
				}
			}
		}
	});
	logIt(`Coverage dir ${coverageDir}`);
	grunt.loadNpmTasks("intern");
	grunt.loadNpmTasks("grunt-contrib-clean");
	grunt.loadNpmTasks("grunt-contrib-copy");

	/**
	 * This is a magically special test runner task which allows you to invoke a specic test environment
	 * or have one picked for you.
	 *
	 * Simply run the "test" target and it will do the "guessing" for you: `npx grunt test`
	 * Or you can be specific and tell it what you want: `npx grunt test:local` or `npx grunt test:sauce`.
	 */
	grunt.registerTask("test", "Configures and invokes intern tests", function () {
		let internTask = "intern";
		/*
		 * This (writing intern json to file) should not be necessary, we can pass config directly like so:
		 options: Object.assign({
		 config: "@sauce",
		 reporters: "runner"
		 }, internConfig)
		 * The problem is that when running "serveOnly" intern ignores such config and only reads intern.json
		 * That's probably an intern bug but this works around it.
		 */
		grunt.file.write("intern.json", JSON.stringify(internConfig, null, "\t"));
		if (arguments.length) {
			internTask += ":" + arguments[0];
		} else if (process.env.SAUCE_USERNAME && process.env.SAUCE_ACCESS_KEY) {
			logIt("Found SAUCE_USERNAME & SAUCE_ACCESS_KEY");
			internTask += ":sauce";
		} else {
			logIt("Running tests locally because SAUCE_USERNAME & SAUCE_ACCESS_KEY not set");
			internTask += ":local";
			if (!process.env.MOZ_HEADLESS) {
				// in "guess" mode we run headless
				process.env.MOZ_HEADLESS = 1;
			}
		}

		grunt.task.run("clean:test");
		grunt.task.run("copy:test");

		if (!/:serve/.test(internTask)) {
			// If not running a standalone selenium server then INTERN_ARGS should be set
			let internArgs = process.env.INTERN_ARGS;
			if (internArgs) {
				logIt("INTERN_ARGS: " + process.env.INTERN_ARGS);
			} else if (internConfig.environments) {
				logIt("INTERN_ARGS not set, using environments from config");
			} else {
				logIt("INTERN_ARGS not set, using default: " + defaultInternArgs);
				logIt("https://github.com/theintern/intern/blob/master/docs/configuration.md#environment-variable");
				process.env.INTERN_ARGS = defaultInternArgs;
			}
		} else {
			logIt("To run tests visit in browser: http://localhost:9000/__intern/");
			logIt("To run a single suite: http://localhost:9000/__intern/?suites=target/test-classes/wcomponents-theme/intern/wc.dom.WidgetDescriptor.test.js");
		}
		grunt.task.run(internTask);
	});

	/**
	 * This is a very important grunt logging utility.
	 * @param {string} msg Will be output to build log.
	 */
	function logIt(msg) {
		let colors = ["blue", "cyan", "green", "magenta", "red", "yellow", "rainbow"];  // "white", "black", "grey",
		let color = colors[rnd(0, colors.length - 1)];
		grunt.log.writeln(msg[color], String.fromCodePoint(rnd(0x1F400, 0x1F43C)));
	}
};
/**
 * Helper for very important grunt logging utility.
 * @param min Minimum random.
 * @param max Maximum random.
 * @returns A random number in the range, inclusive.
 */
function rnd(min, max) {
	let minInt = Math.ceil(min);
	let maxInt = Math.floor(max);
	return Math.floor(Math.random() * (maxInt - minInt + 1)) + minInt;
}
