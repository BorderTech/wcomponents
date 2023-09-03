import shed from "wc/dom/shed";
import triggerManager from "wc/ajax/triggerManager";
import ajaxRegion from "wc/ui/ajaxRegion";
import initialise from "wc/dom/initialise";
import uid from "wc/dom/uid";
import convertDynamicContent from "wc/dom/convertDynamicContent";
import timers from "wc/timers";
import event from "wc/dom/event";
import getForm from "wc/ui/getForm";

const MAGIC_CLASS = "wc_magic",
	GET_ATTRIB = "data-wc-get";
let inited;

const magicContainer = `.${MAGIC_CLASS}`;
const dynamicContainer = `${magicContainer}.wc_dynamic`;
const lameContainer = ".wc_lame";
/**
 * Provides a mechanism to load the content of containers on demand. This is used by the various AJAX enabled
 * container components and LAME mode.
 */
const instance = {
	/**
	 * Load a container. This allows any other container component to actively call an ajax load.
	 * Used primarily by {@link module:wc/dom/dialog}.
	 * @function module:wc/ui/containerload.load
	 * @public
	 * @param {Element} element the container to load
	 * @param {boolean} eager true to load without serialising the whole form
	 * @param {boolean} get true to do a HTTP GET rather than POST
	 */
	load: function(element, eager, get) {
		init();
		requestLoad(element, eager, get);
	},

	/**
	 * To be called when a candidate element is made visible.
	 *
	 * @param {Element} element The element being made visisble.
	 */
	onshow: function(element) {
		return handleExpandOrShow(element);
	},

	/**
	 * To be called when a candidate element is expanded.
	 *
	 * @param {Element} element The element being expanded.
	 */
	onexpand: function(element) {
		const _element = findFirstContainer(element, [lameContainer, magicContainer]);
		return handleExpandOrShow(_element);
	},

	/**
	 * Set up the initialisation process for components in the current payload. This is called from the page
	 * setup (XSLT).
	 * @function module:wc/ui/containerload.register
	 * @public
	 * @param {String[]} idArr An array of ids of elements which need to have this functionality.
	 */
	register: function(idArr) {
		if (idArr && idArr.length) {
			initialise.addCallback(() => processNow(idArr));
		}
	}
};

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
	const id = element.id || (element.id = uid()),
		oneShot = !element.matches(dynamicContainer),
		method = get ? "get" : "post",
		serialiseForm = !noPost;

	const alias = element.getAttribute("data-wc-ajaxalias") || id;
	const getData = element.getAttribute(GET_ATTRIB) || null;

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
	let promise;
	if (element) {
		promise = new Promise((resolve, reject) => {
			registerTrigger(element, eager, get);
			const trigger = triggerManager.getTrigger(element.id);
			if (trigger) {
				/* If dynamic we MUST remove the trigger otherwise the panel becomes clickable and will
				 * reload itself on every click in the panel.
				 * If not dynamic (e.g. lazy) we MUST NOT remove the trigger, otherwise we lose the
				 * "one shot" state and a new trigger will be created which always has one shot left
				 * (so lazy effectively becomes dynamic).
				 */
				if (element.matches(dynamicContainer)) {
					triggerManager.removeTrigger(element.id);
				} else {
					/* if not dynamic remove the magic class, otherwise we will reload every time we open
					 * because we build a new trigger for each AJAX load just in case it is closed/hidden
					 * inside a dynamic ancestor
					 */
					element.classList.remove(MAGIC_CLASS);
				}
				// Fire in a timeout to ensure controls have set state for form serialisation
				timers.setTimeout(() => trigger.fire().then(resolve, reject), 0);
			} else {
				reject();
			}
		});
	} else {
		promise = Promise.resolve();
	}
	return promise;
}

/**
 * Helper for shedSubscriber.
 * Deal with an element being expanded or shown.
 *
 * @param element The element being shown or expanded.
 * @private
 * @function
 */
function handleExpandOrShow(element) {
	let promise;
	if (element) {
		if (element.matches(lameContainer)) {
			const form = getForm(element, true);
			if (form) {
				timers.setTimeout(event.fire, 0, form, "submit");
			}
		} else if (element.matches(magicContainer)) {
			promise = requestLoad(element, false, true);
		}
	}
	return promise || Promise.resolve();
}

/**
 * Helper for shedSubscriber.
 * Deal with an element being collapsed or hidden.
 *
 * @param {HTMLElement} element The element being collapsed or hidden.
 * @param {string} action The action, COLLAPSE or HIDE.
 * @private
 * @function
 */
function handleCollapseOrHide(element, action) {
	const el = (action === shed.actions.COLLAPSE) ?
		findFirstContainer(element, [lameContainer, dynamicContainer]) : element;
	if (el) {
		convertDynamicContent(el);
	}
}

function init() {
	if (!inited) {
		inited = true;
		shed.subscribe(shed.actions.EXPAND, instance.onexpand);
		shed.subscribe(shed.actions.COLLAPSE, handleCollapseOrHide);
		shed.subscribe(shed.actions.SHOW, instance.onshow);
		shed.subscribe(shed.actions.HIDE, handleCollapseOrHide);
	}
}

/**
 *
 * @param {Element} element
 * @param {string[]} widgets types to match
 * @return {Element}
 */
function findFirstContainer(element, widgets) {
	if (!element) {
		return null;
	}
	const selector = widgets.join();
	if (element.matches(selector)) {
		return element;
	}
	const result = Array.from(element.children).find(child => child.matches(selector)) || element;
	return result?.matches(selector) ? result : null;
}


/**
 * Request that a container's content be loaded. Deliberately does not check `matches(magicContainer)`
 * so that anything can leverage this functionality regardless of whether it possesses the right "className"
 * or not. In particular can be called by registration scripts built in XSLT phase.
 *
 * @function
 * @private
 * @param {String} id The id of the container to load.
 */
function requestEagerLoad(id) {
	const element = document.getElementById(id);
	if (element && !(element.innerHTML && element.innerHTML.trim())) {
		console.log("Eager loading: ", id);
		requestLoad(element, true, true);
	}
}

function processNow(idArr) {
	init();
	let id;
	while ((id = idArr.shift())) {
		requestEagerLoad(id);
	}
}

// we have to register the initialise because we have components which rely on the shed subscribers to
//    load containers (collapsible, tab etc.)
initialise.addCallback(init);

export default instance;
