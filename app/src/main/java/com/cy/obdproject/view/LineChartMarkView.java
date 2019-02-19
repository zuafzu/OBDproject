package com.cy.obdproject.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class LineChartMarkView extends MarkerView {

    private TextView tv_time;
    private TextView tvValue;
    private IAxisValueFormatter xAxisValueFormatter;
    private String unit;

    public LineChartMarkView(Context context, String unit, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.layout_markview);
        this.xAxisValueFormatter = xAxisValueFormatter;
        this.unit = unit;

        tv_time = findViewById(R.id.tv_time);
        tvValue = findViewById(R.id.tv_value);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容
        tv_time.setText("时间：" + xAxisValueFormatter.getFormattedValue(e.getX(), null));
        tvValue.setText("数据：" + e.getY() + unit);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
