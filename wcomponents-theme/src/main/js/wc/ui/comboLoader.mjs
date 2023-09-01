import initialise from "wc/dom/initialise";
import listLoader from "wc/ui/listLoader";
import comboBox from "wc/ui/comboBox";


const selectSelector = "select",
	suggestionListSelector = comboBox.getListWidget().toString(),  // can handle a selector or a Widget
	BUSY = "aria-busy";

/**
 * Implements eager loading of existing options for combo boxes.
 */
const instance = {
	/**
	 * This sets up a registry of ids for eager loading data lists and adds an initialise callback to
	 * process the registry.
	 * @public
	 * @param {String[]} idArr Array of element ID values.
	 */
	register: function(idArr) {
		if (idArr && idArr.length) {
			initialise.addCallback(function() {
				processNow(idArr);
			});
		}
	}
};

/**
 * Converts a select element to listbox options and replaces the optionList
 * options with the new ones
 *
 * @function
 * @private
 * @param {(HTMLSelectElement|Object)} dataList a select element or object with an 'options' array
 * @param {HTMLUListElement} optionList the optionList to populate (a UL element).
 */
function selectToOptions(dataList, optionList) {
	const options = dataList.options;

	// just make sure we reset the list to throw out any we no longer need
	optionList.innerHTML = "";

	for (const element of options) {
		let next = element;
		let item = document.createElement("span");
		item.setAttribute("data-wc-value", next.innerHTML);
		item.setAttribute("role", "option");
		item.className = "wc-invite";
		item.innerHTML = next.innerHTML;
		item.tabIndex = 0;
		optionList.appendChild(item);
	}
}

function callbackFactory(optionList, element) {
	return function (data) {
		try {
			if (data && (data = data.querySelector(selectSelector))) {
				selectToOptions(data, optionList);
			}
		} finally {
			element.removeAttribute(BUSY);
		}
	};
}

/*
 * Registry processor for data list driven combos.
 */
function processNow(idArr) {
	let id;
	const DATA_LIST_ATTRIB = "data-wc-list";

	while ((id = idArr.shift())) {
		const element = document.getElementById(id);
		if (element) {
			const dataId = element.getAttribute(DATA_LIST_ATTRIB);
			let optionList;
			if (dataId) {
				if (element.matches(suggestionListSelector)) {
					optionList = element;
				} else {
					optionList = comboBox._getList(element);
				}
			}
			if (optionList) {
				if (!element.matches(suggestionListSelector)) {
					element.setAttribute(BUSY, "true");
				}
				const onsuccess = callbackFactory(optionList, element);
				listLoader.load(dataId, element).then(onsuccess);
			}
		}
	}
}



export default instance;
