package com.example.finnl.gotrack;

import android.os.Message;
import android.widget.TextView;

import java.util.TimerTask;

public class Timer {
    private double time = 0;

    private MainActivity creator;
    private java.util.Timer timer;
    int type;
    private boolean isRunning;


    // Timer definition
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
            MainActivity.handler.sendMessage(msg);
        }
    }

    ;


    public Timer(MainActivity creator, int typeSet) {
        this.creator = creator;
        type = typeSet;

        //timer.cancel();
        if (type != 1) {
            startTimer();
        }
    }

    // return counted Time
    public double getTime() {
        return time;
    }

    public void killTimer() {
        if (isRunning) {
            timer.cancel();
            timer = null;
            isRunning = false;
        }
    }

    public void startTimer() {
        if (!isRunning) {
            timer = new java.util.Timer();
            timer.scheduleAtFixedRate(new RideTimerTask(), 1000, 1000);
            isRunning = true;
        }
    }

    // return state of Timer
    public boolean getActive() {
        return isRunning;
    }
}
