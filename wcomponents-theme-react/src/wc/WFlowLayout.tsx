import { WComponentSet } from "../WComponent.tsx";
import { type WComponentNode } from "../data.ts";

export default function WFlowLayout(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let layoutClasses = "wc-flowlayout";
	const gap = wcNode.attributes["gap"];
	const align = wcNode.attributes["align"];
	if (gap) {
		layoutClasses += ` wc-${align === "vertical" ? "v" : "h"}gap-${gap}`;
	}
	if (align) {
		layoutClasses += ` wc-align-${align}`;
	}
	const valign = wcNode.attributes["valign"];
	if (valign) {
		layoutClasses += ` wc_fl_${valign}`;
	}

	return (
		<div className={layoutClasses}>
			{wcNode.children.map((node) => {
				const cell = node as Element;
				return (
					cell.tagName === "ui:cell" && (
						<div className="wc-cell">
							<WComponentSet xmlNodes={Array.from(cell.children)} />
						</div>
					)
				);
			})}
		</div>
	);
}
