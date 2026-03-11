# aggregation-assessment

# How to run:
mvn -DskipTests clean spring-boot:run `<CSV_FILE_PATH>`

# used ChatGPT for following:
1. formula for to calculate Running Average
2. simulate input data
3. `groupingByConcurrent()` instead of `groupingBy()` in `parallelStream()` using `Stackoverflow`
4. had modified `Event` record to have `equals()` and `hashCode()` but had to lookup something which removes duplicate; ChatGPT suggested `distinct()` 

# Assumptions:
1. Datasource is CSV file
2. value column cannot have non-null/non-numeric

# Notes:
1. Have slightly modified `Event` record to have `equals()` &  `hashCode()` which is leveraged by `Stream.distinct()`

# Explanation:
1. first filter the events with negative elements
2. using `distinct()` avoid duplicate event with same id & timestamp and then map it to AggregationDTO
3. `parallelStream()` with `groupingByConcurrent()` to make sure values don't get overridden(which may happen in groupingBy()), instead get accumulated correctly
4. `reducing()` to accumulate all the values in a single event for the respective events

# Heap usage:

    Without running aggregation function:
        used: 16114972 ~ 16MB

    When running aggregation function:
        used: 22104628 ~ 22MB