define(["wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ajax/triggerManager",
	"wc/dom/serialize",
	"wc/dom/Widget",
	"wc/timers",
	"wc/ui/getFirstLabelForElement",
	"wc/ui/label",
	"wc/i18n/i18n",
	"wc/dom/textContent",
	"wc/ui/ajax/processResponse",
	"wc/dom/classList"],
	function(attribute, event, initialise, shed, triggerManager, serialize, Widget, timers, getFirstLabelForElement, label, i18n, textContent, processResponse, classList) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/onchangeSubmit~OnchangeSubmit
		 * @private
		 */
		function OnchangeSubmit() {
			var submitting = false,  // this is a safety net to prevent double submits if both the change event and shed subscriber fire.
				SUBMITTER = new Widget("", "wc_soc"),
				LOAD_SELECT = SUBMITTER.extend("", {"data-wc-list": null}),
				TRIGGERS = [SUBMITTER.extend("", {"type": "checkbox"}),
					SUBMITTER.extend("", {"type": "radio"}),
					SUBMITTER.extend("", {"role": "checkbox"}),
					SUBMITTER.extend("", {"role": "radio"})],
				FORM = new Widget("form"),
				optionOnLoad = [],
				ignoreChange = false,
				DEP_WARNING = "DEPRECATION WARNING: onChangeSubmit is deprecated as it causes accessibility problems. Use AJAX or a submit button.";

			/**
			 * Registry setter helper for selects which are loaded dynamically via a datalist. Stores the option which
			 * was selected on load.
			 * @function
			 * @private
			 * @param {Element} element The select element to store.
			 */
			function setLoadedOptionRegistry(element) {
				var elementId = element.id;
				optionOnLoad[elementId] = getElementValue(element);
			}

			/**
			 * Registry getter helper for selects which are loaded dynamically via a datalist. Get the option which was
			 * selected on load.
			 * @function
			 * @private
			 * @param {Element} element A select element.
			 */
			function getLoadedOptionRegistry(element) {
				var elementId = element.id;
				return optionOnLoad[elementId];
			}

			/**
			 * Registry unsetter helper for selects which are loaded dynamically via a datalist: removes the reference
			 * to the option which was selected on load.
			 * @function
			 * @private
			 * @param {Element} element The select to unset.
			 */
			function removeLoadedOptionRegistry(element) {
				var elementId = element.id;
				optionOnLoad[elementId] = null;
			}

			/**
			 * Get the serialized value of an element if it is a select which is loaded dynamically via a datalist.
			 * @function
			 * @private
			 * @param {Element} element The element to serialize.
			 * @returns {String} The serialized value of element if it is a cacheable SELECT.
			 */
			function getElementValue(element) {
				var result;
				if (LOAD_SELECT.isOneOfMe(element)) {
					result = serialize.serialize([element]);
				}
				return result;
			}

			/**
			 * Fire the custom submit event if needed. Start the custom event rolling by checking if we have an ajax
			 * trigger if so, don't go any further. Otherwise, check if we have a submitOnChange element and queue a
			 * form submission if we do.
			 * @function
			 * @private
			 * @param {Element} element The element firing the submitOnChange.
			 */
			function fireElement(element) {
				var form, loadedOption, testValue;
				if (!submitting) {
					if (!triggerManager || !triggerManager.getTrigger(element)) {
						if (SUBMITTER.isOneOfMe(element) && (form = FORM.findAncestor(element))) {
							if (LOAD_SELECT.isOneOfMe(element)) {
								loadedOption = getLoadedOptionRegistry(element);
								testValue = getElementValue(element);

								if (loadedOption !== testValue) {
									console.warn(DEP_WARNING);
									timers.setTimeout(event.fire, 0, form, event.TYPE.submit);
								}
								removeLoadedOptionRegistry(element);
							} else {
								submitting = true;
								console.warn(DEP_WARNING);
								timers.setTimeout(event.fire, 0, form, event.TYPE.submit);
							}
						}
					}
				} else {
					console.warn("onchange submit fired twice");  // this is going to be hard to spot when the page is submitting
				}
			}


			/**
			 * IE focusin listener used to wire up the change listener on the  target element.
			 * @function
			 * @private
			 * @param {Event} $event The focusin event.
			 */
			function focusEvent($event) {
				var target = $event.target,
					inited = "wc/ui/onchangeSubmit.bootstrap";
				if (SUBMITTER.isOneOfMe(target)) {
					if (!attribute.get(target, inited)) {
						attribute.set(target, inited, true);
						event.add(target, event.TYPE.change, changeEvent);
					}
					if (LOAD_SELECT.isOneOfMe(target)) {
						setLoadedOptionRegistry(target);
					}
				}
			}

			/**
			 * Focus event listener for focus for browsers which can wire directly on focus.
			 * Needed by FF & chrome and used to set up the loaded option of a select with options dynamically loaded
			 * from a datalist.
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 */
			function domFocusEvent($event) {
				var target = $event.target;
				if (LOAD_SELECT.isOneOfMe(target)) {
					setLoadedOptionRegistry(target);
				}
			}

			/**
			 * Change event listener to start the submit process rolling. This is not for checkboxes and radiobuttons
			 * which will fire through the shed observer it is essentially for dropdowns.
			 * @function
			 * @private
			 * @param {Event} $event the change event.
			 */
			function changeEvent($event) {
				var element = $event.target;
				try {
					if (!$event.defaultPrevented && !ignoreChange && SUBMITTER.isOneOfMe(element) && !Widget.isOneOfMe(element, TRIGGERS)) {
						fireElement(element);
					}
				} finally {
					ignoreChange = false;
				}
			}

			/**
			 * Listens to select, deselect, collapse state changes to fire submit on change as required.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being acted upon.
			 */
			function shedObserver(element) {
				if (Widget.isOneOfMe(element, TRIGGERS)) {
					fireElement(element);
				}
			}

			function addAllWarnings(container) {
				if (SUBMITTER.isOneOfMe(container)) {
					instance.warn(container);
				} else {
					Array.prototype.forEach.call(SUBMITTER.findDescendants(container), function(next) {
						instance.warn(next);
					});
				}
			}

			/**
			 * Allow an external module which manipulates labels to be able to set the SoC warning.
			 * @function module:wc/ui/onchangeSubmit.warn
			 * @public
			 * @param {Element} el THe element which may be able to "submit on change"
			 * @param {Element} [lbl] The element's label/legend/labelling element if it is already available - just prevents us having to do double
			 * look-ups.
			 */
			this.warn = function(el, lbl) {
				var myLabel;
				if (!el || !SUBMITTER.isOneOfMe(el) || triggerManager.getTrigger(el)) {
					return;
				}
				myLabel = lbl || getFirstLabelForElement(el);
				if (myLabel) {
					i18n.translate("submitOnChange").then(function(submitOnChangeHint) {
						var hintContent,
							// do not allow an application to override i18n in order to to make this warning empty
							realSoCHint = submitOnChangeHint || "Changing the value of this field will cause immediate save.",
							hint = label.getHint(myLabel);
						if (hint) {
							hintContent = textContent.get(hint);
							if (hintContent.indexOf(realSoCHint) === -1) {
								label.setHint(myLabel, realSoCHint);
							}
						} else {
							label.setHint(myLabel, realSoCHint);
						}
						// if the label is off-screen force it back on.
						classList.remove(myLabel, "wc-off");
					});
				}
			};

			/**
			 * Set up the core body listeners for submit on change.
			 * @function module:wc/ui/onchangeSubmit.initialise
			 * @public
			 * @param {Element} element The element being initialised - document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, domFocusEvent, null, null, true);
					event.add(element, event.TYPE.change, changeEvent, null, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				timers.setTimeout(addAllWarnings, 0, element);
			};

			/**
			 * Call to initialise this instance - wires up boostrap listeners.
			 * @function module:wc/ui/onchangeSubmit.postInit
			 * @public
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.DESELECT, shedObserver);
				shed.subscribe(shed.actions.COLLAPSE, shedObserver);
				processResponse.subscribe(addAllWarnings, true);
			};

			/**
			 * Set a flag to ignore a change event.
			 * @function module:wc/ui/onchangeSubmit.ignoreNextChange
			 * @public
			 */
			this.ignoreNextChange = function() {
				ignoreChange = true;
			};

			/**
			 * Allow external scripts to clear their own prevent submit on next change event before any change event is
			 * fired.
			 * @function module:wc/ui/onchangeSubmit.clearIgnoreChange
			 * @public
			 */
			this.clearIgnoreChange = function() {
				ignoreChange = false;
			};
		}

		/**
		 * Provides a means to invoke a form submission directly from the change event of a form control. If a form control is
		 * marked as 'submitOnChange' then we need to queue up a form submission request when it changes.
		 *
		 * **NOTE:** this has certain negative accessibility implications around unexpectedly changing context. As a consequence
		 * we recommend submitOnChange not be used and it may be removed from future releases.
		 *
		 * @module
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/ajax/triggerManager
		 * @requires module:wc/dom/serialize
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/timers
		 * @requires module:wc/ui/getFirstLabelForElement
		 *
		 * @todo document private members, check source order.
		 * @deprecated
		 */
		var instance = new OnchangeSubmit();
		initialise.register(instance);
		return instance;
	});
