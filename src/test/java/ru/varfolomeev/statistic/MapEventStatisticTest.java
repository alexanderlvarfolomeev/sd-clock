package ru.varfolomeev.statistic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.varfolomeev.clock.FixedTickedClock;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapEventStatisticTest {
    private final static double DELTA = 5e-3;
    private final static String EVENT_PREFIX = "test";
    private final static int COUNT_PER_EVENT = 1000;

    private final FixedTickedClock clock = new FixedTickedClock(Instant.now());
    private EventStatistic eventStatistic;

    @Before
    public void before() {
        eventStatistic = new MapEventStatistic(clock);
    }

    @Test
    public void testEmptyStatistic() {
        Assert.assertEquals(0, eventStatistic.getEventStatisticByName(EVENT_PREFIX), DELTA);
        Assert.assertEquals(Map.of(), eventStatistic.getAllEventStatistic());
    }

    @Test
    public void testSingleStatistic() {
        double rpm = calcRPM(1);
        eventStatistic.incEvent(EVENT_PREFIX);
        Assert.assertEquals(rpm, eventStatistic.getEventStatisticByName(EVENT_PREFIX), DELTA);
        Assert.assertEquals(Map.of(EVENT_PREFIX, rpm), eventStatistic.getAllEventStatistic());
    }

    @Test
    public void testManyStatistics() {
        for (int i = 0; i < COUNT_PER_EVENT; i++) {
            eventStatistic.incEvent(eventName(i));
        }
        Map<String, Double> actualStatistic = new HashMap<>();
        for (int i = 0; i < COUNT_PER_EVENT; i++) {
            String event = eventName(i);
            double rpm = calcRPM(1);
            Assert.assertEquals(rpm, eventStatistic.getEventStatisticByName(event), DELTA);
            actualStatistic.put(event, rpm);
        }
        Assert.assertEquals(actualStatistic, eventStatistic.getAllEventStatistic());
    }

    @Test
    public void testOftenStatistic() {
        for (int i = 1; i <= COUNT_PER_EVENT; i++) {
            eventStatistic.incEvent(EVENT_PREFIX);
            double rpm = calcRPM(i);
            Assert.assertEquals(rpm, eventStatistic.getEventStatisticByName(EVENT_PREFIX), DELTA);
            Assert.assertEquals(Map.of(EVENT_PREFIX, rpm), eventStatistic.getAllEventStatistic());
        }
    }

    @Test
    public void testOutdatedStatistic() {
        eventStatistic.incEvent(EVENT_PREFIX);
        waitHourAndSecond();
        Assert.assertEquals(0.0, eventStatistic.getEventStatisticByName(EVENT_PREFIX), DELTA);
    }

    @Test
    public void testOutdatedStatistics() {
        for (int i = 0; i < COUNT_PER_EVENT; i++) {
            waitHourAndSecond();

            String event = eventName(i);
            double rpmOf1 = calcRPM(1);
            eventStatistic.incEvent(event);
            final int ii = i;
            Assert.assertEquals(
                    IntStream
                            .range(0, i + 1)
                            .boxed()
                            .collect(Collectors.toMap(this::eventName, j -> j == ii ? rpmOf1 : 0)),
                    eventStatistic.getAllEventStatistic()
            );
        }
    }

    private String eventName(int i) {
        return EVENT_PREFIX + i;
    }

    private void waitHourAndSecond() {
        clock.plusSeconds(TimeUnit.HOURS.toSeconds(1) + 1);
    }

    private double calcRPM(int count) {
        return count / 60D;
    }
}
