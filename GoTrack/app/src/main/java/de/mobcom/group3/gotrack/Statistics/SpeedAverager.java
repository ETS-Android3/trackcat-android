package de.mobcom.group3.gotrack.Statistics;

import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Timer;

/*
 * This class calculates the average Speed in M/S via the selected Timer
 * */
public class SpeedAverager {

    final private static int WALKER_MAX_SPEED = 14;
    final private static int BIKE_MAX_SPEED = 40;

    private MainActivity creator;

    private mCounter mCounter;
    private Timer timerClass;
    private double avgSpeed = 0.0; // m/S
    private int type;


    /*
     * Init the Averager with the selected Timer
     * */
    public SpeedAverager(MainActivity creator, mCounter mCounter, Timer timerSet, int typeSet) {
        this.creator = creator;
        timerClass = timerSet;
        this.mCounter = mCounter;
        type = typeSet;
    }

    // calc avg Speed by selected Timer
    public void calcAvgSpeed() {
        double mAmount = mCounter.getAmount();
        double time = timerClass.getTime();

        avgSpeed = (mAmount / time);
    }

    // switch Timer
    public void switchTimer(Timer timerClassSet, int typeSet) {
        timerClass = timerClassSet;
        type = typeSet;
        calcAvgSpeed();
    }

    /* return current Type of Timer */
    public int getType() {
        return type;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    /* Liefert den entsprechenden Typene einer Durchschnittsgeschwindigkeit */
    public static int getRouteType(double avg) {
        int type;
        /* Läufer */
        if (avg * 60 * 60 / 1000 < WALKER_MAX_SPEED) {
            type = 0;
        }
        /* Fahrrad */
        else if (avg * 60 * 60 / 1000 < BIKE_MAX_SPEED) {
            type = 1;
        }
        /* Auto */
        else {
            type = 2;
        }
        return type;
    }

    /* Liefert die entsprechende Icon ID zu einem Typen */
    public static int getTypeIcon(int type, boolean list) {
        int drawableInt = 0;
        if (list) {
            switch (type) {
                case 0:
                    drawableInt = R.drawable.activity_running_record_list;
                    break;
                case 1:
                    drawableInt = R.drawable.activity_biking_record_list;
                    break;
                case 2:
                    drawableInt = R.drawable.activity_caring_record_list;
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    drawableInt = R.drawable.activity_running_record;
                    break;
                case 1:
                    drawableInt = R.drawable.activity_biking_record;
                    break;
                case 2:
                    drawableInt = R.drawable.activity_caring_record;
                    break;
            }
        }
        return drawableInt;
    }
}
