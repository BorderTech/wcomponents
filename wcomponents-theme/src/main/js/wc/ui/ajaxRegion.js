/**
 * The idea behind AJAX Regions is that parts of a web page (regions) are dynamically updated.
 *
 * The AjaxRegion class is responsible for working out which elements should trigger an AJAX request and when. The
 * response processing has been moved out to {@link module:wc/ui/ajax/processResponse} to solve some unpleasant
 * circular dependencies and potential races.
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
define(["wc/dom/event",
		"wc/dom/attribute",
		"wc/dom/isSuccessfulElement",
		"wc/dom/tag",
		"wc/ajax/Trigger",
		"wc/ajax/triggerManager",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/dom/initialise",
		"wc/ui/ajax/processResponse"],
	/** @param event wc/dom/event @param attribute wc/dom/attribute @param isSuccessfulElement wc/dom/isSuccessfulElement @param tag wc/dom/tag @param Trigger wc/ajax/Trigger @param triggerManager wc/ajax/triggerManager @param shed wc/dom/shed @param Widget wc/dom/Widget @param initialise wc/dom/initialise @param processResponse wc/ui/ajax/processResponse @ignore */
	function(event, attribute, isSuccessfulElement, tag, Trigger, triggerManager, shed, Widget, initialise, processResponse) {
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
				ignoreChange = false;
			/**
			 * Register an ajax trigger. This method is a wrapper for {@link module:wc/ajax/triggerManager#addTrigger}
			 * and UI controls will usually use this module rather than going straight to the trigger manager module.
			 * @see {@link module:wc/ajax/triggerManager}
			 * @function module:wc/ui/ajaxRegion.register
			 * @public
			 * @param {Object} obj The registration object
			 */
			this.register = function (obj) {
				function registerTrigger(next) {
					var trigger = new Trigger(next, processResponse.processResponseXml, processResponse.processError);
					triggerManager.addTrigger(trigger);
					return trigger;
				}

				if (Array.isArray(obj)) {
					obj.forEach(registerTrigger);
				}
				else {
					registerTrigger(obj);
				}
			};

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
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSubscriber);
				console.log("Initialising trigger listeners");
			};

			/**
			 * Get an ajax trigger associated with an element or id.
			 * @function module:wc/ui/ajaxRegion.getTrigger
			 * @public
			 * @param {(String|Element)} arg The ID of the trigger to retrieve OR a DOM element which may be associated with a trigger.
			 * @param {boolean} [ignoreAncestor] If true will not search in DOM ancestry for an element with a trigger.
			 * @returns {?module:wc/ajax/Trigger} The trigger, if found.
			 * @see {@link module:wc/ajax/triggerManager#getTrigger} for full details.
			 */
			this.getTrigger = function(arg, ignoreAncestor) {
				var result;
				result = triggerManager.getTrigger(arg, ignoreAncestor);
				return result;
			};

			/**
			 * Determines if a given element is an ajax trigger. NOTE: this will return true if the element is an active
			 * trigger even if it has used up its shots.
			 * @function  module:wc/ui/ajaxRegion.isTrigger
			 * @public
			 * @param {Element} element the element to test.
			 * @returns {Boolean} Return true if the element is an ajax trigger.
			 */
			this.isTrigger = function(element) {
				var result = false;
				if (!shed.isDisabled(element)) {
					if (element.hasAttribute(ALIAS)) {
						result = true;
					}
					else {
						result = !!this.getTrigger(element);
					}
				}
				return result;
			};

			/**
			 * Register and fire an ajaxTrigger only when required. This is used when we do not want to fire an
			 * ajaxTrigger on change or click (eg WShuffler, WMultiSelectPair) but in some other circumstance.
			 * @function module:wc/ui/ajaxRegion.requestLoad
			 * @public
			 * @param {Element} element The element which is being changed.
			 * @param {Object} [obj] A trigger definition dto.
			 */
			this.requestLoad = function(element, obj) {
				var trigger,
					alias,
					loads,
					id;
				if (element.hasAttribute("aria-controls")) {
					trigger = triggerManager.getTrigger(element);

					if (!trigger) {
						if (obj) {
							this.register(obj);
						}
						else {
							id = element.id;
							alias = element.getAttribute(ALIAS);
							loads = element.getAttribute("aria-controls").split(" ");
							this.register({
								id: id,
								loads: loads,
								alias: alias});
						}

						trigger = triggerManager.getTrigger(element);
					}

					if (trigger) {
						fireThisTrigger(element, trigger);
					}
				}
			};

			/**
			 * Checks if an element is an ajax trigger and if so fires it.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element we consider a candidate for being an AJAX trigger. If the element is
			 *    indeed an AJAX trigger then it will be fired by this function.
			 * NOTE: all ajaxTriggers will have an attribute "data-wc-ajaxalias"
			 */
			function checkActivateTrigger(element) {
				var result = false, trigger;
				if (instance.isTrigger(element)) {
					trigger = instance.getTrigger(element, true);
					result = fireThisTrigger(element, trigger);
				}
				return result;
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

			/**
			 * Does an element trigger an ajax request when it changes?
			 *
			 * Some elements should not do ajax stuff on click, instead it makes sense for them to
			 * use the change event.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to check whether it does ajax on change.
			 * @returns {Boolean} true if this element should ajax on change.
			 */
			function triggersOnChange(element) {
				var tagName = element.tagName,
					type = element.type,
					result = false;
				// NOTE: a standalone listbox or dropdown is an ajax trigger, a select element as a sub element of a compund controller is not
				if ((tagName === tag.SELECT && element.getAttribute(ALIAS) === element.id) || tagName === tag.TEXTAREA ||
						(tagName === tag.INPUT && !(type === "radio" || type === "checkbox" || type === "file"))) {
					// Don't allow file to trigger on change it breaks multiFileUploader when large number of files are selected
					result = true;
				}
				return result;
			}

			function shedSubscriber(element) {
				var type = element.type;

				if (element && element.tagName === tag.INPUT && (type === "radio" || type === "checkbox")) {
					checkActivateTrigger(element);
				}
			}


			/*
			 * @param {Event} $event An event
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented) {
					BUTTON = BUTTON || new Widget(tag.BUTTON);
					element = Widget.findAncestor($event.target, [BUTTON, ANCHOR]);

					if (element && !shed.isDisabled(element) && checkActivateTrigger(element) && (isSubmitElement(element) || isNavLink(element))) {
						$event.preventDefault();
					}
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
					event.add(element, event.TYPE.change, changeEvent, 100);
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

			function fireThisTrigger(element, trigger) {
				var result = false, isSuccessful;
				if (trigger) {
					if (trigger.successful === null) {
						result = true;
					}
					else {
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
			 * Set a flag to ignore a change event, useful if doing a lot of changes and only want to fire one AJAX
			 * request.
			 * @function  module:wc/ui/ajaxRegion.ignoreNextChange
			 * @public
			 */
			this.ignoreNextChange = function() {
				ignoreChange = true;
			};

			/**
			 * Allow external scripts to clear their own prevent submit on next
			 * change event before any change event is fired.
			 * @function module:wc/ui/ajaxRegion.clearIgnoreChange
			 * @public
			 */
			this.clearIgnoreChange = function() {
				ignoreChange = false;
			};
		}
		var /** @alias module:wc/ui/ajaxRegion */ instance = new AjaxRegion();
		initialise.register(instance);
		return instance;
	});
