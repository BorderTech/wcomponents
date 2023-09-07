const WRAPPER = "div.wc-table",
	/**
	 * Common items required for table functionality.
	 * @module
	 *
	 */

	instance = {
		WRAPPER,
		TABLE: "table",
		THEAD: "thead",
		TBODY: "tbody",
		TR: "tr",
		TH: "th",
		TD: "td",
		BUTTON: "button",
		/**
		 *
		 * @param {Element} element
		 * @param {boolean|number} isOneShot
		 * @returns {{formRegion: string, loads: string[], alias: string, id: string, oneShot: (number)}}
		 */
		getAjaxDTO: function (element, isOneShot) {
			const alias = getWrapperId(element);
			return {
				id: element.id,
				loads: alias ? [alias] : [],
				alias,
				formRegion: alias,
				oneShot: isOneShot ? 1 : -1
			};
		}

	};

/**
 *
 * @param {Element} element
 * @returns {string}
 */
function getWrapperId(element) {
	const wrapper = element?.closest(WRAPPER);
	return wrapper?.id;
}

export default instance;
