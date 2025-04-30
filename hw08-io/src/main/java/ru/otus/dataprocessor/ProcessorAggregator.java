package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> groupedMap = data.stream()
                .collect(Collectors.groupingBy(
                        Measurement::name,
                        Collectors.summingDouble(Measurement::value)
                ));

        return groupedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}