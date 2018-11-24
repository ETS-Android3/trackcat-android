package de.mobcom.group3.gotrack.Charts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Arrays;

public class BarChartFragment extends Fragment {
    private final int UPPER_BOUNDARY_X = 8;
    private final int LOWER_BOUNDARY_X = 0;
    private final int LOWER_BOUNDARY_Y = 0;

    private View view;
    private XYPlot plot;
    private int incrementStepsX = 1;
    private int incrementStepsY = 1;
    private int barWidth = 10;

    public BarChartFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        // Getting in xml defined Plot
        plot = view.findViewById(R.id.barPlot);

        // Arrays for the Plot
        // Keep first and last Value 0 for better Visualization
        Number[] series1Numbers = {0, 1, 2, 3, 4, 5, 6, 7, 0};

        // Turning Arrays to XYSeries
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        // create a bar formatter with a red fill color and a white outline:
        BarFormatter bf = new BarFormatter(Color.RED, Color.WHITE);
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
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL,incrementStepsY);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new XLabelFormat());

        // Bar Width is set by plots own BarRenderer Instance
        BarRenderer renderer = plot.getRenderer(BarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(barWidth));

        return view;
    }


}
