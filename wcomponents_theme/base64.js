/*
 * This is a helper script to base64 encode image urls in CSS files in a give directory.
 * Used by build-css.xml
 *
 * It should be called with three args as shown below.
 *
 * @author Rick Brown
 */
var fs = require("fs"),
	path = require("path"),
	b64img = require("css-b64-images"),
	argCount = process.argv.length;


if (argCount > 3) {
	var done = {},
		webdir = process.argv[argCount - 4],
		excludes = process.argv[argCount - 3],  // comma separated list of filename patterns to exclude
		indir = process.argv[argCount - 2],
		outdir = process.argv[argCount -1];
	excludes = excludes.split(",");
	console.log("webdir:", webdir, "excludes:", excludes, "indir:", indir, "outdir:", outdir);
	fs.exists(indir, function(exists) {
		if (exists) {
			fs.exists(outdir, function(exists) {
				if (exists) {
					fs.readdir(indir, function (err, list) {
						list.forEach(function (file) {
							var isExcluded = function(ext) {
									var fileExt = path.extname(file),
										skip = fileExt !== ".css" || file.indexOf(ext) >= 0;
									return skip;
								},
								infile = path.join(indir, file),
								outfile = path.join(outdir, file);
							if (!done.hasOwnProperty(file)) {
								done[file] = true;
								if (!excludes.some(isExcluded)) {
									b64img.fromFile(infile, webdir, function(err, css){
										if(err) {
											for (var i = 0; i < err.length; i++) {
												var ex = err[i];
												if (ex.path && ex.path.indexOf("webfont") >= 0 && ex.code === "ENOENT") {
													console.error("Did not embed webfont", ex.path);
												}
												else {
													console.error(ex);
												}
											}
										}
										fs.writeFileSync(outfile, css);
										console.log("Completed embedding base64 images in", file);
									});
								}
								else {
									console.log("Skipping base64 on", file);
								}
							}

						});
					});
				}
				else {
					console.error("output directory does not exist", outdir);
				}
			});
		}
		else {
			console.error("input directory does not exist", indir);
		}
	});
}
else {
	console.log("Usage: base64 web-dir \"comma,separated,excludes\" source-dir dest-dir");
}
