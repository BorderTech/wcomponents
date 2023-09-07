/**
 * Provides functionality required to set focus when invoking an internal link and to add label-like functionality to
 * the label surrogates used for various compound components.
 *
 */
import focus from "wc/dom/focus";
import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";

const LEGEND = "legend",
	FOR_ATTRIB = "data-wc-for",
	WIDGETS = [LEGEND, "a", `[${FOR_ATTRIB}]`].join();

/**
 *
 * @param {Element} element
 * @return {boolean}
 */
function actionClickEvent(element) {
	if (!shed.isDisabled(element)) {
		let target;
		if (element.matches(LEGEND)) {
			target = element.parentElement;
		} else if (element.hasAttribute(FOR_ATTRIB)) {
			target = document.getElementById(element.getAttribute(FOR_ATTRIB));
		} else if (element.hasAttribute("href")) {
			const url = element.getAttribute("href");
			if (url.indexOf("#") === 0) {
				target = document.getElementById(url.substring(1));
			}
		}
		if (target && !shed.isDisabled(target)) {
			if (focus.canFocus(target)) {
				focus.setFocusRequest(target);
				return true;
			}
			if (focus.canFocusInside(target)) {
				focus.focusFirstTabstop(target);
				return true;
			}
		}
	}
	return false;
}

/**
 * @param {MouseEvent} $event
 */
function clickEvent($event) {
	const { defaultPrevented, target } = $event;
	if (defaultPrevented || !(target instanceof  HTMLElement)) {
		return;
	}
	/** @type HTMLElement */
	const element = target.closest(WIDGETS);
	if (element && !shed.isDisabled(element)) {
		if (actionClickEvent(element)) {
			$event.preventDefault();
		}
	}
}

initialise.register({
	/**
	 * Initialisation function for internal link.
	 * @param {HTMLBodyElement} element The element being initialised
	 */
	initialise: element => event.add(element, "click", clickEvent)
});
