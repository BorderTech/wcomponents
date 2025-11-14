import { WComponentSet } from "../WComponent.tsx";
import type { WComponentNode } from "../data.ts";

function getClassesFromMarginTag(marginElement: Element): string {
	let value = marginElement.getAttribute("all");
	if (value) {
		return ` wc-margin-all-${value}`;
	} else {
		let className = "";
		value = marginElement.getAttribute("north");
		if (value) {
			className += ` wc-margin-north-${value}`;
		}
		value = marginElement.getAttribute("east");
		if (value) {
			className += ` wc-margin-east-${value}`;
		}
		value = marginElement.getAttribute("south");
		if (value) {
			className += ` wc-margin-south-${value}`;
		}
		value = marginElement.getAttribute("west");
		if (value) {
			className += ` wc-margin-west-${value}`;
		}
		return className;
	}
}

export default function WSection(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let marginClasses = "";
	wcNode.children.forEach(
		(child) => child.tagName === "ui:margin" && (marginClasses += getClassesFromMarginTag(child)),
	);

	return (
		<section
			id={wcNode.id}
			className={`wc-section${wcNode.className ? " " + wcNode.className : ""}${marginClasses}`}
		>
			<WComponentSet wcElements={wcNode.children} />
		</section>
	);
}
