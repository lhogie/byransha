import { Box, Button } from "@mui/material";
import { useCallback, useRef } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";

interface BNodeNavigatorDisplayProps {
  content: any;
  jumpToNode: (nodeId: number | string) => void;
}

export const BNodeNavigatorDisplay = ({
  content,
  jumpToNode,
}: BNodeNavigatorDisplayProps) => {
  const handleButtonClick = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>, nodeId: number | string) => {
      event.stopPropagation();
      event.preventDefault();
      jumpToNode(nodeId);
    },
    [jumpToNode],
  );

  const inNodes = Object.keys(content.ins || {});
  const outNodes = Object.keys(content.outs || {});
  const allNodes = [
    ...inNodes.map((node) => ({
      key: node,
      id: content.ins[node],
      type: "in" as const,
    })),
    ...outNodes.map((node) => ({
      key: node,
      id: content.outs[node],
      type: "out" as const,
    })),
  ];

  if (allNodes.length <= 50) {
    return (
      <Box sx={{ p: 1 }}>
        <Box sx={{ mb: 2 }}>
          {inNodes.map((inNode) => (
            <Button
              key={inNode}
              onClick={(e) => handleButtonClick(e, content.ins[inNode])}
              variant="contained"
              sx={{
                bgcolor: "#3949ab",
                color: "#fff",
                mr: 1,
                mb: 1,
                "&:hover": { bgcolor: "#5c6bc0" },
              }}
            >
              {inNode} ({content.ins[inNode]})
            </Button>
          ))}
        </Box>
        <Box sx={{ paddingY: "10px" }}>
          {outNodes.map((outNode) => (
            <Button
              key={outNode}
              onClick={(e) => handleButtonClick(e, content.outs[outNode])}
              variant="contained"
              sx={{
                bgcolor: "#00897b",
                color: "#fff",
                mr: 1,
                mb: 1,
                "&:hover": { bgcolor: "#26a69a" },
              }}
            >
              {outNode} ({content.outs[outNode]})
            </Button>
          ))}
        </Box>
      </Box>
    );
  }

  const parentRef = useRef<HTMLDivElement>(null);
  const rowVirtualizer = useVirtualizer({
    count: allNodes.length,
    estimateSize: () => 56,
    getScrollElement: () => parentRef.current,
    overscan: 10,
  });

  return (
    <Box
      ref={parentRef}
      sx={{
        height: 400,
        overflowY: "auto",
        position: "relative",
        border: "1px solid #e0e0e0",
        borderRadius: 1,
        p: 1,
      }}
    >
      <Box
        sx={{
          height: `${rowVirtualizer.getTotalSize()}px`,
          position: "relative",
          width: "100%",
        }}
      >
        {rowVirtualizer.getVirtualItems().map((virtualItem) => {
          const node = allNodes[virtualItem.index];
          if (!node) return null;

          const isInNode = node.type === "in";

          return (
            <Box
              key={virtualItem.key}
              sx={{
                position: "absolute",
                top: 0,
                left: 0,
                width: "100%",
                height: `${virtualItem.size}px`,
                transform: `translateY(${virtualItem.start}px)`,
                display: "flex",
                alignItems: "center",
                px: 1,
              }}
            >
              <Button
                onClick={(e) => handleButtonClick(e, node.id)}
                variant="contained"
                sx={{
                  bgcolor: isInNode ? "#3949ab" : "#00897b",
                  color: "#fff",
                  "&:hover": {
                    bgcolor: isInNode ? "#5c6bc0" : "#26a69a",
                  },
                }}
              >
                {node.key} ({node.id})
              </Button>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
};
