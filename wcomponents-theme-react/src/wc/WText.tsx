import type { WComponentNode } from "../data.ts";

function getTextElementTagFromType(type: string): "strong" | "em" | "ins" | "del" | "span" {
	if (type === "emphasised" || type === "highPriority") {
		return "strong";
	}
	if (type === "mediumPriority") {
		return "em";
	}
	if (type === "insert") {
		return "ins";
	}
	if (type === "delete") {
		return "del";
	}
	return "span";
}

// TODO: This component is a stub and does not yet fully implement wc.ui.text.xsl.
export default function WText(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const className = `wc-text wc-text-type-${wcNode.attributes["type"] ?? "plain"} ${wcNode.className}`;

	const space = wcNode.attributes["space"];
	if (space === "paragraphs") {
		return (
			<div className={className}>
				<p>{wcNode.value}</p>
			</div>
		);
	}
	if (space) {
		return <pre className={className}>{wcNode.value}</pre>;
	}

	const TagName = getTextElementTagFromType(wcNode.attributes["type"]);
	return <TagName className={className}>{wcNode.value}</TagName>;
}
