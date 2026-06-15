import WFieldIndicator from 'wc/ui/fieldIndicator.mjs';

const { afterEach, describe, document, expect, it } = globalThis;

describe('Field Indicator', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('Without `type`', async () => {
		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `<${WFieldIndicator.tagName}
			id="id" class="class" for="for"
		/>`.trim();

		/** @type {WFieldIndicator} */
		const element = /** @type {WFieldIndicator} */ (template.content.firstElementChild);
		document.body.appendChild(element);

		expect(element.querySelector(':scope > span > i')).toBeTruthy();
	});

	it('`type` is "error"', async () => {
		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `<${WFieldIndicator.tagName} type="error"/>`.trim();

		/** @type {WFieldIndicator} */
		const element = /** @type {WFieldIndicator} */ (template.content.firstElementChild);
		document.body.appendChild(element);

		expect(element.querySelector(':scope > span > i')).toBeTruthy();
	});

	it('`type` is "warn"', async () => {
		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `<${WFieldIndicator.tagName} type="warn"/>`.trim();

		/** @type {WFieldIndicator} */
		const element = /** @type {WFieldIndicator} */ (template.content.firstElementChild);
		document.body.appendChild(element);

		expect(element.querySelector(':scope > span > i')).toBeTruthy();
	});

	it('`type` is "info"', async () => {
		/** @type {HTMLTemplateElement} */
		const template = document.createElement('template');
		template.innerHTML = `<${WFieldIndicator.tagName} type="info"/>`.trim();

		/** @type {WFieldIndicator} */
		const element = /** @type {WFieldIndicator} */ (template.content.firstElementChild);
		document.body.appendChild(element);

		expect(element.querySelector(':scope > span > i')).toBeTruthy();
	});
});
