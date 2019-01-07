package de.mobcom.group3.gotrack.Charts;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import de.mobcom.group3.gotrack.Charts.Formats.XLabelFormat;
import de.mobcom.group3.gotrack.R;

import java.text.DecimalFormat;
import java.util.Arrays;

public class BarChartFragment extends Fragment {
    private static final String PREF_DARK_THEME = "dark_theme";
    private final int UPPER_BOUNDARY_X = 8;
    private final int LOWER_BOUNDARY_X = 0;
    private final int LOWER_BOUNDARY_Y = 0;

    private View view;
    private XYPlot plot;
    private int incrementStepsX = 1;
    private int barWidth = 10;

    private Number[] series1Numbers;
    String title;
    String rangeTitle;
    int color;
    double incrementStepsY;

    double[] values = new double[0];

    public BarChartFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Checks current Theme and uses the correct xml
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PREF_DARK_THEME, false)) {
            view = inflater.inflate(R.layout.fragment_line_chart_dark, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        }

        if (getArguments() != null) {
            // Gets all Arguments from Bundle
            values = getArguments().getDoubleArray("array");
            title = getArguments().getString("title");
            color = getArguments().getInt("color");
            rangeTitle = getArguments().getString("rangeTitle");
            incrementStepsY = getArguments().getDouble("stepsY");

            // Gets Every value from bundle in number Array
            series1Numbers = new Number[values.length];
            for (int i = 0; i < series1Numbers.length; i++) {
                series1Numbers[i] = values[i];
            }

        } else {
            // Setting default vals, if plot is not used with bundle arguments
            title = "Series01";
            rangeTitle="km/h";
            color = Color.GRAY;
            incrementStepsY = 10;
            series1Numbers = new Number[]{0, 1, 2, 3, 4, 5, 6, 7, 0};
        }

        // Getting in xml defined Plot
        plot = view.findViewById(R.id.linePlot);
        plot.setTitle(title);
        plot.setRangeLabel(rangeTitle);

        // Turning Arrays to XYSeries
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);

        // create a bar formatter with a fill color and outline:
        BarFormatter bf = new BarFormatter(color, color);
        // Add Series of Values to Plot (Every Value is one Bar)
        plot.addSeries(series1, bf);

        // Setting Start of X-Axis at 0 and End at 8
        // Range will start at 0
        // Steps of X-Axis are Incrementing as Natural Value from in Step by 1
        // Graph X-Axis Label Style is customed to Weekdays
        plot.setDomainLowerBoundary(LOWER_BOUNDARY_X, BoundaryMode.FIXED);
        plot.setDomainUpperBoundary(UPPER_BOUNDARY_X, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(LOWER_BOUNDARY_Y, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, incrementStepsX);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, incrementStepsY);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new XLabelFormat());

        // If Steps are to small to show in 0.0 Format, it will be set as 0.00 for Range
        if(incrementStepsY < 0.01){
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("0.000"));
        }else if(incrementStepsY < 0.1){
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("0.00"));
        }

        // Bar Width is set by plots own BarRenderer Instance
        BarRenderer renderer = plot.getRenderer(BarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(barWidth));

        return view;
    }


}
