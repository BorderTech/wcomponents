define(function() {
	/**
	 * Recursive mixin function.
	 * Properties from source will take precendence over those on target.
	 * Object properties will be recurisvely "mixed in".
	 * @param {object} source The object whose properties we want to steal.
	 * @param {object} [target] The object to write the source properties to.
	 * @param {boolean} [shallow] If `true` then do not recurse the mixin for object values.
	 *
	 * @returns {Object} The target object or a new object if no target provided.
	 */
	function mixin(source, target, shallow) {
		var result = target || {};
		if (source) {
			for (var prop in source) {
				if (source.hasOwnProperty(prop)) {
					if (source[prop] && source[prop].constructor === Object) {
						if (!shallow && (!result[prop] || result[prop].constructor === Object)) {
							result[prop] = mixin(source[prop], result[prop]);
						} else {
							result[prop] = source[prop];
						}
					} else {
						result[prop] = source[prop];
					}
				}
			}
		}
		return result;
	}

	return mixin;
});
