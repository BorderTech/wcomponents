import {fileURLToPath} from "url";
import {JSDOM} from "jsdom";

/**
 * To help with type checking, get a select element from here.
 * @param {string} id
 * @param {HTMLDocument} [doc]
 * @returns {HTMLSelectElement}
 */
export const getSelect = (id, doc = document) => /** @type {HTMLSelectElement} */(doc.getElementById(id));

/**
 * To help with type checking, get an input element from here.
 * @param {string} id
 * @param {HTMLDocument} [doc]
 * @returns {HTMLInputElement}
 */
export const getInput = (id, doc = document) => /** @type {HTMLInputElement} */(doc.getElementById(id));

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
