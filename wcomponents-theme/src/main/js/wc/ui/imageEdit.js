define(["wc/has", "wc/dom/event", "wc/dom/uid", "wc/dom/classList", "wc/timers", "wc/dom/shed", "wc/config",
	"wc/loader/resource", "wc/i18n/i18n", "fabric", "Mustache", "wc/ui/dialogFrame", "getUserMedia"],
function(has, event, uid, classList, timers, shed, wcconfig, loader, i18n, fabric, Mustache, dialogFrame, getUserMedia) {
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
			faceDetection = new FaceDetection(),
			overlayUrl,
			defaults = {
				width: 320,
				height: 240
			},
			stateStack = [],
			registeredIds = {},
			fbCanvas, fbImage;


		function getDialogFrameConfig(onclose) {
			return {
				onclose: onclose,
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
							id = element.getAttribute("data-wc-selector");
						if (id && element.localName === "button") {
							uploader = document.getElementById(id);
							if (uploader) {
								img = document.getElementById(element.getAttribute("data-wc-img"));
								if (img) {
									file = imgToFile(img);
									multiFileUploader.upload(uploader, [file]);
								}
								else {
									imageEdit.editFiles({
										id: id,
										name: element.getAttribute("data-wc-editor")
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
					editorId = obj.getAttribute("data-wc-editor");
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
				getEditor(config, callbacks, file).then(function() {
					var fileReader;
					fbCanvas = new fabric.Canvas("wc_img_canvas");
					fbCanvas.setWidth(config.width || defaults.width);
					fbCanvas.setHeight(config.height || defaults.height);
					fbCanvas.on("selection:cleared", function() {
						if (fbImage) {
							fbCanvas.setActiveObject(fbImage);
						}
					});
					overlayUrl = config.overlay;
					if (file) {
						fileReader = new FileReader();
						fileReader.onload = filereaderLoaded;
						fileReader.readAsDataURL(file);
					}
					else {
						imageCapture.play({
							width: fbCanvas.getWidth(),
							height: fbCanvas.getHeight()
						});
					}
				});
			});
			return promise;
		};

		/*
		 * Callback invoked when a FileReader instance has loaded.
		 */
		function filereaderLoaded($event) {
			renderImage($event.target.result);
		}

		/**
		 * We're assuming that the image should not scale too small...
		 * This should probably be a config parameter.
		 * @param {number} availWidth The width of the canvas.
		 * @param {number} availHeight The height of the canvas.
		 * @param {number} imgWidth The raw image width.
		 * @param {number} imgHeight The raw image height.
		 * @returns {number} The minimum scale to keep this image from getting too small.
		 */
		function calcMinScale(availWidth, availHeight, imgWidth, imgHeight) {
			var result, minScaleDefault = 0.1, minScaleX, minScaleY,
				minWidth = availWidth * 0.7,
				minHeight = availHeight * 0.7;
			if (imgWidth > minWidth) {
				minScaleX = minWidth / imgWidth;
			}
			else {
				minScaleX = minScaleDefault;
			}
			if (imgHeight > minHeight) {
				minScaleY = minHeight / imgHeight;
			}
			else {
				minScaleY = minScaleDefault;
			}
			result = Math.max(minScaleX, minScaleY);
			return result;
		}

		/*
		 * Displays an img element in the image editor.
		 * @param {Element|string} img An image element or a dataURL.
		 */
		function renderImage(img, callback) {
			var minScaleLimit = 0.1,
				width = fbCanvas.getWidth(),
				height = fbCanvas.getHeight(),
				imageWidth, imageHeight;
			try {
				if (img.nodeType) {
					renderFabricImage(new fabric.Image(img));
				}
				else {
					fabric.Image.fromURL(img, renderFabricImage);
				}
			}
			catch (ex) {
				console.warn(ex);
			}
			function renderFabricImage(fabricImage) {
				fbImage = fabricImage;
				fabricImage.set({
					angle: 0,
					top: 0,
					left: 0,
					lockScalingFlip: true,
					lockUniScaling: true,
					centeredScaling: true,
					centeredRotation: true
				});
				imageWidth = fabricImage.getWidth();
				imageHeight = fabricImage.getHeight();
				if (imageWidth > imageHeight) {
					fabricImage.scaleToWidth(width).setCoords();
				}
				else {
					fabricImage.scaleToHeight(height).setCoords();
				}
				fabricImage.width = imageWidth;
				fabricImage.height = imageHeight;
				minScaleLimit = calcMinScale(width, height, imageWidth, imageHeight);
				fabricImage.minScaleLimit = minScaleLimit;
				stateStack.length = 0;
				fbCanvas.clear();
				fbCanvas.add(fabricImage);
				if (overlayUrl) {
					fbCanvas.setOverlayImage(overlayUrl, positionOverlay);
				}
				else {
					fbCanvas.renderAll();
				}
				fabricImage.saveState();
				stateStack.push(JSON.stringify(fabricImage.originalState));
				if (callback) {
					callback();
				}
			}
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
		 * @param {Object} config Map of configuration properties.
		 * @param {Object} callbacks An object with two callbacks: "win" and "lose".
		 * @param {File} file The file being edited.
		 * @returns {Promise} Resolved with the top level editor DOM element when it is ready.
		 * @function
		 * @private
		 */
		function getEditor(config, callbacks, file) {
			var promise = new Promise(function(resolve, reject) {
				var container = document.body.appendChild(document.createElement("div"));
				container.className = "wc_img_editor";

				loader.load(TEMPLATE_NAME, true, true).then(function(template) {
					var eventConfig, editorHtml, editorProps = {
							style: {
								width: config.width || defaults.width,
								height: config.height || defaults.height
							},
							heading: {
								capture: "Capture",
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
								camera: "Camera",
								face: "Detect Face"
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
								camera: "Take a photo from your webcam",
								face: "Attempt to detect and center facial image"
							},
							feature: {
								face: false
							}
						};
					editorHtml = Mustache.to_html(template, editorProps);

					container.innerHTML = editorHtml;
					eventConfig = attachEventHandlers(container);
					zoomControls(eventConfig);
					moveControls(eventConfig);
					resetControl(eventConfig);
					cancelControl(eventConfig, container, callbacks, file);
					saveControl(eventConfig, container, callbacks, file);
					rotationControls(eventConfig);
					// if (config.face) {
					faceDetection.initControls(eventConfig, container);
					// }
					if (!file) {
						classList.add(container, "wc_camenable");
						classList.add(container, "wc_showcam");
						imageCapture.snapshotControl(eventConfig, container);
					}
					resolve(container);
				}, reject);
			});

			return Promise.all([promise, dialogFrame.open(getDialogFrameConfig(function() {
				imageCapture.stop();
				callbacks.lose();
			}))]).then(function(values) {
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
			var timer,
				MAX_SPEED = 10,
				MIN_SPEED = 0.5,
				START_SPEED = 1.5,
				speed = START_SPEED,
				eventConfig = {
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
				var element = $event.target,
					config = getEventConfig(element, "click");
				if (config) {
					pressEnd();
					timer = timers.setTimeout(config.func, 0, config);
				}
			}

			function callbackWrapper(config) {
				config.func(config, speed);
				// Speed up while the button is being held down
				speed += (speed * 0.1);
				if (speed < MIN_SPEED) {
					speed = MIN_SPEED;
				}
				else if (speed > MAX_SPEED) {
					speed = MAX_SPEED;
				}
			}

			function pressStart($event) {
				var config = getEventConfig($event.target, "press");
				if (config) {
					pressEnd();
					timer = timers.setInterval(callbackWrapper, 100, config);
				}
			}

			function pressEnd() {
				speed = START_SPEED;
				if (timer) {
					timers.clearInterval(timer);
				}
			}

			function getEventConfig(element, type) {
				var name = element.name;
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
		function numericProp(config, speed) {
			var newValue,
				currentValue,
				getter = config.getter || ("get" + config.prop),
				setter = config.setter || ("set" + config.prop),
				step = config.step || 1; // do not allow step to be 0
			if (fbImage) {
				currentValue = fbImage[getter]();
				if (config.exact) {
					newValue = rotateToStepHelper(currentValue, step);
				}
				else if (speed) {
					newValue = currentValue + (step * speed);
				}
				else {
					newValue = currentValue + step;
				}
				if (config.min) {
					newValue = Math.max(config.min, newValue);
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
				step: -0.05,
				min: 0.1
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
			var canvasElement, overlay, result, done = function() {
					canvasElement = fbCanvas = fbImage = null;
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
						fbCanvas.renderAll();
					}
					if (file && !hasChanged()) {
						console.log("No changes made, using original file");
						result = file;  // if the user has made no changes simply pass thru the original file.
					}
					else {
						canvasElement = fbCanvas.getElement();
						result = canvasElement.toDataURL();
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
			var scale = 1,
				canvas = document.createElement("canvas"),
				context, file, dataUrl, blob, config = {
					name: element.id
				};
			if (element && element.src) {
				canvas.width = element.width * scale;
				canvas.height = element.height * scale;
				context = canvas.getContext("2d");
				context.drawImage(element, 0, 0);
				dataUrl = canvas.toDataURL("image/png");
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
		 * Encapsulates the face detection functionality.
		 *
		 * @constructor
		 */
		function FaceDetection() {

			this.initControls = function(eventConfig, container) {
				require(["wc/ui/facetracking"], function(facetracking) {
					eventConfig.click.face = {
						func: function() {
							var button = container.querySelector("[name='face']"),
								done = function(msg) {
									if (msg) {
										console.log(msg);
									}
									if (button) {
										shed.enable(button);
									}
								};
							if (button) {
								shed.disable(button);
							}
							facetracking.track(fbImage.getElement()).then(function(rect) {
								if (rect) {
									zoomFace(rect);
								}
								done();
							}, done);
						}
					};
				});
			};

			/**
			 * Attempts to zoom in on a face in the image.
			 * @param {Object} rect Coordinates of the face to zoom.
			 */
			function zoomFace(rect) {
				var newLeft, newTop,
					totalWidth = fbCanvas.getWidth(),
					ZOOM_TO_PC = 0.8,
					totalPadPc = Math.max(0, 1 - ZOOM_TO_PC),
					totalPadPixels = totalWidth * totalPadPc,
					targetWidthPixels = totalWidth * ZOOM_TO_PC,
					targetScale = targetWidthPixels / rect.width;
				fbImage.scale(targetScale);
				fbImage.setAngle(0);  // TODO we should really rotate the image we pass to trackingjs
				newLeft = (totalPadPixels / 2) - (rect.x * targetScale);
				newTop = totalPadPixels - (rect.y * targetScale);  // The face is lower on the head so it probably needs more padding...
				fbImage.setLeft(newLeft);
				fbImage.setTop(newTop);
				fbCanvas.renderAll();
			};

	//		function markFace(rect) {
	//			var leftOffset = fbImage.getLeft(),
	//				topOffset = fbImage.getTop(),
	//				div = document.createElement("div"),
	//				container = document.querySelector(".canvas-container");
	//			div.style.position = "relative";
	//			div.style.border = "1px lime dashed";
	//			div.addEventListener("click", function() {
	//				container.removeChild(div);
	//			}, false);
	//			div.style.width = rect.width + "px";
	//			div.style.height = rect.height + "px";
	//			div.style.top = (rect.y + topOffset) + "px";
	//			div.style.left = (rect.x + leftOffset) + "px";
	//			container.appendChild(div);
	//
	//			if (container) {
	//				var divs = container.querySelectorAll("div");
	//				for (var i = 0; i < divs.length; i++) {
	//					divs[i].parentNode.removeChild(divs[i]);
	//				}
	//			}
	//		}
		}

		/**
		 * Encapsulates the image capture functionality.
		 *
		 * TODO allow user to select video source or rely on platform to provide this?
		 *
		 * @constructor
		 */
		function ImageCapture() {
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

			has.add("rtc-gum", function() {
				return (gumWrapper());
			});

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
					renderImage(canvas.toDataURL());
					pos = 0;
				}
			}

			/*
			 * Wires up the "take photo" feature.
			 */
			this.snapshotControl = function (eventConfig, container) {
				var click = eventConfig.click,
					done = function(_video) {
						var video = _video || getVideo();
						classList.remove(container, "wc_showcam");
						imageCapture.stop();
						video.parentNode.removeChild(video);
					};
				activateCameraControl(eventConfig, container);
				click.snap = {
					func: function() {
						var video, img;
						if (currentOptions.context === "webrtc") {
							video = getVideo();
							if (video) {
								video.pause();
								img = videoToImage(video);
								renderImage(img, done);
							}
						}
						else if (currentOptions.context === "flash") {
							currentOptions.capture();
							classList.remove(container, "wc_showcam");
						}
						else {
							alert("No context was supplied to getSnapshot()");
						}
					}
				};
			};

			function activateCameraControl(eventConfig, container) {
				var click = eventConfig.click;
				click.camera = {
					func: function() {
						imageCapture.play({
							width: fbCanvas.getWidth(),
							height: fbCanvas.getHeight()
						});
						classList.add(container, "wc_showcam");
					}
				};
			}

			/*
			 * Wraps the call to getUserMedia to hide the turmoil.
			 * WARNING: call "gumWithFallback" instead - it will use native gum if found and fall back if necessary.
			 *
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

			/*
			 * Entry point to gum.
			 * Uses native getUserMedia if possible and falls back to plugins if it must.
			 */
			function gumWithFallback(constraints, playCb, errCb) {
				if (getUserMedia && arguments.length === 3) {
					getUserMedia(constraints, playCb, errCb);
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
					}
					else if (_stream.stop) {
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
				var globalOptions, globalConf = wcconfig.get("wc/ui/imageEdit");
				if (globalConf && globalConf.options) {
					globalOptions = globalConf.options;
				}
				else {
					globalOptions = {};
				}
				currentOptions = Object.assign({}, defaultOptions, globalOptions, options);
				currentOptions.width *= 1;
				currentOptions.height *= 1;
				window.webcam = currentOptions;  // Needed for flash fallback
				if (!has("rtc-gum") && currentOptions.swffile === defaultOptions.swffile && (currentOptions.width !== 320 || currentOptions.height !== 240)) {
					/*
					 * The default swffile can only support 320 x 240.
					 * Compile new swf files at different resolutions if you need them: https://github.com/infusion/jQuery-webcam
					 * You can then change the swffile location in the options using wc/config
					 */
					console.warn("The default flash fallback only supports 320 x 240");
					currentOptions.width = 320;
					currentOptions.height = 240;
				}
				gumWithFallback(currentOptions, playCb, errCb);
			};

			function videoToDataUrl(video, scale) {
				var _scale = scale || 1,
					canvas = document.createElement("canvas");
				canvas.width = video.videoWidth * _scale;
				canvas.height = video.videoHeight * _scale;
				canvas.getContext("2d").drawImage(video, 0, 0, canvas.width, canvas.height);
				return canvas.toDataURL();
			}

			function videoToImage(video, scale) {
				var dataUrl = videoToDataUrl(video, scale),
					img = new Image();
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
	}
	return imageEdit;
});
