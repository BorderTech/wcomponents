export async function getClientLayout(): Promise<WComponentNode> {
	const response = await fetch("/app", {
		headers: {
			"X-wcnoxslt": "wcnoxslt",
		},
	});
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

export function getWComponentNodeFromElement(
	element: Element,
	extraAttributes?: { [key: string]: string },
): WComponentNode {
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
		children: Array.from(element.children),
	};
}

export interface WComponentNode {
	tagName: string;
	id: string;
	className: string;
	attributes: { [key: string]: string };
	value: string;
	children: Element[];
}
