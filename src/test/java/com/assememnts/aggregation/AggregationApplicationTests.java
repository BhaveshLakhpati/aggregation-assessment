package com.assememnts.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class AggregationApplicationTests {
    @ParameterizedTest
    @ValueSource(strings = {
            "events_dataset_v1.csv",
            "events_dataset_v2.csv"
    })
    void test_with_dataset(final String filename) {
        ClassPathResource resource = new ClassPathResource(filename);
        try (Stream<String> stream = Files.lines(resource.getFilePath())
                .skip(1)) {
            Map<String, AggregationDTO> aggregationResult = EventAggregationHelper.aggregate(stream.parallel()
                            .map(line -> {
                                String[] data = line.split(",");
                                return new Event(data[0], Long.parseLong(data[1]), Double.parseDouble(data[2]));
                            }))
                    .join();
            Assertions.assertNotNull(aggregationResult);

            try (Stream<String> fileStream = Files.lines(resource.getFilePath())) {
                Map<String, AggregationDTO> expected = fileStream.skip(1)
                        .limit(4)
                        .map(line -> {
                            String[] expectedValues = line.substring(line.indexOf(",,") + 2)
                                    .split(",");

                            return new AggregationDTO(expectedValues[0],
                                    Long.parseLong(expectedValues[1]),
                                    Long.parseLong(expectedValues[2]),
                                    Integer.parseInt(expectedValues[3]),
                                    Double.parseDouble(expectedValues[4]),
                                    Double.parseDouble(expectedValues[5]));
                        })
                        .collect(Collectors.toMap(AggregationDTO::getId, Function.identity()));
                log.info("expected: {}, actual: {}", expected, aggregationResult);

                aggregationResult.forEach((key, value) -> {
                    AggregationDTO expectedAgg = expected.get(key);

                    Assertions.assertAll(
                            () -> Assertions.assertEquals(value.getMinTimestamp(), expectedAgg.getMinTimestamp()),
                            () -> Assertions.assertEquals(value.getMaxTimestamp(), expectedAgg.getMaxTimestamp()),
                            () -> Assertions.assertEquals(value.getCounter().get(), expectedAgg.getCounter().get()),
                            () -> Assertions.assertEquals(value.getSum(), expectedAgg.getSum(), 0.1),
                            () -> Assertions.assertEquals(value.getAvgValue().get(), expectedAgg.getAvgValue().get(), 0.1)
                    );
                });
            }
        } catch (final Exception exception) {
            Assertions.fail(exception);
        }
    }

}
