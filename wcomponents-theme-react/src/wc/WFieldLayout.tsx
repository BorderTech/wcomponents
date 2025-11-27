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

export default function WFieldLayout(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let marginClasses = "";
	wcNode.children.forEach((node) => {
		const child = node as Element;
		child.tagName === "ui:margin" && (marginClasses += getClassesFromMarginTag(child));
	});

	const className = wcNode.className ? " " + wcNode.className : "";
	const labelWidth = wcNode.attributes["labelWidth"] ? ` wc_fld_lblwth_${wcNode.attributes["labelWidth"]}` : "";
	const ordered = wcNode.attributes["ordered"] ? " wc_ordered" : "";
	const layout = wcNode.attributes["layout"] ? ` wc-layout-${wcNode.attributes["layout"]}` : "";

	return (
		<div
			id={wcNode.id}
			role="presentation"
			className={`wc-fieldlayout${className}${marginClasses}${labelWidth}${ordered}${layout}`}
		>
			<WComponentSet xmlNodes={wcNode.children} />
		</div>
	);
}
