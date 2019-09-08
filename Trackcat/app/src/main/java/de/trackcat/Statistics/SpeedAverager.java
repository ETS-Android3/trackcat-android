package de.trackcat.Statistics;

import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Recording.Timer;

/* This class calculates the average Speed in M/S via the selected Timer */
public class SpeedAverager {

    final private static int WALKER_MAX_SPEED = 14;
    final private static int BIKE_MAX_SPEED = 40;

    private MainActivity creator;

    private mCounter mCounter;
    private Timer timerClass;
    private double avgSpeed = 0.0; // m/S
    private int type;


    /* Init the Averager with the selected Timer */
    public SpeedAverager(MainActivity creator, mCounter mCounter, Timer timerSet, int typeSet) {
        this.creator = creator;
        timerClass = timerSet;
        this.mCounter = mCounter;
        type = typeSet;
    }

    /* Calc avg Speed by selected Timer */
    public void calcAvgSpeed() {
        double mAmount = mCounter.getAmount();
        double time = timerClass.getTime();

        avgSpeed = (mAmount / time);
    }

    /* Switch Timer */
    public void switchTimer(Timer timerClassSet, int typeSet) {
        timerClass = timerClassSet;
        type = typeSet;
        calcAvgSpeed();
    }

    /* Return current Type of Timer */
    public int getType() {
        return type;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    /* Return correct type to averagespeed */
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

    /* Return icon-id to type */
    public static int getTypeIcon(int type) {
        int drawableInt = 0;

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

        return drawableInt;
    }
}
