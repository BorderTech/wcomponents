/**
 * Module to manage launching non-native content.
 *
 * This exists primarily because Internet Explorer fires the "beforeunload" and "unload" event when certain types of
 * links are clicked that will, in fact, not unload the page.
 *
 * It also exists to help other browsers with our own "wc_content" links, while they may not necessarily need it this
 * area is often problematic and can change from version to version. I believe it is ultimately simpler and more
 * consistent to route all browsers through this logic.
 *
 * Ideally we would cancel the beforeunload event however since IE11 that results in unavoidable prompts to the user
 * asking "Are you sure you want to leave this page?". Because of this we must cancel the click event but then honour
 * the intent of the click in another way.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/redirect
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/redirect"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param redirect wc/ui/redirect @ignore */
	function(event, initialise, Widget, redirect) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/launchLink~LaunchLink
		 * @private
		 */
		function LaunchLink() {
			/**
			 * Gets the href from an element, which may be the URL data- attribute of a link-like element (such as a
			 * WButton with renderAs='link' or WMenuItem with a URL, both of which are button elements).
			 *
			 * @function
			 * @private
			 * @param {Element} element An element with an href OR one of our custom equivalents.
			 * @returns {String} The href if found, otherwise falsey (actually "")
			 */
			function getHref(element) {
				var result;
				if (element) {
					result = element.href || element.getAttribute("data-wc-url");
				}
				return result || "";
			}

			/**
			 * Click event listener. Interested in clicks on anchors or anchor-like elements.
			 *
			 * IE triggers "beforeunload" when a mailto link is clicked (and other custom protocols too).
			 *
			 * Also, IE fires beforeUnload when a link is clicked that actually results in downloading
			 * an attachment, e.g. some binary data, rather than navigating the page.
			 * For example if we have a link that points to a "gif" image, and in the response the HTTP
			 * Content-Disposition header is set to "attachment" IE still fires the beforeunloadevent.
			 * This would result in the loading indicator being displayed when a file is downloaded.
			 *
			 * @function
			 * @private
			 * @param {Event} $event A click event.
			 */
			function clickEvent($event) {
				var href,
					hasPopup,
					element = ANCHOR_WD.findAncestor($event.target) || FAKE_ANCHOR_WD.findAncestor($event.target);
				if (element) {
					hasPopup = element.getAttribute("aria-haspopup");
					if (hasPopup !== "true") {
						href = getHref(element);
						if (href) {
							if (redirect.isLaunchUrl(href) || element.hasAttribute(CONTENT_ATTRIB)) {
								$event.preventDefault();  // since we have prevented the link action we're going to have to do it ourselves
								redirect.register(href);  // redirect (poorly named) knows how to "do stuff" with URLs.
							}
						}
					}
				}
			}

			/**
			 * Inline links will result in the page being entirely replaced with the content of a browser plugin.
			 * Think PDF link that opens in the same window.
			 * @function module:wc/ui/launchLink.isInlineLink
			 * @public
			 * @param {Element} element The element to test.
			 * @returns {Boolean} true if this element has an inline attachment link.
			 */
			this.isInlineLink = function(element) {
				var result = false,
					href = getHref(element);
				if (href) {
					result = href.indexOf(INLINE_CONTENT_FLAG) > 0;
				}
				return result;
			};

			/**
			 * Initialise the functionality by wiring up a click event listener.
			 * @function module:wc/ui/launchLink.initialise
			 * @public
			 * @param {type} element
			 * @returns {undefined}
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, 100);
			};
		}

		var /** @alias module:wc/ui/launchLink */ instance = new LaunchLink(),
			CONTENT_ATTRIB = "data-wc-attach",
			INLINE_CONTENT_FLAG = "wc_content=inline",
			ANCHOR_WD = new Widget("a"),
			FAKE_ANCHOR_WD = new Widget("", "", {"data-wc-url": null});

		initialise.addBodyListener(instance);

		return instance;
	});
