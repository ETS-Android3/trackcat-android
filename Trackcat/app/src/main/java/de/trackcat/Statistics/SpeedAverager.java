package de.trackcat.Statistics;

import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Recording.Timer;

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

        /* Icons in Listenansicht an Theme angepasst */
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
        }
        /* Icons an jeder anderen genutzen Stelle */
        else {
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