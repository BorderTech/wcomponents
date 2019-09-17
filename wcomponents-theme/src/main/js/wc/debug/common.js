require(["wc/debug/hotReloadClient",
	"wc/debug/consoleColor",
	"wc/debug/indicator",
	"wc/debug/i18n",
	"wc/debug/label",
	"wc/debug/heading"
/* , "wc/debug/a11y" */], function(hotReloadClient) {
	console.log("Loaded debug modules");
	hotReloadClient.getConnection();
});
