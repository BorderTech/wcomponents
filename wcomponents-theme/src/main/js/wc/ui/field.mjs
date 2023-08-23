import processResponse from "wc/ui/ajax/processResponse";
import initialise from "wc/dom/initialise";
/**
* A more or less annoyingly pointless module to provide some support for the limited XSLT options when content arrives
* in an ajax response with no context. In this case a WField may be an AJAX target so has to transform without knowing
* its WFieldLayout context. Who thought this stuff up?
*
*/


const fieldSelector = ".wc-field",
	placeholderSelector = ".wc_fld_pl",
	NO_PARENT_ATTRIB = "data-wc-nop";

/**
 * Before inserting a container into the DOM we may need to manipulate some properties which are not
 * available to the XSLT as they are ancestor dependent. This will only be the case if the element being
 * acted upon is the output of ui:field without a ui:fieldlayout parent.
 *
 * @function
 * @private
 * @param {Element} element The reference element (element being replaced).
 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
 */
function ajaxSubscriber(element, documentFragment) {
	let fieldElement;

	if (!element?.matches(fieldSelector)) {
		return;
	}

	// we have to have a field layout to reference, it should be element's parent element and we only care if it has a data-wc-labelwidth attribute
	const layout = element.parentNode;
	if (!layout) {
		return;
	}

	if (documentFragment.getElementById) {
		// IE, perhaps some others
		fieldElement = documentFragment.getElementById(element.id);
	} else if (documentFragment.querySelector) {
		fieldElement = documentFragment.querySelector(`#${element.id}`);
	}

	if (!fieldElement) {
		return;
	}

	if (fieldElement.getAttribute(NO_PARENT_ATTRIB) === "true") {
		fieldElement.removeAttribute(NO_PARENT_ATTRIB);
		if (layout.classList.contains("stacked")) {
			const pl = fieldElement.querySelector(placeholderSelector);
			if (pl) {
				pl.parentElement.removeChild(pl);
			}
		}
	}
}

export default initialise.register({
	postInit: () => processResponse.subscribe(ajaxSubscriber)
});
