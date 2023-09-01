/**
 * Provides functionality to provide a back to top link which is scroll and viewport size aware.
 */

import i18n from "wc/i18n/i18n";
import event from "wc/dom/event";
import focus from "wc/dom/focus";
import getViewportSize from "wc/dom/getViewportSize";
import shed from "wc/dom/shed";
import wcconfig from "wc/config";
import initialise from "wc/dom/initialise";

/**
 * Custom configuration
 * @type Object
 */
let config;
/**
 * This property can be set to a positive integer to force showing the scroll to top link at X pixels of
 * scroll. If it is not set (or set to 0) then the scroll to top link will appear when more than one
 * viewport height of scroll has occurred.
 *
 * Can be set in module configuration as property "scroll".
 *
 * @var
 * @type {number}
 * @private
 * @default 0
 */
let minScrollBeforeShow = 0;

/**
 * The description of the back to top link HTML artifact.
 * @constant
 * @type {string}
 * @private
 */
const tagName = "wc-backtotop";
const className = "wc_btt";
const backtotopSelector = tagName;

const template = () => `<a href="#" class="${className}"><i class='fa fa-chevron-circle-up fa-5x'></i><span class='wc-off'>
	${i18n.get("back_to_top")}
</span></a>`;

/**
 * Event listener to show or hide the back to top link after scroll or resize events.
 *
 * @function
 * @private
 */
function genericEvent() {
	const scroll = document.documentElement.scrollTop || document.body.scrollTop;
	let min;

	if (minScrollBeforeShow > 0) {
		min = minScrollBeforeShow;
	} else {
		min = getViewportSize().height;
	}
	showHide((scroll > min));
}

/**
 * Toggles the visibility of the back to top link based on the argument show.
 *
 * @function
 * @private
 * @param {boolean} show If true the back to top link is shown, otherwise it is hidden.
 * @param {BackToTop} [target] Optionally provide the instance to show/hide.
 */
function showHide(show, target) {
	let link = target || document.body.querySelector(backtotopSelector);
	if (show) {
		if (!link) {

			link = /** @type {BackToTop} */ (document.createElement(tagName));
			link = document.body.appendChild(link);
		}
		shed.show(link, true);  // nothing needs to be notified that the back to top link is showing
	} else if (link) {
		shed.hide(link, true);  // nothing needs to be notified that the back to top link is hidden
	}
}

class BackToTop extends HTMLElement {
	constructor() {
		super();

		/**
		 * Click event handler to scroll the page when the back to top link is clicked.
		 *
		 * @function
		 * @private
		 * @param {MouseEvent} $event The click event.
		 */
		event.add(this, "click", ($event) => {
			const docEl = this.ownerDocument.documentElement;
			const { defaultPrevented } = $event;
			if (defaultPrevented || this.disabled) {
				return;
			}
			$event.preventDefault();
			if (docEl.scrollIntoView) {
				docEl.scrollIntoView();
			} else {
				docEl.scrollTop = 0;
			}
			focus.focusFirstTabstop(docEl);  // this would actually be sufficient if we could guarantee a focusable element.
		});

		/**
		 * Hide the back to top link when the ESCAPE key is pressed.
		 *
		 * @function
		 * @private
		 * @param {KeyboardEvent} $event The wrapped keydown event.
		 */
		event.add(this, "keydown", ({ key }) => {
			if (key === "Escape") {
				showHide(false, this);
			}
		});
	}

	connectedCallback() {
		this.innerHTML = template();
	}

	/**
	 *
	 * @return {boolean}
	 */
	get disabled() {
		return this.hasAttribute("disabled");
	}

	/**
	 *
	 * @param {string} val
	 */
	set disabled(val) {
		if (val) {
			showHide(false, this);  // just in case the link is showing at the time it is disabled.
			this.setAttribute("disabled", val);
		} else {
			this.removeAttribute("disabled");
		}
	}
}

initialise.register({ initialise: () => {
	config = wcconfig.get("wc/ui/backToTop", {
		scroll: minScrollBeforeShow
	});
	minScrollBeforeShow = config.scroll;

	if (!customElements.get(tagName)) {
		event.add(globalThis, { type: "scroll", listener: genericEvent, passive: true });
		event.add(globalThis, { type: "resize", listener: genericEvent, passive: true });
		customElements.define(tagName, BackToTop);
	}
}});

/**
* @typedef {Object} config Configuration for the back to top link.
* @property {number} scroll The number of pixels to scroll before showing the back to top link. If 0 then the scroll to top link will appear
*  when more than one viewport height of scroll has occurred.
*/
