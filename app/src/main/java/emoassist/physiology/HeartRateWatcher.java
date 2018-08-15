package emoassist.physiology;

import emoassist.activities.EmotionsActivity;

public abstract class HeartRateWatcher {

    protected long timeStarted, timeStopped;
    private String timeElapsedFormatted = "";
    private boolean running = false;
    private boolean wasEverStarted = false;

    protected EmotionsActivity myact = null;

    public HeartRateWatcher(EmotionsActivity a) {
        myact = a;
    }



    private void calculateTimeElapsedFormatted() {
        long elapsedMillis = timeStopped - timeStarted;
        long elapsedSecs = elapsedMillis / 1000;
        long elapsedMins = elapsedSecs / 60;
        long elapsedHours = elapsedMins / 60;
        timeElapsedFormatted = elapsedHours + ":" + elapsedMins + ":"
                + elapsedSecs;
    }

    public void stop() {
        running = false;
        timeStopped = System.currentTimeMillis();
        calculateTimeElapsedFormatted();
    }



}
