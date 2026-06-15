
const { document, HTMLElement, window } = globalThis;

export default class WFieldIndicator extends HTMLElement {
	static tagName = 'wc-field-indicator';

	/** @type {boolean} */
	#connectedCallbackOnce = false;
	connectedCallback() {
		if (this.#connectedCallbackOnce) return;
		this.#connectedCallbackOnce = true;
		this.render();
	}

	render() {
		/** @type {string | null} */
		const id = this.id,
			/** @type {string | null} */
			type = this.getAttribute('type') || '',
			/** @type {string | null} */
			dataAttribute = this.getAttribute('for');

		this.removeAttribute('id');

		/** @type {string} */
		let additionalClass = this.getAttribute('class') || '';

		let iconClass = 'fa-check-circle';
		if (type === 'error') iconClass = 'fa-times-circle';
		else if (type === 'warn') iconClass = 'fa-exclamation-triangle';
		else if (type === 'info') iconClass = 'fa-info-circle';

		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `
			<span
				${(id) ? `id="${id}"` : ''}
				class="${['wc-fieldindicator', `wc-fieldindicator-type-${type}`, additionalClass].filter(Boolean).join(' ')}"
				${(dataAttribute) ? `data-wc-dfor="${dataAttribute}"` : ''}
			>
				<i aria-hidden="true" class="${['fa', iconClass].filter(Boolean).join(' ')}" />
			</span>
		`.trim();

		/** @type {HTMLSpanElement} */
		const content = /** @type {HTMLSpanElement} */ (template.content.firstElementChild);

		content.append(...this.children);
		this.appendChild(content);
	}
}

if (!window.customElements.get(WFieldIndicator.tagName)) {
	window.customElements.define(WFieldIndicator.tagName, WFieldIndicator);
}
