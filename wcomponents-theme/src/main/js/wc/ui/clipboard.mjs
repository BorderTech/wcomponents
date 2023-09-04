import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import debounce from "wc/debounce";

/**
 *
 * @param {MouseEvent & {target: HTMLElement}} $event
 */
function clickEvent($event) {
	/** @type {HTMLButtonElement} */
	const button = $event.target.closest("button.wc-clipboard");
	if (button) {
		copyContent(button);
	}
}

/**
 *
 * @param {HTMLButtonElement} element
 */
function copyContent(element) {
	const targetId = element.getAttribute("aria-controls");
	if (targetId) {
		const target = document.getElementById(targetId);
		if (target) {
			const text = target.innerText;
			if (text) {
				navigator.clipboard.writeText(text).then(function() {
					console.log("Copied to clipboard", text);
				}).catch(function(error) {
					console.info("Error copying to clipboard", error);
				});
			}
		}
	}
}

initialise.register({
	/**
	 *
	 * @param {HTMLElement} element
	 */
	initialise: function(element) {
		event.add(element, "click", debounce(clickEvent, 250));
	}
});
