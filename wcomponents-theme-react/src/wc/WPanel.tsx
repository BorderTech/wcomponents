import { WComponentSet } from "../WComponent.tsx";
import type { WComponentNode } from "../data.ts";
import { getClassesFromMarginTag } from "../utils.ts";

// Implementation of ui:panel.
export default function WPanel(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let marginClasses = "";
	wcNode.children.forEach((node) => {
		const child = node as Element;
		child.tagName === "ui:margin" && (marginClasses += getClassesFromMarginTag(child));
	});

	const panelType = wcNode.attributes["type"];
	switch (panelType) {
		case "chrome":
		case "action":
			const title = wcNode.attributes["title"];
			return (
				<section id={wcNode.id} className={`wc-panel wc-panel-type-${panelType}${marginClasses}`}>
					{title && <h1>{title}</h1>}
					<WComponentSet xmlNodes={wcNode.children} />
				</section>
			);
		case "header":
			return (
				<header id={wcNode.id} className={`wc-panel wc-panel-type-header${marginClasses}`} role="banner">
					<WComponentSet xmlNodes={wcNode.children} />
				</header>
			);
		case "footer":
			return (
				<footer id={wcNode.id} className={`wc-panel wc-panel-type-footer${marginClasses}`}>
					<WComponentSet xmlNodes={wcNode.children} />
				</footer>
			);
		default:
			return (
				<div id={wcNode.id} className={`wc-panel ${wcNode.className}${marginClasses}`}>
					<WComponentSet xmlNodes={wcNode.children} extraAttributes={{ panelClass: wcNode.className }} />
				</div>
			);
	}
}
