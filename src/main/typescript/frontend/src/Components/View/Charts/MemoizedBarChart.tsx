import ReactECharts from "echarts-for-react";
import { memo, useMemo } from "react";

interface MemoizedBarChartProps {
	prettyName: string | undefined;
	data: Record<string, any>;
	keys: string[];
}

export const MemoizedBarChart = memo(
	({ prettyName, data, keys }: MemoizedBarChartProps) => {
		const option = useMemo(
			() => ({
				tooltip: {
					confine: true,
					trigger: "axis",
					axisPointer: {
						type: "shadow",
					},
				},
				grid: {
					containLabel: false,
				},
				xAxis: {
					data: keys,
					axisLabel: {
						rotate: 45,
						formatter: (value: string) =>
							value.length > 10 ? `${value.substring(0, 10)}...` : value,
					},
				},
				yAxis: {
					type: "value",
				},
				series: {
					name: prettyName,
					type: "bar",
					data: keys.map((key) => {
						return data[key];
					}),
					emphasis: {
						focus: "series",
						blurScope: "coordinateSystem",
					},
					animationDuration: 300,
				},
			}),
			[data, keys, prettyName],
		);

		return (
			<ReactECharts
				lazyUpdate
				option={option}
				style={{ height: "100%", minHeight: "300px", width: "100%" }}
			/>
		);
	},
);

MemoizedBarChart.displayName = "MemoizedBarChart";
