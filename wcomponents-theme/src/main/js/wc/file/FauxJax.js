define(["wc/dom/formUpdateManager",
		"lib/sprintf",
		"wc/i18n/i18n",
		"wc/dom/event",
		"wc/dom/Widget",
		"wc/dom/uid"],
	function(formUpdateManager, sprintf, i18n, event, Widget, uid) {
		"use strict";
		var BUSY_ATTRIBUTE = "aria-busy",
			uploading = {},
			fileInputWd = new Widget("INPUT", "", { type: "file" });

		/**
		 * Keeps track of which elements are currently loading.
		 * @function
		 * @private
		 * @param {Element} [container] The container that is currently uploading.
		 * @param {Boolean} [stopped] Call with true when the upload has finished.
		 * @returns {number} The number of files currently loading.
		 */
		function trackLoading(container, stopped) {
			var id;
			if (container) {
				id = container.id || (container.id = uid());
				if (stopped) {
					delete uploading[id];
					container.removeAttribute(BUSY_ATTRIBUTE);
				}
				else {
					uploading[id] = true;
					container.setAttribute(BUSY_ATTRIBUTE, true);
				}
			}
			return Object.keys(uploading).length;
		}

		/**
		 * This is used exclusively by wc/ui/multiFileUploader to support asynchronous file uploads in legacy browsers.
		 * It has been refactored to a separate module for easier deletion in a utopian future where IE9 and earlier are
		 * nothing more than war stories told by old web developers in the same vein as dial-up modems and floppy disks.
		 *
		 * @param {Function} createFileInfo
		 * @param {Function} getUploadUrl
		 * @constructor module:wc/file/FauxJax
		 * @private
		 */
		function FauxJax(createFileInfo, getUploadUrl) {
			var FORM_ID_SUFFIX = "_form",
				IFRAME_ID_SUFFIX = "_iframe";

			this.createFileInfo = createFileInfo;

			/**
			 * Make a pseudo ajax request.
			 * @function
			 * @public
			 * @param {Object} dto
			 */
			this.request = function(dto) {
				var form;
				if (!uploading[dto.container.id]) {
					trackLoading(dto.container);
					form = getFileUploadForm(dto.element, callbackWrapperFactory(dto));
					if (form) {
						this.writeState(form, dto.container, dto.element);
						try {
							event.fire(form, event.TYPE.submit);
						}
						catch (ex) {  // this can happen if user types invalid path in file input element (can't happen in FF)
							console.log(ex);
						}
					}
				}
			};

			/**
			 * Cancel a pseudo-AJAX request.
			 * TODO if this was ever used it would need to call trackLoading
			 * @function
			 * @public
			 * @param {Element} triggerElement The element which initiated the request we want to cancel.
			 */
			this.cancelRequest = function(triggerElement) {
				var iframe = getHiddenIframe(triggerElement);
				if (iframe) {
					if (iframe.contentWindow.stop) {
						iframe.contentWindow.stop();  // browsers
					}
					else {
						iframe.contentWindow.document.execCommand("Stop");  // IE
					}
				}
			};

			/**
			 * Returns the hidden iframe for this file selector element.  If it can't find one already
			 * existing then it will create one (append to body).
			 * We are using an inline style for the iframe rather than external
			 * CSS to ensure that the hidden iframe does not get unhidden by
			 * mistake by CSS changes.
			 * @function
			 * @private
			 * @param {Element} element The element instigating the file upload.
			 * @param {Function} callback The callback function.
			 */
			function getHiddenIframe(element, callback) {
				var result, iframeHTML, iframeId = element.name + IFRAME_ID_SUFFIX;
				result = document.getElementById(iframeId);
				if (!result && callback) {
					iframeHTML = '<iframe name="%s" id="%1$s" src="javascript:false;" style="display:none;">' + i18n.get("${wc.ui.loading.loadMessage}") + '</iframe>';
					iframeHTML = sprintf.sprintf(iframeHTML, iframeId);
					document.body.insertAdjacentHTML("beforeEnd", iframeHTML);
					result = document.getElementById(iframeId);
					if (result.addEventListener) {
						result.addEventListener(event.TYPE.load, callback, false);
					}
					else {
						result.attachEvent("on" + event.TYPE.load, callback);
					}
				}
				return result;
			}

			/**
			 * Returns the "hidden" form for this file selector element. If it can't find one already existing then it
			 * will create one (append to body).
			 *
			 * The form is like this:
			 * <pre><code>
			 * <form target="{name of hidden iframe}"
			 *   enctype="multipart/form-data"
			 *   action="{the url to upload to}"
			 *   method="POST"
			 *   name="{element.name + _form}"
			 *   id="{element.name + _form}"
			 *   style="display:none;">
			 *       <input type="hidden" name="wc_target" value="element.id">
			 * </form></code></pre>
			 *
			 * NOTE: We are using an inline style here rather than external CSS to ensure that the hidden form does not
			 * get unhidden by mistake by CSS changes.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element instigating the file upload.
			 * @param {Function} callback The function to call after uploading.
			 * @returns {Element} The file upload form.
			 */
			function getFileUploadForm(element, callback) {
				var result,
					formId = element.name + FORM_ID_SUFFIX,
					action,
					formHTML,
					iframeId;
				result = document.getElementById(formId);
				if (!result && callback) {
					action = getUploadUrl(element);
					if (action) {
						formHTML = '${wc.ui.multiFileUploader.html.form}';
						iframeId = getHiddenIframe(element, function() {
							result.innerHTML = "";
							callback();
						}).id;
						formHTML = sprintf.sprintf(formHTML, iframeId, action, formId, element.name);
						document.body.insertAdjacentHTML("beforeEnd", formHTML);
						result = document.getElementById(formId);
					}
					else {
						console.log("File upload URL not set ");
					}
				}
				return result;
			}

			/**
			 * Create a callback function for a file upload form.
			 * @function
			 * @private
			 * @param {Function} callback The original callback function.
			 * @param {Element} container The fileUploadWidget container.
			 * @returns {Function} A curried callbackWrapper.
			 */
			function callbackWrapperFactory(dto) {
				return function ($event) {
					$event = $event || window.event;
					var target = $event.target || $event.srcElement,
						doc = target.contentWindow.document,
						body = doc.body;
					try {
						dto.callback(body);
						dto.complete(dto.element.id);
					}
					catch (ex) {
						console.log("Error in callback ", ex);
					}
					finally {
						body.innerHTML = "";  // reset
						trackLoading(dto.container, true);
					}
				};
			}
		}

		/**
		 *
		 * @returns {Number} The total number of uploads in progress.
		 */
		FauxJax.prototype.getUploading = function() {
			return trackLoading();
		};

		/*
		 * @param form The hidden form that will be posted in the background to upload the file.
		 */
		FauxJax.prototype.writeState = function(form, container) {
			var newContainer,
				fileId,
				element, newElement;
			newContainer = container.cloneNode(true);
			// innerHTML is not enough, the value will be lost, we need to move the actual element
			element = fileInputWd.findDescendant(container);
			element.removeAttribute("id");
			newElement = fileInputWd.findDescendant(newContainer);
			form.appendChild(newElement);
			fileId = this.createFileInfo(container, element.value);  // If we are here the browser is legacy and the element will only ever have a single file value
			formUpdateManager.writeStateField(form, "wc_target", element.name);
			formUpdateManager.writeStateField(form, "wc_fileid", fileId);
			/*
			 * The reason we swap the new element back into the original form is so that the upload
			 * can be easily cancelled and the widget will still be in a valid state.
			 */
			swapElements(element, newElement);
		};

		/*
		 * Swaps two elements around in the DOM, i.e. they will change places.
		 * @param el1 a DOM element to switch around
		 * @param el2 a DOM element to switch around
		 */
		function swapElements(el1, el2) {
			var parent1 = el1.parentNode,
				parent2 = el2.parentNode,
				el1next = el1.nextSibling;
			parent2.replaceChild(el1, el2);
			if (el1next) {
				parent1.insertBefore(el2, el1next);
			}
			else {
				parent1.appendChild(el2);
			}
		}

		return FauxJax;
	}
);
