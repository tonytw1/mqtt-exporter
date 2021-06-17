package uk.co.eelpieconsulting.monitoring.model;

import org.joda.time.DateTime;

public class Metric {

    private final String name, lastValue;
    private final DateTime date;

    public Metric(String name, String lastValue, DateTime date) {
        super();
        this.name = name;
        this.lastValue = lastValue;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getLastValue() {
        return lastValue;
    }

    public DateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", lastValue='" + lastValue + '\'' +
                ", date=" + date +
                '}';
    }
}
