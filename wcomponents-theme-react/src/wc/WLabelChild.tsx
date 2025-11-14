import type { WComponentNode } from "../data.ts";
import { WComponentSet } from "../WComponent.tsx";

// TODO: This component does not yet fully implement wc.ui.decoratedlabel.xsl
export default function WLabelChild(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;
	const localName = wcNode.tagName.slice(3);
	console.log("wlabel");

	/*let htmlChild = wcNode.value;
	wcNode.children.forEach((child) => (htmlChild += child.outerHTML));*/
	return (
		<span
			id={wcNode.id}
			className={`wc-${localName} wc_dlbl_seg`} /*dangerouslySetInnerHTML={{ __html: htmlChild }}*/
		>
			{wcNode.value}
			<WComponentSet wcElements={wcNode.children} />
		</span>
	);
}
