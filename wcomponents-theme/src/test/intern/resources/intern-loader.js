/**
 * This is a custom loader for intern which provides AMD support using requirejs.
 */
intern.registerLoader(function (options) {
	function initLoader(requirejs) {

		/**
		 * Configure requireJS
		 * Options are from intern.json config
		 */
		requirejs.config(options);

		/**
		 * This is the function intern will actually call to load modules
		 * @param {String[]} modules The dependencies to load.
		 * @returns {Promise} resolved when done.
		 */
		return function (modules) {
			var testFileRe = /^target\/test-classes\/.+\/intern\/(.+).js$/;
			modules = modules.map(function(nextModule) {
				if (testFileRe.test(nextModule)) {
					/*
					 * We want requirejs to treat this as a module not a URL.
					 * This happens when using a glob pattern in intern config.suites,
					 * the glob matcher asks the loader to load URIs not modules.
					 * We want modules.
					 */
					return nextModule.replace(/\.js$/, "");
				}
				return nextModule;
			});
			return new Promise(function (resolve, reject) {
				requirejs(modules, resolve, reject);
			});
		};
	}

	if (typeof window !== "undefined") {
		return intern.loadScript("node_modules/requirejs/require.js").then(function () {
			return initLoader(window.requirejs);
		});
	}
	return initLoader(require("requirejs"));
});
