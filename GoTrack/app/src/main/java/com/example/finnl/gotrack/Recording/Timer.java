package com.example.finnl.gotrack.Recording;

import android.os.Message;
import android.widget.TextView;

import com.example.finnl.gotrack.MainActivity;

import java.util.TimerTask;

/*
 * Timer counts the secsonds.
 * */
public class Timer {
    private double time = 0;

    private java.util.Timer timer;
    int type;
    private boolean isRunning;


    /*
     * Define Timer to add up Time and create String in HH:MM:SS format
     * */
    private class RideTimerTask extends TimerTask {

        @Override
        public void run() {
            // add up Time
            time += 1;

            // create readable String
            int hours = (int) Math.floor((time / 60) / 60);

            double timeCalc = time - hours * 60 * 60;

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

            // send message to View
            Message msg = new Message();
            msg.what = type;
            msg.obj = hours + ":" + minutesStr + ":" + seconds;
            RecordFragment.handler.sendMessage(msg);
        }
    }


    /*
     * create new Instance and set type
     * 0 = Total Timer
     * 1 = Timer counts while speed > 0
     * */
    public Timer(int typeSet) {
        type = typeSet;

        if (type != 1) {
            startTimer();
        }
    }

    // return counted Time
    public double getTime() {
        return time;
    }


    /*
     * stops Timer
     * */
    public void stopTimer() {
        if (isRunning) {
            timer.cancel();
            timer = null;
            isRunning = false;
        }
    }

    /*
     * starts Timer
     * */
    public void startTimer() {
        if (!isRunning) {
            /* initialise Timer */
            timer = new java.util.Timer();
            /* start Timer on 1 sec */
            timer.scheduleAtFixedRate(new RideTimerTask(), 1000, 1000);
            isRunning = true;
        }
    }

    // return state of Timer
    public boolean getActive() {
        return isRunning;
    }
}
