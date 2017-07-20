define(["wc/isNumeric", "wc/i18n/i18n", "ccv", "face"], function(isNumeric, i18n, ccv, cascade) {
	var instance = {
		_interval: 5,
		_minNeighbours: 1,
		_confidenceThreshold: 0,
		track: trackFace,
		getValidator: getValidator,
		validationIgnorable: true
	}; //
//		constraints = {  // constraints would ideally allow larger images on more powerful devices (or should we resize image?)
//			px: 640 * 480,
//			len: 99999
//		};

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
			} else if (isNumeric(config.face) && config.face > 0) {
				faceCount = config.face;
			}
			if (faceCount) {
				return instance.track(element).then(function(arr) {
					var confidentFaces, error = {
							ignorable: instance.validationIgnorable
						};
					if (arr) {
						confidentFaces = arr.filter(isFace);
						if (confidentFaces.length < config.face) {
							error.message = i18n.get("imgedit_message_val_minface");
							if (instance.validationIgnorable) {
								error.message += "\n" + i18n.get("validation_common_ignore");
							}
							return error;
						} else if (confidentFaces.length > config.face) {
							error.message = i18n.get("imgedit_message_val_maxface");
							if (instance.validationIgnorable) {
								error.message += "\n" + i18n.get("validation_common_ignore");
							}
							return error;
						}
					}
				});
			}
			return Promise.resolve(null);
		};
	}

	function isFace(faceDto) {
		return faceDto && faceDto.confidence > instance._confidenceThreshold;
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
					interval: instance._interval,
					min_neighbors: instance._minNeighbours
				});
				resolve(faces);
			} catch (ex) {
				reject(ex);
			}
		});
		return result;
	}

//	function getSize(obj) {
//		if (obj) {
//			return obj.width * obj.height;
//		}
//		return 0;
//	}

	return instance;
});
