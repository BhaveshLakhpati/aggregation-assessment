package com.assememnts.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class AggregationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AggregationApplication.class, args);
    }

    @Override
    public void run(final String[] args) {
        if(args.length < 1) {
            throw new RuntimeException("File name required!");
        }
        try (final Stream<String> fileStream = Files.lines(Path.of(args[0]))
                .skip(1L)) {
            EventAggregationHelper.aggregate(fileStream
                            .parallel()
                            .map(line -> {
                                String[] data = line.split(",");
                                return new Event(data[0], Long.parseLong(data[1]), Double.parseDouble(data[2]));
                            }))
                    .exceptionally(throwable -> {
                        log.error("Error while performing aggregation: ", throwable);

                        return null;
                    })
                    .thenAccept(aggregation -> {
                        log.info("Aggregation result: {}", aggregation);
                    })
                    .join();
        } catch (final IOException exception) {
            log.error("Error while reading file: ", exception);
        }
    }
}
