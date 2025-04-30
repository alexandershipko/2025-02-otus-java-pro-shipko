package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class FileSerializer implements Serializer {

    private final String fileName;

    private final ObjectMapper objectMapper;


    public FileSerializer(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            objectMapper.writeValue(new File(fileName), data);
        } catch (IOException e) {
            throw new FileProcessException("Error serializing data to file: " + fileName, e);
        }
    }

}