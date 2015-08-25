/*
 * This can be used to run our ESLint rules on a single file.
 * The indea is that you could configure a custom command in your IDE to report issues on the current file without having to
 * do a complete build.
 *
 * For example in Netbeans (on Mac):
 * 1. Install NodeJS Plugin (it's in the default available plugins) http://plugins.netbeans.org/plugin/36653/nodejs
 * 2. Netbeans > Preferences > Node.js > "Default run command"
 *    cd ${workingdir};
 *    /usr/local/bin/node /path/to/lintfile.js ${selectedfile};
 * 3. Now you can right-click any file and "Run with Node.js" to lint it.
 */
var CLIEngine = require("eslint").CLIEngine;

var cli = new CLIEngine({
	useEslintrc: true,
	ignore: true
});

var lintTarget = process.argv[process.argv.length - 1];
if (lintTarget && /\.js$/.test(lintTarget) && !/lintfile\.js$/.test(lintTarget)) {
	console.log("Attempting to lint ", lintTarget);
	var report = cli.executeOnFiles([lintTarget]);
	var formatter = cli.getFormatter();
	var prettyReport = formatter(report.results);
	if (prettyReport) {
		console.log(prettyReport);
	}
	else {
		console.log("Nothing to report besides the fact that you are awesome!");
	}
}
else {
	console.log("USAGE IS: node lintfile.js someJsFile.js");
}
