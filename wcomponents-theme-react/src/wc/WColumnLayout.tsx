import { WComponentSet } from "../WComponent.tsx";
import { type WComponentNode } from "../data.ts";

interface ColumnMeta {
	width: string | null;
	align: string;
}

function getColumnMetaFromColumnTag(columnElement: Element): ColumnMeta {
	return {
		width: columnElement.getAttribute("width"),
		align: columnElement.getAttribute("align") ?? "left",
	};
}

function getChunkedArray(array: Element[], chunkSize: number): Element[][] {
	const chunkedArray: Element[][] = [];
	if (chunkSize > 0) {
		let index = 0;
		while (index < array.length) {
			chunkedArray.push(array.slice(index, index + chunkSize));
			index += chunkSize;
		}
	}
	return chunkedArray;
}

function getCellClassName(columnMeta: ColumnMeta): string {
	return `wc-cell wc-column wc-align-${columnMeta.align}${columnMeta.width ? " wc_col_" + columnMeta.width : ""}`;
}

export default function WColumnLayout(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	let layoutClasses = "wc-columnlayout";
	const vGap = wcNode.attributes["vgap"];
	if (vGap) {
		layoutClasses += ` wc-vgap-${vGap}`;
	}
	const align = wcNode.attributes["align"];
	if (align) {
		layoutClasses += ` wc-align-${align}`;
	}

	let rowClasses = "wc-row";
	const hGap = wcNode.attributes["hgap"];
	if (hGap) {
		rowClasses += ` wc-hgap-${hGap}`;
	}
	const panelClass = wcNode.attributes["panelClass"];
	if (panelClass.includes("wc-respond")) {
		rowClasses += " wc-respond";
	}

	const columnMeta: ColumnMeta[] = [];
	const cells: Element[] = [];
	wcNode.children.forEach((child) => {
		if (child.tagName === "ui:column") {
			columnMeta.push(getColumnMetaFromColumnTag(child));
		} else if (child.tagName === "ui:cell") {
			cells.push(child);
		}
	});

	const rows: Element[][] = getChunkedArray(cells, columnMeta.length);

	return (
		<div className={layoutClasses}>
			{rows.map((row) => (
				<div className={rowClasses}>
					{row.map((cell, i) => (
						<div className={getCellClassName(columnMeta[i])}>
							<WComponentSet wcElements={Array.from(cell.children)} />
						</div>
					))}
				</div>
			))}
		</div>
	);
}
