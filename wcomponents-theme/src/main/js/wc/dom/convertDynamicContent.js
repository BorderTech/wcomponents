/**
 * @module
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/serialize
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/formUpdateManager", "wc/dom/serialize", "wc/dom/Widget"],
	/** @param formUpdateManager wc/dom/formUpdateManager @param serialize wc/dom/serialize @param Widget wc/dom/Widget @ignore */
	function(formUpdateManager, serialize, Widget) {
		"use strict";
		var FORM_CONTROLS;

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
			var tempContainer = formUpdateManager.getStateContainer(container),
				nodes, serializedForm;
			FORM_CONTROLS = FORM_CONTROLS || new Widget("", "", {"name": null});

			container.parentNode.appendChild(tempContainer);
			formUpdateManager.update(container, null, true);

			if ((nodes = FORM_CONTROLS.findDescendants(container)) && nodes.length) {
				serializedForm = serialize.serialize(nodes, false);
				serialize.deserialize(serializedForm, tempContainer);
			}

			container.innerHTML = "";
			while (tempContainer.firstChild) {
				container.appendChild(tempContainer.firstChild);
			}
			tempContainer.parentNode.removeChild(tempContainer);
		}
		return convert;
	});
