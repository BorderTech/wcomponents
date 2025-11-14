import type { WComponentNode } from "../data.ts";

export default function WHeading(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	const HeadingTag = `h${wcNode.attributes["level"]}` as "h1" | "h2" | "h3" | "h4" | "h5" | "h6";

	return (
		<HeadingTag id={wcNode.id} className="wc-heading">
			{wcNode.value}
		</HeadingTag>
	);
}
