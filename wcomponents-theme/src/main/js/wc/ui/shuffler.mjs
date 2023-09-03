import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import formUpdateManager from "wc/dom/formUpdateManager";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import ajaxRegion from "wc/ui/ajaxRegion";

const moveButtonQs = "button.wc_sorter";
const containerQs = ".wc-shuffler";
const shufflerSelect = "select.wc_shuffler";
const UP = "up",
	DOWN = "down",
	TOP = "top",
	BOTTOM = "bottom";

/**
 * @param {HTMLFormElement} form
 * @param {HTMLElement} stateContainer
 */
function writeState(form, stateContainer) {
	/**
	 * "Clean up" the state of the dual multi select control.
	 * I.E. ensure the correct options are selected/deselected in the submit element,
	 * based on the selections made in the available/chosen elements.
	 * @param {HTMLElement} container
	 */
	function _writeState(container) {
		/** @type {HTMLSelectElement} */
		const list = container.querySelector(shufflerSelect);

		if (list && !list.disabled) {
			const options = list.options;
			for (let i = 0; i < options.length; i++) {
				let next = options[i];
				formUpdateManager.writeStateField(stateContainer, container.id, next.value);
			}
		}
	}
	Array.from(form.querySelectorAll(containerQs)).forEach(_writeState);
}

/**
 * Moves options within a select or optgroup. Options in an optgroup are bound by that optgroup.
 * @function
 * @private
 * @param {HTMLButtonElement} element The button which causes all the fuss.
 */
function move(element) {
	let selected;
	const select = document.getElementById(element.getAttribute("aria-controls")),
		position = element.value;

	/**
	 * Given an option we look up position and move the option accordingly (if possible)
	 * This is not as easy as it sounds!
	 * If we are moving an option up we see if it has a previous sibling and if the
	 * 	previous sibling is not selected we move the option. We do the selected test
	 *  to prevent the situation which would occur when attempting to move a consecutive
	 *  group of options.
	 * If we are moving the option down we want to find the sibling after the next sibling.
	 *   If we find one we put the option before it. If we don't we look for the option's
	 *   next sibling (this means the option is the penultimate sibling) and if we find one
	 *   we make the option the last option of its parent. If the option has no next sibling
	 *   it is already at the bottom and is not moved.
	 * If we are moving the option to the top we get the parent element's first child and if
	 *   it is not the option we put the option before it (no good trying to move an option
	 *   before itself!)
	 * If we are moving the option to the bottom of the list we check if it has a next sibling
	 *   and if so make it the parent element's last child, otherwise it is already the last
	 *   child
	 *
	 * @param {HTMLElement} option the option element to move
	 */
	function _moveIt(option) {
		let reference,
			parent = option.parentElement;
		switch (position) {
			case UP:
				reference = option.previousElementSibling;
				if (reference && selected.indexOf(reference) === -1) {  // the test on selected is to prevent a group of consecutive options at the top or bottom fighting each other
					parent.insertBefore(option, reference);
				}
				break;
			case DOWN:
				if ((reference = option.nextElementSibling)) {
					reference = reference.nextElementSibling;  // we want the option after the next option (if there is one)
				}
				if (reference) {
					if (selected.indexOf(option.nextElementSibling) === -1 || selected.indexOf(reference) === -1) {
						parent.insertBefore(option, reference);
					}
				} else if ((reference = option.nextElementSibling) && selected.indexOf(reference) === -1) {
					// this will happen if we try to move the penultimate child down
					parent.appendChild(option);
				}
				break;
			case TOP:
				if ((reference = parent.firstElementChild) && reference !== option) {
					parent.insertBefore(option, reference);
				}
				break;
			case BOTTOM:
				if (option.nextElementSibling) {
					parent.appendChild(option);
				}
				break;
		}
	}

	selected = select ? getFilteredGroup(select) : null;
	if (Array.isArray(selected) && selected.length) {
		if (position === TOP || position === DOWN) {
			for (let i = selected.length - 1; i >= 0; --i) {  // reverse the order of move to top
				_moveIt(selected[i]);
			}
		} else {
			selected.forEach(_moveIt);
		}
		// If we are in a WShuffler we will have to manually fire any ajax triggers
		const container = element.closest(containerQs);
		if (container instanceof HTMLElement && ajaxRegion.getTrigger(container, true)) {
			ajaxRegion.requestLoad(container, null, true);
		}
	}
}

/**
 * @param {MouseEvent & { target: HTMLElement }} $event
 */
function clickEvent({ target, defaultPrevented }) {
	const element = defaultPrevented ? null : target?.closest(moveButtonQs);
	if (element instanceof HTMLButtonElement && !element.disabled) {
		move(element);
	}
}

/**
 * Provides functionality for changing the order of options in a list (select or optgroup).
 */
initialise.register({
	/**
	 * Set up shuffle controller.
	 * @param {HTMLElement} element The element being initialised, usually `document.body`
	 */
	initialise: function (element) {
		event.add(element, "click", clickEvent);
		formUpdateManager.subscribe(writeState);
	},
	/**
	 * Unsubscribes event listeners etc.
	 * @param {HTMLElement} element The element being de-initialised, usually document.body.
	 */
	deinit: function(element) {
		event.remove(element, "click", clickEvent);
		formUpdateManager.unsubscribe(writeState);
	}
});
