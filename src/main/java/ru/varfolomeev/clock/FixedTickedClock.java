package ru.varfolomeev.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class FixedTickedClock extends Clock {
    private Instant inst;
    private final ZoneId zoneId;

    public FixedTickedClock(Instant inst, ZoneId zoneId) {
        this.inst = inst;
        this.zoneId = zoneId;
    }

    public FixedTickedClock(Instant inst) {
        this(inst, ZoneId.systemDefault());
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new FixedTickedClock(inst, zone);
    }

    @Override
    public Instant instant() {
        return inst;
    }

    public void plusSeconds(long seconds) {
        inst = inst.plusSeconds(seconds);
    }
}
