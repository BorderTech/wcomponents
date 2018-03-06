define(["wc/has", "wc/mixin", "wc/config", "wc/dom/Widget", "wc/dom/event", "wc/dom/uid", "wc/dom/classList", "wc/timers", "wc/ui/prompt",
	"wc/i18n/i18n", "fabric", "wc/ui/dialogFrame", "wc/template", "wc/ui/ImageCapture", "wc/ui/ImageUndoRedo", "wc/file/getMimeType", "wc/file/size"],
function(has, mixin, wcconfig, Widget, event, uid, classList, timers, prompt, i18n, fabric, dialogFrame, template, ImageCapture, ImageUndoRedo, getMimeType, fileSize) {
	var timer,
		imageEdit = new ImageEdit();

	/**
	 * Used when checking the newly created image is named with the correct extension.
	 * If it does not already match any in the array then the extension at index zero will be appended.
	 **/
	ImageEdit.prototype.mimeToExt = {
		"image/jpeg": ["jpeg", "jpg"],
		"image/png": ["png"],
		"image/webp": ["webp"]
	};

	ImageEdit.prototype.renderCanvas = function(callback) {
		if (timer) {
			timers.clearTimeout(timer);
		}
		timer = timers.setTimeout(function() {
			var fbCanvas = imageEdit.getCanvas();
			fbCanvas.renderAll();
			if (callback) {
				callback();
			}
		}, 50);
	};

	ImageEdit.prototype.defaults = {
		maxsize: 20971520,  // limit the size, in bytes, of an image that can be loaded in the image editor (so the page does not hang)
		width: 320,
		height: 240,
		format: "png",  // png or jpeg
		quality: 1,  // only if format is jpeg
		multiplier: 1,
		face: false,
		rotate: true,
		zoom: true,
		move: true,
		redact: false,
		reset: true,
		undo: true,
		cancel: true,
		save: true,
		crop: true,
		invalidprompt: true,  // display a message to the user if the image fails validation (if false we assume this is handled elsewhere)
		ftlignore: false,  // user can try to ignore file too large warning
		msgidftl: "",  // i18n message ID for "file too large" validation
		msgidftlfix: "imgedit_message_fixtoolarge",  // i18n message ID for "fix file too large"
		autoresize: true  // if true then loading an image that exceeds size validaiton constraints will automatically trigger a resize attempt
	};

	/**
	 * This provides a mechanism to allow the user to edit images during the upload process.
	 * It may also be used to edit static images after they have been uploaded as long as a file uploader is configured to take the edited images.
	 *
	 * @constructor
	 */
	function ImageEdit() {

		var inited,
			TEMPLATE_NAME = "imageEdit.xml",
			imageCapture = new ImageCapture(this),
			overlayUrl,
			undoRedo,
			registeredIds = {},
			fbCanvas,
			BUTTON = new Widget("button"),
			NOOP = function() {

			};

		this.getCanvas = function() {
			return fbCanvas;
		};

		/**
		 * Registers a configuration object against a unique ID to specificy variables such as overlay image URL, width, height etc.
		 *
		 * @param {Object[]} arr Configuration objects.
		 */
		this.register = function(arr) {
			var i, next, inline = [];
			for (i = 0; i < arr.length; i++) {
				next = arr[i];
				registeredIds[next.id] = next;
				if (next.inline) {
					inline.push(next);
				}
			}
			if (!inited) {
				inited = true;
				require(["wc/dom/initialise", "wc/dom/formUpdateManager"], function(initialise, formUpdateManager) {
					initialise.addCallback(function(element) {
						event.add(element, "click", clickEvent);
						handleInline(inline);
						inline = null;
					});

					formUpdateManager.subscribe(imageEdit);

					/**
					 * Listens for edit requests on static images.
					 * @param {Event} $event A click event.
					 */
					function clickEvent($event) {
						var img, uploader, file, id,
							element = BUTTON.findAncestor($event.target);
						if (element) {
							id = element.getAttribute("data-wc-selector");
							if (id && element.localName === "button") {
								uploader = document.getElementById(id);
								if (uploader) {
									img = document.getElementById(element.getAttribute("data-wc-img"));
									if (img) {
										file = imgToFile(img);
										imageEdit.upload(uploader, [file]);
									} else {
										var win = function(files) {
											imageEdit.upload(uploader, files, true);
										};
										var lose = function(message) {
											if (message) {
												prompt.alert(message);
											}
										};
										imageEdit.editFiles({
											id: id,
											name: element.getAttribute("data-wc-editor")
										}, win, lose);
									}
								}
							}
						}
					}
				});
			}
		};

		/**
		 * Shares a method signature with multifileuploader upload (which overrides this method).
		 */
		this.upload = NOOP;

		/**
		 * Will be overriden in some circumstances.
		 */
		this.writeState = NOOP;

		/**
		 * Retrieve a configuration object.
		 * @param {Object} obj Get the configuration registered for the "id" or "name" property of this object (in that order).
		 * @returns {Object} configuration
		 */
		this.getConfig = function(obj) {
			var editorId, instanceConfig, result = wcconfig.get("wc/ui/imageEdit", this.defaults);
			if (obj) {
				instanceConfig = registeredIds[obj.id] || registeredIds[obj.name];
				if (!instanceConfig) {
					if ("getAttribute" in obj) {
						editorId = obj.getAttribute("data-wc-editor");
					} else {
						editorId = obj.editorId;
					}
					if (editorId) {
						instanceConfig = registeredIds[editorId];
					}
				}
				if (instanceConfig && !instanceConfig.__wcmixed) {
					result = mixin(instanceConfig, result);  // override defaults with explicit settings
					result.__wcmixed = true;  // flag that we have mixed in the defaults so it doesn't need to happen again
				}
			}
			return result;
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
		 * @param {Function} onSuccess Called with an array of File blobs that have potentially been edited by the user.
		 * @param {Function} onError called if something goes wrong.
		 */
		this.editFiles = function(obj, onSuccess, onError) {
			var config = imageEdit.getConfig(obj);
			var file, sizes, idx = 0, result = [], files = obj.files,
				done = function(arg) {
					try {
						onSuccess(arg);
					} finally {
						dialogFrame.close();
					}
				};
			try {
				if (files) {
					sizes = fileSize.get(obj);
					if (has("dom-canvas")) {
						editNextFile();
					} else {
						done(files);
					}
				} else if (config.camera) {
					editFile(config, null, saveEditedFile, onError);
				}
			} catch (ex) {
				onError(ex);
			}

			/*
			 * Once the user has commited their changes buffer the result and see if there is another file queued for editing.
			 */
			function saveEditedFile(fileToSave) {
				result.push(fileToSave);
				editNextFile();
			}

			/*
			 * Prompt the user to edit the next file in the queue.
			 */
			function editNextFile() {
				var size;
				if (files && idx < files.length) {
					size = sizes[idx];
					file = files[idx++];
					if (typeof file === "string") {
						if (file.indexOf("data:image/") === 0) {
							editFile(config, file, saveEditedFile, onError);
						} else {
							console.warn("Not a file", file);
						}
					} else if (file.type.indexOf("image/") === 0) {
						if (size > config.maxsize) {
							console.log("File size %d exceeds editor max %d", size, config.maxsize);
							saveEditedFile(file);
						} else {
							editFile(config, file, saveEditedFile, onError);
						}
					} else {
						saveEditedFile(file);
					}
				} else {
					done(result);
				}
			}
		};

		/**
		 * Callback is called with the edited image when editing completed.
		 * @param {Object} config Options for the image editor
		 * @param {File} file The image to edit.
		 * @param {function} win callback on success (passed a File)
		 * @param {function} lose callback on error
		 */
		function editFile(config, file, win, lose) {
			var callbacks = {
					win: win,
					lose: lose
				},
				gotEditor = function(editor) {
					var fileReader;
					fbCanvas = new fabric.Canvas("wc_img_canvas", {
						enableRetinaScaling: false
					});
					fbCanvas.setWidth(config.width);
					fbCanvas.setHeight(config.height);
//					fbCanvas.on("selection:cleared", function() {
//						if (fbImage) {
//							fbCanvas.setActiveObject(fbImage);
//						}
//					});
					overlayUrl = config.overlay;
					if (typeof file === "string") {
						imageEdit.renderImage(file);
					} else if (file) {
						fileReader = new FileReader();
						fileReader.onload = function ($event) {
							imageEdit.renderImage($event.target.result, function() {
								validateImage(file, editor).then(function(message) {
									if (message) {
										prompt.alert(message);
									}
								});
							});
						};
						fileReader.readAsDataURL(file);
					} else {
						imageCapture.play({
							width: fbCanvas.getWidth(),
							height: fbCanvas.getHeight()
						});
					}
					if (config.sync) {
						// This is not currently supported on the backend
						imageEdit.writeState = function() {
							callbacks.saveFunc = function() {
								saveImage({
									editor: editor,
									callbacks: callbacks,
									cancel: false });
							};
							callbacks.formatForSave = getCanvasAsDataUrl;
							checkThenSave(callbacks);
						};
					}
				};
			if (config.face) {
				require(["wc/ui/facetracking"], function(facetracking) {
					callbacks.validate = facetracking.getValidator(config);
					getEditor(config, callbacks, file).then(gotEditor);
				});
			} else if (config.redact) {
				require(["wc/ui/imageRedact"], function(imageRedact) {
					config.redactor = imageRedact;
					getEditor(config, callbacks, file).then(function() {
						gotEditor();
						config.redactor.activate(imageEdit);
					});
				});
			} else {
				getEditor(config, callbacks, file).then(gotEditor);
			}
			return callbacks;
		}

		/**
		 * Displays the editor inline if requested by the user.
		 * This probably only makes sense with a single inline editor but it's written to handle an array so as not to be mlimited by my imagination.
		 * @param inline An array of editors that are configured to be inline.
		 */
		function handleInline(inline) {
			var count = inline.length;
			if (count > 0) {
				inline.forEach(function(config) {
					if (config.image) {  // Right now display nothing unless there is an image in the editor, could be changed, but empty editor?
						imageEdit.editFiles({
							id: config.id,
							name: config.id,
							files: [config.image]
						});
					}
				});
			}
		}

		/**
		 * We're assuming that the image should not scale too small...
		 * This should probably be a config parameter.
		 * @param {number} availWidth The width of the canvas.
		 * @param {number} availHeight The height of the canvas.
		 * @param {number} imgWidth The raw image width.
		 * @param {number} imgHeight The raw image height.
		 * @param {fabric.Image} fbImage The image we are limiting
		 * @returns {number} The minimum scale to keep this image from getting too small.
		 */
		function calcMinScale(availWidth, availHeight, imgWidth, imgHeight, fbImage) {
			var result, minScaleDefault = 0.1, minScaleX, minScaleY,
				minWidth = availWidth * 0.7,
				minHeight = availHeight * 0.7;
			if (imgWidth > minWidth) {
				minScaleX = minWidth / imgWidth;
			} else {
				minScaleX = minScaleDefault;
			}
			if (imgHeight > minHeight) {
				minScaleY = minHeight / imgHeight;
			} else {
				minScaleY = minScaleDefault;
			}
			result = Math.max(minScaleX, minScaleY);
			if (fbImage.scaleX || fbImage.scaleY) {
				// if the image has been auto-scaled already then we should allow it to stay in those parameters
				result = Math.min(fbImage.scaleX, fbImage.scaleY, result);
			}
			return result;
		}

		/*
		 * Displays an img element in the image editor.
		 * @param {Element|string} img An image element or a dataURL.
		 */
		this.renderImage = function(img, callback) {
			var width = fbCanvas.getWidth(),
				height = fbCanvas.getHeight(),
				imageWidth, imageHeight;
			try {
				if (img.nodeType) {
					renderFabricImage(new fabric.Image(img));
				} else {
					fabric.Image.fromURL(img, renderFabricImage);
				}
			} catch (ex) {
				console.warn(ex);
			}
			function renderFabricImage(fabricImage) {
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
					// fbCanvas.setZoom(width / imageWidth);
					fabricImage.scaleToWidth(width).setCoords();
				} else {
					// fbCanvas.setZoom(height / imageHeight);
					fabricImage.scaleToHeight(height).setCoords();
				}
				fabricImage.width = imageWidth;
				fabricImage.height = imageHeight;
				calcMinScale(width, height, imageWidth, imageHeight, fabricImage);
				fbCanvas.clear();
				addToCanvas(fabricImage);

				if (overlayUrl) {
					fbCanvas.setOverlayImage(overlayUrl, positionOverlay);
				}
				fbCanvas.renderAll();
				fabricImage.saveState();
				undoRedo = new ImageUndoRedo(imageEdit);
				undoRedo.save();
				if (callback) {
					callback();
				}
			}
		};

		function addToCanvas(object) {
			fbCanvas.add(object);
		}

		this.selectAll = function() {
			var objects = fbCanvas.getObjects().map(function(o) {
				return o.set('active', true);
			});

			var group = new fabric.Group(objects, {
				originX: 'center',
				originY: 'center'
			});

			fbCanvas._activeObject = null;

			fbCanvas.setActiveGroup(group.setCoords()).renderAll();
			return group;
		};

		this.getFbImage = function(container) {
			var objects, currentContainer = (container || fbCanvas);
			if (currentContainer && currentContainer.getObjects) {
				objects = currentContainer.getObjects("image");
				if (objects && objects.length) {
					return objects[0];
				}
				objects = currentContainer.getObjects("group");
				if (objects && objects.length) {
					return objects[0];
				}
//				for (i = 0; i < objects.length; i++) {
//					next = objects[i];
//					result = imageEdit.getFbImage(next);
//					if (result) {
//						return result;
//					}
//				}
			}
			return null;
		};

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
		 * Show or hide the overlay image.
		 * @param fabricCanvas The FabricJS canvas.
		 * @param show If truthy unhides (shows) the overlay.
		 */
		function showHideOverlay(fabricCanvas, show) {
			var overlay = fabricCanvas.overlayImage;
			if (overlay) {
				fabricCanvas.overlayImage.visible = !!show;
				fabricCanvas.renderAll();
			}
		}

		function getDialogFrameConfig(onclose) {
			return i18n.translate("imgedit_title").then(function(title) {
				return {
					onclose: onclose,
					id: "wc_img_editor",
					modal: true,
					resizable: true,
					title: title
				};
			});
		}

		function getEditorContext(config, callbacks) {
			if (config.inline) {
				var contentContainer = document.getElementById(config.id);
				if (contentContainer) {
					return Promise.resolve(callbacks.render(contentContainer));
				}
				return Promise.reject("Can not find element", config.id);
			}
			return getDialogFrameConfig(function() {
				imageCapture.stop();
				callbacks.lose();
			}).then(function(dialogConfig) {
				callbacks.rendered = function() {
					dialogFrame.reposition();
				};
				if (dialogFrame.isOpen()) {
					return callbacks.render(dialogFrame.getContent());
				}
				return dialogFrame.open(dialogConfig).then(function() {
					return callbacks.render(dialogFrame.getContent());
				});
			});
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
			callbacks.render = renderEditor;

			function renderEditor(contentContainer) {
				var result = new Promise(function (win, lose) {
					var container = document.body.appendChild(document.createElement("div")),
						editorProps = {
							style: {
								width: config.width,
								height: config.height,
								textclass: "wc-off",
								btnclass: "wc_btn_icon"
							},
							feature: {
								face: false,
								rotate: config.rotate,
								zoom: config.zoom,
								move: config.move,
								redact: config.redact,
								reset: config.reset,
								undo: config.undo,
								cancel: config.cancel,
								save: config.save
							}
						},
						done = function(cntnr) {
							var actions = attachEventHandlers(cntnr);
							zoomControls(actions.events);
							moveControls(actions.events);
							resetControl(actions.events);
							cancelControl(actions.events, cntnr, callbacks, file);
							saveControl(actions.events, cntnr, callbacks, file);
							rotationControls(actions.events);
							if (config.redactor) {
								config.redactor.controls(actions.events, cntnr);
							}

							if (!file) {
								classList.add(cntnr, "wc_camenable");
								classList.add(cntnr, "wc_showcam");
								imageCapture.snapshotControl(actions.events, cntnr);
							}


							if (contentContainer && cntnr) {
								contentContainer.innerHTML = "";
								contentContainer.appendChild(cntnr);
								if (callbacks.rendered) {
									callbacks.rendered(contentContainer);
								}
							}
							win(cntnr);
						};
					try {
						return getTranslations(editorProps).then(function() {
							container.className = "wc_img_editor";
							container.setAttribute("data-wc-editor", config.id);
							template.process({
								source: TEMPLATE_NAME,
								loadSource: true,
								target: container,
								context: editorProps,
								callback: function() {
									done(container);
								}
							});
							return container;
						}, lose);
					} catch (ex) {
						lose(ex);
					}
				});
				return result;  // a promise
			}  // end "renderEditor"
			return getEditorContext(config, callbacks);
		}

		function getTranslations(obj) {
			var messages = ["imgedit_action_camera", "imgedit_action_cancel", "imgedit_action_redact",
				"imgedit_action_redo", "imgedit_action_reset", "imgedit_action_save", "imgedit_action_snap", "imgedit_action_undo",
				"imgedit_capture", "imgedit_message_camera", "imgedit_message_cancel", "imgedit_message_move_center", "imgedit_message_move_down",
				"imgedit_message_move_left", "imgedit_message_move_right", "imgedit_message_move_up",
				"imgedit_message_nocapture", "imgedit_message_redact", "imgedit_message_redo", "imgedit_message_reset",
				"imgedit_message_rotate_left", "imgedit_message_rotate_left90", "imgedit_message_rotate_right",
				"imgedit_message_rotate_right90", "imgedit_message_save", "imgedit_message_snap",
				"imgedit_message_undo", "imgedit_message_zoom_in", "imgedit_message_zoom_out", "imgedit_move",
				"imgedit_move_center", "imgedit_move_down", "imgedit_move_left", "imgedit_move_right", "imgedit_move_up", "imgedit_redact",
				"imgedit_rotate", "imgedit_rotate_left", "imgedit_rotate_left90", "imgedit_rotate_right",
				"imgedit_rotate_right90", "imgedit_zoom", "imgedit_zoom_in", "imgedit_zoom_out"];
			return i18n.translate(messages).then(function(translations) {
				var result = obj || {};
				messages.forEach(function(message, idx) {
					result[message] = translations[idx];

				});
				return result;
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
			var eventTimer,
				MAX_SPEED = 10,
				MIN_SPEED = 0.5,
				START_SPEED = 1.5,
				speed = START_SPEED,
				pressAction,
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
				var element = $event.target;
				if (!invoke.call(this, element, "click", $event)) {
					element = BUTTON.findAncestor(element);
					invoke.call(this, element, "click", $event);
				}
			}

			/**
			 * Increment the current speed.
			 * Used when a button is held down rather than clicked.
			 */
			function speedUp() {
				// Speed up while the button is being held down
				speed += (speed * 0.1);
				if (speed < MIN_SPEED) {
					speed = MIN_SPEED;
				} else if (speed > MAX_SPEED) {
					speed = MAX_SPEED;
				}
			}

			/**
			 * Call when the user begins to press a control.
			 * This starts the poller that will contiue to invoke the action as long as the control is pressed.
			 */
			function startPressPoller() {
				pressEnd();  // stop any previous poller
				eventTimer = timers.setInterval(function() {
					if (pressAction) {
						pressAction();
					}
				}, 100);
			}

			/**
			 * This handles the event fired when the user begins to press a control.
			 * @param {Event} $event The press event.
			 */
			function pressStart($event) {
				var target = $event.target,
					element = BUTTON.findAncestor(target),
					config = getEventConfig(element, "press");
				if (config) {
					pressEnd();
					startPressPoller();
					pressAction = function() {
						config.func(config, speed);
						speedUp();
					};
				}
			}

			/**
			 * Call to signal the end of a "press".
			 */
			function pressEnd() {
				try {
					if (eventTimer) {
						timers.clearInterval(eventTimer);
						if (pressAction) {
							// Ensure every press gets at least one invocation (otherwise click will do nothing)
							pressAction();
						}
					}
				} finally {
					speed = START_SPEED;
					pressAction = null;
				}
			}

			/**
			 * Gets the configuration for a particular event.
			 * @param {Element|String} action Either an element which should trigger an action (e.g. a save button) or the name of the action (e.g. "save")
			 * @param {String} type The type of event, e.g. "click" or "press"
			 * @returns A config object which knows how to action an event.
			 */
			function getEventConfig(action, type) {
				var isString = typeof action === "string";
				if (!action) {
					return null;
				}
				var name = isString ? action : action.name;
				if ((isString || action.localName === "button" || action.type === "checkbox") && name && eventConfig[type]) {
					return eventConfig[type][name];
				}
				return null;
			}

			/**
			 * Used to invoke an action on this editor.
			 * @param {Element|String} action Either an element which should trigger an action (e.g. a save button) or the name of the action (e.g. "save")
			 * @param {String} type The type of event, e.g. "click" or "press"
			 * @param payload Optionally provide a payload to be passed to the handler.
			 * @returns {Boolean} true if a matching action was found and (queued to be) invoked.
			 */
			function invoke(action, type, payload) {
				var result = false,
					config = getEventConfig(action, type || "click");
				if (config) {
					result = true;
					pressEnd();
					eventTimer = timers.setTimeout(config.func.bind(this, config, payload), 0);
				}
				return result;
			}

			return {
				events: eventConfig
			};
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
				fbImage = imageEdit.getFbImage(),  // this could be a group, does it matter?
				getter = config.getter || ("get" + config.prop),
				setter = config.setter || ("set" + config.prop),
				step = config.step || 1; // do not allow step to be 0
			if (fbImage) {
				currentValue = fbImage[getter]();
				if (config.exact) {
					newValue = rotateToStepHelper(currentValue, step);
				} else if (speed) {
					newValue = currentValue + (step * speed);
				} else {
					newValue = currentValue + step;
				}
				if (config.min) {
					newValue = Math.max(config.min, newValue);
				}
				fbImage[setter](newValue);
				imageEdit.renderCanvas(function() {
					if (undoRedo) {
						undoRedo.save();
					}
				});
				// fbCanvas.calcOffset();
			}
		}

		/*
		 * Wires up the "move" feature.
		 */
		function moveControls(eventConfig) {
			var press = eventConfig.press,
				click = eventConfig.click;
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

			click.center = {
				func: function() {
					var fbImage = imageEdit.getFbImage();
					if (fbImage) {
						fbImage.center();
					}
				}
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
		 * Wires up the "reset/undo/redo" feature.
		 */
		function resetControl(eventConfig) {
			var click = eventConfig.click;
			click.undo = {
				func: function() {
					if (undoRedo) {
						undoRedo.undo();
					}
				}
			};
			click.redo = {
				func: function() {
					if (undoRedo) {
						undoRedo.redo();
					}
				}
			};
			click.reset = {
				func: function() {
					if (undoRedo) {
						undoRedo.reset();
					}
				}
			};
		}

		/*
		 * Wires up the "cancel" feature.
		 */
		function cancelControl(eventConfig, editor, callbacks/* , file */) {
			var click = eventConfig.click,
				cancelFunc = function() {
					try {
						saveImage({
							editor: editor,
							callbacks: callbacks,
							cancel: true });
					} finally {
						dialogFrame.close();
					}
				};
			click.cancel = {
				func: cancelFunc
			};
		}

		/*
		 * Wires up the "save" feature.
		 */
		function saveControl(eventConfig, editor, callbacks, file) {
			var click = eventConfig.click;
			/**
			 * The call into the save "internals" with the args specific to this closure.
			 * @param [imageToSave] The image formatted for saving. This is a performance optimization: if the image
			 *    has already been formatted for saving during validation etc then it can be passed thru here to save having to format again.
			 */
			callbacks.saveFunc = function(imageToSave) {
				saveImage({
					editor: editor,
					callbacks: callbacks,
					cancel: false,
					originalImage: file,
					imageToSave: imageToSave });
			};

			/**
			 * Respond to the user's intent to save.
			 */
			click.save = {
				func: function() {
					callbacks.formatForSave = getCanvasAsFile;
					callbacks.validate = function() {
						var max, formattedImage,
							selector = getFileSelector(editor);
						if (selector) {
							max = fileSize.getMax(selector);
							if (max) {
								formattedImage = getImageToSave(null, file, callbacks.formatForSave);
								return validateImage(formattedImage, editor).then(function(message) {
									var config = imageEdit.getConfig(selector),
										result = {
											validated: formattedImage,
											ignorable: config.ftlignore,
											error: message,
											prompt: config.invalidprompt
										};
									return result;
								});
							}
						}
						return Promise.resolve({});
					};
					checkThenSave(callbacks);
				}
			};
			return click.save;
		}

		function validateImage(imageBlob, editor) {
			var config, selector, msg;
			if (imageBlob && imageBlob.size) {
				selector = getFileSelector(editor);
				config = imageEdit.getConfig(selector);
				msg = fileSize.check({
					element: selector,
					testObj: { files: [imageBlob] },
					msgId: config.msgidftl
				});
				if (msg) {
					if (config.autoresize) {
						msg = "";  // don't bug the user, we'll try to resolve this automatically
						if (undoRedo) {
							undoRedo._forceChange = true;
						}
					} else {
						return i18n.translate(config.msgidftlfix).then(function(message) {
							if (message) {
								msg += "\n" + message;
							}
							return msg;
						});
					}
				}
			}
			return Promise.resolve(msg || "");
		}

		/**
		 * One step before the exit point (which is "saveImage") do some checks before actually saving.
		 * @param callbacks
		 */
		function checkThenSave(callbacks) {
			if (imageEdit.getFbImage()) {
				if (callbacks.validate) {
					// showHideOverlay(fbCanvas);  // This hide is for the validation, not the save.
					callbacks.validate().then(function(validationResult) {
						var error, imageToSave;
						if (validationResult) {
							error = validationResult.error;
							imageToSave = validationResult.validated;
						}
						if (error) {
							// showHideOverlay(fbCanvas, true);  // Unhide the overlay post validation (save will have to hide it again).
							if (validationResult.ignorable) {
								prompt.confirm(error, function(ignoreValidationError) {
									if (ignoreValidationError) {
										callbacks.saveFunc(imageToSave);
									} else {
										callbacks.lose();
									}
								});
							} else {
								if (validationResult.prompt) {
									prompt.alert(error);
								}
								callbacks.lose(error);
							}
						} else {
							callbacks.saveFunc(imageToSave);
						}

					}, function() {
						callbacks.lose();
					});
				} else {
					callbacks.saveFunc();
				}
			} else {
				// we should only be here if the user has not taken a snapshot from the video stream
				i18n.translate("imgedit_noimage").then(function(message) {
					prompt.alert(message);
				});
			}
		}

		/**
		 * The exit point of the editor, either save or cancel the edit.
		 * @param args Args required for the save, see below.
		 *	param {Element} args.editor The top level container element of the editor component.
		 *	param {Object} args.callbacks "win" and "lose".
		 *	param {boolean} args.cancel Cease all editing, the user wishes to cancel.
		 *	param {File} [args.originalImage] The binary originalImage being edited.
		 *	param [args.imageToSave] The image formatted for saving. This is a performance optimization: if the image
		 *    has already been formatted for saving during validation etc then it can be passed thru here to save having to format again.
		 */
		function saveImage(args) {
			var result,
				editor = args.editor,
				callbacks = args.callbacks,
				done = function() {
					fbCanvas = null;  // = canvasElement
					imageCapture.stop();
					editor.parentNode.removeChild(editor);
				};
			try {
				if (args.cancel) {
					done();
					callbacks.lose();
				} else {
					if (args.imageToSave) {
						result = args.imageToSave;
					} else {
						result = getImageToSave(editor, args.originalImage, callbacks.formatForSave);
					}
					done();
					callbacks.win(result);
				}
			} finally {
//				dialogFrame.close();
				dialogFrame.resetContent();
			}
		}

		/**
		 * Before saving the image we may wish to discard any scaling the user has performed.
		 * This function removes scaling on the image and preserves relative ratios with other objects on the canvas.
		 * @param {fabric.Image} fbImage The image to un-scale.
		 */
		function unscale(fbImage) {
			var originaSize = fbImage.getOriginalSize(),
				objects = fbCanvas.getObjects().map(function(o) {
					return o.set("active", true);
				}),
				group = new fabric.Group(objects, {
					originX: "left",
					originY: "top"
				});
			fbCanvas.setActiveGroup(group.setCoords()).renderAll();
			group.scaleToWidth(originaSize.width);
			group.scaleToHeight(originaSize.height);
			return group;
		}

		/**
		 * Intended for synchronous upload, will add the edited image to the form as a base64 encoded data URL.
		 * The initiating file input will be disabled so that the base64 field can masquerade in its place.
		 * @param {Element} editor The file input associated with the image we are editing.
		 */
		function getCanvasAsDataUrl(editor) {
			var param, form, formWd, fileSelector, stateField, serialized = canvasToDataUrl();
			if (serialized) {
				fileSelector = getFileSelector(editor);
				if (fileSelector) {
					param = fileSelector.name;
					if (param) {
						formWd = new Widget("form");
						form = formWd.findAncestor(fileSelector);
						fileSelector.disabled = true;
						stateField = form.appendChild(document.createElement("input"));
						stateField.type = "hidden";
						stateField.name = param;
						stateField.value = serialized;
					}
				}
			}
			return stateField;
		}

		function getFileSelector(editor) {
			// TODO this doesn't seem right
			var result, editorId = editor.getAttribute("data-wc-editor");
			result = document.querySelector("input[type=file][data-wc-editor=" + editorId + "]");
			return result;
		}

		/**
		 * Get the image which is to be  saved.
		 * Note that if no edits have been made the original image may be used.
		 * @param {Element} editor The file input associated with the image we are editing.
		 * @param {Blob} originalImage The source image file which the user loaded into the editor.
		 * @param {Function} [renderer] The function to use to convert the image on the canvas to the desired save format.
		 * @returns The image (including any edits) in the format configured for saving.
		 */
		function getImageToSave(editor, originalImage, renderer) {
			var config = imageEdit.getConfig(editor), result, renderFunc = renderer || getCanvasAsFile;
			if (originalImage && !hasChanged(config)) {
				console.log("No changes made, using original file");
				result = originalImage;  // if the user has made no changes simply pass thru the original file.
			} else {
				showHideOverlay(fbCanvas);
				result = renderFunc(editor, originalImage);
				showHideOverlay(fbCanvas, true);
			}
			return result;
		}

		/**
		 * Gets the edited image on the canvas as a binary file.
		 * @param {Element} editor The file input associated with the image we are editing.
		 * @param {Blob} originalImage The original image file being edited.
		 * @returns {File} The edited image as a file / blob.
		 */
		function getCanvasAsFile(editor, originalImage) {
			var result = canvasToDataUrl();
			if (result) {
				result = dataURItoBlob(result);
				result = blobToFile(result, originalImage);
			}
			return result;
		}

		/**
		 * Serialize the edited image on the canvas to a data url.
		 * @returns {string} The image on the canvas as a data url.
		 */
		function canvasToDataUrl() {
			var result, toDataUrlParams, config, object,
				fbImage = imageEdit.getFbImage();
			if (fbImage) {
				config = imageEdit.getConfig();
				if (config.crop) {
					object = fbImage;
					toDataUrlParams = {
						left: 0,
						top: 0,
						width: Math.min(fbCanvas.getWidth(), object.getWidth()),
						height: Math.min(fbCanvas.getHeight(), object.getHeight())
					};
				} else {
					object = unscale(fbImage);
					toDataUrlParams = {
						left: object.getLeft(),
						top: object.getTop(),
						width: object.getWidth(),
						height: object.getHeight()
					};
				}
				// Add params such as format, quality, multiplier etc
				toDataUrlParams = mixin(toDataUrlParams, config);

				// canvasElement = fbCanvas.getElement();
				// result = canvasElement.toDataURL();
				result = fbCanvas.toDataURL(toDataUrlParams);
			}
			return result;
		}

		/**
		 * Determine if there are changes to the image in the editor.
		 * @param {Object} config Map of configuration properties.
		 * @returns {boolean} true if there are changes to be saved.
		 */
		function hasChanged(config) {
			var result, fbImage;
			if (undoRedo) {
				result = undoRedo._forceChange || undoRedo.hasChanges();
			}
			if (config && !result) {
				fbImage = imageEdit.getFbImage();
				if (fbImage && config.crop) {
					// When the image is initially loaded it is scaled to fit. If "crop" is true this will NOT be undone on save and should be considered an "edit".
					result = fbImage.scaleX !== 1 || fbImage.scaleY !== 1;  // Note that this check problably makes autoresize redundant in most cases
					if (result) {
						console.log("Image has been automatically scaled");
					}
				}
			}
			return result;
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
				canvas.width = element.naturalWidth * scale;
				canvas.height = element.naturalHeight * scale;
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
				// noinspection JSAnnotator
				blob.type = filePropertyBag.type;
			}
			blob.lastModifiedDate = filePropertyBag.lastModified;
			blob.lastModified = filePropertyBag.lastModified.getTime();
			blob.name = name;
			checkFileExtension(blob);
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
			} else {
				byteString = unescape(dataURI.split(",")[1]);
			}

			// separate out the mime component
			mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

			// write the bytes of the string to a typed array
			ia = new window.Uint8Array(byteString.length);
			for (i = 0; i < byteString.length; i++) {
				ia[i] = byteString.charCodeAt(i);
			}

			return new Blob([ia], { type: mimeString });
		}

		/**
		 * Ensures that the file name ends with an extension that matches its mime type.
		 * @param file The file to check.
		 */
		function checkFileExtension(file) {
			var expectedExtension,
				info = getMimeType({
					files: [file]
				});
			if (info && info.length) {
				info = info[0];
				expectedExtension = imageEdit.mimeToExt[info.mime];
				if (info.mime && expectedExtension) {
					if (expectedExtension.indexOf(info.ext) < 0) {
						file.name += "." + expectedExtension[0];
					}
				}
			}
		}
	}
	return imageEdit;
});
