import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import Widget from "wc/dom/Widget";
import debounce from "wc/debounce";

const clipboardButton = new Widget("button", "wc-clipboard");

const instance = {
	initialise: function(element) {
		event.add(element, "click", debounce(clickEvent, 250));
	}
};

function clickEvent($event) {
	const button = clipboardButton.findAncestor($event.target);
	if (button) {
		copyContent(button);
	}
}

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

export default initialise.register(instance);
