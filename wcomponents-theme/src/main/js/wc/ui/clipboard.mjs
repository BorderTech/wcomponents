import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import debounce from "wc/debounce.mjs";

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
	const doc = element.ownerDocument;
	// @ts-ignore
	if (targetId) {
		const target = doc.getElementById(targetId);
		if (target) {
			const text = target.innerText;
			if (text) {
				doc.defaultView.navigator.clipboard.writeText(text).then(function() {
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
	 * @param {Element} element
	 */
	initialise: function(element) {
		const doc = element.ownerDocument;
		event.add(element, "click", debounce(clickEvent, 250));
		// @ts-ignore
		doc.defaultView.navigator.permissions.query({ name: "clipboard-write" }).then(permissionStatus => {
			if (permissionStatus.state !== "denied") {
				const body = doc.body;
				body.classList.add("wc-clipwrite");
			}
		});
	}
});
