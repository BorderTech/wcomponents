import { WComponentSet } from "../WComponent.tsx";
import { type WComponentNode } from "../data.ts";

export default function WListLayout(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let layoutClasses = "wc-listlayout";

	const gap = wcNode.attributes["gap"];
	const type = wcNode.attributes["type"];
	if (gap) {
		layoutClasses += ` wc-${type === "flat" ? "h" : "v"}gap-${gap}`;
	}

	const align = wcNode.attributes["align"];
	layoutClasses += ` wc-align-${align ?? "left"}`;

	if (type) {
		layoutClasses += ` wc-listlayout-type-${type}`;
	}

	const ordered = wcNode.attributes["ordered"];
	const separator = wcNode.attributes["separator"];
	if (!separator || separator === "none") {
		layoutClasses += " wc_list_nb";
	} else if (!ordered) {
		layoutClasses += ` wc-listlayout-separator-${separator}`;
	}

	const ListType = ordered ? "ol" : "ul";

	return (
		<ListType className={layoutClasses}>
			{wcNode.children.map((node, i) => {
				const cell = node as Element;
				return (
					cell.tagName === "ui:cell" && (
						<li key={i}>
							<WComponentSet xmlNodes={Array.from(cell.children)} />
						</li>
					)
				);
			})}
		</ListType>
	);
}
