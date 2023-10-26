import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import timers from "wc/timers.mjs";

const events = [],
	tooltipsSelector = "span[role='tooltip']",
	TOOLTIP_TTL = 5000;

let showing,
	isOpen = false;

/**
 * Provides a mechanism to expose access keys in the UI. This is done using a little "tooltip" style element which
 * is shown when the ALT/META key is pressed.
 */
const instance = {
	/**
	 * Get a toolTip from an element.
	 * @function module:wc/ui/tooltip.getTooltip
	 * @param {Element} element An HTML element which may contain a toolTip.
	 * @returns {HTMLElement} A toolTip element.
	 */
	getTooltip: element => element.querySelector(tooltipsSelector)
};

/*
 * Manages scheduled cleanups of any tooltips being shown - they will be
 * automatically hidden in n seconds unless another routine has hidden
 * them before that time.
 */
function scheduledCleanup(hiding) {
	if (hiding) {  // someone else is hiding the tooltips so cancel cleanup
		if (showing) {
			timers.clearTimeout(showing);
			showing = null;
		}
	} else {  // someone is (re)showing tooltips, (re)schedule a cleanup
		if (showing) {
			timers.clearTimeout(showing);
		}
		showing = timers.setTimeout(toggleTooltips, TOOLTIP_TTL, true);
	}
}

function toggleTooltips(hide) {
	const tooltips = Array.from(document.querySelectorAll(tooltipsSelector)),
		showHide = hide ? "hide" : "show";

	isOpen = !hide;
	for (const next of tooltips) {
		let nextId = next.id;
		let control = document.getElementById(nextId.slice(0, nextId.indexOf("_wctt")));
		if (!control || (control && !shed.isDisabled(control))) {
			shed[showHide](next, true);
		}
	}
	scheduledCleanup(hide);
}

/**
 * Handles a key down.
 * @param {KeyboardEvent} $event
 */
function keydownEvent($event) {
	if (!$event.defaultPrevented && !isOpen && $event.key === "Alt" && !($event.repeat)) {
		isOpen = true;
		toggleTooltips(false);
	}
}

/* NOTE IE8 does not fire keyup when releasing ALT if there is a previous keyup (such as releasing the accesskey key) and if the browser
 * native accesskey functionality (for menus) has been invoked.
 */
/**
 * Handles a key up.
 * @param {KeyboardEvent} $event
 */
function keyupEvent($event) {
	if (isOpen && !$event.defaultPrevented) {
		toggleTooltips(true);
	}
}


initialise.register({
	/**
	 * Initialise the tooltips by attaching necessary event listeners.
	 * @function module:wc/ui/tooltip.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: element => {
		events.push(event.add(element, "keydown", keydownEvent));
		events.push(event.add(element, "keyup", keyupEvent));
	},

	/**
	 * Unsubscribes event listeners etc.
	 */
	deinit: () => event.remove(events)
});

export default instance;
