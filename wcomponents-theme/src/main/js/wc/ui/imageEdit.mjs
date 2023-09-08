import mixin from "wc/mixin";
import wcconfig from "wc/config";
import event from "wc/dom/event";
import timers from "wc/timers";
import prompt from "wc/ui/prompt";
import i18n from "wc/i18n/i18n";
import fabric from "lib/fabric";
import dialogFrame from "wc/ui/dialogFrame";
import ImageCapture from "wc/ui/ImageCapture";
import ImageUndoRedo from "wc/ui/ImageUndoRedo";
import fileSize from "wc/file/size";
import fileUtil from "wc/file/util";

let timer, imageCapture;
let inited,
	overlayUrl,
	undoRedo,
	fbCanvas;
const registeredIds = {},
	BUTTON = { findAncestor: el => /** @type {HTMLButtonElement} */(el?.closest("button")) };


/**
 * This provides a mechanism to allow the user to edit images during the upload process.
 * It may also be used to edit static images after they have been uploaded as long as a file uploader is configured to take the edited images.
 */
const imageEdit = {
	renderCanvas: function(callback) {
		if (timer) {
			timers.clearTimeout(timer);
		}
		timer = timers.setTimeout(() => {
			fbCanvas = imageEdit.getCanvas();
			fbCanvas.renderAll();
			if (callback) {
				callback();
			}
		}, 50);
	},

	defaults: {
		maxsize: 20971520,  // limit the size, in bytes, of an image that can be loaded in the image editor (so the page does not hang)
		width: 320,
		height: 240,
		format: "png",  // png or jpeg
		quality: 1,  // only if format is jpeg
		multiplier: 1,
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
	},

	getCanvas: () => fbCanvas,

	/**
	 * Registers a configuration object against a unique ID to specify variables such as overlay image URL, width, height etc.
	 *
	 * @param {Object[]} arr Configuration objects.
	 */
	register: function(arr) {
		let inline = arr.filter(next => {
			registeredIds[next.id] = next;  // yes, the filter has a side effect
			return next.inline;
		});
		if (!inited) {
			inited = true;
			// @ts-ignore
			require(["wc/dom/initialise", "wc/dom/formUpdateManager"], (initialise, formUpdateManager) => {
				initialise.addCallback((element) => {
					event.add(element, "click", clickEvent);
					handleInline(inline);
					inline = null;
				});
				formUpdateManager.subscribe(imageEdit);
			});
		}
	},

	/**
	 * Shares a method signature with multifileuploader upload (which overrides this method).
	 */
	upload: () => {},

	/**
	 * Will be overriden in some circumstances.
	 */
	writeState: () => {},

	/**
	 * Retrieve a configuration object.
	 * @param {Object} obj Get the configuration registered for the "id" or "name" property of this object (in that order).
	 * @returns {Object} configuration
	 */
	getConfig: function(obj) {
		let result = wcconfig.get("wc/ui/imageEdit", this.defaults);
		let instanceConfig = registeredIds[obj.id] || registeredIds[obj.name];
		if (obj) {
			if (!instanceConfig) {
				let editorId = ("getAttribute" in obj) ? obj.getAttribute("data-wc-editor") : obj.editorId;
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
	},

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
	editFiles: function(obj, onSuccess, onError) {
		const config = imageEdit.getConfig(obj);
		let sizes, idx = 0;
		const result = [], files = obj.files;
		const done = arg => {
			try {
				onSuccess(arg);
			} finally {
				dialogFrame.close();
			}
		};
		try {
			if (files) {
				sizes = fileSize.get(obj);
				editNextFile();
			} else if (config.camera) {
				editFile(config, null, saveEditedFile, onError);
			}
		} catch (ex) {
			onError(ex);
		}

		/*
		 * Once the user has committed their changes buffer the result and see if there is another file queued for editing.
		 */
		function saveEditedFile(fileToSave) {
			result.push(fileToSave);
			editNextFile();
		}

		/*
		 * Prompt the user to edit the next file in the queue.
		 */
		function editNextFile() {
			if (files && idx < files.length) {
				let size = sizes[idx];
				let file = files[idx++];
				if (typeof file === "string") {
					if (file.startsWith("data:image/")) {
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
	},

	/**
	 * Displays an img element in the image editor.
	 * @param {Element|string} img An image element or a dataURL.
	 * @param {function} [callback]
	 */
	renderImage: function(img, callback) {
		const width = fbCanvas.getWidth(),
			height = fbCanvas.getHeight();
		try {
			if (img instanceof HTMLImageElement) {
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
			const imageWidth = fabricImage.getScaledWidth();
			const imageHeight = fabricImage.getScaledHeight();
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
	},

	selectAll: function() {
		// All objects on the canvas
		const objects = fbCanvas.getObjects().map(o => o.set('active', true));

		// Create active selection from the objects (ActiveSelection extends Group)
		const selection = new fabric.ActiveSelection(objects, {
			canvas: fbCanvas,
			originX: 'center',
			originY: 'center'
		});

		fbCanvas._activeObject = null;
		fbCanvas.setActiveObject(selection);
		fbCanvas.renderAll();

		return selection;
	},

	getFbImage: function(container) {
		const currentContainer = (container || fbCanvas);
		if (currentContainer?.getObjects) {
			let objects = currentContainer.getObjects("image");
			if (objects?.length) {
				return objects[0];
			}
			objects = currentContainer.getObjects("group");
			if (objects?.length) {
				return objects[0];
			}
			// for (i = 0; i < objects.length; i++) {
			//	next = objects[i];
			//	result = imageEdit.getFbImage(next);
			//	if (result) {
			//		return result;
			//	}
			// }
		}
		return null;
	}
};



function addToCanvas(object) {
	fbCanvas.add(object);
}

/**
 * Allows for lazy instantiation of ImageCapture.
 * @returns {ImageCapture} Instance of ImageCapture.
 */
function getImageCapture() {
	if (!imageCapture) {
		imageCapture = new ImageCapture(imageEdit);
	}
	return imageCapture;
}


/**
 * Listens for edit requests on static images.
 * @param {MouseEvent & { target: HTMLElement }} $event A click event.
 */
function clickEvent($event) {
	const element = BUTTON.findAncestor($event.target);
	if (element) {
		const id = element.getAttribute("data-wc-selector");
		if (id && element.localName === "button") {
			const uploader = document.getElementById(id);
			if (uploader) {
				const img = /** @type {HTMLImageElement} */(document.getElementById(element.getAttribute("data-wc-img")));
				if (img) {
					const file = imgToFile(img);
					imageEdit.upload(uploader, [file]);
				} else {
					const win = files => imageEdit.upload(uploader, files, true);
					const lose = message => message ? prompt.alert(message) : '';
					imageEdit.editFiles({
						id: id,
						name: element.getAttribute("data-wc-editor")
					}, win, lose);
				}
			}
		}
	}
}

/**
 * Callback is called with the edited image when editing completed.
 * @param {Object} config Options for the image editor
 * @param {File|string} file The image to edit.
 * @param {function} win callback on success (passed a File)
 * @param {function} lose callback on error
 */
function editFile(config, file, win, lose) {
	const callbacks = {
			win: win,
			lose: lose
		},
		gotEditor = function(editor) {
			fbCanvas = new fabric.Canvas("wc_img_canvas", {
				enableRetinaScaling: false
			});
			fbCanvas.setWidth(config.width);
			fbCanvas.setHeight(config.height);
			overlayUrl = config.overlay;
			if (typeof file === "string") {
				imageEdit.renderImage(file);
			} else if (file) {
				const fileReader = new FileReader();
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
				getImageCapture().play({
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
	if (config.redact) {
		// @ts-ignore
		require(["wc/ui/imageRedact"], function(imageRedact) {
			config.redactor = imageRedact;
			getEditor(config, callbacks, file).then(function(editor) {
				gotEditor(editor);
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
 * This probably only makes sense with a single inline editor, but it's written to handle an array so as not to be limited by my imagination.
 * @param inline An array of editors that are configured to be inline.
 */
function handleInline(inline) {
	const count = inline.length;
	if (count > 0) {
		inline.forEach(config => {
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
	const minScaleDefault = 0.1,
		minWidth = availWidth * 0.7,
		minHeight = availHeight * 0.7;
	const minScaleX = (imgWidth > minWidth) ? minWidth / imgWidth : minScaleDefault;
	const minScaleY = (imgHeight > minHeight) ? minHeight / imgHeight : minScaleDefault;

	let result = Math.max(minScaleX, minScaleY);
	if (fbImage.scaleX || fbImage.scaleY) {
		// if the image has been auto-scaled already then we should allow it to stay in those parameters
		result = Math.min(fbImage.scaleX, fbImage.scaleY, result);
	}
	return result;
}

/**
 * Ensures that the overlay image is correctly positioned.
 * The overlay MUST be the correct aspect ratio.
 * @private
 * @function
 */
function positionOverlay() {
	const overlay = fbCanvas.overlayImage,
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
	const overlay = fabricCanvas.overlayImage;
	if (overlay) {
		fabricCanvas.overlayImage.visible = !!show;
		fabricCanvas.renderAll();
	}
}

function getDialogFrameConfig(onclose) {
	return i18n.translate("imgedit_title").then(title => {
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
		const contentContainer = document.getElementById(config.id);
		if (contentContainer) {
			return Promise.resolve(callbacks.render(contentContainer));
		}
		return Promise.reject(new Error(`Can not find element ${config.id}`));
	}
	return getDialogFrameConfig(() => {
		getImageCapture().stop();
		callbacks.lose();
	}).then(dialogConfig => {
		callbacks.rendered = () => dialogFrame.reposition();
		if (dialogFrame.isOpen()) {
			return callbacks.render(dialogFrame.getContent());
		}
		return dialogFrame.open(dialogConfig).then(() => callbacks.render(dialogFrame.getContent()));
	});
}

/**
 * Builds the editor DOM and displays it to the user.
 * @param {Object} config Map of configuration properties.
 * @param {Object} callbacks An object with two callbacks: "win" and "lose".
 * @param {File} file The file being edited.
 * @returns {Promise<Element>} Resolved with the top level editor DOM element when it is ready.
 * @function
 * @private
 */
function getEditor(config, callbacks, file) {
	callbacks.render = renderEditor;

	/**
	 * @param {Element} contentContainer
	 * @returns {Promise<Element>}
	 */
	function renderEditor(contentContainer) {
		const container = document.body.appendChild(document.createElement("div")),
			editorProps = {
				style: {
					width: config.width,
					height: config.height,
					textclass: "wc-off",
					btnclass: "wc_btn_icon"
				},
				feature: {
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
				const actions = attachEventHandlers(cntnr);
				zoomControls(actions.events);
				moveControls(actions.events);
				resetControl(actions.events);
				cancelControl(actions.events, cntnr, callbacks);
				saveControl(actions.events, cntnr, callbacks, file);
				rotationControls(actions.events);
				if (config.redactor) {
					config.redactor.controls(actions.events, cntnr);
				}

				if (!file) {
					cntnr.classList.add("wc_camenable");
					cntnr.classList.add("wc_showcam");
					getImageCapture().snapshotControl(actions.events, cntnr);
				}

				if (contentContainer && cntnr) {
					contentContainer.innerHTML = "";
					contentContainer.appendChild(cntnr);
					if (callbacks.rendered) {
						callbacks.rendered(contentContainer);
					}
				}
				return cntnr;
			};
		return getTranslations(editorProps).then(() =>{
			container.className = "wc_img_editor";
			container.setAttribute("data-wc-editor", config.id);
			timers.setTimeout(() => {
				container.innerHTML = getDialogContent(editorProps);
				done(container);
			}, 0);
			return container;
		});
	}  // end "renderEditor"
	return getEditorContext(config, callbacks);
}

function getDialogContent(context) {
	const featureFilter = name => context.feature[name];
	return `
		<div class="wc-row">
			<div class="wc_img_editpane wc-column">
				<div class="wc_img_canvas">
					<canvas id="wc_img_canvas"></canvas>
				</div>
			</div>
			<div class="wc_img_cap wc-column" style="height: ${context.style.height}px">
				<div id="wc_img_video_container"></div>
				<button title="${context.imgedit_message_snap}" type="button" class="wc_btn_snap ${context.style.btnclass}" name="snap"><i aria-hidden="true" class="fa fa-camera"></i><span class="${context.style.textclass}">${context.imgedit_action_snap}</span></button>
			</div>
			<div class="wc_img_nocap wc-column">
				<p>${context.imgedit_message_nocapture}</p>
			</div>
			<div class="wc_img_controls wc-column">
				${controlsTemplate(context, ["rotate", "zoom", "move", "redact", "reset"].filter(featureFilter))}
				<div>
					${controlsTemplate(context, ["undo", "cancel", "save"].filter(featureFilter))}
				</div>
			</div>
		</div>`;
}

/**
 * @param context
 * @param {String[]} features
 */
function controlsTemplate(context, features) {
	const templates = {
		rotate: `
	<fieldset>
		<legend>${context.imgedit_rotate}</legend>
		<button title="${context.imgedit_message_rotate_left90}" type="button" class="wc_btn_anticlock90 ${context.style.btnclass}" name="anticlock90"><i aria-hidden="true" class="fa fa-step-backward"></i><span class="${context.style.textclass}">${context.imgedit_rotate_left90}</span></button>
		<button title="${context.imgedit_message_rotate_left}" type="button" class="wc_btn_anticlock ${context.style.btnclass}" name="anticlock"><i aria-hidden="true" class="fa fa-undo"></i><span class="${context.style.textclass}">${context.imgedit_rotate_left}</span></button>
		<button title="${context.imgedit_message_rotate_right}" type="button" class="wc_btn_clock ${context.style.btnclass}" name="clock"><i aria-hidden="true" class="fa fa-repeat"></i><span class="${context.style.textclass}">${context.imgedit_rotate_right}</span></button>
		<button title="${context.imgedit_message_rotate_right90}" type="button" class="wc_btn_clock90 ${context.style.btnclass}" name="clock90"><i aria-hidden="true" class="fa fa-step-forward"></i><span class="${context.style.textclass}">${context.imgedit_rotate_right90}</span></button>
	</fieldset>`,
		zoom: `
	<fieldset>
		<legend>${context.imgedit_zoom}</legend>
		<button title="${context.imgedit_message_zoom_out}" type="button" class="wc_btn_out ${context.style.btnclass}" name="out"><i aria-hidden="true" class="fa fa-search-minus"></i><span class="${context.style.textclass}">${context.imgedit_zoom_out}</span></button>
		<button title="${context.imgedit_message_zoom_in}" type="button" class="wc_btn_in ${context.style.btnclass}" name="in"><i aria-hidden="true" class="fa fa-search-plus"></i><span class="${context.style.textclass}">${context.imgedit_zoom_in}</span></button>
	</fieldset>`,
		move: `
	<fieldset class="wc_img_buttons">
		<legend>${context.imgedit_move}</legend>
		<button title="${context.imgedit_message_move_up}" type="button" class="wc_btn_up ${context.style.btnclass}" name="up"><i aria-hidden="true" class="fa fa-caret-up"></i><span class="${context.style.textclass}">${context.imgedit_move_up}</span></button>
		<button title="${context.imgedit_message_move_left}" type="button" class="wc_btn_left ${context.style.btnclass}" name="left"><i aria-hidden="true" class="fa fa-caret-left"></i><span class="${context.style.textclass}">${context.imgedit_move_left}</span></button>
		<button title="${context.imgedit_message_move_center}" type="button" class="wc_btn_center ${context.style.btnclass}" name="center"><i aria-hidden="true" class="fa fa-bullseye"></i><span class="${context.style.textclass}">${context.imgedit_move_center}</span></button>
		<button title="${context.imgedit_message_move_right}" type="button" class="wc_btn_right ${context.style.btnclass}" name="right"><i aria-hidden="true" class="fa fa-caret-right"></i><span class="${context.style.textclass}">${context.imgedit_move_right}</span></button>
		<button title="${context.imgedit_message_move_down}" type="button" class="wc_btn_down ${context.style.btnclass}" name="down"><i aria-hidden="true" class="fa fa-caret-down"></i><span class="${context.style.textclass}">${context.imgedit_move_down}</span></button>
	</fieldset>`,
		redact: `
	<fieldset>
		<legend>${context.imgedit_redact}</legend>
		<label>${context.imgedit_action_redact}
			<input title="${context.imgedit_message_redact}" type="checkbox" name="redact"/>
		</label>
	</fieldset>
`,
		capture: `
	<fieldset class="wc_img_capture">
		<legend>${context.imgedit_capture}</legend>
		<button title="${context.imgedit_message_camera}" type="button" class="wc_btn_camera ${context.style.btnclass}" name="camera"><i aria-hidden="true" class="fa fa-video-camera"></i><span class="${context.style.textclass}">${context.imgedit_action_camera}</span></button>
	</fieldset>`,
		reset: `
	<div>
		<button title="${context.imgedit_message_reset}" type="button" name="reset">${context.imgedit_action_reset}</button>
	</div>`,
		undo: `
	<button title="${context.imgedit_message_undo}" type="button" class="wc_btn_undo ${context.style.btnclass}" name="undo">
		<i aria-hidden="true" class="fa fa-reply"></i><span class="${context.style.textclass}">${context.imgedit_action_undo}</span>
	</button>
	<button title="${context.imgedit_message_redo}" type="button" class="wc_btn_redo ${context.style.btnclass}" name="redo">
		<i aria-hidden="true" class="fa fa-share"></i><span class="${context.style.textclass}">${context.imgedit_action_redo}</span>
	</button>`,
		cancel: `
	<button title="${context.imgedit_message_cancel}" type="button" class="wc_btn_cancel ${context.style.btnclass}" name="cancel">
		<i aria-hidden="true" class="fa fa-trash"></i><span class="${context.style.textclass}">${context.imgedit_action_cancel}</span>
	</button>`,
		save: `
	<button title="${context.imgedit_message_save}" type="button" class="wc_btn_save ${context.style.btnclass}" name="save">
		<i aria-hidden="true" class="fa fa-floppy-o"></i><span class="${context.style.textclass}">${context.imgedit_action_save}</span>
	</button>`
	};
	return features.map(feature => templates[feature]).join("\n");
}

function getTranslations(obj) {
	const messages = ["imgedit_action_camera", "imgedit_action_cancel", "imgedit_action_redact",
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
	return i18n.translate(messages).then(translations => {
		const result = obj || {};
		messages.forEach((message, idx) => {
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
	const MAX_SPEED = 10,
		MIN_SPEED = 0.5,
		START_SPEED = 1.5,
		eventConfig = {
			press: {},
			click: {}
		};
	let pressAction, eventTimer, speed = START_SPEED;
	event.add(container, "mousedown", pressStart);
	event.add(container, "touchstart", pressStart);
	event.add(container, "mouseout", pressEnd);
	event.add(container, "click", ($event) => {
		let target = $event.target;
		if (!invoke.call(this, target, "click", $event)) {
			target = BUTTON.findAncestor(target);
			invoke.call(this, target, "click", $event);
		}
	});
	event.add(document.body, "mouseup", pressEnd);
	event.add(document.body, "touchcancel", pressEnd);
	event.add(container, "touchend", pressEnd);

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
	 * This starts the poller that will continue to invoke the action as long as the control is pressed.
	 */
	function startPressPoller() {
		pressEnd();  // stop any previous poller
		eventTimer = timers.setInterval(() => {
			if (pressAction) {
				pressAction();
			}
		}, 100);
	}

	/**
	 * This handles the event fired when the user begins to press a control.
	 * @param {TouchEvent|MouseEvent} $event The press event.
	 */
	function pressStart({ target }) {
		const element = BUTTON.findAncestor(target),
			config = getEventConfig(element, "press");
		if (config) {
			pressEnd();
			startPressPoller();
			pressAction = () => {
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
		if (!action) {
			return null;
		}
		const isElement = (action instanceof Element && action.matches("button,[type='checkbox']"));
		const name = /** @type {string} */ (isElement ? action.getAttribute("name") : action);

		if (name && eventConfig[type]) {
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
		let result = false;
		const config = getEventConfig(action, type || "click");
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
	if (!step) {
		return currentValue;  // no step why are you calling me?
	}

	if (!currentValue) {  // start at 0
		return step;
	}

	if (currentValue % step === 0) {  // current value is already a multiple of step so everything is easy.
		return currentValue + step;
	}

	const interim = currentValue + step;  // this is a simple rotate by step, now we need to work out where we should be.
	return Math.floor(interim / step) * step;
}

/*
 * Helper for features that change numeric properties of the image on the canvas.
 */
function numericProp(config, speed) {
	const fbImage = imageEdit.getFbImage(),  // this could be a group, does it matter?
		step = config.step || 1; // do not allow step to be 0
	if (fbImage) {
		let newValue, currentValue;
		if (config.getter) {
			currentValue = fbImage[config.getter]();
		} else if (config.prop) {
			currentValue = fbImage[config.prop];
		}
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
		if (config.setter) {
			fbImage[config.setter](newValue);
		} else if (config.prop) {
			fbImage[config.prop] = newValue;
		}
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
	const press = eventConfig.press,
		click = eventConfig.click;
	press.up = {
		func: numericProp,
		prop: "top",
		step: -1
	};

	press.down = {
		func: numericProp,
		prop: "top",
		step: 1
	};

	press.left = {
		func: numericProp,
		prop: "left",
		step: -1
	};

	press.right = {
		func: numericProp,
		prop: "left",
		step: 1
	};

	click.center = {
		func: () => {
			const fbImage = imageEdit.getFbImage();
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
	const press = eventConfig.press;
	press.in = {
		func: numericProp,
		prop: "scaleX",
		setter: "scale",
		step: 0.05
	};

	press.out = {
		func: numericProp,
		prop: "scaleX",
		setter: "scale",
		step: -0.05,
		min: 0.1
	};
}

/*
 * Wires up the "rotation" feature.
 */
function rotationControls(eventConfig) {
	const press = eventConfig.press;
	press.clock = {
		func: numericProp,
		prop: "angle",
		setter: "rotate",
		step: 1
	};

	press.anticlock = {
		func: numericProp,
		prop: "angle",
		setter: "rotate",
		step: -1
	};

	const click = eventConfig.click;
	click.clock90 = {
		func: numericProp,
		prop: "angle",
		setter: "rotate",
		step: 90,
		exact: true
	};

	click.anticlock90 = {
		func: numericProp,
		prop: "angle",
		setter: "rotate",
		step: -90,
		exact: true
	};
}

/*
 * Wires up the "reset/undo/redo" feature.
 */
function resetControl(eventConfig) {
	const click = eventConfig.click;
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
	const click = eventConfig.click,
		cancelFunc = () =>{
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
	const click = eventConfig.click;
	/**
	 * The call into the save "internals" with the args specific to this closure.
	 * @param [imageToSave] The image formatted for saving. This is a performance optimization: if the image
	 *    has already been formatted for saving during validation etc then it can be passed thru here to save having to format again.
	 */
	callbacks.saveFunc = (imageToSave) => {
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
				const selector = getFileSelector(editor);
				if (selector) {
					const max = fileSize.getMax(selector);
					if (max) {
						const formattedImage = getImageToSave(null, file, callbacks.formatForSave);
						return validateImage(formattedImage, editor).then(message => {
							const config = imageEdit.getConfig(selector);
							return {
								validated: formattedImage,
								ignorable: config.ftlignore,
								error: message,
								prompt: config.invalidprompt
							};
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
	let msg;
	if (imageBlob?.size) {
		const selector = getFileSelector(editor);
		const config = imageEdit.getConfig(selector);
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
				return i18n.translate(config.msgidftlfix).then(message => {
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
				let error, imageToSave;
				if (validationResult) {
					error = validationResult.error;
					imageToSave = validationResult.validated;
				}
				if (error) {
					// showHideOverlay(fbCanvas, true);  // Unhide the overlay post validation (save will have to hide it again).
					if (validationResult.ignorable) {
						prompt.confirm(error, ignoreValidationError => {
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
	const editor = args.editor,
		callbacks = args.callbacks,
		done = function() {
			fbCanvas = null;  // = canvasElement
			getImageCapture().stop();
			editor.parentNode.removeChild(editor);
		};
	try {
		if (args.cancel) {
			done();
			callbacks.lose();
		} else {
			const result = args.imageToSave || getImageToSave(editor, args.originalImage, callbacks.formatForSave);
			done();
			callbacks.win(result);
		}
	} finally {
		// dialogFrame.close();
		dialogFrame.resetContent();
	}
}

/**
 * Before saving the image we may wish to discard any scaling the user has performed.
 * This function removes scaling on the image and preserves relative ratios with other objects on the canvas.
 * @param {fabric.Image} fbImage The image to un-scale.
 */
function unscale(fbImage) {
	// Original size of image
	const originaSize = fbImage.getOriginalSize();

	// All objects on the canvas
	const objects = fbCanvas.getObjects().map(function(o) {
		return o.set("active", true);
	});

	// Create active selection from the objects (ActiveSelection extends Group)
	const selection = new fabric.ActiveSelection(objects, {
		canvas: fbCanvas,
		originX: "left",
		originY: "top"
	});

	selection.scaleToWidth(originaSize.width);
	selection.scaleToHeight(originaSize.height);

	fbCanvas._activeObject = null;
	fbCanvas.setActiveObject(selection);
	fbCanvas.renderAll();

	return selection;
}

/**
 * Intended for synchronous upload, will add the edited image to the form as a base64 encoded data URL.
 * The initiating file input will be disabled so that the base64 field can masquerade in its place.
 * @param {Element} editor The file input associated with the image we are editing.
 */
function getCanvasAsDataUrl(editor) {
	const serialized = canvasToDataUrl();
	if (serialized) {
		const fileSelector = getFileSelector(editor);
		if (fileSelector) {
			const param = fileSelector.name;
			if (param) {
				const form = fileSelector.form || fileSelector.closest("form");
				fileSelector.disabled = true;
				const stateField = form.appendChild(document.createElement("input"));
				stateField.type = "hidden";
				stateField.name = param;
				stateField.value = serialized;
				return stateField;
			}
		}
	}
	return null;
}

/**
 *
 * @param {Element} editor
 * @returns {HTMLInputElement}
 */
function getFileSelector(editor) {
	// TODO this doesn't seem right
	const editorId = editor.getAttribute("data-wc-editor");
	return /** @type {HTMLInputElement} */(document.querySelector(`input[type=file][data-wc-editor='${editorId}']`));
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
	const config = imageEdit.getConfig(editor), renderFunc = renderer || getCanvasAsFile;
	if (originalImage && !hasChanged(config)) {
		console.log("No changes made, using original file");
		return originalImage;  // if the user has made no changes simply pass thru the original file.
	}
	showHideOverlay(fbCanvas);
	const result = renderFunc(editor, originalImage);
	showHideOverlay(fbCanvas, true);
	return result;
}

/**
 * Gets the edited image on the canvas as a binary file.
 * @param {Element} editor The file input associated with the image we are editing.
 * @param {Blob} originalImage The original image file being edited.
 * @returns {File} The edited image as a file / blob.
 */
function getCanvasAsFile(editor, originalImage) {
	const dataUrl = canvasToDataUrl();
	if (dataUrl) {
		let result = fileUtil.blobToFile(fileUtil.dataURItoBlob(dataUrl), originalImage);
		result = fileUtil.fixFileExtension(result);
		return result;
	}
	return null;
}

/**
 * Serialize the edited image on the canvas to a data url.
 * @returns {string} The image on the canvas as a data url.
 */
function canvasToDataUrl() {
	let result, toDataUrlParams, object;
	const fbImage = imageEdit.getFbImage();
	if (fbImage) {
		const config = imageEdit.getConfig();
		if (config.crop) {
			object = fbImage;
			toDataUrlParams = {
				left: 0,
				top: 0,
				width: Math.min(fbCanvas.getWidth(), object.getScaledWidth()),
				height: Math.min(fbCanvas.getHeight(), object.getScaledHeight())
			};
		} else {
			object = unscale(fbImage);
			toDataUrlParams = {
				left: object.get('left'),
				top: object.get('top'),
				width: object.getScaledWidth(),
				height: object.getScaledHeight()
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
	let result;
	if (undoRedo) {
		result = undoRedo._forceChange || undoRedo.hasChanges();
	}
	if (config && !result) {
		const fbImage = imageEdit.getFbImage();
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
 * @param {HTMLImageElement} element An img element.
 * @returns {File} The image as a binary File.
 */
function imgToFile(element) {
	const scale = 1,
		canvas = document.createElement("canvas"),
		config = {
			name: element.id
		};
	/** @type File */
	let file;
	if (element?.src) {
		canvas.width = element.naturalWidth * scale;
		canvas.height = element.naturalHeight * scale;
		const context = canvas.getContext("2d");
		context.drawImage(element, 0, 0);
		const dataUrl = canvas.toDataURL("image/png");
		const blob = fileUtil.dataURItoBlob(dataUrl);
		file = fileUtil.blobToFile(blob, config);
		file = fileUtil.fixFileExtension(file);
	}
	return file;
}

export default imageEdit;
