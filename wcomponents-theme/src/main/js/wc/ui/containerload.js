/**
 * Provides a meachanism to load the content of containers on demand. This is used by the various AJAX enabled
 * container components and LAME mode.
 *
 * @module
 *
 * @requires module:wc/dom/shed
 * @requires module:wc/ajax/triggerManager
 * @requires module:wc/ui/ajaxRegion
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/uid
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/convertDynamicContent
 * @requires module:wc/timers
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/event
 *
 * @todo document private members.
 */
define(["wc/dom/shed",
		"wc/ajax/triggerManager",
		"wc/ui/ajaxRegion", "wc/dom/initialise", "wc/dom/uid", "wc/dom/Widget",
		"wc/dom/classList", "wc/dom/convertDynamicContent", "wc/timers", "wc/dom/tag", "wc/dom/event"],
	/** @param shed wc/dom/shed @param triggerManager wc/ajax/triggerManager @param ajaxRegion wc/ui/ajaxRegion @param initialise wc/dom/initialise @param uid wc/dom/uid @param Widget wc/dom/Widget @param classList wc/dom/classList @param convertDynamicContent wc/dom/convertDynamicContent @param timers wc/timers @param tag wc/dom/tag @param event wc/dom/event @ignore */
	function(shed, triggerManager, ajaxRegion, initialise, uid, Widget, classList, convertDynamicContent, timers, tag, event) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/containerload~Container
		 * @private
		 */
		function Container() {
			var MAGIC_CLASS = "wc_magic",
				MAGIC_CONTAINER = new Widget("", MAGIC_CLASS),
				DYNAMIC_CONTAINER = MAGIC_CONTAINER.extend("wc_dynamic"),
				LAME_CONTAINER = new Widget("", "wc_lame"),
				GET_ATTRIB = "${wc.ui.ajax.attribute.getData}",
				FORM,
				inited;

			/**
			 * register an ajaxTrigger for the container when it is required.
			 * NOTE: ajax containers are self loading, so the "loads" property is itself.
			 *
			 * @function
			 * @private
			 * @param {Element} element The container for which we are registering a trigger.
			 * @param {Boolean} noPost Indicates the trigger does not need to post data (for eager load).
			 * @param {Boolean} get Use GET rather than POST.
			 */
			function registerTrigger(element, noPost, get) {
				var alias, getData,
					id = element.id || (element.id = uid()),
					oneShot = !DYNAMIC_CONTAINER.isOneOfMe(element),
					method = get ? "get" : "post",
					serialiseForm = !noPost;

				alias = element.getAttribute("data-wc-ajaxalias") || id;
				getData = element.getAttribute(GET_ATTRIB) || null;

				ajaxRegion.register({
					id: id,
					loads: [id],
					alias: alias,
					oneShot: oneShot,
					getData: getData,
					serialiseForm: serialiseForm,
					method: method,
					formRegion: alias
				});
			}

			/**
			 * Calls for ajax load of an element (container).
			 *
			 * The element will be automatically ajax enabled and set up its own trigger.
			 * It is up to the caller to ensure that it makes sense to load this element via AJAX.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to load
			 * @param {Boolean} eager true for eager load (does not need to post data)
			 * @param {boolean} get true to use GET rather than POST which is useful for eager panels.
			 */
			function requestLoad(element, eager, get) {
				var trigger;
				if (element) {
					registerTrigger(element, eager, get);
					trigger = triggerManager.getTrigger(element.id);
					if (trigger) {
						/* If dynamic we MUST remove the trigger otherwise the panel becomes clickable and will
						 * reload itself on every click in the panel.
						 * If not dynamic (e.g. lazy) we MUST NOT remove the trigger, otherwise we lose the
						 * "one shot" state and a new trigger will be created which always has one shot left
						 * (so lazy effectively becomes dynamic).
						 */
						if (DYNAMIC_CONTAINER.isOneOfMe(element)) {
							triggerManager.removeTrigger(element.id);
						}
						/* if not dynamic remove the magic class, otherwise we will reload every time we open
						 * because we build a new trigger for each AJAX load just in case it is closed/hidden
						 * inside a dynamic ancestor */
						else {
							classList.remove(element, MAGIC_CLASS);
						}
						// Fire in a timeout to ensure controls have set state for form serialisation
						timers.setTimeout(trigger.fire.bind(trigger), 0);
					}
				}
			}


			/**
			 * Listen to shed events and respond accordingly.
			 *
			 * @param element The element being shown or expanded.
			 * @param action The shed action.
			 * @private
			 * @function
			 */
			function shedSubscriber(element, action) {
				var _widgets = [LAME_CONTAINER, MAGIC_CONTAINER];

				if (action === shed.actions.EXPAND || action === shed.actions.SHOW) {
					handleExpandOrShow(element, action, _widgets);
				}
				else if (action === shed.actions.COLLAPSE || action === shed.actions.HIDE) {
					handleCollapseOrHide(element, action, _widgets);
				}
			}

			/**
			 * Helper for shedSubscriber.
			 * Deal with an element being expanded or shown.
			 *
			 * @param element The element being shown or expanded.
			 * @param action The action, EXPAND or SHOW.
			 * @param _widgets Array of "my" widgets.
			 * @private
			 * @function
			 */
			function handleExpandOrShow(element, action, _widgets) {
				var _element;
				if (action === shed.actions.EXPAND) {
					// _element = LAME_CONTAINER.isOneOfMe(element) ? element : (LAME_CONTAINER.findDescendant(element, true)||element);
					_element = Widget.isOneOfMe(element, _widgets) ? element : Widget.findDescendant(element, _widgets, true);
				}
				else {
					_element = element;
				}
				if (_element && Widget.isOneOfMe(_element, _widgets)) {
					if (LAME_CONTAINER.isOneOfMe(_element)) {
						FORM = FORM || new Widget(tag.FORM);
						if ((_element = FORM.findAncestor(_element))) {
							timers.setTimeout(event.fire, 0, _element, event.TYPE.submit);
						}
					}
					else {
						requestLoad(_element, false, true);
					}
				}
			}

			/**
			 * Helper for shedSubscriber.
			 * Deal with an element being collapsed or hidden.
			 *
			 * @param element The element being collapsed or hidden.
			 * @param action The action, COLLAPSE or HIDE.
			 * @param _widgets Array of "my" widgets.
			 * @private
			 * @function
			 */
			function handleCollapseOrHide(element, action, _widgets) {
				var _element;
				_widgets = [LAME_CONTAINER, DYNAMIC_CONTAINER];
				if (action === shed.actions.COLLAPSE) {
					 _element = Widget.isOneOfMe(element, _widgets) ? element : (Widget.findDescendant(element, _widgets, true) || element);
				}
				else {
					_element = element;
				}
				if (_element && Widget.isOneOfMe(_element, _widgets)) {
					convertDynamicContent(_element);
				}
			}

			function init() {
				if (!inited) {
					inited = true;
					shed.subscribe(shed.actions.EXPAND, shedSubscriber);
					shed.subscribe(shed.actions.COLLAPSE, shedSubscriber);
					shed.subscribe(shed.actions.SHOW, shedSubscriber);
					shed.subscribe(shed.actions.HIDE, shedSubscriber);
				}
			}


			/**
			 * Request that a container's content be loaded. Deliberately does not check MAGIC_CONTAINER.isOneOfMe
			 * so that anything can leverage this functionality regardless of whether it possesses the right "className"
			 * or not. In particular can be called by registration scripts built in XSLT phase.
			 *
			 * @function
			 * @private
			 * @param {String} id The id of the container to load.
			 */
			function requestEagerLoad(id) {
				var element = document.getElementById(id);
				if (element && !(element.innerHTML && element.innerHTML.trim())) {
					console.log("Eager loading: ", id);
					requestLoad(element, true, true);
				}
			}


			function processNow(idArr) {
				var id;
				init();
				while ((id = idArr.shift())) {
					requestEagerLoad(id);
				}
			}

			// we have to register the initialise because we have components which rely on the shed subscribers to
			//    load containers (collapsible, tab etc)
			initialise.addCallback(init);

			/**
			 * Load a container. This allows any other container component to actively call an ajax load. Used primarily
			 * by {@link module:wc/dom/dialog}.
			 * @function module:wc/ui/containerload.load
			 * @public
			 * @param {Element} element the container to load
			 * @param {boolean} eager true to load without serialising the whole form
			 * @param {boolean} get true to do a HTTP GET rather than POST
			 */
			this.load = function(element, eager, get) {
				init();
				requestLoad(element, eager, get);
			};
			/**
			 * Set up the initialisation process for components in the current payload. This is called from the page
			 * setup (XSLT).
			 * @function module:wc/ui/containerload.register
			 * @public
			 * @param {String[]} idArr An array of ids of elements which need to have this functionality.
			 */
			this.register = function(idArr) {
				if (idArr && idArr.length) {
					initialise.addCallback(function() {
						processNow(idArr);
					});
				}
			};
		}
		return /** @alias module:wc/ui/containerload */ new Container();
	});
