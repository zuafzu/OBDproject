package com.cy.obdproject.tools;

import android.content.Context;
import android.util.Log;

import com.cy.obdproject.view.LineChartMarkView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by caoyingfu on 2017/5/12.
 */

public class ChartUtils {

    public static int time = 0;
    private final static int maxXCount = 3;

    // 设置显示的样式(折线图)右上
    public static void showLineChart(Context context, final ArrayList<String> labels, LineChart lineChart, final String unit) {
        try {
            if (labels.size() > 0) {
                // 是否显示表格颜色
                lineChart.setDrawGridBackground(false);
                // x轴位置
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(context.getResources().getColor(android.R.color.black));
                xAxis.setAvoidFirstLastClipping(true);
                xAxis.setTextSize(10);
                xAxis.setAxisMinimum(0f);
                xAxis.setGranularity(1f);
                xAxis.setLabelRotationAngle(0f);//设置x轴字体显示角度
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if ((value == 0 || (value >= 0 && value % 1 == 0)) && labels.size() > value) {
                            return labels.get((int) value);
                        } else {
                            return "";
                        }
                    }

                });
                xAxis.setLabelCount(maxXCount, false);
                // y轴位置
                YAxis yl = lineChart.getAxisRight();
                yl.setDrawLabels(false);
                yl.setDrawGridLines(true);
                yl.setDrawAxisLine(false);
                //保证Y轴从0开始，不然会上移一点
                yl.setAxisMinimum(0f);
                YAxis yr = lineChart.getAxisLeft();
                yr.setAxisMinimum(0f);
                yr.setStartAtZero(false);
                yr.setDrawGridLines(false);
                yr.setDrawLabels(true);
                yr.setTextColor(context.getResources().getColor(android.R.color.black));
                yr.setTextSize(10);
                yr.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        String n = "" + value;
                        String s = "";
                        if (n.contains(".")) {
                            String[] m = n.split("\\.");
                            if (m[1].length() > 2) {
                                n = m[0] + "." + m[1].substring(0, 2);
                            }
                            if (m[1].length() < 2) {
                                n = m[0] + "." + m[1].substring(0, 1) + "0";
                            }
                            s = n + unit;
                        } else {
                            s = n + ".00" + unit;
                        }
                        return s;
                    }

                });
                // 下方标注
                Legend legend = lineChart.getLegend();
                legend.setTextColor(context.getResources().getColor(android.R.color.black));
                legend.setTextSize(10);
                legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
                legend.setWordWrapEnabled(true);
                legend.setEnabled(true);
                // 显示
                lineChart.setExtraBottomOffset(1);// 底部数据显示不完整 遮拦了
                Description description = new Description();
                description.setText("");
                lineChart.setDescription(description);
                lineChart.animateXY(time, time);

                LineChartMarkView mv = new LineChartMarkView(context, unit, xAxis.getValueFormatter());
                mv.setChartView(lineChart);
                lineChart.setMarker(mv);

                lineChart.fitScreen();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            } else {
                // x轴位置
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if ((value == 0 || (value >= 0 && value % 1 == 0)) && labels.size() > value) {
                            return labels.get((int) value);
                        } else {
                            return "";
                        }
                    }

                });
                lineChart.notifyDataSetChanged();
            }
            // 当前统计图表中最多在x轴坐标线上显示的总量
            lineChart.setVisibleXRangeMaximum(maxXCount);
            // 将坐标移动到最新
            // 此代码将刷新图表的绘图
            lineChart.moveViewToX(labels.size() - maxXCount);

        } catch (Exception e) {
            Log.e("cyf123", "showLineChart err : " + e.getMessage());
        }
    }

}

