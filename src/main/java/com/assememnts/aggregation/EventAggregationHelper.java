package com.assememnts.aggregation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EventAggregationHelper {

    public static CompletableFuture<Map<String, AggregationDTO>> aggregate(final Stream<Event> events) {
        return CompletableFuture.supplyAsync(() -> events
                .parallel()
                .filter(event -> event.value() >= 0)
                .distinct()
                .map(AggregationDTO::new)
                .collect(
                        Collectors.groupingByConcurrent(
                                AggregationDTO::getId,
                                Collectors.reducing(new AggregationDTO(), (one, two) -> {
                                    AggregationDTO neww = new AggregationDTO();
                                    neww.setId(two.getId());
                                    neww.setSum(one.getSum() + two.getSum());

                                    AtomicInteger counter = new AtomicInteger(
                                            one.getCounter().get() + 1
                                    );
                                    neww.setCounter(counter);

                                    AtomicReference<Double> average = new AtomicReference<
                                            Double
                                            >(one.getAvgValue().get());
                                    double avg =
                                            average.get() +
                                                    (two.getSum() - average.get()) /
                                                            Math.max(counter.get(), 1);
                                    average.set(avg);
                                    neww.setAvgValue(average);

                                    neww.setMaxTimestamp(
                                            Math.max(one.getMaxTimestamp(), two.getTimestamp())
                                    );

                                    if (one.getMinTimestamp() <= 0) {
                                        neww.setMinTimestamp(two.getTimestamp());
                                    } else {
                                        neww.setMinTimestamp(
                                                Math.min(
                                                        one.getMinTimestamp(),
                                                        two.getTimestamp()
                                                )
                                        );
                                    }

                                    return neww;
                                })
                        )
                ));
    }
}
