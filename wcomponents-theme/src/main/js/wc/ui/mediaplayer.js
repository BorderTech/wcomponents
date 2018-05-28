define([ "wc/has", "wc/dom/classList", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/ajax/processResponse"],
	function(has, classList, initialise, Widget, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/mediaplayer~MediaPlayer
		 * @private
		 */
		function MediaPlayer() {
			var useNative = (has("ie") <= 8 ? false : true),
				SOURCE_CLASS = "wc-src",
				SOURCE_LINK,
				TRACK_LINK,
				AUDIO = new Widget("audio"),
				VIDEO = new Widget("video"),
				SOURCE,
				MEDIA = [AUDIO, VIDEO];

			/**
			 * Add a poster image to a source link.
			 * @function
			 * @private
			 * @param {Element} media The video element.
			 * @param {Element) source The source link element.
			 */
			function makePosterLink(media, source) {
				var poster = media.getAttribute("poster"),
					WIDTH = "width",
					HEIGHT = "height",
					width = media.getAttribute(WIDTH),
					height = media.getAttribute(HEIGHT),
					alt = media.getAttribute("data-wc-alt") || media.getAttribute("title"),
					img = document.createElement("img");

				img.src = poster;
				if (width) {
					img.setAttribute(WIDTH, width);
				}
				if (height) {
					img.setAttribute(HEIGHT, height);
				}
				if (alt) {
					img.setAttribute("alt", alt);
				}
				classList.remove(source, SOURCE_CLASS);
				source.innerHTML = "";
				source.appendChild(img);
			}

			/**
			 * Expose src and track links for media which is not playable.
			 * @function
			 * @private
			 * @param {Element} media The HTML AUDIO or VIDEO element.
			 */
			function moveSourceLinks(media) {
				var parent = media.parentNode, sources;

				function _append(next) {
					parent.appendChild(next);
				}

				// move the source links
				SOURCE_LINK = SOURCE_LINK || new Widget("a", SOURCE_CLASS);
				sources = SOURCE_LINK.findDescendants(media);
				if (media.getAttribute("poster") && sources.length === 1) {
					// if we have one source and a poster then make the poster into a link to the source
					makePosterLink(media, sources[0]);
				}
				Array.prototype.forEach.call(sources, _append);

				// move the track links (if any)
				TRACK_LINK = TRACK_LINK || new Widget("a", "wc-track");
				sources = TRACK_LINK.findDescendants(media);
				if (sources && sources.length) {
					parent.insertAdjacentHTML("beforeend", "<br>");
					Array.prototype.forEach.call(sources, _append);
				}
				parent.removeChild(media);
			}

			/**
			 * Attempts to to determine the MIME type of the SOURCE element's source.
			 * @function
			 * @private
			 * @param {Element} source the SOURCE element.
			 * @returns {String} The MIME type of the source if known.
			 * @todo Use {@link module:wc/file/getMimeType}.
			 */
			function getType(source) {
				var url = source.getAttribute("src"),
					type = source.getAttribute("type"),
					ext;

				if (type) {
					if (~type.indexOf(";")) {
						/* only return the mime part of the type in case the attribute contains the codec
						 * see http://www.whatwg.org/specs/web-apps/current-work/multipage/video.html#the-source-element.*/
						return type.substr(0, type.indexOf(";"));
					}
					return type;
				}
				if (url && (ext = url.substring(url.lastIndexOf(".") + 1))) {
					return (/(mp4|m4v|ogg|ogv|webm|flv|wmv|mpeg|mov)/gi.test(ext) ? "video" : "audio") + "/" + ext;
				}
				return null;
			}

			/**
			 * Filter function to determine if a media element is able to play any provided source file.
			 * @function
			 * @private
			 * @param {Element} media A HTML AUDIO or VIDEO element.
			 * @returns {Boolean} true if the media element has no playable sources in the current user agent.
			 */
			function cantPlay(media) {
				var sources, i, len, type;

				if (media.canPlayType) {
					SOURCE = SOURCE || new Widget("source");
					sources = SOURCE.findDescendants(media);
					for (i = 0, len = sources.length; i < len; ++i) {
						if ((type = getType(sources[i])) && media.canPlayType(type)) {
							return false;
						}
					}
				}
				return true;
			}

			/**
			 * @function
			 * @private
			 * @param {Element} element A media element or container.
			 */
			function setUpMediaControls(element) {
				var elements = Widget.isOneOfMe(element, MEDIA) ? [element] : Widget.findDescendants(element, MEDIA);
				if (useNative) {
					Array.prototype.filter.call(elements, cantPlay).forEach(moveSourceLinks);
				} else {
					Array.prototype.forEach.call(elements, moveSourceLinks);
				}
			}

			/**
			 * Initialisation: expose the source and track links for unplayable media.
			 * @function  module:wc/ui/mediaplayer.postInit
			 * @public
			 * @param {Element} element the element being initialised
			 */
			this.initialise = function(element) {
				setUpMediaControls(element);
				processResponse.subscribe(setUpMediaControls, true);
			};
		}
		/**
		 * Provides some fill-in support for audio and video elements.
		 *
		 * @module
		 * @requires module:wc/has
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/ui/ajax/processResponse
		 */
		var instance = new MediaPlayer();
		initialise.register(instance);
		return instance;
	});
