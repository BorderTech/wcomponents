define(["wc/loader/prefetch", "tracking"], function(prefetch, tracking) {
	var FACE_LIB = "lib/tracking/build/data/face-min";

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

	function getSize(rect) {
		if (rect) {
			return rect.width * rect.height;
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
				require([FACE_LIB], function() {
					var tracker = new tracking.ObjectTracker("face");

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
