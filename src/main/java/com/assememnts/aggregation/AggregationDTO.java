package com.assememnts.aggregation;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class AggregationDTO {

    private String id;
    private long timestamp;
    private double sum;

    private AtomicInteger counter;
    private AtomicReference<Double> avgValue;

    private long minTimestamp;
    private long maxTimestamp;

    public AggregationDTO() {
        this.id = null;
        this.timestamp = -1;
        this.sum = 0;
        this.minTimestamp = -1;
        this.maxTimestamp = -1;
        this.counter = new AtomicInteger();
        this.avgValue = new AtomicReference<>(0.0d);
    }

    public AggregationDTO(String id,
                          long minTimestamp,
                          long maxTimestamp,
                          int counter,
                          double sum,
                          double avgValue) {
        this.id = id;
        this.sum = sum;
        this.counter = new AtomicInteger(counter);
        this.avgValue = new AtomicReference<>(avgValue);
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }

    public AggregationDTO(final Event event) {
        this.id = event.id();
        this.timestamp = event.timestamp();
        this.sum = event.value();
    }
}
