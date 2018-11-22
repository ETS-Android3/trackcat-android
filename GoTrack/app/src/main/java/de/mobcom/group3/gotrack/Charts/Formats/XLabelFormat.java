package de.mobcom.group3.gotrack.Charts.Formats;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class XLabelFormat extends Format {
    // Keep first and last Value empty for better Visualization
    final String[] xLabels = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", ""};
    @Override
    public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
        // TODO Auto-generated method stub

        int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
        String labelString = xLabels[parsedInt];
        arg1.append(labelString);
        return arg1;
    }

    @Override
    public Object parseObject(String arg0, ParsePosition arg1) {
        // TODO Auto-generated method stub
        return java.util.Arrays.asList(xLabels).indexOf(arg0);
    }

}
