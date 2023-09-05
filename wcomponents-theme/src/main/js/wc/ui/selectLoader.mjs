import listLoader from "wc/ui/listLoader";
import initialise from "wc/dom/initialise";
import selectboxSearch from "wc/ui/selectboxSearch";
import shed from "wc/dom/shed";
import event from "wc/dom/event";
import i18n from "wc/i18n/i18n";
import getLabelsForElement from "wc/dom/getLabelsForElement";
import feedback from "wc/ui/feedback";

const DISABLED_BY_ME = "data-wc-selectloader-disabled";

const instance = {
	/**
	 * Load data list SELECT elements.
	 * @function
	 * @public
	 * @param {String[]} idArr An array of element ids.
	 */
	register: function(idArr) {
		if (idArr?.length) {
			initialise.addCallback(() => processNow(idArr));
		}
	},
	/**
	 * Populates the select with options.
	 * Select lists will call this public method directly on page load.
	 * @param {String} id The id of the select element we are loading.
	 */
	load: function (id) {
		const element = document.getElementById(id),
			win = callbackFactory(id),
			lose = errorCallbackFactory(id);
		if (element) {
			element.setAttribute("aria-busy", "true");
			listLoader.load(element.getAttribute("data-wc-list"), element, false).then(win, lose);
		}
	}
};

/**
 * Generates new callback functions curried with the id of the element we want the callback to operate on.
 * @function
 * @private
 * @param {String} id The id of a select element.
 */
function callbackFactory(id) {
	/**
	 * Split this out to keep cyclomatic complexity down.
	 * @param {HTMLSelectElement} selectList
	 * @param {DocumentFragment} datalist
	 */
	const process = (selectList, datalist) => {
		const currentOptions = Array.from(selectList.selectedOptions);
		const optContainer = datalist.querySelector("select");
		if (optContainer) {
			selectList.innerHTML = optContainer.innerHTML;
			// re-select all the options that were originally selected
			currentOptions.forEach(next => {
				const nextIdx = selectboxSearch.indexOf(next, optContainer);
				if (nextIdx >= 0) {
					selectList.options[nextIdx].selected = true;  // do not shed publish as the selection has not changed.
					if (!selectList.hasAttribute(("multiple"))) {  // the following is a Safari 8.0.8 bug workaround.
						const selIdx = selectboxSearch.indexOf(next, selectList);
						if (selectList.selectedIndex !== selIdx) {
							selectList.selectedIndex = selIdx;
						}
					}
				}
			});
		} else {
			console.warn("Datalist malformed");
		}
	}
	/**
	 * @param {DocumentFragment} datalist
	 */
	return function (datalist) {
		const element = document.getElementById(id);
		if (element) {
			const message = getErrorMessage(id, false);
			if (message) {
				feedback.remove(message, element);
			}
			try {
				const selectList = /** @type {HTMLSelectElement} */
					(element.matches("select") ?
					element : element.querySelector("select"));
				if (!selectList) {
					return;
				}
				if (shed.isDisabled(element) && element.hasAttribute(DISABLED_BY_ME)) {
					element.removeAttribute(DISABLED_BY_ME);
					shed.enable(element, true);
				}
				process(selectList, datalist);
			} finally {
				element.removeAttribute("aria-busy");
			}
		} else {
			console.warn("Could not load list", id);
		}
	};
}

/**
 * Generates new callback functions curried with the id of the element we want the callback to operate on.
 * @function
 * @private
 * @param {String} id The id of a select element.
 */
function errorCallbackFactory(id) {
	return function () {
		const element = document.getElementById(id);
		if (element) {
			getErrorMessage(id, true);
			if (!shed.isDisabled(element)) {
				element.setAttribute(DISABLED_BY_ME, "true");
				shed.disable(element, true);
			}
			element.removeAttribute("aria-busy");
		} else {
			console.warn("Could not find element", id);
		}
	};
}

/**
 * Present the user with a message if the list cannot be loaded.
 * To a large extent this probably belongs in listLoader, so it can be reused.
 * @param {string} id
 * @param {boolean} create
 * @return {HTMLElement|null}
 *
 */
function getErrorMessage(id, create) {
	const element = document.getElementById(id),
		AJAX_ERROR_CLASS = "wc-selectload-error";
	let message = feedback.getBox(element, feedback.LEVEL.ERROR);
	if (message?.classList.contains(AJAX_ERROR_CLASS)) {
		return message;
	}
	if (!message && element && create) {
		const labels = getLabelsForElement(element, true);
		let label = labels?.length ? ` '${labels[0].textContent}'` : "";
		label = i18n.get("loader_loaderr", label);
		const errorResult = feedback.flagError({
			target: element,
			messages: label
		});
		message = document.getElementById(errorResult);
		if (errorResult && message) {
			message.classList.add(AJAX_ERROR_CLASS);
			const button = document.createElement("button");
			button.type = "button";
			button.innerHTML = i18n.get("loader_retry", label);
			event.add(button, "click", /** @param {MouseEvent} $event */ $event => {
				$event.preventDefault();  // important! stop any other listeners responding to this button
				instance.load(id);
			}, false);
			message.appendChild(button);
		}
		return message;
	}
	return null;
}

/**
 * Registration processor
 * @param {String[]} idArr An array of element ids.
 */
function processNow(idArr) {
	let id;
	while ((id = idArr.shift())) {
		instance.load(id);
	}
}
/**
 * This module allows the options of a select list to be loaded and cached. This is all about improving performance by
 * keeping the payload small and using listLoader to get cache benefits.
 *
 * Note that IE8 ruins this in a few ways:
 *
 * * Ideally we would simply transform the options into a documentFragment and then append that documentFragment to the
 *   existing select. However, IE8 can simply not cope with options that are not inside a select. This creates a heavier
 *   routine for adding options the select.  Optgroup is not good as a container as it requires a label and alters
 *   formatting.
 * * Even with the above concession to IE, using a select instead of a documentFragment IE8 still has further issues in
 *   that you can't say sel1.innerHTML = sel2.innerHTML.  So we are forced to loop through each option and add then one
 *   by one for IE8 (tested on IE9, still can't do it).
 *
 * @module
 */

export default instance;
