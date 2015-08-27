/**
 * Provides diagnostic information in client side debug mode.
 *
 * @module
 * @requires module:wc/has
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/getViewportSize
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/clearSelection
 * @requires module:wc/timers
 * @requires module:wc/dom/getBox
 * @requires module:wc/dom/textContent
 * @requires module:wc/ui/positionable
 * @requires module:wc/dom/storage
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/ui/dialog
 * @requires module:wc/debug/consoleColor
 * @requires module:wc/ui/draggable
 * @requires module:wc/ui/resizeable
 * @todo Needs re-ordering
 * @todo Could do with a cleanup there seems to be quite a bit of redundancy. This is debug only code so is a bit orphaned.
 */
define(["wc/has",
		"wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/getViewportSize",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/dom/clearSelection",
		"wc/timers",
		"wc/dom/getBox",
		"wc/dom/textContent",
		"wc/ui/positionable",
		"wc/dom/storage",
		"wc/ui/ajax/processResponse",
		"wc/ui/dialog",
		"wc/debug/consoleColor"],
	/** @param has wc/has @param classList wc/dom/classList @param event wc/dom/event @param focus wc/dom/focus @param getViewportSize wc/dom/getViewportSize @param initialise wc/dom/initialise @param shed wc/dom/shed @param tag wc/dom/tag @param Widget wc/dom/Widget @param clearSelection wc/dom/clearSelection @param timers wc/timers @param getBox wc/dom/getBox @param textContent wc/dom/textContent @param positionable wc/ui/positionable @param storage wc/dom/storage @param processResponse wc/ui/ajax/processResponse @ignore */
	function(has, classList, event, focus, getViewportSize, initialise, shed, tag, Widget, clearSelection, timers, getBox, textContent, positionable, storage, processResponse) {
		"use strict";

		/*
		 * Unused dependencies:
		 * debugInfo needs dialog as it provides the basic info "dialog" functionality. We also require
		 * wc/debug/consoleColor because it is used in debug mode but is not required by debugInfo itself.
		 *
		 * TODO: include "wc/ui/draggable", "wc/ui/resizeable" in the dependency list and remove the call to require below.
		 */

		var debugInfo = new DebugInfo();
		initialise.register(debugInfo);

		require(["wc/ui/draggable", "wc/ui/resizeable"]);

		/**
		 * @alias module:wc/debug/debugInfo
		 */
		function DebugInfo() {
			var CONTAINER_ID = "wcdebugcontainer",
				CLASS_ATTRIB = "${wc.debug.debugInfo.attrib.debugClass}",
				CLASS = {INFO_BTN: "wc_db_infobtn",
						INFO: "wc_db_info",
						IS_BEING_DEBUGGED: "wc_db_active",
						DL_COLUMN: "column",
						ANIMATE_MOVE: "wc_dragflow",
						ANIMATE_RESIZE: "wc_resizeflow",
						ANIMATE_SHOW: "wc_db_showing"},
				ATTRIB_HAS_DEBUG_INFO = "${wc.debug.debugInfo.attrib.hasDebugInfo}",
				DEBUG_INFO_TARGET = new Widget("", "", {"${wc.debug.debugInfo.attrib.hasDebugInfo}": null}),
				INFO_BUTTON = new Widget(tag.BUTTON, CLASS.INFO_BTN),
				CLOSE_BUTTON = new Widget(tag.BUTTON, "wc_dialog_close"),
				INSTRUCTION = new Widget(tag.P, "wc_db_message"),
				INFO_CONTAINER_WIDGET = new Widget("", "wc_db_infobox"),
				BREADCRUMB_WIDGET = new Widget(tag.UL, "wc_db_crumbs"),
				DEBUGABLE_CHILDREN = new Widget(tag.UL, "wc_db_children"),
				DEBUG_ATTRIB = "data-wc-dubugdebug",
				INFO_ATTRIB = "ata-wc-debuginfo",
				WARN_ATTRIB = "data-wc-debugwarn",
				ERROR_ATTRIB = "data-wc-debugerr",
				OFFSET = 24,  // pixel offset of debug info container relative to top left of element being debugged
				registry = {},
				lastInfoTarget,
				touchTimeout,
				OTHER_INFO,
				RX = /([^\[]*)\[([^\]]*)\]([^\[]*)/g,
				NL_RX = /\\n/g,
				INFO_BTN_AS_LINK = CLASS.INFO_BTN + " wc_btn_link",
				toTag = tag.toTag,
				DL = tag.DL, DT = tag.DT, DD = tag.DD,
				STORE_KEY = "wc/debug/debuginfo.donotshowinstructions";

			/**
			 * Initialise event listeners. This is a subscriber to {@link module:wc/dom/initialise}.
			 * @function
			 * @public
			 * @param {Element} element The element being initialised. Notionally anyelement, in practise document.body
			 */
			this.initialise = function(element) {
				var container;
				event.add(element, event.TYPE.click, clickEvent, -1);
				event.add(element, event.TYPE.keydown, keydownEvent, -1);
				if (has("event-ontouchstart")) {
					event.add(element, event.TYPE.touchstart, touchstartEvent, -1);
					event.add(element, event.TYPE.touchend, touchendEvent, -1);
					event.add(element, event.TYPE.touchcancel, touchcancelEvent);
				}

				if ((container = document.getElementById(CONTAINER_ID))) {
					event.add(container, event.TYPE.animationend, animationendEvent, -1);

					if ("onwebkitanimationend" in window) {
						event.add(container, "webkitAnimationEnd", animationendEvent, -1);
					}
					event.add(container, event.TYPE.transitionend, transitionendEvent);

					if ("onwebkittransitionend" in window) {
						event.add(container, "webkitTransitionEnd", transitionendEvent);
					}
				}
			};

			/**
			 * Late initialisation: set up subscribers, remove instructions if required and count the page's DOM
			 * elements. This is a subscriber to {@link module:wc/dom/initialise}.
			 *
			 * @function
			 * @public
			 */
			this.postInit = function() {
				var info = INSTRUCTION.findDescendant(document.body);
				shed.subscribe(shed.actions.SHOW, shedSubscriber);
				shed.subscribe(shed.actions.HIDE, shedSubscriber);

				if (info) {
					if (storage.get(STORE_KEY, true)) {
						removeInstructions(info);
					}
					else {
						shed.show(info);
					}
				}
				countElements();
				processResponse.subscribe(countElements, true);
			};

			/**
			 * Register debug info for later use.
			 * @function
			 * @public
			 * @param {Object} objArray An array of registry objects.
			 * @param {boolean} [isAjax] Indicates that the registration object was created as part of an AJAX response.
			 */
			this.register = function(objArray, isAjax) {
				objArray.forEach(function(next) {
					var nextId = next["id"];
					registry[nextId] = next;
					if (isAjax) {
						resetCurrentElement(next);
					}
				});
			};

			/**
			 * When a debugInfo registration object is part of an AJAX response the element to which the info belongs
			 * may have to be updated with new information.
			 * @function
			 * @private
			 * @param {Object} next The debugInfo registration object.
			 */
			function resetCurrentElement(next) {
				var id = next["id"],
					javaClass = next["javaclass"],
					elId = next["javaId"], el, dbAttrib;
				if (elId && (el = document.getElementById(elId))) {
					if ((dbAttrib = el.getAttribute(ATTRIB_HAS_DEBUG_INFO))) {
						if (dbAttrib.indexOf(id) === -1) {
							el.setAttribute(ATTRIB_HAS_DEBUG_INFO, dbAttrib + " " + id);
						}
					}
					else {
						el.setAttribute(ATTRIB_HAS_DEBUG_INFO, id);
					}
					if (javaClass && !(dbAttrib = el.getAttribute(CLASS_ATTRIB))) {
						el.setAttribute(CLASS_ATTRIB, javaClass);
					}
				}
			}

			/**
			 * A {@link module:wc/dom/shed} subscriber to listen for SHOW and HIDE of debug info. Cleanup and reset
			 * focus on HIDE, set transient attributes on SHOW.
			 * @function
			 * @private
			 * @param {Element} element The DOM element being shown/hidden
			 * @param {String} action The shed event type SHOW or HIDE.
			 */
			function shedSubscriber(element, action) {
				var target;
				if (element && element.id === CONTAINER_ID) {
					if (action === shed.actions.HIDE) {
						if (lastInfoTarget && (target = document.getElementById(lastInfoTarget))) {
							removeDebugInfoDetails();
							if (focus.canFocus(target)) {
								focus.setFocusRequest(target);
							}
							else {
								focus.focusFirstTabstop(target);
							}
							lastInfoTarget = null;
						}
						positionable.reset(element);
					}
					else {
						if (lastInfoTarget && (target = document.getElementById(lastInfoTarget))) {
							classList.add(target, CLASS.IS_BEING_DEBUGGED);
							positionable.pinTo(element, target, getPinToConfig(element, target));
							positionable.forceToViewPort(element);
						}
						clearSelection();
						focus.focusFirstTabstop(element);
					}
				}
			}

			/**
			 * Gets a configuration object for {@link module:wc/ui/positionable#pinTo}.
			 * @function
			 * @private
			 * @param {Element} container The element to which we are pinning new content.
			 * @param {Element} element The element we are pinning to container.
			 * @returns {Object} The configuration object.
			 */
			function getPinToConfig(container, element) {
				var box = getBox(container),
					elBox = getBox(element),
					vpSize = getViewportSize(true),
					result = {pos: positionable.POS.NE, hOffset: OFFSET, vOffset: OFFSET};  // normally pin to the NE corner of the inside of the box
				if (elBox.right + box.width <= vpSize.width) {
					// if we can fit the info to the right of the element, put it there with no offset
					result = {pos: (positionable.POS.NORTH | positionable.POS.EAST), outside: true};
				}
				else if (elBox.left <= box.width) {
					// if we can fit the info to the left of the element, put it there
					result = {pos: (positionable.POS.NORTH | positionable.POS.WEST), outside: true};
				}
				else if (box.width >= elBox.width) {
					// debug info wider than element
					if (elBox.width > OFFSET && (elBox.right + box.width - OFFSET <= vpSize.width )) {
						// if we can fit the info just inside the right edge of element
						result = {pos: (positionable.POS.NORTH | positionable.POS.EAST), outside: true, hOffset: (-1 * OFFSET), vOffset: OFFSET};
					}
					else if (elBox.left + OFFSET <= box.width) {
						result = {pos: (positionable.POS.NORTH | positionable.POS.WEST), outside: true, hOffset: OFFSET, vOffset: OFFSET};
					}
					else if (elBox.left > vpSize.width - elBox.right) {
						// more room to the left
						result = {pos: (positionable.POS.NORTH | positionable.POS.WEST), outside: true, hOffset: (box.width - elBox.left), vOffset: OFFSET};
					}
					else {
						// more room to the right
						result = {pos: (positionable.POS.NORTH | positionable.POS.EAST), outside: true, hOffset: (-1 * (box.width + elBox.right - vpSize.width)), vOffset: OFFSET};
					}
					// otherwise use the default
				}
				return result;
			}

			/**
			 * Click event handler.
			 *<ul>
			 *    <li>Click on the "instructions" removes it from the DOM;</li>
			 *    <li>click on the debug info close button closes the info box;</li>
			 *    <li>click on a breadcrumb or child "info button" button resets the debug info to that button's target
			 *    component;</li>
			 *    <li>META + SHIFT + CLICK on component opens the debug info for that component (if any);</li>
			 *    <li>ALT + SHIFT + CLICK on a component shows the WComponent name helper;</li>
			 *    <li>other clicks outside the debug info box will close the debug info box.</li>
			 *</ul>
			 * @function
			 * @private
			 * @param {Event} $event The wrapped click event.
			 */
			function clickEvent($event) {
				var target = $event.target,
					element,
					preventDefaultAction,
					container,
					META = ($event.ctrlKey || $event.metaKey),
					SHIFT = $event.shiftKey,
					targetId,
					html;

				OTHER_INFO = OTHER_INFO || [
					new Widget("", "", {"ata-wc-debuginfo": null}),
					new Widget("", "", {"data-wc-debugwarn": null}),
					new Widget("", "", {"data-wc-debugerr": null})
				];

				if ((element = INSTRUCTION.findAncestor(target))) {
					removeInstructions(element);
				}
				else if (CLOSE_BUTTON.findAncestor(target)) {
					hide();
				}
				else if (((element = INFO_BUTTON.findAncestor(target)) && (targetId = element.value) && (element = document.getElementById(targetId))) || (META && SHIFT && (element = DEBUG_INFO_TARGET.findAncestor(target)))) {
					preventDefaultAction = showDebugInfo(element);
				}
				else if (META && SHIFT && (element = Widget.findAncestor(target, OTHER_INFO)) && (html = getAttribHTML(element, true))) {
					// a component may not have an ID and therefore we cannot find its debugInfo even if it has it this should always be called after trying to find DEBUG_INFO_TARGET
					preventDefaultAction = showInfoHelper(html, element);
				}
				else if ((container = document.getElementById(CONTAINER_ID)) && !(target.compareDocumentPosition(container) & Node.DOCUMENT_POSITION_CONTAINS) && !shed.isHidden(container)) {
					hide();
				}
				if (preventDefaultAction) {
					$event.preventDefault();
				}
			}

			/**
			 * Touchstart event handler to open debug info box on a touch surface by touching and holding.
			 * @function
			 * @private
			 * @param {Event} $event The wrapped touchstart event.
			 */
			function touchstartEvent($event) {
				var target = $event.touches[0].target, element;

				if (touchTimeout) {
					timers.clearTimeout(touchTimeout);
					touchTimeout = null;
				}

				if ((element = INSTRUCTION.findAncestor(target))) {
					removeInstructions(element);
				}

				if ($event.defaultPrevented) {
					return;
				}

				if ((element = document.getElementById(CONTAINER_ID)) && !shed.isHidden(element)) {
					if (!(target.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINS)) {
						hide();
					}
					else {
						// a touch inside the debug info container can be ignored.
						return;
					}
				}

				touchTimeout = timers.setTimeout(function () {
					var _element = DEBUG_INFO_TARGET.findAncestor(target);
					if (_element) {
						showDebugInfo(_element);
						$event.preventDefault();
					}
				}, 1500, target);
			}

			/**
			 * Touchend event handler which will clear the touch timeout (if any) to prevent debug info opening just by
			 * touching a component.
			 *
			 * @function
			 * @private
			 * @todo merge with touchcancelEvent now it is the same.
			 */
			function touchendEvent(/* $event */) {
				if (touchTimeout) {
					timers.clearTimeout(touchTimeout);
					touchTimeout = null;
				}
			}

			/**
			 * Touchcancel event handler which will clear the touch timeout (if any).
			 *
			 * If the debug info "instructions" are the target they are removed from the DOM.
			 * If the touch is outside the debug info box then the debug info box is closed.
			 * @function
			 * @private
			 * @todo merge with touchendEvent
			 */
			function touchcancelEvent(/* $event */) {
				if (touchTimeout) {
					timers.clearTimeout(touchTimeout);
					touchTimeout = null;
				}
			}

			/**
			 * keydown event listener. ESCAPE to remove instructions and close the debugging info dialog box.
			 * CTRL + META + F2 on a debugging target to open the debug info dialog box.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The wrapped keydown event.
			 */
			function keydownEvent($event) {
				var element;
				if (!$event.defaultPrevented) {
					if ($event.keyCode === KeyEvent.DOM_VK_ESCAPE) {
						/* ESCAPE key removes debug info instructions */
						if ((element = INSTRUCTION.findDescendant(document.body))) {
							removeInstructions(element);
						}

						/* hide debug info */
						if ((element = document.getElementById(CONTAINER_ID)) && !shed.isHidden(element)) {
							hide();
							$event.preventDefault();  // do not close any dialogs etc.
						}
					}
					else if (($event.ctrlKey || $event.metaKey) && $event.keyCode === KeyEvent.DOM_VK_F2 && (element = DEBUG_INFO_TARGET.findAncestor($event.target))) {
						// META + F2 will show debug info for currently focussed element (if any)
						showDebugInfo(element);
					}
				}
			}

			/**
			 * animationend event handler. Used to remove the fade-in class. The fade in is a bit naff...
			 * @function
			 * @private
			 * @param {Event} $event The wrapped animationend event.
			 */
			function animationendEvent($event) {
				var container;
				if (!$event.defaultPrevented && $event.animationName === "debugfadein" && (container = document.getElementById(CONTAINER_ID))) {
					classList.remove(container, CLASS.ANIMATE_SHOW);
				}
			}

			/**
			 * transitionend event handler. Used to clean up after a resize or move transition just in case the
			 * transition has resulted in the info box being partially or wholly outside ofthe vierwport. We remove the
			 * animated move and resize classes, force the debug info back into view port then reapply them. The timeout
			 * is probably unnecessary.
			 * @function
			 * @private
			 * @param {Event} $event The wrapped transitionend event.
			 */
			function transitionendEvent($event) {
				var container;
				if (!$event.defaultPrevented && (container = document.getElementById(CONTAINER_ID)) && container === $event.target) {
					try {
						classList.remove(container, CLASS.ANIMATE_MOVE);
						classList.remove(container, CLASS.ANIMATE_RESIZE);
						positionable.forceToViewPort(container);
					}
					finally {
						timers.setTimeout(function() {
							classList.add(container, CLASS.ANIMATE_MOVE);
							classList.add(container, CLASS.ANIMATE_RESIZE);
						}, 0);
					}
				}
			}

			/**
			 * Hide the debug info container and clear the last used debug info.
			 * @function
			 * @private
			 */
			function hide() {
				var container = document.getElementById(CONTAINER_ID);
				if (container) {
					shed.hide(container);
				}
			}

			/**
			 * Removes the debug info instruction box. We store the removal so we do not show the info box again.
			 * @function
			 * @private
			 * @param {Element} element The instruction box to remove. This is a bit dangerous and should be fixed.
			 * @todo Do not allow an element to be passed in to this function: get the instruction box here.
			 */
			function removeInstructions(element) {
				element.parentNode.removeChild(element);
				storage.put(STORE_KEY, true, true);
			}

			/**
			 * Shows the debug info container and the debug info for the target element.
			 * @function
			 * @private
			 * @param {Element} element the button calling this function.
			 * @returns {Boolean} true if any debug info is shown.
			 */
			function showDebugInfo(element) {
				var targetId,
					result = false,
					infoHTML = "",
					entries, i, len;

				if ((targetId = element.getAttribute(ATTRIB_HAS_DEBUG_INFO))) {
					lastInfoTarget = element.id;

					if ((entries = getRegistryEntriesFor(targetId))) {
						for (i = 0, len = entries.length; i < len; ++i) {
							infoHTML += entryToHTML(entries[i], ((i === len - 1) ? element : null), (i === 0));
						}
						infoHTML += getAttribHTML(element);
					}

					result = showInfoHelper(infoHTML, element);
				}
				return result;
			}

			/**
			 * Gets all of the registry entries associated with a particular ID string which is a space separated list
			 * of IDs.
			 * @function
			 * @private
			 * @param {String} idString A space separated list of debugInfo ids.
			 * @returns {Array} an array of registry objects
			 * @todo anonymise the map function.
			 */
			function getRegistryEntriesFor(idString) {
				var result = [],
					idArray = idString.split(/\s+/);

				function _getEntry(next) {
					return registry[next];
				}
				result = idArray.map(_getEntry);
				return result;
			}

			/**
			 * Convert a registry entry object onto a debug info HTML string.
			 * @function
			 * @private
			 * @param {Object} entry A debug info registry entry.
			 * @param {Element} [element] An element associated with the entry - the element for which we are showing the debug info.
			 * @param {Boolean} [showId] If true show the id attribute of the XML element which generated the registry
			 *    entry. This may not be the ID of the element being debugged (but usually is).
			 * @returns {String} A HTML string able to be displayed as debug info.
			 */
			function entryToHTML(entry, element, showId) {
				var result = "", details, i, elementId;

				result += toTag(DL, false, "class='" + CLASS.INFO + " " + CLASS.DL_COLUMN + "' id='" + entry["id"] + "'");
				if (showId && (elementId = entry["javaId"])) {
					result += toTag(DT) + "WComponent ID" + toTag(DT, true) + toTag(DD) + elementId + toTag(DD, true);
				}
				result += javaClassTypeInfoToHTML(entry["javaclass"], entry["javatype"]);
				if ((details = entry["detail"]) && details.length) {
					for (i = 0; i < details.length; ++i) {
						result += toTag(DT) + details[i]["key"] + toTag(DT, true) + toTag(DD) + details[i]["value"] + toTag(DD, true);
					}
				}
				if (element && element.hasAttribute(DEBUG_ATTRIB)) {
					result += toTag(DT) + "HTML debug info" + toTag(DT, true) + toTag(DD) + parseAttrib(element.getAttribute(DEBUG_ATTRIB)) + toTag(DD, true);
				}
				result += toTag(DL, true);
				return result;
			}

			/**
			 * Set up a breadcrumb trail of ancestor elements of the target element.
			 * @function
			 * @private
			 * @param {Element} targetElement The element for which we are showing debug info.
			 */
			function populateBreadcrumbs(targetElement) {
				var bchtml = "", parent, ancestry = [], nextCrumb,
					container = document.getElementById(CONTAINER_ID),
					breadcrumbContainer = BREADCRUMB_WIDGET.findDescendant(container);

				parent = targetElement;

				while ((parent = parent.parentNode) && parent.tagName !== tag.HTML) {
					if (DEBUG_INFO_TARGET.isOneOfMe(parent)) {
						ancestry.unshift(parent);
					}
				}

				while (ancestry.length) {
					nextCrumb = ancestry.shift();
					bchtml += makeInfoButton(nextCrumb.id, nextCrumb.getAttribute(CLASS_ATTRIB));
				}

				bchtml += toTag(tag.LI) + targetElement.getAttribute(CLASS_ATTRIB) + toTag(tag.LI, true);

				breadcrumbContainer.innerHTML = "";
				breadcrumbContainer.innerHTML = bchtml;
			}

			/**
			 * Populate the list of debuggable child elements.
			 * @function
			 * @private
			 * @param {Element} targetElement The element being debugged.
			 */
			function populateNextChildList(targetElement) {
				var html = "", container, childContainer;

				function _filter(next) {
					return (DEBUG_INFO_TARGET.findAncestor(next.parentNode) === targetElement);
				}

				function _makeTarget(el) {
					var elClass = el.getAttribute(CLASS_ATTRIB), text;
					if (elClass) {
						text = textContent.get(el);
						html += makeInfoButton(el.id, elClass.substr(elClass.lastIndexOf(".") + 1) + " (" + text.substr(0, 12) + (text.length > 12 ? "..." : "") + ")");
					}
				}

				(Array.prototype.filter.call(DEBUG_INFO_TARGET.findDescendants(targetElement), _filter)).forEach(_makeTarget);

				if ((container = document.getElementById(CONTAINER_ID)) && (childContainer = DEBUGABLE_CHILDREN.findDescendant(container))) {
					childContainer.innerHTML = "";
					childContainer.innerHTML = html;
				}

			}

			/**
			 * Make a HTML button element which will move the info to another element.
			 * @function
			 * @private
			 * @param {String} id The id of the element the button will set as the current debug element.
			 * @param {String} content The content to show on the button.
			 * @returns {String} A HTML snippet for a button element.
			 */
			function makeInfoButton(id, content) {
				var LI = tag.LI, BUTTON = tag.BUTTON;
				return toTag(LI) + toTag(BUTTON, false, "type='button' class='" + INFO_BTN_AS_LINK + "' value='" + id + "'") + content + toTag(BUTTON, true) + toTag(LI, true);
			}

			/**
			 * Hide any visible debug info.
			 * @function
			 * @private
			 * @todo anonymise the forEach iterator function.
			 */
			function removeDebugInfoDetails() {
				var infoContainer = getInfoContainer(),
					info, thisDebugWidget;

				function _removeClass(nextTarget) {
					classList.remove(nextTarget, CLASS.IS_BEING_DEBUGGED);
				}

				while ((info = infoContainer.firstChild)) {
					thisDebugWidget = DEBUG_INFO_TARGET.extend("", {"${wc.debug.debugInfo.attrib.hasDebugInfo}": info.id});
					Array.prototype.forEach.call(thisDebugWidget.findDescendants(document.body), _removeClass);
					infoContainer.removeChild(info);
				}
			}


			/**
			 * Convert an HTML attribute value into a HTML string to be used in the debug info. This takes the value
			 * of the specific data-wc-debug* attribute[s].
			 * @function
			 * @private
			 * @param {String} attrib The value of a HTML element attribute.
			 * @returns {String} A HTML snippet
			 * @todo could do with a clean-up.
			 */
			function parseAttrib(attrib) {
				var match,
					j,
					arr,
					result = "";
				if (attrib) {
					if (RX.test(attrib)) {
						RX.lastIndex = 0;
						while ((match = RX.exec(attrib))) {
							// match[0] is the total match, we want the captured groups
							if (match[1]) {
								result += makeP(match[1]);
							}
							if (match[2]) {
								arr = match[2].split(",");
								for (j = 0; j < arr.length; ++j) {
									result += parseAttribContent(arr[j]);
								}
							}
							if (match[3]) {
								result += makeP(match[3]);
							}
						}
					}
					else {  // we have error/warning info without a list of nesting errors
						result += attrib.replace(NL_RX, "<br/>");
					}
				}
				return result;
			}

			/**
			 * Makes a HTML P element containing some 'stuff'
			 * @function
			 * @private
			 * @param {String} pContent The content for the paragraph.
			 * @returns {String} A String representation of a HTML paragraph.
			 */
			function makeP(pContent) {
				var P = tag.P;
				return toTag(P) + pContent.replace(NL_RX, "<br/>") + toTag(P, true);
			}

			/**
			 * Convert the debug attribute inner content into a HTML snippet to be included in the debug info.
			 * @function
			 * @private
			 * @param {String} str The attribute content to be parsed.
			 * @returns {String} A HTML snippet to be displayed in the debuig info.
			 */
			function parseAttribContent(str) {
				var element,
					BUTTON = tag.BUTTON,
					result = toTag(DL, false, "class='" + CLASS.DL_COLUMN + "'"),
					entries,
					targetId;
				if ((element = document.getElementById(str))) {
					result += toTag(DT) + "HTML Element" + toTag(DT, true) + toTag(DD) + toTag(BUTTON, false, "type='button' class='" + INFO_BTN_AS_LINK + "' value='" + str + "'") + element.tagName + " (id: " + element.id + ")" + toTag(BUTTON, true) + toTag(DD, true);
					if ((targetId = element.getAttribute(ATTRIB_HAS_DEBUG_INFO)) && (entries = getRegistryEntriesFor(targetId)) && entries.length) {
						entries.forEach(function(entry) {
								result += javaClassTypeInfoToHTML(entry["javaclass"], entry["javatype"]);
							});
					}
				}
				else {
					result += toTag(DT) + "Unidentified WComponent" + toTag(DT, true) + toTag(DD);
					switch (str) {
						case "text":
							result += "WStyledText";
							break;
						case "abbr":
							result += "WAbbreviatedText";
							break;
						case "hr":
							result += "WHorizontalRule";
							break;
						default:
							result += "W" + str.substr(0, 1).toUpperCase() + str.substr(1, str.length);
							break;
					}
				}
				result += toTag(DD, true) + toTag(DL, true);

				return result;
			}

			/**
			 * Convert the Java class name and Type names to HTML snippets.
			 * @function
			 * @private
			 * @param {String} javaclass
			 * @param {String} javatype
			 * @returns {String} A HTML string.
			 */
			function javaClassTypeInfoToHTML(javaclass, javatype) {
				var result = toTag(DT) + "class" + toTag(DT, true) + toTag(DD) + javaclass + toTag(DD, true), A = tag.A;
				if (javatype) {
					result += toTag(DT) + "type" + toTag(DT, true) + toTag(DD) + toTag(A, false, "href='${wc.debug.debugInfo.javadocRoot}" + javatype.replace(/\./g, "/") + ".html' target='javadoc'") + javatype + toTag(A, true) + toTag(DD, true);
				}
				return result;
			}

			/**
			 * This is a helper function that actually shows the debug info dialog and sets its content.
			 * @function
			 * @private
			 * @param {String} html The content for the debug info dialog.
			 * @param {Element} element The element being debugged.
			 * @returns {Boolean} true if the debug info container is able to be shown.
			 */
			function showInfoHelper(html, element) {
				var container = document.getElementById(CONTAINER_ID),
					infoContainer = getInfoContainer(),
					result = false, isHidden;

				if (container && infoContainer) {
					result = true;
					isHidden = shed.isHidden(container);
					if (!isHidden) {
						removeDebugInfoDetails();
						positionable.reset(container);
					}
					infoContainer.innerHTML = html;
					populateBreadcrumbs(element);
					populateNextChildList(element);
					if (isHidden) {
						classList.add(container, CLASS.ANIMATE_SHOW);
					}
					shed.show(container);
				}
				return result;
			}

			/**
			 * Get the content of the ERROR, WARNING, INFO and optionally DEBUG debug attributes of the element being
			 * debugged and convert their content into a HTML snippet to be included in the debug info dialog.
			 * @function
			 * @private
			 * @param {Element} element The element being debugged.
			 * @param {Boolean} includeDebug if true include the content of the DEBUG data-wc-debug* attribute.
			 * @returns {String} a HTML snippet containing the formatted content of these attributes.
			 */
			function getAttribHTML(element, includeDebug) {
				var result = makeMessage("wc_error", "Errors", element.getAttribute(ERROR_ATTRIB)) +
							makeMessage("wc_warning", "Warnings", element.getAttribute(WARN_ATTRIB)) +
							makeMessage("wc_info", "Information", element.getAttribute(INFO_ATTRIB));
				if (includeDebug) {
					result += makeMessage("wc_debug", "Debug", element.getAttribute(DEBUG_ATTRIB));
				}
				return result;
			}

			/**
			 * Makes a debugging info message to include in the debug info. This is purely client-side information such
			 * as specification violations, accessibility warnings etc.
			 * @function
			 * @private
			 * @param {String} level The message level.
			 * @param {String} headingContent The content of the HTML heading applied to the message.
			 * @param {String} messageContent The actual debugging information to be output; this will be parsed.
			 * @returns {String} a HTML snippet to include in the debug info.
			 */
			function makeMessage(level, headingContent, messageContent) {
				var H3 = tag.H3, result = "";
				if (messageContent) {
					result = toTag(H3, false, "class='" + level + "'") + headingContent + toTag(H3, true) + parseAttrib(messageContent);
				}
				return result;
			}

			/**
			 * Get the sub-element of the debug info dialog which actually holds the debug info messages.
			 * @function
			 * @private
			 * @returns {?Element} The container.
			 */
			function getInfoContainer() {
				var container = document.getElementById(CONTAINER_ID),
					result;
				if (container) {
					result = INFO_CONTAINER_WIDGET.findDescendant(container);
				}
				return result;
			}

			/**
			 * Count the number of Element nodes in the current document and set it as the value of an attribute on
			 * document.body.
			 * @function
			 * @private
			 */
			function countElements() {
				document.body.setAttribute("data-wc-nodeCount", document.getElementsByTagName("*").length);
			}

			// Public for testing
			/** @ignore */
			this._TEST = false;
			/** @ignore */
			this._getRegistry = function() {
				return registry;
			};
			/** @ignore */
			this._clearRegistry = function() {
				registry = {};
				storage.erase(STORE_KEY, true);
			};
			/**
			 * @param {Element} container The debug info container.
			 * @ignore */
			this._getBreadcrumbContainer = function(container) {
				return BREADCRUMB_WIDGET.findDescendant(container);
			};
			// public for testing event handlers
			/** @ignore */
			this._clickEvent = clickEvent;
			/** @ignore */
			this._keydownEvent = keydownEvent;
//			this._touchstartEvent = touchstartEvent;
//			this._touchendEvent = touchendEvent;
		}
		return debugInfo;
	});
