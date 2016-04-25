/**
 * Provides functionality for textareas. For textareas that have a maxlength property the number of remaining characters
 * are shown in a ticker.
 *
 * <p>We deliberately bypass the browser native implementation of maxlength on textareas. This is to allow users to work
 * in the textarea before submitting the page. For example a user may paste in a large body of text knowing that it is
 * too long. The user should be allowed to do this and then work within the textarea to reduce the length before
 * submitting. If the length of the textarea is constrained then the user would be forced to open another application
 * (for example a text editor) paste the large text there, reduce the length of the text (without an immediate character
 * count) and then paste into the textarea. The HTML5 browsers have it wrong, we have it right.. (or not...)</p>
 *
 * <p>A series of TEXTAREA bugs in IE8 (not related to WComponents code) makes the characters remaining ticker
 * impossible to implement robustly in IE8 so we have removed it.</p>
 *
 * <p>Beware, IE has a feature (still in IE9) in that whenever you modify the DOM in any way the undo stack is
 * destroyed!!</p>
 *
 * <p>The relationship between the counter and the textarea is guided by the ARIA authoring practices:
 * <q cite="http://www.w3.org/TR/wai-aria-practices/#focus_change">
 *	the dynamic content (the character count) must be owned by the textarea as a live region
 * </q></p>
 *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires external:lib/sprintf
 * @requires module:wc/timers
 *
 * @todo Document private members, fix source order.
 */
define(["wc/dom/attribute",
		"wc/dom/classList",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"lib/sprintf",
		"wc/timers"],
	/** @param attribute wc/dom/attribute @param classList wc/dom/classList @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param sprintf lib/sprintf @param timers wc/timers @ignore */
	function(attribute, classList, event, initialise, shed, Widget, i18n, sprintf, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/textarea~TextArea
		 * @private
		 */
		function TextArea() {
			var INITED_KEY = "__maxlength_inited__",
				TEXTAREA = new Widget("textarea"),
				TEXTAREA_MAXLENGTH = TEXTAREA.extend("", {"maxLength": null}),
				TEXTAREA_MAXLENGTH_FAUX = TEXTAREA.extend("", {"${wc.ui.maxLength.attribute.maxlength}": null}),
				TEXTAREA_CONSTRAINED = [TEXTAREA_MAXLENGTH, TEXTAREA_MAXLENGTH_FAUX, TEXTAREA.extend("", {"${wc.common.attrib.min}": null})],
				TICKER_DELAY = 250,
				tickerTimeout;


			function hideCounter(element) {
				var counter;
				if ((counter = instance.getCounter(element)) && !shed.isHidden(counter)) {
					shed.hide(counter, true);
				}
			}

			function showCounter(element) {
				var counter;
				if ((counter = instance.getCounter(element)) && shed.isHidden(counter)) {
					shed.show(counter, true);
				}
			}

			/**
			 * Get the 'real' length of the string in a textarea including double chrs for new lines.
			 *
			 * @function
			 * @private
			 * @param {Element} element The textarea to test
			 * @returns {Number} The 'length' of the value string amended for new lines.
			 */
			function getLength(element) {
				var len = 0, raw = element.value, arr, arrLen;
				if (!raw) {
					return 0;
				}
				arr = raw.split("\n");
				arrLen = arr.length;
				if (arrLen === 1) {
					return raw.length;
				}
				arr.forEach(function(next, idx) {
					var l = next.length;
					if (idx < arrLen - 1) {
						len += l + 2; // add two chars for each new line after an existing line of text
					}
					else if (next) { // if the last item in the array is content add its length
						len += l;
					}
					/*
					else { // if the last member of the array is an empty string then this means the last char entered by the user was a return and its extra chars were counted above.

					}
					*/
				});
				return len;
			}

			/**
			 * There has been a change to the field's content, recalculate the maxlength counter.
			 *
			 * @function
			 * @private
			 * @param {Element} element The field in question.
			 */
			function tick(element) {
				var maxLength, count, counter, ERR = "wc_error";
				if ((counter = instance.getCounter(element))) {
					maxLength = instance.getMaxlength(element);
					count = (maxLength - getLength(element));
					counter.setAttribute("value", count);
					counter.setAttribute("title", sprintf.sprintf(i18n.get("${wc.ui.maxlength.i18n.message}", count)));
					if (count < 0) {
						/* NOTE: this is not part of revalidation since we just want to
						 * set a visual flag on the ticker, not insert a visible error message
						 * since maxLength violation is an allowed transient state until
						 * such time as the control is part of a form submission.*/
						classList.add(counter, ERR);
					}
					else {
						classList.remove(counter, ERR);
					}
				}
			}

			/**
			 * There has been a change to the field's content, queue up a recalculation of the maxlength counter.
			 *
			 * @function
			 * @private
			 * @param {Element} element The textarea requiring the ticker.
			 */
			function queueTick(element) {
				if (tickerTimeout) {
					timers.clearTimeout(tickerTimeout);
					tickerTimeout = null;
				}
				tickerTimeout = timers.setTimeout(tick, TICKER_DELAY, element);
			}

			/*
			 * Responds to new input in a textual form field.
			 * @param {Event} $event The current event.
			 */
			function handleInput($event) {
				var which = $event.keyCode,
					element = $event.target;

				if (which === undefined || (which < KeyEvent.DOM_VK_END || which > KeyEvent.DOM_VK_DOWN)) {  // would be undefined if called from a non-key driven event
					queueTick(element);
				}
			}
			/*
			 * When the text field loses focus we must hide the counter
			 */
			function blurEvent($event) {
				hideCounter($event.currentTarget);
			}

			/**
			 * Check to see if an element with a maxlength has been focused
			 * and wire up events and show the counter if necessary.
			 *
			 * NOTE: browsers which do not support event capture do not get a ticker.
			 * This is because of a series of bugs in IE8 which make it impossible to
			 * have the characters remaining ticker AND keep the undo stack AND not
			 * trigger a cursor reset bug if the textarea element has content which
			 * includes a soft wrap, a hard break and has enough lines of text to
			 * cause a scroll (this is more common than it sounds).
			 *
			 * @function
			 * @private
			 * @param {Event} $event The current event.
			 */
			function focusEvent($event) {
				var element = $event.target, canCapture;
				if (TEXTAREA.isOneOfMe(element)) {
					canCapture = event.canCapture;
					if (!attribute.get(element, INITED_KEY)) {
						attribute.set(element, INITED_KEY, true);
						if (Widget.isOneOfMe(element, TEXTAREA_CONSTRAINED)) {
							if (canCapture) {  // see note in comment for this.initialise
								event.add(element, event.TYPE.input, handleInput, null, null, true);
								event.add(element, event.TYPE.blur, blurEvent, null, null, true);
								tick(element);  // tick on focusIn to set initial title attribute (not available in XSLT1)
							}
						}
					}
					if (canCapture) {
						queueTick(element);
						showCounter(element);
					}
				}
			}

			/**
			 * Set up event handlers.
			 * @function module:wc/ui/textarea.initialise
			 * @param {Element} element the element being initialised, usually document.body
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Get the description of a textarea component.
			 * @function module:wc/ui/textarea.getWidget
			 * @param {Boolean} [withConstraints] true to only get constrained text areas (with max-length and/or
			 *    min-length constraints).
			 * @returns {module:wc/dom/Widget}
			 */
			this.getWidget = function(withConstraints) {
				return (withConstraints ? TEXTAREA_CONSTRAINED : TEXTAREA);
			};

			/**
			 * Get the counter element related to a text area.
			 * @function module:wc/ui/textarea.getCounter
			 * @param {Element} element A text field with a maxlength property.
			 * @returns {?Element} The counter element associated with this field (if any).
			 */
			this.getCounter = function(element) {
				return document.getElementById((element.id + "_tick"));
			};

			/**
			 * The the maximum number of characters allowed in a textarea.
			 * @function module:wc/ui/textarea.getMaxlength
			 * @param {Element} element A textarea.
			 * @returns {number} The maximum character count for this textarea or 0 if it is not constrained.
			 */
			this.getMaxlength = function(element) {
				var result = element.getAttribute("maxLength") || element.getAttribute("${wc.ui.maxLength.attribute.maxlength}");
				if (result) {
					result = parseInt(result);
				}
				return result || 0;
			};
		}

		var /** @alias module:wc/ui/textarea*/ instance = new TextArea();
		instance.constructor = TextArea;
		initialise.register(instance);
		return instance;
	});
