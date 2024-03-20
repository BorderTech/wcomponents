import {fileURLToPath} from "url";
import {JSDOM} from "jsdom";
import domTesting from "@testing-library/dom";


/**
 * To help with type checking, get a select element from here.
 * @param {HTMLElement} container
 * @param {string} testId
 * @returns {HTMLSelectElement}
 */
export const getSelect = (container, testId) => {
	return /** @type {HTMLSelectElement} */(domTesting.getByTestId(container, testId));
};

/**
 * To help with type checking, get a select element from here.
 * @param {HTMLElement} container
 * @param {string} testId
 * @returns {Promise<HTMLSelectElement>}
 */
export const findSelect = (container, testId) => {
	return /** @type {Promise<HTMLSelectElement>} */(domTesting.findByTestId(container, testId));
};


/**
 * To help with type checking, get an input element from here.
 * @param {HTMLElement} container
 * @param {string} testId
 * @returns {HTMLInputElement}
 */
export const getInput = (container, testId) => {
	return /** @type {HTMLInputElement} */(domTesting.getByTestId(container, testId));
};

/**
 * To help with type checking, get an input element from here.
 * @param {HTMLElement} container
 * @param {string} testId
 * @returns {Promise<HTMLInputElement>}
 */
export const findInput = (container, testId) => {
	return /** @type {Promise<HTMLInputElement>} */(domTesting.findByTestId(container, testId));
};

/**
 * To help with type checking, get a button element from here.
 * @param {HTMLElement} container
 * @param {string} testId
 * @returns {HTMLButtonElement}
 */
export const getButton = (container, testId) => {
	return /** @type {HTMLButtonElement} */(domTesting.getByTestId(container, testId));
};

/**
 * Gets the path to src/test/resource or src/main/resource
 * @param {string} subPath A file name to append, e.g. "file.txt"
 * @param {boolean} main If true will get the path to src/main/resource
 * @returns {string} The path to the resource dir
 */
export const getResoucePath = (subPath, main) => {
	const type = main ? "main" : "test";
	const url = new URL(`../../${type}/resource/${subPath}`, import.meta.url);
	return fileURLToPath(url);
};

/**
 * Adds files to a file selector
 * @param {HTMLElement} input
 * @param {Array<{ value: string, type: string }>} fileData
 * @return {*}
 */
export function addFilesToInput(input, fileData) {
	const files = [];

	fileData.forEach(({ value, type }) => {
		files.push(new File([value], `file-${files.length}`, { type: type }));
	});
	Object.defineProperty(input, 'files', {
		value: files,
		writable: false,
	});
	return input;
}

/**
 * JSDom doesn't report offset dimensions, this is a workaround.
 * @param view A Window
 */
export function fudgeDimensions(view) {
	// Allows you to set style on an element and have it report an offset dimension
	Object.defineProperties(view.HTMLElement.prototype, {
		offsetLeft: {
			get () {
				return parseFloat(this.style.marginLeft) || 0;
			}
		},
		offsetTop: {
			get () {
				return parseFloat(this.style.marginTop) || 0;
			}
		},
		offsetHeight: {
			get () {
				return parseFloat(this.style.height) || 0;
			}
		},
		offsetWidth: {
			get () {
				return parseFloat(this.style.width) || 0;
			}
		}
	});
}

/**
 *
 * @param {string} urlResource The HTML file to load from test/resource
 * @return {Promise} resolved with a DOM loaded from the HTML
 */
export function setUpExternalHTML(urlResource) {
	return JSDOM.fromFile(getResoucePath(urlResource, false)).then(dom => {
		fudgeDimensions(dom.window);
		return dom;
	});
}
