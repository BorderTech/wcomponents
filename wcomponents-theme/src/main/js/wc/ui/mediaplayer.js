/**
 * Provides some fill-in support for audio and video elements. This is mostly around providing alternative access
 * arrangements when the media cannot be played.
 *
 * @module
 * @requires module:wc/i18n/i18n
 * @requires module:wc/has
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/uid
 * @requires module:wc/ui/ajax/processResponse
 */
define([ "wc/has", "wc/dom/classList", "wc/dom/event", "wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/ui/ajax/processResponse"],
	/** @param  has @param classList @param event @param initialise @param shed @param Widget @param processResponse @ignore */
	function(has, classList, event, initialise, shed, Widget, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/mediaplayer~MediaPlayer
		 * @private
		 */
		function MediaPlayer() {
			var useNative = true,
				LAME_CONTROLS = "${wc.ui.media.attrib.lameControls}",
				SOURCE_LINK,
				TRACK_LINK,
				PLAY_BUTTON = new Widget("button", "wc_av_play"),
				LAME_MEDIA = new Widget("", "", {"${wc.ui.media.attrib.lameControls}": null}),
				AUDIO = new Widget("${wc.dom.html5.element.audio}"),
				VIDEO = new Widget("${wc.dom.html5.element.video}"),
				SOURCE,
				MEDIA = [AUDIO, VIDEO],
				subscribed;

			/**
			 * Toggle the play button on click. The actual playing is devolved to a SHED observer since there are many
			 * ways to skin this cat.
			 * @function
			 * @private
			 * @param {event} $event The wrapped click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = PLAY_BUTTON.findAncestor($event.target)) && !shed.isDisabled(element)) {
					shed.toggle(element, shed.actions.SELECT);
					$event.preventDefault();
				}
			}

			/**
			 * When media is natively playable but the controls attribute is "PLAY" we need to artificially keep the
			 * selected state of the play button in sync with the playing state of the media. This does just that.
			 * @function
			 * @private
			 * @param {event} $event The wrapped play event.
			 */
			function playEvent($event) {
				var media, parent, pauseButton;
				if (!$event.defaultPrevented && (media = $event.target) && (parent = media.parentElement) && (pauseButton = PLAY_BUTTON.findDescendant(parent))) {
					shed.select(pauseButton, true);
				}
			}

			/**
			 * When media is natively playable but the controls attribute is "PLAY" we need to artificially keep the
			 * selected state of the play button in sync with the playing state of the media. This does just that.
			 * @function
			 * @private
			 * @param {event} $event The wrapped ended event.
			 */
			function endedEvent($event) {
				var media, parent, pauseButton;
				if (!$event.defaultPrevented && (media = $event.target) && (parent = media.parentNode)) {
					if ((pauseButton = PLAY_BUTTON.findDescendant(parent))) {
						shed.deselect(pauseButton);
					}
					media.setCurrent = 0;
				}
			}

			/**
			 * When media is natively playable but the controls attribute is "PLAY" we need to artificially keep the
			 * selected state of the play button in sync with the playing state of the media. This does just that.
			 * @function
			 * @private
			 * @param {event} $event The wrapped pause event.
			 */
			function pauseEvent($event) {
				var media, parent, pauseButton;
				if (!$event.defaultPrevented && (media = $event.target) && (parent = media.parentElement) && (pauseButton = PLAY_BUTTON.findDescendant(parent))) {
					shed.deselect(pauseButton, true);
				}
			}

			/**
			 * Subscribe to shed SELECT/DESELECT if we have a PLAY/PAUSE button.
			 * @function
			 * @private
			 * @param {Element} element The PLAY/PAUSE button.
			 * @param {String} action the shed action shed.actions.SELECT or shed.actions.DESELECT.
			 */
			function shedSubscriber(element, action) {
				var media, CONTROLS = "aria-controls", controls;
				if (PLAY_BUTTON.isOneOfMe(element) && (controls = element.getAttribute(CONTROLS)) && (media = document.getElementById(controls))) {
					if (action === shed.actions.SELECT) {
						media.play();
					}
					else {
						media.pause();
					}
				}
			}

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
					alt = media.getAttribute("${wc.ui.media.attrib.alt}") || media.getAttribute("title"),
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
				classList.remove(source, "src");
				source.innerHTML = "";
				source.appendChild(img);
			}


			/**
			 * Initialisation helper.
			 * Yes, this is a lot to happen, especially as it has to happen for every media element but that is the
			 * price we pay for rubbish support. Fortunately most of it will only run in those rare occasions where we
			 * have controls='play' in the XML.
			 * @function
			 * @private
			 * @param {Element} element The HTML AUDIO or VIDEO element being established.
			 */
			function setupPlayer(element) {
				var parent,
					playButton;
				event.add(element, event.TYPE.ended, endedEvent);

				if (element.getAttribute(LAME_CONTROLS) === "play") {
					parent = element.parentElement;
					if ((playButton = PLAY_BUTTON.findDescendant(parent))) {
						event.add(element, event.TYPE.play, playEvent);
						event.add(element, event.TYPE.pause, pauseEvent);
					}
					if (!subscribed) {
						subscribed = true;
						shed.subscribe(shed.actions.SELECT, shedSubscriber);
						shed.subscribe(shed.actions.DESELECT, shedSubscriber);
					}
				}
				// TODO: more of  a thought than a real todo: we shouldn't really allow controls='none'...
				element.removeAttribute(LAME_CONTROLS);
			}

			/**
			 * Initialise document for AUDIO and VIDEO elements.
			 * @function  module:wc/ui/mediaplayer.initialise
			 * @public
			 * @param {Element} element The element being initialised, usually document.body
			 */
			this.initialise = function(element) {
				if (has("ie") <= 8) {
					useNative = false;
				}
				else {
					event.add(element, event.TYPE.click, clickEvent);
				}
			};

			/**
			 * Expose src and track links for media which is not playable.
			 * @function
			 * @private
			 * @param {Element} media The HTML AUDIO or VIDEO element.
			 */
			function moveSourceLinks(media) {
				var parent = media.parentNode, playButton, sources;

				function _append(next) {
					parent.appendChild(next);
				}

				// if we attached a play button because @controls='play' but the media is not playable, then we need to remove the play button
				if ((playButton = PLAY_BUTTON.findDescendant(parent))) {
					playButton.parentNode.removeChild(playButton);
				}

				// move the source links
				SOURCE_LINK = SOURCE_LINK || new Widget("a", "src");
				sources = SOURCE_LINK.findDescendants(media);
				if (media.getAttribute("poster") && sources.length === 1) {  // if we have one source and a poster then make the poster into a link to the source
					makePosterLink(media, sources[0]);
				}
				Array.prototype.forEach.call(sources, _append);

				// move the track links (if any)
				TRACK_LINK = TRACK_LINK || new Widget("a", "track");
				sources = TRACK_LINK.findDescendants(media);
				if (sources && sources.length) {
					parent.appendChild(document.createElement("br"));
					Array.prototype.forEach.call(sources, _append);
				}
				parent.removeChild(media);
			}

			/**
			 * Attempts to to determine the MIME type of the SOURCE element's source.
			 * @function
			 * @private
			 * @param {Element} source the SOURCE element.
			 * @returns {?String} The MIME type of the source if known.
			 * @todo Use {@link module:wc/file/getMimeType}.
			 */
			function getType(source) {
				var result = "",
					url = source.getAttribute("src"),
					type = source.getAttribute("type"),
					ext;

				if (type) {
					if (~type.indexOf(";")) {
						/* only return the mime part of the type in case the attribute contains the codec
						 * see http://www.whatwg.org/specs/web-apps/current-work/multipage/video.html#the-source-element.*/
						result = type.substr(0, type.indexOf(";"));
					}
					else {
						result = type;
					}
				}
				else if (url && (ext = url.substring(url.lastIndexOf(".") + 1))) {
					result = (/(mp4|m4v|ogg|ogv|webm|flv|wmv|mpeg|mov)/gi.test(ext) ? "video" : "audio") + "/" + ext;
				}
				return result;
			}

			/**
			 * Filter function to determine if a media element is able to play any provided source file.
			 * @function
			 * @private
			 * @param {Element} media A HTML AUDIO or VIDEO element.
			 * @returns {Boolean} true if the media element has no playable sources in the current user agent.
			 */
			function cantPlay(media) {
				var sources, j, len, nextSource, type, result = true;

				if (media.canPlayType) {
					SOURCE = SOURCE || new Widget("${wc.dom.html5.element.source}");
					sources = SOURCE.findDescendants(media);
					for (j = 0, len = sources.length; j < len; ++j) {
						nextSource = sources[j];

						if ((type = getType(nextSource)) && media.canPlayType(type)) {
							result = false;
							break;
						}
					}
				}
				return result;
			}

			/**
			 * @function
			 * @private
			 * @param {Element} element A media element or container.
			 */
			function setUpMediaControls(element) {
				var elements = Widget.isOneOfMe(element, MEDIA) ? [element] : Widget.findDescendants(element, MEDIA),
					crippledElements = LAME_MEDIA.isOneOfMe(element) ? [element] : LAME_MEDIA.findDescendants(element);
				if (useNative) {
					Array.prototype.forEach.call(crippledElements, setupPlayer); // set up play/pause before testing cantPlay.
					Array.prototype.filter.call(elements, cantPlay).forEach(moveSourceLinks);
				}
				else {
					Array.prototype.forEach.call(elements, moveSourceLinks);
				}
			}

			/**
			 * Late initialisation: expose the source and track links for unplayable media.
			 * @function  module:wc/ui/mediaplayer.postInit
			 * @public
			 */
			this.postInit = function(element) {
				setUpMediaControls(element);
				processResponse.subscribe(setUpMediaControls, true);
			};
		}

		var /** @alias module:wc/ui/mediaplayer */ instance = new MediaPlayer();
		initialise.register(instance);
		return instance;
	});
