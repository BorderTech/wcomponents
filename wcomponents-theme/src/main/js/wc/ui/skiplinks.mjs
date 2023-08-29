import initialise from "wc/dom/initialise";
import processResponse from "wc/ui/ajax/processResponse";

// TODO make this a custom element
const template = (id, title) => `<a class='wc-skiplink' href='#${id}'>${title}</a>`;

function setup() {
	const ARIA_HIDDEN = "aria-hidden";
	const containerSelector = ".wc-skiplinks";
	const panelOfInterestSelector = "[data-wc-title][accesskey]";
	const b = document.body;

	const skipLinkContainer = b.querySelector(containerSelector);
	if (skipLinkContainer) {
		skipLinkContainer.innerHTML = "";

		Array.from(b.querySelectorAll(panelOfInterestSelector)).forEach(function (next) {
			const id = next.id, title = next.getAttribute("data-wc-title");
			if (id && title) {
				skipLinkContainer.insertAdjacentHTML("beforeend", template(id, title));
			}
		});

		if (skipLinkContainer.innerHTML) {
			skipLinkContainer.removeAttribute(ARIA_HIDDEN);
			// ensure the skipLinks are the first child of the document body
			if (b.firstChild !== skipLinkContainer) {
				b.insertBefore(skipLinkContainer, b.firstChild);
			}
		} else {
			skipLinkContainer.setAttribute(ARIA_HIDDEN, "true");
		}
	}
}

initialise.register({
	postInit: function () {
		setup();
		processResponse.subscribe(setup, true);
	}
});
