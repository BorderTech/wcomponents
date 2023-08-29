/**
 * Models an editable image.
 */

import initialise from "wc/dom/initialise";
import i18n from "wc/i18n/i18n";
import processResponse from "wc/ui/ajax/processResponse";

const imageSelector = "img[data-wc-editor]";
const template = (context) => `<button type="button" data-wc-editor="${context.editor}" data-wc-selector="${context.editor}" data-wc-img="${context.id}" class="wc_btn_icon wc-invite"><i aria-hidden="true" class="fa fa-picture-o"></i><span class="wc-off">${context.text}</span></button>`;

function makeEditButton(element) {
	const id = element.id,
		sibling = element.nextSibling;
	if (sibling && sibling.getAttribute("data-wc-img") === id) {
		return;
	}
	i18n.translate("imgedit_edit").then(function(editButtonText) {
		const props = {
			id: id,
			editor: element.getAttribute("data-wc-editor"),
			text: editButtonText
		};
		const html = template(props);
		element.insertAdjacentHTML("afterend", html);
	});
}

function makeEditButtons(element) {
	const el = element || document.body;
	if (element?.matches(imageSelector)) {
		makeEditButton(element);
	} else {
		Array.from(el.querySelectorAll(imageSelector)).forEach(makeEditButton);
	}
}

initialise.register({
	postInit: () => processResponse.subscribe(makeEditButtons, true),
	initialise: (element) => makeEditButtons(element)
});
