package com.example.finnl.gotrack.Statistics;


import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.Recording.Timer;
import com.example.finnl.gotrack.Statistics.KmCounter;

public class KmhAverager {
    private MainActivity creator;

    private KmCounter kmCounter;
    private Timer timerClass;
    private double avgSpeed = 0.0;
    private int type;



    public KmhAverager(MainActivity creator, KmCounter kmCounter, Timer timerSet, int typeSet) {
        this.creator = creator;
        timerClass = timerSet;
        this.kmCounter=kmCounter;
        type=typeSet;
    }


    // calc avg Speed by selected Timer
    public void calcAvgSpeed() {
        double mAmount = kmCounter.getAmount();
        double time = timerClass.getTime();

        avgSpeed = (mAmount/time);


        //creator.newAvgKmh((ms*60*60)/1000);
    }

    // switch Timer
    public void switchTimer(Timer timerClassSet, int typeSet){
        timerClass = timerClassSet;
        type=typeSet;
        calcAvgSpeed();
    }

    public int getType(){
        return type;
    }
}
