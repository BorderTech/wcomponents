import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import clearSelector from "wc/file/clearSelector.mjs";
import validate from "wc/file/validate.mjs";

const INITED_KEY = "wc.ui.fileUpload.inited",
	CONTAINER = ".wc-fileupload",
	inputElementWd = `${CONTAINER} > input[type='file']`;

const instance = {
	/**
	 * Get the descriptor of the multi file upload component.
	 * @function module:wc/ui/fileUpload.getWidget
	 * @returns {string} The widget descriptor.
	 */
	getWidget: () => inputElementWd,

	/**
	 * Tests if an element is a file upload.
	 * @function module:wc/ui/fileUpload.isOneOfMe
	 * @param {Element} element The DOM element to test
	 * @returns {Boolean} true if element is the Widget type requested
	 */
	isOneOfMe: element => element?.matches(inputElementWd),

	/**
	 * Sets a file selector to an empty value.
	 * As usual this apparently simple task is made complex due to Internet Explorer.
	 * @param {Element} element A file input.
	 */
	clearInput: function (element) {
		clearSelector(element, (selector, cloned) => {
			if (cloned) {
				initialiseFileInput(selector);
			}
		});
	}
};

/**
 * The user would like to upload a file via a file input, this is the entry point to the process.
 * @param {HTMLInputElement} element The file input the user is interacting with.
 */
function upload(element) {
	if (!element.value) {
		// nothing to do
		return;
	}
	validate.check({
		selector: element,
		notify: true,
		errback: instance.clearInput
	});
}

/**
 * Change event on the file input.
 * Somebody wants to upload a file... Is it the right size and type?
 * @function
 * @private
 * @param {UIEvent & {target: HTMLInputElement}} $event The change event.
 */
function changeEvent($event) {
	if (!$event.defaultPrevented && $event.target.matches(inputElementWd)) {
		upload($event.target);
	}
}

/**
 * Set up a file selector on first use.
 * @function
 * @private
 * @param {HTMLInputElement} element A file input.
 */
function initialiseFileInput(element) {
	if (element.matches(inputElementWd)) {
		const form = element.form;
		if (!form[INITED_KEY]) {
			form[INITED_KEY] = true;
			event.add(form, "change", changeEvent);
		}
	}
}

initialise.register({
	/**
	 * Initialise file upload functionality by adding a focus listener.
	 * @function module:wc/ui/fileUpload.initialise
	 * @param {Element} element The element being initialised - usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: ({target}) => initialiseFileInput(target), capture: true });
	}
});

export default instance;
