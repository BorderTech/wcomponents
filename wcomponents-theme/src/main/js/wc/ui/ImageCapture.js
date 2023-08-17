define(["wc/dom/event", "wc/ui/prompt", "wc/config"], function(event, prompt, wcconfig) {
	/**
	 * Encapsulates the image capture functionality.
	 * This is not a truly reusable module, it is part of imageEdit.js but has been split out for ease of maintenance.
	 *
	 * TODO allow user to select video source or rely on platform to provide this?
	 * @param {ImageEdit} imageEdit The instance of ImageEdit this is really part of (yes we're bypassing requirejs going this way)
	 * @constructor
	 */
	function ImageCapture(imageEdit) {
		const VIDEO_CONTAINER = "wc_img_video_container",
			defaultOptions = {
				video: true,  // { facingMode: "user" },
				audio: false,
				extern: null,
				append: true,
				context: "",
				el: VIDEO_CONTAINER,
				mode: "callback",
				quality: 85
			};

		let streaming,
			_stream,
			currentOptions;


		/*
		 * Wires up the "take photo" feature.
		 */
		this.snapshotControl = function (eventConfig, container) {
			const imageCapture = this,
				click = eventConfig.click,
				done = function(_video) {
					const video = _video || getVideo();
					container.classList.remove("wc_showcam");
					imageCapture.stop();
					video.parentNode.removeChild(video);
				};
			activateCameraControl.call(this, eventConfig, container);
			click.snap = {
				func: function() {
					const video = getVideo();
					if (video) {
						video.pause();
						videoToImage(video, null, function($event) {
							imageEdit.renderImage($event.target, done);
						});
					}
				}
			};
		};

		function activateCameraControl(eventConfig, container) {
			const imageCapture = this,
				click = eventConfig.click;
			click.camera = {
				func: function() {
					var fbCanvas = imageEdit.getCanvas();
					imageCapture.play({
						width: fbCanvas.getWidth(),
						height: fbCanvas.getHeight()
					});
					container.classList.add("wc_showcam");
				}
			};
		}

		/*
		 * Entry point to gum.
		 * Uses native getUserMedia if possible and falls back to plugins if it must.
		 */
		function gumWithFallback(constraints, playCallback, errorCallback) {
			if (window.navigator.mediaDevices && window.navigator.mediaDevices.getUserMedia) {
				window.navigator.mediaDevices.getUserMedia(constraints).then(playCallback).catch(errorCallback);
			} else if (window.getUserMedia && arguments.length === 3) {
				window.getUserMedia(constraints, playCallback, errorCallback);
			}
		}

		/**
		 *
		 * @param {MediaStream} stream
		 */
		function playCb(stream) {
			_stream = stream;
			const video = getVideo(true);
			try {
				const vendorURL = window.URL || window.webkitURL;
				video.src = vendorURL ? vendorURL.createObjectURL(stream) : stream;
			} catch (error) {
				video.srcObject = stream;
			}

			video.onerror = function () {
				stream.getVideoTracks()[0].stop();
				errCb(arguments[0]);
			};

			video.setAttribute("width", currentOptions.width);
			streaming = false;
			video.addEventListener("canplay", function() {
				let height;
				if (!streaming) {
					height = video.videoHeight / (video.videoWidth / currentOptions.width);

					// Firefox currently has a bug where the height can't be read from the video
					if (isNaN(height)) {
						height = currentOptions.width / (currentOptions.width / currentOptions.height);
					}

					video.setAttribute("width", currentOptions.width);
					video.setAttribute("height", height);

					streaming = true;
				}
			}, false);
		}

		function errCb(err) {
			console.log("An error occured! " + err);
			// dialogFrame.close();
		}

		/**
		 * Close the web camera video stream.
		 */
		this.stop = function(pause) {
			const video = getVideo();
			if (video && !pause) {
				video.src = "";
			}
			if (_stream) {
				if (_stream.getTracks) {
					const tracks = _stream.getTracks();
					for (let i = 0; i < tracks.length; i++) {
						const track = tracks[i];
						if (track.stop) {
							track.stop();
						}
					}
				} else if (_stream.stop) {
					_stream.stop();
				}
				if (!pause) {
					_stream = null;
				}
			}
		};

		/**
		 * Start the web camera video stream.
		 * Any options not provided will be set to default values.
		 * Default values may be globally overridden by "wc/config" options.
		 * Option values provided to this function call override default and global option values.
		 *
		 * In other words:
		 * 1. Options passed in argument trump all.
		 * 2. Options set in wc/config trump defaults.
		 * 3. Option defaults will be used if not set in any other way.
		 *
		 * To understand the options take a look at: https://github.com/addyosmani/getUserMedia.js and/or https://github.com/infusion/jQuery-webcam
		 */
		this.play = function(options) {
			const globalConf = wcconfig.get("wc/ui/imageEdit", {
				options: {}
			});
			currentOptions = Object.assign({}, defaultOptions, globalConf.options, options);
			currentOptions.width *= 1;
			currentOptions.height *= 1;
			gumWithFallback(currentOptions, playCb, errCb);
		};

		function videoToDataUrl(video, scale) {
			const scaleFactor = scale || 1,
				onCanvas = document.createElement("canvas");
			onCanvas.width = video.videoWidth * scaleFactor;
			onCanvas.height = video.videoHeight * scaleFactor;
			onCanvas.getContext("2d").drawImage(video, 0, 0, onCanvas.width, onCanvas.height);
			return onCanvas.toDataURL();
		}

		function videoToImage(video, scale, onload) {
			const dataUrl = videoToDataUrl(video, scale),
				img = new Image();
			if (onload) {
				event.add(img, "load", onload);
			}
			img.src = dataUrl;
			return img;
		}

		/**
		 * Gets the video element.
		 * @param {boolean} [createNew] If true, create the element if it doesn't exist.
		 * @returns {HTMLVideoElement} The video element.
		 */
		function getVideo(createNew) {
			const container = document.getElementById(VIDEO_CONTAINER);
			if (!container) {
				return null;
			}
			let result = container.querySelector("video");
			if (createNew && !result) {
				result = document.createElement('video');
				const offsetWidth = parseInt(container.offsetWidth, 10);
				const offsetHeight = parseInt(container.offsetHeight, 10);

				if (currentOptions.width < offsetWidth && currentOptions.height < offsetHeight) {
					currentOptions.width = offsetWidth;
					currentOptions.height = offsetHeight;
				}
				result.width = currentOptions.width;
				result.height = currentOptions.height;
				result.autoplay = true;
				container.appendChild(result);
			}
			return result;
		}
	}

	return ImageCapture;
});
