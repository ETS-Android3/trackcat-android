package de.mobcom.group3.gotrack.Charts;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mobcom.group3.gotrack.R;

import java.util.Arrays;

public class LineChartFragment extends Fragment {
    private static final String PREF_DARK_THEME = "dark_theme";
    private final int LOWER_BOUNDARY_X = 0;
    private final int LOWER_BOUNDARY_Y = 0;
    private View view;
    private XYPlot plot;
    private int pointPerSegment = 10;
    private int incrementStepsX = 1;
    private int incrementStepsY = 10;
    private Number[] series1Numbers;

    public LineChartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Variablen */
        String title = "Series1";
        LineAndPointFormatter series1Format;

        /* bei übergebenen Argumenten diese Anzeigen */
        if (getArguments() != null) {

            /* Werte aus Bundle auslesen */
            title = getArguments().getString("title");
            String rangeTitle = getArguments().getString("rangeTitle");
            double[] values = getArguments().getDoubleArray("array");

            /* nach Theme Design wählen */
            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PREF_DARK_THEME, false)) {
                view = inflater.inflate(R.layout.fragment_line_chart_dark, container, false);
                series1Format =
                        new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels_dark);
            } else {
                view = inflater.inflate(R.layout.fragment_line_chart, container, false);
                series1Format =
                        new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);
            }

            /* Setzen der Werte zum Füllen des Plots */
            series1Numbers = new Number[values.length];
            int maxValue = 0;
            for (int i = 0; i < series1Numbers.length; i++) {
                series1Numbers[i] = (int) Math.round(values[i]);
                if ((int) Math.round(values[i]) > maxValue) {
                    maxValue = (int) Math.round(values[i]);
                }
            }

            pointPerSegment = series1Numbers.length;
            incrementStepsY = maxValue / 5;
            incrementStepsX = series1Numbers.length / 5;

            /* Werte dem Plot hinzufügen */
            XYSeries series1 = new SimpleXYSeries(
                    Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);

            /* Kurven runden */
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(pointPerSegment, CatmullRomInterpolator.Type.Centripetal));
            series1Format.setPointLabelFormatter(null);

            /* Titel und Werte dem Plot hinzufügen */
            plot = view.findViewById(R.id.linePlot);
            plot.setTitle(title);
            plot.setRangeLabel(rangeTitle);
            plot.addSeries(series1, series1Format);

        } else {

            /* Defaul Plot, bei keinen übergebenen Werten*/
            series1Numbers = new Number[]{1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
            XYSeries series1 = new SimpleXYSeries(
                    Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);
            series1Format =
                    new LineAndPointFormatter(getActivity(), R.xml.line_and_point_formatter_with_labels);
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(pointPerSegment, CatmullRomInterpolator.Type.Centripetal));
            series1Format.setPointLabelFormatter(null);
            plot = view.findViewById(R.id.linePlot);
            plot.setTitle(title);
            plot.addSeries(series1, series1Format);
        }

        /* Schrittweite der Achsen etc. setzen */
        plot.setDomainLowerBoundary(LOWER_BOUNDARY_X, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(LOWER_BOUNDARY_Y, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, incrementStepsX);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, incrementStepsY);

        return view;
    }
}
