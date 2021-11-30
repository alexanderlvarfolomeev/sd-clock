package ru.varfolomeev.statistic;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MapEventStatistic implements EventStatistic {
    private final Map<String, LinkedList<Long>> eventTimestampMap;
    private final Clock clock;

    public MapEventStatistic(Map<String, LinkedList<Long>> eventTimestampMap, Clock clock) {
        this.eventTimestampMap = eventTimestampMap;
        this.clock = clock;
    }

    public MapEventStatistic(Clock clock) {
        this(new HashMap<>(), clock);
    }

    @Override
    public void incEvent(String name) {
        eventTimestampMap.computeIfAbsent(name, n -> new LinkedList<>()).addLast(clock.millis());
    }

    @Override
    public double getEventStatisticByName(String name) {
        return getEventRPM(clock.millis(), eventTimestampMap.get(name));
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        long millis = clock.millis();
        return eventTimestampMap.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), getEventRPM(millis, e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void printStatistic() {
        Map<String, Double> allEventStatistic = getAllEventStatistic();
        allEventStatistic.forEach((name, rpm) -> System.out.printf("%s: %f%n", name, rpm));
    }

    private double getEventRPM(long currentMillis, LinkedList<Long> timestamps) {
        if (timestamps == null) {
            return 0;
        } else {
            pollOutdatedTimestamps(currentMillis, timestamps);
            return timestamps.size() / 60D;
        }
    }

    private void pollOutdatedTimestamps(long currentMillis, LinkedList<Long> timestamps) {
        long hourAgoMillis = currentMillis - TimeUnit.HOURS.toMillis(1);
        while (!timestamps.isEmpty() && timestamps.peekFirst() < hourAgoMillis) {
            timestamps.pollFirst();
        }
    }
}
