/**
 * Passing module configuration around is surprisingly complex at the moment.
 * Do you use a global object? A loader specific mechanism like RequireJS configuration?
 * The aim of this module is to encapsulate the underlying mechanism and present a simple configuration API to other modules.
 */
define(["module"], function(module) {
	var instance = new Config();
	initialise();

	function initialise() {
		var config;
		if (module && module.config) {
			// The loader is RequireJS, module.config should be legit
			config = module.config();
			if (config && config.dehydrated) {
				config = JSON.parse(config.dehydrated);
				instance.set(config);
			}
		}
	}

	/**
	 * @constructor
	 * @returns {undefined}
	 */
	function Config() {
		var configObject = {};

		this.set = function(config, id) {
			if (id) {
				configObject[id] = config || configObject[id];
			}
			else {
				configObject = config || configObject;
			}
		};

		this.get = function(id) {
			return configObject[id];
		};
	}
	return instance;
});
