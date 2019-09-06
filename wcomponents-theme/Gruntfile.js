/* eslint-env node, es6  */
let internConfig = require("./intern").config;

module.exports = function (grunt) {

	grunt.initConfig({
		intern: {
			local: {
				options: {
					config: "@local",
					reporters: "runner"
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

	grunt.loadNpmTasks("intern");

	grunt.registerTask("test", "Configures and invokes intern tests", function() {
		let internTask = "intern";
		/*
		 * This should not be necessary, we can pass config directly like so:
			options: Object.assign({
				config: "@sauce",
				reporters: "runner"
			}, internConfig)
		 * The problem is that when running "serveOnly" intern ignores such config and only reads intern.json
		 * That's probably an intern bug but this works around it.
		 */
		grunt.file.write("intern.json", JSON.stringify(internConfig));

		if (arguments.length) {
			internTask += ":" + arguments[0];
		}
		grunt.task.run(internTask);
	});
};
