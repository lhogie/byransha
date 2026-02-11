import ReactECharts from "echarts-for-react";
import { memo, useMemo } from "react";

interface MemoizedLineChartProps {
	data: Array<{
		id: string;
		data: Array<{ x: number; y: number }>;
	}>;
}

export const MemoizedLineChart = memo(({ data }: MemoizedLineChartProps) => {
	const option = useMemo(
		() => ({
			tooltip: {
				trigger: "axis",
				confine: true,
			},
			color: [
				"#5470c6",
				"#91cc75",
				"#fac858",
				"#ee6666",
				"#73c0de",
				"#3ba272",
				"#fc8452",
				"#9a60b4",
				"#ea7ccc",
			],
			legend: {
				data: data.map((series: any) => series.id),
				orient: "vertical",
				right: 10,
				top: "center",
			},
			grid: {
				left: "5%",
				right: "15%",
				bottom: "10%",
				top: "10%",
				containLabel: true,
			},
			xAxis: {
				type: "value",
				name: "X Axis",
				nameLocation: "middle",
				nameGap: 30,
			},
			yAxis: {
				type: "value",
				name: "Y Axis",
				min: -1,
				max: 1,
				nameLocation: "middle",
				nameGap: 50,
			},
			series: data.map((series: any) => ({
				name: series.id,
				type: "line",
				data: series.data.map((point: any) => [point.x, point.y]),
				showSymbol: false,
				symbolSize: 8,
				emphasis: {
					focus: "series",
				},
				animationDuration: 300,
			})),
		}),
		[data],
	);

	return (
		<ReactECharts
			lazyUpdate
			option={option}
			style={{ height: "100%", minHeight: "300px", width: "100%" }}
		/>
	);
});

MemoizedLineChart.displayName = "MemoizedLineChart";
