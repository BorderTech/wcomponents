import { WComponentSet } from "../WComponent.tsx";
import type { WComponentNode } from "../data.ts";
import { getClassesFromMarginTag } from "../utils.ts";

// Implements wc.ui.section.xsl.
// TODO: Verify all section functionality is implemented.
export default function WSection(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let marginClasses = "";
	wcNode.children.forEach((node) => {
		const child = node as Element;
		child.tagName === "ui:margin" && (marginClasses += getClassesFromMarginTag(child));
	});

	return (
		<section
			id={wcNode.id}
			className={`wc-section${wcNode.className ? " " + wcNode.className : ""}${marginClasses}`}
		>
			<WComponentSet xmlNodes={wcNode.children} />
		</section>
	);
}
