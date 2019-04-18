package de.trackcat.Charts;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.trackcat.MainActivity;
import de.trackcat.R;

import java.util.Arrays;

public class LineChartFragment extends Fragment {
    private final int LOWER_BOUNDARY_Y = 0;
    private View view;
    private XYPlot plot;
    private int pointPerSegment = 10;
    private double incrementStepsY = 10;
    private Number[] series1Numbers;
    private double[] values;
    private String rangeTitle;
    private String title;
    private LineAndPointFormatter series1Format;
    ;

    public LineChartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Checks current Theme and uses the correct xml and formatter */
        if (MainActivity.getDarkTheme()) {
            view = inflater.inflate(R.layout.fragment_line_chart_dark, container, false);
            series1Format =
                    new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels_dark);
        } else {
            view = inflater.inflate(R.layout.fragment_line_chart, container, false);
            series1Format =
                    new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);
        }

        if (getArguments() != null) {
            /* Gets all Arguments from Bundle */
            title = getArguments().getString("title");
            rangeTitle = getArguments().getString("rangeTitle");
            values = getArguments().getDoubleArray("array");
        } else {
            /* Setting default vals, if plot is not used with bundle arguments */
            title = "Series1";
            rangeTitle = "Range";
            values = new double[]{1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        }

        /* Sets every value as Value in number Array */
        series1Numbers = new Number[values.length];
        double maxValue = 0;
        for (int i = 0; i < series1Numbers.length; i++) {
            series1Numbers[i] = values[i];
            if (maxValue < values[i]) {
                maxValue = values[i];
            }
        }
        String description=title;
        if(title=="Höhenmeter"){
            description= title+" (WGS-84)";
        }

        /* Incrementing Steps are created dynamically */
        incrementStepsY = maxValue / 5;

        /* Turning Arrays to XYSeries */
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, description);

        /* Smoothing curves */
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(pointPerSegment, CatmullRomInterpolator.Type.Centripetal));
        series1Format.setPointLabelFormatter(null);

        /* Getting in xml defined Plot */
        plot = view.findViewById(R.id.linePlot);
        plot.setTitle(title);
        plot.setRangeLabel(rangeTitle);

        /* Add a new Series to the XYPlot */
        plot.addSeries(series1, series1Format);

        /* Lower Boundaries are set to 0 as defined in final Variables */
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.TRANSPARENT);
        plot.setRangeLowerBoundary(LOWER_BOUNDARY_Y, BoundaryMode.FIXED);
        plot.setRangeUpperBoundary(maxValue, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, incrementStepsY);


        return view;
    }
}
