import { Suspense, useCallback, useRef, useEffect } from "react";
import { CircularProgress } from "@mui/material";
import { JsonView, collapseAllNested } from "react-json-view-lite";
import { ChromePicker } from "react-color";
import CustomCodeBlock from "@global/CustomCodeBlock";
import { graphviz } from "d3-graphviz";
import { ChartDisplay } from "./ChartDisplay";
import { ClassAttributeFieldDisplay } from "./ClassAttributeFieldDisplay";
import { BNodeNavigatorDisplay } from "./BNodeNavigatorDisplay";

interface ContentDisplayProps {
	viewId: string;
	content: any;
	contentType: string;
	backgroundColor: string;
	jumpToNode: (nodeId: number | string) => void;
	hexColor: string;
	onHexColorChange: (color: { hex: string }) => void;
	prettyName?: string;
	fetchNextPage: () => void;
	hasNextPage: boolean;
	isFetchingNextPage: boolean;
}

export const ContentDisplay = ({
	viewId,
	content,
	contentType,
	backgroundColor,
	jumpToNode,
	hexColor,
	onHexColorChange,
	prettyName,
	fetchNextPage,
	hasNextPage,
	isFetchingNextPage,
}: ContentDisplayProps) => {
	const graphvizRef = useRef<HTMLDivElement>(null);

	const renderGraphviz = useCallback(() => {
		if (contentType === "text/dot" && graphvizRef.current) {
			graphviz(graphvizRef.current, {
				engine: "dot",
				fit: true,
				zoom: true,
			}).renderDot(content);
		}
	}, [content, contentType]);

	useEffect(() => {
		renderGraphviz();
	}, [renderGraphviz]);

	if (!content) {
		return <div className="error-message">No content available.</div>;
	}

	if (contentType === "application/json") {
		if (viewId === "class_attribute_field") {
			return (
				<ClassAttributeFieldDisplay
					content={content}
					fetchNextPage={fetchNextPage}
					hasNextPage={hasNextPage}
					isFetchingNextPage={isFetchingNextPage}
				/>
			);
		} else if (
			viewId === "char_example_xy" ||
			viewId.endsWith("_distribution") ||
			viewId.endsWith("nivo_view")
		) {
			return (
				<div
					className="graph"
					onClick={(e) => {
						e.stopPropagation();
						e.preventDefault();
					}}
				>
					<Suspense fallback={<CircularProgress />}>
						<ChartDisplay
							viewId={viewId}
							content={content}
							onNodeClick={jumpToNode}
							prettyName={prettyName}
						/>
					</Suspense>
				</div>
			);
		} else if (viewId === "bnode_navigator") {
			return (
				<BNodeNavigatorDisplay content={content} jumpToNode={jumpToNode} />
			);
		} else {
			return (
				<div
					className="content-container"
					style={{ background: backgroundColor }}
				>
					<Suspense fallback={<CircularProgress />}>
						<JsonView data={content} shouldExpandNode={collapseAllNested} />
					</Suspense>
				</div>
			);
		}
	} else if (contentType === "text/dot") {
		return (
			<div
				className="content-container graphviz-container"
				style={{ background: backgroundColor }}
			>
				<div ref={graphvizRef} />
			</div>
		);
	} else if (contentType === "text/html") {
		return (
			<div
				className="content-container html-content"
				style={{ background: backgroundColor }}
			>
				<div dangerouslySetInnerHTML={{ __html: content }} />
			</div>
		);
	} else if (contentType === "image/svg") {
		return (
			<div className="content-container" style={{ background: "transparent" }}>
				<div
					dangerouslySetInnerHTML={{ __html: content }}
					style={{ background: "transparent" }}
				/>
			</div>
		);
	} else if (contentType === "image/svg+xml") {
		return (
			<div className="content-container" style={{ background: "transparent" }}>
				<img
					src={`data:image/svg+xml;base64,${content}`}
					alt="Graphviz"
					style={{ background: "transparent" }}
				/>
			</div>
		);
	} else if (contentType === "text/plain") {
		return (
			<div
				className="content-container"
				style={{ background: backgroundColor }}
			>
				<pre>{content}</pre>
			</div>
		);
	} else if (contentType === "image/png" || contentType === "image/jpeg") {
		return (
			<div
				className="content-container"
				style={{ background: backgroundColor }}
			>
				<img src={`data:${contentType};base64,${content}`} alt="Content" />
			</div>
		);
	} else if (contentType === "image/jsondot") {
		return (
			<div
				className="content-container"
				style={{ background: backgroundColor }}
			>
				<Suspense fallback={<CircularProgress />}>
					<JsonView data={content} />
				</Suspense>
			</div>
		);
	} else if (contentType === "text/java") {
		return (
			<div
				className="content-container"
				style={{ background: backgroundColor }}
			>
				<Suspense fallback={<CircularProgress />}>
					<CustomCodeBlock language="java" code={content} />
				</Suspense>
			</div>
		);
	} else if (contentType === "text/hex") {
		return (
			<div
				className="content-container"
				style={{ background: backgroundColor }}
			>
				<ChromePicker
					color={hexColor}
					disableAlpha
					onChangeComplete={onHexColorChange}
				/>
			</div>
		);
	} else {
		return (
			<div className="error-message">
				Unsupported content type: {contentType}
			</div>
		);
	}
};
