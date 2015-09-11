/**
 * Provides functionality to automatically fire an ajax trigger after 'n' milliseconds.
 * @module
 * @requires module:wc/ajax/triggerManager
 * @requires module:wc/dom/initialise
 * @requires module:wc/timers
 */
define(["wc/ajax/triggerManager", "wc/dom/initialise", "wc/timers"],
	/** @param triggerManager wc/ajax/triggerManager @param initialise wc/dom/initialise @param timers wc/timers @ignore */
	function(triggerManager, initialise, timers) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/ajax/delayedTrigger~DelayedTrigger
		 * @private
		 */
		function DelayedTrigger() {
			/**
			 * Fire a particular ajax trigger.
			 *
			 * @function
			 * @private
			 * @param {String} id The trigger's id.
			 */
			function trigger(id) {
				var trig = triggerManager.getTrigger(id);
				try {
					if (trig) {
						trig.method = trig.METHODS.GET;
						trig.serialiseForm = false;
						trig.fire();
					}
				}
				catch (ex) {
					console.log("error in delayed ajax trigger for id " + id, ex.message);
				}
			}

			/**
			 * Process an array of delayed trigger registration objects.
			 *
			 * @function
			 * @private
			 * @param {module:wc/ui/ajax/delayedTrigger~registrationObject[]} objArr
			 */
			function process(objArr) {
				var regObj;
				while ((regObj = objArr.shift())) {
					timers.setTimeout(trigger, regObj.delay, regObj.id);
				}
			}

			/**
			 * Registration function for delayed triggers.
			 *
			 * @function module:wc/ui/ajax/delayedTrigger.register
			 * @public
			 * @param {module:wc/ui/ajax/delayedTrigger~registrationObject[]} objArr
			 */
			this.register = function(objArr) {
				initialise.addCallback(function() {
					process(objArr);
				});
			};
		}
		return /** @alias module:wc/ui/ajax/delayedTrigger */ new DelayedTrigger();

		/**
		 * @typedef {Object} module:wc/ui/ajax/delayedTrigger~registrationObject
		 * @property {String} id An identifier.
		 * @property {int} delay How long to wait in milliseconds.
		 */
	});
