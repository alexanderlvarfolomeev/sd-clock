package ru.varfolomeev;

import ru.varfolomeev.statistic.EventStatistic;
import ru.varfolomeev.statistic.MapEventStatistic;

import java.time.Clock;

public class Main {
    public static void main(String... args) {
        EventStatistic eventStatistic = new MapEventStatistic(Clock.systemDefaultZone());
        "abracadabra".chars().mapToObj(ch -> String.valueOf((char) ch)).forEach(eventStatistic::incEvent);
        eventStatistic.printStatistic();
    }
}
