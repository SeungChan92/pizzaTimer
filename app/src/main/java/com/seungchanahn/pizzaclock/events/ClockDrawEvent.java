package com.seungchanahn.pizzaclock.events;

/**
 * Created by anirban on 5/21/17.
 */

public class ClockDrawEvent {
    private int intervalSec;

    public ClockDrawEvent(int intervalSec) {
        this.intervalSec = intervalSec;
    }

    public int getIntervalSec() {
        return intervalSec;
    }
}
