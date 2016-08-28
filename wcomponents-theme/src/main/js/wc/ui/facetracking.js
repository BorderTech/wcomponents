define(["wc/isNumeric", "wc/i18n/i18n", "ccv", "face"], function(isNumeric, i18n, ccv, cascade) {
	var instance = {
			track: trackFace,
			getValidator: getValidator
		},
		constraints = {  // constraints would ideally allow larger images on more powerful devices (or should we resize image?)
			px: 640 * 480,
			len: 99999
		};

	function getValidator(config) {
		/**
		 * Validator that resolves with an error object if one was encountered, otherwise null
		 * @param {Element} element the html canvas element to validate
		 * @returns {Promise} resolved with null if all is good
		 */
		return function (element) {
			// maybe this needs "minfaces" and "maxfaces" but realistically i think the only "face" use case will be 'one face, no more, no less'
			var faceCount;
			if (config.face === true) {
				faceCount = 1;
			}
			else if (isNumeric(config.face) && config.face > 0) {
				faceCount = config.face;
			}
			if (config.face) {
				return instance.track(element).then(function(arr) {
					var error = {};
					if (arr) {
						if (arr.length < config.face) {
							error.message = i18n.get("imgedit_message_val_minface");
							return error;
						}
						else if (arr.length > config.face) {
							error.message = i18n.get("imgedit_message_val_maxface");
							return error;
						}
					}
				});
			}
			return Promise.resolve(null);
		};
	}

	/**
	 *
	 * @param {Element} obj The html canvas element in which to find faces
	 * @returns {Promise}
	 */
	function trackFace(obj) {
		var result = new Promise(function(resolve, reject) {
			try {
				var faces = ccv.detect_objects({
					canvas: obj,
					cascade: cascade,
					interval: 5,
					min_neighbors: 1
				});
				resolve(faces);
			}
			catch (ex) {
				reject(ex);
			}
		});
		return result;
	}

	function getSize(obj) {
		if (obj) {
			return obj.width * obj.height;
		}
		return 0;
	}

	return instance;
});
