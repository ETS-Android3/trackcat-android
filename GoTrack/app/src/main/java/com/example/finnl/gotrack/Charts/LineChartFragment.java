package com.example.finnl.gotrack.Charts;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import com.example.finnl.gotrack.R;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;


public class LineChartFragment extends Fragment {
    private View view;
    private XYPlot plot;

    public LineChartFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        // Getting in xml defined Plot
        plot = view.findViewById(R.id.linePlot);

        // Arrays for the Plot
        final Number[] domainLabels = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 14};
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

        // Turning Arrays to XYSeries
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Create Formatters
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);
        LineAndPointFormatter series2Format =
                new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels2);

        // Add an "Dash" effect to Series2
        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));

        // Smoothing curves
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // Add a new Series to the XYPlot
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos){
                    int i = Math.round(((Number)obj).floatValue());
                    return toAppendTo.append(domainLabels[i]);
                }
                @Override
                public Object parseObject(String source, ParsePosition pos){
                    return null;
                }
            });

        return view;
    }

}
