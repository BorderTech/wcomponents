define(["wc/has", "wc/dom/event", "wc/dom/uid", "wc/dom/classList", "wc/timers",
	"wc/loader/resource", "wc/i18n/i18n", "fabric", "Mustache", "wc/ui/dialogFrame"],
function(has, event, uid, classList, timers, loader, i18n, fabric, Mustache, dialogFrame) {
	var imageEdit = new ImageEdit();

	/**
	 * This provides a mechanism to allow the user to edit images during the upload process.
	 * It may also be used to edit static images after they have been uploaded as long as a file uploader is configured to take the edited images.
	 *
	 * @constructor
	 */
	function ImageEdit() {

		var inited,
			TEMPLATE_NAME = "imageEdit.xml",
			imageCapture = new ImageCapture(),
			overlayUrl,
			defaults = {
				width: 300,
				height: 400
			},
			stateStack = [],
			registeredIds = {},
			fbCanvas, fbImage, frameConfig;


		function getDialogFrameConfig() {
			return {
				id: "wc_img_editor",
				modal: true,
				resizable: true,
				title: i18n.get("${wc.ui.imageEdit.title}")
			};
		}

		/**
		 * Registers a configuration object against a unique ID to specificy variables such as overlay image URL, width, height etc.
		 *
		 * @param {Object[]} arr Configuration objects.
		 */
		this.register = function(arr) {
			var i, next;
			for (i = 0; i < arr.length; i++) {
				next = arr[i];
				registeredIds[next.id] = next;
			}
			if (!inited) {
				inited = true;
				require(["wc/dom/initialise", "wc/ui/multiFileUploader"], function(initialise, multiFileUploader) {
					initialise.addCallback(function(element) {
						event.add(element, "click", clickEvent);
					});

					/**
					 * Listens for edit requests on static images.
					 * @param {Event} $event A click event.
					 */
					function clickEvent($event) {
						var img, uploader, file,
							element = $event.target,
							id = element.getAttribute("data-selector");
						if (id && element.localName === "button") {
							uploader = document.getElementById(id);
							if (uploader) {
								img = document.getElementById(element.getAttribute("data-img"));
								if (img) {
									file = imgToFile(img);
									multiFileUploader.upload(uploader, [file]);
								}
								else {
									imageEdit.editFiles({
										id: id,
										name: element.getAttribute("data-editor")
									}).then(function(files) {
										multiFileUploader.upload(uploader, files, true);
									}, function(message) {
										if (message) {
											alert(message);
										}
									});
								}
							}
						}
					}
				});
				loader.preload(TEMPLATE_NAME);
			}
		};

		/**
		 * Retrieve a configuration object.
		 * @param {Object} obj Get the configuration registered for the "id" or "name" property of this object (in that order).
		 * @returns {Object} configuration
		 */
		this.getConfig = function(obj) {
			var editorId, result = registeredIds[obj.id] || registeredIds[obj.name];
			if (!result) {
				if ("getAttribute" in obj) {
					editorId = obj.getAttribute("data-editor");
				}
				else {
					editorId = obj.editorId;
				}
				if (editorId) {
					result = registeredIds[editorId];
				}
			}
			return result || Object.create(defaults);
		};

		/**
		 * Prompt the user to edit the image files.
		 *
		 * If other (non-image) files are present they will be passed through unchanged.
		 * If more than one image file is present the editor will be displayed for each image file one after the other.
		 * If the edit operation is aborted at any point for any file then the entire edit process is aborted (the promise will reject).
		 *
		 *
		 * @param {Object} obj An object with a "files" property that references an array of File blobs to be edited and a registered "id" or "name".
		 * @returns {Promise} resolved with an array of File blobs that have potentially been edited by the user.
		 */
		this.editFiles = function(obj) {
			var config = imageEdit.getConfig(obj),
				promise = new Promise(function(resolve, reject) {
					var file, idx = 0, result = [], files = obj.files;
					try {
						if (files) {
							if (has("dom-canvas")) {
								editNextFile();
							}
							else {
								resolve(files);
							}
						}
						else if (config.camera) {
							if (has("rtc-gum")) {
								promise = editFile(config, null).then(saveEditedFile, reject);
							}
							else {
								reject(i18n.get("${wc.ui.imageEdit.message.nortcgum}"));
							}
						}
					}
					catch (ex) {
						reject();
					}

					/*
					 * Once the user has commited their changes buffer the result and see if there is another file queued for editing.
					 */
					function saveEditedFile(file) {
						result.push(file);
						editNextFile();
					}

					/*
					 * Prompt the user to edit the next file in the queue.
					 */
					function editNextFile() {
						if (files && idx < files.length) {
							file = files[idx++];
							if (file.type.indexOf("image/") === 0) {
								promise = editFile(config, file).then(saveEditedFile, reject);
							}
							else {
								saveEditedFile(file);
							}
						}
						else {
							resolve(result);
						}
					}
				});
			return promise;
		};

		/**
		 * Promise is resolved with the edited image when editing completed.
		 * @param {Object} config Options for the image editor
		 * @param {File} file The image to edit.
		 * @returns {Promise} Resolved with File.
		 */
		function editFile(config, file) {
			var promise = new Promise(function(resolve, reject) {
				var callbacks = {
					win: resolve.bind(promise),
					lose: reject.bind(promise)
				};
				getEditor(callbacks, file).then(function() {
					var fileReader;
					fbCanvas = new fabric.Canvas("wc_img_canvas");
					fbCanvas.setWidth(config.width || defaults.width);
					fbCanvas.setHeight(config.height || defaults.height);
					overlayUrl = config.overlay;
					if (file) {
						fileReader = new FileReader();
						fileReader.onload = filereaderLoaded;
						fileReader.readAsDataURL(file);
					}
					else {
						imageCapture.initialize(fbCanvas.getWidth(), fbCanvas.getHeight());
						imageCapture.play();
					}
				});
			});
			return promise;
		};

		/*
		 * Callback invoked when a FileReader instance has loaded.
		 */
		function filereaderLoaded($event) {
			loadImageFromDataUrl($event.target.result);
		}

		/*
		 * Callback invoked when an img element has loaded.
		 */
		function imageLoaded($event) {
			renderImage($event.target);
		}

		/**
		 * Loads an image from a data URL into the editor.
		 * @param {string} dataUrl An image encoded into a data url.
		 */
		function loadImageFromDataUrl(dataUrl) {
			var imgObj = new Image();
			imgObj.src = dataUrl;
			imgObj.onload = imageLoaded;
		}

		/*
		 * Displays an img element in the image editor.
		 * @param {Element} img An image element.
		 */
		function renderImage(img) {
			var width = fbCanvas.getWidth(),
				height = fbCanvas.getHeight(),
				imageWidth, imageHeight;
			fbImage = new fabric.Image(img);
			fbImage.set({
				angle: 0,
				top: 0,
				left: 0,
				minScaleLimit: 0.1,
				lockUniScaling: true,
				centeredScaling: true,
				centeredRotation: true
			});
			imageWidth = fbImage.getWidth();
			imageHeight = fbImage.getHeight();
			if (imageWidth > imageHeight) {
				fbImage.scaleToWidth(width).setCoords();
			}
			else {
				fbImage.scaleToHeight(height).setCoords();
			}
			stateStack.length = 0;
			fbCanvas.clear();
			fbCanvas.add(fbImage);
			if (overlayUrl) {
				fbCanvas.setOverlayImage(overlayUrl, positionOverlay);
			}
			else {
				fbCanvas.renderAll();
			}
			fbImage.saveState();
			stateStack.push(JSON.stringify(fbImage.originalState));
		}

		/**
		 * Ensures that the overlay image is correctly positioned.
		 * The overlay MUST be the correct aspect ratio.
		 * @private
		 * @function
		 */
		function positionOverlay() {
			var overlay = fbCanvas.overlayImage,
				width = fbCanvas.getWidth();
			if (overlay) {
				overlay.scaleToWidth(width).setCoords();
				fbCanvas.renderAll();
			}
		}


		/**
		 * Builds the editor DOM and displays it to the user.
		 * @param {Object} callbacks An object with two callbacks: "win" and "lose".
		 * @param {File} file The file being edited.
		 * @returns {Promise} Resolved with the top level editor DOM element when it is ready.
		 * @function
		 * @private
		 */
		function getEditor(callbacks, file) {
			frameConfig = frameConfig || getDialogFrameConfig();
			var promise = new Promise(function(resolve, reject) {
				var container = document.body.appendChild(document.createElement("div"));
				container.className = "wc_img_editor";

				loader.load(TEMPLATE_NAME, true, true).then(function(template) {
					var eventConfig, editorHtml, i18nProps = {
							heading: {
								rotate: i18n.get("${wc.ui.imageEdit.rotate}"),
								move: i18n.get("${wc.ui.imageEdit.move}"),
								zoom: i18n.get("${wc.ui.imageEdit.zoom}")
							},
							action: {
								rotateLeft: i18n.get("${wc.ui.imageEdit.rotate.left}"),
								rotateRight: i18n.get("${wc.ui.imageEdit.rotate.right}"),
								rotateLeft90: i18n.get("${wc.ui.imageEdit.rotate.left90}"),
								rotateRight90: i18n.get("${wc.ui.imageEdit.rotate.right90}"),
								moveLeft: i18n.get("${wc.ui.imageEdit.move.left}"),
								moveRight: i18n.get("${wc.ui.imageEdit.move.right}"),
								moveUp: i18n.get("${wc.ui.imageEdit.move.up}"),
								moveDown: i18n.get("${wc.ui.imageEdit.move.down}"),
								zoomIn: i18n.get("${wc.ui.imageEdit.zoom.in}"),
								zoomOut: i18n.get("${wc.ui.imageEdit.zoom.out}"),
								reset: i18n.get("${wc.ui.imageEdit.action.reset}"),
								cancel: i18n.get("${wc.ui.imageEdit.action.cancel}"),
								save: i18n.get("${wc.ui.imageEdit.action.save}"),
								snap: i18n.get("${wc.ui.imageEdit.action.snap}"),
								speed: i18n.get("${wc.ui.imageEdit.action.speed}")
							},
							message: {
								novideo: "Video stream not available.",
								nocapture: "Your browser does not support image capture.",
								rotateLeft: "Rotate the image anti-clockwise",
								rotateRight: "Rotate the image clockwise",
								rotateLeft90: "Rotate anti-clockwise to next multiple of 90º",
								rotateRight90: "Rotate clockwise to next multiple of 90º",
								moveLeft: "Move the image to the left",
								moveRight: "Move the image to the right",
								moveUp: "Move the image to the up",
								moveDown: "Move the image to the down",
								zoomIn: "Zoom in",
								zoomOut: "Zoom out",
								reset: "Undo all changes to the image",
								cancel: "Abort image editing",
								save: "Save the image",
								snap: "Take a snapshot from the video stream",
								speed: "How quickly the buttons affect the image"
							},
							value: {
								speedNow: 1.5,
								speedMin: 0.5,
								speedMax: 5
							}
						};
					editorHtml = Mustache.to_html(template, i18nProps);

					container.innerHTML = editorHtml;
					eventConfig = attachEventHandlers(container);
					zoomControls(eventConfig);
					moveControls(eventConfig);
					resetControl(eventConfig);
					cancelControl(eventConfig, container, callbacks, file);
					saveControl(eventConfig, container, callbacks, file);
					rotationControls(eventConfig);
					if (file) {
						classList.add(container, "nocap");
					}
					else if (has("rtc-gum")) {
						imageCapture.snapshotControl(eventConfig);
					}
					else {
						classList.add(container, "cantplay");
					}
					resolve(container);
				}, reject);
			});

			return Promise.all([promise, dialogFrame.open(frameConfig)]).then(function(values) {
				var dialogContent = dialogFrame.getContent(),
					container = values[0];

				if (dialogContent && container) {
					dialogContent.innerHTML = "";
					dialogContent.appendChild(container);
					dialogFrame.reposition();
				}
			});
		}

		/**
		 * Wire up event listeners for the editor.
		 * @param {Element} container The top level editor DOM element.
		 * @returns {Object} An object used to map events to actions.
		 * @function
		 * @private`
		 */
		function attachEventHandlers(container) {
			var timer, eventConfig = {
					press: {},
					click: {}
				};
			event.add(container, "mousedown", pressStart);
			event.add(container, "touchstart", pressStart);
			event.add(container, "mouseout", pressEnd);
			event.add(container, "click", clickEvent);
			event.add(document.body, "mouseup", pressEnd);
			event.add(document.body, "touchcancel", pressEnd);
			event.add(container, "touchend", pressEnd);

			function clickEvent($event) {
				var config = getEventConfig($event.target, "click");
				if (config) {
					timers.clearTimeout(timer);
					timer = timers.setTimeout(config.func, 0, config);
				}
			}

			function callbackWrapper(config) {
				config.func(config);
				timer = timers.setTimeout(callbackWrapper, 100, config);
			}

			function pressStart($event) {
				var config = getEventConfig($event.target, "press");
				if (config) {
					timer = timers.setTimeout(callbackWrapper, 100, config);
				}
			}

			function pressEnd() {
				timers.clearTimeout(timer);
			}

			function getEventConfig(element, type) {
				var name = element.className;
				if (element.localName === "button" && name && eventConfig[type]) {
					return eventConfig[type][name];
				}
				return null;
			}

			return eventConfig;
		}

		/*
		 * Get the angle to set when we want to rotate an image (which may already be rotated) to the next multiple
		 * of step.
		 *
		 * @param {Number} currentValue The current angle of rotation.
		 * @param {Number} step The angle of unit rotation, eg 90 or 45 (or Math.PI if you are really odd).
		 * @returns {Number} The number of degrees to which we want to set the item being rotated.
		 */
		function rotateToStepHelper(currentValue, step) {
			var interim;

			if (!step) {
				return currentValue; // no step why are you calling me?
			}

			if (!currentValue) { // start at 0
				return step;
			}

			if (currentValue % step === 0) { // current value is already a multiple of step so everything is easy.
				return currentValue + step;
			}

			interim = currentValue + step; // this is a simple rotate by step, now we need to work out where we should be.
			return Math.floor(interim / step) * step;
		}

		/*
		 * Helper for features that change numeric properties of the image on the canvas.
		 */
		function numericProp(config) {
			var newValue,
				currentValue,
				getter = config.getter || ("get" + config.prop),
				setter = config.setter || ("set" + config.prop),
				speed = document.getElementById("wc_img_speed"),
				step = config.step || 1; // do not allow step to be 0
			if (fbImage) {
				currentValue = fbImage[getter]();
				if (config.exact) {
					newValue = rotateToStepHelper(currentValue, step);
				}
				else if (speed) {
					newValue = currentValue + (step * speed.value);
				}
				else {
					newValue = currentValue + step;
				}
				fbImage[setter](newValue);
				fbCanvas.renderAll();
			}
		}

		/*
		 * Wires up the "reset" feature.
		 */
		function resetCanvas() {
			var originalState = stateStack.length > 1 ? stateStack.pop() : stateStack[0];
			if (originalState) {
				originalState = JSON.parse(originalState);
				if (fbImage) {
					fbImage.setOptions(originalState);
					fbCanvas.renderAll();
				}
			}
		}

		/*
		 * Wires up the "move" feature.
		 */
		function moveControls(eventConfig) {
			var press = eventConfig.press;
			press.up = {
				func: numericProp,
				prop: "Top",
				step: -1
			};

			press.down = {
				func: numericProp,
				prop: "Top",
				step: 1
			};

			press.left = {
				func: numericProp,
				prop: "Left",
				step: -1
			};

			press.right = {
				func: numericProp,
				prop: "Left",
				step: 1
			};
		}

		/*
		 * Wires up the "zoom" feature.
		 */
		function zoomControls(eventConfig) {
			var press = eventConfig.press;
			press.in = {
				func: numericProp,
				getter: "getScaleX",
				setter: "scale",
				step: 0.05
			};

			press.out = {
				func: numericProp,
				getter: "getScaleX",
				setter: "scale",
				step: -0.05
			};
		}

		/*
		 * Wires up the "rotation" feature.
		 */
		function rotationControls(eventConfig) {
			var press = eventConfig.press;
			press.clock = {
				func: numericProp,
				prop: "Angle",
				step: 1
			};

			press.anticlock = {
				func: numericProp,
				prop: "Angle",
				step: -1
			};

			var click = eventConfig.click;
			click.clock90 = {
				func: numericProp,
				prop: "Angle",
				step: 90,
				exact: true
			};

			click.anticlock90 = {
				func: numericProp,
				prop: "Angle",
				step: -90,
				exact: true
			};
		}

		/*
		 * Wires up the "reset" feature.
		 */
		function resetControl(eventConfig) {
			var click = eventConfig.click;
			click.reset = {
				func: resetCanvas
			};
		}

		/*
		 * Wires up the "cancel" feature.
		 */
		function cancelControl(eventConfig, editor, callbacks/* , file */) {
			var click = eventConfig.click;
			click.cancel = {
				func: saveImage.bind(null, editor, callbacks, true)
			};
		}

		/*
		 * Wires up the "save" feature.
		 */
		function saveControl(eventConfig, editor, callbacks, file) {
			var click = eventConfig.click;
			click.save = {
				func: saveImage.bind(null, editor, callbacks, false, file)
			};
		}

		/**
		 * The exit point of the editor, either save or cancel the edit.
		 * @param {Element} editor The top level container element of the editor component.
		 * @param {Object} callbacks "win" and "lose".
		 * @param {boolean} cancel Cease all editing, the user wishes to cancel.
		 * @param {File} [file] The binary file being edited.
		 */
		function saveImage(editor, callbacks, cancel, file) {
			var overlay, result, done = function() {
					fbCanvas = fbImage = null;
					imageCapture.stop();
					editor.parentNode.removeChild(editor);
				};
			try {
				if (cancel) {
					done();
					callbacks.lose();
				}
				else {
					fbCanvas.deactivateAll();  // selection box should not be part of the image
					overlay = fbCanvas.overlayImage;
					if (overlay) {
						fbCanvas.overlayImage.visible = false;  // remove the overlay
					}
					if (file && !hasChanged()) {
						console.log("No changes made, using original file");
						result = file;  // if the user has made no changes simply pass thru the original file.
					}
					else {
						result = fbCanvas.toDataURL();
						result = dataURItoBlob(result);
						result = blobToFile(result, file);
					}
					done();
					callbacks.win(result);
				}
			}
			finally {
				dialogFrame.close();
				dialogFrame.resetContent();
			}
		}

		/**
		 * Determine if the user has actually made any changes to the image in the editor.
		 * @returns {boolean} true if the user has made changes.
		 */
		function hasChanged() {
			var currentState, originalState = stateStack[0];
			fbImage.saveState();
			currentState = JSON.stringify(fbImage.originalState);
			return currentState !== originalState;
		}

		/**
		 * Converts an img element to a File blob.
		 * @param {Element} element An img element.
		 * @returns {File} The image as a binary File.
		 */
		function imgToFile(element) {
			var file, fbImageTemp, dataUrl, blob, config = {
					name: element.id
				};
			if (element && element.src) {
				fbImageTemp = new fabric.Image(element);
				dataUrl = fbImageTemp.toDataURL();
				blob = dataURItoBlob(dataUrl);
				file = blobToFile(blob, config);
			}
			return file;
		}

		/**
		 * Converts a generic binary blob to a File blob.
		 * @param {Blob} blob
		 * @param {Object} [config] Attempt to set some of the file properties such as "type", "name"
		 * @returns {File} The File blob.
		 */
		function blobToFile(blob, config) {
			var name,
				filePropertyBag = {
					type: blob.type,
					lastModified: new Date()
				};
			if (config) {
				if (!filePropertyBag.type) {
					filePropertyBag.type = config.type;
				}
				name = config.name;
			}
			name = name || uid();


//			if (typeof File === "function") {
//				return new File([blob], name, filePropertyBag);
//			}
			if (!blob.type) {
				blob.type = filePropertyBag.type;
			}
			blob.lastModifiedDate = filePropertyBag.lastModified;
			blob.lastModified = filePropertyBag.lastModified.getTime();
			blob.name = name;
			return blob;
		}

		/**
		 * Converts a data url to a binary blob.
		 * @param {string} dataURI
		 * @returns {Blob} The binary blob.
		 */
		function dataURItoBlob(dataURI) {
			// convert base64/URLEncoded data component to raw binary data held in a string
			var byteString, mimeString, ia, i;
			if (dataURI.split(",")[0].indexOf("base64") >= 0) {
				byteString = atob(dataURI.split(",")[1]);
			}
			else {
				byteString = unescape(dataURI.split(",")[1]);
			}

			// separate out the mime component
			mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

			// write the bytes of the string to a typed array
			ia = new Uint8Array(byteString.length);
			for (i = 0; i < byteString.length; i++) {
				ia[i] = byteString.charCodeAt(i);
			}

			return new Blob([ia], { type: mimeString });
		}

		/**
		 * Encapsulates the image capture functionality.
		 *
		 * TODO allow user to select video source or rely on platform to provide this?
		 *
		 * @constructor
		 */
		function ImageCapture() {
			var _stream,
				streaming,
				VIDEO_ID = "wc_img_video";

			has.add("rtc-gum", function() {
				return (gumWrapper());
			});

			/*
			 * Wires up the "take photo" feature.
			 */
			this.snapshotControl = function (eventConfig) {
				var click = eventConfig.click;
				if (has("rtc-gum")) {
					click.snap = {
						func: function() {
							var dataUrl,
								fbImageTemp,
								video = document.getElementById(VIDEO_ID);
							if (video) {
								fbImageTemp = new fabric.Image(video);
								dataUrl = fbImageTemp.toDataURL();
								loadImageFromDataUrl(dataUrl);
							}
						}
					};
				}
			};

			/**
			 * Initialize the video element.
			 * Provide the width and the video height will be scaled to fit.
			 * @param {number} width The width used to scale the video.
			 * @param {number} [_height] Not used under normal circumstances.
			 */
			this.initialize = function(width, _height) {
				var video = document.getElementById(VIDEO_ID);
				video.setAttribute("width", width);
				streaming = false;
				video.addEventListener("canplay", function() {
					var height;
					if (!streaming) {
						height = video.videoHeight / (video.videoWidth / width);

						// Firefox currently has a bug where the height can't be read from the video
						if (isNaN(height)) {
							height = width / (width / _height);
						}

						video.setAttribute("width", width);
						video.setAttribute("height", height);

						streaming = true;
					}
				}, false);
			};

			/*
			 * Wraps the call to getUserMedia to hide the turmoil.
			 * Has the same signature and return as the native getUserMedia call EXCEPT if you call it with no args it's basically a feature test
			 * and returns truthy if GUM is supported.
			 */
			function gumWrapper(constraints, playCb, errCb) {
				var i, next, props = ["getUserMedia", "webkitGetUserMedia", "mozGetUserMedia", "msGetUserMedia"];
				for (i = 0; i < props.length; i++) {
					next = props[i];
					if (navigator.mediaDevices && navigator.mediaDevices[next]) {
						if (arguments.length === 3) {
							navigator.mediaDevices[next](constraints).then(playCb, errCb);
						}
						return true;
					}
					if (navigator[next]) {
						if (arguments.length === 3) {
							navigator[next](constraints, playCb, errCb);
						}
						return true;
					}
				}
				return false;
			};

			function playCb(stream) {
				var vendorURL, video = document.getElementById(VIDEO_ID);
				_stream = stream;
				if (navigator.mozGetUserMedia) {
					video.mozSrcObject = stream;
				}
				else {
					vendorURL = window.URL || window.webkitURL;
					video.src = vendorURL.createObjectURL(stream);
				}
				video.play();
			}

			function errCb(err) {
				console.log("An error occured! " + err);
			}

			/**
			 * Close the web camera video stream.
			 */
			this.stop = function() {
				var i, track, tracks, video = document.getElementById(VIDEO_ID);
				if (video) {
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
					}
					else if (_stream.stop) {
						_stream.stop();
					}
					_stream = null;
				}
			};

			/**
			 * Start the web camera video stream.
			 */
			this.play = function() {
				var constraints = {
					video: true,// { facingMode: "user" },
					audio: false
				};
				if (has("rtc-gum")) {
					gumWrapper(constraints, playCb, errCb);
				}
			};
		}
	}
	return imageEdit;
});
