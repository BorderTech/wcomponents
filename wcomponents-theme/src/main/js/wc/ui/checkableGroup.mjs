
import WFieldIndicator from "./fieldIndicator.mjs";

const { crypto, document, HTMLElement, window } = globalThis;

class SelectableGroup extends HTMLElement {
	/**
	 * @returns {Number} the number of layout columns
	 */
	get layoutColumnCount() {
		return Number(this.getAttribute('layoutColumnCount')) || 0;
	}
}

export default class WCheckBoxSelect extends SelectableGroup {
	//  TODO make this tagname wc-cbselect or wc-checkboxselect or something else without dashes
	static tagName = 'wc-check-box-select';

	/** @type {boolean} */
	#connectedCallbackOnce = false;
	connectedCallback() {
		if (this.#connectedCallbackOnce) return;
		this.#connectedCallbackOnce = true;
		this.#render();
	}

	// TODO:
	// - reduce complexity of functions
	// - move shared code to superclass
	// - make template literals more readable by moving conditional logic to helpers, accessors or local vars
	// - why the <template> element?

	/**
	 * Render read only content
	 * @returns {void} ?
	 */
	#renderReadOnly() {
		/** @type {string} */
		const id = this.id;
		this.removeAttribute('id');

		/** @type {NodeListOf<Element>} */
		const options = this.querySelectorAll(WOption.tagName);

		/** @type {number} */
		const layoutColumnCount = this.layoutColumnCount;

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<span
				${id ? `id=${id}` : ''}
				class="${['wc-checkboxselect', 'wc-checkableselect', this.className].filter(Boolean).join(' ')} ${
					this.hasAttribute('layout') ? 'wc-layout-' + this.getAttribute('layout') : ''
				}"
				data-wc-component="checkboxselect"
				${
					// TODO: Need to fix the type of `hidden`
					this.hasAttribute('hidden') ? 'hidden="hidden"' : ''
				}
			>${
				layoutColumnCount > 1 ? `<span data-wc-colcount="${layoutColumnCount}"></span>` : ''
			}</span>
		`.trim();

		/** @type {HTMLSpanElement} */
		const content = /** @type {HTMLSpanElement} */ (template.content.firstElementChild);

		if (layoutColumnCount > 1) {
			/** @type {HTMLSpanElement | null} */
			const contentChild = content.querySelector(':scope > span');
			if (contentChild) for (const option of options) contentChild.appendChild(option);
		} else {
			for (const option of options) content.appendChild(option);
		}

		content.prepend(...this.children);
		this.appendChild(content);
	}

	/**
	 * Main renderer
	 * @returns {void} ?
	 */
	#render() {
		if (this.hasAttribute('readOnly')) {
			this.#renderReadOnly();
			return;
		}

		/** @type {string} */
		const id = this.id;
		this.removeAttribute('id');

		/** @type {HTMLElement | null} */
		const fieldIndicator = this.querySelector(WFieldIndicator.tagName),
			/** @type {NodeListOf<Element>} */
			options = this.querySelectorAll(WOption.tagName);

		/** @type {number} */
		const layoutColumnCount = this.layoutColumnCount;

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<fieldset
				${id ? `id="${id}"` : ''}
				class="${[
					'wc-checkboxselect', 'wc-checkableselect', this.hasAttribute('frameless') ? 'wc_noborder' : '',
					this.hasAttribute('required') ? 'wc_req' : '',
					this.hasAttribute('layout') ? 'wc-layout-' + this.getAttribute('layout') : ''
				].filter(Boolean).join(' ')}"
				${this.hasAttribute('hidden') ? 'hidden="hidden"' : ''}
				${this.hasAttribute('toolTip') ? `title="${this.getAttribute('toolTip')}"` : ''}
				${this.hasAttribute('accessibleText') ? `aria-label="${this.getAttribute('accessibleText')}"` : ''}
				${fieldIndicator?.id ? `aria-describedby="${fieldIndicator.id}"` : ''}
				${fieldIndicator?.hasAttribute('type') && fieldIndicator.getAttribute('type') === 'error' ? 'aria-invalid="true"' : ''}
				${options.length > 0 && this.hasAttribute('min') ? `data-wc-min="${this.getAttribute('min')}"` : ''}
				${options.length > 0 && this.hasAttribute('max') ? `data-wc-max="${this.getAttribute('max')}"` : ''}
			>
				<div
					${layoutColumnCount > 1 && this.hasAttribute('max') ? `data-wc-colcount="${layoutColumnCount}"` : ''}
				></div>
				<input
					type="hidden"
					name="${id}-h"
					value="x"
					${this.hasAttribute('disabled') ? 'disabled="true"' : ''}
				/>
			</fieldset>
		`.trim();

		/** @type {HTMLFieldSetElement} */
		const content = /** @type {HTMLFieldSetElement} */ (template.content.firstElementChild),
			/** @type {HTMLDivElement | null} */
			containerChild = content.querySelector(':scope > div');

		if (containerChild) {
			for (const option of options) {
				option.setAttribute('type', 'checkbox');
				containerChild.appendChild(option);
			}
		}

		content.prepend(...this.children);
		this.appendChild(content);
	}
}

export class WOption extends HTMLElement {
	static tagName = 'wc-option';

	/** @type {boolean} */
	#connectedCallbackOnce = false;
	connectedCallback() {
		if (this.#connectedCallbackOnce) return;
		this.#connectedCallbackOnce = true;
		this.#render();
	}

	/**
	 * Render Checkable Group Read only
	 * @param {string | null} value - Value
	 * @returns {void} ?
	 */
	#renderCheckableGroupRO(value) {
		/** @type {string} */
		const innerText = this.innerText;
		this.innerHTML = '';

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<span
				class="${['wc-option', (this.parentElement && this.parentElement.querySelectorAll(this.tagName).length < 2) ? 'wc-inline' : ''].filter(Boolean).join(' ')}"
			>${innerText || value || ''}</span>
		`.trim();

		/** @type {HTMLSpanElement} */
		const content = /** @type {HTMLSpanElement} */ (template.content.firstElementChild);

		content.prepend(...this.children);
		this.appendChild(content);
	}

	/**
	 * Render Checkable Group
	 * @param {HTMLElement | null} parentElement - Parent element
	 * @param {string | null} value - Value
	 * @returns {void} ?
	 */
	#renderCheckableGroup(parentElement, value) {
		/** @type {string} */
		const innerText = this.innerText;

		this.innerHTML = '';

		/** @type {string} */
		let name = '';
		if (parentElement?.id) name = parentElement.id;
		else if (parentElement?.firstElementChild?.id) name = parentElement.firstElementChild.id;

		/** @type {boolean} */
		const parentIsRadioButtonSelect = parentElement?.tagName.toLowerCase() === WRadioButtonSelect.tagName.toLowerCase();

		/** @type {string | null} */
		let _type = this.getAttribute('type');
		if (parentIsRadioButtonSelect) _type = 'radio';

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<label class="wc-option">
				<input
					id="${crypto.randomUUID()}"
					${_type ? `type="${_type}"` : ''}
					${name ? `name="${name}"` : ''}
					${value ? `value="${value}"` : ''}
					${
						// TODO: Need to fix the type of `checked`
						this.hasAttribute('selected') ? 'checked="checked"' : ''
					}
					${parentElement?.hasAttribute('disabled') ? 'disabled="disabled"' : ''}
					${parentElement?.hasAttribute('submitOnChange') ? 'class="wc_soc"' : ''}
					${
						// TODO: Need to fix the type of `required`
						parentIsRadioButtonSelect && parentElement.hasAttribute('required') ? 'required="required"' : ''
					}
					${parentIsRadioButtonSelect && this.hasAttribute('isNull') ? 'data-wc-null="1"' : ''}
				/>
				<span class="wc-labeltext">${innerText || value || ''}</span>
			</label>
		`.trim();

		/** @type {HTMLLabelElement} */
		const content = /** @type {HTMLLabelElement} */ (template.content.firstElementChild);

		this.appendChild(content);
	}

	/**
	 * Main renderer
	 * @returns {void} ?
	 */
	#render() {
		this.removeAttribute('name');

		/** @type {HTMLElement | null} */
		let parentElement = null,
			/** @type {boolean} */
			checkableGroup = false,
			/** @type {boolean} */
			checkableGroupRO = false,
			/** @type {string | null} */
			value = this.getAttribute('value');

		if (value) {
			this.setAttribute('data-value', value);
			this.removeAttribute('value');
		}

		for (let i = 0, p = this.parentElement; i < 3 && p; i++, p = p?.parentElement) {
			if (
				[WCheckBoxSelect.tagName.toLowerCase(), WRadioButtonSelect.tagName.toLowerCase()].includes(
					p?.tagName.toLowerCase()
				)
			) {
				if (p.hasAttribute('readOnly')) {
					parentElement = p;
					checkableGroupRO = true;
					break;
				}
				parentElement = p;
				checkableGroup = true;
				break;
			}
		}

		if (checkableGroupRO) this.#renderCheckableGroupRO(value);
		else if (checkableGroup) this.#renderCheckableGroup(parentElement, value);
	}
}

export class WRadioButtonSelect extends SelectableGroup {
	static tagName = 'wc-radio-button-select';

	/** @type {boolean} */
	#connectedCallbackOnce = false;
	connectedCallback() {
		if (this.#connectedCallbackOnce) return;
		this.#connectedCallbackOnce = true;
		this.#render();
	}

	/**
	 * Render Read only
	 * @returns {void} ?
	 */
	#renderReadOnly() {
		/** @type {string} */
		const id = this.id;
		this.removeAttribute('id');

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<span
				${id ? `id="${id}"` : ''}
				class="${[
					'wc-radiobuttonselect', 'wc-checkableselect', this.className,
					this.hasAttribute('layout') ? `wc-layout-${this.getAttribute('layout')}` : ''
				].filter(Boolean).join(' ')}"
				${
					// TODO: Need to fix the type of `hidden`
					this.hasAttribute('hidden') ? 'hidden="hidden"' : ''
				}
				data-wc-component="radiobuttonselect"
			/>
		`.trim();

		/** @type {HTMLSpanElement} */
		const content = /** @type {HTMLSpanElement} */ (template.content.firstElementChild);

		content.append(...this.children);
		this.appendChild(content);
	}

	/**
	 * Main renderer
	 * @returns {void} ?
	 */
	#render() {
		if (this.hasAttribute('readOnly')) {
			this.#renderReadOnly();
			return;
		}

		/** @type {string} */
		const id = this.id;
		this.removeAttribute('id');

		/** @type {HTMLElement | null} */
		const fieldIndicator = this.querySelector(WFieldIndicator.tagName),
			/** @type {NodeListOf<Element>} */
			options = this.querySelectorAll(WOption.tagName);

		/** @type {number} */
		const layoutColumnCount = this.layoutColumnCount;

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<fieldset
				${id ? `id="${id}"` : ''}
				class="${[
					'wc-radiobuttonselect', 'wc-checkableselect', this.className,
					this.hasAttribute('frameless') ? 'wc_noborder' : '',
					this.hasAttribute('required') ? 'wc_req' : '',
					this.hasAttribute('layout') ? `wc-layout-${this.getAttribute('layout')}` : ''
				].filter(Boolean).join(' ')}"
				${
					// TODO: Need to fix the type of `hidden`
					this.hasAttribute('hidden') ? 'hidden="hidden"' : ''
				}
				${this.hasAttribute('toolTip') ? `title="${this.getAttribute('toolTip')}"` : ''}
				${this.hasAttribute('accessibleText') ? `aria-label="${this.getAttribute('accessibleText')}"` : ''}
				${fieldIndicator?.id ? `aria-describedby="${fieldIndicator.id}"` : ''}
				${fieldIndicator?.hasAttribute('type') && fieldIndicator.getAttribute('type') === 'error' ? 'aria-invalid="true"' : ''}
			>
				<div ${layoutColumnCount > 1 ? `data-wc-colcount="${layoutColumnCount}"` : ''}></div>
				<input
					type="hidden"
					name="${id}-h"
					value="x"
					${
						// TODO: Need to fix the type of `disabled`
						this.hasAttribute('disabled') ? 'disabled="disabled"' : ''
					}
				/>
			</fieldset>
		`.trim();

		/** @type {HTMLFieldSetElement} */
		const content = /** @type {HTMLFieldSetElement} */ (template.content.firstElementChild),
			/** @type {HTMLDivElement | null} */
			contentChild = content.querySelector(':scope > div');

		if (contentChild) for (const option of options) contentChild.appendChild(option);

		content.prepend(...this.children);
		this.appendChild(content);
	}
}

if (!window.customElements.get(WCheckBoxSelect.tagName)) {
	window.customElements.define(WCheckBoxSelect.tagName, WCheckBoxSelect);
}

if (!window.customElements.get(WOption.tagName)) {
	window.customElements.define(WOption.tagName, WOption);
}

if (!window.customElements.get(WRadioButtonSelect.tagName)) {
	window.customElements.define(WRadioButtonSelect.tagName, WRadioButtonSelect);
}
