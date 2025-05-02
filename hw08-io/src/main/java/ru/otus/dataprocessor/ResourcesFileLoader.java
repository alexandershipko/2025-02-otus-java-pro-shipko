package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.Measurement;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;


public class ResourcesFileLoader implements Loader {

    private final String fileName;

    private final ObjectMapper objectMapper;


    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Measurement> load() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileProcessException("File not found in resources: " + fileName);
        }

        try (inputStream) {
            List<Measurement> measurements = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            measurements.sort(Comparator.comparing(Measurement::name));
            return measurements;
        } catch (Exception e) {
            throw new FileProcessException("Error reading or parsing the JSON file: " + fileName, e);
        }
    }

}