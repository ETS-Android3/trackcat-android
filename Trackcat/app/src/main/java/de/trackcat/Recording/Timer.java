package de.trackcat.Recording;

import android.os.Message;

import java.util.TimerTask;

/* Timer counts the seconds. */
public class Timer {
    private long time = 0;

    private java.util.Timer timer;
    private int type;
    private boolean isRunning;

    Timer() {
    }

    /* Define Timer to add up Time and create String in HH:MM:SS format */
    private class RideTimerTask extends TimerTask {

        @Override
        public void run() {
            time += 1;
            sendTime();
        }
    }

    /* Create and Send readable Message String  */
    void sendTime() {

        /* Send message to View */
        Message msg = new Message();
        msg.what = type;
        msg.obj = secToString(time);
        RecordFragment.handler.sendMessage(msg);
    }

    /* Build Readable String from Seconds */
    String secToString(double secs) {

        /* create readable String */
        int hours = (int) Math.floor((secs / 60) / 60);

        double timeCalc = secs - hours * 60 * 60;

        int minutes = (int) Math.floor(timeCalc / 60);
        String seconds = (timeCalc - minutes * 60) / 100 + "";
        if (seconds.length() < 4) {
            seconds += 0;
        }

        seconds = seconds.substring(2);

        String minutesStr = minutes + "";
        if (minutesStr.length() < 2) {
            minutesStr = 0 + minutesStr;
        }

        return hours + ":" + minutesStr + ":" + seconds;
    }

    /*
     * create new Instance and set type
     * 0 = Total Timer
     * 1 = Timer counts while speed > 0
     * */
    Timer(int typeSet) {
        type = typeSet;

        if (type != 1) {
            startTimer();
        }
    }

    /* Return counted Time */
    public long getTime() {
        return time;
    }

    /* Stops Timer */
    void stopTimer() {
        if (isRunning) {
            timer.cancel();
            timer = null;
            isRunning = false;
        }
    }

    /* Starts Timer */
    void startTimer() {
        if (!isRunning) {
            /* Initialise Timer */
            timer = new java.util.Timer();
            /* Start Timer on 1 sec */
            timer.scheduleAtFixedRate(new RideTimerTask(), 1000, 1000);
            isRunning = true;
        }
    }

    /* Return state of Timer */
    boolean getActive() {
        return isRunning;
    }
}
