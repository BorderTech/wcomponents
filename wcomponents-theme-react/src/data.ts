export async function handleResponse(response: Response): Promise<WComponentNode | null> {
	if (response.ok) {
		const parser = new DOMParser();
		const xml: XMLDocument = parser.parseFromString(await response.text(), "text/xml");
		const root = xml.children[0];
		if (!root || root.tagName !== "ui:root") {
			throw new Error("Cannot find valid ui:root in XML");
		}
		const application = root.children[0];
		if (!application || application.tagName !== "ui:application") {
			throw new Error("Cannot find valid ui:application in XML");
		}
		return getWComponentNodeFromElement(application);
	} else {
		throw new Error(`${response.status} ${response.statusText}`);
	}
}

export async function getClientLayout(): Promise<WComponentNode | null> {
	const response = await fetch("/app", {
		headers: {
			"X-wcnoxslt": "wcnoxslt",
		},
	});

	return handleResponse(response);
}

export async function processSearchRequest(formData: FormData, appParams: { [key: string]: string }) {
	for (const param in appParams) {
		formData.append(param, appParams[param]);
	}

	const response = await fetch("/app", {
		method: "POST",
		headers: {
			"X-wcnoxslt": "wcnoxslt",
		},
		body: formData,
	});

	return handleResponse(response);
}

export async function processAjaxRequest(
	currentApp: WComponentNode,
	appParams: { [key: string]: string },
	triggerId: string,
	targetId: string,
): Promise<WComponentNode | null> {
	const formId = currentApp.id;
	const form = document.getElementById(formId);
	if (!form) {
		return null;
	}

	const formData = new FormData(form as HTMLFormElement);

	for (const param in appParams) {
		formData.append(param, appParams[param]);
	}
	formData.append(triggerId, "x");
	formData.append("wc-ajax", triggerId);

	//formData.append("_1b0b", "");
	//formData.append("main_panel_1b1", "0");
	//formData.append("example_selector_tree-h", "x");
	//formData.append("main_panel_0d0a.selected", "x");
	//formData.append("main_panel_0d-h", "x");

	const response = await fetch(`/app?${new URLSearchParams(appParams).toString()}`, {
		method: "POST",
		headers: {
			"X-wcnoxslt": "wcnoxslt",
		},
		body: formData,
	});

	return handleResponse(response);
}

export function getWComponentNodeFromElement(
	xmlNode: ChildNode,
	extraAttributes?: { [key: string]: string },
): WComponentNode | null {
	if (xmlNode.nodeType === Node.ELEMENT_NODE) {
		const element = xmlNode as Element;
		const attributes: { [key: string]: string } = {};
		for (const attr of element.attributes) {
			attributes[attr.name] = attr.value;
		}
		return {
			tagName: element.tagName,
			id: element.id,
			className: element.className,
			attributes: { ...attributes, ...extraAttributes },
			value: element.textContent,
			children: Array.from(xmlNode.childNodes),
		};
	}
	if (xmlNode.nodeType === Node.TEXT_NODE) {
		return {
			tagName: "",
			id: "",
			className: "",
			attributes: {},
			value: xmlNode.textContent ?? "",
			children: [],
		};
	}
	return null;
}

export interface WComponentNode {
	tagName: string;
	id: string;
	className: string;
	attributes: { [key: string]: string };
	value: string;
	children: ChildNode[];
}
