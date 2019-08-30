/* eslint-env node, es6  */
/**
 * This is used to transform a java properties file to its JSON equivalent.
 */
const properties = require ("properties");
const fs = require("fs");

main(process.argv);

function main(argv) {
	if (argv && argv.length > 2 && argv[2] && argv[3]) {
		readPropertiesFile(argv[2], argv[3]);
	} else {
		console.log("Usage: node propertiesToJson.js /path/to/file.properties /path/to/destination.json");
		if (argv) {
			console.log("Got ", argv.join());
		}
	}
}

function readPropertiesFile(propertiesFile, jsonFile) {
	properties.parse (propertiesFile, { path: true }, function (error, obj) {
		var jsonString;
		if (error) return console.error (error);
		jsonString = JSON.stringify(obj, null, 1);
		fs.writeFile(jsonFile, jsonString, function(err) {
			if (err) {
				return console.log(err);
			}
			console.log("Wrote file ", jsonFile);
		});
	});
}
