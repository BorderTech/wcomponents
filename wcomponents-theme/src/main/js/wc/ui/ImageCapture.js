define(["wc/has", "wc/dom/classList", "wc/dom/event", "wc/ui/prompt", "wc/config", "getUserMedia"], function(has, classList, event, prompt, wcconfig, getUserMedia) {
	/**
	 * Encapsulates the image capture functionality.
	 * This is not a truly reusable module, it is part of imageEdit.js but has been split out for ease of maintenance.
	 *
	 * TODO allow user to select video source or rely on platform to provide this?
	 * @param {ImageEdit} imageEdit The instance of ImageEdit this is really part of (yes we're bypassing requirejs going this way)
	 * @constructor
	 */
	function ImageCapture(imageEdit) {
		var VIDEO_CONTAINER = "wc_img_video_container",
			context,
			canvas,
			image,
			streaming,
			_stream,
			pos = 0,
			currentOptions,
			defaultOptions = {
				video: true,  // { facingMode: "user" },
				audio: false,
				extern: null,
				append: true,
				context: "",
				swffile: window.require.toUrl("lib/getusermedia-js/fallback/jscam_canvas_only.swf"),
				el: VIDEO_CONTAINER,
				mode: "callback",
				quality: 85,
				onCapture: onCapture,
				onSave: onSave
			};

		/*
		 * Flash event handler
		 */
		function onCapture() {
//				context = fbCanvas.getContext("2d");
//				context.clearRect(0, 0, currentOptions.width, currentOptions.height);
//				image = context.getImageData(0, 0, currentOptions.width, currentOptions.height);
			canvas = document.createElement("canvas");
			canvas.height = currentOptions.height;
			canvas.width = currentOptions.width;
			context = canvas.getContext("2d");
			context.clearRect(0, 0, currentOptions.width, currentOptions.height);
			image = context.getImageData(0, 0, currentOptions.width, currentOptions.height);
			currentOptions.save();
		}

		/*
		 * Flash event handler
		 */
		function onSave(data) {
			var col = data.split(";"),
				tmp = null,
				width = currentOptions.width,
				height = currentOptions.height;
			for (var i = 0; i < width; i++) {
				tmp = parseInt(col[i], 10);
				image.data[pos + 0] = (tmp >> 16) & 0xff;
				image.data[pos + 1] = (tmp >> 8) & 0xff;
				image.data[pos + 2] = tmp & 0xff;
				image.data[pos + 3] = 0xff;
				pos += 4;
			}

			if (pos >= 4 * width * height) {
				// fbCanvas.getContext("2d").putImageData(image, 0, 0);
				context.putImageData(image, 0, 0);
				imageEdit.renderImage(canvas.toDataURL());
				pos = 0;
			}
		}

		/*
		 * Wires up the "take photo" feature.
		 */
		this.snapshotControl = function (eventConfig, container) {
			var imageCapture = this,
				click = eventConfig.click,
				done = function(_video) {
					var video = _video || getVideo();
					classList.remove(container, "wc_showcam");
					imageCapture.stop();
					video.parentNode.removeChild(video);
				};
			activateCameraControl.call(this, eventConfig, container);
			click.snap = {
				func: function() {
					var video;
					if (currentOptions.context === "webrtc") {
						video = getVideo();
						if (video) {
							video.pause();
							videoToImage(video, null, function($event) {
								imageEdit.renderImage($event.target, done);
							});
						}
					} else if (currentOptions.context === "flash") {
						currentOptions.capture();
						classList.remove(container, "wc_showcam");
					} else {
						prompt.alert("No context was supplied to getSnapshot()");
					}
				}
			};
		};

		function activateCameraControl(eventConfig, container) {
			var imageCapture = this,
				click = eventConfig.click;
			click.camera = {
				func: function() {
					var fbCanvas = imageEdit.getCanvas();
					imageCapture.play({
						width: fbCanvas.getWidth(),
						height: fbCanvas.getHeight()
					});
					classList.add(container, "wc_showcam");
				}
			};
		}

		/*
		 * Entry point to gum.
		 * Uses native getUserMedia if possible and falls back to plugins if it must.
		 */
		function gumWithFallback(constraints, playCallback, errorCallback) {
			if (getUserMedia && arguments.length === 3) {
				getUserMedia(constraints, playCallback, errorCallback);
			}
		}

		function playCb(stream) {
			var video, vendorURL;
			_stream = stream;
			if (currentOptions.context === "webrtc") {

				video = currentOptions.videoEl;
				vendorURL = window.URL || window.webkitURL;
				video.src = vendorURL ? vendorURL.createObjectURL(stream) : stream;

				video.onerror = function () {
					stream.getVideoTracks()[0].stop();
					errCb(arguments[0]);
				};

				video.setAttribute("width", currentOptions.width);
				streaming = false;
				video.addEventListener("canplay", function() {
					var height;
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
		}

		function errCb(err) {
			console.log("An error occured! " + err);
//				dialogFrame.close();
		}

		/**
		 * Close the web camera video stream.
		 */
		this.stop = function(pause) {
			var i, track, tracks, video = getVideo();
			if (video && !pause) {
				video.src = "";
			}
			if (_stream) {
				if (_stream.getTracks) {
					tracks = _stream.getTracks();
					for (i = 0; i < tracks.length; i++) {
						track = tracks[i];
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
			var play = function() {
					gumWithFallback(currentOptions, playCb, errCb);
				},
				globalConf = wcconfig.get("wc/ui/imageEdit", {
					options: {}
				});
			currentOptions = Object.assign({}, defaultOptions, globalConf.options, options);
			currentOptions.width *= 1;
			currentOptions.height *= 1;
			window.webcam = currentOptions;  // Needed for flash fallback
			if (has("rtc-gum")) {
				play();
			} else if (has("flash")) {
				if (currentOptions.swffile === defaultOptions.swffile && (currentOptions.width !== 320 || currentOptions.height !== 240)) {
					/*
					 * The default swffile can only support 320 x 240.
					 * Compile new swf files at different resolutions if you need them: https://github.com/infusion/jQuery-webcam
					 * You can then change the swffile location in the options using wc/config
					 */
					console.warn("The default flash fallback only supports 320 x 240");
					currentOptions.width = 320;
					currentOptions.height = 240;
				}
				play();
			} else {
				console.error("Browser does not support web-rtc or flash. It should not be possible to get here.");
			}
		};

		function videoToDataUrl(video, scale) {
			var scaleFactor = scale || 1,
				onCanvas = document.createElement("canvas");
			onCanvas.width = video.videoWidth * scaleFactor;
			onCanvas.height = video.videoHeight * scaleFactor;
			onCanvas.getContext("2d").drawImage(video, 0, 0, onCanvas.width, onCanvas.height);
			return onCanvas.toDataURL();
		}

		function videoToImage(video, scale, onload) {
			var dataUrl = videoToDataUrl(video, scale),
				img = new Image();
			if (onload) {
				event.add(img, "load", onload);
			}
			img.src = dataUrl;
			return img;
		}

		/**
		 * Gets the video element.
		 * @returns {Element} The video element.
		 */
		function getVideo() {
			var container = document.getElementById(VIDEO_CONTAINER);
			if (!container) {
				return null;
			}
			return container.querySelector("video");
		}
	}

	return ImageCapture;
});
