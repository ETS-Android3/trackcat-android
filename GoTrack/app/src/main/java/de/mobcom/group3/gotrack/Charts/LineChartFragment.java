package de.mobcom.group3.gotrack.Charts;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import de.mobcom.group3.gotrack.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;


public class LineChartFragment extends Fragment {
    private final int LOWER_BOUNDARY_X = 0;
    private final int LOWER_BOUNDARY_Y = 0;
    private View view;
    private XYPlot plot;
    private int pointPerSegment = 10;
    private int incrementStepsX = 1;
    private int incrementStepsY = 10;


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
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};

        // Turning Arrays to XYSeries
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        // Create Formatters with xml defined Format
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);


        // Smoothing curves
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(pointPerSegment, CatmullRomInterpolator.Type.Centripetal));
        series1Format.setPointLabelFormatter(null);

        // Add a new Series to the XYPlot
        plot.addSeries(series1, series1Format);


        // Setting Start of X-Axis at 0 and End at 8
        // Range will start at 0
        // Steps of X-Axis are Incrementing as Natural Value from in Step by 1
        // Graph X-Axis Label Style is customed to Weekdays
        plot.setDomainLowerBoundary(LOWER_BOUNDARY_X, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(LOWER_BOUNDARY_Y, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, incrementStepsX);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, incrementStepsY);

        return view;
    }

}
