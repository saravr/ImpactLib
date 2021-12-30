package com.sensorapp.impactlib;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class SinglePlot {
    private Timer timer;
    private Float currentValue = 0f;
    private final LineChart chart;
    private final PlotConfig config;
    private final String label;
    private final int lineColor;

    @SuppressWarnings("unused")
    public SinglePlot(LineChart lineChart, String plotDescription, String plotLabel, int plotLineColor, PlotConfig plotConfig) {
        chart = lineChart;
        config = plotConfig;
        label = plotLabel;
        lineColor = plotLineColor;

        Description description = new Description();
        description.setText(plotDescription);
        chart.setDescription(description);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);

        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @SuppressWarnings("unused")
    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                addEntry(currentValue, label);
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 500L);
    }

    @SuppressWarnings("unused")
    public void stop() {
        timer.cancel();
        timer = null;
    }

    @SuppressWarnings("unused")
    public void addData(Float yAxisValue) {
        currentValue = yAxisValue;
    }

    private void addEntry(Float yAxisValue, String label) {
        LineData data = chart.getData();
        if (data == null) {
            return;
        }

        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet(label);
            data.addDataSet(set);
        }

        Entry entry = new Entry((float) set.getEntryCount(), yAxisValue);
        data.addEntry(entry, 0);

        data.notifyDataChanged();

        chart.notifyDataSetChanged();
        chart.setVisibleXRangeMaximum(config.maximumXRange);
        chart.moveViewToX((float) data.getEntryCount());
    }

    private LineDataSet createSet(String label) {
        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(lineColor);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
}
