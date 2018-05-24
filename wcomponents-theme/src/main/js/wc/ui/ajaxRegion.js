define(["wc/dom/event",
	"wc/debounce",
	"wc/dom/attribute",
	"wc/dom/isSuccessfulElement",
	"wc/dom/tag",
	"wc/ajax/Trigger",
	"wc/ajax/triggerManager",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/dom/initialise",
	"wc/ui/ajax/processResponse",
	"wc/dom/classList",
	"wc/mixin",
	"wc/timers"],
	function(event, debounce, attribute, isSuccessfulElement, tag, Trigger, triggerManager, shed, Widget, initialise, processResponse, classList, mixin, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/ajaxRegion~AjaxRegion
		 * @private
		 */
		function AjaxRegion() {
			var INITED_FLAG = "wc.ui.ajaxRegion.inited",
				ANCHOR = new Widget(tag.A),
				BUTTON,
				PSEUDO_PROTOCOL_RE = /^[\w]+\:[^\/].*$/,
				ALIAS = "data-wc-ajaxalias",
				ignoreChange = false,
				triggers = [];

			function fireThisTrigger(element, trigger) {
				var result = false, isSuccessful;
				if (trigger) {
					if (trigger.successful === null) {
						result = true;
					} else {
						isSuccessful = isSuccessfulElement(element, true);
						if ((trigger.successful === true && isSuccessful) || (trigger.successful === false && !isSuccessful)) {
							result = true;
						}
					}
				}
				if (result) {
					trigger._submitTriggerElement = true;
					// the trigger may still decide not to fire (eg all shots are used up)
					trigger.fire();
				}
				return result;
			}

			/**
			 * Checks if an element is an ajax trigger and if so fires it.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element we consider a candidate for being an AJAX trigger. If the element is indeed an AJAX trigger then
			 * it will be fired by this function.
			 */
			function checkActivateTrigger(element) {
				var trigger;
				if ((trigger = instance.getTrigger(element))) {
					return fireThisTrigger(element, trigger);
				}
				return false;
			}

			/**
			 * Is an element a form submitting element?
			 * @function
			 * @private
			 * @param {Element} element The element to test.
			 * @returns {Boolean} true if the element is a type that submits a form when clicked (ie a submit button).
			 */
			function isSubmitElement(element) {
				var result = false,
					tagName = element.tagName,
					type = element.type;
				if (type === "submit" || type === "image") {
					if (tagName === tag.INPUT || tagName === tag.BUTTON) {
						result = true;
					}
				}
				return result;
			}

			function shedSubscriber(element) {
				var type = element.type;

				if (element && element.tagName === tag.INPUT && (type === "radio" || type === "checkbox")) {
					checkActivateTrigger(element);
				}
			}

			/**
			 * Does an element trigger an ajax request when it changes? Some elements should not do ajax stuff on click,
			 * instead it makes sense for them to use the change event.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to check whether it does ajax on change.
			 * @returns {Boolean} true if this element should ajax on change.
			 */
			function triggersOnChange(element) {
				var tagName = element.tagName,
					type = element.type;
				// NOTE: a standalone listbox or dropdown is an ajax trigger, a select element as a sub element of a compund controller is not
				if (shed.isSelectable(element) || classList.contains(element, "wc-noajax")) {
					return false;
				}
				// Don't allow file to trigger on change it breaks multiFileUploader when large number of files are selected
				return (tagName === tag.SELECT || tagName === tag.TEXTAREA || (tagName === tag.INPUT && type !== "file"));
			}

			/*
			 * @param {Event} $event An event
			 */
			function clickEvent($event) {
				var element;
				BUTTON = BUTTON || new Widget(tag.BUTTON);
				element = Widget.findAncestor($event.target, [BUTTON, ANCHOR]);

				if (!$event.defaultPrevented && element && !shed.isDisabled(element) && checkActivateTrigger(element) && (isSubmitElement(element) || isNavLink(element))) {
					$event.preventDefault();
				}
			}

			/*
			 * Some elements should fire trigger on change event, for example SELECT elements.
			 * @param {Event} $event An event
			 */
			function changeEvent($event) {
				if (ignoreChange) {
					return;
				}
				checkActivateTrigger($event.target);
			}

			/**
			 * Focus event listener adds a change event to triggers which trigger on change.
			 *
			 * @function
			 * @private
			 * @param {Event} $event A focus event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && !attribute.get(element, INITED_FLAG) && triggersOnChange(element)) {
					attribute.set(element, INITED_FLAG, true);
					event.add(element, event.TYPE.change, debounce(changeEvent, 250), 100);
				}
			}

			/**
			 * Check whether this element is an anchor element itself OR if it is nested within an anchor element.
			 * Only returns true if the link will navigate the page. If it is a link that will target another frame
			 * or window, or will launch an external app (via a custom protocol e.g. mailto:) then it will return
			 * false.
			 *
			 * @function
			 * @private
			 * @param {Element} element
			 * @returns {boolean} true if the element is a link which will navigate the page
			 */
			function isNavLink(element) {
				var result = false, link = ANCHOR.findAncestor(element);
				if (link && link.getAttribute("aria-haspopup") !== "true" && !link.getAttribute("target") && !PSEUDO_PROTOCOL_RE.test(link.href)) {
					result = true;
				}
				return result;
			}

			/**
			 * Accessibility helper. Any target of an ajax trigger should be marked as an aria-live region.
			 * @function
			 * @private
			 */
			function setControlsAttribute() {
				if (triggers && triggers.length) {
					try {
						triggers.forEach(function(next) {
							var trigger = triggerManager.getTrigger(next),
								controllerId,
								controller,
								loads;
							if (!trigger) {
								return;
							}
							controllerId = trigger.alias || trigger.id;
							if (controllerId && (controller = document.getElementById(controllerId)) && (loads = trigger.loads) && loads.length) {
								controller.setAttribute("aria-controls", loads.join(" "));
								loads.forEach(function(load) {
									var loadEl = document.getElementById(load);
									if (loadEl) {
										loadEl.setAttribute("aria-live", "polite");
									}
								});
							}
						});
					} finally {
						triggers = [];
					}
				}
			}

			/**
			 * Set up event and {@link module:wc/dom/shed} subscribers.
			 * @function module:wc/ui/ajaxRegion.initialise
			 * @public
			 * @param {Element} element document body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, 50); // Trigger ajax AFTER other events to avoid submitting form fields before they can be updated.
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				console.log("Initialising trigger listeners");
			};

			/**
			 * Late initialisation. We set the aria-live regions late to give everything time to register its triggers.
			 * @function
			 * @public
			 */
			this.postInit = function() {
				setControlsAttribute();
				processResponse.subscribe(setControlsAttribute, true);
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSubscriber);
			};

			/**
			 * Get an ajax trigger associated with an element or id.
			 * @function module:wc/ui/ajaxRegion.getTrigger
			 * @public
			 * @param {(String|Element)} arg The ID of the trigger to retrieve OR a DOM element which may be associated with a trigger.
			 * @param {boolean} [ignoreAncestor] If true will not search in DOM ancestry for an element with a trigger.
			 * @returns {module:wc/ajax/Trigger} The trigger, if found.
			 * @see {@link module:wc/ajax/triggerManager#getTrigger} for full details.
			 */
			this.getTrigger = function(arg, ignoreAncestor) {
				var result;
				result = triggerManager.getTrigger(arg, ignoreAncestor);
				return result;
			};

			/**
			 * Register and fire an ajaxTrigger only when required. This is used when we do not want to fire an
			 * ajaxTrigger on change or click (eg WShuffler, WMultiSelectPair) but in some other circumstance.
			 * @function module:wc/ui/ajaxRegion.requestLoad
			 * @public
			 * @param {Element} element The element which is being changed.
			 * @param {Object} [obj] A trigger definition dto.
			 * @param {Boolean} [ignoreAncestor] Indicates to not look up the tree when trying to find a trigger.
			 */
			this.requestLoad = function(element, obj, ignoreAncestor) {
				var trigger = triggerManager.getTrigger(element, ignoreAncestor),
					alias,
					loads,
					id,
					controls;

				if (!trigger) {
					if (obj) {
						this.register(obj);
					} else {
						id = element.id;
						alias = element.getAttribute(ALIAS);
						if ((controls = element.getAttribute("aria-controls"))) {
							loads = controls.split(" ");
						} else {
							loads = [id];
						}
						this.register({
							id: id,
							loads: loads,
							alias: alias});
					}

					trigger = triggerManager.getTrigger(element);
				} else if (obj) {
					mixin(obj, trigger);  // QC158630
				}

				if (trigger) {
					fireThisTrigger(element, trigger);
				}
			};

			/**
			 * Allow external scripts to clear their own prevent submit on next change event before any change event is fired.
			 * @function module:wc/ui/ajaxRegion.clearIgnoreChange
			 * @public
			 */
			this.clearIgnoreChange = function() {
				ignoreChange = false;
			};

			/**
			 * Set a flag to ignore a change event, useful if doing a lot of changes and only want to fire one AJAX request.
			 * @function  module:wc/ui/ajaxRegion.ignoreNextChange
			 * @public
			 */
			this.ignoreNextChange = function() {
				ignoreChange = true;
			};

			/**
			 * Fire a delayed trigger.
			 * @function
			 * @private
			 * @param {String} triggerId the ID of the trigger to fire
			 */
			function fireAfterDelay(triggerId) {
				var trigger = triggerManager.getTrigger(triggerId);
				if (trigger) {
					try {
						trigger.method = trigger.METHODS.GET;
						trigger.serialiseForm = false;
						trigger.oneShot = 1;
						trigger.fire();
					} catch (ex) {
						console.log("error in delayed ajax trigger for id " + triggerId, ex.message);
					}
				}
			}

			/**
			 * Helper for module:wc/ui/ajaxRegion.register.
			 * @function
			 * @private
			 * @param {Object} next The registration object
			 */
			function _register(next) {
				var trigger = new Trigger(next, processResponse.processResponseXml, processResponse.processError),
					delay = next.delay,
					triggerId = next.id;
				triggerManager.addTrigger(trigger);
				if (!triggers) {
					triggers = [];
				}
				triggers[triggers.length] = triggerId;
				if (delay) {
					initialise.addCallback(function() {
						timers.setTimeout(fireAfterDelay, delay, triggerId);
					});
				}
				return trigger;
			}

			/**
			 * Register an ajax trigger. This method is a wrapper for {@link module:wc/ajax/triggerManager#addTrigger} and UI controls will usually
			 * use this module rather than going straight to the trigger manager module.
			 * @see {@link module:wc/ajax/triggerManager}
			 * @function module:wc/ui/ajaxRegion.register
			 * @public
			 * @param {Object} obj The registration object
			 */
			this.register = function (obj) {
				if (Array.isArray(obj)) {
					obj.forEach(_register);
				} else {
					_register(obj);
				}
			};
		}

		/**
		 * This module is responsible for working out which elements should trigger an AJAX request and when.
		 *
		 * The response processing is in {@link module:wc/ui/ajax/processResponse} to solve some unpleasant circular dependencies and potential races.
		 *
		 * @module
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/isSuccessfulElement
		 * @requires module:wc/dom/tag
		 * @requires module:wc/ajax/Trigger
		 * @requires module:wc/ajax/triggerManager
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/ajax/processResponse
		 *
		 * @todo re-order code, document private members.
		 */
		var instance = new AjaxRegion();
		initialise.register(instance);
		return instance;
	});
