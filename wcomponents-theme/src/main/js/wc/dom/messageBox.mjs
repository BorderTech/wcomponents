import i18n from "wc/i18n/i18n.mjs";

const MB_CLASS = "wc-messagebox";
const messageBoxTagName = "wc-messagebox";
const messageTagName = "wc-message";

/**
 * WMessageBox HTML template.
 * @param {string} title
 * @param {string} iconClass
 * @param {string} className
 * @return {string}
 */
const template = (title, iconClass, className) => `
	<style>
		h1 > span {
			left: 0;
			max-height: none;
			max-width: none;
			overflow: visible;
			position: static;
		}
	</style>
	<section class="${className}" part="${className}">
		<h1><i aria-hidden="true" class="fa ${iconClass}"></i><span>${title}</span><slot name="icon"/></h1>
	</section>
	<div class="wc_messages" part="wc_messages">
		<slot/>
	</div>`;

/**
 * Pull CSS styles into this Shadow DOM.
 * @param {ShadowRoot} shadow
 */
function yankStyles(shadow) {
	if (shadow.querySelector("link")) {
		return;
	}
	Array.from(document.head.querySelectorAll("link[rel='stylesheet']:not([href*='//'])")).forEach(css => {
		shadow.appendChild(css.cloneNode(true));
	});
}

/**
 *
 * @param {string} type One of: `'success'|'warn'|'info'|'error'`
 * @return {Promise<{title: string, iconClass: string}>}
 */
function getHeader(type) {
	let iconClass = "fa-fw";
	let titleKey;
	switch (type) {
		case "success":
			iconClass = `${iconClass} fa-check-circle`;
			titleKey = "messagetitle_success";
			break;
		case "warn":
			iconClass = `${iconClass} fa-exclamation-triangle`;
			titleKey = "messagetitle_warn";
			break;
		case "info":
			iconClass = `${iconClass} fa-info-circle`;
			titleKey = "messagetitle_info";
			break;
		case "error":  // success was the default in xslt, error in java
		default:
			iconClass = `${iconClass} fa-minus-circle`;
			titleKey = "messagetitle_error";
			break;
	}
	if (titleKey) {
		return i18n.translate(titleKey).then((titleVal) => {
			return {
				iconClass: iconClass,
				title: titleVal
			};
		});
	}
	return Promise.resolve({
		iconClass: iconClass,
		title: ""
	});
}

class WMessageBox extends HTMLElement {
	constructor() {
		super();
		const root = this.attachShadow({ mode: "open" });
		this.getHtml().then(html => {
			root.innerHTML = html.trim();
			yankStyles(this.shadowRoot);
		});
	}

	connectedCallback() {
		if (!this.getAttribute("type")) {
			this.setAttribute("type", "error");
		}
	}

	/**
	 * Generate the HTML for this instance
	 * @return {Promise<string>}
	 */
	getHtml() {
		const type = this.getAttribute("type") || "error";
		const className = `${MB_CLASS} wc-messagebox-type-${type} ${this.className} ${type}`;
		return getHeader(type).then(({ iconClass, title }) => {
			return Promise.resolve(template(title || this.getAttribute("title"), iconClass, className));
		});
	}
}

class WMessage extends HTMLDivElement {
	// Nothing to do here for now
}

if (!customElements.get(messageBoxTagName)) {
	customElements.define(messageBoxTagName, WMessageBox);
	customElements.define(messageTagName, WMessage, { extends: "div" });
}
