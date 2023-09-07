import prefetch from "wc/loader/prefetch";
import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import uid from "wc/dom/uid";
import Trigger from "wc/ajax/Trigger";
import clearSelector from "wc/file/clearSelector";
import validate from "wc/file/validate";
import i18n from "wc/i18n/i18n";
import formUpdateManager from "wc/dom/formUpdateManager";
import filedrop from "wc/file/filedrop";
import ajax from "wc/ajax/ajax";
import prompt from "wc/ui/prompt";
import focus from "wc/dom/focus";
import isNumeric from "wc/isNumeric";
import ajaxRegion from "wc/ui/ajaxRegion";
import wcconfig from "wc/config";
import debounce from "wc/debounce";
import toDocFragment from "wc/dom/toDocFragment";
import feedback from "wc/ui/feedback";
import icon from "wc/ui/icon";
import "wc/ui/fieldset";
// Note `wc/ui/fieldset` is implicitly required to handle various aspects of managing the wrapper element.
// TODO rework the whole AJAX part of this
const
	/**
	 * Provides functionality associated with uploading multiple files using a WMultiFileWidget.
	 *
	 * @module
	 */
	instance = new MultiFileUploader(),
	changed = {},
	CLASS_NAME = "wc-multifileupload",
	COL_ATTR = "data-wc-cols",
	MAX_FILES_ATTR = "data-wc-maxfiles",
	CLASS_NO_BULLET = "wc_list_nb",
	CLASS_WRAPPER = "wc_files",
	CLASS_FILE_INFO = "wc-file",
	CLASS_FILE_LIST = "wc_filelist",
	CLASS_AJAX_UPLOADER = "wc-ajax",
	containerWd = `.${CLASS_NAME}`,
	inputElementWd = `${containerWd} input[type='file']`,
	fileInfoContainerWd = `ul.${CLASS_FILE_LIST}`,
	fileInfoItem = "li",
	fileInfoWd = `${containerWd} ${fileInfoItem}.${CLASS_FILE_INFO}`,
	itemActivationWd = "a",
	removeButtonWd = `${fileInfoWd} button`,
	filesWrapperWd = `div.${CLASS_WRAPPER}`,
	cameraButtonWd = `${containerWd} button.wc_btn_camera`,
	inflightXhrs = {},
	progressWd = `${fileInfoWd} progress`;

/**
 * @typedef {Object} module:wc/file/MultiFileUploader~fileInfo
 * @property {string} url The URL to upload the file to.
 * @property {Element} element The name of the file input.
 * @property {Element} container The multiFileWidget wrapper element.
 * @property {Function} callback The function to call with the response of each file upload.
 * @property {Function} complete The function to call when all files have been uploaded.
 * @property {File[]} files A collection of files where each file implements the File interface http://www.w3.org/TR/FileAPI/#dfn-file.
 */

/*
 * An asynchronous file uploader which allows multiple file selection via file selector or
 * drag and drop (some features are NOT polyfilled but we offer anyone a new browser free of charge).
 *
 * Once the upload is complete the server should place the file in a temporary location
 * until the page is submitted.  The user may have deselected some (or all) of the files
 * uploaded - the server must check and honor these selections.
 */

/**
 * @constructor
 * @alias module:wc/ui/multiFileUpload~MultiFileUploader
 * @private
 */
function MultiFileUploader() {
	const INITED_KEY = "wc/ui/multiFileUploader.inited";
	let uploader;

	prefetch.jsModule("wc/ui/imageEdit");

	/**
	 * Change event on the file input.
	 * Somebody wants to upload a file...
	 * @function
	 * @private
	 * @param {UIEvent & {target: HTMLInputElement}} $event The change event.
	 */
	function changeEvent($event) {
		const { target, defaultPrevented } = $event;
		if (!defaultPrevented && target.matches(inputElementWd)) {
			checkDoUpload(target, null);
		}
	}

	/**
	 * Call when files are added or removed as this is the definition of a change for this component.
	 * @param {string} id The id of a multiFileWidget.
	 */
	function filesChanged(id) {
		const element = document.getElementById(id);
		if (element && ajaxRegion.getTrigger(element, true)) {
			ajaxRegion.requestLoad(element, null, true);
		}
	}

	/**
	 * Click event in the fileupload widget.
	 * Handle things like removing an attachment.
	 * @function
	 * @private
	 * @param {MouseEvent & { target: HTMLElement } } $event The click event.
	 */
	function clickEvent($event) {
		const { defaultPrevented, target } = $event;
		if (defaultPrevented) {
			bootStrap($event);  // increasingly browsers do not focus some elements when they are clicked (traditionally webkit only did this) - I'm looking at you FireFox.
			return;
		}
		const element = target.closest("button") || target;
		initialiseFileInput(element);
		const fileInfo = element.closest(fileInfoWd);
		if (fileInfo) {
			if (element.matches(removeButtonWd)) {
				const proceed = prompt.confirm(i18n.get("file_confirmdelete"));
				if (proceed) {
					removeFileItem(fileInfo);
				}
				return;
			}
			const container = fileInfo.closest(containerWd);
			if (container?.classList.contains(CLASS_AJAX_UPLOADER)) {
				const trigger = element.closest(itemActivationWd);
				if (trigger) {
					// trigger.removeAttribute("target");
					trigger.setAttribute("data-wc-params", `wc_fileid=${encodeURIComponent(fileInfo.id)}`);
					console.log("wc_fileid", fileInfo.id);
				}
			}
		} else if (element.matches(cameraButtonWd)) {
			// @ts-ignore
			require(["wc/ui/imageEdit"], function (imageEdit) {
				if (imageEdit.upload !== instance.upload) {
					imageEdit.upload = instance.upload;
				}
			});
		}
	}

	/**
	 * @param {Element} fileInfo
	 */
	function removeFileItem(fileInfo) {
		const container = fileInfo.closest(containerWd);
		if (container) {
			fileInfo.parentNode.removeChild(fileInfo);
			let xhr;
			if (inflightXhrs.hasOwnProperty(fileInfo.id) && (xhr = inflightXhrs[fileInfo.id])) {
				if (xhr.abort) {
					xhr.abort();
				}
			}
			changed[container.id] = changed[container.id] || debounce(/** @param {string} id */id => {
				filesChanged(id);
				reflowFileItemsAfterRemove(id);
				delete changed[id];
			}, 300);
			changed[container.id](container.id);
		}
	}

	/**
	 * @param {string} id
	 */
	function reflowFileItemsAfterRemove(id) {
		const container = document.getElementById(id);
		const cols = container?.hasAttribute(COL_ATTR) ? Number(container.getAttribute(COL_ATTR)) : 0;
		if (cols && isNumeric(cols) && cols > 1) {
			// cols 0 and cols 1 are handled as a single list
			const itemContainers = getColumns(container);
			const itemContainerCount = itemContainers.length;
			if (itemContainerCount > 1) {
				// We only care if there is more than one UL therefore testing "greater than one"
				const items = container.querySelectorAll(fileInfoWd);
				for (let i = 0; i < items.length; i++) {
					let itemContainer = itemContainers[i % cols];
					itemContainer.appendChild(items[i]);
				}
			}
		}
	}

	/**
	 * This allows other code to request an async file upload using a WMultiFileWidget.
	 * For example file dropzones.
	 * @param {Element} element A file input or an element that contains a file input.
	 * @param {File[]} files Binary file data.
	 * @param {boolean} [suppressEdit] true if image editing should be bypassed regardless of whether it is configured or not.
	 */
	this.upload = function (element, files, suppressEdit) {
		const input = /** @type {HTMLInputElement} */ (
			element.matches(inputElementWd) ? element : element.querySelector(inputElementWd));
		if (input) {
			/*
			 * The focus is primarily necessary to bootstrap the file widget.
			 * This is critical if the file widget is needs to be wired up by
			 * other controllers such as ajax trigger.
			 */
			focus.setFocusRequest(input, () => checkDoUpload(input, files, suppressEdit));
		}
	};

	/**
	 * Validate the file chosen and commence the asynchronous upload if all is well.
	 * @function
	 * @private
	 * @param {HTMLInputElement} element A file input element.
	 * @param {File[]} [files] A collection of File items to use instead of element.files.
	 * @param {boolean} [suppressEdit] true if image editing should be bypassed regardless of whether it is configured or not.
	 */
	function checkDoUpload(element, files, suppressEdit) {
		const useFilesArg = (!element.value && (files && files.length > 0)),
			done = () => instance.clearInput(element);

		getUploader(function (theUploader) {  // this wraps the possible async wait for the fauxjax module to load, otherwise clearInput has been called before the upload begins
			const checkAndUpload = useTheseFiles => {
					validate.check({
						selector: element,
						files: useTheseFiles,
						notify: true,
						callback: selector => {
							try {
								if (selector.matches(inputElementWd)) {
									commenceUpload({
										uploader: theUploader,
										element: selector,
										files: useTheseFiles
									});
								}
							} finally {
								done();
							}
						},
						errback: done
					});
				},
				upload = function(obj) {
					const editorId = element.getAttribute("data-wc-editor");
					if (!suppressEdit && editorId) {
						// @ts-ignore
						require(["wc/ui/imageEdit"], function (imageEdit) {
							obj.editorId = editorId;
							if (imageEdit.upload !== instance.upload) {
								imageEdit.upload = instance.upload;
							}
							imageEdit.editFiles(obj, checkAndUpload, done);
						});
					} else {
						checkAndUpload(obj.files);
					}
				};
			if (element.value || useFilesArg) {
				const testObj = useFilesArg ? {files: files, name: element.name, value: element.value, accept: element.accept} : element;
				const filesToAdd = (testObj.files ? testObj.files.length : 1);
				const maxFileInfo = checkMaxFiles(element, filesToAdd);
				if (maxFileInfo.valid) {
					upload(testObj);
				} else {
					done();
					prompt.alert(i18n.get("file_toomany", filesToAdd, maxFileInfo.max, maxFileInfo.before));
				}
			}
		});
	}

	/**
	 * Checks if the maxFiles count will be exceeded if we proceed with the upload
	 * @param {HTMLInputElement} element The DOM element responsible for the upload
	 * @param {Number} newFileCount The number of files being added
	 * @returns {Object} the property 'valid' will be false if the maxFiles count will be exceeded
	 */
	function checkMaxFiles(element, newFileCount) {
		let currentFiles;
		const config = wcconfig.get("wc/ui/multiFileUploader", {
				overwrite: false
			}),
			result = {
				valid: true,
				max: 0,
				before: 0,
				after: 0,
				removed: 0
			},
			fix = (resObj) => {
				/*
					This function implements some pretty dangerous behavior: it will enforce the max file limit
					by removing already uploaded files to make way for new ones.
				 */
				const removeCount = resObj.after - resObj.max;  // this is how many we need to remove
				for (let i = 0; i < removeCount; i++) {
					removeFileItem(currentFiles[i]);
					resObj.removed++;
				}
				resObj.after -= resObj.removed;
			};
		if (newFileCount) {
			result.max = getMaxFiles(element);
			if (result.max) {
				const container = element.closest(containerWd);
				if (container) {
					currentFiles = container.querySelectorAll(fileInfoWd);
					result.before = currentFiles.length;
					result.after = result.before + newFileCount;
					if (result.after > result.max) {
						if (config.overwrite && newFileCount <= result.max) {
							const message = i18n.get("file_confirmoverwrite", newFileCount, result.max, result.before);
							if (message) {
								if (prompt.confirm(message)) {
									fix(result);
								}
							} else {
								fix(result);
							}
						}
						result.valid = (result.after <= result.max);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets the "max files" constraint for this file input.
	 * Sets a limit for the number of files this file selector should allow.
	 * @param {HTMLInputElement} element A file input.
	 * @returns {Number} The max files constraint if set, otherwise 0.
	 */
	function getMaxFiles(element) {
		if (element) {
			if (element.hasAttribute(MAX_FILES_ATTR)) {
				const maxFiles = element.getAttribute(MAX_FILES_ATTR);
				if (isNumeric(maxFiles)) {
					return Math.max(Number(maxFiles), 0);
				}
			}
		}
		return 0;
	}

	/**
	 * Upload the file asynchronously now.
	 * @function
	 * @private
	 * @param {Object} config an object with the following properties:
	 *    {HTMLInputElement} element A file input element.
	 *    {Function} callback A function that will be called if and when all of the files are uploaded correctly
	 *    {File[]} [files] A collection of File items to use instead of element.files.
	 */
	function commenceUpload(config) {
		const element = config.element,
			files = (config.files || element.files || []),
			container = element.closest(containerWd),
			url = getUploadUrl(element),
			request = {
				container: container,
				callback: processResponse,
				complete: filesChanged,
				element: element,
				files: files,
				url: url
			};
		if (container) {
			config.uploader.request(request);
		}
	}

	function getUploader(callback) {
		if (!uploader) {
			uploader = new TrueAjax();
			callback(uploader);
		} else {
			callback(uploader);
		}
	}

	/**
	 * Callback which will be invoked when the server responds to an asynchronous file upload.
	 * Note that this is called for EACH uploaded file.
	 * @function
	 * @private
	 * @param {Element} response An HTML element which contains the content to display in the list of uploaded files.
	 */
	function processResponse(response) {
		const newFiles = response.querySelectorAll(fileInfoItem);
		if (newFiles.length > 0) {
			for (let i = 0; i < newFiles.length; i++) {
				updateFileInfo(newFiles[i]);
			}
		} else {
			throw new Error("Unexpected fileupload response");
		}
	}

	/**
	 *
	 * @param {Element} newFile
	 */
	function updateFileInfo(newFile) {
		const fileId = newFile.getAttribute("id"),
			oldFile = document.getElementById(fileId);
		delete inflightXhrs[fileId];
		if (oldFile) {
			oldFile.innerHTML = newFile.innerHTML;
			// oldFile.parentNode.replaceChild(newFile, oldFile);  // Problems with importing node
			return;
		}
		const containerId = newFile.getAttribute("data-wc-containerid");
		const container = document.getElementById(containerId);
		if (container) {
			// This is an extreme edge case - if the fileWidget UI has been replaced during upload attempt to recover
			container.insertAdjacentHTML("beforeend", newFile.outerHTML);
			return;
		}
		console.warn("Could not find", fileId);
	}

	/**
	 * Handles the event/s that trigger bootstrapping of this widget.
	 * @param {Event & { target: HTMLElement }} $event The event that triggers bootstrapping.
	 */
	function bootStrap({ target }) {
		initialiseFileInput(target);
	}

	/**
	 * Set up a file selector on first use.
	 * @param {HTMLElement} element A file input.
	 */
	function initialiseFileInput(element) {
		if (element.matches(inputElementWd)) {
			const inited = element[INITED_KEY];
			if (!inited && element instanceof HTMLInputElement) {
				console.log("Initialising on first use", element.name);
				element[INITED_KEY] =  true;
				initialiseForm(element.form);
			}
		}
	}

	/**
	 * Initialise the form that contains the multifile widget.
	 * @function
	 * @private
	 * @param {HTMLFormElement} form The form to initialise.
	 */
	function initialiseForm(form) {
		const inited = form[INITED_KEY];
		if (!inited) {
			form[INITED_KEY] = true;
			if (!form.classList.contains(CLASS_NAME)) {
				event.add(form, "submit", submitEvent, -50);
				event.add(form, "change", changeEvent);
			}
		}
	}

	/**
	 * Listen to submit events on the page that contains the file widgets.
	 * If the user tries to submit the form while there are uploads pending we should warn them
	 * that it will cancel their uploads.
	 * @param {SubmitEvent} $event The submit event.
	 */
	function submitEvent($event) {
		if (!$event.defaultPrevented && uploader && uploader.getUploading() > 0) {
			const proceed = prompt.confirm(i18n.get("file_confirmnav"));
			if (!proceed) {
				$event.preventDefault();
			}
		}
	}

	/**
	 * Register the dropzone for this multiFileWidget.
	 * It only makes sense to call this if the multiFileWidget in question has a dropzone associated with it.
	 * @param {string} id The id of a mutliFileWidget (i.e. the top level container).
	 */
	function registerDropzone(id) {
		let input;
		const element = document.getElementById(id);
		if (element && (input = element.querySelector(inputElementWd))) {
			const dropzoneId = input.getAttribute("data-dropzone");
			if (dropzoneId) {
				input = null;
				filedrop.register(dropzoneId, (type, files) => {
					const className = "wc_dragging";
					if (type === "drop") {
						instance.upload(element, files);
						element.classList.remove(className);
					} else if (type === "dragstart") {
						element.classList.add(className);
					} else if (type === "dragstop") {
						element.classList.remove(className);
					}
				});
			}
		}
	}

	/**
	 * Register file widgets - dropzones cannot be lazily initialized.
	 * @function
	 * @public
	 * @param {String[]} idArr An array of mutliFileWidget ids.
	 */
	this.register = function (idArr) {
		if (idArr?.length) {
			initialise.addCallback(() => {
				let id;
				while ((id = idArr.shift())) {
					registerDropzone(id);
				}
			});
		}
	};

	/**
	 * @param {HTMLFormElement} form
	 * @param {HTMLElement} container
	 */
	function writeState(form, container) {
		/** @type {HTMLElement[]} */
		const multiFileWidgets = Array.from(form.querySelectorAll(containerWd));
		multiFileWidgets.forEach(multiFileWidget => {
			const fileInfos = multiFileWidget.querySelectorAll(fileInfoWd);
			const value = `${multiFileWidget.id}.selected`;
			for (let i = 0; i < fileInfos.length; i++) {
				let { id } = fileInfos[i];
				let stateField = formUpdateManager.writeStateField(container, value, id);
				// stateField.checked = true;  // WTF?
				container.appendChild(stateField);
			}
		});
	}

	/**
	 * Initialise file upload functionality by adding a focus listener.
	 * @function module:wc/ui/multiFileUpload.initialise
	 * @param {HTMLElement} element The element being initialised - usually document.body.
	 */
	this.initialise = function (element) {
		formUpdateManager.subscribe({ writeState });
		event.add(element, { type: "focus", listener: bootStrap, capture: true });
		event.add(element, "click", clickEvent);
		element.classList.add("wc-rtc-gum");
	};

	/**
	 * Get the descriptor of the multi file upload component.
	 * @function module:wc/ui/multiFileUpload.getWidget
	 * @returns {string} The widget descriptor.
	 */
	this.getWidget = () => containerWd;

	/**
	 * Get the descriptor of the file input element associated with a file upload.
	 * Note WMultiFileWidget may also output a load of checkbox elements - one for each file currently uploaded.
	 *
	 * @function module:wc/ui/multiFileUpload.getInputWidget
	 * @returns {string} The widget descriptor.
	 */
	this.getInputWidget = () => inputElementWd;

	/**
	 *
	 * @param {Element} container
	 * @return {HTMLUListElement[]}
	 */
	function getFileInfoContainers(container) {
		/** @type {HTMLUListElement} */
		return container ? Array.from(container.querySelectorAll(fileInfoContainerWd)) : [];
	}

	/**
	 *
	 * @param {Element} container
	 * @return {HTMLUListElement[]}
	 */
	function getColumns(container) {
		const cols = Number(container.getAttribute(COL_ATTR)) || 1;
		let itemContainers = getFileInfoContainers(container);
		// cols 0 and cols 1 are handled as a single list

		if (itemContainers.length < cols) {
			let filesWrapper;
			if (cols > 1) {
				filesWrapper = container.querySelector(filesWrapperWd);
				if (!filesWrapper) {
					filesWrapper = document.createElement("div");
					filesWrapper.className = CLASS_WRAPPER;
					container.appendChild(filesWrapper);
				}
			}
			for (let i = itemContainers.length; i < cols; i++) {
				let col = document.createElement("ul");
				col.className = `${CLASS_NO_BULLET} ${CLASS_FILE_LIST}`;
				if (filesWrapper) {
					filesWrapper.appendChild(col);
				} else {
					container.appendChild(col);
				}
			}
			return getFileInfoContainers(container);
		}
		if (cols === 0 && !itemContainers.length) {
			let col = document.createElement("ul");
			col.className = `${CLASS_NO_BULLET} wc-listlayout-type-flat ${CLASS_FILE_LIST}`;
			container.appendChild(col);
			return getFileInfoContainers(container);
		}
		return itemContainers;
	}

	/**
	 *
	 * @param {Element} container
	 * @return {HTMLUListElement}
	 */
	function getNextColumn(container) {
		const smallest = {
				idx: 0,
				count: -1
			},
			columns = getColumns(container);
		for (let i = 0; i < columns.length; i++) {
			let next = columns[i];
			let items = next.querySelectorAll(fileInfoWd);
			if (smallest.count < 0 || items.length < smallest.count) {
				smallest.idx = i;
				smallest.count = items.length;
				if (smallest.count === 0) {
					break;
				}
			}
		}
		return columns[smallest.idx];
	}

	/**
	 * Creates the UI widget presented to the user while a file is uploading.
	 * It will be created and added to the DOM.
	 * @param {HTMLElement} container The multiFileUploader top level element.
	 * @param {string} fileName The name of the file being uploaded.
	 * @returns {string} The ID of the newly created UI widget.
	 */
	this.createFileInfo = function (container, fileName) {
		const id = uid(),
			itemContainer = getNextColumn(container),
			item = document.createElement(fileInfoItem);
		item.className = CLASS_FILE_INFO;
		const removeButton = document.createElement("button");
		removeButton.setAttribute("type", "button");  // .type causes issues in legacy IE
		removeButton.className = "wc_btn_icon wc_btn_abort";
		removeButton.value = i18n.get("file_abort", fileName);
		icon.add(removeButton, "fa-ban");
		item.appendChild(removeButton);
		item.appendChild(document.createTextNode(fileName));
		const progress = item.appendChild(document.createElement("progress"));
		progress.setAttribute("min", "0");
		progress.setAttribute("max", "100");
		progress.setAttribute("value", "0");
		item.setAttribute("id", id);
		itemContainer.appendChild(item);
		return id;
	};

	/**
	 * Tests if an element is a file upload.
	 * @function module:wc/ui/multiFileUpload.isOneOfMe
	 * @param {Element} element The DOM element to test
	 * @param {Boolean} input If true test the input element, not the container
	 * @returns {Boolean} true if element is the Widget type requested
	 */
	this.isOneOfMe = (element, input) => element?.matches(input ? inputElementWd : containerWd);

	/**
	 * Sets a file selector to an empty value.
	 * As usual this apparently simple task is made complex due to Internet Explorer.
	 * @param {HTMLInputElement} element A file input.
	 */
	this.clearInput = function (element) {
		clearSelector(element, (selector, cloned) => {
			if (cloned) {
				initialiseFileInput(selector);
			}
		});
	};
}

/**
 *
 * @param {HTMLInputElement} element A file selector
 * @returns {string} The file upload URL for this fileselector
 */
function getUploadUrl(element) {
	let result = Trigger.getUrl(element);
	if (!result && element.form) {
		result = element.form.action;
		console.log("File upload URL not set, attempting to use original form action instead", result);
	}
	return result;
}

/**
 * Handles progress events and updates the DOM accordingly.
 * @param {string} fileInfoId The ID of the widget tracking the upload in the DOM.
 */
function progressEventFactory(fileInfoId) {
	/**
	 * @param {ProgressEvent} e
	 */
	return function (e) {
		const fileInfo = document.getElementById(fileInfoId);
		if (e.lengthComputable && fileInfo) {
			const progress = fileInfo.querySelector("progress");
			if (progress) {
				progress.value = (e.loaded / e.total) * 100;
				console.log(fileInfoId, "loaded:", e.loaded, "total:", e.total);
			}
		}
	};
}

/**
 * If something goes wrong with the upload then tell the user about it and do some cleanup.
 * @param {string} fileInfoId The ID of the widget tracking the upload in the DOM.
 */
function errorHandlerFactory(fileInfoId) {
	return /** @param {string} errorMessage */(errorMessage) => {
		const fileInfo = document.getElementById(fileInfoId);
		delete inflightXhrs[fileInfoId];
		if (fileInfo) {
			feedback.flagError({
				element: fileInfo,
				message: errorMessage
			});
		}
		console.log("Error in file upload:", fileInfoId);
	};
}

/**
 * Handle the case where the user aborts the upload.
 * @param {string} fileInfoId The ID of the widget tracking the upload in the DOM.
 */
function abortHandlerFactory(fileInfoId) {
	return () => {
		delete inflightXhrs[fileInfoId];
		console.log("Aborted file upload:", fileInfoId);
	};
}

/**
 * Asynchronously upload files to the server using XMLHTTPRequest.
 * This replaces an older implementation using hidden iframe + hidden form.
 * @constructor module:wc/ui/multiFileUpload~TrueAjax
 * @private
 */
function TrueAjax() {
	/**
	 * @returns {Number} The total number of uploads in progress.
	 */
	this.getUploading = () => document.body.querySelectorAll(progressWd).length;

	/**
	 * Upload the files reference in the dto.
	 * @param {module:wc/file/MultiFileUploader~fileInfo} dto
	 */
	this.request = function (dto) {
		const { name, container, files, url, element } = dto;
		try {
			for (let i = 0; i < files.length; i++) {
				let file = files[i];
				let id = instance.createFileInfo(container, file.name);
				sendFile(url, name, id, file, callbackWrapper(dto, id));
			}
		} finally {
			instance.clearInput(element);
		}
	};

	/**
	 * Returns a callback for sendFile.
	 * @param {module:wc/file/MultiFileUploader~fileInfo} dto
	 * @param {string} fileId A unique ID by which to track this particular file upload.
	 * @returns {Function} The callback wrapper.
	 */
	function callbackWrapper(dto, fileId) {
		return function (srcTree) {
			processResponse({
				dto,
				srcTree,
				xhr: this
			}, fileId);
		};
	}

	/**
	 * @param response
	 * @param {string} fileId
	 */
	function processResponse(response, fileId) {
		const onError = function () {
				errorHandlerFactory(fileId).call(response.xhr);
			},
			dto = response.dto,
			container = document.createElement("ul");
		let df = toDocFragment(response.xhr.responseText);

		if (df) {
			if (df.NodeType === Node.DOCUMENT_NODE) {
				df = df.firstElementChild;
			}
			container.appendChild(df);
			dto.callback(container);
			const inflight = Object.keys(inflightXhrs);
			if (inflight.length === 0) {
				dto.complete(dto.container.id);
			}
			if (!container.innerHTML) {
				onError();
			}
		}
	}

	/**
	 * Upload this file to the server.
	 * @param {string} uri The URL to upload the file to.
	 * @param {string} uploadName The name of the file selector, i.e. the parameter name expected by the server.
	 * @param {string} fileId A unique ID by which to track this particular file upload.
	 * @param {File} file The file to upload.
	 * @param {Function} callback The function to call on success.
	 */
	function sendFile(uri, uploadName, fileId, file, callback) {
		const formData = new FormData(),
			onProgress = progressEventFactory(fileId),
			onError = errorHandlerFactory(fileId),
			onAbort = abortHandlerFactory(fileId);
		formData.append("wc_ajax", uploadName);
		formData.append("wc_ajax_int", "x");
		formData.append("wc_fileuploadid", fileId);
		/*
		 * On the line below we specify the file name because some browsers do not support the File constructor.
		 * In this case the file object is actually a Blob with the same duck type as a File.
		 * The name, however, is a readonly property of blob and while we may appear to have overridden the value we probably haven't.
		 */
		formData.append(uploadName, file, file.name);

		const request = {
			url: uri,
			callback: callback,
			onProgress: onProgress,
			onError: onError,
			onAbort: onAbort,
			cache: false,
			responseType: ajax.responseType.XML,
			postData: formData
		};

		inflightXhrs[fileId] = ajax.simpleRequest(request);
	}
}

initialise.register(instance);
export default instance;
