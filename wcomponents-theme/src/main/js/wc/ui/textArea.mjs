/**
 * Provides functionality for textareas. For textareas that have a maxlength property the number of remaining characters
 * are shown in a ticker.
 *
 * <p>We deliberately bypass the browser native implementation of maxlength on textareas. This is to allow users to work
 * in the textarea before submitting the page. For example a user may paste in a large body of text knowing that it is
 * too long. The user should be allowed to do this and then work within the textarea to reduce the length before
 * submitting. If the length of the textarea is constrained then the user would be forced to open another application
 * (for example a text editor) paste the large text there, reduce the length of the text (without an immediate character
 * count) and then paste into the textarea. The HTML5 browsers have it wrong, we have it right... (or not...)</p>
 *
 * <p>A series of TEXTAREA bugs in IE8 (not related to WComponents code) makes the characters remaining ticker
 * impossible to implement robustly in IE8, so we have removed it.</p>
 *
 * <p>Beware, IE has a feature (still in IE9) in that whenever you modify the DOM in any way the undo stack is
 * destroyed!!</p>
 *
 * <p>The relationship between the counter and the textarea is guided by the ARIA authoring practices:
 * <q cite="http://www.w3.org/TR/wai-aria-practices/#focus_change">
 * the dynamic content (the character count) must be owned by the textarea as a live region
 * </q></p>
 *
 * @module
 *
 */

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import i18n from "wc/i18n/i18n.mjs";
import debounce from "wc/debounce.mjs";
import wrappedInput from "wc/dom/wrappedInput.mjs";

const events = [],
	INITED_KEY = "__maxlength_inited__",
	TEXTAREA = "textarea",
	TEXTAREA_CONSTRAINED = [`${TEXTAREA}[maxlength]`, `${TEXTAREA}[data-wc-maxlength]`, `${TEXTAREA}[data-wc-min]`, `${TEXTAREA}[minlength]`].join(),
	TICKER_DELAY = 250;

const instance = {
	/**
	 * Get the description of a textarea component.
	 * @function module:wc/ui/textarea.getWidget
	 * @param {Boolean} [withConstraints] true to only get constrained text areas (with max-length and/or
	 *    min-length constraints).
	 * @returns {string}
	 */
	getWidget: withConstraints => withConstraints ? TEXTAREA_CONSTRAINED : TEXTAREA,

	/**
	 * Get the counter element related to a text area.
	 * @function module:wc/ui/textarea.getCounter
	 * @param {Element} element A text field with a maxlength property.
	 * @returns {HTMLElement} The counter element associated with this field (if any).
	 */
	getCounter: function(element) {
		const wrapper = element.matches(TEXTAREA) ? wrappedInput.getWrapper(element) : element;
		if (wrapper) {
			return document.getElementById(`${wrapper.id}_tick`);
		}
		return null;
	},
	/**
	 * The minimum number of characters allowed in a textarea.
	 * @function module:wc/ui/textarea.getMinlength
	 * @param {Element} element A textarea.
	 * @returns {number} The minimum character count for this textarea or 0 if it is not constrained.
	 */
	getMinlength: function(element) {
		const result = element.getAttribute("minlength") || element.getAttribute("data-wc-min");
		if (result) {
			return parseInt(result);
		}
		return 0;
	},
	/**
	 * The maximum number of characters allowed in a textarea.
	 * @function module:wc/ui/textarea.getMaxlength
	 * @param {Element} element A textarea.
	 * @returns {number} The maximum character count for this textarea or 0 if it is not constrained.
	 */
	getMaxlength: function(element) {
		const result = element.getAttribute("maxlength") || element.getAttribute("data-wc-maxlength");
		if (result) {
			return parseInt(result);
		}
		return 0;
	},
	/**
	 * Get the 'real' length of the string in a textarea including double chars for new lines.
	 *
	 * @function
	 * @public
	 * @param {HTMLTextAreaElement} element The textarea to test
	 * @returns {Number} The 'length' of the value string amended for new lines.
	 */
	getLength: function(element) {
		const raw = element.value;
		if (!raw) {
			return 0;
		}
		const arr = raw.split("\n");
		const arrLen = arr.length;
		if (arrLen === 1) {
			return raw.length;
		}
		let len = 0;
		arr.forEach((next, idx) => {
			const l = next.length;
			if (idx < arrLen - 1) {
				len += l + 2;  // add two chars for each new line after an existing line of text
			} else if (next) {  // if the last item in the array is content add its length
				len += l;
			}
			/*
			else { // if the last member of the array is an empty string then this means the last char entered by the user was a return and its extra chars were counted above.

			}
			*/
		});
		return len;
	}
};

/**
 * @param {Element} element
 */
function hideCounter(element) {
	const counter = instance.getCounter(element);
	if (counter && !counter.hidden) {
		counter.hidden = true;
	}
}

/**
 * @param {Element} element
 */
function showCounter(element) {
	const counter = instance.getCounter(element);
	if (counter && counter.hidden) {
		counter.hidden = false;
	}
}

/**
 * There has been a change to the field's content, recalculate the maxlength counter.
 *
 * @function
 * @private
 * @param {Element} element The field in question.
 */
const tick = debounce(element => {
	const counter = instance.getCounter(element);
	if (counter) {
		const maxLength = instance.getMaxlength(element);
		const count = (maxLength - instance.getLength(element));
		i18n.translate("chars_remaining", count).then(/** @param {string} title */title => {
			counter.setAttribute("value", String(count));
			counter.setAttribute("title", title);
			const ERR = "wc-err";
			if (count < 0) {
				/* NOTE: this is not part of revalidation since we just want to
				 * set a visual flag on the ticker, not insert a visible error message
				 * since maxLength violation is an allowed transient state until
				 * such time as the control is part of a form submission.*/
				counter.classList.add(ERR);
			} else {
				counter.classList.remove(ERR);
			}
		});
	}
}, TICKER_DELAY);

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
 * @param {FocusEvent & { target: HTMLTextAreaElement }} $event The current event.
 */
function focusEvent({ target }) {
	if (target.matches(TEXTAREA)) {
		if (!target[INITED_KEY]) {
			target[INITED_KEY] = true;
			if (target.matches(TEXTAREA_CONSTRAINED)) {
				events.push(event.add(target, { type: "input", listener: ({ target: t }) => tick(t), capture: true }));
				events.push(event.add(target, { type: "blur", listener: ({ currentTarget }) => hideCounter(currentTarget), capture: true }));
				tick(target);  // tick on focusIn to set initial title attribute (not available in XSLT1)
			}
		}
		tick(target);
		showCounter(target);
	}
}

initialise.register({
	/**
	 * Set up event handlers.
	 * @function module:wc/ui/textarea.initialise
	 * @param {Element} element the element being initialised, usually `document.body`
	 */
	initialise: element => events.push(event.add(element, { type: "focus", listener: focusEvent, capture: true })),

	/**
	 * Unsubscribes event listeners etc.
	 */
	deinit: () => event.remove(events)
});

export default instance;

// Handle registration of rich text areas
const rtfTag = "wc-rtf";
class WRichTextField extends HTMLElement {
	connectedCallback() {
		import("wc/ui/rtf.mjs").then(({ default: c }) => {
			c.register([this.parentElement.getAttribute("id")]);
		});
	}
}

if (!customElements.get(rtfTag)) {
	customElements.define(rtfTag, WRichTextField);
}
