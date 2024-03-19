import diagnostic from "wc/dom/diagnostic.mjs";
import wrappedInput from "wc/dom/wrappedInput.mjs";
import icon from "wc/ui/icon.mjs";
import getLabelsForElement from "wc/dom/getLabelsForElement.mjs";
import wcconfig from "wc/config.mjs";

const checkables = ["input[type='checkbox']", "input[type='radio']"].join();
const writeOutsideThese = ["input", "select", "textarea"].join(),
	BEFORE_END = "beforeend";

const instance = {
	/**
	 * For the convenience of consuming UI modules.
	 */
	LEVEL: diagnostic.LEVEL,
	/**
	 *
	 * @param {Element} element
	 * @param {module:wc/dom/diagnostic.LEVEL} level
	 * @return {boolean}
	 */
	isOneOfMe: (element, level) => diagnostic.isOneOfMe(element, level),

	/**
	 * Change the diagnostic level of an existing diagnostic box.
	 * @function
	 * @public
	 * @param {Element} box the diagnostic box to change
	 * @param {module:wc/dom/diagnostic.LEVEL} toLevel the level to change to
	 * @param {Element} [target] the diagnostic box's target element if already known - saves us finding it twice.
	 */
	change: function(box, toLevel, target) {
		check(box, false);
		if (!toLevel || toLevel < 1) {
			console.log("twit");
			return;
		}
		const oldLevel = diagnostic.getLevel(box);
		if (oldLevel === toLevel) {
			return;  // nothing to do
		}
		const newClass = diagnostic.getBoxClass(toLevel);
		const oldClass = diagnostic.getBoxClass(oldLevel);
		if (oldClass === newClass) {
			// we probably tried to get to a non-existent level.
			return;
		}
		const newIdExtension = diagnostic.getIdExtension(toLevel);
		const oldIdExtension = diagnostic.getIdExtension(oldLevel);
		const testId = box.id.replace(oldIdExtension, newIdExtension);
		// if we already have a diagnostic box at the requested level we cannot create a new one
		if (box.ownerDocument.getElementById(testId)) {
			console.log("cannot create diagnostic box with duplicate id");
			// this.remove(diag, target);
			this.clear(box);
			return;
		}
		box.classList.add(newClass);
		box.classList.remove(oldClass);
		box.id = testId;
		this.clear(box);
		// now change the icon
		changeIcon(box, oldLevel, toLevel);
		const realTarget = target || diagnostic.getTarget(box);
		if (realTarget) {
			toggleValidity(realTarget);
		}
	},

	/**
	 * Remove all messages from a diagnostic box.
	 * @function
	 * @public
	 * @param {Element} box the diagnostic box to change
	 * @throws {TypeError} if `diag` is not a diagnostic box
	 */
	clear: function(box) {
		check(box, false);
		const messages = this.getMessages(box);
		if (messages) {
			messages.forEach(next => box.removeChild(next));
		}
	},

	/**
	 * Add messages to an existing diagnostic box.
	 * @function
	 * @public
	 * @param {Element} box the diagnostic box
	 * @param {String|String[]} messages the message(s) to add
	 * @return {HTMLElement|HTMLElement[]} The message element(s) in the DOM.
	 */
	addMessages: function(box, messages) {
		let result;
		check(box, false);
		if (Array.isArray(messages)) {
			result = messages.map(next => addHelper(box, next));
		} else {
			result = addHelper(box, messages);
		}
		return result;
	},

	/**
	 * Gets the messages already inside a given diagnostic box.
	 * @function
	 * @public
	 * @param {Element} box the diagnostic box
	 * @returns {HTMLElement[]} messages inside the diagnostic box, if any
	 */
	getMessages: function(box) {
		if (!check(box, true)) {
			return null;
		}
		return Array.from(box.querySelectorAll(diagnostic.getMessage()));
	},

	/**
	 * Set the messages inside an existing message box to a new message or set of messages.
	 * @function
	 * @public
	 * @param {Element} box the diagnostic box
	 * @param {String|String[]} messages
	 */
	set: function(box, messages) {
		check(box, false);
		this.clear(box);
		if (messages) {
			this.addMessages(box, messages);
		}
	},

	/**
	 * Public for testing.
	 * @ignore
	 */
	_flag: flag,

	/**
	 * Flag a component with an error message.
	 * @function
	 * @public
	 * @param {module:wc/ui/feedback~flagDto} args a config dto
	 * @returns {String} the id of the error container (if one is present/created)
	 */
	flagError: function(args) {
		const dto = args;
		dto.level = this.LEVEL.ERROR;
		return flag(dto);
	},

	/**
	 * Flag a component with a warning message.
	 * @function
	 * @public
	 * @param {module:wc/ui/feedback~flagDto} args a config dto
	 * @returns {String} the id of the message container (if one is present/created)
	 */
	flagWarning: function (args) {
		const dto = args;
		dto.level = this.LEVEL.WARN;
		return flag(dto);
	},

	/**
	 * Flag a component with an info message.
	 * @function
	 * @public
	 * @param {module:wc/ui/feedback~flagDto} args a config dto
	 * @returns {String} the id of the message container (if one is present/created)
	 */
	flagInfo: function (args) {
		const dto = args;
		dto.level = this.LEVEL.INFO;
		return flag(dto);
	},

	/**
	 * Flag a component with a success message.
	 * @function
	 * @public
	 * @param {module:wc/ui/feedback~flagDto} args a config dto
	 * @returns {String} the id of the message container (if one is present/created)
	 */
	flagSuccess: function (args) {
		const dto = args;
		dto.level = this.LEVEL.SUCCESS;
		return flag(dto);
	},

	/**
	 * Find a diagnostic box belonging to an element.
	 * @function
	 * @public
	 * @param {Element|String} element the element being diagnosed (or its id)
	 * @param {number} [ofLevel=1] the diagnostic level, if not set get ERROR diagnostic box. Set to -1 to get one of any type.
	 * @returns {HTMLElement} the diagnostic box of the required level (if any).
	 */
	getBox: function (element, ofLevel) {
		const level = ofLevel || this.LEVEL.ERROR;
		let result, transientWidget;

		let target = checkAndGetElement(element);
		if (wrappedInput.isWrappedInput(target)) {
			target = wrappedInput.getWrapper(target);
		}
		if (!target.id) {
			return null;
		}
		const id = target.id;

		if (level === -1) {
			transientWidget = `${diagnostic.getWidget()}[data-wc-dfor='${id}']`;
			result = target.querySelector(transientWidget);
			if (result) {  // fast but insufficient
				return /** @type {HTMLElement} */ (result);
			}
			for (let lvl in this.LEVEL) {
				result = this.getBox(element, this.LEVEL[lvl]);
				if (result) {
					return result;
				}
			}
			return null;
		}
		transientWidget = `${diagnostic.getByType(level)}[data-wc-dfor='${id}']`;
		return target.querySelector(transientWidget) || target.ownerDocument.body.querySelector(transientWidget);
	},

	/**
	 * Get the last diagnostic box WITHIN (or withing the wrapper of) a
	 * @param {Element|String} element the element being tested or an id of an element
	 * @returns {HTMLElement} the last diagnostic box if any.
	 */
	getLast: function(element) {
		let target = checkAndGetElement(element);
		if (!target.id) {
			return null;
		}
		if (wrappedInput.isWrappedInput(target)) {
			target = wrappedInput.getWrapper(target);
		}
		const candidates = diagnostic.getWithin(target);
		return candidates.length ? candidates.pop() : null;
	},

	/**
	 *
	 * @param {module:wc/ui/feedback~flagDto} args
	 * @return {String|null}
	 */
	add: function(args) {
		const AFTER_END = "afterend";
		const messages = args.message || args["messages"];  // this was a mess with both properties in use
		const level = args.level || diagnostic.LEVEL.ERROR;
		let writeWhere = args.position,
			target = args.element || args["target"];  // this was a mess with both properties in use

		if (!(messages && target && target.nodeType === Node.ELEMENT_NODE)) {
			// no messages or target for the messages
			// don't throw: just do nothing
			console.warn("trying to add nothing or to nothing");
			return null;
		}

		if (wrappedInput.isWrappedInput(target)) {
			target = wrappedInput.getWrapper(target);
		}
		let flagTarget;
		if (target.matches(checkables)) {
			flagTarget = getLabelsForElement(target);
			flagTarget = flagTarget?.length ? flagTarget[0] : null;
			if (flagTarget) {
				writeWhere = BEFORE_END;
			} else {
				writeWhere = AFTER_END;
			}
		} else if (wrappedInput.isOneOfMe(target)) {
			writeWhere = BEFORE_END;
		}
		if (!writeWhere) {
			writeWhere = target.matches(writeOutsideThese) ? AFTER_END : BEFORE_END;
		}
		flagTarget = flagTarget || target;
		let html = getBoxHTML(target.id, messages, level);
		if (html) {
			const boxId = html.id;
			html = html.html;
			flagTarget.insertAdjacentHTML(writeWhere, html);
			toggleValidity(target);
			return boxId;
		}
		return null;
	},

	/**
	 * Public for testing.
	 * @ignore
	 */
	_removeDiagnostic: removeDiagnostic,

	/**
	 * Remove a feedback message.
	 * @function
	 * @public
	 * @param {Element} element either an error diagnostic or an element with an error diagnostic
	 * @param {Element} [target] an element with a diagnostic **if** element is a diagnostic, and we have already found its "owner".
	 * @param {number} [level=1] the diagnostic level to remove if element is not a diagnostic box
	 * @returns {boolean} `true` if a diagnostic box was found and removed.
	 */
	remove: function(element, target, level) {
		if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
			return false;
		}
		// read carefully before you try merging these two to be more 'efficient'/
		if (this.isOneOfMe(element)) {
			removeDiagnostic(element, target);
			return true;
		}
		const lvl = level || this.LEVEL.ERROR;
		const errorContainer = this.getBox(element, lvl);
		if (errorContainer) {
			removeDiagnostic(errorContainer, element);
			return true;
		}
		return false;
	}
};

/**
 * @param {Element|string} element
 * @return {HTMLElement}
 */
function checkAndGetElement(element) {
	if (!element) {
		throw new TypeError("element must not be falsy");
	}
	const target = /** @type {HTMLElement} */(typeof element === "string" ? document.getElementById(element) : element);

	if (!target?.tagName) {
		throw new TypeError("element does not represent an HTML Element");
	}
	return target;
}

/**
 * Type check for diagnostic boxes.
 * @param {Element} diag the element to test
 * @param {Boolean} lenient if `true` do not error on a failed test, instead return false
 * @returns {Boolean} `true` if `diag` is a diagnostic box, otherwise `false` if `lenient` is `true`.
 * @throws {TypeError} if `diag` is not a diagnostic box and `lenient` is not `true`.
 */
function check(diag, lenient) {
	if (!(diag && diagnostic.isOneOfMe(diag))) {
		if (lenient) {
			return false;
		}
		throw new TypeError("Argument must be a feedback box");
	}
	return true;
}

/**
 *
 * @param {Element} diag
 * @param {number} fromLevel
 * @param {number} toLevel
 */
function changeIcon(diag, fromLevel, toLevel) {
	const oldClass = diagnostic.getIconName(fromLevel),
		newClass = diagnostic.getIconName(toLevel);
	icon.change(diag, newClass, oldClass);
}

/**
 * Mark/unmark a component as invalid.
 * @function
 * @private
 * @param {Element} target the component to mark
 * @param {boolean} [clear] if `true` set the component to be valid **and** remove its `aria-describedby` attribute
 */
function toggleValidity(target, clear) {
	const INVALID_ATTRIB = "aria-invalid",
		DESCRIBED_ATTRIB = "aria-describedby";
	if (!target?.tagName) {
		return;
	}
	const element =  wrappedInput.getInput(target) || target;
	if (element) {
		if (clear) {
			element.removeAttribute(INVALID_ATTRIB);
			element.removeAttribute(DESCRIBED_ATTRIB);
			return;
		}
		let diag = instance.getBox(target, diagnostic.LEVEL.ERROR);
		if (diag) {
			element.setAttribute(INVALID_ATTRIB, "true");
			element.setAttribute(DESCRIBED_ATTRIB, diag.id);
			return;
		}
		diag = instance.getBox(target, -1);
		if (diag) {
			element.removeAttribute(INVALID_ATTRIB);
			element.setAttribute(DESCRIBED_ATTRIB, diag.id);
		}
	}
}

/**
 *
 * @param {string} message
 * @return {string}
 */
function getMessageHTML(message) {
	if (!message) {
		throw new TypeError("Message must not be falsy");
	}
	if (message.constructor !== String) {
		if (message.toString) {
			message = message.toString();
		} else {
			throw new TypeError("Message must be a string");
		}
	}
	return diagnostic.getMessageHtml(message);
}

/**
 * Adds a message to a container.
 * If the message already exists in the container it will not be added again.
 * @param {Element} box The message container
 * @param {string} message The message to add
 * @return {HTMLElement} The message element in the DOM.
 */
function addHelper(box, message) {
	if (!(message && message.constructor === String)) {
		throw new TypeError("Message must be a string");
	}
	let current = instance.getMessages(box);
	if (current) {
		for (const element of current) {
			if (message.toLocaleLowerCase() === element.innerHTML.toLocaleLowerCase()) {
				// already have this message
				return element;
			}
		}
	}
	box.insertAdjacentHTML(BEFORE_END, getMessageHTML(message));
	current = instance.getMessages(box);
	return current[current.length - 1];
}

/**
 * Generate the HTML to create a diagnostic box.
 * @function
 * @private
 * @param {Object} args
 * @param {Element} [args.el] The element which is the diagnostic target if not set then args.id must be set.
 * @param {String} [args.id] The base id for the diagnostic box. If not set then args.el must be an element with an id.
 * @param {number} [args.level=1] the diagnostic level, defaults to ERROR
 * @param {String|String[]|NodeList} [args.messages] If `falsy` then the diagnostic box will be empty. If a String the diagnostic will
 *   contain one message containing this String. If a NodeList then the diagnostic messages will be the innerHTML of each element node
 *   in the NodeList and the textContent of each text node in the NodeList. If something else the messages are treated as a single
 *   "thing" and the diagnostic box will attempt to call toString() on it.
 * @returns {{html: string, id: string}} property html: The HTML which creates a complete diagnostic box, property id: the id of the added box
 */
function getHTML(args) {

	const el = args.el,
		level = args.level || diagnostic.LEVEL.ERROR;
	let messages = args.messages;

	const targetId = args.id || (el ? el.id : null);
	if (!targetId) {
		throw new TypeError("Cannot get error box without an id.");
	}
	let messageHtml;
	if (messages) {
		if (messages instanceof NodeList) {
			messages = Array.from(messages, next=> {
				if (next.nodeType === Node.ELEMENT_NODE) {
					return /** @type HTMLElement */ (next).innerHTML;
				}
				if (next.textContent) {
					return next.textContent;
				}
				return null;
			});
		}
		messageHtml = Array.isArray(messages) ? messages.map(getMessageHTML) : [getMessageHTML(messages)];
	}
	return diagnostic.getBoxHtml(messageHtml, targetId, level, diagnostic.getIconName(level));
}



/**
 * Get the HTML which creates a diagnostic box.
 * @function
 * @private
 * @param {String} targetId the id of the component to which the message box is added
 * @param {String|String[]} messages the message(s) to add
 * @param {number} [level=1] the diagnostic level
 * @returns {Object} property html: The HTML which creates a complete diagnostic box, property id: the id of the box
 */
function getBoxHTML(targetId, messages, level) {
	if (!(targetId && messages)) {
		return null;
	}
	const msgArray = Array.isArray(messages) ? messages : [messages];
	return getHTML({
		id: targetId,
		messages: msgArray,
		level: level
	});
}

/**
 * Remove an existing diagnostic box.
 * @function
 * @public
 * @param {Element} [diag] the box to remove if not set then target must be set
 * @param {Element} [target] the diagnostic's target if diag is not set or if we already have it. If not set and `diag` is set we can
 *   calculate the target element.
 */
function removeDiagnostic(diag, target) {
	if (!(diag || target)) {
		console.error("What! I am magic now? What do you want to remove you womble?");
		throw new TypeError("You forgot the args");
	}
	let realTarget, realDiag;
	if (diag && check(diag, true)) {
		realTarget = target || diagnostic.getTarget(diag);
		realDiag = diag;
	} else if (target && !diag) {
		realDiag = instance.getBox(target, -1);
		realTarget = target;
	}

	if (realDiag) {
		const parent = realDiag.parentElement;
		if (parent) {
			parent.removeChild(realDiag);
		}
		if (realTarget) {
			toggleValidity(realTarget, true);
		}
	}
}

/**
 * Flag a component with a message.
 * @function
 * @private
 * @param {module:wc/ui/feedback~flagDto} args a config dto
 * @returns {String} the id of the message container (if one is present/created)
 */
function flag(args) {
	if (!args) {
		return null;
	}
	let target = args.element;
	const messages = args.message;
	const level = args.level;
	if (!(target && messages && level)) {
		return null;
	}
	if (typeof target === "string") {
		target = document.getElementById(target);
		if (!target) {
			return null;
		}
	}

	// if the target already has an appropriate box then use it
	let errorContainer = instance.getBox(target, level);
	if (errorContainer) {
		instance.change(errorContainer, level);
		instance.addMessages(errorContainer, messages);
		return errorContainer.id;
	} // Success and failure are mutually exclusive
	errorContainer = level === diagnostic.LEVEL.ERROR ? instance.getBox(target, diagnostic.LEVEL.SUCCESS) : null;
	if (!errorContainer && level === diagnostic.LEVEL.SUCCESS) {
		errorContainer = instance.getBox(target, diagnostic.LEVEL.ERROR);
	}
	if (errorContainer) {
		instance.change(errorContainer, level);
		instance.addMessages(errorContainer, messages);
		return errorContainer.id;
	}

	return instance.add({
		element: target,
		message: messages,
		level: level
	});
}

export default instance;

/**
 * @typedef {Object} module:wc/ui/feedback~flagDto The properties used to describe a custom error message.
 * @property {String|String[]} message The message to display.
 * @property {HTMLElement} element The element which is to be flagged with the error message.
 * @property {module:wc/dom/diagnostic.LEVEL|number} [level] The message severity.
 * @property {InsertPosition} [position]
 */
