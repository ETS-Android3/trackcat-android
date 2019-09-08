package de.trackcat.Charts.Formats;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class XLabelFormat extends Format {
    /* Keep first and last Value empty for better Visualization */
    /* This creates Domain Labels in Weekday Format for the Weekly Summary */
    private String Monday = "Mo";
    private String Tuesday = "Di";
    private String Wednesday = "Mi";
    private String Thursday = "Do";
    private String Friday = "Fr";
    private String Saturday = "Sa";
    private String Sunday = "So";

    private String[] xLabels;

    private String[] label1 = {"", Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday, ""};
    private String[] label2 = {"", Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday, Monday, ""};
    private String[] label3 = {"", Wednesday, Thursday, Friday, Saturday, Sunday, Monday, Tuesday, ""};
    private String[] label4 = {"", Thursday, Friday, Saturday, Sunday, Monday, Tuesday, Wednesday, ""};
    private String[] label5 = {"", Friday, Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, ""};
    private String[] label6 = {"", Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, ""};
    private String[] label7 = {"", Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, ""};
    private String[] labelDefault = {"", Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, ""};

    public XLabelFormat(int curDay) {
        /* this will set the correct Labelstyle corresponding to the current weekday */
        /* This way, the current weekday will always be displayed last on the graphs */
        switch (curDay) {
            case 1:
                xLabels = label1;
                break;
            case 2:
                xLabels = label2;
                break;
            case 3:
                xLabels = label3;
                break;
            case 4:
                xLabels = label4;
                break;
            case 5:
                xLabels = label5;
                break;
            case 6:
                xLabels = label6;
                break;
            case 7:
                xLabels = label7;
                break;
            default:
                xLabels = labelDefault;
                break;
        }
    }

    @Override
    public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {

        int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
        String labelString = xLabels[parsedInt];
        arg1.append(labelString);
        return arg1;
    }

    @Override
    public Object parseObject(String arg0, ParsePosition arg1) {
        return java.util.Arrays.asList(xLabels).indexOf(arg0);
    }
}
