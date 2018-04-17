/**
 * Passing module configuration around is surprisingly complex at the moment.
 * Do you use a global object? A loader specific mechanism like RequireJS configuration?
 * The aim of this module is to encapsulate the underlying mechanism and present a simple configuration API to other modules.
 */
define(["wc/mixin", "module"], function(mixin, module) {
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
	 */
	function Config() {
		var configObject = {};

		/**
		 * Register a configuration object for a given id or completely replace the entire registry with the given object.
		 *
		 * @param {Object} config The configuration object to set.
		 * @param {string} [id] The ID against which to register this configuration. If falsey will replace the entire registery with the
		 *    configuration (did this ever seem like a good idea?).
		 */
		this.set = function(config, id) {
			var alreadySet, newConfig;
			if (id) {
				if (!config) {
					// reset any existing config to null
					if (configObject[id]) {
						delete configObject[id];
					}
					return;
				}
				alreadySet = configObject[id];
				if (alreadySet) {
					newConfig = mixin(alreadySet);
					configObject[id] = mixin(config, newConfig, true);
				} else {
					configObject[id] = config;
				}
			} else {
				configObject = config || configObject;
			}
		};

		/**
		 * Get the config registered for this id.
		 * @param {string} id Specify which configuration you want to get.
		 * @param {Object} [defaults] Optionally provide a default configuration, the result will contain the result of the defaults overriden by any
		 *    registered configuration.
		 *    Note that object properties will be recursively mixed in, anything else (arrays, strings, numbers etc) will be overriden.
		 *    With this argument provided the result will never be null.
		 * @returns {Object} A configuration object.
		 */
		this.get = function(id, defaults) {
			var defaultConfig, result = configObject[id];
			if (defaults) {
				defaultConfig = mixin(defaults);  // make a copy of defaults;
				result = mixin(result, defaultConfig);  // override defaults with explicit settings (mixin can handle result being null)
			}
			return result;
		};
	}
	return instance;
});
