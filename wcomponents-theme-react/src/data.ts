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

export async function processCustomRequest(
	currentApp: WComponentNode,
	appParams: { [key: string]: string },
	triggerId: string,
	triggerValue: string,
	ajax?: boolean,
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
	formData.append(triggerId, triggerValue);
	if (ajax) {
		formData.append("wc-ajax", triggerId);
	}

	const urlParams = ajax ? `?${new URLSearchParams(appParams).toString()}` : "";
	const response = await fetch(`/app${urlParams}`, {
		method: "POST",
		headers: {
			"X-wcnoxslt": "wcnoxslt",
		},
		body: formData,
	});

	return handleResponse(response);
}

export async function processTabSetRequest(
	currentApp: WComponentNode,
	appParams: { [key: string]: string },
	tabSetId: string,
	tabId: string,
	tabIndex: number,
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
	formData.append(tabSetId, String(tabIndex));
	formData.append("wc-ajax", tabId);

	const response = await fetch(
		// Trusting that FormData is just strings for now.
		`/app?${new URLSearchParams(formData as unknown as Record<string, string>).toString()}`,
		{
			headers: {
				"X-wcnoxslt": "wcnoxslt",
			},
		},
	);

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
