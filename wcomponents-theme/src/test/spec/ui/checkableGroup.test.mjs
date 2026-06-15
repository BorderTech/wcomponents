import WCheckBoxSelect, { WOption, WRadioButtonSelect } from 'wc/ui/checkableGroup.mjs';
import WFieldIndicator from 'wc/ui/fieldIndicator.mjs';

const { afterEach, describe, document, expect, it } = globalThis;

describe('Checkable Group', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	describe('default', () => {
		describe('Read only', () => {
			it('Without `layoutColumnCount`', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}
						id="id"
						class="class"
						layout="layout"
						readOnly="readOnly"
						hidden="hidden"
					/>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > span')).toBeTruthy();
			});

			it('Without valid `layoutColumnCount` amount', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}
						readOnly="readOnly"
						layoutColumnCount="1"
					>
						<${WOption.tagName} />
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > span')).toBeTruthy();
			});

			it('With `layoutColumnCount` amount', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}
						readOnly="readOnly"
						layoutColumnCount="2"
					>
						<${WOption.tagName} />
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > span > span[data-wc-colcount="2"]')).toBeTruthy();
			});
		});

		describe('default - 2', () => {
			it('Hidden input', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}
						id="id"
						frameless="frameless"
						required="required"
						layout="layout"
						hidden="hidden"
						toolTip="toolTip"
						accessibleText="accessibleText"
						disabled="disabled"
					/>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > input[type="hidden"]')).toBeTruthy();
			});

			it('With field-indicator', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}>
						<${WFieldIndicator.tagName} id="id" type="error"/>
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > wc-field-indicator')).toBeTruthy();
			});

			it('With option', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName} min="0" max="1" layoutColumnCount="2">
						<${WOption.tagName}/>
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > div > wc-option')).toBeTruthy();
			});
		});
	});

	describe('Option', () => {
		describe('Checkable Group Read Only', () => {
			it('With inner text', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName} readOnly="readOnly">
						<${WOption.tagName}>TEST</${WOption.tagName}>
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(`:scope > span > ${WOption.tagName} > span`)).toBeTruthy();
			});

			it('With value attribute', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName} readOnly="readOnly">
						<${WOption.tagName} value="test-1"></${WOption.tagName}>
						<${WOption.tagName} value="test-2"></${WOption.tagName}>
						<${WOption.tagName} value="test-3"></${WOption.tagName}>
						<${WOption.tagName} value="test-4"></${WOption.tagName}>
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(`:scope > span > ${WOption.tagName} > span`)).toBeTruthy();
			});
		});

		describe('Checkable Group - 2', () => {
			it('With inner text - 2', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WRadioButtonSelect.tagName}
						disabled="disabled" submitOnChange="submitOnChange" required="required"
					>
						<${WOption.tagName} isNull selected>TEST</${WOption.tagName}>
					</${WRadioButtonSelect.tagName}>
				`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(`:scope > fieldset > div > ${WOption.tagName} > label > span`)).toBeTruthy();
			});

			it('With inner text - 3', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WCheckBoxSelect.tagName}>
						<${WOption.tagName} value="test"/>
					</${WCheckBoxSelect.tagName}>
				`.trim();

				/** @type {WCheckBoxSelect} */
				const element = /** @type {WCheckBoxSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(`:scope > fieldset > div > ${WOption.tagName} > label > span`)).toBeTruthy();
			});
		});
	});

	describe('Radio Button Select', () => {
		describe('Read only - 2', () => {
			it('With id', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WRadioButtonSelect.tagName}
						id="id"
						class="class"
						layout="layout"
						readOnly="readOnly"
						hidden="hidden"
					/>
				`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > span')).toBeTruthy();
			});

			it('Without id', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `<${WRadioButtonSelect.tagName} readOnly="readOnly"/>`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > span')).toBeTruthy();
			});
		});

		describe('default - 3', () => {
			it('Hidden input - 2', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WRadioButtonSelect.tagName}
						id="id"
						class="class"
						frameless="frameless"
						required="required"
						layout="layout"
						hidden="hidden"
						toolTip="toolTip"
						accessibleText="accessibleText"
						disabled="disabled"
					/>
				`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > input[type="hidden"]')).toBeTruthy();
			});

			it('With field-indicator - 2', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WRadioButtonSelect.tagName}>
						<${WFieldIndicator.tagName} id="id" type="error"/>
					</${WRadioButtonSelect.tagName}>
				`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > wc-field-indicator')).toBeTruthy();
			});

			it('With option - 2', async () => {
				/** @type {HTMLTemplateElement} */
				const template = document.createElement('template');
				template.innerHTML = `
					<${WRadioButtonSelect.tagName} layoutColumnCount="2">
						<${WOption.tagName}/>
					</${WRadioButtonSelect.tagName}>
				`.trim();

				/** @type {WRadioButtonSelect} */
				const element = /** @type {WRadioButtonSelect} */ (template.content.firstElementChild);
				document.body.appendChild(element);

				expect(element.querySelector(':scope > fieldset > div > wc-option')).toBeTruthy();
			});
		});
	});
});
