import formUpdateManager from "wc/dom/formUpdateManager";
import serialize from "wc/dom/serialize";

/**
 * Provides a means to remove Elements from a no-longer relevant dynamic region without losing their state information.
 * It does a call to {@link module:wc/dom/formUpdateManager} using a container as a stand-in for form (NOT a region)
 * and attaching the state fields to the container (which is why we use the container as a form stand in not just a
 * region). By getting the stateContainer before calling update we can place it outside the dynamic region then move the
 * state fields into the dynamic region after blatting its content.
 *
 * @function module:wc/dom/convertDynamicContent
 * @param {Element} container A container element but primarily designed to work with DYNAMIC containers.
 */
function convert (container) {
	const tempContainer = formUpdateManager.getStateContainer(container);
	container.parentNode.appendChild(tempContainer);
	formUpdateManager.update(container, null, true);
	const nodes = container.querySelectorAll("[name]");
	if (nodes.length) {
		const serializedForm = serialize.serialize(nodes, false);
		serialize.deserialize(serializedForm, tempContainer);
	}

	container.innerHTML = "";
	while (tempContainer.firstChild) {
		container.appendChild(tempContainer.firstChild);
	}
	tempContainer.parentNode.removeChild(tempContainer);
}
export default convert;
