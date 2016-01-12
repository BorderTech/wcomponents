define(["wc/ui/modalShim", "wc/has", "wc/dom/event", "wc/dom/uid", "wc/dom/classList", "wc/timers", "wc/loader/resource", "fabric"],
function(modalShim, has, event, uid, classList, timers, loader, fabric) {
	var imageEdit = new ImageEdit();

	/**
	 * This provides a mechanism to allow the user to edit images during the upload process.
	 * It may also be used to edit static images after they have been uploaded as long as a file uploader is configured to take the edited images.
	 *
	 * @constructor
	 */
	function ImageEdit() {

		var inited,
			imageCapture = new ImageCapture(),
			overlayUrl,
			defaults = {
				width: 300,
				height: 400
			},
			stateStack = [],
			registeredIds = {},
			fbCanvas, fbImage;

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
									});
								}
							}
						}
					}
				});
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
							promise = editFile(config, null).then(saveEditedFile, reject);
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
			var imgObj = new Image();
			imgObj.src = $event.target.result;
			imgObj.onload = imageLoaded;
		}

		/*
		 * Callback invoked when an img element has loaded.
		 */
		function imageLoaded($event) {
			renderImage($event.target);
		}

		/*
		 * Displays an img element in the image editor.
		 * @param {Element} img An image element.
		 */
		function renderImage(img) {
			var overlay,
				width = fbCanvas.getWidth(),
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
			overlay = fbCanvas.overlayImage;
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
			var promise = new Promise(function(resolve, reject) {
				var container = document.body.appendChild(document.createElement("div"));
				container.className = container.id = "wc_img_editor";
				loader.load("imageEdit.xml", true, true).then(function(html) {
					var eventConfig;
					container.innerHTML = html;
					modalShim.setModal(container);
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
					else if (!imageCapture.snapshotControl(eventConfig)) {
						classList.add(container, "cantplay");
					}
					resolve(container);
				}, reject);
			});
			return promise;
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
			event.add(container, "mouseout", pressEnd);
			event.add(container, "click", clickEvent);
			event.add(document.body, "mouseup", pressEnd);

			function clickEvent($event) {
				var config = getEventConfig($event.target, "click");
				if (config) {
					timers.clearTimeout(timer);
					timer = timers.setTimeout(config.func, 0);
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
		 * Helper for features that change numeric properties of the image on the canvas.
		 */
		function numericProp(config) {
			var newValue, currentValue, getter = config.getter || ("get" + config.prop),
				setter = config.setter || ("set" + config.prop),
				speed = document.getElementById("wc_img_speed");
			if (fbImage) {
				currentValue = fbImage[getter]();
				if (speed) {
					newValue = currentValue + (config.step * speed.value);
				}
				else {
					newValue = currentValue + config.step;
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
		function cancelControl(eventConfig, editor, callbacks, file) {
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
					result = fbCanvas.toDataURL();
					result = dataURItoBlob(result);
					result = blobToFile(result, file);
					done();
					callbacks.win(result);
				}
			}
			finally {
				modalShim.clearModal();
			}
		}

		/**
		 * Converts an img element to a File blob.
		 * @param {Element} element An img element.
		 * @returns {File} The image as a binary File.
		 */
		function imgToFile(element) {
			var file, img, dataUrl, blob, config = {
					name: element.id
				};
			if (element && element.src) {
				img = new fabric.Image(element);
				dataUrl = img.toDataURL();
				blob = dataURItoBlob(dataUrl);
				file = blobToFile(blob, config);
			}
			return file;
		}


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
			var file = new File([blob], name, filePropertyBag);
			return file;
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
		 * @constructor
		 */
		function ImageCapture() {
			var streaming,
				hasUserMedia,
				VIDEO_ID = "wc_img_video";

			/*
			 * Wires up the "take photo" feature.
			 */
			this.snapshotControl = function (eventConfig) {
				var click = eventConfig.click;
				hasUserMedia = gumWrapper();
				if (hasUserMedia) {
					click.snap = {
						func: function() {
							var video = document.getElementById(VIDEO_ID);
							if (video) {
								renderImage(video);
							}
						}
					};
				}
				return hasUserMedia;
			};

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
			 *
			 * TODO this should probably be tidied up and be its own module.
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

			this.play = function() {
				var video = document.getElementById(VIDEO_ID),
					constraints = {
						video: true,
						audio: false
					},
					playCb = function(stream) {
						if (navigator.mozGetUserMedia) {
							video.mozSrcObject = stream;
						}
						else {
							var vendorURL = window.URL || window.webkitURL;
							video.src = vendorURL.createObjectURL(stream);
						}
						video.play();
					},
					errCb = function(err) {
						console.log("An error occured! " + err);
					};
				if (hasUserMedia) {
					gumWrapper(constraints, playCb, errCb);
				}

			};
		}
	}
	return imageEdit;
});