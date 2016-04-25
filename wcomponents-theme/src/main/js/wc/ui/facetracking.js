define(["wc/loader/prefetch", "tracking"], function(prefetch, tracking) {
	var constraints = {  // constraints would ideally allow larger images on more powerful devices (or should we resize image?)
			px: 640 * 480,
			len: 99999
		},
		FACE_LIB = "lib/tracking/build/data/face-min";

	prefetch.jsModule(FACE_LIB);

	function findLargestFace(arr) {
		var i, next, size, result = {
				rect: arr[0],
				size: getSize(arr[0])
			};
		for (i = 1; i < arr.length; i++) {
			next = arr[i];
			size = getSize(next);
			if (size > result.size) {
				result = {
					rect: next,
					size: size
				};
			}
		}
		return result.rect;
	}

	function getSize(obj) {
		if (obj) {
			return obj.width * obj.height;
		}
		return 0;
	}

	/**
	 * Finds faces in an image.
	 * @param {Element} obj The html IMG element in which to find faces.
	 * @param {boolean} all If true resolves with all faces, otherwise it picks the largest face.
	 * @returns {Promise}
	 */
	function trackFace(obj, all) {
		var result = new Promise(function(resolve, reject) {
			try {
				// the require at this point is to avoid race conditions with the main tracking library and the "face" plugin
				if (!obj) {
					reject("Must provide an image");
				}
				else if (getSize(obj) > constraints.px || obj.src.length > constraints.len) {
					reject("Image is too large");
				}
				else {
					require([FACE_LIB], function() {
						var tracker = new tracking.ObjectTracker("face");
						tracker.setInitialScale(1);
						tracker.setStepSize(2);
						tracker.setEdgesDensity(0.1);
						tracker.once("track", function(event) {
							if (all) {
								resolve(event.data);
							}
							else {
								resolve(findLargestFace(event.data));
							}
						});
						tracking.track(obj, tracker);
					});
				}
			}
			catch (ex) {
				reject(ex);
			}

		});
		return result;
	}

	return {
		track: trackFace
	};
});
