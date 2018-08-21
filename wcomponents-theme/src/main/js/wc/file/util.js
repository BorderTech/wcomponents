define(["wc/dom/uid", "wc/file/getMimeType"], function(uid, getMimeType) {
	
	var instance = new FileUtil();
	/**
	 * Used when checking the newly created image is named with the correct extension.
	 * If it does not already match any in the array then the extension at index zero will be appended.
	 **/
	FileUtil.prototype.mimeToExt = {
		"image/jpeg": ["jpeg", "jpg"],
		"image/png": ["png"],
		"image/webp": ["webp"]
	};

	function FileUtil() {
		/**
		 * Converts a generic binary blob to a File blob.
		 * @param {Blob} blob
		 * @param {Object} [config] Attempt to set some of the file properties such as "type", "name"
		 * @returns {File} The File blob.
		 */
		this.blobToFile = function (blob, config) {
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
		};
		

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
				expectedExtension = instance.mimeToExt[info.mime];
				if (info.mime && expectedExtension) {
					if (expectedExtension.indexOf(info.ext) < 0) {
						file.name += "." + expectedExtension[0];
					}
				}
			}
		}
		
		

		/**
		 * Converts a data url to a binary blob.
		 * @param {string} dataURI
		 * @returns {Blob} The binary blob.
		 */
		this.dataURItoBlob = function (dataURI) {
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
		};
	}
	return instance;

});
