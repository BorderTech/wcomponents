/**
 * Provides Ajax and state writing functionality for check boxes.
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/dom/cbrShedPublisher
 *
 * @todo Get rid of the state writing: it is nuts!
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/shed",
		"wc/dom/formUpdateManager",
		"wc/ui/ajax/processResponse",
		"wc/dom/cbrShedPublisher"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param formUpdateManager wc/dom/formUpdateManager @param processResponse wc/ui/ajax/processResponse @ignore */
	function(initialise, Widget, shed, formUpdateManager, processResponse) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/checkbox~CheckBox
		 * @private
		 */
		function CheckBox() {
			var CHECKBOX = new Widget("input", "", { "type": "checkbox" }),
				CB_ALONE;

			/**
			 * Provides the {@link module:wc/dom/Widget} description of a CHECKBOX.
			 * @function module:wc/ui/checkbox.getWidget
			 * @public
			 * @returns {Widget}
			 */
			this.getWidget = function() {
				return CHECKBOX;
			};

			/**
			 * This is a writeState for standalone WCheckBox elements (not part of a WCheckBoxSelect) which are not
			 * checked.
			 * TODO: get rid of this one way or another it should never have been written.
			 *
			 * @function
			 * @private
			 * @param {Element} form The form or form segment which is having its state written.
			 * @param {Element} container The HTML element into which the state is written.
			 */
			function writeState(form, container) {
				var cb;
				CB_ALONE = CB_ALONE || CHECKBOX.extend("", { "${wc.ui.checkBox.attribute.standAlone}": null });
				cb = CB_ALONE.findDescendants(form);

				cb = Array.prototype.filter.call(cb, function (next) {
					return !(shed.isSelected(next) || shed.isDisabled(next));
				});

				cb.forEach(function (next) {
					formUpdateManager.writeStateField(container, next.name, "");
				});
			}

			/**
			 * when a checkBox is added using AJAX it may need to find out if it is controlled and if so add its ID to
			 * the controllers's aria-controls attribute.
			 *
			 * @function
			 * @private
			 * @param {Element} element The reference element (element being replaced).
			 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
			 */
			function ajaxSubscriber(element, documentFragment) {
				var GROUP_ATTRIB = "data-wc-cbgroup",
					CONTROLS = "aria-controls";

				if (element && !CHECKBOX.isOneOfMe(element)) {  // can only replace like-for-like and checkboxes have no content
					Array.prototype.forEach.call(CHECKBOX.findDescendants(documentFragment), function (_el) {
						var refElement,
							myId = _el.id,
							myGroupName = _el.getAttribute(GROUP_ATTRIB),
							localController = new Widget("", "", { "aria-controls": myId }),
							refElementWd = CHECKBOX.extend("", { "data-wc-cbgroup": myGroupName });

						// we are only interested in ui:checkbox which have a groupName
						// if I existed in the document prior to ajax I do not need to do anything
						if (_el.type === "checkbox" && !document.getElementById(myId) && !localController.findDescendant(document.body)) {
							// ok, so we need to get a handle on other checkboxes with my group name
							if ((refElement = refElementWd.findDescendant(document.body))) {
								localController = new Widget("", "", {"aria-controls": refElement.id});

								Array.prototype.forEach.call(localController.findDescendants(document.body), function(next) {
									var controlled;
									controlled = next.getAttribute(CONTROLS);
									if (controlled) {
										controlled = controlled.split(/\s+/);
										if (controlled.indexOf(myId) === -1) {
											controlled.push(myId);
											controlled = controlled.join(" ");
											next.setAttribute(CONTROLS, controlled);
										}
									}
									else {
										next.setAttribute(CONTROLS, myId);
									}
								});
							}
						}
					});
				}
			}

			/**
			 * Wire up subscribers after initialisation.
			 * @function module:wc/ui/checkbox.postInit
			 * @public
			 */
			this.postInit = function() {
				formUpdateManager.subscribe(writeState);
				processResponse.subscribe(ajaxSubscriber);
			};
		}

		var /** @alias module:wc/ui/checkbox */ instance = new CheckBox();
		initialise.register(instance);
		return instance;
	});
